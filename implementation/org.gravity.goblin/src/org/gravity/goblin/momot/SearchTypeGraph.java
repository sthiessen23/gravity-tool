package org.gravity.goblin.momot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.model.Unit;
import org.gravity.goblin.SearchParameters;
import org.gravity.goblin.Utility;
import org.gravity.goblin.constraints.VisibilityConstraintCalculator;
import org.gravity.goblin.fitness.AntiPatternCalculator;
import org.gravity.goblin.fitness.CohesionCalculator;
import org.gravity.goblin.fitness.CouplingCalculator;
import org.gravity.goblin.fitness.IFitnessCalculator;
import org.gravity.goblin.fitness.VisibilityCalculator;
import org.gravity.goblin.orchestration.MoveMethodTransformationSearchOrchestration;
import org.gravity.goblin.repair.PostProcessRepairMultiDimensionalFitnessFunction;
import org.gravity.goblin.repair.VisibilityReducer;
import org.gravity.goblin.repair.VisibilityRepairer;
import org.gravity.goblin.typegraph.equality.EqualityHelper;
import org.gravity.hulk.HulkAPI;
import org.gravity.typegraph.basic.BasicPackage;
import org.gravity.typegraph.basic.TypeGraph;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.operator.OnePointCrossover;
import org.moeaframework.core.operator.TournamentSelection;
import org.osgi.framework.Bundle;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import at.ac.tuwien.big.moea.SearchExperiment;
import at.ac.tuwien.big.moea.experiment.executor.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.moea.search.algorithm.EvolutionaryAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.LocalSearchAlgorithmFactory;
import at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension;
import at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType;
import at.ac.tuwien.big.momot.ModuleManager;
import at.ac.tuwien.big.momot.TransformationResultManager;
import at.ac.tuwien.big.momot.TransformationSearchOrchestration;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.search.algorithm.operator.mutation.TransformationParameterMutation;
import at.ac.tuwien.big.momot.search.algorithm.operator.mutation.TransformationPlaceholderMutation;
import at.ac.tuwien.big.momot.search.fitness.IEGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.dimension.AbstractEGraphFitnessDimension;
import at.ac.tuwien.big.momot.search.fitness.dimension.TransformationLengthDimension;
import at.ac.tuwien.big.momot.search.solution.repair.ITransformationRepairer;

import static org.gravity.hulk.HulkAPI.AntiPatternNames.*;

@SuppressWarnings("all")
public class SearchTypeGraph {

	protected List<FitnessFunction> fitnessFunctions;
	public static List<FitnessFunction> constraints;

	// protected final String[] modules = new String[] {
	// "transformations/allInOne.henshin" };
	/*
	 * protected final String[] unitsToRemove = new String[] {
	 * "MoveMethod::rules::libCheck", "MoveMethod::rules::MoveMethod",
	 * "MoveMethod::rules::checkPreconditions",
	 * "MoveMethod::rules::dyn1MoveMethod",
	 * "MoveMethod::rules::MoveMethodWithoutPreconstraints" ,
	 * "MoveMethod::rules::changeVisibility",
	 * "MoveMethod::rules::dyn2MoveMethod" //
	 * ,"MoveMethod::rules::MoveMethodMain" };
	 */

	public class FitnessFunction {
		public String Name;
		public FunctionType type;
		public IFitnessCalculator calculator;

		public FitnessFunction(String name, FunctionType type, IFitnessCalculator calc) {
			this.Name = name;
			this.type = type;
			this.calculator = calc;
		}
	}

	public void initializeFitnessFunctions() {
		fitnessFunctions = new ArrayList<FitnessFunction>();
		fitnessFunctions.add(new FitnessFunction("Coupling", FunctionType.Minimum, new CouplingCalculator()));
		fitnessFunctions.add(new FitnessFunction("LCOM", FunctionType.Minimum, new CohesionCalculator()));
		fitnessFunctions.add(new FitnessFunction("Number of Blobs", FunctionType.Minimum, new AntiPatternCalculator()));
		fitnessFunctions.add(new FitnessFunction("Visibility", FunctionType.Minimum, new VisibilityCalculator()));
		//exclude repairs, not needed anymore because of visibility
		/*if (SearchParameters.useRepair) {
			fitnessFunctions.add(new FitnessFunction("Number Repairs", FunctionType.Minimum, new RepairMetricCalculator()));
		}*/

	}

	public void initializeConstraints() {
		constraints = new ArrayList<FitnessFunction>();
		if (SearchParameters.useConstraints) {
			constraints
					.add(new FitnessFunction("Visibility", FunctionType.Minimum, new VisibilityConstraintCalculator()));
		}
	}

	private void initializeAlgorithms(TransformationSearchOrchestration orchestration,
			EvolutionaryAlgorithmFactory<TransformationSolution> moea) {
		
		TournamentSelection ts = null;
		if(SearchParameters.useCustomDominanceComperator) {
			ts = new TournamentSelection(2, new CustomDominanceComperator(fitnessFunctions));
		}else {
			ts = new TournamentSelection(2);
		}
		
		orchestration.addAlgorithm("NSGAIII", moea.createNSGAIII(ts,
				new OnePointCrossover(SearchParameters.OnePointCrossoverProbability),
				new TransformationParameterMutation(SearchParameters.TransformationParameterMutationProbability,
						orchestration.getModuleManager()),
				new TransformationPlaceholderMutation(SearchParameters.TransformationPlaceholderMutationProbability)));
	}

	// -----------------------------non configurable
	// stuff-------------------------------------------
	protected IFitnessDimension<TransformationSolution> createFitnessDimension(FitnessFunction function) {
		return new AbstractEGraphFitnessDimension(function.Name, function.type) {
			@Override
			protected double internalEvaluate(TransformationSolution solution) {
				return function.calculator.calculate(Utility.getPG(solution.getResultGraph()));
			}
		};
	}

	protected IFitnessDimension<TransformationSolution> createSolutionLengthFitness() {
		IFitnessDimension<TransformationSolution> dimension = new TransformationLengthDimension("SolutionLength");
		dimension.setFunctionType(FunctionType.Minimum);
		return dimension;
	}

	protected ModuleManager createModuleManager() {
		Bundle bundle = Platform.getBundle("org.goblin.movemethod");
		ModuleManager manager = new ModuleManager();
		for (String module : SearchParameters.modules) {
			if(bundle == null) {
				manager.addModule(module);
			}else {
				URL entry = bundle.getEntry(module);
				try {
					String substring = entry.getFile().substring(entry.getFile().lastIndexOf('/') + 1);
					Path temp = Files.createTempDirectory("goblin");
					File file = new File(temp.toFile(), substring);
					Files.copy(entry.openStream(), file.toPath());
					manager.addModule(file.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		List<String> units = new ArrayList<String>();
		for (Unit unit : manager.getUnits()) {
			units.add(manager.getQualifiedName(unit));
		}
		units.removeAll((Arrays.asList(SearchParameters.units)));
		manager.removeUnits(units);

		return manager;
	}

	protected IEGraphMultiDimensionalFitnessFunction createFitnessFunction() {
		PostProcessRepairMultiDimensionalFitnessFunction function = new PostProcessRepairMultiDimensionalFitnessFunction();
		// IEGraphMultiDimensionalFitnessFunction function = new
		// EGraphMultiDimensionalFitnessFunction();
		function.addObjective(createSolutionLengthFitness());

		for (FitnessFunction fitness : fitnessFunctions) {
			function.addObjective(createFitnessDimension(fitness));
		}
		for (FitnessFunction constraint : constraints) {
			function.addConstraint(createFitnessDimension(constraint));
		}
		if (SearchParameters.useRepair) {
			function.setSolutionRepairer(new VisibilityRepairer());
		}
		if(SearchParameters.useOptimizationRepair){
			function.setOptimizationRepairer(new VisibilityReducer());
		}

		return function;
	}

	protected TransformationSearchOrchestration createOrchestration(final String initialGraph,
			final int solutionLength) {
		MoveMethodTransformationSearchOrchestration orchestration = new MoveMethodTransformationSearchOrchestration();
		// TransformationSearchOrchestration orchestration = new
		// TransformationSearchOrchestration();
		ModuleManager moduleManager = createModuleManager();
		EGraph graph = moduleManager.loadGraph(initialGraph);
		orchestration.setModuleManager(moduleManager);
		orchestration.setProblemGraph(graph);
		orchestration.setSolutionLength(solutionLength);
		orchestration.setFitnessFunction(createFitnessFunction());
		orchestration.setEqualityHelper(new EqualityHelper());

		EvolutionaryAlgorithmFactory<TransformationSolution> moea = orchestration
				.createEvolutionaryAlgorithmFactory(SearchParameters.populationSize);
		LocalSearchAlgorithmFactory<TransformationSolution> local = orchestration.createLocalSearchAlgorithmFactory();

		initializeAlgorithms(orchestration, moea);
		return orchestration;
	}

	protected SearchExperiment<TransformationSolution> createExperiment(
			final TransformationSearchOrchestration orchestration) {
		SearchExperiment<TransformationSolution> experiment = new SearchExperiment<TransformationSolution>(
				orchestration, SearchParameters.maxEvaluations);
		experiment.setNumberOfRuns(SearchParameters.nrRuns);
		experiment.addProgressListener(new SeedRuntimePrintListener());
		return experiment;
	}

	public TransformationResultManager performSearch(String initialGraph, int solutionLength) {
		return performSearch(initialGraph, solutionLength, new File("./"));
	}

	public TransformationResultManager performSearch(final String initialGraph, final int solutionLength, File folder) {
		TransformationSearchOrchestration orchestration = createOrchestration(initialGraph, solutionLength);
		SearchPrinter printer = new SearchPrinter(orchestration);
		printer.printSearchInfo(SearchParameters.INITIAL_MODEL, SearchParameters.modules,
				SearchParameters.populationSize, SearchParameters.maxEvaluations, SearchParameters.nrRuns);
		SearchExperiment<TransformationSolution> experiment = createExperiment(orchestration);
		long start = System.currentTimeMillis();
		experiment.run();
		long duration = System.currentTimeMillis() -start;
		try {
			Files.write(new File(folder, "durationGoblinInMs.txt").toPath(), Long.toString(duration).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return printer.printResults(experiment, folder);
	}

	private void handleInput(String[] args) {
		JCommander jCommander = new JCommander(new SearchParameters());
		jCommander.setProgramName("Search Type Graph");

		try {
			jCommander.parse(args);
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			jCommander.usage();
			System.exit(0);
		}

		if (SearchParameters.help) {
			jCommander.usage();
			System.exit(0);
		}

	}

	public static void main(final String... args) {
		SearchTypeGraph search = new SearchTypeGraph();
		// parseArgs(args);
		search.handleInput(args);
		BasicPackage.eINSTANCE.eClass();
		System.out.println("Search started.");

		search.initializeFitnessFunctions();
		search.initializeConstraints();
		search.performSearch(SearchParameters.INITIAL_MODEL, SearchParameters.SOLUTION_LENGTH);
		System.out.println("Search finished.");
	}
}
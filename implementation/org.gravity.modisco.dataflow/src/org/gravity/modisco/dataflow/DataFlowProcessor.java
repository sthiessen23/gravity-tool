package org.gravity.modisco.dataflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gmt.modisco.java.AbstractMethodInvocation;
import org.eclipse.gmt.modisco.java.Expression;
import org.eclipse.gmt.modisco.java.SingleVariableAccess;
import org.eclipse.gmt.modisco.java.SingleVariableDeclaration;
import org.eclipse.gmt.modisco.java.VariableDeclarationFragment;
import org.gravity.modisco.MFlow;
import org.gravity.modisco.MAbstractMethodDefinition;
import org.gravity.modisco.MDefinition;
import org.gravity.modisco.MFieldDefinition;
import org.gravity.modisco.MGravityModel;
import org.gravity.modisco.MethodInvocationStaticType;
import org.gravity.modisco.ModiscoFactory;
import org.gravity.modisco.processing.AbstractTypedModiscoProcessor;

/**
 * A preprocessor for calculating data flow edges
 * 
 * @author speldszus
 *
 */
public class DataFlowProcessor extends AbstractTypedModiscoProcessor<MDefinition> {

	@Override
	public boolean process(MGravityModel model, Collection<MDefinition> elements, IProgressMonitor monitor) {
		SubMonitor sub = SubMonitor.convert(monitor, "Create model elements for data flow", elements.size());
		boolean success = true;
		sub.beginTask("Statement pre-processing", 10);
		success &= preProcessStatements(model);
		sub.internalWorked(5);
		/* Naive approach; probably not going to be used anymore
		for(MDefinition m : elements) {
			sub.beginTask(m.getName(), 12);
			EList<SingleVariableAccess> fieldAccesses = m.getMAbstractFieldAccess();
			sub.internalWorked(1);
			EList<AbstractMethodInvocation> methodInvocations = m.getAbstractMethodInvocations();
			sub.internalWorked(1);
			EList<MethodInvocationStaticType> staticInvocations = m.getInvocationStaticTypes();
			sub.internalWorked(1);
			if (!fieldAccesses.isEmpty()) success &= createDataFlowEdgesForSVA(fieldAccesses, m);
			sub.internalWorked(3);
			if (!methodInvocations.isEmpty()) success &= createDataFlowEdgesForAMI(methodInvocations, m);
			sub.internalWorked(3);
			if (!staticInvocations.isEmpty()) success &= createDataFlowEdgesForMIST(staticInvocations, m); 
			sub.internalWorked(3);
			m.getAbstractMethodInvocations().get(0).getMethod().getBody().getStatements();
		}
		*/
		return success;
	}

	@Override
	public Class<MDefinition> getSupportedType() {
		return MDefinition.class;
	}
	
	/**
	 * Pre-processes the statements of all field and method definitions to determine the data flows between statement nodes.
	 * The resulting intermediate representation of data flow is then used to derive the data flow of each field/method.
	 * 
	 * @param model The model, whose statements are processed.
	 * @return true, if the pre-processing was successful.
	 */
	private boolean preProcessStatements(MGravityModel model) {
		if (model == null) {
			return true;
		}
		List<StatementHandlerDataFlow> handlers = new ArrayList<>();
		for (MAbstractMethodDefinition methodDef : model.getMAbstractMethodDefinitions()) {
			StatementHandlerDataFlow methodProcessor = new StatementHandlerDataFlow(methodDef);
			methodProcessor.getFlowNodeForElement(methodDef);
			for (SingleVariableDeclaration param : methodDef.getParameters()) {
				methodProcessor.getFlowNodeForElement(param);
			}
			methodProcessor.handle(methodDef.getBody());
			handlers.add(methodProcessor);
		}
		for (MFieldDefinition fieldDef : model.getMFieldDefinitions()) {
			for (VariableDeclarationFragment fragment : fieldDef.getFragments()) {
				StatementHandlerDataFlow fieldProcessor = new StatementHandlerDataFlow(fragment);
				Expression initializer = fragment.getInitializer();
				fieldProcessor.getFlowNodeForElement(fragment);
				fieldProcessor.getFlowNodeForElement(fieldDef);
				fieldProcessor.getExpressionHandler().handle(initializer);
				handlers.add(fieldProcessor);
			}
		}
		GraphVisualizer.drawGraphs(handlers); // Drawing one graph per handler; comment out, if no graphs are needed
		return handlers.size() > 0;
	}

	
	/**
	 * Creates data flow edges between all elements from the given element list and the given definition.
	 * @param <T>
	 * 
	 * @param elementList A list of model elements.
	 * @param field The definition, to which the data flow edges will be connected.
	 * @return A boolean, which indicates, if the creation of all edges was successful.
	 */
	/* Naive approach; probably not going to be used anymore
	private boolean createDataFlowEdgesForSVA(EList<SingleVariableAccess> elementList, MDefinition definition) {
		for (SingleVariableAccess accessed : elementList) {
			// TODO: Insert edge between accessed and accessing member
			// readAccess ? fieldFlow : checkFieldDef()
		}
		return false;
	}
	
	private boolean createDataFlowEdgesForAMI(EList<AbstractMethodInvocation> elementList, MDefinition definition) {
		for (AbstractMethodInvocation accessed : elementList) {
			if (accessed.getMethod().getClass() != null) {
				MFlow f = ModiscoFactory.eINSTANCE.createMFlow();
				//f.getReturnFlow().add(definition);
			}
		}
		return false;
	}
	
	private boolean createDataFlowEdgesForMIST(EList<MethodInvocationStaticType> elementList, MDefinition definition) {
		for (MethodInvocationStaticType accessed : elementList) {
			if (accessed.getMethodInvoc().getMethod().getClass() != null) {
				MFlow f = ModiscoFactory.eINSTANCE.createMFlow();
				//f.getReturnFlow().add(definition);
			}
		}
		return false;
	}
	*/
}
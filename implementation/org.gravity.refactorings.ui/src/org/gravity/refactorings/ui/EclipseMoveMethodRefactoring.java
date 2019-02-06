package org.gravity.refactorings.ui;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.MoveMethodDescriptor;
import org.eclipse.jdt.internal.corext.refactoring.JavaRefactoringDescriptorUtil;
import org.eclipse.jdt.internal.corext.refactoring.structure.MoveInstanceMethodProcessor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;
import org.gravity.eclipse.JavaHelper;
import org.gravity.typegraph.basic.TClass;
import org.gravity.typegraph.basic.TMethodSignature;

/**
 * This class provides the functionality to execute eclipse refactorings
 * 
 *  @author speldszus
 *
 */
@SuppressWarnings("restriction")
public class EclipseMoveMethodRefactoring {
	
	private static final Logger LOGGER = Logger.getLogger(EclipseMoveMethodRefactoring.class.getName());
	
	private final HashMap<String, IType> types;
	private final IJavaProject project;
	
	/**
	 * Searches for all types defined in the given Java project and initializes the refactoring engine
	 * 
	 * @param project The Java project on which refactorings should be executed
	 * @throws JavaModelException if reading the defined types failed
	 */
	public EclipseMoveMethodRefactoring(IJavaProject project) throws JavaModelException {
		this.project = project;
		this.types = JavaHelper.getTypesForProject(project);
	}
	
	/**
	 * Executed a move method refactoring
	 * 
	 * @param tSourceClass The source class 
	 * @param tTargetClass The target class
	 * @param tMethod The method which should be moved
	 * @param monitor A progress monitor
	 * @return true, iff the refactoring has been executed successfully
	 * @throws JavaModelException If the corresponding eclipse method cannot be found
	 */
	public boolean moveMethod(TClass tSourceClass, TClass tTargetClass, TMethodSignature tMethod,
					IProgressMonitor monitor) throws JavaModelException {
		if (tSourceClass.isTLib() || tTargetClass.isTLib()) {
			LOGGER.log(Level.ERROR, "Source or target class is library.");
			return false;
		}

		IType src = types.get(tSourceClass.getFullyQualifiedName());
		IType trg = types.get(tTargetClass.getFullyQualifiedName());
		IMethod iMethod = JavaHelper.getIMethod(tMethod, src);
		
		LOGGER.log(Level.INFO, iMethod.toString());
		return move(project, trg,iMethod, monitor);
	}

	private static boolean move(IJavaProject project, IType trg, IMethod method, IProgressMonitor monitor) {
		Map<String, String> map = initilizeRefactoringSpecification(trg, method);

		MoveMethodDescriptor refactoringDescriptor = (MoveMethodDescriptor) RefactoringCore
				.getRefactoringContribution(IJavaRefactorings.MOVE_METHOD)
				.createDescriptor(IJavaRefactorings.MOVE_METHOD, project.getProject().getName(), "move method", "", map,
						RefactoringDescriptor.MULTI_CHANGE);
		RefactoringStatus status = new RefactoringStatus();
		try {
			MoveRefactoring refactoring = (MoveRefactoring) refactoringDescriptor.createRefactoring(status);
			refactoring.checkAllConditions(monitor);
			MoveInstanceMethodProcessor processor = (MoveInstanceMethodProcessor) refactoring.getProcessor();
			boolean detected = false;
			for (IVariableBinding possibleTrg : processor.getPossibleTargets()) {
				String qualifiedName = possibleTrg.getType().getQualifiedName();
				if (trg.getFullyQualifiedName().equals(qualifiedName)) {
					processor.setTarget(possibleTrg);
					detected = true;
					break;
				}
			}
			if (!detected) {
				return false;
			}
			Change change = refactoring.createChange(monitor);
			change.perform(monitor);
			return true;

		} catch (CoreException e) {
			LOGGER.log(Level.ERROR, e.getLocalizedMessage(), e);
			return false;
		}
	}

	/**
	 * Initializes a map describing the refactoring
	 * 
	 * @param trg The target type to which the method should be moved
	 * @param method The method which should be moved
	 * @return the configuration map
	 */
	private static Map<String, String> initilizeRefactoringSpecification(IType trg, IMethod method) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(JavaRefactoringDescriptorUtil.ATTRIBUTE_INPUT, method.getHandleIdentifier());
		map.put(JavaRefactoringDescriptorUtil.ATTRIBUTE_NAME, method.getElementName());
		map.put("deprecate", "false");
		map.put("remove", "true");
		map.put("inline", "true");
		map.put("getter", "true");
		map.put("setter", "true");
		map.put("targetName", trg.getElementName());
		map.put("targetIndex", "0");
		return map;
	}
	
	/**
	 * Returns the java project on which eclipse refactorings can be performed
	 * 
	 * @return the java project
	 */
	public IJavaProject getJavaProject() {
		return this.project;
	}
}

package org.gravity.modisco.dataflow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SyslogAppender;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gmt.modisco.java.AbstractMethodDeclaration;
import org.eclipse.gmt.modisco.java.AbstractVariablesContainer;
import org.eclipse.gmt.modisco.java.ArrayAccess;
import org.eclipse.gmt.modisco.java.ArrayCreation;
import org.eclipse.gmt.modisco.java.ArrayInitializer;
import org.eclipse.gmt.modisco.java.ArrayLengthAccess;
import org.eclipse.gmt.modisco.java.Assignment;
import org.eclipse.gmt.modisco.java.BooleanLiteral;
import org.eclipse.gmt.modisco.java.CastExpression;
import org.eclipse.gmt.modisco.java.CharacterLiteral;
import org.eclipse.gmt.modisco.java.ClassInstanceCreation;
import org.eclipse.gmt.modisco.java.ConditionalExpression;
import org.eclipse.gmt.modisco.java.ConstructorInvocation;
import org.eclipse.gmt.modisco.java.Expression;
import org.eclipse.gmt.modisco.java.FieldAccess;
import org.eclipse.gmt.modisco.java.FieldDeclaration;
import org.eclipse.gmt.modisco.java.InfixExpression;
import org.eclipse.gmt.modisco.java.InstanceofExpression;
import org.eclipse.gmt.modisco.java.MethodDeclaration;
import org.eclipse.gmt.modisco.java.MethodInvocation;
import org.eclipse.gmt.modisco.java.NamedElement;
import org.eclipse.gmt.modisco.java.NullLiteral;
import org.eclipse.gmt.modisco.java.NumberLiteral;
import org.eclipse.gmt.modisco.java.ParenthesizedExpression;
import org.eclipse.gmt.modisco.java.PostfixExpression;
import org.eclipse.gmt.modisco.java.PrefixExpression;
import org.eclipse.gmt.modisco.java.SingleVariableAccess;
import org.eclipse.gmt.modisco.java.SingleVariableDeclaration;
import org.eclipse.gmt.modisco.java.Statement;
import org.eclipse.gmt.modisco.java.StringLiteral;
import org.eclipse.gmt.modisco.java.SuperFieldAccess;
import org.eclipse.gmt.modisco.java.SuperMethodInvocation;
import org.eclipse.gmt.modisco.java.ThisExpression;
import org.eclipse.gmt.modisco.java.TypeAccess;
import org.eclipse.gmt.modisco.java.TypeLiteral;
import org.eclipse.gmt.modisco.java.UnresolvedItemAccess;
import org.eclipse.gmt.modisco.java.VariableDeclaration;
import org.eclipse.gmt.modisco.java.VariableDeclarationExpression;
import org.eclipse.gmt.modisco.java.VariableDeclarationFragment;
import org.eclipse.gmt.modisco.java.VariableDeclarationStatement;
import org.gravity.modisco.MMethodDefinition;

public class ExpressionHandlerDataFlow {

	private static final Logger LOGGER = Logger.getLogger(ExpressionHandlerDataFlow.class.getName());
	
	/**
	 * The statement handler associated with this expression handler.
	 */
	private StatementHandlerDataFlow statementHandler;
	
	/**
	 * The misc handler associated with this expression handler.
	 */
	private MiscHandlerDataFlow miscHandler;
	
	public ExpressionHandlerDataFlow(StatementHandlerDataFlow parentHandler) {
		statementHandler = parentHandler;
		miscHandler = parentHandler.getMiscHandler();
	}
	
	public FlowNode handle(Expression expression) {
		if (expression == null) {
			return null; // assume nothing to do is success
		}
		if (expression instanceof ArrayLengthAccess) {
			ArrayLengthAccess arrayLengthAccess = (ArrayLengthAccess) expression;
			return handle(arrayLengthAccess.getArray());
		} else if (expression instanceof FieldAccess) {
			FieldAccess fieldAccess = (FieldAccess) expression;
			return handle(fieldAccess);
		} else if (expression instanceof MethodInvocation) {
			MethodInvocation methodInvocation = (MethodInvocation) expression;
			return handle(methodInvocation);

		} else if (expression instanceof ConstructorInvocation) {
			ConstructorInvocation constructorInvocation = (ConstructorInvocation) expression;
			return statementHandler.handle(constructorInvocation);

		} else if (expression instanceof StringLiteral) {
			StringLiteral stringLiteral = (StringLiteral) expression;
			return null;

		} else if (expression instanceof NullLiteral) {
			NullLiteral nullLiteral = (NullLiteral) expression;
			return null;

		} else if (expression instanceof ArrayCreation) {
			ArrayCreation arrayCreation = (ArrayCreation) expression;
			return handle(arrayCreation);

		} else if (expression instanceof ArrayInitializer) {
			ArrayInitializer arrayInitializer = (ArrayInitializer) expression;
			return handle(arrayInitializer);
		} else if (expression instanceof NumberLiteral) {
			return statementHandler.getFlowNodeForElement(expression);

		} else if (expression instanceof SingleVariableAccess) {
			SingleVariableAccess singleVariableAccess = (SingleVariableAccess) expression;
			return handle(singleVariableAccess);

		} else if (expression instanceof TypeAccess) {
			TypeAccess typAccess = (TypeAccess) expression;
			return null;

		} else if (expression instanceof InfixExpression) {
			InfixExpression infixExpression = (InfixExpression) expression;
			return handle(infixExpression);
		} else if (expression instanceof ClassInstanceCreation) {
			ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
			return handle(classInstanceCreation);
		} else if (expression instanceof InstanceofExpression) {
			InstanceofExpression instanceofExpression = (InstanceofExpression) expression;
			return handle(instanceofExpression);
		} else if (expression instanceof Assignment) {
			Assignment assignment = (Assignment) expression;
			return handle(assignment);
		} else if (expression instanceof PrefixExpression) {
			PrefixExpression prefixExpression = (PrefixExpression) expression;
			return handle(prefixExpression);
		} else if (expression instanceof SuperMethodInvocation) {
			SuperMethodInvocation superMethodInvocation = (SuperMethodInvocation) expression;
			return handle(superMethodInvocation);
		} else if (expression instanceof ThisExpression) {
			ThisExpression thisExpression = (ThisExpression) expression;
			return null;

		} else if (expression instanceof CastExpression) {
			CastExpression castExpression = (CastExpression) expression;
			return handle(castExpression);
		} else if (expression instanceof ParenthesizedExpression) {
			ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression;
			return handle(parenthesizedExpression);
		} else if (expression instanceof BooleanLiteral) {
			BooleanLiteral booleanLiteral = (BooleanLiteral) expression;
			return null;
		} else if (expression instanceof CharacterLiteral) {
			CharacterLiteral characterLiteral = (CharacterLiteral) expression;
			return null;
		} else if (expression instanceof ConditionalExpression) {
			ConditionalExpression conditionalExpression = (ConditionalExpression) expression;
			return handle(conditionalExpression);
		} else if (expression instanceof ArrayAccess) {
			ArrayAccess arrayAccess = (ArrayAccess) expression;
			handle(arrayAccess);
		} else if (expression instanceof TypeLiteral) {
			TypeLiteral typeLiteral = (TypeLiteral) expression;
			return null;
		} else if (expression instanceof VariableDeclarationExpression) {
			VariableDeclarationExpression variableDeclarationExpression = (VariableDeclarationExpression) expression;
			return handle(variableDeclarationExpression);
		} else if (expression instanceof PostfixExpression) {
			PostfixExpression postfixExpression = (PostfixExpression) expression;
			return handle(postfixExpression);

		} else if (expression instanceof SuperFieldAccess) {
			SuperFieldAccess superFieldAccess = (SuperFieldAccess) expression;
			return handle(superFieldAccess);
		} else if (expression instanceof UnresolvedItemAccess) {
			UnresolvedItemAccess itemAccess = (UnresolvedItemAccess) expression;
			return null;

		}
		LOGGER.log( Level.INFO, "ERROR: Unknown Expression: " + expression);
		return null;
	}

	private FlowNode handle(SuperFieldAccess superFieldAccess) {
		return handle(superFieldAccess.getField());
	}

	private FlowNode handle(PostfixExpression postfixExpression) {
		return handle(postfixExpression.getOperand());
	}

	private FlowNode handle(VariableDeclarationExpression variableDeclarationExpression) {
		if (variableDeclarationExpression == null) {
			return null; // assume nothing to do is success
		}
		FlowNode member = statementHandler.getFlowNodeForElement(variableDeclarationExpression);
		if (member.isFromAlreadySeen()) {
			return member;
		}
		for (VariableDeclarationFragment fragment : variableDeclarationExpression.getFragments()) {
			miscHandler.handle(fragment);
		}
		return member;
	}

	private FlowNode handle(ArrayCreation arrayCreation) {
		FlowNode member = statementHandler.getFlowNodeForElement(arrayCreation);
		if (member.isFromAlreadySeen()) {
			return member;
		}
		handle(arrayCreation.getInitializer());
		for (Expression dimension : arrayCreation.getDimensions()) {
			handle(dimension);
		}
		return member;
	}

	private FlowNode handle(ArrayInitializer arrayInitializer) {
		if (arrayInitializer == null) {
			return null; // assume nothing to to is success
		}
		FlowNode member = statementHandler.getFlowNodeForElement(arrayInitializer);
		if (member.isFromAlreadySeen()) {
			return member;
		}
		for (Expression initializerExpression : arrayInitializer.getExpressions()) {
			handle(initializerExpression);
		}
		return member;
	}

	private FlowNode handle(SingleVariableAccess singleVariableAccess) {
		FlowNode member = statementHandler.getFlowNodeForElement(singleVariableAccess);
		if (member.isFromAlreadySeen()) {
			return member;
		}
		handle(singleVariableAccess.getQualifier());
		VariableDeclaration variable = singleVariableAccess.getVariable();
		FlowNode varDeclNode = statementHandler.getFlowNodeForElement(variable);
		// Assignment flows
		boolean isAssignment = false;
		EObject currentContainer = singleVariableAccess.eContainer();
		LinkedList<EObject> seenContainers = new LinkedList<>();
		seenContainers.add(currentContainer);
		while (!(currentContainer instanceof Statement) && (currentContainer != null)) {
			if (currentContainer instanceof Assignment) {
				isAssignment = true;
				Assignment assignment = (Assignment) currentContainer;
				switch (assignment.getOperator()) {
				case ASSIGN:
					EStructuralFeature assignmentSide;
					int queueSize = seenContainers.size();
					if (queueSize > 1) {
						assignmentSide = seenContainers.get(queueSize - 2).eContainingFeature();	
					} else {
						assignmentSide = singleVariableAccess.eContainingFeature();
					}
					if (assignmentSide.equals(assignment.getLeftHandSide().eContainingFeature())) {
						member.addOutRef(varDeclNode);
						propagateBackWriteAccess(new LinkedList<>(seenContainers), member);
					} else if (assignmentSide.equals(assignment.getRightHandSide().eContainingFeature())) {
						member.addInRef(varDeclNode);
						propagateBackReadAccess(new LinkedList<>(seenContainers), member);
					} else {
						LOGGER.log(Level.INFO, "Unknown assignment side");
					}
					break;
				case BIT_AND_ASSIGN:
				case BIT_OR_ASSIGN:
				case BIT_XOR_ASSIGN:
				case DIVIDE_ASSIGN:
				case LEFT_SHIFT_ASSIGN:
				case MINUS_ASSIGN:
				case PLUS_ASSIGN:
				case REMAINDER_ASSIGN:
				case RIGHT_SHIFT_SIGNED_ASSIGN:
				case RIGHT_SHIFT_UNSIGNED_ASSIGN:
				case TIMES_ASSIGN:
					member.addInRef(varDeclNode); 
					member.addOutRef(varDeclNode);
					propagateBackReadAccess(new LinkedList<>(seenContainers), member);
					propagateBackWriteAccess(new LinkedList<>(seenContainers), member);
				break;
				default:
					LOGGER.log(Level.INFO, "Unknown operator used in assignment");
					break;
				}
				break;
			}
			currentContainer = currentContainer.eContainer();
			seenContainers.add(currentContainer);
		}
		if (!isAssignment) {
			statementHandler.getFlowNodeForElement(singleVariableAccess.eContainer()).addInRef(member);
			// Field and (non-param) local flows
			if (variable instanceof VariableDeclarationFragment) {
				VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) variable;
				AbstractVariablesContainer variablesContainer = variableDeclarationFragment.getVariablesContainer();
				if (variablesContainer instanceof FieldDeclaration) { // Read access of a field
					// TODO: Create read access edge
					statementHandler.getFlowNodeForElement(variablesContainer);
					statementHandler.getMemberIn().add(member);
					member.addInRef(varDeclNode);
				} else if (variablesContainer instanceof VariableDeclarationStatement) { // Read access of a local
					member.addInRef(statementHandler.getAlreadySeen().get(variableDeclarationFragment));
				} else if (variablesContainer instanceof VariableDeclarationExpression) {
					System.out.println("This happens as well...?"); // TODO Does this happen?
				} else {
					LOGGER.log(Level.INFO, "Unknown VariableDeclarationFragment expression");
				}
				// Parameter flows
			} else if (variable instanceof SingleVariableDeclaration) { 
				SingleVariableDeclaration singleVariableDeclaration = (SingleVariableDeclaration) variable;
				if (singleVariableDeclaration.eContainer() instanceof AbstractMethodDeclaration) { // Read access of a parameter
					// TODO Read access of a parameter only here? Or in general for each SingleVariableDecl?
					member.addInRef(statementHandler.getFlowNodeForElement(singleVariableDeclaration)); // Add edge from decl to access
				} else {
					System.out.println("Called on local " + variable.getName()); // TODO Does this happen?
				}
			} else {
				LOGGER.log(Level.INFO, "Unknown SingleVariableAccess expression");
			}
		}
		return member;
	}

	private FlowNode handle(InfixExpression infixExpression) {
		FlowNode member = statementHandler.getFlowNodeForElement(infixExpression);
		if (member.isFromAlreadySeen()) {
			return member;
		}
		handle(infixExpression.getLeftOperand());
		handle(infixExpression.getRightOperand());
		for (Expression extendedOperand : infixExpression.getExtendedOperands()) {
			handle(extendedOperand);
		}
		statementHandler.getFlowNodeForElement(infixExpression.eContainer()).addInRef(member);
		return member;
	}

	private FlowNode handle(ClassInstanceCreation classInstanceCreation) {
		FlowNode member = statementHandler.getFlowNodeForElement(classInstanceCreation);
		if (member.isFromAlreadySeen()) {
			return member;
		}
		handle(classInstanceCreation.getExpression());
		for (Expression argument : classInstanceCreation.getArguments()) {
			handle(argument);
		}
		statementHandler.getFlowNodeForElement(classInstanceCreation.eContainer()).addInRef(member);
		return member;
	}

	private FlowNode handle(InstanceofExpression instanceofExpression) {
		return handle(instanceofExpression.getLeftOperand());
	}

	private FlowNode handle(Assignment assignment) {
		FlowNode member = statementHandler.getFlowNodeForElement(assignment);
		if (member.isFromAlreadySeen()) {
			return member;
		}
		// TODO: Store access type
		Expression leftHandSide = assignment.getLeftHandSide();
		FlowNode leftHandFlow = handle(leftHandSide);
		if (leftHandFlow.getModelElement() instanceof FieldDeclaration) { // TODO Fix!
			statementHandler.getMemberOut().add(member); // TODO FieldDeclaration correct type to check against?
		}
		Expression rightHandSide = assignment.getRightHandSide();
		handle(rightHandSide);
		statementHandler.getFlowNodeForElement(assignment.eContainer()).addInRef(member);
		return member;
	}

	private FlowNode handle(PrefixExpression prefixExpression) {
		return handle(prefixExpression.getOperand());
	}

	private FlowNode handle(SuperMethodInvocation superMethodInvocation) {
		FlowNode member = statementHandler.getFlowNodeForElement(superMethodInvocation);
		if (member.isFromAlreadySeen()) {
			return member;
		}
		for (Expression argument : superMethodInvocation.getArguments()) {
			handle(argument);
		}
		return member;
	}

	private FlowNode handle(CastExpression castExpression) {
		return handle(castExpression.getExpression());
	}

	private FlowNode handle(ParenthesizedExpression parenthesizedExpression) {
		return handle(parenthesizedExpression.getExpression());
	}

	private FlowNode handle(ConditionalExpression conditionalExpression) {
		if (conditionalExpression == null) {
			return null; // assume nothing to do is success
		}
		FlowNode member = statementHandler.getFlowNodeForElement(conditionalExpression);
		if (member.isFromAlreadySeen()) {
			return member;
		}
		member.addInRef(handle(conditionalExpression.getExpression()));
		member.addInRef(handle(conditionalExpression.getThenExpression()));
		member.addInRef(handle(conditionalExpression.getElseExpression()));
		return member;
	}

	private FlowNode handle(ArrayAccess arrayAccess) {
		if (arrayAccess == null) {
			return null; // assume nothing to do is success;
		}
		FlowNode member = statementHandler.getFlowNodeForElement(arrayAccess);
		if (member.isFromAlreadySeen()) {
			return member;
		}
		handle(arrayAccess.getArray());
		handle(arrayAccess.getIndex());
		statementHandler.getFlowNodeForElement(arrayAccess.eContainer()).addInRef(member);
		return member;
	}
	
	// TODO: Never called?
	private FlowNode handle(FieldAccess fieldAccess) {
		if (fieldAccess == null) {
			return null; // assume nothing to do is success
		}
		FlowNode member = statementHandler.getFlowNodeForElement(fieldAccess);
		if (member.isFromAlreadySeen()) {
			return member;
		}
		handle(fieldAccess.getExpression());
		handle(fieldAccess.getField());
		statementHandler.getMemberIn().add(member);
		return member;
	}

	private FlowNode handle(MethodInvocation methodInvocation) {
		FlowNode member = statementHandler.getFlowNodeForElement(methodInvocation);
		if (member.isFromAlreadySeen()) {
			return member;
		}
		handle(methodInvocation.getExpression());
		EList<Expression> arguments = methodInvocation.getArguments();
		if (!arguments.isEmpty()) {
			for (Expression argument : arguments) {
				handle(argument); // TODO: Flow back to MI
			}
			statementHandler.getMemberOut().add(member);
		}
		AbstractMethodDeclaration calledMethod = methodInvocation.getMethod();
		statementHandler.getFlowNodeForElement(calledMethod); // Creating a FlowNode for the called method
		for (SingleVariableDeclaration param : calledMethod.getParameters()) {
			statementHandler.getFlowNodeForElement(param); // Creating a FlowNode for the method's params
		}
		if (((MethodDeclaration) calledMethod).getReturnType().getType().getName() != "void") {
			statementHandler.getMemberIn().add(member);
			statementHandler.getFlowNodeForElement(methodInvocation.eContainer()).addInRef(member);
		}
		return member;
	}
	
	private void propagateBackReadAccess(LinkedList<EObject> seenContainers, FlowNode currentNode) {
		if (!seenContainers.isEmpty()) {
			EObject currentObj = seenContainers.poll();
			FlowNode newNode = statementHandler.getFlowNodeForElement(currentObj);
			currentNode.addOutRef(newNode);
			propagateBackReadAccess(seenContainers, newNode);
		}
	}
	
	private void propagateBackWriteAccess(LinkedList<EObject> seenContainers, FlowNode currentNode) {
		if (!seenContainers.isEmpty()) {
			EObject currentObj = seenContainers.poll();
			FlowNode newNode = statementHandler.getFlowNodeForElement(currentObj);
			currentNode.addInRef(newNode);
			propagateBackWriteAccess(seenContainers, newNode);
		}
	}
	
	public StatementHandlerDataFlow getStatementHandler() {
		return statementHandler;
	}
	
	public MiscHandlerDataFlow getMiscHandler() {
		return miscHandler;
	}

}
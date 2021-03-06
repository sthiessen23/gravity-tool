/**
 */
package org.gravity.hulk.detection.metrics.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.gravity.hulk.antipatterngraph.HMetric;

import org.gravity.hulk.antipatterngraph.metrics.HIncommingInvocationMetric;
import org.gravity.hulk.antipatterngraph.metrics.MetricsFactory;

import org.gravity.hulk.detection.impl.HClassBasedMetricCalculatorImpl;

import org.gravity.hulk.detection.metrics.HIncommingInvocationCalculator;
import org.gravity.hulk.detection.metrics.MetricsPackage;

import org.gravity.typegraph.basic.TClass;
// <-- [user defined imports]
import org.gravity.typegraph.basic.TAbstractType;
import org.gravity.typegraph.basic.TAccess;
import org.gravity.typegraph.basic.TMember;
import org.gravity.typegraph.basic.impl.TMethodDefinitionImpl;
import java.util.ArrayList;
// [user defined imports] -->

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>HIncomming Invocation Calculator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class HIncommingInvocationCalculatorImpl extends HClassBasedMetricCalculatorImpl
		implements HIncommingInvocationCalculator {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HIncommingInvocationCalculatorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MetricsPackage.Literals.HINCOMMING_INVOCATION_CALCULATOR;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public HMetric calculateMetric(TClass tClass) {
		HIncommingInvocationMetric metric = MetricsFactory.eINSTANCE.createHIncommingInvocationMetric();
		metric.setTAnnotated(tClass);
		metric.setValue(calculateValue(tClass));
		getHAnnotation().add(metric);
		return metric;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public double calculateValue(TClass tClass) {
		// [user code injected with eMoflon]

		int invoc = 0;
		ArrayList<TMember> invocingMethods = new ArrayList<TMember>();
		for (TMember m : tClass.getDefines()) {
			if (m instanceof TMethodDefinitionImpl) {
				for (TAccess t : m.getAccessedBy()) {
					TMember tSource = t.getTSource();
					if (tSource == null) {
						//System.out.println(
						//"Method within Class " + tClass.getTName() + " was accessed without an Access Source");
						continue;
					}
					TAbstractType definingClass = tSource.getDefinedBy();
					if (definingClass == null) {
						//System.out.println("Method within Class " + tClass.getTName()
						//+ " was accessed by a Source without defining Class");
						continue;
					}
					if (!definingClass.equals(tClass) && !invocingMethods.contains(tSource)) {

						invocingMethods.add(tSource);
						invoc++;

					}
				}
			}
		}
		return invoc;

	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
		case MetricsPackage.HINCOMMING_INVOCATION_CALCULATOR___CALCULATE_METRIC__TCLASS:
			return calculateMetric((TClass) arguments.get(0));
		case MetricsPackage.HINCOMMING_INVOCATION_CALCULATOR___CALCULATE_VALUE__TCLASS:
			return calculateValue((TClass) arguments.get(0));
		}
		return super.eInvoke(operationID, arguments);
	}

	// <-- [user code injected with eMoflon]

	@Override
	public String getGuiName() {
		return "Incomming Invocation custom";
	}

	// [user code injected with eMoflon] -->
} //HIncommingInvocationCalculatorImpl

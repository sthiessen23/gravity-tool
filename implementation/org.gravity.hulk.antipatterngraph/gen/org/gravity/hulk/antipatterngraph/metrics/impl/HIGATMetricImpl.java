/**
 */
package org.gravity.hulk.antipatterngraph.metrics.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.gravity.hulk.antipatterngraph.impl.HMetricImpl;

import org.gravity.hulk.antipatterngraph.metrics.HIGATMetric;
import org.gravity.hulk.antipatterngraph.metrics.MetricsPackage;

import org.gravity.typegraph.basic.TVisibility;
// <-- [user defined imports]
import org.gravity.typegraph.basic.TClass;
import org.eclipse.swt.widgets.TreeItem;
// [user defined imports] -->

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>HIGAT Metric</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.gravity.hulk.antipatterngraph.metrics.impl.HIGATMetricImpl#getHMinVis <em>HMin Vis</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HIGATMetricImpl extends HMetricImpl implements HIGATMetric {
	/**
	 * The default value of the '{@link #getHMinVis() <em>HMin Vis</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHMinVis()
	 * @generated
	 * @ordered
	 */
	protected static final TVisibility HMIN_VIS_EDEFAULT = TVisibility.TPRIVATE;

	/**
	 * The cached value of the '{@link #getHMinVis() <em>HMin Vis</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHMinVis()
	 * @generated
	 * @ordered
	 */
	protected TVisibility hMinVis = HMIN_VIS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HIGATMetricImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MetricsPackage.Literals.HIGAT_METRIC;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TVisibility getHMinVis() {
		return hMinVis;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setHMinVis(TVisibility newHMinVis) {
		TVisibility oldHMinVis = hMinVis;
		hMinVis = newHMinVis == null ? HMIN_VIS_EDEFAULT : newHMinVis;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MetricsPackage.HIGAT_METRIC__HMIN_VIS, oldHMinVis,
					hMinVis));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case MetricsPackage.HIGAT_METRIC__HMIN_VIS:
			return getHMinVis();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case MetricsPackage.HIGAT_METRIC__HMIN_VIS:
			setHMinVis((TVisibility) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case MetricsPackage.HIGAT_METRIC__HMIN_VIS:
			setHMinVis(HMIN_VIS_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case MetricsPackage.HIGAT_METRIC__HMIN_VIS:
			return hMinVis != HMIN_VIS_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (hMinVis: ");
		result.append(hMinVis);
		result.append(')');
		return result.toString();
	}
	// <-- [user code injected with eMoflon]

	@Override
	public TreeItem createItemContents(TreeItem item, int style) {
		item.setText("IGAT(" + ((TClass) getTAnnotated()).getTName() + ")=" + getValue());
		return item;
	}

	// [user code injected with eMoflon] -->
} //HIGATMetricImpl

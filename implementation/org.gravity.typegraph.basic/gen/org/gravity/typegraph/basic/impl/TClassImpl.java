/**
 */
package org.gravity.typegraph.basic.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.gravity.typegraph.basic.BasicPackage;
import org.gravity.typegraph.basic.TAbstractType;
import org.gravity.typegraph.basic.TAccess;
import org.gravity.typegraph.basic.TClass;
import org.gravity.typegraph.basic.TFieldDefinition;
import org.gravity.typegraph.basic.TFieldSignature;
import org.gravity.typegraph.basic.TInterface;
import org.gravity.typegraph.basic.TMember;
import org.gravity.typegraph.basic.TMethodDefinition;
import org.gravity.typegraph.basic.TMethodSignature;
import org.gravity.typegraph.basic.TSignature;
import org.gravity.typegraph.basic.TSyntethicMethod;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>TClass</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.gravity.typegraph.basic.impl.TClassImpl#getParentClass <em>Parent Class</em>}</li>
 *   <li>{@link org.gravity.typegraph.basic.impl.TClassImpl#getChildClasses <em>Child Classes</em>}</li>
 *   <li>{@link org.gravity.typegraph.basic.impl.TClassImpl#getImplements <em>Implements</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TClassImpl extends TAbstractTypeImpl implements TClass {
	/**
	 * The cached value of the '{@link #getParentClass() <em>Parent Class</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParentClass()
	 * @generated
	 * @ordered
	 */
	protected TClass parentClass;

	/**
	 * The cached value of the '{@link #getChildClasses() <em>Child Classes</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChildClasses()
	 * @generated
	 * @ordered
	 */
	protected EList<TClass> childClasses;

	/**
	 * The cached value of the '{@link #getImplements() <em>Implements</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplements()
	 * @generated
	 * @ordered
	 */
	protected EList<TInterface> implements_;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TClassImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BasicPackage.Literals.TCLASS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TClass getParentClass() {
		if (parentClass != null && parentClass.eIsProxy()) {
			InternalEObject oldParentClass = (InternalEObject)parentClass;
			parentClass = (TClass)eResolveProxy(oldParentClass);
			if (parentClass != oldParentClass) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BasicPackage.TCLASS__PARENT_CLASS, oldParentClass, parentClass));
			}
		}
		return parentClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TClass basicGetParentClass() {
		return parentClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParentClass(TClass newParentClass, NotificationChain msgs) {
		TClass oldParentClass = parentClass;
		parentClass = newParentClass;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BasicPackage.TCLASS__PARENT_CLASS, oldParentClass, newParentClass);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setParentClass(TClass newParentClass) {
		if (newParentClass != parentClass) {
			NotificationChain msgs = null;
			if (parentClass != null)
				msgs = ((InternalEObject)parentClass).eInverseRemove(this, BasicPackage.TCLASS__CHILD_CLASSES, TClass.class, msgs);
			if (newParentClass != null)
				msgs = ((InternalEObject)newParentClass).eInverseAdd(this, BasicPackage.TCLASS__CHILD_CLASSES, TClass.class, msgs);
			msgs = basicSetParentClass(newParentClass, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BasicPackage.TCLASS__PARENT_CLASS, newParentClass, newParentClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<TClass> getChildClasses() {
		if (childClasses == null) {
			childClasses = new EObjectWithInverseResolvingEList<TClass>(TClass.class, this, BasicPackage.TCLASS__CHILD_CLASSES, BasicPackage.TCLASS__PARENT_CLASS);
		}
		return childClasses;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<TInterface> getImplements() {
		if (implements_ == null) {
			implements_ = new EObjectWithInverseResolvingEList.ManyInverse<TInterface>(TInterface.class, this, BasicPackage.TCLASS__IMPLEMENTS, BasicPackage.TINTERFACE__IMPLEMENTED_BY);
		}
		return implements_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public EList<TMethodDefinition> getDeclaredTMethodDefinitions() {
		// [user code injected with eMoflon]
		final EList<TMethodDefinition> tMethodDefinitions = new BasicEList<>();
		for (final TMember member : getDefines()) {
			if (member instanceof TMethodDefinition) {
				tMethodDefinitions.add((TMethodDefinition) member);
			}
		}
		return tMethodDefinitions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public EList<TMember> getAllTMembers() {
		// [user code injected with eMoflon]

		final EList<TMember> allMembers = new BasicEList<>();
		TClass current = this;
		while (current != null) {
			for (final TMember member : current.getDefines()) {
				EList<?> redefinedBy;
				if (member instanceof TMethodDefinition) {
					final TMethodDefinition method = (TMethodDefinition) member;
					redefinedBy = method.getOverriddenBy();
				} else if (member instanceof TFieldDefinition) {
					final TFieldDefinition field = (TFieldDefinition) member;
					redefinedBy = field.getHiddenBy();
				} else if (member instanceof TSyntethicMethod) {
					// Ignore synthetic methods
					continue;
				} else {
					throw new RuntimeException("Unknown TMember subtype.");
				}
				boolean contained = false;
				for (final Object o : redefinedBy) {
					contained |= allMembers.contains(o);
				}
				if (!contained) {
					allMembers.add(member);
				}
			}
			current = current.getParentClass();
		}
		return allMembers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public EList<TClass> getAllChildren() {
		// [user code injected with eMoflon]
		final EList<TClass> children = new BasicEList<>();
		for (final TClass child : getChildClasses()) {
			children.add(child);
			children.addAll(child.getChildClasses());
		}
		return children;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public boolean isSuperTypeOf(final TAbstractType tType) {
		if(equals(tType)) {
			return true;
		}
		if (tType instanceof TClass) {
			final TClass tParentClass = ((TClass) tType).getParentClass();
			if (tParentClass == null) {
				return false;
			}
			if (tParentClass.equals(this)) {
				return true;
			}
			return isSuperTypeOf(tParentClass);
		} else if (tType instanceof TInterface) {
			final EList<TInterface> tInterfaces = getImplements();
			if (tInterfaces.contains(tType)) {
				return true;
			} else {
				for (final TInterface tInterface : tInterfaces) {
					if (tInterface.isSuperTypeOf(tType)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public EList<TMember> getAllOutgoingAccesses(final TSignature signature) {
		// [user code injected with eMoflon]

		EList<? extends TMember> definitions = null;
		if (signature instanceof TFieldSignature) {
			final TFieldSignature tFieldSignature = (TFieldSignature) signature;
			definitions = tFieldSignature.getDefinitions();
		} else if (signature instanceof TMethodSignature) {
			final TMethodSignature tMethodSignature = (TMethodSignature) signature;
			definitions = tMethodSignature.getDefinitions();
		}

		final EList<TMember> returnList = new BasicEList<>();
		for (final TMember tDefinition : definitions) {
			if (this.equals(tDefinition.getDefinedBy())) {
				for (final TAccess tAccess : tDefinition.getTAccessing()) {
					returnList.add(tAccess.getTTarget());
				}
			}

		}
		return returnList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public boolean hasAParentThisTMember(final TMember member) {
		// [user code injected with eMoflon]
		final TClass parent = getParentClass();
		if (parent != null && !parent.equals(this)) {
			if (parent.hasTMember(member)) {
				return true;
			}
			return parent.hasAParentThisTMember(member);
		}
		return false;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public TClass getResolvedParentClass() {
		// [user code injected with eMoflon]
		if (this.parentClass != null) {
			return this.parentClass;
		}
		final TAbstractType object = getPg().getType("java.lang.Object");
		if (object != null) {
			return (TClass) object;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BasicPackage.TCLASS__PARENT_CLASS:
				if (parentClass != null)
					msgs = ((InternalEObject)parentClass).eInverseRemove(this, BasicPackage.TCLASS__CHILD_CLASSES, TClass.class, msgs);
				return basicSetParentClass((TClass)otherEnd, msgs);
			case BasicPackage.TCLASS__CHILD_CLASSES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getChildClasses()).basicAdd(otherEnd, msgs);
			case BasicPackage.TCLASS__IMPLEMENTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getImplements()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BasicPackage.TCLASS__PARENT_CLASS:
				return basicSetParentClass(null, msgs);
			case BasicPackage.TCLASS__CHILD_CLASSES:
				return ((InternalEList<?>)getChildClasses()).basicRemove(otherEnd, msgs);
			case BasicPackage.TCLASS__IMPLEMENTS:
				return ((InternalEList<?>)getImplements()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case BasicPackage.TCLASS__PARENT_CLASS:
				if (resolve) return getParentClass();
				return basicGetParentClass();
			case BasicPackage.TCLASS__CHILD_CLASSES:
				return getChildClasses();
			case BasicPackage.TCLASS__IMPLEMENTS:
				return getImplements();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case BasicPackage.TCLASS__PARENT_CLASS:
				setParentClass((TClass)newValue);
				return;
			case BasicPackage.TCLASS__CHILD_CLASSES:
				getChildClasses().clear();
				getChildClasses().addAll((Collection<? extends TClass>)newValue);
				return;
			case BasicPackage.TCLASS__IMPLEMENTS:
				getImplements().clear();
				getImplements().addAll((Collection<? extends TInterface>)newValue);
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
			case BasicPackage.TCLASS__PARENT_CLASS:
				setParentClass((TClass)null);
				return;
			case BasicPackage.TCLASS__CHILD_CLASSES:
				getChildClasses().clear();
				return;
			case BasicPackage.TCLASS__IMPLEMENTS:
				getImplements().clear();
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
			case BasicPackage.TCLASS__PARENT_CLASS:
				return parentClass != null;
			case BasicPackage.TCLASS__CHILD_CLASSES:
				return childClasses != null && !childClasses.isEmpty();
			case BasicPackage.TCLASS__IMPLEMENTS:
				return implements_ != null && !implements_.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case BasicPackage.TCLASS___GET_DECLARED_TMETHOD_DEFINITIONS:
				return getDeclaredTMethodDefinitions();
			case BasicPackage.TCLASS___GET_ALL_TMEMBERS:
				return getAllTMembers();
			case BasicPackage.TCLASS___GET_ALL_CHILDREN:
				return getAllChildren();
			case BasicPackage.TCLASS___GET_ALL_OUTGOING_ACCESSES__TSIGNATURE:
				return getAllOutgoingAccesses((TSignature)arguments.get(0));
			case BasicPackage.TCLASS___HAS_APARENT_THIS_TMEMBER__TMEMBER:
				return hasAParentThisTMember((TMember)arguments.get(0));
			case BasicPackage.TCLASS___GET_RESOLVED_PARENT_CLASS:
				return getResolvedParentClass();
			case BasicPackage.TCLASS___TO_STRING:
				return toString();
			case BasicPackage.TCLASS___GET_PARENTS:
				return getParents();
		}
		return super.eInvoke(operationID, arguments);
	}
	// <-- [user code injected with eMoflon]

	@Override
	public String toString() {
		final String string = super.toString();
		return string.substring(0, string.length() - 1).concat(", name: ").concat(this.tName).concat(")");
	}

	@Override
	public EList<TClass> getParents() {
		final EList<TClass> parents = new BasicEList<>();
		TClass parent = this.getParentClass();
		while (parent != null) {
			parents.add(parent);
			parent = parent.getParentClass();
		}
		return parents;
	}

	@Override
	public boolean hasCommonSuperType(final TAbstractType tAbstractType) {
		if (!(tAbstractType instanceof TClassImpl)) {
			return false;
		}
		final TClassImpl tClass = (TClassImpl) tAbstractType;

		final List<TClass> parents = this.getParents();
		parents.add(this);
		final List<TClass> otherParents = tClass.getParents();
		otherParents.add(tClass);

		return (!Collections.disjoint(parents, otherParents));
	}

	// [user code injected with eMoflon] -->
} //TClassImpl

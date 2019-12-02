package org.gravity.refactorings.ui.handler.jobs;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.gravity.eclipse.GravityActivator;
import org.gravity.eclipse.converter.IPGConverter;
import org.gravity.eclipse.exceptions.NoConverterRegisteredException;
import org.gravity.eclipse.util.JavaASTUtil;
import org.gravity.refactorings.RefactoringFailedException;
import org.gravity.refactorings.impl.PullUpMethod;
import org.gravity.refactorings.ui.Messages;
import org.gravity.refactorings.ui.dialogs.RefactoringDialog;
import org.gravity.typegraph.basic.TClass;
import org.gravity.typegraph.basic.TMethodSignature;
import org.gravity.typegraph.basic.TParameter;
import org.gravity.typegraph.basic.TypeGraph;

/**
 * A job for executing a pull up method refactoring
 *
 * @author speldszus
 *
 */
public final class PullUpMethodJob extends WorkspaceJob {

	private final Shell shell;

	private final TypeDeclaration childType;
	private final MethodDeclaration method;
	private final ICompilationUnit icu;

	public PullUpMethodJob(MethodDeclaration method, TypeDeclaration childType, ICompilationUnit icu, Shell shell) {
		super(Messages.pullUpUMethod);
		this.childType = childType;
		this.shell = shell;
		this.method = method;
		this.icu = icu;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		IPGConverter converter;
		try {
			converter = GravityActivator.getDefault().getConverter(this.icu.getJavaProject().getProject());
		} catch (final NoConverterRegisteredException e) {
			return new Status(Status.ERROR, GravityActivator.PLUGIN_ID, Messages.installConverter);
		}
		if (!converter.convertProject(this.icu.getJavaProject(), monitor)) {
			asyncPrintError(this.shell, Messages.refactoringError, Messages.createPMFailed);

		}
		final TypeGraph pg = converter.getPG();

		final TClass tChild = JavaASTUtil.getTClass(this.childType, pg);
		if (tChild != null) {
			final TClass tParent = tChild.getParentClass();
			final TMethodSignature tSignature = JavaASTUtil.getTMethodSignature(this.method, pg);

			final PullUpMethod refactoring = new PullUpMethod();

			if (refactoring.isApplicable(tSignature, tParent)) {
				Display.getDefault().asyncExec(() -> {
					final Dialog dialog = new RefactoringDialog(PullUpMethodJob.this.shell, Messages.executeRefactoring,
							getPUMMessage(tParent, tSignature));
					final int status = dialog.open();

					if (status == 0) {
						converter.syncProjectBwd(SynchronizationHelper -> {
							try {
								refactoring.perform(tSignature, tParent);
							} catch (final RefactoringFailedException e) {
								asyncPrintError(PullUpMethodJob.this.shell, Messages.refactoringNotPossible,
										Messages.pullUpMethodFailed);
							}
						}, monitor);
					}
				});
			} else {
				asyncPrintError(this.shell, Messages.refactoringNotPossible, Messages.pullUpMethodNotPossible);
			}

		} else {
			asyncPrintError(this.shell, Messages.refactoringInfo, Messages.classNotFound);
		}
		return Status.OK_STATUS;
	}

	private void asyncPrintError(Shell shell, String title, String message) {
		Display.getDefault().asyncExec(() -> MessageDialog.openError(shell, title, message));
	}

	static String getPUMMessage(TClass tParent, TMethodSignature tSignature) {
		StringBuilder builder = new StringBuilder(200).append(
				"All access dependencies have been checked successfully,\nplease check if all implementations of the method\n\n\t")
				.append(tSignature.getMethod().getTName()).append('(');
		TParameter param = tSignature.getFirstParameter();
		while (param != null) {
			builder.append(param.getType().getTName());
			param = param.getNext();
			if (param != null) {
				builder.append(", ");
			}
		}
		builder = builder.append(")\n\n").append("in the classes\n\n");
		for (final TClass c : tParent.getChildClasses()) {
			builder = builder.append('\t').append(c.getFullyQualifiedName()).append('\n');
		}
		builder.append("\nhave an equivalent implementation.");
		return builder.toString();
	}

}

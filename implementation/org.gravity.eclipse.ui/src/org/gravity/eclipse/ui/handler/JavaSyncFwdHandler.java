package org.gravity.eclipse.ui.handler;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.gravity.eclipse.GravityActivator;
import org.gravity.eclipse.Messages;
import org.gravity.eclipse.converter.IPGConverter;

public class JavaSyncFwdHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ISelectionService service = window.getSelectionService();
		IStructuredSelection structured = (IStructuredSelection) service.getSelection();
		List<Object> selection = Arrays.asList(structured.toArray());

		Job job = new Job("Sync PG") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				for (Object entry : selection) {
					if (entry instanceof IResource) {
						throw new RuntimeException(Messages.JavaParseHandler_0 + entry);
					} else if (entry instanceof IJavaProject) {
						IJavaProject iJavaProject = (IJavaProject) entry;
						IPGConverter converter = GravityActivator.getDefault().getConverter(iJavaProject.getProject());
						if (!converter.syncProjectFwd(monitor)) {
							throw new RuntimeException("No PG has been created");
						}
					} else if (entry instanceof IPackageFragment) {
						throw new RuntimeException(Messages.JavaParseHandler_1 + entry);
					} else {
						throw new RuntimeException(Messages.JavaParseHandler_2 + entry);
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();

		return null;

	}

	@Override
	public boolean isEnabled() {
		return GravityActivator.getDefault().getSelectedConverterFactory().supportsFWDSync();
	}

	@Override
	public boolean isHandled() {
		return GravityActivator.getDefault().getSelectedConverterFactory().supportsFWDSync();
	}

}
package org.gravity.eclipse.ui.handler;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.gravity.eclipse.GravityActivator;
import org.gravity.eclipse.importer.ImportException;
import org.gravity.eclipse.importer.NoRootFolderException;
import org.gravity.eclipse.importer.gradle.GradleImport;
import org.gravity.eclipse.importer.maven.MavenImport;

/**
 * A handler for importing gradle projects as single eclipse project into the
 * workspace
 *
 * @author speldszus
 *
 */
public class ProjectImportHandler extends AbstractHandler {

	private static final Logger LOGGER = Logger.getLogger(ProjectImportHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final FileDialog dialog = new FileDialog(workbench.getActiveWorkbenchWindow().getShell());
		dialog.setFilterExtensions(new String[] { "*.gradle", "pom.xml" });
		final String result = dialog.open();

		final File parentFile = new File(result).getParentFile();

		final Job job;
		if(result.endsWith(".gradle")) {
			job = new GradleImportJob(parentFile);
		}
		else if(result.endsWith("pom.xml")) {
			job = new MavenImportJob(parentFile);
		}
		else {
			throw new ExecutionException("No supported root file has been selected: \""+result+'\"');
		}
		job.setUser(true);
		job.schedule();
		return null;
	}

	private final class GradleImportJob extends Job {
	
		private final File parentFile;
	
		private GradleImportJob(File parentFile) {
			super("Import gradle project");
			this.parentFile = parentFile;
		}
	
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				final IJavaProject project = new GradleImport(this.parentFile, true).importProject(monitor);
				if (project != null) {
					project.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
				}
				return Status.OK_STATUS;
			} catch (final CoreException e) {
				LOGGER.warn(e);
				return new Status(IStatus.WARNING, GravityActivator.PLUGIN_ID,
						"Refreshing the workspace failed a manual refresh can be necessary!", e);
			} catch (final NoRootFolderException e) {
				LOGGER.log(Level.ERROR, e);
				return new Status(IStatus.ERROR, GravityActivator.PLUGIN_ID, "The import of the gradle project at \""
						+ this.parentFile.getPath() + "\" failed. The root of the gradle project couldn't be found!", e);
			} catch (IOException | ImportException e) {
				LOGGER.log(Level.ERROR, e);
				return new Status(IStatus.ERROR, GravityActivator.PLUGIN_ID,
						"The import of the gradle project at \"" + this.parentFile.getPath() + "\" failed.", e);
			}
		}
	}
	
	private final class MavenImportJob extends Job {
		
		private final File parentFile;
	
		private MavenImportJob(File parentFile) {
			super("Import maven project");
			this.parentFile = parentFile;
		}
	
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				final IJavaProject project = new MavenImport(this.parentFile, true).importProject(monitor);
				if (project != null) {
					project.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
				}
				return Status.OK_STATUS;
			} catch (final CoreException e) {
				LOGGER.warn(e);
				return new Status(IStatus.WARNING, GravityActivator.PLUGIN_ID,
						"Refreshing the workspace failed a manual refresh can be necessary!", e);
			} catch (final NoRootFolderException e) {
				LOGGER.log(Level.ERROR, e);
				return new Status(IStatus.ERROR, GravityActivator.PLUGIN_ID, "The import of the maven project at \""
						+ this.parentFile.getPath() + "\" failed. The root of the maven project couldn't be found!", e);
			} catch (ImportException e) {
				LOGGER.log(Level.ERROR, e);
				return new Status(IStatus.ERROR, GravityActivator.PLUGIN_ID,
						"The import of the maven project at \"" + this.parentFile.getPath() + "\" failed.", e);
			}
		}
	}

}

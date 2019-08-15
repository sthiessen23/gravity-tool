package org.gravity.modisco.dataflow.tests;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.gravity.eclipse.GravityActivator;
import org.gravity.modisco.MGravityModel;
import org.gravity.modisco.discovery.GravityModiscoProjectDiscoverer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * A parameterized test for testing the dataflow extension of GRaViTY
 * 
 * @author speldszus
 *
 */
@RunWith(Parameterized.class)
public class DataflowProcessingTest {

	/**
	 * The logger of this class
	 */
	private static final Logger LOGGER = Logger.getLogger(DataflowProcessingTest.class);

	private static final boolean VERBOSE = true;
	
	/**
	 * The project the tests should be performed on
	 */
	private final IJavaProject project;
	
	/**
	 * Creates a new instance of this test
	 * 
	 * @param name The name of the test
	 * @param project The project to test on
	 */
	public DataflowProcessingTest(String name, IJavaProject project) {
		this.project = project;
		GravityActivator.getDefault().setVerbose(VERBOSE);
	}
	
	/**
	 * Collects the test cases from the test workspace
	 * 
	 * @return A collection of test configurations as name - project pair
	 */
	@Parameters(name="{0}")
	public static Collection<Object[]> collectTestcases() {
		Collection<Object[]> testCases = new LinkedList<>();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		for (IProject test : projects) {
			try {
				if (test.getNature(JavaCore.NATURE_ID) != null) {
					testCases.add(new Object[] { test.getName(), JavaCore.create(test) });
				}
			} catch (CoreException e) {
				LOGGER.log(Level.ERROR, e);
			}
		}
		return testCases;
	}
	
	/**
	 * Transforms every input project and checks the created model
	 *  
	 * @throws DiscoveryException If the discovery failed
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void test() throws DiscoveryException, FileNotFoundException, IOException {
		MGravityModel pm = new GravityModiscoProjectDiscoverer().discoverMGravityModelFromProject(project, new NullProgressMonitor());
		pm.eResource().save(new FileOutputStream("out/"+pm.getName()+".xmi"), Collections.emptyMap());
		//TODO: Implement more tests
		assertNotNull(pm);
	}
}
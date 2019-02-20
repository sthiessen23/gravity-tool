package org.gravity.goblin.tests;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.jdt.core.JavaCore;
import org.gravity.eclipse.converter.IPGConverter;
import org.gravity.goblin.constraints.VisibilityConstraintCalculator;
import org.gravity.tgg.modisco.MoDiscoTGGConverterFactory;
import org.gravity.typegraph.basic.TClass;
import org.gravity.typegraph.basic.TVisibility;
import org.gravity.typegraph.basic.TypeGraph;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.gravity.goblin.tests.TestDescription.*;
import static org.junit.Assert.*;

@RunWith(value = Parameterized.class)
public class Tests {

	private static final Logger LOGGER = Logger.getLogger(Tests.class);
	
	String name;
	IProject project;
	String unitName;
	boolean precondition;
	double postcondition;
	Map<String, Object> parameters;
	
	
	
	public Tests(String name, IProject project, String unitName, boolean precondition, double postcondition, Map<String, Object> parameters){
		this.name = name;
		this.project = project;
		this.unitName = unitName;
		this.precondition = precondition;
		this.postcondition = postcondition;	
		this.parameters = parameters;
		
	}
	
	public void initializeParameters(TypeGraph pg){
		Map<String, TClass> classes = new HashMap<String, TClass>();
		Map<String, Object> methods = new HashMap<String, Object>();
		Map<String, Object> other = new HashMap<String, Object>();
		
		
		for(Entry<String, Object> param: parameters.entrySet()){
			String str = (String)param.getValue();
			int index = str.indexOf(":");
			String type = str.substring(index + 1);
			String name = str.substring(0, index);
			
			if(type.equals("TClass")){
				classes.put(param.getKey(),pg.getClass(name));
			}
			
			if(type.equals("TMethodSignature")){
				methods.put(param.getKey(), name);
			}
			
			if(type.equals("TVisibility")){
				other.put(param.getKey(), TVisibility.get((String)param.getValue()));
			}
			
		}
		
		for(Entry<String, Object> method: methods.entrySet()){
			String methodString = (String)method.getValue();
			int index = methodString.lastIndexOf(".");
			String className = methodString.substring(0, index);
			String methodName = methodString.substring(index +1);
			
			for(Entry<String, TClass> tClass : classes.entrySet()){
				if(tClass.getValue().getTName().equals(className)
						|| tClass.getValue().getFullyQualifiedName().equals(className)){
					methods.put(method.getKey(), tClass.getValue().getTMethodSignature(methodName));
					continue;
				}
			}
			
				
		}
		parameters.putAll(classes);
		parameters.putAll(methods);
		parameters.putAll(other);
	}
	
	@Test
	public void testrun(){
		HenshinExecutor executor = new HenshinExecutor();
		MoDiscoTGGConverterFactory modisco = new MoDiscoTGGConverterFactory();
		
		IPGConverter converter = modisco.createConverter(project);
		boolean success = converter.convertProject(JavaCore.create(project), new NullProgressMonitor());
		assertTrue("Could not create PG", success);
		
		TypeGraph pg = converter.getPG();	
		executor.initialize(new EGraphImpl(pg));
		initializeParameters(pg);
		boolean preConditionResult = executor.executeUnit(unitName, parameters);
	
		//boolean preConditionResult = HenshinExecutor.executeDyn2(HenshinExecutor.getDyn2Parameter(method, target, pg));
		
		assertEquals("Transformation went wrong",precondition, preConditionResult);
		assertEquals(postcondition, new VisibilityConstraintCalculator().calculate(pg), 0.0);
			
	}
	
	@Parameters(name = "{0}")
	public static Iterable<Object[]> collecTests(){
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ArrayList<Object[]> iter = new ArrayList<>();
		for (IProject test : projects) {
			try {
				if (test.getNature(JavaCore.NATURE_ID) != null) {
					test.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					test.accept(new IResourceVisitor() {
						
						@Override
						public boolean visit(IResource resource) throws CoreException {
							if(resource.getFileExtension() != null &&resource.getFileExtension().equalsIgnoreCase("JSON")){
								JSONParser parser = new JSONParser();
								IFile resourceFile = (IFile) resource;
								Reader reader = new InputStreamReader(resourceFile.getContents());
								try {
									Object json = parser.parse(reader);
									if(json == null){
										return false;
									}
									

									JSONObject jsonObject = (JSONObject) json;
									
									ArrayList<Object> testProject = new ArrayList<Object>();
									testProject.add(test.getName()+"_"+resource.getName());
									testProject.add(test);
									testProject.add(jsonObject.get(UNITNAME));
									testProject.add(jsonObject.get(PRECONDITIONRESULT));
									testProject.add(jsonObject.get(POSTCODITIONRESULT));
									testProject.add((Map<String, Object>)jsonObject.get("Parameters"));

									iter.add(testProject.toArray());
									
								} catch (IOException e) {
									LOGGER.log(Level.ERROR, e.getMessage(), e);
								} catch (ParseException e) {
									LOGGER.log(Level.ERROR, e.getMessage(), e);
								}
							}
							else{
								return true;
							}
							return false;
						}
					});
					
				}
			} catch (CoreException e) {
				LOGGER.log(Level.ERROR, e.getMessage(), e);
			}
		}
		return iter;
	}
	
	
	
}

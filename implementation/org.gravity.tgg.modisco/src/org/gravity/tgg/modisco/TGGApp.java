package org.gravity.tgg.modisco;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmt.modisco.java.emf.JavaPackage;
import org.emoflon.ibex.tgg.operational.csp.constraints.factories.modisco.UserDefinedRuntimeTGGAttrConstraintFactory;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;
import org.emoflon.ibex.tgg.runtime.engine.DemoclesTGGEngine;
import org.gravity.modisco.ModiscoPackage;
import org.gravity.typegraph.basic.BasicPackage;

public class TGGApp extends SYNC {

	private static final String MODISCO_FLATTENED_TGG_XMI = "platform:/plugin/org.gravity.tgg.modisco/model/Modisco_flattened.tgg.xmi";
	private static final String MODISCO_ECORE = "platform:/plugin/org.gravity.tgg.modisco/model/Modisco.ecore";
	private static final String MODISCO_TGG_XMI = "platform:/plugin/org.gravity.tgg.modisco/model/Modisco.tgg.xmi";

	public TGGApp() throws IOException {
		super(createIbexOptions());
		registerBlackInterpreter(new DemoclesTGGEngine());
	}

	
	@Override
	public void loadModels() throws IOException {
		s = createResource(options.projectPath() + "/instances/src.xmi");
		t = createResource(options.projectPath() + "/instances/trg.xmi");
		c = createResource(options.projectPath() + "/instances/corr.xmi");
		p = createResource(options.projectPath() + "/instances/protocol.xmi");
	}
	
	@Override
	protected void registerUserMetamodels() throws IOException {
		registerPackage(JavaPackage.eINSTANCE);
		
//		rs.getPackageRegistry().put("platform:/resource/org.gravity.modisco/model/Modisco.ecore", ModiscoPackage.eINSTANCE);
		registerPackage(ModiscoPackage.eINSTANCE);
		
//		rs.getPackageRegistry().put("platform:/resource/org.gravity.typegraph.basic/model/Basic.ecore", BasicPackage.eINSTANCE);
		registerPackage(BasicPackage.eINSTANCE);
		
		EPackage tggPackage = loadMetaModelPackage(MODISCO_ECORE);
//		rs.getPackageRegistry().put("platform:/resource/org.gravity.tgg.modisco/model/Modisco.ecore", tggPackage);
		registerPackage(tggPackage);
		options.setCorrMetamodel(tggPackage);
		EcoreUtil.resolveAll(rs);
	}

	/**
	 * @param ePackage
	 */
	private void registerPackage(EPackage ePackage) {
		rs.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		rs.getResources().remove(ePackage.eResource());
	}

	/**
	 * @param uri
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public EPackage loadMetaModelPackage(String uri) throws IOException, MalformedURLException {
		Resource tggResource = loadResource(uri);
		EPackage tggPackage = (EPackage) tggResource.getContents().get(0);
		return tggPackage;
	}

	@Override
	public Resource loadResource(String uri) throws IOException, MalformedURLException {
		Resource resource = rs.createResource(URI.createURI(uri));
		InputStream tggRulesStream = new URL(uri)
				.openConnection().getInputStream();
		resource.load(tggRulesStream,Collections.emptyMap());
		EcoreUtil.resolveAll(resource);
		return resource;
	}
	
	@Override
	protected Resource loadTGGResource() throws IOException {
		return loadResource(MODISCO_TGG_XMI);
	}
	
	@Override
	protected Resource loadFlattenedTGGResource() throws IOException {
		return loadResource(MODISCO_FLATTENED_TGG_XMI);
	}

	private static IbexOptions createIbexOptions() {
		IbexOptions options = new IbexOptions();
		options.projectName("Modisco");
		options.projectPath("org.gravity.tgg.modisco");
		options.debug(false);
		options.userDefinedConstraints(new UserDefinedRuntimeTGGAttrConstraintFactory());
		return options;
	}
}
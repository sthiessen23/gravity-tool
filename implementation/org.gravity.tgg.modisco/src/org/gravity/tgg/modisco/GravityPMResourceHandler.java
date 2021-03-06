package org.gravity.tgg.modisco;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.modisco.java.emf.JavaPackage;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;
import org.gravity.eclipse.GravityActivator;
import org.gravity.modisco.ModiscoPackage;
import org.gravity.typegraph.basic.BasicPackage;

import language.TGG;
import runtime.RuntimePackage;

final class GravityPMResourceHandler extends TGGResourceHandler {

	static final String PLATFORM_RESOURCE = "platform:/resource/"; //$NON-NLS-1$

	static final String MODISCO_FLATTENED_TGG_XMI_LOCATION = "model/Modisco_flattened.tgg.xmi"; //$NON-NLS-1$
	static final String MODISCO_FLATTENED_TGG_XMI_URI = PLATFORM_RESOURCE + MoDiscoTGGActivator.PLUGIN_ID + "/" //$NON-NLS-1$
			+ MODISCO_FLATTENED_TGG_XMI_LOCATION;
	static final String MODISCO_ECORE_LOCATION = "model/Modisco.ecore"; //$NON-NLS-1$
	static final String MODISCO_ECORE_URI = PLATFORM_RESOURCE + MoDiscoTGGActivator.PLUGIN_ID + "/" //$NON-NLS-1$
			+ MODISCO_ECORE_LOCATION;
	static final String MODISCO_TGG_XMI_LOCATION = "model/Modisco.tgg.xmi"; //$NON-NLS-1$
	static final String MODISCO_TGG_XMI_URI = PLATFORM_RESOURCE + MoDiscoTGGActivator.PLUGIN_ID + "/" //$NON-NLS-1$
			+ MODISCO_TGG_XMI_LOCATION;

	private final IProject project;

	GravityPMResourceHandler(IProject project) throws IOException {
		this.project = project;
	}

	@Override
	public void loadModels() throws IOException {
		String name = project.getName();
		final String gravityFolder = PLATFORM_RESOURCE + name + '/' + GravityActivator.GRAVITY_FOLDER_NAME + '/';
		this.source = createResource(gravityFolder + "src.xmi"); //$NON-NLS-1$
		this.target = createResource(gravityFolder + name + ".xmi"); //$NON-NLS-1$
		this.corr = createResource(gravityFolder + "corr.xmi"); //$NON-NLS-1$
		this.protocol = createResource(gravityFolder + "protocol.xmi"); //$NON-NLS-1$
	}

	@Override
	protected void registerUserMetamodels() throws IOException {
		assert rs.getPackageRegistry().containsKey(RuntimePackage.eINSTANCE.getNsURI());
		assert rs.getPackageRegistry().containsKey(JavaPackage.eINSTANCE.getNsURI());
		assert rs.getPackageRegistry().containsKey(ModiscoPackage.eINSTANCE.getNsURI());
		assert rs.getPackageRegistry().containsKey(BasicPackage.eINSTANCE.getNsURI());
		this.options.tgg.corrMetamodel(((TGG)loadTGGResource().getContents().get(0)).getCorr());
		EcoreUtil.resolveAll(this.rs);
	}

	@Override
	public Resource loadResource(final String uri) throws IOException {
		return rs.getResource(URI.createURI(uri), true);
	}

	/**
	 * Loads a resource from the given input stream under the given URI
	 *
	 * @param uri    The URI of the resource
	 * @param stream The stream containing the resources contents
	 * @return The loaded resource
	 * @throws IOException If the resource couldn't be loaded
	 */
	private Resource loadResource(final String uri, final InputStream stream) throws IOException {
		final Resource resource = this.rs.createResource(URI.createURI(uri));
		resource.load(stream, Collections.emptyMap());
		return resource;
	}

	@Override
	protected Resource loadTGGResource() throws IOException {
		try (InputStream stream = MoDiscoTGGActivator.getEntryAsStream(MODISCO_TGG_XMI_LOCATION)) {
			return loadResource(MODISCO_TGG_XMI_URI, stream);
		}
	}

	@Override
	protected Resource loadFlattenedTGGResource() throws IOException {
		try (InputStream stream = MoDiscoTGGActivator.getEntryAsStream(MODISCO_FLATTENED_TGG_XMI_LOCATION)) {
			return loadResource(MODISCO_FLATTENED_TGG_XMI_URI, stream);
		}
	}
}
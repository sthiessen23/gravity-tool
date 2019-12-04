package org.gravity.tgg.modisco;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.gravity.eclipse.converter.IPGConverter;
import org.gravity.eclipse.converter.IPGConverterFactory;

/**
 * The factory for creating MoDisco TGG converters for projects.
 *
 * @author speldszus
 *
 */
public class MoDiscoTGGConverterFactory implements IPGConverterFactory {

	/**
	 * The logger of this class
	 */
	private static final Logger LOGGER = Logger.getLogger(MoDiscoTGGConverterFactory.class);

	@Override
	public IPGConverter createConverter(IProject project) {
		try {
			final MoDiscoTGGConverter converter = new MoDiscoTGGConverter();
			MoDiscoTGGActivator.getDefault().addConverter(converter);
			return converter;
		} catch (final IOException e) {
			LOGGER.error(e);
		}
		return null;
	}

	@Override
	public String getName() {
		return "MoDisco Converter";
	}

	@Override
	public String getDescription() {
		return "This converter uses MoDisco as intermidiate representation.";
	}

	@Override
	public boolean belongsToFactory(IPGConverter converter) {
		return MoDiscoTGGConverter.class.isInstance(converter);
	}

	@Override
	public boolean supportsFWDTrafo() {
		return true;
	}

	@Override
	public boolean supportsBWDTrafo() {
		return false;
	}

	@Override
	public boolean supportsFWDSync() {
		final MoDiscoTGGActivator activator = MoDiscoTGGActivator.getDefault();
		if (activator == null) {
			LOGGER.log(Level.ERROR, "The modisco TGG activator is null");
			return false;
		}
		return activator.getSelectedPatcher() != null;
	}

	@Override
	public boolean supportsBWDSync() {
		return true;
	}

}

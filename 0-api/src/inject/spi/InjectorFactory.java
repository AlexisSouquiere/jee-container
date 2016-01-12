package inject.spi;

import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InjectorFactory {

	private final static Logger LOGGER = Logger.getLogger(InjectorFactory.class.getName());

	public static IInjector createInjector() {
		LOGGER.log(Level.INFO, "Loading " + IInjector.class.getName());
		ServiceLoader<IInjector> factory = ServiceLoader.load(IInjector.class);

		IInjector injector = factory.iterator().next();

		return injector;
	}

}

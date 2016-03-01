package inject.spi;

import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InjectorFactory {

	private final static Logger LOGGER = Logger.getLogger(InjectorFactory.class.getName());

	private static IInjector injector = null;

	public static IInjector createInjector() {
		if(injector == null) {
			LOGGER.log(Level.INFO, "Loading " + IInjector.class.getName());
			ServiceLoader<IInjector> factory = ServiceLoader.load(IInjector.class);

			injector = factory.iterator().next();
		}

		LOGGER.log(Level.INFO, "Injector " + injector.getClass().getName() + " successfully instantiated");

		return injector;
	}

}

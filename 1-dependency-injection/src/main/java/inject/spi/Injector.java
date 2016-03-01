package inject.spi;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import inject.api.annotations.Inject;

public class Injector extends AbstractInjector {
	private final static Logger LOGGER = Logger.getLogger(Injector.class.getName());

	public void inject(Object instance) {
		LOGGER.log(Level.INFO, "Getting all the fiels annotating with " + Inject.class.getName());
		Field[] fields = instance.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (f.isAnnotationPresent(Inject.class)) {
				Class<?> klass = mapInterfaceClass.get(f.getType());

				try {
					Object service = klass.newInstance();

					LOGGER.log(Level.INFO,
							"Injecting instance of " + klass.getName() + " in " + instance.getClass().getName());

					f.setAccessible(true);
					f.set(instance, service);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}

package inject.spi;

import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.withAnnotation;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.reflections.Reflections;

import inject.api.annotations.Inject;
import inject.api.annotations.Prefered;
import inject.examples.BootstrapedInjection;

public class BootstrapInjector extends AbstractInjector {

	private final static Logger LOGGER = Logger.getLogger(BootstrapedInjection.class.getName());

	// TODO: faire le bind depuis le constructeur sur toutes les @Inject de
	// toutes les classes du classpath
	// Utilisation collector API Reflections

	@SuppressWarnings("unchecked")
	public <T> void inject(Object instance) {
		Reflections reflections = new Reflections("inject");

		LOGGER.log(Level.INFO, "Getting all the fiels annotating with " + Inject.class.getName() + " in "
				+ instance.getClass().getName());
		Set<Field> fields = getAllFields(instance.getClass(), withAnnotation(Inject.class));

		for (Field f : fields) {
			Class<T> iClass = (Class<T>) f.getType(); // interface

			// Bind if the association has not already been done
			if (map.get(iClass) == null) {
				// Fetch all the implementation of the interface
				Set<Class<? extends T>> subTypes = (Set<Class<? extends T>>) reflections.getSubTypesOf(iClass);

				if (subTypes.size() == 0) {
					throw new RuntimeException("Unable to get a subclass of type " + iClass.getName());
				}

				// Get the right implementation from all the implementations of
				// the interface
				Class<? extends T> subClass = null;
				// Only one implementation
				if (subTypes.size() == 1) {
					subClass = (Class<? extends T>) subTypes.toArray()[0];
				}
				// More than 1 implementation : need to filter on @Prefered
				else {
					for (Class<? extends T> klass : subTypes) {
						if (klass.isAnnotationPresent(Prefered.class)) {
							subClass = klass;
							break;
						}
					}

					if (subClass == null) {
						throw new RuntimeException("Several implementations were found. Use @Prefered to choose one");
					}
				}

				this.bind(iClass, subClass);
			}

			try {
				Object dependency = map.get(iClass);

				// Inject dependencies recursively
				this.inject(dependency);

				// Process the injection
				LOGGER.log(Level.INFO,
						"Injecting " + dependency.getClass().getName() + " into " + instance.getClass().getName());
				// Inject dependency into the instance
				f.setAccessible(true);
				f.set(instance, dependency);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}

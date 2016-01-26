package inject.spi;

import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.withAnnotation;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import inject.api.annotations.Inject;
import inject.api.annotations.Prefered;
import inject.examples.BootstrapedInjection;

public class BootstrapInjector extends AbstractInjector {

	private final static Logger LOGGER = Logger.getLogger(BootstrapedInjection.class.getName());

	// Proxy avec Before/After / API Proxy de Java

	public <T> BootstrapInjector() {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage("inject")).setScanners(new FieldAnnotationsScanner()));
		// Get all the fields annotated with @Inject in the classpath
		Set<Field> fieldsToInject = reflections.getFieldsAnnotatedWith(Inject.class);

		for (Field fieldToInject : fieldsToInject) {
			Class<?> klass = fieldToInject.getDeclaringClass();
			if (map.get(klass) == null) {
				injectDependencies(klass);
			}
		}
	}

	private <T> void injectDependencies(Class<?> klass) {
		Reflections reflections = new Reflections("inject");

		LOGGER.log(Level.INFO,
				"Getting all the fiels annotating with " + Inject.class.getName() + " in " + klass.getName());
		Set<Field> fields = getAllFields(klass, withAnnotation(Inject.class));

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
				} else {
					// More than 1 implementation : need to filter on @Prefered
					subClass = resolveMultipleImplementations(subTypes);
				}

				this.bind(iClass, subClass);
			}
		}
	}

	private <T> Class<? extends T> resolveMultipleImplementations(Set<Class<? extends T>> subTypes) {
		Class<? extends T> subClass = null;

		for (Class<? extends T> subType : subTypes) {
			if (subType.isAnnotationPresent(Prefered.class)) {
				subClass = subType;
				break;
			}
		}

		if (subClass == null) {
			throw new RuntimeException("Several implementations were found. Use @Prefered to choose one");
		}

		return subClass;
	}

	@SuppressWarnings("unchecked")
	public <T> void inject(Object instance) {
		LOGGER.log(Level.INFO, "Getting all the fiels annotating with " + Inject.class.getName() + " in "
				+ instance.getClass().getName());
		Set<Field> fields = getAllFields(instance.getClass(), withAnnotation(Inject.class));

		for (Field f : fields) {
			Class<T> iClass = (Class<T>) f.getType(); // interface

			try {
				Object dependency = map.get(iClass);

				// Inject dependencies recursively
				this.inject(dependency);
				this.processInjection(instance, f, dependency);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected void processInjection(Object instance, Field f, Object dependency)
			throws IllegalArgumentException, IllegalAccessException {
		// Process the injection
		LOGGER.log(Level.INFO,
				"Injecting " + dependency.getClass().getName() + " into " + instance.getClass().getName());
		// Inject dependency into the instance
		f.setAccessible(true);
		f.set(instance, dependency);
	}
}
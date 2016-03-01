package inject.spi;

import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import inject.api.annotations.PostConstruct;
import inject.api.annotations.Singleton;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import inject.api.annotations.Inject;
import inject.api.annotations.Prefered;

public class BootstrapInjector extends AbstractInjector {

	private final static Logger LOGGER = Logger.getLogger(BootstrapInjector.class.getName());

	protected Map<Class<?>, Object> mapSingletons = new HashMap<Class<?>, Object>();

    /**
     * BootstrapInject constructor
     *
     * Scan the classpath to find every class field annotated with @Inject in order
     * to create the mapping between the interface and the implementation
     *
     * @param <T>
     */
	public <T> BootstrapInjector() {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage("inject")).setScanners(new FieldAnnotationsScanner()));

        LOGGER.log(Level.INFO,
                "Getting all the fields annotating with " + Inject.class.getName());

		Set<Field> fieldsToInject = reflections.getFieldsAnnotatedWith(Inject.class);

		for (Field fieldToInject : fieldsToInject) {
			Class<?> iClass = fieldToInject.getType();
            bind(iClass);
		}
	}

    /**
     * Get the implementation for a given interface and create the binding.
     * Deals with multiple implementation and @Prefered annotation
     *
     * @param iClass - the interface
     * @param <T>
     */
	private <T> void bind(Class<T> iClass) {
		Reflections reflections = new Reflections("inject");

        // Bind if the association has not already been done
        if (mapInterfaceClass.get(iClass) == null
                && mapSingletons.get(iClass) == null) {
            // Fetch all the implementation of the interface
            Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(iClass);

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

    /**
     * Get the implementation which has the @Prefered annotation
     *
     * @param subTypes - a list of implementations for an interface
     * @param <T>
     * @return the right subclass
     */
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

    /**
     * Create the mapping between the interface and the implementation.
     * Deals with normal and singleton beans
     *
     * @param iClass - the interface
     * @param implementation - the implementation
     * @param <T>
     * @return the injector
     */
    public <T> IInjector bind(Class<T> iClass, Class<? extends T> implementation) {
        boolean isSingleton = implementation.isAnnotationPresent(Singleton.class);

        try {
            if (isSingleton) {
                LOGGER.log(Level.INFO, "Creating new singleton of " + implementation.getName());
                Object singleton = implementation.newInstance();
                mapSingletons.put(iClass, singleton);
                processPostConstruct(singleton);
            } else {
                LOGGER.log(Level.INFO, "Creating mapping for interface " + iClass.getName()
                        + " with implementation" + implementation.getName());
                mapInterfaceClass.put(iClass, implementation);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to bind " + iClass.getName() + " with " + implementation.getName());
        }

        return this;
    }

	@SuppressWarnings("unchecked")
	public <T> void inject(Object instance) {
		LOGGER.log(Level.INFO, "Getting all the fields annotating with " + Inject.class.getName() + " in "
				+ instance.getClass().getName());
		Set<Field> fields = getAllFields(instance.getClass(), withAnnotation(Inject.class));

		for (Field f : fields) {
			Class<T> iClass = (Class<T>) f.getType(); // interface

			try {
				Object dependency = null;

                if(mapInterfaceClass.get(iClass) != null) {
                    dependency = mapInterfaceClass.get(iClass).newInstance();
                    // Calling @PostConstruct methods for the dependency
                    processPostConstruct(dependency);
                }
                else {
                    dependency = mapSingletons.get(iClass);
                }

				// Inject dependencies recursively
				this.inject(dependency);
				this.processInjection(instance, f, dependency);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

    /**
     * Process the injection of the dependency into the instance
     *
     * @param instance - the class where the dependency will be injected
     * @param f - the field corresponding to the dependency
     * @param dependency - the dependency to inject
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
	protected void processInjection(Object instance, Field f, Object dependency)
			throws IllegalArgumentException, IllegalAccessException {
		// Process the injection
		LOGGER.log(Level.INFO,
				"Injecting " + dependency.getClass().getName() + " into " + instance.getClass().getName());

		// Inject dependency into the instance
		f.setAccessible(true);
		f.set(instance, dependency);
	}

    /**
     * Call all the methods annotated with @PostConstruct
     *
     * @param instance
     */
    protected void processPostConstruct(Object instance) {
        Set<Method> methods = getAllMethods(instance.getClass(), withAnnotation(PostConstruct.class));

        for(Method method : methods) {
            try {
                method.invoke(instance);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING,
                        "Unable to call @PostConstruct method  " + instance.getClass().getName()
                                + "." + method.getName() + "()");
            }
        }
    }
}
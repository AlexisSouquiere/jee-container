package inject.proxy;

import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import inject.api.annotations.Inject;
import inject.api.annotations.Transactional;
import inject.spi.BootstrapInjector;

public class TransactionalInjection extends BootstrapInjector {

	private final static Logger LOGGER = Logger.getLogger(TransactionalInjection.class.getName());

	public TransactionalInjection() {

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

				if (containsTransactionalMethods(dependency)) {
					// Create the proxy
					CustomHandler handler = new CustomHandler(dependency);
					dependency = Proxy.newProxyInstance(dependency.getClass().getClassLoader(), new Class[] { iClass },
							handler);
				}

				this.processInjection(instance, f, dependency);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Class annotation or method annotation
	 * 
	 * @param instance
	 * @return
	 */
	private <T> boolean containsTransactionalMethods(Object instance) {
		Set<Method> fields = getAllMethods(instance.getClass(), withAnnotation(Transactional.class));
		return instance.getClass().isAnnotationPresent(Transactional.class) || (fields != null & !fields.isEmpty());
	}

}

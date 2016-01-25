package inject.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import inject.api.annotations.Transactional;

public class CustomHandler implements InvocationHandler {

	private final static Logger LOGGER = Logger.getLogger(CustomHandler.class.getName());

	private Object dependency;

	public CustomHandler(Object dependency) {
		this.dependency = dependency;
	}

	public void before() {
		LOGGER.log(Level.INFO, "CustomHandler.before()");
	}

	public void after() {
		LOGGER.log(Level.INFO, "CustomHandler.after()");
	}

	public void error() {
		LOGGER.log(Level.INFO, "CustomHandler.error()");
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (isAnnotationPresent(method, Transactional.class)) {
			before();

			try {
				return method.invoke(dependency, args);
			} catch (Exception e) {
				error();
				throw new RuntimeException(e);
			} finally {
				after();
			}
		} else {
			return method.invoke(dependency, args);
		}
	}

	private boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotation) throws Exception {
		Method m = dependency.getClass().getMethod(method.getName());

		return dependency.getClass().isAnnotationPresent(annotation) || m.isAnnotationPresent(annotation);
	}
}

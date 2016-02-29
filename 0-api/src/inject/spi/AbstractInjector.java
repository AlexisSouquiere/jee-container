package inject.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractInjector implements IInjector {
	private final static Logger LOGGER = Logger.getLogger(AbstractInjector.class.getName());

	protected Map<Class<?>, Class<?>> mapInterfaceClass = new HashMap<Class<?>, Class<?>>();

	public <T> IInjector bind(Class<T> iClass, Class<? extends T> implementation) {
		if (mapInterfaceClass.get(iClass) == null) {
			try {
				LOGGER.log(Level.INFO, "Creating new instance of " + implementation.getName());
				mapInterfaceClass.put(iClass, implementation);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Unable to bind " + iClass.getName() + " with " + implementation.getName());
			}
		}

		return this;
	}

	public abstract <T> void inject(Object instance);
}

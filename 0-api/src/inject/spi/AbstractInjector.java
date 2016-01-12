package inject.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractInjector implements IInjector {
	private final static Logger LOGGER = Logger.getLogger(AbstractInjector.class.getName());

	protected Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();

	public <T> IInjector bind(Class<T> klass1, Class<? extends T> klass2) {
		if (map.get(klass1) == null) {
			try {
				LOGGER.log(Level.INFO, "Creating new instance of " + klass2.getName());
				map.put(klass1, klass2.newInstance());
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Unable to bind " + klass1.getName() + " with " + klass2.getName());
			}
		}

		return this;
	}

	public abstract <T> void inject(Object instance);
}

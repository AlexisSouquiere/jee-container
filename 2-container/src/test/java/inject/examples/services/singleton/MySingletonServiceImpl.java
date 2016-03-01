package inject.examples.services.singleton;

import java.util.logging.Level;
import java.util.logging.Logger;

import inject.api.annotations.PostConstruct;
import inject.api.annotations.Singleton;
import inject.examples.PostConstructState;

@Singleton
public class MySingletonServiceImpl implements MySingletonService {
	private final static Logger LOGGER = Logger.getLogger(MySingletonServiceImpl.class.getName());

	private static int counter = 0;
	
	@PostConstruct
	public void init() {
		LOGGER.log(Level.INFO, "[PostConstruct] MySingletonServiceImpl.init()");
		counter++;
	}
	
	public void doSomething() {
		LOGGER.log(Level.INFO, "MySingletonServiceImpl.doSomething()");
	}

}

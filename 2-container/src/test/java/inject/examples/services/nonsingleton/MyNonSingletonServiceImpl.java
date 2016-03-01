package inject.examples.services.nonsingleton;

import java.util.logging.Level;
import java.util.logging.Logger;

import inject.api.annotations.PostConstruct;
import inject.api.annotations.Singleton;
import inject.examples.PostConstructState;

public class MyNonSingletonServiceImpl implements MyNonSingletonService {
	private final static Logger LOGGER = Logger.getLogger(MyNonSingletonServiceImpl.class.getName());

	private static int counter = 0;
	
	@PostConstruct
	public void init() {
		LOGGER.log(Level.INFO, "[PostConstruct] MyNonSingletonServiceImpl.init()");
		counter++;
	}
	
	public void doSomething() {
		LOGGER.log(Level.INFO, "MyNonSingletonServiceImpl.doSomething()");
	}

}

package inject.examples.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import inject.examples.MyService;

public class MyServiceImpl implements MyService {

	private final static Logger LOGGER = Logger.getLogger(MyServiceImpl.class.getName());

	public void doSomething() {
		LOGGER.log(Level.INFO, "MyServiceImpl.doSomething()");
	}

	public void doSomethingThatThrowsException() {
        throw new RuntimeException("error");
    }

}

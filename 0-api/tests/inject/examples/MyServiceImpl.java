package inject.examples;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyServiceImpl implements MyService {

	private final static Logger LOGGER = Logger.getLogger(MyServiceImpl.class.getName());

	public void doSomething() {
		LOGGER.log(Level.INFO, "MyServiceImpl.doSomething()");
	}

	public void doSomethingThatThrowsException() {
        throw new RuntimeException("error");
    }

}

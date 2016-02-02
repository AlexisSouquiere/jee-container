package inject.examples;

import java.util.logging.Level;
import java.util.logging.Logger;

import inject.api.annotations.Inject;
import inject.api.annotations.Prefered;

@Prefered
public class MyServiceImpl2 implements MyService {

	private final static Logger LOGGER = Logger.getLogger(MyServiceImpl2.class.getName());

	@Inject
	private EntityManager entityManager;

	public void doSomething() {
		LOGGER.log(Level.INFO, "MyServiceImpl2.doSomething()");
	}

	public void doSomethingThatThrowsException() {
		throw new RuntimeException("error");
	}
}

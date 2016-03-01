package inject.examples.services.nonsingleton;

import java.util.logging.Level;
import java.util.logging.Logger;

import inject.api.annotations.Inject;
import inject.api.annotations.PostConstruct;
import inject.api.annotations.Prefered;
import inject.examples.MyService;
import inject.examples.PostConstructState;

@Prefered
public class MyServiceImpl2 implements MyService {

	private final static Logger LOGGER = Logger.getLogger(MyServiceImpl2.class.getName());

	@Inject
	private EntityManager entityManager;
	
	private PostConstructState postConstructState = PostConstructState.NOT_CALLED;
	
	private static int counter = 0;

	@PostConstruct
	public void init() {
		LOGGER.log(Level.INFO, "[PostConstruct] MyServiceImpl.init()");
		postConstructState = PostConstructState.CALLED;
		counter++;
	}

	public void doSomething() {
		LOGGER.log(Level.INFO, "MyServiceImpl2.doSomething()");
	}

	public void doSomethingThatThrowsException() {
		throw new RuntimeException("error");
	}
}

package inject.examples;

import java.util.logging.Level;
import java.util.logging.Logger;

import inject.api.annotations.Inject;
import inject.api.annotations.Prefered;

@Prefered
public class MyServiceTransactionalImpl implements MyService {

	private final static Logger LOGGER = Logger.getLogger(MyServiceTransactionalImpl.class.getName());

	@Inject
	private EntityManager entityManager;

	public void doSomething() {
		LOGGER.log(Level.INFO, "MyServiceImpl.doSomething()");
		entityManager.find();
	}

}

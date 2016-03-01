package inject.examples;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import inject.api.annotations.Inject;
import inject.examples.services.MyServiceImpl;
import inject.spi.InjectorFactory;

public class HardCodedInjection {

	@Inject
	MyService service;
	
	@Before
	public void before() {
		InjectorFactory.createInjector().bind(MyService.class, MyServiceImpl.class).inject(this);
	}
	
	@Test
	public void test() {
		assertNotNull(service);
		assertTrue(service instanceof MyServiceImpl);
	}

}

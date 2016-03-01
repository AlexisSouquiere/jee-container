package inject.examples;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import inject.api.annotations.Inject;
import inject.examples.services.nonsingleton.MyNonSingletonService;
import inject.examples.services.nonsingleton.MyNonSingletonServiceImpl;
import inject.spi.InjectorFactory;

public class NonSingletonTest {
	
	@Inject
	MyNonSingletonService service;
	
	@Before
	public void before() {
		InjectorFactory.createInjector().inject(this);
		InjectorFactory.createInjector().inject(this);
	}

	@Test
	public void testInjectionWithoutSingleton() {
		// Assert
        assertThat(Whitebox.getInternalState(MyNonSingletonServiceImpl.class, "counter")).isEqualTo(2);
	}

}
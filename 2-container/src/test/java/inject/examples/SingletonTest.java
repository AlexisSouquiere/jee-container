package inject.examples;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import inject.api.annotations.Inject;
import inject.examples.services.MyServiceImpl2;
import inject.spi.InjectorFactory;

public class SingletonTest {
	
	@Inject
	MyService service;
	
	@Before
	public void before() {
		InjectorFactory.createInjector().inject(this);
		InjectorFactory.createInjector().inject(this);
	}

	@Test
	public void testInjectionWithoutSingleton() {
		// Assert
        assertThat(Whitebox.getInternalState(MyServiceImpl2.class, "counter")).isEqualTo(2);
	}

}
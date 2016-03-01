package inject.examples;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import inject.api.annotations.Inject;
import inject.examples.services.MySingletonService;
import inject.examples.services.MySingletonServiceImpl;
import inject.spi.InjectorFactory;

public class NoneSingletonTest {
	
	@Inject
	MySingletonService service;
	
	@Before
	public void before() {
		InjectorFactory.createInjector().inject(this);
		InjectorFactory.createInjector().inject(this);
	}

	@Test
	public void testSingletonInjection() {
		// Assert
        assertThat(Whitebox.getInternalState(MySingletonServiceImpl.class, "counter")).isEqualTo(1);
	}

}
package inject.examples;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import inject.api.annotations.Inject;
import inject.examples.services.nonsingleton.MyServiceImpl2;
import inject.spi.InjectorFactory;

public class PreferedTest {
	
	@Inject
	MyService service;

	@Before
	public void before() {
		InjectorFactory.createInjector().inject(this);
	}

	@Test
	public void testServicesWithPreferedAnnotation() throws Exception {
		assertThat(service).isNotNull();
		assertThat(service).isInstanceOf(MyServiceImpl2.class);
	}
}

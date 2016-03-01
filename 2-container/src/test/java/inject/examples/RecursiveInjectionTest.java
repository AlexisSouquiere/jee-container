package inject.examples;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

import inject.api.annotations.Inject;
import inject.examples.services.nonsingleton.MyServiceImpl2;
import inject.spi.InjectorFactory;

public class RecursiveInjectionTest {

	@Inject
	MyService service;

	@Before
	public void before() {
		InjectorFactory.createInjector().inject(this);
	}

	@Test
	public void testRecursiveInjection() throws Exception {
		assertThat(service).isNotNull();
		assertThat(service).hasFieldOrProperty("entityManager");

		Field field = MyServiceImpl2.class.getDeclaredField("entityManager");
		field.setAccessible(true);
		assertThat(field.get(service)).isNotNull();
	}
}

package inject.examples;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

import inject.api.annotations.Inject;
import inject.spi.InjectorFactory;

public class BootstrapedInjection {

	@Inject
	MyService service;

	@Before
	public void before() {
		InjectorFactory.createInjector().inject(this);
	}

	@Test
	public void test() throws Exception {
		assertThat(service).isNotNull();
		assertThat(service).isInstanceOf(MyServiceImpl2.class);
		assertThat(service).hasFieldOrProperty("entityManager");

		Field field = MyServiceImpl2.class.getDeclaredField("entityManager");
		field.setAccessible(true);
		assertThat(field.get(service)).isNotNull();
	}
}

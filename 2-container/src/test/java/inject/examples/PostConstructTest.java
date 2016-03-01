package inject.examples;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import inject.api.annotations.Inject;
import inject.spi.InjectorFactory;

public class PostConstructTest {
	
	@Inject
	MyService service;
	
	@Before
	public void before() {
		InjectorFactory.createInjector().inject(this);
	}

	@Test
	public void testPostConstructAnnotation() {
		// Assert
        assertThat(Whitebox.getInternalState(service, "postConstructState")).isEqualTo(PostConstructState.CALLED);
	}

}

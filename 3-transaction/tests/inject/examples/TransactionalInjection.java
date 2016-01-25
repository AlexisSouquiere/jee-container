package inject.examples;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import inject.api.annotations.Inject;
import inject.spi.InjectorFactory;

public class TransactionalInjection {

	@Inject
	MyService service;

	@Inject
	EntityManager entityManager;

	// TODO: Créer classe extends CustomHandler avec flag beforeCalled et le
	// tester

	@Before
	public void before() {
		InjectorFactory.createInjector().inject(this);

		InvocationHandler handler = Proxy.getInvocationHandler(entityManager);
		handler = PowerMockito.spy(handler);
	}

	@Test
	public void test() throws Exception {
		assertThat(service).isNotNull();
		service.doSomething();

		PowerMockito.verifyPrivate(Proxy.getInvocationHandler(entityManager), Mockito.times(1)).invoke("before");
	}
}

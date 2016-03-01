package inject.examples;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;

import inject.proxy.CustomHandler;
import inject.proxy.TransactionState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import inject.api.annotations.Inject;
import inject.spi.InjectorFactory;

public class TransactionalInjectionTest {

	@Inject
	MyService service;

	@Before
	public void before() {
		InjectorFactory.createInjector().inject(this);
	}

	@Test
	public void doSomethingTest() throws Exception {
        // Arrange
        CustomHandler handler =
                (CustomHandler) Proxy.getInvocationHandler(Whitebox.getInternalState(service, "entityManager"));

        // Act
		service.doSomething();

        // Assert
		assertThat(handler.getTransactionState()).isEqualTo(TransactionState.COMMIT);
	}

	@Test(expected = RuntimeException.class)
	public void doSomethingThatThrowsExceptionTest() throws Exception {
        // Arrange
        CustomHandler handler =
                (CustomHandler) Proxy.getInvocationHandler(Whitebox.getInternalState(service, "entityManager"));

        // Act
		service.doSomethingThatThrowsException();

        // Assert
        assertThat(handler.getTransactionState()).isEqualTo(TransactionState.ROLLBACK);
	}
}

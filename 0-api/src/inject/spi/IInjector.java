package inject.spi;

public interface IInjector {
	<T> void inject(Object instance);

	<T> IInjector bind(Class<T> klass1, Class<? extends T> klass2);
}

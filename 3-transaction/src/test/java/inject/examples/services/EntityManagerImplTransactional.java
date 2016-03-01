package inject.examples.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import inject.api.annotations.Prefered;
import inject.api.annotations.Transactional;
import inject.examples.services.EntityManager;

@Prefered
@Transactional
public class EntityManagerImplTransactional implements EntityManager {

	private final static Logger LOGGER = Logger.getLogger(EntityManagerImplTransactional.class.getName());

	public void find() {
		LOGGER.log(Level.INFO, "EntitiyManagerImpl.find()");
	}
}

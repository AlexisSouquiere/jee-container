package inject.examples.services;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EntityManagerImpl implements EntityManager {

	private final static Logger LOGGER = Logger.getLogger(EntityManagerImpl.class.getName());

	public void find() {
		LOGGER.log(Level.INFO, "EntitiyManagerImpl.find()");
	}

}

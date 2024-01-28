package ch.andre601.foxes_eat_eggs;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Deprecated, but QFAPI is not yet available for 1.20.4... Cringe
public class FoxesEatEggs implements ModInitializer{

	public static final Logger LOGGER = LoggerFactory.getLogger("Foxes Eat Eggs");

	@Override
	public void onInitialize(){
		LOGGER.info("Loading Foxes Eat Eggs...");
	}
}

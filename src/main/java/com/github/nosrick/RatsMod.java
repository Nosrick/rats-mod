package com.github.nosrick;

import com.github.nosrick.registry.EntityRegistry;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RatsMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("rats");

	public static final String MOD_ID = "rats";

	@Override
	public void onInitialize() {

		EntityRegistry.registerAll();

		LOGGER.info("Hello Fabric world!");
	}
}

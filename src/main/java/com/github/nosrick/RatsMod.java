package com.github.nosrick;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RatsMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("rats");

	public static final String MOD_ID = "rats";

	@Override
	public void onInitialize() {
		
		LOGGER.info("Hello Fabric world!");
	}
}

package com.tet;

import com.tet.effect.TetEffects;
import com.tet.item.TetItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TetMod implements ModInitializer {
	public static final String MOD_ID = "tet";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[Brake & Throttle] Copyright (c) 2026 Tea_Leaf_Fox. Licensed under CC BY-NC-SA 4.0.");

		TetEffects.initialize();
		TetItems.initialize();
		TetTickHandler.register();
	}
}
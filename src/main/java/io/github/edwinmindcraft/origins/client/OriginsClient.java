package io.github.edwinmindcraft.origins.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OriginsClient {
	/**
	 * Allow for up to a 1s delay between two packets.<br/>
	 * Forge is weird sometimes.
	 */
	public static final int DISPLAY_SCREEN_DELAY = 20;

	public static int DISPLAY_ORIGIN_SCREEN = 0;
	public static boolean SHOW_DIRT_BACKGROUND = false;
}

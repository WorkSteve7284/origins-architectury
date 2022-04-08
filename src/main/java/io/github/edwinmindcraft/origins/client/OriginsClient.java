package io.github.edwinmindcraft.origins.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
public class OriginsClient {
	public static final AtomicBoolean AWAITING_DISPLAY = new AtomicBoolean();
	public static final AtomicBoolean OPEN_NEXT_LAYER = new AtomicBoolean();
	public static boolean SHOW_DIRT_BACKGROUND = false;
}

package io.github.apace100.origins;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.origins.registry.ModBlocks;
import io.github.apace100.origins.registry.ModEntities;
import io.github.apace100.origins.util.OriginsConfigSerializer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

public class OriginsClient {

	public static KeyMapping usePrimaryActivePowerKeybind;
	public static KeyMapping useSecondaryActivePowerKeybind;
	public static KeyMapping viewCurrentOriginKeybind;

	public static ClientConfig config;

	public static boolean isServerRunningOrigins = false;

	public static void initialize() {

		AutoConfig.register(ClientConfig.class, OriginsConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();

		usePrimaryActivePowerKeybind = new KeyMapping("key.origins.primary_active", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "category." + Origins.MODID);
		useSecondaryActivePowerKeybind = new KeyMapping("key.origins.secondary_active", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category." + Origins.MODID);
		viewCurrentOriginKeybind = new KeyMapping("key.origins.view_origin", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, "category." + Origins.MODID);

		ApoliClient.registerPowerKeybinding("key.origins.primary_active", usePrimaryActivePowerKeybind);
		ApoliClient.registerPowerKeybinding("key.origins.secondary_active", useSecondaryActivePowerKeybind);
		ApoliClient.registerPowerKeybinding("primary", usePrimaryActivePowerKeybind);
		ApoliClient.registerPowerKeybinding("secondary", useSecondaryActivePowerKeybind);

		// "none" is the default key used when none is specified.
		ApoliClient.registerPowerKeybinding("none", usePrimaryActivePowerKeybind);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(OriginsClient::clientSetup);
		bus.addListener(OriginsClient::entityRenderers);
	}

	public static void clientSetup(FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPORARY_COBWEB.get(), RenderType.cutout());

		ClientRegistry.registerKeyBinding(usePrimaryActivePowerKeybind);
		ClientRegistry.registerKeyBinding(useSecondaryActivePowerKeybind);
		ClientRegistry.registerKeyBinding(viewCurrentOriginKeybind);
	}

	public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.ENDERIAN_PEARL.get(), ThrownItemRenderer::new);
	}

	@Config(name = Origins.MODID)
	public static class ClientConfig implements ConfigData {

		public int xOffset = 0;
		public int yOffset = 0;

		@ConfigEntry.BoundedDiscrete(max = 1)
		public float phantomizedOverlayStrength = 0.8F;

		@Override
		public void validatePostLoad() {
			if (phantomizedOverlayStrength < 0F) {
				phantomizedOverlayStrength = 0F;
			} else if (phantomizedOverlayStrength > 1F) {
				phantomizedOverlayStrength = 1F;
			}
		}
	}
}

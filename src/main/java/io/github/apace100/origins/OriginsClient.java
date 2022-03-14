package io.github.apace100.origins;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.origins.registry.ModBlocks;
import io.github.apace100.origins.registry.ModEntities;
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

	public static boolean isServerRunningOrigins = false;

	public static void initialize() {

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
}

package io.github.edwinmindcraft.origins.api;

import io.github.edwinmindcraft.calio.api.CalioAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class OriginsAPI {
	private static final ConcurrentHashMap<ResourceLocation, ResourceLocation> POWER_SOURCE_CACHE = new ConcurrentHashMap<>();
	public static final String MODID = "origins";

	public static final Capability<IOriginContainer> ORIGIN_CONTAINER = CapabilityManager.get(new CapabilityToken<>() {});

	public static Registry<Origin> getOriginsRegistry(MinecraftServer server) {
		return CalioAPI.getDynamicRegistries(server).get(OriginsDynamicRegistries.ORIGINS_REGISTRY);
	}

	public static Registry<Origin> getOriginsRegistry() {
		return CalioAPI.getDynamicRegistries().get(OriginsDynamicRegistries.ORIGINS_REGISTRY);
	}

	public static Registry<OriginLayer> getLayersRegistry(MinecraftServer server) {
		return CalioAPI.getDynamicRegistries(server).get(OriginsDynamicRegistries.LAYERS_REGISTRY);
	}

	public static Registry<OriginLayer> getLayersRegistry() {
		return CalioAPI.getDynamicRegistries().get(OriginsDynamicRegistries.LAYERS_REGISTRY);
	}

	public static List<OriginLayer> getActiveLayers() {
		return getLayersRegistry().stream().filter(OriginLayer::enabled).sorted().toList();
	}

	public static ResourceLocation getPowerSource(Origin origin) {
		var registryName = origin.getRegistryName();
		Validate.notNull(registryName, "Unregistered origins cannot provide powers.");
		return POWER_SOURCE_CACHE.computeIfAbsent(registryName, OriginsAPI::createPowerSource);
	}

	public static ResourceLocation getPowerSource(ResourceLocation origin) {
		Validate.notNull(origin, "Unregistered origins cannot provide powers.");
		return POWER_SOURCE_CACHE.computeIfAbsent(origin, OriginsAPI::createPowerSource);
	}

	private static ResourceLocation createPowerSource(ResourceLocation key) {
		//Fabric command compat.
		//If this were up to me, the power source would've been <namespace>:origins/<path>
		return new ResourceLocation(key.getNamespace(), key.getPath());
	}
}

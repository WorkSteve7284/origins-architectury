package io.github.apace100.origins.util;

import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class PowerKeyManager {

	private static final HashMap<ResourceLocation, String> KEY_CACHE = new HashMap<>();

	public static void clearCache() {
		KEY_CACHE.clear();
	}

	public static String getKeyIdentifier(ResourceLocation powerId) {
		if (KEY_CACHE.containsKey(powerId)) {
			return KEY_CACHE.get(powerId);
		}
		String key = getKeyFromPower(powerId);
		KEY_CACHE.put(powerId, key);
		return key;
	}

	private static String getKeyFromPower(ResourceLocation powerId) {
		if (PowerTypeRegistry.contains(powerId)) {
			Registry<ConfiguredPower<?, ?>> powers = ApoliAPI.getPowers();
			ConfiguredPower<?, ?> powerType = powers.get(powerId);
			if (powerType == null)
				return "";
			return powerType.getKey(null)
					.map(key -> key.key().equals("none") ? "key.origins.primary_active" : key.key())
					.or(() -> powerType.getContainedPowers().values().stream().map(x -> getKeyFromPower(x.getRegistryName())).filter(x -> !x.isBlank()).findFirst())
					.orElse("");
		}
		return "";
	}
}

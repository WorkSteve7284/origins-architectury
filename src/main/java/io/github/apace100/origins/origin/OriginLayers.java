package io.github.apace100.origins.origin;

import com.google.common.collect.ImmutableSet;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @deprecated Use {@link OriginsAPI#getLayersRegistry()} where possible instead.
 */
@Deprecated
public class OriginLayers {

	private static final Map<io.github.edwinmindcraft.origins.api.origin.OriginLayer, OriginLayer> CACHE_MAP = new ConcurrentHashMap<>();

	public static OriginLayer getLayer(ResourceLocation id) {
		return OriginsAPI.getLayersRegistry().getOptional(id).map(OriginLayer::new).orElse(null);
	}

	public static Collection<OriginLayer> getLayers() {
		return OriginsAPI.getLayersRegistry().stream().map(OriginLayer::new).collect(ImmutableSet.toImmutableSet());
	}

	public static int size() {
		return OriginsAPI.getLayersRegistry().keySet().size();
	}

	public static void clear() {
		CACHE_MAP.clear();
	}

	/**
	 * @deprecated Dead code.
	 */
	@Deprecated
	public static void add(OriginLayer layer) {}

	public static OriginLayer get(io.github.edwinmindcraft.origins.api.origin.OriginLayer layer) {
		return CACHE_MAP.computeIfAbsent(layer, OriginLayer::new);
	}
}

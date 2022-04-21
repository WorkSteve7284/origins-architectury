package io.github.apace100.origins.component;

import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

@SuppressWarnings("deprecation")
public interface OriginComponent {

	boolean hasOrigin(OriginLayer layer);

	boolean hasAllOrigins();

	HashMap<OriginLayer, Origin> getOrigins();

	Origin getOrigin(OriginLayer layer);

	boolean hadOriginBefore();

	void setOrigin(OriginLayer layer, Origin origin);

	void sync();

	@Deprecated(forRemoval = true)
	default void onPowersRead() {}

	static void sync(Player player) {
		IOriginContainer.get(player).ifPresent(IOriginContainer::synchronize);
	}

	static void onChosen(Player player, boolean hadOriginBefore) {
		IOriginContainer.get(player).ifPresent(x -> x.onChosen(hadOriginBefore));
	}

	static void partialOnChosen(Player player, boolean hadOriginBefore, Origin origin) {
		IOriginContainer.get(player).ifPresent(x -> x.onChosen(origin.getWrapped(), hadOriginBefore));
	}

	default boolean checkAutoChoosingLayers(Player player, boolean includeDefaults) {
		return IOriginContainer.get(player).map(x -> x.checkAutoChoosingLayers(includeDefaults)).orElse(false);
	}
}

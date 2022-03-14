package io.github.edwinmindcraft.origins.api.capabilities;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.power.OriginsCallbackPower;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.common.network.S2CSynchronizePowerContainer;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.origin.IOriginCallbackPower;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.common.network.S2CSynchronizeOrigin;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IOriginContainer extends INBTSerializable<Tag> {
	static LazyOptional<IOriginContainer> get(@Nullable Entity entity) {
		return entity != null ? entity.getCapability(OriginsAPI.ORIGIN_CONTAINER) : LazyOptional.empty();
	}

	/**
	 * Sets the origin for the given layer.
	 *
	 * @param layer  The layer to set the origin for.
	 * @param origin The origin to given to the player.
	 */
	void setOrigin(OriginLayer layer, Origin origin);

	/**
	 * Returns the origin for the given layer, or {@link Origin#EMPTY} if no origin was set for that layer.
	 *
	 * @param layer The layer to get the origin of.
	 *
	 * @return The origin for the given layer.
	 */
	Origin getOrigin(OriginLayer layer);

	/**
	 * Checks if the player has an origin for the given layer.
	 *
	 * @param layer The layer to check.
	 *
	 * @return {@code true} if the layer has an assigned origin, {@code false} if the origin is {@link Origin#EMPTY}.
	 */
	boolean hasOrigin(OriginLayer layer);

	/**
	 * Checks if this player has all origins currently assigned.
	 *
	 * @return {@code false} if any layer is empty, {@code true otherwise}.
	 */
	default boolean hasAllOrigins() {
		return OriginsAPI.getActiveLayers().stream().allMatch(this::hasOrigin);
	}

	/**
	 * Returns {@code true} if the player had all origins set at some point, {@code false} otherwise.
	 */
	boolean hadAllOrigins();

	/**
	 * Returns a read-only {@link Map} that contains all the layers and origins for the given player.
	 */
	Map<OriginLayer, Origin> getOrigins();

	/**
	 * Requests a synchronization for this component.
	 */
	void synchronize();

	/**
	 * Returns true if a synchronization should be done.
	 */
	boolean shouldSync();

	void tick();

	S2CSynchronizeOrigin getSynchronizationPacket();

	boolean checkAutoChoosingLayers(boolean includeDefaults);

	/**
	 * Executes {@link IOriginCallbackPower#onChosen(IDynamicFeatureConfiguration, Entity, boolean)} on powers associated
	 * with the given origin.
	 * @param origin The origin to trigger onChosen for.
	 * @param isOrb If first pick actions should be triggered.
	 */
	void onChosen(Origin origin, boolean isOrb);

	/**
	 * Executes {@link IOriginCallbackPower#onChosen(IDynamicFeatureConfiguration, Entity, boolean)} on all powers.
	 * @param isOrb If first pick actions should be triggered.
	 */
	void onChosen(boolean isOrb);

	void onReload();

	OriginComponent asLegacyComponent();
	
	Player getOwner();
}

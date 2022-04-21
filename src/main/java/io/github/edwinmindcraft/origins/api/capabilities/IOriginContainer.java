package io.github.edwinmindcraft.origins.api.capabilities;

import io.github.apace100.origins.component.OriginComponent;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.origin.IOriginCallbackPower;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.common.network.S2CSynchronizeOrigin;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface IOriginContainer extends INBTSerializable<Tag> {
	/**
	 * A shorthand to access the container for a given entity.
	 *
	 * @param entity The entity to access the container of.
	 *
	 * @return A lazy optional that may contain the container if applicable.
	 */
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
		return OriginsAPI.getActiveLayers().stream()
				.filter(x -> !x.empty(this.getOwner())) //Check if the player is eligible to at least one origin.
				.allMatch(this::hasOrigin);
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

	/**
	 * Runs a tick of this container. This is used for synchronisation and cleanup purposes
	 */
	void tick();

	/**
	 * Returns the packet used to synchronize origins with the client.
	 *
	 * @return The generated {@link S2CSynchronizeOrigin} packet.
	 */
	S2CSynchronizeOrigin getSynchronizationPacket();

	/**
	 * Checks and applies layers with an automatic origin present.
	 *
	 * @param includeDefaults Should the default origins also be applied.
	 *
	 * @return {@code true} if an origin was set, {@code false} otherwise.
	 */
	boolean checkAutoChoosingLayers(boolean includeDefaults);

	/**
	 * Executes {@link IOriginCallbackPower#onChosen(IDynamicFeatureConfiguration, Entity, boolean)} on powers associated
	 * with the given origin.
	 *
	 * @param origin The origin to trigger onChosen for.
	 * @param isOrb  If first pick actions should be triggered.
	 */
	void onChosen(Origin origin, boolean isOrb);

	/**
	 * Executes {@link IOriginCallbackPower#onChosen(IDynamicFeatureConfiguration, Entity, boolean)} on all powers.
	 *
	 * @param isOrb If first pick actions should be triggered.
	 */
	void onChosen(boolean isOrb);

	/**
	 * Called when the datapacks are finished reloading.
	 */
	void onReload();

	/**
	 * Converts this component into a fabric compatible one.
	 *
	 * @return The fabric compatible version of this component.
	 */
	OriginComponent asLegacyComponent();

	/**
	 * Accessor for the player that owns this container.
	 *
	 * @return The owner of this container.
	 */
	Player getOwner();
}

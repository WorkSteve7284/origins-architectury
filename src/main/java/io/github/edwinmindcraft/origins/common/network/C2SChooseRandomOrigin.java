package io.github.edwinmindcraft.origins.common.network;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public record C2SChooseRandomOrigin(ResourceLocation layer) {
	public static C2SChooseRandomOrigin decode(FriendlyByteBuf buf) {
		return new C2SChooseRandomOrigin(buf.readResourceLocation());
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeResourceLocation(this.layer());
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			ServerPlayer sender = contextSupplier.get().getSender();
			if (sender == null) return;
			IOriginContainer.get(sender).ifPresent(container -> {
				OriginLayer layer = OriginsAPI.getLayersRegistry().get(this.layer());
				if (layer == null) {
					Origins.LOGGER.warn("Player {} tried to select a random origin for missing layer {}", sender.getScoreboardName(), this.layer());
					return;
				}
				if (container.hasAllOrigins() || container.hasOrigin(layer)) {
					Origins.LOGGER.warn("Player {} tried to choose origin for layer {} while having one already.", sender.getScoreboardName(), this.layer());
					return;
				}
				Optional<Origin> selected = layer.selectRandom(sender);
				if (!layer.allowRandom() || selected.isEmpty()) {
					Origins.LOGGER.warn("Player {} tried to choose a random Origin for layer {}, which is not allowed!", sender.getScoreboardName(), this.layer());
					container.setOrigin(layer, Origin.EMPTY);
					return;
				}
				Origin origin = selected.get();
				boolean hadOriginBefore = container.hadAllOrigins();
				boolean hadAllOrigins = container.hasAllOrigins();
				container.setOrigin(layer, origin);
				container.checkAutoChoosingLayers(false);
				container.synchronize();
				if (container.hasAllOrigins() && !hadAllOrigins) {
					OriginComponent.onChosen(sender, hadOriginBefore);
				}
				Origins.LOGGER.info("Player {} was randomly assigned the following Origin: \"{}\" for layer: {}", sender.getScoreboardName(), origin.getRegistryName(), this.layer());
			});
		});
		contextSupplier.get().setPacketHandled(true);
	}
}

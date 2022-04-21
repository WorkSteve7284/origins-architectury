package io.github.edwinmindcraft.origins.common.network;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.common.OriginsCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public record C2SChooseOrigin(ResourceLocation layer, ResourceLocation origin) {

	public static C2SChooseOrigin decode(FriendlyByteBuf buf) {
		return new C2SChooseOrigin(buf.readResourceLocation(), buf.readResourceLocation());
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeResourceLocation(this.layer());
		buf.writeResourceLocation(this.origin());
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			ServerPlayer sender = contextSupplier.get().getSender();
			if (sender == null) return;
			IOriginContainer.get(sender).ifPresent(container -> {
				OriginLayer layer = OriginsAPI.getLayersRegistry().get(this.layer());
				if (layer == null) {
					Origins.LOGGER.warn("Player {} tried to select an origin for missing layer {}", sender.getScoreboardName(), this.layer());
					return;
				}
				if (container.hasAllOrigins() || container.hasOrigin(layer)) {
					Origins.LOGGER.warn("Player {} tried to choose origin for layer {} while having one already.", sender.getScoreboardName(), this.layer());
					return;
				}
				Origin origin = OriginsAPI.getOriginsRegistry().get(this.origin());
				if (origin == null) {
					Origins.LOGGER.warn("Player {} chose unknown origin: {} for layer {}", sender.getScoreboardName(), this.origin(), this.layer());
					return;
				}
				if (!origin.isChoosable() || !layer.contains(this.origin(), sender)) {
					Origins.LOGGER.warn("Player {} tried to choose invalid origin: {} for layer: {}", sender.getScoreboardName(), this.origin(), this.layer());
					container.setOrigin(layer, Origin.EMPTY);
				} else {
					boolean hadOriginBefore = container.hadAllOrigins();
					boolean hadAllOrigins = container.hasAllOrigins();
					container.setOrigin(layer, origin);
					container.checkAutoChoosingLayers(false);
					if (container.hasAllOrigins() && !hadAllOrigins) {
						OriginComponent.onChosen(sender, hadOriginBefore);
					}
				}
				container.synchronize();
				OriginsCommon.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sender), new S2CConfirmOrigin(this.layer(), this.origin()));
			});
		});
		contextSupplier.get().setPacketHandled(true);
	}
}

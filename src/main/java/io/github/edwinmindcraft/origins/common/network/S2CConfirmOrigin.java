package io.github.edwinmindcraft.origins.common.network;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.client.OriginsClient;
import io.github.edwinmindcraft.origins.client.OriginsClientUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CConfirmOrigin(ResourceLocation layer, ResourceLocation origin) {

	public static S2CConfirmOrigin decode(FriendlyByteBuf buf) {
		return new S2CConfirmOrigin(buf.readResourceLocation(), buf.readResourceLocation());
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeResourceLocation(this.layer());
		buf.writeResourceLocation(this.origin());
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player player = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> OriginsClientUtils::getClientPlayer);
			if (player == null) return;
			IOriginContainer.get(player).ifPresent(x -> x.setOrigin(OriginsAPI.getLayersRegistry().get(this.layer()), OriginsAPI.getOriginsRegistry().get(this.origin())));
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> OriginsClient.OPEN_NEXT_LAYER.set(true));
		});
		contextSupplier.get().setPacketHandled(true);
	}
}

package io.github.apace100.origins.registry;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.common.OriginsCommon;
import io.github.edwinmindcraft.origins.common.network.S2COpenOriginScreen;
import io.github.edwinmindcraft.origins.common.registry.OriginRegisters;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

	public static final RegistryObject<Item> ORB_OF_ORIGIN = OriginRegisters.ITEMS.register("orb_of_origin", () -> new Item(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE)) {
		@Override
		public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
			if (!world.isClientSide) {
				IOriginContainer.get(user).ifPresent(container -> {
					OriginsAPI.getActiveLayers().forEach(x -> container.setOrigin(x, Origin.EMPTY));
					container.checkAutoChoosingLayers(false);
					container.synchronize();
					container.tick();
					OriginsCommon.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) user), new S2COpenOriginScreen(false));
				});
			}
			ItemStack stack = user.getItemInHand(hand);
			if (!user.isCreative()) {
				stack.shrink(1);
			}
			return InteractionResultHolder.consume(stack);
		}
	});

	public static void register() {}
}

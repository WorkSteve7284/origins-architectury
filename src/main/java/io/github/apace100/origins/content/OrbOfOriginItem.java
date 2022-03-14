package io.github.apace100.origins.content;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.common.OriginsCommon;
import io.github.edwinmindcraft.origins.common.network.S2COpenOriginScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrbOfOriginItem extends Item {

	public OrbOfOriginItem() {
		super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE));
	}

	@Override
	@NotNull
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			IOriginContainer.get(player).ifPresent(container -> {
				Map<OriginLayer, Origin> targets = this.getTargets(stack);
				if (targets.size() > 0) {
					for (Map.Entry<OriginLayer, Origin> target : targets.entrySet()) {
						container.setOrigin(target.getKey(), target.getValue());
					}
				} else {
					for (OriginLayer layer : OriginsAPI.getActiveLayers()) {
						container.setOrigin(layer, Origin.EMPTY);
					}
				}
				if (player instanceof ServerPlayer sp) {
					container.checkAutoChoosingLayers(false);
					container.synchronize();
					OriginsCommon.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new S2COpenOriginScreen(false));
				}
			});
		}
		if (!player.isCreative()) {
			stack.shrink(1);
		}
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flags) {
		Map<OriginLayer, Origin> targets = this.getTargets(stack);
		for (Map.Entry<OriginLayer, Origin> entry : targets.entrySet()) {
			if (entry.getValue() == Origin.EMPTY)
				components.add(new TranslatableComponent("item.origins.orb_of_origin.layer_generic", entry.getKey().name()).withStyle(ChatFormatting.GRAY));
			else
				components.add(new TranslatableComponent("item.origins.orb_of_origin.layer_specific", entry.getKey().name(), entry.getValue().getName()).withStyle(ChatFormatting.GRAY));
		}
	}

	private Map<OriginLayer, Origin> getTargets(ItemStack stack) {
		HashMap<OriginLayer, Origin> targets = new HashMap<>();
		if (!stack.hasTag()) {
			return targets;
		}
		CompoundTag nbt = stack.getTag();
		if (!nbt.contains("Targets", Tag.TAG_LIST)) {
			return targets;
		}
		ListTag targetList = (ListTag) nbt.get("Targets");
		for (Tag nbtElement : targetList) {
			if (nbtElement instanceof CompoundTag targetNbt) {
				if (targetNbt.contains("Layer", Tag.TAG_STRING)) {
					try {
						ResourceLocation id = new ResourceLocation(targetNbt.getString("Layer"));
						OriginLayer layer = OriginsAPI.getLayersRegistry().get(id);
						if (layer == null) continue;
						Origin origin = Origin.EMPTY;
						if (targetNbt.contains("Origin", Tag.TAG_STRING)) {
							ResourceLocation originId = new ResourceLocation(targetNbt.getString("Origin"));
							origin = OriginsAPI.getOriginsRegistry().get(originId);
						}
						if (origin == null)
							continue;
						if (layer.enabled() && (layer.contains(origin.getRegistryName()) || origin.isSpecial())) {
							targets.put(layer, origin);
						}
					} catch (Exception e) {
						// no op
					}
				}
			}
		}
		return targets;
	}
}

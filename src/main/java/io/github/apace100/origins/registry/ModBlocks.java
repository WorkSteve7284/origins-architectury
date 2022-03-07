package io.github.apace100.origins.registry;

import io.github.apace100.origins.block.TemporaryCobwebBlock;
import io.github.edwinmindcraft.origins.common.registry.OriginRegisters;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

	public static final RegistryObject<TemporaryCobwebBlock> TEMPORARY_COBWEB = register("temporary_cobweb", () -> new TemporaryCobwebBlock(BlockBehaviour.Properties.of(Material.WEB).noCollission().requiresCorrectToolForDrops().strength(4.0F)), false);

	public static void register() {
	}

	private static <T extends Block> RegistryObject<T> register(String blockName, Supplier<T> block) {
		return register(blockName, block, true);
	}

	private static <T extends Block> RegistryObject<T> register(String blockName, Supplier<T> block, boolean withBlockItem) {
		RegistryObject<T> register = OriginRegisters.BLOCKS.register(blockName, block);
		if (withBlockItem)
			OriginRegisters.ITEMS.register(blockName, () -> new BlockItem(register.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
		return register;
	}
}
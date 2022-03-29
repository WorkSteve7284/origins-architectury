package io.github.edwinmindcraft.origins.data.tag;

import io.github.apace100.origins.Origins;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class OriginsBlockTags {
	private static TagKey<Block> tag(String path) {
		return BlockTags.create(Origins.identifier(path));
	}

	public static TagKey<Block> COBWEBS = tag("cobwebs");
	public static TagKey<Block> UNPHASABLE = tag("unphasable");
	public static TagKey<Block> NATURAL_STONE = tag("natural_stone");
}

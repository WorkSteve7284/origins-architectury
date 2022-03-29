package io.github.edwinmindcraft.origins.data.tag;

import io.github.apace100.origins.Origins;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class OriginsItemTags {
	private static TagKey<Item> tag(String path) {
		return ItemTags.create(Origins.identifier(path));
	}

	public static TagKey<Item> MEAT = tag("meat");
	public static TagKey<Item> IGNORE_DIET = tag("ignore_diet");
	public static TagKey<Item> RANGED_WEAPONS = tag("ranged_weapons");
}

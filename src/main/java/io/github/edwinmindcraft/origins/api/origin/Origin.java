package io.github.edwinmindcraft.origins.api.origin;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.data.OriginsDataTypes;
import io.github.apace100.origins.origin.Impact;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.registry.ICalioDynamicRegistryManager;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Origin extends ForgeRegistryEntry.UncheckedRegistryEntry<Origin> {
	@ObjectHolder("origins:empty")
	public static final Origin EMPTY = new Origin(ImmutableSet.of(), ItemStack.EMPTY, true, -1, Impact.NONE, new TextComponent(""), new TextComponent(""), ImmutableSet.of(), true);

	private final Set<ResourceLocation> powers;
	private final ItemStack icon;
	private final boolean unchoosable;
	private final int order;
	private final Impact impact;
	private final Component name;
	private final Component description;
	private final Set<OriginUpgrade> upgrades;
	private final boolean special;

	public static final Codec<Origin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CalioCodecHelper.setOf(ResourceLocation.CODEC).fieldOf("powers").forGetter(Origin::getPowers),
			CalioCodecHelper.optionalField(ItemStack.CODEC, "icon", ItemStack.EMPTY).forGetter(Origin::getIcon),
			CalioCodecHelper.optionalField(Codec.BOOL, "unchoosable", false).forGetter(Origin::isUnchoosable),
			CalioCodecHelper.optionalField(Codec.INT, "order", Integer.MAX_VALUE).forGetter(Origin::getOrder),
			CalioCodecHelper.optionalField(OriginsDataTypes.IMPACT, "impact", Impact.NONE).forGetter(Origin::getImpact),
			CalioCodecHelper.COMPONENT_CODEC.fieldOf("name").forGetter(Origin::getName),
			CalioCodecHelper.COMPONENT_CODEC.fieldOf("description").forGetter(Origin::getDescription),
			CalioCodecHelper.setOf(OriginUpgrade.CODEC).fieldOf("upgrades").forGetter(Origin::getUpgrades),
			CalioCodecHelper.optionalField(Codec.BOOL, "special", false).forGetter(Origin::isSpecial)
	).apply(instance, Origin::new));

	public Origin(Set<ResourceLocation> powers, ItemStack icon, boolean unchoosable, int order, Impact impact, Component name, Component description, Set<OriginUpgrade> upgrades, boolean special) {
		this.powers = ImmutableSet.copyOf(powers);
		this.icon = icon;
		this.unchoosable = unchoosable;
		this.order = order;
		this.impact = impact;
		this.name = name;
		this.description = description;
		this.upgrades = ImmutableSet.copyOf(upgrades);
		this.special = special;
	}

	public Origin(Set<ResourceLocation> powers, ItemStack icon, boolean unchoosable, int order, Impact impact, Component name, Component description, Set<OriginUpgrade> upgrades) {
		this(powers, icon, unchoosable, order, impact, name, description, upgrades, false);
	}

	public Origin cleanup(ICalioDynamicRegistryManager manager) {
		Registry<ConfiguredPower<?, ?>> powers = manager.get(ApoliDynamicRegistries.CONFIGURED_POWER_KEY);
		return new Origin(
				this.getPowers().stream().filter(powers::containsKey).collect(ImmutableSet.toImmutableSet()),
				this.getIcon(),
				this.isUnchoosable(),
				this.getOrder(),
				this.getImpact(),
				this.getName(),
				this.getDescription(),
				this.getUpgrades(),
				this.isSpecial());
	}

	public Set<ResourceLocation> getPowers() {
		return this.powers;
	}

	public ItemStack getIcon() {
		return this.icon;
	}

	public boolean isUnchoosable() {
		return this.unchoosable;
	}

	public int getOrder() {
		return this.order;
	}

	public Impact getImpact() {
		return this.impact;
	}

	public Component getName() {
		return this.name;
	}

	public Component getDescription() {
		return this.description;
	}

	public Set<OriginUpgrade> getUpgrades() {
		return this.upgrades;
	}

	public boolean isChoosable() {
		return !this.isUnchoosable();
	}

	public boolean isSpecial() {
		return this.special;
	}

	public Optional<OriginUpgrade> findUpgrade(ResourceLocation advancement) {
		return this.getUpgrades().stream().filter(x -> Objects.equals(x.advancement(), advancement)).findFirst();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Origin(").append(this.getRegistryName()).append(")[");
		boolean first = true;
		for (ResourceLocation power : this.getPowers()) {
			if (first)
				first = false;
			else
				builder.append(',');
			builder.append(power);
		}
		return builder.append(']').toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		Origin origin = (Origin) o;
		return Objects.equals(this.getRegistryName(), origin.getRegistryName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getRegistryName());
	}
}

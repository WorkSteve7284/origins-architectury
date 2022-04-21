package io.github.edwinmindcraft.origins.api.origin;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import io.github.edwinmindcraft.calio.api.registry.ICalioDynamicRegistryManager;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class OriginLayer extends ForgeRegistryEntry.UncheckedRegistryEntry<OriginLayer> implements Comparable<OriginLayer> {

	public static final Codec<OriginLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("order").forGetter(OriginLayer::order),
			CalioCodecHelper.setOf(ConditionedOrigin.CODEC).fieldOf("origins").forGetter(OriginLayer::conditionedOrigins),
			Codec.BOOL.fieldOf("enabled").forGetter(OriginLayer::enabled),
			CalioCodecHelper.COMPONENT_CODEC.fieldOf("name").forGetter(OriginLayer::name),
			CalioCodecHelper.COMPONENT_CODEC.fieldOf("missing_name").forGetter(OriginLayer::missingName),
			CalioCodecHelper.COMPONENT_CODEC.fieldOf("missing_description").forGetter(OriginLayer::missingDescription),
			Codec.BOOL.fieldOf("allow_random").forGetter(OriginLayer::allowRandom),
			Codec.BOOL.fieldOf("allow_random_unchoosable").forGetter(OriginLayer::allowRandomUnchoosable),
			CalioCodecHelper.setOf(ResourceLocation.CODEC).fieldOf("random_exclusions").forGetter(OriginLayer::randomExclusions),
			CalioCodecHelper.optionalField(ResourceLocation.CODEC, "default").forGetter(x -> Optional.ofNullable(x.defaultOrigin())),
			Codec.BOOL.fieldOf("auto_choose").forGetter(OriginLayer::autoChoose),
			CalioCodecHelper.optionalField(Codec.BOOL, "hidden", false).forGetter(OriginLayer::hidden),
			CalioCodecHelper.optionalField(GuiTitle.CODEC, "gui_title", GuiTitle.DEFAULT).forGetter(OriginLayer::title)
	).apply(instance, OriginLayer::new));

	private final int order;
	private final Set<ConditionedOrigin> conditionedOrigins;
	private final boolean enabled;
	private final Component name;
	private final Component missingName;
	private final Component missingDescription;
	private final boolean allowRandom;
	private final boolean allowRandomUnchoosable;
	private final Set<ResourceLocation> randomExclusions;
	private final @Nullable ResourceLocation defaultOrigin;
	private final boolean autoChoose;
	private final boolean hidden;
	private final GuiTitle title;

	public OriginLayer(int order, ResourceLocation registryName,
					   Set<ConditionedOrigin> conditionedOrigins,
					   boolean enabled, Component name,
					   Component missingName,
					   Component missingDescription, boolean allowRandom,
					   boolean allowRandomUnchoosable,
					   Set<ResourceLocation> randomExclusions,
					   @Nullable ResourceLocation defaultOrigin,
					   boolean autoChoose, boolean hidden,
					   GuiTitle title) {
		this(order, conditionedOrigins, enabled, name, missingName, missingDescription, allowRandom, allowRandomUnchoosable, randomExclusions, defaultOrigin, autoChoose, hidden, title);
		this.setRegistryName(registryName);
	}

	public OriginLayer(int order,
					   Set<ConditionedOrigin> conditionedOrigins,
					   boolean enabled, Component name,
					   Component missingName,
					   Component missingDescription, boolean allowRandom,
					   boolean allowRandomUnchoosable,
					   Set<ResourceLocation> randomExclusions,
					   @Nullable ResourceLocation defaultOrigin,
					   boolean autoChoose, boolean hidden,
					   GuiTitle title) {
		this.order = order;
		this.conditionedOrigins = conditionedOrigins;
		this.enabled = enabled;
		this.name = name;
		this.missingName = missingName;
		this.missingDescription = missingDescription;
		this.allowRandom = allowRandom;
		this.allowRandomUnchoosable = allowRandomUnchoosable;
		this.randomExclusions = randomExclusions;
		this.defaultOrigin = defaultOrigin;
		this.autoChoose = autoChoose;
		this.hidden = hidden;
		this.title = title;
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private OriginLayer(Integer order, Set<ConditionedOrigin> conditionedOrigins,
						Boolean enabled, Component name, Component missingName, Component missingDescription,
						Boolean allowRandom, Boolean allowRandomUnchoosable, Set<ResourceLocation> randomExclusions,
						Optional<ResourceLocation> defaultOrigin, Boolean autoChoose, Boolean hidden, GuiTitle title) {
		this(order, conditionedOrigins, enabled, name, missingName, missingDescription, allowRandom, allowRandomUnchoosable, randomExclusions, defaultOrigin.orElse(null), autoChoose, hidden, title);
	}

	public OriginLayer cleanup(ICalioDynamicRegistryManager registries) {
		Registry<Origin> registry = registries.get(OriginsDynamicRegistries.ORIGINS_REGISTRY);
		return new OriginLayer(this.order(),
				this.conditionedOrigins().stream().map(x -> x.cleanup(registries)).filter(x -> !x.isEmpty()).collect(ImmutableSet.toImmutableSet()),
				this.enabled(), this.name(),
				this.missingName(), this.missingDescription(),
				this.allowRandom(), this.allowRandomUnchoosable(),
				this.randomExclusions().stream().filter(registry::containsKey).collect(ImmutableSet.toImmutableSet()),
				this.defaultOrigin(),
				this.autoChoose(),
				this.hidden(),
				this.title()
		);
	}

	public boolean hasDefaultOrigin() {
		return this.defaultOrigin() != null;
	}

	public Set<ResourceLocation> origins() {
		return this.conditionedOrigins().stream().flatMap(ConditionedOrigin::stream).collect(Collectors.toSet());
	}

	public Set<ResourceLocation> origins(Player player) {
		return this.conditionedOrigins().stream().flatMap(x -> x.stream(player)).collect(Collectors.toSet());
	}

	public boolean empty() {
		return this.conditionedOrigins().stream().flatMap(ConditionedOrigin::stream).findAny().isEmpty();
	}

	public boolean empty(Player player) {
		return this.conditionedOrigins().stream().flatMap(x -> x.stream(player)).findAny().isEmpty();
	}

	public List<ResourceLocation> randomOrigins(Player player) {
		Registry<Origin> origins = OriginsAPI.getOriginsRegistry();
		return this.conditionedOrigins().stream().flatMap(x -> x.stream(player))
				.filter(o -> !this.randomExclusions().contains(o))
				.filter(id -> origins.getOptional(id).map(x -> this.allowRandomUnchoosable() || x.isChoosable()).orElse(false))
				.collect(Collectors.toList());
	}

	public boolean contains(ResourceLocation origin) {
		return this.conditionedOrigins().stream().anyMatch(x -> x.origins().contains(origin));
	}

	public boolean contains(ResourceLocation origin, Player player) {
		return this.conditionedOrigins().stream().anyMatch(x -> ConfiguredEntityCondition.check(x.condition(), player) && x.origins().contains(origin));
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof OriginLayer layer))
			return false;
		return Objects.equals(this.getRegistryName(), layer.getRegistryName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getRegistryName());
	}

	/**
	 * FORGE ONLY<br>
	 * Finds and returns the automatic origin for the given player if applicable.
	 *
	 * @param player The player to check the origin for.
	 *
	 * @return Either an optional containing {@link ResourceLocation} of the origin if applicable, or {@link Optional#empty()}.
	 */
	@NotNull
	public Optional<Origin> getAutomaticOrigin(Player player) {
		if (!this.autoChoose())
			return Optional.empty();
		Registry<Origin> registry = OriginsAPI.getOriginsRegistry();
		List<Origin> origins = this.origins(player).stream().flatMap(x -> registry.getOptional(x).stream().filter(Origin::isChoosable)).toList();
		if (this.allowRandom() && origins.isEmpty())
			return this.selectRandom(player);
		if (origins.size() > 1)
			return Optional.empty();
		return origins.stream().findFirst();
	}

	public Optional<Origin> selectRandom(Player player) {
		if (!this.allowRandom())
			return Optional.empty();
		Registry<Origin> origins = OriginsAPI.getOriginsRegistry();
		List<Origin> candidates = this.conditionedOrigins.stream()
				.flatMap(x -> x.stream(player))
				.flatMap(x -> origins.getOptional(x).stream())
				.filter(x -> this.allowRandomUnchoosable() || x.isChoosable()).toList();
		if (candidates.isEmpty())
			return Optional.empty();
		if (candidates.size() == 1)
			return Optional.of(candidates.get(0));
		return Optional.of(candidates.get(player.getRandom().nextInt(candidates.size())));
	}

	public int getOriginOptionCount(Player playerEntity) {
		Registry<Origin> origins = OriginsAPI.getOriginsRegistry();
		long choosableOrigins = this.origins(playerEntity).stream().flatMap(x -> origins.getOptional(x).stream()).filter(Origin::isChoosable).count();
		if (this.allowRandom() && this.randomOrigins(playerEntity).size() > 0)
			choosableOrigins++;
		return Math.toIntExact(choosableOrigins);
	}

	@Override
	public int compareTo(@NotNull OriginLayer o) {
		return Integer.compare(this.order(), o.order());
	}

	public int order() {return this.order;}

	/**
	 * @deprecated Use {@link #getRegistryName()} instead.
	 */
	@Nullable
	@Deprecated(forRemoval = true, since = "2.3.3.1")
	public ResourceLocation registryName() {
		return this.getRegistryName();
	}

	public Set<ConditionedOrigin> conditionedOrigins() {return this.conditionedOrigins;}

	public boolean enabled() {return this.enabled;}

	public Component name() {return this.name;}

	public Component missingName() {return this.missingName;}

	public Component missingDescription() {return this.missingDescription;}

	public boolean allowRandom() {return this.allowRandom;}

	public boolean allowRandomUnchoosable() {return this.allowRandomUnchoosable;}

	public Set<ResourceLocation> randomExclusions() {return this.randomExclusions;}

	public @Nullable ResourceLocation defaultOrigin() {return this.defaultOrigin;}

	public boolean autoChoose() {return this.autoChoose;}

	public boolean hidden() {return this.hidden;}

	public GuiTitle title() {return this.title;}

	@Override
	public String toString() {
		return "OriginLayer[" +
			   "order=" + this.order + ", " +
			   "conditionedOrigins=" + this.conditionedOrigins + ", " +
			   "enabled=" + this.enabled + ", " +
			   "name=" + this.name + ", " +
			   "missingName=" + this.missingName + ", " +
			   "missingDescription=" + this.missingDescription + ", " +
			   "allowRandom=" + this.allowRandom + ", " +
			   "allowRandomUnchoosable=" + this.allowRandomUnchoosable + ", " +
			   "randomExclusions=" + this.randomExclusions + ", " +
			   "defaultOrigin=" + this.defaultOrigin + ", " +
			   "autoChoose=" + this.autoChoose + ", " +
			   "hidden=" + this.hidden + ", " +
			   "title=" + this.title + ']';
	}
}

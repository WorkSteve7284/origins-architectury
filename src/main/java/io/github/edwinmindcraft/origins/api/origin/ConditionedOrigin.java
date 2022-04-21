package io.github.edwinmindcraft.origins.api.origin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityCondition;
import io.github.edwinmindcraft.apoli.api.registry.ApoliBuiltinRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.calio.api.registry.ICalioDynamicRegistryManager;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public record ConditionedOrigin(
		@NotNull Holder<ConfiguredEntityCondition<?, ?>> condition,
		Set<ResourceLocation> origins) {

	public static final Codec<ConditionedOrigin> LARGE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredEntityCondition.optional("condition").forGetter(ConditionedOrigin::condition),
			SerializableDataTypes.IDENTIFIERS.fieldOf("origins").forGetter(x -> ImmutableList.copyOf(x.origins()))
	).apply(instance, (condition, origins) -> new ConditionedOrigin(condition, ImmutableSet.copyOf(origins))));

	public static final Codec<ConditionedOrigin> STRING_CODEC = Codec.STRING.flatComapMap(s -> {
		ResourceLocation resourceLocation = ResourceLocation.tryParse(s);
		return new ConditionedOrigin(ApoliBuiltinRegistries.CONFIGURED_ENTITY_CONDITIONS.get().getHolder(ApoliDynamicRegistries.CONDITION_DEFAULT).orElseThrow(), resourceLocation != null ? ImmutableSet.of(resourceLocation) : ImmutableSet.of());
	}, co -> {
		if (co.origins().size() != 1)
			return DataResult.error("Invalid size: " + co.origins().size());
		return co.origins().stream().findFirst().map(ResourceLocation::toString).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown Error"));
	});

	public static final Codec<ConditionedOrigin> CODEC = Codec.either(STRING_CODEC, LARGE_CODEC)
			.xmap(e -> e.map(Function.identity(), Function.identity()), co -> co.origins().size() == 1 && co.condition().is(ApoliDynamicRegistries.CONDITION_DEFAULT) ? Either.left(co) : Either.right(co));

	public Stream<ResourceLocation> stream(Player player) {
		return ConfiguredEntityCondition.check(this.condition(), player) ? this.origins().stream() : Stream.empty();
	}

	public Stream<ResourceLocation> stream() {
		return this.origins().stream();
	}

	public boolean isEmpty() {
		return this.origins().isEmpty();
	}

	public ConditionedOrigin cleanup(ICalioDynamicRegistryManager registries) {
		WritableRegistry<Origin> registry = registries.get(OriginsDynamicRegistries.ORIGINS_REGISTRY);
		return new ConditionedOrigin(this.condition(), this.origins().stream().filter(registry::containsKey).collect(ImmutableSet.toImmutableSet()));
	}

	public enum Serializer implements JsonSerializer<ConditionedOrigin>, JsonDeserializer<ConditionedOrigin> {
		INSTANCE;

		@Override
		public ConditionedOrigin deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			DataResult<Pair<ConditionedOrigin, JsonElement>> result = CODEC.decode(JsonOps.INSTANCE, json);
			return result.getOrThrow(false, s -> {
				throw new JsonParseException("Expected origin in layer to be either a string or an object.");
			}).getFirst();
		}

		@Override
		public JsonElement serialize(ConditionedOrigin src, Type typeOfSrc, JsonSerializationContext context) {
			if (src.isEmpty())
				return new JsonPrimitive("<empty conditioned origin>");
			return CODEC.encodeStart(JsonOps.INSTANCE, src).getOrThrow(false, s -> {});
		}
	}
}

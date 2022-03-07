package io.github.edwinmindcraft.origins.common.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record OriginConfiguration(ResourceLocation origin,
								  @Nullable ResourceLocation layer) implements IDynamicFeatureConfiguration {
	public static final Codec<OriginConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("origin").forGetter(OriginConfiguration::origin),
			CalioCodecHelper.optionalField(ResourceLocation.CODEC, "layer").forGetter(x -> Optional.ofNullable(x.layer()))
	).apply(instance, (o, l) -> new OriginConfiguration(o, l.orElse(null))));
}

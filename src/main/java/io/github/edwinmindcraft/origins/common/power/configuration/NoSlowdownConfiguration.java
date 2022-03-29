package io.github.edwinmindcraft.origins.common.power.configuration;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Another take on the "no_cobweb_slowdown" power that doesn't need to
 * work for powder snow and sweet berry bushes.
 * If {@link #blocks()} is null, this will prevent slowdown from all sources.
 */
public record NoSlowdownConfiguration(@Nullable TagKey<Block> blocks) implements IDynamicFeatureConfiguration {
	//FIXME As this is a non-standard power, move it to a list.
	public static final Codec<NoSlowdownConfiguration> CODEC = TagKey.hashedCodec(Registry.BLOCK_REGISTRY).optionalFieldOf("tag")
			.xmap(x -> new NoSlowdownConfiguration(x.orElse(null)), x -> Optional.ofNullable(x.blocks())).codec();

	public boolean test(BlockState state) {
		return this.blocks() == null || state.is(this.blocks());
	}
}

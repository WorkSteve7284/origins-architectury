package io.github.edwinmindcraft.origins.common.condition;

import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.common.condition.configuration.OriginConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class OriginCondition extends EntityCondition<OriginConfiguration> {
	public OriginCondition() {
		super(OriginConfiguration.CODEC);
	}

	@Override
	public boolean check(OriginConfiguration configuration, Entity entity) {
		return IOriginContainer.get(entity).resolve().map(container -> {
			if (configuration.layer() != null) {
				OriginLayer layer = OriginsAPI.getLayersRegistry().get(configuration.layer());
				return layer != null && configuration.origin().equals(container.getOrigin(layer).getRegistryName());
			}
			return container.getOrigins().values().stream().map(ForgeRegistryEntry::getRegistryName).anyMatch(configuration.origin()::equals);
		}).orElse(false);
	}
}

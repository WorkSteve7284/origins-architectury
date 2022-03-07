package io.github.edwinmindcraft.origins.common.power;

import io.github.apace100.origins.power.OriginsPowerTypes;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.origins.common.power.configuration.WaterVisionConfiguration;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class WaterVisionPower extends PowerFactory<WaterVisionConfiguration> {
	public static Optional<Float> getWaterVisionStrength(LivingEntity living) {
		if (!OriginsPowerTypes.WATER_VISION.isPresent())
			return Optional.empty();
		return IPowerContainer.getPowers(living, OriginsPowerTypes.WATER_VISION.get()).stream().map(x -> x.getConfiguration().strength()).max(Float::compareTo);
	}

	public WaterVisionPower() {
		super(WaterVisionConfiguration.CODEC);
	}
}

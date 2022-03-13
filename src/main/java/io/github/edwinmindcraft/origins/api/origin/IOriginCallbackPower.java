package io.github.edwinmindcraft.origins.api.origin;

import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface IOriginCallbackPower<T extends IDynamicFeatureConfiguration> {
	@SuppressWarnings("unchecked")
	static <C extends IDynamicFeatureConfiguration, F extends PowerFactory<C>> void onChosen(ConfiguredPower<C, F> power, Entity living, boolean isOrb) {
		if (power.getFactory() instanceof IOriginCallbackPower)
			((IOriginCallbackPower<C>) power.getFactory()).onChosen(power.getConfiguration(), living, isOrb);
	}

	void onChosen(T configuration, Entity entity, boolean isOrb);
}

package io.github.apace100.origins.origin;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Use {@link io.github.edwinmindcraft.origins.api.origin.OriginLayer} where possible instead.
 */
@Deprecated
public class OriginLayer implements Comparable<OriginLayer> {
	private final io.github.edwinmindcraft.origins.api.origin.OriginLayer wrapped;

	public OriginLayer(io.github.edwinmindcraft.origins.api.origin.OriginLayer wrapped) {this.wrapped = wrapped;}

	public String getOrCreateTranslationKey() {
		return this.wrapped.name() instanceof TranslatableComponent tc ? tc.getKey() : "";
	}

	public io.github.edwinmindcraft.origins.api.origin.OriginLayer getWrapped() {
		return this.wrapped;
	}

	public String getTranslationKey() {
		return this.getOrCreateTranslationKey();
	}

	public String getMissingOriginNameTranslationKey() {
		return this.wrapped.missingName() instanceof TranslatableComponent tc ? tc.getKey() : "";
	}

	public String getMissingOriginDescriptionTranslationKey() {
		return this.wrapped.missingDescription() instanceof TranslatableComponent tc ? tc.getKey() : "";
	}

	public ResourceLocation getIdentifier() {
		return this.wrapped.registryName();
	}

	public boolean isEnabled() {
		return this.wrapped.enabled();
	}

	public boolean hasDefaultOrigin() {
		return this.wrapped.hasDefaultOrigin();
	}

	public ResourceLocation getDefaultOrigin() {
		return this.wrapped.defaultOrigin();
	}

	public boolean shouldAutoChoose() {
		return this.wrapped.autoChoose();
	}

	public List<ResourceLocation> getOrigins() {
		return ImmutableList.copyOf(this.wrapped.origins());
	}

	public List<ResourceLocation> getOrigins(Player playerEntity) {
		return ImmutableList.copyOf(this.wrapped.origins(playerEntity));
	}

	public int getOriginOptionCount(Player playerEntity) {
		return this.wrapped.getOriginOptionCount(playerEntity);
	}

	public boolean contains(Origin origin) {
		return this.wrapped.contains(origin.getIdentifier());
	}

	public boolean contains(Origin origin, Player playerEntity) {
		return this.wrapped.contains(origin.getIdentifier(), playerEntity);
	}

	public boolean isRandomAllowed() {
		return this.wrapped.allowRandom();
	}

    public boolean isHidden() {
        return this.wrapped.hidden();
    }

	public List<ResourceLocation> getRandomOrigins(Player playerEntity) {
		return this.wrapped.randomOrigins(playerEntity);
	}

	@Override
	public int hashCode() {
		return this.wrapped.registryName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof OriginLayer)) {
			return false;
		} else {
			return this.wrapped.registryName().equals(((OriginLayer) obj).wrapped.registryName());
		}
	}

	@Override
	public int compareTo(OriginLayer o) {
		return this.wrapped.compareTo(o.wrapped);
	}
}

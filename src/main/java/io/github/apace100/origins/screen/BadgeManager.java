package io.github.apace100.origins.screen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.github.apace100.apoli.integration.PowerLoadEvent;
import io.github.apace100.origins.Origins;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.ITogglePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.calio.api.event.CalioDynamicRegistryEvent;
import io.github.edwinmindcraft.origins.common.network.S2CSynchronizeBadges;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BadgeManager {

	public S2CSynchronizeBadges createPacket() {
		return new S2CSynchronizeBadges(Multimaps.unmodifiableMultimap(this.badges));
	}

	private final Multimap<ResourceLocation, Badge> badges = HashMultimap.create();

	@Nullable
	private Badge findBadge(ConfiguredPower<?, ?> power) {
		if (power.getFactory() instanceof ITogglePower)
			return Badge.TOGGLE;
		else if (power.getFactory() instanceof IActivePower)
			return Badge.ACTIVE;
		return null;
	}

	private void onPowerLoad(PowerLoadEvent.Post event) {
		ResourceLocation id = event.getId();
		JsonElement data = event.getAdditionalData("badges");
		ConfiguredPower<?, ?> power = event.getPower();
		List<Badge> badges = new LinkedList<>();
		if (!power.getData().hidden() && !data.isJsonNull()) {
			if (data.isJsonArray()) {
				data.getAsJsonArray().forEach(badgeJson -> {
					try {
						if (badgeJson.isJsonObject()) {
							badges.add(Badge.fromJson(badgeJson));
						} else {
							Origins.LOGGER.error("\"badges\" field in power \"{}\" contained an entry that was not a JSON object.", id);
						}
					} catch (JsonParseException e) {
						Origins.LOGGER.error("\"badges\" field in power \"{}\" failed to deserialize with error: {}.", id, e.getMessage());
					}
				});
			} else {
				Origins.LOGGER.error("\"badges\" field in power \"{}\" should be an array.", id);
			}
		}
		if (badges.isEmpty()) {
			Badge autoBadge = this.findBadge(power);
			if (autoBadge == null) {
				for (ConfiguredPower<?, ?> subPower : power.getContainedPowers().values()) {
					autoBadge = this.findBadge(subPower);
					if (autoBadge != null)
						break;
				}
			}
			if(autoBadge != null) //Forgetting this is derp tier.
				badges.add(autoBadge);
		}
		badges.forEach(x -> this.addBadge(id, x));
	}

	private void onDynamicRegistryReload(CalioDynamicRegistryEvent.Reload event) {
		this.clear();
	}

	public BadgeManager() {
		MinecraftForge.EVENT_BUS.addListener(this::onPowerLoad);
		MinecraftForge.EVENT_BUS.addListener(this::onDynamicRegistryReload);
		ApoliAPI.addAdditionalDataField("badges"); //Badges are a thing that exists.
	}

	public void clear() {
		this.badges.clear();
	}

	public void addBadge(ResourceLocation powerId, Badge badge) {
		this.badges.put(powerId, badge);
	}

	public Collection<Badge> getBadges(ResourceLocation powerId) {
		if (!this.badges.containsKey(powerId)) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableCollection(this.badges.get(powerId));
	}
}

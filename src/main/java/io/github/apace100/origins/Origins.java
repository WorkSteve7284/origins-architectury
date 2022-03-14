package io.github.apace100.origins;

import io.github.apace100.apoli.util.NamespaceAlias;
import io.github.apace100.origins.command.LayerArgumentType;
import io.github.apace100.origins.command.OriginArgumentType;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.power.OriginsEntityConditions;
import io.github.apace100.origins.power.OriginsPowerTypes;
import io.github.apace100.origins.registry.*;
import io.github.apace100.origins.screen.BadgeManager;
import io.github.apace100.origins.util.ChoseOriginCriterion;
import io.github.apace100.origins.util.OriginsConfigSerializer;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.common.OriginsCommon;
import io.github.edwinmindcraft.origins.data.OriginsData;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Origins.MODID)
public class Origins {

	public static final String MODID = OriginsAPI.MODID;
	public static String VERSION = "";
	public static final Logger LOGGER = LogManager.getLogger(Origins.class);

	public static ServerConfig config;
	public static BadgeManager badgeManager;

	public Origins() {
		VERSION = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();
		LOGGER.info("Origins " + VERSION + " is initializing. Have fun!");
		AutoConfig.register(ServerConfig.class, OriginsConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ServerConfig.class).getConfig();

		NamespaceAlias.addAlias(MODID, "apoli");

		OriginsPowerTypes.register();
		OriginsEntityConditions.register();

		ModBlocks.register();
		ModItems.register();
		ModTags.register();
		ModEnchantments.register();
		ModEntities.register();
		ModLoot.register();
		Origin.init();

		OriginsCommon.initialize();
		OriginsData.initialize();

		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> OriginsClient::initialize);
		NamespaceAlias.addAlias("origins", "apoli");

		CriteriaTriggers.register(ChoseOriginCriterion.INSTANCE);
		ArgumentTypes.register("origins:origin", OriginArgumentType.class, new EmptyArgumentSerializer<>(OriginArgumentType::origin));
		ArgumentTypes.register("origins:layer", LayerArgumentType.class, new EmptyArgumentSerializer<>(LayerArgumentType::layer));

		badgeManager = new BadgeManager();
	}

	public static ResourceLocation identifier(String path) {
		return new ResourceLocation(Origins.MODID, path);
	}

	@Config(name = Origins.MODID + "_server")
	public static class ServerConfig implements ConfigData {

		public boolean performVersionCheck = true;
	}
}

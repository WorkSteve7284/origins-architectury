package io.github.edwinmindcraft.origins.common;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.OriginsPowerTypes;
import io.github.apace100.origins.registry.ModLoot;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.common.network.*;
import io.github.edwinmindcraft.origins.common.registry.OriginRegisters;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class OriginsCommon {
	private static final String NETWORK_VERSION = "1.0";

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(Origins.identifier("network"), () -> NETWORK_VERSION, NETWORK_VERSION::equals, NETWORK_VERSION::equals);

	private static void initializeNetwork() {
		int message = 0;
		CHANNEL.messageBuilder(S2CSynchronizeOrigin.class, message++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(S2CSynchronizeOrigin::encode).decoder(S2CSynchronizeOrigin::decode)
				.consumer(S2CSynchronizeOrigin::handle).add();
		CHANNEL.messageBuilder(S2COpenOriginScreen.class, message++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(S2COpenOriginScreen::encode).decoder(S2COpenOriginScreen::decode)
				.consumer(S2COpenOriginScreen::handle).add();
		CHANNEL.messageBuilder(S2CConfirmOrigin.class, message++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(S2CConfirmOrigin::encode).decoder(S2CConfirmOrigin::decode)
				.consumer(S2CConfirmOrigin::handle).add();
		CHANNEL.messageBuilder(S2CSynchronizeBadges.class, message++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(S2CSynchronizeBadges::encode).decoder(S2CSynchronizeBadges::decode)
				.consumer(S2CSynchronizeBadges::handle).add();

		CHANNEL.messageBuilder(C2SChooseRandomOrigin.class, message++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(C2SChooseRandomOrigin::encode).decoder(C2SChooseRandomOrigin::decode)
				.consumer(C2SChooseRandomOrigin::handle).add();
		CHANNEL.messageBuilder(C2SChooseOrigin.class, message++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(C2SChooseOrigin::encode).decoder(C2SChooseOrigin::decode)
				.consumer(C2SChooseOrigin::handle).add();

		Origins.LOGGER.debug("Registered {} packets", message);
	}

	public static void initialize() {
		IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
		OriginRegisters.register();
		OriginsPowerTypes.register();
		mod.addListener(OriginsCommon::registerCapabilities);
		mod.addListener(OriginsCommon::commonSetup);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(IOriginContainer.class);
	}

	public static void commonSetup(FMLCommonSetupEvent event) {
		initializeNetwork();
		event.enqueueWork(ModLoot::register);
	}
}

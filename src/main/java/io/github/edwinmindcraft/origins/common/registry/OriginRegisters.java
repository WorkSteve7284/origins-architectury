package io.github.edwinmindcraft.origins.common.registry;

import com.google.common.collect.ImmutableSet;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.origin.Impact;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.registry.OriginsBuiltinRegistries;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;

public class OriginRegisters {
	public static final DeferredRegister<PowerFactory<?>> POWER_FACTORIES = DeferredRegister.create(ApoliRegistries.POWER_FACTORY_CLASS, Origins.MODID);
	public static final DeferredRegister<Origin> ORIGINS = DeferredRegister.create(Origin.class, Origins.MODID);

	public static final RegistryObject<Origin> EMPTY = ORIGINS.register("empty", () -> Origin.EMPTY);

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		OriginsBuiltinRegistries.ORIGINS = ORIGINS.makeRegistry("origins", () -> new RegistryBuilder<Origin>().setDefaultKey(Origins.identifier("empty")));;

		POWER_FACTORIES.register(bus);
		ORIGINS.register(bus);
	}
}

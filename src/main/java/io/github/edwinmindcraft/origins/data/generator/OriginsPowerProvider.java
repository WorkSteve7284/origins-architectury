package io.github.edwinmindcraft.origins.data.generator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.OriginsPowerTypes;
import io.github.apace100.origins.registry.ModBlocks;
import io.github.edwinmindcraft.apoli.api.configuration.*;
import io.github.edwinmindcraft.apoli.api.generator.PowerGenerator;
import io.github.edwinmindcraft.apoli.api.power.ConditionData;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.PowerData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.action.configuration.BlockConfiguration;
import io.github.edwinmindcraft.apoli.common.action.meta.IfElseConfiguration;
import io.github.edwinmindcraft.apoli.common.condition.configuration.EnchantmentConfiguration;
import io.github.edwinmindcraft.apoli.common.condition.configuration.FluidTagComparisonConfiguration;
import io.github.edwinmindcraft.apoli.common.condition.configuration.InBlockAnywhereConfiguration;
import io.github.edwinmindcraft.apoli.common.condition.meta.ConditionStreamConfiguration;
import io.github.edwinmindcraft.apoli.common.power.configuration.*;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import io.github.edwinmindcraft.apoli.common.registry.action.ApoliBlockActions;
import io.github.edwinmindcraft.apoli.common.registry.action.ApoliEntityActions;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliBlockConditions;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliEntityConditions;
import io.github.edwinmindcraft.apoli.common.registry.condition.ApoliItemConditions;
import io.github.edwinmindcraft.origins.common.power.configuration.NoSlowdownConfiguration;
import io.github.edwinmindcraft.origins.common.power.configuration.WaterVisionConfiguration;
import io.github.edwinmindcraft.origins.data.tag.OriginsBlockTags;
import io.github.edwinmindcraft.origins.data.tag.OriginsItemTags;
import net.minecraft.core.NonNullList;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OriginsPowerProvider extends PowerGenerator {
	public OriginsPowerProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, Origins.MODID, existingFileHelper);
	}

	private static Map<String, ConfiguredPower<?, ?>> makeAquaAffinity() {
		ImmutableMap.Builder<String, ConfiguredPower<?, ?>> builder = ImmutableMap.builder();
		builder.put("underwater",
				ApoliPowers.MODIFY_BREAK_SPEED.get()
						.configure(
								new ModifyValueBlockConfiguration(ListConfiguration.of(new AttributeModifier(UUID.randomUUID(), "Unnamed attribute modifier", 4, AttributeModifier.Operation.MULTIPLY_TOTAL)), null),
								PowerData.builder().addCondition(ApoliEntityConditions.and(
										ApoliEntityConditions.SUBMERGED_IN.get().configure(new TagConfiguration<>(FluidTags.WATER)),
										ApoliEntityConditions.ENCHANTMENT.get().configure(new EnchantmentConfiguration(new IntegerComparisonConfiguration(Comparison.EQUAL, 0), Enchantments.AQUA_AFFINITY, EnchantmentConfiguration.Calculation.SUM))
								)).build()));
		builder.put("ungrounded",
				ApoliPowers.MODIFY_BREAK_SPEED.get()
						.configure(
								new ModifyValueBlockConfiguration(ListConfiguration.of(new AttributeModifier(UUID.randomUUID(), "Unnamed attribute modifier", 4, AttributeModifier.Operation.MULTIPLY_TOTAL)), null),
								PowerData.builder().addCondition(ApoliEntityConditions.and(
										ApoliEntityConditions.FLUID_HEIGHT.get().configure(new FluidTagComparisonConfiguration(new DoubleComparisonConfiguration(Comparison.GREATER_THAN, 0), FluidTags.WATER)),
										ApoliEntityConditions.ON_BLOCK.get().configure(FieldConfiguration.of(Optional.empty()), new ConditionData(true))
								)).build()));
		return builder.build();
	}

	private static Map<String, ConfiguredPower<?, ?>> makeMasterOfWebs() {
		ConfiguredBlockCondition<?, ?> inCobwebs = ApoliBlockConditions.IN_TAG.get().configure(new TagConfiguration<>(OriginsBlockTags.COBWEBS));

		ImmutableMap.Builder<String, ConfiguredPower<?, ?>> builder = ImmutableMap.builder();
		builder.put("webbing", ApoliPowers.TARGET_ACTION_ON_HIT.get().configure(
				new ConditionedCombatActionConfiguration(200, new HudRender(true, 5, Origins.identifier("textures/gui/resource_bar.png"), null, false), null, null,
						ApoliEntityActions.BLOCK_ACTION_AT.get().configure(FieldConfiguration.of(
								ApoliBlockActions.IF_ELSE.get().configure(new IfElseConfiguration<>(
										ApoliBlockConditions.REPLACEABLE.get().configure(NoConfiguration.INSTANCE),
										ApoliBlockActions.SET_BLOCK.get().configure(new BlockConfiguration(ModBlocks.TEMPORARY_COBWEB.get())),
										null, ApoliBlockActions.PREDICATE, ApoliBlockActions.EXECUTOR))
						))),
				PowerData.DEFAULT));
		builder.put("no_slowdown", OriginsPowerTypes.NO_SLOWDOWN.get().configure(new NoSlowdownConfiguration(OriginsBlockTags.COBWEBS), PowerData.DEFAULT));
		builder.put("climbing", ApoliPowers.CLIMBING.get().configure(
				new ClimbingConfiguration(true, ApoliEntityConditions.POWER_ACTIVE.get().configure(new PowerReference(Origins.identifier("master_of_webs_climbing")))),
				PowerData.builder().addCondition(ApoliEntityConditions.and(
						ApoliEntityConditions.IN_BLOCK_ANYWHERE.get().configure(new InBlockAnywhereConfiguration(inCobwebs)),
						ApoliEntityConditions.POWER_ACTIVE.get().configure(new PowerReference(Origins.identifier("climbing_toggle")))
				)).build()
		));
		builder.put("punch_through", ApoliPowers.PREVENT_BLOCK_SELECTION.get().configure(FieldConfiguration.of(Optional.of(inCobwebs)), PowerData.builder()
				.addCondition(ApoliEntityConditions.SNEAKING.get().configure(NoConfiguration.INSTANCE, new ConditionData(true))).build()));
		builder.put("sense", ApoliPowers.ENTITY_GLOW.get().configure(new EntityGlowConfiguration(ApoliEntityConditions.and(
				ApoliEntityConditions.IN_BLOCK_ANYWHERE.get().configure(new InBlockAnywhereConfiguration(inCobwebs)),
				ApoliEntityConditions.ENTITY_GROUP.get().configure(FieldConfiguration.of(MobType.ARTHROPOD), new ConditionData(true))
		)), PowerData.DEFAULT));
		//FIXME Recipe serialization is broken for now.
		builder.put("web_crafting", ApoliPowers.RECIPE.get().configure(FieldConfiguration.of(
				new ShapelessRecipe(Origins.identifier("master_of_webs/web_crafting"), "", Items.COBWEB.getDefaultInstance(), NonNullList.of(Ingredient.of(Items.STRING), Ingredient.of(Items.STRING)))
		), PowerData.DEFAULT));
		return builder.build();
	}

	@Override
	protected void populate() {
		PowerData hidden = PowerData.builder().hidden().build();
		ConditionData inverted = new ConditionData(true);
		this.add("like_water", OriginsPowerTypes.LIKE_WATER.get().configure(NoConfiguration.INSTANCE, PowerData.DEFAULT));
		this.add("water_breathing", OriginsPowerTypes.WATER_BREATHING.get().configure(NoConfiguration.INSTANCE, PowerData.DEFAULT));
		this.add("scare_creepers", OriginsPowerTypes.SCARE_CREEPERS.get().configure(NoConfiguration.INSTANCE, PowerData.DEFAULT));
		this.add("water_vision", ApoliPowers.MULTIPLE.get().configure(new MultipleConfiguration<>(ImmutableMap.of(
						"vision", OriginsPowerTypes.WATER_VISION.get().configure(new WaterVisionConfiguration(1.0F), PowerData.builder()
								.addCondition(ApoliEntityConditions.POWER_ACTIVE.get().configure(new PowerReference(Origins.identifier("water_vision_toggle")))).build()),
						"toggle", ApoliPowers.TOGGLE_NIGHT_VISION.get().configure(new ToggleNightVisionConfiguration(true, IActivePower.Key.PRIMARY, 1.0F), PowerData.builder()
								.addCondition(ApoliEntityConditions.SUBMERGED_IN.get().configure(new TagConfiguration<>(FluidTags.WATER))).build()))),
				PowerData.DEFAULT));
		this.add("no_cobweb_slowdown", OriginsPowerTypes.NO_SLOWDOWN.get().configure(new NoSlowdownConfiguration(OriginsBlockTags.COBWEBS), hidden));
		//this.add("master_of_webs", ApoliPowers.MULTIPLE.get().configure(new MultipleConfiguration<>(makeMasterOfWebs()), PowerData.DEFAULT));
		this.add("conduit_power_on_land", OriginsPowerTypes.CONDUIT_POWER_ON_LAND.get().configure(NoConfiguration.INSTANCE, hidden));


		this.add("aerial_combatant", ApoliPowers.MODIFY_DAMAGE_DEALT.get().configure(new ModifyDamageDealtConfiguration(new AttributeModifier("Extra damage while fall flying", 1, AttributeModifier.Operation.MULTIPLY_BASE)), PowerData.builder().addCondition(ApoliEntityConditions.FALL_FLYING.get().configure(NoConfiguration.INSTANCE)).build()));
		this.add("air_from_potions", ApoliPowers.ACTION_ON_ITEM_USE.get().configure(new ActionOnItemUseConfiguration(ApoliItemConditions.INGREDIENT.get().configure(FieldConfiguration.of(Ingredient.of(Items.POTION))), ApoliEntityActions.GAIN_AIR.get().configure(FieldConfiguration.of(60)), null), hidden));
		this.add("aqua_affinity", ApoliPowers.MULTIPLE.get().configure(new MultipleConfiguration<>(makeAquaAffinity()), PowerData.DEFAULT));
		this.add("aquatic", ApoliPowers.ENTITY_GROUP.get().configure(FieldConfiguration.of(MobType.WATER), hidden));
		this.add("arcane_skin", ApoliPowers.MODEL_COLOR.get().configure(new ColorConfiguration(0.5F, 0.5F, 1.0F, 0.7F), PowerData.DEFAULT));
		this.add("arthropod", ApoliPowers.ENTITY_GROUP.get().configure(FieldConfiguration.of(MobType.ARTHROPOD), hidden));

		this.add("burn_in_daylight", ApoliPowers.BURN.get().configure(new BurnConfiguration(20, 6), PowerData.builder().addCondition(ApoliEntityConditions.and(ApoliEntityConditions.EXPOSED_TO_SUN.get().configure(NoConfiguration.INSTANCE), ApoliEntityConditions.INVISIBLE.get().configure(NoConfiguration.INSTANCE, inverted))).build()));
		this.add("burning_wrath", ApoliPowers.MODIFY_DAMAGE_DEALT.get().configure(new ModifyDamageDealtConfiguration(new AttributeModifier("Additional damage while on fire", 3, AttributeModifier.Operation.ADDITION)), PowerData.builder().addCondition(ApoliEntityConditions.ON_FIRE.get().configure(NoConfiguration.INSTANCE)).build()));
		ConfiguredItemCondition<?, ?> carnivore = ApoliItemConditions.and(
				ApoliItemConditions.OR.get().configure(ConditionStreamConfiguration.or(ImmutableList.of(ApoliItemConditions.INGREDIENT.get().configure(FieldConfiguration.of(Ingredient.of(OriginsItemTags.MEAT))), ApoliItemConditions.MEAT.get().configure(NoConfiguration.INSTANCE)), ApoliItemConditions.PREDICATE), inverted),
				ApoliItemConditions.FOOD.get().configure(NoConfiguration.INSTANCE),
				ApoliItemConditions.INGREDIENT.get().configure(FieldConfiguration.of(Ingredient.of(OriginsItemTags.IGNORE_DIET)), inverted));
		this.add("carnivore", ApoliPowers.PREVENT_ITEM_USAGE.get().configure(FieldConfiguration.of(Optional.of(carnivore)), PowerData.DEFAULT));
		this.add("cat_vision", ApoliPowers.NIGHT_VISION.get().configure(FieldConfiguration.of(0.4F), PowerData.builder().addCondition(ApoliEntityConditions.SUBMERGED_IN.get().configure(new TagConfiguration<>(FluidTags.WATER), inverted)).build()));

	}
}

package io.github.apace100.origins.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.jetbrains.annotations.NotNull;

public class WaterProtectionEnchantment extends Enchantment {

    public WaterProtectionEnchantment(Rarity weight, EnchantmentCategory type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    public int getMinCost(int level) {
        return 8 + level * 5;
    }

    public int getMaxCost(int level) {
        return this.getMinCost(level) + 8;
    }

    public boolean isTreasureOnly() {
        return true;
    }

    public int getMaxLevel() {
        return 4;
    }
    
}

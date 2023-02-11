package com.mactso.hostilewatermobs.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class WaterSnakeGland extends Item {
	public WaterSnakeGland(final Item.Properties builder) {
		super(builder);

	}

	public ItemStack finishUsingItem(final ItemStack stack, final Level level, final LivingEntity livingEntityIn) {

		ItemStack returnStack = super.finishUsingItem(stack, level, livingEntityIn);

		if (livingEntityIn instanceof Player) {
		((Player) livingEntityIn).getCooldowns().addCooldown((Item) this, 20);
		}	
		
		MobEffect effect = MobEffects.DOLPHINS_GRACE;
		int duration = 600;

		if (GlandUtility.applyGlandEffect(livingEntityIn, effect, duration)) {
			returnStack.setCount(returnStack.getCount()-1);
		}

		return returnStack;

	}
}

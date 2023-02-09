package com.mactso.hostilewatermobs.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class WaterSnakeGland extends Item {
	public WaterSnakeGland(final Item.Properties builder) {
		super(builder);

	}

	public ItemStack finishUsingItem(final ItemStack stack, final World level, final LivingEntity livingEntityIn) {

		ItemStack returnStack = super.finishUsingItem(stack, level, livingEntityIn);

		if (livingEntityIn instanceof PlayerEntity) {
		((PlayerEntity) livingEntityIn).getCooldowns().addCooldown((Item) this, 20);
		}	
		
		Effect effect = Effects.DOLPHINS_GRACE;
		int duration = 600;

		if (GlandUtility.applyGlandEffect(livingEntityIn, effect, duration)) {
			returnStack.setCount(returnStack.getCount()-1);
		}

		return returnStack;

	}
}

package com.mactso.hostilewatermobs.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class WaterSnakeGland extends Item {
	public WaterSnakeGland(final Item.Properties builder) {
		super(builder);

	}

	public ItemStack finishUsingItem(final ItemStack stack, final Level worldIn, final LivingEntity livingEntityIn) {

		ItemStack returnStack = super.finishUsingItem(stack, worldIn, livingEntityIn);
		if (livingEntityIn instanceof Player) {
		((Player) livingEntityIn).getCooldowns().addCooldown((Item) this, 20);

		}		
		MobEffectInstance ei = livingEntityIn.getEffect(MobEffects.DOLPHINS_GRACE);
		
		if (ei != null) {
			if ((ei.getDuration() > 10) && (ei.getDuration()<600)) {
				livingEntityIn.playSound(SoundEvents.DISPENSER_FAIL, 0.8f, 0.5f);
				return returnStack;
			}
			livingEntityIn.removeEffectNoUpdate(MobEffects.DOLPHINS_GRACE );
		}
		
		returnStack.setCount(returnStack.getCount() - 1);
		if (worldIn.isClientSide) {
			return returnStack;
		}

		livingEntityIn.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 600, 0, false, true));
		livingEntityIn.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 0.8f, 0.5f);
		return returnStack;


	}
}

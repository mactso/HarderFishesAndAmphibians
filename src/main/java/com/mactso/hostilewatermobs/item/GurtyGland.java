package com.mactso.hostilewatermobs.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class GurtyGland extends Item {
	public GurtyGland(final Item.Properties builder) {
		super(builder);

	}

	public ItemStack finishUsingItem(final ItemStack stack, final Level worldIn, final LivingEntity livingEntityIn) {

		ItemStack returnStack = super.finishUsingItem(stack, worldIn, livingEntityIn);
		if (livingEntityIn instanceof Player) {
		((Player) livingEntityIn).getCooldowns().addCooldown((Item) this, 20);

		}		
		MobEffectInstance ei = livingEntityIn.getEffect(MobEffects.WATER_BREATHING);
		
		if (ei != null) {
			if (ei.getDuration() > 10) {
				livingEntityIn.playSound(SoundEvents.DISPENSER_FAIL, 0.8f, 0.5f);
				return returnStack;
			}
			livingEntityIn.removeEffectNoUpdate(MobEffects.WATER_BREATHING );
		}
		
		returnStack.setCount(returnStack.getCount() - 1);
		if (worldIn.isClientSide) {
			return returnStack;
		}

		livingEntityIn.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0, false, true));
		livingEntityIn.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 0.8f, 0.5f);
		return returnStack;

//			worldIn.playSound((PlayerEntity) null, leX, leY, leZ, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
//					SoundCategory.PLAYERS, 0.6f, 0.5f);



	}
}

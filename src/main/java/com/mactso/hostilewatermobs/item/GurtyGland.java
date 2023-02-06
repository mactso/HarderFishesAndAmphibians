package com.mactso.hostilewatermobs.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
		
		if (livingEntityIn.isInWater()) {
			Random rand = livingEntityIn.level.getRandom();
			BlockPos pos = livingEntityIn.eyeBlockPosition();
			Vec3 lookVector = livingEntityIn.getLookAngle();
			for(int i = 0; i < 31; ++i) {
	        	double x = pos.getX() +0.5d + (lookVector.x * 1.0D) -.5d + rand.nextDouble(1.0d);
	        	double y = pos.getY() +0.5d + (lookVector.y * 0.1D) -.5d + rand.nextDouble(1.0d);
	        	double z = pos.getZ() +0.5d + (lookVector.z * 1.0D) -.5d + rand.nextDouble(1.0d);
	        	livingEntityIn.level.addParticle(ParticleTypes.BUBBLE, x,y,z, 0.0D, 0.05D, 0.0D);
	         }	
		}
		
		if (worldIn.isClientSide) {
			return returnStack;
		}
		returnStack.setCount(returnStack.getCount()-1);

		livingEntityIn.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0, false, true));
		livingEntityIn.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 0.8f, 0.5f);
		return returnStack;

	}
}

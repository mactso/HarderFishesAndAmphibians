package com.mactso.hostilewatermobs.item;

import java.util.Random;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;

public class GlandUtility {

	public static boolean applyGlandEffect(final LivingEntity livingEntityIn, Effect effect, int duration) {
		EffectInstance ei = livingEntityIn.getEffect(effect);
		if (ei != null) {
			if (ei.getDuration() > 10) {
				livingEntityIn.playSound(SoundEvents.DISPENSER_FAIL, 0.8f, 0.5f);
				return false;
			} else {
				livingEntityIn.removeEffectNoUpdate(effect);
			}
		}
		livingEntityIn.addEffect(new EffectInstance(effect, duration, 0, false, true));
		livingEntityIn.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 0.8f, 0.5f);
		makeBubblesIfInWater(livingEntityIn);
		return true;
	}

	private static void makeBubblesIfInWater(final LivingEntity livingEntityIn) {
		if (livingEntityIn.isInWater()) {
			Random rand = livingEntityIn.level.getRandom();
			Vector3d pos = livingEntityIn.getEyePosition(1.0f);
			Vector3d lookVector = livingEntityIn.getLookAngle();
			for (int i = 0; i < 31; ++i) {
				double x = pos.x + (lookVector.x * 1.0D) - .5d + rand.nextDouble();
				double y = pos.y + (lookVector.y * 0.1D) - .5d + rand.nextDouble();
				double z = pos.z + (lookVector.z * 1.0D) - .5d + rand.nextDouble();
				livingEntityIn.level.addParticle(ParticleTypes.BUBBLE, x, y, z, 0.0D, 0.05D, 0.0D);
			}
		}
	}
}

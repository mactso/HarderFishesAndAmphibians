package com.mactso.hostilewatermobs.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import java.util.Random;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class GlandUtility {

	public static boolean applyGlandEffect(final LivingEntity livingEntityIn, MobEffect effect, int duration) {
		MobEffectInstance ei = livingEntityIn.getEffect(effect);
		if (ei != null) {
			if (ei.getDuration() > 10) {
				livingEntityIn.playSound(SoundEvents.DISPENSER_FAIL, 0.8f, 0.5f);
				return false;
			} else {
				livingEntityIn.removeEffectNoUpdate(effect);
			}
		}
		livingEntityIn.addEffect(new MobEffectInstance(effect, duration, 0, false, true));
		livingEntityIn.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 0.8f, 0.5f);
		makeBubblesIfInWater(livingEntityIn);
		return true;
	}

	private static void makeBubblesIfInWater(final LivingEntity livingEntityIn) {
		if (livingEntityIn.isInWater()) {
			Random rand = livingEntityIn.level.getRandom();
			Vec3 pos = livingEntityIn.getEyePosition(1.0f);
			Vec3 lookVector = livingEntityIn.getLookAngle();
			for (int i = 0; i < 31; ++i) {
				double x = pos.x + (lookVector.x * 1.0D) - .5d + rand.nextDouble();
				double y = pos.y + (lookVector.y * 0.1D) - .5d + rand.nextDouble();
				double z = pos.z + (lookVector.z * 1.0D) - .5d + rand.nextDouble();
				livingEntityIn.level.addParticle(ParticleTypes.BUBBLE, x, y, z, 0.0D, 0.05D, 0.0D);
			}
		}
	}
}

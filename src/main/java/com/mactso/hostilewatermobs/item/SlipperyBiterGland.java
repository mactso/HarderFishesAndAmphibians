package com.mactso.hostilewatermobs.item;

import net.minecraft.util.SoundEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class SlipperyBiterGland extends Item {
	public SlipperyBiterGland(final Item.Properties builder) {
		super(builder);

	}

	public ItemStack onItemUseFinish(final ItemStack stack, final World worldIn, final LivingEntity livingEntityIn) {
		final ItemStack returnStack = super.onItemUseFinish(stack, worldIn, livingEntityIn);

		if (worldIn.isRemote) {
			return returnStack;
		}

		final double leX = livingEntityIn.getPosX();
		final double leY = livingEntityIn.getPosY();
		final double leZ = livingEntityIn.getPosZ();

		for (int i = 0; i < 8; ++i) {
			final double potX = livingEntityIn.getPosX() + (livingEntityIn.getRNG().nextDouble() - 0.5) * 18.0;
			final double potY = MathHelper.clamp(
					livingEntityIn.getPosY() + (livingEntityIn.getRNG().nextInt(4) - 2), 0.0,
					(double) (worldIn.func_234938_ad_() - 1));
			final double potZ = livingEntityIn.getPosZ() + (livingEntityIn.getRNG().nextDouble() - 0.5) * 18.0;
			if (livingEntityIn.isPassenger()) {
				livingEntityIn.stopRiding();
			}
			if (livingEntityIn.attemptTeleport(potX, potY, potZ, true)) {
				worldIn.playSound((PlayerEntity) null, leX, leY, leZ, SoundEvents.ENTITY_FOX_TELEPORT, SoundCategory.PLAYERS,
						0.6f, 0.5f);
				livingEntityIn.playSound(SoundEvents.ENTITY_FOX_TELEPORT, 0.8f, 0.5f);
				break;
			}
		}
		if (livingEntityIn instanceof PlayerEntity) {
			((PlayerEntity) livingEntityIn).getCooldownTracker().setCooldown((Item) this, 20);

		}

		return returnStack;
	}
}

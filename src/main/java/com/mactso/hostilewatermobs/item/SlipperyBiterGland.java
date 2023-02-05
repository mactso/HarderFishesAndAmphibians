package com.mactso.hostilewatermobs.item;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class SlipperyBiterGland extends Item {
	public SlipperyBiterGland(final Item.Properties builder) {
		super(builder);

	}

	public ItemStack finishUsingItem(final ItemStack stack, final Level worldIn, final LivingEntity livingEntityIn) {

		ItemStack returnStack = super.finishUsingItem(stack, worldIn, livingEntityIn);

		if (worldIn.isClientSide) {
			return returnStack;
		}
		returnStack.setCount(returnStack.getCount()-1);
		final double leX = livingEntityIn.getX();
		final double leY = livingEntityIn.getY();
		final double leZ = livingEntityIn.getZ();

		for (int i = 0; i < 8; ++i) {
			double potX = leX + (livingEntityIn.getRandom().nextDouble() - 0.5) * 18.0;
			double potY = Mth.clamp(
					leY + (livingEntityIn.getRandom().nextInt(4) - 2), 0.0,
					(double) (worldIn.getHeight() - 1));
			double potZ = leZ + (livingEntityIn.getRandom().nextDouble() - 0.5) * 18.0;
			if (livingEntityIn.isPassenger()) {
				livingEntityIn.stopRiding();
			}
			if (potY < 5) potY = leY;
			BlockPos targetPos = new BlockPos (potX, potY, potZ);
		    if (worldIn.hasChunkAt(targetPos)) {
				boolean topSafe = worldIn.getFluidState(targetPos.above()).is(FluidTags.WATER)
						|| worldIn.getBlockState(targetPos.above()).getBlock() == Blocks.AIR;

				boolean bottomSafe = worldIn.getBlockState(targetPos).getBlock() == Blocks.AIR
						|| worldIn.getFluidState(targetPos).is(FluidTags.WATER);

				if (topSafe && bottomSafe ) {
					livingEntityIn.teleportTo(potX, potY, potZ);
					worldIn.playSound((Player) null, leX, leY, leZ, SoundEvents.FOX_TELEPORT, SoundSource.PLAYERS,
								0.6f, 0.5f);
					livingEntityIn.playSound(SoundEvents.FOX_TELEPORT, 0.8f, 0.5f);
					break;
				}		    	
		    }
		}
		if (livingEntityIn instanceof Player) {
			((Player) livingEntityIn).getCooldowns().addCooldown((Item) this, 20);

		}

		return returnStack;
	}
}

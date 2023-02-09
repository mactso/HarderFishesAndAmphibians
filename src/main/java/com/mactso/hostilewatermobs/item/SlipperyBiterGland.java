package com.mactso.hostilewatermobs.item;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SlipperyBiterGland extends Item {
	public SlipperyBiterGland(final Item.Properties builder) {
		super(builder);

	}

	public ItemStack finishUsingItem(final ItemStack stack, final World worldIn, final LivingEntity livingEntityIn) {

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
			double potY = MathHelper.clamp(
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
					worldIn.playSound((PlayerEntity) null, leX, leY, leZ, SoundEvents.FOX_TELEPORT, SoundCategory.PLAYERS,
								0.6f, 0.5f);
					livingEntityIn.playSound(SoundEvents.FOX_TELEPORT, 0.8f, 0.5f);
					break;
				}		    	
		    }
		}
		if (livingEntityIn instanceof PlayerEntity) {
			((PlayerEntity) livingEntityIn).getCooldowns().addCooldown((Item) this, 20);

		}

		return returnStack;
	}
}

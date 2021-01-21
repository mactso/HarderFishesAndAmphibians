package com.mactso.hostilewatermobs.item;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class GurtyGland extends Item {
	public GurtyGland(final Item.Properties builder) {
		super(builder);

	}

	public ItemStack onItemUseFinish(final ItemStack stack, final World worldIn, final LivingEntity livingEntityIn) {

		ItemStack returnStack = super.onItemUseFinish(stack, worldIn, livingEntityIn);
		if (livingEntityIn instanceof PlayerEntity) {
		((PlayerEntity) livingEntityIn).getCooldownTracker().setCooldown((Item) this, 20);

		}		
		EffectInstance ei = livingEntityIn.getActivePotionEffect(Effects.WATER_BREATHING);
		
		if (ei != null) {
			if (ei.getDuration() > 10) {
				livingEntityIn.playSound(SoundEvents.BLOCK_DISPENSER_FAIL, 0.8f, 0.5f);
				return returnStack;
			}
			livingEntityIn.removeActivePotionEffect(Effects.WATER_BREATHING );
		}
		
		returnStack.setCount(returnStack.getCount() - 1);
		if (worldIn.isRemote) {
			return returnStack;
		}

		livingEntityIn.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, 600, 0, false, true));
		livingEntityIn.playSound(SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 0.8f, 0.5f);
		return returnStack;

//			worldIn.playSound((PlayerEntity) null, leX, leY, leZ, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
//					SoundCategory.PLAYERS, 0.6f, 0.5f);



	}
}

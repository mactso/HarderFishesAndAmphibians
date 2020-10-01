package com.mactso.harderfishesandamphibians.entities;

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;


public class RiverGuardianEntity extends GuardianEntity {

	public static final float field_213629_b = EntityType.GUARDIAN.getWidth() * 0.75f;

	public RiverGuardianEntity(EntityType<? extends RiverGuardianEntity> type, World worldIn) {
		super(type, worldIn);
		this.enablePersistence();
		if (this.wander != null) {
			this.wander.setExecutionChance(200);
		}

	}

	public static AttributeModifierMap.MutableAttribute func_234283_m_() {
		return GuardianEntity.func_234292_eK_().createMutableAttribute(Attributes.MOVEMENT_SPEED, (double) 0.7F)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 16.0D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
				.createMutableAttribute(Attributes.MAX_HEALTH, 12.0D);
	}

	   public static boolean canSpawn (EntityType<? extends RiverGuardianEntity> type, IWorld worldIn, SpawnReason reason, BlockPos p_223363_3_, Random randomIn) {
		      return worldIn.getBlockState(p_223363_3_).isIn(Blocks.WATER) && worldIn.getBlockState(p_223363_3_.up()).isIn(Blocks.WATER);
	   }
	
	   public boolean attackEntityFrom(DamageSource source, float amount) {
		      if (!this.isMoving() && !source.isMagicDamage() && source.getImmediateSource() instanceof LivingEntity) {
		         LivingEntity livingentity = (LivingEntity)source.getImmediateSource();
		         if (!source.isExplosion()) {
		            livingentity.attackEntityFrom(DamageSource.causeThornsDamage(this), 1.0F);
		         }
		      }

		      if (this.wander != null) {
		         this.wander.makeUpdate();
		      }

		      return super.attackEntityFrom(source, amount);
		   }
	   
	public int getAttackDuration() {
		return 40;
	}

	
	
	protected SoundEvent getAmbientSound() {
		// TODO Raise Pitch because smaller
		return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_GUARDIAN_AMBIENT
				: SoundEvents.ENTITY_GUARDIAN_AMBIENT_LAND;
	}

	// TODO Raise Pitch because smaller
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_HURT
				: SoundEvents.ENTITY_ELDER_GUARDIAN_HURT_LAND;
	}

	// TODO Raise Pitch because smaller
	protected SoundEvent getDeathSound() {
		return this.isInWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH
				: SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH_LAND;
	}

	protected SoundEvent getFlopSound() {
		return SoundEvents.ENTITY_ELDER_GUARDIAN_FLOP;
	}

	protected void updateAITasks() {
		super.updateAITasks();
//		int i = 1200;
//		if ((this.ticksExisted + this.getEntityId()) % 1200 == 0) {
//			Effect effect = Effects.MINING_FATIGUE;
//			List<ServerPlayerEntity> list = ((ServerWorld) this.world).getPlayers((p_210138_1_) -> {
//				return this.getDistanceSq(p_210138_1_) < 2500.0D
//						&& p_210138_1_.interactionManager.survivalOrAdventure();
//			});
//			int j = 2;
//			int k = 6000;
//			int l = 1200;
//
//			for (ServerPlayerEntity serverplayerentity : list) {
//				if (!serverplayerentity.isPotionActive(effect)
//						|| serverplayerentity.getActivePotionEffect(effect).getAmplifier() < 2
//						|| serverplayerentity.getActivePotionEffect(effect).getDuration() < 1200) {
//					serverplayerentity.connection.sendPacket(new SChangeGameStatePacket(
//							SChangeGameStatePacket.field_241774_k_, this.isSilent() ? 0.0F : 1.0F));
//					serverplayerentity.addPotionEffect(new EffectInstance(effect, 6000, 2));
//				}
//			}
//		}

		if (!this.detachHome()) {
			this.setHomePosAndDistance(this.getPosition(), 16);
		}

	}
}

package com.mactso.harderfishesandamphibians.entities;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.harderfishesandamphibians.sound.ModSounds;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;


public class RiverGuardianEntity extends GuardianEntity {

    public static final float field_213629_b = EntityType.GUARDIAN.getWidth() * 0.37f;
    private static final DataParameter<Boolean> MOVING = EntityDataManager.createKey(GuardianEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(RiverGuardianEntity.class, DataSerializers.VARINT);
    
	public RiverGuardianEntity(EntityType<? extends RiverGuardianEntity> type, World worldIn) {
		super(type, worldIn);
		this.enablePersistence();
		if (this.wander != null) {
			this.wander.setExecutionChance(400);
		}

	}

	   protected void registerGoals() {
		      MoveTowardsRestrictionGoal movetowardsrestrictiongoal = new MoveTowardsRestrictionGoal(this, 1.0D);
		      this.wander = new RandomWalkingGoal(this, 1.0D, 80);
		      this.goalSelector.addGoal(4, new RiverGuardianEntity.AttackGoal(this));
		      this.goalSelector.addGoal(5, movetowardsrestrictiongoal);
		      this.goalSelector.addGoal(7, this.wander);
		      this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		      this.goalSelector.addGoal(8, new LookAtGoal(this, GuardianEntity.class, 12.0F, 0.01F));
		      this.goalSelector.addGoal(9, new LookRandomlyGoal(this));
		      this.wander.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		      movetowardsrestrictiongoal.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, new RiverGuardianEntity.TargetPredicate(this)));
		   }
	
	public static AttributeModifierMap.MutableAttribute registerAttributes() {
		return GuardianEntity.func_234292_eK_().createMutableAttribute(Attributes.MOVEMENT_SPEED, (double) 0.7F)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 16.0D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.5D)
				.createMutableAttribute(Attributes.MAX_HEALTH, 12.0D);
	}

	   public static boolean canSpawn (EntityType<? extends RiverGuardianEntity> type, IWorld world, SpawnReason reason, BlockPos pos, Random randomIn) {

		   boolean inSwamp = false;
		   boolean inOcean = false;
		   boolean inRiver = false;
		   int spawnChance = 1;
		   int riverGuardianCap = 41;
	   
		   
		   if (world.getDifficulty() == Difficulty.PEACEFUL) return false;
		   
		   if (reason == SpawnReason.SPAWNER) return true;
		   if (reason == SpawnReason.SPAWN_EGG) return true;
		   if (world.getBiome(pos).getCategory() == Category.OCEAN) {
			   inOcean = true;
			   if (pos.getY() > 34) {
				   return false;
			   }
		   }

		   if (world.getBiome(pos).getCategory() == Category.SWAMP) {
			   inSwamp = true;
			   spawnChance += 7;
			   riverGuardianCap += 7;
		   }

		   if (world.getBiome(pos).getCategory() == Category.RIVER) {
			   inRiver = true;
			   riverGuardianCap += 9;
			   spawnChance += 9;
			   System.out.println ("spawn riverGuardian +9");
		   }

		   
		   if (world instanceof ServerWorld) {
//			   ServerWorld serverWorld = (ServerWorld) world;
			   if (((ServerWorld) world).getEntities(ModEntities.RIVER_GUARDIAN, (entity)-> true).size() > riverGuardianCap) { 
				   return false;
			   }
//			   ((ServerWorld) world).getEntities(); // for a total count of all entities.
		   }

		   
		   if (pos.getY() < 24) {
			   riverGuardianCap += 6;
			   spawnChance += 6;			   
		   }
		   
		   if (((randomIn.nextInt(30) < spawnChance ) || !(world.canBlockSeeSky(pos))) && world.getFluidState(pos).isTagged(FluidTags.WATER)) {
			   Chunk c = (Chunk) world.getChunk(pos);
			   ClassInheritanceMultiMap<Entity>[] aL = c.getEntityLists();
			   int height = pos.getY()/16;
			   if (height < 0) height = 0; // cubic chunks
			   if (aL[height].getByClass(RiverGuardianEntity.class).size() > 1) {
				  return false; 
			   }
			   return true;
		   }
		   int z= 3;
		   return false;
	   }
	
	   public boolean attackEntityFrom(DamageSource source, float amount) {
		   if (!this.isMoving() && !source.isMagicDamage() && source.getImmediateSource() instanceof LivingEntity) {
		         LivingEntity livingentity = (LivingEntity)source.getImmediateSource();
		         if (!source.isExplosion()) {
		        	livingentity.heal(1.0f);  // this negates the 2.0 damage caused by super and avoids nasty ASM code.
//		            livingentity.attackEntityFrom(DamageSource.causeThornsDamage(this), 1.0F);
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
		return this.isInWaterOrBubbleColumn() ? ModSounds.RIVER_GUARDIAN_HURT
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
	
	   static class TargetPredicate implements Predicate<LivingEntity> {
		      private final RiverGuardianEntity parentEntity;

		      public TargetPredicate(RiverGuardianEntity guardian) {
		         this.parentEntity = guardian;
		      }

		      public boolean test(@Nullable LivingEntity p_test_1_) {
		         return (p_test_1_ instanceof PlayerEntity || p_test_1_ instanceof CodEntity) && p_test_1_.getDistanceSq(this.parentEntity) > 7.0D;
		      }
		   }
	   
	   static class AttackGoal extends Goal {
		      private final RiverGuardianEntity riverGuardian;
		      private int tickCounter;
//		      private final boolean isElder;

		      public AttackGoal(RiverGuardianEntity guardian) {
		         this.riverGuardian = guardian;
//		         this.isElder = guardian instanceof ElderGuardianEntity;
		         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		      }

		      /**
		       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		       * method as well.
		       */
		      public boolean shouldExecute() {
		         LivingEntity livingentity = this.riverGuardian.getAttackTarget();
		         return livingentity != null && livingentity.isAlive();
		      }

		      /**
		       * Returns whether an in-progress EntityAIBase should continue executing
		       */
		      public boolean shouldContinueExecuting() {
//			         return super.shouldContinueExecuting() && (this.isElder || this.riverGuardian.getDistanceSq(this.riverGuardian.getAttackTarget()) > 9.0D);

		    	  return super.shouldContinueExecuting() && ( this.riverGuardian.getDistanceSq(this.riverGuardian.getAttackTarget()) > 7.0D);
		      }

		      /**
		       * Execute a one shot task or start executing a continuous task
		       */
		      public void startExecuting() {
		         this.tickCounter = -10;
		         this.riverGuardian.getNavigator().clearPath();
		         this.riverGuardian.getLookController().setLookPositionWithEntity(this.riverGuardian.getAttackTarget(), 90.0F, 90.0F);
		         this.riverGuardian.isAirBorne = true;
		      }

		      /**
		       * Reset the task's internal state. Called when this task is interrupted by another one
		       */
		      public void resetTask() {
		         this.riverGuardian.setTargetedEntity(0);
		         this.riverGuardian.setAttackTarget((LivingEntity)null);
		         this.riverGuardian.wander.makeUpdate();
		      }

		      /**
		       * Keep ticking a continuous task that has already been started
		       */
		      public void tick() {
		         LivingEntity livingentity = this.riverGuardian.getAttackTarget();
		         this.riverGuardian.getNavigator().clearPath();
		         this.riverGuardian.getLookController().setLookPositionWithEntity(livingentity, 90.0F, 90.0F);
		         if (!this.riverGuardian.canEntityBeSeen(livingentity)) {
		            this.riverGuardian.setAttackTarget((LivingEntity)null);
		         } else {
		            ++this.tickCounter;
		            if (this.tickCounter == 0) {
		               this.riverGuardian.setTargetedEntity(this.riverGuardian.getAttackTarget().getEntityId());
		               if (!this.riverGuardian.isSilent()) {
		                  this.riverGuardian.world.setEntityState(this.riverGuardian, (byte)21);
		               }
		            } else if (this.tickCounter >= this.riverGuardian.getAttackDuration()) {
		               float f = 1.0F;
		               if (this.riverGuardian.world.getDifficulty() == Difficulty.HARD) {
		                  f += 1.0F;
		               }

//		               if (this.isElder) {
//		                  f += 2.0F;
//		               }

		               livingentity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this.riverGuardian, this.riverGuardian), f);
		               livingentity.attackEntityFrom(DamageSource.causeMobDamage(this.riverGuardian), (float)this.riverGuardian.getAttributeValue(Attributes.ATTACK_DAMAGE));
		               this.riverGuardian.setAttackTarget((LivingEntity)null);
		            }

		            super.tick();
		         }
		      }
		   }

	   protected void registerData() {
		      super.registerData();
		      this.dataManager.register(MOVING, false);
		      this.dataManager.register(TARGET_ENTITY, 0);
		   }
	   
	   private void setTargetedEntity(int entityId) {
		      this.dataManager.set(TARGET_ENTITY, entityId);
		   }
}

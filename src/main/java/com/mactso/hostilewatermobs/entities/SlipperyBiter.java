package com.mactso.hostilewatermobs.entities;

import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.sound.ModSounds;
import com.mactso.hostilewatermobs.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.FollowBoatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class SlipperyBiter extends WaterAnimal implements NeutralMob, Enemy {

	public static class MySmoothSwimmingMoveControl extends MoveControl {
		private final int maxTurnX;
		private final int maxTurnY;
		private final float inWaterSpeedModifier;
		private final float outsideWaterSpeedModifier;
		private final boolean applyGravity;
		private final SlipperyBiter parentEntity;

		public MySmoothSwimmingMoveControl(SlipperyBiter entityIn, int p_148071_, int p_148072_, float p_148073_,
				float p_148074_, boolean p_148075_) {
			super(entityIn);
			this.parentEntity = entityIn;
			this.maxTurnX = p_148071_;
			this.maxTurnY = p_148072_;
			this.inWaterSpeedModifier = p_148073_;
			this.outsideWaterSpeedModifier = p_148074_;
			this.applyGravity = p_148075_;
		}

		private Vec3 rotateVector(final Vec3 p_207400_1_) {
			Vec3 lvt_2_1_ = p_207400_1_.xRot(this.parentEntity.getViewXRot((float) 1.0) * 0.017453292f);
			lvt_2_1_ = lvt_2_1_.yRot(-this.parentEntity.yBodyRotO * 0.017453292f);
			return lvt_2_1_;
		}

		public void tick() {
			if (this.applyGravity && this.mob.isInWater()) {
				this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
			}
			trySlipperyDartingMove();
			if (this.operation == MoveControl.Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
				double d0 = this.wantedX - this.mob.getX();
				double d1 = this.wantedY - this.mob.getY();
				double d2 = this.wantedZ - this.mob.getZ();
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				if (d3 < (double) 2.5000003E-7F) {
					this.mob.setZza(0.0F);
				} else {
					float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
					this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, (float) this.maxTurnY));
					this.mob.yBodyRot = this.mob.getYRot();
					this.mob.yHeadRot = this.mob.getYRot();
					float f1 = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
					if (this.mob.isInWater()) {
						this.mob.setSpeed(f1 * this.inWaterSpeedModifier);
						double d4 = Math.sqrt(d0 * d0 + d2 * d2);
						if (Math.abs(d1) > (double) 1.0E-5F || Math.abs(d4) > (double) 1.0E-5F) {
							float f2 = -((float) (Mth.atan2(d1, d4) * (double) (180F / (float) Math.PI)));
							f2 = Mth.clamp(Mth.wrapDegrees(f2), (float) (-this.maxTurnX), (float) this.maxTurnX);
							this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, 5.0F));
						}

						float f4 = Mth.cos(this.mob.getXRot() * ((float) Math.PI / 180F));
						float f3 = Mth.sin(this.mob.getXRot() * ((float) Math.PI / 180F));
						this.mob.zza = f4 * f1;
						this.mob.yya = -f3 * f1;
					} else {
						this.mob.setSpeed(f1 * this.outsideWaterSpeedModifier);
					}

				}
			} else {
				this.mob.setSpeed(0.0F);
				this.mob.setXxa(0.0F);
				this.mob.setYya(0.0F);
				this.mob.setZza(0.0F);
			}
		}

		public void trySlipperyDartingMove() {
			if (parentEntity.slipperyTimer > parentEntity.level().getGameTime()) {
				return;
			}

			BlockPos biterPos = this.parentEntity.blockPosition();
			LivingEntity entity = this.parentEntity.getTarget();
			if (entity != null) {
				int distanceSq = (int) entity.distanceToSqr(this.parentEntity);
				if ((distanceSq > 80) && (distanceSq < 125)) {
					Level w = parentEntity.getCommandSenderWorld();
					Vec3 v = entity.getLookAngle();
					Vec3 vI = new Vec3 (v.x() * -4, v.y(), v.z() * -4);
					BlockPos tempPos = BlockPos.containing(entity.getX() + vI.x, entity.getY() + 1 + vI.y,
							entity.getZ() + vI.z());
					if (w.getFluidState(tempPos).is(FluidTags.WATER)) {
						w.setBlockAndUpdate(biterPos, Blocks.AIR.defaultBlockState());
						w.playSound((Player) null, biterPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 0.5f,
								0.5f);
						// water collapsing into resulting void
						w.playSound((Player) null, biterPos, SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 0.15f,
								0.15f);
						final Vec3 backwardsVector = this.rotateVector(new Vec3(0.0, -1.0, 0.0)).add(
								this.parentEntity.getX(), this.parentEntity.getY(),
								this.parentEntity.getZ());
						for (int i = 0; i < 15; ++i) {
							final Vec3 randXZVec = this.rotateVector(new Vec3(this.parentEntity.random.nextFloat() * 0.6 - 0.3,
											-1.0, w.random.nextFloat() * 0.6 - 0.3));
							final Vec3 randSpreadVec = randXZVec
									.scale(0.3 + this.parentEntity.random.nextFloat() * 2.0f);

							((ServerLevel) w).sendParticles((ParticleOptions) ParticleTypes.SOUL_FIRE_FLAME,
									backwardsVector.x, backwardsVector.y + 1.0, backwardsVector.z, 0, randSpreadVec.x,
									randSpreadVec.y, randSpreadVec.z, -0.10000000149011612);

							((ServerLevel) w).sendParticles((ParticleOptions) ParticleTypes.SOUL_FIRE_FLAME,
									backwardsVector.x, backwardsVector.y + 1.0, backwardsVector.z, 0, randSpreadVec.x,
									randSpreadVec.y, randSpreadVec.z, -0.10000000149011612);

							((ServerLevel) w).sendParticles((ParticleOptions) ParticleTypes.GLOW, backwardsVector.x,
									backwardsVector.y + 1.0, backwardsVector.z, 0, randSpreadVec.x, randSpreadVec.y,
									randSpreadVec.z, -0.10000000149011612);

						}
						this.parentEntity.teleportTo(tempPos.getX(), tempPos.getY() + .02f, tempPos.getZ());
						parentEntity.slipperyTimer = w.getGameTime() + 60;
					}
				}

			}
		}

	}

	static class TargetPredicate implements Predicate<LivingEntity> {
		private static int aa = 0;
		private final SlipperyBiter parentEntity;

		public TargetPredicate(SlipperyBiter entityIn) {
			this.parentEntity = entityIn;
		}

		public boolean test(@Nullable LivingEntity entity) {

			if (!(entity instanceof Player)) {
				return false;
			}

			if (((Player) entity).isCreative()) {
				return false;
			}

			if (((Player) entity).isSpectator()) {
				return false;
			}

			if (this.parentEntity.getTarget() != null) {
				if (entity == this.parentEntity.getKillCredit()) {
					return true;
				} else {
					return false;
				}
			}

			Level level = entity.getCommandSenderWorld();

			if (entity instanceof Turtle) {
				return false;
			}

			boolean targetInWater = false;
			if ((level.getFluidState(entity.blockPosition()).is(FluidTags.WATER))
					|| (level.getFluidState(entity.blockPosition().above()).is(FluidTags.WATER))) {
				targetInWater = true;
			}

			aa++;

			if (!targetInWater) {
				this.parentEntity.setPersistentAngerTarget(null);
				this.parentEntity.setTarget(null);
				this.parentEntity.setTargetedEntity(0);
				return false;
			}

			// this may be redundant. TargetPredicate may only be called for entities in
			// range.
			AttributeInstance followDistance = parentEntity.getAttribute(Attributes.FOLLOW_RANGE);
			int fRSq = (int) (followDistance.getValue() * followDistance.getValue());
			if (((int) entity.distanceToSqr(this.parentEntity) > fRSq)) {
				return false;
			}

			// 1 to ~500
			int distanceSq = (int) entity.distanceToSqr(this.parentEntity);

			String bC = Utility.getBiomeCategory(level,level.getBiome(this.parentEntity.blockPosition()));
			// a little less aggressive in swamps
			if ((bC == Utility.SWAMP) && (distanceSq > 255)) {
				if (aa % 3 == 0) {
					level.playSound((Player) entity, entity.blockPosition(), ModSounds.SLIPPERY_BITER_AMBIENT,
							SoundSource.HOSTILE, 1.0f, 1.0f);
				}
				return false;
			}

			// less aggressive in light, more aggressive in the dark
			int lightLevel = level.getMaxLocalRawBrightness(this.parentEntity.blockPosition());
			if ((lightLevel > 13) && (distanceSq > 255)) {
				if (aa % 3 == 0) {
					level.playSound((Player) entity, entity.blockPosition(), ModSounds.SLIPPERY_BITER_AMBIENT,
							SoundSource.HOSTILE, 1.0f, 1.0f);
				}
				return false;
			}

			if (distanceSq > 524) {
				if (aa % 3 == 0) {
					level.playSound((Player) entity, entity.blockPosition(), ModSounds.SLIPPERY_BITER_AMBIENT,
							SoundSource.HOSTILE, 1.0f, 1.0f);
				}
				return false;
			}

			this.parentEntity.setTarget(entity);
			this.parentEntity.setTargetedEntity(entity.getId());
			level.playSound((Player) null, entity.blockPosition(), ModSounds.SLIPPERY_BITER_AMBIENT, SoundSource.HOSTILE,
					1.0f, 1.0f);
			return true;
		}
	}

	private static final UniformInt rangedInteger = TimeUtil.rangeOfSeconds(20, 39);
	public static final float SIZE = EntityType.SALMON.getWidth() * 1.05f;
	public static final float LARGE_SIZE = EntityType.SALMON.getWidth() * 1.30f;
	private static final EntityDataAccessor<Boolean> MOVING = SynchedEntityData.defineId(SlipperyBiter.class,
			EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(SlipperyBiter.class,
			EntityDataSerializers.INT);

	private static final EntityDataAccessor<Integer> SUB_TYPE = SynchedEntityData.defineId(SlipperyBiter.class,
			EntityDataSerializers.INT);
	public static int DEFAULT_SLIPPERY_BITER = 0;
	public static int LARGE_SLIPPERY_BITER = 1;

	private static int calcNetMobCap(LevelAccessor level, BlockPos pos) {

		int mobCap = MyConfig.getSlipperyBiterSpawnCap() + level.getServer().getPlayerCount();

		if (isDeep(pos)) {
			return mobCap + 7;
		}

		String bC = Utility.getBiomeCategory(level, level.getBiome(pos));
		if (bC == Utility.SWAMP) {
			return mobCap + 5;
		}

		// support for unknown modded wet biomes.
		Biome bv = level.getBiome(pos).value();
		

		if (!Utility.isBiomeWet( bv, pos )) {
			return mobCap + 3;
		}

		return mobCap;
	}

	public static boolean canSpawn(EntityType<? extends SlipperyBiter> type, LevelAccessor level, MobSpawnType reason,
			BlockPos pos, RandomSource randomIn) {

		Utility.debugMsg(1, pos, "canSpawn slipperyBiter?");
		// SpawnPlacements.Type.IN_WATER

		if (Utility.isSpawnRateThrottled(level, 20)) {
			return false;
		}

		if (Utility.isInBubbleColumn(level, pos)) {
			return false;
		}

		if (level.getDifficulty() == Difficulty.PEACEFUL)
			return false;

		if (isTooBright(level, pos))
			return false;

		if (reason == MobSpawnType.SPAWN_EGG)
			return true;

		if (reason == MobSpawnType.SPAWNER)
			return true;

		if (isBadAltitude(level, pos))
			return false;

		if (isFailBiomeLimits(level, pos))
			return false;

		// prevent local overcrowding
		if (Utility.isOverCrowded(level, SlipperyBiter.class, pos, 5))
			return false;

		int mobCount = ((ServerLevel) level).getEntities(ModEntities.SLIPPERY_BITER, (entity) -> true).size();
		if (mobCount >= calcNetMobCap(level, pos)) {
			return false;
		}

		Utility.debugMsg(1, pos, "spawned slippery biter.");

		return true;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.73D)
				.add(Attributes.FOLLOW_RANGE, 20.0D).add(Attributes.ATTACK_DAMAGE, 2.5D)
				.add(Attributes.MAX_HEALTH, 10.5D);
	}

	public static EntityDimensions getSize() {
		float width = 0.7f;
		float height = 0.17f;
		boolean fixed_size = false;
		return new EntityDimensions(width, height, fixed_size);
	}

	private static boolean isBadAltitude(LevelAccessor level, BlockPos pos) {

		if (pos.getY() > 128)
			return true;
		if (pos.getY() < -60)
			return true;

		return false;
	}

	private static boolean isDeep(BlockPos pos) {
		return (pos.getY() < 30);
	}

	private static boolean isFailBiomeLimits(LevelAccessor level, BlockPos pos) {

		String bC = Utility.getBiomeCategory(level, level.getBiome(pos));
		if (bC == Utility.MUSHROOM || bC == Utility.THEEND) {
			return true;
		}

		if (isDeep(pos))
			return false;

		if (!level.canSeeSkyFromBelowWater(pos))
			return false;

		Biome bv = level.getBiome(pos).value();
		
		if (!Utility.isBiomeWet( bv, pos )) {
			return true;
		}

		Holder<Biome> b = level.getBiome(pos);
		if (b.is(BiomeTags.IS_NETHER)) {
			return true;
		}

		if (b.is(BiomeTags.HAS_DESERT_PYRAMID)) {
			return true;
		}

		if (b.is(BiomeTags.HAS_END_CITY)) {
			return true;
		}

		return false;
	}


	private static boolean isTooBright(LevelAccessor level, BlockPos pos) {

		if (isDeep(pos)) {
			if (level.getMaxLocalRawBrightness(pos) > 11) {
				return true;
			}
		}

		if (level.getMaxLocalRawBrightness(pos) > 12) {
			return true; // combined skylight and blocklight
		}

		if (level.getBrightness(LightLayer.BLOCK, pos) > MyConfig.getBlockLightLevel()) {
			return true;
		}

		return false;

	}

	private long slipperyTimer;

	protected MeleeAttackGoal myMeleeAttackGoal;

	private LivingEntity targetedEntity;

	private float clientSideTailAnimation;

	private float clientSideTailAnimationO;

	private float clientSideTailAnimationSpeed;

	private boolean clientSideTouchedGround;

	private int angerTime;

	private UUID angerTarget;

	public SlipperyBiter(EntityType<? extends WaterAnimal> type, Level worldIn) {

		super(type, worldIn);

		this.xpReward = 7;
		this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
		this.moveControl = new MySmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, false);
		this.lookControl = new SmoothSwimmingLookControl(this, 10);
		this.clientSideTailAnimation = this.random.nextFloat();
		this.clientSideTailAnimationO = this.clientSideTailAnimation;
		this.slipperyTimer = 0;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte("SubType", (byte) getSubType());
	}

	@SuppressWarnings("resource")
	@Override
	public void aiStep() {
		if (this.isAlive()) {

			if (!this.level().isClientSide) {
				this.updatePersistentAnger((ServerLevel) this.level(), false);
			}

			if (this.level().isClientSide) {

				this.clientSideTailAnimationO = this.clientSideTailAnimation;
				if (!this.isInWater()) {
					this.clientSideTailAnimationSpeed = 1.8f;
					final Vec3 workMotion = this.getDeltaMovement();
					if (workMotion.y > 0.0 && this.clientSideTouchedGround && !this.isSilent()) {
						this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), this.getFlopSound(),
								this.getSoundSource(), 1.0f, 1.0f, false);
					}
					this.clientSideTouchedGround = (workMotion.y < 0.0
							&& this.level().loadedAndEntityCanStandOn(this.blockPosition().below(), (Entity) this));
				} else if (this.isMoving()) {
					if (this.clientSideTailAnimationSpeed < 0.5f) {
						this.clientSideTailAnimationSpeed = 4.0f;
					} else {
						this.clientSideTailAnimationSpeed += (0.5f - this.clientSideTailAnimationSpeed) * 0.1f;
					}
				} else {
					this.clientSideTailAnimationSpeed += (0.1f - this.clientSideTailAnimationSpeed) * 0.2f;
				}
				this.clientSideTailAnimation += this.clientSideTailAnimationSpeed;

				if (this.isMoving() && this.isInWater()) {
					final Vec3 workLookVector = this.getViewVector(0.0f);
					for (int lvt_2_1_ = 0; lvt_2_1_ < 2; ++lvt_2_1_) {
						this.level().addParticle((ParticleOptions) ParticleTypes.BUBBLE,
								this.getRandomX(0.5) - workLookVector.x * 1.5,
								this.getRandomY() - workLookVector.y * 1.5,
								this.getRandomZ(0.5) - workLookVector.z * 1.5, 0.0, 0.0, 0.0);
					}
				}
				if (this.hasTargetedEntity()) {
					if (this.getTargetedEntity() != null) {
						this.getLookControl().setLookAt(this.getTargetedEntity(), 90.0f, 90.0f);
						this.getLookControl().tick();
					}
				}
			}

			if (this.isInWaterOrBubble()) {
				this.setAirSupply(300);
			} else if (this.onGround()) {
				this.setDeltaMovement(
						this.getDeltaMovement().add((double) (((this.random.nextFloat() * 2.0f) - 1.0f) * 0.4f), 0.5,
								(double) (((this.random.nextFloat() * 2.0f) - 1.0f) * 0.4f)));
				this.setYRot(this.random.nextFloat() * 360.0f);
				this.setOnGround(false);
				this.hasImpulse = true;
				if (!this.level().isClientSide()) {
					level().playSound((Player) null, this.blockPosition(), this.getFlopSound(), SoundSource.HOSTILE, 1.0f,
							1.0f);
				}

			}

		}
		super.aiStep();
	}

	public boolean canBreatheUnderwater() {
		return true;
	}

	public boolean checkSpawnObstruction(LevelReader worldIn) {
		return worldIn.isUnobstructed(this);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define((EntityDataAccessor<Boolean>) SlipperyBiter.MOVING, false);
		this.entityData.define((EntityDataAccessor<Integer>) SlipperyBiter.TARGET_ENTITY, 0);
		this.entityData.define((EntityDataAccessor<Integer>) SlipperyBiter.SUB_TYPE, 0);
	}

	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
			MobSpawnType reason, SpawnGroupData spawnDataIn, CompoundTag dataTag) {

		BlockPos pos = blockPosition();
		int workingSubtype = DEFAULT_SLIPPERY_BITER;
		if (worldIn.getRandom().nextFloat() < 0.9) {
			if (difficultyIn.getDifficulty() == Difficulty.HARD) {
				float newHealth = getMaxHealth() + 1.0f;
				this.setHealth(newHealth);
				this.getAttribute(Attributes.ATTACK_DAMAGE)
						.addTransientModifier(new AttributeModifier("difficulty", 0.5, Operation.ADDITION));
			}
		} else {
			workingSubtype = LARGE_SLIPPERY_BITER;
			if (difficultyIn.getDifficulty() != Difficulty.HARD) {
				float newHealth = getMaxHealth() + 2.0f;
				this.setHealth(newHealth);
				this.getAttribute(Attributes.ATTACK_DAMAGE)
						.addTransientModifier(new AttributeModifier("difficulty", 0.3, Operation.ADDITION));
			} else {
				float newHealth = getMaxHealth() + 4.0f;
				this.setHealth(newHealth);
				this.getAttribute(Attributes.MAX_HEALTH)
						.addTransientModifier(new AttributeModifier("difficulty", 1.5, Operation.ADDITION));
				this.getAttribute(Attributes.ATTACK_DAMAGE)
						.addTransientModifier(new AttributeModifier("difficulty", 0.6, Operation.ADDITION));

			}
		}

		this.entityData.set(SUB_TYPE, workingSubtype);

		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	protected SoundEvent getAmbientSound() {

		return ModSounds.SLIPPERY_BITER_AMBIENT;
	}

	public float getClientSideTailAnimation() {
		return clientSideTailAnimation;
	}

	public float getClientSideTailAnimationO() {
		return clientSideTailAnimationO;
	}

	public float getClientSideTailAnimationSpeed() {
		return clientSideTailAnimationSpeed;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSounds.SLIPPERY_BITER_DEATH;
	}

	protected SoundEvent getFlopSound() {
		return ModSounds.SLIPPERY_BITER_FLOP;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return ModSounds.SLIPPERY_BITER_HURT;
	}

	@Override
	public UUID getPersistentAngerTarget() {
		return angerTarget;
	}

	@Override
	public int getRemainingPersistentAngerTime() {
		return this.angerTime;
	}

	public int getSubType() {
		return entityData.get(SUB_TYPE);
	}

	@SuppressWarnings("resource")
	@Nullable
	public LivingEntity getTargetedEntity() {
		if (!this.hasTargetedEntity()) {
			return null;
		}
		if (!this.level().isClientSide) {
			return this.getTarget();
		}
		if (this.targetedEntity != null) {
			return this.targetedEntity;
		}
		final Entity targetEntity = this.level()
				.getEntity((int) this.entityData.get((EntityDataAccessor<Integer>) SlipperyBiter.TARGET_ENTITY));
		if (targetEntity instanceof LivingEntity) {
			return this.targetedEntity = (LivingEntity) targetEntity;
		}
		return null;
	}

	public boolean hasTargetedEntity() {
		return this.entityData.get((EntityDataAccessor<Integer>) SlipperyBiter.TARGET_ENTITY) != 0;
	}

	public boolean isClientSideTouchedGround() {
		return clientSideTouchedGround;
	}

	public boolean isMoving() {
		return (boolean) this.entityData.get((EntityDataAccessor<Boolean>) SlipperyBiter.MOVING);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		entityData.set(SUB_TYPE, (int) compound.getByte("SubType"));
	}

	protected void registerGoals() {

		this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1.0D, 20));
		this.goalSelector.addGoal(3, new FollowBoatGoal(this));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 7.0F));
		this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
				(Predicate<LivingEntity>) new TargetPredicate(this)));
		this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, true));

		super.registerGoals();
	}

	private void setMoving(final boolean movingStatus) {
		this.entityData.set((EntityDataAccessor<Boolean>) SlipperyBiter.MOVING, movingStatus);
	}

	@Override
	public void setPersistentAngerTarget(UUID target) {
		this.angerTarget = target;
	}

	@Override
	public void setRemainingPersistentAngerTime(int time) {
		this.angerTime = time;

	}

	private void setTargetedEntity(final int targetEntityId) {
		this.entityData.set((EntityDataAccessor<Integer>) SlipperyBiter.TARGET_ENTITY, targetEntityId);
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return true;
	}

	@Override
	public void startPersistentAngerTimer() {
		this.setRemainingPersistentAngerTime(rangedInteger.sample(this.random));
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (this.isEffectiveAi() && this.isInWater()) {
			if (this.getTarget() != null) {
				if (travelVector.length() != 0) {

				}
			}
//			float aispeed = this.getAIMoveSpeed();
//			Vector3d thisVmotion = this.getMotion();
//			thisVmotion = this.getMotion().scale(0.5D);
			this.moveRelative(this.getSpeed() * 0.9f, travelVector);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
			if (this.getTarget() == null) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
			}
		} else {
			super.travel(travelVector);
		}

	}
}

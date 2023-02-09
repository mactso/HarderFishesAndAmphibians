package com.mactso.hostilewatermobs.entities;

import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.sound.ModSounds;
import com.mactso.hostilewatermobs.utility.Utility;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.controller.DolphinLookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.FindWaterGoal;
import net.minecraft.entity.ai.goal.FollowBoatGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;

public class SlipperyBiter extends WaterMobEntity implements IAngerable, IMob {

	static class MoveHelperController extends MovementController {
		private final SlipperyBiter workSlipperyBiterEntity;

		public MoveHelperController(final SlipperyBiter slipperyBiterEntityIn) {
			super(slipperyBiterEntityIn);
			this.workSlipperyBiterEntity = slipperyBiterEntityIn;

		}

		private Vector3d rotateVector(final Vector3d p_207400_1_) {
			Vector3d lvt_2_1_ = p_207400_1_.xRot(this.workSlipperyBiterEntity.getViewXRot((float) 1.0) * 0.017453292f);
			lvt_2_1_ = lvt_2_1_.yRot(-this.workSlipperyBiterEntity.yBodyRotO * 0.017453292f);
			return lvt_2_1_;
		}

		@Override
		public void tick() {

			// mild buoyancy
			if (this.workSlipperyBiterEntity.isInWater()) {
				this.workSlipperyBiterEntity
						.setDeltaMovement(this.workSlipperyBiterEntity.getDeltaMovement().add(0.0D, 0.003D, 0.0D));
			}
			// Slippery Biter can teleport behind the player if they are 9 to 15 meters
			// away.
			trySlipperyDartingMove();

			if (this.operation == MovementController.Action.MOVE_TO
					&& !this.workSlipperyBiterEntity.getNavigation().isDone()) {
				double d0 = this.wantedX - this.workSlipperyBiterEntity.getX();
				double d1 = this.wantedY - this.workSlipperyBiterEntity.getY();
				double d2 = this.wantedZ - this.workSlipperyBiterEntity.getZ();
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				if (d3 < (double) 2.5000003E-7F) {
					this.mob.setZza(0.0F);
				} else {
					float f = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
					this.workSlipperyBiterEntity.yRot = this.rotlerp(this.workSlipperyBiterEntity.yRot, f, 10.0F);
					this.workSlipperyBiterEntity.yBodyRot = this.workSlipperyBiterEntity.yRot;
					this.workSlipperyBiterEntity.yHeadRot = this.workSlipperyBiterEntity.yRot;
					float f1 = (float) (this.speedModifier
							* this.workSlipperyBiterEntity.getAttributeValue(Attributes.MOVEMENT_SPEED));
					if (this.workSlipperyBiterEntity.isInWater()) {
						this.workSlipperyBiterEntity.setSpeed(f1);
						float f2 = -((float) (MathHelper.atan2(d1, MathHelper.sqrt(d0 * d0 + d2 * d2))
								* (double) (180F / (float) Math.PI)));
						f2 = MathHelper.clamp(MathHelper.wrapDegrees(f2), -85.0F, 85.0F);
						this.workSlipperyBiterEntity.xRot = this.rotlerp(this.workSlipperyBiterEntity.xRot, f2, 5.0F);
						float f3 = MathHelper.cos(this.workSlipperyBiterEntity.xRot * ((float) Math.PI / 180F));
						float f4 = MathHelper.sin(this.workSlipperyBiterEntity.xRot * ((float) Math.PI / 180F));
						this.workSlipperyBiterEntity.zza = f3 * f1;
						this.workSlipperyBiterEntity.yya = -f4 * f1;
					} else {
						this.workSlipperyBiterEntity.setSpeed(f1 * 0.2F);
					}

				}
			} else {
				this.workSlipperyBiterEntity.setSpeed(0.0F);
				this.workSlipperyBiterEntity.setXxa(0.0F);
				this.workSlipperyBiterEntity.setYya(0.0F);
				this.workSlipperyBiterEntity.setZza(0.0F);
			}
		}

		private void trySlipperyDartingMove() {
			if (workSlipperyBiterEntity.slipperyTimer > workSlipperyBiterEntity.level.getGameTime()) {
				return;
			}
			BlockPos biterPos = this.workSlipperyBiterEntity.blockPosition();
			LivingEntity entity = this.workSlipperyBiterEntity.getTarget();
			if (entity != null) {
				int distanceSq = (int) entity.distanceToSqr(this.workSlipperyBiterEntity);
				if ((distanceSq > 80) && (distanceSq < 125)) {
					World w = workSlipperyBiterEntity.getCommandSenderWorld();
					Vector3d v = entity.getLookAngle();
					Vector3i vI = new Vector3i(v.x() * -4, v.y(), v.z() * -4);
					BlockPos tempPos = new BlockPos(entity.getX() + vI.getX(), entity.getY() + 1 + vI.getY(),
							entity.getZ() + vI.getZ());
					if (w.getFluidState(tempPos).is(FluidTags.WATER)) {
						w.setBlockAndUpdate(biterPos, Blocks.AIR.defaultBlockState());
						w.playSound((PlayerEntity) null, biterPos, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.HOSTILE,
								0.5f, 0.5f);
						// water collapsing into resulting void
						w.playSound((PlayerEntity) null, biterPos, SoundEvents.GENERIC_EXPLODE, SoundCategory.AMBIENT,
								0.15f, 0.15f);
						final Vector3d backwardsVector = this.rotateVector(new Vector3d(0.0, -1.0, 0.0)).add(
								this.workSlipperyBiterEntity.getX(), this.workSlipperyBiterEntity.getY(),
								this.workSlipperyBiterEntity.getZ());
						for (int i = 0; i < 15; ++i) {
							final Vector3d randXZVec = this.rotateVector(
									new Vector3d(this.workSlipperyBiterEntity.random.nextFloat() * 0.6 - 0.3, -1.0,
											w.random.nextFloat() * 0.6 - 0.3));
							final Vector3d randSpreadVec = randXZVec
									.scale(0.3 + this.workSlipperyBiterEntity.random.nextFloat() * 2.0f);
							((ServerWorld) w).sendParticles((IParticleData) ParticleTypes.SOUL_FIRE_FLAME,
									backwardsVector.x, backwardsVector.y + 1.0, backwardsVector.z, 0, randSpreadVec.x,
									randSpreadVec.y, randSpreadVec.z, -0.10000000149011612);
							((ServerWorld) w).sendParticles((IParticleData) ParticleTypes.SQUID_INK, backwardsVector.x,
									backwardsVector.y + 1.0, backwardsVector.z, 0, randSpreadVec.x, randSpreadVec.y,
									randSpreadVec.z, -0.10000000149011612);

						}
						this.workSlipperyBiterEntity.teleportTo(tempPos.getX(), tempPos.getY() + .02f, tempPos.getZ());
						workSlipperyBiterEntity.slipperyTimer = w.getGameTime() + 60;
					}
				}

			}
		}
	}
	static class TargetPredicate implements Predicate<LivingEntity> {
		private static int aa = 0;
		private final SlipperyBiter parentEntity;

		public TargetPredicate(SlipperyBiter biter) {
			this.parentEntity = biter;
		}

		public boolean test(@Nullable LivingEntity entity) {

			if (!(entity instanceof PlayerEntity)) {
				return false;
			}

			if (((PlayerEntity) entity).isCreative()) {
				return false;
			}

			if (((PlayerEntity) entity).isSpectator()) {
				return false;
			}

			if (this.parentEntity.getTarget() != null) {
				if (entity == this.parentEntity.getKillCredit()) {
					return true;
				} else {
					return false;
				}
			}

			World w = entity.getCommandSenderWorld();

			if (entity instanceof TurtleEntity) {
				return false;
			}

			boolean targetInWater = false;
			if ((w.getFluidState(entity.blockPosition()).is(FluidTags.WATER))
					|| (w.getFluidState(entity.blockPosition().above()).is(FluidTags.WATER))) {
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
			ModifiableAttributeInstance followDistance = parentEntity.getAttribute(Attributes.FOLLOW_RANGE);
			int fRSq = (int) (followDistance.getValue() * followDistance.getValue());
			if (((int) entity.distanceToSqr(this.parentEntity) > fRSq)) {
				return false;
			}

			// 1 to ~500
			int distanceSq = (int) entity.distanceToSqr(this.parentEntity);
			Biome biome = w.getBiome(this.parentEntity.blockPosition());
			Category bC = biome.getBiomeCategory();

			// a little less aggressive in swamps
			if ((bC == Category.SWAMP) && (distanceSq > 255)) {
				if (aa % 3 == 0) {
					w.playSound((PlayerEntity) entity, entity.blockPosition(), ModSounds.SLIPPERY_BITER_AMBIENT,
							SoundCategory.HOSTILE, 1.0f, 1.0f);
				}
				return false;
			}

			// less aggressive in light, more aggressive in the dark
			int lightLevel = w.getMaxLocalRawBrightness(this.parentEntity.blockPosition());
			if ((lightLevel > 13) && (distanceSq > 255)) {
				if (aa % 3 == 0) {
					w.playSound((PlayerEntity) entity, entity.blockPosition(), ModSounds.SLIPPERY_BITER_AMBIENT,
							SoundCategory.HOSTILE, 1.0f, 1.0f);
				}
				return false;
			}

			if (distanceSq > 524) {
				if (aa % 3 == 0) {
					w.playSound((PlayerEntity) entity, entity.blockPosition(), ModSounds.SLIPPERY_BITER_AMBIENT,
							SoundCategory.HOSTILE, 1.0f, 1.0f);
				}
				return false;
			}

			this.parentEntity.setTarget(entity);
			this.parentEntity.setTargetedEntity(entity.getId());
			w.playSound((PlayerEntity) null, entity.blockPosition(), ModSounds.SLIPPERY_BITER_AMBIENT,
					SoundCategory.HOSTILE, 1.0f, 1.0f);
			return true;
		}
	}
	public static final float SIZE = EntityType.SALMON.getWidth() * 1.05f;
	public static final float LARGE_SIZE = EntityType.SALMON.getWidth() * 1.30f;
	private static final DataParameter<Boolean> MOVING = EntityDataManager.defineId(SlipperyBiter.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.defineId(SlipperyBiter.class,
			DataSerializers.INT);
	private static final DataParameter<Integer> SUB_TYPE = EntityDataManager.defineId(SlipperyBiter.class,
			DataSerializers.INT);
	public static int DEFAULT_SLIPPERY_BITER = 0;
	public static int LARGE_SLIPPERY_BITER = 1;
	private static final RangedInteger rangedInteger = TickRangeConverter.rangeOfSeconds(20, 39);
	private static int calcNetMobCap(IWorld level, BlockPos pos) {

		int mobCap = MyConfig.getSlipperyBiterSpawnCap() + ((ServerWorld) level).getServer().getPlayerCount();

		if (isDeep(pos)) {
			return mobCap + 7;
		}

		String bC = Utility.getBiomeCategory(level.getBiome(pos));
		if (bC == Utility.SWAMP) {
			return mobCap + 5;
		}

		// support for unknown modded wet biomes.
		Biome biome = level.getBiome(pos);
		ResourceLocation biomeNameResourceKey = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)
				.getKey(biome);
		String biomename = biomeNameResourceKey.toString();
		RegistryKey<Biome> biomeKey = RegistryKey.create(Registry.BIOME_REGISTRY, biomeNameResourceKey);
		if (BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.WATER))
			return mobCap + 3;
		if (BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.WET))
			return mobCap + 3;

		return mobCap;
	}
	public static boolean canSpawn(EntityType<? extends SlipperyBiter> type, IWorld level, SpawnReason reason,
			BlockPos pos, Random randomIn) {

		Utility.debugMsg(1, pos, "canSpawn slipperyBiter?");
		// SpawnPlacements.Type.IN_WATER

		if (Utility.isSpawnRateThrottled(level, 16)) {
			return false;
		}

		if (Utility.isInBubbleColumn(level, pos)) {
			return false;
		}

		if (level.getDifficulty() == Difficulty.PEACEFUL)
			return false;

		if (isTooBright(level, pos))
			return false;

		if (reason == SpawnReason.SPAWN_EGG)
			return true;

		if (reason == SpawnReason.SPAWNER)
			return true;

		if (isBadAltitude(level, pos))
			return false;

		if (isFailBiomeLimits(level, pos))
			return false;

		// prevent local overcrowding
		if (Utility.isOverCrowded(level, SlipperyBiter.class, pos, 5))
			return false;

		int mobCount = ((ServerWorld) level).getEntities(ModEntities.SLIPPERY_BITER, (entity) -> true).size();
		if (mobCount >= calcNetMobCap(level, pos)) {
			return false;
		}

		Utility.debugMsg(2, pos, "Slippery Biter spawned.");

		return true;
	}
	public static EntitySize getSize() {
		float width = 0.7f;
		float height = 0.17f;
		boolean fixed_size = false;
		return new EntitySize(width, height, fixed_size);
	}
	private static boolean isBadAltitude(IWorld level, BlockPos pos) {

		if (pos.getY() > 128)
			return true;
		if (pos.getY() < -60)
			return true;

		return false;
	}
	private static boolean isDeep(BlockPos pos) {
		return (pos.getY() < 30);
	}
	private static boolean isFailBiomeLimits(IWorld level, BlockPos pos) {

		String bC = Utility.getBiomeCategory(level.getBiome(pos));
		if (bC == Utility.MUSHROOM || bC == Utility.THEEND) {
			return true;
		}

		if (isDeep(pos))
			return false;

		if (!level.canSeeSkyFromBelowWater(pos))
			return false;

		if (Utility.isOcean(level, pos)) {
			return true;
		}

		return false;
	}
	public static boolean isInBubbleColumn(IWorld world, BlockPos pos) {
		return world.getBlockState(pos).is(Blocks.BUBBLE_COLUMN);
	}

	private static boolean isTooBright(IWorld level, BlockPos pos) {

		if (isDeep(pos)) {
			if (level.getMaxLocalRawBrightness(pos) > 11) {
				return true;
			}
		}

		if (level.getMaxLocalRawBrightness(pos) > 12) {
			return true; // combined skylight and blocklight
		}

		if (level.getBrightness(LightType.BLOCK, pos) > MyConfig.getBlockLightLevel()) {
			return true;
		}

		return false;

	}

	public static AttributeModifierMap.MutableAttribute registerAttributes() {
		return MonsterEntity.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.16D)
				.add(Attributes.FOLLOW_RANGE, 24.0D).add(Attributes.ATTACK_DAMAGE, 2.5D)
				.add(Attributes.MAX_HEALTH, 10.5D);
	}

	private int angerTime;

	private UUID angerTarget;

	private long slipperyTimer;

	protected MeleeAttackGoal myMeleeAttackGoal;

	private LivingEntity targetedEntity;

	private float clientSideTailAnimation;

	private float clientSideTailAnimationO;

	private float clientSideTailAnimationSpeed;

	private boolean clientSideTouchedGround;

	public SlipperyBiter(EntityType<? extends WaterMobEntity> type, World worldIn) {

		super(type, worldIn);
		this.xpReward = 7;
		this.setPathfindingMalus(PathNodeType.WATER, 0.0f);
		this.moveControl = new MoveHelperController(this);
		this.lookControl = new DolphinLookController(this, 10);
		this.clientSideTailAnimation = this.random.nextFloat();
		this.clientSideTailAnimationO = this.clientSideTailAnimation;
		this.slipperyTimer = 0;
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte("SubType", (byte) getSubType());
	}

	@Override
	public void aiStep() {
		if (this.isAlive()) {
			if (this.level.isClientSide) {

				this.clientSideTailAnimationO = this.clientSideTailAnimation;
				if (!this.isInWater()) {
					this.clientSideTailAnimationSpeed = 1.8f;
					final Vector3d workMotion = this.getDeltaMovement();
					if (workMotion.y > 0.0 && this.clientSideTouchedGround && !this.isSilent()) {
						this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), this.getFlopSound(),
								this.getSoundSource(), 1.0f, 1.0f, false);
					}
					this.clientSideTouchedGround = (workMotion.y < 0.0
							&& this.level.loadedAndEntityCanStandOn(this.blockPosition().below(), (Entity) this));
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
					final Vector3d workLookVector = this.getViewVector(0.0f);
					for (int lvt_2_1_ = 0; lvt_2_1_ < 2; ++lvt_2_1_) {
						this.level.addParticle((IParticleData) ParticleTypes.BUBBLE,
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
			} else if (this.onGround) {
				this.setDeltaMovement(
						this.getDeltaMovement().add((double) (((this.random.nextFloat() * 2.0f) - 1.0f) * 0.4f), 0.5,
								(double) (((this.random.nextFloat() * 2.0f) - 1.0f) * 0.4f)));
				this.yRot = this.random.nextFloat() * 360.0f;
				this.onGround = false;
				this.hasImpulse = true;
				if (!level.isClientSide()) {
					level.playSound((PlayerEntity) null, this.blockPosition(), this.getFlopSound(),
							SoundCategory.HOSTILE, 1.0f, 1.0f);
				}

			}

			if (this.hasTargetedEntity()) {
				this.yRot = this.yHeadRot;
			}
		}
		super.aiStep();
	}

	public boolean canBreatheUnderwater() {
		return true;
	}

	public boolean checkSpawnObstruction(IWorldReader worldIn) {
		return worldIn.isUnobstructed(this);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define((DataParameter<Boolean>) SlipperyBiter.MOVING, false);
		this.entityData.define((DataParameter<Integer>) SlipperyBiter.TARGET_ENTITY, 0);
		this.entityData.define((DataParameter<Integer>) SlipperyBiter.SUB_TYPE, 0);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			ILivingEntityData spawnDataIn, CompoundNBT dataTag) {

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

	@Nullable
	public LivingEntity getTargetedEntity() {
		if (!this.hasTargetedEntity()) {
			return null;
		}
		if (!this.level.isClientSide) {
			return this.getTarget();
		}
		if (this.targetedEntity != null) {
			return this.targetedEntity;
		}
		final Entity targetEntity = this.level
				.getEntity((int) this.entityData.get((DataParameter<Integer>) SlipperyBiter.TARGET_ENTITY));
		if (targetEntity instanceof LivingEntity) {
			return this.targetedEntity = (LivingEntity) targetEntity;
		}
		return null;
	}

	public boolean hasTargetedEntity() {
		return (int) this.entityData.get((DataParameter<Integer>) SlipperyBiter.TARGET_ENTITY) != 0;
	}

	public boolean isClientSideTouchedGround() {
		return clientSideTouchedGround;
	}

	public boolean isMoving() {
		return (boolean) this.entityData.get((DataParameter<Boolean>) SlipperyBiter.MOVING);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		entityData.set(SUB_TYPE, (int) compound.getByte("SubType"));
	}

	protected void registerGoals() {

		this.goalSelector.addGoal(0, new FindWaterGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1.0D, 1));
		this.goalSelector.addGoal(3, new FollowBoatGoal(this));
		this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 7.0F));
		this.goalSelector.addGoal(9, new LookRandomlyGoal(this));

		this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
				(Predicate<LivingEntity>) new TargetPredicate(this)));
		this.targetSelector.addGoal(2, new ResetAngerGoal<>(this, true));

		super.registerGoals();
	}

	private void setMoving(final boolean movingStatus) {
		this.entityData.set((DataParameter<Boolean>) SlipperyBiter.MOVING, movingStatus);
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
		this.entityData.set((DataParameter<Integer>) SlipperyBiter.TARGET_ENTITY, targetEntityId);
	}

	@Override
	public void startPersistentAngerTimer() {
		this.setRemainingPersistentAngerTime(rangedInteger.randomValue(this.random));
	}

	@Override
	public void travel(Vector3d travelVector) {
		if (this.isEffectiveAi() && this.isInWater()) {
			if (this.getTarget() != null) {
				if (travelVector.length() != 0) {
					int x = 3;
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

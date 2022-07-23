package com.mactso.hostilewatermobs.entities;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.sound.ModSounds;
import com.mactso.hostilewatermobs.utility.TwoGuysLib;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.server.level.ServerLevel;

public class WaterSnakeEntity extends WaterAnimal implements Enemy, RangedAttackMob {
	static class GoLandGoal extends Goal {
		private final WaterSnakeEntity watersnake;
		private final double speed;
		private int chance;
		private int timer;
		private boolean finished;

		GoLandGoal(WaterSnakeEntity watersnakeIn, double speedIn, int chanceIn) {
			this.watersnake = watersnakeIn;
			this.speed = speedIn;
			this.chance = chanceIn;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean canContinueToUse() {
			return !watersnake.getNavigation().isDone() && !this.finished && !watersnake.isGoingNest();
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {
			if (watersnake.getRandom().nextInt(this.chance) != 0) {
				return false;
			}
			if (!watersnake.level.isDay()) {
				if (watersnake.isGoingNest()) {
					return false;
				}
				return true;
			}
			return false;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void start() {
			MyConfig.debugMsg(1, watersnake.blockPosition(), "Start Walking to Land");
			Random random = watersnake.random;
			int k = random.nextInt(128) - 64;
			int l = random.nextInt(9) - 4;
			int i1 = random.nextInt(128) - 64;
			if ((double) l + watersnake.getY() > (double) (watersnake.level.getSeaLevel() - 1)) {
				l = 0;
			}

			BlockPos blockpos = new BlockPos((double) k + watersnake.getX(), (double) l + watersnake.getY(),
					(double) i1 + watersnake.getZ());
			watersnake.setTravelPos(blockpos);
			watersnake.setTravelling(true);
			this.finished = false;
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void stop() {
			watersnake.setTravelling(false);
			super.stop();
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			BlockPos blockpos = watersnake.getNestPos();
			boolean isNearNest = blockpos.closerThan(watersnake.position(), 16.0D);
			if (isNearNest) {
				++this.timer;
			}

			if (watersnake.getNavigation().isDone()) {
				Vec3 vector3d = Vec3.atBottomCenterOf(watersnake.getTravelPos());
				Vec3 vector3d1 = DefaultRandomPos.getPosTowards(watersnake, 16, 3, vector3d,
						(double) ((float) Math.PI / 10F));
				if (vector3d1 == null) {
					vector3d1 = DefaultRandomPos.getPosTowards(watersnake, 8, 7, vector3d,
							(double) ((float) Math.PI / 10F));
				}

				if (vector3d1 != null && !isNearNest
						&& !watersnake.level.getBlockState(new BlockPos(vector3d1)).is(Blocks.WATER)) {
					vector3d1 = DefaultRandomPos.getPosTowards(watersnake, 16, 5, vector3d,
							(double) ((float) Math.PI / 10F));
				}

				if (vector3d1 == null) {
					this.finished = true;
					return;
				}

				watersnake.getNavigation().moveTo(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
			}

		}
	}

	static class GoNestGoal extends Goal {
		private WaterSnakeEntity watersnake;
		private double speed;
		private boolean noPath;
		private int timer;

		GoNestGoal(WaterSnakeEntity watersnake, double speedIn) {
			this.watersnake = watersnake;
			this.speed = speedIn;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean canContinueToUse() {
			if (this.noPath) {
				return false;
			}
			if (timer > 400) {
				return false;
			}
			return !watersnake.getNestPos().closerThan(watersnake.position(), 5.0D);
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {
			if (watersnake.isNestingTime()) {
				return true;
			}
			if (!watersnake.getNestPos().closerThan(watersnake.position(), 64.0D)) {
				return true;
			}
			return false;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void start() {
			watersnake.setGoingNest(true);
			this.noPath = false;
			this.timer = 0;
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void stop() {
			watersnake.setGoingNest(false);
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			BlockPos blockpos = watersnake.getNestPos();
			boolean isNearNest = blockpos.closerThan(watersnake.position(), 16.0D);

			if (watersnake.getCommandSenderWorld().getGameTime() % 20 == 0) {
				MyConfig.debugMsg(1, watersnake.blockPosition(), "Tick GoToNest at " + watersnake.getNestPos());
			}

			if (isNearNest) {
				++this.timer;
			}

			if (watersnake.getNavigation().isDone()) {
				Vec3 vector3d = Vec3.atBottomCenterOf(blockpos);
				Vec3 vector3d1 = DefaultRandomPos.getPosTowards(watersnake, 16, 3, vector3d,
						(double) ((float) Math.PI / 10F));
				if (vector3d1 == null) {
					vector3d1 = DefaultRandomPos.getPosTowards(watersnake, 8, 7, vector3d,
							(double) ((float) Math.PI / 10F));
				}

				if (vector3d1 != null && !isNearNest
						&& !watersnake.level.getBlockState(new BlockPos(vector3d1)).is(Blocks.WATER)) {
					vector3d1 = DefaultRandomPos.getPosTowards(watersnake, 16, 5, vector3d,
							(double) ((float) Math.PI / 10F));
				}

				if (vector3d1 == null) {
					this.noPath = true;
					return;
				}

				watersnake.getNavigation().moveTo(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
			}

		}
	}

	static class GoWanderGoal extends RandomStrollGoal {
		private final WaterSnakeEntity watersnake;

		private GoWanderGoal(WaterSnakeEntity watersnakeIn, double speedIn, int chance) {
			super(watersnakeIn, speedIn, chance);
			watersnake = watersnakeIn;
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {
			return !watersnake.isInWater() && !watersnake.isGoingNest() ? super.canUse() : false;
		}
	}

	static class GoWaterGoal extends MoveToBlockGoal {
		private final WaterSnakeEntity watersnake;
		private int chance;

		private GoWaterGoal(WaterSnakeEntity watersnakeIn, double speedIn, int chanceIn) {
			super(watersnakeIn, speedIn, 24);
			watersnake = watersnakeIn;
			chance = chanceIn;
			this.verticalSearchStart = -1;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean canContinueToUse() {
			return !watersnake.isInWater() && this.tryTicks <= 1200
					&& this.isValidTarget(watersnake.level, this.blockPos);
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {
			if (watersnake.isGoingNest()) {
				return false;
			}
			if (!watersnake.level.isDay()) {
				return false;
			}
			if (watersnake.random.nextInt(chance) == 0) {
				return true;
			}
			return false;
		}

		/**
		 * Return true to set given position as destination
		 */
		protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
			return worldIn.getBlockState(pos).is(Blocks.WATER);
		}

		public boolean shouldRecalculatePath() {
			return this.tryTicks % 80 == 0;
		}
	}

	static class MoveHelperController extends MoveControl {
		private final WaterSnakeEntity watersnake;
		private int jumpTimer = 0;

		public MoveHelperController(final WaterSnakeEntity watersnakeEntityIn) {
			super(watersnakeEntityIn);
			this.watersnake = watersnakeEntityIn;

		}

		public void tick() {

			this.updateSpeed();

			if (watersnake.isAngry()) {
				watersnake.setTailHeight(0.77f);
			} else {
				watersnake.setTailHeight(-0.27f);
			}

			if (watersnake.getCommandSenderWorld().getGameTime() % 20 == 0) {
				MyConfig.debugMsg(2, watersnake.blockPosition(),
						"Tick MoveHelperController to " + watersnake.getTravelPos());
			}
			if (this.operation == MoveControl.Operation.MOVE_TO && !watersnake.getNavigation().isDone()) {
				double dx = this.wantedX - watersnake.getX();
				double dy = this.wantedY - watersnake.getY();
				double dz = this.wantedZ - watersnake.getZ();
				double distance = (double) Math.sqrt(dx * dx + dy * dy + dz * dz); // TODO Mth?
				dy = dy / distance;
				float f = (float) (Mth.atan2(dz, dx) * (double) (180F / (float) Math.PI)) - 90.0F;

				watersnake.setYRot(this.rotlerp(watersnake.getYRot(), f, 90.0F));
				watersnake.setYBodyRot(watersnake.getYRot());

				float f1 = (float) (speedModifier * this.watersnake.getAttributeValue(Attributes.MOVEMENT_SPEED));
				watersnake.setSpeed(Mth.lerp(0.225F, watersnake.getSpeed(), f1));
				watersnake.setDeltaMovement(
						watersnake.getDeltaMovement().add(0.0D, (double) watersnake.getSpeed() * dy * 0.1D, 0.0D));
			} else {
				watersnake.setSpeed(0.0F);
			}
		}

		private void updateSpeed() {
			if (watersnake.isInWater()) {
				watersnake.setDeltaMovement(this.watersnake.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
				if (!watersnake.getNestPos().closerThan(watersnake.position(), 16.0D)) {
					watersnake.setSpeed(Math.max(watersnake.getSpeed() / 2.0F, 0.11F));
				}
			} else if (watersnake.onGround) {
				watersnake.setSpeed(Math.max(watersnake.getSpeed() / 1.9F, 0.17F));
			}

		}

	}

	static class Navigator extends WaterBoundPathNavigation {
		Navigator(WaterSnakeEntity watersnake, Level worldIn) {
			super(watersnake, worldIn);
		}

		/**
		 * If on ground or swimming and can swim
		 */
		protected boolean canUpdatePath() {
			return true;
		}

		protected PathFinder createPathFinder(int p_179679_1_) {
			this.nodeEvaluator = new AmphibiousNodeEvaluator(true);
			return new PathFinder(this.nodeEvaluator, p_179679_1_);
		}

		public boolean isStableDestination(BlockPos pos) {
			if (this.mob instanceof WaterSnakeEntity) {
				WaterSnakeEntity watersnake = (WaterSnakeEntity) this.mob;
				if (watersnake.isTravelling()) {
					return this.level.getBlockState(pos).is(Blocks.WATER);
				}
			}

			return !this.level.getBlockState(pos.below()).isAir();
		}
	}

	static class PanicGoal extends net.minecraft.world.entity.ai.goal.PanicGoal {
		PanicGoal(WaterSnakeEntity watersnake, double speedIn) {
			super(watersnake, speedIn);
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {

			if (mob.getLastHurtByMob() == null) {
				return false;
			}
			if (mob.level.getDifficulty() == Difficulty.PEACEFUL) {
				return false;
			}
			// hmm this was "level"... now it's blockgetter. double check this.
			BlockPos blockpos = this.lookForWater((BlockGetter) this.mob.level, this.mob, 7);
			if (blockpos != null) {
				this.posX = (double) blockpos.getX();
				this.posY = (double) blockpos.getY();
				this.posZ = (double) blockpos.getZ();
				return true;
			} else {
				return this.findRandomPosition();
			}
		}
	}

	static class SwimGoal extends net.minecraft.world.entity.ai.goal.FloatGoal {
		WaterSnakeEntity watersnake;
		int chance;

		SwimGoal(WaterSnakeEntity watersnakeIn) {
			super(watersnakeIn);
			this.chance = 40;
			this.watersnake = watersnakeIn;
		}

		SwimGoal(WaterSnakeEntity watersnakeIn, int chanceIn) {
			super(watersnakeIn);
			this.chance = chanceIn;
			this.watersnake = watersnakeIn;
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {

			if (watersnake.random.nextInt(chance) != 0) {
				return false;
			}
			return true;

		}
	}

	static class TargetPredicate implements Predicate<LivingEntity> {
		private final WaterSnakeEntity waterSnakeEntity;

		public TargetPredicate(WaterSnakeEntity waterSnakeIn) {
			waterSnakeEntity = waterSnakeIn;
		}

		// called to decide if a target in range is valid to attack
		public boolean test(@Nullable LivingEntity entity) {

			// 's don't attack each other
			if (entity instanceof WaterSnakeEntity) {
				return false;
			}

			// villagers have secret tricks to make 's not want to eat them
			if (entity instanceof Villager) {
				return false;
			}

			// watersnakes are non-hostile to turtles.
			if (entity instanceof Turtle) {
				return false;
			}

			// 's don't attack things they can't see unless attacked first.
			if (!waterSnakeEntity.hasLineOfSight(entity)) {
				if (entity != waterSnakeEntity.getKillCredit()) {
					return false;
				}
			}

			// 's don't attack players in creative or spectator mode

			if (entity instanceof Player) {
				if (((Player) entity).isCreative()) {
					return false;
				} else if (((Player) entity).isSpectator()) {
					return false;
				}
			}

			boolean validTarget = false;
			// 's always take revenge on their attackers, regardless of any other condition
			if (waterSnakeEntity.getTarget() != null) {
				if (entity == this.waterSnakeEntity.getKillCredit()) {
					waterSnakeEntity.setTarget(entity);
					return true;
				}
			}

			// distance to entity.
			int dstToEntitySq = (int) entity.distanceToSqr(waterSnakeEntity);
			Vec3i nestPos = (Vec3i) waterSnakeEntity.getNestPos();

			// water snakes always attack if entity threatens the nest and is near entity.
			Vec3i entityPosVec = (Vec3i) entity.blockPosition();
			int nestThreatDistance = (int) entityPosVec.distSqr(waterSnakeEntity.getNestPos());

			// water snakes get angry at creatures near their nest area if the snake is
			// nearby.
			if ((nestThreatDistance < waterSnakeEntity.nestProtectionDistSq) && (dstToEntitySq < 121)) {
				waterSnakeEntity.setTarget(entity);
				return true;
			}

			// Don't attack things when too far from nest.
			if ((nestPos.distSqr(waterSnakeEntity.blockPosition()) > 1600)) {
				waterSnakeEntity.setTarget(null);
				return false;
			}

			Level w = entity.getCommandSenderWorld();

			// rarely attack random fish and other creatures in range.
			if (!(entity instanceof Player)) {
				if (w.random.nextInt(600) != 100) {
					waterSnakeEntity.setTarget(null);
					return false;
				}
			}

			// a little less aggressive in swamps
			Biome biome = w.getBiome(waterSnakeEntity.blockPosition());
			BiomeCategory bC = biome.getBiomeCategory();
			if ((bC == BiomeCategory.SWAMP)) {
				dstToEntitySq += 64;
			}

			// less aggressive in light
			int lightLevel = w.getMaxLocalRawBrightness(this.waterSnakeEntity.blockPosition());
			if (lightLevel > 13) {
				dstToEntitySq += 81;
			}

			if (w.isRaining()) {
				dstToEntitySq *= 0.6f;
			}

			if ((w.getFluidState(entity.blockPosition()).is(FluidTags.WATER))
					|| (w.getFluidState(entity.blockPosition().above()).is(FluidTags.WATER))) {
				dstToEntitySq *= 0.75f;
			}

			double followDistance = waterSnakeEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue();
			int followDistanceSq = (int) (followDistance * followDistance);

			// if modified distance to entity > follow distance attribute, don't attack.
			if (dstToEntitySq > (followDistanceSq)) {
				// But if a player and in range and random playsound (2.5%) then play a warning
				// ambient sound.
				if (entity instanceof Player) {
					int playSound = waterSnakeEntity.random.nextInt(50);

					if ((dstToEntitySq < 900) && (playSound == 21)) {
						w.playSound(null, entity.blockPosition(), ModSounds.WATER_SNAKE_AMBIENT, SoundSource.HOSTILE,
								0.35f, 1.0f);
					}
				}
				waterSnakeEntity.setTarget(null);
				return false;
			}

			waterSnakeEntity.setTarget(entity);
			w.playSound(null, waterSnakeEntity.blockPosition(), ModSounds.WATER_SNAKE_ANGRY, SoundSource.HOSTILE, 1.0f,
					1.0f);
			return true;
		}
	}

	public class WatersnakeAttackGoal extends MeleeAttackGoal {
		private final WaterSnakeEntity snake;

		public WatersnakeAttackGoal(final WaterSnakeEntity snakeIn, final double speedBoost,
				final boolean followingTargetEvenIfNotSeen) {
			super((PathfinderMob) snakeIn, speedBoost, followingTargetEvenIfNotSeen);
			this.snake = snakeIn;
			snakeIn.startAttackTime = snake.level.getGameTime() + 16;
		}

		public void start() {
			super.start();
			MyConfig.debugMsg(1, "Start Melee Attack");
			this.snake.setAggressive(true);
		}

		public void stop() {
			super.stop();
			MyConfig.debugMsg(1, "Stop Melee Attack");
			this.snake.setAggressive(false);
		}

	}

	//
	// GOAL section
	//
	public class WatersnakeSpitAttackGoal extends RangedAttackGoal {
		private final WaterSnakeEntity snake;
		private final float attackRadius;

		public WatersnakeSpitAttackGoal(final WaterSnakeEntity snakeIn, final double speedModifier,
				final int attackIntervalMin, final int attackIntervalMax, final float attackRadius) {

			super(snakeIn, speedModifier, attackIntervalMin, attackIntervalMax, attackRadius);
			this.snake = snakeIn;
			this.attackRadius = attackRadius;
			snakeIn.startSpittingTime = snake.level.getGameTime() + 16;

		}

		public void start() {
			super.start();
			snake.startSpittingTime = snake.level.getGameTime() + 16;
			MyConfig.debugMsg(1, "Start Spitting");
			this.snake.setAggressive(true);
		}

		public void stop() {
			super.stop();
			MyConfig.debugMsg(1, "Stop Spitting");
			this.snake.setAggressive(false);
		}

//		public void tick() {
//			super.tick();
//			if (this.snake.getTarget() == null) {
//				return;
//			}
//			double distance = this.snake.distanceToSqr(this.snake.getTarget().getX(), this.snake.getTarget().getY(), this.snake.getTarget().getZ());
//			MyConfig.debugMsg(0, "Distance to Target: "+ distance);
//
//			if (distance < 3.0d) {
//				MyConfig.debugMsg(0, "Too Close Stop Spitting");
//				stop();
//			}
//			if (distance > this.attackRadius) {
//				MyConfig.debugMsg(0, "Too Far Stop Spitting");
//				stop();
//			}
//		}

	}

	private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(WaterSnakeEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(WaterSnakeEntity.class,
			EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> GOING_NEST = SynchedEntityData.defineId(WaterSnakeEntity.class,
			EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<BlockPos> NEST_POS = SynchedEntityData.defineId(WaterSnakeEntity.class,
			EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<BlockPos> TRAVEL_POS = SynchedEntityData.defineId(WaterSnakeEntity.class,
			EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<Boolean> TRAVELLING = SynchedEntityData.defineId(WaterSnakeEntity.class,
			EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> ATK_ANIM_TIME = SynchedEntityData.defineId(WaterSnakeEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> SPIT_TIME = SynchedEntityData.defineId(WaterSnakeEntity.class,
			EntityDataSerializers.INT);
	public static final int ANGER_MILD = 300;
	public static final int ANGER_INTENSE = 1200;
	private static long lastSpawnTime = 0;
	public static final float SIZE = EntityType.PIG.getWidth() * 0.45f;
	private static final UniformInt rangedInteger = TimeUtil.rangeOfSeconds(20, 39);

	public static boolean canSpawn(EntityType<? extends WaterSnakeEntity> watersnakeIn, LevelAccessor worldIn,
			MobSpawnType reason, BlockPos pos, Random randomIn) {

		if (worldIn.isClientSide()) {
			return false;
		}

		ServerLevel w = (ServerLevel) worldIn;

		MyConfig.debugMsg(1, pos, "checking spawn gurty");

//		if (lastSpawnTime+24000 > w.getGameTime()) {
//			int lightLevel = w.getMaxLocalRawBrightness(pos);
//			int roll = w.getRandom().nextInt(8);
//			if (lightLevel > roll) { 
//				return false;
//			}
//		} else { // once a day- can spawn at light level up to 9;
//			lastSpawnTime = w.getGameTime();
//			int lightLevel = w.getMaxLocalRawBrightness(pos);
//			if (lightLevel > 9) { 
//				return false;
//			}
//		}
//		if (w.getDifficulty() == Difficulty.PEACEFUL)
//			return false;  // if peaceful will not attack player characters

		if (reason == MobSpawnType.SPAWN_EGG)
			return true;

//		if (w.getLightValue(pos) > 13) {
//			return false;
//		}

		if (reason == MobSpawnType.SPAWNER) {
			return true;
		}

		if (w.getBlockState(pos).getMaterial().isLiquid()) {
			return false;
		}

		if (!w.getBlockState(pos.below()).getMaterial().isSolid()) {
			return false;
		}

		if (isBubbleColumn(w, pos)) {
			return false;
		}

		// watersnakes require nearby water.
		if (!TwoGuysLib.findWaterBlocks(watersnakeIn, w, pos, 21, 4, 9)) {
			return false;
		}

		int watersnakeSpawnChance = MyConfig.getWatersnakeSpawnChance();
		int watersnakeSpawnCap = MyConfig.getWatersnakeSpawnCap();
		int watersnakeSpawnRoll = randomIn.nextInt(30);
		int watersnakeCount = ((ServerLevel) w).getEntities(ModEntities.WATER_SNAKE, (entity) -> true).size();

		if (watersnakeCount < 7) {
			watersnakeSpawnRoll = 0;
		}

		Biome biome = w.getBiome(pos);
		BiomeCategory bC = biome.getBiomeCategory();
		if ((bC == BiomeCategory.OCEAN) || (bC == BiomeCategory.RIVER) || (bC == BiomeCategory.SWAMP)
				|| (bC == BiomeCategory.BEACH)) {
			watersnakeSpawnChance += 7;
		}

		if ((bC == BiomeCategory.SWAMP) || (bC == BiomeCategory.BEACH)) {
			watersnakeSpawnCap += 7;
		}

//			System.out.println(
//					"Classic Snake Spawn Cap: " + watersnakeSpawnCap + " Count : " + watersnakeCount + " Chance:" + watersnakeSpawnChance);

		if (watersnakeCount > watersnakeSpawnCap)
			return false;

		if (watersnakeSpawnRoll > watersnakeSpawnChance)
			return false;

		List<GurtyEntity> listG = worldIn.getEntitiesOfClass(GurtyEntity.class,
				new AABB(pos.north(16).west(16).above(8), pos.south(16).east(16).below(8)));

		if (listG.size() > 5) {
			return false;
		}

		if (MyConfig.getDebugLevel() > 0) {
			System.out.println("spawn watersnake true at " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
		}
		// TODO
		System.out.println("spawn watersnake true at " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
		return true;

	}

	@Nullable
	private static Vec3 findSolidBlock(EntityType<? extends WaterSnakeEntity> snakeIn, LevelAccessor world,
			BlockPos blockPos, int maxXZ, int maxY) {
		Random rand = world.getRandom();
		for (int i = 0; i < 12; ++i) {
			int xD = rand.nextInt(maxXZ + maxXZ) - maxXZ;
			int zD = rand.nextInt(maxXZ + maxXZ) - maxXZ;
			int yD = rand.nextInt(maxY + maxY) - maxY;
			if (blockPos.getY() + yD > 0 && blockPos.getY() + yD < 254) {
				if (world.getBlockState(blockPos).getMaterial().isSolid()) {
					return Vec3.atBottomCenterOf(blockPos.east(xD).above(yD).west(zD));
				}
			}
		}
		return null;
	}

	public static boolean isBubbleColumn(LevelAccessor world, BlockPos pos) {
		return world.getBlockState(pos).is(Blocks.BUBBLE_COLUMN);
	}

	public static AttributeSupplier.Builder createAttributes() {

		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double) 0.26F)
				.add(Attributes.FOLLOW_RANGE, 20.0D).add(Attributes.ATTACK_DAMAGE, 2.5D)
				.add(Attributes.MAX_HEALTH, 5.0D);
	}

	private boolean hasNest;

	private int nestProtectionDistSq;

	private long angryTime;

	public long startAttackTime;

	protected RandomStrollGoal wander;

	protected float tailHeight = -0.2707964f;

	protected float bodyHeight = 0.2707964f;

	private boolean didSpit;

	public long startSpittingTime;

	public long hissingTime;

	public double snakeHeight = EntityType.PIG.getHeight() * 0.5f;

	public double snakeWidth = EntityType.PIG.getWidth() * 0.45f;

	private LivingEntity targetedEntity;

	public WaterSnakeEntity(EntityType<? extends WaterSnakeEntity> type, Level worldIn) {

		super(type, worldIn);
		this.xpReward = 7;
		this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
		this.moveControl = new WaterSnakeEntity.MoveHelperController(this);
		this.maxUpStep = 1.0f;
		this.nestProtectionDistSq = MyConfig.getGurtyNestDistance();
		nestProtectionDistSq = (nestProtectionDistSq * nestProtectionDistSq) + 3;
		// this.setBoundingBox(new AxisA);
	}

	@Override
	protected void actuallyHurt(DamageSource source, float damageAmount) {

		if (source == null) {
			source = DamageSource.GENERIC;
		}
		if (damageAmount < 0) {
			damageAmount = 0;
		}

		if (source.isProjectile()) {
			damageAmount *= 0.5f; // reduce projectile damage by 33% scaly skin
		}

		if (damageAmount > 3.5) {
			damageAmount -= 3.0; // thick leathery skin.
		}

		if (getESize() >= 1) { // larger snakes take less damage.
			damageAmount *= 0.75f;
		}

		if (source.getMsgId() == DamageSource.thorns(source.getEntity()).getMsgId()) {
			damageAmount *= 0.25f; // highly resistant to Thorns
		}
		if (source.getMsgId() == DamageSource.SWEET_BERRY_BUSH.getMsgId()) {
			damageAmount = 0.0f; // thick scaly skin.
		}
		if (source.getMsgId() == DamageSource.CACTUS.getMsgId()) {
			damageAmount = 0.0f; // thick scaly skin.
			return;
		}
		// resistant to magic and magic thorns
		if (source.getMsgId() == DamageSource.MAGIC.getMsgId()) {
			damageAmount *= 0.8f; // partial magic immunity
		}
		if (source.isBypassArmor()) {
			damageAmount *= 0.8f; // partial magic immunity
		}
		if (source.isExplosion()) {
			damageAmount *= 0.8f; // weak explosion resistance
		}
		super.actuallyHurt(source, damageAmount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("NestPosX", this.getNestPos().getX());
		compound.putInt("NestPosY", this.getNestPos().getY());
		compound.putInt("NestPosZ", this.getNestPos().getZ());
		compound.putInt("TravelPosX", this.getTravelPos().getX());
		compound.putInt("TravelPosY", this.getTravelPos().getY());
		compound.putInt("TravelPosZ", this.getTravelPos().getZ());
	}

	public int AttackAnimTime() {
		return (int) this.entityData.get((EntityDataAccessor<Integer>) WaterSnakeEntity.ATK_ANIM_TIME);
	}

	@Override
	public boolean canBeAffected(MobEffectInstance potioneffectIn) {
		net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(
				this, potioneffectIn);
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() != net.minecraftforge.eventbus.api.Event.Result.DEFAULT)
			return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;

		MobEffect effect = potioneffectIn.getEffect();
		if (effect == MobEffects.POISON) {
			return false;
		}

		return true;
	}

	public boolean canBreatheUnderwater() {
		return true;
	}

	/**
	 * Makes the entity despawn if requirements are reached
	 */
	@Override
	public void checkDespawn() {
		if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
			this.remove(RemovalReason.DISCARDED);
		} else if (!this.isPersistenceRequired() && !this.requiresCustomPersistence()) {
			Entity entity = this.level.getNearestPlayer(this, -1.0D);
			net.minecraftforge.eventbus.api.Event.Result result = net.minecraftforge.event.ForgeEventFactory
					.canEntityDespawn(this);
			if (result == net.minecraftforge.eventbus.api.Event.Result.DENY) {
				noActionTime = 0;
				entity = null;
			} else if (result == net.minecraftforge.eventbus.api.Event.Result.ALLOW) {
				this.remove(RemovalReason.DISCARDED);
				entity = null;
			}
			if (entity != null) {
				double d0 = entity.distanceToSqr(this);
				int i = this.getType().getCategory().getDespawnDistance();
				int j = i * i;
				if (d0 > (double) j && this.removeWhenFarAway(d0)) {
					removeNest();
					this.remove(RemovalReason.DISCARDED);

				}

				int k = this.getType().getCategory().getNoDespawnDistance();
				int l = k * k;
				if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && d0 > (double) l
						&& this.removeWhenFarAway(d0)) {
					this.remove(RemovalReason.DISCARDED);
				} else if (d0 < (double) l) {
					this.noActionTime = 0;
				}
			}

		} else {
			this.noActionTime = 0;
		}
	}

	// Movement and Navigator Section
	// Returns new PathNavigateGround instance
	protected PathNavigation createNavigation(Level worldIn) {
		return new WaterSnakeEntity.Navigator(this, worldIn);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define((EntityDataAccessor<Integer>) WaterSnakeEntity.TARGET_ENTITY, 0);
		this.entityData.define((EntityDataAccessor<Boolean>) WaterSnakeEntity.ANGRY, false);
		this.entityData.define(NEST_POS, BlockPos.ZERO);

		this.entityData.define(TRAVEL_POS, BlockPos.ZERO);
		this.entityData.define(GOING_NEST, false);
		this.entityData.define(TRAVELLING, false);
	}

	@Override
	public void die(DamageSource cause) {
		removeNest();
		super.die(cause);
	}

	@Override
	public boolean doHurtTarget(Entity entityIn) {
		entityIn.playSound(SoundEvents.PLAYER_HURT, 0.7f, 0.7f);
		if (entityIn instanceof LivingEntity) {
			((LivingEntity) entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, 60, 1));
		}
		return super.doHurtTarget(entityIn);
	}

	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
			MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
		BlockPos pos = blockPosition();
		this.hasNest = true;
		setTravelPos(BlockPos.ZERO);
		// Reuse any nearby nest.
		BlockPos nestPos = null;
		nestPos = helperFindExistingNest(pos);
		if (nestPos == null) {
			setNestPos(pos);
			level.setBlockAndUpdate(pos, Blocks.CORNFLOWER.defaultBlockState());
		} else {
			setNestPos(nestPos);
		}

		setHealth(getMaxHealth() + MyConfig.getGurtyBaseHitPoints());

		if (difficultyIn.getDifficulty() == Difficulty.HARD) {
			float hardHealth = getMaxHealth() + 3.0f;
			setHealth(hardHealth);
			getAttribute(Attributes.ATTACK_DAMAGE)
					.addTransientModifier(new AttributeModifier("difficulty", 0.5, Operation.ADDITION));

		}

		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	protected SoundEvent getAmbientSound() {
		return ModSounds.WATER_SNAKE_AMBIENT;
	}

	protected SoundEvent getAngrySound() {
		this.hissingTime = this.level.getGameTime();
		return ModSounds.WATER_SNAKE_ANGRY;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSounds.WATER_SNAKE_DEATH;
	}

	public int getESize() {
		return this.getId() % 16 - 8;
	}

	@Override
	protected int getFireImmuneTicks() {
		return 20;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return ModSounds.WATER_SNAKE_HURT;
	}

	@Override
	public int getMaxSpawnClusterSize() {
		int max = 1 + (MyConfig.getGurtySpawnCap() / 6);
		return max;
	}

	private BlockPos getNestPos() {
		return this.entityData.get(NEST_POS);
	}

	public float getScale() {
		return 1.0f;
	}

	public EntityDimensions getSize() { // why should this be static?

//		float width = 0.9f;
//		float height = 0.6f;
		boolean fixed_size = false;
		return new EntityDimensions((float) this.snakeWidth, (float) this.snakeHeight, fixed_size);
	}

	public double getSnakeHeight() {
		return snakeHeight;
	}

	public double getSnakeWidth() {
		return snakeWidth;
	}

	public float getTailHeight() {
		return this.tailHeight;
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
				.getEntity((int) this.entityData.get((EntityDataAccessor<Integer>) WaterSnakeEntity.TARGET_ENTITY));
		if (targetEntity instanceof LivingEntity) {
			return this.targetedEntity = (LivingEntity) targetEntity;
		}
		return null;
	}

	private BlockPos getTravelPos() {
		return this.entityData.get(TRAVEL_POS);
	}

	@Override
	protected void handleAirSupply(int p_209207_1_) {
		// 's are amphibians
	}

	public boolean hasTargetedEntity() {
		return (int) this.entityData.get((EntityDataAccessor<Integer>) WaterSnakeEntity.TARGET_ENTITY) != 0;
	}

	private BlockPos helperFindExistingNest(BlockPos pos) {
		final int[] nestY = { 0, -1, -2, 1, 2 };
		for (int iY = 0; iY < 5; iY++) {
			for (int iX = -7; iX < 7; iX++) {
				for (int iZ = -7; iZ < 7; iZ++) {
					if (level.getBlockState(pos.west(iX).above(nestY[iY]).north(iZ)).getBlock() == Blocks.CORNFLOWER) {
						return pos.west(iX).above(nestY[iY]).north(iZ);
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {

		if ((this.level.isClientSide) || (this.isDeadOrDying()) || this.isInvulnerableTo(source)) {
			return false;
		}

		if (source == DamageSource.OUT_OF_WORLD) {
			return super.hurt(source, amount);
		}

		if ((amount > 0.0f) && (source.getEntity() != null)) {
			Entity entity = source.getEntity();

			if (entity.level.getDifficulty() != Difficulty.PEACEFUL) {
				setTarget((LivingEntity) entity);
				setTargetedEntity(entity.getId());
			}
			angryTime = level.getGameTime() + ANGER_INTENSE;

		}
		return super.hurt(source, amount);
	}

	public boolean isAngry() {
		return this.entityData.get((EntityDataAccessor<Boolean>) WaterSnakeEntity.ANGRY);
	}

	private boolean isGoingNest() {
		return this.entityData.get(GOING_NEST);
	}

	protected boolean isNestingTime() {
		long time = this.level.getDayTime() % 24000;
		if ((time > 11000 && time < 11250) || (time > 250 && time < 500)) {
			return true;
		}
		return false;
	}

	private boolean isTravelling() {
		return this.entityData.get(TRAVELLING);
	}

	// handle /kill command
	@Override
	public void kill() {
		this.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
	}

	@Override
	public void performRangedAttack(LivingEntity p0, float p1) {
		this.spit(p0);
	}

	@Override
	protected void playStepSound(final BlockPos p_180429_1_, final BlockState p_180429_2_) {
		this.playSound(ModSounds.WATER_SNAKE_STEP, 0.10f, 0.5f);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		int i = compound.getInt("NestPosX");
		int j = compound.getInt("NestPosY");
		int k = compound.getInt("NestPosZ");
		this.setNestPos(new BlockPos(i, j, k));
		super.readAdditionalSaveData(compound);
		int l = compound.getInt("TravelPosX");
		int i1 = compound.getInt("TravelPosY");
		int j1 = compound.getInt("TravelPosZ");
		this.setTravelPos(new BlockPos(l, i1, j1));
	}

	protected void registerGoals() {
		int rndWalkOdds = 40;
		double walkSpeedModifier = 0.9d;
		int rndSwimOdds = 40;
		double swimSpeedModifier = 1.05d;

		wander = new RandomStrollGoal((PathfinderMob) this, walkSpeedModifier, rndWalkOdds);

		this.goalSelector.addGoal(0, new WatersnakeAttackGoal(this, 2.3D, true));
		// snakeIn, speedModifier, attackIntervalMin, attackIntervalMax, attackRadius
		this.goalSelector.addGoal(0, (Goal) new WatersnakeSpitAttackGoal(this, 1.25, 40, 50, 12.0f));
//		this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.2F));
		this.goalSelector.addGoal(3, new WaterSnakeEntity.GoNestGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new WaterSnakeEntity.GoWaterGoal(this, swimSpeedModifier, rndSwimOdds));
		this.goalSelector.addGoal(3, new WaterSnakeEntity.GoLandGoal(this, walkSpeedModifier, rndWalkOdds));
//		this.goalSelector.addGoal(4, new WaterSnakeEntity.PanicGoal(this, 1.4D));
		this.goalSelector.addGoal(4, new WaterSnakeEntity.GoWanderGoal(this, 1.4D, 40));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 12.0F));
		this.goalSelector.addGoal(6, new SwimGoal(this, 60));

		this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
				(Predicate<LivingEntity>) new TargetPredicate(this)));

		super.registerGoals();
	}

	private void removeNest() {
		if (hasNest) {
			level.setBlockAndUpdate(this.blockPosition(), Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public boolean requiresCustomPersistence() {

		if (this.level instanceof ServerLevel) {
			int watersnakeCount = ((ServerLevel) this.level).getEntities(ModEntities.WATER_SNAKE, (entity) -> true)
					.size();
			if (watersnakeCount < 3) {
				return true;
			}
			if (this.isAngry()) {
				return true;
			}
		}
		return super.requiresCustomPersistence();
	}

	public void setAngry(boolean bool) {
		this.angryTime = this.level.getGameTime();
		this.entityData.set((EntityDataAccessor<Boolean>) WaterSnakeEntity.ANGRY, bool);
	}

	private void setAttackAnimTime(final int attackAnimTime) {
		this.entityData.set((EntityDataAccessor<Integer>) WaterSnakeEntity.ATK_ANIM_TIME, attackAnimTime);
	}


	private void setGoingNest(boolean goNest) {
		this.entityData.set(GOING_NEST, goNest);
	}

	public void setNestPos(BlockPos posNest) {
		this.entityData.set(NEST_POS, posNest);
	}

	@Override
	protected void setRot(float yaw, float pitch) {
		float f = this.yRotO;
		float lerpYaw = Mth.lerp(0.05f, f, yaw);
		super.setRot(yaw, pitch);
	}

	public void setSnakeHeight(float snakeHeight) {
		this.snakeHeight = snakeHeight;
	}

	public void setSnakeWidth(float snakeWidth) {
		this.snakeWidth = snakeWidth;
	}

	public void setTailHeight(float amt) {
		this.tailHeight = amt;
	}

	@Override
	public void setTarget(LivingEntity entityIn) {
		if (entityIn == null) {
			this.setTargetedEntity(0);
			setAngry(false);
			this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(21.0F);
		} else {
			this.angryTime = this.level.getGameTime() + ANGER_MILD;
			this.setTargetedEntity(entityIn.getId());
			if (entityIn instanceof ServerPlayer) {
				this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(42.0F);
			}
			setAngry(true);
		}
		super.setTarget(entityIn);
	}

	private void setTargetedEntity(final int targetEntityId) {
		this.entityData.set((EntityDataAccessor<Integer>) WaterSnakeEntity.TARGET_ENTITY, targetEntityId);
	}

	private void setTravelling(boolean bool) {
		this.entityData.set(TRAVELLING, bool);
	}

	private void setTravelPos(BlockPos posTravel) {
		this.entityData.set(TRAVEL_POS, posTravel);
	}

	private void spit(final LivingEntity p_190713_1_) {
		final WaterSnakePoisonSpitEntity lvt_2_1_ = new WaterSnakePoisonSpitEntity(this.level, this);
		final double lvt_3_1_ = p_190713_1_.getX() - this.getX();
		final double lvt_5_1_ = p_190713_1_.getY(0.3333333333333333) - lvt_2_1_.getY();
		final double lvt_7_1_ = p_190713_1_.getZ() - this.getZ();
		final double lvt_9_1_ = Math.sqrt(lvt_3_1_ * lvt_3_1_ + lvt_7_1_ * lvt_7_1_) * 0.2d;
		lvt_2_1_.shoot(lvt_3_1_, lvt_5_1_ + lvt_9_1_, lvt_7_1_, 1.5f, 10.0f);
		if (!this.isSilent()) {
			this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT,
					this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
		}
		this.level.addFreshEntity((Entity) lvt_2_1_);
		this.didSpit = true;
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (this.isEffectiveAi() && this.isInWater()) {
			this.moveRelative(0.15F, travelVector);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
			if (this.getTarget() == null
					&& (!this.isGoingNest() || !this.getNestPos().closerThan(this.position(), 20.0D))) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
			}
		} else {
			super.travel(travelVector);
		}

	}

}
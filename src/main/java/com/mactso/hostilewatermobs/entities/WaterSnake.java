package com.mactso.hostilewatermobs.entities;

import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.sound.ModSounds;
import com.mactso.hostilewatermobs.util.TwoGuysLib;
import com.mactso.hostilewatermobs.utility.Utility;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.pathfinding.WalkAndSwimNodeProcessor;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.AxisAlignedBB;
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

public class WaterSnake extends WaterMobEntity implements IMob, IRangedAttackMob {
	static class GoLandGoal extends Goal {
		private final WaterSnake watersnake;
		private final double speed;
		private int chance;
		private boolean finished;

		GoLandGoal(WaterSnake watersnakeIn, double speedIn, int chanceIn) {
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
			Utility.debugMsg(2, watersnake.blockPosition(), "Start Walking to Land");
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
			if (watersnake.getNavigation().isDone()) {
				Vector3d vector3d = Vector3d.atBottomCenterOf(watersnake.getTravelPos());
				Vector3d vector3d1 = RandomPositionGenerator.getPosTowards(watersnake, 16, 3, vector3d,
						(double) ((float) Math.PI / 10F));
				if (vector3d1 == null) {
					vector3d1 = RandomPositionGenerator.getPosTowards(watersnake, 8, 7, vector3d);
				}

				if (vector3d1 != null) {
					int i = MathHelper.floor(vector3d1.x);
					int j = MathHelper.floor(vector3d1.z);
					int k = 34;
					if (!watersnake.level.hasChunksAt(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
						vector3d1 = null;
					}
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
		private WaterSnake watersnake;
		private double speed;
		private boolean noPath;
		private int timer;

		GoNestGoal(WaterSnake watersnake, double speedIn) {
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
				Utility.debugMsg(1, watersnake.blockPosition(), "Tick GoToNest at " + watersnake.getNestPos());
			}

			if (isNearNest) {
				++this.timer;
			}

			if (watersnake.getNavigation().isDone()) {
				Vector3d vector3d = Vector3d.atBottomCenterOf(blockpos);
				Vector3d vector3d1 = RandomPositionGenerator.getPosTowards(watersnake, 16, 3, vector3d,
						(double) ((float) Math.PI / 10F));
				if (vector3d1 == null) {
					vector3d1 = RandomPositionGenerator.getPosTowards(watersnake, 8, 7, vector3d);
				}

				if (vector3d1 != null && !isNearNest
						&& !watersnake.level.getBlockState(new BlockPos(vector3d1)).is(Blocks.WATER)) {
					vector3d1 = RandomPositionGenerator.getPosTowards(watersnake, 16, 5, vector3d);
				}

				if (vector3d1 == null) {
					this.noPath = true;
					return;
				}

				watersnake.getNavigation().moveTo(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
			}

		}
	}
	static class GoWanderGoal extends RandomWalkingGoal {
		private final WaterSnake watersnake;

		private GoWanderGoal(WaterSnake watersnakeIn, double speedIn, int chance) {
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
		private final WaterSnake watersnake;
		private int chance;

		private GoWaterGoal(WaterSnake watersnakeIn, double speedIn, int chanceIn) {
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
		protected boolean isValidTarget(IWorldReader worldIn, BlockPos pos) {
			return worldIn.getBlockState(pos).is(Blocks.WATER);
		}

		public boolean shouldRecalculatePath() {
			return this.tryTicks % 80 == 0;
		}
	}
	static class MoveHelperController extends MovementController {
		private final WaterSnake watersnake;
		private int jumpTimer = 0;

		public MoveHelperController(final WaterSnake watersnakeEntityIn) {
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
				Utility.debugMsg(2, watersnake.blockPosition(),
						"Tick MoveHelperController to " + watersnake.getTravelPos());
			}
			if (this.operation == MovementController.Action.MOVE_TO && !watersnake.getNavigation().isDone()) {
				double dx = this.wantedX - watersnake.getX();
				double dy = this.wantedY - watersnake.getY();
				double dz = this.wantedZ - watersnake.getZ();
				double distance = (double) MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
				dy = dy / distance;
				float f = (float) (MathHelper.atan2(dz, dx) * (double) (180F / (float) Math.PI)) - 90.0F;
				watersnake.yRot = this.rotlerp(watersnake.yRot, f, 90.0F);
				watersnake.yBodyRot = watersnake.yRot;
				float f1 = (float) (speedModifier * this.watersnake.getAttributeValue(Attributes.MOVEMENT_SPEED));
				watersnake.setSpeed(MathHelper.lerp(0.225F, watersnake.getSpeed(), f1));
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
	static class Navigator extends SwimmerPathNavigator {
		Navigator(WaterSnake watersnake, World worldIn) {
			super(watersnake, worldIn);
		}

		/**
		 * If on ground or swimming and can swim
		 */
		protected boolean canUpdatePath() {
			return true;
		}

		protected PathFinder createPathFinder(int p_179679_1_) {
			this.nodeEvaluator = new WalkAndSwimNodeProcessor();
			return new PathFinder(this.nodeEvaluator, p_179679_1_);
		}

		public boolean isStableDestination(BlockPos pos) {
			if (this.mob instanceof WaterSnake) {
				WaterSnake watersnake = (WaterSnake) this.mob;
				if (watersnake.isTravelling()) {
					return this.level.getBlockState(pos).is(Blocks.WATER);
				}
			}

			return !this.level.getBlockState(pos.below()).isAir();
		}
	}
	static class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
		PanicGoal(WaterSnake watersnake, double speedIn) {
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
			BlockPos blockpos = this.lookForWater(this.mob.level, this.mob, 7, 4);
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
	static class SwimGoal extends net.minecraft.entity.ai.goal.SwimGoal {
		WaterSnake watersnake;
		int chance;

		SwimGoal(WaterSnake watersnakeIn) {
			super(watersnakeIn);
			this.chance = 40;
			this.watersnake = watersnakeIn;
		}

		SwimGoal(WaterSnake watersnakeIn, int chanceIn) {
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
		private final WaterSnake waterSnakeEntity;

		public TargetPredicate(WaterSnake waterSnakeIn) {
			waterSnakeEntity = waterSnakeIn;
		}

		// called to decide if a target in range is valid to attack
		public boolean test(@Nullable LivingEntity entity) {

			// 's don't attack each other
			if (entity instanceof WaterSnake) {
				return false;
			}

			// villagers have secret tricks to make 's not want to eat them
			if (entity instanceof VillagerEntity) {
				return false;
			}

			// watersnakes are non-hostile to turtles.
			if (entity instanceof TurtleEntity) {
				return false;
			}

			
			// 's don't attack things they can't see unless attacked first.
			if (!waterSnakeEntity.canSee(entity)) {
				if (entity != waterSnakeEntity.getKillCredit()) {
					return false;
				}
			}

			// 's don't attack players in creative or spectator mode

			if (entity instanceof PlayerEntity) {
				if (((PlayerEntity) entity).isCreative()) {
					return false;
				} else if (((PlayerEntity) entity).isSpectator()) {
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
			Vector3i nestPos = (Vector3i) waterSnakeEntity.getNestPos();

			// water snakes always attack if entity threatens the nest and is near entity.
			Vector3i entityPosVec = (Vector3i) entity.blockPosition();
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

			World w = entity.getCommandSenderWorld();

			// rarely attack random fish and other creatures in range.
			if (!(entity instanceof PlayerEntity)) {
				if (w.random.nextInt(600) != 100) {
					waterSnakeEntity.setTarget(null);
					return false;
				}
			}

			// a little less aggressive in swamps
			Biome biome = w.getBiome(waterSnakeEntity.blockPosition());
			Category bC = biome.getBiomeCategory();
			if ((bC == Category.SWAMP)) {
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
				if (entity instanceof PlayerEntity) {
					int playSound = waterSnakeEntity.random.nextInt(50);

					if ((dstToEntitySq < 900) && (playSound == 21)) {
						w.playSound(null, entity.blockPosition(), ModSounds.WATER_SNAKE_AMBIENT, SoundCategory.HOSTILE,
								0.35f, 1.0f);
					}
				}
				waterSnakeEntity.setTarget(null);
				return false;
			}

			waterSnakeEntity.setTarget(entity);
			w.playSound(null, waterSnakeEntity.blockPosition(), ModSounds.WATER_SNAKE_ANGRY, SoundCategory.HOSTILE,
					1.0f, 1.0f);
			return true;
		}
	}
	public class WatersnakeAttackGoal extends MeleeAttackGoal {
		private final WaterSnake snake;

		public WatersnakeAttackGoal(final WaterSnake snakeIn, final double speedBoost,
				final boolean followingTargetEvenIfNotSeen) {
			super((CreatureEntity) snakeIn, speedBoost, followingTargetEvenIfNotSeen);
			this.snake = snakeIn;
			snakeIn.startAttackTime = snake.level.getGameTime()+16;
		}

		public void start() {
			super.start();
			Utility.debugMsg(1, "Start Melee Attack");
			this.snake.setAggressive(true);
		}

		public void stop() {
			super.stop();
			Utility.debugMsg(1, "Stop Melee Attack");
			this.snake.setAggressive(false);
		}


	}
	//
	// GOAL section
	//
	public class WatersnakeSpitAttackGoal extends RangedAttackGoal {
		private final WaterSnake snake;
		private final float attackRadius;

		public WatersnakeSpitAttackGoal(final WaterSnake snakeIn, final double speedModifier, final int attackIntervalMin, final int attackIntervalMax, final float attackRadius) {
	
			super(snakeIn, speedModifier, attackIntervalMin, attackIntervalMax, attackRadius);
			this.snake = snakeIn;
			this.attackRadius = attackRadius;
			snakeIn.startSpittingTime = snake.level.getGameTime()+16;

		}

		public void start() {
			super.start();
			snake.startSpittingTime = snake.level.getGameTime()+16;
			Utility.debugMsg(2, "Start Spitting");
			this.snake.setAggressive(true);
		}

		public void stop() {
			super.stop();
			Utility.debugMsg(2, "Stop Spitting");
			this.snake.setAggressive(false);
		}

	}

	private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.defineId(WaterSnake.class,
			DataSerializers.INT);
	private static final DataParameter<Boolean> ANGRY = EntityDataManager.defineId(WaterSnake.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> GOING_NEST = EntityDataManager.defineId(WaterSnake.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<BlockPos> NEST_POS = EntityDataManager.defineId(WaterSnake.class,
			DataSerializers.BLOCK_POS);
	private static final DataParameter<BlockPos> TRAVEL_POS = EntityDataManager.defineId(WaterSnake.class,
			DataSerializers.BLOCK_POS);
	private static final DataParameter<Boolean> TRAVELLING = EntityDataManager.defineId(WaterSnake.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> ATK_ANIM_TIME = EntityDataManager.defineId(WaterSnake.class,
			DataSerializers.INT);
	private static final DataParameter<Integer> SPIT_TIME = EntityDataManager.defineId(WaterSnake.class,
			DataSerializers.INT);
	
	public static final int NUM_WATER_CHECKS = 17;
	public static final int ANGER_MILD = 300;
	public static final int ANGER_INTENSE = 1200;
	private static long lastSpawnTime = 0;
	public static final float SIZE = EntityType.PIG.getWidth() * 0.45f;
	private static final RangedInteger rangedInteger = TickRangeConverter.rangeOfSeconds(20, 39);
	private static int calcNetMobCap(IWorld level, BlockPos pos) {

		int mobCap = MyConfig.getWatersnakeSpawnCap() +
				((ServerWorld) level).getServer().getPlayerCount();

		int waterBonus = TwoGuysLib.fastRandomBlockCount(level, Blocks.WATER, pos, NUM_WATER_CHECKS);

		String bC = Utility.getBiomeCategory(level.getBiome(pos));
		if ((bC == Utility.OCEAN) || (bC == Utility.RIVER) || (bC == Utility.SWAMP) || (bC == Utility.BEACH)) {
			mobCap += 1 + (waterBonus/2);
			return mobCap;
		}

		// support for unknown modded wet biomes.
		Biome biome = level.getBiome(pos);
		ResourceLocation biomeNameResourceKey = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)
				.getKey(biome);
		String biomename = biomeNameResourceKey.toString();
		RegistryKey<Biome> biomeKey = RegistryKey.create(Registry.BIOME_REGISTRY, biomeNameResourceKey);
		if (BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.WATER)) 
			mobCap += 5;		
		if (BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.WET)) 
			mobCap += 3;		
		
		return mobCap;
	}
	
	public static boolean canSpawn(EntityType<? extends WaterSnake> watersnakeIn, IWorld level,
			SpawnReason reason, BlockPos pos, Random randomIn) {

		Utility.debugMsg(1, pos, "canSpawn waterSnake?");
		// SpawnPlacements.Type.ON_GROUND
		
		if (Utility.isSpawnRateThrottled(level, 0)) {
			return false;
		}
		
		if (level.getDifficulty() == Difficulty.PEACEFUL)
			return false;
		
		if (reason == SpawnReason.SPAWN_EGG)
			return true;

		if (isTooBright(level, pos))
			return false;
		
		if (reason == SpawnReason.SPAWNER) {
			return true;
		}

		if (isBadAltitude(level, pos))
			return false;

		if (isFailBiomeLimits(level, pos))
			return false;
	
		// prevent local overcrowding
		if (Utility.isOverCrowded(level, WaterSnake.class, pos, 5))
			return false;
		
		int mobCount = ((ServerWorld) level).getEntities(ModEntities.WATER_SNAKE, (entity) -> true).size();
		if (mobCount >= calcNetMobCap(level, pos)) {
			return false;
		}

		Utility.debugMsg(1, "spawn watersnake true at " + pos.getX() + " " + pos.getY() + " " + pos.getZ());

		return true;
	}
	
	@Nullable
	private static Vector3d findSolidBlock(EntityType<? extends WaterSnake> snakeIn, IWorld world,
			BlockPos blockPos, int maxXZ, int maxY) {
		Random rand = world.getRandom();
		for (int i = 0; i < 12; ++i) {
			int xD = rand.nextInt(maxXZ + maxXZ) - maxXZ;
			int zD = rand.nextInt(maxXZ + maxXZ) - maxXZ;
			int yD = rand.nextInt(maxY + maxY) - maxY;
			if (blockPos.getY() + yD > 0 && blockPos.getY() + yD < 254) {
				if (world.getBlockState(blockPos).getMaterial().isSolid()) {
					return Vector3d.atBottomCenterOf(blockPos.east(xD).above(yD).west(zD));
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	private static boolean isBadAltitude(IWorld level, BlockPos pos) {

		if (pos.getY() > level.getSeaLevel() + 32)
			return true;
		if (pos.getY() < level.getSeaLevel() - 48)
			return true;

		return false;
	}
	
	public static boolean isBubbleColumn(IWorld world, BlockPos pos) {
		return world.getBlockState(pos).is(Blocks.BUBBLE_COLUMN);
	}
	
	private static boolean isDeep(BlockPos pos) {
		return (pos.getY() < 48);
	}

	private static boolean isFailBiomeLimits(IWorld level, BlockPos pos) {

		String bC = Utility.getBiomeCategory(level.getBiome(pos));
		if ( bC == Utility.MUSHROOM || bC == Utility.THEEND	) {
			return true;
		}
		
		Biome biome = level.getBiome(pos);
		ResourceLocation biomeNameResourceKey = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
		String biomename = biomeNameResourceKey.toString();
		RegistryKey<Biome> biomeKey = RegistryKey.create(Registry.BIOME_REGISTRY, biomeNameResourceKey);

		if ((BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.HOT)) && 
				(BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.DRY))) {
			return true;
		}

		return false;
	}


	private static boolean isTooBright(IWorld level, BlockPos pos) {

		if (isDeep(pos)) {
			if (level.getMaxLocalRawBrightness(pos) > 9) {
				return true;
			}
		}

		if (level.getMaxLocalRawBrightness(pos) > 13) {
			return true; // combined skylight and blocklight
		}

		if (level.getBrightness(LightType.BLOCK, pos) > MyConfig.getBlockLightLevel()) {
			return true;
		}

		return false;

	}

	public static AttributeModifierMap.MutableAttribute registerAttributes() {

		return MonsterEntity.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double) 0.26F)
				.add(Attributes.FOLLOW_RANGE, 20.0D).add(Attributes.ATTACK_DAMAGE, 2.5D)
				.add(Attributes.MAX_HEALTH, 18.5D);
	}

	private boolean hasNest;

	private int nestProtectionDistSq;

	private long angryTime;

	public long startAttackTime;

	protected RandomWalkingGoal wander;

	protected float tailHeight = -0.2707964f;

	protected float bodyHeight = 0.2707964f;

	private boolean didSpit;

	public long startSpittingTime;

	public long hissingTime;

	public double snakeHeight = EntityType.PIG.getHeight() * 0.5f;

	public double snakeWidth = EntityType.PIG.getWidth() * 0.45f;

	private LivingEntity targetedEntity;
	
	public WaterSnake(EntityType<? extends WaterSnake> type, World worldIn) {

		super(type, worldIn);
		this.xpReward = 7;
		this.setPathfindingMalus(PathNodeType.WATER, 0.0f);
		this.moveControl = new WaterSnake.MoveHelperController(this);
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
		if (damageAmount < 0 ) {
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
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("NestPosX", this.getNestPos().getX());
		compound.putInt("NestPosY", this.getNestPos().getY());
		compound.putInt("NestPosZ", this.getNestPos().getZ());
		compound.putInt("TravelPosX", this.getTravelPos().getX());
		compound.putInt("TravelPosY", this.getTravelPos().getY());
		compound.putInt("TravelPosZ", this.getTravelPos().getZ());
	}

	public int AttackAnimTime() {
		return (int) this.entityData.get((DataParameter<Integer>) WaterSnake.ATK_ANIM_TIME);
	}

	@Override
	public boolean canBeAffected(EffectInstance potioneffectIn) {
		net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(
				this, potioneffectIn);
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() != net.minecraftforge.eventbus.api.Event.Result.DEFAULT)
			return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;

		Effect effect = potioneffectIn.getEffect();
		if (effect == Effects.POISON) {
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
			this.remove();
		} else if (!this.isPersistenceRequired() && !this.requiresCustomPersistence()) {
			Entity entity = this.level.getNearestPlayer(this, -1.0D);
			net.minecraftforge.eventbus.api.Event.Result result = net.minecraftforge.event.ForgeEventFactory
					.canEntityDespawn(this);
			if (result == net.minecraftforge.eventbus.api.Event.Result.DENY) {
				noActionTime = 0;
				entity = null;
			} else if (result == net.minecraftforge.eventbus.api.Event.Result.ALLOW) {
				this.remove();
				entity = null;
			}
			if (entity != null) {
				double d0 = entity.distanceToSqr(this);
				int i = this.getType().getCategory().getDespawnDistance();
				int j = i * i;
				if (d0 > (double) j && this.removeWhenFarAway(d0)) {
					removeNest();
					this.remove();

				}

				int k = this.getType().getCategory().getNoDespawnDistance();
				int l = k * k;
				if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && d0 > (double) l
						&& this.removeWhenFarAway(d0)) {
					this.remove();
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
	protected PathNavigator createNavigation(World worldIn) {
		return new WaterSnake.Navigator(this, worldIn);
	}
	
	
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define((DataParameter<Integer>) WaterSnake.TARGET_ENTITY, 0);
		this.entityData.define((DataParameter<Boolean>) WaterSnake.ANGRY, false);
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
			((LivingEntity) entityIn).addEffect(new EffectInstance(Effects.POISON, 60, 1));
		}
		return super.doHurtTarget(entityIn);
	}

	@Override
	@Nullable
	public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			@Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
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

	public EntitySize getSize() { // why should this be static?

//		float width = 0.9f;
//		float height = 0.6f;
		boolean fixed_size = false;
		return new EntitySize((float) this.snakeWidth, (float) this.snakeHeight, fixed_size);
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
				.getEntity((int) this.entityData.get((DataParameter<Integer>) WaterSnake.TARGET_ENTITY));
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
		return (int) this.entityData.get((DataParameter<Integer>) WaterSnake.TARGET_ENTITY) != 0;
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
		return this.entityData.get((DataParameter<Boolean>) WaterSnake.ANGRY);
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
	public void readAdditionalSaveData(CompoundNBT compound) {
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

		wander = new RandomWalkingGoal((CreatureEntity) this, walkSpeedModifier, rndWalkOdds);

		this.goalSelector.addGoal(0, new WatersnakeAttackGoal(this, 2.3D, true));
		// snakeIn, speedModifier, attackIntervalMin, attackIntervalMax, attackRadius
		this.goalSelector.addGoal(0, (Goal) new WatersnakeSpitAttackGoal(this, 1.25, 40, 50, 12.0f));
//		this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.2F));
		this.goalSelector.addGoal(3, new WaterSnake.GoNestGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new WaterSnake.GoWaterGoal(this, swimSpeedModifier, rndSwimOdds));
		this.goalSelector.addGoal(3, new WaterSnake.GoLandGoal(this, walkSpeedModifier, rndWalkOdds));
//		this.goalSelector.addGoal(4, new WaterSnakeEntity.PanicGoal(this, 1.4D));
		this.goalSelector.addGoal(4, new WaterSnake.GoWanderGoal(this, 1.4D, 40));
		this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
		this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 12.0F));
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

		if (this.level instanceof ServerWorld) {
			int watersnakeCount = ((ServerWorld) this.level).getEntities(ModEntities.WATER_SNAKE, (entity) -> true)
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
		this.entityData.set((DataParameter<Boolean>) WaterSnake.ANGRY, bool);
	}

	private void setAttackAnimTime(final int attackAnimTime) {
		this.entityData.set((DataParameter<Integer>) WaterSnake.ATK_ANIM_TIME, attackAnimTime);
	}

	@Override
	public void setBoundingBox(AxisAlignedBB p_174826_1_) {
		super.setBoundingBox(p_174826_1_);
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
		float lerpYaw = MathHelper.lerp(0.05f, f, yaw);
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
			if (entityIn instanceof ServerPlayerEntity) {
				this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(42.0F);
			}
			setAngry(true);
		}
		super.setTarget(entityIn);
	}

	private void setTargetedEntity(final int targetEntityId) {
		this.entityData.set((DataParameter<Integer>) WaterSnake.TARGET_ENTITY, targetEntityId);
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
		final float lvt_9_1_ = MathHelper.sqrt(lvt_3_1_ * lvt_3_1_ + lvt_7_1_ * lvt_7_1_) * 0.2f;
		lvt_2_1_.shoot(lvt_3_1_, lvt_5_1_ + lvt_9_1_, lvt_7_1_, 1.5f, 10.0f);
		if (!this.isSilent()) {
			this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT,
					this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
		}
		this.level.addFreshEntity((Entity) lvt_2_1_);
		this.didSpit = true;
	}

	@Override
	public void travel(Vector3d travelVector) {
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

package com.mactso.hostilewatermobs.entities;

import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.block.ModBlocks;
import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.sound.ModSounds;
import com.mactso.hostilewatermobs.utility.TwoGuysLib;
import com.mactso.hostilewatermobs.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event.Result;

public class Gurty extends PathfinderMob implements NeutralMob, Enemy {

	static class GoLandGoal extends Goal {
		private final Gurty gurty;
		private final double speed;
		private int chance;
		private boolean finished;

		GoLandGoal(Gurty entityIn, double speedIn, int chanceIn) {
			this.gurty = entityIn;
			this.speed = speedIn;
			this.chance = chanceIn;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean canContinueToUse() {
			return !gurty.getNavigation().isDone() && !this.finished && !gurty.isGoingNest();
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {
			if (gurty.getRandom().nextInt(this.chance) != 0) {
				return false;
			}
			if (!gurty.level.isDay()) {
				if (gurty.isGoingNest()) {
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
			RandomSource random = gurty.random;
			int k = random.nextInt(128) - 64;
			int l = random.nextInt(9) - 4;
			int i1 = random.nextInt(128) - 64;
			if ((double) l + gurty.getY() > (double) (gurty.level.getSeaLevel() - 1)) {
				l = 0;
			}

			BlockPos blockpos = BlockPos.containing((double) k + gurty.getX(), (double) l + gurty.getY(),
					(double) i1 + gurty.getZ());
			gurty.setTravelPos(blockpos);
			gurty.setTravelling(true);
			this.finished = false;
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void stop() {
			gurty.setTravelling(false);
			super.stop();
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			if (gurty.getNavigation().isDone()) {
				Vec3 vector3d = Vec3.atBottomCenterOf(gurty.getTravelPos());
				Vec3 vector3d1 = DefaultRandomPos.getPosTowards(gurty, 16, 3, vector3d,
						(double) ((float) Math.PI / 10F));
				if (vector3d1 == null) {
					vector3d1 = DefaultRandomPos.getPosTowards(gurty, 8, 7, vector3d, (double) ((float) Math.PI / 10F));
				}

				if (vector3d1 != null) {
					int i = Mth.floor(vector3d1.x);
					int j = Mth.floor(vector3d1.z);
					int k = 34;
					if (!gurty.level.hasChunksAt(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
						vector3d1 = null;
					}
				}

				if (vector3d1 == null) {
					this.finished = true;
					return;
				}

				gurty.getNavigation().moveTo(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
			}

		}
	}

	static class GoNestGoal extends Goal {
		private Gurty gurty;
		private double speed;
		private boolean noPath;
		private int timer;

		GoNestGoal(Gurty gurty, double speedIn) {
			this.gurty = gurty;
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
			return !gurty.getNestPos().closerToCenterThan(gurty.position(), 5.0D);
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {
			if (gurty.isNestingTime()) {
				return true;
			}
			if (!gurty.getNestPos().closerToCenterThan(gurty.position(), 64.0D)) {
				return true;
			}
			return false;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void start() {
			gurty.setGoingNest(true);
			this.noPath = false;
			this.timer = 0;
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void stop() {
			gurty.setGoingNest(false);
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			BlockPos blockpos = gurty.getNestPos();
			boolean isNearNest = blockpos.closerToCenterThan(gurty.position(), 16.0D);
			if (isNearNest) {
				++this.timer;
			}

			if (gurty.getNavigation().isDone()) {
				Vec3 vector3d = Vec3.atBottomCenterOf(blockpos);
				Vec3 vector3d1 = DefaultRandomPos.getPosTowards(gurty, 16, 3, vector3d,
						(double) ((float) Math.PI / 10F));
				if (vector3d1 == null) {
					vector3d1 = DefaultRandomPos.getPosTowards(gurty, 8, 7, vector3d, (double) ((float) Math.PI / 10F));
				}

				if (vector3d1 != null && !isNearNest
						&& !gurty.level.getBlockState(BlockPos.containing(vector3d1.x,vector3d1.y,vector3d1.z)).is(Blocks.WATER)) {
					vector3d1 = DefaultRandomPos.getPosTowards(gurty, 16, 5, vector3d,
							(double) ((float) Math.PI / 10F));
				}

				if (vector3d1 == null) {
					this.noPath = true;
					return;
				}

				gurty.getNavigation().moveTo(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
			}

		}
	}

	static class GoWanderGoal extends RandomStrollGoal {
		private final Gurty gurty;

		private GoWanderGoal(Gurty gurtyIn, double speedIn, int chance) {
			super(gurtyIn, speedIn, chance);
			gurty = gurtyIn;
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {
			return !gurty.isInWater() && !gurty.isGoingNest() ? super.canUse() : false;
		}
	}

	static class GoWaterGoal extends MoveToBlockGoal {
		private final Gurty gurty;
		private int chance;

		private GoWaterGoal(Gurty gurtyIn, double speedIn, int chanceIn) {
			super(gurtyIn, speedIn, 24);
			gurty = gurtyIn;
			chance = chanceIn;
			this.verticalSearchStart = -1;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean canContinueToUse() {
			return !gurty.isInWater() && this.tryTicks <= 1200 && this.isValidTarget(gurty.level, this.blockPos);
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {
			if (gurty.isGoingNest()) {
				return false;
			}
			if (!gurty.level.isDay()) {
				return false;
			}
			if (gurty.random.nextInt(chance) == 0) {
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
		private final Gurty gurty;
		private int jumpTimer = 0;

		public MoveHelperController(final Gurty gurtyEntityIn) {
			super(gurtyEntityIn);
			this.gurty = gurtyEntityIn;

		}

		public void tick() {
			this.updateSpeed();

			if (gurty.isAngry()) {
				gurty.setTailHeight(0.77f);
			} else {
				gurty.setTailHeight(-0.27f);
			}

			if (this.operation == MoveControl.Operation.MOVE_TO && !gurty.getNavigation().isDone()) {
				double dx = this.wantedX - gurty.getX();
				double dy = this.wantedY - gurty.getY();
				double dz = this.wantedZ - gurty.getZ();
				// TODO: should use "Mth.sqrt (float) instead?
				double distance = (double) Math.sqrt(dx * dx + dy * dy + dz * dz);
				dy = dy / distance;
				float f = (float) (Mth.atan2(dz, dx) * (double) (180F / (float) Math.PI)) - 90.0F;
				gurty.setYRot(this.rotlerp(gurty.getYRot(), f, 90.0F));
				gurty.setYBodyRot(gurty.getYRot());
				float f1 = (float) (speedModifier * this.gurty.getAttributeValue(Attributes.MOVEMENT_SPEED));
				gurty.setSpeed(Mth.lerp(0.225F, gurty.getSpeed(), f1));
				gurty.setDeltaMovement(gurty.getDeltaMovement().add(0.0D, (double) gurty.getSpeed() * dy * 0.1D, 0.0D));
			} else {
				gurty.setSpeed(0.0F);
			}
		}

		private void updateSpeed() {
			if (gurty.isInWater()) {
				gurty.setDeltaMovement(this.gurty.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
				if (!gurty.getNestPos().closerToCenterThan(gurty.position(), 16.0D)) {
					gurty.setSpeed(Math.max(gurty.getSpeed() / 2.0F, 0.11F));
				}
			} else if (gurty.onGround) {
				gurty.setSpeed(Math.max(gurty.getSpeed() / 1.9F, 0.17F));
			}

		}

	}

	static class Navigator extends WaterBoundPathNavigation {
		Navigator(Gurty gurty, Level worldIn) {
			super(gurty, worldIn);
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
			if (this.mob instanceof Gurty) {
				Gurty gurty = (Gurty) this.mob;
				if (gurty.isTravelling()) {
					return this.level.getBlockState(pos).is(Blocks.WATER);
				}
			}

			return !this.level.getBlockState(pos.below()).isAir();
		}
	}

	static class PanicGoal extends net.minecraft.world.entity.ai.goal.PanicGoal {
		PanicGoal(Gurty gurty, double speedIn) {
			super(gurty, speedIn);
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
			BlockPos blockpos = this.lookForWater(this.mob.level, this.mob, 7); // used to be 7, 4. parm gone.
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
		Gurty gurty;
		int chance;

		SwimGoal(Gurty gurtyIn) {
			super(gurtyIn);
			this.chance = 40;
			this.gurty = gurtyIn;
		}

		SwimGoal(Gurty gurtyIn, int chanceIn) {
			super(gurtyIn);
			this.chance = chanceIn;
			this.gurty = gurtyIn;
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {

			if (gurty.random.nextInt(chance) != 0) {
				return false;
			}
			return true;

		}
	}

	static class TargetPredicate implements Predicate<LivingEntity> {
		private final Gurty parentEntity;

		public TargetPredicate(Gurty gurtyIn) {
			parentEntity = gurtyIn;
		}

		// called to decide if a target in range is valid to attack
		public boolean test(@Nullable LivingEntity entity) {

			// gurty's don't attack each other
			if (entity instanceof Gurty) {
				return false;
			}

			// villagers have secret tricks to make gurty's not want to eat them
			if (entity instanceof Villager) {
				return false;
			}

			// gurtys are non-hostile to turtles.
			if (entity instanceof Turtle) {
				return false;
			}

			// gurty's don't attack things they can't see unless attacked first.
			if (!parentEntity.hasLineOfSight(entity)) { // was canSee(). Bugger to find- found in Ghast.
				if (entity != parentEntity.getKillCredit()) {
					return false;
				}
			}

			// gurty's don't attack players in creative or spectator mode

			if (entity instanceof Player) {
				if (((Player) entity).isCreative()) {
					return false;
				} else if (((Player) entity).isSpectator()) {
					return false;
				}
			}

			boolean validTarget = false;
			// gurty's always take revenge on their attackers, regardless of any other
			// condition
			if (parentEntity.getTarget() != null) {
				if (entity == this.parentEntity.getKillCredit()) {
					parentEntity.setTarget(entity);
					return true;
				}
			}

			// distance to entity.
			int dstToEntitySq = (int) entity.distanceToSqr(parentEntity);
			Vec3i nestPos = (Vec3i) parentEntity.getNestPos();

			// gurty's always attack if entity threatens the nest and gurty is near entity.
			Vec3i entityPosVec = (Vec3i) entity.blockPosition();
			int nestThreatDistance = (int) entityPosVec.distSqr(parentEntity.getNestPos());

			// gurty's get angry at creatures near their nest area if the gurty is nearby.
			if ((nestThreatDistance < parentEntity.nestProtectionDistSq) && (dstToEntitySq < 121)) {
				parentEntity.setTarget(entity);
				return true;
			}

			// Don't attack things when too far from nest.
			if ((nestPos.distSqr(parentEntity.blockPosition()) > 1600)) {
				parentEntity.setTarget(null);
				return false;
			}

			Level w = entity.getCommandSenderWorld();

			// rarely attack random fish and other creatures in range.
			if (!(entity instanceof Player)) {
				if (w.random.nextInt(600) != 100) {
					parentEntity.setTarget(null);
					return false;
				}
			}

			// a little less aggressive in swamps
			String bC = Utility.getBiomeCategory(w.getBiome(parentEntity.blockPosition()));
			if ((bC == Utility.SWAMP)) {
				dstToEntitySq += 64;
			}

			// less aggressive in light
			int lightLevel = w.getMaxLocalRawBrightness(this.parentEntity.blockPosition());
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

			double followDistance = parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue();
			int followDistanceSq = (int) (followDistance * followDistance);

			// if modified distance to entity > follow distance attribute, don't attack.
			if (dstToEntitySq > (followDistanceSq)) {
				// But if a player and in range and random playsound (2.0%) then play a warning
				// ambient sound.
				if (entity instanceof Player) {
					int playSound = parentEntity.random.nextInt(50);

					if ((dstToEntitySq < 900) && (playSound == 21)) {
						w.playSound(null, entity.blockPosition(), ModSounds.GURTY_AMBIENT, SoundSource.HOSTILE, 0.35f, 1.0f);
					}
				}
				parentEntity.setTarget(null);
				return false;
			}

			parentEntity.setTarget(entity);
			w.playSound(null, parentEntity.blockPosition(), ModSounds.GURTY_ANGRY, SoundSource.HOSTILE, 1.0f, 1.0f);
			return true;
		}
	}

	private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(Gurty.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(Gurty.class,
			EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> GOING_NEST = SynchedEntityData.defineId(Gurty.class,
			EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<BlockPos> NEST_POS = SynchedEntityData.defineId(Gurty.class,
			EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<BlockPos> TRAVEL_POS = SynchedEntityData.defineId(Gurty.class,
			EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<Boolean> TRAVELLING = SynchedEntityData.defineId(Gurty.class,
			EntityDataSerializers.BOOLEAN);

	public static final int ANGER_MILD = 300;

	public static final int ANGER_INTENSE = 1200;

	public static final int NUM_WATER_CHECKS = 21;
	
	public static final float SIZE = EntityType.PIG.getWidth() * 1.25f;

	private static long lastSpawnTime = 0;

	private static final UniformInt rangedInteger = TimeUtil.rangeOfSeconds(20, 39);



	private static int calcNetMobCap(LevelAccessor level, BlockPos pos) {

		int mobCap = MyConfig.getGurtySpawnCap() + level.getServer().getPlayerCount();

		String bC = Utility.getBiomeCategory(level.getBiome(pos));
		if (bC == Utility.RIVER) {
			mobCap += 7;
			return mobCap;
		}
		if (bC == Utility.SWAMP) {
			mobCap += 5;
			return mobCap;
		}

		// support for unknown modded wet biomes.
		if (level.getBiome(pos).is(BiomeTags.IS_OCEAN)) {
			mobCap += 3;
			return mobCap;
		}

		if (level.getBiome(pos).is(BiomeTags.IS_RIVER)) {
			mobCap += 5;
			return mobCap;
		}

		return mobCap;
	}

	public static boolean canSpawn(EntityType<? extends Gurty> gurtyIn, LevelAccessor level, MobSpawnType reason,
			BlockPos pos, RandomSource randomIn) {

		Utility.debugMsg(1, pos, "canSpawn Gurty?");
		// SpawnPlacements.Type.ON_GROUND

		if (Utility.isSpawnRateThrottled(level, 0)) {
			return false;
		}

		if (level.getDifficulty() == Difficulty.PEACEFUL)
			return false;

		if (reason == MobSpawnType.SPAWN_EGG)
			return true;

		if (isTooBright(level, pos))
			return false;

		if (reason == MobSpawnType.SPAWNER) {
			return true;
		}

		if (isBadAltitude(level, pos))
			return false;

		if (isFailBiomeLimits(level, pos))
			return false;

		if (!isWaterNearby(level, pos)) {
			return false;
		}

		// prevent local overcrowding
		if (Utility.isOverCrowded(level, Gurty.class, pos, 5))
			return false;

		int mobCount = ((ServerLevel) level).getEntities(ModEntities.GURTY, (entity) -> true).size();
		if (mobCount >= calcNetMobCap(level, pos)) {
			return false;
		}

		Utility.debugMsg(1, pos, "spawned Gurty.");

		return true;

	}

	public static AttributeSupplier.Builder createAttributes() {

		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double) 0.26F)
				.add(Attributes.FOLLOW_RANGE, 20.0D).add(Attributes.ATTACK_DAMAGE, 2.5D)
				.add(Attributes.MAX_HEALTH, 21.5D);
	}

	@Nullable
	private static Vec3 findSolidBlock(EntityType<? extends Gurty> gurtyIn, LevelAccessor world, BlockPos blockPos,
			int maxXZ, int maxY) {
		RandomSource rand = world.getRandom();
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

	public static EntityDimensions getSize() {
		float width = 0.9f;
		float height = 0.6f;
		boolean fixed_size = false;
		return new EntityDimensions(width, height, fixed_size);
	}

	@SuppressWarnings("deprecation")
	private static boolean isBadAltitude(LevelAccessor level, BlockPos pos) {

		if (pos.getY() < level.getSeaLevel() - 10)
			return true;
		if (pos.getY() > level.getSeaLevel() + 32)
			return true;

		return false;
	}

	public static boolean isBubbleColumn(LevelAccessor world, BlockPos pos) {
		return world.getBlockState(pos).is(Blocks.BUBBLE_COLUMN);
	}

	private static boolean isFailBiomeLimits(LevelAccessor level, BlockPos pos) {

		String bC = Utility.getBiomeCategory(level.getBiome(pos));
		if (bC == Utility.MUSHROOM || bC == Utility.THEEND) {
			return true;
		}

		
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

		if (level.getMaxLocalRawBrightness(pos) > 13) {
			return true; // combined skylight and blocklight
		}

		if (level.getBrightness(LightLayer.BLOCK, pos) > MyConfig.getBlockLightLevel()) {
			return true; // let player lighting from blocks stop spawning.
		}

		if (!level.dimensionType().hasSkyLight()) {
			return true; // no gurties in dimensions lacking skylight
		}

		return false;
	}

	private static boolean isWaterNearby(LevelAccessor level, BlockPos pos) {
		if (TwoGuysLib.fastRandomBlockCheck(level, Blocks.WATER, pos, NUM_WATER_CHECKS)) {
			return true;
		}
		;
		return false;
	}

	private int angerTime;

	private UUID angerTarget;

	private boolean hasNest;

	private int nestProtectionDistSq;

	protected RandomStrollGoal wander;

	protected float tailHeight = -0.2707964f;

	private LivingEntity targetedEntity;

	@SuppressWarnings("deprecation")
	public Gurty(EntityType<? extends Gurty> type, Level worldIn) {

		super(type, worldIn);
		this.xpReward = 7;
		this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
		this.moveControl = new Gurty.MoveHelperController(this);
		this.maxUpStep = 1.0f;
		this.nestProtectionDistSq = MyConfig.getGurtyNestDistance();
		nestProtectionDistSq = (nestProtectionDistSq * nestProtectionDistSq) + 3;
	}

	@Override
	protected void actuallyHurt(DamageSource source, float damageAmount) {

		if (source.getEntity() != null && source.getEntity() instanceof Mob) {
			damageAmount *= 0.50f; // Gurties take less damage from monsters
		}
		
		if (source.is(DamageTypeTags.IS_PROJECTILE)) {
			damageAmount *= 0.66f; // reduce projectile damage by 33% thick skin
		}
		float baseDefense = MyConfig.getGurtyBaseDefense();
		if (baseDefense < 4.5f) {
			baseDefense = 4.5f;
		}
		if (damageAmount > 4.5) {
			damageAmount -= MyConfig.getGurtyBaseDefense(); // thick leathery skin.
		}

		if (damageAmount > 2.0) {

			if (getESize() >= 0) {
				damageAmount *= 0.93f;
			}
		}

		if (source.is(DamageTypes.FALL)) {
			damageAmount *= 0.5f;
		}
		if (source.is(DamageTypes.SWEET_BERRY_BUSH)) {
			damageAmount = 0.0f; // thick leathery skin.
		}
		if (source.is(DamageTypes.CACTUS)) {
			damageAmount = 0.0f; // thick leathery skin.
			return;
		}
		// resistant to magic and magic thorns
		if (source.is(DamageTypes.MAGIC)) {			
			damageAmount *= 0.66f; // partial magic immunity
		}
		
		if (source.is(DamageTypeTags.BYPASSES_ARMOR)) {
			damageAmount *= 0.66f; // partial magic immunity
		}

		if (source.is(DamageTypes.EXPLOSION)) {
			damageAmount *= 0.1f; // strong explosion resistance
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

	@Override
	public boolean canBeAffected(MobEffectInstance potioneffectIn) {
//		net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(
//				this, potioneffectIn);
//		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
//		if (event.getResult() != net.minecraftforge.eventbus.api.Event.Result.DEFAULT)
//			return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;

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
			removeNest();
			this.remove(RemovalReason.DISCARDED);
		} else if (!this.isPersistenceRequired() && !this.requiresCustomPersistence()) {
			Entity entity = this.level.getNearestPlayer(this, -1.0D);
			Result result = ForgeEventFactory.canEntityDespawn(this, (ServerLevelAccessor) this.getLevel());
			if (result == Result.DENY) {
				noActionTime = 0;
				entity = null;
			} else if (result == Result.ALLOW) {
				removeNest();
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
					removeNest();
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
		return new Gurty.Navigator(this, worldIn);
	}

	@Override
	protected int decreaseAirSupply(int currentAir) {
		return currentAir;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define((EntityDataAccessor<Integer>) Gurty.TARGET_ENTITY, 0);
		this.entityData.define((EntityDataAccessor<Boolean>) Gurty.ANGRY, false);
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
		entityIn.playSound(SoundEvents.GENERIC_EAT, 0.7f, 0.7f);
		entityIn.playSound(SoundEvents.GENERIC_EAT, 0.7f, 0.6f);
		entityIn.playSound(SoundEvents.GENERIC_EAT, 0.7f, 0.8f);
		if (entityIn instanceof LivingEntity) {
			((LivingEntity) entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, 60, 1));
		}
		return super.doHurtTarget(entityIn);
	}

	@Override
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason,
			@Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {

		BlockPos pos = blockPosition();
		setNestPos(pos);
//		Block b = worldIn.getBlockState(pos).getBlock();
		setTravelPos(BlockPos.ZERO);
		int nestCount = 0;
		// Prevent nestSpam
		for (int i = -5; i < 6; i++) {
			for (int j = -5; j < 6; j++) {
				for (int k = -1; k < 2; k++) {
					if (level.getBlockState(pos.west(i).north(j).above(k)).getBlock() == ModBlocks.NEST_BLOCK) {
						nestCount++;
						if (nestCount > 3) {
							break;
						}
					}
				}
			}
		}
		if (nestCount == 0) {
			level.setBlockAndUpdate(pos, ModBlocks.NEST_BLOCK.defaultBlockState());
			this.hasNest = true;
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
		return ModSounds.GURTY_AMBIENT;
	}

	protected SoundEvent getAngrySound() {
		return ModSounds.GURTY_ANGRY;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSounds.GURTY_DEATH;
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
		return ModSounds.GURTY_HURT;
	}

	@Override
	public int getMaxSpawnClusterSize() {
		int max = 1 + (MyConfig.getGurtySpawnCap() / 6);
		return max;
	}

	private BlockPos getNestPos() {
		return this.entityData.get(NEST_POS);
	}

	@Override
	public UUID getPersistentAngerTarget() {
		return angerTarget;
	}

	@Override
	public int getRemainingPersistentAngerTime() {
		return this.angerTime;
	}

	public float getScale() {
		return 1.0f;
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
				.getEntity((int) this.entityData.get((EntityDataAccessor<Integer>) Gurty.TARGET_ENTITY));
		if (targetEntity instanceof LivingEntity) {
			return this.targetedEntity = (LivingEntity) targetEntity;
		}
		return null;
	}

	private BlockPos getTravelPos() {
		return this.entityData.get(TRAVEL_POS);
	}

	public boolean hasTargetedEntity() {
		return (int) this.entityData.get((EntityDataAccessor<Integer>) Gurty.TARGET_ENTITY) != 0;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {

		// prevent bad behavior by other mods.
		if (source == null) {
			return false;
		}
		// prevent bad behavior by other mods.
		if (amount < 0) {
			amount = 0;
		}

		if ((this.level.isClientSide) || (this.isDeadOrDying()) || this.isInvulnerableTo(source)) {
			return false;
		}

		if (source.is(DamageTypes.IN_WALL)) {
			return super.hurt(source, amount);
		}

		if ((amount > 0.0f) && (source.getEntity() != null)) {
			Entity entity = source.getEntity();

			// gurty thorns damage in melee when angry.
			if ((!source.is(DamageTypeTags.IS_PROJECTILE)) && (this.isAngry())) {
				float thornDamage = 1.5f;
				if (entity.level.getDifficulty() == Difficulty.NORMAL) {
					thornDamage = 2.0f;
				} else if (entity.level.getDifficulty() == Difficulty.HARD) {
					thornDamage = 3.0f;
				}
				entity.hurt(entity.level.damageSources().thorns((Entity) this), thornDamage);
			}

			if (entity instanceof LivingEntity) {
				if (entity.level.getDifficulty() != Difficulty.PEACEFUL) {
					setTarget((LivingEntity) entity);
					setTargetedEntity(entity.getId());
				}
			}
			angerTime = (int) level.getGameTime() + ANGER_INTENSE;

		}
		return super.hurt(source, amount);
	}

	public boolean isAngry() {
		return this.entityData.get((EntityDataAccessor<Boolean>) Gurty.ANGRY);
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
//	@Override
//	public void kill() {
//		this.hurt(DamageSource.OUT_OF_WORLD,null,null, Float.MAX_VALUE);
//	}

	@Override
	protected void playStepSound(final BlockPos p_180429_1_, final BlockState p_180429_2_) {
		this.playSound(ModSounds.GURTY_STEP, 0.15f, 1.0f);
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

		this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 2.3D, true));
		this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.2F));
		this.goalSelector.addGoal(3, new Gurty.GoNestGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new Gurty.GoWaterGoal(this, swimSpeedModifier, rndSwimOdds));
		this.goalSelector.addGoal(3, new Gurty.GoLandGoal(this, walkSpeedModifier, rndWalkOdds));
		this.goalSelector.addGoal(4, new Gurty.PanicGoal(this, 1.4D));
		this.goalSelector.addGoal(4, new Gurty.GoWanderGoal(this, 1.4D, 40));
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
			level.setBlockAndUpdate(this.getNestPos(), Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public boolean requiresCustomPersistence() {

		if (this.level instanceof ServerLevel) {
			int gurtyCount = ((ServerLevel) this.level).getEntities(ModEntities.GURTY, (entity) -> true).size();
			if (gurtyCount < 3) {
				return true;
			}
			if (this.isAngry()) {
				return true;
			}
		}
		return super.requiresCustomPersistence();
	}

	public void setAngry(boolean bool) {
		this.entityData.set((EntityDataAccessor<Boolean>) Gurty.ANGRY, bool);
	}

	private void setGoingNest(boolean goNest) {
		this.entityData.set(GOING_NEST, goNest);
	}

	public void setNestPos(BlockPos posNest) {
		this.entityData.set(NEST_POS, posNest);
	}

	@Override
	public void setPersistentAngerTarget(UUID val) {
		this.angerTarget = val;
	}

	@Override
	public void setRemainingPersistentAngerTime(int val) {
		this.angerTime = val;
	}

	@Override
	protected void setRot(float yaw, float pitch) {
		float f = this.yRotO;
		float lerpYaw = Mth.lerp(0.05f, f, yaw);
		super.setRot(yaw, pitch);
	}

	public void setTailHeight(float amt) {
		this.tailHeight = amt;
	}
	
	//
	// GOAL section
	//
	@Override
	public void setTarget(LivingEntity entityIn) {
		if (entityIn == null) {
			this.setTargetedEntity(0);
			setAngry(false);
			this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(21.0F);
		} else {
			this.angerTime = (int) this.level.getGameTime() + ANGER_MILD;
			this.setTargetedEntity(entityIn.getId());
			if (entityIn instanceof ServerPlayer) {
				this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(42.0F);
			}
			setAngry(true);
		}
		super.setTarget(entityIn);
	}

	private void setTargetedEntity(final int targetEntityId) {
		this.entityData.set((EntityDataAccessor<Integer>) Gurty.TARGET_ENTITY, targetEntityId);
	}


	private void setTravelling(boolean bool) {
		this.entityData.set(TRAVELLING, bool);
	}

	private void setTravelPos(BlockPos posTravel) {
		this.entityData.set(TRAVEL_POS, posTravel);
	}

	@Override
	public void startPersistentAngerTimer() {
		this.angerTime = 300; // This should be mild or intense anger. for now 15 seconds

	}

	@Override
	public void travel(Vec3 travelVector) {
		if (this.isEffectiveAi() && this.isInWater()) {
			this.moveRelative(0.15F, travelVector);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
			if (this.getTarget() == null
					&& (!this.isGoingNest() || !this.getNestPos().closerToCenterThan(this.position(), 20.0D))) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
			}
		} else {
			super.travel(travelVector);
		}

	}

}

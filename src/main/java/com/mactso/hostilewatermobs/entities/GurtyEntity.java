package com.mactso.hostilewatermobs.entities;

import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.sound.ModSounds;
import com.mactso.hostilewatermobs.util.TwoGuysLib;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
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
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
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
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

public class GurtyEntity extends WaterMobEntity implements IMob {
	private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.defineId(GurtyEntity.class,
			DataSerializers.INT);
	private static final DataParameter<Boolean> ANGRY = EntityDataManager.defineId(GurtyEntity.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> GOING_NEST = EntityDataManager.defineId(GurtyEntity.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<BlockPos> NEST_POS = EntityDataManager.defineId(GurtyEntity.class,
			DataSerializers.BLOCK_POS);
	private static final DataParameter<BlockPos> TRAVEL_POS = EntityDataManager.defineId(GurtyEntity.class,
			DataSerializers.BLOCK_POS);
	private static final DataParameter<Boolean> TRAVELLING = EntityDataManager.defineId(GurtyEntity.class,
			DataSerializers.BOOLEAN);

	public static final int ANGER_MILD = 300;
	public static final int ANGER_INTENSE = 1200;
	public static final float SIZE = EntityType.PIG.getWidth() * 1.25f;

	private boolean hasNest;
	private int nestProtectionDistSq;
	private long angerTime;
	private long attackedTime;
	protected RandomWalkingGoal wander;
	protected float tailHeight = -0.2707964f;

	private LivingEntity targetedEntity;

	private static final RangedInteger rangedInteger = TickRangeConverter.rangeOfSeconds(20, 39);

	public GurtyEntity(EntityType<? extends GurtyEntity> type, World worldIn) {

		super(type, worldIn);
		this.xpReward = 7;
		this.setPathfindingMalus(PathNodeType.WATER, 0.0f);
		this.moveControl = new GurtyEntity.MoveHelperController(this);
		this.maxUpStep = 1.0f;
		this.nestProtectionDistSq = MyConfig.getGurtyNestDistance();
		nestProtectionDistSq = (nestProtectionDistSq * nestProtectionDistSq) + 3;
	}

	public static AttributeModifierMap.MutableAttribute registerAttributes() {

		return MonsterEntity.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double) 0.26F)
				.add(Attributes.FOLLOW_RANGE, 20.0D)
				.add(Attributes.ATTACK_DAMAGE, 2.5D)
				.add(Attributes.MAX_HEALTH, 5.0D);
	}

	public float getTailHeight() {
		return this.tailHeight;
	}
	
	public void setTailHeight(float amt ) {
		this.tailHeight = amt;
	}
	private BlockPos getNestPos() {
		return this.entityData.get(NEST_POS);
	}

	private boolean isGoingNest() {
		return this.entityData.get(GOING_NEST);
	}

	private BlockPos getTravelPos() {
		return this.entityData.get(TRAVEL_POS);
	}

	public int getESize() {
		return  this.getId()%16-8;
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

	public boolean hasTargetedEntity() {
		return (int) this.entityData.get((DataParameter<Integer>) GurtyEntity.TARGET_ENTITY) != 0;
	}

	public boolean isAngry() {
		return this.entityData.get((DataParameter<Boolean>) GurtyEntity.ANGRY);
	}

	private void setGoingNest(boolean goNest) {
		this.entityData.set(GOING_NEST, goNest);
	}

	public void setNestPos(BlockPos posNest) {
		this.entityData.set(NEST_POS, posNest);
	}


	private void setTravelling(boolean bool) {
		this.entityData.set(TRAVELLING, bool);
	}

	private void setTravelPos(BlockPos posTravel) {
		this.entityData.set(TRAVEL_POS, posTravel);
	}

	public void setAngry(boolean bool) {
		this.entityData.set((DataParameter<Boolean>) GurtyEntity.ANGRY, bool);
	}

	private void setTargetedEntity(final int targetEntityId) {
		this.entityData.set((DataParameter<Integer>) GurtyEntity.TARGET_ENTITY, targetEntityId);
	}

	@Override
	protected void setRot(float yaw, float pitch) {
		float f = this.yRotO;
		float lerpYaw = MathHelper.lerp(0.05f, f, yaw);
		super.setRot(yaw, pitch);
	}

	public static boolean isBubbleColumn(IWorld world, BlockPos pos) {
		return world.getBlockState(pos).is(Blocks.BUBBLE_COLUMN);
	}

	@Override
	protected int getFireImmuneTicks() {
		return 20;
	}

	// handle /kill command
	@Override
	public void kill() {
		this.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
	}

	@Override
	protected void handleAirSupply(int p_209207_1_) {
		// gurty's are amphibians
	}

	public boolean canBreatheUnderwater() {
		return true;
	}

	public float getScale() {
		return 1.0f;
	}

	public static EntitySize getSize() {
		float width = 0.9f;
		float height = 0.6f;
		boolean fixed_size = false;
		return new EntitySize(width, height, fixed_size);
	}

	@Override
	protected void playStepSound(final BlockPos p_180429_1_, final BlockState p_180429_2_) {
		this.playSound(ModSounds.GURTY_STEP, 0.15f, 1.0f);
	}

	protected SoundEvent getAmbientSound() {
		return ModSounds.GURTY_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSounds.GURTY_DEATH;
	}

	protected SoundEvent getAngrySound() {
		return ModSounds.GURTY_ANGRY;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return ModSounds.GURTY_HURT;
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

			// gurty thorns damage in melee when angry.

			if ((!source.isProjectile()) && (this.isAngry())) {
				float thornDamage = 1.5f;
				if (entity.level.getDifficulty() == Difficulty.NORMAL) {
					thornDamage = 2.0f;
				} else if (entity.level.getDifficulty() == Difficulty.HARD) {
					thornDamage = 3.0f;
				}
				entity.hurt(DamageSource.thorns((Entity) this), thornDamage);
			}
			
			setLastHurtByMob((LivingEntity) entity);

			if (entity.level.getDifficulty() != Difficulty.PEACEFUL) {
				setTarget((LivingEntity) entity);
				setTargetedEntity(entity.getId());
			}
			angerTime = level.getGameTime() + ANGER_INTENSE;


		}
		return super.hurt(source, amount);
	}

	@Override
	public boolean doHurtTarget(Entity entityIn) {
		entityIn.playSound(SoundEvents.GENERIC_EAT, 0.7f, 0.7f);
		entityIn.playSound(SoundEvents.GENERIC_EAT, 0.7f, 0.6f);
		entityIn.playSound(SoundEvents.GENERIC_EAT, 0.7f, 0.8f);
        if (entityIn instanceof LivingEntity) {
            ((LivingEntity)entityIn).addEffect(new EffectInstance(Effects.POISON, 60, 1));
        }
		return super.doHurtTarget(entityIn);
	}

	@Override
	protected void actuallyHurt(DamageSource source, float damageAmount) {

		
		if (source.isProjectile()) {
			damageAmount *= 0.66f; // reduce projectile damage by 33% thick skin
		}
		float baseDefense = MyConfig.getGurtyBaseDefense();
		if (baseDefense < 4.5f) {
			baseDefense = 4.5f;
		}
		if (damageAmount > 4.5) {
			damageAmount -= MyConfig.getGurtyBaseDefense();  // thick leathery skin.
		}

		if (damageAmount > 2.0) {
			
			if (getESize() >= 0) {
			damageAmount *= 0.93f;
			}
		}
		int x=3;
		if (source.getMsgId() == DamageSource.FALL.getMsgId()) {
			damageAmount *= 0.5f;
		}
		if (source.getMsgId() == DamageSource.SWEET_BERRY_BUSH.getMsgId()) {
			damageAmount = 0.0f;   // thick leathery skin.
		}
		if (source.getMsgId() == DamageSource.CACTUS.getMsgId()) {
			damageAmount = 0.0f;   // thick leathery skin.
			return;
		}
		// resistant to magic and magic thorns
		if (source.getMsgId() == DamageSource.MAGIC.getMsgId()) { 
			damageAmount *= 0.66f; // partial magic immunity
		}
		if (source.isBypassArmor()) { 
			damageAmount *= 0.66f; // partial magic immunity
		}
		if (source.isExplosion()) {
			damageAmount *= 0.1f; // strong explosion resistance
		}
		super.actuallyHurt(source, damageAmount);
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

	@Override
	public void setTarget(LivingEntity entityIn) {
		if (entityIn == null) {
			this.setTargetedEntity(0);
			setAngry(false);
			this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(21.0F);
		} else {
			this.angerTime = this.level.getGameTime() + ANGER_MILD;
			this.setTargetedEntity(entityIn.getId());
			if (entityIn instanceof ServerPlayerEntity) {
				this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(42.0F);
			}
			setAngry(true);
		}
		super.setTarget(entityIn);
	}

	@Nullable
	private static Vector3d findSolidBlock(EntityType<? extends GurtyEntity> gurtyIn, IWorld world, BlockPos blockPos,
			int maxXZ, int maxY) {
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

	public static boolean canSpawn(EntityType<? extends GurtyEntity> gurtyIn, IWorld worldIn, SpawnReason reason,
			BlockPos pos, Random randomIn) {

		if (worldIn.isClientSide()) {
			return false;
		}

		ServerWorld w = (ServerWorld) worldIn;
		

		if (!w.dimensionType().hasSkyLight()) {
			return false;  // no gurties in dimensions lacking skylight 
		}
//		if (w.getDifficulty() == Difficulty.PEACEFUL)
//			return false;  // if peaceful will not attack player characters

		if (reason == SpawnReason.SPAWN_EGG)
			return true;

//		if (w.getLightValue(pos) > 13) {
//			return false;
//		}

		if (reason == SpawnReason.SPAWNER) {
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

		// gurties require lots of nearby water.
		if (!TwoGuysLib.findWaterBlock(gurtyIn, worldIn, pos, 21, 4, 9)) {
			return false;
		}


		int gurtySpawnChance = MyConfig.getGurtySpawnChance();
		int gurtySpawnCap = MyConfig.getGurtySpawnCap();
		int gurtySpawnRoll = randomIn.nextInt(30);
		int gurtyCount = ((ServerWorld) w).getEntities(ModEntities.GURTY, (entity) -> true).size();

		if (gurtyCount < 7) {
			gurtySpawnRoll = 0;
		}
		
		Biome biome = w.getBiome(pos);
		Category bC = biome.getBiomeCategory();
		if ((bC == Category.OCEAN) || (bC == Category.RIVER) || (bC == Category.SWAMP) || (bC == Category.BEACH)) {
			gurtySpawnChance += 7;
		}

		if ((bC == Category.SWAMP) || (bC == Category.BEACH)) {
			gurtySpawnCap += 7;
		}


//			System.out.println(
//					"Gurty Spawn Cap: " + gurtySpawnCap + " Count : " + gurtyCount + " Chance:" + gurtySpawnChance);


		if (gurtyCount > gurtySpawnCap) {
			return false;
		}

		if (gurtySpawnRoll < gurtySpawnChance) {
			Chunk c = (Chunk) w.getChunk(pos);
			ClassInheritanceMultiMap<Entity>[] aL = c.getEntitySections();
			int height = pos.getY() / 16;
			if (height < 0) {
				height = 0; // cubic chunk
			}
			if (aL[height].find(GurtyEntity.class).size() > 5) {
				return false;
			}

			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("spawn Gurty true at " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean requiresCustomPersistence() {
		
		if (this.level instanceof ServerWorld) {
			int gurtyCount = ((ServerWorld) this.level).getEntities(ModEntities.GURTY, (entity) -> true).size();
			if (gurtyCount < 3) { 
				return true;
			}
			if (this.isAngry()) {
				return true;
			}
		}
		return super.requiresCustomPersistence();
	}

	
	@Override
	public void die(DamageSource cause) {
   	    	removeNest();
		super.die(cause);
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
      net.minecraftforge.eventbus.api.Event.Result result = net.minecraftforge.event.ForgeEventFactory.canEntityDespawn(this);
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
         if (d0 > (double)j && this.removeWhenFarAway(d0)) {
        	 removeNest();
        	 this.remove();
            
         }

         int k = this.getType().getCategory().getNoDespawnDistance();
         int l = k * k;
         if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && d0 > (double)l && this.removeWhenFarAway(d0)) {
            this.remove();
         } else if (d0 < (double)l) {
            this.noActionTime = 0;
         }
      }

   } else {
      this.noActionTime = 0;
   }
}

private void removeNest() {
	if (hasNest) {
		level.setBlockAndUpdate(this.getNestPos(), Blocks.AIR.defaultBlockState());
	}
}
	@Override
	@Nullable
	public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			@Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {

		BlockPos pos = blockPosition();
		setNestPos(pos);
		
		setTravelPos(BlockPos.ZERO);
		int nestCount = 0;
		// Prevent nestSpam
		for (int i = -5; i<6; i++) {
			for (int j = -5; j<6; j++) {
				for (int k = -1; k<2; k++) {
					if (level.getBlockState(pos.west(i).north(j).above(k)).getBlock() == Blocks.CORNFLOWER) {
						nestCount++;
						if (nestCount > 3) {
							break;
						}
					}
				}
			}
		}
		if (nestCount < 3) {
			level.setBlockAndUpdate(pos, Blocks.CORNFLOWER.defaultBlockState());
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

	protected void registerGoals() {
		int rndWalkOdds = 40;
		double walkSpeedModifier = 0.9d;
		int rndSwimOdds = 40;
		double swimSpeedModifier = 1.05d;

		wander = new RandomWalkingGoal((CreatureEntity) this, walkSpeedModifier, rndWalkOdds);

		this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 2.3D, true));
		this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.2F));
		this.goalSelector.addGoal(3, new GurtyEntity.GoNestGoal(this,1.0D));
		this.goalSelector.addGoal(3, new GurtyEntity.GoWaterGoal(this, swimSpeedModifier, rndSwimOdds));
		this.goalSelector.addGoal(3, new GurtyEntity.GoLandGoal(this, walkSpeedModifier, rndWalkOdds));
		this.goalSelector.addGoal(4, new GurtyEntity.PanicGoal(this, 1.4D));
		this.goalSelector.addGoal(4, new GurtyEntity.GoWanderGoal(this, 1.4D, 40));
		this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
		this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 12.0F));
		this.goalSelector.addGoal(6, new SwimGoal(this,60));
		
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
				(Predicate<LivingEntity>) new TargetPredicate(this)));
		
		super.registerGoals();
	}
	@Override
	public int getMaxSpawnClusterSize() {
		int max = 1 + (MyConfig.getGurtySpawnCap()/6);
		return max;
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
				.getEntity((int) this.entityData.get((DataParameter<Integer>) GurtyEntity.TARGET_ENTITY));
		if (targetEntity instanceof LivingEntity) {
			return this.targetedEntity = (LivingEntity) targetEntity;
		}
		return null;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define((DataParameter<Integer>) GurtyEntity.TARGET_ENTITY, 0);
		this.entityData.define((DataParameter<Boolean>) GurtyEntity.ANGRY, false);
		this.entityData.define(NEST_POS, BlockPos.ZERO);

		this.entityData.define(TRAVEL_POS, BlockPos.ZERO);
		this.entityData.define(GOING_NEST, false);
		this.entityData.define(TRAVELLING, false);
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

	static class TargetPredicate implements Predicate<LivingEntity> {
		private final GurtyEntity gurtyEntity;

		public TargetPredicate(GurtyEntity gurtyIn) {
			gurtyEntity = gurtyIn;
		}

		// called to decide if a target in range is valid to attack
		public boolean test(@Nullable LivingEntity entity) {

			// gurty's don't attack each other
			if (entity instanceof GurtyEntity) {
				return false;
			}

			// villagers have secret tricks to make gurty's not want to eat them
			if (entity instanceof VillagerEntity) {
				return false;
			}

			// gurty's don't attack things they can't see unless attacked first.
			if (!gurtyEntity.canSee(entity)) {
				if (entity != gurtyEntity.getKillCredit()) {
					return false;
				}
			}

			// gurty's don't attack players in creative or spectator mode

			if (entity instanceof PlayerEntity) {
				if (((PlayerEntity) entity).isCreative()) {
					return false;
				} else if (((PlayerEntity) entity).isSpectator()) {
					return false;
				}
			}

			boolean validTarget = false;
			// gurty's always take revenge on their attackers, regardless of any other condition
			if (gurtyEntity.getTarget() != null) {
				if (entity == this.gurtyEntity.getKillCredit()) {
					gurtyEntity.setTarget(entity);
					return true;
				}
			}

			// distance to entity.
			int dstToEntitySq = (int) entity.distanceToSqr(gurtyEntity);
			Vector3i nestPos = (Vector3i) gurtyEntity.getNestPos();
			
			// gurty's always attack if entity threatens the nest and gurty is near entity.
			Vector3i entityPosVec = (Vector3i) entity.blockPosition();
			int nestThreatDistance = (int) entityPosVec.distSqr(gurtyEntity.getNestPos());

			// gurty's get angry at creatures near their nest area if the gurty is nearby.
			if ((nestThreatDistance < gurtyEntity.nestProtectionDistSq) && (dstToEntitySq < 121)) {
				gurtyEntity.setTarget(entity);
				return true;
			}
			
			// Don't attack things when too far from nest.
			if ((nestPos.distSqr(gurtyEntity.blockPosition()) > 1600)) {
				gurtyEntity.setTarget(null);
				return false;
			}
			
			World w = entity.getCommandSenderWorld();

			// rarely attack random fish and other creatures in range.
			if (!(entity instanceof PlayerEntity)) {
				if (w.random.nextInt(600) != 100) {
					gurtyEntity.setTarget(null);
					return false;
				}
			}

			// a little less aggressive in swamps
			Biome biome = w.getBiome(gurtyEntity.blockPosition());
			Category bC = biome.getBiomeCategory();			
			if ((bC == Category.SWAMP)) {
				dstToEntitySq += 64;
			}
			
			// less aggressive in light
			int lightLevel = w.getMaxLocalRawBrightness(this.gurtyEntity.blockPosition());
			if (lightLevel > 13) {
				dstToEntitySq += 81;
			}

			if (w.isRaining()) {
				dstToEntitySq *= 0.6f;
			}
			
			if ((w.getFluidState(entity.blockPosition()).is(FluidTags.WATER)) ||
				(w.getFluidState(entity.blockPosition().above()).is(FluidTags.WATER))
					) {
				dstToEntitySq *= 0.75f;
			}

			double followDistance = gurtyEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue();
			int followDistanceSq = (int) (followDistance * followDistance);
			
			// if modified distance to entity > follow distance attribute, don't attack.
 			if (dstToEntitySq > (followDistanceSq)) {
				// But if a player and in range and random playsound (2.5%) then play a warning ambient sound.
				if (entity instanceof PlayerEntity) {
					int playSound = gurtyEntity.random.nextInt(50);

					if ((dstToEntitySq < 900) && (playSound == 21)) {
						w.playSound(null, entity.blockPosition(), ModSounds.GURTY_AMBIENT, SoundCategory.HOSTILE,
								0.35f, 1.0f);
					}
				}
				gurtyEntity.setTarget(null);
				return false;
			}
			
			
			gurtyEntity.setTarget(entity);
			w.playSound(null, gurtyEntity.blockPosition(), ModSounds.GURTY_ANGRY, SoundCategory.HOSTILE, 1.0f, 1.0f);
			return true;
		}
	}

	// Movement and Navigator Section
	// Returns new PathNavigateGround instance
	protected PathNavigator createNavigation(World worldIn) {
		return new GurtyEntity.Navigator(this, worldIn);
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

	static class MoveHelperController extends MovementController {
		private final GurtyEntity gurty;
		private int jumpTimer = 0;

		public MoveHelperController(final GurtyEntity gurtyEntityIn) {
			super(gurtyEntityIn);
			this.gurty = gurtyEntityIn;

		}

		private void updateSpeed() {
			if (gurty.isInWater()) {
				gurty.setDeltaMovement(this.gurty.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
				if (!gurty.getNestPos().closerThan(gurty.position(), 16.0D)) {
					gurty.setSpeed(Math.max(gurty.getSpeed() / 2.0F, 0.11F));
				}
			} else if (gurty.onGround) {
				gurty.setSpeed(Math.max(gurty.getSpeed() / 1.9F, 0.17F));
			}

		}
		
		public void tick() {
			this.updateSpeed();
			
			if (gurty.isAngry()) {
				gurty.setTailHeight(0.77f);
			} else {
				gurty.setTailHeight(-0.27f);
			}

			if (this.operation == MovementController.Action.MOVE_TO && !gurty.getNavigation().isDone()) {
				double dx = this.wantedX - gurty.getX();
				double dy = this.wantedY - gurty.getY();
				double dz = this.wantedZ - gurty.getZ();
				double distance = (double) MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
				dy = dy / distance;
				float f = (float) (MathHelper.atan2(dz, dx) * (double) (180F / (float) Math.PI)) - 90.0F;
				gurty.yRot = this.rotlerp(gurty.yRot, f, 90.0F);
				gurty.yBodyRot = gurty.yRot;
				float f1 = (float) (speedModifier * this.gurty.getAttributeValue(Attributes.MOVEMENT_SPEED));
				gurty.setSpeed(MathHelper.lerp(0.225F, gurty.getSpeed(), f1));
				gurty.setDeltaMovement(gurty.getDeltaMovement().add(0.0D, (double) gurty.getSpeed() * dy * 0.1D, 0.0D));
			} else {
				gurty.setSpeed(0.0F);
			}
		}

	}

	static class GoWanderGoal extends RandomWalkingGoal {
		      private final GurtyEntity gurty;

		      private GoWanderGoal(GurtyEntity gurtyIn, double speedIn, int chance) {
		         super(gurtyIn, speedIn, chance);
		         gurty = gurtyIn;
		      }

		      /**
		       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		       * method as well.
		       */
		      public boolean canUse() {
		         return !gurty.isInWater() && !gurty.isGoingNest()  ? super.canUse() : false;
		      }
	}
	
	static class SwimGoal extends net.minecraft.entity.ai.goal.SwimGoal {
		GurtyEntity gurty;
		int chance;
		SwimGoal(GurtyEntity gurtyIn) {
			super(gurtyIn);
			this.chance = 40;
			this.gurty = gurtyIn;
		}

		SwimGoal(GurtyEntity gurtyIn, int chanceIn) {
			super(gurtyIn);
			this.chance = chanceIn;
			this.gurty = gurtyIn;
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {
			
			if (gurty.random.nextInt(chance) != 0 ){
				return false;
			}
			return true;

		}
	}
	
	static class Navigator extends SwimmerPathNavigator {
		Navigator(GurtyEntity gurty, World worldIn) {
			super(gurty, worldIn);
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
			if (this.mob instanceof GurtyEntity) {
				GurtyEntity gurty = (GurtyEntity) this.mob;
				if (gurty.isTravelling()) {
					return this.level.getBlockState(pos).is(Blocks.WATER);
				}
			}

			return !this.level.getBlockState(pos.below()).isAir();
		}
	}
	
	//
	// GOAL section
	//

	static class GoNestGoal extends Goal {
		private GurtyEntity gurty;
		private double speed;
		private boolean noPath;
		private int timer;

		GoNestGoal(GurtyEntity gurty, double speedIn) {
			this.gurty = gurty;
			this.speed = speedIn;
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean canUse() {
			if (gurty.isNestingTime()) {
				return true;
			}
			if (!gurty.getNestPos().closerThan(gurty.position(), 64.0D)) {
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
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean canContinueToUse() {
			if (this.noPath) {
				return false;
			}
			if (timer > 400) {
				return false;
			}
			return !gurty.getNestPos().closerThan(gurty.position(), 5.0D);
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			BlockPos blockpos = gurty.getNestPos();
			boolean isNearNest = blockpos.closerThan(gurty.position(), 16.0D);
			if (isNearNest) {
				++this.timer;
			}

			if (gurty.getNavigation().isDone()) {
				Vector3d vector3d = Vector3d.atBottomCenterOf(blockpos);
				Vector3d vector3d1 = RandomPositionGenerator.getPosTowards(gurty, 16, 3, vector3d,
						(double) ((float) Math.PI / 10F));
				if (vector3d1 == null) {
					vector3d1 = RandomPositionGenerator.getPosTowards(gurty, 8, 7, vector3d);
				}

				if (vector3d1 != null && !isNearNest
						&& !gurty.level.getBlockState(new BlockPos(vector3d1)).is(Blocks.WATER)) {
					vector3d1 = RandomPositionGenerator.getPosTowards(gurty, 16, 5, vector3d);
				}

				if (vector3d1 == null) {
					this.noPath = true;
					return;
				}

				gurty.getNavigation().moveTo(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
			}

		}
	}


	
	static class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
		PanicGoal(GurtyEntity gurty, double speedIn) {
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

	static class GoLandGoal extends Goal {
		private final GurtyEntity gurty;
		private final double speed;
		private int chance;
		private boolean finished;

		GoLandGoal(GurtyEntity gurtyIn, double speedIn, int chanceIn ) {
			this.gurty = gurtyIn;
			this.speed = speedIn;
			this.chance = chanceIn;
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
			Random random = gurty.random;
			int k = random.nextInt(128) - 64;
			int l = random.nextInt(9) - 4;
			int i1 = random.nextInt(128) - 64;
			if ((double) l + gurty.getY() > (double) (gurty.level.getSeaLevel() - 1)) {
				l = 0;
			}

			BlockPos blockpos = new BlockPos((double) k + gurty.getX(), (double) l + gurty.getY(),
					(double) i1 + gurty.getZ());
			gurty.setTravelPos(blockpos);
			gurty.setTravelling(true);
			this.finished = false;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			if (gurty.getNavigation().isDone()) {
				Vector3d vector3d = Vector3d.atBottomCenterOf(gurty.getTravelPos());
				Vector3d vector3d1 = RandomPositionGenerator.getPosTowards(gurty, 16, 3, vector3d,
						(double) ((float) Math.PI / 10F));
				if (vector3d1 == null) {
					vector3d1 = RandomPositionGenerator.getPosTowards(gurty, 8, 7, vector3d);
				}

				if (vector3d1 != null) {
					int i = MathHelper.floor(vector3d1.x);
					int j = MathHelper.floor(vector3d1.z);
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

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean canContinueToUse() {
			return !gurty.getNavigation().isDone() && !this.finished && !gurty.isGoingNest();
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void stop() {
			gurty.setTravelling(false);
			super.stop();
		}
	}
	
	static class GoWaterGoal extends MoveToBlockGoal {
		private final GurtyEntity gurty;
		private int chance;

		private GoWaterGoal(GurtyEntity gurtyIn, double speedIn, int chanceIn) {
			super(gurtyIn, speedIn, 24);
			gurty = gurtyIn;
			chance = chanceIn;
			this.verticalSearchStart = -1;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean canContinueToUse() {
			return !gurty.isInWater() && this.tryTicks <= 1200
					&& this.isValidTarget(gurty.level, this.blockPos);
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

		public boolean shouldRecalculatePath() {
			return this.tryTicks % 80 == 0;
		}

		/**
		 * Return true to set given position as destination
		 */
		protected boolean isValidTarget(IWorldReader worldIn, BlockPos pos) {
			return worldIn.getBlockState(pos).is(Blocks.WATER);
		}
	}

}

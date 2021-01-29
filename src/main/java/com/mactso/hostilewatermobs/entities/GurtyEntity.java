package com.mactso.hostilewatermobs.entities;

import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.sound.ModSounds;

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
	private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(GurtyEntity.class,
			DataSerializers.VARINT);
	private static final DataParameter<Boolean> ANGRY = EntityDataManager.createKey(GurtyEntity.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> GOING_NEST = EntityDataManager.createKey(GurtyEntity.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<BlockPos> NEST_POS = EntityDataManager.createKey(GurtyEntity.class,
			DataSerializers.BLOCK_POS);
	private static final DataParameter<BlockPos> TRAVEL_POS = EntityDataManager.createKey(GurtyEntity.class,
			DataSerializers.BLOCK_POS);
	private static final DataParameter<Boolean> TRAVELLING = EntityDataManager.createKey(GurtyEntity.class,
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

	private static final RangedInteger rangedInteger = TickRangeConverter.convertRange(20, 39);

	public GurtyEntity(EntityType<? extends GurtyEntity> type, World worldIn) {

		super(type, worldIn);
		this.experienceValue = 7;
		this.setPathPriority(PathNodeType.WATER, 0.0f);
		this.moveController = new GurtyEntity.MoveHelperController(this);
		this.stepHeight = 1.0f;
		this.nestProtectionDistSq = MyConfig.getGurtyNestDistance();
		nestProtectionDistSq = (nestProtectionDistSq * nestProtectionDistSq) + 3;
	}

	public static AttributeModifierMap.MutableAttribute registerAttributes() {

		return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MOVEMENT_SPEED, (double) 0.26F)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 20.0D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.5D)
				.createMutableAttribute(Attributes.MAX_HEALTH, 5.0D);
	}

	public float getTailHeight() {
		return this.tailHeight;
	}
	
	public void setTailHeight(float amt ) {
		this.tailHeight = amt;
	}
	private BlockPos getNestPos() {
		return this.dataManager.get(NEST_POS);
	}

	private boolean isGoingNest() {
		return this.dataManager.get(GOING_NEST);
	}

	private BlockPos getTravelPos() {
		return this.dataManager.get(TRAVEL_POS);
	}

	public int getESize() {
		return  this.getEntityId()%16-8;
	}
	
	protected boolean isNestingTime() {
		long time = this.world.getDayTime() % 24000;
		if ((time > 11000 && time < 11250) || (time > 250 && time < 500)) {
			return true;
		}
		return false;
	}

	private boolean isTravelling() {
		return this.dataManager.get(TRAVELLING);
	}

	public boolean hasTargetedEntity() {
		return (int) this.dataManager.get((DataParameter<Integer>) GurtyEntity.TARGET_ENTITY) != 0;
	}

	public boolean isAngry() {
		return this.dataManager.get((DataParameter<Boolean>) GurtyEntity.ANGRY);
	}

	private void setGoingNest(boolean goNest) {
		this.dataManager.set(GOING_NEST, goNest);
	}

	public void setNestPos(BlockPos posNest) {
		this.dataManager.set(NEST_POS, posNest);
	}


	private void setTravelling(boolean bool) {
		this.dataManager.set(TRAVELLING, bool);
	}

	private void setTravelPos(BlockPos posTravel) {
		this.dataManager.set(TRAVEL_POS, posTravel);
	}

	public void setAngry(boolean bool) {
		this.dataManager.set((DataParameter<Boolean>) GurtyEntity.ANGRY, bool);
	}

	private void setTargetedEntity(final int targetEntityId) {
		this.dataManager.set((DataParameter<Integer>) GurtyEntity.TARGET_ENTITY, targetEntityId);
	}

	@Override
	protected void setRotation(float yaw, float pitch) {
		float f = this.prevRotationYaw;
		float lerpYaw = MathHelper.lerp(0.05f, f, yaw);
		super.setRotation(yaw, pitch);
	}

	public static boolean isBubbleColumn(IWorld world, BlockPos pos) {
		return world.getBlockState(pos).isIn(Blocks.BUBBLE_COLUMN);
	}

	@Override
	protected int getFireImmuneTicks() {
		return 20;
	}

	// handle /kill command
	@Override
	public void onKillCommand() {
		this.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
	}

	@Override
	protected void updateAir(int p_209207_1_) {
		// gurty's are amphibians
	}

	public boolean canBreatheUnderwater() {
		return true;
	}

	public float getRenderScale() {
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
	public boolean attackEntityFrom(DamageSource source, float amount) {

		if ((this.world.isRemote) || (this.getShouldBeDead()) || this.isInvulnerableTo(source)) {
			return false;
		}

		if (source == DamageSource.OUT_OF_WORLD) {
			return super.attackEntityFrom(source, amount);
		}


		if ((amount > 0.0f) && (source.getTrueSource() != null)) {
			Entity entity = source.getTrueSource();

			// gurty thorns damage in melee when angry.

			if ((!source.isProjectile()) && (this.isAngry())) {
				float thornDamage = 1.5f;
				if (entity.world.getDifficulty() == Difficulty.NORMAL) {
					thornDamage = 2.0f;
				} else if (entity.world.getDifficulty() == Difficulty.HARD) {
					thornDamage = 3.0f;
				}
				entity.attackEntityFrom(DamageSource.causeThornsDamage((Entity) this), thornDamage);
			}
			
			setRevengeTarget((LivingEntity) entity);

			if (entity.world.getDifficulty() != Difficulty.PEACEFUL) {
				setAttackTarget((LivingEntity) entity);
				setTargetedEntity(entity.getEntityId());
			}
			angerTime = world.getGameTime() + ANGER_INTENSE;


		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		entityIn.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.7f, 0.7f);
		entityIn.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.7f, 0.6f);
		entityIn.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.7f, 0.8f);
        if (entityIn instanceof LivingEntity) {
            ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.POISON, 60, 1));
        }
		return super.attackEntityAsMob(entityIn);
	}

	@Override
	protected void damageEntity(DamageSource source, float damageAmount) {

		
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
		if (source.getDamageType() == DamageSource.FALL.getDamageType()) {
			damageAmount *= 0.5f;
		}
		if (source.getDamageType() == DamageSource.SWEET_BERRY_BUSH.getDamageType()) {
			damageAmount = 0.0f;   // thick leathery skin.
		}
		if (source.getDamageType() == DamageSource.CACTUS.getDamageType()) {
			damageAmount = 0.0f;   // thick leathery skin.
			return;
		}
		// resistant to magic and magic thorns
		if (source.getDamageType() == DamageSource.MAGIC.getDamageType()) { 
			damageAmount *= 0.66f; // partial magic immunity
		}
		if (source.isUnblockable()) { 
			damageAmount *= 0.66f; // partial magic immunity
		}
		if (source.isExplosion()) {
			damageAmount *= 0.1f; // strong explosion resistance
		}
		super.damageEntity(source, damageAmount);
	}

	@Override
	public boolean isPotionApplicable(EffectInstance potioneffectIn) {
		net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(
				this, potioneffectIn);
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() != net.minecraftforge.eventbus.api.Event.Result.DEFAULT)
			return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;

		Effect effect = potioneffectIn.getPotion();
		if (effect == Effects.POISON) {
			return false;
		}

		return true;
	}

	@Override
	public void setAttackTarget(LivingEntity entityIn) {
		if (entityIn == null) {
			this.setTargetedEntity(0);
			setAngry(false);
			this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(21.0F);
		} else {
			this.angerTime = this.world.getGameTime() + ANGER_MILD;
			this.setTargetedEntity(entityIn.getEntityId());
			if (entityIn instanceof ServerPlayerEntity) {
				this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(42.0F);
			}
			setAngry(true);
		}
		super.setAttackTarget(entityIn);
	}

	@Nullable
	private static Vector3d findWaterBlock(EntityType<? extends GurtyEntity> gurtyIn, IWorld world, BlockPos blockPos,
			int maxXZ, int maxY) {
		Random rand = world.getRandom();
		for (int i = 0; i < 19; ++i) {
			int xD = rand.nextInt(maxXZ + maxXZ) - maxXZ;
			int zD = rand.nextInt(maxXZ + maxXZ) - maxXZ;
			int yD = rand.nextInt(maxY + maxY) - maxY;
			if (blockPos.getY() + yD > 0 && blockPos.getY() + yD < 254) {
				if (world.hasWater(blockPos)) {
					return Vector3d.copyCenteredHorizontally(blockPos.east(xD).up(yD).west(zD));
				}
			}
		}
		return null;
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
					return Vector3d.copyCenteredHorizontally(blockPos.east(xD).up(yD).west(zD));
				}
			}
		}
		return null;
	}

	public static boolean canSpawn(EntityType<? extends GurtyEntity> gurtyIn, IWorld worldIn, SpawnReason reason,
			BlockPos pos, Random randomIn) {

		if (worldIn.isRemote()) {
			return false;
		}

		ServerWorld w = (ServerWorld) worldIn;

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

		if (!w.getBlockState(pos.down()).getMaterial().isSolid()) {
			return false;
		}

		if (isBubbleColumn(w, pos)) {
			return false;
		}

		// gurties below sea level require nearby water.
		if (pos.getY()+1 < w.getSeaLevel()) {
			if (findWaterBlock(gurtyIn, w, pos, 21, 4) == null) {
				return false;
			}
		}

		int gurtySpawnChance = MyConfig.getGurtySpawnChance();
		int gurtySpawnCap = MyConfig.getGurtySpawnCap();
		int gurtySpawnRoll = randomIn.nextInt(30);
		int gurtyCount = ((ServerWorld) w).getEntities(ModEntities.GURTY, (entity) -> true).size();

		if (gurtyCount < 7) {
			gurtySpawnRoll = 0;
		}
		
		Biome biome = w.getBiome(pos);
		Category bC = biome.getCategory();
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
			ClassInheritanceMultiMap<Entity>[] aL = c.getEntityLists();
			int height = pos.getY() / 16;
			if (height < 0) {
				height = 0; // cubic chunk
			}
			if (aL[height].getByClass(GurtyEntity.class).size() > 5) {
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
	public boolean preventDespawn() {
		
		if (this.world instanceof ServerWorld) {
			int gurtyCount = ((ServerWorld) this.world).getEntities(ModEntities.GURTY, (entity) -> true).size();
			if (gurtyCount < 3) { 
				return true;
			}
			if (this.isAngry()) {
				return true;
			}
		}
		return super.preventDespawn();
	}

	
	@Override
	public void onDeath(DamageSource cause) {
   	    	removeNest();
		super.onDeath(cause);
	}
/**
 * Makes the entity despawn if requirements are reached
 */
	@Override
	public void checkDespawn() {
   if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.isDespawnPeaceful()) {
      this.remove();
   } else if (!this.isNoDespawnRequired() && !this.preventDespawn()) {
      Entity entity = this.world.getClosestPlayer(this, -1.0D);
      net.minecraftforge.eventbus.api.Event.Result result = net.minecraftforge.event.ForgeEventFactory.canEntityDespawn(this);
      if (result == net.minecraftforge.eventbus.api.Event.Result.DENY) {
         idleTime = 0;
         entity = null;
      } else if (result == net.minecraftforge.eventbus.api.Event.Result.ALLOW) {
         this.remove();
         entity = null;
      }
      if (entity != null) {
         double d0 = entity.getDistanceSq(this);
         int i = this.getType().getClassification().getInstantDespawnDistance();
         int j = i * i;
         if (d0 > (double)j && this.canDespawn(d0)) {
        	 removeNest();
        	 this.remove();
            
         }

         int k = this.getType().getClassification().getRandomDespawnDistance();
         int l = k * k;
         if (this.idleTime > 600 && this.rand.nextInt(800) == 0 && d0 > (double)l && this.canDespawn(d0)) {
            this.remove();
         } else if (d0 < (double)l) {
            this.idleTime = 0;
         }
      }

   } else {
      this.idleTime = 0;
   }
}

private void removeNest() {
	if (hasNest) {
		world.setBlockState(this.getNestPos(), Blocks.AIR.getDefaultState());
	}
}
	@Override
	@Nullable
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			@Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {

		BlockPos pos = getPosition();
		setNestPos(pos);
		
		setTravelPos(BlockPos.ZERO);
		int nestCount = 0;
		// Prevent nestSpam
		for (int i = -5; i<6; i++) {
			for (int j = -5; j<6; j++) {
				for (int k = -1; k<2; k++) {
					if (world.getBlockState(pos.west(i).north(j).up(k)).getBlock() == Blocks.CORNFLOWER) {
						nestCount++;
						if (nestCount > 3) {
							break;
						}
					}
				}
			}
		}
		if (nestCount < 3) {
			world.setBlockState(pos, Blocks.CORNFLOWER.getDefaultState());
			this.hasNest = true;
		}

		setHealth(getMaxHealth() + MyConfig.getGurtyBaseHitPoints());
		
		if (difficultyIn.getDifficulty() == Difficulty.HARD) {
			float hardHealth = getMaxHealth() + 3.0f;
			setHealth(hardHealth);
			getAttribute(Attributes.ATTACK_DAMAGE)
					.applyNonPersistentModifier(new AttributeModifier("difficulty", 0.5, Operation.ADDITION));

		}

		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
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
		
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setCallsForHelp());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
				(Predicate<LivingEntity>) new TargetPredicate(this)));
		
		super.registerGoals();
	}
	@Override
	public int getMaxSpawnedInChunk() {
		int max = 1 + (MyConfig.getGurtySpawnCap()/6);
		return max;
	}
	@Nullable
	public LivingEntity getTargetedEntity() {
		if (!this.hasTargetedEntity()) {
			return null;
		}
		if (!this.world.isRemote) {
			return this.getAttackTarget();
		}
		if (this.targetedEntity != null) {
			return this.targetedEntity;
		}
		final Entity targetEntity = this.world
				.getEntityByID((int) this.dataManager.get((DataParameter<Integer>) GurtyEntity.TARGET_ENTITY));
		if (targetEntity instanceof LivingEntity) {
			return this.targetedEntity = (LivingEntity) targetEntity;
		}
		return null;
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register((DataParameter<Integer>) GurtyEntity.TARGET_ENTITY, 0);
		this.dataManager.register((DataParameter<Boolean>) GurtyEntity.ANGRY, false);
		this.dataManager.register(NEST_POS, BlockPos.ZERO);

		this.dataManager.register(TRAVEL_POS, BlockPos.ZERO);
		this.dataManager.register(GOING_NEST, false);
		this.dataManager.register(TRAVELLING, false);
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("NestPosX", this.getNestPos().getX());
		compound.putInt("NestPosY", this.getNestPos().getY());
		compound.putInt("NestPosZ", this.getNestPos().getZ());
		compound.putInt("TravelPosX", this.getTravelPos().getX());
		compound.putInt("TravelPosY", this.getTravelPos().getY());
		compound.putInt("TravelPosZ", this.getTravelPos().getZ());
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		int i = compound.getInt("NestPosX");
		int j = compound.getInt("NestPosY");
		int k = compound.getInt("NestPosZ");
		this.setNestPos(new BlockPos(i, j, k));
		super.readAdditional(compound);
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
			if (!gurtyEntity.canEntityBeSeen(entity)) {
				if (entity != gurtyEntity.getAttackingEntity()) {
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
			if (gurtyEntity.getAttackTarget() != null) {
				if (entity == this.gurtyEntity.getAttackingEntity()) {
					gurtyEntity.setAttackTarget(entity);
					return true;
				}
			}

			// distance to entity.
			int dstToEntitySq = (int) entity.getDistanceSq(gurtyEntity);
			Vector3i nestPos = (Vector3i) gurtyEntity.getNestPos();
			
			// gurty's always attack if entity threatens the nest and gurty is near entity.
			Vector3i entityPosVec = (Vector3i) entity.getPosition();
			int nestThreatDistance = (int) entityPosVec.distanceSq(gurtyEntity.getNestPos());

			// gurty's get angry at creatures near their nest area if the gurty is nearby.
			if ((nestThreatDistance < gurtyEntity.nestProtectionDistSq) && (dstToEntitySq < 121)) {
				gurtyEntity.setAttackTarget(entity);
				return true;
			}
			
			// Don't attack things when too far from nest.
			if ((nestPos.distanceSq(gurtyEntity.getPosition()) > 1600)) {
				gurtyEntity.setAttackTarget(null);
				return false;
			}
			
			World w = entity.getEntityWorld();

			// rarely attack random fish and other creatures in range.
			if (!(entity instanceof PlayerEntity)) {
				if (w.rand.nextInt(600) != 100) {
					gurtyEntity.setAttackTarget(null);
					return false;
				}
			}

			// a little less aggressive in swamps
			Biome biome = w.getBiome(gurtyEntity.getPosition());
			Category bC = biome.getCategory();			
			if ((bC == Category.SWAMP)) {
				dstToEntitySq += 64;
			}
			
			// less aggressive in light
			int lightLevel = w.getLight(this.gurtyEntity.getPosition());
			if (lightLevel > 13) {
				dstToEntitySq += 81;
			}

			if (w.isRaining()) {
				dstToEntitySq *= 0.6f;
			}
			
			if ((w.getFluidState(entity.getPosition()).isTagged(FluidTags.WATER)) ||
				(w.getFluidState(entity.getPosition().up()).isTagged(FluidTags.WATER))
					) {
				dstToEntitySq *= 0.75f;
			}

			double followDistance = gurtyEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue();
			int followDistanceSq = (int) (followDistance * followDistance);
			
			// if modified distance to entity > follow distance attribute, don't attack.
 			if (dstToEntitySq > (followDistanceSq)) {
				// But if a player and in range and random playsound (2.5%) then play a warning ambient sound.
				if (entity instanceof PlayerEntity) {
					int playSound = gurtyEntity.rand.nextInt(50);

					if ((dstToEntitySq < 900) && (playSound == 21)) {
						w.playSound(null, entity.getPosition(), ModSounds.GURTY_AMBIENT, SoundCategory.HOSTILE,
								0.35f, 1.0f);
					}
				}
				gurtyEntity.setAttackTarget(null);
				return false;
			}
			
			
			gurtyEntity.setAttackTarget(entity);
			w.playSound(null, gurtyEntity.getPosition(), ModSounds.GURTY_ANGRY, SoundCategory.HOSTILE, 1.0f, 1.0f);
			return true;
		}
	}

	// Movement and Navigator Section
	// Returns new PathNavigateGround instance
	protected PathNavigator createNavigator(World worldIn) {
		return new GurtyEntity.Navigator(this, worldIn);
	}

	@Override
	public void travel(Vector3d travelVector) {
		if (this.isServerWorld() && this.isInWater()) {
			this.moveRelative(0.15F, travelVector);
			this.move(MoverType.SELF, this.getMotion());
			this.setMotion(this.getMotion().scale(0.9D));
			if (this.getAttackTarget() == null
					&& (!this.isGoingNest() || !this.getNestPos().withinDistance(this.getPositionVec(), 20.0D))) {
				this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
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
				gurty.setMotion(this.gurty.getMotion().add(0.0D, 0.005D, 0.0D));
				if (!gurty.getNestPos().withinDistance(gurty.getPositionVec(), 16.0D)) {
					gurty.setAIMoveSpeed(Math.max(gurty.getAIMoveSpeed() / 2.0F, 0.11F));
				}
			} else if (gurty.onGround) {
				gurty.setAIMoveSpeed(Math.max(gurty.getAIMoveSpeed() / 1.9F, 0.17F));
			}

		}
		
		public void tick() {
			this.updateSpeed();
			
			if (gurty.isAngry()) {
				gurty.setTailHeight(0.77f);
			} else {
				gurty.setTailHeight(-0.27f);
			}

			if (this.action == MovementController.Action.MOVE_TO && !gurty.getNavigator().noPath()) {
				double dx = this.posX - gurty.getPosX();
				double dy = this.posY - gurty.getPosY();
				double dz = this.posZ - gurty.getPosZ();
				double distance = (double) MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
				dy = dy / distance;
				float f = (float) (MathHelper.atan2(dz, dx) * (double) (180F / (float) Math.PI)) - 90.0F;
				gurty.rotationYaw = this.limitAngle(gurty.rotationYaw, f, 90.0F);
				gurty.renderYawOffset = gurty.rotationYaw;
				float f1 = (float) (speed * this.gurty.getAttributeValue(Attributes.MOVEMENT_SPEED));
				gurty.setAIMoveSpeed(MathHelper.lerp(0.225F, gurty.getAIMoveSpeed(), f1));
				gurty.setMotion(gurty.getMotion().add(0.0D, (double) gurty.getAIMoveSpeed() * dy * 0.1D, 0.0D));
			} else {
				gurty.setAIMoveSpeed(0.0F);
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
		      public boolean shouldExecute() {
		         return !gurty.isInWater() && !gurty.isGoingNest()  ? super.shouldExecute() : false;
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
		public boolean shouldExecute() {
			
			if (gurty.rand.nextInt(chance) != 0 ){
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
		protected boolean canNavigate() {
			return true;
		}

		protected PathFinder getPathFinder(int p_179679_1_) {
			this.nodeProcessor = new WalkAndSwimNodeProcessor();
			return new PathFinder(this.nodeProcessor, p_179679_1_);
		}

		public boolean canEntityStandOnPos(BlockPos pos) {
			if (this.entity instanceof GurtyEntity) {
				GurtyEntity gurty = (GurtyEntity) this.entity;
				if (gurty.isTravelling()) {
					return this.world.getBlockState(pos).isIn(Blocks.WATER);
				}
			}

			return !this.world.getBlockState(pos.down()).isAir();
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
		public boolean shouldExecute() {
			if (gurty.isNestingTime()) {
				return true;
			}
			if (!gurty.getNestPos().withinDistance(gurty.getPositionVec(), 64.0D)) {
				return true;
			}
			return false;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			gurty.setGoingNest(true);
			this.noPath = false;
			this.timer = 0;
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void resetTask() {
			gurty.setGoingNest(false);
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			if (this.noPath) {
				return false;
			}
			if (timer > 400) {
				return false;
			}
			return !gurty.getNestPos().withinDistance(gurty.getPositionVec(), 5.0D);
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			BlockPos blockpos = gurty.getNestPos();
			boolean isNearNest = blockpos.withinDistance(gurty.getPositionVec(), 16.0D);
			if (isNearNest) {
				++this.timer;
			}

			if (gurty.getNavigator().noPath()) {
				Vector3d vector3d = Vector3d.copyCenteredHorizontally(blockpos);
				Vector3d vector3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(gurty, 16, 3, vector3d,
						(double) ((float) Math.PI / 10F));
				if (vector3d1 == null) {
					vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(gurty, 8, 7, vector3d);
				}

				if (vector3d1 != null && !isNearNest
						&& !gurty.world.getBlockState(new BlockPos(vector3d1)).isIn(Blocks.WATER)) {
					vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(gurty, 16, 5, vector3d);
				}

				if (vector3d1 == null) {
					this.noPath = true;
					return;
				}

				gurty.getNavigator().tryMoveToXYZ(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
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
		public boolean shouldExecute() {

			if (creature.getRevengeTarget() == null) {
				return false;
			}
			if (creature.world.getDifficulty() == Difficulty.PEACEFUL) {
				return false;
			}
			BlockPos blockpos = this.getRandPos(this.creature.world, this.creature, 7, 4);
			if (blockpos != null) {
				this.randPosX = (double) blockpos.getX();
				this.randPosY = (double) blockpos.getY();
				this.randPosZ = (double) blockpos.getZ();
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
		public boolean shouldExecute() {
			if (gurty.getRNG().nextInt(this.chance) != 0) {
	               return false;
	        }
			if (!gurty.world.isDaytime()) {
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
		public void startExecuting() {
			Random random = gurty.rand;
			int k = random.nextInt(128) - 64;
			int l = random.nextInt(9) - 4;
			int i1 = random.nextInt(128) - 64;
			if ((double) l + gurty.getPosY() > (double) (gurty.world.getSeaLevel() - 1)) {
				l = 0;
			}

			BlockPos blockpos = new BlockPos((double) k + gurty.getPosX(), (double) l + gurty.getPosY(),
					(double) i1 + gurty.getPosZ());
			gurty.setTravelPos(blockpos);
			gurty.setTravelling(true);
			this.finished = false;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			if (gurty.getNavigator().noPath()) {
				Vector3d vector3d = Vector3d.copyCenteredHorizontally(gurty.getTravelPos());
				Vector3d vector3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(gurty, 16, 3, vector3d,
						(double) ((float) Math.PI / 10F));
				if (vector3d1 == null) {
					vector3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(gurty, 8, 7, vector3d);
				}

				if (vector3d1 != null) {
					int i = MathHelper.floor(vector3d1.x);
					int j = MathHelper.floor(vector3d1.z);
					int k = 34;
					if (!gurty.world.isAreaLoaded(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
						vector3d1 = null;
					}
				}

				if (vector3d1 == null) {
					this.finished = true;
					return;
				}

				gurty.getNavigator().tryMoveToXYZ(vector3d1.x, vector3d1.y, vector3d1.z, this.speed);
			}

		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return !gurty.getNavigator().noPath() && !this.finished && !gurty.isGoingNest();
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void resetTask() {
			gurty.setTravelling(false);
			super.resetTask();
		}
	}
	
	static class GoWaterGoal extends MoveToBlockGoal {
		private final GurtyEntity gurty;
		private int chance;

		private GoWaterGoal(GurtyEntity gurtyIn, double speedIn, int chanceIn) {
			super(gurtyIn, speedIn, 24);
			gurty = gurtyIn;
			chance = chanceIn;
			this.field_203112_e = -1;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return !gurty.isInWater() && this.timeoutCounter <= 1200
					&& this.shouldMoveTo(gurty.world, this.destinationBlock);
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean shouldExecute() {
			if (gurty.isGoingNest()) {
				return false;
			}
			if (!gurty.world.isDaytime()) {
				return false;
			}
			if (gurty.rand.nextInt(chance) == 0) {
				return true;
			}
			return false;
		}

		public boolean shouldMove() {
			return this.timeoutCounter % 80 == 0;
		}

		/**
		 * Return true to set given position as destination
		 */
		protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
			return worldIn.getBlockState(pos).isIn(Blocks.WATER);
		}
	}

}

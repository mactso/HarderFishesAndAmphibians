package com.mactso.hostilewatermobs.entities;

import java.util.Random;
import java.util.UUID;
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
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
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
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

public class GurtyEntity extends WaterMobEntity  {
	private boolean swimmingUp;
	private long angerTime;
	protected MeleeAttackGoal myMeleeAttackGoal;
	protected RandomWalkingGoal wander;
	private LivingEntity targetedEntity;
	private boolean clientSideTouchedGround;
	protected final SwimmerPathNavigator waterNavigator;
	protected final GroundPathNavigator groundNavigator;
	public static final int ANGER_MILD = 300;
	public static final int ANGER_INTENSE = 1200;
	public static final float SIZE = EntityType.PIG.getWidth() * 1.15f;
	public static final float LARGE_SIZE = EntityType.PIG.getWidth() * 1.30f;
	private static final DataParameter<Boolean> MOVING = EntityDataManager.createKey(GurtyEntity.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(GurtyEntity.class,
			DataSerializers.VARINT);
	private static final DataParameter<Boolean> ANGRY = EntityDataManager.createKey(GurtyEntity.class,
			DataSerializers.BOOLEAN);

	private static final RangedInteger rangedInteger = TickRangeConverter.convertRange(20, 39);

	public GurtyEntity(EntityType<? extends WaterMobEntity> type, World worldIn) {

		super(type, worldIn);
		this.experienceValue = 7;
		this.setPathPriority(PathNodeType.WATER, 0.0f);
		this.moveController = new MoveHelperController(this);
		this.lookController = new DolphinLookController(this, 10);
		this.waterNavigator = new SwimmerPathNavigator(this, worldIn);
		this.groundNavigator = new GroundPathNavigator(this, worldIn);
		this.stepHeight = 0.7f;


	}

	public boolean isNotColliding(IWorldReader worldIn) {
		return worldIn.checkNoEntityCollision(this);
	}

	public long getAngerTime() {
		return this.angerTime;
	}
	public boolean isAngry() {
//		System.out.println(this.getEntityId() + " time =" + this.angerTime);
		boolean rVal = this.dataManager.get((DataParameter<Boolean>) GurtyEntity.ANGRY);
		return rVal;
	}
	
	@Override
	protected void setRotation(float yaw, float pitch) {
		float f = this.prevRotationYaw;
		float lerpYaw = MathHelper.lerp(0.1f, f, yaw);
		
		if (f != yaw) {
			System.out.println("Gurty: PrevYaw: " + f + " newYaw :" + yaw + " lerpYaw :" + lerpYaw );
		}
		super.setRotation(yaw, pitch);
	}
	
	public static boolean isInBubbleColumn(IWorld world, BlockPos pos) {
		return world.getBlockState(pos).isIn(Blocks.BUBBLE_COLUMN);
	}

	public void setSwimmingUp(boolean b) {
		this.swimmingUp = b;
	}

	@Override
	protected int getFireImmuneTicks() {
		return 10;
	}

	public static boolean canSpawn(EntityType<? extends GurtyEntity> type, IWorld world, SpawnReason reason,
			BlockPos pos, Random randomIn) {

		if (world.getDifficulty() == Difficulty.PEACEFUL)
			return false;

		if (reason == SpawnReason.SPAWN_EGG)
			return true;

//		this might block glass and half slabs... or it might block water too.  test later.
//		if !(canSpawnOn(type, world, reason, pos, randomIn))

		boolean inWater = world.getFluidState(pos).isTagged(FluidTags.WATER)
				|| world.getFluidState(pos.up()).isTagged(FluidTags.WATER);

		if (!inWater) {
			return false;
		}

		if (isInBubbleColumn(world, pos)) {
			return false;
		}

		if (reason == SpawnReason.SPAWNER)
			return true;

		boolean isDark = world.getLight(pos) < 9;
		boolean isDeep = pos.getY() < 30;
		if (isDeep && !isDark) {
			return false;
		}

		int gurtySpawnChance = MyConfig.getGurtySpawnChance();
		int gurtySpawnCap = MyConfig.getGurtySpawnCap();
		int gurtySpawnRoll = randomIn.nextInt(30);

		Biome biome = world.getBiome(pos);
		Category bC = biome.getCategory();
		if (bC == Category.OCEAN) {
			if (world.getLight(pos) > 13) {
				return false;
			}
		}

		if (bC == Category.SWAMP) {
			gurtySpawnChance += 7;
			gurtySpawnCap += 7;
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("spawn swamp slipperyBiter + 7");
			}
		}

		if (world instanceof ServerWorld) {
			int gurtyCount = ((ServerWorld) world).getEntities(ModEntities.GURTY, (entity) -> true).size();
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("SlipperyBiter Count : " + gurtyCount);
			}
			if (gurtyCount > gurtySpawnCap) {
				return false;
			}
		}

		if (MyConfig.getaDebugLevel() > 0) {
			System.out.println("SlipperyBiter Spawn Cap:" + gurtySpawnCap + " Spawn Chance:" + gurtySpawnChance);
		}

		if ((gurtySpawnRoll < gurtySpawnChance) || !(world.canBlockSeeSky(pos))) {
			Chunk c = (Chunk) world.getChunk(pos);
			ClassInheritanceMultiMap<Entity>[] aL = c.getEntityLists();
			int height = pos.getY() / 16;
			if (height < 0)
				height = 0; // cubic chunks
			if (aL[height].getByClass(GurtyEntity.class).size() > 5) {
				return false;
			}

			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("spawn slipperyBiter true at " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
			}
			return true;
		}

		return false;
	}

	@Override
	protected void updateAir(int p_209207_1_) {
		// gurty's are amphibians
	}

	@Override
	public void setHomePosAndDistance(BlockPos pos, int distance) {
		super.setHomePosAndDistance(pos, distance);
	}
	
	@Override  
	@Nullable
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			@Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {

		BlockPos pos = getPosition();
		if (world.getBlockState(pos.down()).getBlock() == Blocks.GRASS_BLOCK) {
			world.setBlockState(pos, Blocks.CORNFLOWER.getDefaultState());
		}
		if (world.getBlockState(pos.down()).getBlock() == Blocks.GRAVEL) {
			world.setBlockState(pos, Blocks.DIRT.getDefaultState());
			world.setBlockState(pos, Blocks.CORNFLOWER.getDefaultState());
		}
		if (world.getBlockState(pos.down()).getBlock() == Blocks.DIRT) {
			world.setBlockState(pos, Blocks.CORNFLOWER.getDefaultState());
		}
		this.setHomePosAndDistance(pos, 37);

		if (difficultyIn.getDifficulty() == Difficulty.HARD) {
				float newHealth = getMaxHealth() + 3.0f;
				this.setHealth(newHealth);
				this.getAttribute(Attributes.ATTACK_DAMAGE)
						.applyNonPersistentModifier(new AttributeModifier("difficulty", 0.5, Operation.ADDITION));
		}



		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	public static EntitySize getSize() {
		float width = 0.8f;
		float height = 0.5f;
		boolean fixed_size = false;
		return new EntitySize(width, height, fixed_size);
	}

	public static AttributeModifierMap.MutableAttribute registerAttributes() {
		return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MOVEMENT_SPEED, (double) 0.25F)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 20.0D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.5D)
				.createMutableAttribute(Attributes.MAX_HEALTH, 11.0D);
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

	public boolean isClientSideTouchedGround() {
		return clientSideTouchedGround;
	}

	public boolean canBreatheUnderwater() {
		return true;
	}

//	@Override
//	public void func_230258_H__() {
//		this.setAngerTime(rangedInteger.getRandomWithinRange(this.rand));
//	}


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

	public boolean hasTargetedEntity() {
		return (int) this.dataManager.get((DataParameter<Integer>) GurtyEntity.TARGET_ENTITY) != 0;
	}

	public boolean isMoving() {
		return (boolean) this.dataManager.get((DataParameter<Boolean>) GurtyEntity.MOVING);
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register((DataParameter<Boolean>) GurtyEntity.MOVING, false);
		this.dataManager.register((DataParameter<Integer>) GurtyEntity.TARGET_ENTITY, 0);
		this.dataManager.register((DataParameter<Boolean>) GurtyEntity.ANGRY,false);
	}

	protected void registerGoals() {
		int rndWalkOdds = 100;
		double walkSpeedModifier = 1.0d;
		int rndSwimOdds = 200;
		double swimSpeedModifier = 1.05d;
		
		this.wander = new RandomWalkingGoal((CreatureEntity)this, walkSpeedModifier, rndWalkOdds);

		this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(1, new GoToNestGoal(this, 1.0F));
		this.goalSelector.addGoal(2, (Goal)this.wander);
		this.goalSelector.addGoal(3, new GoToLandGoal(this, 1.0F));
		this.goalSelector.addGoal(3, new RandomSwimmingGoal(this, swimSpeedModifier, rndSwimOdds));
		this.goalSelector.addGoal(3, new GoToWaterGoal(this, 1.0F));
		this.goalSelector.addGoal(3, new SwimGoal(this));
		this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 12.0F));
		this.goalSelector.addGoal(9, new LookRandomlyGoal(this));

		this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setCallsForHelp());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
				(Predicate<LivingEntity>) new TargetPredicate(this)));
		super.registerGoals();
	}


	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
	    compound.put("HomePos", NBTUtil.writeBlockPos(super.getHomePosition()));
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		if (this.world instanceof ServerWorld) {
			setHomePosAndDistance(NBTUtil.readBlockPos(compound.getCompound("HomePos")), 19);
		}
	}

	private void setMoving(final boolean movingStatus) {
		this.dataManager.set((DataParameter<Boolean>) GurtyEntity.MOVING, movingStatus);
	}

	private void setTargetedEntity(final int targetEntityId) {
		this.dataManager.set((DataParameter<Integer>) GurtyEntity.TARGET_ENTITY, targetEntityId);
	}

   /**
    * Called by the /kill command.
    */
	@Override
   public void onKillCommand() {
      this.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
   }

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.world.isRemote) {
			return false;
		} else if (this.getShouldBeDead()) {
			return false;
		} else if (this.isInvulnerableTo(source)) {
			return false;
		} else if (source == DamageSource.OUT_OF_WORLD) {
			return super.attackEntityFrom(source, amount);
		}
		else {
			if (this.isSleeping()) {
				this.wakeUp();
			}
			this.idleTime = 0;
			System.out.println("Gurty #"+this.getEntityId()+"attacked");
			Entity entity = source.getTrueSource();
			if (amount > 0.0F && entity != null && entity instanceof ServerPlayerEntity) {
				PlayerEntity player = (PlayerEntity) entity;
				ModifiableAttributeInstance attr = this.getAttribute(Attributes.FOLLOW_RANGE);
				double new_range = 48.0;
				if (new_range > attr.getBaseValue())
					attr.setBaseValue(new_range);
				this.setRevengeTarget(player);
				this.setAttackTarget(player);
				this.angerTime = this.world.getGameTime()+ANGER_INTENSE;
				this.setRevengeTarget(player);
			}
			
			// gurty thorns only apply other entities meleeing the gurty and varies with difficulty level
			if (!source.isProjectile()) {
				entity = source.getImmediateSource(); 
				if (entity != null) { 
					if (this.isAngry()) { // surprise attack no thorns.
						if (entity.world.getDifficulty() == Difficulty.EASY) {
							entity.attackEntityFrom(DamageSource.causeThornsDamage((Entity) this), 1.5f);
						} else if (entity.world.getDifficulty() == Difficulty.NORMAL) {
							entity.attackEntityFrom(DamageSource.causeThornsDamage((Entity) this), 2.0f);
						} else if (entity.world.getDifficulty() == Difficulty.HARD) {
							entity.attackEntityFrom(DamageSource.causeThornsDamage((Entity) this), 3.0f);
						}
					}
					
				}
			}

		}
		return super.attackEntityFrom(source, amount);
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		entityIn.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.7f, 0.7f);
		return super.attackEntityAsMob(entityIn);
	}
	
	@Override
	protected void damageEntity(DamageSource source, float damageAmount)
	{
		if (source.isProjectile())
			damageAmount *= 0.66f; // reduce projectile damage by 33% thick skin
		super.damageEntity(source, damageAmount);
	}
	
	@Override
	public void setAttackTarget(LivingEntity entityIn) {
		if (entityIn == null) {
			this.setTargetedEntity(0);
			this.dataManager.set((DataParameter<Boolean>) GurtyEntity.ANGRY, false);
		} else {
			this.angerTime = this.world.getGameTime() + ANGER_MILD;
			this.setTargetedEntity(entityIn.getEntityId());
			this.dataManager.set((DataParameter<Boolean>) GurtyEntity.ANGRY, true);
		}
		super.setAttackTarget(entityIn);
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
				}

				if (((PlayerEntity) entity).isSpectator()) {
					return false;
				}
			}

			boolean validTarget = false;
			// gurty's get angry at things that attacked them regardless of distance
			if (gurtyEntity.getAttackTarget() != null) {
				if (entity == this.gurtyEntity.getAttackingEntity()) {
					validTarget = true;
				} 
			}

			World w = entity.getEntityWorld();
			if ((w.getFluidState(entity.getPosition()).isTagged(FluidTags.WATER))
					|| (w.getFluidState(entity.getPosition().up()).isTagged(FluidTags.WATER))) {
				validTarget = true;
			}



			// distance to entity.

			int distanceSq = (int) entity.getDistanceSq(gurtyEntity);
			double followDistance = gurtyEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue();
			int followDistanceSq = (int) (followDistance * followDistance);
			int playSound = gurtyEntity.rand.nextInt(60);

			// 1 to ~500
			Biome biome = w.getBiome(gurtyEntity.getPosition());
			Category bC = biome.getCategory();

			// a little less aggressive in swamps
			if ((bC == Category.SWAMP)) {
				distanceSq += 64;
			}
			// less aggressive in light
			int lightLevel = w.getLight(this.gurtyEntity.getPosition());
			if (lightLevel > 13) {
				distanceSq += 81;
			}
			// more aggressive in rain
			if (w.isRaining()) {
				validTarget = true;
				distanceSq *= 0.75f;
			}

			if (distanceSq > (followDistanceSq)) {
				if (entity instanceof PlayerEntity) {
					if ((distanceSq < 900) &&  (playSound == 21)) {
						w.playSound(null, gurtyEntity.getPosition(), ModSounds.GURTY_AMBIENT,
									SoundCategory.HOSTILE, 0.9f, 1.0f);
					}
				}
				return false;
			}

			
			// nest distance
			float maxDistance = gurtyEntity.getMaximumHomeDistance();
			Vector3i posVec = (Vector3i) entity.getPosition();
			Vector3i nestVec = (Vector3i) gurtyEntity.getHomePosition();
			int nestDistance = (int) posVec.distanceSq(nestVec);
			// gurty's get angry at creatures near their nest area if the gurty is nearby.
			if ((nestDistance < 49) && (distanceSq < 170)){
				validTarget = true;
			}
			
			if (!validTarget) {
				gurtyEntity.setAttackTarget(null);
				return false;
			}
			
			gurtyEntity.setAttackTarget(entity);
			w.playSound(null, gurtyEntity.getPosition(), ModSounds.GURTY_ANGRY,
					SoundCategory.HOSTILE, 1.0f, 1.0f);
			return true;
		}
	}

	@Override
	public void travel(Vector3d travelVector) {
		if (this.isServerWorld() && this.isInWater()) {
			if (this.getAttackTarget() != null) {
				if (travelVector.length() != 0) {
					int x = 3;
				}
			}
//			float aispeed = this.getAIMoveSpeed();
//			Vector3d thisVmotion = this.getMotion();
//			thisVmotion = this.getMotion().scale(0.5D);
			this.moveRelative(this.getAIMoveSpeed() * 1.0f, travelVector);
			this.move(MoverType.SELF, this.getMotion());
			this.setMotion(this.getMotion().scale(1.0D));
			if (this.getAttackTarget() == null) {
				this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
			}
		} else {
			super.travel(travelVector);
		}

	}

	@Override
	public void livingTick() {

		super.livingTick();
	}


	static class MoveHelperController extends MovementController {
		private final GurtyEntity workGurtyEntity;
		private int jumpTimer = 0;

		// TODO final vs not final?
		public MoveHelperController(final GurtyEntity gurtyEntityIn) {
			super(gurtyEntityIn);
			this.workGurtyEntity = gurtyEntityIn;

		}

		@Override
		public void tick() {

			// mild buoyancy
			double y = workGurtyEntity.getMotion().getY();
			if (this.workGurtyEntity.isInWater()) {
				if (y < 0.003) {
					this.workGurtyEntity.setMotion(this.workGurtyEntity.getMotion().add(0.0D, 0.003D, 0.0D));
				}
			} else {
				if (y > 0) {
					this.workGurtyEntity.setMotion(this.workGurtyEntity.getMotion().add(0.0D, (0-y), 0.0D));
				}
			}
            float f = (float)this.workGurtyEntity.getAttributeValue(Attributes.MOVEMENT_SPEED);
            
            
            if (this.action == MovementController.Action.JUMPING) {
            	jumpTimer++;
            	if (jumpTimer > 60) {
    				this.workGurtyEntity.setMoveVertical(0.0F);
    				jumpTimer = 0;
    				BlockPos tPos = null;
    				if (this.workGurtyEntity.getAttackTarget() !=null) {
    					tPos = this.workGurtyEntity.getAttackTarget().getPosition();
    				}
    				if (tPos != null) {
    					this.setMoveTo(tPos.getX(), tPos.getY(), tPos.getZ()	, 0.21f);
    				}
            	}
            }
            super.tick();
//			if (this.action == MovementController.Action.MOVE_TO && !this.workGurtyEntity.getNavigator().noPath()) {
//				double d0 = this.posX - this.workGurtyEntity.getPosX();
//				double d1 = this.posY - this.workGurtyEntity.getPosY();
//				double d2 = this.posZ - this.workGurtyEntity.getPosZ();
//				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
//				if (d3 < (double) 2.5000003E-7F) {
//					this.mob.setMoveForward(0.0F);
//				} else {
//					float f = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
//					this.workGurtyEntity.rotationYaw = this.limitAngle(this.workGurtyEntity.rotationYaw, f, 10.0F);
//					this.workGurtyEntity.renderYawOffset = this.workGurtyEntity.rotationYaw;
//					this.workGurtyEntity.rotationYawHead = this.workGurtyEntity.rotationYaw;
//					float f1 = (float) (this.speed * this.workGurtyEntity.getAttributeValue(Attributes.MOVEMENT_SPEED));
//					if (this.workGurtyEntity.isInWater()) {
//						this.workGurtyEntity.setAIMoveSpeed(f1 * 0.018F);
//						float f2 = -((float) (MathHelper.atan2(d1, MathHelper.sqrt(d0 * d0 + d2 * d2))
//								* (double) (180F / (float) Math.PI)));
//						f2 = MathHelper.clamp(MathHelper.wrapDegrees(f2), -85.0F, 85.0F);
//						this.workGurtyEntity.rotationPitch = this.limitAngle(this.workGurtyEntity.rotationPitch, f2,
//								5.0F);
//						float f3 = MathHelper.cos(this.workGurtyEntity.rotationPitch * ((float) Math.PI / 180F));
//						float f4 = MathHelper.sin(this.workGurtyEntity.rotationPitch * ((float) Math.PI / 180F));
//						this.workGurtyEntity.moveForward = f3 * f1;
//						this.workGurtyEntity.moveVertical = -f4 * f1;
//					} else {
//						this.workGurtyEntity.setAIMoveSpeed(f1 * 0.2F);
//					}
//
//				}
//			} else {
//				this.workGurtyEntity.setAIMoveSpeed(0.0F);
//				this.workGurtyEntity.setMoveStrafing(0.0F);
//				this.workGurtyEntity.setMoveVertical(0.0F);
//				this.workGurtyEntity.setMoveForward(0.0F);
//			}
		}

		private Vector3d func_207400_b(final Vector3d vectorIn) {
			Vector3d lvt_2_1_ = vectorIn.rotatePitch(this.workGurtyEntity.getPitch((float) 1.0) * 0.017453292f);
			lvt_2_1_ = lvt_2_1_.rotateYaw(-this.workGurtyEntity.prevRenderYawOffset * 0.017453292f);
			return lvt_2_1_;
		}

	}



	
	static class GoToLandGoal extends MoveToBlockGoal {
		private final GurtyEntity gurty;
		static int searchLength;

		public GoToLandGoal(final GurtyEntity gurtyIn, final double movementSpeed) {
			super((CreatureEntity) gurtyIn, movementSpeed, searchLength = 8, 2);
			this.gurty = gurtyIn;
		}

		public boolean shouldExecute() {
			if (gurty.getRNG().nextInt(400) != 111) {
				return false;
			}
			// true if night; wander land at night;
			if (gurty.world.isDaytime()) {
				System.out.println("Land Goal: skip during day.");
				return false;
			}
 			return super.shouldExecute();
		}

		@Override
		protected int getRunDelay(final CreatureEntity c) {
			return 100 + c.getRNG().nextInt(100);
		}

		protected boolean shouldMoveTo(final IWorldReader p_179488_1_, final BlockPos pos) {
			final BlockPos upPos = pos.up();
			return p_179488_1_.isAirBlock(upPos) && p_179488_1_.isAirBlock(upPos.up());
		}
		@Override
		public boolean shouldContinueExecuting() {
			// TODO Auto-generated method stub
			if (gurty.world.isDaytime()) {
 				return false;
			}
//			System.out.println("Gurty continues heading towards land goal.");
			return super.shouldContinueExecuting();
		}

		public void startExecuting() {
			boolean inWater = gurty.world.getFluidState(gurty.getPosition()).isTagged(FluidTags.WATER);
			if (inWater) {
				System.out.println("Gurty in water starts heading to land goal.");
				gurty.setSwimmingUp(true);
				gurty.setMotion(gurty.getMotion().add(0.0,0.003,0)); //xxzzy
				gurty.navigator = (PathNavigator) gurty.waterNavigator;
			} else {
				System.out.println("Gurty on land starts heading to land goal.");
				gurty.navigator = (PathNavigator) gurty.groundNavigator;
			}
			super.startExecuting();
		}

		
		public void resetTask() {
			super.resetTask();
		}

		@Override
		protected boolean searchForDestination() {

			for (int i = 0; i < 10; ++i) {
//				Vector3d lvt_1_1_ = RandomPositionGenerator.findRandomTarget(this.creature, 10, 7);

				int newX = (int) gurty.getPosX() + gurty.rand.nextInt(31) - 15;
				int newZ = (int) gurty.getPosZ() + gurty.rand.nextInt(31) - 15;
				int newY = gurty.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, newX, newZ);
				BlockPos rndPos = new BlockPos(newX, newY, newZ);
				BlockState bS = (gurty.world.getBlockState(rndPos.down()));
				System.out.println("Gurty Block: " + bS.getBlock().getRegistryName());
				if (!bS.isIn(Blocks.WATER)) {
					this.destinationBlock = rndPos;
					return true;
				}
			}

			return false;

		}
	}

	static class GoToWaterGoal extends MoveToBlockGoal {
		private final GurtyEntity gurty;

		public GoToWaterGoal(final GurtyEntity gurtyIn, final double movementSpeed) {
			super((CreatureEntity) gurtyIn, movementSpeed, 8, 2);
			this.gurty = gurtyIn;
		}

		public boolean shouldExecute() {
			if (gurty.getRNG().nextInt(400) != 111) {
				return false;
			}
			// true if night; wander land at night;
			if (!gurty.world.isDaytime()) {
				return false;
			}
			System.out.println("Water Goal: Gurty starts.");
			return super.shouldExecute();
		}

		@Override
		protected int getRunDelay(final CreatureEntity c) {
			return 100 + c.getRNG().nextInt(100);
		}

		protected boolean shouldMoveTo(final IWorldReader p_179488_1_, final BlockPos pos) {
			final BlockPos upPos = pos.up();
			return p_179488_1_.isAirBlock(upPos) && p_179488_1_.isAirBlock(upPos.up());
		}
		@Override
		public boolean shouldContinueExecuting() {
			// TODO Auto-generated method stub
			if (!gurty.world.isDaytime()) {
				return false;
			}
//			System.out.println("Water Goal: Gurty continues.");
			return super.shouldContinueExecuting();
		}
		public void startExecuting() {
			boolean inWater = gurty.world.getFluidState(gurty.getPosition()).isTagged(FluidTags.WATER);
			if (inWater) {
//				System.out.println("Water Goal: Gurty in water goes to water.");
				if (this.destinationBlock.getY() > gurty.getPosY()) {
					gurty.setSwimmingUp(true);
				} else {
					gurty.setSwimmingUp(false);
				}
				gurty.navigator = (PathNavigator) gurty.waterNavigator;
			} else {
				System.out.println("Water Goal: Gurty on land goes to water.");
				gurty.navigator = (PathNavigator) gurty.waterNavigator;
			}
			super.startExecuting();
		}

		public void resetTask() {
			super.resetTask();
		}

		@Override
		protected boolean searchForDestination() {
			for (int i = 0; i < 10; ++i) {
				int newX = (int) gurty.getPosX() + gurty.rand.nextInt(31) - 15;
				int newZ = (int) gurty.getPosZ() + gurty.rand.nextInt(31) - 15;
				int newY = gurty.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, newX, newZ);
				BlockPos rndPos = new BlockPos(newX, newY, newZ);
				BlockState bS = (gurty.world.getBlockState(rndPos.down()));
				System.out.println("Gurty Block: " + bS.getBlock().getRegistryName());
				if (bS.isIn(Blocks.WATER)) {
					System.out.println("Gurty water goal selected:" + rndPos);
					this.destinationBlock = rndPos;
					return true;
				}
			}

			return false;

		}
	}

	
	
	static class GoToNestGoal extends MoveToBlockGoal {
		private final GurtyEntity gurty;

		public GoToNestGoal(final GurtyEntity gurtyIn, final double movementSpeed) {
			super((CreatureEntity) gurtyIn, movementSpeed, 8, 2);
			this.gurty = gurtyIn;
		}

		@Override
		protected int getRunDelay(final CreatureEntity c) {
			return 200 + c.getRNG().nextInt(100);
		}

		public boolean shouldContinueExecuting() {
			return super.shouldContinueExecuting();
		}
		
		public boolean shouldExecute() {
			// true if night; wander land at night;
			if (nestingTime()) {
				System.out.println("Gurty starts heading to nest goal.");
				return true;
			}

			return super.shouldExecute();
		}

		protected boolean nestingTime () {
			long time = gurty.world.getDayTime() % 24000;
			if ((time > 11000 && time < 11500) || (time > 500 && time < 1000)) {
				return true;
			}
			return false;
		}
		protected boolean shouldMoveTo(final IWorldReader p_179488_1_, final BlockPos p_179488_2_) {
			final BlockPos lvt_3_1_ = p_179488_2_.up();
			boolean destNotSolid = 	p_179488_1_.isAirBlock(lvt_3_1_) && p_179488_1_.isAirBlock(lvt_3_1_.up());
			return destNotSolid;
		}

		public void startExecuting() {
			if (gurty.isInWater()) {
				gurty.setSwimmingUp(true);
				gurty.navigator = gurty.waterNavigator;
			} else {
				gurty.navigator = gurty.groundNavigator;
			}
			System.out.println("Goto nest.  Start Executing.");
			// this helps gurty's hop out of the water.
			gurty.setMotion(this.gurty.getMotion().add(0.0D, 0.5D, 0.0D));
			super.startExecuting();
		}

		public void resetTask() {
			super.resetTask();
		}

		@Override
		protected boolean searchForDestination() {
			BlockPos creaturePos = this.gurty.getPosition();
			// return to nest at dusk and dawn.
			if (nestingTime()) {
				this.destinationBlock = this.gurty.getHomePosition();
				return true;
			}
			// check on nest periodically.
			int r = this.gurty.world.getRandom().nextInt(20);
			if (r == 7) {
				this.destinationBlock = this.gurty.getHomePosition();
				return true;
			}
			return false;
		}
	}
}

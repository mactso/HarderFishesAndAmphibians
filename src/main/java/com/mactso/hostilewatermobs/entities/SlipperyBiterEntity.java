package com.mactso.hostilewatermobs.entities;

import java.util.EnumSet;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.FindWaterGoal;
import net.minecraft.entity.ai.goal.FollowBoatGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.Goal.Flag;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundCategory;
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
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

public class SlipperyBiterEntity extends MonsterEntity implements IAngerable {
	

	


	
	private int angerTime;
	private UUID angerTarget;
	protected RandomWalkingGoal wander;
	private LivingEntity targetedEntity;

	public static final float SIZE = EntityType.SALMON.getWidth() * 1.05f;
	private static final DataParameter<Boolean> MOVING =  EntityDataManager.createKey((Class) SlipperyBiterEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey((Class) SlipperyBiterEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> SUB_TYPE = EntityDataManager.createKey(SlipperyBiterEntity.class,
			DataSerializers.VARINT);
	private static int DEFAULT_SLIPPERY_BITER = 0;
    private static final RangedInteger rangedInteger = TickRangeConverter.convertRange(20, 39);
	public static boolean canSpawn(EntityType<? extends SlipperyBiterEntity> type, IWorld world, SpawnReason reason,
			BlockPos pos, Random randomIn) 
	{

		if (world.getDifficulty() == Difficulty.PEACEFUL)
			return false;

		if (reason == SpawnReason.SPAWN_EGG)
			return true;

		boolean inWater = world.getFluidState(pos).isTagged(FluidTags.WATER)
				|| world.getFluidState(pos.up()).isTagged(FluidTags.WATER);
		if (!inWater) {
			return false;
		}

		if (reason == SpawnReason.SPAWNER)
			return true;

		boolean isDark = world.getLight(pos) < 9;
		boolean isDeep = pos.getY() < 30;
		if (isDeep && !isDark) {
			return false;
		}

		int slipperyBiterSpawnChance = MyConfig.getSlipperyBiterSpawnChance();
		int slipperyBiterCap = MyConfig.getSlipperyBiterSpawnCap();
		int slipperyBiterSpawnRoll = randomIn.nextInt(30);

		if (isDeep) {
			slipperyBiterCap += 6;
			slipperyBiterSpawnChance += 9;
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("spawn deep slipperyBiter +9");
			}
		}

		Biome biome = world.getBiome(pos);
		Category bC = biome.getCategory();
		if (bC == Category.OCEAN) {
			if (world.getLight(pos) > 8) {
				return false;
			}
		}

		if (bC == Category.SWAMP) {
			slipperyBiterSpawnChance += 7;
			slipperyBiterCap += 7;
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("spawn swamp slipperyBiter + 7");
			}
		}


		if (world instanceof ServerWorld) {
			int slipperyBiterCount = ((ServerWorld) world).getEntities(ModEntities.SLIPPERY_BITER, (entity) -> true)
					.size();
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("SlipperyBiter Count : " + slipperyBiterCount);
			}
			if (slipperyBiterCount > slipperyBiterCap) {
				return false;
			}
		}

		if (MyConfig.getaDebugLevel() > 0) {
			System.out.println(
					"SlipperyBiter Spawn Cap:" + slipperyBiterCap + " Spawn Chance:" + slipperyBiterSpawnChance);
		}

		if ((slipperyBiterSpawnRoll < slipperyBiterSpawnChance) || !(world.canBlockSeeSky(pos))) {
			Chunk c = (Chunk) world.getChunk(pos);
			ClassInheritanceMultiMap<Entity>[] aL = c.getEntityLists();
			int height = pos.getY() / 16;
			if (height < 0)
				height = 0; // cubic chunks
			if (aL[height].getByClass(SlipperyBiterEntity.class).size() > 5) {
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
	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			ILivingEntityData spawnDataIn, CompoundNBT dataTag) {

		this.dataManager.set(SUB_TYPE, DEFAULT_SLIPPERY_BITER);

		if (difficultyIn.getDifficulty() == Difficulty.HARD) {
			this.getAttribute(Attributes.ATTACK_DAMAGE)
					.applyNonPersistentModifier(new AttributeModifier("difficulty", 0.5, Operation.ADDITION));
		}
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}
	
	public static EntitySize getSize() {
		float width = 0.7f;
		float height = 0.4f;
		boolean fixed_size = false;
		return new EntitySize( width, height, fixed_size );
	}
	
	public static AttributeModifierMap.MutableAttribute registerAttributes() {
		return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MOVEMENT_SPEED, (double) 0.6F)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 24.0D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.5D)
				.createMutableAttribute(Attributes.MAX_HEALTH, 11.0D);
	}


	public SlipperyBiterEntity(EntityType<? extends MonsterEntity>  type, World worldIn) {

		super(type, worldIn);

		this.experienceValue = 7;
		this.setPathPriority(PathNodeType.WATER, 0.0f);
		this.moveController = new MoveHelperController(this);
		if (this.wander != null) {
			this.wander.setExecutionChance(400);
		}
	}
	
	public boolean canBreatheUnderwater() {
		return true;
	}
	
	@Override
	public void func_230258_H__() {
		this.setAngerTime(rangedInteger.getRandomWithinRange(this.rand));
	}
	
	@Override
	public UUID getAngerTarget() {
		return angerTarget;
	}
	
	@Override
	public int getAngerTime() {
		return this.angerTime;
	}


	public int getSubType() {
		return dataManager.get(SUB_TYPE);
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
				.getEntityByID((int) this.dataManager.get((DataParameter<Integer>) SlipperyBiterEntity.TARGET_ENTITY));
		if (targetEntity instanceof LivingEntity) {
			return this.targetedEntity = (LivingEntity) targetEntity;
		}
		return null;
	}
	
	public boolean hasTargetedEntity() {
		return (int) this.dataManager.get((DataParameter<Integer>) SlipperyBiterEntity.TARGET_ENTITY) != 0;
	}
	
	public boolean isMoving() {
		return (boolean) this.dataManager.get((DataParameter<Boolean>) SlipperyBiterEntity.MOVING);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		dataManager.set(SUB_TYPE, (int) compound.getByte("SubType"));
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register((DataParameter<Boolean>) SlipperyBiterEntity.MOVING, false);
		this.dataManager.register((DataParameter<Integer>) SlipperyBiterEntity.TARGET_ENTITY, 0);
		this.dataManager.register((DataParameter<Integer>) SlipperyBiterEntity.SUB_TYPE, DEFAULT_SLIPPERY_BITER);
	}

	protected void registerGoals() {

		this.wander = new RandomWalkingGoal(this, 1.0, 80); // 4 seconds...
		
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
		this.goalSelector.addGoal(0, new FindWaterGoal(this));
//		this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1.0D, 1));
//		this.goalSelector.addGoal(3, new FollowBoatGoal(this));
		this.goalSelector.addGoal(4, new AttackGoal(this));
//		MoveTowardsRestrictionGoal targetEntity = new MoveTowardsRestrictionGoal(this, 1.0D);
//		targetEntity.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
//		this.goalSelector.addGoal(5, targetEntity);
//		this.targetSelector.addGoal(7, new ResetAngerGoal<>(this, true));
		this.goalSelector.addGoal(7, (Goal)this.wander);
		this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
//		this.goalSelector.addGoal(9, new LookRandomlyGoal(this));
//		this.wander.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		super.registerGoals();
	}


	@Override
	public void setAngerTarget(UUID target) {
		this.angerTarget = target;
	}

	@Override
	public void setAngerTime(int time) {
		this.angerTime = time;
		
	}

	private void setMoving(final boolean movingStatus) {
		this.dataManager.set((DataParameter<Boolean>) SlipperyBiterEntity.MOVING, movingStatus);
	}

	private void setTargetedEntity(final int targetEntityId) {
		this.dataManager.set((DataParameter<Integer>) SlipperyBiterEntity.TARGET_ENTITY, targetEntityId);
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("SubType", (byte) getSubType());
	}

	static class TargetPredicate implements Predicate<LivingEntity> {
		private final SlipperyBiterEntity parentEntity;

		public TargetPredicate(SlipperyBiterEntity biter) {
			this.parentEntity = biter;
		}

		public boolean test(@Nullable LivingEntity entity) {

			if (!(entity instanceof PlayerEntity)) {
				return false;
			}

			World w = entity.getEntityWorld();
			
			boolean targetInWater = false;
			if ((w.getFluidState(entity.getPosition()).isTagged(FluidTags.WATER))||
				(w.getFluidState(entity.getPosition().up()).isTagged(FluidTags.WATER)))
			{
				targetInWater = true;
			}		
			
			if (!targetInWater) {
				return false;
			}

			// this may be redundant.  TargetPredicate may only be called for entities in range.
			ModifiableAttributeInstance followDistance = parentEntity.getAttribute(Attributes.FOLLOW_RANGE);
			int fRSq = (int) ( followDistance.getValue() * followDistance.getValue());
			if (((int) entity.getDistanceSq(this.parentEntity) > fRSq)) {
				return false;
			}

			// 1 to ~500
			int distanceSq = (int) entity.getDistanceSq(this.parentEntity);


			Biome biome = w.getBiome(this.parentEntity.getPosition());
			Category bC = biome.getCategory();

			// less aggressive in swamps
			if ((bC == Category.SWAMP) && (distanceSq > 255)) {
				return false;
			}

			// less aggressive in light, more aggressive in the dark
			int lightLevel = w.getLight(this.parentEntity.getPosition());
			if ((lightLevel > 13) && (distanceSq > 100)) {
				return false;
			}
			
			// Slippery Biter can teleport behind the player if they are too far away.
			BlockPos biterPos = this.parentEntity.getPosition();
			if (distanceSq > 150) {
				w.addParticle(ParticleTypes.POOF,biterPos.getX(),biterPos.getY(),biterPos.getZ(), 0.05, -0.05, 0.05);
				w.addParticle(ParticleTypes.POOF,biterPos.getX(),biterPos.getY(),biterPos.getZ(), 0.05, -0.05, 0.05);
				w.addParticle(ParticleTypes.POOF,biterPos.getX(),biterPos.getY(),biterPos.getZ(), 0.05, -0.05, 0.05);
				w.playSound((PlayerEntity)entity, biterPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 0.5f, 0.5f);
				// water collapsing into resulting void
				w.playSound((PlayerEntity)entity, biterPos, SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.AMBIENT, 0.25f, 0.25f);
				Vector3d v = entity.getLookVec();
				v.mul(-3.0d,-0.5d,-3.0d);
				Vector3i vI = new Vector3i(v.getX(),v.getY(),v.getZ());
				BlockPos tempPos = new BlockPos (vI.getX(),vI.getY(),vI.getZ());
				if (w.getFluidState(entity.getPosition()).isTagged(FluidTags.WATER)) {
					this.parentEntity.setPositionAndUpdate(tempPos.getX(), tempPos.getY(), tempPos.getZ());
				}
			}
			return true;
		}
	}	
	
	
	static class AttackGoal extends Goal {
		private final SlipperyBiterEntity slipperyBiter;
		private int tickCounter;
		
		public AttackGoal(final SlipperyBiterEntity slipperyBiterIn) {
			this.slipperyBiter = slipperyBiterIn;
			this.setMutexFlags((EnumSet) EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		public void resetTask() {
			this.slipperyBiter.setTargetedEntity(0);
			this.slipperyBiter.setAttackTarget((LivingEntity) null);
			this.slipperyBiter.wander.makeUpdate();
		}

		public boolean shouldContinueExecuting() {
			return super.shouldContinueExecuting();
		}

		public boolean shouldExecute() {
			final LivingEntity targetEntity = this.slipperyBiter.getAttackTarget();
			return targetEntity != null && targetEntity.isAlive();
		}


		public void startExecuting() {
			this.tickCounter = -10;
			this.slipperyBiter.getNavigator().clearPath();
			this.slipperyBiter.getLookController().setLookPositionWithEntity((Entity) this.slipperyBiter.getAttackTarget(), 90.0f,
					90.0f);
		}
		

		public void tick() {
			
			final LivingEntity thisLivingEntity = this.slipperyBiter.getAttackTarget();
			this.slipperyBiter.getNavigator().clearPath();
			this.slipperyBiter.getLookController().setLookPositionWithEntity((Entity) thisLivingEntity, 90.0f, 90.0f);

			if (!this.slipperyBiter.canEntityBeSeen((Entity) thisLivingEntity)) {
				this.slipperyBiter.setAttackTarget((LivingEntity) null);
				return;
			}

            ++this.tickCounter;
            if (this.tickCounter == 0) {
                this.slipperyBiter.setTargetedEntity(this.slipperyBiter.getAttackTarget().getEntityId());
            }
			
			if (this.slipperyBiter.onGround) {
				Vector3d newMotion = this.slipperyBiter.getMotion();
				newMotion.add(new Vector3d(0.0f,-0.2f,0.0f));
				this.slipperyBiter.setMotion(newMotion);
                this.slipperyBiter.rotationYaw = this.slipperyBiter.rand.nextFloat() * 360.0f;
                this.slipperyBiter.onGround = false;
                this.slipperyBiter.isAirBorne = true;
	            }
			super.tick();

			this.slipperyBiter.getNavigator().tryMoveToEntityLiving(thisLivingEntity, 5);		
			
		}
	}
	static class MoveHelperController extends MovementController {
		private final SlipperyBiterEntity workSlipperyBiterEntity;

		public MoveHelperController(final SlipperyBiterEntity slipperyBiterEntityIn) {
			super(slipperyBiterEntityIn);
			this.workSlipperyBiterEntity = slipperyBiterEntityIn;

		}

		public void tick() {
			if (this.action != MovementController.Action.MOVE_TO
					|| this.workSlipperyBiterEntity.getNavigator().noPath()) {

				this.workSlipperyBiterEntity.setAIMoveSpeed(0.0f);
				this.workSlipperyBiterEntity.setMoving(false);
				return;

			}

			final Vector3d vecSlipperyBiterLocationChange = new Vector3d(
					this.posX - this.workSlipperyBiterEntity.getPosX(),
					this.posY - this.workSlipperyBiterEntity.getPosY(),
					this.posZ - this.workSlipperyBiterEntity.getPosZ());

			final double locationVectorLength = vecSlipperyBiterLocationChange.length();

			final double xChange = vecSlipperyBiterLocationChange.x / locationVectorLength;
			final double yChange = vecSlipperyBiterLocationChange.y / locationVectorLength;
			final double zChange = vecSlipperyBiterLocationChange.z / locationVectorLength;
			final double workTan = MathHelper.atan2(vecSlipperyBiterLocationChange.z, vecSlipperyBiterLocationChange.x)
					* 57.2957763671875;
			final float lvt_10_1_ = (float) (workTan - 90.0f);

			this.workSlipperyBiterEntity.rotationYaw = this.limitAngle(this.workSlipperyBiterEntity.rotationYaw,
					lvt_10_1_, 90.0f);
			this.workSlipperyBiterEntity.renderYawOffset = this.workSlipperyBiterEntity.rotationYaw;

			final float lvt_11_1_ = (float) (this.speed
					* this.workSlipperyBiterEntity.getAttributeValue(Attributes.MOVEMENT_SPEED));
			final float lvt_12_1_ = MathHelper.lerp(0.125f, this.workSlipperyBiterEntity.getAIMoveSpeed(), lvt_11_1_);
			this.workSlipperyBiterEntity.setAIMoveSpeed(lvt_12_1_);
			final double lvt_13_1_ = Math
					.sin((this.workSlipperyBiterEntity.ticksExisted + this.workSlipperyBiterEntity.getEntityId()) * 0.25)
					* 0.03;
			final double lvt_15_1_ = Math.cos(this.workSlipperyBiterEntity.rotationYaw * 0.012453292f);
			final double lvt_17_1_ = Math.sin(this.workSlipperyBiterEntity.rotationYaw * 0.012453292f);
			final double lvt_19_1_ = Math.sin(
					(this.workSlipperyBiterEntity.ticksExisted + this.workSlipperyBiterEntity.getEntityId()) * 0.35)
					* 0.03;

			this.workSlipperyBiterEntity.setMotion(this.workSlipperyBiterEntity.getMotion().add(lvt_13_1_ * lvt_15_1_,
					lvt_19_1_ * (lvt_17_1_ + lvt_15_1_) * 0.25 + lvt_12_1_ * yChange * 0.1, lvt_13_1_ * lvt_17_1_));

			final LookController lvt_21_1_ = this.workSlipperyBiterEntity.getLookController();
			final double lvt_22_1_ = this.workSlipperyBiterEntity.getPosX() + xChange * 2.0;
			final double lvt_24_1_ = this.workSlipperyBiterEntity.getPosYEye() + yChange / locationVectorLength;
			final double lvt_26_1_ = this.workSlipperyBiterEntity.getPosZ() + zChange * 2.0;
			double lvt_28_1_ = lvt_21_1_.getLookPosX();
			double lvt_30_1_ = lvt_21_1_.getLookPosY();
			double lvt_32_1_ = lvt_21_1_.getLookPosZ();
			if (!lvt_21_1_.getIsLooking()) {
				lvt_28_1_ = lvt_22_1_;
				lvt_30_1_ = lvt_24_1_;
				lvt_32_1_ = lvt_26_1_;
			}
			this.workSlipperyBiterEntity.getLookController().setLookPosition(
					MathHelper.lerp(0.125, lvt_28_1_, lvt_22_1_), MathHelper.lerp(0.125, lvt_30_1_, lvt_24_1_),
					MathHelper.lerp(0.125, lvt_32_1_, lvt_26_1_), 10.0f, 40.0f);
			this.workSlipperyBiterEntity.setMoving(true);
		}
	}

}

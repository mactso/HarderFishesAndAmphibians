package com.mactso.hostilewatermobs.entities;

import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.sound.ModSounds;
import com.mactso.hostilewatermobs.utility.Utility;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;

public class RiverGuardian extends GuardianEntity implements IMob {

	static class TargetPredicate implements Predicate<LivingEntity> {
		private static int timer = 0;
		private static int hunttimer = 0;
		private final RiverGuardian parentEntity;

		public TargetPredicate(RiverGuardian guardian) {
			this.parentEntity = guardian;
		}

		private int calcBaseHuntingRange(LivingEntity entity) {
			
			int huntingRange = 170;
			
			// more aggressive in darkness
			int lightLevel = parentEntity.level.getMaxLocalRawBrightness(parentEntity.blockPosition());
			huntingRange = huntingRange + ((10 - lightLevel) * 10);
			// more aggressive in some biomes

			String bC = Utility.getBiomeCategory(entity.level.getBiome(entity.blockPosition()));
			if (bC == Utility.OCEAN) {
				huntingRange += 49;
			}
			if (bC == Utility.SWAMP) {
				huntingRange += 49;
			}
			
			// less aggressive if same subtype of river guardian
			if (entity instanceof RiverGuardian) {
				RiverGuardian e = (RiverGuardian) entity;
				if (e.getSubType() == parentEntity.getSubType()) {
					huntingRange -= 36;
				}
			}
			return huntingRange;
		}

		private boolean isInAttackRange(int distanceSq) {
			if (distanceSq < 7.0) {
				return false;
			}
			// over 23 meters away
			if ((distanceSq > 529.0)) {
				return false;
			}
			return true;
		}

		public boolean test(@Nullable LivingEntity entity) {

			// silence river guardian attack unless attacking a player or an entity close to a player.

			World w = entity.getCommandSenderWorld();
			parentEntity.setSilent(true);
			
			int range = MyConfig.getRiverGuardianSoundRange();
			
			range *= range;
			if (range > 0) {
				PlayerEntity p = w.getNearestPlayer(parentEntity, range); // note: range not squared.
				if (p != null) {
					int actualRange = (int) p.distanceToSqr(parentEntity);
					if (actualRange <= range) {
						parentEntity.setSilent(false);
					}
				}
			}
			
			boolean playerIsTarget = false;

			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity s = (ServerPlayerEntity) entity;
				if (s.isCreative()) {
					return false;
				} else {
					playerIsTarget = true;
				}
			}

			int distanceSq = (int) entity.distanceToSqr(this.parentEntity);
			// note: guardians do not attack when close. They back away.
			if (!isInAttackRange(distanceSq)) {
				return false;
			}

			if (playerIsTarget) {
				parentEntity.setSilent(false);
				return true;
			}

			if (hunttimer++ <40) {
				return false;
			}
			hunttimer = 0;
			
			// Ignore other River Guardians while it is Raining.
			boolean isRiverGuardianEntity = entity instanceof RiverGuardian;
			if (w.isRaining() && isRiverGuardianEntity) {
				return false;
			}
			
			if (hunttimer++ <80) {
				return false;
			}
			hunttimer = 0;
			
			// ignore monsters
			boolean isMonster = entity instanceof MonsterEntity;
			if (entity instanceof SlipperyBiter) {
				isMonster = true;
			}

			if (isMonster && !isRiverGuardianEntity) {
				return false;
			}

			if (entity.isBaby() && (MyConfig.getRiverGuardianPreysOnVillagerChildren() == false)) {
				return false;
			}

//			if (entity instanceof FoxEntity) {
//				return false;  no foxes in 1.16.X
//			}
			
			if (entity instanceof TurtleEntity) {
				return false;
			}

			// attack nearby prey animals
			boolean preyAnimal = entity instanceof CodEntity || entity instanceof PigEntity || entity instanceof ChickenEntity
					|| entity instanceof RabbitEntity || entity instanceof RiverGuardian;

			int huntingRange = calcBaseHuntingRange(entity);

			int subtype = parentEntity.getSubType();
			if (subtype == ALBINO_RIVER_GUARDIAN) {
				if (entity instanceof BatEntity) {
					preyAnimal = true;
				}
				huntingRange = +122;
			} else if (subtype == COLD_RIVER_GUARDIAN) {
				if (entity instanceof CodEntity) {
					preyAnimal = false;
				}
				if (entity instanceof SalmonEntity) {
					preyAnimal = true;
					huntingRange += 26;
				}
			} else if (subtype == WARM_RIVER_GUARDIAN) {
				if (entity instanceof ParrotEntity) {
					preyAnimal = true;
				}
				huntingRange += 70;
			} else {

			}

			if (distanceSq < huntingRange) {
				if (parentEntity.random.nextInt(4000) <= 4000) {
					return true;
				}
			}

			return false;

		}


	}

	public static final float ELDER_SIZE_SCALE = EntityType.GUARDIAN.getWidth() * 0.37f;
	private static final DataParameter<Integer> SUB_TYPE = EntityDataManager.defineId(RiverGuardian.class,
			DataSerializers.INT);
	private static int DEFAULT_RIVER_GUARDIAN = 0;
	private static int COLD_RIVER_GUARDIAN = 1;
	private static int WARM_RIVER_GUARDIAN = 2;

	private static int ALBINO_RIVER_GUARDIAN = 3;

	private static int calcNetMobCap(IWorld level, BlockPos pos) {

		int mobCap = MyConfig.getRiverGuardianSpawnCap() +
				((ServerWorld) level).getServer().getPlayerCount();

		if (isDeep(pos)) {
			mobCap += 6;
			return mobCap;
		}

		String bC = Utility.getBiomeCategory(level.getBiome(pos));
		if (bC == Utility.RIVER) {
			mobCap += 9;
			return mobCap;
		}
		if (bC == Utility.SWAMP) {
			mobCap += 7;
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
	
	public static boolean canSpawn(EntityType<? extends RiverGuardian> type, IWorld level, SpawnReason reason,
			BlockPos pos, Random randomIn) {

		Utility.debugMsg(1, pos, "canSpawn riverGuardian?");
		// SpawnPlacements.Type.IN_WATER
		
		if (Utility.isSpawnRateThrottled(level,50)) {
			return false;
		}

		if (Utility.isInBubbleColumn(level, pos)) {
			return false;
		}

		if (level.getDifficulty() == Difficulty.PEACEFUL)
			return false;

		if (reason == SpawnReason.SPAWN_EGG)
			return true;

		if (isTooBright(level, pos))
			return false;

		if (reason == SpawnReason.SPAWNER)
			return true;


		if (isBadAltitude(level, pos))
			return false;

 
		if (isFailBiomeLimits(level, pos))
			return false;

		// prevent local overcrowding
		if (Utility.isOverCrowded(level, RiverGuardian.class, pos, 5))
			return false;
		
		int mobCount = ((ServerWorld) level).getEntities(ModEntities.RIVER_GUARDIAN, (entity) -> true).size();
		if (mobCount >= calcNetMobCap(level, pos)) {
			return false;
		}

		Utility.debugMsg(1, pos, "spawned riverGuardian.");

		return true;

	}
	
	private static boolean isBadAltitude(IWorld level, BlockPos pos) {

		if (pos.getY() > 128)
			return true;
		if (pos.getY() < -60)
			return true;

		return false;
	}

	private static boolean isDeep(BlockPos pos) {
		return (pos.getY() < 35);
	}

	private static boolean isFailBiomeLimits(IWorld level, BlockPos pos) {

		String bC = Utility.getBiomeCategory(level.getBiome(pos));
		if ( bC == Utility.MUSHROOM || bC == Utility.THEEND	) {
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
	
	
	private static boolean isTooBright(IWorld level, BlockPos pos) {

		if (isDeep(pos)) {
			if (level.getMaxLocalRawBrightness(pos) > 9) {
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
		return GuardianEntity.createAttributes().add(Attributes.MOVEMENT_SPEED, (double) 0.65F)
				.add(Attributes.FOLLOW_RANGE, 24.0D)
				.add(Attributes.ATTACK_DAMAGE, 2.0D)
				.add(Attributes.MAX_HEALTH, 18.5D);
	}

	public RiverGuardian(EntityType<? extends RiverGuardian> type, World worldIn) {
		super(type, worldIn);
		this.xpReward = 7;
		if (this.randomStrollGoal != null) {
			this.randomStrollGoal.setInterval(400);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte("SubType", (byte) getSubType());
	}
	
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SUB_TYPE, 0);

	}

	@Override

	public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			ILivingEntityData spawnDataIn, CompoundNBT dataTag) {

		BlockPos pos = blockPosition(); // getPosition

		Biome biome = worldIn.getBiome(pos);
		ResourceLocation biomeNameResourceKey = worldIn.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
		String biomename = biomeNameResourceKey.toString();
		RegistryKey<Biome> biomeKey = RegistryKey.create(Registry.BIOME_REGISTRY, biomeNameResourceKey);
		
		int workingSubType = DEFAULT_RIVER_GUARDIAN;
		
		if (BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.COLD)) {
			workingSubType = COLD_RIVER_GUARDIAN;
			if (pos.getY() < 29) {
				workingSubType = ALBINO_RIVER_GUARDIAN;
			}
		}

		if (BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.HOT)) {
			workingSubType = WARM_RIVER_GUARDIAN;
			if (pos.getY() < 29) {
				workingSubType = ALBINO_RIVER_GUARDIAN;
			}
		}

		entityData.set(SUB_TYPE, workingSubType);

		if (difficultyIn.getDifficulty() == Difficulty.HARD) {
			this.getAttribute(Attributes.ATTACK_DAMAGE)
					.addTransientModifier(new AttributeModifier("difficulty", 0.5, Operation.ADDITION));
		}
		return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	public LivingEntity getActiveAttackTarget() {
		return super.getActiveAttackTarget();
	}

	protected SoundEvent getAmbientSound() {

		return this.isInWaterOrBubble() ? ModSounds.RIVER_GUARDIAN_AMBIENT : ModSounds.RIVER_GUARDIAN_HURT_LAND;
	}

	public int getAttackDuration() {
		return 90;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isInWaterOrBubble() ? ModSounds.RIVER_GUARDIAN_DEATH : ModSounds.RIVER_GUARDIAN_DEATH_LAND;
	}

	@Override
	protected SoundEvent getFlopSound() {
		return ModSounds.RIVER_GUARDIAN_FLOP;
	}

	// TODO Raise Pitch because smaller
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return this.isInWaterOrBubble() ? ModSounds.RIVER_GUARDIAN_HURT : ModSounds.RIVER_GUARDIAN_HURT_LAND;
	}

	public int getSubType() {
		return entityData.get(SUB_TYPE);
	}

	@Override
	public void handleEntityEvent(byte id) {
		
		super.handleEntityEvent(id);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source == null) {
			source = DamageSource.GENERIC;
		}
		if (amount < 0 ) {
			amount = 0;
		}

		String type = source.msgId;
		if (type.equals("thorns")) {
			amount = 0;
		}
//		EntityDamageSource e = (EntityDamageSource) source;
//		if (e.getIsThornsDamage()) {
//		}
		return super.hurt(source, amount);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		entityData.set(SUB_TYPE, (int) compound.getByte("SubType"));
	}

	@Override
	protected void registerGoals() {
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
				new RiverGuardian.TargetPredicate(this)));
		super.registerGoals();
	}

}



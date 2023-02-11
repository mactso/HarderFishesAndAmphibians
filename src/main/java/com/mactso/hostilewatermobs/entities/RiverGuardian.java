package com.mactso.hostilewatermobs.entities;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.sound.ModSounds;
import com.mactso.hostilewatermobs.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;

public class RiverGuardian extends Guardian implements Enemy {

	static class TargetPredicate implements Predicate<LivingEntity> {
		private static int timer = 0;
		private static int hunttimer = 0;
		private final RiverGuardian parentEntity;

		public TargetPredicate(RiverGuardian guardian) {
			this.parentEntity = guardian;
			this.hunttimer = 0;
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

			// silence river guardian attack unless attacking a player or an entity close to
			// a player.

			Level w = entity.getCommandSenderWorld();
			parentEntity.setSilent(true);

			int range = MyConfig.getRiverGuardianSoundRange();

			range *= range;
			if (range > 0) {
				Player p = w.getNearestPlayer(parentEntity, range); // note: range not squared.
				if (p != null) {
					int actualRange = (int) p.distanceToSqr(parentEntity);
					if (actualRange <= range) {
						parentEntity.setSilent(false);
					}
				}
			}

			boolean playerIsTarget = false;
			if (entity instanceof ServerPlayer) {
				ServerPlayer s = (ServerPlayer) entity;
				if (s.isCreative()) {
					return false;
				} else {
					playerIsTarget = true;
				}
			}

			int distanceSq = (int) entity.distanceToSqr(this.parentEntity);
			// note: river guardians do not attack when close. They back away.
			if (!isInAttackRange(distanceSq)) {
				return false;
			}

			if (playerIsTarget) {
				parentEntity.setSilent(false);
				return true;
			}

			if (hunttimer++ < 80) {
				return false;
			}
			hunttimer = 0;

			// Ignore other River Guardians while it is Raining.
			boolean isRiverGuardianEntity = entity instanceof RiverGuardian;
			if (w.isRaining() && isRiverGuardianEntity) {
				return false;
			}

			// ignore monsters
			boolean isMonster = entity instanceof Monster;
			if (entity instanceof SlipperyBiter) {
				isMonster = true;
			}

			if (isMonster && !isRiverGuardianEntity) {
				return false;
			}

			if (entity.isBaby() && (MyConfig.getRiverGuardianPreysOnVillagerChildren() == false)) {
				return false;
			}

			if (entity instanceof Fox) {
				return false;
			}

			if (entity instanceof Turtle) {
				return false;
			}

			// attack nearby prey animals
			boolean preyAnimal = entity instanceof Cod || entity instanceof Pig || entity instanceof Chicken
					|| entity instanceof Rabbit || entity instanceof RiverGuardian;

			int huntingRange = calcBaseHuntingRange(entity);

			int subtype = parentEntity.getSubType();
			if (subtype == ALBINO_RIVER_GUARDIAN) {
				if (entity instanceof Bat) {
					preyAnimal = true;
				}
				huntingRange = +122;
			} else if (subtype == COLD_RIVER_GUARDIAN) {
				if (entity instanceof Cod) {
					preyAnimal = false;
				}
				if (entity instanceof Salmon) {
					preyAnimal = true;
					huntingRange += 26;
				}
			} else if (subtype == WARM_RIVER_GUARDIAN) {
				if (entity instanceof Parrot) {
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
	private static final EntityDataAccessor<Integer> SUB_TYPE = SynchedEntityData.defineId(RiverGuardian.class,
			EntityDataSerializers.INT);
	private static int DEFAULT_RIVER_GUARDIAN = 0;
	private static int COLD_RIVER_GUARDIAN = 1;
	private static int WARM_RIVER_GUARDIAN = 2;

	private static int ALBINO_RIVER_GUARDIAN = 3;

	private static int calcNetMobCap(LevelAccessor level, BlockPos pos) {

		int mobCap = MyConfig.getRiverGuardianSpawnCap() + level.getServer().getPlayerCount();

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
		if (level.getBiome(pos).is(BiomeTags.IS_OCEAN)) {
			mobCap += 5;
			return mobCap;
		}

		if (level.getBiome(pos).is(BiomeTags.IS_RIVER)) {
			mobCap += 9;
			return mobCap;
		}

		if (level.getBiome(pos).is(BiomeTags.HAS_SWAMP_HUT)) {
			mobCap += 5;
			return mobCap;
		}

		return mobCap;
	}

	public static boolean canSpawn(EntityType<? extends RiverGuardian> type, LevelAccessor level, MobSpawnType reason,
			BlockPos pos, RandomSource randomIn) {

		Utility.debugMsg(1, pos, "canSpawn riverGuardian?");
		// SpawnPlacements.Type.IN_WATER

		if (isSpawnRateThrottled(level)) {
			return false;
		}

		if (Utility.isInBubbleColumn(level, pos)) {
			return false;
		}

		if (level.getDifficulty() == Difficulty.PEACEFUL)
			return false;

		if (reason == MobSpawnType.SPAWN_EGG)
			return true;

		if (isTooBright(level, pos))
			return false;

		if (reason == MobSpawnType.SPAWNER)
			return true;

		if (isBadAltitude(level, pos))
			return false;

		if (isFailBiomeLimits(level, pos))
			return false;

		// prevent local overcrowding
		if (Utility.isOverCrowded(level, RiverGuardian.class, pos, 5))
			return false;

		int mobCount = ((ServerLevel) level).getEntities(ModEntities.RIVER_GUARDIAN, (entity) -> true).size();
		if (mobCount >= calcNetMobCap(level, pos)) {
			return false;
		}

		Utility.debugMsg(1, pos, "spawned riverGuardian.");

		return true;

	}

	public static AttributeSupplier.Builder createAttributes() {
		return Guardian.createAttributes().add(Attributes.MOVEMENT_SPEED, (double) 0.65F)
				.add(Attributes.FOLLOW_RANGE, 24.0D).add(Attributes.ATTACK_DAMAGE, 2.0D)
				.add(Attributes.MAX_HEALTH, 18.5D);
	}

	private static boolean isBadAltitude(LevelAccessor level, BlockPos pos) {

		if (pos.getY() > 128)
			return true;
		if (pos.getY() < -60)
			return true;

		return false;
	}

	private static boolean isDeep(BlockPos pos) {
		return (pos.getY() < 35);
	}

	private static boolean isFailBiomeLimits(LevelAccessor level, BlockPos pos) {

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


	// needed for water creatures because so many valid spawn blocks.
	private static boolean isSpawnRateThrottled(LevelAccessor level) {
		if (level.getRandom().nextInt(2) != 0) {
			return true;
		}
		return false;
	}

	private static boolean isTooBright(LevelAccessor level, BlockPos pos) {

		if (isDeep(pos)) {
			if (level.getMaxLocalRawBrightness(pos) > 9) {
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

	public RiverGuardian(EntityType<? extends RiverGuardian> type, Level worldIn) {
		super(type, worldIn);
		this.xpReward = 7;
		if (this.randomStrollGoal != null) {
			this.randomStrollGoal.setInterval(400);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte("SubType", (byte) getSubType());
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SUB_TYPE, 0);

	}

	@Override

	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
			MobSpawnType reason, SpawnGroupData spawnDataIn, CompoundTag dataTag) {
		BlockPos pos = blockPosition(); // getPosition

		Holder<Biome> biomeHolder = worldIn.getBiome(pos);
		Biome biome = biomeHolder.value();
//		ResourceLocation biomeNameResourceKey = worldIn.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
//		String biomename = biomeNameResourceKey.toString();
//		ResourceKey<Biome> biomeKey = ResourceKey.create(Registry.BIOME_REGISTRY, biomeNameResourceKey);

		int workingSubType = DEFAULT_RIVER_GUARDIAN;

//		TempCategory tC = biome. getTempCategory();

//		boolean isCold = BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.COLD);
//		isCold = biomename.contains("cold") || biomename.contains("frozen") || biomename.contains("icy")
//				|| biomename.contains("ice_spikes") || biomename.contains("snowy")
//				|| biome.doesSnowGenerate(worldIn, pos) || BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.COLD);

		if (biome.coldEnoughToSnow(pos)) {
			workingSubType = COLD_RIVER_GUARDIAN;
			if (pos.getY() < 29) {
				workingSubType = ALBINO_RIVER_GUARDIAN;
			}
		}

//		boolean isWarm = false;
//		isWarm = (biomename.contains("warm") && !(biomename.contains("lukewarm"))) || biomename.contains("swamp")
//				|| biomename.contains("jungle") || biomename.contains("desert") || tC == TempCategory.WARM;

		if (!biome.isHumid() && biome.warmEnoughToRain(pos)) {
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
		if (amount < 0) {
			amount = 0;
		}

		String type = source.msgId;
		if (type.equals("thorns")) {
			amount = 0;
		}

		return super.hurt(source, amount);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
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

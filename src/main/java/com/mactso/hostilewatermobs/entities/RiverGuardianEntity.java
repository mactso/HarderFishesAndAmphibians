package com.mactso.hostilewatermobs.entities;

import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.sound.ModSounds;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.BiomeDictionary;

public class RiverGuardianEntity extends Guardian implements Enemy {

	public static final float ELDER_SIZE_SCALE = EntityType.GUARDIAN.getWidth() * 0.37f;
	private static final EntityDataAccessor<Integer> SUB_TYPE = SynchedEntityData.defineId(RiverGuardianEntity.class,
			EntityDataSerializers.INT);
	private static int DEFAULT_RIVER_GUARDIAN = 0;
	private static int COLD_RIVER_GUARDIAN = 1;
	private static int WARM_RIVER_GUARDIAN = 2;
	private static int ALBINO_RIVER_GUARDIAN = 3;

	public RiverGuardianEntity(EntityType<? extends RiverGuardianEntity> type, Level worldIn) {
		super(type, worldIn);
		this.xpReward = 7;
		if (this.randomStrollGoal != null) {
			this.randomStrollGoal.setInterval(400);
		}
	}

	
	@Override

	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason,
			SpawnGroupData spawnDataIn, CompoundTag dataTag) {
		BlockPos pos = blockPosition(); // getPosition
		if (MyConfig.getDebugLevel() > 0) {
			System.out.println ("riverguardian on intialspawn at " + pos.toString());
		}
		Biome biome = worldIn.getBiome(pos);
		ResourceLocation biomeNameResourceKey = worldIn.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
		String biomename = biomeNameResourceKey.toString();
		ResourceKey<Biome> biomeKey = ResourceKey.create(Registry.BIOME_REGISTRY, biomeNameResourceKey);
		
		int workingSubType = DEFAULT_RIVER_GUARDIAN;
		if (MyConfig.getDebugLevel() > 0) {
			System.out.println("default");
		}

		
//		TempCategory tC = biome. getTempCategory();
		
//		boolean isCold = BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.COLD);
//		isCold = biomename.contains("cold") || biomename.contains("frozen") || biomename.contains("icy")
//				|| biomename.contains("ice_spikes") || biomename.contains("snowy")
//				|| biome.doesSnowGenerate(worldIn, pos) || BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.COLD);

		if (BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.COLD)) {
			if (MyConfig.getDebugLevel() > 0) {
				System.out.println("cold");
			}
			workingSubType = COLD_RIVER_GUARDIAN;
			if (pos.getY() < 29) {
				if (MyConfig.getDebugLevel() > 0) {
					System.out.println("cold albino");
				}
				workingSubType = ALBINO_RIVER_GUARDIAN;
			}
		}

//		boolean isWarm = false;
//		isWarm = (biomename.contains("warm") && !(biomename.contains("lukewarm"))) || biomename.contains("swamp")
//				|| biomename.contains("jungle") || biomename.contains("desert") || tC == TempCategory.WARM;

		if (BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.HOT)) {
			workingSubType = WARM_RIVER_GUARDIAN;
			if (MyConfig.getDebugLevel() > 0) {
				System.out.println("warm");
			}
			if (pos.getY() < 29) {
				if (MyConfig.getDebugLevel() > 0) {
					System.out.println("warm albino");
				}
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
	protected void registerGoals() {
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
				new RiverGuardianEntity.TargetPredicate(this)));
		super.registerGoals();
	}

	@Override
	public LivingEntity getActiveAttackTarget() {
		return super.getActiveAttackTarget();
	}

	@Override
	public void handleEntityEvent(byte id) {
		
		super.handleEntityEvent(id);
	}
	
	public static AttributeSupplier.Builder registerAttributes() {
		return Guardian.createAttributes().add(Attributes.MOVEMENT_SPEED, (double) 0.65F)
				.add(Attributes.FOLLOW_RANGE, 24.0D)
				.add(Attributes.ATTACK_DAMAGE, 2.0D)
				.add(Attributes.MAX_HEALTH, 11.0D);
	}

	public static boolean canSpawn(EntityType<? extends RiverGuardianEntity> type, LevelAccessor world, MobSpawnType reason,
			BlockPos pos, Random randomIn) {

		if (world.getDifficulty() == Difficulty.PEACEFUL)
			return false;

		if (reason == MobSpawnType.SPAWN_EGG)
			return true;

		boolean inWater = world.getFluidState(pos).is(FluidTags.WATER)
				|| world.getFluidState(pos.above()).is(FluidTags.WATER);
		if (!inWater) {
			return false;
		}

		if (reason == MobSpawnType.SPAWNER)
			return true;

		boolean isDark = world.getMaxLocalRawBrightness(pos) < 9;
		boolean isDeep = pos.getY() < 30;
		if (isDeep && !isDark) {
			return false;
		}

		int riverGuardianSpawnChance = MyConfig.getRiverGuardianSpawnChance();
		int riverGuardianCap = MyConfig.getRiverGuardianSpawnCap();
		int riverGuardianSpawnRoll = randomIn.nextInt(30);

		if (isDeep) {
			riverGuardianCap += 6;
			riverGuardianSpawnChance += 9;
			if (MyConfig.getDebugLevel() > 0) {
				System.out.println("spawn deep riverGuardian +9");
			}
		}

		Biome biome = world.getBiome(pos);
		BiomeCategory bC = biome.getBiomeCategory();
		if (bC == BiomeCategory.OCEAN) {
			if (pos.getY() > 33) {
				return false;
			}
			if (world.getMaxLocalRawBrightness(pos) > 8) {
				return false;
			}
		}

		if (bC == BiomeCategory.SWAMP) {
			riverGuardianSpawnChance += 7;
			riverGuardianCap += 7;
			if (MyConfig.getDebugLevel() > 0) {
				System.out.println("spawn swamp riverGuardian +7");
			}
		}

		if (bC == BiomeCategory.RIVER) {
			riverGuardianCap += 9;
			riverGuardianSpawnChance += 11;
			if (MyConfig.getDebugLevel() > 0) {
				System.out.println("spawn riverGuardian +9");
			}
		}

		if (world instanceof ServerLevel) {
			int riverGuardianCount = ((ServerLevel) world).getEntities(ModEntities.RIVER_GUARDIAN, (entity) -> true)
					.size();
			if (MyConfig.getDebugLevel() > 0) {
				System.out.println("River Guardian Count : " + riverGuardianCount);
			}
			if (riverGuardianCount > riverGuardianCap) {
				return false;
			}
		}

		if (MyConfig.getDebugLevel() > 0) {
			System.out.println(
					"River Guardian Spawn Cap:" + riverGuardianCap + " Spawn Chance:" + riverGuardianSpawnChance);
		}

		if ((riverGuardianSpawnRoll < riverGuardianSpawnChance) || !(world.canSeeSkyFromBelowWater(pos))) {
			LevelChunk c = (LevelChunk) world.getChunk(pos);
			ClassInstanceMultiMap<Entity>[] aL = c.getEntitySections();
			int height = pos.getY() / 16;
			if (height < 0)
				height = 0; // cubic chunks
			if (aL[height].find(RiverGuardianEntity.class).size() > 1) {
				return false;
			}
			if (MyConfig.getDebugLevel() > 0) {
				System.out.println("spawn riverGuardian true at " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
			}
			return true;
		}

		return false;
	}

	public int getAttackDuration() {
		return 90;
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

	protected SoundEvent getAmbientSound() {

		return this.isInWaterOrBubble() ? ModSounds.RIVER_GUARDIAN_AMBIENT : ModSounds.RIVER_GUARDIAN_HURT_LAND;
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

	static class TargetPredicate implements Predicate<LivingEntity> {
		private final RiverGuardianEntity parentEntity;
		private static int timer = 0;

		public TargetPredicate(RiverGuardianEntity guardian) {
			this.parentEntity = guardian;
		}

		public boolean test(@Nullable LivingEntity entity) {

			// silence river guardian attack unless attacking a player or an entity close to a player.

			boolean playerIsTarget = false;
			if (entity instanceof ServerPlayer) {
				ServerPlayer s = (ServerPlayer) entity;
				if (s.isCreative()) {
					return false;
				}
				playerIsTarget = true;
			}

			int distanceSq = (int) entity.distanceToSqr(this.parentEntity);
			// note: guardians do not attack when close.  They back away.
			if (!isInAttackRange(distanceSq)) {
				return false;
			}

			if (playerIsTarget) {
				parentEntity.setSilent(false);
				return true;
			}
			
			Level w = entity.getCommandSenderWorld();
			int range = MyConfig.getRiverGuardianSoundRange();
			parentEntity.setSilent(true);
			if (range > 0) {
				Player p = null;
				p = w.getNearestPlayer(entity, range); // note: range not squared.
				if (p != null) {
					int actualRange = (int) p.distanceToSqr(entity);
//					System.out.println("player "+p.getDisplayName().getString()+" near "+ entity.getName().getString() + " target:" + range + " (" + actualRange);
					parentEntity.setSilent(false);
				}
				p = w.getNearestPlayer(parentEntity, range); // note: range not squared.
				if (p != null) {
					int actualRange = (int) p.distanceToSqr(parentEntity);
//					System.out.println("player "+p.getDisplayName().getString()+" near attacker:" + range + " (" + actualRange);
					parentEntity.setSilent(false);
				}
				
			}

			
			// Ignore other River Guardians while it is Raining.
			boolean isRiverGuardianEntity = entity instanceof RiverGuardianEntity;
			if (w.isRaining() && isRiverGuardianEntity) {
				return false;
			}


			// ignore monsters
			boolean isMonster = entity instanceof Monster;
			if (entity instanceof SlipperyBiterEntity) {
				isMonster = true;
			}
			
			if (isMonster && !isRiverGuardianEntity && !entity.isBaby()) {
				return false;
			}

			if (entity instanceof Villager) {
				if (entity.isBaby() == false) {
					return false;
				} else
				if (MyConfig.getRiverGuardianPreysOnVillagerChildren() == false) {
					return false;
				}
			}
			
			if (entity instanceof Turtle) {
				return false;
			}
			
			
			// attack nearby prey animals
			boolean preyAnimal = entity instanceof Cod || entity instanceof Pig
					|| entity instanceof Chicken || entity instanceof Rabbit || entity.isBaby()
					|| entity instanceof RiverGuardianEntity;



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

		private int calcBaseHuntingRange(LivingEntity entity) {
			
			int huntingRange = 170;
			
			// more aggressive in darkness
			int lightLevel = parentEntity.level.getMaxLocalRawBrightness(parentEntity.blockPosition());
			huntingRange = huntingRange + ((10 - lightLevel) * 10);
			// more aggressive in some biomes

			BiomeCategory bC = entity.level.getBiome(entity.blockPosition()).getBiomeCategory();
			if (bC == BiomeCategory.OCEAN) {
				huntingRange += 49;
			}
			if (bC == BiomeCategory.SWAMP) {
				huntingRange += 49;
			}
			
			// less aggressive if same subtype of river guardian
			if (entity instanceof RiverGuardianEntity) {
				RiverGuardianEntity e = (RiverGuardianEntity) entity;
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
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(SUB_TYPE, 0);

	}

	public int getSubType() {
		return entityData.get(SUB_TYPE);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte("SubType", (byte) getSubType());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		entityData.set(SUB_TYPE, (int) compound.getByte("SubType"));
	}

}



package com.mactso.hostilewatermobs.entities;

import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.sound.ModSounds;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ClassInheritanceMultiMap;
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
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;

public class RiverGuardianEntity extends GuardianEntity implements IMob {

	public static final float field_213629_b = EntityType.GUARDIAN.getWidth() * 0.37f;
	private static final DataParameter<Integer> SUB_TYPE = EntityDataManager.createKey(RiverGuardianEntity.class,
			DataSerializers.VARINT);
	private static int DEFAULT_RIVER_GUARDIAN = 0;
	private static int COLD_RIVER_GUARDIAN = 1;
	private static int WARM_RIVER_GUARDIAN = 2;
	private static int ALBINO_RIVER_GUARDIAN = 3;

	public RiverGuardianEntity(EntityType<? extends RiverGuardianEntity> type, World worldIn) {
		super(type, worldIn);
		this.experienceValue = 7;
		if (this.wander != null) {
			this.wander.setExecutionChance(400);
		}
	}

	
	@Override

	public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			ILivingEntityData spawnDataIn, CompoundNBT dataTag) {
		BlockPos pos = getPosition(); // getPosition
		if (MyConfig.getaDebugLevel() > 0) {
			System.out.println ("riverguardian on intialspawn at " + pos.toString());
		}
		Biome biome = worldIn.getBiome(pos);
		ResourceLocation biomeNameResourceKey = worldIn.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(biome);
		String biomename = biomeNameResourceKey.toString();
		RegistryKey<Biome> biomeKey = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, biomeNameResourceKey);
		
		int workingSubType = DEFAULT_RIVER_GUARDIAN;
		if (MyConfig.getaDebugLevel() > 0) {
			System.out.println("default");
		}

		
//		TempCategory tC = biome. getTempCategory();
		
//		boolean isCold = BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.COLD);
//		isCold = biomename.contains("cold") || biomename.contains("frozen") || biomename.contains("icy")
//				|| biomename.contains("ice_spikes") || biomename.contains("snowy")
//				|| biome.doesSnowGenerate(worldIn, pos) || BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.COLD);

		if (BiomeDictionary.hasType(biomeKey, BiomeDictionary.Type.COLD)) {
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("cold");
			}
			workingSubType = COLD_RIVER_GUARDIAN;
			if (pos.getY() < 29) {
				if (MyConfig.getaDebugLevel() > 0) {
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
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("warm");
			}
			if (pos.getY() < 29) {
				if (MyConfig.getaDebugLevel() > 0) {
					System.out.println("warm albino");
				}
				workingSubType = ALBINO_RIVER_GUARDIAN;
			}
		}

		dataManager.set(SUB_TYPE, workingSubType);

		if (difficultyIn.getDifficulty() == Difficulty.HARD) {
			this.getAttribute(Attributes.ATTACK_DAMAGE)
					.applyNonPersistentModifier(new AttributeModifier("difficulty", 0.5, Operation.ADDITION));
		}
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	protected void registerGoals() {
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
				new RiverGuardianEntity.TargetPredicate(this)));
		super.registerGoals();
	}

	@Override
	public LivingEntity getTargetedEntity() {
		return super.getTargetedEntity();
	}

	@Override
	public void handleStatusUpdate(byte id) {
		
		super.handleStatusUpdate(id);
	}
	
	public static AttributeModifierMap.MutableAttribute registerAttributes() {
		return GuardianEntity.func_234292_eK_().createMutableAttribute(Attributes.MOVEMENT_SPEED, (double) 0.65F)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 24.0D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D)
				.createMutableAttribute(Attributes.MAX_HEALTH, 11.0D);
	}

	public static boolean canSpawn(EntityType<? extends RiverGuardianEntity> type, IWorld world, SpawnReason reason,
			BlockPos pos, Random randomIn) {

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

		int riverGuardianSpawnChance = MyConfig.getRiverGuardianSpawnChance();
		int riverGuardianCap = MyConfig.getRiverGuardianSpawnCap();
		int riverGuardianSpawnRoll = randomIn.nextInt(30);

		if (isDeep) {
			riverGuardianCap += 6;
			riverGuardianSpawnChance += 9;
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("spawn deep riverGuardian +9");
			}
		}

		Biome biome = world.getBiome(pos);
		Category bC = biome.getCategory();
		if (bC == Category.OCEAN) {
			if (pos.getY() > 33) {
				return false;
			}
			if (world.getLight(pos) > 8) {
				return false;
			}
		}

		if (bC == Category.SWAMP) {
			riverGuardianSpawnChance += 7;
			riverGuardianCap += 7;
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("spawn swamp riverGuardian +7");
			}
		}

		if (bC == Category.RIVER) {
			riverGuardianCap += 9;
			riverGuardianSpawnChance += 11;
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("spawn riverGuardian +9");
			}
		}

		if (world instanceof ServerWorld) {
			int riverGuardianCount = ((ServerWorld) world).getEntities(ModEntities.RIVER_GUARDIAN, (entity) -> true)
					.size();
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("River Guardian Count : " + riverGuardianCount);
			}
			if (riverGuardianCount > riverGuardianCap) {
				return false;
			}
		}

		if (MyConfig.getaDebugLevel() > 0) {
			System.out.println(
					"River Guardian Spawn Cap:" + riverGuardianCap + " Spawn Chance:" + riverGuardianSpawnChance);
		}

		if ((riverGuardianSpawnRoll < riverGuardianSpawnChance) || !(world.canBlockSeeSky(pos))) {
			Chunk c = (Chunk) world.getChunk(pos);
			ClassInheritanceMultiMap<Entity>[] aL = c.getEntityLists();
			int height = pos.getY() / 16;
			if (height < 0)
				height = 0; // cubic chunks
			if (aL[height].getByClass(RiverGuardianEntity.class).size() > 1) {
				return false;
			}
			if (MyConfig.getaDebugLevel() > 0) {
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
	public boolean attackEntityFrom(DamageSource source, float amount) {
		String type = source.damageType;
		if (type.equals("thorns")) {
			amount = 0;
		}
//		EntityDamageSource e = (EntityDamageSource) source;
//		if (e.getIsThornsDamage()) {
//		}
		return super.attackEntityFrom(source, amount);
	}

	protected SoundEvent getAmbientSound() {

		return this.isInWaterOrBubbleColumn() ? ModSounds.RIVER_GUARDIAN_AMBIENT : ModSounds.RIVER_GUARDIAN_HURT_LAND;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isInWaterOrBubbleColumn() ? ModSounds.RIVER_GUARDIAN_DEATH : ModSounds.RIVER_GUARDIAN_DEATH_LAND;
	}

	@Override
	protected SoundEvent getFlopSound() {
		return ModSounds.RIVER_GUARDIAN_FLOP;
	}

	// TODO Raise Pitch because smaller
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return this.isInWaterOrBubbleColumn() ? ModSounds.RIVER_GUARDIAN_HURT : ModSounds.RIVER_GUARDIAN_HURT_LAND;
	}

	static class TargetPredicate implements Predicate<LivingEntity> {
		private final RiverGuardianEntity parentEntity;
		private static int timer = 0;

		public TargetPredicate(RiverGuardianEntity guardian) {
			this.parentEntity = guardian;
		}

		public boolean test(@Nullable LivingEntity entity) {

			// silence river guardian attack unless attacking a player or an entity close to a player.


			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity s = (ServerPlayerEntity) entity;
				if (s.isCreative()) {
					return false;
				}
			}

			int distanceSq = (int) entity.getDistanceSq(this.parentEntity);
			// under 3 meters away.
			if (distanceSq < 7.0) {
				return false;
			}

			// over 23 meters away
			if ((distanceSq > 529.0)) {
				return false;
			}

			World w = entity.getEntityWorld();
			
			int range = MyConfig.getRiverGuardianSoundRange();
			PlayerEntity pT = w.getClosestPlayer(entity, 15);
			BlockPos pTPos = new BlockPos (0,0,0);
			if (pT!= null) {
				pTPos = pT.getPosition();
			}
			PlayerEntity pR = w.getClosestPlayer(parentEntity, 15);
			BlockPos pRPos = new BlockPos (0,0,0);
			if (pR!= null) {
				pRPos = pR.getPosition();
			}

			boolean PlayerNearTarget = (w.getClosestPlayer(entity, MyConfig.getRiverGuardianSoundRange()) != null);
			boolean PlayerNearRiverGuardian = (w.getClosestPlayer(parentEntity, MyConfig.getRiverGuardianSoundRange()) != null);
			boolean nearbyPlayer = PlayerNearTarget || PlayerNearRiverGuardian;

			if (MyConfig.getaDebugLevel() > 0) {
				if (((timer++)%10 == 0) && nearbyPlayer) {
					System.out.print (" riverPos: " + parentEntity.getPosition());
					System.out.print (" targetPos: " + entity.getPosition());
					System.out.println (" range: " + range);
					System.out.print (" PlayerNearTarget: " + PlayerNearTarget);
					System.out.print (" PlayernearestT: " + pTPos);
					System.out.print (" PlayerNearRiverGuardian: " + PlayerNearRiverGuardian);
					System.out.print (" PlayernearestR: " + pRPos);
					System.out.println (" nearbyPlayer: " + nearbyPlayer);
					
				}
			}
			
			// Ignore other River Guardians while it is Raining.
			boolean isRiverGuardianEntity = entity instanceof RiverGuardianEntity;
			if (w.isRaining() && isRiverGuardianEntity) {
				return false;
			}

			// ignore monsters
			boolean isMonster = entity instanceof MonsterEntity;
			if (isMonster && !isRiverGuardianEntity && !entity.isChild()) {
				return false;
			}

			boolean isVillager = entity instanceof VillagerEntity;
			if (isVillager) {
				if (entity.isChild() == false) {
					return false;
				} else
				if (MyConfig.getRiverGuardianPreysOnVillagerChildren() == false) {
					return false;
				}
			}
			
			
			// attack nearby prey animals
			boolean preyAnimal = entity instanceof CodEntity || entity instanceof PigEntity
					|| entity instanceof ChickenEntity || entity instanceof RabbitEntity || entity.isChild();

			int subtype = parentEntity.getSubType();

			int huntingRange = 37;
			if (subtype == ALBINO_RIVER_GUARDIAN) {
				if (entity instanceof BatEntity) {
					preyAnimal = true;
				}
				huntingRange = 122;
			} else if (subtype == COLD_RIVER_GUARDIAN) {
				if (entity instanceof CodEntity) {
					preyAnimal = false;
				}
				if (entity instanceof SalmonEntity) {
					preyAnimal = true;
					huntingRange = 26;
				}
			} else if (subtype == WARM_RIVER_GUARDIAN) {
				if (entity instanceof ParrotEntity) {
					preyAnimal = true;
				}
				huntingRange = 70;
			} else {

			}

		
			if (preyAnimal) {
				if (distanceSq < huntingRange) {
					parentEntity.setSilent(!(nearbyPlayer));
					return true;
				}
				return false;
			}

			if (entity instanceof AnimalEntity) {
				return false;
			}

			if (entity instanceof WaterMobEntity) {
				return false;
			}

			int aggressionRange = w.rand.nextInt(450) + 49;

			BlockPos pos = entity.getPosition();
			Biome biome = w.getBiome(pos);
			Category bC = biome.getCategory();
//			TempCategory tC = biome.getTempCategory();

			// more aggressive in some biomes
			if (bC == Category.OCEAN) {
				aggressionRange = aggressionRange + 49;
			}
			if (bC == Category.SWAMP) {
				aggressionRange = aggressionRange + 49;
			}

			// less aggressive in light, more aggressive in the dark
			int lightLevel = w.getLight(pos);
			aggressionRange = aggressionRange + ((10 - lightLevel) * 10);
			if (aggressionRange > distanceSq) {
				parentEntity.setSilent(!(nearbyPlayer));
				return true;
			}

			if (isRiverGuardianEntity) {
				RiverGuardianEntity e = (RiverGuardianEntity) entity;
				if (e.getSubType() == subtype) {
					aggressionRange -= 36;
				}
				if (aggressionRange > distanceSq) {
					parentEntity.setSilent(!(nearbyPlayer));
					return true;
		 		}
				return false;
			}

			if (aggressionRange > distanceSq) {
				if (entity instanceof PlayerEntity) {
					parentEntity.setSilent(false);
					return true;
				}
			}
			return false;

		}
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(SUB_TYPE, 0);

	}

	public int getSubType() {
		return dataManager.get(SUB_TYPE);
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("SubType", (byte) getSubType());
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		dataManager.set(SUB_TYPE, (int) compound.getByte("SubType"));
	}

}



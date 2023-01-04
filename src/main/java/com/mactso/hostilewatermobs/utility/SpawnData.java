package com.mactso.hostilewatermobs.utility;


import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.entities.ModEntities;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo;
import net.minecraftforge.common.world.ModifiableStructureInfo.StructureInfo;
import net.minecraftforge.common.world.StructureSettingsBuilder.StructureSpawnOverrideBuilder;


public class SpawnData {

	public static void onBiome(Holder<Biome> biome, BiomeInfo.Builder builderIn) {
		String threadname = Thread.currentThread().getName();
		int weight;
		int min;
		int max;
		int rgSC = MyConfig.getRiverGuardianSpawnChance();
		int gSC = MyConfig.getGurtySpawnChance();
		int sbSC = MyConfig.getSlipperyBiterSpawnChance();
		int wsSC = MyConfig.getWatersnakeSpawnChance();
		MobSpawnSettingsBuilder builder = builderIn.getMobSpawnSettings();
		String biomeCategory = Utility.getBiomeCategory(biome);
		
		
		if (biomeCategory == Utility.THEEND) {
			// the end is alien.
		} else if (biomeCategory == Utility.NETHER) {
			// no water in nether.
		} else if (biomeCategory == Utility.RIVER) {
			if (rgSC > 0)
				builder.addSpawn  (MobCategory.MONSTER, new SpawnerData(ModEntities.RIVER_GUARDIAN, weight = rgSC + (rgSC / 2) + 1, min = 1, max = 1));
			if (sbSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.SLIPPERY_BITER, weight = rgSC + (rgSC / 3) + 1, min = 1, max = 1));
			if (wsSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.WATER_SNAKE, weight = wsSC + (wsSC / 3) + 1, min = 1, max = 1));
			if (gSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.GURTY, weight = gSC, min = 1, max = 1));
			builder.addSpawn  (MobCategory.WATER_AMBIENT,new SpawnerData(EntityType.COD, weight = MyConfig.getCodSpawnBoost() / 3, min = 1, max = 2));
			builder.addSpawn  (MobCategory.WATER_AMBIENT,
					new SpawnerData(EntityType.SALMON, weight = MyConfig.getSalmonSpawnBoost() / 5, min = 1, max = 2));
			builder.addSpawn  (MobCategory.WATER_CREATURE, new SpawnerData(EntityType.SQUID, weight = MyConfig.getSquidSpawnBoost() / 2, min = 1, max = 2));
		} else if (biomeCategory == Utility.SWAMP) {
			builder.addSpawn  (MobCategory.WATER_AMBIENT,new SpawnerData(EntityType.COD, weight = MyConfig.getCodSpawnBoost() / 2, min = 1, max = 2));
			if (rgSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.RIVER_GUARDIAN, weight = (rgSC * 2 / 3) + 1, min = 1, max = 1));
			if (gSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.GURTY, weight = (gSC * 110) / 100, min = 1, max = 3));
			if (sbSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.SLIPPERY_BITER, weight = (sbSC * 2 / 3) + 1, min = 1, max = 1));
			if (wsSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.WATER_SNAKE, weight = wsSC + (wsSC / 3) + 1, min = 1, max = 4));
		} else if (biomeCategory == Utility.OCEAN) {
			if (rgSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.RIVER_GUARDIAN, weight = rgSC, min = 1, max = 1));
			if (gSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.GURTY, weight = (gSC / 2), min = 1, max = 1));
			if (sbSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.SLIPPERY_BITER, weight = sbSC, min = 1, max = 3));
			builder.addSpawn  (MobCategory.WATER_AMBIENT,new SpawnerData(EntityType.COD, weight = MyConfig.getCodSpawnBoost(), min = 2, max = 4));
			builder.addSpawn  (MobCategory.WATER_AMBIENT,new SpawnerData(EntityType.SALMON, weight = MyConfig.getSalmonSpawnBoost(), min = 2, max = 4));
			builder.addSpawn  (MobCategory.WATER_CREATURE,new SpawnerData(EntityType.SQUID, weight = MyConfig.getSquidSpawnBoost(), min = 1, max = 4));
			builder.addSpawn  (MobCategory.WATER_CREATURE,new SpawnerData(EntityType.DOLPHIN, weight = MyConfig.getDolphinSpawnboost(), min = 1, max = 2));
		} else if (biomeCategory == Utility.BEACH) {
			if (gSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.GURTY, weight = gSC, min = 1, max = 3));
			if (wsSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.WATER_SNAKE, weight = wsSC + (wsSC / 3) + 1, min = 1, max = 1));
		} else {
			if (rgSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.RIVER_GUARDIAN, weight = (rgSC / 8) + 1, min = 1, max = 1));
			if (gSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.GURTY, weight = (gSC / 8), min = 1, max = 1));
			if (sbSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.SLIPPERY_BITER, weight = (sbSC / 8) + 1, min = 1, max = 2));
			if (wsSC > 0)
				builder.addSpawn  (MobCategory.MONSTER,new SpawnerData(ModEntities.WATER_SNAKE, weight = wsSC/2, min = 1, max = 2));
		}
		
	}
	
	public static void onStructure(Holder<Structure> struct, StructureInfo.Builder builderIn) {

		String threadname = Thread.currentThread().getName();
		int weight;
		int min;
		int max;
		int rgSC = MyConfig.getRiverGuardianSpawnChance();
		int gSC = MyConfig.getGurtySpawnChance();
		int sbSC = MyConfig.getSlipperyBiterSpawnChance();
		int wsSC = MyConfig.getWatersnakeSpawnChance();
		Structure structure = struct.get();
		Optional<ResourceKey<Structure>> key = struct.unwrapKey();
		if (!key.isPresent())
			return;

		@Nullable
		StructureSpawnOverrideBuilder builder = builderIn.getStructureSettings()
				.getOrAddSpawnOverrides(MobCategory.MONSTER);
		if (key.get().location().getNamespace().equals("minecraft")) {
			if (structure == StructurePieceType.OCEAN_RUIN) {
				if (rgSC > 0)
					builder.addSpawn(new SpawnerData(ModEntities.RIVER_GUARDIAN, weight = rgSC, min = 1, max = 1));
				if (sbSC > 0)
					builder.addSpawn(
							new SpawnerData(ModEntities.SLIPPERY_BITER, weight = (sbSC / 2) + 1, min = 1, max = 1));
			} else if (structure == StructurePieceType.SHIPWRECK_PIECE) {
				if (rgSC > 0)
					builder.addSpawn(new SpawnerData(ModEntities.RIVER_GUARDIAN, weight = rgSC, min = 1, max = 1));
				if (sbSC > 0)
					builder.addSpawn(
							new SpawnerData(ModEntities.SLIPPERY_BITER, weight = (sbSC / 2) + 1, min = 1, max = 1));
			} else if (structure == StructurePieceType.BURIED_TREASURE_PIECE) {
				if (rgSC > 0)
					builder.addSpawn(new SpawnerData(ModEntities.RIVER_GUARDIAN, weight = rgSC, min = 1, max = 1));
				if (gSC > 0)
					builder.addSpawn(new SpawnerData(ModEntities.GURTY, (gSC / 2) + 1, min = 1, max = 3));
				if (sbSC > 0)
					builder.addSpawn(
							new SpawnerData(ModEntities.SLIPPERY_BITER, weight = (sbSC / 2) + 1, min = 1, max = 1));
			} else if (structure == StructurePieceType.SWAMPLAND_HUT) {
				if (rgSC > 0)
					builder.addSpawn(
							new SpawnerData(ModEntities.RIVER_GUARDIAN, weight = (rgSC / 3) + 1, min = 1, max = 1));
				if (gSC > 0)
					builder.addSpawn(new SpawnerData(ModEntities.GURTY, weight = (gSC / 3) + 1, min = 1, max = 3));
				if (sbSC > 0)
					builder.addSpawn(new SpawnerData(ModEntities.SLIPPERY_BITER, (sbSC / 3) + 1, min = 1, max = 1));
				if (wsSC > 0)
					builder.addSpawn(
							new SpawnerData(ModEntities.WATER_SNAKE, weight = (wsSC / 3) + 1, min = 1, max = 1));

			} else if (structure == StructurePieceType.RUINED_PORTAL) {
				if (rgSC > 0)
					builder.addSpawn(
							new SpawnerData(ModEntities.RIVER_GUARDIAN, weight = (rgSC / 2) + 1, min = 1, max = 1));
				if (gSC > 0)
					builder.addSpawn(new SpawnerData(ModEntities.GURTY, weight = (gSC / 2) + 1, min = 1, max = 3));
				if (sbSC > 0)
					builder.addSpawn(
							new SpawnerData(ModEntities.SLIPPERY_BITER, weight = (sbSC / 3) + 1, min = 1, max = 1));
				if (wsSC > 0)
					builder.addSpawn(
							new SpawnerData(ModEntities.WATER_SNAKE, weight = (wsSC / 3) + 1, min = 1, max = 1));
			}
		} else {
			if (MyConfig.getModStructureBoost() > 0) {
				weight = MyConfig.getModStructureBoost();
				builder.addSpawn(new SpawnerData(EntityType.SKELETON, weight, min = 1, max = 3));
				builder.addSpawn(new SpawnerData(EntityType.ZOMBIE, 1 + weight / 2, min = 1, max = 3));
				builder.addSpawn(new SpawnerData(EntityType.WITCH, 1 + weight / 6, min = 1, max = 3));
			}
		}
	}
	
}

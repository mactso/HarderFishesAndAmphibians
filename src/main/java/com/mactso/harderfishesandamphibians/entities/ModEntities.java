package com.mactso.harderfishesandamphibians.entities;

import com.mactso.harderfishesandamphibians.Main;
import com.mactso.harderfishesandamphibians.renderer.RiverGuardianRenderer;
import com.mactso.harderfishesandamphibians.util.SpawnData;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;

public class ModEntities {
//	   public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = register("elder_guardian", EntityType.Builder.<ElderGuardianEntity>create(ElderGuardianEntity::new, EntityClassification.MONSTER).size(1.9975F, 1.9975F).func_233606_a_(10));
//	   public static final EntityType<GuardianEntity> GUARDIAN = register("guardian", EntityType.Builder.<GuardianEntity>create(GuardianEntity::new, EntityClassification.MONSTER).size(0.85F, 0.85F).func_233606_a_(8));
	 
	public static final EntityType<RiverGuardianEntity> RIVER_GUARDIAN = register("river_guardian", EntityType.Builder.create(RiverGuardianEntity::new, EntityClassification.MONSTER).size(0.6F, 0.6F));//.func_233606_a_(5));

	private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder)
	{
		System.out.println(Main.MODID);
		System.out.println(name);
		String key = Main.MODID + ":" + name;
		EntityType<T> type = builder.build(key);
		type.setRegistryName(key);
		return type;
	}

	public static void register(IForgeRegistry<EntityType<?>> forgeRegistry)
	{
		forgeRegistry.registerAll(RIVER_GUARDIAN);

		EntitySpawnPlacementRegistry.register(RIVER_GUARDIAN, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RiverGuardianEntity::canSpawn);

		GlobalEntityTypeAttributes.put(RIVER_GUARDIAN, RiverGuardianEntity.registerAttributes().create());
	}

	@OnlyIn(Dist.CLIENT)
	public static void register(EntityRendererManager renderManager)
	{
		renderManager.register(RIVER_GUARDIAN, new RiverGuardianRenderer(renderManager));
		
	}

	public static void addSpawnData()
	{

		SpawnData.copyBiomeSpawn(EntityType.SALMON, RIVER_GUARDIAN, 50, 1, 3);
	

	}
	
	public static void removeSpawnData()
	{
		SpawnData.removeBiomeSpawn(RIVER_GUARDIAN);
	}
}

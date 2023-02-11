package com.mactso.hostilewatermobs.entities;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.renderer.GurtyRenderer;
//import com.mactso.hostilewatermobs.client.renderer.SlipperyBiterRenderer;
//import com.mactso.hostilewatermobs.client.renderer.WaterSnakeRenderer;
import com.mactso.hostilewatermobs.client.renderer.RiverGuardianRenderer;
import com.mactso.hostilewatermobs.client.renderer.SlipperyBiterRenderer;
import com.mactso.hostilewatermobs.client.renderer.WaterSnakeRenderer;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModEntities {

	public static final EntityType<RiverGuardian> RIVER_GUARDIAN = register("riverguardian",
			EntityType.Builder.of(RiverGuardian::new, MobCategory.MONSTER).sized(0.85F, 0.85F).clientTrackingRange(5));

	public static final EntityType<SlipperyBiter> SLIPPERY_BITER = register("slipperybiter",
			EntityType.Builder.of(SlipperyBiter::new, MobCategory.MONSTER).sized(0.9F, 0.7F).clientTrackingRange(21));

	public static final EntityType<Gurty> GURTY = register("gurty",
			EntityType.Builder.of(Gurty::new, MobCategory.MONSTER).sized(1.1F, 1.0F).clientTrackingRange(21));

	public static final EntityType<WaterSnake> WATER_SNAKE = register("watersnake",
			EntityType.Builder.of(WaterSnake::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(21));

	private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {

		String key = Main.MODID + ":" + name;
		EntityType<T> type = builder.build(key);

		return type;
	}

//	private static SoundEvent create(String key)
//	{
//		ResourceLocation res = new ResourceLocation(Main.MODID, key);
//		SoundEvent ret = new SoundEvent(res);
//		return ret;
//	}

	public static void register(IForgeRegistry<EntityType<?>> forgeRegistry) {

		forgeRegistry.register("riverguardian", RIVER_GUARDIAN);
		forgeRegistry.register("gurty", GURTY);
		forgeRegistry.register("slipperybiter", SLIPPERY_BITER);
		forgeRegistry.register("watersnake", WATER_SNAKE);

		SpawnPlacements.register(RIVER_GUARDIAN, SpawnPlacements.Type.IN_WATER,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RiverGuardian:: canSpawn);

		SpawnPlacements.register(GURTY, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				Gurty::canSpawn);

		SpawnPlacements.register(SLIPPERY_BITER, SpawnPlacements.Type.IN_WATER,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SlipperyBiter::canSpawn);

		SpawnPlacements.register(WATER_SNAKE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				WaterSnake::canSpawn);
	}

	public static void onAttribute(final EntityAttributeCreationEvent event) {
		event.put(RIVER_GUARDIAN, RiverGuardian.createAttributes().build());
		event.put(GURTY, Gurty.createAttributes().build());
		event.put(SLIPPERY_BITER, SlipperyBiter.createAttributes().build());
		event.put(WATER_SNAKE, WaterSnake.createAttributes().build());

	}

	@OnlyIn(Dist.CLIENT)
	public static void registerEntityRenderers(final RegisterRenderers event) {
		// event.registerEntityRenderer(NASTY_SKELETON, NastySkeletonRenderer::new);

		event.registerEntityRenderer(RIVER_GUARDIAN, RiverGuardianRenderer::new);
		event.registerEntityRenderer(GURTY, GurtyRenderer::new);
		event.registerEntityRenderer(SLIPPERY_BITER, SlipperyBiterRenderer::new);
		event.registerEntityRenderer(WATER_SNAKE, WaterSnakeRenderer::new);

	}

}

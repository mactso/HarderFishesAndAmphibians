package com.mactso.hostilewatermobs.client.renderer;


import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.WaterSnakeModel;
import com.mactso.hostilewatermobs.client.renderer.layers.WaterSnakeAngryLayer;
import com.mactso.hostilewatermobs.entities.WaterSnakeEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterSnakeRenderer extends MobRenderer<WaterSnakeEntity, WaterSnakeModel<WaterSnakeEntity>> {
	private WaterSnakeEntity watersnakeIn;
//		BiomeMaker b;

	private static final ResourceLocation WATER_SNAKE_TEXTURES = new ResourceLocation(Main.MODID,
			"textures/entity/classicsnake_texture.png");


	public WaterSnakeRenderer(final EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new WaterSnakeModel<>(), 0.3f);
        addLayer(new WaterSnakeAngryLayer<>(this));
	}

	protected void scale(WaterSnakeEntity entityIn, PoseStack matrixStackIn,
		  float partialTickTime) 
	{

		  int eSize =  entityIn.getId()%16-8;
		  watersnakeIn = entityIn;
		  boolean watersnakeAngry = watersnakeIn.isAngry();
		  
		  int angrySize = 0;
		  if (watersnakeAngry) {
			  angrySize += 1;
		  }
		  float uniqueSize = 0.4f + (float)(eSize+angrySize)/(32);

		  matrixStackIn.scale(WaterSnakeEntity.SIZE + uniqueSize, WaterSnakeEntity.SIZE+uniqueSize, WaterSnakeEntity.SIZE+uniqueSize);

	}

	public void render(WaterSnakeEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int packedLightIn) {

		float f = entityIn.yRotO;
		float lerpYaw = Mth.lerp(partialTicks, f, entityYaw);
		super.render(entityIn, lerpYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(WaterSnakeEntity entity) {
		return WATER_SNAKE_TEXTURES;
	}
}
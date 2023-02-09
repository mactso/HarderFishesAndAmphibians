package com.mactso.hostilewatermobs.client.renderer;


import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.WaterSnakeModel;
import com.mactso.hostilewatermobs.client.renderer.layers.WaterSnakeAngryLayer;
import com.mactso.hostilewatermobs.entities.WaterSnake;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterSnakeRenderer extends MobRenderer<WaterSnake, WaterSnakeModel<WaterSnake>> {
	private WaterSnake watersnakeIn;
//		BiomeMaker b;

	private static final ResourceLocation WATER_SNAKE_TEXTURES = new ResourceLocation(Main.MODID,
			"textures/entity/watersnake_texture.png");


	public WaterSnakeRenderer(final EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new WaterSnakeModel<>(), 0.3f);
        addLayer(new WaterSnakeAngryLayer<>(this));
	}

	protected void scale(WaterSnake entityIn, MatrixStack matrixStackIn,
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

		  matrixStackIn.scale(WaterSnake.SIZE + uniqueSize, WaterSnake.SIZE+uniqueSize, WaterSnake.SIZE+uniqueSize);

	}

	public void render(WaterSnake entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int packedLightIn) {

		float f = entityIn.yRotO;
		float lerpYaw = MathHelper.lerp(partialTicks, f, entityYaw);
		super.render(entityIn, lerpYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(WaterSnake entity) {
		return WATER_SNAKE_TEXTURES;
	}
}
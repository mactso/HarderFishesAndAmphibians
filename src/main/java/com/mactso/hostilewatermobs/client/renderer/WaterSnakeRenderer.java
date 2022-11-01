package com.mactso.hostilewatermobs.client.renderer;


import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.WaterSnakeModel;
import com.mactso.hostilewatermobs.client.renderer.layers.WaterSnakeGlowingLayer;
import com.mactso.hostilewatermobs.entities.WaterSnake;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WaterSnakeRenderer extends MobRenderer<WaterSnake, WaterSnakeModel<WaterSnake>> {

	private static final ResourceLocation WATER_SNAKE_TEXTURES = new ResourceLocation(Main.MODID,
			"textures/entity/watersnake_texture.png");
	private static final ResourceLocation WATER_SNAKE_ANGRY_LAYER = new ResourceLocation(Main.MODID,
			"textures/entity/watersnake_angry_texture.png");

	public WaterSnakeRenderer(EntityRendererProvider.Context ctx) {

		super(ctx, new WaterSnakeModel<>(ctx.bakeLayer(WaterSnakeModel.LAYER_LOCATION)), 0.3f);
		this.addLayer(new WaterSnakeGlowingLayer<WaterSnake, WaterSnakeModel<WaterSnake>>(this));
	}

	protected void scale(WaterSnake entityIn, PoseStack matrixStackIn, float partialTickTime) {
		float uniqueSize = entityIn.getId() % 16 - 8;
		if (((WaterSnake) entityIn).isAngry()) {
			uniqueSize++;
		}
		uniqueSize = 0.4f + (float) (uniqueSize) / (16);
		matrixStackIn.scale(WaterSnake.SIZE + uniqueSize, WaterSnake.SIZE + uniqueSize, WaterSnake.SIZE + uniqueSize);

	}

	public void render(WaterSnake entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int packedLightIn) {

		float f = entityIn.yRotO;
		float lerpYaw = Mth.lerp(partialTicks, f, entityYaw);
		super.render(entityIn, lerpYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(WaterSnake entity) {
		return WATER_SNAKE_TEXTURES;
	}
}
package com.mactso.hostilewatermobs.client.renderer.layers;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.WaterSnakeModel;
import com.mactso.hostilewatermobs.entities.WaterSnake;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class WaterSnakeGlowingLayer<T extends WaterSnake, M extends WaterSnakeModel<T>> extends EyesLayer<T, M> {
	private static final RenderType RENDER_TYPE = RenderType
			.eyes(new ResourceLocation(Main.MODID, "textures/entity/watersnake_angry_texture.png"));

	public WaterSnakeGlowingLayer(RenderLayerParent<T, M> renderer) {
		super(renderer);
	}

	// int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch
	@Override 
	public void render(PoseStack posestack, MultiBufferSource mbs, int packedLightIn, T p_225628_4_,
			float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_,
			float p_225628_10_) {
		// TODO Auto-generated method stub
		WaterSnake watersnakeIn = (WaterSnake) p_225628_4_;
		if (watersnakeIn.isAngry()) {
			super.render(posestack, mbs, packedLightIn, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_,
					p_225628_8_, p_225628_9_, p_225628_10_);
		}
	}

	@Override
	public RenderType renderType() {
		return RENDER_TYPE;
	}
}

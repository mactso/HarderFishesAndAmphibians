package com.mactso.hostilewatermobs.client.renderer.layers;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.WaterSnakeModel;
import com.mactso.hostilewatermobs.entities.WaterSnakeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.resources.ResourceLocation;

public class WaterSnakeAngryLayer<T extends WaterAnimal, M extends WaterSnakeModel<T>> extends EyesLayer<T, M> {
	private static final RenderType RENDER_TYPE = RenderType
			.eyes(new ResourceLocation(Main.MODID, "textures/entity/classicsnake_angry_texture.png"));

	public WaterSnakeAngryLayer(RenderLayerParent<T, M> renderer) {
		super(renderer);
	}

	@Override
	public void render(PoseStack p_225628_1_, MultiBufferSource p_225628_2_, int p_225628_3_, T p_225628_4_,
			float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_,
			float p_225628_10_) {
		// TODO Auto-generated method stub
		WaterSnakeEntity classicSnakeIn = (WaterSnakeEntity) p_225628_4_;
		if (classicSnakeIn.isAngry()) {
			super.render(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_,
					p_225628_8_, p_225628_9_, p_225628_10_);
		}
	}

	@Override
	public RenderType renderType() {
		return RENDER_TYPE;
	}
}


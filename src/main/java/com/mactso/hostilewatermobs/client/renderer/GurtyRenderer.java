package com.mactso.hostilewatermobs.client.renderer;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.GurtyModel;
import com.mactso.hostilewatermobs.client.renderer.layers.GlowingLayer;
import com.mactso.hostilewatermobs.client.renderer.layers.GurtyGlowingLayer;
import com.mactso.hostilewatermobs.entities.Gurty;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GurtyRenderer extends MobRenderer<Gurty, GurtyModel<Gurty>> {

	private static final ResourceLocation GURTY_TEXTURES = new ResourceLocation(Main.MODID,
			"textures/entity/gurty_texture.png");
	private static final ResourceLocation GURTY_ANGRY_LAYER = new ResourceLocation(Main.MODID, "textures/entity/gurty_angry_texture.png");

	// suspect this is the size but may just be shadow size
	public GurtyRenderer(EntityRendererProvider.Context ctx) {
		
		super(ctx, new GurtyModel<>(ctx.bakeLayer(GurtyModel.LAYER_LOCATION)), 0.3f);
		this.addLayer(new GurtyGlowingLayer<Gurty, GurtyModel<Gurty>>(this));
	}

	protected void scale(Gurty entityIn, PoseStack matrixStackIn, float partialTickTime) 
	{
			  float uniqueSize =  entityIn.getId()%16-8;
			  if (((Gurty) entityIn).isAngry()) {
				  uniqueSize++;
			  }
			  uniqueSize =0.4f+ (float)(uniqueSize)/(16);
			  matrixStackIn.scale(Gurty.SIZE+uniqueSize, Gurty.SIZE+uniqueSize, Gurty.SIZE+uniqueSize);

	}

	public void render(Gurty entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int packedLightIn) {
		super.render((Gurty) entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(Gurty entity) {
		return GURTY_TEXTURES;
	}
}


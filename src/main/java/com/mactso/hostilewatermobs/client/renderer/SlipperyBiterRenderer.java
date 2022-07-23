package com.mactso.hostilewatermobs.client.renderer;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.SlipperyBiterModel;
import com.mactso.hostilewatermobs.client.renderer.layers.GlowingLayer;
import com.mactso.hostilewatermobs.entities.SlipperyBiterEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;



public class SlipperyBiterRenderer extends MobRenderer<SlipperyBiterEntity, EntityModel<SlipperyBiterEntity>> {

	private static final ResourceLocation SlIPPERY_BITER_TEXTURES 
		= new ResourceLocation(Main.MODID,	"textures/entity/slipperybiter.png");
    private static final ResourceLocation SLIPPERY_BITER_EYES = new ResourceLocation(Main.MODID, "textures/entity/slipperybiter_eyes.png");

	// suspect this is the size but may just be shadow size
	public SlipperyBiterRenderer(EntityRendererProvider.Context renderManagerIn) {
		super(renderManagerIn, new SlipperyBiterModel<>(), 0.3f);
		this.addLayer(new GlowingLayer<>(this, SLIPPERY_BITER_EYES));
	}

	protected void scale(SlipperyBiterEntity entityIn, PoseStack matrixStackIn,
			float partialTickTime) {
		  int subtype = ((SlipperyBiterEntity) entityIn).getSubType();
		  int eSize = (((SlipperyBiterEntity) entityIn).getId()%16)-8;
		  float uniqueSize = (float)eSize/48;
		  subtype = (2 + subtype) %2;
		  if (subtype == SlipperyBiterEntity.LARGE_SLIPPERY_BITER) {
				matrixStackIn.scale(SlipperyBiterEntity.LARGE_SIZE+uniqueSize+0.4f, SlipperyBiterEntity.LARGE_SIZE+uniqueSize+0.35f, SlipperyBiterEntity.LARGE_SIZE+uniqueSize+0.4f);
		  } else {
				matrixStackIn.scale(SlipperyBiterEntity.SIZE+uniqueSize, SlipperyBiterEntity.SIZE+uniqueSize, SlipperyBiterEntity.SIZE+uniqueSize);
		  }
	}

	public void render(SlipperyBiterEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int packedLightIn) {
		super.render((SlipperyBiterEntity) entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(SlipperyBiterEntity entity) {
		return SlIPPERY_BITER_TEXTURES;
	}
}

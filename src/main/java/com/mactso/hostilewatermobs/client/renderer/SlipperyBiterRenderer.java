package com.mactso.hostilewatermobs.client.renderer;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.GurtyModel;
import com.mactso.hostilewatermobs.client.model.SlipperyBiterModel;
import com.mactso.hostilewatermobs.client.renderer.layers.SlipperyBiterGlowingLayer;
import com.mactso.hostilewatermobs.entities.SlipperyBiter;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlipperyBiterRenderer extends MobRenderer<SlipperyBiter, SlipperyBiterModel<SlipperyBiter>> {

//		BiomeMaker b;
	// public static final ResourceLocation ALBINO_RIVER_GUARDIAN_TEXTURE = new
	// ResourceLocation(Main.MODID , "textures/entity/albino_river_guardian.png");
	private static final ResourceLocation SlIPPERY_BITER_TEXTURES = new ResourceLocation(Main.MODID,
			"textures/entity/slipperybiter.png");
	private static final ResourceLocation SlIPPERY_BITER_GLOWING_LAYER = new ResourceLocation(Main.MODID,"textures/entity/slipperybiter_eyes.png");
	// suspect this is the size but may just be shadow size
	// suspect this is the size but may just be shadow size
	public SlipperyBiterRenderer(EntityRendererProvider.Context ctx) {
		
		super(ctx, new SlipperyBiterModel<>(ctx.bakeLayer(SlipperyBiterModel.LAYER_LOCATION)), 0.3f);
		this.addLayer(new SlipperyBiterGlowingLayer<SlipperyBiter, SlipperyBiterModel<SlipperyBiter>>(this));
	}

	protected void scale(SlipperyBiter entityIn, PoseStack matrixStackIn,
			float partialTickTime) {
		  int subtype = ((SlipperyBiter) entityIn).getSubType();
		  int eSize = (((SlipperyBiter) entityIn).getId()%16)-8;
		  float uniqueSize = (float)eSize/48;
		  subtype = (2 + subtype) %2;
		  if (subtype == SlipperyBiter.LARGE_SLIPPERY_BITER) {
//			  System.out.println ("Large ("+SlipperyBiterEntity.LARGE_SIZE+") + "+uniqueSize);
				matrixStackIn.scale(SlipperyBiter.LARGE_SIZE+uniqueSize+0.4f, SlipperyBiter.LARGE_SIZE+uniqueSize+0.35f, SlipperyBiter.LARGE_SIZE+uniqueSize+0.4f);
		  } else {
//			  System.out.println ("Normal ("+SlipperyBiterEntity.SIZE+") + " + uniqueSize);
				matrixStackIn.scale(SlipperyBiter.SIZE+uniqueSize, SlipperyBiter.SIZE+uniqueSize, SlipperyBiter.SIZE+uniqueSize);
			  
		  }
	}

	public void render(SlipperyBiter entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int packedLightIn) {
		super.render((SlipperyBiter) entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(SlipperyBiter entity) {
		return SlIPPERY_BITER_TEXTURES;
	}

}

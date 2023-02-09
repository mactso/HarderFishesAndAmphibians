package com.mactso.hostilewatermobs.client.renderer;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.SlipperyBiterModel;
import com.mactso.hostilewatermobs.client.renderer.layers.SlipperyBiterEyesLayer;
import com.mactso.hostilewatermobs.entities.SlipperyBiter;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlipperyBiterRenderer extends MobRenderer<SlipperyBiter, SlipperyBiterModel<SlipperyBiter>> {

	private static final ResourceLocation SlIPPERY_BITER_TEXTURES = new ResourceLocation(Main.MODID,
			"textures/entity/slipperybiter.png");

	// suspect this is the size but may just be shadow size
	public SlipperyBiterRenderer(final EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new SlipperyBiterModel<>(), 0.3f);
        addLayer(new SlipperyBiterEyesLayer<>(this));
	}

	protected void scale(SlipperyBiter entityIn, MatrixStack matrixStackIn,
			float partialTickTime) {
		  int subtype = ((SlipperyBiter) entityIn).getSubType();
		  int eSize = (((SlipperyBiter) entityIn).getId()%16)-8;
		  float uniqueSize = (float)eSize/48;
		  subtype = (2 + subtype) %2;
		  if (subtype == SlipperyBiter.LARGE_SLIPPERY_BITER) {
				matrixStackIn.scale(SlipperyBiter.LARGE_SIZE+uniqueSize+0.4f, SlipperyBiter.LARGE_SIZE+uniqueSize+0.35f, SlipperyBiter.LARGE_SIZE+uniqueSize+0.4f);
		  } else {
				matrixStackIn.scale(SlipperyBiter.SIZE+uniqueSize, SlipperyBiter.SIZE+uniqueSize, SlipperyBiter.SIZE+uniqueSize);
			  
		  }
	}

	public void render(SlipperyBiter entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render((SlipperyBiter) entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(SlipperyBiter entity) {
		return SlIPPERY_BITER_TEXTURES;
	}
}

package com.mactso.hostilewatermobs.client.renderer;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.SlipperyBiterModel;
import com.mactso.hostilewatermobs.client.renderer.layers.SlipperyBiterEyesLayer;
import com.mactso.hostilewatermobs.entities.SlipperyBiterEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlipperyBiterRenderer extends MobRenderer<SlipperyBiterEntity, SlipperyBiterModel<SlipperyBiterEntity>> {

//		BiomeMaker b;
	// public static final ResourceLocation ALBINO_RIVER_GUARDIAN_TEXTURE = new
	// ResourceLocation(Main.MODID , "textures/entity/albino_river_guardian.png");
	private static final ResourceLocation SlIPPERY_BITER_TEXTURES = new ResourceLocation(Main.MODID,
			"textures/entity/slipperybiter.png");

	// suspect this is the size but may just be shadow size
	public SlipperyBiterRenderer(final EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new SlipperyBiterModel<>(), 0.3f);
        addLayer(new SlipperyBiterEyesLayer<>(this));
	}

	protected void preRenderCallback(SlipperyBiterEntity entityIn, MatrixStack matrixStackIn,
			float partialTickTime) {
		  int subtype = ((SlipperyBiterEntity) entityIn).getSubType();
		  int eSize = (((SlipperyBiterEntity) entityIn).getEntityId()%16)-8;
		  float uniqueSize = (float)eSize/48;
		  subtype = (2 + subtype) %2;
		  if (subtype == SlipperyBiterEntity.LARGE_SLIPPERY_BITER) {
//			  System.out.println ("Large ("+SlipperyBiterEntity.LARGE_SIZE+") + "+uniqueSize);
				matrixStackIn.scale(SlipperyBiterEntity.LARGE_SIZE+uniqueSize+0.4f, SlipperyBiterEntity.LARGE_SIZE+uniqueSize+0.35f, SlipperyBiterEntity.LARGE_SIZE+uniqueSize+0.4f);
		  } else {
//			  System.out.println ("Normal ("+SlipperyBiterEntity.SIZE+") + " + uniqueSize);
				matrixStackIn.scale(SlipperyBiterEntity.SIZE+uniqueSize, SlipperyBiterEntity.SIZE+uniqueSize, SlipperyBiterEntity.SIZE+uniqueSize);
			  
		  }
	}

	public void render(SlipperyBiterEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render((SlipperyBiterEntity) entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getEntityTexture(SlipperyBiterEntity entity) {
		return SlIPPERY_BITER_TEXTURES;
	}
}

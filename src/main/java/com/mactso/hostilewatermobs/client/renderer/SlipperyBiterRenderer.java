package com.mactso.hostilewatermobs.client.renderer;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.SlipperyBiterModel;
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
			"textures/entity/slipperybitertexture.png");

	// suspect this is the size but may just be shadow size
	public SlipperyBiterRenderer(final EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new SlipperyBiterModel<>(), 0.3f);
	}

	protected void preRenderCallback(SlipperyBiterEntity entitylivingbaseIn, MatrixStack matrixStackIn,
			float partialTickTime) {
		matrixStackIn.scale(SlipperyBiterEntity.SIZE, SlipperyBiterEntity.SIZE, SlipperyBiterEntity.SIZE);
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

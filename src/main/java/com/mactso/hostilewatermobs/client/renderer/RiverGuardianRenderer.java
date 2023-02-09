package com.mactso.hostilewatermobs.client.renderer;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.entities.RiverGuardian;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RiverGuardianRenderer extends GuardianRenderer {

	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation(Main.MODID, "textures/entity/riverguardian.png"),
			new ResourceLocation(Main.MODID, "textures/entity/cold_riverguardian.png"),
			new ResourceLocation(Main.MODID, "textures/entity/warm_riverguardian.png"),
			new ResourceLocation(Main.MODID, "textures/entity/albino_riverguardian.png") };

	public RiverGuardianRenderer(EntityRendererProvider.Context renderManagerIn) {
		super(renderManagerIn);
	}

	protected void scale(Guardian entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {

		matrixStackIn.scale(RiverGuardian.ELDER_SIZE_SCALE, RiverGuardian.ELDER_SIZE_SCALE,
				RiverGuardian.ELDER_SIZE_SCALE);
	}

	@Override
	public void render(Guardian entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int packedLightIn) {
		// TODO Auto-generated method stub
		super.render((Guardian) entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	public ResourceLocation getTextureLocation(Guardian entity) {
		int subtype = ((RiverGuardian) entity).getSubType();
		subtype = (4 + subtype) % 4;
		return TEXTURES[subtype];
	}
}

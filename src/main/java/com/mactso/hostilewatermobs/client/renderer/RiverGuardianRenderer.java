package com.mactso.hostilewatermobs.client.renderer;
import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.entities.RiverGuardian;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RiverGuardianRenderer extends GuardianRenderer {

	
	private static final ResourceLocation[] TEXTURES = {
			   new ResourceLocation(Main.MODID , "textures/entity/riverguardian.png"),
			   new ResourceLocation(Main.MODID , "textures/entity/cold_riverguardian.png"),
			   new ResourceLocation(Main.MODID , "textures/entity/warm_riverguardian.png"),	
			   new ResourceLocation(Main.MODID , "textures/entity/albino_riverguardian.png")
	};
   //   private static final ResourceLocation RIVER_GUARDIAN_BEAM_TEXTURE = new ResourceLocation(Main.MODID , "textures/entity/river_guardian_beam.png");
//   private static final RenderType BEAM_RENDER_TYPE = RenderType.getEntityCutoutNoCull(RIVER_GUARDIAN_BEAM_TEXTURE);
   
   // suspect this is the size but may just be shadow size
   public RiverGuardianRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, RiverGuardian.ELDER_SIZE_SCALE);
   }

   protected void scale(GuardianEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
	   
      matrixStackIn.scale(RiverGuardian.ELDER_SIZE_SCALE, RiverGuardian.ELDER_SIZE_SCALE, RiverGuardian.ELDER_SIZE_SCALE);
   }
@Override
	public void render(GuardianEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render((GuardianEntity) entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}
   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getTextureLocation(GuardianEntity entity) {
	  int subtype = ((RiverGuardian) entity).getSubType();
	  subtype = (4 + subtype) %4;
	  return TEXTURES [subtype];
   }
}

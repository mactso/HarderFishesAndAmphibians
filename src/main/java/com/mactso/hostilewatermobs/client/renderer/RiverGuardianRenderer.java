package com.mactso.hostilewatermobs.client.renderer;
import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.entities.RiverGuardianEntity;
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

	
//   public static final ResourceLocation ALBINO_RIVER_GUARDIAN_TEXTURE = new ResourceLocation(Main.MODID , "textures/entity/albino_river_guardian.png");
//   public static final ResourceLocation RIVER_GUARDIAN_TEXTURE = new ResourceLocation(Main.MODID , "textures/entity/river_guardian.png");
//   public static final ResourceLocation COLD_RIVER_GUARDIAN_TEXTURE = new ResourceLocation(Main.MODID , "textures/entity/cold_river_guardian.png");
//   public static final ResourceLocation WARM_RIVER_GUARDIAN_TEXTURE = new ResourceLocation(Main.MODID , "textures/entity/warm_river_guardian.png");
	private static final ResourceLocation[] TEXTURES = {
			   new ResourceLocation(Main.MODID , "textures/entity/river_guardian.png"),
			   new ResourceLocation(Main.MODID , "textures/entity/cold_river_guardian.png"),
			   new ResourceLocation(Main.MODID , "textures/entity/warm_river_guardian.png"),	
			   new ResourceLocation(Main.MODID , "textures/entity/albino_river_guardian.png")
	};
   //   private static final ResourceLocation RIVER_GUARDIAN_BEAM_TEXTURE = new ResourceLocation(Main.MODID , "textures/entity/river_guardian_beam.png");
//   private static final RenderType BEAM_RENDER_TYPE = RenderType.getEntityCutoutNoCull(RIVER_GUARDIAN_BEAM_TEXTURE);
   
   // suspect this is the size but may just be shadow size
   public RiverGuardianRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, RiverGuardianEntity.ELDER_SIZE_SCALE);
   }

   protected void scale(GuardianEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
	   
      matrixStackIn.scale(RiverGuardianEntity.ELDER_SIZE_SCALE, RiverGuardianEntity.ELDER_SIZE_SCALE, RiverGuardianEntity.ELDER_SIZE_SCALE);
   }
@Override
	public void render(GuardianEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int packedLightIn) {
		// TODO Auto-generated method stub
		super.render((GuardianEntity) entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}
   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getTextureLocation(GuardianEntity entity) {
	  int subtype = ((RiverGuardianEntity) entity).getSubType();
	  subtype = (4 + subtype) %4;
	  return TEXTURES [subtype];
   }
}

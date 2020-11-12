package com.mactso.hostilewatermobs.renderer;
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
//   private static final RenderType field_229107_h_ = RenderType.getEntityCutoutNoCull(RIVER_GUARDIAN_BEAM_TEXTURE);
   
   // suspect this is the size but may just be shadow size
   public RiverGuardianRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, RiverGuardianEntity.field_213629_b);
   }

   protected void preRenderCallback(GuardianEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
	   
      matrixStackIn.scale(RiverGuardianEntity.field_213629_b, RiverGuardianEntity.field_213629_b, RiverGuardianEntity.field_213629_b);
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
   public ResourceLocation getEntityTexture(GuardianEntity entity) {
	  int subtype = ((RiverGuardianEntity) entity).getSubType();
	  subtype = (4 + subtype) %4;
	  return TEXTURES [subtype];
   }
}

package com.mactso.harderfishesandamphibians.renderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;

import com.mactso.harderfishesandamphibians.Main;
import com.mactso.harderfishesandamphibians.entities.RiverGuardianEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RiverGuardianRenderer extends GuardianRenderer {
   public static final ResourceLocation RIVER_GUARDIAN_TEXTURE = new ResourceLocation(Main.MODID , "textures/entity/river_guardian.png");
   private static final ResourceLocation RIVER_GUARDIAN_BEAM_TEXTURE = new ResourceLocation(Main.MODID , "textures/entity/river_guardian_beam.png");
   private static final RenderType field_229107_h_ = RenderType.getEntityCutoutNoCull(RIVER_GUARDIAN_BEAM_TEXTURE);
   
   // suspect this is the size but may just be shadow size
   public RiverGuardianRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, RiverGuardianEntity.field_213629_b);
   }

   protected void preRenderCallback(GuardianEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
	   
      matrixStackIn.scale(RiverGuardianEntity.field_213629_b, RiverGuardianEntity.field_213629_b, RiverGuardianEntity.field_213629_b);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(GuardianEntity entity) {
      return RIVER_GUARDIAN_TEXTURE;
   }
}

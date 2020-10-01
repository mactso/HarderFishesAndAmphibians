package com.mactso.harderfishesandamphibians.renderer;
import net.minecraft.client.renderer.entity.*;

import com.mactso.harderfishesandamphibians.entities.RiverGuardianEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RiverGuardianRenderer extends GuardianRenderer {
   public static final ResourceLocation GUARDIAN_ELDER_TEXTURE = new ResourceLocation("textures/entity/guardian_elder.png");
  
   // suspect this is the size but may just be shadow size
   public RiverGuardianRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, 0.75F);
   }

   protected void preRenderCallback(GuardianEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
	   
      matrixStackIn.scale(RiverGuardianEntity.field_213629_b, RiverGuardianEntity.field_213629_b, ElderGuardianEntity.field_213629_b);
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(GuardianEntity entity) {
      return GUARDIAN_ELDER_TEXTURE;
   }
}

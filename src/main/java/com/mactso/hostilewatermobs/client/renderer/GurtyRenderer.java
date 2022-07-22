package com.mactso.hostilewatermobs.client.renderer;


import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.GurtyModel;
import com.mactso.hostilewatermobs.client.renderer.layers.GurtyAngryLayer;
import com.mactso.hostilewatermobs.entities.GurtyEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GurtyRenderer extends MobRenderer<GurtyEntity, GurtyModel<GurtyEntity>> {
	private GurtyEntity gurtyIn;
//		BiomeMaker b;

	private static final ResourceLocation GURTY_TEXTURES = new ResourceLocation(Main.MODID,
			"textures/entity/gurty_texture.png");


	public GurtyRenderer(final EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn, new GurtyModel<>(), 0.3f);
        addLayer(new GurtyAngryLayer<>(this));
	}

	protected void scale(GurtyEntity entityIn, PoseStack matrixStackIn,
		  float partialTickTime) 
	{

		  int eSize =  entityIn.getId()%16-8;
		  gurtyIn = entityIn;
		  boolean gurtyAngry = gurtyIn.isAngry();
//		  if (gurtyAngry) {
//			  System.out.println("Gurty"+ entityIn.getEntityId()  +" angry");
//		  }
		  
		  int angrySize = 0;
		  if (gurtyAngry) {
			  angrySize += 1;
		  }
		  float uniqueSize =0.4f+ (float)(eSize+angrySize)/(16);

//			  System.out.println ("Normal ("+GurtyBiterEntity.SIZE+") + " + uniqueSize);
		  matrixStackIn.scale(GurtyEntity.SIZE+uniqueSize, GurtyEntity.SIZE+uniqueSize, GurtyEntity.SIZE+uniqueSize);

	}

	public void render(GurtyEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int packedLightIn) {

		float f = entityIn.yRotO;
		float lerpYaw = Mth.lerp(partialTicks, f, entityYaw);
		super.render(entityIn, lerpYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(GurtyEntity entity) {
		return GURTY_TEXTURES;
	}
}
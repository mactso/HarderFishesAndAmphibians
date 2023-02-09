package com.mactso.hostilewatermobs.client.model;


import com.google.common.collect.ImmutableList;
import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.entities.WaterSnake;
import com.mactso.hostilewatermobs.utility.Utility;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

//Made with Blockbench 4.4.3
//Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
//Paste this class into your mod and generate all required imports


public class WaterSnakeModel<T extends Entity> extends ListModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Main.MODID, "watersnake"), "main");

	private final ModelPart body;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart jaw;
	
	private final ModelPart tail;
	private final ModelPart tailtip;
	
	public WaterSnakeModel(ModelPart root) {
		
		body = root.getChild("body");

		neck = body.getChild("neck");

		head = neck.getChild("head");
		jaw = head.getChild("jaw");

		tail = body.getChild("tail");
		tailtip = tail.getChild("tailtip");

	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -5.0F, 3.5F, 5.0F, 5.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 24.0F, -10.5F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.5F, 5.5F, 0.0F, -0.1309F, 0.0F));

		neck.addOrReplaceChild("bodyfront", CubeListBuilder.create().texOffs(19, 25).addBox(-1.5F, -1.5F, -11.0F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.225F, -1.675F, -0.5236F, 0.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(26, 0).addBox(-3.5F, -7.6388F, -12.4474F, 6.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(5, 3).addBox(-3.0F, -8.3837F, -7.7723F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 0).addBox(1.0F, -8.3837F, -7.7723F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -2.9667F, -4.25F, 0.3491F, 0.0F, 0.0F));

		head.addOrReplaceChild("rightcheek", CubeListBuilder.create().texOffs(38, 23).addBox(-2.4324F, -1.0F, -4.886F, 4.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -6.5448F, -7.9816F, 0.0F, -0.3054F, 0.0F));

		head.addOrReplaceChild("leftcheek", CubeListBuilder.create().texOffs(27, 41).addBox(1.0537F, -7.6115F, -10.1809F, 4.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0667F, -2.0F, 0.0F, 0.3054F, 0.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(32, 12).addBox(-3.0F, -1.5F, -7.5F, 6.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -4.6992F, -4.9392F, 0.1745F, 0.0F, 0.0F));

		PartDefinition teeth = jaw.addOrReplaceChild("teeth", CubeListBuilder.create().texOffs(0, 0).addBox(2.6734F, -0.5F, -2.935F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 3).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -2.0865F, -6.4312F, 0.0F, -0.6545F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, -1.5F, 18.25F, -0.0436F, -0.0873F, 0.0F));

		tail.addOrReplaceChild("tailbox", CubeListBuilder.create().texOffs(0, 21).addBox(-2.5F, -2.5F, -0.6F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.0F, 0.0F, 0.48F, 0.0F, 0.0F));

		PartDefinition tailtip = tail.addOrReplaceChild("tailtip", CubeListBuilder.create(), PartPose.offset(-1.3459F, -5.6956F, 7.9451F));

		tailtip.addOrReplaceChild("tailtipbox", CubeListBuilder.create().texOffs(0, 37).addBox(-0.6673F, 0.0765F, 0.0767F, 1.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6981F, 0.2182F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

		final float PI = 3.1415927f;
		final float ONE_DEGREE_RADIAN = 0.017453292f;
		final float age = ageInTicks - entity.tickCount;
		float swaySpeed = 0.2f;
		float jawSpeed = 0.15f;
		float headNeckAngle = 0.1f+Mth.sin(PI/4)/16;
		Vec3 vector3d = entity.getDeltaMovement();
	    boolean isMoving = false;
	    if (vector3d.length() != 0) isMoving = true;
	    WaterSnake snake = (WaterSnake) entity;
	    boolean isAttacking = false;
	    boolean isAngry = ((WaterSnake) entity).isAngry();
	    boolean hasTargetedEntity = ((WaterSnake) entity).hasTargetedEntity();
	    Utility.debugMsg(2, "isAngry: " + isAngry + " has Target: " +hasTargetedEntity);
	    if (isAngry && hasTargetedEntity) {
	    	isAttacking = true;
	    	swaySpeed = 0.4f;
			jawSpeed = 0.1f;
	    	headNeckAngle =0.1f+Mth.sin(PI/4);
	    }
	    
	    float attackAnim= (float) snake.startAttackTime - snake.level.getGameTime();
	    if (isAttacking) {
		    Utility.debugMsg(2, "attackAnim: "+attackAnim);
	    }
	    if ( attackAnim > 0) {
	    	Utility.debugMsg(2, "snake.attackAnim: "+snake.attackAnim);
	    	Utility.debugMsg(2, "snake.swingTime: "+snake.swingTime);
	    	headNeckAngle += Mth.sin(attackAnim/32);
	    }

	    float spitAnim= (float) snake.startSpittingTime - snake.level.getGameTime();
	    if (isAttacking) {
	    	Utility.debugMsg(2, "spitAnim: "+spitAnim);
	    }
	    if ( spitAnim > 0) {
	    	Utility.debugMsg(2, "snake.attackAnim: "+snake.attackAnim);
	    	Utility.debugMsg(2, "snake.swingTime: "+snake.swingTime);
	    	headNeckAngle += Mth.cos(spitAnim/32);
	    }
        
        //Testing Area
	    headNeckAngle += 0.1f *(Mth.sin(ageInTicks * 0.05f + 1 * 0.15f * PI));

	    head.xRot = headNeckAngle; // up and down
	    head.yRot = -0.1f * netHeadYaw * ONE_DEGREE_RADIAN/2.0f; // left to right
	    head.zRot = Mth.sin(ageInTicks*0.1f)/8; // slight head tilt back and forth

	    neck.xRot = -headNeckAngle; // up and down
        neck.yRot = swaySpeed * Mth.cos(ageInTicks * 0.1f);
	    
	    jaw.xRot = 0.2f+Mth.sin(ageInTicks*jawSpeed)*0.1f; // up and down
	    if (snake.hissingTime+12 > entity.level.getGameTime()) {
		    jaw.xRot = 0.6f+Mth.sin(ageInTicks*jawSpeed)*0.1f; // up and down
	    }
	    
	    jaw.zRot = 0.0f;
	    jaw.yRot = 0.0f;
  	      
        //Testing Area
		//this.headModel.yRot = (headYaw /1.75F) * 0.017453292f;	    
        body.yRot = (netHeadYaw /1.75F) * ONE_DEGREE_RADIAN + 0.001f * Mth.cos(ageInTicks * 0.1f + 2 * 0.15f * PI) * PI * 0.05f;
        body.x = Mth.sin(ageInTicks * swaySpeed);
 
        tail.yRot = 0.2f*Mth.cos(ageInTicks * swaySpeed);
        tail.xRot = headNeckAngle +0.1f*Mth.sin(ageInTicks * swaySpeed);

        tailtip.xRot = -headNeckAngle + 0.1f*Mth.sin(ageInTicks * swaySpeed);
        tailtip.zRot = 0.2f*Mth.cos(ageInTicks * swaySpeed);

		
	}

	private void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
	modelRenderer.xRot = x;
	modelRenderer.yRot = y;
	modelRenderer.zRot = z;
}
	
	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
	
	@Override
	public Iterable<ModelPart> parts() {
		return (Iterable<ModelPart>) ImmutableList.of(this.body, this.head,this.jaw, this.tail, this.tailtip );

	}
}
package com.mactso.hostilewatermobs.client.model;

import com.google.common.collect.ImmutableList;
import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.entities.WaterSnakeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;


public class WaterSnakeModel <T extends Entity> extends ListModel<T>  {
	private final ModelPart bonebody;
	private final ModelPart boneneck;
	private final ModelPart bonehead;
	private final ModelPart boneeyes;
	private final ModelPart boneteeth;
	private final ModelPart bonejaw;
	private final ModelPart bonecheek;
	private final ModelPart rightcheek_r1;
	private final ModelPart leftcheek_r1;
	private final ModelPart bonetail;
	private final ModelPart bonetailtip;

	public WaterSnakeModel() {
		texHeight = 64;
		texWidth = 64;

		bonebody = new ModelPart(this);
		bonebody.setPos(0.5F, 24.0F, -10.5F);
		bonebody.texOffs(0, 0).addBox(-3.5F, -5.0F, 3.5F, 5.0F, 5.0F, 15.0F, 0.0F, false);

		boneneck = new ModelPart(this);
		boneneck.setPos(0.0F, -0.5F, 5.5F);
		bonebody.addChild(boneneck);
		setRotationAngle(boneneck, 27.5F, -0.0F, 0.0F);
		boneneck.texOffs(19, 25).addBox(-2.5F, -3.725F, -12.675F, 3.0F, 3.0F, 12.0F, 0.0F, false);

		bonehead = new ModelPart(this);
		bonehead.setPos(-0.5F, -2.5F, -13.0F);
		boneneck.addChild(bonehead);
		setRotationAngle(bonehead, -22.5F, 0.0F, 0.0F);
		bonehead.texOffs(26, 0).addBox(-3.5F, -1.05F, -8.0F, 6.0F, 2.0F, 9.0F, 0.0F, false);

		boneeyes = new ModelPart(this);
		boneeyes.setPos(0.5F, 0.6F, 6.75F);
		bonehead.addChild(boneeyes);
		boneeyes.texOffs(5, 0).addBox(0.5F, -2.6F, -8.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		boneeyes.texOffs(5, 3).addBox(-3.5F, -2.6F, -8.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		boneteeth = new ModelPart(this);
		boneteeth.setPos(0.0F, 3.0F, 18.0F);
		bonehead.addChild(boneteeth);
		boneteeth.texOffs(0, 3).addBox(-3.0F, -2.0F, -25.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		boneteeth.texOffs(0, 0).addBox(1.0F, -2.0F, -25.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

	
		bonejaw = new ModelPart(this);
		bonejaw.setPos(-0.5F, -1.1667F, 0.1667F);
		bonehead.addChild(bonejaw);
		setRotationAngle(bonejaw, 0.2182F, 0.0F, 0.0F);
		bonejaw.texOffs(32, 12).addBox(-3.0F, 1.6667F, -8.9167F, 6.0F, 1.0F, 9.0F, 0.0F, false);

		bonecheek = new ModelPart(this);
		bonecheek.setPos(2.0F, -1.0F, -4.0F);
		bonehead.addChild(bonecheek);
		

		rightcheek_r1 = new ModelPart(this);
		rightcheek_r1.setPos(-5.0F, 0.0F, 0.0F);
		bonecheek.addChild(rightcheek_r1);
		setRotationAngle(rightcheek_r1, 0.0F, -0.2182F, 0.0F);
		rightcheek_r1.texOffs(38, 23).addBox(-2.0F, 0.0F, -4.5F, 4.0F, 2.0F, 9.0F, 0.0F, false);

		leftcheek_r1 = new ModelPart(this);
		leftcheek_r1.setPos(0.0F, 0.0F, 0.0F);
		bonecheek.addChild(leftcheek_r1);
		setRotationAngle(leftcheek_r1, 0.0F, 0.2182F, 0.0F);
		leftcheek_r1.texOffs(27, 41).addBox(-2.0F, 0.0F, -4.5F, 4.0F, 2.0F, 9.0F, 0.0F, false);

		bonetail = new ModelPart(this);
		bonetail.setPos(-0.5F, -3.0F, 16.5F);
		bonebody.addChild(bonetail);
		setRotationAngle(bonetail, 0.3491F, 0.0F, 0.0F);
		bonetail.texOffs(0, 21).addBox(-2.0F, -1.0F, 1.15F, 3.0F, 3.0F, 12.0F, 0.0F, false);

		bonetailtip = new ModelPart(this);
		bonetailtip.setPos(-0.5F, 0.5F, 12.0F);
		bonetail.addChild(bonetailtip);
		setRotationAngle(bonetailtip, 0.3491F, 0.0F, 0.0F);
		bonetailtip.texOffs(0, 37).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 12.0F, 0.0F, false);
		

	}

	@Override
	public void setupAnim(final T entity, final float limbSwing, final float limbSwingAmount,
			final float ageInTicks, final float headYaw, final float headPitch) {

		final float PI = 3.1415927f;
		final float ONE_DEGREE_RADIAN = 0.017453292f;
		final float age = ageInTicks - entity.tickCount;
		float swaySpeed = 0.2f;
		float jawSpeed = 0.15f;
		float headNeckAngle = 0.1f+Mth.sin(PI/4)/16;
		Vec3 vector3d = entity.getDeltaMovement();
	    boolean isMoving = false;
	    if (vector3d.length() != 0) isMoving = true;
	    WaterSnakeEntity snake = (WaterSnakeEntity) entity;
	    boolean isAttacking = false;
	    boolean isAngry = ((WaterSnakeEntity) entity).isAngry();
	    boolean hasTargetedEntity = ((WaterSnakeEntity) entity).hasTargetedEntity();
	    MyConfig.debugMsg(1, "is: " + isAngry + "has: " +hasTargetedEntity);
	    if (isAngry && hasTargetedEntity) {
	    	isAttacking = true;
	    	swaySpeed = 0.4f;
			jawSpeed = 0.1f;
	    	headNeckAngle =0.1f+Mth.sin(PI/4);
	    }
	    
	    float attackAnim= (float) snake.startAttackTime - snake.level.getGameTime();
	    if (isAttacking) {
		    MyConfig.debugMsg(1, "attackAnim: "+attackAnim);
	    }
	    if ( attackAnim > 0) {
	    	MyConfig.debugMsg(1, "snake.attackAnim: "+snake.attackAnim);
	    	MyConfig.debugMsg(1, "snake.swingTime: "+snake.swingTime);
	    	headNeckAngle += Mth.sin(attackAnim/32);
	    }

	    float spitAnim= (float) snake.startSpittingTime - snake.level.getGameTime();
	    if (isAttacking) {
	    	MyConfig.debugMsg(1, "spitAnim: "+spitAnim);
	    }
	    if ( spitAnim > 0) {
	    	MyConfig.debugMsg(1, "snake.attackAnim: "+snake.attackAnim);
	    	MyConfig.debugMsg(1, "snake.swingTime: "+snake.swingTime);
	    	headNeckAngle += Mth.cos(spitAnim/32);
	    }
        
        //Testing Area
	    headNeckAngle += 0.1f *(Mth.sin(ageInTicks * 0.05f + 1 * 0.15f * PI));

	    bonehead.xRot = headNeckAngle; // up and down
	    bonehead.yRot = -0.1f * headYaw * ONE_DEGREE_RADIAN/2.0f; // left to right
	    bonehead.zRot = Mth.sin(ageInTicks*0.1f)/8; // slight head tilt back and forth

	    boneneck.xRot = -headNeckAngle; // up and down
        boneneck.yRot = swaySpeed * Mth.cos(ageInTicks * 0.1f);
	    
	    bonejaw.xRot = 0.2f+Mth.sin(ageInTicks*jawSpeed)*0.1f; // up and down
	    if (snake.hissingTime+12 > entity.level.getGameTime()) {
		    bonejaw.xRot = 0.6f+Mth.sin(ageInTicks*jawSpeed)*0.1f; // up and down
	    }
	    
	    bonejaw.zRot = 0.0f;
	    bonejaw.yRot = 0.0f;
  	      
        //Testing Area
		//this.headModel.yRot = (headYaw /1.75F) * 0.017453292f;	    
        bonebody.yRot = (headYaw /1.75F) * ONE_DEGREE_RADIAN + 0.001f * Mth.cos(ageInTicks * 0.1f + 2 * 0.15f * PI) * PI * 0.05f;
        bonebody.x = Mth.sin(ageInTicks * swaySpeed);
 
        bonetail.yRot = 0.2f*Mth.cos(ageInTicks * swaySpeed);
        bonetail.xRot = headNeckAngle +0.1f*Mth.sin(ageInTicks * swaySpeed);

        bonetailtip.xRot = -headNeckAngle + 0.1f*Mth.sin(ageInTicks * swaySpeed);
        bonetailtip.zRot = 0.2f*Mth.cos(ageInTicks * swaySpeed);
        
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		bonebody.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}

	@Override
	public Iterable<ModelPart> parts() {
		return (Iterable<ModelPart>) ImmutableList.of((ModelPart) this.bonebody, (ModelPart) this.boneneck,
				(ModelPart) this.bonetail, (ModelPart) this.bonetailtip);

	}


}

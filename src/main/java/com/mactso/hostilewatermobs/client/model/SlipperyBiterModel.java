package com.mactso.hostilewatermobs.client.model;

import com.google.common.collect.ImmutableList;
import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.entities.SlipperyBiter;

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

//Made with Blockbench 4.4.2
//Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
//Paste this class into your mod and generate all required imports


public class SlipperyBiterModel<T extends Entity> extends ListModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Main.MODID, "slipperybiter"), "main");

	private final ModelPart head;
	
	private final ModelPart body_front;
	private final ModelPart fin_back_1;

	private final ModelPart body_rear;
	private final ModelPart fin_back_2;

	private final ModelPart fin_left;
	private final ModelPart fin_right;
	private final ModelPart tail;
	
	public SlipperyBiterModel(ModelPart root) {

		head = root.getChild("head");

		body_front = root.getChild("body_front");
		fin_left = body_front.getChild("fin_left");
		fin_right = body_front.getChild("fin_right");
		fin_back_1 = body_front.getChild("fin_back_1");

		body_rear = root.getChild("body_rear");
		fin_back_2 = body_rear.getChild("fin_back_2");
		tail = body_rear.getChild("tail");
		
		
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(22, 0).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, -4.0F));

		PartDefinition body_front = partdefinition.addOrReplaceChild("body_front", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, -4.0F));

		body_front.addOrReplaceChild("fin_back_1", CubeListBuilder.create().texOffs(4, 2).addBox(0.0F, 0.0F, 1.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.5F, 5.0F));

		PartDefinition fin_left = body_front.addOrReplaceChild("fin_left", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.5F, 1.5F, 0.0F, -1.5708F, 0.0F, -0.7854F));

		fin_left.addOrReplaceChild("fin_left_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, -0.1309F, 0.0F));

		PartDefinition fin_right = body_front.addOrReplaceChild("fin_right", CubeListBuilder.create(), PartPose.offsetAndRotation(1.5F, 1.5F, 0.0F, -1.5708F, 0.0F, 0.7854F));

		fin_right.addOrReplaceChild("fine_right_r1", CubeListBuilder.create().texOffs(4, 0).addBox(0.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, -0.0436F, 0.0F));

		PartDefinition body_rear = partdefinition.addOrReplaceChild("body_rear", CubeListBuilder.create().texOffs(0, 13).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 4.0F));

		body_rear.addOrReplaceChild("fin_back_2", CubeListBuilder.create().texOffs(2, 3).addBox(0.0F, 0.0F, 1.0F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.5F, -1.0F, 0.0F, 0.0F, 0.0436F));

		body_rear.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(20, 10).addBox(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 8.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		final float age = ageInTicks - entity.tickCount;

		if ((ageInTicks / 1000) % 3 == 0) {
			setRotationAngle(fin_left, -1.5708F, -0.2F * Mth.cos(ageInTicks / 20 * 0.3F), -0.7854F);
			setRotationAngle(fin_right, -1.5708F, -0.2F * Mth.cos(ageInTicks / 20 * 0.3F), 0.7854F);
		} else {
			setRotationAngle(fin_left, -1.5708F, -0.2F, -0.7854F);
			setRotationAngle(fin_right, -1.5708F, -0.2F, 0.7854F);
		}

		this.body_rear.yRot = -1 * 0.25F * Mth.sin(1 * 0.2F * ageInTicks);

				
		float tailSwingMagnitude = 0.1f;
		if (((SlipperyBiter) entity).isMoving()) {
			tailSwingMagnitude = 1.0f;
		}
		this.tail.yRot = 0;
		this.tail.zRot = 0;
		this.tail.xRot = 0;
	
		fin_back_1.zRot = 0.0f;
		/* linear interpolation */
		SlipperyBiter s = (SlipperyBiter) entity;
		
		final float lerpTailValue = Mth.lerp(age, s.getClientSideTailAnimationO(),
				s.getClientSideTailAnimation());
		this.body_rear.zRot = Mth.sin(lerpTailValue* tailSwingMagnitude) * 3.1415927f * 0.1f * tailSwingMagnitude;
		this.fin_back_2.zRot = Mth.sin(lerpTailValue) * 3.1415927f * 0.1f * tailSwingMagnitude;
		this.tail.yRot = Mth.sin(lerpTailValue*8*tailSwingMagnitude ) * 3.1415927f * 0.6f * tailSwingMagnitude;
	}

	private void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
	modelRenderer.xRot = x;
	modelRenderer.yRot = y;
	modelRenderer.zRot = z;
}
	
	@Override
	public Iterable<ModelPart> parts() {
		return (Iterable<ModelPart>) ImmutableList.of(this.body_front, this.body_rear, this.head);

	}

}
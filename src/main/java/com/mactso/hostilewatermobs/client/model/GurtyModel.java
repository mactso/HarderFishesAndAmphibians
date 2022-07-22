package com.mactso.hostilewatermobs.client.model;

import com.google.common.collect.ImmutableList;
import com.mactso.hostilewatermobs.entities.GurtyEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;

//Made with Blockbench 3.7.4
//Exported for Minecraft version 1.15
//Paste this class into your mod and generate all required imports


public class GurtyModel<T extends Entity> extends QuadrupedModel<T> {

	private final ModelPart body;
	private final ModelPart headModel;
	private final ModelPart jaw;
	private final ModelPart legBackLeft;
	private final ModelPart legBackRight;
	private final ModelPart legFrontLeft;
	private final ModelPart legFrontRight;
	private final ModelPart spines;
	private final ModelPart tail;

	public GurtyModel() {
		this(0.0f);
	}
	public GurtyModel(final float inFloat) {
		super(6, inFloat, false, 4.0f, 4.0f, 2.0f, 2.0f, 24);
		this.texWidth = 64;
		this.texHeight = 64;

		texWidth = 64;
		texHeight = 64;
		
		legBackLeft = new ModelPart(this);
		legBackLeft.setPos(-3.0F, 17.0F, 5.5F);
		legBackLeft.texOffs(23, 0).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 3.0F, 0.0F, false);
		legBackLeft.texOffs(24, 26).addBox(-1.0F, 6.0F, -1.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);

		legBackRight = new ModelPart(this);
		legBackRight.setPos(3.0F, 17.0F, 5.5F);
		legBackRight.texOffs(0, 0).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 3.0F, 0.0F, false);
		legBackRight.texOffs(16, 25).addBox(-1.0F, 6.0F, -1.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);

		body = new ModelPart(this);
		body.setPos(0.0F, 15.0F, 9.0F);
		body.texOffs(0, 0).addBox(-3.0F, -2.0F, -11.0F, 6.0F, 5.0F, 11.0F, 0.0F, false);

		spines = new ModelPart(this);
		spines.setPos(-0.4466F, -1.25F, -6.7622F);
		body.addChild(spines);
		setRotationAngle(spines, -1.1873F, 0.0F, 0.0F);
		spines.texOffs(30, 12).addBox(-2.7723F, -0.4462F, -2.8358F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		spines.texOffs(12, 30).addBox(2.7277F, -0.4462F, -2.8358F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		spines.texOffs(29, 5).addBox(0.2277F, -0.4462F, -2.8358F, 1.0F, 1.0F, 4.0F, 0.0F, false);

		tail = new ModelPart(this);
		tail.setPos(0.0F, -0.75F, -1.0F);
		body.addChild(tail);
		// change middle value to "wag" tail in setrotationangles below
		setRotationAngle(tail, 1.182F, 1.0F, 0.0F);
		tail.texOffs(30, 17).addBox(-0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);

		legFrontLeft = new ModelPart(this);
		legFrontLeft.setPos(-3.0F, 17.0F, -1.0F);
		legFrontLeft.texOffs(8, 24).addBox(-0.5F, 0.0F, 0.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

		legFrontRight = new ModelPart(this);
		legFrontRight.setPos(3.0F, 17.0F, -1.0F);
		legFrontRight.texOffs(0, 24).addBox(-1.5F, 0.0F, 0.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

		headModel = new ModelPart(this);
		headModel.setPos(0.0F, 16.0F, -1.0F);
		headModel.texOffs(0, 16).addBox(-2.0F, -5.5F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		headModel.texOffs(7, 0).addBox(1.0F, -5.5F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		headModel.texOffs(0, 16).addBox(-2.5F, -4.0F, -5.0F, 5.0F, 3.0F, 5.0F, 0.0F, false);
		headModel.texOffs(26, 31).addBox(1.0F, -3.5F, -6.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		headModel.texOffs(18, 30).addBox(-3.0F, -3.5F, -6.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

		jaw = new ModelPart(this);
		jaw.setPos(0.0F, 0.0F, 0.0F);
		headModel.addChild(jaw);
		jaw.texOffs(15, 19).addBox(-2.5F, -1.0F, -5.0F, 5.0F, 1.0F, 5.0F, 0.0F, false);
	}

    
	public void setRotationAngle (ModelPart m, float x, float y, float z) {
		m.xRot = x;
		m.yRot = y;
		m.zRot = z;
		
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
//		left_foot.render(matrixStack, buffer, packedLight, packedOverlay);
//		right_foot.render(matrixStack, buffer, packedLight, packedOverlay);
		legBackLeft.render(matrixStack, buffer, packedLight, packedOverlay);
		legBackRight.render(matrixStack, buffer, packedLight, packedOverlay);
		body.render(matrixStack, buffer, packedLight, packedOverlay);
		legFrontLeft.render(matrixStack, buffer, packedLight, packedOverlay);
		legFrontRight.render(matrixStack, buffer, packedLight, packedOverlay);
		headModel.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public void setupAnim(final T entityIn, final float limbSwingAngle, final float limbSwingSpeedFactor,
			final float ageInTicks, final float headYaw, final float headPitch) {
		
		GurtyEntity gurtyEntity = (GurtyEntity) entityIn;
		this.headModel.xRot = headPitch * 0.017453292f;
		this.headModel.yRot = (headYaw /1.75F) * 0.017453292f;
		this.body.xRot = (3.14159265f / 4.0f) * 0.0f; // make body horizontal
		this.legBackRight.xRot = Mth.cos(limbSwingAngle * 0.6662f) * 1.4f * limbSwingSpeedFactor;
		this.legBackLeft.xRot = Mth.cos(limbSwingAngle * 0.6662f + 3.1415927f) * 1.4f * limbSwingSpeedFactor;
		this.legFrontRight.xRot = Mth.cos(limbSwingAngle * 0.6662f + 3.1415927f) * 1.4f * limbSwingSpeedFactor;
		this.legFrontLeft.xRot = Mth.cos(limbSwingAngle * 0.6662f) * 1.4f * limbSwingSpeedFactor;
		this.spines.xRot = -0.107964f;
		this.tail.yRot = -0.2F * Mth.cos(ageInTicks * 0.3F);
		this.jaw.xRot = Math.abs(0.15f * Mth.cos(ageInTicks * 0.1F));
		this.jaw.yRot = -0.04f * Mth.cos(ageInTicks * 0.17F);
		if (gurtyEntity.isAngry()) {
			if (this.tail.xRot < 0.771f) {
				this.tail.xRot += 0.025f;
//				System.out.print(this.tail.rotateAngleX);
			}
			this.tail.yRot = -0.1F * Mth.cos(ageInTicks * 0.4F);
			//			this.tail.rotateAngleX = 0.7707964f; // make tail stick out and up
			this.spines.xRot = -0.6207964f + Math.abs(0.2f * Mth.cos(ageInTicks * 0.25F)); // make spines point forward and up
			this.jaw.xRot = Math.abs(0.3f * Mth.cos(ageInTicks * 0.4F));
			this.jaw.yRot = -0.14f * Mth.cos(ageInTicks * 0.6F);
		} else {
			if (this.tail.xRot > -0.271f) {
				this.tail.xRot -= 0.009f;
//				System.out.print(this.tail.rotateAngleX);
			}
		}
	}

	@Override
	protected Iterable<ModelPart> headParts() {
		return (Iterable<ModelPart>) ImmutableList.of((ModelPart) this.headModel,(ModelPart) this.jaw);
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return (Iterable<ModelPart>) ImmutableList.of((ModelPart) this.body, (ModelPart) this.legFrontLeft,
				(ModelPart) this.legBackLeft, (ModelPart) this.legFrontRight, (ModelPart) this.legBackRight, this.tail,this.spines);
	}

}
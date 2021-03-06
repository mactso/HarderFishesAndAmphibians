package com.mactso.hostilewatermobs.client.model;

import com.google.common.collect.ImmutableList;
import com.mactso.hostilewatermobs.entities.GurtyEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

//Made with Blockbench 3.7.4
//Exported for Minecraft version 1.15
//Paste this class into your mod and generate all required imports


public class GurtyModel<T extends Entity> extends QuadrupedModel<T> {

	private final ModelRenderer body;
	private final ModelRenderer headModel;
	private final ModelRenderer jaw;
	private final ModelRenderer legBackLeft;
	private final ModelRenderer legBackRight;
	private final ModelRenderer legFrontLeft;
	private final ModelRenderer legFrontRight;
	private final ModelRenderer spines;
	private final ModelRenderer tail;

	public GurtyModel() {
		this(0.0f);
	}
	public GurtyModel(final float inFloat) {
		super(6, inFloat, false, 4.0f, 4.0f, 2.0f, 2.0f, 24);
		this.textureWidth = 64;
		this.textureHeight = 64;

		textureWidth = 64;
		textureHeight = 64;
		
		legBackLeft = new ModelRenderer(this);
		legBackLeft.setRotationPoint(-3.0F, 17.0F, 5.5F);
		legBackLeft.setTextureOffset(23, 0).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 3.0F, 0.0F, false);
		legBackLeft.setTextureOffset(24, 26).addBox(-1.0F, 6.0F, -1.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);

		legBackRight = new ModelRenderer(this);
		legBackRight.setRotationPoint(3.0F, 17.0F, 5.5F);
		legBackRight.setTextureOffset(0, 0).addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 3.0F, 0.0F, false);
		legBackRight.setTextureOffset(16, 25).addBox(-1.0F, 6.0F, -1.0F, 2.0F, 1.0F, 4.0F, 0.0F, false);

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 15.0F, 9.0F);
		body.setTextureOffset(0, 0).addBox(-3.0F, -2.0F, -11.0F, 6.0F, 5.0F, 11.0F, 0.0F, false);

		spines = new ModelRenderer(this);
		spines.setRotationPoint(-0.4466F, -1.25F, -6.7622F);
		body.addChild(spines);
		setRotationAngle(spines, -1.1873F, 0.0F, 0.0F);
		spines.setTextureOffset(30, 12).addBox(-2.7723F, -0.4462F, -2.8358F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		spines.setTextureOffset(12, 30).addBox(2.7277F, -0.4462F, -2.8358F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		spines.setTextureOffset(29, 5).addBox(0.2277F, -0.4462F, -2.8358F, 1.0F, 1.0F, 4.0F, 0.0F, false);

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, -0.75F, -1.0F);
		body.addChild(tail);
		// change middle value to "wag" tail in setrotationangles below
		setRotationAngle(tail, 1.182F, 1.0F, 0.0F);
		tail.setTextureOffset(30, 17).addBox(-0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);

		legFrontLeft = new ModelRenderer(this);
		legFrontLeft.setRotationPoint(-3.0F, 17.0F, -1.0F);
		legFrontLeft.setTextureOffset(8, 24).addBox(-0.5F, 0.0F, 0.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

		legFrontRight = new ModelRenderer(this);
		legFrontRight.setRotationPoint(3.0F, 17.0F, -1.0F);
		legFrontRight.setTextureOffset(0, 24).addBox(-1.5F, 0.0F, 0.0F, 2.0F, 7.0F, 2.0F, 0.0F, false);

		headModel = new ModelRenderer(this);
		headModel.setRotationPoint(0.0F, 16.0F, -1.0F);
		headModel.setTextureOffset(0, 16).addBox(-2.0F, -5.5F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		headModel.setTextureOffset(7, 0).addBox(1.0F, -5.5F, -1.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		headModel.setTextureOffset(0, 16).addBox(-2.5F, -4.0F, -5.0F, 5.0F, 3.0F, 5.0F, 0.0F, false);
		headModel.setTextureOffset(26, 31).addBox(1.0F, -3.5F, -6.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		headModel.setTextureOffset(18, 30).addBox(-3.0F, -3.5F, -6.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(0.0F, 0.0F, 0.0F);
		headModel.addChild(jaw);
		jaw.setTextureOffset(15, 19).addBox(-2.5F, -1.0F, -5.0F, 5.0F, 1.0F, 5.0F, 0.0F, false);
	}

    
	public void setRotationAngle (ModelRenderer m, float x, float y, float z) {
		m.rotateAngleX = x;
		m.rotateAngleY = y;
		m.rotateAngleZ = z;
		
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
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
	public void setRotationAngles(final T entityIn, final float limbSwingAngle, final float limbSwingSpeedFactor,
			final float ageInTicks, final float headYaw, final float headPitch) {
		
		GurtyEntity gurtyEntity = (GurtyEntity) entityIn;
		this.headModel.rotateAngleX = headPitch * 0.017453292f;
		this.headModel.rotateAngleY = (headYaw /1.75F) * 0.017453292f;
		this.body.rotateAngleX = (3.14159265f / 4.0f) * 0.0f; // make body horizontal
		this.legBackRight.rotateAngleX = MathHelper.cos(limbSwingAngle * 0.6662f) * 1.4f * limbSwingSpeedFactor;
		this.legBackLeft.rotateAngleX = MathHelper.cos(limbSwingAngle * 0.6662f + 3.1415927f) * 1.4f * limbSwingSpeedFactor;
		this.legFrontRight.rotateAngleX = MathHelper.cos(limbSwingAngle * 0.6662f + 3.1415927f) * 1.4f * limbSwingSpeedFactor;
		this.legFrontLeft.rotateAngleX = MathHelper.cos(limbSwingAngle * 0.6662f) * 1.4f * limbSwingSpeedFactor;
		this.spines.rotateAngleX = -0.107964f;
		this.tail.rotateAngleY = -0.2F * MathHelper.cos(ageInTicks * 0.3F);
		this.jaw.rotateAngleX = Math.abs(0.15f * MathHelper.cos(ageInTicks * 0.1F));
		this.jaw.rotateAngleY = -0.04f * MathHelper.cos(ageInTicks * 0.17F);
		if (gurtyEntity.isAngry()) {
			if (this.tail.rotateAngleX < 0.771f) {
				this.tail.rotateAngleX += 0.025f;
//				System.out.print(this.tail.rotateAngleX);
			}
			this.tail.rotateAngleY = -0.1F * MathHelper.cos(ageInTicks * 0.4F);
			//			this.tail.rotateAngleX = 0.7707964f; // make tail stick out and up
			this.spines.rotateAngleX = -0.6207964f + Math.abs(0.2f * MathHelper.cos(ageInTicks * 0.25F)); // make spines point forward and up
			this.jaw.rotateAngleX = Math.abs(0.3f * MathHelper.cos(ageInTicks * 0.4F));
			this.jaw.rotateAngleY = -0.14f * MathHelper.cos(ageInTicks * 0.6F);
		} else {
			if (this.tail.rotateAngleX > -0.271f) {
				this.tail.rotateAngleX -= 0.009f;
//				System.out.print(this.tail.rotateAngleX);
			}
		}
	}

	@Override
	protected Iterable<ModelRenderer> getHeadParts() {
		return (Iterable<ModelRenderer>) ImmutableList.of((ModelRenderer) this.headModel,(ModelRenderer) this.jaw);
	}

	@Override
	protected Iterable<ModelRenderer> getBodyParts() {
		return (Iterable<ModelRenderer>) ImmutableList.of((ModelRenderer) this.body, (ModelRenderer) this.legFrontLeft,
				(ModelRenderer) this.legBackLeft, (ModelRenderer) this.legFrontRight, (ModelRenderer) this.legBackRight, this.tail,this.spines);
	}

}
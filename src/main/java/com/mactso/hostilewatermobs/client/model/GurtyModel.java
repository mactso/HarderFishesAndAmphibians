package com.mactso.hostilewatermobs.client.model;

import com.google.common.collect.ImmutableList;
import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.entities.Gurty;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.QuadrupedModel;
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

// Made with Blockbench 4.4.2
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings

// Paste this class into your mod and generate all required imports

public class GurtyModel<T extends Entity> extends QuadrupedModel<T> {

	private static boolean scaleHead = false;
	private static float babyBodyScale = 4.0f;

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(Main.MODID, "gurty"), "main");

	private final ModelPart jaw;
	private final ModelPart spines;
	private final ModelPart tail;

	public GurtyModel(ModelPart root) {

		super(root, scaleHead, babyBodyScale, babyBodyScale, babyBodyScale, babyBodyScale, 24);

		this.jaw = head.getChild("jaw");
		this.spines = body.getChild("spines");
		this.tail = body.getChild("tail");

	}

	public static LayerDefinition createBodyLayer() {

		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition legBackLeft = partdefinition.addOrReplaceChild("left_hind_leg",
				CubeListBuilder.create().texOffs(23, 0)
						.addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(24, 26)
						.addBox(-1.0F, 6.0F, -1.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-3.0F, 17.0F, 5.5F));

		PartDefinition legBackRight = partdefinition.addOrReplaceChild("right_hind_leg",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(16, 25)
						.addBox(-1.0F, 6.0F, -1.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(3.0F, 17.0F, 5.5F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
				.addBox(-3.0F, -2.0F, -11.0F, 6.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 15.0F, 9.0F));

		PartDefinition spines = body.addOrReplaceChild("spines",
				CubeListBuilder.create().texOffs(30, 12)
						.addBox(-2.3333F, -0.4038F, -3.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(12, 30)
						.addBox(3.1667F, -0.4038F, -3.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(29, 5)
						.addBox(0.6667F, -0.4038F, -3.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.8856F, -1.5962F, -6.198F, -0.0873F, 0.0F, 0.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(),
				PartPose.offset(0.0F, -0.75F, -1.0F));

		PartDefinition tail_r1 = tail
				.addOrReplaceChild("tail_r1",
						CubeListBuilder.create().texOffs(30, 17).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition legFrontLeft = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create()
				.texOffs(8, 24).addBox(-0.5F, 0.0F, 0.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-3.0F, 17.0F, -1.0F));

		PartDefinition legFrontRight = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create()
				.texOffs(0, 24).addBox(-1.5F, 0.0F, 0.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(3.0F, 17.0F, -1.0F));

		PartDefinition head = partdefinition.addOrReplaceChild("head",
				CubeListBuilder.create().texOffs(0, 16)
						.addBox(-2.0F, -5.5F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(7, 0)
						.addBox(1.0F, -5.5F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 16)
						.addBox(-2.5F, -4.0F, -5.0F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(26, 31)
						.addBox(1.0F, -3.5F, -6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(18, 30)
						.addBox(-3.0F, -3.5F, -6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 16.0F, -1.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(15, 19).addBox(-2.5F, -1.0F,
				-5.0F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		leftHindLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		rightHindLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		leftFrontLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		rightFrontLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(T entityIn, float limbSwingAngle, float limbSwingSpeedFactor, float ageInTicks, float headYaw,
			float headPitch) {

		Gurty gurtyEntity = (Gurty) entityIn;
		this.head.xRot = headPitch * 0.017453292f;
		this.head.yRot = (headYaw / 1.75F) * 0.017453292f;
		this.body.xRot = (3.14159265f / 4.0f) * 0.0f; // make body horizontal
		this.rightHindLeg.xRot = Mth.cos(limbSwingAngle * 0.6662f) * 1.4f * limbSwingSpeedFactor;
		this.leftHindLeg.xRot = Mth.cos(limbSwingAngle * 0.6662f + 3.1415927f) * 1.4f * limbSwingSpeedFactor;
		this.rightFrontLeg.xRot = Mth.cos(limbSwingAngle * 0.6662f + 3.1415927f) * 1.4f * limbSwingSpeedFactor;
		this.leftFrontLeg.xRot = Mth.cos(limbSwingAngle * 0.6662f) * 1.4f * limbSwingSpeedFactor;
		this.spines.xRot = -0.107964f;
		this.tail.yRot = -0.2F * Mth.cos(ageInTicks * 0.3F);
		this.jaw.xRot = Math.abs(0.15f * Mth.cos(ageInTicks * 0.1F));
		this.jaw.yRot = -0.04f * Mth.cos(ageInTicks * 0.17F);
		if (gurtyEntity.isAngry()) {
			if (this.tail.xRot < 0.771f) {
				this.tail.xRot += 0.025f;

			}
			this.tail.yRot = -0.1F * Mth.cos(ageInTicks * 0.4F);
			// this.tail.rotateAngleX = 0.7707964f; // make tail stick out and up
			this.spines.xRot = -0.6207964f + Math.abs(0.2f * Mth.cos(ageInTicks * 0.25F)); // make spines point forward
																							// and up
			this.jaw.xRot = Math.abs(0.3f * Mth.cos(ageInTicks * 0.4F));
			this.jaw.yRot = -0.14f * Mth.cos(ageInTicks * 0.6F);
		} else {
			if (this.tail.xRot > -0.271f) {
				this.tail.xRot -= 0.009f;

			}
		}
	}

	@Override
	protected Iterable<ModelPart> headParts() {
		return (Iterable<ModelPart>) ImmutableList.of((ModelPart) this.head, (ModelPart) this.jaw);
	}

	@Override
	protected Iterable<ModelPart> bodyParts() {
		return (Iterable<ModelPart>) ImmutableList.of((ModelPart) this.body, (ModelPart) this.leftFrontLeg,
				(ModelPart) this.leftHindLeg, (ModelPart) this.rightFrontLeg, (ModelPart) this.rightHindLeg, this.tail,
				this.spines);
	}

}

package com.mactso.hostilewatermobs.client.model;

import com.google.common.collect.ImmutableList;
import com.mactso.hostilewatermobs.entities.SlipperyBiterEntity;

import net.minecraft.client.renderer.entity.model.GuardianModel;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlipperyBiterModel<T extends Entity> extends SegmentedModel<T> {
	private final ModelRenderer body_front;
	private final ModelRenderer fin_left;
	private final ModelRenderer fin_right;
	private final ModelRenderer body_rear;
	private final ModelRenderer head;
	private final ModelRenderer fin_back_1;
	private final ModelRenderer fin_back_2;
	private final ModelRenderer tail;

	public SlipperyBiterModel() {

		this.textureWidth = 32;
		this.textureHeight = 32;

		textureWidth = 32;
		textureHeight = 32;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 18.0F, -4.0F);
		head.setTextureOffset(22, 0).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F, 0.0F, false);

		body_front = new ModelRenderer(this);
		body_front.setRotationPoint(0.0F, 18.0F, -4.0F);
		body_front.setTextureOffset(0, 0).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F, 0.0F, false);

		fin_back_1 = new ModelRenderer(this);
		fin_back_1.setRotationPoint(0.0F, -4.5F, 5.0F);
		body_front.addChild(fin_back_1);
		fin_back_1.setTextureOffset(4, 2).addBox(0.0F, 0.0F, 1.0F, 0.0F, 2.0F, 2.0F, 0.0F, false);

		fin_left = new ModelRenderer(this);
		fin_left.setRotationPoint(-1.5F, 1.5F, 0.0F);
		body_front.addChild(fin_left);
		setRotationAngle(fin_left, -1.5708F, 0.0F, -0.7854F);
		fin_left.setTextureOffset(0, 0).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F, 0.0F, false);

		fin_right = new ModelRenderer(this);
		fin_right.setRotationPoint(1.5F, 1.5F, 0.0F);
		body_front.addChild(fin_right);
		setRotationAngle(fin_right, -1.5708F, 0.0F, 0.7854F);
		fin_right.setTextureOffset(4, 0).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F, 0.0F, false);

		body_rear = new ModelRenderer(this);
		body_rear.setRotationPoint(0.0F, 18.0F, 4.0F);
		body_rear.setTextureOffset(0, 13).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F, 0.0F, false);

		fin_back_2 = new ModelRenderer(this);
		fin_back_2.setRotationPoint(0.0F, -4.5F, -1.0F);
		body_rear.addChild(fin_back_2);
		setRotationAngle(fin_back_2, 0.0F, 0.0F, 0.0436F);
		fin_back_2.setTextureOffset(2, 3).addBox(0.0F, 0.0F, 1.0F, 0.0F, 2.0F, 3.0F, 0.0F, false);

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, 0.0F, 8.0F);
		body_rear.addChild(tail);
		tail.setTextureOffset(20, 10).addBox(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F, 0.0F, false);

	}

// This may becoming but the 1.16.3 salmon didn't use it yet.
//	@Override
//	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
//		body_front.render(matrixStack, buffer, packedLight, packedOverlay);
//		body_back.render(matrixStack, buffer, packedLight, packedOverlay);
//		head.render(matrixStack, buffer, packedLight, packedOverlay);
//		fin_back_1.render(matrixStack, buffer, packedLight, packedOverlay);
//		fin_back_2.render(matrixStack, buffer, packedLight, packedOverlay);
//		tail.render(matrixStack, buffer, packedLight, packedOverlay);
//	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return (Iterable<ModelRenderer>) ImmutableList.of(this.body_front, this.body_rear, this.head);

	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(final T entity, final float limbSwing, final float limbSwingAmount,
			final float ageInTicks, final float netHeadYaw, final float headPitch) {

		final float age = ageInTicks - entity.ticksExisted;

		if ((ageInTicks / 1000) % 3 == 0) {
			setRotationAngle(fin_left, -1.5708F, -0.2F * MathHelper.cos(ageInTicks / 20 * 0.3F), -0.7854F);
			setRotationAngle(fin_right, -1.5708F, -0.2F * MathHelper.cos(ageInTicks / 20 * 0.3F), 0.7854F);
		} else {
			setRotationAngle(fin_left, -1.5708F, -0.2F, -0.7854F);
			setRotationAngle(fin_right, -1.5708F, -0.2F, 0.7854F);
		}

		float tailSwingMagnitude = 0.2f;
		if (((SlipperyBiterEntity) entity).isMoving()) {
			tailSwingMagnitude = 1.0f;
		}
		fin_back_1.rotateAngleZ = 0.0f;
		/* linear interpolation */
		SlipperyBiterEntity s = (SlipperyBiterEntity) entity;
		
		final float lerpTailValue = MathHelper.lerp(age, s.getClientSideTailAnimationO(),
				s.getClientSideTailAnimation());
		this.body_rear.rotateAngleZ = MathHelper.sin(lerpTailValue) * 3.1415927f * 0.05f * tailSwingMagnitude;
		this.fin_back_2.rotateAngleZ = MathHelper.sin(lerpTailValue) * 3.1415927f * 0.1f * tailSwingMagnitude;

	}

}

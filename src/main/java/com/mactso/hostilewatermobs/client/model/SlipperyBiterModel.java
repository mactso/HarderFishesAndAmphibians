package com.mactso.hostilewatermobs.client.model;

import com.google.common.collect.ImmutableList;

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
	return (Iterable<ModelRenderer>) ImmutableList.of( this.body_front, this.body_rear,
				 this.head);

	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	
	@Override
	public void setRotationAngles(final T entity, final float limbSwing, final float limbSwingAmount,
			final float ageInTicks, final float netHeadYaw, final float headPitch
			) {
		
		if ((ageInTicks/1000)% 3 == 0) {
			setRotationAngle(fin_left, -1.5708F, -0.2F * MathHelper.cos(ageInTicks/20 * 0.3F), -0.7854F);
			setRotationAngle(fin_right, -1.5708F, -0.2F * MathHelper.cos(ageInTicks/20 * 0.3F), 0.7854F);
		}
		else {
			setRotationAngle(fin_left, -1.5708F, -0.2F , -0.7854F);
			setRotationAngle(fin_right, -1.5708F, -0.2F, 0.7854F);
		}
		
		if ((ageInTicks/10000)% 3 == 0) {
			fin_back_1.rotateAngleZ = -0.2F * MathHelper.cos(ageInTicks/4 * 0.2F);
			fin_back_2.rotateAngleZ = -0.2F * MathHelper.cos(ageInTicks/4 * 0.2F);
		} else {
			fin_back_1.rotateAngleZ = 0.0f;
			fin_back_2.rotateAngleZ = 0.0f;
		}
		
		float baseTailFlipSpeed_ = 0.7f;			
		float currentTimeTailFlipValue = 0.7f;
		float animationSpeed = ageInTicks/4;

		Vector3d m = entity.getMotion();
		if (m.length() < 0.1f){
			baseTailFlipSpeed_ = 0.2f;			
			currentTimeTailFlipValue = 0.2f;
			animationSpeed = ageInTicks/8;
		}

		if (!entity.isInWater()) {
			baseTailFlipSpeed_ = 0.7f;
			currentTimeTailFlipValue = 0.9f;
			animationSpeed = ageInTicks;
			entity.rotationYaw = 0.90f;
		}
		
		
		this.body_rear.rotateAngleY = -baseTailFlipSpeed_ * 0.25f * MathHelper.sin(currentTimeTailFlipValue * 0.6f * animationSpeed);
		this.tail.rotateAngleY = -baseTailFlipSpeed_ * 0.25f * MathHelper.sin(currentTimeTailFlipValue * 0.6f * animationSpeed);
	}


}

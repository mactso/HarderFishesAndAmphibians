package com.mactso.hostilewatermobs.client.renderer.layers;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.SlipperyBiterModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.util.ResourceLocation;

public class SlipperyBiterEyesLayer <T extends WaterMobEntity, M extends SlipperyBiterModel<T>> extends AbstractEyesLayer<T, M>
{
	private static final RenderType RENDER_TYPE = RenderType.eyes(new ResourceLocation(Main.MODID, "textures/entity/slipperybiter_eyes.png"));
	
    public SlipperyBiterEyesLayer(IEntityRenderer<T, M> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return RENDER_TYPE;
    }
}

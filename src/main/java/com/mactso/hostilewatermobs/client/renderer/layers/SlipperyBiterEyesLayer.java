package com.mactso.hostilewatermobs.client.renderer.layers;

import com.mactso.hostilewatermobs.Main;
import com.mactso.hostilewatermobs.client.model.SlipperyBiterModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.resources.ResourceLocation;

public class SlipperyBiterEyesLayer <T extends WaterAnimal, M extends SlipperyBiterModel<T>> extends EyesLayer<T, M>
{
	private static final RenderType RENDER_TYPE = RenderType.eyes(new ResourceLocation(Main.MODID, "textures/entity/slipperybiter_eyes.png"));
	
    public SlipperyBiterEyesLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return RENDER_TYPE;
    }
}

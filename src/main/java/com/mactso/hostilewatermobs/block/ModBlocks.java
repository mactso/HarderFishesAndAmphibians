package com.mactso.hostilewatermobs.block;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks
{
	public static final Block NEST_BLOCK = new NestBlock(Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).noCollission().instabreak().sound(SoundType.GRASS).lightLevel(blockstate -> 7));

	public static void register(IForgeRegistry<Block> forgeRegistry	)
	{
		Block b = Blocks.CORNFLOWER;
		forgeRegistry.register("nest_block",NEST_BLOCK);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void setRenderLayer()
	{
		ItemBlockRenderTypes.setRenderLayer(NEST_BLOCK, RenderType.cutout());

	}
}

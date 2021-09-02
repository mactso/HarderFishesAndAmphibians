package com.mactso.hostilewatermobs.block;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks
{
	public static final Block NEST_BLOCK = new NestBlock(Properties.of(Material.GRASS).noCollission().instabreak().sound(SoundType.GRASS)).setRegistryName("nest_block");

	public static void register(IForgeRegistry<Block> forgeRegistry	)
	{
		forgeRegistry.register(NEST_BLOCK);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void setRenderLayer()
	{
		RenderTypeLookup.setRenderLayer(NEST_BLOCK, RenderType.cutout());

	}
}

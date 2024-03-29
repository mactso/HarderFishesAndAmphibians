package com.mactso.hostilewatermobs.utility;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableStructureInfo.StructureInfo.Builder;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.registries.IForgeRegistry;

public class ModStructureModifier implements StructureModifier
{
    public static final ModStructureModifier INSTANCE = new ModStructureModifier();

    @Override
    public void modify(Holder<Structure> struct, Phase phase, Builder builder)
    {
        if (phase == Phase.AFTER_EVERYTHING)
        {
            SpawnData.onStructure(struct, builder);
        }
    }

    @Override
    public Codec<? extends StructureModifier> codec()
    {
        return Codec.unit(ModStructureModifier.INSTANCE);
    }

    public static void register(@Nullable IForgeRegistry<Object> forgeRegistry)
    {
        forgeRegistry.register("special", INSTANCE.codec());
    }
}

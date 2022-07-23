//package com.mactso.hostilewatermobs.config;
//
//
//

//import java.util.Set;
//
//public class PointOfInterestRegistry {
//
//    public static final DeferredRegister<PoiType> DEF_REG = DeferredRegister.create(ForgeRegistries.POI_TYPES, Main.MODID);
//    public static final RegistryObject<PoiType> GURTY_NEST = DEF_REG.register("gurty_nest", () -> new PoiType(getBlockStates(ModBlocks.NEST_BLOCK), 32, 6));
//
//    private static Set<BlockState> getBlockStates(Block block) {
//        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
//    }
//
//}
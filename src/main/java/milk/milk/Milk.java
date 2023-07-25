package milk.milk;
import milk.milk.blocks.MilkBlock;
import milk.milk.blocks.MilkFluid;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import static net.minecraft.item.Items.BUCKET;
import static net.minecraft.item.Items.MILK_BUCKET;

public class Milk implements ModInitializer {
    public static final String MOD_NAME = "milk";

    public static final TagKey<Fluid> MILK_TAG = TagKey.of(RegistryKeys.FLUID, new Identifier("milk"));

    public static final FlowableFluid STILL_MILK = Registry.register(
            Registries.FLUID,
            id("still_milk"),
            new MilkFluid.Still()
    );
    public static final FlowableFluid FLOWING_MILK = Registry.register(
            Registries.FLUID,
            id("flowing_milk"),
            new MilkFluid.Flowing()
    );
    public static final Block MILK_BLOCK = Registry.register(
            Registries.BLOCK,
            id("milk_block"),
            new MilkBlock(STILL_MILK, FabricBlockSettings.copyOf(Blocks.WATER).mapColor(MapColor.WHITE))
    );

    @Override
    public void onInitialize() {
        System.out.println("Mod Initializing");


        // transfer
        FluidStorage.combinedItemApiProvider(MILK_BUCKET).register(context ->
                new FullItemFluidStorage(context, bucket -> ItemVariant.of(BUCKET), FluidVariant.of(STILL_MILK), FluidConstants.BUCKET)
        );
        FluidStorage.combinedItemApiProvider(BUCKET).register(context ->
                new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(MILK_BUCKET), STILL_MILK, FluidConstants.BUCKET)
        );
    }


    public static Identifier id(String path) {
        return new Identifier(MOD_NAME, path);
    }
}

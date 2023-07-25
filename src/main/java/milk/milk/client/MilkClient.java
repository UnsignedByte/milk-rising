package milk.milk.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import static milk.milk.Milk.*;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class MilkClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        final Identifier textureBase = id("milk");
        final Identifier stillTexture = new Identifier(textureBase.getNamespace(), "block/" + id("still_milk").getPath());
        final Identifier flowingTexture = new Identifier(textureBase.getNamespace(), "block/" + id("flowing_milk").getPath());

        FluidRenderHandler handler = new SimpleFluidRenderHandler(stillTexture, flowingTexture);
        FluidRenderHandlerRegistry.INSTANCE.register(STILL_MILK, handler);
        FluidRenderHandlerRegistry.INSTANCE.register(FLOWING_MILK, handler);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), STILL_MILK, FLOWING_MILK);
    }
}
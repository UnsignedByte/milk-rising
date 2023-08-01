package milk.milk.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static milk.milk.Milk.*;

@Mixin(LandPathNodeMaker.class)
public class LandPathNodeMakerMixin {
    @Redirect(method = "getStart()Lnet/minecraft/entity/ai/pathing/PathNode;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean sus(BlockState instance, Block block) {
        return instance.isOf(Blocks.WATER) || instance.isOf(MILK_BLOCK);
    }

    @Redirect(method = "getStart()Lnet/minecraft/entity/ai/pathing/PathNode;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;", ordinal = 1))
    private FluidState sus2(BlockState instance) {
        FluidState fs = instance.getFluidState();
        if (fs == STILL_MILK.getStill(false)) {
            return Fluids.WATER.getStill(false);
        }
        return fs;
    }
}

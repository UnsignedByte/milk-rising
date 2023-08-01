package milk.milk.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.MobNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static milk.milk.Milk.MILK_BLOCK;

@Mixin(MobNavigation.class)
public class MobNavigationMixin {
    @Redirect(method = "getPathfindingY", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean isOf(BlockState instance, Block block) {
        return instance.isOf(Blocks.WATER) || instance.isOf(MILK_BLOCK);
    }
}

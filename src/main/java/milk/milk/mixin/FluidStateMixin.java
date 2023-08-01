package milk.milk.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static milk.milk.Milk.MILK_TAG;

@Mixin(FluidState.class)
public abstract class FluidStateMixin {
    @Shadow
    public abstract Fluid getFluid();

    /**
     * @author s
     * @reason s
     */
    @Overwrite
    public boolean isIn(TagKey<Fluid> tag) {
        if (tag == FluidTags.WATER) {
            return this.getFluid().isIn(FluidTags.WATER) || this.getFluid().isIn(MILK_TAG);
        }
        return this.getFluid().isIn(tag);
    }
}

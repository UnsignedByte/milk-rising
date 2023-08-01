package milk.milk;

import milk.milk.blocks.MilkBlock;
import milk.milk.blocks.MilkFluid;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.SpawnSettings;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.item.Items.BUCKET;
import static net.minecraft.item.Items.MILK_BUCKET;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Milk implements ModInitializer {
    public static final String MOD_NAME = "milk";

    public static final TagKey<Fluid> MILK_TAG = TagKey.of(RegistryKeys.FLUID, id("milk"));

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
            new MilkBlock(STILL_MILK, FabricBlockSettings.copyOf(Blocks.WATER).mapColor(MapColor.OFF_WHITE).replaceable())
    );

    @Override
    public void onInitialize() {
        System.out.println("Mod Initializing");

        // transfer
        FluidStorage.combinedItemApiProvider(MILK_BUCKET).register(context ->
                new FullItemFluidStorage(context, bucket -> ItemVariant.of(BUCKET), FluidVariant.of(STILL_MILK),
                        FluidConstants.BUCKET)
        );
        FluidStorage.combinedItemApiProvider(BUCKET).register(context ->
                new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(MILK_BUCKET), STILL_MILK,
                        FluidConstants.BUCKET)
        );

        Predicate<BiomeSelectionContext> biomes = BiomeSelectors.all();

        BiomeModifications.create(id("creature_change")).add(
                ModificationPhase.REPLACEMENTS,
                biomes,
                context -> {
                    BiomeModificationContext.SpawnSettingsContext settings = context.getSpawnSettings();
                    settings.clearSpawns(SpawnGroup.CREATURE);
                    settings.setSpawnCost(EntityType.COW, 0.7, 1);
                    settings.addSpawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(
                            EntityType.COW,
                            100,
                            1,
                            2
                    ));
                    settings.addSpawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(
                            EntityType.COW,
                            200,
                            1,
                            4
                    ));
                }
        );

        AtomicBoolean enabled = new AtomicBoolean(false);
        AtomicInteger milk_height = new AtomicInteger(-64);
        AtomicInteger rising_timer = new AtomicInteger(0);
        AtomicInteger prev_increments = new AtomicInteger(0);
        AtomicInteger wait_time = new AtomicInteger(5*60*20); // wait time in ticks (5 mins)
        final int wb_radius = 128;
        AtomicReference<ServerBossBar> bossBar = new AtomicReference<>(new ServerBossBar(Text.literal("Milk Rises"), BossBar.Color.WHITE, BossBar.Style.PROGRESS));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("milk")
                        .then(literal("start")
                        .executes(context -> {
                            if (enabled.get()) {
                                context.getSource().sendError(Text.literal("Milk already rising."));
                                return 1;
                            }
                            enabled.set(true);
                            prev_increments.set(0);
                            rising_timer.set(0);
                            context.getSource().getWorld().getWorldBorder().setSize(wb_radius*2);
                            bossBar.get().setVisible(true);
                            return 1;
                        }))));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("milk")
                        .then(literal("pause")
                        .executes(context -> {
                            if (!enabled.get()) {
                                context.getSource().sendError(Text.literal("Milk already paused."));
                                return 1;
                            }
                            enabled.set(false);
                            bossBar.get().setVisible(false);
                            return 1;
                        }))));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("milk")
                        .then(literal("delay").then(argument("len", integer(0))
                                .executes(context -> {
                                    int v = getInteger(context, "len");
                                    wait_time.set(v);
                                    context.getSource().sendMessage(Text.literal("Set wait time to "+v+"."));
                                    return 1;
                                })))));

        ServerTickEvents.END_WORLD_TICK.register((ServerWorld world) -> {
            if (enabled.get()) {
                ServerBossBar bb = bossBar.get();
                int time = rising_timer.getAndIncrement();

                for (ServerPlayerEntity p : world.getPlayers()) {
                    bb.addPlayer(p);
                }

                int wt = wait_time.get();

                bb.setPercent((float) time / wt);
                if (time >= wt) {
                    // milk rises
                    rising_timer.set(0);

                    int new_height = milk_height.get() + prev_increments.incrementAndGet();
                    milk_height.set(new_height);

                    bb.setName(Text.literal("Milk Height: " + new_height));

                    for (int x = -wb_radius; x <= wb_radius; x++) {
                        for (int z = -wb_radius; z <= wb_radius; z++) {
                            BlockPos pos = new BlockPos(x, new_height, z);
                            if (world.isAir(pos)) {
                                world.setBlockState(pos, MILK_BLOCK.getDefaultState());
                            }
                        }
                    }
                }
            }
        });
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_NAME, path);
    }
}

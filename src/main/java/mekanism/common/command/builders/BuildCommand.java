package mekanism.common.command.builders;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import com.mojang.brigadier.builder.ArgumentBuilder;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BuildCommand {

    public static final ArgumentBuilder<CommandSource, ?> COMMAND = Commands.literal("build")
        .then(Commands.literal("remove")
            .requires(cs -> cs.hasPermissionLevel(4))
            .executes(ctx -> {
                CommandSource source = ctx.getSource();
                Entity entity = source.getEntity();
                if (entity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
                    BlockRayTraceResult result = MekanismUtils.rayTrace(player, 100);
                    if (result.getType() != RayTraceResult.Type.MISS) {
                        destroy(player.getEntityWorld(), result.getPos());
                    }
                }
                return 0;
            }));

    public static void register(String name, StructureBuilder builder) {
        COMMAND.then(Commands.literal(name)
              .requires(cs -> cs.hasPermissionLevel(4))
              .executes(ctx -> {
                  CommandSource source = ctx.getSource();
                  Entity entity = source.getEntity();
                  if (entity instanceof ServerPlayerEntity) {
                      ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
                      BlockRayTraceResult result = MekanismUtils.rayTrace(player, 100);
                      if (result.getType() != RayTraceResult.Type.MISS) {
                          BlockPos pos = result.getPos().offset(Direction.UP);
                          builder.build(player.getEntityWorld(), pos);
                      }
                  }
                  return 0;
              }));
    }

    private static void destroy(World world, BlockPos pos) {
        Set<BlockPos> traversed = new HashSet<>();
        Stack<BlockPos> openSet = new Stack<>();
        openSet.add(pos);
        traversed.add(pos);
        while (!openSet.isEmpty()) {
            BlockPos ptr = openSet.pop();
            BlockState state = world.getBlockState(ptr);
            if (state.getBlock().getRegistryName().getNamespace().contains("mekanism")) {
                world.setBlockState(ptr, Blocks.AIR.getDefaultState());
                for (Direction side : Direction.values()) {
                    BlockPos offset = ptr.offset(side);
                    if (!traversed.contains(offset)) {
                        openSet.add(offset);
                        traversed.add(offset);
                    }
                }
            }
        }
    }
}

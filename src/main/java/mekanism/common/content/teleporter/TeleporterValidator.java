package mekanism.common.content.teleporter;

import mekanism.common.content.blocktype.BlockType;
import mekanism.common.lib.math.voxel.VoxelCuboid;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol;
import mekanism.common.lib.multiblock.StructureHelper;
import mekanism.common.registries.MekanismBlockTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class TeleporterValidator extends CuboidStructureValidator<TeleporterMultiblockData> {

	private static final VoxelCuboid MIN_CUBOID = new VoxelCuboid(1, 4, 1);
	private static final VoxelCuboid MAX_CUBOID = new VoxelCuboid(10, 10, 1);

	@Override
	protected FormationProtocol.CasingType getCasingType(BlockState state) {
		Block block = state.getBlock();
		if (BlockType.is(block, MekanismBlockTypes.TELEPORTER_FRAME)) {
			return FormationProtocol.CasingType.FRAME;
		} else if (BlockType.is(block, MekanismBlockTypes.TELEPORTER)) {
			return FormationProtocol.CasingType.OTHER;
		}
		return FormationProtocol.CasingType.INVALID;
	}

	@Override
	public boolean precheck() {
		cuboid = StructureHelper.fetchCuboid(structure, MIN_CUBOID, MAX_CUBOID, EnumSet.complementOf(EnumSet.of(VoxelCuboid.CuboidSide.TOP)), 8);
		return cuboid != null;
	}
}

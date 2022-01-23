package mekanism.common.tile.multiblock;

import mekanism.common.Mekanism;
import mekanism.common.content.teleporter.TeleporterMultiblockData;
import mekanism.common.lib.multiblock.MultiblockManager;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityTeleporterFrame extends TileEntityMultiblock<TeleporterMultiblockData> {

	public TileEntityTeleporterFrame(BlockPos pos, BlockState state) {
		super(MekanismBlocks.TELEPORTER_FRAME, pos, state);
	}

	@Override
	public TeleporterMultiblockData createMultiblock() {
		return new TeleporterMultiblockData(this);
	}

	@Override
	public MultiblockManager<TeleporterMultiblockData> getManager() {
		return Mekanism.teleporterManager;
	}
}

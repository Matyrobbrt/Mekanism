package mekanism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank.Current;
import mekanism.api.text.ILangEntry;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.bar.GuiMergedChemicalBar;
import mekanism.client.gui.element.button.GuiGasMode;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tier.ChemicalTankTier;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.common.util.text.TextUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiChemicalTank extends GuiConfigurableTile<TileEntityChemicalTank, MekanismTileContainer<TileEntityChemicalTank>> {

    public GuiChemicalTank(MekanismTileContainer<TileEntityChemicalTank> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        //Add the side holder before the slots, as it holds a couple of the slots
        addButton(GuiSideHolder.armorHolder(this));
        super.addGuiElements();
        addButton(new GuiMergedChemicalBar<>(this, tile, tile.getChemicalTank(), 42, 16, 116, 10, true));
        addButton(new GuiInnerScreen(this, 42, 37, 118, 28, () -> {
            List<ITextComponent> ret = new ArrayList<>();
            Current current = tile.getChemicalTank().getCurrent();
            if (current == Current.EMPTY) {
                ret.add(MekanismLang.CHEMICAL.translate(MekanismLang.NONE));
                ret.add(MekanismLang.GENERIC_FRACTION.translate(0, tile.getTier() == ChemicalTankTier.CREATIVE ? MekanismLang.INFINITE : TextUtils.format(tile.getTier().getStorage())));
            } else if (current == Current.GAS) {
                addStored(ret, tile.getChemicalTank().getGasTank(), MekanismLang.GAS);
            } else if (current == Current.INFUSION) {
                addStored(ret, tile.getChemicalTank().getInfusionTank(), MekanismLang.INFUSE_TYPE);
            } else if (current == Current.PIGMENT) {
                addStored(ret, tile.getChemicalTank().getPigmentTank(), MekanismLang.PIGMENT);
            } else if (current == Current.SLURRY) {
                addStored(ret, tile.getChemicalTank().getSlurryTank(), MekanismLang.SLURRY);
            } else {
                throw new IllegalStateException("Unknown current type");
            }
            return ret;
        }));
        addButton(new GuiGasMode(this, 159, 72, true, () -> tile.dumping, tile.getBlockPos(), 0));
    }

    private void addStored(List<ITextComponent> ret, IChemicalTank<?, ?> tank, ILangEntry langKey) {
        ret.add(langKey.translate(tank.getStack()));
        if (!tank.isEmpty() && tile.getTier() == ChemicalTankTier.CREATIVE) {
            ret.add(MekanismLang.INFINITE.translate());
        } else {
            ret.add(MekanismLang.GENERIC_FRACTION.translate(TextUtils.format(tank.getStored()),
                  tile.getTier() == ChemicalTankTier.CREATIVE ? MekanismLang.INFINITE : TextUtils.format(tank.getCapacity())));
        }
    }

    @Override
    protected void drawForegroundText(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        renderTitleText(matrix);
        drawString(matrix, inventory.getDisplayName(), inventoryLabelX, inventoryLabelY, titleTextColor());
        super.drawForegroundText(matrix, mouseX, mouseY);
    }
}
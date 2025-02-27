package mekanism.client.gui.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import mekanism.client.gui.GuiUtils;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiArrowSelection extends GuiTexturedElement {

    private static final ResourceLocation ARROW = MekanismUtils.getResource(ResourceType.GUI, "arrow_selection.png");

    private final Supplier<ITextComponent> textComponentSupplier;

    public GuiArrowSelection(IGuiWrapper gui, int x, int y, Supplier<ITextComponent> textComponentSupplier) {
        super(ARROW, gui, x, y, 33, 19);
        this.textComponentSupplier = textComponentSupplier;
    }

    @Override
    public boolean isMouseOver(double xAxis, double yAxis) {
        //TODO: override isHovered
        return this.active && this.visible && xAxis >= x + 16 && xAxis < x + width - 1 && yAxis >= y + 1 && yAxis < y + height - 1;
    }

    @Override
    public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        super.renderToolTip(matrix, mouseX, mouseY);
        ITextComponent component = textComponentSupplier.get();
        if (component != null) {
            int tooltipX = mouseX + 5;
            int tooltipY = mouseY - 5;
            GuiUtils.renderBackgroundTexture(matrix, GuiInnerScreen.SCREEN, GuiInnerScreen.SCREEN_SIZE, GuiInnerScreen.SCREEN_SIZE, tooltipX - 3, tooltipY - 4, getStringWidth(component) + 6, 16, 256, 256);
            IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
            matrix.pushPose();
            //Make sure the text is above other renders like JEI
            matrix.translate(0.0D, 0.0D, 300);
            getFont().drawInBatch(component, tooltipX, tooltipY, screenTextColor(), false, matrix.last().pose(),
                  renderType, false, 0, MekanismRenderer.FULL_LIGHT);
            matrix.popPose();
            renderType.endBatch();
        }
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(matrix, mouseX, mouseY, partialTicks);
        minecraft.textureManager.bind(getResource());
        blit(matrix, x, y, 0, 0, width, height, width, height);
    }
}
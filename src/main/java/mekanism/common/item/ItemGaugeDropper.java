package mekanism.common.item;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.fluid.IExtendedFluidHandler;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.merged.GaugeDropperContentsHandler;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.StorageUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class ItemGaugeDropper extends Item {

    public ItemGaugeDropper(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return StorageUtils.getDurabilityForDisplay(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        FluidStack fluidStack = StorageUtils.getStoredFluidFromNBT(stack);
        if (!fluidStack.isEmpty()) {
            //TODO: Technically doesn't support things where the color is part of the texture such as lava
            // for chemicals it is supported via allowing people to override getColorRepresentation in their
            // chemicals
            if (fluidStack.getFluid().isSame(Fluids.LAVA)) {
                //Special case lava
                return 0xFFDB6B19;
            }
            return fluidStack.getFluid().getAttributes().getColor(fluidStack);
        }
        return ChemicalUtil.getRGBDurabilityForDisplay(stack);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!world.isClientSide) {
                Optional<IFluidHandlerItem> fluidCapability = FluidUtil.getFluidHandler(stack).resolve();
                if (fluidCapability.isPresent()) {
                    IFluidHandlerItem fluidHandler = fluidCapability.get();
                    if (fluidHandler instanceof IExtendedFluidHandler) {
                        IExtendedFluidHandler fluidHandlerItem = (IExtendedFluidHandler) fluidHandler;
                        for (int tank = 0; tank < fluidHandlerItem.getTanks(); tank++) {
                            fluidHandlerItem.setFluidInTank(tank, FluidStack.EMPTY);
                        }
                    }
                }
                clearChemicalTanks(stack, GasStack.EMPTY);
                clearChemicalTanks(stack, InfusionStack.EMPTY);
                clearChemicalTanks(stack, PigmentStack.EMPTY);
                clearChemicalTanks(stack, SlurryStack.EMPTY);
                ((ServerPlayerEntity) player).refreshContainer(player.containerMenu);
            }
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        return new ActionResult<>(ActionResultType.PASS, stack);
    }

    private static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> void clearChemicalTanks(ItemStack stack, STACK empty) {
        Optional<IChemicalHandler<CHEMICAL, STACK>> cap = stack.getCapability(ChemicalUtil.getCapabilityForChemical(empty)).resolve();
        if (cap.isPresent()) {
            IChemicalHandler<CHEMICAL, STACK> handler = cap.get();
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                handler.setChemicalInTank(tank, empty);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        StorageUtils.addStoredSubstance(stack, tooltip, false);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new ItemCapabilityWrapper(stack, GaugeDropperContentsHandler.create());
    }
}
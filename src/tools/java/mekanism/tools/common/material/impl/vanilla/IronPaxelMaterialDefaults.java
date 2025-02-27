package mekanism.tools.common.material.impl.vanilla;

import javax.annotation.Nonnull;
import mekanism.tools.common.material.VanillaPaxelMaterial;
import net.minecraft.item.ItemTier;

public class IronPaxelMaterialDefaults extends VanillaPaxelMaterial {

    @Nonnull
    @Override
    public ItemTier getVanillaTier() {
        return ItemTier.IRON;
    }

    @Override
    public float getPaxelDamage() {
        return 7;
    }

    @Override
    public String getConfigCommentName() {
        return "Iron";
    }
}
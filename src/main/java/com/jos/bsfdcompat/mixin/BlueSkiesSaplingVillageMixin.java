package com.jos.bsfdcompat.mixin;

import com.legacy.blue_skies.world.everbright.gen.features.BluebrightTreeFeature;
import com.legacy.blue_skies.world.everbright.gen.features.FrostbrightTreeFeature;
import com.legacy.blue_skies.world.everbright.gen.features.SmallPineTreeFeature;
import com.legacy.blue_skies.world.everbright.gen.features.StarlitTreeFeature;
import com.legacy.blue_skies.world.everdawn.gen.features.CrescentFruitTreeFeature;
import com.legacy.blue_skies.world.everdawn.gen.features.DuskTreeFeature;
import com.legacy.blue_skies.world.everdawn.gen.features.LargeDuskTreeFeature;
import com.legacy.blue_skies.world.everdawn.gen.features.LunarTreeFeature;
import com.legacy.blue_skies.world.everdawn.gen.features.MapleTreeFeature;
import com.legacy.blue_skies.world.general_features.AbstractSkyTreeFeature;
import com.legacy.blue_skies.world.general_features.CometTreeFeature;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;

/** Allows sapling trees in villages during gameplay, preserving world generation checks. */
@Mixin(value = {
        BluebrightTreeFeature.class,
        StarlitTreeFeature.class,
        SmallPineTreeFeature.class,
        FrostbrightTreeFeature.class,
        LunarTreeFeature.class,
        CometTreeFeature.class,
        DuskTreeFeature.class,
        LargeDuskTreeFeature.class,
        MapleTreeFeature.class,
        CrescentFruitTreeFeature.class
}, remap = false)
public abstract class BlueSkiesSaplingVillageMixin extends AbstractSkyTreeFeature {
    protected BlueSkiesSaplingVillageMixin(Codec<TreeConfiguration> codec, boolean natural) {
        super(codec, natural);
    }

    @Override
    public boolean test(WorldGenLevel level, Structure structure, BlockPos pos) {
        // This predicate identifies blocking structures; false excludes a village from that list.
        if (level instanceof ServerLevel
                && level.registryAccess().registryOrThrow(Registries.STRUCTURE)
                .wrapAsHolder(structure).is(StructureTags.VILLAGE)) {
            return false;
        }
        return super.test(level, structure, pos);
    }
}

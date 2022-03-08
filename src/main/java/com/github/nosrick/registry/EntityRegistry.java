package com.github.nosrick.registry;

import com.github.nosrick.RatsMod;
import com.github.nosrick.entity.RatEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;

public abstract class EntityRegistry {

    public static EntityType<RatEntity> RAT;

    protected static EntityType<? extends LivingEntity> registerEntity(String name, EntityType<? extends LivingEntity> type) {
        return Registry.register(Registry.ENTITY_TYPE, RatsMod.MOD_ID + ":" + name, type);
    }

    public static void registerAll() {
        RAT = Registry.register(
                Registry.ENTITY_TYPE,
                RatsMod.MOD_ID + ":" + "rat",
                FabricEntityTypeBuilder.createMob()
                        .entityFactory(RatEntity::new)
                        .spawnGroup(SpawnGroup.AMBIENT)
                        .dimensions(
                                EntityDimensions.changing(0.3f, 0.25f))
                        .trackRangeBlocks(8)
                        .spawnRestriction(
                                SpawnRestriction.Location.ON_GROUND,
                                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                                RatEntity::canMobSpawn)
                        .build());
        FabricDefaultAttributeRegistry.register(RAT, RatEntity.createDefaultAttributes());
    }
}

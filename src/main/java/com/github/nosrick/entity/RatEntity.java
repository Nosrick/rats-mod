package com.github.nosrick.entity;

import com.github.nosrick.registry.EntityRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.UUID;

public class RatEntity extends TameableEntity implements IAnimatable {

    protected Goal action;
    protected VillagerProfession ratProfession;

    protected static final TrackedData<String> STATE = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.STRING);
    protected static final TrackedData<Integer> AFFECTION = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.INTEGER);

    //Rat behaviour states
    public static final String STATE_LOAFING = "loafing";
    public static final String STATE_IDLING = "idling";
    public static final String STATE_EATING = "eating";
    public static final String STATE_NONE = "none";

    protected final AnimationFactory factory = new AnimationFactory(this);

    public RatEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);

        this.ignoreCameraFrustum = false;
        this.stepHeight = 2f;
        this.moveControl = new MoveControl(this);
        this.lookControl = new LookControl(this);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        RatEntity babyRat = EntityRegistry.RAT.create(world);

        UUID ownerUuid = this.getOwnerUuid();
        if(ownerUuid != null) {
            babyRat.setOwnerUuid(ownerUuid);
            babyRat.setTamed(true);
            babyRat.setBaby(true);
            babyRat.dataTracker.set(AFFECTION, 10);
        }

        return babyRat;
    }

    public static DefaultAttributeContainer.Builder createDefaultAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10d)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5d)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1d)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    protected <E extends IAnimatable> PlayState animationPredicate(AnimationEvent<E> event) {

        if(event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.run", true));
            this.dataTracker.set(STATE, STATE_NONE);
            return PlayState.CONTINUE;
        }

        String animationState = this.dataTracker.get(STATE);

        switch (animationState) {
            case STATE_EATING -> {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.eat", true));
                return PlayState.CONTINUE;
            }
            case STATE_LOAFING -> {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.loaf", false));
                return PlayState.CONTINUE;
            }
            case STATE_IDLING -> {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.idle", false));
                return PlayState.CONTINUE;
            }
        }

        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0, this::animationPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    protected void mobTick() {
        super.mobTick();

        if(this.getOwnerUuid() != null
                && this.dataTracker.get(AFFECTION) > 50) {
            this.dataTracker.set(STATE, STATE_LOAFING);
        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.3f));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1d, true));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1d));
        this.goalSelector.add(8, new WanderAroundPointOfInterestGoal(this, 1d, false));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
        this.goalSelector.add(10, new LookAroundGoal(this));
    }
}

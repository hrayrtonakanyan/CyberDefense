package com.hro.hrogame.gameobject.effect.residualeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.animation.particleanimation.ParticleAnimation;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.controller.SoundController;
import com.hro.hrogame.data.effect.residualeffectdata.FreezeOverTimeEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.GameObjectAdapter;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.utils.Util;

public class FreezeOverTimeEffect extends Effect {

    // region Static fields
    public static final float DURATION = 3;
    public static final float MAX_DURATION = 8;
    public static final float SPEED_RATIO = 0.6f;
    public static final float MIN_SPEED_RATIO = 0.1f;
    // endregion

    // region Instance fields
    private FreezeOverTimeEffectData data;
    private ParticleAnimation animation;
    private boolean isAllowedToExecute = true;
    private int level;
    // endregion

    // region C-tor
    public FreezeOverTimeEffect(GameObject owner, EntityManager entityManager, SoundController soundController, FreezeOverTimeEffectData data) {
        super(owner, entityManager, soundController);
        this.data = data;
        addFreezeOverTimeEffectAnimation();
        makeEffectOvertime();
        makeAutoExecutable();
        addOnDieListener();
    }
    // endregion

    // region Add on initialization
    private void addFreezeOverTimeEffectAnimation() {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("freeze_over_time"), Gdx.files.internal(""));
        adjustParticleEffectSize(particleEffect);
        animation = new ParticleAnimation(particleEffect);
        animation.setPosition(owner.getX(Align.center), owner.getY(Align.center), Align.center);
        animation.start();
        addActor(animation);
    }
    private void adjustParticleEffectSize(ParticleEffect particleEffect) {
        for (ParticleEmitter emitter : particleEffect.getEmitters()) {
            emitter.getSpawnWidth().setHigh(owner.getWidth());
            emitter.getSpawnHeight().setHigh(owner.getHeight());
        }
    }
    private void addOnDieListener() {
        owner.addGameObjectAdapter(new GameObjectAdapter() {
            @Override
            public void onDie(GameObject dyingUnit, GameObject killerUnit) {
                clearTimer();
            }
        });
    }
    // endregion

    // region Act
    @Override
    public void act(float delta) {
        super.act(delta);
        animation.setPosition(owner.getX(Align.center), owner.getY(Align.center), Align.center);
    }
    // endregion

    // region Execution
    @Override
    protected boolean isExecutable() {
        if (isAllowedToExecute) return true;
        else {
            owner.unFreeze();
            owner.removeEffect(this);
            return false;
        }
    }
    @Override
    protected void execute() {
        isAllowedToExecute = false;
        if (owner.isInvincible()) return;
        owner.freeze(data.speedRatio.current);
    }
    // endregion

    // region Renew and level up
    @Override
    public void levelUpEffect(int level)  {
        this.level = level;
        Util.calcProgressAndDefineWeight(0, level, ParametersConstants.PROGRESS_RATIO, true,
                data.duration, data.speedRatio);
    }
    public void reNew(int level) {
        if (this.level == level) isAllowedToExecute = true;
        else {
            levelUpEffect(level);
            isAllowedToExecute = true;
        }
    }
    // endregion

    // region Getters
    @Override
    protected float getCoolDown() {
        return data.duration.current;
    }
    @Override
    public float getEffectWeight() {
        return 0;
    }
    // endregion
}

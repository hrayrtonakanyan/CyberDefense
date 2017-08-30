package com.hro.hrogame.gameobject.effect.residualeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.animation.particleanimation.ParticleAnimation;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.effect.residualeffectdata.FreezeOverTimeEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.GameObjectAdapter;
import com.hro.hrogame.gameobject.effect.Effect;

public class FreezeOverTimeEffect extends Effect {

    // region Instance fields
    private FreezeOverTimeEffectData data;
    private ParticleAnimation animation;
    private boolean isAllowedToExecute = true;
    // endregion

    // region C-tor
    public FreezeOverTimeEffect(GameObject owner, EntityManager entityManager, FreezeOverTimeEffectData data) {
        super(owner, entityManager);
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
        owner.freeze(data.speedRatio);
    }
    // endregion

    // region Renew
    public void reNew() {
        isAllowedToExecute = true;
    }
    // endregion

    // region Getters
    @Override
    protected float getCoolDown() {
        return data.duration;
    }
    // endregion
}

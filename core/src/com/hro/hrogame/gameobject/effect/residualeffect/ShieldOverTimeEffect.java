package com.hro.hrogame.gameobject.effect.residualeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.animation.particleanimation.ParticleAnimation;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.effect.residualeffectdata.ShieldOverTimeEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.GameObjectAdapter;
import com.hro.hrogame.gameobject.effect.Effect;

public class ShieldOverTimeEffect extends Effect {

    // region Instance fields
    private ShieldOverTimeEffectData data;
    private ParticleAnimation animation;
    private boolean isAllowedToExecute = true;
    // endregion

    // region C-tor
    public ShieldOverTimeEffect(GameObject owner, EntityManager entityManager, ShieldOverTimeEffectData data) {
        super(owner, entityManager);
        this.data = data;
        addShieldOverTimeEffectAnimation();
        makeEffectOvertime();
        makeAutoExecutable();
        addOnDieListener();
    }
    // endregion

    // region Add on initialization
    private void addShieldOverTimeEffectAnimation() {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("shield_over_time"), Gdx.files.internal(""));
        adjustParticleEffectSize(particleEffect);
        animation = new ParticleAnimation(particleEffect);
        animation.setPosition(owner.getX(Align.center), owner.getY(Align.center), Align.center);
        animation.start();
        addActor(animation);
    }
    private void adjustParticleEffectSize(ParticleEffect particleEffect) {
        for (ParticleEmitter emitter : particleEffect.getEmitters()) {
            emitter.getSpawnWidth().setHigh(owner.getWidth());
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
            owner.makeAttackable();
            owner.removeEffect(this);
            return false;
        }
    }
    @Override
    protected void execute() {
        System.out.println(owner.getPlayerType() + " become invincible");
        owner.makeInvincible();
        isAllowedToExecute = false;
    }
    // endregion

    // region Renew
    public void reNew() {
        isAllowedToExecute = true;
    }
    // endregion

    // region Getter
    @Override
    protected float getCoolDown() {
        return data.duration;
    }
    // endregion
}

package com.hro.hrogame.gameobject.effect.residualeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.animation.particleanimation.ParticleAnimation;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.controller.SoundController;
import com.hro.hrogame.data.effect.residualeffectdata.ShieldOverTimeEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.GameObjectAdapter;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.utils.Util;

public class ShieldOverTimeEffect extends Effect {

    // region Static fields
    public static final float DURATION = 3;
    public static final float MAX_DURATION = 10;
    // endregion

    // region Instance fields
    private ShieldOverTimeEffectData data;
    private ParticleAnimation animation;
    private boolean isAllowedToExecute = true;
    private int level;
    // endregion

    // region C-tor
    public ShieldOverTimeEffect(GameObject owner, EntityManager entityManager, SoundController soundController, ShieldOverTimeEffectData data) {
        super(owner, entityManager, soundController);
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

    // region Renew and level up
    @Override
    public void levelUpEffect(int level)  {
        this.level = level;
        Util.calcProgressAndDefineWeight(0, level, ParametersConstants.PROGRESS_RATIO, true,
                data.duration);
    }
    public void reNew(int level) {
        if (this.level == level) isAllowedToExecute = true;
        else {
            levelUpEffect(level);
            isAllowedToExecute = true;
        }
    }
    // endregion

    // region Getter
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

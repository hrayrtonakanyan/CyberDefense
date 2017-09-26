package com.hro.hrogame.gameobject.effect.residualeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.hro.hrogame.animation.particleanimation.ParticleAnimation;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.effect.residualeffectdata.BurnOverTimeEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.GameObjectAdapter;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.utils.Util;

public class BurnOverTimeEffect extends Effect {

    // region Static fields
    public static final float COOLDOWN = 5;
    public static final float MIN_COOLDOWN = 1;
    public static final float DAMAGE = 20;
    public static final float MAX_DAMAGE = 50;
    public static final float TOTAL_DAMAGE = 60;
    public static final float MAX_TOTAL_DAMAGE = 150;
    // endregion

    // region Instance fields
    private BurnOverTimeEffectData data;
    private ParticleAnimation animation;
    private float maxDamageAmount;
    private int level;
    // endregion

    // region C-tor
    public BurnOverTimeEffect(GameObject owner, EntityManager entityManager, BurnOverTimeEffectData data) {
        super(owner, entityManager);
        this.data = data;
        maxDamageAmount = data.maxDamageAmount.current;
        addBurnOverTimeEffectAnimation();
        addOnDieListener();
        makeEffectOvertime();
        startEffect();
    }
    // endregion

    // region Initialization methods
    private void addBurnOverTimeEffectAnimation() {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("burn_over_time"), Gdx.files.internal(""));
        animation = new ParticleAnimation(particleEffect);
        animation.setPosition(owner.getX(Align.center), owner.getY(Align.center), Align.center);
        animation.start();
        addActor(animation);
    }
    private void addOnDieListener() {
        owner.addGameObjectAdapter(new GameObjectAdapter() {
            @Override
            public void onDie(GameObject dyingUnit, GameObject killerUnit) {
                clearTimer();
            }
        });
    }
    private void startEffect() {
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                makeAutoExecutable();
            }
        }, data.cooldown.current);
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
        if (maxDamageAmount > 0) return true;
        else {
            owner.removeEffect(this);
            return false;
        }
    }
    @Override
    protected void execute() {
        if (owner.isInvincible()) return;
        owner.takeDamage(this, data.damage.current);
        maxDamageAmount -= data.damage.current;
    }
    // endregion

    // region Renew and level up
    @Override
    public void levelUpEffect(int level) {
        this.level = level;
        Util.calcProgressAndDefineWeight(0, level, ParametersConstants.PROGRESS_RATIO, true,
                data.cooldown, data.damage, data.maxDamageAmount);
    }
    public void reNew(int level) {
        if (this.level == level) maxDamageAmount = data.maxDamageAmount.current;
        else {
            levelUpEffect(level);
            maxDamageAmount = data.maxDamageAmount.current;
        }
    }
    // endregion

    // region Getters
    @Override
    protected float getCoolDown() {
        return data.cooldown.current;
    }
    @Override
    public float getEffectWeight() {
        return 0;
    }
    // endregion
}

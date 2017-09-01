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

public class BurnOverTimeEffect extends Effect {

    // region Static fields
    public static final float COOLDOWN = 3;
    public static final float MIN_COOLDOWN = 3;
    public static final float DAMAGE = 20;
    public static final float MAX_DAMAGE_AMOUNT = 60;
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
        maxDamageAmount = data.maxDamageAmount;
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
        }, data.cooldown);
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
        owner.takeDamage(this, data.damage);
        maxDamageAmount -= data.damage;
    }
    // endregion

    // region Renew and level up
    @Override
    public void levelUp(boolean showParticle) {
        if (isMaxLevel) return;
        data.cooldown -= data.cooldown * ParametersConstants.WEIGHT_PROGRESS;
        if (data.cooldown < MIN_COOLDOWN) {
            data.cooldown = MIN_COOLDOWN;
            isMaxLevel = true;
            return;
        }
        data.damage += data.damage * ParametersConstants.WEIGHT_PROGRESS;
        data.maxDamageAmount += data.maxDamageAmount * ParametersConstants.WEIGHT_PROGRESS;
    }
    public void reNew(int level) {
        if (this.level == level) maxDamageAmount = data.maxDamageAmount;
        else {
            this.level = level;
            setLevel(level);
            maxDamageAmount = data.maxDamageAmount;
        }
    }
    // endregion

    // region Getters
    @Override
    protected float getCoolDown() {
        return data.cooldown;
    }
    @Override
    public int getWeight() {
        return 0;
    }
    // endregion
}

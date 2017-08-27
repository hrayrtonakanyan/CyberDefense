package com.hro.hrogame.gameobject.effect.residualeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.hro.hrogame.animation.ParticleAnimation;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.effect.residualeffectdata.BurnOverTimeEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.GameObjectAdapter;
import com.hro.hrogame.gameobject.effect.Effect;

public class BurnOverTimeEffect extends Effect {

    // region Instance fields
    private BurnOverTimeEffectData data;
    private ParticleAnimation animation;
    private float maxDamageAmount;
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

    // region Add on initialization
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
        return true;
    }
    @Override
    protected void execute() {
        if (maxDamageAmount <= 0) owner.removeEffect(this);
        else {
            owner.takeDamage(this, data.damage);
            maxDamageAmount -= data.damage;

            owner.setColor(Color.RED);
            Timer.instance().scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    owner.setColor(Color.WHITE);
                }
            }, 1);
            System.out.println("Target Health: " + owner.getCurrentHealth() + "/" + owner.getMaxHealth());
        }
    }
    // endregion

    // region Renew
    public void reNew() {
        maxDamageAmount = data.maxDamageAmount;
    }
    // endregion

    // region Getters
    @Override
    protected float getCoolDown() {
        return data.cooldown;
    }
    // endregion
}

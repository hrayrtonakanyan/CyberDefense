package com.hro.hrogame.gameobject.effect.cannoneffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.animation.particleanimation.ParticleAnimation;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.controller.SoundController;
import com.hro.hrogame.data.effect.cannoneffectdata.CannonEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.unit.BaseUnit;
import com.hro.hrogame.gameobject.unit.TankUnit;

import java.util.List;

public abstract class CannonEffect extends Effect {

    // region Instance field
    protected ParticleAnimation hittingAnimation;
    protected CannonEffectData data;
    private Vector2 firingPoint;
    private GameObject target;
    // endregion

    // region C-tor
    public CannonEffect(Skin skin, GameObject owner, EntityManager entityManager,
                        SoundController soundController, CannonEffectData data) {
        super(skin, owner, entityManager, soundController);
        this.data = data;
        firingPoint = new Vector2();
        levelUpEffect(owner.getLevel());
        makeAutoExecutable();
        initAnimation();
    }
    // endregion

    private void initAnimation() {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("bang_particle"), skin.getAtlas());
        hittingAnimation = new ParticleAnimation(particleEffect);
    }

    // region Act
    @Override
    public void act(float delta) {
        sensor.setPosition(owner.getWidth() / 2, owner.getHeight() / 2, Align.center);
        super.act(delta);
    }
    // endregion

    // region Execution
    @Override
    protected boolean isExecutable() {
        return sensor.obtainEnemies().size() > 0;
    }
    @Override
    protected void execute() {
        if (target != null && !target.isDead()) {
            shootABullet(target);
            return;
        }
        List<GameObject> targetList = sensor.obtainEnemies((int) data.targetLimit.current);
        for (GameObject target : targetList) {
            shootABullet(target);
        }
    }
    // endregion

    // region Shoot
    protected abstract void shootABullet(GameObject target);
    private Vector2 defineFiringPoint() {
        if (owner == null) throw new RuntimeException("Owner must not be null.");
        if (owner instanceof BaseUnit) firingPoint.set(owner.getWidth() / 2, owner.getHeight() / 2);
        if (owner instanceof TankUnit) firingPoint.set(owner.getWidth(), owner.getHeight() / 2);
        return localToStageCoordinates(firingPoint);
    }
    // endregion

    // region Setter
    public void setTarget(GameObject target) {
        this.target = target;
    }
    // endregion

    // region Getter
    @Override
    protected float getCoolDown() {
        return data.cooldown.current;
    }
    @Override
    public float getEffectWeight() {
        return data.weight;
    }
    public float getFiringPointX() {
        return defineFiringPoint().x;
    }
    public float getFiringPointY() {
        return defineFiringPoint().y;
    }
    // endregion
}

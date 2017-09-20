package com.hro.hrogame.gameobject.effect.cannoneffect;

import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.effect.cannoneffectdata.CannonEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.unit.BaseUnit;
import com.hro.hrogame.gameobject.unit.TankUnit;
import com.hro.hrogame.primitives.Point;

import java.util.List;

public abstract class CannonEffect extends Effect {

    // region Instance field
    protected CannonEffectData data;
    // endregion

    // region C-tor
    public CannonEffect(GameObject owner, EntityManager entityManager, CannonEffectData data) {
        super(owner, entityManager);
        this.data = data;
        levelUpEffect(owner.getLevel());
        makeAutoExecutable();
    }
    // endregion

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
        List<GameObject> targetList = sensor.obtainEnemies((int)data.targetLimit.current);
        for (GameObject target : targetList) {
            shootABullet(target);
        }
    }
    // endregion

    // region Shoot
    protected abstract void shootABullet(GameObject target);
    protected Point defineShootingPoint() {
        if (owner == null) throw new RuntimeException("Owner must not be null.");
        if (owner instanceof BaseUnit) return new Point(owner.getX() + owner.getWidth() / 2,
                                                        owner.getY() + owner.getHeight() / 2);
        if (owner instanceof TankUnit) return new Point(owner.getX() + owner.getWidth(),
                                                        owner.getY() + owner.getHeight() / 2);
        throw new RuntimeException("The Owner of the bullet is not Firing Unit");
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
    // endregion
}

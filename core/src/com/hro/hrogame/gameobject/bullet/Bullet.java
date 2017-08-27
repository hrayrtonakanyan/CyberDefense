package com.hro.hrogame.gameobject.bullet;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.bullet.BulletData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.sensor.UnitSensor;

import java.util.ArrayList;

/**
 * Created by Lion on 8/14/17.
 */
public abstract class Bullet extends GameObject {

    // region Instance fields
    protected BulletData bulletData;
    protected EntityManager entityManager;
    protected ArrayList<BulletListener> bulletListeners = new ArrayList<>();
    // endregion

    // region C-tor
    public Bullet(EntityManager entityManager) {
        this.entityManager = entityManager;
        setTouchable(Touchable.disabled);
    }
    // endregion

    // region Abstract
    public abstract void initialize(BulletData bulletData);
    public abstract UnitSensor getUnitSensor();
    // endregion

    // region Add/Remove
    public void addBulletListener(BulletListener listener) {
        bulletListeners.add(listener);
    }
    public boolean removeBulletListener(BulletListener listener) {
        return bulletListeners.remove(listener);
    }
    // endregion

    // region Getters
    @Override
    public GameObject revealOwningUnit() {
        return this;
    }
    public BulletData getBulletData() {
        return bulletData;
    }
    // endregion
}

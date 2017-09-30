package com.hro.hrogame.gameobject.bullet;

import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.bullet.BulletData;
import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.GameObjectAdapter;
import com.hro.hrogame.primitives.ProgressiveAttribute;
import com.hro.hrogame.sensor.CircleSensor;
import com.hro.hrogame.sensor.SensorType;
import com.hro.hrogame.sensor.UnitSensor;

import java.util.List;

public class TargetBullet extends Bullet {

    // region Instance fields
    private CircleSensor sensor;
    private GameObject target;
    private GameObjectAdapter adapter = initAdapter();
    // endregion

    // region C-tor
    public TargetBullet(EntityManager entityManager) {
        super(entityManager);
    }
    // endregion

    // region Initialize
    @Override
    public void initialize(BulletData bulletData) {
        if (sensor != null) throw new RuntimeException("Initialize method is only allowed to call once per bullet instance");
        this.bulletData = bulletData;
        GameObjectData gameObjectData = new GameObjectData();
        gameObjectData.speed = new ProgressiveAttribute(bulletData.speed, bulletData.speed);
        gameObjectData.health = new ProgressiveAttribute(0, 0);
        setGameObjectData(gameObjectData);
        // TODO: 8/16/17 Remove texture and set drawable;
        setAppearance(bulletData.texturePath);
        sensor = (CircleSensor) entityManager.createSensor(this, SensorType.CIRCLE_SENSOR);
        updateSensorPosition();
        addActor(sensor);
    }
    private GameObjectAdapter initAdapter() {
        return new GameObjectAdapter() {
            @Override
            public void onPositionChange(GameObject gameObject) {
                updateDestination();
            }
            @Override
            public void onDie(GameObject dyingUnit, GameObject killerUnit) {
                entityManager.removeBullet(TargetBullet.this);
            }
        };
    }
    private GameObjectAdapter createGameObjectAdapter() {
        return new GameObjectAdapter() {
            @Override
            public void onDestinationArrive(GameObject gameObject) {
                List<GameObject> hitUnitList = sensor.obtainEnemies(bulletData.hitUnitLimit);
                notifyOnHit(hitUnitList);
                entityManager.removeBullet(TargetBullet.this);
            }
            @Override
            public void onPositionChange(GameObject gameObject) {
                updateSensorPosition();
            }
            @Override
            public void onSizeChange(GameObject gameObject) {
                updateSensorPosition();
            }
        };
    }
    // endregion

    // region Shoot
    public void shoot(GameObject target) {
        this.target = target;
        target.addGameObjectAdapter(adapter);
        updateDestination();
        addGameObjectAdapter(createGameObjectAdapter());
        setOrigin(Align.center);
    }
    // endregion

    // region Update
    private void updateDestination() {
        setDestination(target.getX(Align.center), target.getY(Align.center));
        updateAngle();
    }
    private void updateAngle() {
        float dy = target.getY(Align.center) - getY(Align.center);
        float dx = target.getX(Align.center) - getX(Align.center);
        float angle;
        angle = (float)Math.toDegrees(Math.atan2(dy, dx));
        setRotation(angle);
    }
    private void updateSensorPosition() {
        sensor.setPosition(getWidth() / 2, getHeight() / 2, Align.center);
    }
    // endregion

    // region Notify
    private void notifyOnHit(List<GameObject> hitUnitList) {
        for (BulletListener listener : bulletListeners) listener.onHit(hitUnitList);
    }
    // endregion

    // region Getter
    @Override
    public UnitSensor getUnitSensor() {
        return sensor;
    }
    // endregion
}

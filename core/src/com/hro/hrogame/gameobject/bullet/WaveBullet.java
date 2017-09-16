package com.hro.hrogame.gameobject.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.timer.Timer;
import com.hro.hrogame.animation.particleanimation.ParticleAnimation;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.bullet.BulletData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.primitives.Point;
import com.hro.hrogame.sensor.CircleSensor;
import com.hro.hrogame.sensor.SensorType;
import com.hro.hrogame.sensor.UnitSensor;

import java.util.ArrayList;
import java.util.List;

public class WaveBullet extends Bullet {

    // region Instance fields
    private Point centerPoint = new Point();
    private List<GameObject> excludeList = new ArrayList<>();;
    private CircleSensor sensor;
    private Timer hitTimer;
    private float maxRadius;
    // endregion

    // region C-tor
    public WaveBullet(EntityManager entityManager) {
        super(entityManager);
    }
    // endregion

    // region Initialize
    @Override
    public void initialize(BulletData bulletData) {
        if (sensor != null) throw new RuntimeException("Initialize method is only allowed to call once per bullet instance");
        this.bulletData = bulletData;
        sensor = (CircleSensor) entityManager.createSensor(this, SensorType.CIRCLE_SENSOR);
//        sensor.removeAppearance();
        sensor.setPosition(getX(Align.center), getY(Align.center), Align.center);
        addActor(sensor);
        maxRadius = calculateMaxRadius();
        hitTimer = new Timer();
        hitTimer.scheduleTask(hitTimer.createTask(0, 0.5f, new Runnable() {
            @Override
            public void run() {
                hit();
            }
        }));
    }
    private float calculateMaxRadius() {
        float a = Gdx.graphics.getWidth() / 2;
        float b = Gdx.graphics.getHeight() / 2;
        return (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }
    // endregion

    // region Act
    @Override
    public void act(float delta) {
        super.act(delta);
        hitTimer.update(delta);
        if (getWidth() / 2 <= maxRadius) fuzz(delta);
        else entityManager.removeBullet(this);
    }
    private void fuzz(float delta) {
        setSize(getWidth() + bulletData.speed * delta, getHeight() + bulletData.speed * delta);
        super.setPosition(centerPoint.x, centerPoint.y, Align.center);
        sensor.setRadius(getWidth() / 2);
    }
    // endregion

    // region Hit
    private void hit() {
        List<GameObject> hitUnitList = sensor.obtainEnemies();
        for (GameObject gameObject : excludeList) {
            if (hitUnitList.contains(gameObject)) hitUnitList.remove(gameObject);
        }
        notifyOnHit(hitUnitList);
        excludeList.addAll(hitUnitList);
    }
    private void notifyOnHit(List<GameObject> hitUnitList) {
        for (BulletListener listener : bulletListeners) listener.onHit(hitUnitList);
    }
    // endregion

    // region Override
    @Override
    public void play() {
        super.play();
        hitTimer.resume();
    }
    @Override
    public void pause() {
        super.pause();
        hitTimer.pause();
    }
    @Override
    public boolean remove() {
        hitTimer.clear();
        return super.remove();
    }
    // endregion

    // region Setters
    public void setBulletAnimation(ParticleEffect bulletParticleEffect) {
        for (ParticleEmitter emitter : bulletParticleEffect.getEmitters()) {
            emitter.getVelocity().setHigh(bulletData.speed / 2);
        }
        ParticleAnimation bulletAnimation = new ParticleAnimation(bulletParticleEffect);
        bulletAnimation.setPosition(centerPoint.x, centerPoint.y, Align.center);
        bulletAnimation.start();
        addActor(bulletAnimation);
    }
    public void setFuzzPosition(float x, float y) {
        centerPoint.x = x;
        centerPoint.y = y;
    }
    @Override
    public void setPosition(float x, float y, int alignment) {
        throw new RuntimeException("For setting position use .setFuzzPosition() method");
    }
    @Override
    public void setPosition(float x, float y) {
        throw new RuntimeException("For setting position use .setFuzzPosition() method");
    }
    // endregion

    // region Getters
    @Override
    public UnitSensor getUnitSensor() {
        return sensor;
    }
    // endregion
}

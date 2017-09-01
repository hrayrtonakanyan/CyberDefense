package com.hro.hrogame.controller;

import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.PlayerRace;
import com.hro.hrogame.gameobject.bullet.Bullet;
import com.hro.hrogame.gameobject.bullet.BulletType;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.effect.EffectType;
import com.hro.hrogame.gameobject.unit.UnitType;
import com.hro.hrogame.sensor.SensorType;
import com.hro.hrogame.sensor.UnitSensor;

public interface EntityManager extends EntityProvider {

    GameObject createUnit(UnitType type, PlayerRace race, int level);
    Effect createEffect(GameObject owner, EffectType type);
    UnitSensor createSensor(GameObject owner, SensorType type);
    Bullet createBullet(BulletType bulletType);
    boolean removeUnit(GameObject unit);
    boolean removeBullet(Bullet bullet);
    // TODO: 8/14/17 change this implementation to take drawable name instead of texture.
}

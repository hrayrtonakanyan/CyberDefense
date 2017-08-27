package com.hro.hrogame.controller;

import com.hro.hrogame.gameobject.PlayerRace;
import com.hro.hrogame.gameobject.bullet.Bullet;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.bullet.BulletType;
import com.hro.hrogame.gameobject.unit.UnitType;
import com.hro.hrogame.sensor.UnitSensor;

/**
 * Created by Lion on 8/14/17.
 */
public interface EntityManager extends EntityProvider {

    Bullet createBullet(BulletType bulletType);
    GameObject createUnit(UnitType type, PlayerRace race, int level);
    UnitSensor createSensor(GameObject owner, UnitType type);
    boolean removeUnit(GameObject unit);
    boolean removeBullet(Bullet bullet);
    // TODO: 8/14/17 change this implementation to take drawable name instead of texture.
}

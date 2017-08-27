package com.hro.hrogame.controller;

import com.badlogic.gdx.graphics.Texture;
import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.filter.ClosestUnitFilter;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.GameObjectAdapter;
import com.hro.hrogame.gameobject.PlayerRace;
import com.hro.hrogame.gameobject.bullet.Bullet;
import com.hro.hrogame.gameobject.bullet.BulletType;
import com.hro.hrogame.gameobject.bullet.TargetBullet;
import com.hro.hrogame.gameobject.bullet.WaveBullet;
import com.hro.hrogame.gameobject.unit.BaseUnit;
import com.hro.hrogame.gameobject.unit.TankUnit;
import com.hro.hrogame.gameobject.unit.UnitType;
import com.hro.hrogame.sensor.CircleSensor;
import com.hro.hrogame.sensor.RectangleSensor;
import com.hro.hrogame.sensor.UnitSensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hro.hrogame.constants.ParametrsConstants.*;

/**
 * Created by Lion on 8/14/17.
 */
public class EntityFactory implements EntityManager {


    // TODO: 8/21/2017 Remove the static textures and use skin.getDrawable() instead.
    public static Texture circleTexture = new Texture("circle_range.png");


    private HashMap<PlayerRace, ArrayList<GameObject>> unitMap = new HashMap<>();

    public EntityFactory() {
        for (PlayerRace race : PlayerRace.values()) {
            unitMap.put(race, new ArrayList<GameObject>());
        }
    }

    // region Obtain Game Objects
    @Override
    public List<GameObject> obtainAll(List<GameObject> list) {
        list.clear();
        for (PlayerRace type : PlayerRace.values()) {
            List<GameObject> unitList = unitMap.get(type);
            list.addAll(unitList);
        }
        return list;
    }
    @Override
    public List<GameObject> obtainAllEnemies(PlayerRace requester, List<GameObject> list) {
        list.clear();
        obtainAll(list);
        for (int i = 0; i < list.size(); i++) {
            GameObject currentUnit = list.get(i);
            if (!currentUnit.getPlayerType().equals(requester)) continue;
            list.remove(currentUnit);
            i--;
        }
        return list;
    }
    @Override
    public List<GameObject> obtainAllAllies(GameObject requester, List<GameObject> list) {
        list.clear();
        List<GameObject> unitList = unitMap.get(requester.getPlayerType());
        list.addAll(unitList);
        list.remove(requester);
        return list;
    }
    // endregion

    // region Obtain Game Objects in range
    @Override
    public List<GameObject> obtainAll(List<GameObject> list, UnitSensor sensor) {
        obtainAll(list);
        return sensor.filterUnitsInShape(list);
    }
    @Override
    public List<GameObject> obtainEnemies(List<GameObject> list, UnitSensor sensor) {
        obtainAllEnemies(sensor.revealPlayerType(), list);
        return sensor.filterUnitsInShape(list);
    }
    @Override
    public List<GameObject> obtainAllies(List<GameObject> list, UnitSensor sensor) {
        obtainAllAllies(sensor.revealOwningUnit(), list);
        return sensor.filterUnitsInShape(list);
    }
    // endregion

    // region Create
    @Override
    public Bullet createBullet(BulletType bulletType) {
        // TODO: 8/16/17 Change bullet creation to use appropriate pools.
        switch (bulletType) {
            case TARGET_BULLET:
                return new TargetBullet(this);
            case WAVE_BULLET:
                return new WaveBullet(this);
        }
        return null;
    }
    @Override
    public GameObject createUnit(UnitType type, PlayerRace race, int level) {
        //TODO Change unit creation to use appropriate pools.
        switch (type) {
            case BASE:
                GameObjectData baseData = new GameObjectData(1, 50, 600, "base.png");
                BaseUnit baseUnit = new BaseUnit(baseData);
                baseUnit.setPlayerRace(race);
                baseUnit.addGameObjectAdapter(entityFactoryAdapter());
                addUnitToUnitMap(baseUnit);
                return baseUnit;
            case TANK:
                GameObjectData tankData = new GameObjectData(1, 50, 600, "tank.png");
                TankUnit tankUnit = new TankUnit(tankData);
                tankUnit.setPlayerRace(race);
                tankUnit.addGameObjectAdapter(entityFactoryAdapter());
                addUnitToUnitMap(tankUnit);
                return tankUnit;
        }
        return null;
    }
    @Override
    public UnitSensor createSensor(GameObject owner, UnitType type) {
        switch (type) {
            case BASE:
                RectangleSensor baseSensor = new RectangleSensor(owner, this);
                baseSensor.setUnitFilter(new ClosestUnitFilter(baseSensor));
                baseSensor.setSize(BASE_SIMPLE_CANNON_SENSOR_WIDTH, BASE_SIMPLE_CANNON_SENSOR_HEIGHT);
                return baseSensor;
            case TANK:
                CircleSensor tankSensor = new CircleSensor(owner, this, circleTexture);
                tankSensor.setUnitFilter(new ClosestUnitFilter(tankSensor));
                tankSensor.setRadius(TANK_SIMPLE_CANNON_SENSOR_RADIUS);
                return tankSensor;
            case BULLET:
                CircleSensor bulletSensor = new CircleSensor(owner, this, null);
                bulletSensor.setUnitFilter(new ClosestUnitFilter(bulletSensor));
                bulletSensor.setRadius(((Bullet) owner).getBulletData().splashAreaRadius);
                return bulletSensor;
        }
        return null;
    }
    // endregion

    // region Remove
    @Override
    public boolean removeUnit(GameObject unit) {
        unitMap.get(unit.getPlayerType()).remove(unit);
        //TODO Add pool release functionality here.
        return unit.remove();
    }
    @Override
    public boolean removeBullet(Bullet bullet) {
        //TODO Add pool release functionality here.
        return bullet.remove();
    }
    // endregion

    private GameObjectAdapter entityFactoryAdapter() {
        return new GameObjectAdapter() {
            @Override
            public void onPlayerTypeChange(GameObject gameObject, PlayerRace oldPlayerType) {
                unitMap.get(oldPlayerType).remove(gameObject);
                addUnitToUnitMap(gameObject);
            }
            @Override
            public void onDie(GameObject dyingUnit, GameObject killerUnit) {
                unitMap.get(dyingUnit.getPlayerType()).remove(dyingUnit);
                // TODO: 8/17/17 write the pool logic here
                dyingUnit.remove();
                System.out.println("Unit killed!");
            }
        };
    }
    private void addUnitToUnitMap(GameObject unit) {
        unitMap.get(unit.getPlayerType()).add(unit);
    }
}

package com.hro.hrogame.controller;

import com.badlogic.gdx.graphics.Texture;
import com.hro.hrogame.data.effect.cannoneffectdata.CannonEffectData;
import com.hro.hrogame.data.effect.shieldeffectdata.AbsorbShieldEffectData;
import com.hro.hrogame.data.effect.waveeffectdata.FreezerEffectData;
import com.hro.hrogame.data.effect.waveeffectdata.HellFireEffectData;
import com.hro.hrogame.data.effect.waveeffectdata.StunnerEffectData;
import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.filter.ClosestUnitFilter;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.GameObjectAdapter;
import com.hro.hrogame.gameobject.PlayerRace;
import com.hro.hrogame.gameobject.bullet.Bullet;
import com.hro.hrogame.gameobject.bullet.BulletType;
import com.hro.hrogame.gameobject.bullet.TargetBullet;
import com.hro.hrogame.gameobject.bullet.WaveBullet;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.effect.EffectType;
import com.hro.hrogame.gameobject.effect.cannoneffect.CannonEffect;
import com.hro.hrogame.gameobject.effect.shieldeffect.AbsorbShieldEffect;
import com.hro.hrogame.gameobject.effect.waveeffect.FreezerEffect;
import com.hro.hrogame.gameobject.effect.waveeffect.HellFireEffect;
import com.hro.hrogame.gameobject.effect.waveeffect.StunnerEffect;
import com.hro.hrogame.gameobject.unit.BaseUnit;
import com.hro.hrogame.gameobject.unit.TankUnit;
import com.hro.hrogame.gameobject.unit.UnitType;
import com.hro.hrogame.sensor.CircleSensor;
import com.hro.hrogame.sensor.RectangleSensor;
import com.hro.hrogame.sensor.SensorType;
import com.hro.hrogame.sensor.UnitSensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hro.hrogame.constants.ParametersConstants.*;
import static com.hro.hrogame.sensor.SensorType.CIRCLE_SENSOR;
import static com.hro.hrogame.sensor.SensorType.RECTANGLE_SENSOR;

/**
 * Created by Lion on 8/14/17.
 */
public class EntityFactory implements EntityManager {

    // TODO: 8/21/2017 Remove the static textures and use skin.getDrawable() instead.
    public static Texture circleTexture = new Texture("circle_range.png");

    // region Instance fields
    private HashMap<PlayerRace, ArrayList<GameObject>> unitMap = new HashMap<>();
    // endregion

    // region C-tor
    public EntityFactory() {
        for (PlayerRace race : PlayerRace.values()) {
            unitMap.put(race, new ArrayList<GameObject>());
        }
    }
    // endregion

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
    public Effect createEffect(GameObject owner, EffectType type) {
        switch (type) {

            case SIMPLE_CANNON:
                CannonEffectData simpleCannonData = new CannonEffectData(SIMPLE_CANNON_EFFECT_COOLDOWN,
                                                                         SIMPLE_CANNON_EFFECT_DAMAGE,
                                                                         SIMPLE_CANNON_EFFECT_TARGET_LIMIT);
                CannonEffect simpleCannonEffect = new CannonEffect(owner, this, simpleCannonData);
                CircleSensor simpleCannonSensor = (CircleSensor) createSensor(owner, CIRCLE_SENSOR);
                if (owner instanceof BaseUnit) simpleCannonSensor.setRadius(BASE_SIMPLE_CANNON_EFFECT_SENSOR_RADIUS);
                else if (owner instanceof TankUnit) simpleCannonSensor.setRadius(TANK_SIMPLE_CANNON_EFFECT_SENSOR_RADIUS);
                simpleCannonEffect.setSensor(simpleCannonSensor);
                return simpleCannonEffect;

            case HARD_CANNON:
                CannonEffectData hardCannonData = new CannonEffectData(HARD_CANNON_EFFECT_COOLDOWN,
                                                                       HARD_CANNON_EFFECT_DAMAGE,
                                                                       HARD_CANNON_EFFECT_TARGET_LIMIT);
                CannonEffect hardCannonEffect = new CannonEffect(owner, this, hardCannonData);
                CircleSensor hardCannonSensor = (CircleSensor) createSensor(owner, CIRCLE_SENSOR);
                if (owner instanceof BaseUnit) hardCannonSensor.setRadius(BASE_HARD_CANNON_EFFECT_SENSOR_RADIUS);
                else if (owner instanceof TankUnit) hardCannonSensor.setRadius(TANK_HARD_CANNON_EFFECT_SENSOR_RADIUS);
                hardCannonEffect.setSensor(hardCannonSensor);
                return hardCannonEffect;

            case HELL_FIRE:
                HellFireEffectData hellFireData = new HellFireEffectData(HELL_FIRE_EFFECT_COOLDOWN,
                                                                         HELL_FIRE_EFFECT_DAMAGE);
                HellFireEffect hellFireEffect = new HellFireEffect(owner, this, hellFireData);
                UnitSensor hellFireSensor = null;
                if (owner instanceof BaseUnit) {
                    hellFireSensor = createSensor(owner, RECTANGLE_SENSOR);
                    hellFireSensor.setSize(BASE_RECTANGLE_SENSOR_WIDTH, BASE_RECTANGLE_SENSOR_HEIGHT);
                }
                else if (owner instanceof TankUnit) {
                    hellFireSensor = createSensor(owner, CIRCLE_SENSOR);
                    ((CircleSensor) hellFireSensor).setRadius(TANK_HELL_FIRE_EFFECT_SENSOR_RADIUS);
                }
                hellFireEffect.setSensor(hellFireSensor);
                return hellFireEffect;

            case FREEZER:
                FreezerEffectData freezerData = new FreezerEffectData(FREEZER_EFFECT_COOLDOWN);
                FreezerEffect freezerEffect = new FreezerEffect(owner, this, freezerData);
                UnitSensor freezerSensor = null;
                if (owner instanceof BaseUnit) {
                    freezerSensor = createSensor(owner, RECTANGLE_SENSOR);
                    freezerSensor.setSize(BASE_RECTANGLE_SENSOR_WIDTH, BASE_RECTANGLE_SENSOR_HEIGHT);
                }
                else if (owner instanceof TankUnit) {
                    freezerSensor = createSensor(owner, CIRCLE_SENSOR);
                    ((CircleSensor) freezerSensor).setRadius(TANK_FREEZER_EFFECT_SENSOR_RADIUS);
                }
                freezerEffect.setSensor(freezerSensor);
                return freezerEffect;

            case STUNNER:
                StunnerEffectData stunnerData = new StunnerEffectData(STUNNER_EFFECT_COOLDOWN);
                StunnerEffect stunnerEffect = new StunnerEffect(owner, this, stunnerData);
                UnitSensor stunnerSensor = null;
                if (owner instanceof BaseUnit) {
                    stunnerSensor = createSensor(owner, RECTANGLE_SENSOR);
                    stunnerSensor.setSize(BASE_RECTANGLE_SENSOR_WIDTH, BASE_RECTANGLE_SENSOR_HEIGHT);
                }
                else if (owner instanceof TankUnit) {
                    stunnerSensor = createSensor(owner, CIRCLE_SENSOR);
                    ((CircleSensor) stunnerSensor).setRadius(TANK_STUNNER_EFFECT_SENSOR_RADIUS);
                }
                stunnerEffect.setSensor(stunnerSensor);
                return stunnerEffect;

            case ABSORB_SHIELD:
                AbsorbShieldEffectData absorbShieldData = new AbsorbShieldEffectData(ABSORB_SHIELD_EFFECT_COOLDOWN);
                AbsorbShieldEffect absorbShieldEffect = new AbsorbShieldEffect(owner, this, absorbShieldData);
                UnitSensor absorbShieldSensor = null;
                if (owner instanceof BaseUnit) {
                    absorbShieldSensor = createSensor(owner, RECTANGLE_SENSOR);
                    absorbShieldSensor.setSize(BASE_RECTANGLE_SENSOR_WIDTH, BASE_RECTANGLE_SENSOR_HEIGHT);
                }
                else if (owner instanceof TankUnit) {
                    absorbShieldSensor = createSensor(owner, CIRCLE_SENSOR);
                    ((CircleSensor) absorbShieldSensor).setRadius(TANK_ABSORB_SHIELD_EFFECT_SENSOR_RADIUS);
                }
                absorbShieldEffect.setSensor(absorbShieldSensor);
                return absorbShieldEffect;
        }
        return null;
    }
    @Override
    public UnitSensor createSensor(GameObject owner, SensorType type) {
        switch (type) {
            case RECTANGLE_SENSOR:
                RectangleSensor rectangleSensor = new RectangleSensor(owner, this);
                rectangleSensor.setUnitFilter(new ClosestUnitFilter(rectangleSensor));
                return rectangleSensor;
            case CIRCLE_SENSOR:
                CircleSensor circleSensor = new CircleSensor(owner, this, circleTexture);
                circleSensor.setUnitFilter(new ClosestUnitFilter(circleSensor));
                return circleSensor;
        }
        return null;
    }
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

package com.hro.hrogame.controller;

import com.badlogic.gdx.graphics.Texture;
import com.hro.hrogame.data.effect.cannoneffectdata.CannonEffectData;
import com.hro.hrogame.data.effect.residualeffectdata.BurnOverTimeEffectData;
import com.hro.hrogame.data.effect.residualeffectdata.FreezeOverTimeEffectData;
import com.hro.hrogame.data.effect.residualeffectdata.ShieldOverTimeEffectData;
import com.hro.hrogame.data.effect.residualeffectdata.StunOverTimeEffectData;
import com.hro.hrogame.data.effect.selfdestructioneffectdata.SelfDestructionEffectData;
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
import com.hro.hrogame.gameobject.effect.cannoneffect.HardCannonEffect;
import com.hro.hrogame.gameobject.effect.cannoneffect.SimpleCannonEffect;
import com.hro.hrogame.gameobject.effect.residualeffect.BurnOverTimeEffect;
import com.hro.hrogame.gameobject.effect.residualeffect.FreezeOverTimeEffect;
import com.hro.hrogame.gameobject.effect.residualeffect.ShieldOverTimeEffect;
import com.hro.hrogame.gameobject.effect.residualeffect.StunOverTimeEffect;
import com.hro.hrogame.gameobject.effect.selfdestruction.SelfDestructionEffect;
import com.hro.hrogame.gameobject.effect.shieldeffect.AbsorbShieldEffect;
import com.hro.hrogame.gameobject.effect.waveeffect.FreezerEffect;
import com.hro.hrogame.gameobject.effect.waveeffect.HellFireEffect;
import com.hro.hrogame.gameobject.effect.waveeffect.StunnerEffect;
import com.hro.hrogame.gameobject.unit.BaseUnit;
import com.hro.hrogame.gameobject.unit.RamUnit;
import com.hro.hrogame.gameobject.unit.TankUnit;
import com.hro.hrogame.gameobject.unit.UnitType;
import com.hro.hrogame.primitives.ProgressiveAttribute;
import com.hro.hrogame.sensor.CircleSensor;
import com.hro.hrogame.sensor.RectangleSensor;
import com.hro.hrogame.sensor.SensorType;
import com.hro.hrogame.sensor.UnitSensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hro.hrogame.sensor.SensorType.CIRCLE_SENSOR;
import static com.hro.hrogame.sensor.SensorType.RECTANGLE_SENSOR;

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
            for (GameObject unit : unitList) {
                if (unit.getStage() != null) list.add(unit);
            }
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
        for (GameObject unit : unitList) {
            if (unit.getStage() != null) list.add(unit);
        }
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
                return createBaseUnit(race, level);
            case TANK:
                return createTankUnit(race, level);
            case RAM:
                return createRamUnit(race, level);
        }
        return null;
    }
    private BaseUnit createBaseUnit(PlayerRace race, int level) {
        ProgressiveAttribute speed = new ProgressiveAttribute(BaseUnit.SPEED, BaseUnit.MAX_SPEED);
        ProgressiveAttribute health = new ProgressiveAttribute(BaseUnit.HEALTH, BaseUnit.MAX_HEALTH);
        GameObjectData data = new GameObjectData(level, speed, health, BaseUnit.TEXTURE_PATH);
        BaseUnit unit = new BaseUnit(data);
        unit.setSize(BaseUnit.WIDTH, BaseUnit.HEIGHT);
        unit.addEffect(createEffect(unit, EffectType.SIMPLE_CANNON));
        unit.setPlayerRace(race);
        unit.addGameObjectAdapter(createEntityFactoryAdapter());
        addUnitToUnitMap(unit);
        return unit;
    }
    private TankUnit createTankUnit(PlayerRace race, int level) {
        ProgressiveAttribute speed = new ProgressiveAttribute(TankUnit.SPEED, TankUnit.MAX_SPEED);
        ProgressiveAttribute health = new ProgressiveAttribute(TankUnit.HEALTH, TankUnit.MAX_HEALTH);
        GameObjectData data = new GameObjectData(level, speed, health, TankUnit.TEXTURE_PATH);
        TankUnit unit = new TankUnit(data);
        unit.setSize(TankUnit.WIDTH, TankUnit.HEIGHT);
        unit.addEffect(createEffect(unit, EffectType.SIMPLE_CANNON));
        unit.setPlayerRace(race);
        unit.addGameObjectAdapter(createEntityFactoryAdapter());
        addUnitToUnitMap(unit);
        return unit;
    }
    private RamUnit createRamUnit(PlayerRace race, int level) {
        ProgressiveAttribute speed = new ProgressiveAttribute(RamUnit.SPEED, RamUnit.MAX_SPEED);
        ProgressiveAttribute health = new ProgressiveAttribute(RamUnit.HEALTH, RamUnit.MAX_HEALTH);
        GameObjectData data = new GameObjectData(level, speed, health, RamUnit.TEXTURE_PATH);
        RamUnit unit = new RamUnit(data);
        unit.setSize(RamUnit.WIDTH, RamUnit.HEIGHT);
        unit.addEffect(createEffect(unit, EffectType.SELF_DESTRUCTION));
        unit.setPlayerRace(race);
        unit.addGameObjectAdapter(createEntityFactoryAdapter());
        addUnitToUnitMap(unit);
        return unit;
    }
    @Override
    public Effect createEffect(GameObject owner, EffectType type) {
        switch (type) {
            case SIMPLE_CANNON:
                return createSimpleCannonEffect(owner);
            case HARD_CANNON:
                return createHardCannonEffect(owner);
            case HELL_FIRE:
                return createHellFireEffect(owner);
            case FREEZER:
                return createFreezerEffect(owner);
            case STUNNER:
                return createStunnerEffect(owner);
            case ABSORB_SHIELD:
                return createAbsorbShieldEffect(owner);
            case SELF_DESTRUCTION:
                return createSelfDestructionEffect(owner);
            case BURN_OVER_TIME:
                return createBurnOverTimeEffect(owner);
            case FREEZE_OVER_TIME:
                return createFreezeOverTimeEffect(owner);
            case STUN_OVER_TIME:
                return createStunOverTimeEffect(owner);
            case SHIELD_OVER_TIME:
                return createShieldOverTimeEffect(owner);
            default:
                throw new RuntimeException("Unknown Effect type passed");
        }
    }
    private SimpleCannonEffect createSimpleCannonEffect(GameObject owner) {
        ProgressiveAttribute cooldown = new ProgressiveAttribute(SimpleCannonEffect.COOLDOWN, SimpleCannonEffect.MIN_COOLDOWN);
        ProgressiveAttribute damage = new ProgressiveAttribute(SimpleCannonEffect.DAMAGE, SimpleCannonEffect.MAX_DAMAGE);
        ProgressiveAttribute targetLimit = new ProgressiveAttribute(SimpleCannonEffect.TARGET_LIMIT, SimpleCannonEffect.MAX_TARGET_LIMIT);
        CannonEffectData data = new CannonEffectData(SimpleCannonEffect.INITIAL_WEIGHT, cooldown, damage, targetLimit);
        SimpleCannonEffect effect = new SimpleCannonEffect(owner, this, data);
        CircleSensor sensor = (CircleSensor) createSensor(owner, CIRCLE_SENSOR);
        if (owner instanceof BaseUnit) sensor.setRadius(SimpleCannonEffect.SENSOR_RADIUS_FOR_BASE);
        else if (owner instanceof TankUnit) sensor.setRadius(SimpleCannonEffect.SENSOR_RADIUS_FOR_TANK);
        effect.setSensor(sensor);
        return effect;
    }
    private HardCannonEffect createHardCannonEffect(GameObject owner) {
        ProgressiveAttribute cooldown = new ProgressiveAttribute(HardCannonEffect.COOLDOWN, HardCannonEffect.MIN_COOLDOWN);
        ProgressiveAttribute damage = new ProgressiveAttribute(HardCannonEffect.DAMAGE, HardCannonEffect.MAX_DAMAGE);
        ProgressiveAttribute targetLimit = new ProgressiveAttribute(HardCannonEffect.TARGET_LIMIT, HardCannonEffect.MAX_TARGET_LIMIT);
        CannonEffectData data = new CannonEffectData(HardCannonEffect.INITIAL_WEIGHT, cooldown, damage, targetLimit);
        HardCannonEffect effect = new HardCannonEffect(owner, this, data);
        CircleSensor sensor = (CircleSensor) createSensor(owner, CIRCLE_SENSOR);
        if (owner instanceof BaseUnit) sensor.setRadius(HardCannonEffect.SENSOR_RADIUS_FOR_BASE);
        else if (owner instanceof TankUnit) sensor.setRadius(HardCannonEffect.SENSOR_RADIUS_FOR_TANK);
        effect.setSensor(sensor);
        return effect;
    }
    private HellFireEffect createHellFireEffect(GameObject owner) {
        ProgressiveAttribute cooldown = new ProgressiveAttribute(HellFireEffect.COOLDOWN, HellFireEffect.MIN_COOLDOWN);
        ProgressiveAttribute damage = new ProgressiveAttribute(HellFireEffect.DAMAGE, HellFireEffect.MAX_DAMAGE);
        HellFireEffectData data = new HellFireEffectData(HellFireEffect.INITIAL_WEIGHT, cooldown, damage);
        HellFireEffect effect = new HellFireEffect(owner, this, data);
        UnitSensor sensor;
        if (owner instanceof BaseUnit) {
            sensor = createSensor(owner, RECTANGLE_SENSOR);
            sensor.setSize(BaseUnit.RECTANGLE_SENSOR_WIDTH, BaseUnit.RECTANGLE_SENSOR_HEIGHT);
        } else {
            sensor = createSensor(owner, CIRCLE_SENSOR);
            ((CircleSensor) sensor).setRadius(HellFireEffect.SENSOR_RADIUS_FOR_TANK);
        }
        effect.setSensor(sensor);
        return effect;
    }
    private FreezerEffect createFreezerEffect(GameObject owner) {
        ProgressiveAttribute cooldown = new ProgressiveAttribute(FreezerEffect.COOLDOWN, FreezerEffect.MIN_COOLDOWN);
        FreezerEffectData data = new FreezerEffectData(FreezerEffect.INITIAL_WEIGHT, cooldown);
        FreezerEffect effect = new FreezerEffect(owner, this, data);
        UnitSensor sensor;
        if (owner instanceof BaseUnit) {
            sensor = createSensor(owner, RECTANGLE_SENSOR);
            sensor.setSize(BaseUnit.RECTANGLE_SENSOR_WIDTH, BaseUnit.RECTANGLE_SENSOR_HEIGHT);
        } else {
            sensor = createSensor(owner, CIRCLE_SENSOR);
            ((CircleSensor) sensor).setRadius(FreezerEffect.SENSOR_RADIUS_FOR_TANK);
        }
        effect.setSensor(sensor);
        return effect;
    }
    private StunnerEffect createStunnerEffect(GameObject owner) {
        ProgressiveAttribute cooldown = new ProgressiveAttribute(StunnerEffect.COOLDOWN, StunnerEffect.MIN_COOLDOWN);
        StunnerEffectData data = new StunnerEffectData(StunnerEffect.INITIAL_WEIGHT, cooldown);
        StunnerEffect effect = new StunnerEffect(owner, this, data);
        UnitSensor sensor;
        if (owner instanceof BaseUnit) {
            sensor = createSensor(owner, RECTANGLE_SENSOR);
            sensor.setSize(BaseUnit.RECTANGLE_SENSOR_WIDTH, BaseUnit.RECTANGLE_SENSOR_HEIGHT);
        } else {
            sensor = createSensor(owner, CIRCLE_SENSOR);
            ((CircleSensor) sensor).setRadius(StunnerEffect.SENSOR_RADIUS_FOR_TANK);
        }
        effect.setSensor(sensor);
        return effect;
    }
    private AbsorbShieldEffect createAbsorbShieldEffect(GameObject owner) {
        ProgressiveAttribute cooldown = new ProgressiveAttribute(AbsorbShieldEffect.COOLDOWN, AbsorbShieldEffect.MIN_COOLDOWN);
        AbsorbShieldEffectData data = new AbsorbShieldEffectData(AbsorbShieldEffect.INITIAL_WEIGHT, cooldown);
        AbsorbShieldEffect effect = new AbsorbShieldEffect(owner, this, data);
        UnitSensor sensor;
        if (owner instanceof BaseUnit) {
            sensor = createSensor(owner, RECTANGLE_SENSOR);
            sensor.setSize(BaseUnit.RECTANGLE_SENSOR_WIDTH, BaseUnit.RECTANGLE_SENSOR_HEIGHT);
        } else {
            sensor = createSensor(owner, CIRCLE_SENSOR);
            ((CircleSensor) sensor).setRadius(AbsorbShieldEffect.SENSOR_RADIUS_FOR_TANK);
        }
        effect.setSensor(sensor);
        return effect;
    }
    private SelfDestructionEffect createSelfDestructionEffect(GameObject owner) {
        ProgressiveAttribute damage = new ProgressiveAttribute(SelfDestructionEffect.DAMAGE, SelfDestructionEffect.MAX_DAMAGE);
        SelfDestructionEffectData data = new SelfDestructionEffectData(SelfDestructionEffect.INITIAL_WEIGHT, damage);
        SelfDestructionEffect effect = new SelfDestructionEffect(owner, this, data);
        UnitSensor sensor = createSensor(owner, CIRCLE_SENSOR);
        ((CircleSensor) sensor).setRadius(SelfDestructionEffect.SENSOR_RADIUS_FOR_RAM);
        effect.setSensor(sensor);
        return effect;
    }
    private BurnOverTimeEffect createBurnOverTimeEffect(GameObject owner) {
        ProgressiveAttribute cooldown = new ProgressiveAttribute(BurnOverTimeEffect.COOLDOWN,
                                                                 BurnOverTimeEffect.MIN_COOLDOWN);
        ProgressiveAttribute damage = new ProgressiveAttribute(BurnOverTimeEffect.DAMAGE,
                                                               BurnOverTimeEffect.MAX_DAMAGE);
        ProgressiveAttribute totalDamage = new ProgressiveAttribute(BurnOverTimeEffect.TOTAL_DAMAGE, BurnOverTimeEffect.MAX_TOTAL_DAMAGE);
        BurnOverTimeEffectData data = new BurnOverTimeEffectData(cooldown, damage, totalDamage);
        return new BurnOverTimeEffect(owner, this, data);
    }
    private FreezeOverTimeEffect createFreezeOverTimeEffect(GameObject owner) {
        ProgressiveAttribute duration = new ProgressiveAttribute(FreezeOverTimeEffect.DURATION,
                                                                 FreezeOverTimeEffect.MAX_DURATION);
        ProgressiveAttribute speedRatio = new ProgressiveAttribute(FreezeOverTimeEffect.SPEED_RATIO,
                                                                   FreezeOverTimeEffect.MIN_SPEED_RATIO);
        FreezeOverTimeEffectData data = new FreezeOverTimeEffectData(duration, speedRatio);
        return new FreezeOverTimeEffect(owner, this, data);
    }
    private StunOverTimeEffect createStunOverTimeEffect(GameObject owner) {
        ProgressiveAttribute duration = new ProgressiveAttribute(StunOverTimeEffect.DURATION, StunOverTimeEffect.MAX_DURATION);
        StunOverTimeEffectData data = new StunOverTimeEffectData(duration);
        return new StunOverTimeEffect(owner, this, data);
    }
    private ShieldOverTimeEffect createShieldOverTimeEffect(GameObject owner) {
        ProgressiveAttribute duration = new ProgressiveAttribute(ShieldOverTimeEffect.DURATION, ShieldOverTimeEffect.MAX_DURATION);
        ShieldOverTimeEffectData data = new ShieldOverTimeEffectData(duration);
        return new ShieldOverTimeEffect(owner, this, data);
    }
    @Override
    public UnitSensor createSensor(GameObject owner, SensorType type) {
        switch (type) {
            case RECTANGLE_SENSOR:
                RectangleSensor rectangleSensor = new RectangleSensor(owner, this);
                rectangleSensor.setUnitFilter(new ClosestUnitFilter(rectangleSensor));
                return rectangleSensor;
            case CIRCLE_SENSOR:
                CircleSensor circleSensor = new CircleSensor(owner, this, null);
                circleSensor.setUnitFilter(new ClosestUnitFilter(circleSensor));
                if (owner instanceof TargetBullet) {
                    circleSensor.setRadius(((TargetBullet)owner).getBulletData().splashAreaRadius);
                }
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

    private GameObjectAdapter createEntityFactoryAdapter() {
        return new GameObjectAdapter() {
            @Override
            public void onPlayerTypeChange(GameObject gameObject, PlayerRace oldPlayerType) {
                unitMap.get(oldPlayerType).remove(gameObject);
                addUnitToUnitMap(gameObject);
            }
            @Override
            public void onDie(GameObject dyingUnit, GameObject killerUnit) {
                removeUnit(dyingUnit);
            }
        };
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
        if (bullet instanceof WaveBullet) ((WaveBullet) bullet).getTimer().clear();
        return bullet.remove();
    }
    // endregion

    // region Add
    private void addUnitToUnitMap(GameObject unit) {
        unitMap.get(unit.getPlayerType()).add(unit);
    }
    // endregion
}

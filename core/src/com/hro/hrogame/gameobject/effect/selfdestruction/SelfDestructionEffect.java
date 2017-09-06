package com.hro.hrogame.gameobject.effect.selfdestruction;

import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.effect.selfdestructioneffectdata.SelfDestructionEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.utils.Util;

import java.util.List;

public class SelfDestructionEffect extends Effect {

    // region Static fields
    public static final float SENSOR_RADIUS_FOR_RAM = 40;
    public static final int INITIAL_WEIGHT = 5;
    public static final float DAMAGE = 50;
    public static final float MAX_DAMAGE = 100;
    // endregion

    // region Instance fields
    private SelfDestructionEffectData data;
    // endregion

    // region C-tor
    public SelfDestructionEffect(GameObject owner, EntityManager entityManager, SelfDestructionEffectData data) {
        super(owner, entityManager);
        this.data = data;
        levelUpEffect(owner.getLevel());
        makeAutoExecutable();
    }
    // endregion

    // region Execution
    @Override
    protected boolean isExecutable() {
        return sensor.obtainEnemies().size() > 0;
    }
    @Override
    protected void execute() {
        List<GameObject> enemies = sensor.obtainEnemies();
        for (GameObject enemy : enemies) enemy.takeDamage(this, data.damage.current);
        owner.selfDestruct();
    }
    // endregion

    // region Level Up
    @Override
    public void levelUpEffect(int level) {
        data.weight = Util.calcProgressAndDefineWeight(INITIAL_WEIGHT, level, ParametersConstants.PROGRESS_RATIO,
                true, data.damage);
    }
    // endregion

    // region Getter
    @Override
    protected float getCoolDown() {
        return 0;
    }
    @Override
    public float getEffectWeight() {
        return data.weight;
    }
    // endregion
}

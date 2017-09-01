package com.hro.hrogame.gameobject.effect.shieldeffect;

import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.effect.shieldeffectdata.AbsorbShieldEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.effect.EffectType;
import com.hro.hrogame.gameobject.effect.residualeffect.ShieldOverTimeEffect;

public class AbsorbShieldEffect extends Effect {

    // region Static fields
    public static final int WEIGHT = 10;
    public static final float COOLDOWN = 10;
    public static final float MIN_COOLDOWN = 1;
    public static final int SENSOR_RADIUS_FOR_TANK = 140;
    // endregion

    // region Instance fields
    private AbsorbShieldEffectData data;
    // endregion

    // region C-tor
    public AbsorbShieldEffect(GameObject owner, EntityManager entityManager, AbsorbShieldEffectData data) {
        super(owner, entityManager);
        this.data = data;
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
        System.out.println("Absorb shield acquired on " + owner.getPlayerType());
        acquireShield(owner);
    }
    // endregion

    // region Acquire
    private void acquireShield(GameObject target) {
        ShieldOverTimeEffect effect = target.isEffectAcquired(ShieldOverTimeEffect.class);
        if (effect == null) {
            effect = (ShieldOverTimeEffect) entityManager.createEffect(target, EffectType.SHIELD_OVER_TIME);
            effect.setLevel(owner.getLevel());
            target.addEffect(effect);
        }
        else effect.reNew(owner.getLevel());
    }
    // endregion

    // region Level Up
    @Override
    public void levelUp(boolean showParticle) {
        if (isMaxLevel) return;
        data.cooldown -= data.cooldown * ParametersConstants.WEIGHT_PROGRESS;
        if (data.cooldown < MIN_COOLDOWN) {
            data.cooldown = MIN_COOLDOWN;
            isMaxLevel = true;
            return;
        }
        data.weight += data.weight * ParametersConstants.WEIGHT_PROGRESS;
    }
    // endregion

    // region Getter
    @Override
    public boolean isPositionValidForEffect() {
        return true;
    }
    @Override
    protected float getCoolDown() {
        return data.cooldown;
    }

    @Override
    public int getWeight() {
        return data.weight;
    }
    // endregion
}

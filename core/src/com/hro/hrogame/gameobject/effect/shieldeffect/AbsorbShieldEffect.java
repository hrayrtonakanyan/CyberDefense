package com.hro.hrogame.gameobject.effect.shieldeffect;

import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.data.effect.shieldeffectdata.AbsorbShieldEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.effect.EffectType;
import com.hro.hrogame.gameobject.effect.residualeffect.ShieldOverTimeEffect;

public class AbsorbShieldEffect extends Effect {

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

    // region Create
    private void acquireShield(GameObject target) {
        ShieldOverTimeEffect effect = target.isEffectAcquired(ShieldOverTimeEffect.class);
        if (effect == null) target.addEffect(entityManager.createEffect(target, EffectType.SHIELD_OVER_TIME));
        else effect.reNew();
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
    // endregion
}

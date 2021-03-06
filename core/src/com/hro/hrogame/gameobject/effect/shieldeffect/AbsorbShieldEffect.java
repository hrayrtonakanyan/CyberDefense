package com.hro.hrogame.gameobject.effect.shieldeffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.controller.EntityManager;
import com.hro.hrogame.controller.SoundController;
import com.hro.hrogame.data.effect.shieldeffectdata.AbsorbShieldEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.effect.EffectType;
import com.hro.hrogame.gameobject.effect.residualeffect.ShieldOverTimeEffect;
import com.hro.hrogame.utils.Util;

public class AbsorbShieldEffect extends Effect {

    // region Static fields
    public static final int INITIAL_WEIGHT = 10;
    public static final float COOLDOWN = 40;
    public static final float MIN_COOLDOWN = 15;
    public static final int SENSOR_RADIUS_FOR_TANK = Gdx.graphics.getWidth() / 5;
    // endregion

    // region Instance fields
    private AbsorbShieldEffectData data;
    // endregion

    // region C-tor
    public AbsorbShieldEffect(Skin skin, GameObject owner, EntityManager entityManager, SoundController soundController, AbsorbShieldEffectData data) {
        super(skin, owner, entityManager, soundController);
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
        acquireShield(owner);
    }
    // endregion

    // region Acquire
    private void acquireShield(GameObject target) {
        ShieldOverTimeEffect effect = target.isEffectAcquired(ShieldOverTimeEffect.class);
        if (effect == null) {
            effect = (ShieldOverTimeEffect) entityManager.createEffect(target, EffectType.SHIELD_OVER_TIME);
            effect.levelUpEffect(owner.getLevel());
            target.addEffect(effect);
        }
        else effect.reNew(owner.getLevel());
    }
    // endregion

    // region Level Up
    @Override
    public void levelUpEffect(int level) {
        data.weight = Util.calcProgressAndDefineWeight(INITIAL_WEIGHT, level, ParametersConstants.PROGRESS_RATIO,
                true, data.cooldown);
    }
    // endregion

    // region Getter
    @Override
    public boolean isPositionValidForEffect() {
        return true;
    }
    @Override
    protected float getCoolDown() {
        return data.cooldown.current;
    }

    @Override
    public float getEffectWeight() {
        return data.weight;
    }
    // endregion
}

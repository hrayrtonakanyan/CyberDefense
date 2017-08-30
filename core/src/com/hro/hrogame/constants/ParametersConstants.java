package com.hro.hrogame.constants;

import com.badlogic.gdx.Gdx;

public class ParametersConstants {

    // region TargetBullet data
    public static final float TARGET_BULLET_WIDTH = 60.25f;
    public static final float TARGET_BULLET_HEIGHT = 20.12f;
    // endregion

    // region Base effects sensor size
    public static final float BASE_RECTANGLE_SENSOR_WIDTH = Gdx.graphics.getWidth();
    public static final float BASE_RECTANGLE_SENSOR_HEIGHT = Gdx.graphics.getHeight();
    // endregion

    // region SimpleCannonEffectData
    public static final float SIMPLE_CANNON_EFFECT_COOLDOWN = 5;
    public static final float SIMPLE_CANNON_EFFECT_DAMAGE = 25;
    public static final int SIMPLE_CANNON_EFFECT_TARGET_LIMIT = 1;
    public static final int BASE_SIMPLE_CANNON_EFFECT_SENSOR_RADIUS = 100;
    public static final int TANK_SIMPLE_CANNON_EFFECT_SENSOR_RADIUS = 100;
    // endregion

    // region HardCannonEffectData
    public static final float HARD_CANNON_EFFECT_COOLDOWN = 5;
    public static final float HARD_CANNON_EFFECT_DAMAGE = 25;
    public static final int HARD_CANNON_EFFECT_TARGET_LIMIT = 1;
    public static final int BASE_HARD_CANNON_EFFECT_SENSOR_RADIUS = 1;
    public static final int TANK_HARD_CANNON_EFFECT_SENSOR_RADIUS = 1;
    // endregion

    // region HellFireEffectData
    public static final float HELL_FIRE_EFFECT_COOLDOWN = 10;
    public static final float HELL_FIRE_EFFECT_DAMAGE = 50;
    public static final int TANK_HELL_FIRE_EFFECT_SENSOR_RADIUS = 100;
    // endregion

    // region FreezerEffectData
    public static final float FREEZER_EFFECT_COOLDOWN = 10;
    public static final int TANK_FREEZER_EFFECT_SENSOR_RADIUS = 100;
    // endregion

    // region StunnerEffectData
    public static final float STUNNER_EFFECT_COOLDOWN = 10;
    public static final int TANK_STUNNER_EFFECT_SENSOR_RADIUS = 100;
    // endregion

    // region AbsorbShieldEffectData
    public static final float ABSORB_SHIELD_EFFECT_COOLDOWN = 10;
    public static final int TANK_ABSORB_SHIELD_EFFECT_SENSOR_RADIUS = 100;
    // endregion

    // region BurnOverTimeEffect data
    public static final float BURN_OVER_TIME_EFFECT_COOLDOWN = 3;
    public static final float BURN_OVER_TIME_EFFECT_DAMAGE = 20;
    public static final float BURN_OVER_TIME_EFFECT_MAX_DAMAGE_AMOUNT = 60;
    // endregion

    // region FreezeOverTimeEffect data
    public static final float FREEZE_OVER_TIME_EFFECT_DURATION = 5;
    public static final float FREEZE_OVER_TIME_EFFECT_SPEED_RATIO = 0.5f;
    // endregion

    // region StunOverTimeEffect data
    public static final float STUN_OVER_TIME_EFFECT_DURATION = 5;
    // endregion

    // region ShieldOverTimeEffect data
    public static final float SHIELD_OVER_TIME_EFFECT_DURATION = 20;
    // endregion
}

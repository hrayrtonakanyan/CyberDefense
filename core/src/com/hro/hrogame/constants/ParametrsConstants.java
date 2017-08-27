package com.hro.hrogame.constants;

import com.badlogic.gdx.Gdx;

public class ParametrsConstants {

    public static final float BASE_SIMPLE_CANNON_SENSOR_WIDTH = Gdx.graphics.getWidth();
    public static final float BASE_SIMPLE_CANNON_SENSOR_HEIGHT = Gdx.graphics.getHeight();

    public static final float TANK_SIMPLE_CANNON_SENSOR_RADIUS = 120;

    public static final float BULLET_SENSOR_RADIUS = 100;

    public static final float SIMPLE_CANNON_EFFECT_COOLDOWN = 1;

    // region FreezerEffect parameters

    // endregion

    // region TargetBullet params
    public static final float TARGET_BULLET_WIDTH = 60.25f;
    public static final float TARGET_BULLET_HEIGHT = 20.12f;
    // endregion

    // region StunOverTimeEffect params
    public static final float STUN_OVER_TIME_EFFECT_DURATION = 5;
    // endregion

    // region FreezeOverTimeEffect parameters
    public static final float FREEZE_OVER_TIME_EFFECT_DURATION = 5;
    public static final float FREEZE_OVER_TIME_EFFECT_SPEED_RATIO = 0.5f;
    // endregion

    // region BurnOverTimeEffect parameters
    public static final float BURN_OVER_TIME_EFFECT_COOLDOWN = 3;
    public static final float BURN_OVER_TIME_EFFECT_DAMAGE = 20;
    public static final float BURN_OVER_TIME_EFFECT_MAX_DAMGE_AMOUNT = 60;
    // endregion
}

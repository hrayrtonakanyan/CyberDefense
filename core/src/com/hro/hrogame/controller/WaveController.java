package com.hro.hrogame.controller;

import com.hro.hrogame.constants.ParametersConstants;

public class WaveController {

    // region Static fields
    public static final float INITIAL_WEIGHT = 50;
    public static final float TANK_UNITS_CREATION_RATIO = 0.8f;
    public static final float RAM_UNITS_CREATION_RATIO = 0.2f;
    public static final int ENEMY_UNITS_LEVEL_UP_FREQUENCY_PER_WAVE = 2;
    // endregion

    // region Instance fields
    private int waveNumber = 0;
    private float weight;
    private float gameProgressRatio;
    // endregion

    // region C-tor
    public WaveController(float weight, float gameProgressRatio) {
        this.weight = weight;
        this.gameProgressRatio = gameProgressRatio;
    }
    // endregion

    // region Calculation
    public float calculateWaveWeight() {
        waveNumber++;
        if (waveNumber == 1) return WaveController.INITIAL_WEIGHT;
        weight += weight * gameProgressRatio * ParametersConstants.PROGRESS_RATIO;
        return weight;
    }
    // endregion

    // region Getter
    public int getWaveNumber() {
        return waveNumber;
    }
    // endregion
}

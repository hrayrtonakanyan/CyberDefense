package com.hro.hrogame.controller;

public class WaveController {

    // region Static fields
    public static final int WAVE_NOMINAL_WEIGHT = 100;
    public static final float GAME_PROGRESS_RATIO = 1;
    // endregion

    // region Instance fields
    private int waveNumber = 1;
    private int waveNominalWeight;
    private float gameProgressRatio;
    // endregion

    // region C-tor
    public WaveController(int waveNominalWeight, float gameProgressRatio) {
        this.waveNominalWeight = waveNominalWeight;
        this.gameProgressRatio = gameProgressRatio;
    }
    // endregion

    // region Calculation
    public float calculateWaveWeight() {
        float waveWeight = waveNominalWeight * waveNumber * gameProgressRatio;
        waveNumber++;
        return waveWeight;
    }
    // endregion
}

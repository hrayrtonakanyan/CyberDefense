package com.hro.hrogame.data.effect.residualeffectdata;

import com.hro.hrogame.primitives.ProgressiveAttribute;

public class FreezeOverTimeEffectData {

    public ProgressiveAttribute duration;
    public ProgressiveAttribute speedRatio;

    public FreezeOverTimeEffectData(ProgressiveAttribute duration, ProgressiveAttribute speedRatio) {
        this.duration = duration;
        this.speedRatio = speedRatio;
    }
}

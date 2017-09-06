package com.hro.hrogame.data.effect.waveeffectdata;

import com.hro.hrogame.primitives.ProgressiveAttribute;

public class StunnerEffectData {

    public float weight;
    public ProgressiveAttribute cooldown;

    public StunnerEffectData(int weight, ProgressiveAttribute cooldown) {
        this.weight = weight;
        this.cooldown = cooldown;
    }
}

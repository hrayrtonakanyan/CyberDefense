package com.hro.hrogame.data.effect.waveeffectdata;

import com.hro.hrogame.primitives.ProgressiveAttribute;

public class FreezerEffectData {

    public int weight;
    public ProgressiveAttribute cooldown;

    public FreezerEffectData(int weight, ProgressiveAttribute cooldown) {
        this.weight = weight;
        this.cooldown = cooldown;
    }
}

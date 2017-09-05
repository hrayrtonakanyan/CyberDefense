package com.hro.hrogame.data.effect.waveeffectdata;

import com.hro.hrogame.primitives.ProgressiveAttribute;

public class HellFireEffectData {

    public int weight;
    public ProgressiveAttribute cooldown;
    public ProgressiveAttribute damage;

    public HellFireEffectData(int weight, ProgressiveAttribute cooldown, ProgressiveAttribute damage) {
        this.weight = weight;
        this.cooldown = cooldown;
        this.damage = damage;
    }
}

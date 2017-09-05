package com.hro.hrogame.data.effect.shieldeffectdata;

import com.hro.hrogame.primitives.ProgressiveAttribute;

public class AbsorbShieldEffectData {

    public int weight;
    public ProgressiveAttribute cooldown;

    public AbsorbShieldEffectData(int weight, ProgressiveAttribute cooldown) {
        this.weight = weight;
        this.cooldown = cooldown;
    }
}

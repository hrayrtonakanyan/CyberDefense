package com.hro.hrogame.data.effect.selfdestructioneffectdata;

import com.hro.hrogame.primitives.ProgressiveAttribute;

public class SelfDestructionEffectData {

    public int weight;
    public ProgressiveAttribute damage;

    public SelfDestructionEffectData(int weight, ProgressiveAttribute damage) {
        this.weight = weight;
        this.damage = damage;
    }
}

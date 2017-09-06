package com.hro.hrogame.data.effect.cannoneffectdata;

import com.hro.hrogame.primitives.ProgressiveAttribute;

public class CannonEffectData {

    public float weight;
    public ProgressiveAttribute cooldown;
    public ProgressiveAttribute damage;
    public ProgressiveAttribute targetLimit;

    public CannonEffectData(int weight, ProgressiveAttribute cooldown, ProgressiveAttribute damage, ProgressiveAttribute targetLimit) {
        this.weight = weight;
        this.cooldown = cooldown;
        this.damage = damage;
        this.targetLimit = targetLimit;
    }
}

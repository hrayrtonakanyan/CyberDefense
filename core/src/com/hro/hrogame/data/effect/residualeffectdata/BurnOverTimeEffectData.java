package com.hro.hrogame.data.effect.residualeffectdata;

import com.hro.hrogame.primitives.ProgressiveAttribute;

public class BurnOverTimeEffectData {

    public ProgressiveAttribute cooldown;
    public ProgressiveAttribute maxDamageAmount;
    public ProgressiveAttribute damage;

    public BurnOverTimeEffectData(ProgressiveAttribute cooldown, ProgressiveAttribute damage, ProgressiveAttribute maxDamageAmount) {
        this.cooldown = cooldown;
        this.damage = damage;
        this.maxDamageAmount = maxDamageAmount;
    }
}

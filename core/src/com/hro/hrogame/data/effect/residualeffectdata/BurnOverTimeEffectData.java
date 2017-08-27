package com.hro.hrogame.data.effect.residualeffectdata;

public class BurnOverTimeEffectData {

    public final float cooldown;
    public final float maxDamageAmount;
    public final float damage;

    public BurnOverTimeEffectData(float cooldown, float damge, float maxDamageAmount) {
        this.cooldown = cooldown;
        this.damage = damge;
        this.maxDamageAmount = maxDamageAmount;
    }
}

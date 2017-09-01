package com.hro.hrogame.data.effect.residualeffectdata;

public class BurnOverTimeEffectData {

    public float cooldown;
    public float maxDamageAmount;
    public float damage;

    public BurnOverTimeEffectData(float cooldown, float damge, float maxDamageAmount) {
        this.cooldown = cooldown;
        this.damage = damge;
        this.maxDamageAmount = maxDamageAmount;
    }
}

package com.hro.hrogame.data.effect.cannoneffectdata;

public class CannonEffectData {

    public float level;
    public int weight;
    public float cooldown;
    public float damage;
    public int targetLimit;

    public CannonEffectData(int weight, float cooldown, float damage, int targetLimit) {
        this.weight = weight;
        this.cooldown = cooldown;
        this.damage = damage;
        this.targetLimit = targetLimit;
    }
}

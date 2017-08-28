package com.hro.hrogame.data.effect.cannoneffectdata;

/**
 * Created by Lion on 8/15/17.
 */
public class CannonEffectData {


    public float level;  // TODO: 8/16/17 Remove the mocked data.
    public float cooldown;
    public float damage;
    public int targetLimit;

    public CannonEffectData(float level, float cooldown, float damage, int targetLimit) {
        this.level = level;
        this.cooldown = cooldown;
        this.damage = damage;
        this.targetLimit = targetLimit;
    }
}

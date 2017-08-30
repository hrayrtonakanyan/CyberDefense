package com.hro.hrogame.data.effect.cannoneffectdata;

/**
 * Created by Lion on 8/15/17.
 */
public class CannonEffectData {


    public float level = 1;  // TODO: 8/16/17 Remove the mocked data.
    public float cooldown;
    public float damage;
    public int targetLimit;

    public CannonEffectData(float cooldown, float damage, int targetLimit) {
        this.cooldown = cooldown;
        this.damage = damage;
        this.targetLimit = targetLimit;
    }
}

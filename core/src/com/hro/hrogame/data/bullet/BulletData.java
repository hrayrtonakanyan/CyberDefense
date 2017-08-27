package com.hro.hrogame.data.bullet;

/**
 * Created by Lion on 8/17/17.
 */
public class BulletData {

    public int hitUnitLimit;
    public float speed;
    public float splashAreaRadius;


    public BulletData(int hitUnitLimit, float speed, float splashAreaRadius) {
        this.hitUnitLimit = hitUnitLimit;
        this.speed = speed;
        this.splashAreaRadius = splashAreaRadius;
    }
}

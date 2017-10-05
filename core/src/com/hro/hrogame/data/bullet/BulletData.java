package com.hro.hrogame.data.bullet;

public class BulletData {

    public String drawableName;
    public int hitUnitLimit;
    public float speed;
    public float splashAreaRadius;

    public BulletData(String drawableName, int hitUnitLimit, float speed, float splashAreaRadius) {
        this.drawableName = drawableName;
        this.hitUnitLimit = hitUnitLimit;
        this.speed = speed;
        this.splashAreaRadius = splashAreaRadius;
    }

    public BulletData(float speed) {
        this.speed = speed;
    }
}

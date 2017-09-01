package com.hro.hrogame.data.bullet;

public class BulletData {

    public String texturePath;
    public int hitUnitLimit;
    public float speed;
    public float splashAreaRadius;

    public BulletData(String texturePath, int hitUnitLimit, float speed, float splashAreaRadius) {
        this.texturePath = texturePath;
        this.hitUnitLimit = hitUnitLimit;
        this.speed = speed;
        this.splashAreaRadius = splashAreaRadius;
    }
}

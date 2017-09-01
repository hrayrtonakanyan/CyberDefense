package com.hro.hrogame.data.gameobject;

/**
 * Created by Lion on 8/14/17.
 */
public class GameObjectData {

    public int level = 1;
    public float speed;
    public int health = -1;
    public String texturePath;

    public GameObjectData() {
    }

    public GameObjectData(int level, float speed, int health, String texturePath) {
        this.level = level;
        this.speed = speed;
        this.health = health;
        this.texturePath = texturePath;
    }
}

package com.hro.hrogame.data.gameobject;

import com.hro.hrogame.primitives.ProgressiveAttribute;

public class GameObjectData {

    public int level = 1;
    public ProgressiveAttribute speed;
    public ProgressiveAttribute health;
    public String texturePath;

    public GameObjectData() {
    }

    public GameObjectData(int level, ProgressiveAttribute speed, ProgressiveAttribute health, String texturePath) {
        this.level = level;
        this.speed = speed;
        this.health = health;
        this.texturePath = texturePath;
    }
}

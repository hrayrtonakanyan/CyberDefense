package com.hro.hrogame.gameobject.unit;

import com.badlogic.gdx.Gdx;
import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.gameobject.GameObject;

public class TankUnit extends GameObject {

    // region Static fields
    public static final String TEXTURE_PATH = "tank.png";
    public static final int WIDTH = Gdx.graphics.getWidth() / 10;
    public static final int HEIGHT = Gdx.graphics.getHeight() / 11;

    public static final float SPEED = Gdx.graphics.getWidth() / 16;
    public static final float MAX_SPEED = SPEED * 2;
    public static final int HEALTH = 40;
    public static final int MAX_HEALTH = 800;
    // endregion

    // region C-tor
    public TankUnit(GameObjectData data) {
        super(data);
    }
    // endregion

    // region Getter
    @Override
    public GameObject revealOwningUnit() {
        return this;
    }
    // endregion
}

package com.hro.hrogame.gameobject.unit;

import com.badlogic.gdx.Gdx;
import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.gameobject.GameObject;

public class BaseUnit extends GameObject {

    // region Static fields
    public static final String TEXTURE_PATH = "base.png";
    public static final int WIDTH = Gdx.graphics.getHeight() / 7;
    public static final int HEIGHT = Gdx.graphics.getHeight() / 7;

    public static final float SPEED = 0;
    public static final float MAX_SPEED = 0;
    public static final int HEALTH = 1200;
    public static final int MAX_HEALTH = 1600;
    public static final float RECTANGLE_SENSOR_WIDTH = Gdx.graphics.getWidth();
    public static final float RECTANGLE_SENSOR_HEIGHT = Gdx.graphics.getHeight();
    // endregion

    // region C-tor
    public BaseUnit(GameObjectData data) {
        super(data);
        setHealthBarLength(100);
    }
    // endregion

    // region Getter
    @Override
    public GameObject revealOwningUnit() {
        return this;
    }
    // endregion
}

package com.hro.hrogame.gameobject.unit;

import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.gameobject.GameObject;

public class BaseUnit extends GameObject {

    // region Static fields
    public static final float SPEED = 0;
    public static final float MAX_SPEED = 0;
    public static final int HEALTH = 1500;
    public static final int MAX_HEALTH = 4500;
    public static final int WIDTH = 40;
    public static final int HEIGHT = 40;
    public static final String TEXTURE_PATH = "base.png";
    // endregion

    // region C-tor
    public BaseUnit(GameObjectData data) {
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

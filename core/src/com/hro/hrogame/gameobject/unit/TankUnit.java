package com.hro.hrogame.gameobject.unit;

import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.gameobject.GameObject;

public class TankUnit extends GameObject {

    // region Static fields
    public static final float SPEED = 50;
    public static final float MAX_SPEED = 100;
    public static final int HEALTH = 100;
    public static final int MAX_HEALTH = 500;
    public static final int WIDTH = 55;
    public static final int HEIGHT = 40;
    public static final String TEXTURE_PATH = "tank.png";
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

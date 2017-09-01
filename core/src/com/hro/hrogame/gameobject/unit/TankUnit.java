package com.hro.hrogame.gameobject.unit;

import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.gameobject.GameObject;

public class TankUnit extends GameObject {

    // region Static fields
    public static final float SPEED = 50;
    public static final int HEALTH = 400;
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

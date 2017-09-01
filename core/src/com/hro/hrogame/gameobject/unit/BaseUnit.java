package com.hro.hrogame.gameobject.unit;

import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.gameobject.GameObject;

public class BaseUnit extends GameObject {

    // region Static fields
    public static final float SPEED = 0;
    public static final int HEALTH = 600;
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

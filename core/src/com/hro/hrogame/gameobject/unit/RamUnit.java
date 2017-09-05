package com.hro.hrogame.gameobject.unit;

import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.gameobject.GameObject;

public class RamUnit extends GameObject{

    // region Static fields
    public static final float SPEED = 80;
    public static final float MAX_SPEED = 360;
    public static final int HEALTH = 200;
    public static final int MAX_HEALTH = 600;
    public static final String TEXTURE_PATH = "ram.png";
    // endregion

    // region C-tor
    public RamUnit(GameObjectData data) {
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

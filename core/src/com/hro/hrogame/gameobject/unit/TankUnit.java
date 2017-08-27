package com.hro.hrogame.gameobject.unit;

import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.gameobject.GameObject;

/**
 * Created by Lion on 8/14/17.
 */
public class TankUnit extends GameObject {

    public TankUnit(GameObjectData data) {
        super(data);
    }

    @Override
    public GameObject revealOwningUnit() {
        return this;
    }
}

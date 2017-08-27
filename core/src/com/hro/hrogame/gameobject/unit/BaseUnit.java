package com.hro.hrogame.gameobject.unit;

import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.gameobject.GameObject;

/**
 * Created by Lion on 8/14/17.
 */
public class BaseUnit extends GameObject {

    public BaseUnit(GameObjectData data) {
        super(data);
    }

    @Override
    public GameObject revealOwningUnit() {
        return this;
    }
}

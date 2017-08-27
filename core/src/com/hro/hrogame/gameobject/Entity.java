package com.hro.hrogame.gameobject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by Lion on 8/14/17.
 */
public abstract class Entity extends Group {


    public static Vector2 coordinateConvertVector = new Vector2(0, 0);


    public Entity() {
        setTransform(false);
    }

    public abstract GameObject revealOwningUnit();
    public PlayerRace revealPlayerType() {
        return revealOwningUnit().getPlayerType();
    }
}

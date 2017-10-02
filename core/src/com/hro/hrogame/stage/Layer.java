package com.hro.hrogame.stage;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.hro.hrogame.gameobject.GameObject;

public class Layer extends Group {

    // region Instance fields
    private boolean isStopped;
    // endregion

    // region C-tor
    public Layer() {
    }
    // endregion

    // region Act
    @Override
    public void act(float delta) {
        if (!isStopped) super.act(delta);
    }
    public void play() {
        isStopped = false;
        for (Actor actor : getChildren()) ((GameObject) actor).play();
        setTouchable(Touchable.enabled);
    }
    public void stop() {
        isStopped = true;
        for (Actor actor : getChildren()) ((GameObject) actor).pause();
        setTouchable(Touchable.disabled);
    }
    // endregion
}

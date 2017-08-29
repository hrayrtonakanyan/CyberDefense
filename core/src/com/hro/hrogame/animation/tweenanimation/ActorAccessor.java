package com.hro.hrogame.animation.tweenanimation;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ActorAccessor implements TweenAccessor<Actor>{

    // region Static fields
    public static final int MOVE_X = 0;
    public static final int MOVE_Y = 1;
    public static final int MOVE_XY = 2;
    public static final int CHANGE_WIDTH = 3;
    public static final int CHANGE_HEIGHT = 4;
    public static final int CHANGE_SIZE = 5;
    public static final int VANISHING = 6;
    // endregion

    // region Getter
    @Override
    public int getValues(Actor actor, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case MOVE_X:
                returnValues[0] = actor.getX();
                break;
            case MOVE_Y:
                returnValues[0] = actor.getY();
                break;
            case MOVE_XY:
                returnValues[0] = actor.getX();
                returnValues[1] = actor.getY();
                break;
            case CHANGE_WIDTH:
                returnValues[0] = actor.getWidth();
                break;
            case CHANGE_HEIGHT:
                returnValues[0] = actor.getHeight();
                break;
            case CHANGE_SIZE:
                returnValues[0] = actor.getWidth();
                returnValues[1] = actor.getHeight();
                break;
            case VANISHING:
                returnValues[0] = actor.getColor().a;
                break;
        }
        return 2;
    }
    // endregion

    // region Setters
    @Override
    public void setValues(Actor actor, int tweenType, float[] newValues) {
        switch (tweenType) {
            case MOVE_X:
                actor.setX(newValues[0]);
                break;
            case MOVE_Y:
                actor.setY(newValues[0]);
                break;
            case MOVE_XY:
                actor.setX(newValues[0]);
                actor.setY(newValues[1]);
                break;
            case CHANGE_WIDTH:
                actor.setWidth(newValues[0]);
                break;
            case CHANGE_HEIGHT:
                actor.setHeight(newValues[0]);
                break;
            case CHANGE_SIZE:
                actor.setWidth(newValues[0]);
                actor.setHeight(newValues[0]);
                break;
            case VANISHING:
                actor.getColor().a = newValues[0];
                break;
        }
    }
    // endregion
}
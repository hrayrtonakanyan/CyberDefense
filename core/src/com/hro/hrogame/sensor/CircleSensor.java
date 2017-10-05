package com.hro.hrogame.sensor;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.hro.hrogame.controller.ShapeEntityProvider;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.shape.CircleShape;
import com.hro.hrogame.shape.Shape;

public class CircleSensor extends UnitSensor {

    // region Instance fields
    private CircleShape circleShape;
    // endregion

    // region C-tor
    public CircleSensor(GameObject owner, ShapeEntityProvider entityProvider) {
        super(owner, entityProvider);

        circleShape = new CircleShape();
        setTouchable(Touchable.disabled);
    }
    // endregion

    // region Getters
    public Shape getShape() {
        return circleShape;
    }
    // endregion

    // region Setter
    public void setRadius(float radius) {
        circleShape.setRadius(radius);
        super.setSize(radius * 2, radius * 2);
        updateShapePosition();
    }
    @Override
    public void setSize(float width, float height) {
        throw new RuntimeException("circleSensor size must not be set externally.");
    }
    @Override
    public void setWidth(float width) {
        throw new RuntimeException("circleSensor size must not be set externally.");
    }
    @Override
    public void setHeight(float height) {
        throw new RuntimeException("circleSensor size must not be set externally.");
    }
    // endregion
}
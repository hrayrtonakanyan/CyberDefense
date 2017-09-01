package com.hro.hrogame.sensor;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.hro.hrogame.controller.ShapeEntityProvider;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.shape.RectangleShape;
import com.hro.hrogame.shape.Shape;

public class RectangleSensor extends UnitSensor {

    private RectangleShape rectangleShape;

    public RectangleSensor(GameObject owner, ShapeEntityProvider entityProvider) {
        super(owner, entityProvider);

        rectangleShape = new RectangleShape();
        setTouchable(Touchable.disabled);
    }

    public Shape getShape() {
        return rectangleShape;
    }


    private void updateShapeSize() {
        rectangleShape.setSize(getWidth(), getHeight());
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        updateShapeSize();
        updateShapePosition();

    }
    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        updateShapeSize();
        updateShapePosition();
    }
    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        updateShapeSize();
        updateShapePosition();
    }
}

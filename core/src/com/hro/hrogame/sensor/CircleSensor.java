package com.hro.hrogame.sensor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.hro.hrogame.controller.ShapeEntityProvider;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.shape.CircleShape;
import com.hro.hrogame.shape.Shape;

public class CircleSensor extends UnitSensor {

    // region Instance fields
    private CircleShape circleShape;
    private Image appearance;
    // endregion

    // region C-tor
    //TODO RemoveTexture and pass a drawable from the skin
    public CircleSensor(GameObject owner, ShapeEntityProvider entityProvider,
                        Texture texture) {
        super(owner, entityProvider);

        circleShape = new CircleShape();
        if (texture != null) {
            appearance = new Image(texture);
            addActor(appearance);
            appearance.setTouchable(Touchable.disabled);
        }
        setTouchable(Touchable.disabled);
    }
    // endregion

    //region Update
    public void removeAppearance() {
        if (appearance == null) return;
        appearance.remove();
        appearance = null;
    }
    private void updateAppearance() {
        if (appearance == null) return;
        appearance.setSize(getWidth(), getHeight());
        appearance.setPosition(0, 0);
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
        updateAppearance();
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

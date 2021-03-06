package com.hro.hrogame.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import java.util.Collection;
import java.util.HashMap;

public class GameStage extends Stage {

    // region Instance fields
    private float speedRatio = 1;
    private HashMap<LayerType, Layer> layers;
    // endregion

    // region C-tor
    public GameStage() {
        init();
    }
    // endregion

    // region init
    private void init() {
        layers = new HashMap<>();

        LayerType types[] = LayerType.values();
        for (LayerType type : types) {
            Layer layer = new Layer();
            layer.setName(type.toString());
            layers.put(type, layer);
            super.addActor(layer);
        }
    }
    // endregion

    // region Act
    @Override
    public void act() {
        super.act(Gdx.graphics.getDeltaTime() * speedRatio);
    }
    // endregion

    // region Add actor
    @Override
    public void addActor(Actor actor) {
        throw new RuntimeException("Wrong overload of .addActor() method was selected.");
    }
    public void addActor(Actor actor, LayerType layer) {
        layers.get(layer).addActor(actor);
    }
    // endregion

    // region Stage functionality
    @Override
    public void clear() {
        play();
        setAlpha(1);
        setTouchable(Touchable.enabled);
        Collection<Layer> values = layers.values();
        for (Layer layer : values) {
            layer.clear();
        }
    }
    public void setAlpha(float alpha) {
        Collection<Layer> values = layers.values();
        for (Layer layer : values) {
            if (layer.getName().equals(LayerType.MENU_UI.toString())) continue;
            layer.getColor().a = alpha;
        }
    }
    public void setTouchable(Touchable influence) {
        Collection<Layer> values = layers.values();
        for (Layer layer : values) {
            layer.setTouchable(influence);
        }
    }
    public void setTouchable(Touchable influence, LayerType... types) {
        for (LayerType type : types) {
            setTouchable(influence, type);
        }
    }
    public void setTouchable(Touchable influence, LayerType type) {
        layers.get(type).setTouchable(influence);
    }
    public void pause() {
        layers.get(LayerType.FOREGROUND).stop();
    }
    public void play() {
        layers.get(LayerType.FOREGROUND).play();
    }
    // endregion

    // region Setter
    public void setSpeedRatio(float speedRatio) {
        this.speedRatio = speedRatio;
    }
    // endregion

    // region Getter
    public float getSpeedRatio() {
        return speedRatio;
    }
    // endregion

}

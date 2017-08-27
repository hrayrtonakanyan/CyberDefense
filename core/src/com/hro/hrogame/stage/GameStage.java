package com.hro.hrogame.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Collection;
import java.util.HashMap;

public class GameStage extends Stage {

    private float speedRatio = 1;
    private HashMap<LayerType, Layer> layers;

    public GameStage() {
        init();
    }

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

    @Override
    public void act() {
        super.act(Gdx.graphics.getDeltaTime() * speedRatio);
    }

    @Override
    public void addActor(Actor actor) {
        throw new RuntimeException("Wrong overload of .addActor() method was selected.");
    }
    public void addActor(Actor actor, LayerType layer) {
        layers.get(layer).addActor(actor);
    }

    @Override
    public void clear() {
        Collection<Layer> values = layers.values();
        for (Layer layer : values) {
            layer.clear();
        }
    }

    public void setAlpha(float alpha) {
        Collection<Layer> values = layers.values();
        for (Layer layer : values) {
            layer.getColor().a = alpha;
        }
    }

    public float getSpeedRatio() {
        return speedRatio;
    }
    public void setSpeedRatio(float speedRatio) {
        this.speedRatio = speedRatio;
    }
}

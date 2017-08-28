package com.hro.hrogame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.HroGame;
import com.hro.hrogame.controller.EntityFactory;
import com.hro.hrogame.data.effect.cannoneffectdata.CannonEffectData;
import com.hro.hrogame.data.effect.shieldeffectdata.AbsorbShieldEffectData;
import com.hro.hrogame.data.effect.waveeffectdata.FreezerEffectData;
import com.hro.hrogame.data.effect.waveeffectdata.HellFireEffectData;
import com.hro.hrogame.data.effect.waveeffectdata.StunnerEffectData;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.PlayerRace;
import com.hro.hrogame.gameobject.effect.cannoneffect.CannonEffect;
import com.hro.hrogame.gameobject.effect.shieldeffect.AbsorbShieldEffect;
import com.hro.hrogame.gameobject.effect.waveeffect.FreezerEffect;
import com.hro.hrogame.gameobject.effect.waveeffect.HellFireEffect;
import com.hro.hrogame.gameobject.effect.waveeffect.StunnerEffect;
import com.hro.hrogame.gameobject.unit.UnitType;
import com.hro.hrogame.sensor.UnitSensor;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import com.hro.hrogame.utils.Util;

import java.util.Random;

public class GameScreen extends ScreenAdapter {

    // region Instance fields
    private HroGame game;
    private GameStage stage;

    final Random random = new Random();
    Actor selected;
    Color defaultColor = new Color(1, 1, 1, 0.8f);
    EntityFactory entityFactory;
    // endregion

    // region C-tor
    public GameScreen(HroGame game) {
        this.game = game;
        this.stage = game.stage;
    }
    // endregion

    // region Show
    @Override
    public void show() {
        test();
    }
    // endregion

    // region Render
    @Override
    public void render(float delta) {
        Util.cleanScreen();
        stage.act();
        stage.draw();
    }
    // endregion

    // region Test
    private void test() {
        entityFactory = new EntityFactory();

        GameObject obj1 = create(UnitType.BASE, PlayerRace.PLAYER, entityFactory, defaultColor);
        GameObject obj2 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        GameObject obj3 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        GameObject obj4 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        GameObject obj5 = create(UnitType.BASE, PlayerRace.PLAYER, entityFactory, defaultColor);
        GameObject obj6 = create(UnitType.BASE, PlayerRace.PLAYER, entityFactory, defaultColor);
        stage.addActor(obj1, LayerType.FOREGROUND);
        stage.addActor(obj2, LayerType.FOREGROUND);
        stage.addActor(obj3, LayerType.FOREGROUND);
        stage.addActor(obj4, LayerType.FOREGROUND);
        stage.addActor(obj5, LayerType.FOREGROUND);
        stage.addActor(obj6, LayerType.FOREGROUND);

        obj1.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Align.center);

        obj1.addEffect(createStunnerEffect(obj1, UnitType.BASE));
        obj2.addEffect(createAbsorbShieldEffect(obj2, UnitType.TANK));
        obj3.addEffect(createHellFireEffect(obj3, UnitType.TANK));
        obj4.addEffect(createSimpleCannonEffect(obj4, UnitType.TANK));
        obj5.addEffect(createFreezerEffect(obj5, UnitType.BASE));


        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget() instanceof GameObject) {
                    if (selected != null)
                        selected.getColor().a = 0.4f;
                    selected = event.getTarget();
                    selected.getColor().a = 1f;
                } else if (selected != null){
                    ((GameObject)selected).setDestination(event.getStageX(), event.getStageY());
                }
            }
        });
    }

    private CannonEffect createSimpleCannonEffect(GameObject obj, UnitType type) {
        CannonEffectData data = new CannonEffectData(1, 5, 25, 1);
        CannonEffect effect = new CannonEffect(obj, entityFactory, data);
        UnitSensor sensor = entityFactory.createSensor(effect, type);
        effect.setSensor(sensor);
        return effect;
    }
    private HellFireEffect createHellFireEffect(GameObject obj, UnitType type) {
        HellFireEffectData data = new HellFireEffectData(10, 50);
        HellFireEffect effect = new HellFireEffect(obj, entityFactory, data);
        UnitSensor sensor = entityFactory.createSensor(effect, type);
        effect.setSensor(sensor);
        return effect;
    }
    private FreezerEffect createFreezerEffect(GameObject obj, UnitType type) {
        FreezerEffectData data = new FreezerEffectData(10);
        FreezerEffect effect = new FreezerEffect(obj, entityFactory, data);
        UnitSensor sensor = entityFactory.createSensor(effect, type);
        effect.setSensor(sensor);
        return effect;
    }
    private StunnerEffect createStunnerEffect(GameObject obj, UnitType type) {
        StunnerEffectData data = new StunnerEffectData(10);
        StunnerEffect effect = new StunnerEffect(obj, entityFactory, data);
        UnitSensor sensor = entityFactory.createSensor(effect, type);
        effect.setSensor(sensor);
        return effect;
    }
    private AbsorbShieldEffect createAbsorbShieldEffect(GameObject obj, UnitType type) {
        AbsorbShieldEffectData data = new AbsorbShieldEffectData(10);
        AbsorbShieldEffect effect = new AbsorbShieldEffect(obj, entityFactory, data);
        UnitSensor sensor = entityFactory.createSensor(effect, type);
        effect.setSensor(sensor);
        return effect;
    }
    private GameObject create(UnitType unitType, PlayerRace race, EntityFactory factory, Color color) {
        GameObject obj1 = factory.createUnit(unitType, race, 1);
        obj1.setSize(60, 60);
        obj1.setColor(color);
        obj1.setPosition(random.nextInt((int)(Gdx.graphics.getWidth() - obj1.getWidth())),
                random.nextInt((int)(Gdx.graphics.getHeight() - obj1.getHeight())));
        return obj1;
    }
    // endregion
}

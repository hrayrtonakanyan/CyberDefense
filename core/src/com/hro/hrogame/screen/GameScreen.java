package com.hro.hrogame.screen;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.HroGame;
import com.hro.hrogame.animation.tweenanimation.TweenAnimation;
import com.hro.hrogame.constants.StringConstants;
import com.hro.hrogame.controller.EntityFactory;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.GameObjectAdapter;
import com.hro.hrogame.gameobject.PlayerRace;
import com.hro.hrogame.gameobject.effect.EffectType;
import com.hro.hrogame.gameobject.unit.UnitType;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import com.hro.hrogame.utils.Util;

import java.util.Random;

public class GameScreen extends ScreenAdapter {

    // region Instance fields
    private HroGame game;
    private GameStage stage;
    private TweenManager tweenManager;

    Label label;
    final Random random = new Random();
    Actor selected;
    Color defaultColor = new Color(1, 1, 1, 0.8f);
    EntityFactory entityFactory;
    // endregion

    // region C-tor
    public GameScreen(HroGame game) {
        this.game = game;
        this.stage = game.stage;
        this.tweenManager = game.tweenManager;
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
        tweenManager.update(delta);
        stage.draw();
    }
    // endregion

    // region Test
    private void test() {
        entityFactory = new EntityFactory();

        GameObject base = create(UnitType.BASE, PlayerRace.PLAYER, entityFactory, defaultColor);
        GameObject obj2 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        GameObject obj3 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        GameObject obj4 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        GameObject obj5 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        GameObject obj6 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        stage.addActor(base, LayerType.FOREGROUND);
        stage.addActor(obj2, LayerType.FOREGROUND);
        stage.addActor(obj3, LayerType.FOREGROUND);
        stage.addActor(obj4, LayerType.FOREGROUND);
        stage.addActor(obj5, LayerType.FOREGROUND);
        stage.addActor(obj6, LayerType.FOREGROUND);

        base.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Align.center);

        base.addEffect(entityFactory.createEffect(base, EffectType.HELL_FIRE));


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

    private GameObject create(UnitType unitType, PlayerRace race, EntityFactory factory, Color color) {
        GameObject obj = factory.createUnit(unitType, race, 1);
        obj.setSize(60, 60);
        obj.setColor(color);
        obj.setPosition(random.nextInt((int)(Gdx.graphics.getWidth() - obj.getWidth())),
                random.nextInt((int)(Gdx.graphics.getHeight() - obj.getHeight())));
        obj.addGameObjectAdapter(new GameObjectAdapter() {
            @Override
            public void onTakeDamage(float damage, GameObject damagedUnit) {
                String text = "-" + (int) damage;
                label = new Label(text, StringConstants.skin);
                label.setPosition(damagedUnit.getX(Align.center), damagedUnit.getY(Align.topRight));
                TweenAnimation.pop_up(label, 3, 30, 0, tweenManager, null);
                stage.addActor(label, LayerType.GAME_UI);
            }
        });
        return obj;
    }
    // endregion
}

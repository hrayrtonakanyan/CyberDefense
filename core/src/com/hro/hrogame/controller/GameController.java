package com.hro.hrogame.controller;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.animation.tweenanimation.TweenAnimation;
import com.hro.hrogame.constants.StringConstants;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.GameObjectAdapter;
import com.hro.hrogame.gameobject.PlayerRace;
import com.hro.hrogame.gameobject.effect.EffectType;
import com.hro.hrogame.gameobject.unit.BaseUnit;
import com.hro.hrogame.gameobject.unit.UnitType;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;

import java.util.Random;

public class GameController {

    // region Instance fields
    private GameStage stage;
    private TweenManager tweenManager;
    private EntityFactory entityFactory;
    private WaveController waveController;
    private BaseUnit baseUnit;


    final Random random = new Random();
    Actor selected;
    Color defaultColor = new Color(1, 1, 1, 0.8f);
    // endregion

    // region C-tor
    public GameController(GameStage stage, TweenManager tweenManager) {
        this.stage = stage;
        this.tweenManager = tweenManager;
        init();
    }
    // endregion

    // region Init
    private void init() {
        entityFactory = new EntityFactory();
        waveController = new WaveController(WaveController.WAVE_NOMINAL_WEIGHT, WaveController.GAME_PROGRESS_RATIO);
        createBase();
        test();
    }
    // endregion

    // region Update
    public void update(float delta){

    }
    // endregion

    // region Create
    private void createBase() {
        baseUnit = (BaseUnit) entityFactory.createUnit(UnitType.BASE, PlayerRace.PLAYER, 1);
        baseUnit.setSize(BaseUnit.WIDTH, BaseUnit.HEIGHT);
        baseUnit.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        baseUnit.addGameObjectAdapter(new GameObjectAdapter() {
            @Override
            public void onTakeDamage(float damage, GameObject damagedUnit) {
                pushOnTakeDamageTweenAnimation(damage, damagedUnit);
            }
            @Override
            public void onDie(GameObject dyingUnit, GameObject killerUnit) {

            }
            @Override
            public void onKill(GameObject dyingUnit, GameObject killerUnit) {

            }
        });
        stage.addActor(baseUnit, LayerType.FOREGROUND);
    }
    private void pushOnTakeDamageTweenAnimation(float damage, GameObject damagedUnit) {
        String text = "-" + (int) damage;
        Label label = new Label(text, StringConstants.skin);
        label.setPosition(damagedUnit.getX(Align.center), damagedUnit.getY(Align.topRight));
        TweenAnimation.pop_up(label, TweenAnimation.POP_UP_DURATION,
                TweenAnimation.POP_UP_MOVE_TARGET,
                TweenAnimation.POP_UP_VANISH_TARGET, tweenManager, null);
        stage.addActor(label, LayerType.GAME_UI);
    }
    public void addEffectToBase(EffectType type) {
        switch (type) {
            case SIMPLE_CANNON:
                baseUnit.addEffect(entityFactory.createEffect(baseUnit, type));
                break;
            case HARD_CANNON:
                baseUnit.addEffect(entityFactory.createEffect(baseUnit, type));
                break;
            case HELL_FIRE:
                baseUnit.addEffect(entityFactory.createEffect(baseUnit, type));
                break;
            case FREEZER:
                baseUnit.addEffect(entityFactory.createEffect(baseUnit, type));
                break;
            case STUNNER:
                baseUnit.addEffect(entityFactory.createEffect(baseUnit, type));
                break;
            case ABSORB_SHIELD:
                baseUnit.addEffect(entityFactory.createEffect(baseUnit, type));
                break;
            default: throw new RuntimeException("The effect doesn't exist, or OverTime effect type was passed. OverTime effects can't be added externally.");
        }
    }
    // endregion

    // region Test
    private void test() {
        GameObject obj2 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        GameObject obj3 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        GameObject obj4 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        GameObject obj5 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        GameObject obj6 = create(UnitType.TANK, PlayerRace.AI, entityFactory, defaultColor);
        stage.addActor(obj2, LayerType.FOREGROUND);
        stage.addActor(obj3, LayerType.FOREGROUND);
        stage.addActor(obj4, LayerType.FOREGROUND);
        stage.addActor(obj5, LayerType.FOREGROUND);
        stage.addActor(obj6, LayerType.FOREGROUND);

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
                pushOnTakeDamageTweenAnimation(damage, damagedUnit);
            }
        });
        return obj;
    }
    // endregion
}

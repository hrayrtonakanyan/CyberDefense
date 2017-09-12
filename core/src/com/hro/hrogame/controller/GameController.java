package com.hro.hrogame.controller;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.animation.particleanimation.AnimationListener;
import com.hro.hrogame.animation.tweenanimation.TweenAnimation;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.constants.StringConstants;
import com.hro.hrogame.gameobject.GameObject;
import com.hro.hrogame.gameobject.GameObjectAdapter;
import com.hro.hrogame.gameobject.PlayerRace;
import com.hro.hrogame.gameobject.effect.EffectType;
import com.hro.hrogame.gameobject.effect.cannoneffect.SimpleCannonEffect;
import com.hro.hrogame.gameobject.unit.BaseUnit;
import com.hro.hrogame.gameobject.unit.RamUnit;
import com.hro.hrogame.gameobject.unit.TankUnit;
import com.hro.hrogame.gameobject.unit.UnitType;
import com.hro.hrogame.primitives.Point;
import com.hro.hrogame.primitives.ProgressiveAttribute;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import com.hro.hrogame.timer.Task;
import com.hro.hrogame.timer.Timer;
import com.hro.hrogame.utils.Util;

import java.util.ArrayList;
import java.util.Random;

public class GameController {

    // region Static fields
    public static final float GAME_PROGRESS_RATIO = 1;
    public static final float BASE_UNIT_PROGRESS_RATIO = 0.7f;
    public static final float UNITS_GENERATION_INTERVAL = 5;
    public static final float DELAY_BETWEEN_WAVES = 15;
    public static final float DELAY_ON_START = 5;
    // endregion

    // region Instance fields
    private Random random = new Random();
    private GameStage stage;
    private Timer waveTimer;
    private TweenManager tweenManager;
    private EntityFactory entityFactory;
    private WaveController waveController;
    private BaseUnit baseUnit;
    private Timeline timeline = null;
    private ArrayList<Timeline> animationTimelineList;
    private ArrayList<GameObject> enemiesWaitList;
    private int playerGold;
    private float playerExperience;
    private float accessExperience = WaveController.INITIAL_WEIGHT;
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
        waveController = new WaveController(WaveController.INITIAL_WEIGHT, GAME_PROGRESS_RATIO);
        animationTimelineList = new ArrayList<>();
        enemiesWaitList = new ArrayList<>();
        waveTimer = new Timer();
        createBase();
        startNewWave();
    }
    // endregion

    // region Update
    public void update(float delta){
        waveTimer.update(delta);
        if (playerExperience >= accessExperience) {
            baseUnit.levelUp();
            calculatePlayerAccessExperience();
            System.out.println("Base unit is level up " + baseUnit.getLevel());
        }
    }
    // endregion

    // region Base
    private void createBase() {
        baseUnit = (BaseUnit) entityFactory.createUnit(UnitType.BASE, PlayerRace.PLAYER, 1);
        baseUnit.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        baseUnit.addGameObjectAdapter(new GameObjectAdapter() {
            @Override
            public void onTakeDamage(float damage, GameObject damagedUnit) {
                pushOnTakeDamageTweenAnimation(damage, damagedUnit);
            }
            @Override
            public void onKill(GameObject dyingUnit, GameObject killerUnit) {
                playerExperience += dyingUnit.getWeight();
                System.out.println("Collected weight " + playerExperience);
            }
        });
        stage.addActor(baseUnit, LayerType.FOREGROUND);
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

    // region Wave
    private void startNewWave() {
        generateWave();
        startWaveTimer();
    }
    private void generateWave() {
        float waveWeight = waveController.calculateWaveWeight();
        System.out.println("Wave weight " + waveWeight);
        int unitLevel = calculateUnitLevelForWave();
        System.out.println("Units level " + unitLevel);
        int tankQuantity = calculateUnitsQuantity(waveWeight, unitLevel, WaveController.TANK_UNITS_CREATION_RATIO, UnitType.TANK);
        System.out.println("Tank quantity in wave " + tankQuantity);
        int ramQuantity = calculateUnitsQuantity(waveWeight, unitLevel, WaveController.RAM_UNITS_CREATION_RATIO, UnitType.RAM);
        System.out.println("Ram quantity in wave " + ramQuantity);
        for (int i = 0; i < tankQuantity; i++) {
            GameObject unit = entityFactory.createUnit(UnitType.TANK, PlayerRace.AI, unitLevel);
            unit.addGameObjectAdapter(new GameObjectAdapter() {
                @Override
                public void onTakeDamage(float damage, GameObject damagedUnit) {
                    pushOnTakeDamageTweenAnimation(damage, damagedUnit);
                }
                @Override
                public void onDie(GameObject dyingUnit, GameObject killerUnit) {
                    int gold = dyingUnit.getReward();
                    playerGold += gold;
                    pushOnDieTweenAnimation(gold, dyingUnit);
                }
            });
            enemiesWaitList.add(unit);
        }
        for (int i = 0; i < ramQuantity; i++) {
            GameObject unit = entityFactory.createUnit(UnitType.RAM, PlayerRace.AI, unitLevel);
            unit.addGameObjectAdapter(new GameObjectAdapter() {
                @Override
                public void onTakeDamage(float damage, GameObject damagedUnit) {
                    pushOnTakeDamageTweenAnimation(damage, damagedUnit);
                }
                @Override
                public void onDie(GameObject dyingUnit, GameObject killerUnit) {
                    int gold = dyingUnit.getReward();
                    playerGold += gold;
                    pushOnDieTweenAnimation(gold, dyingUnit);
                }
            });
            enemiesWaitList.add(unit);
        }
    }
    private void startWaveTimer() {
        waveTimer.scheduleTask(createSpawnUnitTask(), new Runnable() {
            @Override
            public void run() {
                startNewWave();
            }
        });
    }
    private Task createSpawnUnitTask() {
        float delay = DELAY_BETWEEN_WAVES;
        if (waveController.getWaveNumber() == 1) delay = DELAY_ON_START;
        int repeatCount = enemiesWaitList.size();
        return waveTimer.createTask(delay, UNITS_GENERATION_INTERVAL, repeatCount, new Runnable() {
            @Override
            public void run() {
                spawnUnit();
            }
        });
    }
    // endregion

    // region Spawn
    private void spawnUnit() {
        int i = random.nextInt(enemiesWaitList.size());
        GameObject enemy = enemiesWaitList.get(i);
        enemiesWaitList.remove(i);
        Point spawnPoint = generateSpawnPoint();
        enemy.setPosition(spawnPoint.x, spawnPoint.y, Align.center);
        enemy.setDestination(baseUnit.getX(Align.center), baseUnit.getY(Align.center));
        stage.addActor(enemy, LayerType.FOREGROUND);
    }
    private Point generateSpawnPoint() {
        int side = random.nextInt(4);
        float x, y;
        switch (side) {
            case 0:
                x = -40;
                y = random.nextInt((int) stage.getHeight());
                return new Point(x, y);
            case 1:
                x = random.nextInt((int) stage.getWidth());
                y = stage.getHeight() + 40;
                return new Point(x, y);
            case 2:
                x = stage.getWidth() + 40;
                y = random.nextInt((int) stage.getHeight());
                return new Point(x, y);
            case 3:
                x = random.nextInt((int) stage.getWidth());
                y = -40;
                return new Point(x, y);
            default: throw new RuntimeException("Wrong Spawn Point coordinates was generated");
        }
    }
    // endregion

    // region Calculation
    private void calculatePlayerAccessExperience() {
        accessExperience += accessExperience * ParametersConstants.PROGRESS_RATIO *
                                               GameController.GAME_PROGRESS_RATIO *
                                               GameController.BASE_UNIT_PROGRESS_RATIO;
    }
    private int calculateUnitsQuantity(float waveWeight, int unitLevel, float creationRatio, UnitType type) {
        float unitsTotalWeight = waveWeight * creationRatio;
        float unitWeight = calculateUnitWeight(type, unitLevel);
        return (int) (unitsTotalWeight / unitWeight);
    }
    private int calculateUnitLevelForWave() {
        int unitLevel = waveController.getWaveNumber() / WaveController.ENEMY_UNITS_LEVEL_UP_FREQUENCY_PER_WAVE;
        if (unitLevel == 0) unitLevel = 1;
        return unitLevel;
    }
    private float calculateUnitWeight(UnitType type, int level) {
        ProgressiveAttribute health;
        float weight;
        switch (type) {
            case BASE:
                health = new ProgressiveAttribute(BaseUnit.HEALTH, BaseUnit.MAX_HEALTH);
                Util.calcProgressAndDefineWeight(0, level, ParametersConstants.PROGRESS_RATIO, true, health);
                weight = (int) health.current / GameObject.HEALTH_TO_WEIGHT_RATIO;
                weight += Util.calcProgressAndDefineWeight(SimpleCannonEffect.INITIAL_WEIGHT, level,
                        ParametersConstants.PROGRESS_RATIO, false, SimpleCannonEffect.getDataProgressiveAttributes());
                return weight;
            case TANK:
                health = new ProgressiveAttribute(TankUnit.HEALTH, TankUnit.MAX_HEALTH);
                Util.calcProgressAndDefineWeight(0, level, ParametersConstants.PROGRESS_RATIO, true, health);
                weight = (int) health.current / GameObject.HEALTH_TO_WEIGHT_RATIO;
                weight += Util.calcProgressAndDefineWeight(SimpleCannonEffect.INITIAL_WEIGHT, level,
                        ParametersConstants.PROGRESS_RATIO, false, SimpleCannonEffect.getDataProgressiveAttributes());
                return weight;
            case RAM:
                health = new ProgressiveAttribute(RamUnit.HEALTH, RamUnit.MAX_HEALTH);
                Util.calcProgressAndDefineWeight(0, level, ParametersConstants.PROGRESS_RATIO, true, health);
                weight = (int) health.current / GameObject.HEALTH_TO_WEIGHT_RATIO;
                weight += Util.calcProgressAndDefineWeight(SimpleCannonEffect.INITIAL_WEIGHT, level,
                        ParametersConstants.PROGRESS_RATIO, false, SimpleCannonEffect.getDataProgressiveAttributes());
                return weight;
            default: throw new RuntimeException("Wrong UnitType was passed");
        }
    }
    // endregion

    // region Play/Pause
    public void play() {
        waveTimer.resume();
        for (Timeline timeline : animationTimelineList) timeline.resume();
        stage.playGame();
    }
    public void pause() {
        waveTimer.pause();
        for (Timeline timeline : animationTimelineList) timeline.pause();
        stage.pauseGame();
    }
    // endregion

    // region Animation
    private void pushOnTakeDamageTweenAnimation(float damage, GameObject damagedUnit) {
        String text = "-" + (int) damage;
        final Label label = new Label(text, StringConstants.skin);
        label.setPosition(damagedUnit.getX() + damagedUnit.getWidth(), damagedUnit.getY());
        timeline = TweenAnimation.pop_up(label, TweenAnimation.POP_UP_DURATION,
                                                TweenAnimation.POP_UP_MOVE_TARGET,
                                                TweenAnimation.POP_UP_VANISH_TARGET,
                                                tweenManager, new AnimationListener() {
                                                                  @Override
                                                                  public void onComplete() {
                                                                      label.remove();
                                                                      animationTimelineList.remove(timeline);
                                                                  }
                                                              });
        animationTimelineList.add(timeline);
        stage.addActor(label, LayerType.GAME_UI);
    }
    private void pushOnDieTweenAnimation(int gold, GameObject damagedUnit) {
        Image coin = new Image(new Texture("coin.png"));
        coin.setSize(20, 20);
        Label label = new Label(" " + gold, StringConstants.skin);
        label.setX(coin.getWidth());
        final Group reward = new Group();
        reward.setPosition(damagedUnit.getX() + damagedUnit.getWidth(), damagedUnit.getY() + damagedUnit.getHeight());
        reward.addActor(coin);
        reward.addActor(label);
        timeline = TweenAnimation.pop_up(reward, TweenAnimation.POP_UP_DURATION,
                                                 TweenAnimation.POP_UP_MOVE_TARGET,
                                                 TweenAnimation.POP_UP_VANISH_TARGET,
                                                 tweenManager, new AnimationListener() {
                                                                   @Override
                                                                   public void onComplete() {
                                                                       reward.remove();
                                                                       animationTimelineList.remove(timeline);
                                                                   }
                                                               });
        animationTimelineList.add(timeline);
        stage.addActor(reward, LayerType.GAME_UI);
    }
    // endregion

    // region Getter
    public int getWaveNumber() {
        return waveController.getWaveNumber();
    }
    public int getPlayerGold() {
        return playerGold;
    }
    // endregion
}
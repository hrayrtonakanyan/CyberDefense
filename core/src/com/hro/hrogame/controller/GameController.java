package com.hro.hrogame.controller;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
import com.hro.hrogame.ui.EffectDialog;
import com.hro.hrogame.ui.EffectDialogListener;
import com.hro.hrogame.utils.Util;

import java.util.ArrayList;
import java.util.Random;

public class GameController {

    // region Static fields
    public static final float GAME_PROGRESS_RATIO = 1;
    public static final float BASE_UNIT_PROGRESS_RATIO = 0.7f;
    public static final float WAVE_TIMER_INTERVAL = 5;
    public static final float WAVE_TIMER_DELAY = 5;
    // endregion

    // region Instance fields
    private Random random = new Random();
    private GameStage stage;
    private Label goldLabel;
    private Label waveLabel;
    private EffectDialog effectDialog;
    private EntityFactory entityFactory;
    private WaveController waveController;
    private Timer waveTimer;
    private TweenManager tweenManager;
    private BaseUnit baseUnit;
    private Timeline timeline = null;
    private ArrayList<Timeline> animationTimelineList;
    private ArrayList<GameObject> enemiesWaitList;
    private int playerGold;
    private float playerExperience;
    private float accessExperience = WaveController.INITIAL_WEIGHT;
    private boolean isPaused;
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
        waveController = new WaveController(WaveController.INITIAL_WEIGHT, GAME_PROGRESS_RATIO);
        entityFactory = new EntityFactory();
        animationTimelineList = new ArrayList<>();
        enemiesWaitList = new ArrayList<>();
        waveTimer = new Timer();
        initUI();
        createBase();
        startNewWave();
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget().equals(baseUnit)) effectDialog.show();
                else if (!(event.getTarget().equals(effectDialog))) effectDialog.hide();
            }
        });
    }
    // endregion

    // region UI
    private void initUI() {
        effectDialog = new EffectDialog(StringConstants.skin, stage);
        effectDialog.addEffectDialogListener(new EffectDialogListener() {
            @Override
            public void onItemBought(EffectType type, int price) {
                addEffectToBase(type);
                payForPurchase(price);
            }
        });
        createGoldLabel();
        createWaveLabel();
        createPlayPauseButtons();
    }
    private void createGoldLabel() {
        Image coin = new Image(new Texture("coin.png"));
        coin.setSize(20, 20);
        goldLabel = new Label(" " + playerGold, StringConstants.skin);
        coin.setPosition(coin.getWidth(), stage.getHeight() - coin.getHeight() * 2);
        goldLabel.setPosition(coin.getX() + coin.getWidth(), coin.getY());
        stage.addActor(coin, LayerType.MENU_UI);
        stage.addActor(goldLabel, LayerType.MENU_UI);
    }
    private void createWaveLabel() {
        waveLabel = new Label("Wave " + 1, StringConstants.skin);
        waveLabel.setPosition(stage.getWidth() / 2, stage.getHeight() - waveLabel.getHeight(), Align.center);
        stage.addActor(waveLabel, LayerType.MENU_UI);
    }
    private void createPlayPauseButtons() {
        Image playImage = new Image(new Texture("play.png"));
        Image pauseImage = new Image(new Texture("pause.png"));
        Button.ButtonStyle btnStyle = new Button.ButtonStyle(pauseImage.getDrawable(), playImage.getDrawable(), playImage.getDrawable());
        Button playPauseBtn = new Button(btnStyle);
        playPauseBtn.setSize(30, 30);
        playPauseBtn.setPosition(stage.getWidth() - playPauseBtn.getWidth(), stage.getHeight() - playPauseBtn.getHeight(), Align.center);
        playPauseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (isPaused) play();
                else pause();
            }
        });
        stage.addActor(playPauseBtn, LayerType.MENU_UI);
    }
    // endregion

    // region Update
    public void update(float delta){
        tweenManager.update(delta);
        waveTimer.update(delta);
        if (playerExperience >= accessExperience) {
            baseUnit.levelUp();
            calculatePlayerAccessExperience();
            System.out.println("Base unit is level up " + baseUnit.getLevel());
        }
    }
    private void updateWaveInfo(int waveNumber) {
        waveLabel.setText("Wave " + waveNumber);
        animateLabelOnWaveChange();
    }
    private void updateGoldInfo() {
        goldLabel.setText(" " + playerGold);
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
    private void addEffectToBase(EffectType type) {
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
        waveTimer.scheduleTask(createSpawnUnitTask());
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
                    earnGold(gold);
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
                    earnGold(gold);
                    pushOnDieTweenAnimation(gold, dyingUnit);
                }
            });
            enemiesWaitList.add(unit);
        }
    }
    private Task createSpawnUnitTask() {
        int repeatCount = enemiesWaitList.size();
        return waveTimer.createTask(WAVE_TIMER_DELAY, WAVE_TIMER_INTERVAL, repeatCount, new Runnable() {
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
        if (enemiesWaitList.size() == 0) enemy.addGameObjectAdapter(new GameObjectAdapter() {
            @Override
            public void onDie(GameObject dyingUnit, GameObject killerUnit) {
                startNewWave();
                updateWaveInfo(waveController.getWaveNumber());
            }
        });
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
    private void payForPurchase(int price) {
        playerGold -= price;
        effectDialog.setPlayerGold(playerGold);
        updateGoldInfo();
    }
    private void earnGold(int gold) {
        playerGold += gold;
        effectDialog.setPlayerGold(playerGold);
        updateGoldInfo();
    }
    // endregion

    // region Play/Pause
    private void play() {
        waveTimer.resume();
        for (Timeline timeline : animationTimelineList) timeline.resume();
        stage.playGame();
        isPaused = false;
    }
    private void pause() {
        waveTimer.pause();
        for (Timeline timeline : animationTimelineList) timeline.pause();
        stage.pauseGame();
        isPaused = true;
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
    private void animateLabelOnWaveChange() {
        waveLabel.setFontScale(3);
        waveLabel.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        float moveTarget = Gdx.graphics.getHeight() - waveLabel.getHeight() * 2;
        timeline = TweenAnimation.animateWaveLabel(waveLabel, 5,
                moveTarget, 1, tweenManager, new AnimationListener() {
                    @Override
                    public void onComplete() {
                        animationTimelineList.remove(timeline);
                    }
                });
        animationTimelineList.add(timeline);
    }
    // endregion
}
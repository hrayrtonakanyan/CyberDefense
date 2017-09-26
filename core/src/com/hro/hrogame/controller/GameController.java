package com.hro.hrogame.controller;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
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
    public static final float GOLD_LABEL_COIN_RADIUSx2 = Gdx.graphics.getWidth() / 35;
    public static final float PLAY_PAUSE_BUTTON_RADIUSx2 = Gdx.graphics.getHeight() / 16;
    public static final float LABEL_SCALE = 1.5f;
    public static final float WAVE_LABEL_SCALE_MAX = 4;


    public static final float GAME_PROGRESS_RATIO = 1;
    public static final float PLAYER_PROGRESS_RATIO = 0.7f;
    public static final float WAVE_TIMER_INTERVAL = 5;
    public static final float WAVE_TIMER_DELAY = 5;
    // endregion

    // region Instance fields
    private Random random = new Random();
    private GameStage stage;
    private Label goldLabel;
    private Label levelLabel;
    private Label waveLabel;
    private ProgressBar xpBar;
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
    private float playerXP;
    private float accessXP = WaveController.INITIAL_WEIGHT;
    private int aliveUnitsQuantity;
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

        Image background = new Image(new Texture("background.png"));
        background.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(background, LayerType.BACKGROUND);

        initUI();
        createBase();
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
        createLevelLabel();
        createXPBar();
        createWaveLabel();
        createPlayPauseButtons();
    }
    private void createGoldLabel() {
        Image coin = new Image(new Texture("coin.png"));
        coin.setSize(GOLD_LABEL_COIN_RADIUSx2, GOLD_LABEL_COIN_RADIUSx2);
        coin.setPosition(coin.getWidth(), stage.getHeight() - coin.getHeight() * 2);
        goldLabel = new Label(" " + playerGold, StringConstants.skin);
        goldLabel.setFontScale(LABEL_SCALE, LABEL_SCALE);
        goldLabel.setPosition(coin.getX() + coin.getWidth(), coin.getY());
        stage.addActor(coin, LayerType.MENU_UI);
        stage.addActor(goldLabel, LayerType.MENU_UI);
    }
    private void createLevelLabel() {
        levelLabel = new Label("Lvl: " + 1, StringConstants.skin);
        levelLabel.setFontScale(LABEL_SCALE, LABEL_SCALE);
        levelLabel.setPosition(goldLabel.getX() + goldLabel.getWidth() + levelLabel.getWidth(), goldLabel.getY());
        stage.addActor(levelLabel, LayerType.MENU_UI);
    }
    private void createXPBar() {
        Label xpLabel = new Label("XP: ", StringConstants.skin);
        xpLabel.setScale(1.5f, 1.5f);
        xpLabel.setPosition(levelLabel.getX() + levelLabel.getWidth() * 2, levelLabel.getY());
        xpLabel.setFontScale(LABEL_SCALE, LABEL_SCALE);
        xpBar = new ProgressBar(0, accessXP, 1, false, StringConstants.skin);
        xpBar.setSize(150, GOLD_LABEL_COIN_RADIUSx2);
        xpBar.setPosition(xpLabel.getX() + xpLabel.getWidth() * 2, xpLabel.getY());
        xpBar.setValue(0);
        xpBar.setAnimateDuration(0.2f);
        xpBar.setColor(Color.GOLD);
        stage.addActor(xpLabel, LayerType.MENU_UI);
        stage.addActor(xpBar, LayerType.MENU_UI);
    }
    private void createWaveLabel() {
        waveLabel = new Label("Wave " + 1, StringConstants.skin);
        waveLabel.setFontScale(LABEL_SCALE, LABEL_SCALE);
        waveLabel.setPosition(xpBar.getX() + xpBar.getWidth() + waveLabel.getWidth(), xpBar.getY());
        stage.addActor(waveLabel, LayerType.MENU_UI);
    }
    private void createPlayPauseButtons() {
        Image playImage = new Image(new Texture("play.png"));
        Image pauseImage = new Image(new Texture("pause.png"));
        Button.ButtonStyle btnStyle = new Button.ButtonStyle(pauseImage.getDrawable(), playImage.getDrawable(), playImage.getDrawable());
        Button playPauseBtn = new Button(btnStyle);
        playPauseBtn.setSize(PLAY_PAUSE_BUTTON_RADIUSx2, PLAY_PAUSE_BUTTON_RADIUSx2);
        playPauseBtn.setPosition(stage.getWidth() - playPauseBtn.getWidth(), stage.getHeight() - playPauseBtn.getHeight(), Align.center);
        playPauseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (isPaused) play();
                else pause();
                TweenAnimation.bounce(actor, tweenManager, null);
            }
        });
        stage.addActor(playPauseBtn, LayerType.MENU_UI);
    }
    // endregion

    // region Update
    public void update(float delta){
        tweenManager.update(delta);
        waveTimer.update(delta);
        if (playerXP >= accessXP) {
            System.out.println("Player XP is " + playerXP + ", AccessXP was " + accessXP);
            baseUnit.levelUp();
            updateNextLevelAccessXP();
        }
        if (aliveUnitsQuantity == 0) startNewWave();
    }
    private void updateGoldInfo() {
        goldLabel.setText(" " + playerGold);
    }
    private void updateWaveInfo(int waveNumber) {
        waveLabel.setText("Wave " + waveNumber);
        animateLabelOnWaveChange();
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
                playerXP += dyingUnit.getWeight() * PLAYER_PROGRESS_RATIO;
                xpBar.setValue(playerXP);
                System.out.println(xpBar.getValue() + " XP from " + accessXP);
            }
            @Override
            public void onLevelUp(GameObject gameObject, int level) {
                levelLabel.setText("Lvl: " + level);
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
        updateWaveInfo(waveController.getWaveNumber());
    }
    private void generateWave() {
        float waveWeight = waveController.calculateWaveWeight();
        int unitLevel = calculateUnitLevelForWave();
        int tankQuantity = calculateUnitsQuantity(waveWeight, unitLevel, WaveController.TANK_UNITS_CREATION_RATIO, UnitType.TANK);
        int ramQuantity = calculateUnitsQuantity(waveWeight, unitLevel, WaveController.RAM_UNITS_CREATION_RATIO, UnitType.RAM);
        for (int i = 0; i < tankQuantity; i++) {
            GameObject unit = entityFactory.createUnit(UnitType.TANK, PlayerRace.AI, unitLevel);
            unit.addGameObjectAdapter(new GameObjectAdapter() {
                @Override
                public void onTakeDamage(float damage, GameObject damagedUnit) {
                    pushOnTakeDamageTweenAnimation(damage, damagedUnit);
                }
                @Override
                public void onDie(GameObject dyingUnit, GameObject killerUnit) {
                    aliveUnitsQuantity--;
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
                    aliveUnitsQuantity--;
                    int gold = dyingUnit.getReward();
                    earnGold(gold);
                    pushOnDieTweenAnimation(gold, dyingUnit);
                }
            });
            enemiesWaitList.add(unit);
        }
        aliveUnitsQuantity = enemiesWaitList.size();
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
    private void updateNextLevelAccessXP() {
        float previousLevelXP = accessXP;
        float nextLevelXP = accessXP;
        nextLevelXP += nextLevelXP * ParametersConstants.PROGRESS_RATIO;
        accessXP += nextLevelXP;
        xpBar.setRange(previousLevelXP, accessXP);
        xpBar.setValue(playerXP);
        System.out.println("Progress bar XP " + xpBar.getValue() + " XP from " + xpBar.getMaxValue());
        System.out.println("True XP " + playerXP + " XP from " + accessXP);
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
//        waveLabel.setFontScale(WAVE_LABEL_SCALE_MAX);
//        waveLabel.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
//        float moveTarget = Gdx.graphics.getHeight() - waveLabel.getHeight() * 2;
//        timeline = TweenAnimation.animateWaveLabel(waveLabel, 5,
//                moveTarget, LABEL_SCALE, tweenManager, new AnimationListener() {
//                    @Override
//                    public void onComplete() {
//                        animationTimelineList.remove(timeline);
//                    }
//                });
//        animationTimelineList.add(timeline);
    }
    // endregion
}
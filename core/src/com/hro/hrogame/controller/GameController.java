package com.hro.hrogame.controller;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.HroGame;
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
import com.hro.hrogame.screen.GameScreen;
import com.hro.hrogame.screen.MenuScreen;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import com.hro.hrogame.timer.Task;
import com.hro.hrogame.timer.Timer;
import com.hro.hrogame.ui.EffectDialog;
import com.hro.hrogame.ui.EffectDialogListener;
import com.hro.hrogame.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.hro.hrogame.constants.StringConstants.*;

public class GameController {

    // region Static fields
    public static final float WAVE_LABEL_SCALE_MAX = ParametersConstants.FONT_SCALE * 4;

    public static final float GAME_PROGRESS_RATIO = 1;
    public static final float PLAYER_PROGRESS_RATIO = 0.5f;
    public static final float WAVE_TIMER_INTERVAL = 2;
    public static final float WAVE_TIMER_DELAY = 5;
    // endregion

    // region Instance fields
    private Random random = new Random();
    private HroGame game;
    private Skin skin;
    private GameStage stage;
    private Label goldLabel;
    private Label levelLabel;
    private Label waveLabel;
    private Button btnPause;
    private Button btnMusic;
    private Button btnSound;
    private ProgressBar xpBar;
    private EffectDialog effectDialog;

    private TweenManager tweenManager;
    private SoundController soundController;
    private EntityFactory entityFactory;
    private WaveController waveController;

    private Timer waveTimer;
    private BaseUnit baseUnit;
    private HashMap<Actor, Timeline> timelineMap;
    private ArrayList<GameObject> enemiesWaitList;
    private int playerGold;
    private float playerXP;
    private float accessXP = WaveController.INITIAL_WEIGHT;
    private int aliveUnitsQuantity;
    private boolean isPaused;
    // endregion

    // region C-tor
    public GameController(HroGame game) {
        this.game = game;
        this.skin = game.skin;
        this.stage = game.stage;
        this.tweenManager = game.tweenManager;
        this.soundController = game.soundController;
        init();
    }
    // endregion

    // region Init
    private void init() {
        waveController = new WaveController(WaveController.INITIAL_WEIGHT, GAME_PROGRESS_RATIO);
        entityFactory = new EntityFactory(skin, soundController);
        timelineMap = new HashMap<>();
        enemiesWaitList = new ArrayList<>();
        waveTimer = new Timer();

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
        addBackground();
        addEffectDialog();
        addGoldLabel();
        addLevelLabel();
        addXPBar();
        addWaveLabel();
        addPauseButton();
        addMusicButton();
        addSoundButton();
    }
    private void addBackground() {
        Image background = new Image(skin.getDrawable(StringConstants.BACKGROUND_DRAWABLE));
        background.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(background, LayerType.BACKGROUND);
    }
    private void addGoldLabel() {
        Image coin = new Image(skin.getDrawable(StringConstants.COIN_DRAWABLE));
        coin.setSize(ParametersConstants.COIN_DIAMETER, ParametersConstants.COIN_DIAMETER);
        coin.setPosition(coin.getWidth() / 2, stage.getHeight() - coin.getHeight() * 1.5f);
        goldLabel = new Label(" " + playerGold, skin);
        goldLabel.setX(coin.getWidth());
        goldLabel.setPosition(coin.getX() + coin.getWidth(),
                              coin.getY() - coin.getHeight() * (1 - ParametersConstants.FONT_SCALE));
        goldLabel.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);
        stage.addActor(coin, LayerType.GAME_MENU_UI);
        stage.addActor(goldLabel, LayerType.GAME_MENU_UI);

    }
    private void addLevelLabel() {
        levelLabel = new Label("Lvl: " + 1, skin);
        levelLabel.setPosition(goldLabel.getX() + Gdx.graphics.getWidth() / 10, goldLabel.getY());
        levelLabel.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);
        stage.addActor(levelLabel, LayerType.GAME_MENU_UI);
    }
    private void addXPBar() {
        Label xpLabel = new Label("XP: ", skin);
        xpLabel.setPosition(levelLabel.getX() + Gdx.graphics.getWidth() / 8, levelLabel.getY());
        xpLabel.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);

        xpBar = new ProgressBar(0, accessXP, 1, false, skin, StringConstants.XP_BAR);
        float width = Gdx.graphics.getWidth() / 4;
        xpBar.setSize(width, ParametersConstants.COIN_DIAMETER);
        xpBar.setPosition(xpLabel.getX() + stage.getWidth() / 20,
                          stage.getHeight() - xpBar.getHeight() * 1.5f);
        xpBar.setValue(0);
        xpBar.setAnimateDuration(0.2f);
        stage.addActor(xpLabel, LayerType.GAME_MENU_UI);
        stage.addActor(xpBar, LayerType.GAME_MENU_UI);
    }
    private void addWaveLabel() {
        waveLabel = new Label("Wave " + 1, skin);
        waveLabel.setPosition(xpBar.getX() + xpBar.getWidth() + waveLabel.getWidth(), levelLabel.getY());
        waveLabel.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);
        stage.addActor(waveLabel, LayerType.GAME_MENU_UI);
    }
    private void addPauseButton() {
        btnPause = new Button(skin, StringConstants.BTN_PAUSE);
        btnPause.setSize(ParametersConstants.BTN_DIAMETER, ParametersConstants.BTN_DIAMETER);
        btnPause.setPosition(stage.getWidth() - btnPause.getWidth(), stage.getHeight() - btnPause.getHeight(), Align.center);
        btnPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                btnPause.setTouchable(Touchable.disabled);
                soundController.play(SoundType.CLICK);
                if (isPaused) play();
                else pause();

                TweenAnimation.bounce(actor, tweenManager, new AnimationListener() {
                    @Override
                    public void onComplete() {
                        btnPause.setTouchable(Touchable.enabled);
                    }
                });
            }
        });
        stage.addActor(btnPause, LayerType.GAME_MENU_UI);
    }
    private void addMusicButton() {
        if (soundController.isMusicOn()) {
            btnMusic = new Button(skin, StringConstants.BTN_MUSIC_ON);
        } else {
            btnMusic = new Button(skin, StringConstants.BTN_MUSIC_OFF);
        }
        btnMusic.setSize(ParametersConstants.BTN_DIAMETER * 0.8f, ParametersConstants.BTN_DIAMETER * 0.8f);
        btnMusic.setPosition(stage.getWidth() - btnPause.getWidth(), btnPause.getY() - btnPause.getHeight(), Align.center);
        btnMusic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                btnMusic.setTouchable(Touchable.disabled);
                soundController.play(SoundType.CLICK);
                if (soundController.isMusicOn()) soundController.musicOff();
                else soundController.musicOn();
                TweenAnimation.bounce(actor, tweenManager, new AnimationListener() {
                    @Override
                    public void onComplete() {
                        btnMusic.setTouchable(Touchable.enabled);
                    }
                });
            }
        });
    }
    private void addSoundButton() {
        if (soundController.isSoundOn()) {
            btnSound = new Button(skin, StringConstants.BTN_SOUND_ON);
        } else {
            btnSound = new Button(skin, StringConstants.BTN_SOUND_OFF);
        }
        btnSound.setSize(ParametersConstants.BTN_DIAMETER * 0.8f, ParametersConstants.BTN_DIAMETER * 0.8f);
        btnSound.setPosition(stage.getWidth() - btnPause.getWidth(), btnMusic.getY() - btnSound.getHeight(), Align.center);
        btnSound.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                btnSound.setTouchable(Touchable.disabled);
                soundController.play(SoundType.CLICK);
                if (soundController.isSoundOn()) soundController.soundOff();
                else soundController.soundOn();
                TweenAnimation.bounce(actor, tweenManager, new AnimationListener() {
                    @Override
                    public void onComplete() {
                        btnSound.setTouchable(Touchable.enabled);
                    }
                });
            }
        });
    }
    private void addEffectDialog() {
        effectDialog = new EffectDialog(skin, stage);
        effectDialog.addEffectDialogListener(new EffectDialogListener() {
            @Override
            public void onItemBought(EffectType type, int price) {
                addEffectToBase(type);
                payForPurchase(price);
            }
        });
    }
    private void addLooseDialog() {
        Label labelRestart = new Label(RESTART_TITLE, skin);
        Label labelMainMenu = new Label(MAIN_MENU_TITLE, skin);
        Label labelQuit = new Label(QUIT_TITLE, skin);
        labelRestart.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);
        labelMainMenu.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);
        labelQuit.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);

        Button btnRestart = new Button(skin, StringConstants.BTN_RECTANGLE);
        Button btnMainMenu = new Button(skin, StringConstants.BTN_RECTANGLE);
        Button btnQuit = new Button(skin, StringConstants.BTN_RECTANGLE);
        btnRestart.add(labelRestart);
        btnMainMenu.add(labelMainMenu);
        btnQuit.add(labelQuit);
        btnRestart.setSize(ParametersConstants.MAIN_BUTTON_WIDTH, ParametersConstants.MAIN_BUTTON_HEIGHT);
        btnMainMenu.setSize(ParametersConstants.MAIN_BUTTON_WIDTH, ParametersConstants.MAIN_BUTTON_HEIGHT);
        btnQuit.setSize(ParametersConstants.MAIN_BUTTON_WIDTH, ParametersConstants.MAIN_BUTTON_HEIGHT);
        btnRestart.setPosition(stage.getWidth() / 2, stage.getHeight() - btnRestart.getHeight(), Align.center);
        btnMainMenu.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        btnQuit.setPosition(stage.getWidth() / 2, btnRestart.getHeight(), Align.center);

        btnRestart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundController.play(SoundType.CLICK);
                if (soundController.isMusicOn()) soundController.musicRestart();
                stage.clear();
                game.setScreen(new GameScreen(game));
            }
        });
        btnMainMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundController.play(SoundType.CLICK);
                stage.clear();
                game.setScreen(new MenuScreen(game));
            }
        });
        btnQuit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundController.play(SoundType.CLICK);
                Gdx.app.exit();
            }
        });

        stage.addActor(btnRestart, LayerType.MENU_UI);
        stage.addActor(btnMainMenu, LayerType.MENU_UI);
        stage.addActor(btnQuit, LayerType.MENU_UI);
    }
    // endregion

    // region Update
    public void update(float delta){
        tweenManager.update(delta);
        waveTimer.update(delta);
        if (playerXP >= accessXP) {
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
        baseUnit.setHealthBarLength(baseUnit.getWidth() * 2);
        baseUnit.addGameObjectAdapter(new GameObjectAdapter() {
            @Override
            public void onTakeDamage(float damage, GameObject damagedUnit) {
                pushOnTakeDamageTweenAnimation(damage, damagedUnit);
            }
            @Override
            public void onKill(GameObject dyingUnit, GameObject killerUnit) {
                playerXP += dyingUnit.getWeight() * PLAYER_PROGRESS_RATIO;
                xpBar.setValue(playerXP);

                int gold = dyingUnit.getReward();
                earnGold(gold);
                pushOnDieTweenAnimation(gold, dyingUnit);
            }
            @Override
            public void onLevelUp(GameObject gameObject, int level) {
                levelLabel.setText("Lvl: " + level);
            }
            @Override
            public void onDie(GameObject dyingUnit, GameObject killerUnit) {
                pause();
                stage.setAlpha(0.5f);
                stage.setTouchable(Touchable.disabled, LayerType.BACKGROUND,
                                                       LayerType.FOREGROUND,
                                                       LayerType.GAME_UI,
                                                       LayerType.GAME_MENU_UI);
                addLooseDialog();
            }
        });
        stage.addActor(baseUnit, LayerType.FOREGROUND);
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Actor actor = event.getTarget();
                if (actor instanceof TankUnit || actor instanceof RamUnit) {
                    GameObject target = (GameObject) actor;
                    if (!target.isDead()) {
                        SimpleCannonEffect effect = (SimpleCannonEffect) baseUnit.getEffect(EffectType.SIMPLE_CANNON);
                        effect.setTarget(target);
                        animateOnTargetSelect();
                    }
                }
            }
        });
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
        float margin = Gdx.graphics.getWidth() / 10;
        switch (side) {
            case 0:
                x = -margin;
                y = random.nextInt((int) stage.getHeight());
                return new Point(x, y);
            case 1:
                x = random.nextInt((int) stage.getWidth());
                y = stage.getHeight() + margin;
                return new Point(x, y);
            case 2:
                x = stage.getWidth() + margin;
                y = random.nextInt((int) stage.getHeight());
                return new Point(x, y);
            case 3:
                x = random.nextInt((int) stage.getWidth());
                y = -margin;
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
        soundController.musicResume();
        for (Timeline timeline : timelineMap.values()) timeline.resume();
        stage.play();
        isPaused = false;
        btnMusic.remove();
        btnSound.remove();
    }
    private void pause() {
        waveTimer.pause();
        soundController.musicPause();
        for (Timeline timeline : timelineMap.values()) timeline.pause();
        stage.pause();
        isPaused = true;
        stage.addActor(btnMusic,LayerType.GAME_MENU_UI);
        stage.addActor(btnSound,LayerType.GAME_MENU_UI);
    }
    // endregion

    // region Animation
    private void pushOnTakeDamageTweenAnimation(float damage, GameObject damagedUnit) {
        String text = "-" + (int) damage;
        final Label label = new Label(text, skin);
        label.setPosition(damagedUnit.getX() + damagedUnit.getWidth(), damagedUnit.getY());
        label.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);
        Timeline timeline = TweenAnimation.pop_up(label, TweenAnimation.POP_UP_DURATION,
                                                TweenAnimation.POP_UP_MOVE_TARGET,
                                                TweenAnimation.POP_UP_VANISH_TARGET,
                                                tweenManager, new AnimationListener() {
                                                                  @Override
                                                                  public void onComplete() {
                                                                      label.remove();
                                                                      timelineMap.remove(label);
                                                                  }
                                                              });
        timelineMap.put(label, timeline);
        stage.addActor(label, LayerType.GAME_UI);
    }
    private void pushOnDieTweenAnimation(int gold, GameObject damagedUnit) {
        Image coin = new Image(skin.getDrawable(StringConstants.COIN_DRAWABLE));
        coin.setSize(ParametersConstants.COIN_DIAMETER, ParametersConstants.COIN_DIAMETER);
        final Label label = new Label(" " + gold, skin);
        label.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);
        label.setX(coin.getWidth());
        final Group reward = new Group();
        reward.setTransform(false);
        reward.setPosition(damagedUnit.getX() + damagedUnit.getWidth(), damagedUnit.getY() + damagedUnit.getHeight());
        reward.addActor(coin);
        reward.addActor(label);
        Timeline timeline = TweenAnimation.pop_up(reward, TweenAnimation.POP_UP_DURATION,
                                                 TweenAnimation.POP_UP_MOVE_TARGET,
                                                 TweenAnimation.POP_UP_VANISH_TARGET,
                                                 tweenManager, new AnimationListener() {
                                                                   @Override
                                                                   public void onComplete() {
                                                                       reward.remove();
                                                                       timelineMap.remove(reward);
                                                                   }
                                                               });
        timelineMap.put(reward, timeline);
        stage.addActor(reward, LayerType.GAME_UI);
    }
    private void animateLabelOnWaveChange() {
        waveLabel.setFontScale(WAVE_LABEL_SCALE_MAX);
        float moveTargetX = waveLabel.getX();
        float moveTargetY = waveLabel.getY();
        waveLabel.setPosition(stage.getWidth() / 2 - waveLabel.getWidth() / 2 * WAVE_LABEL_SCALE_MAX,
                              stage.getHeight() / 2 - waveLabel.getHeight() / 2 * WAVE_LABEL_SCALE_MAX);
        Timeline timeline = TweenAnimation.animateWaveLabel(waveLabel, 5,
                moveTargetX, moveTargetY, ParametersConstants.FONT_SCALE, tweenManager, new AnimationListener() {
                    @Override
                    public void onComplete() {
                        timelineMap.remove(waveLabel);
                    }
                });
        timelineMap.put(waveLabel, timeline);
    }
    private void animateOnTargetSelect() {
        final Label label = new Label("Target changed", skin);
        label.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        Timeline timeline = TweenAnimation.vanish(label, 3, 0, tweenManager,
                                                    new AnimationListener() {
                                                        @Override
                                                        public void onComplete() {
                                                            label.remove();
                                                            timelineMap.remove(label);
                                                        }
                                                    });
        timelineMap.put(label, timeline);
        stage.addActor(label, LayerType.GAME_MENU_UI);
    }
    // endregion
}
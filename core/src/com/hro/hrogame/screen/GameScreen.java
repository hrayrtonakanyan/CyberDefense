package com.hro.hrogame.screen;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.HroGame;
import com.hro.hrogame.animation.tweenanimation.TweenAnimation;
import com.hro.hrogame.constants.StringConstants;
import com.hro.hrogame.controller.GameController;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import com.hro.hrogame.utils.Util;

public class GameScreen extends ScreenAdapter {

    // region Instance fields
    private HroGame game;
    private GameStage stage;
    private TweenManager tweenManager;
    private GameController gameController;
    private boolean isPaused;
    private Label goldLabel;
    private int playerGold;
    private Label waveLabel;
    private int waveNumber;
    private Timeline waveAnimationTimeline = null;
    // endregion

    // region C-tor
    public GameScreen(HroGame game) {
        this.game = game;
        this.stage = game.stage;
        this.tweenManager = game.tweenManager;
        gameController = new GameController(this.stage, this.tweenManager);
    }
    // endregion

    // region Show
    @Override
    public void show() {
        createGoldLabel();
        createWaveLabel();
        createPlayPauseButtons();
    }
    // endregion

    // region Init
    private void createGoldLabel() {
        Image coin = new Image(new Texture("coin.png"));
        coin.setSize(20, 20);
        goldLabel = new Label(" " + gameController.getPlayerGold(), StringConstants.skin);
        coin.setPosition(coin.getWidth(), stage.getHeight() - coin.getHeight() * 2);
        goldLabel.setPosition(coin.getX() + coin.getWidth(), coin.getY());
        stage.addActor(coin, LayerType.MENU_UI);
        stage.addActor(goldLabel, LayerType.MENU_UI);
    }
    private void createWaveLabel() {
        waveLabel = new Label("Wave " + gameController.getWaveNumber(), StringConstants.skin);
        waveLabel.setPosition(stage.getWidth() / 2, stage.getHeight() - waveLabel.getHeight(), Align.center);
        stage.addActor(waveLabel, LayerType.MENU_UI);
    }
    private void createPlayPauseButtons() {
        // TODO: 9/8/2017 Handle timers work on pause
        Image playImage = new Image(new Texture("play.png"));
        Image pauseImage = new Image(new Texture("pause.png"));
        Button.ButtonStyle btnStyle = new Button.ButtonStyle(pauseImage.getDrawable(), playImage.getDrawable(), playImage.getDrawable());
        Button playPauseBtn = new Button(btnStyle);
        playPauseBtn.setSize(30, 30);
        playPauseBtn.setPosition(stage.getWidth() - playPauseBtn.getWidth(), stage.getHeight() - playPauseBtn.getHeight(), Align.center);
        playPauseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (isPaused) {
                    gameController.play();
                    waveAnimationTimeline.resume();
                    isPaused = false;
                }
                else {
                    gameController.pause();
                    waveAnimationTimeline.pause();
                    isPaused = true;
                }
            }
        });
        stage.addActor(playPauseBtn, LayerType.MENU_UI);
    }
    // endregion

    // region Render
    @Override
    public void render(float delta) {
        Util.cleanScreen();
        stage.act();
        update(delta);
        stage.draw();
    }
    private void update(float delta) {
        gameController.update(delta);
        tweenManager.update(delta);
        updateGoldInfo();
        updateWaveInfo();
    }
    private void updateWaveInfo() {
        int number = gameController.getWaveNumber();
        if (waveNumber == number) return;
        waveNumber = number;
        waveLabel.setText("Wave " + waveNumber);
        animateLabelOnWaveChange();
    }
    private void updateGoldInfo() {
        int gold = gameController.getPlayerGold();
        if (playerGold == gold) return;
        playerGold = gold;
        goldLabel.setText(" " + gameController.getPlayerGold());
    }
    // endregion

    // region Animation
    private void animateLabelOnWaveChange() {
        waveLabel.setFontScale(3);
        waveLabel.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        float moveTarget = Gdx.graphics.getHeight() - waveLabel.getHeight() * 2;
        waveAnimationTimeline = TweenAnimation.animateWaveLabel(waveLabel, 5,
                moveTarget, 1, tweenManager, null);
    }
    // endregion
}

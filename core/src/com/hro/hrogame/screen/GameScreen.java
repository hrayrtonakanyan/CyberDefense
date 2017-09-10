package com.hro.hrogame.screen;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.HroGame;
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

    private Label goldLabel;
    private Label waveLabel;
    private boolean isPaused;
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
        Button.ButtonStyle btnStyle = new Button.ButtonStyle(pauseImage.getDrawable(), playImage.getDrawable(), null);
        Button playPauseBtn = new Button(btnStyle);
        playPauseBtn.setSize(30, 30);
        playPauseBtn.setPosition(stage.getWidth() - playPauseBtn.getWidth(), stage.getHeight() - playPauseBtn.getHeight(), Align.center);
        playPauseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isPaused = !isPaused;
            }
        });
        stage.addActor(playPauseBtn, LayerType.MENU_UI);
    }
    // endregion

    // region Render
    @Override
    public void render(float delta) {
        if (!isPaused) {
            Util.cleanScreen();
            stage.act();
            update(delta);
            stage.draw();
        }
    }
    private void update(float delta) {
        tweenManager.update(delta);
        gameController.update();
        waveLabel.setText("Wave " + gameController.getWaveNumber());
        goldLabel.setText(" " + gameController.getPlayerGold());
    }
    // endregion
}

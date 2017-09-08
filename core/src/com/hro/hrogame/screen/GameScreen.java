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

    Label waveLabel;
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
        createPlayPauseButtons();
        createLabels();
    }
    // endregion

    // region Init
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
    private void createLabels() {
        waveLabel = new Label("Wave " + gameController.getWaveNumber(), StringConstants.skin);
        waveLabel.setPosition(waveLabel.getWidth() / 2 + 20, stage.getHeight() - waveLabel.getHeight(), Align.center);
        stage.addActor(waveLabel, LayerType.MENU_UI);
    }
    // endregion

    // region Render
    @Override
    public void render(float delta) {
        if (!isPaused) {
            Util.cleanScreen();
            stage.act();
            tweenManager.update(delta);
            gameController.update();
            waveLabel.setText("Wave " + gameController.getWaveNumber());
            stage.draw();
        }
    }
    // endregion
}

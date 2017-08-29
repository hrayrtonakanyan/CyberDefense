package com.hro.hrogame.screen;

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
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import com.hro.hrogame.utils.Util;

import static com.hro.hrogame.constants.StringConstants.*;

public class MenuScreen extends ScreenAdapter {

    // region Instance fields
    private HroGame game;
    private TweenManager tweenManager;
    private GameStage stage;
    private Button btnPlay;
    private Button btnQuit;
    private Button.ButtonStyle btnStyle;
    private Label playButtonLabel;
    private Label quitButtonLabel;
    // endregion

    // region C-tor
    public MenuScreen(HroGame game) {
        this.game = game;
        this.stage = game.stage;
        this.tweenManager = game.tweenManager;
    }
    // endregion

    // region Show and init
    @Override
    public void show() {
        initButtonStyle();
        initButtonsLabels();

        btnPlay = new Button(btnStyle);
        btnQuit = new Button(btnStyle);
        btnPlay.add(playButtonLabel);
        btnQuit.add(quitButtonLabel);
        btnPlay.setPosition(stage.getWidth() / 2, stage.getHeight() * 2 / 3, Align.center);
        btnQuit.setPosition(stage.getWidth() / 2, stage.getHeight() / 3, Align.center);
        stage.addActor(btnPlay, LayerType.MENU_UI);
        stage.addActor(btnQuit, LayerType.MENU_UI);

        addButtonListeners();
    }
    private void initButtonStyle() {
        Image btnUnpressed = new Image(new Texture(BUTTON_UNPRESSED));
        Image btnPressed = new Image(new Texture(BUTTON_PRESSED));
        btnStyle = new Button.ButtonStyle(btnUnpressed.getDrawable(), btnPressed.getDrawable(), null);
    }
    private void initButtonsLabels() {
        playButtonLabel = new Label(PLAY_TITLE, skin);
        quitButtonLabel = new Label(QUIT_TITLE, skin);
    }
    private void addButtonListeners() {
        btnPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.clear();
                game.setScreen(new GameScreen(game));
            }
        });
        btnQuit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
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
}

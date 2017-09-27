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
import com.hro.hrogame.animation.tweenanimation.TweenAnimation;
import com.hro.hrogame.controller.SoundController;
import com.hro.hrogame.controller.SoundType;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import com.hro.hrogame.utils.Util;

import static com.hro.hrogame.constants.StringConstants.*;

public class MenuScreen extends ScreenAdapter {

    // region Static fields
    public static final float MAIN_BUTTON_WIDTH = Gdx.graphics.getWidth() / 5;
    public static final float MAIN_BUTTON_HEIGHT = Gdx.graphics.getHeight() / 5;
    public static final float FONT_SCALE = 2;
    // endregion

    // region Instance fields
    private HroGame game;
    private GameStage stage;
    private TweenManager tweenManager;
    private SoundController soundController;
    // endregion

    // region C-tor
    public MenuScreen(HroGame game) {
        this.game = game;
        this.stage = game.stage;
        this.tweenManager = game.tweenManager;
        this.soundController = game.soundController;
    }
    // endregion

    // region Lifecycle
    @Override
    public void show() {
        soundController.musicOn();

        Image background = new Image(new Texture("background.png"));
        background.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(background, LayerType.BACKGROUND);

        createMainButtons();
        createMusicButton();
        createSoundButton();
    }

    @Override
    public void render(float delta) {
        Util.cleanScreen();
        stage.act();
        tweenManager.update(delta);
        stage.draw();
    }
    // endregion

    // region Create
    private void createMainButtons() {
        Image btnUnpressed = new Image(new Texture(BUTTON_UNPRESSED));
        Image btnPressed = new Image(new Texture(BUTTON_PRESSED));
        Button.ButtonStyle btnStyle = new Button.ButtonStyle(btnUnpressed.getDrawable(), btnPressed.getDrawable(), null);

        Label playButtonLabel = new Label(PLAY_TITLE, skin);
        Label quitButtonLabel = new Label(QUIT_TITLE, skin);
        playButtonLabel.setFontScale(FONT_SCALE, FONT_SCALE);
        quitButtonLabel.setFontScale(FONT_SCALE, FONT_SCALE);

        Button btnPlay = new Button(btnStyle);
        Button btnQuit = new Button(btnStyle);
        btnPlay.add(playButtonLabel);
        btnQuit.add(quitButtonLabel);
        btnPlay.setSize(MAIN_BUTTON_WIDTH, MAIN_BUTTON_HEIGHT);
        btnQuit.setSize(MAIN_BUTTON_WIDTH, MAIN_BUTTON_HEIGHT);
        btnPlay.setPosition(stage.getWidth() / 2, stage.getHeight() * 2 / 3, Align.center);
        btnQuit.setPosition(stage.getWidth() / 2, stage.getHeight() / 3, Align.center);

        stage.addActor(btnPlay, LayerType.MENU_UI);
        stage.addActor(btnQuit, LayerType.MENU_UI);

        btnPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundController.play(SoundType.CLICK);
                stage.clear();
                game.setScreen(new GameScreen(game));
            }
        });
        btnQuit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundController.play(SoundType.CLICK);
                Gdx.app.exit();
            }
        });
    }
    private void createMusicButton() {
        Image imageOn = new Image(new Texture(BTN_MUSIC_ON));
        Image imageOff = new Image(new Texture(BTN_MUSIC_OFF));
        Button.ButtonStyle btnStyle = new Button.ButtonStyle(imageOff.getDrawable(), imageOn.getDrawable(), imageOn.getDrawable());
        Button btn = new Button(btnStyle);
        btn.setSize(BTN_DIAMETER, BTN_DIAMETER);
        btn.setPosition(stage.getWidth() - btn.getWidth(), btn.getHeight(), Align.center);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundController.play(SoundType.CLICK);
                if (soundController.isMusicOn()) soundController.musicOff();
                else soundController.musicOn();
                TweenAnimation.bounce(actor, tweenManager, null);
            }
        });
        stage.addActor(btn, LayerType.MENU_UI);
    }
    private void createSoundButton() {
        Image imageOn = new Image(new Texture(BTN_SOUND_ON));
        Image imageOff = new Image(new Texture(BTN_SOUND_OFF));
        Button.ButtonStyle btnStyle = new Button.ButtonStyle(imageOff.getDrawable(), imageOn.getDrawable(), imageOn.getDrawable());
        Button btn = new Button(btnStyle);
        btn.setSize(BTN_DIAMETER, BTN_DIAMETER);
        btn.setPosition(stage.getWidth() - btn.getWidth(), btn.getHeight() * 3, Align.center);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundController.play(SoundType.CLICK);
                if (soundController.isSoundOn()) soundController.soundOff();
                else soundController.soundOn();
                TweenAnimation.bounce(actor, tweenManager, null);
            }
        });
        stage.addActor(btn, LayerType.MENU_UI);
    }
    // endregion
}

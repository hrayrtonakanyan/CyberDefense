package com.hro.hrogame.screen;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.HroGame;
import com.hro.hrogame.animation.particleanimation.AnimationListener;
import com.hro.hrogame.animation.tweenanimation.TweenAnimation;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.constants.StringConstants;
import com.hro.hrogame.controller.SoundController;
import com.hro.hrogame.controller.SoundType;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;
import com.hro.hrogame.utils.Util;

public class MenuScreen extends ScreenAdapter {

    // region Instance fields
    private GameStage stage;
    private Skin skin;
    private HroGame game;
    private TweenManager tweenManager;
    private SoundController soundController;
    // endregion

    // region C-tor
    public MenuScreen(HroGame game) {
        this.game = game;
        this.skin = game.skin;
        this.stage = game.stage;
        this.tweenManager = game.tweenManager;
        this.soundController = game.soundController;
    }
    // endregion

    // region Lifecycle
    @Override
    public void show() {
        if (soundController.isMusicOn()) soundController.musicOn();
        addBackground();
        addMainButtons();
        addMusicButton();
        addSoundButton();
    }

    @Override
    public void render(float delta) {
        Util.cleanScreen();
        stage.act();
        tweenManager.update(delta);
        stage.draw();
    }
    // endregion

    // region Add UI
    private void addBackground() {
        Image background = new Image(skin.getDrawable(StringConstants.BACKGROUND_DRAWABLE));
        background.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(background, LayerType.BACKGROUND);
    }
    private void addMainButtons() {
        Label playButtonLabel = new Label(StringConstants.PLAY_TITLE, skin);
        Label quitButtonLabel = new Label(StringConstants.QUIT_TITLE, skin);
        playButtonLabel.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);
        quitButtonLabel.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);

        Button btnPlay = new Button(skin, StringConstants.BTN_RECTANGLE);
        Button btnQuit = new Button(skin, StringConstants.BTN_RECTANGLE);
        btnPlay.add(playButtonLabel);
        btnQuit.add(quitButtonLabel);
        btnPlay.setSize(ParametersConstants.MAIN_BUTTON_WIDTH, ParametersConstants.MAIN_BUTTON_HEIGHT);
        btnQuit.setSize(ParametersConstants.MAIN_BUTTON_WIDTH, ParametersConstants.MAIN_BUTTON_HEIGHT);
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
    private void addMusicButton() {
        final Button btn;
        if (soundController.isMusicOn()) {
            btn = new Button(skin, StringConstants.BTN_MUSIC_ON);
        } else {
            btn = new Button(skin, StringConstants.BTN_MUSIC_OFF);
        }
        btn.setSize(ParametersConstants.BTN_DIAMETER, ParametersConstants.BTN_DIAMETER);
        btn.setPosition(stage.getWidth() - btn.getWidth(), btn.getHeight(), Align.center);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                btn.setTouchable(Touchable.disabled);
                soundController.play(SoundType.CLICK);
                if (soundController.isMusicOn()) soundController.musicOff();
                else soundController.musicOn();
                TweenAnimation.bounce(actor, tweenManager, new AnimationListener() {
                    @Override
                    public void onComplete() {
                        btn.setTouchable(Touchable.enabled);
                    }
                });
            }
        });
        stage.addActor(btn, LayerType.MENU_UI);
    }
    private void addSoundButton() {
        final Button btn;
        if (soundController.isSoundOn()) {
            btn = new Button(skin, StringConstants.BTN_SOUND_ON);
        } else {
            btn = new Button(skin, StringConstants.BTN_SOUND_OFF);
        }
        btn.setSize(ParametersConstants.BTN_DIAMETER, ParametersConstants.BTN_DIAMETER);
        btn.setPosition(stage.getWidth() - btn.getWidth(), btn.getHeight() * 3, Align.center);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                btn.setTouchable(Touchable.disabled);
                soundController.play(SoundType.CLICK);
                if (soundController.isSoundOn()) soundController.soundOff();
                else soundController.soundOn();
                TweenAnimation.bounce(actor, tweenManager, new AnimationListener() {
                    @Override
                    public void onComplete() {
                        btn.setTouchable(Touchable.enabled);
                    }
                });
            }
        });
        stage.addActor(btn, LayerType.MENU_UI);
    }
    // endregion
}

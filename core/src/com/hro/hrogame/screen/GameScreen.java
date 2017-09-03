package com.hro.hrogame.screen;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.ScreenAdapter;
import com.hro.hrogame.HroGame;
import com.hro.hrogame.controller.GameController;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.utils.Util;

public class GameScreen extends ScreenAdapter {

    // region Instance fields
    private HroGame game;
    private GameStage stage;
    private TweenManager tweenManager;
    private GameController gameController;
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

    }
    // endregion

    // region Render
    @Override
    public void render(float delta) {
        Util.cleanScreen();
        stage.act();
        tweenManager.update(delta);
        gameController.update(delta);
        stage.draw();
    }
    // endregion
}

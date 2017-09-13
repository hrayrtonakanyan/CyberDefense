package com.hro.hrogame.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.hro.hrogame.HroGame;
import com.hro.hrogame.controller.GameController;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.utils.Util;

public class GameScreen extends ScreenAdapter {

    // region Instance fields
    private HroGame game;
    private GameStage stage;
    private GameController gameController;
    // endregion

    // region C-tor
    public GameScreen(HroGame game) {
        this.game = game;
        this.stage = game.stage;
        gameController = new GameController(this.stage, game.tweenManager);
    }
    // endregion

    // region Render
    @Override
    public void render(float delta) {
        Util.cleanScreen();
        stage.act();
        gameController.update(delta);
        stage.draw();
    }
    // endregion
}

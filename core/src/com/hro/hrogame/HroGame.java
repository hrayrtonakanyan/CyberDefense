package com.hro.hrogame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.hro.hrogame.screen.MenuScreen;
import com.hro.hrogame.stage.GameStage;

public class HroGame extends Game {

	public GameStage stage;
	
	@Override
	public void create () {
		stage = new GameStage();
		Gdx.input.setInputProcessor(stage);
		setScreen(new MenuScreen(this));
	}
}

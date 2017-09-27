package com.hro.hrogame;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.hro.hrogame.animation.tweenanimation.ActorAccessor;
import com.hro.hrogame.controller.SoundController;
import com.hro.hrogame.screen.MenuScreen;
import com.hro.hrogame.stage.GameStage;

public class HroGame extends Game {

	public GameStage stage;
	public TweenManager tweenManager;
	public SoundController soundController;
	
	@Override
	public void create () {
		stage = new GameStage();
		tweenManager = new TweenManager();
		soundController = new SoundController();
		Gdx.input.setInputProcessor(stage);
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		setScreen(new MenuScreen(this));
	}
}

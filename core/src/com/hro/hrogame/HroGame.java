package com.hro.hrogame;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hro.hrogame.animation.tweenanimation.ActorAccessor;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.constants.StringConstants;
import com.hro.hrogame.controller.SoundController;
import com.hro.hrogame.screen.MenuScreen;
import com.hro.hrogame.stage.GameStage;

public class HroGame extends Game {

	// region Instance fields
	public Skin skin;
	public GameStage stage;
	public TweenManager tweenManager;
	public SoundController soundController;
	// endregion

	// region Create
	@Override
	public void create () {
		initSkin();
		stage = new GameStage();
		tweenManager = new TweenManager();
		soundController = new SoundController();
		Gdx.input.setInputProcessor(stage);
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		setScreen(new MenuScreen(this));
	}
	// endregion

	// region Skin
	private void initSkin() {
		skin = new Skin(Gdx.files.internal("skin.json"));
		adjustLabelStyle();
		adjustProgressBarStyle(StringConstants.HEALTH_BAR, Gdx.graphics.getHeight() / 100);
		adjustProgressBarStyle(StringConstants.XP_BAR, (int) ParametersConstants.COIN_DIAMETER);
	}
	private void adjustLabelStyle() {
		Label.LabelStyle labelStyle = skin.get(Label.LabelStyle.class);
		labelStyle.font.getData().ascent = labelStyle.font.getData().ascent * -1.58f;
		labelStyle.font.getData().descent = 0;
	}
	private void adjustProgressBarStyle(String type, int height) {
		ProgressBar.ProgressBarStyle style = skin.get(type, ProgressBar.ProgressBarStyle.class);

		Pixmap backgroundPixmap = new Pixmap(10, height, Pixmap.Format.RGB888);
		if (type.equals(StringConstants.HEALTH_BAR)) backgroundPixmap.setColor(Color.RED);
		else backgroundPixmap.setColor(Color.ORANGE);
        backgroundPixmap.fill();
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(backgroundPixmap)));
        backgroundPixmap.dispose();

//		Drawable background = skin.getDrawable("red");
//		background.setMinHeight(height);

		Pixmap knobPixmap = new Pixmap(0, height, Pixmap.Format.RGB888);
		if (type.equals(StringConstants.HEALTH_BAR)) knobPixmap.setColor(Color.GREEN);
		else knobPixmap.setColor(Color.BROWN);
        knobPixmap.fill();
        TextureRegionDrawable knobDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(knobPixmap)));
        knobPixmap.dispose();

//        Drawable knob = skin.getDrawable("green");
//        knob.setLeftWidth(0);
//        knob.setRightWidth(0);
//        knob.setMinHeight(height);

		Pixmap knobBeforePixmap = new Pixmap(10, height, Pixmap.Format.RGB888);
		if (type.equals(StringConstants.HEALTH_BAR)) knobBeforePixmap.setColor(Color.GREEN);
		else knobBeforePixmap.setColor(Color.BROWN);
        knobBeforePixmap.fill();
        TextureRegionDrawable knobBeforeDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(knobBeforePixmap)));
        knobBeforePixmap.dispose();

//        Drawable knobBefore = skin.getDrawable("green");
//        knobBefore.setMinHeight(height);

		style.background = backgroundDrawable;
		style.knob = knobDrawable;
		style.knobBefore = knobBeforeDrawable;
	}
	// endregion
}

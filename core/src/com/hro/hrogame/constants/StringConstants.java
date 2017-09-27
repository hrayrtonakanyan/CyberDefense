package com.hro.hrogame.constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class StringConstants {

    public static final  Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public static final String BACKGROUND = "background.png";

    public static final String PLAY_TITLE = "PLAY";
    public static final String QUIT_TITLE = "QUIT";
    public static final String BUTTON_UNPRESSED = "btn_unpressed.png";
    public static final String BUTTON_PRESSED = "btn_pressed.png";
    public static final String BTN_MUSIC_ON = "music_on.png";
    public static final String BTN_MUSIC_OFF = "music_off.png";
    public static final String BTN_SOUND_ON = "sound_on.png";
    public static final String BTN_SOUND_OFF = "sound_off.png";

    public static final String BTN_PLAY = "play.png";
    public static final String BTN_PAUSE = "pause.png";

    public static final float GOLD_LABEL_COIN_DIAMETER = Gdx.graphics.getWidth() / 35;
    public static final float BTN_DIAMETER = Gdx.graphics.getHeight() / 16;

}

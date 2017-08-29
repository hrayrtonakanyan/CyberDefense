package com.hro.hrogame.constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Lion on 8/14/17.
 */
public class StringConstants {

    public static final  Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public static final String PLAY_TITLE = "PLAY";
    public static final String QUIT_TITLE = "QUIT";
    public static final String BUTTON_UNPRESSED = "btn_unpressed.png";
    public static final String BUTTON_PRESSED = "btn_pressed.png";

}

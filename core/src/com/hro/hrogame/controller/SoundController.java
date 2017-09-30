package com.hro.hrogame.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundController {

    // region Instance fields
    private boolean isSoundOn = true;
    private boolean isMusicOn = true;
    private Sound backgroundMusic;
    private Sound clickSound;
    private Sound simpleCannonSound;
    private Sound simpleCannonBallSound;
    private Sound hardCannonSound;
    private Sound hardCannonBallSound;
    private Sound freezerSound;
    private Sound stunnerSound;
    private Sound hellFireSound;
    // endregion

    // region Init
    {
        clickSound = Gdx.audio.newSound(Gdx.files.internal("click_sound.wav"));
        backgroundMusic = Gdx.audio.newSound(Gdx.files.internal("background_music.mp3"));
        simpleCannonSound = Gdx.audio.newSound(Gdx.files.internal("simple_cannon_sound.mp3"));
        simpleCannonBallSound = Gdx.audio.newSound(Gdx.files.internal("simple_cannonball_sound.wav"));
        hardCannonSound = Gdx.audio.newSound(Gdx.files.internal("hard_cannon_sound.wav"));
        hardCannonBallSound = Gdx.audio.newSound(Gdx.files.internal("hard_cannonball_sound.wav"));
        freezerSound = Gdx.audio.newSound(Gdx.files.internal("freezer_sound.wav"));
        stunnerSound = Gdx.audio.newSound(Gdx.files.internal("stunner_sound.wav"));
        hellFireSound = Gdx.audio.newSound(Gdx.files.internal("hell_fire_sound.wav"));
    }
    // endregion

    // region PLay
    public void play(SoundType type) {
        if (!isSoundOn) return;
        switch (type) {
            case CLICK:
                clickSound.play();
                break;
            case SIMPLE_CANNON:
                simpleCannonSound.play();
                break;
            case SIMPLE_CANNONBALL:
                simpleCannonBallSound.play();
                break;
            case HARD_CANNON:
                hardCannonSound.play();
                break;
            case HARD_CANNONBALL:
                hardCannonBallSound.play();
                break;
            case FREEZER:
                freezerSound.play();
                break;
            case STUNNER:
                stunnerSound.play();
                break;
            case HELL_FIRE:
                hellFireSound.play();
                break;
            default: throw new RuntimeException("Sound type is not initialize.");
        }
    }
    // endregion

    // region Enable
    public void soundOn() {
        isSoundOn = true;
    }
    public void soundOff() {
        isSoundOn = false;
    }
    public void musicOn() {
        isMusicOn = true;
        backgroundMusic.play();
        backgroundMusic.loop();
    }
    public void musicOff() {
        isMusicOn = false;
        backgroundMusic.stop();
    }
    public void musicRestart() {
        musicOff();
        musicOn();
    }
    public void musicResume() {
        backgroundMusic.resume();
    }
    public void musicPause() {
        backgroundMusic.pause();
    }
    // endregion

    // region Getter
    public boolean isSoundOn() {
        return isSoundOn;
    }
    public boolean isMusicOn() {
        return isMusicOn;
    }
    // endregion
}

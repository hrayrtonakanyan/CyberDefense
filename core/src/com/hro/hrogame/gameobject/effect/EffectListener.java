package com.hro.hrogame.gameobject.effect;

/**
 * Created by Lion on 8/15/17.
 */
public interface EffectListener {

    void onReady();
    void onExecute();
    void onDisable();
    void onEnable();


}

package com.hro.hrogame.ui;

import com.hro.hrogame.gameobject.effect.EffectType;

public interface EffectDialogListener {

    void onItemBought(EffectType type, int price);
}

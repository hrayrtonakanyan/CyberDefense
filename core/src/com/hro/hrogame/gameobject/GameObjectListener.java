package com.hro.hrogame.gameobject;

public interface GameObjectListener {

    void onTakeDamage(float damage, GameObject damagedUnit);
    void onDie(GameObject dyingUnit, GameObject killerUnit);
    void onKill(GameObject dyingUnit, GameObject killerUnit);
    void onPlayerTypeChange(GameObject gameObject, PlayerRace oldPlayerType);
    void onPositionChange(GameObject gameObject);
    void onSizeChange(GameObject gameObject);
    void onDestinationArrive(GameObject gameObject);
}

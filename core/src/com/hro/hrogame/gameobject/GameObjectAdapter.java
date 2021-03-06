package com.hro.hrogame.gameobject;

public abstract class GameObjectAdapter implements GameObjectListener {

    @Override
    public void onTakeDamage(float damage, GameObject damagedUnit) {

    }

    @Override
    public void onDie(GameObject dyingUnit, GameObject killerUnit) {

    }

    @Override
    public void onKill(GameObject dyingUnit, GameObject killerUnit) {

    }

    @Override
    public void onPlayerTypeChange(GameObject gameObject, PlayerRace oldPlayerType) {

    }

    @Override
    public void onPositionChange(GameObject gameObject) {

    }

    @Override
    public void onDestinationArrive(GameObject gameObject) {

    }

    @Override
    public void onSizeChange(GameObject gameObject) {

    }

    @Override
    public void onLevelUp(GameObject gameObject, int level) {

    }
}

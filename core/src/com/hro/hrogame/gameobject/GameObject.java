package com.hro.hrogame.gameobject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.effect.EffectListener;
import com.hro.hrogame.primitives.Point;
import com.hro.hrogame.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lion on 8/14/17.
 */
public abstract class GameObject extends Entity {

    // region Instance fields
    private GameObjectData data;
    private PlayerRace playerType = PlayerRace.NONE;
    private Image appearance;
    private ArrayList<GameObjectAdapter> gameObjectAdapterList = new ArrayList<>();
    private ArrayList<Effect> effectList = new ArrayList<>();
    private Point destination;
    private float currentHealth;
    private float currentSpeed;
    private boolean isMoving;
    protected boolean isEnable = true;
    // endregion

    // region C-tor
    public GameObject() {
        data = new GameObjectData();
    }
    public GameObject(GameObjectData data) {
        initWithData(data);
        if (data.health == 0) throw new RuntimeException("Health of the game object must have a positive value if game object data is received on instantiation.");
    }
    // endregion

    // region Init
    private void initWithData(GameObjectData data) {
        this.data = data;
        initCurrentParams(data);
        setAppearance(data.texturePath);
    }
    private void initCurrentParams(GameObjectData data) {
        currentHealth = data.health;
        currentSpeed = data.speed;
    }
    // endregion

    // region Act
    @Override
    public void act(float delta) {
        super.act(delta);
        if (revealPlayerType() == PlayerRace.NONE) throw new RuntimeException("playerType of the game object must not be NONE when add to stage.");
        if (!isEnable) return;
        if (destination == null) return;
        if (isMoving) {
            if (isAtDestination()) return;
            moveToDestination(delta);
            if (Util.calculateDistance(destination, this) < currentSpeed * delta) {
                setPosition(destination.x, destination.y, Align.center);
                isMoving = false;
                notifyDestinationArrive();
            }
        }
    }
    // endregion

    // region Update
    private void updateAppearance() {
        if (appearance == null) return;
        appearance.setSize(getWidth(), getHeight());
        appearance.setRotation(getRotation());
        appearance.setOrigin(getOriginX(), getOriginY());
        appearance.setColor(getColor());
    }
    // endregion

    // region Movable
    private void moveToDestination(float delta) {
        float x = getX(Align.center);
        float y = getY(Align.center);

        float distance = Util.calculateDistance(destination, this);
        float cosAlpha = (destination.x - x) / distance;
        float sinAlpha = (destination.y - y) / distance;
        setPosition(x + currentSpeed * cosAlpha * delta, y + currentSpeed * sinAlpha * delta, Align.center);
    }
    private boolean isAtDestination() {
        return getX(Align.center) == destination.x && getY(Align.center) == destination.y;
    }
    // endregion

    // region Live
    public void takeDamage(GameObject attacker, float damage) {
        if (currentHealth < 0) return;
        if (isDead()) return;
        if (decreaseHealth(damage)) {
            die(attacker);
        }
    }
    /**
     * The only function that decreases the health of the unit and returns true if it must die.
     * @param damage The amount of health that will be decreased.
     * @return true, if the unit is dead
     */
    private boolean decreaseHealth(float damage) {
        if (damage >= currentHealth) return true;
        currentHealth -= damage;
        return false;
    }
    private void die(GameObject attacker) {
        currentHealth = 0;
        // any code associated with the death of the object must go here.
        notifyOnDie(attacker);
    }
    // endregion

    // region Notify
    private void notifyPositionChange() {
        for (GameObjectAdapter adapter : gameObjectAdapterList) adapter.onPositionChange(this);
    }
    private void notifyDestinationArrive() {
        for (GameObjectAdapter adapter : gameObjectAdapterList) adapter.onDestinationArrive(this);
    }
    private void notifyOnDie(GameObject attacker) {
        for (GameObjectAdapter adapter : gameObjectAdapterList) adapter.onDie(this, attacker);
    }
    private void notifySizeChange() {
        for (GameObjectAdapter adapter : gameObjectAdapterList) adapter.onSizeChange(this);
    }
    // endregion

    // region Add
    public void addGameObjectAdapter(GameObjectAdapter listener) {
        if (gameObjectAdapterList.contains(listener)) throw new RuntimeException("The given GameObjectAdapter is already added to the game object.");
        gameObjectAdapterList.add(listener);
    }
    public void addEffect(Effect effect) {
        effectList.add(effect);
        addActor(effect);
    }
    // endregion

    // region Remove
    public void removeGameObjectAdapter(GameObjectAdapter listener) {
        if (!gameObjectAdapterList.contains(listener)) throw new RuntimeException("The given GameObjectAdapter is already added to the game object.");
        gameObjectAdapterList.remove(listener);
    }
    public void removeEffect(Effect effect) {
        effectList.remove(effect);
        effect.clearTimer();
        effect.remove();
    }
    // endregion

    // region Enable
    public void enable() {
        isEnable = true;
        enableAttackEffects();
    }
    private void enableAttackEffects() {
        for (Effect effect : effectList) {
            if (effect.isOverTimeEffect()) continue;
            effect.enable();
            List<EffectListener> listeners = effect.getEffectListeners();
            for (EffectListener listener : listeners) listener.onEnable();
        }
    }
    public void freeze(float speedRatio) {
        currentSpeed *= speedRatio;
    }
    // endregion

    // region Disable
    public void disable() {
        isEnable = false;
        disableAttackEffects();
    }
    private void disableAttackEffects() {
        for (Effect effect : effectList) {
            if (effect.isOverTimeEffect()) continue;
            effect.disable();
            List<EffectListener> listeners = effect.getEffectListeners();
            for (EffectListener listener : listeners) listener.onDisable();
        }
    }
    public void unFreeze() {
        currentSpeed = data.speed;
    }
    // endregion

    // region Setters
    public void setGameObjectData(GameObjectData data) {
        this.data = data;
        initCurrentParams(data);
    }
    // TODO: 8/17/17 Change texture to drawable
    protected void setAppearance(String texturePath) {
        data.texturePath = texturePath;
        appearance = new Image(new Texture(texturePath));
        appearance.setTouchable(Touchable.disabled);
        updateAppearance();
        addActor(appearance);
    }
    public void setDestination(float x, float y) {
        isMoving = true;
        if (this.destination == null) this.destination = new Point();
        this.destination.set(x, y);
    }
    public void setPlayerRace(PlayerRace playerType) {
        this.playerType = playerType;
    }
    public void play() {
        isMoving = true;
    }
    public void stop() {
        isMoving = false;
    }
    @Override
    public void setOrigin(int alignment) {
        super.setOrigin(alignment);
        updateAppearance();
    }
    @Override
    public void setRotation(float degrees) {
        super.setRotation(degrees);
        updateAppearance();
    }
    @Override
    public void rotateBy(float amountInDegrees) {
        super.rotateBy(amountInDegrees);
        updateAppearance();
    }
    @Override
    public void setColor(Color color) {
        super.setColor(color);
        updateAppearance();
    }
    @Override
    public void setX(float x) {
        super.setX(x);
        notifyPositionChange();
    }
    @Override
    public void setY(float y) {
        super.setY(y);
        notifyPositionChange();
    }
    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        notifyPositionChange();
    }
    @Override
    public void setPosition(float x, float y, int alignment) {
        super.setPosition(x, y, alignment);
        notifyPositionChange();
    }
    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        updateAppearance();
        notifySizeChange();
    }
    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        updateAppearance();
        notifySizeChange();
    }
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        updateAppearance();
        notifySizeChange();
    }
    @Override
    public void setScale(float scaleX, float scaleY) {
        throw new RuntimeException("DON'T USE THE SCALE!");
    }
    // endregion

    // region Getters
    public <T extends Effect> T isEffectAcquired(Class<T> c) {
        for (Effect effect : effectList) {
            if (c.isInstance(effect)) return c.cast(effect);
        }
        return null;
    }
    public boolean isDead() {
        return currentHealth == 0;
    }
    public float getCurrentHealth() {
        return currentHealth;
    }
    public float getMaxHealth() {
        return data.health;
    }
    public PlayerRace getPlayerType() {
        return playerType;
    }
    public GameObjectData getGameObjectData() {
        return data;
    }
    // endregion
}
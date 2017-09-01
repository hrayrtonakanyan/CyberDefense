package com.hro.hrogame.gameobject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.constants.StringConstants;
import com.hro.hrogame.data.gameobject.GameObjectData;
import com.hro.hrogame.gameobject.effect.Effect;
import com.hro.hrogame.gameobject.effect.EffectListener;
import com.hro.hrogame.primitives.Point;
import com.hro.hrogame.utils.Util;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject extends Entity {

    // region Static fields
    public static final int HEALTH_TO_WEIGHT_RATIO = 10;
    public static final float SPEED_LIMIT = 150;
    // endregion

    // region Instance fields
    private ArrayList<GameObjectAdapter> gameObjectAdapterList = new ArrayList<>();
    private ArrayList<Effect> effectList = new ArrayList<>();
    private PlayerRace playerType = PlayerRace.NONE;
    private ProgressBar healthBar;
    private Image appearance;
    private GameObjectData data;
    private Point destination;
    private int weight;
    private float currentHealth;
    private float currentSpeed;
    private boolean isInvincible;
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
        setLevel(data.level);
        initCurrentParams(data);
        setAppearance(data.texturePath);
        addHealthBar(data.health);
    }
    private void initCurrentParams(GameObjectData data) {
        currentHealth = data.health;
        currentSpeed = data.speed;
        weight = calculateWeight();
    }
    private void addHealthBar(int health) {
        healthBar = new ProgressBar(0, health, 1, false, StringConstants.skin);
        healthBar.setSize(60, 10);
        healthBar.setValue(health);
        healthBar.setAnimateDuration(1);
        healthBar.setColor(Color.GREEN);
        addGameObjectAdapter(new GameObjectAdapter() {
            @Override
            public void onSizeChange(GameObject gameObject) {
                healthBar.setPosition(getWidth() / 2, getHeight() + 5, Align.center);
            }
            @Override
            public void onTakeDamage(float damage, GameObject damagedUnit) {
                healthBar.setValue(healthBar.getValue() - damage);
                // TODO: 8/29/2017 Inproove healthBar color changeing
                if (healthBar.getValue() <= healthBar.getMaxValue() / 2) healthBar.setColor(Color.ORANGE);
                if (healthBar.getValue() <= healthBar.getMaxValue() / 4) healthBar.setColor(Color.RED);
            }
        });
        addActor(healthBar);
    }
    // endregion

    // region Act
    @Override
    public void act(float delta) {
        super.act(delta);
        if (revealPlayerType() == PlayerRace.NONE) throw new RuntimeException("playerType of the game object must not be NONE when add to stage.");
        if (!isEnable) return;
        if (destination == null) return;
        stopIfAllEffectsPositionsAreValid();
        if (isMoving) {
            if (isAtDestination()) return;
            moveToDestination(delta);
            if (Util.calculateDistance(destination, this) < currentSpeed * delta) {
                setPosition(destination.x, destination.y, Align.center);
                stop();
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

    //region Level up
    public void levelUp(boolean showParticle) {
        data.level++;
        data.health += data.health * ParametersConstants.WEIGHT_PROGRESS;
        data.speed += data.speed * ParametersConstants.WEIGHT_PROGRESS;
        if (data.speed > SPEED_LIMIT) data.speed = SPEED_LIMIT;
        for (Effect effect : effectList) effect.levelUp(false);
        weight = calculateWeight();
    }
    private int calculateWeight() {
        int weight = data.health / HEALTH_TO_WEIGHT_RATIO;
        if (effectList.size() != 0) {
            for (Effect effect : effectList) {
                weight += effect.getWeight();
            }
        }
        return weight;
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
    private void stopIfAllEffectsPositionsAreValid() {
        if (effectList.size() == 0) return;

        boolean isOnlyOverTimeEffects = true;
        for (Effect effect : effectList) {
            if (!effect.isOverTimeEffect()) isOnlyOverTimeEffects = false;
        }
        if (isOnlyOverTimeEffects) return;

        boolean isAllEffectsPositionsAreValid = true;
        for (Effect effect : effectList) {
            if (!effect.isPositionValidForEffect()) isAllEffectsPositionsAreValid = false;
        }
        if (isAllEffectsPositionsAreValid) stop();
    }
    // endregion

    // region Live
    public void takeDamage(GameObject attacker, float damage) {
        if (isInvincible) return;
        if (isDead()) return;
        if (decreaseHealth(damage)) die(attacker);
    }
    private boolean decreaseHealth(float damage) {
        notifyOnTakeDamage(damage);
        if (damage >= currentHealth) return true;
        currentHealth -= damage;
        return false;
    }
    private void die(GameObject attacker) {
        currentHealth = 0;
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
    private void notifyOnTakeDamage(float damage) {
        for (GameObjectAdapter adapter : gameObjectAdapterList) adapter.onTakeDamage(damage, this);
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
        weight = calculateWeight();
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
        weight = calculateWeight();
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
    public void setLevel(int level) {
        for (int i = 1; i < level; i++) levelUp(false);
    }
    public void makeInvincible() {
        isInvincible = true;
    }
    public void makeAttackable() {
        isInvincible = false;
    }
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
        play();
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
    public int getWeight() {
        return weight;
    }public <T extends Effect> T isEffectAcquired(Class<T> c) {
        for (Effect effect : effectList) {
            if (c.isInstance(effect)) return c.cast(effect);
        }
        return null;
    }
    public boolean isInvincible() {
        return isInvincible;
    }
    public boolean isDead() {
        return currentHealth == 0;
    }
    public int getLevel() {
        return data.level;
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
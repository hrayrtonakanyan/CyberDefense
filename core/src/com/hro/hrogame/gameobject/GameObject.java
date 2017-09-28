package com.hro.hrogame.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.animation.particleanimation.AnimationListener;
import com.hro.hrogame.animation.particleanimation.ParticleAnimation;
import com.hro.hrogame.constants.ParametersConstants;
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
    // endregion

    // region Instance fields
    private ParticleAnimation levelUpAnimation;
    private ArrayList<GameObjectAdapter> gameObjectAdapterList = new ArrayList<>();
    private ArrayList<Effect> effectList = new ArrayList<>();
    private PlayerRace playerType = PlayerRace.NONE;
    private ProgressBar healthBar;
    private Image appearance;
    private GameObjectData data;
    private Point destination;
    private float weight;
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
        if (data.health.current == 0) throw new RuntimeException("Health of the game object must have a positive value if game object data is received on instantiation.");
    }
    // endregion

    // region Init
    private void initWithData(GameObjectData data) {
        this.data = data;
        setAppearance(data.texturePath);
        alterParamsOnLevelChange(data.level);
        initCurrentParams(data);
        addHealthBar(data.health.current);
        initLevelUpAnimation();
    }
    private void initCurrentParams(GameObjectData data) {
        currentHealth = data.health.current;
        currentSpeed = data.speed.current;
        weight = calculateWeight();
        if (healthBar != null) {
            healthBar.setRange(0, currentHealth);
            healthBar.setValue(currentHealth);
        }
    }
    private void addHealthBar(float health) {
        int width = Gdx.graphics.getWidth() / 10;
        healthBar = new ProgressBar(0, health, 1, false, createHealthBarStyle());
        healthBar.setSize(width, 0);
        healthBar.setValue(health);
        healthBar.setAnimateDuration(0.2f);
        addGameObjectAdapter(new GameObjectAdapter() {
            @Override
            public void onSizeChange(GameObject gameObject) {
                healthBar.setPosition(getWidth() / 2, getHeight() + 5, Align.center);
            }
            @Override
            public void onTakeDamage(float damage, GameObject damagedUnit) {
                healthBar.setValue(healthBar.getValue() - damage);
            }
            @Override
            public void onLevelUp(GameObject gameObject, int level) {
                healthBar.setValue(healthBar.getMaxValue());
            }
        });
        addActor(healthBar);
    }
    private ProgressBar.ProgressBarStyle createHealthBarStyle() {
        int height = Gdx.graphics.getHeight() / 100;
        Pixmap backgroundPixmap = new Pixmap(10, height, Pixmap.Format.RGB888);
        backgroundPixmap.setColor(Color.RED);
        backgroundPixmap.fill();
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(backgroundPixmap)));
        backgroundPixmap.dispose();

        Pixmap knobPixmap = new Pixmap(0, height, Pixmap.Format.RGB888);
        knobPixmap.setColor(Color.GREEN);
        knobPixmap.fill();
        TextureRegionDrawable knobDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(knobPixmap)));
        knobPixmap.dispose();

        Pixmap knobBeforePixmap = new Pixmap(10, height, Pixmap.Format.RGB888);
        knobBeforePixmap.setColor(Color.GREEN);
        knobBeforePixmap.fill();
        TextureRegionDrawable knobBeforeDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(knobBeforePixmap)));
        knobBeforePixmap.dispose();

        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = backgroundDrawable;
        style.knob = knobDrawable;
        style.knobBefore = knobBeforeDrawable;
        return style;
    }
    private void initLevelUpAnimation() {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("lvlup_particle"), Gdx.files.internal(""));
        for (ParticleEmitter emitter : particleEffect.getEmitters()) {
            emitter.getSpawnWidth().setLow(getWidth());
        }
        levelUpAnimation = new ParticleAnimation(particleEffect);
    }
    // endregion

    // region Act
    @Override
    public void act(float delta) {
        super.act(delta);
        if (revealPlayerType() == PlayerRace.NONE)
            throw new RuntimeException("playerType of the game object must not be NONE when add to stage.");
        if (!isEnable) return;
        if (destination == null) return;
        stopIfAllEffectsPositionsAreValid();
        if (isMoving) {
            if (isAtDestination()) return;
            moveToDestination(delta);
            if (Util.calculateDistance(destination, this) < currentSpeed * delta) {
                setPosition(destination.x, destination.y, Align.center);
                stop();
                notifyOnDestinationArrive();
            }
        }
    }
    public void pause() {
        for (Effect effect : effectList) effect.pause();

    }
    public void play() {
        for (Effect effect : effectList) effect.play();
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
    public void levelUp() {
        data.level++;
        alterParamsOnLevelChange(data.level);
        initCurrentParams(data);
        if (getStage() != null) {
            levelUpAnimation.setPosition(getX() + getWidth() / 2, getY(), Align.center);

            levelUpAnimation.setCompleteListener(new AnimationListener() {
                @Override
                public void onComplete() {
                    levelUpAnimation.remove();
                }
            });
            levelUpAnimation.start();
            addActor(levelUpAnimation);
        }
        notifyOnLevelUp(this, data.level);
    }
    private void alterParamsOnLevelChange(int level) {
        Util.calcProgressAndDefineWeight(0, level, ParametersConstants.PROGRESS_RATIO,
                true, data.health, data.speed);
        if (effectList.size() != 0) {
            for (Effect effect : effectList) {
                effect.levelUpEffect(level);
            }
        }
    }
    private float calculateWeight() {
        float weight = data.health.current / HEALTH_TO_WEIGHT_RATIO;
        if (effectList.size() != 0) {
            for (Effect effect : effectList) {
                weight += effect.getEffectWeight();
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
    public void selfDestruct() {
        die(this);
    }
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
        attacker.notifyOnKill(this);
    }
    // endregion

    // region Notify
    private void notifyOnPositionChange() {
        for (GameObjectAdapter adapter : gameObjectAdapterList) adapter.onPositionChange(this);
    }
    private void notifyOnDestinationArrive() {
        for (GameObjectAdapter adapter : gameObjectAdapterList) adapter.onDestinationArrive(this);
    }
    private void notifyOnDie(GameObject attacker) {
        for (GameObjectAdapter adapter : gameObjectAdapterList) adapter.onDie(this, attacker);
    }
    private void notifyOnSizeChange() {
        for (GameObjectAdapter adapter : gameObjectAdapterList) adapter.onSizeChange(this);
    }
    private void notifyOnTakeDamage(float damage) {
        for (GameObjectAdapter adapter : gameObjectAdapterList) adapter.onTakeDamage(damage, this);
    }
    private void notifyOnKill(GameObject target) {
        for (GameObjectAdapter adapter : gameObjectAdapterList) adapter.onKill(target, this);
    }
    private void notifyOnLevelUp(GameObject target, int level) {
        for (GameObjectAdapter adapter : gameObjectAdapterList) adapter.onLevelUp(target, level);
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
        currentSpeed = data.speed.current;
    }
    // endregion

    // region Setters
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
    public void setHealthBarLength(float length) {
        healthBar.setSize(length, 10);
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
        move();
        if (this.destination == null) this.destination = new Point();
        this.destination.set(x, y);
        rotateToDestination();
    }
    private void rotateToDestination() {
        setOrigin(Align.center);
        float dy = destination.y - getY(Align.center);
        float dx = destination.x - getX(Align.center);
        float angle;
        angle = (float)Math.toDegrees(Math.atan2(dy, dx));
        setRotation(angle);
    }
    public void setPlayerRace(PlayerRace playerType) {
        this.playerType = playerType;
    }
    private void move() {
        isMoving = true;
    }
    private void stop() {
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
        notifyOnPositionChange();
    }
    @Override
    public void setY(float y) {
        super.setY(y);
        notifyOnPositionChange();
    }
    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        notifyOnPositionChange();
    }
    @Override
    public void setPosition(float x, float y, int alignment) {
        super.setPosition(x, y, alignment);
        notifyOnPositionChange();
    }
    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        updateAppearance();
        notifyOnSizeChange();
    }
    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        updateAppearance();
        notifyOnSizeChange();
    }
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        updateAppearance();
        notifyOnSizeChange();
    }
    @Override
    public void setScale(float scaleX, float scaleY) {
        throw new RuntimeException("DON'T USE THE SCALE!");
    }
    // endregion

    // region Getters
    public int getReward() {
        return (int) weight / ParametersConstants.WEIGHT_TO_REWARD_RATIO;
    }
    public float getWeight() {
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
    public PlayerRace getPlayerType() {
        return playerType;
    }
    // endregion
}
package com.hro.hrogame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.hro.hrogame.constants.ParametersConstants;
import com.hro.hrogame.constants.StringConstants;
import com.hro.hrogame.gameobject.effect.EffectType;
import com.hro.hrogame.stage.GameStage;
import com.hro.hrogame.stage.LayerType;

import java.util.ArrayList;

public class EffectDialog extends Group {

    // region Static fields
    public static final float WIDTH = Gdx.graphics.getWidth() / 2;
    public static final float HEIGHT = Gdx.graphics.getHeight() / 2;
    public static final float BUTTON_WIDTH = WIDTH / 5;
    public static final float BUTTON_HEIGHT = HEIGHT / 6;
    public static final float BUTTON_WIDTH_PURCHASED = WIDTH / 3;
    public static final float PADDING = Gdx.graphics.getWidth() / 80;

    public static final String HARD_CANNON_TITLE = "Hard Cannon";
    public static final String FREEZER_TITLE = "Freezer";
    public static final String STUNNER_TITLE = "Stunner";
    public static final String ABSORB_SHIELD_TITLE = "Absorb Shield";
    public static final String HELL_FIRE_TITLE = "Hell Fire";

    public static final String PURCHASED = "Purchased";
    public static final int HARD_CANNON_PRICE = 75;
    public static final int FREEZER_PRICE = 245;
    public static final int STUNNER_PRICE = 380;
    public static final int ABSORB_SHIELD_PRICE = 2500;
    public static final int HELL_FIRE_PRICE = 4000;
    // endregion

    // region Instance fields
    private GameStage stage;
    private Skin skin;
    private Table container;
    private ArrayList<Element> elementList;
    private ArrayList<EffectDialogListener> listenerList;
    private int playerGold;
    // endregion

    // region C-tor
    public EffectDialog(Skin skin, GameStage stage) {
        this.stage = stage;
        this.skin = skin;
        setTransform(false);
        init();
    }
    // endregion

    // region Init
    private void init() {
        container = new Table(skin);
        container.setBackground(StringConstants.BROWN_BACKGROUND_DRAWABLE);
        addActor(container);
        elementList = new ArrayList<>();
        listenerList = new ArrayList<>();
        initRows();
        setSize(WIDTH, HEIGHT);
    }
    private void initRows() {
        createRow(EffectType.HARD_CANNON,   HARD_CANNON_TITLE,   HARD_CANNON_PRICE);
        createRow(EffectType.FREEZER,       FREEZER_TITLE,       FREEZER_PRICE);
        createRow(EffectType.STUNNER,       STUNNER_TITLE,       STUNNER_PRICE);
        createRow(EffectType.ABSORB_SHIELD, ABSORB_SHIELD_TITLE, ABSORB_SHIELD_PRICE);
        createRow(EffectType.HELL_FIRE,     HELL_FIRE_TITLE,     HELL_FIRE_PRICE);
    }
    private void createRow(final EffectType type, String name, final int price) {
        final Element element = new Element(name, price, skin);
        element.button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (element.isPurchased) return;
                for (EffectDialogListener listener : listenerList) listener.onItemBought(type, price);
                element.purchase();
            }
        });
        elementList.add(element);
        container.add(element).expand().fill();
        container.row();
    }
    // endregion

    // region Act
    @Override
    public void act(float delta) {
        super.act(delta);
        updateDialog();
    }
    // endregion

    // region Update and Adjust
    private void updateDialog() {
        for (Element element : elementList) {
            if (element.isPurchased) continue;
            Button button = element.button;
            if (element.price > playerGold) {
                button.setTouchable(Touchable.disabled);
                button.setColor(Color.GRAY);
            } else {
                button.setTouchable(Touchable.enabled);
                button.setColor(Color.ORANGE);
            }
        }
    }
    private void adjustDialog() {
        container.setSize(getWidth(), getHeight());
        setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
    }
    // endregion

    // region Show/Hide
    public void show() {
        stage.addActor(this, LayerType.MENU_UI);
    }
    public void hide() {
        remove();
    }
    // endregion

    // region Add
    public void addEffectDialogListener(EffectDialogListener listener) {
        listenerList.add(listener);
    }
    // endregion

    // region Setter
    public void setPlayerGold(int playerGold) {
        this.playerGold = playerGold;
    }
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        adjustDialog();
    }
    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        adjustDialog();
    }
    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        adjustDialog();
    }
    // endregion

    // region Inner Class
    private class Element extends Table {

        // region Instance fields
        private String effectName;
        private int price;
        private boolean isPurchased;

        private Label label;
        private Label buttonLabel;
        private Button button;
        // endregion

        // region C-tor
        Element(String effectName, int price, Skin skin) {
            super(skin);
            this.effectName = effectName;
            this.price = price;
            init();
        }
        // endregion

        // region Init
        private void init() {
            label = new Label(effectName, getSkin());
            label.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);
            label.setTouchable(Touchable.disabled);
            createButton();
            add(label).expand().left().padLeft(PADDING);
            add(button).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).right().pad(PADDING);
        }
        private void createButton() {
            buttonLabel = new Label(price + "", getSkin());
            buttonLabel.setFontScale(ParametersConstants.FONT_SCALE, ParametersConstants.FONT_SCALE);
            button = new Button(skin, StringConstants.BTN_PURCHASE);
            button.add(buttonLabel);
        }
        // endregion

        // region Setter
        void purchase() {
            isPurchased = true;
            button.setColor(Color.GRAY);
            buttonLabel.setText(PURCHASED);
            button.setTouchable(Touchable.disabled);
            getCell(button).width(BUTTON_WIDTH_PURCHASED).right().pad(PADDING);
        }
        // endregion
    }
    // endregion
}
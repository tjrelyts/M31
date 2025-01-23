package com.tyler.stardust;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SimulatorParameterEditorUI extends ApplicationAdapter {
 
    private Stage stage;
    private Skin skin;

    private Slider massSlider;
    private Slider gravitySlider;

    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        createUI();
    }

    private void createUI() {
        Table editorTable = new Table(skin);
        editorTable.setFillParent(true);
        editorTable.align(Align.topLeft);
        editorTable.pad(10);

        // Mass slider
        editorTable.add("Mass:").padRight(10);
        massSlider = new Slider(1.0f, 100000.0f, 1.0f, false, skin);
        Label massValueLabel = new Label("1.0", skin);
        massSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                massValueLabel.setText(String.format("%.1f", massSlider.getValue()));
            }
        });
        editorTable.add(massSlider).size(100, 20);
        editorTable.add(massValueLabel).padLeft(10);
        editorTable.row();

        // Gravity slider
        editorTable.add("Gravity:").padRight(10);
        gravitySlider = new Slider(1.0f, 1000.0f, 1.0f, false, skin);
        Label gravityLabel = new Label("1.0", skin);
        gravitySlider.setValue(1.0f);  // Default Earth gravity
        gravitySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gravityLabel.setText(String.format("%.1f", gravitySlider.getValue()));
            }
        });
        editorTable.add(gravitySlider).size(100, 20);
        editorTable.add(gravityLabel).padLeft(10);
        editorTable.row();

        stage.addActor(editorTable);
    }

    @Override
    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public float getMass() {
        return massSlider.getValue();
    }

    public float getGravity() {
        return gravitySlider.getValue();
    }

    public Stage getStage() {
        return stage;
    }

} 
package com.tyler.stardust;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class GameScreen implements Screen {
    
    private SimulatorParameterEditorUI simulatorParameterEditorUI;
    private PhysicsEngine physicsEngine;
    private ArrayList<Particle> particleList;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    public GameScreen() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        
        simulatorParameterEditorUI = new SimulatorParameterEditorUI();
        physicsEngine = new PhysicsEngine();
        particleList = new ArrayList<Particle>();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void show() {
        simulatorParameterEditorUI.create();
        Gdx.input.setInputProcessor(new InputMultiplexer(
            simulatorParameterEditorUI.getStage(),
            new InputAdapter() {
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    if (button == Input.Buttons.LEFT) {
                        spawnParticleAtMouse(screenX, screenY);
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean scrolled(float amountX, float amountY) {
                    camera.zoom += amountY * 0.1f * camera.zoom;
                    camera.zoom = Math.max(0.1f, camera.zoom);
                    return true;
                }
            }
        ));
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Handle WASD movement
        float moveSpeed = 500f * camera.zoom; // Adjust speed based on zoom level
        if (Gdx.input.isKeyPressed(Input.Keys.W)) camera.translate(0, moveSpeed * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) camera.translate(0, -moveSpeed * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) camera.translate(-moveSpeed * delta, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) camera.translate(moveSpeed * delta, 0);
        
        camera.update();

        physicsEngine.setGravitationalConstant(simulatorParameterEditorUI.getGravity());        
        physicsEngine.update(particleList, delta);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Particle particle : particleList) {
            shapeRenderer.setColor(particle.getBodyColor());
            shapeRenderer.circle(
                (float)particle.getPosition().x,
                (float)particle.getPosition().y,
                (float)particle.getRadius()
            );
        }
        shapeRenderer.end();

        simulatorParameterEditorUI.render();
    }
    
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.position.set(width / 2f, height / 2f, 0);
        camera.update();
        simulatorParameterEditorUI.resize(width, height);
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        simulatorParameterEditorUI.dispose();
        shapeRenderer.dispose();
    }

    private void spawnParticleAtMouse(int screenX, int screenY) {
        Vector3 worldCoords = new Vector3(screenX, screenY, 0);
        camera.unproject(worldCoords);
        
        Particle particle = new Particle.Builder(
                simulatorParameterEditorUI.getMass(),
                new Vector2D(worldCoords.x, worldCoords.y))
            .velocity(new Vector2D(0, 0))
            .bodyColor(Color.WHITE)
            .build();
            
        particleList.add(particle);
    }
}

package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen {
    private boolean goingToLiverpool = true;
    private Vector2 liverpool = new Vector2(115, 397);
    private Vector2 tonder = new Vector2(1275, 127);

    final MyGdxGame game;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    FrameBuffer fogBuffer;
    Texture fogTexture;
    Texture mapTexture, zeppelinTexture;
    Player player;
    OrthographicCamera camera;
    Viewport viewport;
    EnvironmentalManager environmentalManager;  // Make sure this is defined

    public GameScreen(MyGdxGame game, SpriteBatch batch, Texture mapTexture, Texture zeppelinTexture) {
        this.game = game;
        this.batch = batch;
        this.mapTexture = mapTexture;
        this.zeppelinTexture = zeppelinTexture;

        this.shapeRenderer = new ShapeRenderer();
        this.camera = new OrthographicCamera();
        this.viewport = new StretchViewport(800, 600, camera);
        this.camera.position.set(400, 300, 0);
        this.camera.update();

        this.player = new Player(1275, 127); // Assuming these are pixel coordinates
        this.environmentalManager = new EnvironmentalManager();

        this.fogBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 800, 600, false);
        clearFog();
        this.fogTexture = fogBuffer.getColorBufferTexture();
        this.fogTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    private void clearFog() {
        fogBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fogBuffer.end();
    }

    // Implement other methods...


    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Update methods handle game logic
        handleInput();  // Process player input
        environmentalManager.update(delta, player);  // Update environmental effects like wind
        player.update(delta);  // Update player state based on input and environment

        // Clear the screen with a solid color (here, white)
        ScreenUtils.clear(1, 1, 1, 1);

        // Update the camera's matrices
        camera.update();

        // Tell the SpriteBatch to use the coordinate system specified by the camera
        batch.setProjectionMatrix(camera.combined);

        // Drawing to the screen starts here
        batch.begin();
        // Draw the map as the background
        batch.draw(mapTexture, 0, 0, 800, 600);
        // Draw the zeppelin at the player's current position
        batch.draw(zeppelinTexture, player.getPosition().x, player.getPosition().y, 50, 25);
        // Draw fog texture over the scene
        batch.draw(fogTexture, 0, 0, 800, 600);
        batch.end();  // End of drawing, submit to the GPU

        // Check if the target location has been reached and toggle if needed
        toggleTargetIfNeeded();

        // Optionally log debug information
        logDebugInfo();
    }

    /**
     * Handles player input to update velocity or perform actions.
     */
    private void handleInput() {
        float moveSpeed = 200;  // Speed in pixels per second for horizontal movement
        float verticalSpeed = 100;  // Speed in pixels per second for vertical movement

        // Reset velocity each frame to prevent continuous movement when keys are not pressed
        player.setVelocity(0, 0);

        // Adjust velocity based on arrow key inputs
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.setVelocity(-moveSpeed, player.getVelocityY());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.setVelocity(moveSpeed, player.getVelocityY());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.setVelocity(player.getVelocityX(), verticalSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.setVelocity(player.getVelocityX(), -verticalSpeed);
        }
    }

    /**
     * Toggle the player's target between two locations if the current target is reached.
     */
    private void toggleTargetIfNeeded() {
        if (player.getPosition().epsilonEquals(player.getTarget(), 1f)) {
            goingToLiverpool = !goingToLiverpool;
            player.setTarget(goingToLiverpool ? liverpool : tonder);
        }
    }

    /**
     * Logs the current frame's status for debugging purposes.
     */
    private void logDebugInfo() {
        System.out.println("Rendering frame...");
        System.out.println("Player Position: " + player.getPosition());
        System.out.println("Camera Position: " + camera.position);
    }



    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        mapTexture.dispose();
        zeppelinTexture.dispose();
        fogBuffer.dispose();
    }

}

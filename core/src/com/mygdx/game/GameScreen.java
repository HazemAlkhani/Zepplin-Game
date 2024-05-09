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
    // End point to be reached
    private final Vector2 finalDestination = new Vector2(300, 397); // Example end point, Liverpool

    final MyGdxGame game;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    FrameBuffer fogBuffer;
    Texture fogTexture;
    Texture mapTexture, zeppelinTexture;
    Player player;
    OrthographicCamera camera;
    Viewport viewport;
    EnvironmentalManager environmentalManager;


    public GameScreen(MyGdxGame game, SpriteBatch batch, Texture mapTexture, Texture zeppelinTexture) {
        this.game = game;
        this.batch = batch;
        this.mapTexture = mapTexture;
        this.zeppelinTexture = zeppelinTexture;

        this.shapeRenderer = new ShapeRenderer();
        this.camera = new OrthographicCamera();
        this.viewport = new StretchViewport(800, 600, camera);
        this.camera.position.set(400, 300, 0); // Center the camera
        this.camera.update();

        // Initialize player at specified coordinates
        this.player = new Player(620, 500); // Adjust these coordinates to suit the map size
        this.environmentalManager = new EnvironmentalManager();

        // Create fog buffer for special visual effects
        this.fogBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 800, 600, false);
        clearFog();
        this.fogTexture = fogBuffer.getColorBufferTexture();
        this.fogTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Log initial setup information
        System.out.println("Initial Player Position: " + player.getPosition());
        System.out.println("Zeppelin Texture Loaded: " + (zeppelinTexture != null));
        System.out.println("Map Texture Loaded: " + (mapTexture != null));
    }

    /**
     * Clears the fog buffer.
     */
    private void clearFog() {
        fogBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fogBuffer.end();
    }

    @Override
    public void show() {}

    /**
     * Checks if the player has reached the final destination and ends the game if so.
     */
    private void checkEndGame() {
        if (player.getPosition().epsilonEquals(finalDestination, 1f)) {
            System.out.println("Congratulations, you have reached the destination!");
            Gdx.app.exit(); // Exit the game or switch to a winning screen
        }
    }

    @Override
    public void render(float delta) {
        Vector2 playerPosition = player.getPosition();
        Vector2 playerVelocity = player.getVelocity();

        System.out.println("Player Position: " + playerPosition + ", Velocity: " + playerVelocity);

        // Update game logic
        handleInput();  // Handle player input
        environmentalManager.update(delta, player);  // Update environmental effects on the player
        player.update(delta);  // Update player's movement

        // Clear the screen
        ScreenUtils.clear(1, 1, 1, 1);

        // Update camera and apply its projection matrix
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Draw game objects and effects
        batch.begin();
        batch.draw(mapTexture, 0, 0, 800, 600); // Draw the entire map
        batch.draw(zeppelinTexture, playerPosition.x, playerPosition.y, 50, 25); // Draw zeppelin
        batch.draw(fogTexture, 0, 0, 800, 600);
        batch.end();

        checkEndGame();
    }

    /**
     * Handles player input to adjust velocity based on key presses.
     */
    private void handleInput() {
        // Press 'A' to start moving left and increase speed
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            player.startMovingLeft();
            player.adjustSpeed(1); // Increase speed by 1 unit
        }

        // Press 'Z' to reduce speed, ensuring it doesn't drop below 1
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            player.adjustSpeed(-1); // Decrease speed by 1 unit
        }

        // Retrieve the player's current speed to match it for vertical movement
        float currentSpeed = player.getSpeed();
        player.getVelocityY();
        float currentVelocityY;

        // Adjust vertical movement using arrow keys, matching horizontal speed
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            currentVelocityY = currentSpeed; // Move up at the same speed as the zeppelin
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            currentVelocityY = -currentSpeed; // Move down at the same speed
        } else {
            currentVelocityY = 0; // Stop vertical movement
        }

        // Apply only the vertical movement to the player's velocity
        player.setVelocity(player.getVelocityX(), currentVelocityY);

        // Press 'R' to restart the game
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            game.startGameScreen(); // Restart the game
        }

        // Log the player's velocity for debugging purposes
        System.out.println("Player Velocity: " + player.getVelocity() + ", Speed: " + player.getSpeed());
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

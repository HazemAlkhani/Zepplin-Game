package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameScreen implements Screen {
    private final SpriteBatch batch;
    private final Texture mapTexture;
    private final Texture zeppelinTexture;
    private final Texture backgroundTexture;
    private final OrthographicCamera camera;
    private final Player player;
    private final EnvironmentalManager environmentalManager;
    private final FrameBuffer fogBuffer;
    private final ShapeRenderer shapeRenderer;
    private final Vector2 finalDestination;
    private final Stage stage;
    private final Label speedLabel;
    private final Label windLabel;
    private final Label timerLabel;
    private final Skin uiSkin;
    private float gameTime;
    private static final float MAX_GAME_TIME = 60f;
    private static final float ENDPOINT_RADIUS = 3f;
    private Container<Table> container; // Declare the container as a class variable

    private final Sound zeppelinSound;
    private final Sound windSound;
    private final Sound gameOverSound;
    private final Sound winSound;

    public GameScreen(SpriteBatch batch, Texture mapTexture, Texture zeppelinTexture, Skin uiSkin, AssetManager assetManager) {
        this.batch = batch;
        this.mapTexture = mapTexture;
        this.zeppelinTexture = zeppelinTexture;
        this.uiSkin = uiSkin;

        camera = new OrthographicCamera(800, 600);
        camera.position.set(400, 300, 0);
        camera.update();

        zeppelinSound = assetManager.get("sounds/zeppelinSound.mp3", Sound.class);
        windSound = assetManager.get("sounds/WindSound.mp3", Sound.class);
        gameOverSound = assetManager.get("sounds/gameOverSound.mp3", Sound.class);
        winSound = assetManager.get("sounds/winSound.mp3", Sound.class);

        environmentalManager = new EnvironmentalManager(windSound);
        player = new Player(620, 500);
        fogBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 800, 600, false);
        shapeRenderer = new ShapeRenderer();
        finalDestination = new Vector2(60, 297);
        gameTime = MAX_GAME_TIME;

        backgroundTexture = new Texture(Gdx.files.internal("images/background.png"));

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create labels with specific styles
        speedLabel = new Label("Speed: 0", uiSkin, "green");  // Use green style for zeppelin speed
        windLabel = new Label("Wind: 0,0", uiSkin, "white");  // Use white style for wind speed
        timerLabel = new Label("Time: 60", uiSkin, "red");  // Use red style for timer

        // Create a table and set its background
        Table table = new Table();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));

        // Desired size of the small background
        int backgroundWidth = 300;
        int backgroundHeight = 100;

        // Set the size of the table
        table.setSize(backgroundWidth, backgroundHeight);

        // Add padding and labels to the table
        table.pad(10);
        table.add(speedLabel).expandX().left().padBottom(5).row();
        table.add(windLabel).expandX().left().padBottom(5).row();
        table.add(timerLabel).expandX().left().padBottom(5).row();

        // Create a container to hold the table and position it in the bottom-right corner
        container = new Container<>(table);
        container.setSize(backgroundWidth, backgroundHeight);
        container.setPosition(Gdx.graphics.getWidth() - backgroundWidth - 10, 10); // Position 10 pixels from the right and bottom edges

        // Add the container to the stage
        stage.addActor(container);

        clearFog();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.startMoving();
            zeppelinSound.loop(); // Start playing the Zeppelin sound in a loop
            if (player.canMove) {
                player.adjustSpeed(0.1f);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            player.adjustSpeed(-0.1f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.moveUp();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.moveDown();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            restartGame();
        }
    }

    private void restartGame() {
        player.reset(620, 500);
        gameTime = MAX_GAME_TIME;
        zeppelinSound.stop(); // Stop the Zeppelin sound
    }

    private void clearFog() {
        fogBuffer.begin();
        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fogBuffer.end();
    }

    private void updateUI() {
        speedLabel.setText("Speed: " + player.getSpeed());
        Vector2 wind = environmentalManager.getWind();
        windLabel.setText(String.format("Wind: %.2f, %.2f", wind.x, wind.y));
        timerLabel.setText(String.format("Time: %.2f s", gameTime));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        handleInput();
        if (player.hasGameStarted()) {
            gameTime -= delta;
        }
        if (gameTime <= 0) {
            gameOverSound.play(); // Play game over sound
            showDialog("Game Over", "You are too late!");
            restartGame();
        }

        player.update(delta);
        environmentalManager.update(player);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(mapTexture, 0, 0, 800, 600); // Draw the map first
        batch.draw(zeppelinTexture, player.getPosition().x, player.getPosition().y, 30, 15); // Adjusted Zeppelin size
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(finalDestination.x, finalDestination.y, ENDPOINT_RADIUS); // Adjusted endpoint radius
        shapeRenderer.end();

        updateUI();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        checkGameEndConditions();
    }

    private void checkGameEndConditions() {
        if (gameTime <= 0 && !player.isAtEndpoint(finalDestination)) {
            gameOverSound.play(); // Play game over sound
            showDialog("Game Over", "You are too late!");
            restartGame();
        }

        if (player.isAtEndpoint(finalDestination)) {
            winSound.play(); // Play win sound
            showDialog("Congratulations!", "You have reached Liverpool!");
        }

        if (player.isOutOfBounds(0)) {
            gameOverSound.play(); // Play game over sound
            showDialog("Game Over", "You went far away!");
            restartGame();
        }
    }

    private void showDialog(String title, String message) {
        Dialog dialog = new Dialog(title, uiSkin);
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
        dialog.pack();
        dialog.setPosition((Gdx.graphics.getWidth() - dialog.getWidth()) / 2,
                (Gdx.graphics.getHeight() - dialog.getHeight()) / 2);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        container.setPosition(width - container.getWidth() - 10, 10); // Reposition the container
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
        backgroundTexture.dispose();
        fogBuffer.dispose();
        stage.dispose();
        uiSkin.dispose();
        zeppelinSound.dispose();
        windSound.dispose();
        gameOverSound.dispose();
        winSound.dispose();
    }
}

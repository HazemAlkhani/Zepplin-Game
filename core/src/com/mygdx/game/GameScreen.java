package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
    private final Stage stage;
    private final Label speedLabel;
    private final Label windLabel;
    private final Label timerLabel;
    private final SpriteBatch batch;

    private final Vector2 finalDestination = new Vector2(50, 238); // Example end point
    private float gameTime = 0; // Timer to track total game time
    private final float maxGameTime = 60;

    private final ShapeRenderer shapeRenderer;
    private final Texture mapTexture;
    private final Texture zeppelinTexture;
    private final Player player;
    private final OrthographicCamera camera;
    private final EnvironmentalManager environmentalManager;
    private final FrameBuffer fogBuffer;


    public GameScreen(SpriteBatch batch, Texture mapTexture, Texture zeppelinTexture) {

        this.batch = batch;
        this.mapTexture = mapTexture;
        this.zeppelinTexture = zeppelinTexture;

        camera = new OrthographicCamera(800, 600);
        camera.position.set(400, 300, 0); // Center the camera
        camera.update();

        environmentalManager = new EnvironmentalManager();
        player = new Player(620, 500);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        BitmapFont font = new BitmapFont();
        speedLabel = new Label("Speed: 0", new Label.LabelStyle(font, Color.BLUE));
        windLabel = new Label("Wind: 0,0", new Label.LabelStyle(font, Color.BLUE));
        timerLabel = new Label("Time: 0", new Label.LabelStyle(font, Color.YELLOW));

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);
        table.add(speedLabel).pad(10);
        table.row();
        table.add(windLabel).pad(10);
        table.row();
        table.add(timerLabel).pad(10);
        stage.addActor(table);

        // Ensure shapeRenderer is initialized
        shapeRenderer = new ShapeRenderer();

        fogBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 800, 600, false);
        clearFog();
        Texture fogTexture = fogBuffer.getColorBufferTexture();
        fogTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A) && !player.canMove) {
            player.startMoving();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.moveUp();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.moveDown();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && player.canMove) {
            player.adjustSpeed(0.1f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            player.adjustSpeed(-0.1f);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            restartGame();
        }
    }
    private void restartGame() {
        player.reset(620, 500);
        gameTime = 0;
    }


    @Override
    public void show() {
        // Typically used to setup resources or configurations when screen is shown
    }
    private void clearFog() {
        // Assuming you want to clear a framebuffer or similar
        fogBuffer.begin();
        Gdx.gl.glClearColor(1, 1, 1, 0); // Clear with opaque white
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fogBuffer.end();
    }

    private void updateUI() {
        speedLabel.setText("Speed: " + player.getSpeed());
        Vector2 wind = environmentalManager.getWind();
        windLabel.setText(String.format("Wind: %.2f, %.2f", wind.x, wind.y));
        timerLabel.setText(String.format("Time: %.2f s", gameTime));
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1); // Set clear color to white
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen

        camera.update(); // Update the camera
        batch.setProjectionMatrix(camera.combined); // Set the batch to use the camera's coordinate system

        // Handle user input
        handleInput();
        gameTime += delta;
        player.update(delta);
        environmentalManager.update(delta, player);

        // Drawing the textures
        batch.begin();
        batch.draw(mapTexture, 0, 0, 800, 600); // Draw the map
        batch.draw(zeppelinTexture, player.getPosition().x, player.getPosition().y, 50, 25); // Draw the zeppelin
        batch.end();

        // Draw additional shapes if needed
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(finalDestination.x, finalDestination.y, 10);
        shapeRenderer.end();

        // Update and draw the stage last to ensure it is on top
        updateUI();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // Check game conditions
        checkGameEndConditions();
    }


    private void checkGameEndConditions() {
        if (player.isAtEndpoint(finalDestination)) {
            showDialog("Congratulations!", "You have reached the endpoint!");
            // Consider what should happen here: reset game, stop movement, etc.
        }
        if (player.isOutOfBounds(0)) { // Assuming 0 is the left boundary
            showDialog("Game Over", "You went out of bounds!");
            // Handle game over state
        }
    }

    private void showDialog(String title, String message) {
        Dialog dialog = new Dialog(title, new Window.WindowStyle(new BitmapFont(), Color.WHITE, createDialogBackground()));
        dialog.text(new Label(message, new Label.LabelStyle(new BitmapFont(), Color.WHITE)));
        dialog.button(new TextButton("OK", new TextButton.TextButtonStyle()));
        dialog.show(stage);
    }



    private Drawable createDialogBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        return drawable;
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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

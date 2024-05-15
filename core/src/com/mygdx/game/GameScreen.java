package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
    private final SpriteBatch batch;
    private final Texture mapTexture;
    private final Texture zeppelinTexture;
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

    public GameScreen(SpriteBatch batch, Texture mapTexture, Texture zeppelinTexture, Skin uiSkin) {
        this.batch = batch;
        this.mapTexture = mapTexture;
        this.zeppelinTexture = zeppelinTexture;
        this.uiSkin = uiSkin;

        camera = new OrthographicCamera(800, 600);
        camera.position.set(400, 300, 0);
        camera.update();

        player = new Player(620, 500);
        environmentalManager = new EnvironmentalManager();
        fogBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 800, 600, false);
        shapeRenderer = new ShapeRenderer();
        finalDestination = new Vector2(60, 297);
        gameTime = MAX_GAME_TIME;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        speedLabel = new Label("Speed: 0", uiSkin);
        windLabel = new Label("Wind: 0,0", uiSkin);
        timerLabel = new Label("Time: 60", uiSkin);

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);
        table.add(speedLabel).pad(10);
        table.row();
        table.add(windLabel).pad(10);
        table.row();
        table.add(timerLabel).pad(10);
        stage.addActor(table);

        clearFog();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.startMoving();
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
        batch.draw(mapTexture, 0, 0, 800, 600);
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
            showDialog("Game Over", "You are too late!");
            restartGame();
        }

        if (player.isAtEndpoint(finalDestination)) {
            showDialog("Congratulations!", "You have reached Liverpool!");
        }

        if (player.isOutOfBounds(0)) {
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
        stage.dispose();
        uiSkin.dispose();
    }
}

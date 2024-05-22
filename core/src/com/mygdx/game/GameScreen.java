package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameScreen implements Screen {
    private final MyGdxGame game;
    private final SpriteBatch batch;
    private final Texture mapTexture;
    private final Texture zeppelinTexture;
    private final Texture backgroundTexture;
    private final Texture cloudTexture;
    private final OrthographicCamera camera;
    private final Player player;
    private final EnvironmentalManager environmentalManager;
    private final ShapeRenderer shapeRenderer;
    private final Vector2 finalDestination;
    private final Stage stage;
    private final Label speedLabel;
    private final Label windLabel;
    private final Label timerLabel;
    private final Label levelLabel;
    private final Label cloudsLabel;
    private final Skin uiSkin;
    private final Container<Table> container;
    private float gameTime;
    private static final float MAX_GAME_TIME = 60f;
    private static final float ENDPOINT_RADIUS = 3f;
    private boolean isGameOver;
    private boolean isZeppelinSoundPlaying = false;
    private int level;
    private float zeppelinWidth;
    private float zeppelinHeight;

    private final Sound zeppelinSound;
    private final Sound windSound;
    private final Sound gameOverSound;
    private final Sound winSound;
    private static final float ZEPPELIN_VOLUME = 0.9f;
    private static final float WIND_VOLUME = 0.06f;
    private static final float GAME_OVER_VOLUME = 0.9f;
    private static final float WIN_VOLUME = 0.9f;

    public GameScreen(MyGdxGame game, SpriteBatch batch, Texture mapTexture, Texture zeppelinTexture, Skin uiSkin, AssetManager assetManager, int level) {
        this.game = game;
        this.batch = batch;
        this.mapTexture = mapTexture;
        this.zeppelinTexture = zeppelinTexture;
        this.uiSkin = uiSkin;
        this.level = level;

        camera = new OrthographicCamera(800, 600);
        camera.position.set(400, 300, 0);
        camera.update();

        zeppelinSound = assetManager.get("sounds/zeppelinSound.mp3", Sound.class);
        windSound = assetManager.get("sounds/WindSound.mp3", Sound.class);
        gameOverSound = assetManager.get("sounds/gameOverSound.mp3", Sound.class);
        winSound = assetManager.get("sounds/winSound.mp3", Sound.class);

        cloudTexture = new Texture(Gdx.files.internal("images/cloud.png"));
        environmentalManager = new EnvironmentalManager(windSound, WIND_VOLUME, cloudTexture, level);
        player = new Player(620, 500);
        shapeRenderer = new ShapeRenderer();
        finalDestination = new Vector2(60, 297);
        gameTime = MAX_GAME_TIME;
        isGameOver = false;

        backgroundTexture = new Texture(Gdx.files.internal("images/background.png"));

        // Determine Zeppelin size based on the texture used
        if (zeppelinTexture.equals(assetManager.get("images/Zepplin L19.png", Texture.class))) {
            zeppelinWidth = 45;
            zeppelinHeight = 25;
        } else if (zeppelinTexture.equals(assetManager.get("images/Zepplin L20.png", Texture.class))) {
            zeppelinWidth = 45;
            zeppelinHeight = 10;
        }
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        speedLabel = new Label("Speed: 0", uiSkin, "green");
        windLabel = new Label("Wind: 0,0", uiSkin, "white");
        timerLabel = new Label("Time: 60", uiSkin, "red");
        levelLabel = new Label("Level: " + level, uiSkin, "white");
        cloudsLabel = new Label("Clouds: " + environmentalManager.getNumClouds(), uiSkin, "blue");

        Table table = new Table();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));

        int backgroundWidth = 300;
        int backgroundHeight = 150;

        table.setSize(backgroundWidth, backgroundHeight);

        table.pad(10);
        table.add(speedLabel).expandX().left().padBottom(5).row();
        table.add(windLabel).expandX().left().padBottom(5).row();
        table.add(timerLabel).expandX().left().padBottom(5).row();
        table.add(levelLabel).expandX().left().padBottom(5).row();
        table.add(cloudsLabel).expandX().left().padBottom(5).row();

        container = new Container<>(table);
        container.setSize(backgroundWidth, backgroundHeight);
        container.setPosition(Gdx.graphics.getWidth() - backgroundWidth - 10, 10);

        stage.addActor(container);
    }

    private void handleInput() {
        if (!isGameOver) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                player.startMoving();
                if (!isZeppelinSoundPlaying) {
                    zeppelinSound.loop(ZEPPELIN_VOLUME);
                    isZeppelinSoundPlaying = true;
                }
                player.adjustSpeed(0.1f);
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
    }

    private void restartGame() {
        player.reset(620, 500);
        gameTime = MAX_GAME_TIME;
        zeppelinSound.stop();
        windSound.stop();
        isZeppelinSoundPlaying = false;
        isGameOver = false;
        player.setPaused(false);
    }

    private void updateUI() {
        speedLabel.setText("Speed: " + player.getSpeed());
        Vector2 wind = environmentalManager.getWind();
        windLabel.setText(String.format("Wind: %.2f, %.2f", wind.x, wind.y));
        timerLabel.setText(String.format("Time: %.2f s", gameTime));
        cloudsLabel.setText("Clouds: " + environmentalManager.getNumClouds());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        handleInput();
        if (isGameOver) {
            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            stage.draw();
            return;
        }

        if (player.hasGameStarted()) {
            gameTime -= delta;
        }

        player.update(delta);
        environmentalManager.update(player, delta);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(mapTexture, 0, 0, 800, 600);
        batch.draw(zeppelinTexture, player.getPosition().x, player.getPosition().y, zeppelinWidth, zeppelinHeight);

        environmentalManager.draw(batch);
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);

        environmentalManager.drawCompass(shapeRenderer, 70, 70);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(finalDestination.x, finalDestination.y, ENDPOINT_RADIUS);
        shapeRenderer.end();

        updateUI();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        checkGameEndConditions();
    }

    private void showDialog(String title, String message) {
        player.setPaused(true);
        Dialog dialog = new Dialog(title, uiSkin);
        dialog.text(message).pad(20);
        dialog.button("OK", true).pad(10);
        dialog.show(stage);
        dialog.setSize(400, 200);
        dialog.setPosition((Gdx.graphics.getWidth() - dialog.getWidth()) / 2, (Gdx.graphics.getHeight() - dialog.getHeight()) / 2);
    }

    private void checkGameEndConditions() {
        if (gameTime <= 0 && !player.isAtEndpoint(finalDestination)) {
            windSound.stop();
            zeppelinSound.stop();
            isZeppelinSoundPlaying = false;
            gameOverSound.play(GAME_OVER_VOLUME);
            isGameOver = true;
            showDialog("Game Over", "You are too late!");
        }

        if (player.isAtEndpoint(finalDestination)) {
            zeppelinSound.stop();
            windSound.stop();
            isZeppelinSoundPlaying = false;
            winSound.play(WIN_VOLUME);
            isGameOver = true;
            showDialog("Congratulations!", "You have reached Liverpool!");
        }

        if (player.isOutOfBounds(0)) {
            windSound.stop();
            zeppelinSound.stop();
            isZeppelinSoundPlaying = false;
            gameOverSound.play(GAME_OVER_VOLUME);
            isGameOver = true;
            showDialog("Game Over", "You went far away!");
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        container.setPosition(width - container.getWidth() - 10, 10);
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
        cloudTexture.dispose();
        stage.dispose();
        uiSkin.dispose();
        zeppelinSound.dispose();
        windSound.dispose();
        gameOverSound.dispose();
        winSound.dispose();
    }
}

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class ZeppelinSelection implements Screen {
    private final MyGdxGame game;
    private final SpriteBatch batch;
    private final AssetManager assetManager;
    private final Stage stage;
    private final Skin uiSkin;

    public ZeppelinSelection(MyGdxGame game, SpriteBatch batch, AssetManager assetManager, Skin uiSkin) {
        this.game = game;
        this.batch = batch;
        this.assetManager = assetManager;
        this.stage = new Stage(new ScreenViewport());
        this.uiSkin = uiSkin;

        Gdx.input.setInputProcessor(stage);
        createSelectionScreen();
    }

    private void createSelectionScreen() {
        stage.clear();
        Texture zeppelinTextureL19 = assetManager.get("images/Zepplin L19.png", Texture.class);
        Texture zeppelinTextureL20 = assetManager.get("images/Zepplin L20.png", Texture.class);

        ImageButton.ImageButtonStyle styleL19 = new ImageButton.ImageButtonStyle();
        styleL19.imageUp = new TextureRegionDrawable(zeppelinTextureL19);
        styleL19.imageDown = new TextureRegionDrawable(zeppelinTextureL19);

        ImageButton.ImageButtonStyle styleL20 = new ImageButton.ImageButtonStyle();
        styleL20.imageUp = new TextureRegionDrawable(zeppelinTextureL20);
        styleL20.imageDown = new TextureRegionDrawable(zeppelinTextureL20);

        ImageButton zeppelinButtonL19 = new ImageButton(styleL19);
        ImageButton zeppelinButtonL20 = new ImageButton(styleL20);

        zeppelinButtonL19.setSize(300, 100);
        zeppelinButtonL20.setSize(300, 100);

        zeppelinButtonL19.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, batch, assetManager.get("images/map1.png", Texture.class), zeppelinTextureL19, uiSkin, assetManager, 1));
            }
        });

        zeppelinButtonL20.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, batch, assetManager.get("images/map1.png", Texture.class), zeppelinTextureL20, uiSkin, assetManager, 1));
            }
        });

        Label titleLabel = new Label("Select Your Zeppelin", uiSkin, "title");
        titleLabel.setFontScale(2);

        TextButton aboutButton = new TextButton("About Game", uiSkin);
        aboutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showBackground(assetManager.get("images/info-background.png", Texture.class));
            }
        });

        TextButton historyButton = new TextButton("History", uiSkin);
        historyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showBackground(assetManager.get("images/history-background.png", Texture.class));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(titleLabel).colspan(2).padBottom(20).row();
        table.add(zeppelinButtonL19).size(300, 100).pad(10);
        table.add(zeppelinButtonL20).size(300, 100).pad(10).row();
        table.add(aboutButton).colspan(2).padTop(20).size(300, 100).row();
        table.add(historyButton).colspan(2).padTop(20).size(300, 100);

        stage.addActor(table);
    }

    private void showBackground(Texture backgroundTexture) {
        stage.clear();
        Image backgroundImage = new Image(new TextureRegionDrawable(backgroundTexture));
        backgroundImage.setSize(1200, 800);

        TextButton backButton = new TextButton("Back", uiSkin);
        backButton.setSize(100, 50);
        backButton.setPosition(1100, 50); // Adjust the position as needed
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createSelectionScreen(); // Recreate the selection screen after hiding the background image
            }
        });

        stage.addActor(backgroundImage);
        stage.addActor(backButton);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

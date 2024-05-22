package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MyGdxGame extends Game {
	private SpriteBatch batch;
	private AssetManager assetManager;
	private Skin uiSkin;

	@Override
	public void create() {
		batch = new SpriteBatch();
		assetManager = new AssetManager();

		// Load assets
		assetManager.load("fonts/default.fnt", BitmapFont.class);
		assetManager.load("fonts/larger-font.fnt", BitmapFont.class);
		assetManager.load("images/default-round.png", Texture.class);
		assetManager.load("images/default-round-down.png", Texture.class);
		assetManager.load("images/background.png", Texture.class);
		assetManager.load("images/map1.png", Texture.class);
		assetManager.load("images/Zepplin L19.png", Texture.class);
		assetManager.load("images/Zepplin L20.png", Texture.class);
		assetManager.load("sounds/zeppelinSound.mp3", Sound.class);
		assetManager.load("sounds/WindSound.mp3", Sound.class);
		assetManager.load("sounds/gameOverSound.mp3", Sound.class);
		assetManager.load("sounds/winSound.mp3", Sound.class);
		assetManager.load("ui/uiskin.atlas", TextureAtlas.class);

		// Finish loading
		assetManager.finishLoading();

		// Create the skin
		uiSkin = new Skin();
		uiSkin.addRegions(assetManager.get("ui/uiskin.atlas", TextureAtlas.class));
		uiSkin.add("default-font", assetManager.get("fonts/default.fnt", BitmapFont.class));
		uiSkin.add("larger-font", assetManager.get("fonts/larger-font.fnt", BitmapFont.class));
		uiSkin.add("background", new TextureRegionDrawable(new TextureRegion(assetManager.get("images/background.png", Texture.class))));

		// Load the JSON definitions
		uiSkin.load(Gdx.files.internal("ui/uiskin.json"));

		// Add a style for the title
		uiSkin.add("title", new Label.LabelStyle(new BitmapFont(), com.badlogic.gdx.graphics.Color.BLACK));

		// Set the initial screen to ZeppelinSelection
		setScreen(new ZeppelinSelection(this, batch, assetManager, uiSkin));
	}

	@Override
	public void dispose() {
		if (batch != null) batch.dispose();
		if (assetManager != null) assetManager.dispose();
		if (uiSkin != null) uiSkin.dispose();
	}
}

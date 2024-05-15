package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MyGdxGame extends Game {
	private SpriteBatch batch;
	private AssetManager assetManager;

	@Override
	public void create() {
		batch = new SpriteBatch();
		assetManager = new AssetManager();

		// Load assets
		assetManager.load("default.fnt", BitmapFont.class);
		assetManager.load("ui.atlas", TextureAtlas.class);
		assetManager.load("map1.png", Texture.class);
		assetManager.load("Zepplin L19.png", Texture.class);

		// Finish loading
		assetManager.finishLoading();

		// Create the skin
		Skin uiSkin = new Skin();
		uiSkin.add("default-font", assetManager.get("default.fnt", BitmapFont.class));
		TextureAtlas atlas = assetManager.get("ui.atlas", TextureAtlas.class);
		uiSkin.addRegions(atlas);

		// Load the JSON definitions
		uiSkin.load(Gdx.files.internal("uiskin.json"));

		// Set the game screen
		setScreen(new GameScreen(batch,
				assetManager.get("map1.png", Texture.class),
				assetManager.get("Zepplin L19.png", Texture.class),
				uiSkin));
	}

	@Override
	public void dispose() {
		if (batch != null) batch.dispose();
		if (assetManager != null) assetManager.dispose();
	}
}

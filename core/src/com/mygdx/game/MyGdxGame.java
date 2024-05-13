package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;

public class MyGdxGame extends Game {
	private SpriteBatch batch;
	private AssetManager assetManager;
	private Texture mapTexture;
	private Texture zeppelinTexture;

	@Override
	public void create() {
		batch = new SpriteBatch();
		assetManager = new AssetManager();

		loadAssets();
		setupInitialScreen();
	}

	private void loadAssets() {
		assetManager.load("map1.png", Texture.class);
		assetManager.load("Zepplin L19.png", Texture.class);
		assetManager.finishLoading();  // Ensure all assets are loaded before proceeding

		mapTexture = assetManager.get("map1.png", Texture.class);
		zeppelinTexture = assetManager.get("Zepplin L19.png", Texture.class);
	}

	private void setupInitialScreen() {
		if (assetsLoadedSuccessfully()) {
			setScreen(new GameScreen(batch, mapTexture, zeppelinTexture));
		} else {
			Gdx.app.error("MyGdxGame", "Error: Textures not properly loaded.");
		}
	}

	private boolean assetsLoadedSuccessfully() {
		return mapTexture != null && zeppelinTexture != null;
	}

	@Override
	public void dispose() {
		// Cleanly dispose all resources
		disposeSafely(batch);
		disposeSafely(assetManager);
	}

	private void disposeSafely(Disposable resource) {
		if (resource != null) {
			resource.dispose();
		}
	}
}

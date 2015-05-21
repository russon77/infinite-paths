/**
 TowerDefense : Infinite Tower Defense Game With User Created Maps
 Copyright (C) 2015 Tristan Kernan

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package net.noviden.towerdefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.noviden.towerdefense.MissileFactory.MissileManager;
import net.noviden.towerdefense.TurretFactory.BaseTurret;
import net.noviden.towerdefense.TurretFactory.BasicTurret;
import net.noviden.towerdefense.TurretFactory.ChaingunTurret;
import net.noviden.towerdefense.TurretFactory.HomingTurret;
import net.noviden.towerdefense.TurretFactory.RocketTurret;
import net.noviden.towerdefense.TurretFactory.ShotgunTurret;
import net.noviden.towerdefense.TurretFactory.TurretManager;

public class GameScreen implements Screen {

	private final TowerDefense towerDefense;

	private OrthographicCamera orthoCamera;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private Stage stage;

	private Map map;
	private UnitManager[] unitManagers;
	private net.noviden.towerdefense.TurretFactory.TurretManager turretManager;
	private Player player;

	private Vector3 vector3;

	private Point mouseLocation;

	private Label resourcesLabel, scoreLabel, healthLabel;

	private Table upgradeTable;
	private Label selectedTurretLabel, damageLabel, rangeLabel, uniqueModifierLabel;
	private TextButton upgradeDamageButton, upgradeRangeButton, upgradeUniqueModifierButton,
		sellButton;

	private Stage gameOverStage;
	private Table gameOverTable;
	private Label highScoreLabel, lastScoreLabel;
	private TextButton continueButton;

	public GameScreen(final TowerDefense towerDefense, Map gameMap) {

		this.towerDefense = towerDefense;

		float width = Gdx.graphics.getWidth(),
				height = Gdx.graphics.getHeight();

		// other init things
		vector3 = new Vector3();
		mouseLocation = new Point(Gdx.input.getX(), Gdx.input.getY());

		// select a map here
		map = gameMap;

		unitManagers = new UnitManager[map.paths.length];
		for (int i = 0; i< map.paths.length; i++) {
			unitManagers[i] = new UnitManager(map.paths[i]);
		}

		turretManager = new TurretManager();
		player = new Player();
		MissileManager.initialize();

		// camera and ui
		orthoCamera = new OrthographicCamera();
		orthoCamera.setToOrtho(true, TowerDefense.SCREEN_WIDTH, TowerDefense.SCREEN_HEIGHT);
		orthoCamera.position.set(TowerDefense.SCREEN_WIDTH / 2, TowerDefense.SCREEN_HEIGHT / 2, 0);
		orthoCamera.update();

		// set up ui
		stage = new Stage();

		Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

		Table table = new Table();
		table.setDebug(false);
		table.setFillParent(true);
		stage.addActor(table);

		TextButton exitButton = new TextButton("Exit", skin);
		exitButton.addListener(new ClickListener() {
		   @Override
		   public void clicked(InputEvent event, float x, float y) {
				towerDefense.setScreen(new MainMenuScreen(towerDefense));
		   }
	   	});

		Table selectTypeTable = new Table();
		selectTypeTable.setDebug(true);

		TextButton buttonSelectBasicTurret = new TextButton("Basic", skin, "default");
		buttonSelectBasicTurret.setChecked(true); // TODO
		TextButton buttonSelectChaingunTurret = new TextButton("Chaingun", skin);
		TextButton buttonSelectShotgunTurret = new TextButton("Shotgun", skin);
		TextButton buttonSelectRocketTurret = new TextButton("Rocket", skin);
		TextButton buttonSelectHomingTurret = new TextButton("Homing", skin);

		selectTypeTable.add(buttonSelectBasicTurret).fillX();
		selectTypeTable.add(buttonSelectChaingunTurret);
		selectTypeTable.row();
		selectTypeTable.add(buttonSelectRocketTurret).fillX();
		selectTypeTable.add(buttonSelectShotgunTurret).fillX();
		selectTypeTable.row();
		selectTypeTable.add(buttonSelectHomingTurret).fillX();

		buttonSelectBasicTurret.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.setSelectedTurretType(BaseTurret.Type.NORMAL);
			}
		});

		buttonSelectChaingunTurret.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.setSelectedTurretType(BaseTurret.Type.CHAINGUN);
			}
		});

		buttonSelectRocketTurret.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.setSelectedTurretType(BaseTurret.Type.ROCKET);
			}
		});

		buttonSelectShotgunTurret.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.setSelectedTurretType(BaseTurret.Type.SHOTGUN);
			}
		});

		buttonSelectHomingTurret.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.setSelectedTurretType(BaseTurret.Type.HOMING);
			}
		});

		// add in information view
		Table infoTable = new Table();
		infoTable.setDebug(true);

		resourcesLabel = new Label("Resources: " + player.getResources(), skin);
		scoreLabel = new Label("Score: " + player.getScore(), skin);
		healthLabel = new Label("Health: " + player.getHealth(), skin);

		infoTable.add(healthLabel);
		infoTable.row();
		infoTable.add(scoreLabel);
		infoTable.row();
		infoTable.add(resourcesLabel);

		// turret upgrade user interface
		upgradeTable = new Table();
		upgradeTable.setDebug(true);

		selectedTurretLabel = new Label("Upgrade Turret", skin);
		damageLabel = new Label("Damage (Current)", skin);
		upgradeDamageButton = new TextButton("+10 (R40)", skin);
		rangeLabel = new Label("Range (Current)", skin);
		upgradeRangeButton = new TextButton("+10 (R40)", skin);
		uniqueModifierLabel = new Label("Unique (Current)", skin);
		upgradeUniqueModifierButton = new TextButton("+1 (R40)", skin);

		sellButton = new TextButton("Sell (Value)", skin);

		upgradeTable.add(selectedTurretLabel);
		upgradeTable.add(sellButton);
		upgradeTable.row();
		upgradeTable.add(damageLabel);
		upgradeTable.add(upgradeDamageButton);
		upgradeTable.row();
		upgradeTable.add(rangeLabel);
		upgradeTable.add(upgradeRangeButton);
		upgradeTable.row();
		upgradeTable.add(uniqueModifierLabel);
		upgradeTable.add(upgradeUniqueModifierButton);

		upgradeTable.setVisible(false);

		table.add(exitButton);
		table.row();
		table.add(infoTable);
		table.row();
		table.add(selectTypeTable);
		table.row();
		table.add(upgradeTable);

		table.right();

		upgradeDamageButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				BaseTurret turret = player.getTurretSelectedForUpgrade();
				if (player.getResources() >=
						turret.getUpgradeCost()) {
					player.decreaseResources(turret.getUpgradeCost());
					turret.upgradeDamage();
				}
			}
		});

		upgradeRangeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				BaseTurret turret = player.getTurretSelectedForUpgrade();
				if (player.getResources() >=
						turret.getUpgradeCost()) {
					player.decreaseResources(turret.getUpgradeCost());
					player.getTurretSelectedForUpgrade().upgradeRange();
				}
			}
		});

		upgradeUniqueModifierButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				BaseTurret turret = player.getTurretSelectedForUpgrade();
				if (player.getResources() >=
						turret.getUpgradeCost()) {
					player.decreaseResources(turret.getUpgradeCost());
					turret.upgradeUniqueModifier();
				}
			}
		});

		sellButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.addResources(player.getTurretSelectedForUpgrade().getWorth());
				turretManager.removeTurret(player.getTurretSelectedForUpgrade());
				player.setState(Player.State.TURRET_PLACE);
				upgradeTable.setVisible(false);
			}
		});

		// set up gameOver menu
		gameOverTable = new Table();
		gameOverTable.setFillParent(true);
		gameOverTable.setDebug(true);
		gameOverTable.setVisible(false);

		highScoreLabel = new Label("High Score: ", skin);
		lastScoreLabel = new Label("Last Score: ", skin);
		continueButton = new TextButton("Continue", skin);

		gameOverTable.add(highScoreLabel);
		gameOverTable.row();
		gameOverTable.add(lastScoreLabel);
		gameOverTable.row();
		gameOverTable.add(continueButton);

		gameOverTable.center();

		gameOverStage = new Stage();
		gameOverStage.addActor(gameOverTable);

		continueButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				towerDefense.setScreen(new MainMenuScreen(towerDefense));
				dispose();
			}
		});

		// now set input processing
		InputMultiplexer inputMultiplexer = new InputMultiplexer();

		inputMultiplexer.addProcessor(gameOverStage);
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(new GestureDetector(new MyGestureListener()));
		inputMultiplexer.addProcessor(new MyInputProcessor());

		Gdx.input.setInputProcessor(inputMultiplexer);

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
	}

	public void render (float deltaTime) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updateUi();

		orthoCamera.update();
		batch.setProjectionMatrix(orthoCamera.combined);
		shapeRenderer.setProjectionMatrix(orthoCamera.combined);

		for (UnitManager unitManager : unitManagers) {
			unitManager.act(deltaTime, player);
			turretManager.act(deltaTime / (float) unitManagers.length, unitManager);
			CollisionManager.processCollisions(unitManager);
		}

		net.noviden.towerdefense.MissileFactory.MissileManager.act(deltaTime);
		player.act(deltaTime);

		shapeRenderer.setAutoShapeType(true);

		batch.begin();
		shapeRenderer.begin();

		if (player.getState() == Player.State.TURRET_PLACE) {
			drawOpaqueSelectedTurret();
		}

		map.draw(shapeRenderer);
		for (UnitManager unitManager : unitManagers) {
			unitManager.draw(shapeRenderer);
		}
		net.noviden.towerdefense.MissileFactory.MissileManager.draw(shapeRenderer);
		turretManager.draw(shapeRenderer);

		if (upgradeTable.isVisible()) {
			BaseTurret turret = player.getTurretSelectedForUpgrade();

			shapeRenderer.set(ShapeRenderer.ShapeType.Line);
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.circle(turret.getLocation().x, turret.getLocation().y,
					turret.getRadius());
			shapeRenderer.circle(turret.getLocation().x, turret.getLocation().y,
					turret.getRange());
		}

		shapeRenderer.end();
		batch.end();

		// check if game is over
		if (player.getHealth() <= 0) {
			gameOver();
			dispose();
		}

		stage.act(deltaTime);
		stage.draw();

		gameOverStage.act(deltaTime);
		gameOverStage.draw();
	}

	private void drawOpaqueSelectedTurret() {
		// draw the temp turret at mouseLocation, along with its range
		shapeRenderer.set(ShapeRenderer.ShapeType.Line);

		if (player.onCooldownForPlacingTurrets() ||
				!player.canAffordSelectedTurret()) {
			shapeRenderer.setColor(Color.RED);
		} else {
			shapeRenderer.setColor(Color.GREEN);
		}

		shapeRenderer.circle(mouseLocation.x, mouseLocation.y, 10.0f);

		float range = 0.0f;
		switch (player.getSelectedTurretType()) {
			case NORMAL:
				range = BasicTurret.BASE_RANGE;
				break;
			case CHAINGUN:
				range = ChaingunTurret.BASE_RANGE;
				break;
			case ROCKET:
				range = RocketTurret.BASE_RANGE;
				break;
			case SHOTGUN:
				range = ShotgunTurret.BASE_RANGE;
				break;
			case HOMING:
				range = HomingTurret.BASE_RANGE;
				break;
		}

		shapeRenderer.circle(mouseLocation.x, mouseLocation.y, range);
	}

	private void updateUi() {
		// update all labels/textviews
		resourcesLabel.setText("Resources: " + player.getResources());
		scoreLabel.setText("Score: " + player.getScore());
		healthLabel.setText("Health: " + player.getHealth());

		// update upgrade ui
		if (upgradeTable.isVisible()) {
			BaseTurret turret = player.getTurretSelectedForUpgrade();

			selectedTurretLabel.setText("Upgrade Turret " +
					turret.getId());
			damageLabel.setText("Damage (" + turret.getDamage() + ")");
			rangeLabel.setText("Range (" + turret.getRange() + ")");
			uniqueModifierLabel.setText(turret.getUniqueModifierName() + " (" +
				turret.getUniqueModifierValue() + ")");
			sellButton.setText("Sell (R" + turret.getWorth() + ")");

			upgradeDamageButton.setText("+++ (R" + turret.getUpgradeCost() + ")");
			upgradeRangeButton.setText("+++ (R" + turret.getUpgradeCost() + ")");
			upgradeUniqueModifierButton.setText("+++ (R" + turret.getUpgradeCost() + ")");
		}
	}

	private void gameOver() {
		// show gameOverTable, with button for 'continue'
		if (player.getScore() > towerDefense.highScore) {
			towerDefense.highScore = player.getScore();
			highScoreLabel.setText("NEW HIGH SCORE! " + towerDefense.highScore);
		} else {
			highScoreLabel.setText("High Score: " + towerDefense.highScore);
		}

		lastScoreLabel.setText("Last score: " + player.getScore());

		gameOverTable.setVisible(true);
	}

	public void dispose() {

	}

	public void pause() {}
	public void resume() {}

	public void show() {}
	public void hide() {}

	public void resize(int width, int height) {}


	private class MyGestureListener implements GestureDetector.GestureListener {

		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {

			return false;
		}

		@Override
		public boolean tap(float screenX, float screenY, int count, int button) {

			vector3.set(screenX, screenY, 0);
			orthoCamera.unproject(vector3);

			Point targetLocation = new Point(vector3.x, vector3.y);

			switch (player.getState()) {
				case TURRET_PLACE:
					if (!player.onCooldownForPlacingTurrets() &&
							player.canAffordSelectedTurret() &&
							turretManager.validPlacementForTurret(targetLocation, map)) {

						turretManager.addTurret(targetLocation, player.getSelectedTurretType());
						player.purchaseTurret();
						upgradeTable.setVisible(false);
					}

					break;
				case VIEW:
					BaseTurret turret = turretManager.findTurretByLocation(targetLocation);
					if (turret != null) {
						player.setState(Player.State.TURRET_UPGRADE);
						player.setTurretForUpgrade(turret);
						upgradeTable.setVisible(true);
					}

					break;
			}

			return true;
		}

		@Override
		public boolean longPress(float x, float y) {

			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {

			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {

			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button) {

			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {

			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
							 Vector2 pointer1, Vector2 pointer2) {

			return false;
		}

	}

	private class MyInputProcessor implements InputProcessor {

		@Override
		public boolean keyDown(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer,
								 int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {


			return true;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			vector3.set(screenX, screenY, 0);
			orthoCamera.unproject(vector3);

			mouseLocation.x = vector3.x; mouseLocation.y = vector3.y;

			if (turretManager.validPlacementForTurret(mouseLocation, map)) {
				player.setState(Player.State.TURRET_PLACE);
			} else {
				player.setState(Player.State.VIEW);
			}

			return false;
		}

		@Override
		public boolean scrolled(int amount) {

			return false;
		}

	}
}

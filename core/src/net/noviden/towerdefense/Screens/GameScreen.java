/**
 Infinite Paths : Infinite Tower Defense Game With User Created Maps
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


package net.noviden.towerdefense.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
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

import net.noviden.towerdefense.CollisionManager;
import net.noviden.towerdefense.GameSettings;
import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.MapTransformation.Transformation;
import net.noviden.towerdefense.MapTransformation.TurretTransformation;
import net.noviden.towerdefense.MissileFactory.MissileManager;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Player;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.TowerDefense;
import net.noviden.towerdefense.TurretFactory.BaseTurret;
import net.noviden.towerdefense.TurretFactory.BasicTurret;
import net.noviden.towerdefense.TurretFactory.BuffTurret;
import net.noviden.towerdefense.TurretFactory.ChaingunTurret;
import net.noviden.towerdefense.TurretFactory.HomingTurret;
import net.noviden.towerdefense.TurretFactory.RocketTurret;
import net.noviden.towerdefense.TurretFactory.ShotgunTurret;
import net.noviden.towerdefense.TurretFactory.TurretManager;
import net.noviden.towerdefense.UnitFactory.UnitManager;

import java.text.SimpleDateFormat;
import java.util.Date;

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
	private FPSLogger fpsLogger;

	private Point mouseLocation;

	private Label resourcesLabel, scoreLabel, healthLabel, numTurretsLabel, numUnitsKilledLabel,
		timeLabel;

	private Date startDate;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

	private Table rootTable, upgradeTable;
	private Label selectedTurretLabel, damageLabel, rangeLabel, uniqueModifierLabel;
	private TextButton upgradeDamageButton, upgradeRangeButton, upgradeUniqueModifierButton,
		sellButton;

	private Stage gameOverStage;
	private Table gameOverTable;
	private Label highScoreLabel, lastScoreLabel;
	private TextButton continueButton;
	private final TextButton[] selectTurretButtons;
	final TextButton pauseButton;

	private Label fpsLabel;

	private boolean isPaused;

	private Transformation _transformation;

	public GameScreen(final TowerDefense towerDefense, Map gameMap) {

		// store our callback to the Game class
		this.towerDefense = towerDefense;

		// initialize our input vector here, to save the precious gc
		vector3 = new Vector3();
		// get the initial mouse location, where we draw the next turret to be created
		mouseLocation = new Point(Gdx.input.getX(), Gdx.input.getY());

		// get the map from the arguments
		map = gameMap;

		// create a unit manager for each path on this map
		Path[] paths = map.getPaths();
		unitManagers = new UnitManager[paths.length];
		for (int i = 0; i< paths.length; i++) {
			unitManagers[i] = new UnitManager(paths[i], map.getSettings());
		}

		turretManager = new TurretManager();
		player = new Player(gameMap.getSettings());
		MissileManager.initialize();

		// set up the camera
		// FIXME there's something fishy with this and the screen size
		orthoCamera = new OrthographicCamera();
		orthoCamera.setToOrtho(true, TowerDefense.SCREEN_WIDTH, TowerDefense.SCREEN_HEIGHT);
		orthoCamera.position.set(TowerDefense.SCREEN_WIDTH / 2, TowerDefense.SCREEN_HEIGHT / 2, 0);
		orthoCamera.update();

		// set up the ui by creating the base Stage, where the resources table, upgrade table,
		// 	information table and other fun takes place
		stage = new Stage();

		Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

		rootTable = new Table();
		rootTable.setFillParent(true);
		stage.addActor(rootTable);

		TextButton exitButton = new TextButton("Main Menu", skin);
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				towerDefense.setScreen(new MainMenuScreen(towerDefense));
			}
		});

		pauseButton = new TextButton("Pause", skin);
		pauseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				pauseGame();
			}
		});

		Table selectTypeTable = new Table();

		final TextButton buttonSelectBasicTurret = new TextButton("Basic (R50)", skin, "default");
		final TextButton buttonSelectChaingunTurret = new TextButton("Chaingun (R50)", skin);
		final TextButton buttonSelectShotgunTurret = new TextButton("Shotgun (R50)", skin);
		final TextButton buttonSelectRocketTurret = new TextButton("Rocket (R50)", skin);
		final TextButton buttonSelectHomingTurret = new TextButton("Homing (R50)", skin);
		final TextButton buttonSelectBuffTurret = new TextButton("Buff (R50)", skin);

		selectTurretButtons = new TextButton[]
				{
						buttonSelectBasicTurret, buttonSelectChaingunTurret,
						buttonSelectShotgunTurret, buttonSelectRocketTurret,
						buttonSelectHomingTurret, buttonSelectBuffTurret
				};

		if (!map.getSettings().isTurretTypeDisabled(BaseTurret.Type.NORMAL))
			selectTypeTable.add(buttonSelectBasicTurret);
		if (!map.getSettings().isTurretTypeDisabled(BaseTurret.Type.CHAINGUN))
			selectTypeTable.add(buttonSelectChaingunTurret);


		if (!map.getSettings().isTurretTypeDisabled(BaseTurret.Type.ROCKET))
			selectTypeTable.add(buttonSelectRocketTurret);
		if (!map.getSettings().isTurretTypeDisabled(BaseTurret.Type.SHOTGUN))
			selectTypeTable.add(buttonSelectShotgunTurret);


		if (!map.getSettings().isTurretTypeDisabled(BaseTurret.Type.HOMING))
			selectTypeTable.add(buttonSelectHomingTurret);
		if (!map.getSettings().isTurretTypeDisabled(BaseTurret.Type.BUFF))
			selectTypeTable.add(buttonSelectBuffTurret);

		buttonSelectBasicTurret.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.setTurretSelectedForPurchase(
						new BasicTurret(mouseLocation.clone()));
				resetVisualTurretSelection();
				buttonSelectBasicTurret.setColor(Color.NAVY);
			}
		});

		buttonSelectChaingunTurret.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.setTurretSelectedForPurchase(
						new ChaingunTurret(mouseLocation.clone()));
				resetVisualTurretSelection();
				buttonSelectChaingunTurret.setColor(Color.NAVY);
			}
		});

		buttonSelectRocketTurret.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.setTurretSelectedForPurchase(
						new RocketTurret(mouseLocation.clone()));
				resetVisualTurretSelection();
				buttonSelectRocketTurret.setColor(Color.NAVY);
			}
		});

		buttonSelectShotgunTurret.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.setTurretSelectedForPurchase(
						new ShotgunTurret(mouseLocation.clone()));
				resetVisualTurretSelection();
				buttonSelectShotgunTurret.setColor(Color.NAVY);
			}
		});

		buttonSelectHomingTurret.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.setTurretSelectedForPurchase(
						new HomingTurret(mouseLocation.clone()));
				resetVisualTurretSelection();
				buttonSelectHomingTurret.setColor(Color.NAVY);
			}
		});

		buttonSelectBuffTurret.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.setTurretSelectedForPurchase(
						new BuffTurret(mouseLocation.clone(), turretManager));
				resetVisualTurretSelection();
				buttonSelectBuffTurret.setColor(Color.NAVY);
			}
		});

		resetVisualTurretSelection();

		// add in information view
		Table infoTable = new Table();

		resourcesLabel = new Label("Resources: R" + player.getResources(), skin);
		scoreLabel = new Label("Score: " + player.getScore(), skin);
		healthLabel = new Label("Health: " + player.getHealth(), skin);
		numTurretsLabel = new Label("Turrets Created: " + player.getNumTurretsCreated(), skin);
		numUnitsKilledLabel = new Label("Units killed: " + player.getNumUnitsKilled(), skin);

		startDate = new Date();
		timeLabel = new Label("Elapsed: ", skin);

		infoTable.add(healthLabel).pad(5.0f);
		infoTable.add(scoreLabel).pad(5.0f);
		infoTable.add(resourcesLabel).pad(5.0f);
		infoTable.add(numTurretsLabel).pad(5.0f);
		infoTable.add(numUnitsKilledLabel).pad(5.0f);
		infoTable.add(timeLabel).pad(10.0f);

		// turret upgrade user interface
		upgradeTable = new Table();

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

		Table menuTable = new Table();
		menuTable.add(pauseButton, exitButton);

		fpsLabel = new Label("0fps", skin);

		Table topHalfTable = new Table(), botHalfTable = new Table();
//		topHalfTable.setDebug(true); botHalfTable.setDebug(true);

		topHalfTable.add(infoTable);
		topHalfTable.row();
		topHalfTable.add(selectTypeTable);
		topHalfTable.row();
		topHalfTable.add(upgradeTable).expandX().right();

		botHalfTable.add(fpsLabel).expandX().left().expandY().bottom();
		botHalfTable.add(menuTable).expandX().right().expandY().bottom();

		rootTable.add(topHalfTable).fill().top();
		rootTable.row();
		rootTable.add(botHalfTable).fill().expand();

		/*** END OF TABLES AND ALIGNMENT ***/

		upgradeDamageButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				BaseTurret turret = player.getTurretSelectedForUpgrade();
				if (player.getResources() >=
						turret.getUpgradeCost() &&
						turret.canUpgradeDamage()) {
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
						turret.getUpgradeCost() &&
						turret.canUpgradeUniqueModifier()) {
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

		// set up gameOver menu, which includes its own stage for alignment purposes
		gameOverTable = new Table();
		gameOverTable.setFillParent(true);
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

		// now set input processing, by adding all input sources to the inputMultiplexer
		InputMultiplexer inputMultiplexer = new InputMultiplexer();

		inputMultiplexer.addProcessor(gameOverStage);
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(new GestureDetector(new MyGestureListener()));
		inputMultiplexer.addProcessor(new MyInputProcessor());

		Gdx.input.setInputProcessor(inputMultiplexer);

		// instantiate the spritebatch, where all drawing takes place, and shapeRenderer likewise
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		// game starts off going fast!
		isPaused = false;

		// instantiate transformation
		_transformation = new TurretTransformation(turretManager);

		// instantiate the fpsLogger
		fpsLogger = new FPSLogger();
	}

	public void render (float deltaTime) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// log the FPS to the console
//		fpsLogger.log();

		// write all updates to the game state to the game user interface
		updateUi();

		orthoCamera.update();
		batch.setProjectionMatrix(orthoCamera.combined);
		shapeRenderer.setProjectionMatrix(orthoCamera.combined);

		// call all actors to `act`, or if game is over, set appropriate state
		if (!isPaused && player.getHealth() > 0) {

			MissileManager.act(deltaTime);

			for (UnitManager unitManager : unitManagers) {
				unitManager.act(deltaTime, player);
				turretManager.act(deltaTime, unitManager);
				CollisionManager.processCollisions(unitManager);
			}
		} else if (!isPaused) {
			// otherwise player is dead
			gameOver();
		}

		player.act(deltaTime);

		// call the drawing functions
		shapeRenderer.setAutoShapeType(true);

		batch.begin();
		shapeRenderer.begin();

		if (player.getState() == Player.State.TURRET_PLACE) {
			drawOpaqueSelectedTurret();
		}

		if (upgradeTable.isVisible()) {
			BaseTurret turret = player.getTurretSelectedForUpgrade();

			if (turret != null) {
				turret.drawOpaque(shapeRenderer);
			}
		}

		map.draw(shapeRenderer);
		for (UnitManager unitManager : unitManagers) {
			unitManager.draw(shapeRenderer);
		}
		net.noviden.towerdefense.MissileFactory.MissileManager.draw(shapeRenderer);
		turretManager.draw(shapeRenderer);

		shapeRenderer.end();
		batch.end();

		// act and draw all stages
		stage.act(deltaTime);
		stage.draw();

		gameOverStage.act(deltaTime);
		gameOverStage.draw();

		// transform map
//		_transformation.transform(map, deltaTime);
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

		BaseTurret turret = player.getTurretSelectedForPurchase();
		if (turret != null) {
			turret.drawOpaque(shapeRenderer);
		}
	}

	private void updateUi() {
		// update all labels/textviews
		resourcesLabel.setText("Resources: R" + player.getResources());
		scoreLabel.setText("Score: " + player.getScore());
		healthLabel.setText("Health: " + player.getHealth());
		numTurretsLabel.setText("Turrets Built: " + player.getNumTurretsCreated());
		numUnitsKilledLabel.setText("Units killed: " + player.getNumUnitsKilled());

		Date elapsed = new Date(new Date().getTime() - startDate.getTime());
		timeLabel.setText("Elapsed: " + simpleDateFormat.format(elapsed));

		// update fps
		fpsLabel.setText("" + Gdx.graphics.getFramesPerSecond() + "fps");

		// update upgrade ui
		BaseTurret turret = player.getTurretSelectedForUpgrade();
		if (upgradeTable.isVisible() && turret != null) {

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
		/*
		// show gameOverTable, with button for 'continue'
		if (player.getScore() > towerDefense.highScore) {
			towerDefense.highScore = player.getScore();
			highScoreLabel.setText("NEW HIGH SCORE! " + towerDefense.highScore);
		} else {
			highScoreLabel.setText("High Score: " + towerDefense.highScore);
		}
		*/

		lastScoreLabel.setText("Last score: " + player.getScore());

		gameOverTable.setVisible(true);
	}

	private void resetVisualTurretSelection() {
		for (TextButton textButton : selectTurretButtons) {
			textButton.setColor(Color.GRAY);
		}
	}

	private void pauseGame() {
		isPaused = !isPaused;

		if (isPaused) {
			pauseButton.setText("Unpause");
		} else {
			pauseButton.setText("Pause");
		}
	}

	private void processKeyboardInput(int pKeycode) {
		// unmap the given keycode to the default keycode
		GameSettings.Actions action = GameSettings.getShortcutAction(pKeycode);

		if (action == null) {
			return;
		}

		switch (action) {
			case TOGGLE_SHOW_INTERFACE:
				rootTable.setVisible(
						!rootTable.isVisible());
				upgradeTable.setVisible(false);

				break;
			case TOGGLE_SHOW_FPS:
				fpsLabel.setVisible( !fpsLabel.isVisible() );

				break;
			case PAUSE_GAME:
				pauseGame();

				break;
			case QUICK_EXIT:
				towerDefense.setScreen(new MainMenuScreen(towerDefense));
				dispose();

				break;
			case SELECT_BASIC_TURRET:
				if (!map.getSettings().isTurretTypeDisabled(BaseTurret.Type.NORMAL))
					player.setTurretSelectedForPurchase(
							new BasicTurret(mouseLocation.clone()));
				resetVisualTurretSelection();

				break;
			case SELECT_BUFF_TURRET:
				if (!map.getSettings().isTurretTypeDisabled(BaseTurret.Type.BUFF))
					player.setTurretSelectedForPurchase(
							new BuffTurret(mouseLocation.clone(), turretManager));
				resetVisualTurretSelection();

				break;
			case SELECT_CHAINGUN_TURRET:
				if (!map.getSettings().isTurretTypeDisabled(BaseTurret.Type.CHAINGUN))
					player.setTurretSelectedForPurchase(
							new ChaingunTurret(mouseLocation.clone()));
				resetVisualTurretSelection();

				break;
			case SELECT_HOMING_TURRET:
				if (!map.getSettings().isTurretTypeDisabled(BaseTurret.Type.HOMING))
					player.setTurretSelectedForPurchase(
							new HomingTurret(mouseLocation.clone()));
				resetVisualTurretSelection();

				break;
			case SELECT_ROCKET_TURRET:
				if (!map.getSettings().isTurretTypeDisabled(BaseTurret.Type.ROCKET))
					player.setTurretSelectedForPurchase(
							new RocketTurret(mouseLocation.clone()));
				resetVisualTurretSelection();

				break;
			case SELECT_SHOTGUN_TURRET:
				if (!map.getSettings().isTurretTypeDisabled(BaseTurret.Type.SHOTGUN))
					player.setTurretSelectedForPurchase(
							new ShotgunTurret(mouseLocation.clone()));
				resetVisualTurretSelection();

				break;
			case QUICK_SELL:
				player.addResources(player.getTurretSelectedForUpgrade().getWorth());
				turretManager.removeTurret(player.getTurretSelectedForUpgrade());
				player.setState(Player.State.TURRET_PLACE);
				upgradeTable.setVisible(false);

				break;
		}
	}

	public void dispose() {}

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

			// if the game is paused, or if game is completed,
			//  do not process any input outside of the gameOver stage
			if (isPaused ||
					gameOverTable.isVisible()) {
				return true;
			}

			// unmap the input coordinates
			vector3.set(screenX, screenY, 0);
			orthoCamera.unproject(vector3);

			Point targetLocation = new Point(vector3.x, vector3.y);

			switch (player.getState()) {
				case TURRET_PLACE:
					if (!player.onCooldownForPlacingTurrets() &&
							player.canAffordSelectedTurret() &&
							turretManager.validPlacementForTurret(targetLocation, map)) {

						turretManager.addTurret(player.getTurretSelectedForPurchase());
						player.purchaseTurret();

						upgradeTable.setVisible(true);

						resetVisualTurretSelection();

						return true;
					}

					break;
				case VIEW:
				case TURRET_UPGRADE:
					BaseTurret turret = turretManager.findTurretByLocation(targetLocation);
					if (turret != null) {

						if (turret.equals(player.getTurretSelectedForUpgrade())) {
							player.setState(Player.State.VIEW);
							player.setTurretForUpgrade(null);
							upgradeTable.setVisible(false);
						} else {
							player.setState(Player.State.TURRET_UPGRADE);
							player.setTurretForUpgrade(turret);
							upgradeTable.setVisible(true);

							return true;
						}
					}

					break;
			}

			return false;
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

			processKeyboardInput(keycode);

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

			// for setting the proper mouseLocation and for setting the proper state
			vector3.set(screenX, screenY, 0);
			orthoCamera.unproject(vector3);

			mouseLocation.x = vector3.x; mouseLocation.y = vector3.y;

			if (turretManager.validPlacementForTurret(mouseLocation, map)) {
				player.setState(Player.State.TURRET_PLACE);

				BaseTurret turret = player.getTurretSelectedForPurchase();
				if (turret != null) {
					turret.getLocation().set(mouseLocation);
				}

			} else {
				player.setState(Player.State.VIEW);
			}

			return true;
		}

		@Override
		public boolean scrolled(int amount) {

			return false;
		}

	}
}

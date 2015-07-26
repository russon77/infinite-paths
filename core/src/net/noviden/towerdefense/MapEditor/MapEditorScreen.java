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

package net.noviden.towerdefense.MapEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.MapSettings;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.TowerDefense;
import net.noviden.towerdefense.TurretFactory.BaseTurret;
import net.noviden.towerdefense.UnitFactory.UnitManager;

import java.util.ArrayList;

public class MapEditorScreen implements Screen {

    private final TowerDefense _towerDefense;

    private Stage stage;
    private Map _originalMapReference; // reference to original map, to be overwritten for saving
    private Map _map; // copy of map that is being edited

    private OrthographicCamera _orthoCamera;
    private SpriteBatch _spriteBatch;
    private ShapeRenderer _shapeRenderer;

    private int _selectedPathIndex;

    private UnitManager[] _unitManagers;
    private MapSettings _mapSettings;

    private Table rootTable;
    private InputMultiplexer inputMultiplexer;

    public MapEditorScreen(final TowerDefense towerDefense, Map pMap) {
        _towerDefense = towerDefense;
        _originalMapReference = pMap;
        _map = pMap.clone();

        _orthoCamera = new OrthographicCamera();
        _orthoCamera.setToOrtho(true, TowerDefense.SCREEN_WIDTH, TowerDefense.SCREEN_HEIGHT);
        _orthoCamera.update();

        _spriteBatch = new SpriteBatch();
        _shapeRenderer = new ShapeRenderer();
        _shapeRenderer.setAutoShapeType(true);

        // make use of map settings to have units travel slightly faster, for display niceties
        _mapSettings = new MapSettings();
        _mapSettings.putValue(MapSettings.UNIT_INITIAL_SPEED_KEY, 150.0f);

        // instantiate/create unit managers for paths
        resetUnitManagers();

        // there must exist at least one path if we are EDITING an existing map
        _selectedPathIndex = 0;

        final Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        stage = new Stage();

        rootTable = new Table();
        rootTable.setFillParent(true);

        Table pathAddDeleteTable = new Table();

        TextButton saveButton = new TextButton("Save", skin);
        TextButton exitButton = new TextButton("Exit Without Saving", skin);
        TextButton addPathButton = new TextButton("Add Path", skin);
        TextButton deletePathButton = new TextButton("Delete Current Path", skin);
        TextButton deleteLastNodeOnPathButton =
                new TextButton("Delete End Node", skin);
        TextButton displayOptionsButton = new TextButton("Options", skin);

        pathAddDeleteTable.add(deletePathButton);
        pathAddDeleteTable.add(addPathButton);
        pathAddDeleteTable.add(saveButton);
        pathAddDeleteTable.add(exitButton);
        pathAddDeleteTable.row();
        pathAddDeleteTable.add(deleteLastNodeOnPathButton).expandX().left();
        pathAddDeleteTable.add(displayOptionsButton);

        Table pathSelectorTable = new Table();

        Texture leftArrowTexture = new Texture(Gdx.files.internal("leftArrow.png"));
        Image leftArrowImage = new Image(leftArrowTexture);
        Texture rightArrowTexture = new Texture(Gdx.files.internal("rightArrow.png"));
        Image rightArrowImage = new Image(rightArrowTexture);

        final Label selectedPathLabel = new Label("0", skin);

        pathSelectorTable.add(leftArrowImage).pad(8.0f);
        pathSelectorTable.add(selectedPathLabel).pad(8.0f);
        pathSelectorTable.add(rightArrowImage).pad(8.0f);

        // options table

        // set up options table
        final Table optionsTable = new Table();
        optionsTable.setVisible(false);

        MapSettings currentSettings = _map.getSettings();

        Label initialPlayerHealthLabel = new Label("Initial Player Health:", skin);
        final TextField initialPlayerHealthText =
                new TextField("" +
                        (int) currentSettings.getValue(MapSettings.PLAYER_INITIAL_HEALTH_KEY), skin);
        Label initialPlayerResourcesLabel = new Label("Initial Player Resources:", skin);
        final TextField initialPlayerResourcesText =
                new TextField("" +
                        (int) currentSettings.getValue(MapSettings.PLAYER_INITIAL_RESOURCES_KEY), skin);
        Label initialUnitHealthLabel = new Label("Initial Unit Health:", skin);
        final TextField initialUnitHealthText =
                new TextField("" +
                        currentSettings.getValue(MapSettings.UNIT_INITIAL_HEALTH_KEY), skin);
        Label initialUnitMovementSpeedLabel = new Label("Initial Unit Movement Speed:", skin);
        final TextField initialUnitMovementSpeedText =
                new TextField("" +
                        currentSettings.getValue(MapSettings.UNIT_INITIAL_SPEED_KEY), skin);
        Label initialUnitDamageLabel = new Label("Initial Unit Damage:", skin);
        final TextField initialUnitDamageText =
                new TextField("" +
                        (int) currentSettings.getValue(MapSettings.UNIT_INITIAL_DAMAGE_KEY), skin);
        Label defaultUnitSpawnRateLabel = new Label("Unit Spawn Rate:", skin);
        final TextField defaultUnitSpawnRateText =
                new TextField("" +
                        currentSettings.getValue(MapSettings.UNIT_SPAWN_RATE_KEY), skin);

        // TODO Implement this button
        TextButton displayWavesCreatorButton = new TextButton("Waves Creator", skin);
        TextButton displayTurretsButton = new TextButton("Disable Turrets", skin);

        optionsTable.add(initialPlayerHealthLabel, initialPlayerHealthText);
        optionsTable.row();
        optionsTable.add(initialPlayerResourcesLabel, initialPlayerResourcesText);
        optionsTable.row();
        optionsTable.add(initialUnitHealthLabel, initialUnitHealthText);
        optionsTable.row();
        optionsTable.add(initialUnitMovementSpeedLabel, initialUnitMovementSpeedText);
        optionsTable.row();
        optionsTable.add(initialUnitDamageLabel, initialUnitDamageText);
        optionsTable.row();
        optionsTable.add(defaultUnitSpawnRateLabel, defaultUnitSpawnRateText);
        optionsTable.row();
        optionsTable.add(displayTurretsButton);

        final Table disableTurretsTable = new Table();
        disableTurretsTable.setVisible(false);

        final CheckBox disableChaingunTurretCheckbox =
                new CheckBox("Disable Chaingun", skin);
        final CheckBox disableBasicTurretCheckbox =
                new CheckBox("Disable Basic", skin);
        final CheckBox disableBuffTurretCheckbox =
                new CheckBox("Disable Buff", skin);
        final CheckBox disableHomingTurretCheckbox =
                new CheckBox("Disable Homing", skin);
        final CheckBox disableRocketTurretCheckbox =
                new CheckBox("Disable Rocket", skin);
        final CheckBox disableShotgunTurretCheckbox =
                new CheckBox("Disable Shotgun", skin);

        disableTurretsTable.add(disableChaingunTurretCheckbox);
        disableTurretsTable.row();
        disableTurretsTable.add(disableBasicTurretCheckbox);
        disableTurretsTable.row();
        disableTurretsTable.add(disableBuffTurretCheckbox);
        disableTurretsTable.row();
        disableTurretsTable.add(disableHomingTurretCheckbox);
        disableTurretsTable.row();
        disableTurretsTable.add(disableRocketTurretCheckbox);
        disableTurretsTable.row();
        disableTurretsTable.add(disableShotgunTurretCheckbox);

        // transformations table
        TextButton displayTransformationsTableButton =
                new TextButton("Enable Transformations", skin);

        CheckBox enableRandomTransformationCheckbox =
                new CheckBox("Random", skin);
        CheckBox enableTurretTransformationCheckbox =
                new CheckBox("Turret", skin);
        CheckBox enableUnitTransformationCheckbox =
                new CheckBox("Unit", skin);

        final Table transformationsTable = new Table();
        transformationsTable.setVisible(false);

        transformationsTable.add(enableRandomTransformationCheckbox).row();
        transformationsTable.add(enableTurretTransformationCheckbox).row();
        transformationsTable.add(enableUnitTransformationCheckbox).row();

        optionsTable.row();
        optionsTable.add(disableTurretsTable);
        optionsTable.row();
        optionsTable.add(displayTransformationsTableButton);
        optionsTable.row();
        optionsTable.add(transformationsTable);

        // finally, add tables to root table and set input processing

        rootTable.add(pathSelectorTable).expandX().left().expandY().top();

        rootTable.add(pathAddDeleteTable).expandX().right().expandY().top();

        rootTable.row();

        rootTable.add(optionsTable).expandX().right().expandY().top();

        stage.addActor(rootTable);

        inputMultiplexer = new InputMultiplexer(stage,
                new GestureDetector(new MyGestureListener()));

        Gdx.input.setInputProcessor(inputMultiplexer);

        // listeners
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int index = _towerDefense.maps.indexOf(_originalMapReference);
                String message = "";

                int playerHealth = Integer.parseInt(initialPlayerHealthText.getText()),
                        playerResources = Integer.parseInt(initialPlayerResourcesText.getText()),
                        unitDamage = Integer.parseInt(initialUnitDamageText.getText());
                float unitHealth = Float.parseFloat(initialUnitHealthText.getText()),
                        unitSpeed = Float.parseFloat(initialUnitMovementSpeedText.getText()),
                        unitSpawnRate = Float.parseFloat(defaultUnitSpawnRateText.getText());

                // validation
                if (playerHealth <= 0.0f) {
                    displayErrorMessage(
                            "Error! Player cannot start with 0.0 health", skin);
                    return;
                } else if (unitHealth <= 0.0f) {
                    displayErrorMessage(
                            "Error! Units cannot start with 0.0 health", skin);
                    return;
                } else if (unitDamage <= 0.0f) {
                    displayErrorMessage(
                            "Error! Units cannot start with 0.0 damage", skin);
                    return;
                } else if (unitSpeed <= 0.0f) {
                    displayErrorMessage(
                            "Error! Units cannot start with 0.0 speed", skin);
                    return;
                } else if (unitSpawnRate <= 0.0f) {
                    displayErrorMessage(
                            "Error! Spawn rate cannot be 0.0", skin);
                    return;
                }

                ArrayList<BaseTurret.Type> disabledTypes =
                        new ArrayList<BaseTurret.Type>();

                if (disableBasicTurretCheckbox.isChecked()) {
                    disabledTypes.add(BaseTurret.Type.NORMAL);
                }
                if (disableBuffTurretCheckbox.isChecked()) {
                    disabledTypes.add(BaseTurret.Type.BUFF);
                }
                if (disableChaingunTurretCheckbox.isChecked()) {
                    disabledTypes.add(BaseTurret.Type.CHAINGUN);
                }
                if (disableHomingTurretCheckbox.isChecked()) {
                    disabledTypes.add(BaseTurret.Type.HOMING);
                }
                if (disableRocketTurretCheckbox.isChecked()) {
                    disabledTypes.add(BaseTurret.Type.ROCKET);
                }
                if (disableShotgunTurretCheckbox.isChecked()) {
                    disabledTypes.add(BaseTurret.Type.SHOTGUN);
                }

                if (disabledTypes.size() == BaseTurret.Type.values().length) {
                    displayErrorMessage(
                            "Error! Cannot disable all turrets", skin);
                    return;
                }

                MapSettings mapSettings = new MapSettings();

                BaseTurret.Type[] disabledTypesArr = new BaseTurret.Type[disabledTypes.size()];
                disabledTypesArr = disabledTypes.toArray(disabledTypesArr);

                mapSettings.setDisabledTurretTypes(disabledTypesArr);

                String[] keys =
                        {
                                MapSettings.PLAYER_INITIAL_HEALTH_KEY,
                                MapSettings.PLAYER_INITIAL_RESOURCES_KEY,
                                MapSettings.UNIT_INITIAL_DAMAGE_KEY,
                                MapSettings.UNIT_INITIAL_HEALTH_KEY,
                                MapSettings.UNIT_INITIAL_SPEED_KEY,
                                MapSettings.UNIT_SPAWN_RATE_KEY
                        };

                float[] values =
                        {
                                playerHealth,
                                playerResources,
                                unitDamage,
                                unitHealth,
                                unitSpeed,
                                unitSpawnRate
                        };

                mapSettings.putValues(keys, values);

                // save settings to map
                _map.setSettings(mapSettings);

                // finally, save map
                if (index >= 0) {
                    _towerDefense.maps.set(index, _map);
                    message = "Saved over old map.";
                } else {
                    _towerDefense.maps.add(_map);
                    message = "Something weird happened, saved new copy of map.";
                }

                _originalMapReference = _map;
                _map = _map.clone();

                Dialog saveSucceeded = new Dialog("Success!", skin);
                saveSucceeded.text(message);
                saveSucceeded.button("Continue");
                saveSucceeded.show(stage);
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                _towerDefense.setScreen(new MapEditorSelectorScreen(_towerDefense));
                dispose();
            }
        });

        addPathButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Path[] paths = _map.getPaths();

                // make a copy of old paths array, and add a new path to the end
                Path[] updatedPaths = new Path[paths.length + 1];

                for (int i = 0; i < paths.length; i++) {
                    updatedPaths[i] = paths[i];
                }

                updatedPaths[updatedPaths.length - 1] = new Path(
                        new ArrayList<Point>(), 5.0f); // TODO remove magic number

                _map.setPaths(updatedPaths);

                _selectedPathIndex = updatedPaths.length - 1;
                resetUnitManagers();

                selectedPathLabel.setText("" + _selectedPathIndex);
            }
        });

        deletePathButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Path[] paths = _map.getPaths();

                if (paths.length < 1) {
                    return;
                }

                // make a new array and set it to the map
                Path[] updatedPaths = new Path[paths.length - 1];
                int offset = 0;

                for (int i = 0; i < paths.length; i++) {
                    if (i == _selectedPathIndex) {
                        offset = -1;

                        continue;
                    }

                    updatedPaths[i + offset] = paths[i];
                }

                _map.setPaths(updatedPaths);
                resetUnitManagers();
                if (_selectedPathIndex > 0) {
                    _selectedPathIndex--;
                }

                selectedPathLabel.setText("" + _selectedPathIndex);
            }
        });

        deleteLastNodeOnPathButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Path[] paths = _map.getGenericPaths();

                if (_selectedPathIndex >= paths.length) {
                    return;
                }

                Path selectedPath = paths[_selectedPathIndex];
                if (selectedPath.set.size() > 0) {
                    selectedPath.set.remove(
                            selectedPath.set.size() - 1);

                    resetUnitManagers();
                }
            }
        });

        leftArrowImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (_selectedPathIndex > 0) {
                    _selectedPathIndex--;
                    selectedPathLabel.setText("" + _selectedPathIndex);
                }
            }
        });

        rightArrowImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (_selectedPathIndex < (_map.getPaths().length - 1)) {
                    _selectedPathIndex++;
                    selectedPathLabel.setText("" + _selectedPathIndex);
                }
            }
        });

        displayOptionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                optionsTable.setVisible(!optionsTable.isVisible());
            }
        });

        displayTurretsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                disableTurretsTable.setVisible(!disableTurretsTable.isVisible());
            }
        });

        displayTransformationsTableButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transformationsTable.setVisible(!transformationsTable.isVisible());
            }
        });
    }

    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _orthoCamera.update();
        _spriteBatch.setProjectionMatrix(_orthoCamera.combined);
        _shapeRenderer.setProjectionMatrix(_orthoCamera.combined);

        _spriteBatch.begin();
        _shapeRenderer.begin();

        Path[] paths = _map.getPaths();
        for (int i = 0; i < paths.length; i++) {
            if (i == _selectedPathIndex) {
                _shapeRenderer.setColor(Color.GREEN);
            } else {
                _shapeRenderer.setColor(Color.BLUE);
            }

            for (int j = 0; j < (paths[i].set.size() - 1); j++) {
                Point a = paths[i].set.get(j),
                        b = paths[i].set.get(j + 1);
                _shapeRenderer.line(a.x, a.y, b.x, b.y);

                _shapeRenderer.circle(a.x, a.y, 5.0f);
            }

            if (paths[i].set.size() > 0) {
                Point lastPoint = paths[i].set.get(paths[i].set.size() - 1);

//                _shapeRenderer.circle(lastPoint.x, lastPoint.y, 8.0f);

                float length = 12.0f;
                _shapeRenderer.triangle(lastPoint.x, lastPoint.y,
                        lastPoint.x - length, lastPoint.y - length,
                        lastPoint.x, lastPoint.y - length);
                _shapeRenderer.triangle(lastPoint.x, lastPoint.y,
                        lastPoint.x + length, lastPoint.y + length,
                        lastPoint.x, lastPoint.y + length);
            }
        }

        for (UnitManager unitManager : _unitManagers) {
            if (unitManager != null) {
                unitManager.act(deltaTime, null);
                unitManager.draw(_shapeRenderer);
            }
        }

        _shapeRenderer.end();
        _spriteBatch.end();

        stage.act(deltaTime);
        stage.draw();
    }

    private void resetUnitManagers() {
        Path[] paths = _map.getPaths();
        _unitManagers = new UnitManager[paths.length];
        for (int i = 0; i < _unitManagers.length; i++) {
            if (paths[i].set.size() > 1) {
                _unitManagers[i] = new UnitManager(paths[i], _mapSettings);
            } else {
                _unitManagers[i] = null;
            }
        }
    }

    private void displayErrorMessage(String message, Skin skin) {
        Dialog errorDialog = new Dialog("Error!", skin);
        errorDialog.text(message);
        errorDialog.button("Ok");
        errorDialog.show(stage);
    }

    public void pause() {}
    public void resume() {}

    public void show() {}
    public void hide() {}

    public void dispose() {}

    public void resize(int width, int height) {

        stage.dispose();
        stage = new Stage();
        stage.addActor(rootTable);

        inputMultiplexer.clear();
        inputMultiplexer = new InputMultiplexer(stage,
                new GestureDetector(new MyGestureListener()));

        Gdx.input.setInputProcessor(inputMultiplexer);

        _orthoCamera.setToOrtho(true, width, height);

        resetUnitManagers();
    }

    private class MyGestureListener implements GestureDetector.GestureListener {
        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {

            return false;
        }

        @Override
        public boolean tap(float screenX, float screenY, int count, int button) {

            // add latest `click` to path set
            // TODO FIXME Maybe

            Vector3 vector = new Vector3(screenX, screenY, 0);
            _orthoCamera.unproject(vector);

            Point target = new Point(vector.x, vector.y);
            target.x /= Gdx.graphics.getWidth();
            target.y /= Gdx.graphics.getHeight();

            if (_selectedPathIndex < _map.getNumPaths()) {
                _map.getRealPath(_selectedPathIndex).set.add(target);
                resetUnitManagers();
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
}

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


package net.noviden.towerdefense.MapCreator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.MapEditor.MapEditorSelectorScreen;
import net.noviden.towerdefense.MapSettings;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.TowerDefense;
import net.noviden.towerdefense.TurretFactory.BaseTurret;

import java.util.ArrayList;
import java.util.Stack;
import java.util.UUID;

public class MapCreatorScreen implements Screen {

    final TowerDefense towerDefense;

    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private Stack<Path> paths;
    private ArrayList<Point> pointSet;
    private String name;

    private Table table;
    private InputMultiplexer inputMultiplexer;

    public MapCreatorScreen(final TowerDefense towerDefense) {
        this.towerDefense = towerDefense;

        camera = new OrthographicCamera();
        camera.setToOrtho(true, TowerDefense.SCREEN_WIDTH, TowerDefense.SCREEN_HEIGHT);
        camera.update();

        stage = new Stage();

        final Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        table = new Table();
        table.setFillParent(true);

        name = UUID.randomUUID().toString().substring(0, 5);

        Label nameLabel = new Label("Name:", skin);
        final TextField nameField = new TextField(name, skin);
        TextButton addPathButton = new TextButton("Add New Path", skin);
        TextButton undoButton = new TextButton("Remove Last Point", skin);
        TextButton finishButton = new TextButton("Save Map", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        final TextButton displayOptionsButton = new TextButton("Options", skin);

        // set up options table
        final Table optionsTable = new Table();
        optionsTable.setVisible(false);

        Label initialPlayerHealthLabel = new Label("Initial Player Health:", skin);
        final TextField initialPlayerHealthText =
                new TextField("" + MapSettings.DEFAULT_INITIAL_PLAYER_HEALTH, skin);
        Label initialPlayerResourcesLabel = new Label("Initial Player Resources:", skin);
        final TextField initialPlayerResourcesText =
                new TextField("" + MapSettings.DEFAULT_INITIAL_PLAYER_RESOURCES, skin);
        Label initialUnitHealthLabel = new Label("Initial Unit Health:", skin);
        final TextField initialUnitHealthText =
                new TextField("" + MapSettings.DEFAULT_INITIAL_UNIT_HEALTH, skin);
        Label initialUnitMovementSpeedLabel = new Label("Initial Unit Movement Speed:", skin);
        final TextField initialUnitMovementSpeedText =
                new TextField("" + MapSettings.DEFAULT_INITIAL_UNIT_SPEED, skin);
        Label initialUnitDamageLabel = new Label("Initial Unit Damage:", skin);
        final TextField initialUnitDamageText =
                new TextField("" + MapSettings.DEFAULT_INITIAL_UNIT_DAMAGE, skin);
        Label defaultUnitSpawnRateLabel = new Label("Unit Spawn Rate:", skin);
        final TextField defaultUnitSpawnRateText =
                new TextField("" + MapSettings.DEFAULT_UNIT_SPAWN_RATE, skin);

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

        optionsTable.row();
        optionsTable.add(disableTurretsTable);

        table.add(addPathButton);
        table.add(undoButton);
        table.add(finishButton);
        table.add(exitButton);
        table.row();
        table.add(nameLabel, nameField);
        table.add(displayOptionsButton);
        table.row();
        table.add(optionsTable);

        table.top(); table.right();

        stage.addActor(table);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(new GestureDetector(new MyGestureListener()));

        Gdx.input.setInputProcessor(inputMultiplexer);

        addPathButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // remove last point in set
                if (pointSet.size() > 1) {
                    // valid to create a new path
                    paths.push(new Path(pointSet, 5.0f));
                    System.out.println("Added a path to paths Stack");
                    pointSet = new ArrayList<Point>();
                } else {
                    Dialog errorDialog = new Dialog("Error!", skin);
                    errorDialog.text("Error! Must finish one path before starting another!");
                    errorDialog.button("Ok");
                    errorDialog.show(stage);
                }
            }
        });

        undoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // remove last point in set
                if (pointSet.size() > 0) {
                    pointSet.remove(pointSet.size() - 1);
                } else {
                    if (paths.size() > 0) {
                        Path path = paths.pop();
                        pointSet = (ArrayList<Point>) path.set.clone();
                        System.out.println("Popped from paths");
                    }
                }
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                towerDefense.setScreen(new MapEditorSelectorScreen(towerDefense));
                dispose();
            }
        });

        finishButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (paths.size() == 0 && pointSet.size() < 2) {
                    displayErrorMessage(
                            "Error! Each path must have at least an entrance and exit", skin);
                    return;
                }

                // create settings for map
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

                // make sure current pointSet is included in new map
                if (pointSet.size() > 1) {
                    paths.push(new Path(pointSet, 5.0f));
                }

                // convert stack to array
                Path[] pathsForMap = new Path[paths.size()];
                int i = 0;
                while (!paths.empty()) {
                    pathsForMap[i++] = paths.pop();
                }

                // finally create the map
                Map map = new Map(
                        new Map.Dimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()),
                        pathsForMap,
                        nameField.getText(),
                        mapSettings);

                towerDefense.maps.add(map);

                // return to main menu
                towerDefense.setScreen(new MapEditorSelectorScreen(towerDefense));
                dispose();
            }
        });

        displayOptionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (optionsTable.isVisible()) {
                    optionsTable.setVisible(false);
//                    disableTurretsTable.setVisible(false);
                } else {
                    optionsTable.setVisible(true);
                }
            }
        });

        displayTurretsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (disableTurretsTable.isVisible()) {
                    disableTurretsTable.setVisible(false);
                } else {
                    disableTurretsTable.setVisible(true);
                }
            }
        });

        pointSet = new ArrayList<Point>();
        paths = new Stack<Path>();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // draw paths
        shapeRenderer.setColor(Color.GREEN);
        for (Path path : paths) {
            for (int i = 0; i < (path.set.size() - 1); i++) {
                Point a = path.set.get(i),
                        b = path.set.get(i + 1);
                shapeRenderer.line(a.x, a.y, b.x, b.y);

                shapeRenderer.circle(a.x, a.y, 5.0f);
            }

            if (path.set.size() > 0) {
                Point lastPoint = path.set.get(path.set.size() - 1);
                shapeRenderer.circle(lastPoint.x, lastPoint.y, 8.0f);
            }
        }

        // draw current path a different color
        shapeRenderer.setColor(Color.NAVY);
        for (int i = 0; i < (pointSet.size() - 1); i++) {
            Point a = pointSet.get(i),
                    b = pointSet.get(i + 1);
            shapeRenderer.line(a.x, a.y, b.x, b.y);

            shapeRenderer.circle(a.x, a.y, 5.0f);
        }

        if (pointSet.size() > 0) {
            Point lastPoint = pointSet.get(pointSet.size() - 1);
            shapeRenderer.circle(lastPoint.x, lastPoint.y, 8.0f);
        }


        shapeRenderer.end();
        batch.end();

        stage.act(deltaTime);
        stage.draw();
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
        stage.addActor(table);

        inputMultiplexer.clear();
        inputMultiplexer = new InputMultiplexer(stage,
                new GestureDetector(new MyGestureListener()));

        Gdx.input.setInputProcessor(inputMultiplexer);

        camera.setToOrtho(true, width, height);
    }

    private class MyGestureListener implements GestureDetector.GestureListener {

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {

            return false;
        }

        @Override
        public boolean tap(float screenX, float screenY, int count, int button) {

            pointSet.add(new Point(screenX, screenY));

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

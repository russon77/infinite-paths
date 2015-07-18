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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.MapSettings;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.Screens.MainMenuScreen;
import net.noviden.towerdefense.TowerDefense;
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
    private int _selectedVertexOnPath;

    private UnitManager[] _unitManagers;
    private MapSettings _mapSettings;

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

        _mapSettings = new MapSettings();
        _mapSettings.putValue(MapSettings.UNIT_INITIAL_SPEED_KEY, 150.0f);

        resetUnitManagers();

        // there must exist at least one path if we are EDITING an existing map
        _selectedPathIndex = 0;
        _selectedVertexOnPath = 0;

        final Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        stage = new Stage();

        Table rootTable = new Table();
        rootTable.setFillParent(true);

        Table pathAddDeleteTable = new Table();

        TextButton saveButton = new TextButton("Save", skin);
        TextButton exitButton = new TextButton("Exit Without Saving", skin);
        TextButton addPathButton = new TextButton("Add Path", skin);
        TextButton deletePathButton = new TextButton("Delete Current Path", skin);

        pathAddDeleteTable.add(deletePathButton);
        pathAddDeleteTable.add(addPathButton);
        pathAddDeleteTable.add(saveButton);
        pathAddDeleteTable.add(exitButton);

        Table pathSelectorTable = new Table();

        Texture leftArrowTexture = new Texture(Gdx.files.internal("leftArrow.png"));
        Image leftArrowImage = new Image(leftArrowTexture);
        Texture rightArrowTexture = new Texture(Gdx.files.internal("rightArrow.png"));
        Image rightArrowImage = new Image(rightArrowTexture);

        final Label selectedPathLabel = new Label("0", skin);

        pathSelectorTable.add(leftArrowImage).pad(8.0f);
        pathSelectorTable.add(selectedPathLabel).pad(8.0f);
        pathSelectorTable.add(rightArrowImage).pad(8.0f);

        rootTable.add(pathSelectorTable).expandX().left().expandY().top();

        rootTable.add(pathAddDeleteTable).expandX().right().expandY().top();

        stage.addActor(rootTable);

        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage,
                new GestureDetector(new MyGestureListener()));

        Gdx.input.setInputProcessor(inputMultiplexer);

        // listeners
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO come up with a better algorithm for this

                for (int i = 0; i < _towerDefense.maps.size(); i++) {
                    if (_originalMapReference.equals(_towerDefense.maps.get(i))) {
                        _towerDefense.maps.set(i, _map);

                        Dialog saveSucceeded = new Dialog("Success!", skin);
                        saveSucceeded.text("Save succeeded.");
                        saveSucceeded.button("Continue");

                        return;
                    }
                }

                Dialog saveFailed = new Dialog("Error!", skin);
                saveFailed.text("Failed to save map.");
                saveFailed.button("Continue.");
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
                // TODO

                if (true)
                    return;

                Path[] paths = _map.getPaths();

                // make a copy of old paths array, and add a new path to the end
                Path[] updatedPaths = new Path[paths.length + 1];

                for (int i = 0; i < paths.length; i++) {
                    updatedPaths[i] = updatedPaths[i];
                }

                updatedPaths[updatedPaths.length - 1] = new Path(
                        new ArrayList<Point>(), paths[0].width);
            }
        });

        deletePathButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Path[] paths = _map.getPaths();

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
                _shapeRenderer.circle(lastPoint.x, lastPoint.y, 8.0f);
            }
        }

        for (UnitManager unitManager : _unitManagers) {
            unitManager.act(deltaTime, null);
            unitManager.draw(_shapeRenderer);
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
            _unitManagers[i] = new UnitManager(paths[i], _mapSettings);
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

    public void resize(int width, int height) {}

    private class MyGestureListener implements GestureDetector.GestureListener {
        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {

            return false;
        }

        @Override
        public boolean tap(float screenX, float screenY, int count, int button) {

            // add latest `click` to path set
            _map.getPath(_selectedPathIndex).set.add(new Point(screenX, screenY));

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

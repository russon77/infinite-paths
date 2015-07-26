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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.noviden.towerdefense.GameSettings;
import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.MapEditor.MapEditorSelectorScreen;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.TowerDefense;
import net.noviden.towerdefense.UnitFactory.UnitManager;

public class MainMenuScreen implements Screen {

    private final TowerDefense towerDefense;

    private Stage stage;

    // for dynamic background
    private Map _map;
    private UnitManager[] _unitManagers;

    private SpriteBatch _spriteBatch;
    private ShapeRenderer _shapeRenderer;
    private OrthographicCamera _orthoCamera;

    private Skin _skin;
    private Table menuTable;

    public MainMenuScreen(final TowerDefense towerDefense) {
        this.towerDefense = towerDefense;

        // initialize singletons
        GameSettings.initialize();

        // set up ui
        _skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        stage = new Stage();

        menuTable = new Table();
        menuTable.setFillParent(true);

        stage.addActor(menuTable);

        Gdx.input.setInputProcessor(stage);

        setupUi();

        // set up dynamic background

        _spriteBatch = new SpriteBatch();
        _shapeRenderer = new ShapeRenderer();
        _shapeRenderer.setAutoShapeType(true);

        _orthoCamera = new OrthographicCamera();
        _orthoCamera.setToOrtho(true,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
//        _orthoCamera.position.set(TowerDefense.SCREEN_WIDTH / 2, TowerDefense.SCREEN_HEIGHT / 2, 0);
        _orthoCamera.update();

        int randomMapNo = (int) (Math.random() * (towerDefense.maps.size()));
        _map = towerDefense.maps.get(randomMapNo);

        Path[] paths = _map.getPaths();

        _unitManagers = new UnitManager[paths.length];

        for (int i = 0; i < paths.length; i++) {
            _unitManagers[i] = new UnitManager(paths[i], _map.getSettings());
        }
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update the camera, update the project matrices for our drawing routines
        _orthoCamera.update();
        _spriteBatch.setProjectionMatrix(_orthoCamera.combined);
        _shapeRenderer.setProjectionMatrix(_orthoCamera.combined);

        _spriteBatch.begin();
        _shapeRenderer.begin();

        // draw the map
        _map.draw(_shapeRenderer);

        for (UnitManager unitManager : _unitManagers) {
            // act, and draw
            unitManager.act(deltaTime, null);

            unitManager.draw(_shapeRenderer);
        }

        _shapeRenderer.end();
        _spriteBatch.end();

        // draw the stage, containing all important ui elements, last and `on top`
        stage.act(deltaTime);
        stage.draw();
    }

    public void show() {}

    public void hide() {}

    public void pause() {}

    public void resume() {}

    public void resize(int width, int height) {
//        System.out.println("Resized to " + width + " " + height);

        stage.dispose();
        stage = new Stage();
        stage.addActor(menuTable);

        Gdx.input.setInputProcessor(stage);

        _orthoCamera.setToOrtho(true, width, height);

        Path[] paths = _map.getPaths();
        for (int i = 0; i < _unitManagers.length; i++) {
            _unitManagers[i].updatePath(paths[i]);
        }
    }

    public void dispose() {}

    private void setupUi() {
        Label welcomeLabel = new Label("Welcome to TowerDefense!", _skin);

        TextButton startGame = new TextButton("Start Game", _skin);
        TextButton mapEditor = new TextButton("Map Editor", _skin);
        TextButton settings = new TextButton("Settings", _skin);
        TextButton exitGame = new TextButton("Exit", _skin);

        menuTable.add(welcomeLabel).padBottom(50.0f);
        menuTable.row();
        menuTable.add(startGame).padBottom(5.0f);
        menuTable.row();
        menuTable.add(mapEditor).padBottom(5.0f);
        menuTable.row();
        menuTable.add(settings).padBottom(5.0f);
        menuTable.row();
        menuTable.add(exitGame);

        menuTable.center();

        startGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                towerDefense.setScreen(new MapSelectorScreen(towerDefense));
                dispose();
            }
        });

        mapEditor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                towerDefense.setScreen(new MapEditorSelectorScreen(towerDefense));
                dispose();
            }
        });

        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                towerDefense.setScreen(new GameSettingsScreen(towerDefense));
                dispose();
            }
        });

        exitGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
                dispose();
            }
        });
    }
}

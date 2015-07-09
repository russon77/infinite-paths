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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.noviden.towerdefense.MapCreator.MapCreatorScreen;

public class MainMenuScreen implements Screen {

    private final TowerDefense towerDefense;

    private OrthographicCamera camera;

    private Stage stage;

    public MainMenuScreen(final TowerDefense towerDefense) {
        this.towerDefense = towerDefense;

        GameSettings.initialize();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);

        Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        stage = new Stage();

        Table menuTable = new Table();
        menuTable.setFillParent(true);

        TextButton startGame = new TextButton("Start Game", skin);
        TextButton mapCreator = new TextButton("Map Creator", skin);
        TextButton settings = new TextButton("Settings", skin);
        TextButton exitGame = new TextButton("Exit", skin);

        menuTable.add(startGame);
        menuTable.row();
        menuTable.add(mapCreator);
        menuTable.row();
        menuTable.add(settings);
        menuTable.row();
        menuTable.add(exitGame);

        menuTable.center();

        stage.addActor(menuTable);

        Gdx.input.setInputProcessor(stage);

        startGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                towerDefense.setScreen(new MapSelectorScreen(towerDefense));
                dispose();
            }
        });

        mapCreator.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                towerDefense.setScreen(new MapCreatorScreen(towerDefense));
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

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        towerDefense.batch.begin();
        towerDefense.font.draw(towerDefense.batch, "Welcome to TowerDefense!", 100, 150);
        towerDefense.batch.end();

        stage.act(deltaTime);
        stage.draw();
    }

    public void show() {}

    public void hide() {}

    public void pause() {}

    public void resume() {}

    public void resize(int width, int height) {}

    public void dispose() {}
}

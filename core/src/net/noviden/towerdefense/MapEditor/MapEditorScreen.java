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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.TowerDefense;

public class MapEditorScreen implements Screen {

    private final TowerDefense towerDefense;

    private Stage stage;
    private Map _map;

    public MapEditorScreen(final TowerDefense towerDefense, Map pMap) {
        this.towerDefense = towerDefense;
        _map = pMap;

        Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        stage = new Stage();

        Table rootTable = new Table();
        rootTable.setFillParent(true);



        stage.addActor(rootTable);

        Gdx.input.setInputProcessor(stage);
    }

    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(deltaTime);
        stage.draw();
    }

    public void pause() {}
    public void resume() {}

    public void show() {}
    public void hide() {}

    public void dispose() {}

    public void resize(int width, int height) {}
}

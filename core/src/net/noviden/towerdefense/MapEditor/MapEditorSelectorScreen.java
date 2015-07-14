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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.noviden.towerdefense.MapCreator.MapCreatorScreen;
import net.noviden.towerdefense.Screens.MainMenuScreen;
import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.MapCreator.MapThumbnail;
import net.noviden.towerdefense.TowerDefense;

public class MapEditorSelectorScreen implements Screen {
    private final TowerDefense towerDefense;

    private Stage stage;

    private Map _selectedMap;
    private ImageButton _selectedMapButton;
    private Image _selectedMapIdentifierImage;

    private Table _mapListTable;

    public MapEditorSelectorScreen(final TowerDefense towerDefense) {
        this.towerDefense = towerDefense;

        Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        Texture texture = new Texture(Gdx.files.internal("selectedMap.png"));
        _selectedMapIdentifierImage = new Image(texture);

        stage = new Stage();

        Table rootTable = new Table();
        rootTable.setFillParent(true);

        Table containerTable = new Table();
        _mapListTable = new Table();

        updateMapList();

        ScrollPane scrollPane = new ScrollPane(_mapListTable);
        scrollPane.layout();
        scrollPane.setFadeScrollBars(false);

        Table operationsTable = new Table();
        TextButton createButton = new TextButton("Create", skin);
        TextButton selectButton = new TextButton("Edit", skin);
        TextButton cloneButton = new TextButton("Clone", skin);
        TextButton deleteButton = new TextButton("Delete", skin);

        TextButton exitButton = new TextButton("Exit", skin);

        containerTable.add(scrollPane).fillX().fillY();

        operationsTable.add(createButton).pad(5.0f);
        operationsTable.add(selectButton).pad(5.0f);
        operationsTable.add(cloneButton).pad(5.0f);
        operationsTable.add(deleteButton).pad(5.0f);
        operationsTable.add(exitButton).pad(5.0f);

        rootTable.add(operationsTable).expandY().top().expandX().right();
        rootTable.row();
        rootTable.add(containerTable).top();
        rootTable.center();

        stage.addActor(rootTable);

        Gdx.input.setInputProcessor(stage);

        // set up input listeners
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                towerDefense.setScreen(new MainMenuScreen(towerDefense));
            }
        });

        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                towerDefense.setScreen(new MapCreatorScreen(towerDefense));
            }
        });

        selectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (_selectedMap != null)
                    towerDefense.setScreen(new MapEditorScreen(towerDefense, _selectedMap));
            }
        });

        cloneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (_selectedMap != null) {
                    towerDefense.maps.add(_selectedMap.clone());
                    updateMapList();
                }
            }
        });

        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (_selectedMap != null) {
                    towerDefense.maps.remove(_selectedMap);
                    updateMapList();
                }
            }
        });
    }

    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(deltaTime);
        stage.draw();
    }

    private void updateMapList() {
        _mapListTable.clearChildren();

        for (int i = 0; i < towerDefense.maps.size(); i++) {
            if (i > 0 && i % 3 == 0) {
                _mapListTable.row();
            }

            final Map map = towerDefense.maps.get(i);
            final ImageButton imageButton =
                    new ImageButton(MapThumbnail.createThumbnail(map, 0.25f));

            ClickListener clickListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // if user double clicks on this image, go directly to map editor screen
                    if (getTapCount() > 1) {
                        towerDefense.setScreen(new MapEditorScreen(towerDefense, map));
                        dispose();

                        return;
                    }

                    // otherwise do other fun stuff
                    if (_selectedMapButton != null) {
                        _selectedMapButton.clearChildren();

                        Image image = new Image(
                                MapThumbnail.createThumbnail(_selectedMap, 0.25f));

                        _selectedMapButton.add(image);
                        _selectedMapButton.row();
                    }

                    _selectedMap = map;

                    _selectedMapButton = imageButton;

                    _selectedMapButton.add(_selectedMapIdentifierImage);
                }
            };

            imageButton.addListener(clickListener);

            _mapListTable.add(imageButton).pad(10.0f);
        }
    }

    public void pause() {}
    public void resume() {}

    public void show() {}
    public void hide() {}

    public void dispose() {}

    public void resize(int width, int height) {}
}

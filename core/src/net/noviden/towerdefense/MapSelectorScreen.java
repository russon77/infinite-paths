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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.noviden.towerdefense.MapCreator.MapThumbnail;

public class MapSelectorScreen implements Screen {

    private final TowerDefense towerDefense;

    private Stage stage;

    public MapSelectorScreen(final TowerDefense towerDefense) {
        this.towerDefense = towerDefense;

        Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        stage = new Stage();

        Table rootTable = new Table();
        rootTable.setFillParent(true);

        Table containerTable = new Table();
        Table mapListTable = new Table();

        for (int i = 0; i < towerDefense.maps.size(); i++) {
            if (i > 0 && i % 3 == 0) {
                mapListTable.row();
            }

            final Map map = towerDefense.maps.get(i);

            ClickListener clickListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    towerDefense.setScreen(new GameScreen(towerDefense, map));
                }
            };
            TextButton textButton = new TextButton(map.getName(), skin);
            textButton.addListener(clickListener);

            ImageButton imageButton = new ImageButton(MapThumbnail.createThumbnail(map, 0.25f));
            imageButton.addListener(clickListener);

            mapListTable.add(imageButton).pad(10.0f);
        }

        ScrollPane scrollPane = new ScrollPane(mapListTable);
        scrollPane.layout();
        scrollPane.setFadeScrollBars(false);

        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                towerDefense.setScreen(new MainMenuScreen(towerDefense));
            }
        });

        containerTable.add(scrollPane).fillX().fillY();

        rootTable.add(exitButton);
        rootTable.add(containerTable);
        rootTable.center();

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

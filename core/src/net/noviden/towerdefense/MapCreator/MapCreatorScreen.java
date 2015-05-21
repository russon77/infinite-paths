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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.noviden.towerdefense.MainMenuScreen;
import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.TowerDefense;

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

    public MapCreatorScreen(final TowerDefense towerDefense) {
        this.towerDefense = towerDefense;

        camera = new OrthographicCamera();
        camera.setToOrtho(true, TowerDefense.SCREEN_WIDTH, TowerDefense.SCREEN_HEIGHT);
        camera.update();

        stage = new Stage();

        final Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);

        name = UUID.randomUUID().toString().substring(0, 5);

        Label nameLabel = new Label("Name:", skin);
        final TextField nameField = new TextField(name, skin);
        TextButton addPathButton = new TextButton("Add New Path", skin);
        TextButton undoButton = new TextButton("Remove Last Point", skin);
        TextButton finishButton = new TextButton("Save Map", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        table.add(addPathButton);
        table.add(undoButton);
        table.add(finishButton);
        table.add(exitButton);
        table.row();
        table.add(nameLabel, nameField);

        table.top(); table.right();

        stage.addActor(table);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
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
                towerDefense.setScreen(new MainMenuScreen(towerDefense));
                dispose();
            }
        });

        finishButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (paths.size() == 0 && pointSet.size() < 2) {
                    // display error message
                    // TODO
                    Dialog errorDialog = new Dialog("Error!", skin);
                    errorDialog.text("Error! Each path must have at least an entrance and exit");
                    errorDialog.button("Ok");
                    errorDialog.show(stage);

                    return;
                }

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

                Map map = new Map(
                        new Map.Dimensions(TowerDefense.SCREEN_WIDTH, TowerDefense.SCREEN_HEIGHT),
                        pathsForMap,
                        nameField.getText());

                towerDefense.maps.add(map);

                // return to main menu
                towerDefense.setScreen(new MainMenuScreen(towerDefense));
                dispose();

                /*

                // ask for name
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        // create a new map and add it to the list stored in `game`
                        Map map = new Map(new Map.Dimensions(TowerDefense.SCREEN_WIDTH, TowerDefense.SCREEN_HEIGHT),
                                new Path(pointSet, 5.0f));

                        towerDefense.maps.add(map);

                        // return to main menu
                        towerDefense.setScreen(new MainMenuScreen(towerDefense));
                        dispose();
                    }

                    @Override
                    public void canceled() {

                    }
                }, "Name Your Map", "", "e.g. super awesome fun map");

                */
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

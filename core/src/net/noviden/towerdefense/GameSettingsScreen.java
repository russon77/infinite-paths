package net.noviden.towerdefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.HashMap;

public class GameSettingsScreen implements Screen {
    final TowerDefense _towerDefense;

    private Stage _mainStage;

    private TextButton _currentlySelectedShortcut;
    private int _previouslySelectedKey;

    private HashMap<Integer, GameSettings.Actions> _modifiedKeyboardShortcutsMap;

    public GameSettingsScreen(final TowerDefense pTowerDefense) {
        _towerDefense = pTowerDefense;

        Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        _mainStage = new Stage();

        Table rootTable = new Table();
        rootTable.setFillParent(true);

        _mainStage.addActor(rootTable);

        final CheckBox audioEnabledCheckBox = new CheckBox(" Audio Enabled", skin);
        audioEnabledCheckBox.setChecked(true);

        final CheckBox fullscreenCheckBox = new CheckBox(" Fullscreen", skin);
        audioEnabledCheckBox.setChecked(false);

        Table keyboardShortcutsTable = new Table();
        // TODO fill in this table with current settings

        HashMap<Integer, GameSettings.Actions> myMap = GameSettings.getShortcutMap();

        for (int i : myMap.keySet()) {
            Label actionLabel = new Label(myMap.get(i).toString(), skin);
            final TextButton shortcutButton = new TextButton(Input.Keys.toString(i), skin);

            keyboardShortcutsTable.add(actionLabel).pad(10.0f);
            keyboardShortcutsTable.add(shortcutButton);
            keyboardShortcutsTable.row();

            shortcutButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    _currentlySelectedShortcut = shortcutButton;
                    _previouslySelectedKey =
                            Input.Keys.valueOf(shortcutButton.getText().toString());

                    shortcutButton.setText("Press any un-set key!");
                }
            });
        }

        // add in save and exit buttons
        Table exitFunctionalityTable = new Table();

        TextButton saveAndExitButton = new TextButton("Save and Exit", skin);
        TextButton exitWithoutSavingButton = new TextButton("Exit Without Saving", skin);

        exitFunctionalityTable.add(saveAndExitButton).pad(10.0f);
        exitFunctionalityTable.add(exitWithoutSavingButton);

        // finally, construct display table
        rootTable.add(audioEnabledCheckBox);
        rootTable.row();
        rootTable.add(fullscreenCheckBox);
        rootTable.row();
        rootTable.add(keyboardShortcutsTable);
        rootTable.row();
        rootTable.add(exitFunctionalityTable);

        InputMultiplexer multiplexer = new InputMultiplexer(_mainStage, new MyInputProcessor());

        Gdx.input.setInputProcessor(multiplexer);

        // add in event listeners here

        exitWithoutSavingButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                _towerDefense.setScreen(new MainMenuScreen(_towerDefense));
            }
        });

        saveAndExitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // attempt to save settings

                GameSettings.setAudio(audioEnabledCheckBox.isChecked());

                GameSettings.setFullScreen(fullscreenCheckBox.isChecked());

                // save keyboard shortcuts

                // return to main menu
                _towerDefense.setScreen(new MainMenuScreen(_towerDefense));
            }
        });
    }

    public void render(float pDelta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        _mainStage.act();
        _mainStage.draw();
    }

    public void pause() {}
    public void resume() {}

    public void show() {}
    public void hide() {}

    public void resize(int width, int height) {}
    public void dispose() {}

    private class MyInputProcessor implements InputProcessor {
        @Override
        public boolean keyDown(int keycode) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {

            if (keycode == Input.Keys.ESCAPE) {
                _currentlySelectedShortcut.setText(Input.Keys.toString(_previouslySelectedKey));
                _currentlySelectedShortcut = null;
                _previouslySelectedKey = 0;
            }

            // attempt to set keyboard shortcut
            if (_currentlySelectedShortcut != null) {
                _currentlySelectedShortcut.setText(Input.Keys.toString(keycode));

                _currentlySelectedShortcut = null;
            }

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
            return false;
        }

        @Override
        public boolean scrolled(int amount) {

            return false;
        }
    }
}

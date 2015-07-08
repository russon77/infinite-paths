package net.noviden.towerdefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class GameSettingsScreen implements Screen {
    final TowerDefense _towerDefense;

    private Stage _mainStage;

    public GameSettingsScreen(final TowerDefense pTowerDefense) {
        _towerDefense = pTowerDefense;

        Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        _mainStage = new Stage();

        Table rootTable = new Table();
        rootTable.setFillParent(true);

        _mainStage.addActor(rootTable);

        CheckBox checkBox = new CheckBox("Audio Enabled", skin);

        Gdx.input.setInputProcessor(_mainStage);
    }

    public void render(float pDelta) {
        _mainStage.act();
        _mainStage.draw();
    }

    public void pause() {}
    public void resume() {}

    public void show() {}
    public void hide() {}

    public void resize(int width, int height) {}
    public void dispose() {}
}

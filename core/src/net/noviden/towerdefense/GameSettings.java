package net.noviden.towerdefense;

import com.badlogic.gdx.Input;

import java.util.HashMap;

public class GameSettings {

    public enum Actions {
        PAUSE_GAME, QUICK_EXIT
    }

    private static GameSettings _instance;

    private boolean _isAudioOn, _isFullScreen;

    private HashMap<Integer, Actions> _keyboardShortcutsMap;

    private GameSettings() {
        _keyboardShortcutsMap = new HashMap<Integer, Actions>();
    }

    public static void initialize() {
        _instance = new GameSettings();

        _instance.loadDefaultSettings();
    }

    private void loadDefaultSettings() {
        _isAudioOn = true;
        _isFullScreen = false;

        _keyboardShortcutsMap.put(Input.Keys.SPACE, Actions.PAUSE_GAME);
        _keyboardShortcutsMap.put(Input.Keys.END, Actions.QUICK_EXIT);
    }

    private void loadSettingsFromFile() {
        // TODO
    }

    public static void setAudio(boolean pIsEnabled) {
        _instance._isAudioOn = pIsEnabled;
    }

    public static void setFullScreen(boolean pIsEnabled) {
        _instance._isFullScreen = pIsEnabled;
    }

    public static Actions getShortcutAction(int pMapKey) {
        return _instance._keyboardShortcutsMap.get(pMapKey);
    }

    public static void putShortcut(int pMapKey, Actions pShorcutValue) {
        _instance._keyboardShortcutsMap.put(pMapKey, pShorcutValue);
    }

    public static HashMap<Integer, Actions> getShortcutMap() {
        return _instance._keyboardShortcutsMap;
    }

    // screen sizes TODO
}

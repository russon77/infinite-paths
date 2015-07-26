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

package net.noviden.towerdefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GameSettings implements Serializable {

    private static final String PREFERENCES_FILE_NAME = "settings.xml";
    private static final String KEYBOARD_SHORTCUTS_FILE_NAME = "shortcuts.xml";

    private static final String AUDIO_ENABLED_KEY = "audio_enabled";
    private static final String SFX_VOLUME_KEY = "sfx_volume",
                    MUSIC_VOLUME_KEY = "music_volume";
    private static final String FULLSCREEN_ENABLED_KEY = "fullscreen";

    public enum Actions {
        PAUSE_GAME,
        QUICK_EXIT,


        SELECT_BASIC_TURRET,
        SELECT_BUFF_TURRET,
        SELECT_CHAINGUN_TURRET,
        SELECT_HOMING_TURRET,
        SELECT_ROCKET_TURRET,
        SELECT_SHOTGUN_TURRET,

        UPGRADE_TURRET_DAMAGE,
        UPGRADE_TURRET_RANGE,
        UPGRADE_TURRET_UNIQUE_MODIFIER,

        QUICK_SELL,

        TOGGLE_SHOW_INTERFACE,

        TOGGLE_SHOW_FPS
    }

    private static GameSettings _instance;

    private Preferences _preferences, _keyboardShortcuts;

    private HashMap<Integer, Actions> _keyboardShortcutsMap;

    private GameSettings() {
        _keyboardShortcutsMap = new HashMap<Integer, Actions>();
    }

    public static void initialize() {
        if (_instance == null) {
            _instance = new GameSettings();

            if (!_instance.loadSettingsFromFile()) {
                _instance.loadDefaultSettings();
            }

            if (!_instance.loadShortcutsFromFile()) {
                _instance.loadDefaultShortcuts();
            }
        }
    }

    private void loadDefaultSettings() {
        _preferences.putFloat(SFX_VOLUME_KEY, 1.0f);
        _preferences.putFloat(MUSIC_VOLUME_KEY, 1.0f);
    }

    private void loadDefaultShortcuts() {
        // set up defaults for keyboard shortcuts
        _keyboardShortcutsMap.put(Input.Keys.SPACE, Actions.PAUSE_GAME);
        _keyboardShortcutsMap.put(Input.Keys.END, Actions.QUICK_EXIT);

        _keyboardShortcutsMap.put(Input.Keys.D, Actions.UPGRADE_TURRET_DAMAGE);
        _keyboardShortcutsMap.put(Input.Keys.S, Actions.UPGRADE_TURRET_UNIQUE_MODIFIER);
        _keyboardShortcutsMap.put(Input.Keys.A, Actions.UPGRADE_TURRET_RANGE);

        _keyboardShortcutsMap.put(Input.Keys.Q, Actions.SELECT_BASIC_TURRET);
        _keyboardShortcutsMap.put(Input.Keys.W, Actions.SELECT_HOMING_TURRET);
        _keyboardShortcutsMap.put(Input.Keys.E, Actions.SELECT_ROCKET_TURRET);
        _keyboardShortcutsMap.put(Input.Keys.R, Actions.SELECT_BUFF_TURRET);
        _keyboardShortcutsMap.put(Input.Keys.NUM_1, Actions.SELECT_CHAINGUN_TURRET);
        _keyboardShortcutsMap.put(Input.Keys.NUM_2, Actions.SELECT_SHOTGUN_TURRET);

        _keyboardShortcutsMap.put(Input.Keys.T, Actions.QUICK_SELL);

        _keyboardShortcutsMap.put(Input.Keys.ESCAPE, Actions.TOGGLE_SHOW_INTERFACE);

        _keyboardShortcutsMap.put(Input.Keys.F, Actions.TOGGLE_SHOW_FPS);

        // add each entry in shortcuts to preferences
        for (Map.Entry entry : _keyboardShortcutsMap.entrySet()) {
            _keyboardShortcuts.putString(
                    Input.Keys.toString((Integer) entry.getKey()),
                    entry.getValue().toString());
        }
    }

    private boolean loadSettingsFromFile() {
        // TODO reading and writing a settings file

        _preferences = Gdx.app.getPreferences(PREFERENCES_FILE_NAME);

        // test if preferences is empty or not
        if (!_preferences.contains(AUDIO_ENABLED_KEY)) {
            return false;
        }

        return true;
    }

    private boolean loadShortcutsFromFile() {
        _keyboardShortcuts = Gdx.app.getPreferences(KEYBOARD_SHORTCUTS_FILE_NAME);

        // test if empty
        if (_keyboardShortcuts.get().size() == 0) {
            return false;
        }

        // now, write shortcuts to hashmap
        Map shortcutsMap = _keyboardShortcuts.get();
        Set<Map.Entry> entrySet = shortcutsMap.entrySet();
        Iterator iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            _keyboardShortcutsMap.put(
                    Input.Keys.valueOf((String) entry.getKey()),
                    Actions.valueOf((String) entry.getValue()));
        }


        return true;
    }

    public static void writeSettingsToFile() {
        _instance._preferences.flush();
        _instance._keyboardShortcuts.flush();
    }

    public static void setMusicVolume(float pVolume) {
        _instance._preferences.putFloat(MUSIC_VOLUME_KEY, pVolume);
    }

    public static float getMusicVolume() {
        return _instance._preferences.getFloat(MUSIC_VOLUME_KEY);
    }

    public static void setSoundVolume(float pVolume) {
        _instance._preferences.putFloat(SFX_VOLUME_KEY, pVolume);
    }

    public static float getSoundVolume() {
        return _instance._preferences.getFloat(SFX_VOLUME_KEY);
    }

    public static void setFullScreen(boolean pIsEnabled) {
        _instance._preferences.putBoolean(FULLSCREEN_ENABLED_KEY, pIsEnabled);
    }

    public static boolean isFullScreen() {
        return _instance._preferences.getBoolean(FULLSCREEN_ENABLED_KEY);
    }

    public static Actions getShortcutAction(int pMapKey) {
        return _instance._keyboardShortcutsMap.get(pMapKey);
    }

    public static void putShortcut(int pMapKey, Actions pShorcutValue) {
        // first remove current mapkey for shortcut
        if (_instance._keyboardShortcutsMap.containsValue(pShorcutValue)) {
            int key = getReverse(pShorcutValue);
            _instance._keyboardShortcutsMap.remove(key);
        }

        _instance._keyboardShortcutsMap.put(pMapKey, pShorcutValue);
    }

    public static void putShortcuts(HashMap<Integer, Actions> pMap) {
        for (int key : pMap.keySet()) {
            putShortcut(key, pMap.get(key));
        }

        // DEBUG
        System.out.println("Put all shortcuts into table");
    }

    public static HashMap<Integer, Actions> getShortcutMap() {
        return _instance._keyboardShortcutsMap;
    }

    public static int getReverse(Actions pAction) {
        // return the key for given action in shortcuts table
        for (int key : _instance._keyboardShortcutsMap.keySet()) {
            if (_instance._keyboardShortcutsMap.get(key).equals(pAction)) {
                return key;
            }
        }

        return -1;
    }

    // screen sizes TODO
}

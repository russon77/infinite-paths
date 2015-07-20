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

import com.badlogic.gdx.Input;

import java.io.Serializable;
import java.util.HashMap;

public class GameSettings implements Serializable {

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

    private boolean _isFullScreen;
    private float _soundVolume, _musicVolume;

    private HashMap<Integer, Actions> _keyboardShortcutsMap;

    private GameSettings() {
        _keyboardShortcutsMap = new HashMap<Integer, Actions>();
    }

    public static void initialize() {
        if (_instance == null) {
            _instance = new GameSettings();

            _instance.loadDefaultSettings();
        }
    }

    public static void initialize(GameSettings pInstance) {
        _instance = pInstance;
    }

    public static GameSettings getInstance() {
        return _instance;
    }

    private void loadDefaultSettings() {
        _musicVolume = _soundVolume = 1.0f;

        _isFullScreen = false;

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
    }

    private void loadSettingsFromFile() {
        // TODO reading and writing a settings file
    }

    public static void setMusicVolume(float pVolume) {
        _instance._musicVolume = pVolume;
    }

    public static float getMusicVolume() {
        return _instance._musicVolume;
    }

    public static void setSoundVolume(float pVolume) {
        _instance._soundVolume = pVolume;
    }

    public static float getSoundVolume() {
        return _instance._soundVolume;
    }

    public static void setFullScreen(boolean pIsEnabled) {
        _instance._isFullScreen = pIsEnabled;
    }

    public static boolean isFullScreen() {
        return _instance._isFullScreen;
    }

    public static Actions getShortcutAction(int pMapKey) {
        return _instance._keyboardShortcutsMap.get(pMapKey);
    }

    public static void putShortcut(int pMapKey, Actions pShorcutValue) {
        // first remove current mapkey for shortcut
        _instance._keyboardShortcutsMap.put(pMapKey, pShorcutValue);
    }

    public static void putShortcuts(HashMap<Integer, Actions> pMap) {
        _instance._keyboardShortcutsMap.putAll(pMap);
        System.out.println("Put all shortcuts into table");
    }

    public static HashMap<Integer, Actions> getShortcutMap() {
        return _instance._keyboardShortcutsMap;
    }

    // screen sizes TODO
}

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

import net.noviden.towerdefense.MapTransformation.Transformation;
import net.noviden.towerdefense.TurretFactory.BaseTurret;

import java.io.Serializable;
import java.util.HashMap;

public class MapSettings implements Serializable {

    public static final int DEFAULT_INITIAL_PLAYER_HEALTH = 100;
    public static final int DEFAULT_INITIAL_PLAYER_RESOURCES = 100;
    public static final int DEFAULT_INITIAL_UNIT_DAMAGE = 1;

    public static final float DEFAULT_INITIAL_UNIT_HEALTH = 100.0f;
    public static final float DEFAULT_INITIAL_UNIT_SPEED = 75.0f;
    public static final float DEFAULT_UNIT_SPAWN_RATE = 2.0f;

    public static final String UNIT_INITIAL_DAMAGE_KEY = "unit_initial_damage",
        UNIT_INITIAL_HEALTH_KEY = "unit_initial_health",
        UNIT_INITIAL_SPEED_KEY = "unit_initial_speed",
        UNIT_SPAWN_RATE_KEY = "unit_spawn_rate",
        PLAYER_INITIAL_HEALTH_KEY = "player_initial_health",
        PLAYER_INITIAL_RESOURCES_KEY = "player_initial_resources";

    private HashMap<String, Float> _valuesMap;

    private BaseTurret.Type[] _disabledTurretTypes;

    private int[][] _waves;

    private Transformation _transformation;

    public MapSettings() {

        _valuesMap = new HashMap<String, Float>();

        _valuesMap.put(PLAYER_INITIAL_HEALTH_KEY, (float) DEFAULT_INITIAL_PLAYER_HEALTH);
        _valuesMap.put(PLAYER_INITIAL_RESOURCES_KEY, (float) DEFAULT_INITIAL_PLAYER_RESOURCES);
        _valuesMap.put(UNIT_INITIAL_HEALTH_KEY, DEFAULT_INITIAL_UNIT_HEALTH);
        _valuesMap.put(UNIT_INITIAL_SPEED_KEY, DEFAULT_INITIAL_UNIT_SPEED);
        _valuesMap.put(UNIT_SPAWN_RATE_KEY, DEFAULT_UNIT_SPAWN_RATE);
        _valuesMap.put(UNIT_INITIAL_DAMAGE_KEY, (float) DEFAULT_INITIAL_UNIT_DAMAGE);

        _disabledTurretTypes = null;
        _waves = null;
    }

    public MapSettings clone() {
        MapSettings mapSettings = new MapSettings();

        mapSettings.setDisabledTurretTypes(_disabledTurretTypes);
        mapSettings.putValues(_valuesMap);
        mapSettings.setWaves(_waves);

        return mapSettings;
    }

    public void setDisabledTurretTypes(BaseTurret.Type[] types) {
        _disabledTurretTypes = types;
    }

    public boolean isTurretTypeDisabled(BaseTurret.Type type) {

        if (_disabledTurretTypes == null) {
            return false;
        }

        for (int i = 0; i < _disabledTurretTypes.length; i++) {
            if (type.equals(_disabledTurretTypes[i])) {
                return true;
            }
        }

        return false;
    }

    public float getValue(String key) {
        return _valuesMap.get(key);
    }

    public void putValue(String key, float value) {
        _valuesMap.put(key, value);
    }

    public void putValues(String[] keys, float[] values) {
        if (keys.length != values.length) {
            System.out.println("Error! Mismatched array lengths in putValues in MapSettings");
            return;
        }

        for (int i = 0; i < keys.length; i++) {
            _valuesMap.put(keys[i], values[i]);
        }
    }

    public void putValues(HashMap<String, Float> pValuesMap) {
        _valuesMap.putAll(pValuesMap);
    }

    public void setWaves(int[][] waves) {
        _waves = waves;
    }

    public int[][] getWaves() {
        return _waves;
    }
}

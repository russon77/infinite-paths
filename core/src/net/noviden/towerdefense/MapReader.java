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

import com.badlogic.gdx.files.FileHandle;

import net.noviden.towerdefense.TurretFactory.BaseTurret;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MapReader {
    /***
     * creates a map from a given file handle
     *
     * paths are given in lines like;
     *  STARTPATH
     *  0.25,0.25
     *  0.5,0.5
     *  0.75,0.25
     *  ENDPATH
     *  STARTPATH
     *  0.1,0.8
     *  0.2,0.1
     *  0.8,0.1
     *  ENDPATH
     *
     * tokens are "STARTPATH", "ENDPATH", "," and whatever else for map settings (i.e. name)
     *
     * mapsettings: "STARTSETTINGS", "ENDSETTINGS"
     */

    private static HashMap<String, Integer> _tokenMap;

    public static Map createMapFromFile(FileHandle pFileHandle) {

        if (_tokenMap == null) {
            _tokenMap = new HashMap<String, Integer>();

            _tokenMap.put("STARTPATH", 0);
            _tokenMap.put("STARTSETTINGS", 2);
        }

        ArrayList<Path> paths = new ArrayList<Path>();
        MapSettings mapSettings = null;
        String name = pFileHandle.name();

        BufferedReader reader = pFileHandle.reader(20);

        String s;

        try {
            while (reader.ready()) {
                s = reader.readLine().trim();

                // catch bad input, attempt to continue
                if (!_tokenMap.containsKey(s)) {
                    continue;
                }

                switch (_tokenMap.get(s)) {
                    case 0:
                        Path tmpPath = parsePathFromReader(reader);
                        if (tmpPath != null)
                            paths.add(tmpPath);

                        break;
                    case 2:
                        mapSettings = parseSettingsFromReader(reader);

                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

        // error: no paths supplied on map
        if (paths.size() == 0) {
            return null;
        }

        // error: map settings aren't supplied in file
        if (mapSettings == null) {
//            mapSettings = new MapSettings();
        }

        // convert arraylist to array -- bleh
        Path[] returnablePaths = new Path[paths.size()];
        for (int i = 0; i < returnablePaths.length; i++)
            returnablePaths[i] = paths.get(i);

        return new Map(returnablePaths, name, mapSettings);
    }

    // read until "ENDPATH", at which point return constructed path
    private static Path parsePathFromReader(BufferedReader pReader) {
        String s;
        int split;
        Point point;

        ArrayList<Point> pointSet = new ArrayList<Point>();

        try {
            while (pReader.ready()) {
                s = pReader.readLine().trim();

                if (s.equals("ENDPATH")) {
                    return new Path(pointSet, 5.0f);
                }

                split = s.indexOf(',');

                point = new Point(
                        Float.parseFloat(s.substring(0, split)),
                        Float.parseFloat(s.substring(split + 1, s.length())));

                pointSet.add(point);
            } ;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HashMap<String, Integer> _settingsMap;

    // thanks source = 1.6 UGHHHHHH plz switch(string) nao
    private static MapSettings parseSettingsFromReader(BufferedReader pReader) {

        if (_settingsMap == null) {
            _settingsMap = new HashMap<String, Integer>();

            _settingsMap.put(MapSettings.UNIT_SPAWN_RATE_KEY, 0);
            _settingsMap.put(MapSettings.UNIT_INITIAL_DAMAGE_KEY, 1);
            _settingsMap.put(MapSettings.UNIT_INITIAL_SPEED_KEY, 2);
            _settingsMap.put(MapSettings.UNIT_INITIAL_HEALTH_KEY, 3);

            _settingsMap.put(MapSettings.PLAYER_INITIAL_RESOURCES_KEY, 4);
            _settingsMap.put(MapSettings.PLAYER_INITIAL_HEALTH_KEY, 5);

            _settingsMap.put(BaseTurret.Type.BUFF.toString(), 6);
            _settingsMap.put(BaseTurret.Type.NORMAL.toString(), 7);
            _settingsMap.put(BaseTurret.Type.ROCKET.toString(), 8);
            _settingsMap.put(BaseTurret.Type.CHAINGUN.toString(), 9);
            _settingsMap.put(BaseTurret.Type.HOMING.toString(), 10);
            _settingsMap.put(BaseTurret.Type.SHOTGUN.toString(), 11);
        }

        MapSettings mapSettings = new MapSettings();

        BaseTurret.Type[] disabledTurretTypes;
        ArrayList<BaseTurret.Type> disabledTurretTypeList =
                new ArrayList<BaseTurret.Type>();

        String s, key, value;
        int splitIndex;
        char splitKey = '=';

        try {
            while (pReader.ready()) {
                s = pReader.readLine().trim();

                if (s.equals("ENDSETTINGS")) {

                    break;
                }

                splitIndex = s.indexOf(splitKey);

                // check for error in finding split token
                if (splitIndex < 0) {
                    continue;
                }

                key = s.substring(0, splitIndex).trim();
                value = s.substring(splitIndex + 1, s.length()).trim();

                // check for error in key existence
                if (!_settingsMap.containsKey(key)) {
                    continue;
                }

                switch (_settingsMap.get(key)) {
                    case 0:
                        mapSettings.putValue(MapSettings.UNIT_SPAWN_RATE_KEY,
                                Float.parseFloat(value));

                        break;
                    case 1:
                        mapSettings.putValue(MapSettings.UNIT_INITIAL_DAMAGE_KEY,
                                Float.parseFloat(value));

                        break;
                    case 2:
                        mapSettings.putValue(MapSettings.UNIT_INITIAL_SPEED_KEY,
                                Float.parseFloat(value));

                        break;
                    case 3:
                        mapSettings.putValue(MapSettings.UNIT_INITIAL_HEALTH_KEY,
                                Float.parseFloat(value));

                        break;
                    case 4:
                        mapSettings.putValue(MapSettings.PLAYER_INITIAL_RESOURCES_KEY,
                                Float.parseFloat(value));

                        break;
                    case 5:
                        mapSettings.putValue(MapSettings.PLAYER_INITIAL_HEALTH_KEY,
                                Float.parseFloat(value));

                        break;

                    case 6:
                        if (Boolean.parseBoolean(value))
                            disabledTurretTypeList.add(BaseTurret.Type.BUFF);

                        break;
                    case 7:
                        if (Boolean.parseBoolean(value))
                            disabledTurretTypeList.add(BaseTurret.Type.NORMAL);

                        break;
                    case 8:
                        if (Boolean.parseBoolean(value))
                            disabledTurretTypeList.add(BaseTurret.Type.ROCKET);

                        break;
                    case 9:
                        if (Boolean.parseBoolean(value))
                            disabledTurretTypeList.add(BaseTurret.Type.CHAINGUN);

                        break;
                    case 10:
                        if (Boolean.parseBoolean(value))
                            disabledTurretTypeList.add(BaseTurret.Type.HOMING);

                        break;
                    case 11:
                        if (Boolean.parseBoolean(value))
                            disabledTurretTypeList.add(BaseTurret.Type.SHOTGUN);

                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // insert any disabled turrets
        disabledTurretTypes = new BaseTurret.Type[disabledTurretTypeList.size()];
        disabledTurretTypes = disabledTurretTypeList.toArray(disabledTurretTypes);

        // DEBUG only
//        for (BaseTurret.Type type : disabledTurretTypes)
//            System.out.println(type.toString());

        mapSettings.setDisabledTurretTypes(disabledTurretTypes);

        return mapSettings;
    }
}

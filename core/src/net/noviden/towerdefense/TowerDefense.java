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

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import net.noviden.towerdefense.Maps.MapWriter;

import java.util.ArrayList;

public class TowerDefense extends Game {

//    public static final float SCREEN_WIDTH = 1000.0f, SCREEN_HEIGHT = 1000.0f;

    public ArrayList<net.noviden.towerdefense.Maps.Map> maps;

    public void create() {

        maps = new ArrayList<net.noviden.towerdefense.Maps.Map>();

        FileHandle[] files = Gdx.files.local("maps/").list();
        for (FileHandle file : files) {
            net.noviden.towerdefense.Maps.Map tmpMap = net.noviden.towerdefense.Maps.MapReader.createMapFromFile(file);

            if (tmpMap != null)
                maps.add(tmpMap);
//            maps.add(MapReader.createMapFromFile(file));
        }

        // initialize settings
        GameSettings.initialize();

        // create default maps, add them to game
        if (maps.size() == 0) {
            maps.add(net.noviden.towerdefense.Maps.Map.createFromDefault(net.noviden.towerdefense.Maps.Map.DefaultMaps.X));
            maps.add(net.noviden.towerdefense.Maps.Map.createFromDefault(net.noviden.towerdefense.Maps.Map.DefaultMaps.Z));
            maps.add(net.noviden.towerdefense.Maps.Map.createFromDefault(net.noviden.towerdefense.Maps.Map.DefaultMaps.STAR));
        }

        this.setScreen(new net.noviden.towerdefense.Screens.MainMenuScreen(this));
    }

    public void render() {
        super.render(); //important!
    }

    public void dispose() {

        FileHandle fileHandle;
        for (net.noviden.towerdefense.Maps.Map map : maps) {
            fileHandle = Gdx.files.local("maps/").child(map.getName());

            MapWriter.writeMapToFile(map, fileHandle);
        }

        GameSettings.writeSettingsToFile();

        System.out.println("Serialized settings to disk!");
    }
}

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
import com.badlogic.gdx.Preferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class TowerDefense extends Game {

    public static final float SCREEN_WIDTH = 1000.0f, SCREEN_HEIGHT = 1000.0f;

    public ArrayList<Map> maps;

    public void create() {

        maps = null;

        // attempt to serialize from disk
        try {
            FileInputStream fileInputStream = new FileInputStream("maps.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            maps = (ArrayList<Map>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();

            System.out.println("Successfully loaded maps!");
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }

        // initialize settings
        GameSettings.initialize();

        if (maps == null) {
            maps = new ArrayList<Map>();

            maps.add(Map.createFromDefault(Map.DefaultMaps.X));
            maps.add(Map.createFromDefault(Map.DefaultMaps.Z));
            maps.add(Map.createFromDefault(Map.DefaultMaps.STAR));
        }

        this.setScreen(new net.noviden.towerdefense.Screens.MainMenuScreen(this));
    }

    public void render() {
        super.render(); //important!
    }

    public void dispose() {

        // serialize/write to disk
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("maps.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(maps);
            objectOutputStream.close();
            fileOutputStream.close();

            System.out.println("Serialized maps to disk!");

            GameSettings.writeSettingsToFile();

            System.out.println("Serialized settings to disk!");

        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

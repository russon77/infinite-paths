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


package net.noviden.towerdefense.MissileFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

public class MissileManager {
    private static ArrayList<Missile> activeMissiles;

    private MissileManager() {}

    public static void initialize() {
        // upon a new game, have to reset pool and array
        if (activeMissiles != null) {
            activeMissiles.clear();
        } else {
            activeMissiles = new ArrayList<Missile>();
        }
    }

    public static void act(float deltaTime) {
        for (int i = 0; i < activeMissiles.size(); i++) {
            Missile missile = activeMissiles.get(i);

            missile.act(deltaTime);

            // check if missile should be removed
            if (!missile.isAlive) {
                activeMissiles.remove(i);

                i--;
            }
        }
    }

    public static void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.WHITE);
        for (Missile missile : activeMissiles) {
            missile.draw(shapeRenderer);
        }
    }

    public static void addMissile(Missile missile) {
        activeMissiles.add(missile);
    }

    public static ArrayList<Missile> getActiveMissiles() {
        return activeMissiles;
    }
}

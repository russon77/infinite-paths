/**
 TowerDefense : Infinite Tower Defense Game With User Created Maps
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

package net.noviden.towerdefense.MapCreator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;

public class MapThumbnail {

    public static void drawThumbnail(Map map, float scale, ShapeRenderer shapeRenderer) {

        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);

        for (Path path : map.paths) {

            for (int j = 0; j < (path.set.size() - 1); j++) {
                Point a = path.set.get(j),
                        b = path.set.get(j + 1);

                shapeRenderer.line(scale * a.x, scale * a.y,
                        scale * b.x, scale * b.y);
            }
        }
    }
}

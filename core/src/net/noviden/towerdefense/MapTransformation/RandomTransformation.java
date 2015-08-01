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

package net.noviden.towerdefense.MapTransformation;

import com.badlogic.gdx.Gdx;

import net.noviden.towerdefense.Maps.Map;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;

public class RandomTransformation implements Transformation {

    private enum func {
        SIN,
        COS,
        TAN
    }

    private func _randomSeed;

    public RandomTransformation() {
        _randomSeed = func.values()[(int) (3 * Math.random())];
    }

    public void transform(Map pMap, float pDelta) {
        float tmpX, tmpY;

        for (Path path : pMap.getPaths()) {
            for (Point point : path.set) {
                tmpX = point.x + ((-1.0f) + (2.0f * (float) Math.random())) * pDelta;

                if (tmpX >= 0.0f && tmpX <= Gdx.graphics.getWidth()) {
                    point.x = tmpX;
                }

                tmpY = point.y + ((-1.0f) + (2.0f * (float) Math.random())) * pDelta;
                if (tmpY >= 0.0f && tmpY <= Gdx.graphics.getHeight()) {
                    point.y = tmpY;
                }
            }
        }
    }
}

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

import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;

public class RandomTransformation extends Transformation {

    public void transform(Map pMap, float pDelta) {
        float tmpX, tmpY;

        for (Path path : pMap.getPaths()) {
            for (Point point : path.set) {
                tmpX = point.x + ((-1.0f) + (2.0f * (float) Math.random())) * pDelta;

                if (tmpX >= 0.0f && tmpX <= pMap.dimensions.width) {
                    point.x = tmpX;
                }

                tmpY = point.y + ((-1.0f) + (2.0f * (float) Math.random())) * pDelta;
                if (tmpY >= 0.0f && tmpY <= pMap.dimensions.height) {
                    point.y = tmpY;
                }
            }
        }
    }
}

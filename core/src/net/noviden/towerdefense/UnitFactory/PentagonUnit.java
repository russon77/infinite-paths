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

package net.noviden.towerdefense.UnitFactory;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;

public class PentagonUnit extends Unit {

    private static final float centerToVertex = 15.0f;

    public PentagonUnit(float health, float damage, float speed, Path path) {
        super(health, damage, speed, path);
    }

    public PentagonUnit(float health, float damage, float speed, Path path, Point initialLocation,
                      int currentDestinationIndex) {
        super(health, damage, speed, path, initialLocation, currentDestinationIndex);
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        // draw each unit's health as a percent of its shape
        float percentHealthMissing = 1.0f - (this.health / this.maxHealth);
        float percentToDraw;

        // thank you based wolframalpha
        // http://mathworld.wolfram.com/Pentagon.html

        float c1 = MathUtils.cos(MathUtils.PI * 2.0f / 5.0f) * centerToVertex,
                c2 = MathUtils.cos(MathUtils.PI / 5.0f) * centerToVertex,
                s1 = MathUtils.sin(MathUtils.PI * 2.0f / 5.0f) * centerToVertex,
                s2 = MathUtils.sin(MathUtils.PI * 4.0f / 5.0f) * centerToVertex;

        // draw a pentagon centered at the current location
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BASE_UNIT_COLOR);

        shapeRenderer.identity();

        shapeRenderer.translate(location.x, location.y, 0.f);
        shapeRenderer.rotate(0f, 0f, 1f, rotation);

        // draw the pentagon

        //          A
        //        *   *
        //      E   M   B
        //       *     *
        //        D * C

        // draw triangle MAB
        shapeRenderer.triangle(0,0,
                0, centerToVertex,
                s1, c1);

        // draw triangle MBC
        shapeRenderer.triangle(0,0,
                s1, c1,
                s2, -c2);

        // draw triangle MCD
        shapeRenderer.triangle(0,0,
                s2, -c2,
                -s2, -c2);

        // draw triangle MDE
        shapeRenderer.triangle(0,0,
                -s2, -c2,
                -s1, c1);

        // draw triangle MEA
        shapeRenderer.triangle(0, 0,
                -s1, c1,
                0, centerToVertex);

        if (percentHealthMissing > 0.0f) {
            // draw missing health
            shapeRenderer.setColor(BASE_UNIT_DAMAGED_COLOR);

            // triangle MAB
            percentToDraw =
                    MathUtils.clamp(percentHealthMissing / 0.2f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    0, centerToVertex,
                    s1 * percentToDraw,
                    centerToVertex - (Math.abs(centerToVertex - c1) * percentToDraw));

            // triangle MBC
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.2f) / 0.2f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    s1, c1,
                    s1 - (Math.abs(s1 - s2) * percentToDraw),
                        c1 - ((c1 + c2) * percentToDraw));

            // triangle MCD
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.4f) / 0.2f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    s2, -c2,
                    s2 - (s2 * 2 * percentToDraw), -c2);

            // triangle MDE
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.6f) / 0.2f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    -s2, -c2,
                    -s2 - (Math.abs(s1 - s2) * percentToDraw),
                    -c2 + ((c1 + c2) * percentToDraw));

            // triangle MEA
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.8f) / 0.2f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    -s1, c1,
                    -s1 + (s1 * percentToDraw),
                    c1 + (Math.abs(centerToVertex - c1) * percentToDraw));

        }

        shapeRenderer.identity();
    }

    @Override
    public Unit getNextUnitToSpawn() {
        return new SquareUnit(this.maxHealth, this.getDamage(),
                this.speed, this.path, this.location, this.currentDestinationIndex);
    }
}

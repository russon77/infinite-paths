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

public class SquareUnit extends Unit {

    private static final float sideLength = 15.0f;

    public SquareUnit(float health, float damage, float speed, Path path) {
        super(health, damage, speed, path);
    }

    public SquareUnit(float health, float damage, float speed, Path path, Point initialLocation,
                      int currentDestinationIndex) {
        super(health, damage, speed, path, initialLocation, currentDestinationIndex);
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        // draw each unit's health as a percent of its shape
        float percentHealthMissing = 1.0f - (this.health / this.maxHealth);

        // draw a square centered at the current location
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BASE_UNIT_COLOR);

        shapeRenderer.identity();

        shapeRenderer.translate(location.x, location.y, 0.f);
        shapeRenderer.rotate(0f, 0f, 1f, rotation);
        shapeRenderer.rect(-sideLength / 2, -sideLength / 2, sideLength, sideLength);

        // draw missing health: break up square into 4 distinct triangles
        if (percentHealthMissing > 0.0f) {
            shapeRenderer.setColor(BASE_UNIT_DAMAGED_COLOR);
            float percentToDraw;

            // draw 1 / 4
            percentToDraw =
                    MathUtils.clamp(percentHealthMissing / 0.25f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    sideLength / 2, -sideLength / 2,
                    sideLength / 2, -sideLength / 2 + percentToDraw * sideLength);

            // draw 2 / 4
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.25f) / 0.25f, 0, 1.0f);
            shapeRenderer.triangle(0,0,
                    sideLength / 2, sideLength / 2,
                    sideLength / 2 - percentToDraw * sideLength, sideLength / 2);

            // draw 3 / 4
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.5f) / 0.25f, 0, 1.0f);
            shapeRenderer.triangle(0,0,
                    -sideLength / 2, sideLength / 2,
                    -sideLength / 2, sideLength / 2 - percentToDraw * sideLength);

            // draw 4 / 4
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.75f) / 0.25f, 0, 1.0f);
            shapeRenderer.triangle(0,0,
                    -sideLength / 2, -sideLength / 2,
                    -sideLength / 2 + percentToDraw * sideLength, -sideLength / 2);
        }

        // reset the shapeRenderer transformation matrix
        shapeRenderer.identity();
    }

    @Override
    public Unit getNextUnitToSpawn() {
        return new TriangleUnit(this.maxHealth, this.getDamage(),
                this.speed, this.path, this.location, this.currentDestinationIndex);
    }
}

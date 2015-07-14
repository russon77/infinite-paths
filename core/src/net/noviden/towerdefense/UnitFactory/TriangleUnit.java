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

package net.noviden.towerdefense.UnitFactory;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;

public class TriangleUnit extends Unit {

    private static final float centerToVertex = 13.0f;

    public TriangleUnit(float health, float damage, float speed, Path path) {
        super(health, damage, speed, path);
    }

    public TriangleUnit(float health, float damage, float speed, Path path, Point initialLocation,
                        int currentDestinationIndex) {
        super(health, damage, speed, path, initialLocation, currentDestinationIndex);
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        // draw each unit's health as a percent of its shape
        float percentHealthMissing = 1.0f - (this.health / this.maxHealth);
        float percentToDraw;

        //      A
        //    *   *
        //   *  M  *
        //  *       *
        // C * * * * B

        float ax = 0, ay = centerToVertex,
                bx = (float) Math.sqrt(3.0f) * centerToVertex / 2,
                by = -centerToVertex / 2,
                cx = (-1.0f) * (float) Math.sqrt(3.0f) * centerToVertex / 2,
                cy = -centerToVertex / 2;

        // draw a square centered at the current location
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BASE_UNIT_COLOR);

        shapeRenderer.identity();

        shapeRenderer.translate(location.x, location.y, 0.f);
        shapeRenderer.rotate(0f, 0f, 1f, rotation);

        shapeRenderer.triangle(ax, ay, bx, by, cx, cy);

        // draw in health missing, break down large triangle into three smaller ones
        shapeRenderer.setColor(BASE_UNIT_DAMAGED_COLOR);

        if (percentHealthMissing > 0.0f) {

            // draw 1 / 3, MAB
            percentToDraw =
                    MathUtils.clamp(percentHealthMissing / 0.33f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    ax, ay,
                    ax + (Math.abs(ax - bx) * percentToDraw), ay - (Math.abs(ay - by) * percentToDraw));

            // draw 2 / 3, MBC
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.33f) / 0.33f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    bx, by,
                    bx - (bx * percentToDraw * 2), cy);

            // draw 3 / 3, MCA
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.66f) / 0.33f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    cx, cy,
                    cx + (Math.abs(cx - ax) * percentToDraw), cy + (Math.abs(cy - ay) * percentToDraw));
        }

        shapeRenderer.identity();
    }

    @Override
    public Unit getNextUnitToSpawn() {
        return new Unit(this.maxHealth, this.getDamage(),
                this.speed, this.path, this.location, this.currentDestinationIndex);
    }

    /*
    @Override
    public boolean collidesWith(Missile missile) {
        float distanceBetween = (float) Math.sqrt(
                Math.pow(this.location.x - missile.location.x, 2) +
                        Math.pow(this.location.y - missile.location.y, 2));

        if (distanceBetween < centerToVertex) {
            // possibly a hit
        }

        return false;
    }

    */
}

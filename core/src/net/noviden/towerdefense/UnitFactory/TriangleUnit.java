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
import com.badlogic.gdx.math.Vector2;

import net.noviden.towerdefense.CollisionManager;
import net.noviden.towerdefense.MissileFactory.Missile;
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
    protected void setUpBoundaries() {

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

        points = new Point[3];
        rotatedPoints = new Point[3];

        for (int i = 0; i < rotatedPoints.length; i++)
            rotatedPoints[i] = new Point(0,0);

        rotationVector = new Vector2();

        points[0] = new Point(ax, ay);
        points[1] = new Point(bx, by);
        points[2] = new Point(cx, cy);
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        // draw each unit's health as a percent of its shape
        float percentHealthMissing = 1.0f - (this.health / this.maxHealth);
        float percentToDraw;

        // draw a square centered at the current location
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BASE_UNIT_COLOR);

        shapeRenderer.identity();

        shapeRenderer.translate(location.x, location.y, 0.f);
        shapeRenderer.rotate(0f, 0f, 1f, rotation);

        shapeRenderer.triangle(points[0].x, points[0].y,
                points[1].x, points[1].y,
                points[2].x, points[2].y);

        // draw in health missing, break down large triangle into three smaller ones
        shapeRenderer.setColor(BASE_UNIT_DAMAGED_COLOR);

        if (percentHealthMissing > 0.0f) {

            // draw 1 / 3, MAB
            percentToDraw =
                    MathUtils.clamp(percentHealthMissing / 0.33f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    points[0].x, points[0].y,
                    points[0].x + (Math.abs(points[0].x - points[1].x) * percentToDraw),
                        points[0].y - (Math.abs(points[0].y - points[1].y) * percentToDraw));

            // draw 2 / 3, MBC
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.33f) / 0.33f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    points[1].x, points[1].y,
                    points[1].x - (points[1].x * percentToDraw * 2), points[2].y);

            // draw 3 / 3, MCA
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.66f) / 0.33f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    points[2].x, points[2].y,
                    points[2].x + (Math.abs(points[2].x - points[0].x) * percentToDraw),
                        points[2].y + (Math.abs(points[2].y - points[0].y) * percentToDraw));
        }

        shapeRenderer.identity();
    }

    @Override
    public Unit getNextUnitToSpawn() {
        return new Unit(this.maxHealth, this.getDamage(),
                this.speed, this.path, this.location, this.currentDestinationIndex);
    }

    @Override
    public boolean collidesWith(Missile missile) {
        float distanceBetween = (float) Math.sqrt(
                Math.pow(this.location.x - missile.location.x, 2) +
                        Math.pow(this.location.y - missile.location.y, 2));

        if (distanceBetween < centerToVertex) {
            // possibly a hit, need to investigate further

            for (int i = 0; i < rotatedPoints.length; i++) {
                rotationVector.set(points[i].x, points[i].y);
                rotationVector.rotate(rotation);
                rotatedPoints[i].set(
                        location.x + rotationVector.x, location.y + rotationVector.y);
            }

            return CollisionManager.lineCollision(rotatedPoints, missile);
        }

        return false;
    }
}

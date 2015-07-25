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

public class HexagonUnit extends Unit {

    private static final float centerToVertex = 15.0f;

    public HexagonUnit(float health, float damage, float speed, Path path) {
        super(health, damage, speed, path);
    }

    public HexagonUnit(float health, float damage, float speed, Path path, Point initialLocation,
                        int currentDestinationIndex) {
        super(health, damage, speed, path, initialLocation, currentDestinationIndex);
    }

    @Override
    protected void setUpBoundaries() {

        float s = centerToVertex / 2,
                c = (float) Math.sqrt(3) * centerToVertex / 2;

        points = new Point[6];
        rotatedPoints = new Point[6];

        for (int i = 0; i < rotatedPoints.length; i++)
            rotatedPoints[i] = new Point(0,0);

        rotationVector = new Vector2();

        points[0] = new Point( + centerToVertex, 0);
        points[1] = new Point( + s,  - c);
        points[2] = new Point( - s,  - c);
        points[3] = new Point( - centerToVertex, 0);
        points[4] = new Point( - s, + c);
        points[5] = new Point( + s, + c);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        // draw each unit's health as a percent of its shape
        float percentHealthMissing = 1.0f - (this.health / this.maxHealth);
        float percentToDraw;

        float s = centerToVertex / 2,
                c = (float) Math.sqrt(3) * centerToVertex / 2;

        // draw a hexagon centered at the current location
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BASE_UNIT_COLOR);

        shapeRenderer.identity();

        shapeRenderer.translate(location.x, location.y, 0.f);
        shapeRenderer.rotate(0f, 0f, 1f, rotation);

        //     E * * * F
        //   * * * * * * *
        // D * * * M * * * A
        //   * * * * * * *
        //    C * * * B

        // draw triangle MAB
        shapeRenderer.triangle(0,0,
                centerToVertex, 0,
                s, -c);

        // draw triangle MBC
        shapeRenderer.triangle(0,0,
                s, -c,
                -s, -c);

        // draw triangle MCD
        shapeRenderer.triangle(0,0,
                -s, -c,
                -centerToVertex, 0);

        // draw triangle MDE
        shapeRenderer.triangle(0,0,
                -centerToVertex, 0,
                -s, c);

        // draw triangle MEF
        shapeRenderer.triangle(0,0,
                -s, c,
                s, c);

        // draw triangle MFA
        shapeRenderer.triangle(0, 0,
                s, c,
                centerToVertex, 0);

        if (percentHealthMissing > 0.0f) {

            // draw in missing health
            shapeRenderer.setColor(BASE_UNIT_DAMAGED_COLOR);

            // draw triangle MAB
            percentToDraw =
                    MathUtils.clamp(percentHealthMissing / 0.166f, 0, 1.0f);
            shapeRenderer.triangle(0,0,
                    centerToVertex, 0,
                    centerToVertex - (Math.abs(centerToVertex - s) * percentToDraw),
                        -c * percentToDraw);

            // draw triangle MBC
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.166f) / 0.166f, 0, 1.0f);
            shapeRenderer.triangle(0,0,
                    s, -c,
                    s - (s * 2 * percentToDraw), -c);

            // draw triangle MCD
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.33f) / 0.166f, 0, 1.0f);
            shapeRenderer.triangle(0,0,
                    -s, -c,
                    -s - (Math.abs(centerToVertex - s) * percentToDraw),
                        -c + (c * percentToDraw));

            // draw triangle MDE
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.5f) / 0.166f, 0, 1.0f);
            shapeRenderer.triangle(0,0,
                    -centerToVertex, 0,
                    -centerToVertex + (Math.abs(centerToVertex - s) * percentToDraw),
                        c * percentToDraw);

            // draw triangle MEF
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.66f) / 0.166f, 0, 1.0f);
            shapeRenderer.triangle(0,0,
                    -s, c,
                    -s + (s * 2 * percentToDraw), c);

            // draw triangle MFA
            percentToDraw =
                    MathUtils.clamp((percentHealthMissing - 0.83f) / 0.166f, 0, 1.0f);
            shapeRenderer.triangle(0, 0,
                    s, c,
                    s + (Math.abs(centerToVertex - s) * percentToDraw),
                        c - (c * percentToDraw));
        }

        shapeRenderer.identity();
    }

    @Override
    public Unit getNextUnitToSpawn() {
        return new PentagonUnit(this.maxHealth, this.getDamage(),
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

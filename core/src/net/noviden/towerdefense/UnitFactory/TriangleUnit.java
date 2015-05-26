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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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

        // draw a square centered at the current location
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.PURPLE);

        shapeRenderer.identity();

        shapeRenderer.translate(location.x, location.y, 0.f);
        shapeRenderer.rotate(0f, 0f, 1f, rotation);

        shapeRenderer.triangle(0, centerToVertex,
                centerToVertex / 2, (-1.0f) * (float) Math.sqrt(3.0f) * centerToVertex / 2,
                -centerToVertex / 2, (-1.0f) *(float)  Math.sqrt(3.0f) * centerToVertex / 2);


        shapeRenderer.identity();
    }
}

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


package net.noviden.towerdefense.MissileFactory;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.TowerDefense;
import net.noviden.towerdefense.UnitFactory.Unit;

import java.util.LinkedList;

public class Missile {

    private static final float BASE_SPEED = 35.0f;
    private static final float BASE_RADIUS = 5.0f;

    public Point location;
    public float radius;
    public float damage;

    protected float xVelocity, yVelocity;
    protected boolean isAlive;

    // to make "piercing rounds" and splitting work correctly, keep a list of
    protected LinkedList<Unit> ignoredUnits;

    public Missile() {
        isAlive = true;
    }

    public Missile(Point origin, Point destination, float damage) {
        this.location = origin.clone();
        this.radius = BASE_RADIUS; this.damage = damage;
        this.isAlive = true;

        float distanceBetween = (float) Math.sqrt(
                Math.pow(origin.x - destination.x, 2) + Math.pow(origin.y - destination.y, 2));

        this.xVelocity = (destination.x - origin.x) / distanceBetween * BASE_SPEED;
        this.yVelocity = (destination.y - origin.y) / distanceBetween * BASE_SPEED;

        this.ignoredUnits = new LinkedList<Unit>();
    }

    public void act(float deltaTime) {
        this.location.x += xVelocity * deltaTime * BASE_SPEED;
        this.location.y += yVelocity * deltaTime * BASE_SPEED;

        if (this.location.x < 0.0f || this.location.x > TowerDefense.SCREEN_WIDTH ||
                this.location.y < 0.0f || this.location.y > TowerDefense.SCREEN_HEIGHT) {
            this.isAlive = false;
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(location.x, location.y, BASE_RADIUS);
    }

    public void uniqueAction(Unit unit) {
        // no unique action for base missile
    }

    public boolean ignoresUnit(Unit target) {
        for (Unit unit : ignoredUnits) {
            if (target.location.equals(unit.location)) {
                return true;
            }
        }

        return false;
    }

    public void addIgnoredUnit(Unit unit) {
        ignoredUnits.add(unit);
    }
}

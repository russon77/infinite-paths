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

import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.UnitFactory.Unit;

import java.util.LinkedList;

public class SplittingMissile extends Missile {

    private static final float BASE_RADIUS = 8.0f;
    private static final float BASE_SPEED = 35.0f;

    private static final float BASE_SHRAPNEL_DAMAGE = 15.0f;

    private int numShrapnel;
    private float shrapnelDamage;

    public SplittingMissile(Point origin, Point destination, float damage, int numShrapnel) {
        this.location = origin.clone();
        this.radius = BASE_RADIUS; this.damage = damage;

        float distanceBetween = (float) Math.sqrt(
                Math.pow(origin.x - destination.x, 2) + Math.pow(origin.y - destination.y, 2));

        this.xVelocity = (destination.x - origin.x) / distanceBetween * BASE_SPEED;
        this.yVelocity = (destination.y - origin.y) / distanceBetween * BASE_SPEED;

        this.numShrapnel = numShrapnel;
        this.shrapnelDamage = BASE_SHRAPNEL_DAMAGE;

        this.ignoredUnits = new LinkedList<Unit>();
    }

    @Override
    public void uniqueAction(Unit unit) {
        Point target = new Point(0, 0);

        for (int j = 0; j < numShrapnel; j++) {
            target.x = location.x +
                    (float) Math.cos(2.0f * Math.PI * (float) j / (float) numShrapnel);
            target.y = location.y +
                    (float) Math.sin(2.0f * Math.PI * (float) j / (float) numShrapnel);

            Missile shrapnelMissile = new Missile(location,
                    target, shrapnelDamage);

            // make sure that the split does not only hit the unit it exploded 'on'
            shrapnelMissile.addIgnoredUnit(unit);

            MissileManager.addMissile(shrapnelMissile);
        }
    }

    public int getNumShrapnel() {
        return this.numShrapnel;
    }

    public Point getLocation() {
        return this.location;
    }

    public float getShrapnelDamage() {
        return this.shrapnelDamage;
    }
}

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


package net.noviden.towerdefense.MissileFactory;

import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.UnitFactory.Unit;

import java.util.LinkedList;

public class PierceMissile extends Missile {

    private static final float BASE_RADIUS = 5.0f;
    private static final float BASE_SPEED = 10.0f;

    private int pierceAmount;

    public PierceMissile(Point origin, Point destination, float damage, int pierceAmount) {
        this.location = origin.clone();
        this.radius = BASE_RADIUS; this.damage = damage;

        float distanceBetween = (float) Math.sqrt(
                Math.pow(origin.x - destination.x, 2) + Math.pow(origin.y - destination.y, 2));

        this.xVelocity = (destination.x - origin.x) / distanceBetween * BASE_SPEED;
        this.yVelocity = (destination.y - origin.y) / distanceBetween * BASE_SPEED;

        this.pierceAmount = pierceAmount;

        this.ignoredUnits = new LinkedList<Unit>();
    }

    public boolean canAddIgnoredUnit() {
        return (this.ignoredUnits.size() < this.pierceAmount);
    }

    @Override
    public void uniqueAction(Unit unit) {
        if (canAddIgnoredUnit()) {
            addIgnoredUnit(unit);
        }
    }
}

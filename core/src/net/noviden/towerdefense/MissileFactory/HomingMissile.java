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
import net.noviden.towerdefense.Unit;
import net.noviden.towerdefense.UnitManager;

import java.util.LinkedList;

public class HomingMissile extends net.noviden.towerdefense.MissileFactory.Missile {

    private static final float BASE_RADIUS = 5.0f;
    private static final float BASE_SPEED = 5.0f;

    private static final float BASE_TIME_UNTIL_LOCKON = 2.0f;

    private float speedMultiple;
    private Unit target;
    private UnitManager unitManager;

    private float timeAlive;
    private float range;

    public HomingMissile(Point origin, Unit initialTarget, UnitManager unitManager,
                         float damage, float range) {
        this.location = origin.clone();
        this.radius = BASE_RADIUS; this.damage = damage;
        this.range =range;
        this.timeAlive = 0.0f;

        this.speedMultiple = 1.0f;
        this.target = initialTarget;
        this.unitManager = unitManager;

        // find a random point within small radius and set course for that
        //  before locking on and heading towards main target
        Point randomHeading = new Point(
                origin.x - 5.0f + (float) Math.random() * 5.0f,
                origin.y - 5.0f + (float) Math.random() * 5.0f);

        float distanceBetween = (float) Math.sqrt(
                Math.pow(origin.x - randomHeading.x, 2) +
                        Math.pow(origin.y - randomHeading.y, 2));

        this.xVelocity = (randomHeading.x - origin.x) / distanceBetween * BASE_SPEED * 0.33f;
        this.yVelocity = (randomHeading.y - origin.y) / distanceBetween * BASE_SPEED * 0.33f;

        this.ignoredUnits = new LinkedList<Unit>();
    }

    @Override
    public void act(float deltaTime) {
        timeAlive += deltaTime;

        if (timeAlive > BASE_TIME_UNTIL_LOCKON) {
            // test if target unit still exists
            if (target.isDead()) {
                // find a new target
                for (Unit unit : unitManager.units) {

                    if (unit.isDead()) {
                        continue;
                    }

                    float distanceBetween = (float) Math.sqrt(
                            Math.pow(this.location.x - target.location.x, 2) +
                                    Math.pow(this.location.y - target.location.y, 2));

                    if (distanceBetween < this.range) {
                        target = unit;
                    }
                }
            }

            // if target is still dead, that means no other valid target is available within range
            if (!target.isDead()) {
                // recalculate velocities
                float distanceBetween = (float) Math.sqrt(
                        Math.pow(this.location.x - target.location.x, 2) +
                                Math.pow(this.location.y - target.location.y, 2));

                this.xVelocity = (target.location.x - this.location.x) / distanceBetween * BASE_SPEED;
                this.yVelocity = (target.location.y - this.location.y) / distanceBetween * BASE_SPEED;
            }
        }

        // multiply velocities by small factor to increase velocity
        speedMultiple *= (1.0f + deltaTime);

        // finally advance position of this missile
        this.location.x += xVelocity * deltaTime * speedMultiple;
        this.location.y += yVelocity * deltaTime * speedMultiple;
    }

}

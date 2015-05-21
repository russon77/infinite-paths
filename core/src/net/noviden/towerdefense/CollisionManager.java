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

package net.noviden.towerdefense;

import net.noviden.towerdefense.MissileFactory.Missile;
import net.noviden.towerdefense.MissileFactory.MissileManager;
import net.noviden.towerdefense.MissileFactory.PierceMissile;
import net.noviden.towerdefense.MissileFactory.SlowingMissile;
import net.noviden.towerdefense.MissileFactory.SplittingMissile;

public class CollisionManager {

    public static void processCollisions(UnitManager unitManager) {
        for (Unit unit : unitManager.units) {

            if (unit.isDead()) {
                continue;
            }

            for (int i = 0; i < net.noviden.towerdefense.MissileFactory.MissileManager.missiles.size(); i++) {
                // if distance between is less than sum of their radii, then collision has occurred

//                if (net.noviden.towerdefense.MissileFactory.MissileManager.missiles.size() == 0) {
//                    return;
//                }

                Missile tmpMissile = MissileManager.missiles.get(i);

                if (tmpMissile.ignoresUnit(unit)) {
                    continue;
                }

                float distanceBetween = (float) Math.sqrt(
                        Math.pow(unit.location.x - tmpMissile.location.x, 2) +
                                Math.pow(unit.location.y - tmpMissile.location.y, 2));

                // COLLISION DETECTED
                if (distanceBetween < (tmpMissile.radius + unit.radius)) {

                    // UNIT TAKES DAMAGE
                    unit.takeDamage(tmpMissile.damage);

                    // SUBCLASS SPECIFICS

                    // IMPLEMENT PIERCE THROUGH
                    if (tmpMissile.getClass() == net.noviden.towerdefense.MissileFactory.PierceMissile.class) {
                        PierceMissile pierceMissile = (PierceMissile) tmpMissile;

                        // if missile can pass through this unit, do so, otherwise become deleted
                        if (pierceMissile.canAddIgnoredUnit()) {
                            pierceMissile.addIgnoredUnit(unit);
                            continue;
                        }
                    }

                    // IMPLEMENT SPLIT FOR ROCKETS
                    if(tmpMissile.getClass() == SplittingMissile.class) {
                        SplittingMissile splittingMissile = (SplittingMissile) tmpMissile;

                        splittingMissile.split(unit);
                    }

                    // IMPLEMENT SLOWING MISSILES
                    if (tmpMissile.getClass() == SlowingMissile.class) {
                        SlowingMissile slowingMissile = (SlowingMissile) tmpMissile;

                        unit.slowDown(slowingMissile.getTimeSlowed(), slowingMissile.getPercentSlowed());
                    }

                    // FINALLY, DELETE THIS MISSILE
                    net.noviden.towerdefense.MissileFactory.MissileManager.missiles.remove(i);

                    i--;
                }
            }
        }
    }
}

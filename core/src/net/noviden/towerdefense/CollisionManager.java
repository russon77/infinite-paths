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

package net.noviden.towerdefense;

import net.noviden.towerdefense.MissileFactory.Missile;
import net.noviden.towerdefense.MissileFactory.MissileManager;
import net.noviden.towerdefense.UnitFactory.Unit;
import net.noviden.towerdefense.UnitFactory.UnitManager;

import java.util.ArrayList;

public class CollisionManager {

    // if distance between unit and missile is less than sum of their radii,
    //  then collision has occurred
    public static void processCollisions(UnitManager unitManager) {

        ArrayList<Missile> activeMissiles = MissileManager.getActiveMissiles();
        Missile tmpMissile;

        for (Unit unit : unitManager.units) {

            if (unit.isDead()) {
                continue;
            }

            for (int i = 0; i < activeMissiles.size(); i++) {

                tmpMissile = activeMissiles.get(i);

                if (tmpMissile.ignoresUnit(unit)) {
                    continue;
                }

                if (unit.collidesWith(tmpMissile)) {
                    // UNIT TAKES DAMAGE
                    unit.takeDamage(tmpMissile.damage);

                    // Unique actions
                    tmpMissile.uniqueAction(unit);

                    // FINALLY, DELETE THIS MISSILE
                    activeMissiles.remove(i);

                    i--;
                }
            }
        }
    }
}

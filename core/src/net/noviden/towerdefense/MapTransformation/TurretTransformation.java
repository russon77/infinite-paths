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

package net.noviden.towerdefense.MapTransformation;

import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.TurretFactory.BaseTurret;
import net.noviden.towerdefense.TurretFactory.TurretManager;

public class TurretTransformation implements Transformation {

    /***
     * Transformation that slowly moves all points in set towards nearest turret,
     *  forcing player to strategize around where he places turrets relative to map
     */

    private static final float TRANSFORMATION_SLOWDOWN_FACTOR = 0.01f;

    private TurretManager _turretManager;

    public TurretTransformation(TurretManager pTurretManager) {
        _turretManager = pTurretManager;
    }

    public void transform(Map pMap, float pDelta) {
        BaseTurret closestTurret;
        Point turretLocation;

        for (Path path : pMap.getPaths()) {
            for (Point point : path.set) {
                for (BaseTurret turret : _turretManager.getTurrets()) {

                    turretLocation = turret.getLocation();

                    // finally, translate point
                    if (turretLocation.x < point.x) {
                        point.x -= pDelta * Math.abs(turretLocation.x - point.x)
                                * TRANSFORMATION_SLOWDOWN_FACTOR;
                    } else {
                        point.x += pDelta * Math.abs(turretLocation.x - point.x)
                                * TRANSFORMATION_SLOWDOWN_FACTOR;
                        ;
                    }

                    if (turretLocation.y < point.y) {
                        point.y -= pDelta * Math.abs(turretLocation.y - point.y)
                                * TRANSFORMATION_SLOWDOWN_FACTOR;
                        ;
                    } else {
                        point.y += pDelta * Math.abs(turretLocation.y - point.y)
                                * TRANSFORMATION_SLOWDOWN_FACTOR;
                        ;
                    }

                }
            }
        }
    }

    private BaseTurret findTurretClosestToPoint(Point pPoint) {
        double closestDistance = (-1.0f);
        BaseTurret closestTurret = null;

        for (BaseTurret turret : _turretManager.getTurrets()) {
            double distanceBetween = Math.sqrt(
                    Math.pow(turret.getLocation().x - pPoint.x, 2) +
                            Math.pow(turret.getLocation().y - pPoint.y, 2));

            if (closestDistance < 0.0f ||
                    distanceBetween < closestDistance) {
                closestDistance = distanceBetween;
                closestTurret = turret;
            }
        }

        return closestTurret;
    }
}

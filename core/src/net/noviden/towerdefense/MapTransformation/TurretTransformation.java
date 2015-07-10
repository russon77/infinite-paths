package net.noviden.towerdefense.MapTransformation;

import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.TurretFactory.BaseTurret;
import net.noviden.towerdefense.TurretFactory.TurretManager;

public class TurretTransformation extends Transformation {

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

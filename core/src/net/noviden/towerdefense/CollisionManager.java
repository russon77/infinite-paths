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

import com.badlogic.gdx.math.Vector2;

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

    private static int[] _collisionCountersPerSide = new int[8];
    private static float lengthProjV;
    private static Vector2 pt_v = new Vector2(),
                        seg_v_unit = new Vector2(),
                        proj_v = new Vector2();
    private static Point closest = new Point(0,0);

    /***
     * thanks to
     * http://doswa.com/2009/07/13/circle-segment-intersectioncollision.html
     **/
    public static boolean lineCollision(Point[] points, Missile missile) {
        // number of sides is equal to number of points given
        Vector2[] sides = new Vector2[points.length];

        Point pointA, pointB;
        for (int i = 0; i < sides.length; i++) {
            pointA = points[ (i+1) % sides.length ];
            pointB = points[ i ];

            sides[i] = new Vector2(pointA.x - pointB.x, pointA.y - pointB.y);
        }

        for (int i = 0; i < sides.length; i++) {

            pt_v.set(missile.location.x - points[i].x,
                    missile.location.y - points[i].y);

            seg_v_unit.set(
                    sides[i].cpy().scl(
                            (1.0f / sides[i].len())));

            lengthProjV = Math.abs(pt_v.dot(seg_v_unit));

            if (lengthProjV < 0) {
                closest.set(points[i].x, points[i].y);
            } else if (lengthProjV > sides[i].len()) {
                closest.set(points[(i+1) % 3].x, points[(i+1)%3].y);
            } else {
                proj_v.set(
                        seg_v_unit.scl(lengthProjV));
                closest.set(points[i].x + proj_v.x,
                        points[i].y + proj_v.y);
            }

            float distanceBetweenClosest = (float) Math.sqrt(
                    Math.pow(closest.x - missile.location.x, 2) +
                            Math.pow(closest.y - missile.location.y, 2));

            if (distanceBetweenClosest < missile.radius) {
                // increment counter
                _collisionCountersPerSide[i]++;

                return true;
            }
        }

        return false;
    }
}

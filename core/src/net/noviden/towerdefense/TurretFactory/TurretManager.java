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


package net.noviden.towerdefense.TurretFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.UnitFactory.UnitManager;

import java.util.ArrayList;

public class TurretManager {

    protected ArrayList<BaseTurret> turrets;

    public TurretManager() {
        this.turrets = new ArrayList<BaseTurret>();
    }

    public void act(float deltaTime, UnitManager unitManager) {
        for (BaseTurret turret : turrets) {
            turret.act(deltaTime, unitManager);
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.BLUE);
        for (BaseTurret turret : turrets) {
            turret.draw(shapeRenderer);
        }
    }

    public void addTurret(BaseTurret turret) {
        turrets.add(turret);
    }

    public void removeTurret(BaseTurret turret) {
        turrets.remove(turret);
    }

    // TODO add in map path testing
    public boolean validPlacementForTurret(Point target, Map map) {
        for (BaseTurret turret : turrets) {
            if (turret.containsPoint(target)) {
                return false;
            }
        }

        return true;
    }

    public BaseTurret findTurretByLocation(Point target) {
        for (BaseTurret turret : turrets) {
            if (turret.containsPoint(target)) {
                return turret;
            }
        }

        return null;
    }

    public ArrayList<BaseTurret> getTurrets() {
        return turrets;
    }
}

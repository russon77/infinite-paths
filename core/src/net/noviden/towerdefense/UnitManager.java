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

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

public class UnitManager {

    private static final float PRE_WAVE_COOLDOWN_TIME = 3.0f;
    private static final float BASE_COOLDOWN = 1.0f;
    private static final float BASE_UNIT_HEALTH = 100.0f;
    private static final float BASE_UNIT_SPEED = 100.0f;
    private static final int BASE_UNIT_DAMAGE = 1;

    private Path path;

    public ArrayList<Unit> units;

    // represents wave / difficulty
    private int level;

    private float cooldownTimer;
    private float gameTime;

    public UnitManager(Path path) {
        this.cooldownTimer = PRE_WAVE_COOLDOWN_TIME;
        this.units = new ArrayList<Unit>();
        this.path = path;
        this.level = 0;
        this.gameTime = 0.0f;
    }

    public void act(float deltaTime, Player player) {
        // increase gametime
        gameTime += deltaTime;

        // decrease cooldownTimer
        cooldownTimer -= deltaTime;
        if (cooldownTimer < 0.0f) {
            // spawn a new unit and go on cooldown

            // every 15 seconds units double in health/damage
            float unitHealth = BASE_UNIT_HEALTH * (1.0f + (gameTime / 15.0f)),
                    unitDamage = BASE_UNIT_DAMAGE * (1.0f + (gameTime / 15.0f));
//                    unitSpeed = BASE_UNIT_SPEED * (1.0f + (gameTime / 15.0f));
            units.add(new Unit(unitHealth, unitDamage, BASE_UNIT_SPEED, path));
            cooldownTimer = BASE_COOLDOWN;
        }

        // process all units, calling `act` on each
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);

            unit.act(deltaTime, player);

            if (unit.isDead()) {

                // update player fields based on cause of death and remove unit

                if (unit.reachedEndOfPath()) {
                    player.decreaseHealth(unit.getDamage());
                } else {
                    player.addResources(unit.getWorth());
                    player.increaseScore(unit.getWorth());
                    player.increaseNumUnitsKilled();
                }

                units.remove(i);
            }
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        for (Unit unit : units) {
            unit.draw(shapeRenderer);
        }
    }
}

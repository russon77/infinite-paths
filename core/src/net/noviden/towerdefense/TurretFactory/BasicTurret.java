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

import net.noviden.towerdefense.MissileFactory.MissileManager;
import net.noviden.towerdefense.MissileFactory.PierceMissile;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.UnitFactory.Unit;
import net.noviden.towerdefense.UnitFactory.UnitManager;

public class BasicTurret extends BaseTurret {

    public static final int BASE_COST = 50;
    public static final float BASE_RANGE = 250.0f;
    private static final float BASE_DAMAGE = 25.0f;
    private static final float BASE_COOLDOWN = 0.5f;

    private static final String UNIQUE_MODIFIER_NAME = "Pierce";

    private int pierceAmount; // amount of units to pierce for each missile

    public BasicTurret(Point location) {
        this.location = location;
        this.level = 0;
        this.type = Type.NORMAL;
        this.cooldownTimer = 0.0f;

        this.range = BASE_RANGE;
        this.damage = BASE_DAMAGE;
        this.cooldownLength = BASE_COOLDOWN;
        this.radius = BASE_SIZE_RADIUS;
        this.worth = BASE_COST;
    }

    public void attack(Unit target) {
        MissileManager.addMissile(new PierceMissile(this.location,
                target.location, this.damage, this.pierceAmount));
    }

    public void draw(ShapeRenderer shapeRenderer) {
        // draw base turret
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BASE_TURRET_COLOR);
        shapeRenderer.circle(location.x, location.y, radius);

        // draw identifying feature: one white dot in center
        shapeRenderer.setColor(Color.WHITE);

        // draw center circle
        shapeRenderer.circle(location.x, location.y, BASE_SIZE_RADIUS * 0.15f);
    }

    public void upgradeUniqueModifier() {
        preUpgrade();
        this.pierceAmount++;
    }

    public void upgradeRange() {
        preUpgrade();
        this.range += BASE_RANGE * 0.10f;
    }

    public void upgradeDamage() {
        preUpgrade();
        this.damage += BASE_DAMAGE * 0.10f;
    }

    public boolean canUpgradeUniqueModifier() {
        return true;
    }

    public float getUniqueModifierValue() {
        return this.pierceAmount;
    }

    public String getUniqueModifierName() {
        return UNIQUE_MODIFIER_NAME;
    }

    public int getWorth() {
        return this.worth;
    }
}

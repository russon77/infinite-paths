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


package net.noviden.towerdefense.TurretFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.noviden.towerdefense.MissileFactory.MissileManager;
import net.noviden.towerdefense.MissileFactory.SplittingMissile;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.UnitFactory.Unit;

public class RocketTurret extends BaseTurret {

    public static final int BASE_COST = 50;
    public static final float BASE_RANGE = 375.0f;
    private static final float BASE_DAMAGE = 100.0f;
    private static final float BASE_COOLDOWN = 2.5f;

    private static final String UNIQUE_MODIFIER_NAME = "Shrapnel";

    private static final int BASE_SHRAPNEL_PER_ROCKET = 8;

    private int numShrapnelPerRocket;

    public RocketTurret(Point location) {
        this.location = location;
        this.level = 0;
        this.type = Type.CHAINGUN;
        this.cooldownTimer = 0.0f;

        this.range = BASE_RANGE;
        this.damage = BASE_DAMAGE;
        this.cooldownLength = this.baseCooldownLength = BASE_COOLDOWN;
        this.radius = BASE_SIZE_RADIUS;
        this.worth = BASE_COST;

        this.numShrapnelPerRocket = BASE_SHRAPNEL_PER_ROCKET;
    }

    public void attack(Unit target) {
        MissileManager.addMissile(new SplittingMissile(this.location, target.location,
                this.damage, this.numShrapnelPerRocket));
    }

    public void draw(ShapeRenderer shapeRenderer) {
        // draw base turret
        shapeRenderer.setColor(BASE_TURRET_COLOR);
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(location.x, location.y, BASE_SIZE_RADIUS);

        // draw identifying feature
        shapeRenderer.setColor(Color.WHITE);

        // draw center circle
        shapeRenderer.circle(location.x, location.y, BASE_SIZE_RADIUS * 0.15f);

        // draw n,s,w,e
        shapeRenderer.circle(location.x + (BASE_SIZE_RADIUS * 0.5f),
                location.y,
                BASE_SIZE_RADIUS * 0.15f);

        shapeRenderer.circle(location.x - (BASE_SIZE_RADIUS * 0.5f),
                location.y,
                BASE_SIZE_RADIUS * 0.15f);

        shapeRenderer.circle(location.x,
                location.y + (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);

        shapeRenderer.circle(location.x,
                location.y - (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);

        // draw ne, nw, se, sw
        shapeRenderer.circle(location.x + (BASE_SIZE_RADIUS * 0.5f),
                location.y - (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);

        shapeRenderer.circle(location.x - (BASE_SIZE_RADIUS * 0.5f),
                location.y - (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);

        shapeRenderer.circle(location.x + (BASE_SIZE_RADIUS * 0.5f),
                location.y + (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);

        shapeRenderer.circle(location.x - (BASE_SIZE_RADIUS * 0.5f),
                location.y + (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);
    }

    public void upgradeUniqueModifier() {
        preUpgrade();
        this.numShrapnelPerRocket++;
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
        return this.numShrapnelPerRocket;
    }

    public String getUniqueModifierName() {
        return UNIQUE_MODIFIER_NAME;
    }

    public int getBaseCost() {
        return BASE_COST;
    }
}

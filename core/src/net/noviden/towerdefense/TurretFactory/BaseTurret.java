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

import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.UnitFactory.Unit;
import net.noviden.towerdefense.UnitFactory.UnitManager;

import java.io.Serializable;
import java.util.UUID;

public abstract class BaseTurret {

    protected static final Color BASE_TURRET_COLOR = Color.GRAY;

    protected static final float BASE_SIZE_RADIUS = 10.0f;
    protected static final int BASE_UPGRADE_COST = 10;

    protected enum State {
        SLEEPING, ATTACKING
    };

    public enum Type implements Serializable {
        NORMAL, CHAINGUN, SHOTGUN, ROCKET, HOMING, BUFF
    }

    protected Point location;
    protected State state;
    public Type type;

    // values updated by increasing level AKA stats
    protected float range;
    protected int level; // represents overall strength
    protected float cooldownLength;
    protected float damage;
    protected float radius;

    protected float cooldownTimer;
    protected float _buffCooldownTimer;

    protected Unit target;

    protected String id;
    protected int worth;
    protected int upgradeCost;

    protected BaseTurret() {
        this.id = UUID.randomUUID().toString().substring(0, 5);
        this.upgradeCost = BASE_UPGRADE_COST;
    }

    public abstract void act(float deltaTime, UnitManager unitManager);

    public abstract void draw(ShapeRenderer shapeRenderer);

    public abstract void upgradeDamage();

    public boolean canUpgradeDamage() {
        return true;
    }

    public abstract void upgradeRange();

    public abstract void upgradeUniqueModifier();

    public abstract boolean canUpgradeUniqueModifier();

    protected void buffAttackSpeed(float pPercent, float pTime) {

        if (_buffCooldownTimer > 0.0f) {
            // don't stack buffs
            return;
        }

        this.cooldownLength *= pPercent;
        _buffCooldownTimer = pTime;
    }

    protected void preUpgrade() {
        this.level++;
        this.worth += this.upgradeCost;
        this.upgradeCost += BASE_UPGRADE_COST;
    }

    protected boolean enemyInRange(Unit unit) {
        float distanceBetween = (float) Math.sqrt(
                Math.pow(location.x - unit.location.x, 2) +
                        Math.pow(location.y - unit.location.y, 2));

        if (distanceBetween < range) {
            return true;
        }

        return false;
    }

    /*
     * return NULL if no closer enemy is found
     */
    protected Unit findEnemyInRange(UnitManager unitManager) {
        Unit closestUnit = null;
        float closestDistance;
        float distanceFromCurrentTarget;

        if (target != null) {
            distanceFromCurrentTarget = (float) Math.sqrt(
                    Math.pow(location.x - target.location.x, 2) +
                            Math.pow(location.y - target.location.y, 2));

            closestDistance = distanceFromCurrentTarget;
        } else {
            closestDistance = range;
        }

        for (Unit unit : unitManager.units) {
            float distanceBetween = (float) Math.sqrt(
                    Math.pow(location.x - unit.location.x, 2) +
                            Math.pow(location.y - unit.location.y, 2));

            if (distanceBetween < closestDistance) {
                closestUnit = unit;
                closestDistance = distanceBetween;
            }
        }

        if (target == null) {
            return closestUnit;
        } else if (closestUnit == null) {
            return null;
        }

        distanceFromCurrentTarget = (float) Math.sqrt(
                Math.pow(location.x - target.location.x, 2) +
                        Math.pow(location.y - target.location.y, 2));

        float distanceFromNewTarget = (float) Math.sqrt(
                Math.pow(location.x - closestUnit.location.x, 2) +
                        Math.pow(location.y - closestUnit.location.y, 2));

        if (distanceFromCurrentTarget > distanceFromNewTarget) {
            return closestUnit;
        } else {
            return null;
        }
    }

    public String getId() {
        return this.id;
    }

    public abstract String getUniqueModifierName();

    public float getDamage() {
        return this.damage;
    }

    public float getRange() {
        return this.range;
    }

    public abstract float getUniqueModifierValue();

    public float getRadius() {
        return this.radius;
    }

    public Point getLocation() {
        return this.location;
    }

    public int getWorth() {
        return this.worth;
    }

    public int getUpgradeCost() {
        return this.upgradeCost;
    }

    public boolean containsPoint(Point point) {
        float distanceBetween = (float) Math.sqrt(
                Math.pow(this.location.x - point.x, 2) + Math.pow(this.location.y - point.y, 2));

        if (distanceBetween < this.radius) {
            return true;
        }

        return false;
    }
}

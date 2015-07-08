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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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

    public enum Type implements Serializable {
        NORMAL, CHAINGUN, SHOTGUN, ROCKET, HOMING, BUFF;
    }

    protected Point location;
    public Type type;

    // values updated by increasing level AKA stats
    protected float range;
    protected int level; // represents overall strength
    protected float cooldownLength; // affected by buff turrets
    protected float baseCooldownLength; // never changed
    protected float damage;
    protected float radius;

    protected float cooldownTimer;
    protected float _buffCooldownTimer;

    protected String id;
    protected int worth;
    protected int upgradeCost;

    protected Sound _attackSoundEffect, _upgradeSoundEffect;

    protected BaseTurret() {
        this.id = UUID.randomUUID().toString().substring(0, 5);
        this.upgradeCost = BASE_UPGRADE_COST;

        _attackSoundEffect = Gdx.audio.newSound(Gdx.files.internal("sounds/laser1.mp3"));
        _upgradeSoundEffect = Gdx.audio.newSound(Gdx.files.internal("sounds/powerUp2.mp3"));
    }

    public void act(float deltaTime, UnitManager unitManager) {
        if (cooldownTimer >= 0.0f) {
            cooldownTimer -= deltaTime;
        }

        if (_buffCooldownTimer > 0.0f) {
            _buffCooldownTimer -= deltaTime;

            if (_buffCooldownTimer <= 0.0f) {
                cooldownLength = baseCooldownLength;
            }
        }

        if (cooldownTimer <= 0.0f) {
            Unit unit = findClosestEnemyInRange(unitManager);
            if (unit != null) {
                if (_attackSoundEffect != null) {
                    _attackSoundEffect.play(1.0f);
                }

                attack(unit);
                cooldownTimer = cooldownLength;
            }
        }
    }

    public abstract void attack(Unit target);

    public abstract void draw(ShapeRenderer shapeRenderer);

    public void drawOpaque(ShapeRenderer shapeRenderer) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(location.x, location.y, BASE_SIZE_RADIUS);
        shapeRenderer.circle(location.x, location.y, range);
    }

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

        System.out.println("New attack speed: " + (1.0f / cooldownLength));
    }

    protected boolean isAttackSpeedBuffed() {
        if (_buffCooldownTimer > 0.0f) {
            return true;
        }

        return false;
    }

    protected void preUpgrade() {
        this.level++;
        this.worth += this.upgradeCost;
        this.upgradeCost += BASE_UPGRADE_COST;

        if (_upgradeSoundEffect != null) {
            _upgradeSoundEffect.play(1.0f);
        }
    }

    /*
     * return null if no closer enemy is found
     */
    protected Unit findClosestEnemyInRange(UnitManager unitManager) {
        Unit closestUnit = null;
        float closestDistance = range;

        for (Unit unit : unitManager.units) {
            float distanceBetween = (float) Math.sqrt(
                    Math.pow(location.x - unit.location.x, 2) +
                            Math.pow(location.y - unit.location.y, 2));

            if (distanceBetween < closestDistance) {
                closestUnit = unit;
                closestDistance = distanceBetween;
            }
        }

        return closestUnit;
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

    public abstract int getBaseCost();

    public boolean containsPoint(Point point) {
        float distanceBetween = (float) Math.sqrt(
                Math.pow(this.location.x - point.x, 2) + Math.pow(this.location.y - point.y, 2));

        if (distanceBetween < this.radius) {
            return true;
        }

        return false;
    }
}

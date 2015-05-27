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

import net.noviden.towerdefense.MissileFactory.Missile;
import net.noviden.towerdefense.MissileFactory.MissileManager;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.UnitFactory.Unit;
import net.noviden.towerdefense.UnitFactory.UnitManager;

public class ShotgunTurret extends BaseTurret {

    public static final int BASE_COST = 50;
    public static final float BASE_RANGE = 200.0f;
    private static final float BASE_DAMAGE = 10.0f;
    private static final float BASE_COOLDOWN = 1.8f;

    private static final String UNIQUE_MODIFIER_NAME = "Pellets";

    private static final int BASE_PELLETS_PER_SHOT = 7;

    private int numPelletsPerShot;

    public ShotgunTurret(Point location) {
        this.location = location;
        this.level = 0;
        this.type = Type.CHAINGUN;
        this.cooldownTimer = 0.0f;
        this.state = State.SLEEPING;

        this.range = BASE_RANGE;
        this.damage = BASE_DAMAGE;
        this.cooldownLength = BASE_COOLDOWN;
        this.radius = BASE_SIZE_RADIUS;
        this.worth = BASE_COST;

        this.numPelletsPerShot = BASE_PELLETS_PER_SHOT;
    }

    public void act(float deltaTime,  UnitManager unitManager) {

        if (cooldownTimer >= 0.0f) {
            cooldownTimer -= deltaTime;
        }

        switch (this.state) {
            case SLEEPING:
                Unit unit = findEnemyInRange(unitManager);
                if (unit != null) {
                    target = unit;
                    this.state = State.ATTACKING;
                }

                break;
            case ATTACKING:
                if (target.isDead() || !enemyInRange(target)) {
                    Unit unit1 = findEnemyInRange(unitManager);
                    if (unit1 != null) {
                        target = unit1;
                    } else {
                        this.state = State.SLEEPING;
                    }
                }

                cooldownTimer -= deltaTime;
                if (cooldownTimer < 0.0f) {
                    // behavior: shoot randomly around the target, within certain radius
                    Point targetPoint = new Point(target.location.x, target.location.y);
                    for (int i = 0; i < this.numPelletsPerShot; i++) {
                        targetPoint.x = target.location.x - 20.0f + (float) Math.random() * 40.0f;
                        targetPoint.y = target.location.y - 20.0f + (float) Math.random() * 40.0f;

                        MissileManager.addMissile(new Missile(this.location,
                                targetPoint, this.damage));
                    }

                    cooldownTimer = cooldownLength;
                }

                break;
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        // draw base turret
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(location.x, location.y, BASE_SIZE_RADIUS);

        // draw identifying feature: spread of circles
        shapeRenderer.setColor(Color.WHITE);

        // draw center circle
        shapeRenderer.circle(location.x, location.y, BASE_SIZE_RADIUS * 0.15f);

        // draw right two circles
        shapeRenderer.circle(location.x + (BASE_SIZE_RADIUS * 0.5f),
                location.y + (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);
        shapeRenderer.circle(location.x + (BASE_SIZE_RADIUS * 0.5f),
                location.y - (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);

        // draw left two circles
        shapeRenderer.circle(location.x - (BASE_SIZE_RADIUS * 0.5f),
                location.y - (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);
        shapeRenderer.circle(location.x - (BASE_SIZE_RADIUS * 0.5f),
                location.y + (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);
    }

    public void upgradeUniqueModifier() {
        preUpgrade();
        this.numPelletsPerShot += 2;
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
        return this.numPelletsPerShot;
    }

    public String getUniqueModifierName() {
        return UNIQUE_MODIFIER_NAME;
    }
}

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

import net.noviden.towerdefense.MissileFactory.HomingMissile;
import net.noviden.towerdefense.MissileFactory.MissileManager;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.UnitFactory.Unit;
import net.noviden.towerdefense.UnitFactory.UnitManager;

public class HomingTurret extends BaseTurret {

    public static final int BASE_COST = 50;
    public static final float BASE_RANGE = 500.0f;
    private static final float BASE_DAMAGE = 100.0f;
    private static final float BASE_COOLDOWN = 5.0f;

    private static final String UNIQUE_MODIFIER_NAME = "Extra activeMissiles";

    private int extraMissilesPerShot;

    public HomingTurret(Point location) {
        this.location = location;
        this.level = 0;
        this.type = Type.HOMING;
        this.cooldownTimer = 0.0f;
        this.state = State.SLEEPING;

        this.range = BASE_RANGE;
        this.damage = BASE_DAMAGE;
        this.cooldownLength = BASE_COOLDOWN;
        this.radius = BASE_SIZE_RADIUS;
        this.worth = BASE_COST;
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
                    // always shoot at least one missile
                    MissileManager.addMissile(new HomingMissile(this.location,
                            target, unitManager, this.damage, this.range));

                    // shoot every extra missile
                    for (int i = 0; i < this.extraMissilesPerShot; i++) {
                        MissileManager.addMissile(new HomingMissile(this.location,
                                target, unitManager, this.damage, this.range));
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

        // draw identifying feature: s, c, n, nw, ne
        shapeRenderer.setColor(Color.WHITE);

        // draw s
        shapeRenderer.circle(location.x,
                location.y + (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);

        // draw nw
        shapeRenderer.circle(location.x - (BASE_SIZE_RADIUS * 0.5f),
                location.y - (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);

        // draw n
        shapeRenderer.circle(location.x,
                location.y - (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);

        // draw ne
        shapeRenderer.circle(location.x + (BASE_SIZE_RADIUS * 0.5f),
                location.y - (BASE_SIZE_RADIUS * 0.5f),
                BASE_SIZE_RADIUS * 0.15f);

        // draw nn
        shapeRenderer.circle(location.x,
                location.y - (BASE_SIZE_RADIUS * 0.8f),
                BASE_SIZE_RADIUS * 0.15f);
    }

    public void upgradeUniqueModifier() {
        preUpgrade();
        this.extraMissilesPerShot++;
    }

    public void upgradeRange() {
        preUpgrade();
        this.range += BASE_RANGE * 0.10f;
    }

    public void upgradeDamage() {
        preUpgrade();
        this.damage += BASE_DAMAGE * 0.10f;
    }

    public float getUniqueModifierValue() {
        return this.extraMissilesPerShot;
    }

    public String getUniqueModifierName() {
        return UNIQUE_MODIFIER_NAME;
    }
}

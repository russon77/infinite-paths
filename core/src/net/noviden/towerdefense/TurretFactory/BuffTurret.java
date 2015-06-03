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

import java.util.ArrayList;

public class BuffTurret extends BaseTurret {

    private static final float BASE_BUFF_DURATION = 3.0f;
    private static final float BASE_BUFF_PERCENTAGE = 0.5f;

    public static final float BASE_RANGE = 300.0f;
    public static final int BASE_COST = 50;

    private static final String UNIQUE_MODIFIER_NAME = "Attack Speed Buff";

    private TurretManager _turretManager;
    private ArrayList<BaseTurret> _buffedTurrets;
    private float _buffPercentage;

    public BuffTurret(Point location, TurretManager pTurretManager) {
        this.location = location;
        this.level = 0;
        this.type = Type.NORMAL;
        this.cooldownTimer = 0.0f;

        this.range = BASE_RANGE;
        this.radius = BASE_SIZE_RADIUS;
        this.worth = BASE_COST;

        this.damage = 0;
        this.cooldownLength = BASE_BUFF_DURATION;

        _turretManager = pTurretManager;
        _buffPercentage = BASE_BUFF_PERCENTAGE;
        _buffedTurrets = new ArrayList<BaseTurret>();
    }

    @Override
    public void act(float deltaTime, UnitManager unitManager) {
        // idea: every BASE_BUFF_DURATION seconds, upgrade all nearby turrets

        if (cooldownTimer > 0.0f) {
            cooldownTimer -= deltaTime;
        }

        if (cooldownTimer <= 0.0f) {

            _buffedTurrets.clear();

            for (BaseTurret turret : _turretManager.turrets) {
                if (withinRange(turret)) {
                    turret.buffAttackSpeed(_buffPercentage, BASE_BUFF_DURATION);
                    _buffedTurrets.add(turret);
                }
            }

            // reset cooldown
            cooldownTimer = BASE_BUFF_DURATION;
        }
    }

    public void attack(Unit target) {}

    public void draw(ShapeRenderer shapeRenderer) {
        // draw base turret
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BASE_TURRET_COLOR);
        shapeRenderer.circle(location.x, location.y, radius);

        // draw identifying feature: one white dot in center
        shapeRenderer.setColor(Color.WHITE);

        // draw center circles
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(location.x, location.y, BASE_SIZE_RADIUS * 0.25f);
        shapeRenderer.circle(location.x, location.y, BASE_SIZE_RADIUS * 0.75f);

        // draw a line to buffed turrets, displaying power bonus
        for (BaseTurret turret : _buffedTurrets) {
            shapeRenderer.line(location.x, location.y,
                    turret.location.x, turret.location.y);
        }
    }

    public void upgradeDamage() {
        // no damage associated with this turret
    }

    @Override
    public boolean canUpgradeDamage() {
        return false;
    }

    public void upgradeRange() {
        preUpgrade();
        this.range += BASE_RANGE * 0.10f;
    }

    public void upgradeUniqueModifier() {
        preUpgrade();

        _buffPercentage += 0.10f;
    }

    public boolean canUpgradeUniqueModifier() {
        return true;
    }

    public String getUniqueModifierName() {
        return UNIQUE_MODIFIER_NAME;
    }

    public float getUniqueModifierValue() {
        return _buffPercentage;
    }

    @Override
    protected void buffAttackSpeed(float pPercent, float pTime) {
        // attack speed does nothing on this kind of turret
    }

    private boolean withinRange(BaseTurret pTurret) {
        float distanceBetween = (float) Math.sqrt(
                Math.pow(this.location.x - pTurret.location.x, 2) +
                        Math.pow(this.location.y - pTurret.location.y, 2));

        float sumRadii = pTurret.getRadius() + this.range;

        return (sumRadii >= distanceBetween);
    }
}

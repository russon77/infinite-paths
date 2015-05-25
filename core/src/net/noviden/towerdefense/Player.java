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

import net.noviden.towerdefense.TurretFactory.BaseTurret;
import net.noviden.towerdefense.TurretFactory.BasicTurret;
import net.noviden.towerdefense.TurretFactory.ChaingunTurret;
import net.noviden.towerdefense.TurretFactory.HomingTurret;
import net.noviden.towerdefense.TurretFactory.RocketTurret;
import net.noviden.towerdefense.TurretFactory.ShotgunTurret;

public class Player {

    public static final float BASE_TURRET_PLACE_COOLDOWN = 0.25f;
    private static final int BASE_RESOURCES = 50;
    private static final int BASE_HEALTH = 100;

    public enum State {
        TURRET_PLACE, TURRET_UPGRADE, VIEW
    }

    private State state;
    private BaseTurret.Type selectedTurretType;
    private float placeTurretCooldown;

    private int resources;
    private int score;
    private float health;
    private int numTurretsCreated;
    private int numUnitsKilled;

    private BaseTurret turretSelectedForUpgrade;

    public Player() {
        this.state = State.TURRET_PLACE;
        this.selectedTurretType = BaseTurret.Type.NORMAL;
        this.placeTurretCooldown = BASE_TURRET_PLACE_COOLDOWN;
        this.resources = BASE_RESOURCES;
        this.health = BASE_HEALTH;
        this.score = this.numTurretsCreated = this.numUnitsKilled = 0;
    }

    public void act(float deltaTime) {
        if (placeTurretCooldown >= 0.0f) {
            placeTurretCooldown -= deltaTime;
        }
    }

    public void purchaseTurret() {
        resetCooldown();
        this.resources -= getCostOfSelectedTurret();
        this.numTurretsCreated++;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public BaseTurret.Type getSelectedTurretType() {
        return this.selectedTurretType;
    }

    public boolean onCooldownForPlacingTurrets() {
        if (placeTurretCooldown > 0.0f) {
            return true;
        }

        return false;
    }

    private int getCostOfSelectedTurret() {
        switch (selectedTurretType) {
            case NORMAL:
                return BasicTurret.BASE_COST;
            case ROCKET:
                return RocketTurret.BASE_COST;
            case CHAINGUN:
                return ChaingunTurret.BASE_COST;
            case SHOTGUN:
                return ShotgunTurret.BASE_COST;
            case HOMING:
                return HomingTurret.BASE_COST;
        }

        return 0;
    }

    public boolean canAffordSelectedTurret() {
        if (getCostOfSelectedTurret() <= this.resources) {
            return true;
        }

        return false;
    }

    public int getResources() {
        return this.resources;
    }

    private void resetCooldown() {
        this.placeTurretCooldown = BASE_TURRET_PLACE_COOLDOWN;
    }

    public void setSelectedTurretType(BaseTurret.Type type) {
        this.selectedTurretType = type;
    }

    public void addResources(int amount) {
        this.resources += amount;
    }

    public void decreaseResources(int amount) {
        if ((this.resources - amount) < 0) {
            this.resources = 0;
        } else {
            this.resources -= amount;
        }
    }

    public void decreaseHealth(float amount) {
        if ((this.health - amount) < 0) {
            this.health = 0;
        } else {
            this.health -= amount;
        }
    }

    public int getHealth() {
        return (int) this.health;
    }

    public int getScore() {
        return this.score;
    }

    public int getNumTurretsCreated() {
        return this.numTurretsCreated;
    }

    public int getNumUnitsKilled() {
        return this.numUnitsKilled;
    }

    public void increaseScore(int amount) {
        this.score += amount;
    }

    public void increaseNumUnitsKilled() {
        this.numUnitsKilled++;
    }

    public void setTurretForUpgrade(BaseTurret turret) {
        this.turretSelectedForUpgrade = turret;
    }

    public BaseTurret getTurretSelectedForUpgrade() {
        return this.turretSelectedForUpgrade;
    }
}

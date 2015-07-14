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


package net.noviden.towerdefense.UnitFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.noviden.towerdefense.MissileFactory.Missile;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Player;
import net.noviden.towerdefense.Point;
import net.noviden.towerdefense.TowerDefense;

public class Unit {

    protected static final Color BASE_UNIT_COLOR = Color.BLUE;
    protected static final Color BASE_UNIT_DAMAGED_COLOR = Color.RED;

    private static final float BASE_RADIUS = 10.0f;
    private static final int BASE_WORTH = 20;

    protected float health, maxHealth;
    private float damage;
    public Point location;
    public float radius;

    protected Path path;
    protected int currentDestinationIndex;
    private float xVelocity, yVelocity;
    private float percentSlowed, timeSlowed;
    protected float speed;

    private int worth;

    protected float rotation;

    public Unit() {}

    public Unit(float health, float damage, float speed, Path path) {
        this.health = this.maxHealth = health;
        this.damage = damage;
        this.path = path;

        // initial location is equal to 1st position in path set, and destination the 2nd in set
        this.location = path.set.get(0).clone();
        this.currentDestinationIndex = 1;
        this.radius = BASE_RADIUS;
        this.worth = BASE_WORTH;
        this.speed = speed;

        this.rotation = 0.0f;

        // set initial xVel and yVel based on destination
        Point destination = path.set.get(currentDestinationIndex);

        float distanceBetween = (float) Math.sqrt(
                Math.pow(location.x - destination.x, 2) + Math.pow(location.y - destination.y, 2));

        this.xVelocity = (destination.x - location.x) / distanceBetween;
        this.yVelocity = (destination.y - location.y) / distanceBetween;
    }

    public Unit(float health, float damage, float speed, Path path, Point initialLocation,
                int currentDestinationIndex) {
        this(health, damage, speed, path);

        this.location = initialLocation.clone();
        this.currentDestinationIndex = currentDestinationIndex;

        // set initial xVel and yVel based on destination
        if (currentDestinationIndex >= path.set.size()) {
            // set to last destination in path
            this.currentDestinationIndex = path.set.size() - 1;
        }

        Point destination = path.set.get(this.currentDestinationIndex);

        float distanceBetween = (float) Math.sqrt(
                Math.pow(location.x - destination.x, 2) + Math.pow(location.y - destination.y, 2));

        this.xVelocity = (destination.x - location.x) / distanceBetween;
        this.yVelocity = (destination.y - location.y) / distanceBetween;
    }

    public void act(float deltaTime, Player player) {
        // move along line from by currentLocation -> currentDestination until arrive at
        // destination, then set new destination

        // set the rotation factor for our little guys
        rotation += deltaTime * 108.0f;
        if (rotation > 360.0f) {
            rotation = 0.0f;
        }

        // count down slow timer
        if (timeSlowed >= 0.0f) {
            timeSlowed -= deltaTime;

            if (timeSlowed <= 0.0f) {
                percentSlowed = 0.0f;
            }
        }

        // check for boundaries
        if (location.x < 0 || location.x > TowerDefense.SCREEN_WIDTH ||
                location.y < 0 || location.y > TowerDefense.SCREEN_HEIGHT) {

            // set unit to be removed
            this.health = (-1.0f);

            return;
        }

        Point destination = path.set.get(currentDestinationIndex);

        // base case: time to find a new destination
        if (Math.abs(location.x - destination.x) < 2.5f &&
                Math.abs(location.y - destination.y) < 2.5f) {
            // close enough! on to the next destination
            currentDestinationIndex++;

            // check for end of the line
            if (currentDestinationIndex >= path.set.size()) {
                this.health = -1.0f;
            } else {
                // recalculate xVel and yVel
                destination = path.set.get(currentDestinationIndex);

                float distanceBetween = (float) Math.sqrt(
                        Math.pow(location.x - destination.x, 2) + Math.pow(location.y - destination.y, 2));

                this.xVelocity = (destination.x - location.x) / distanceBetween;
                this.yVelocity = (destination.y - location.y) / distanceBetween;
            }
        } else {
            // recalculate velocities to current destination
            // TODO this is only necessary if map transformations are turned ON

            // recalculate xVel and yVel
            destination = path.set.get(currentDestinationIndex);

            float distanceBetween = (float) Math.sqrt(
                    Math.pow(location.x - destination.x, 2) + Math.pow(location.y - destination.y, 2));

            this.xVelocity = (destination.x - location.x) / distanceBetween;
            this.yVelocity = (destination.y - location.y) / distanceBetween;
        }

        location.x += xVelocity * deltaTime * speed * (1.0f - percentSlowed);
        location.y += yVelocity * deltaTime * speed * (1.0f - percentSlowed);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        // draw each unit's health as a percent of its circle
        float percentHealthMissing = (1.0f - this.health / this.maxHealth);
        float degrees = percentHealthMissing * 360.0f;

        shapeRenderer.setColor(BASE_UNIT_COLOR);
        shapeRenderer.circle(location.x, location.y, BASE_RADIUS);

        if (percentHealthMissing > 0.0f) {
            shapeRenderer.setColor(BASE_UNIT_DAMAGED_COLOR);
            shapeRenderer.arc(location.x, location.y, BASE_RADIUS, rotation, degrees);
        }
    }

    public boolean collidesWith(Missile missile) {
        float distanceBetween = (float) Math.sqrt(
                Math.pow(this.location.x - missile.location.x, 2) +
                        Math.pow(this.location.y - missile.location.y, 2));

        if (distanceBetween < (missile.radius + this.radius)) {
            return true;
        }

        return false;
    }

    public Unit getNextUnitToSpawn() {
        return null;
    }

    public float getDamage() {
        return this.damage;
    }

    public void takeDamage(float amount) {
        this.health -= amount;
    }

    public void slowDown(float timeSlowed, float percentSlowed) {
        this.timeSlowed = timeSlowed;

        // always slow unit by higher factor, never decrease
        if (this.percentSlowed < percentSlowed) {
            this.percentSlowed = percentSlowed;
        }
    }

    public boolean isDead() {
        return (health < 0.0f);
    }

    public int getWorth() {
        return this.worth;
    }

    public float getSpeed() {
        return this.speed;
    }

    public boolean reachedEndOfPath() {
        return (currentDestinationIndex >= path.set.size());
    }
}

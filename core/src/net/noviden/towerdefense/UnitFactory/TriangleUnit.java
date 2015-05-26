package net.noviden.towerdefense.UnitFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;

public class TriangleUnit extends Unit {

    private static final float centerToVertex = 13.0f;

    public TriangleUnit(float health, float damage, float speed, Path path) {
        super(health, damage, speed, path);
    }

    public TriangleUnit(float health, float damage, float speed, Path path, Point initialLocation,
                        int currentDestinationIndex) {
        super(health, damage, speed, path, initialLocation, currentDestinationIndex);
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        // draw each unit's health as a percent of its shape
        float percentHealthMissing = 1.0f - (this.health / this.maxHealth);

        // draw a square centered at the current location
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.PURPLE);

        shapeRenderer.identity();

        shapeRenderer.translate(location.x, location.y, 0.f);
        shapeRenderer.rotate(0f, 0f, 1f, rotation);

        shapeRenderer.triangle(0, centerToVertex,
                centerToVertex / 2, (-1.0f) * (float) Math.sqrt(3.0f) * centerToVertex / 2,
                -centerToVertex / 2, (-1.0f) *(float)  Math.sqrt(3.0f) * centerToVertex / 2);


        shapeRenderer.identity();
    }
}

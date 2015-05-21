package net.noviden.towerdefense;

import java.io.Serializable;

public class Point implements Serializable {
    public float x, y;

    public Point(float x, float y) {
        this.x = x; this.y = y;
    }

    public Point clone() {
        return new Point(this.x, this.y);
    }

    public boolean equals(Object o) {
        Point target = (Point) o;

        if (Math.abs(target.x - this.x) < 0.01 &&
                Math.abs(target.y - this.y) < 0.01) {
            return true;
        }

        return false;
    }
}

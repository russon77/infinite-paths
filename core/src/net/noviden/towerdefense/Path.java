package net.noviden.towerdefense;

import java.io.Serializable;
import java.util.ArrayList;

public class Path implements Serializable {
    // represents width/thickness of a path
    public float width;

    // ordered set describing path from ENTRY to EXIT for units to walk through
    public ArrayList<Point> set;

    public Path(ArrayList<Point> set, float width) {
        this.set = set; this.width = width;
    }
}
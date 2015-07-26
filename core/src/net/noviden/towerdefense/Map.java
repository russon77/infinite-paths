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


package net.noviden.towerdefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.io.Serializable;
import java.util.ArrayList;

public class Map implements Serializable {
    public enum DefaultMaps {
        X, Z, STAR
    }

    public static class Dimensions implements Serializable {
        public float width,height;

        public Dimensions(float width, float height) {
            this.width = width; this.height = height;
        }
    }

    private Path[] paths;
    private String name;
    private MapSettings _settings;

    public Map(Path[] pPaths, String pName, MapSettings pMapSettings) {
        this.paths = pPaths; this.name = pName; this._settings = pMapSettings;
    }

    public Map(Dimensions dimensions, Path[] paths, String name) {

        this.paths = Map.computeGenericPaths(dimensions, paths);

//        this.paths = paths;
        this.name = name;

        _settings = new MapSettings();
    }

    public Map(Dimensions dimensions, Path[] paths, String name, MapSettings settings) {
        this(dimensions, paths, name);

        _settings = settings;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GRAY);

        float w = Gdx.graphics.getWidth(),
                h = Gdx.graphics.getHeight();

        for (Path path : paths) {

//            for (int i = 0; i < (path.set.size() - 1); i++) {
//                Point a = path.set.get(i),
//                        b = path.set.get(i + 1);
//                shapeRenderer.line(a.x, a.y, b.x, b.y);
//            }

            for (int i = 0; i < (path.set.size() - 1); i++) {
                Point a = path.set.get(i),
                        b = path.set.get(i + 1);

                shapeRenderer.line(a.x * w, a.y * h,
                        b.x * w, b.y * h);
            }

            /*
            // draw a triangle at the origin point
            // TODO
            Point originPoint = path.set.get(0);
            shapeRenderer.circle(originPoint.x, originPoint.y, 5.0f);

            // draw a rectangle at the last point
            // TODO
            Point endPoint = path.set.get(path.set.size() - 1);
            shapeRenderer.circle(endPoint.x, endPoint.y, 10.0f);
            */
        }
    }

    public Map clone() {
        Path[] clonedPaths = new Path[this.paths.length];
        for (int i = 0; i < clonedPaths.length; i++) {
            clonedPaths[i] = this.paths[i].clone();
        }
        return new Map(clonedPaths, new String(name), _settings.clone());
    }

    public String getName() {
        return this.name;
    }

    public void setPaths(Path[] paths) {
        // TODO fix this
//        this.paths = Map.computeGenericPaths(paths);
        this.paths = paths;
    }

    public Path getPath(int pIndex) {
        if (pIndex >= this.paths.length) {
            return null;
        }

        Path[] relativePaths = computeRelativePaths(this.paths);

        return relativePaths[pIndex];
    }

    // aka getGenericPath
    public Path getRealPath(int pIndex) {
        if (pIndex >= this.paths.length) {
            return null;
        }

        return this.paths[pIndex];
    }

    public int getNumPaths() {
        return this.paths.length;
    }

    public MapSettings getSettings() {
        return _settings;
    }

    public void setSettings(MapSettings pSettings) {
        _settings = pSettings;
    }

    public static Map createFromFile(String filename) {
        return null;
    }

    public static Map createFromDefault(DefaultMaps id) {
        Map.Dimensions dimensions =
                new Map.Dimensions(Gdx.graphics.getWidth(),
                        Gdx.graphics.getHeight());
        ArrayList<Point> pathSet = new ArrayList<Point>();
        Path[] paths;

        float height = dimensions.height,
                width = dimensions.width;

        switch (id) {
            case X:
                // create the path
                pathSet.add(new Point(0,0));
                pathSet.add(new Point(width, height));
                pathSet.add(new Point(width, 0));
                pathSet.add(new Point(0, height));

                paths = new Path[]
                        {
                                new Path(pathSet, 5)
                        };

                return new Map(dimensions, paths, "X");
            case Z:
                pathSet.add(new Point(0,0));
                pathSet.add(new Point(width, 0));
                pathSet.add(new Point(width, height / 2));
                pathSet.add(new Point(width / 2, height / 3));
                pathSet.add(new Point(0, height));
                pathSet.add(new Point(width / 2, height * 2 / 3));
                pathSet.add(new Point(width, height));

                paths = new Path[]
                        {
                                new Path(pathSet, 5)
                        };

                return new Map(dimensions, paths, "Z");
            case STAR:
                pathSet.add(new Point(0, 0));
                pathSet.add(new Point(width / 2, height));
                pathSet.add(new Point(width, 0));
                pathSet.add(new Point(0, height * 2 / 3));
                pathSet.add(new Point(width, height * 2 / 3));
                pathSet.add(new Point(0, 0));

                paths = new Path[]
                        {
                                new Path(pathSet, 5)
                        };

                return new Map(dimensions, paths, "Star");
        }

        return null;
    }

    private static Path[] computeGenericPaths(Dimensions pDimensions, Path[] pPaths) {
        Path[] genericPaths = new Path[pPaths.length];

        for (int i = 0; i < genericPaths.length; i++) {
            ArrayList<Point> genericPointSet = new ArrayList<Point>();
            for (Point point : pPaths[i].set) {
                // TODO fixme probably broken lols
                Point genericPoint = new Point(
                        point.x / pDimensions.width,
                        point.y / pDimensions.height);

                genericPointSet.add(genericPoint);
            }

            genericPaths[i] = new Path(genericPointSet, 5.0f);
        }

        return genericPaths;
    }

    private static Path[] computeRelativePaths(Path[] pPaths) {
        Path[] relativePaths = new Path[pPaths.length];

        for (int i = 0; i < relativePaths.length; i++) {
            ArrayList<Point> relativePointSet = new ArrayList<Point>();
            for (Point point : pPaths[i].set) {
                // TODO fixme probably broken lols
                Point genericPoint = new Point(
                        point.x * Gdx.graphics.getWidth(),
                        point.y * Gdx.graphics.getHeight());

                relativePointSet.add(genericPoint);
            }

            relativePaths[i] = new Path(relativePointSet, 5.0f);
        }

        return relativePaths;
    }

    public Path[] getPaths() {
        return computeRelativePaths(this.paths);
    }

    public Path[] getGenericPaths() {
        return this.paths;
    }
}

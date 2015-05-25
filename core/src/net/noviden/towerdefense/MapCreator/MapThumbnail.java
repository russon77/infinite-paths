package net.noviden.towerdefense.MapCreator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;

public class MapThumbnail {

    public static void drawThumbnail(Map map, float scale, ShapeRenderer shapeRenderer) {

        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);

        for (Path path : map.paths) {

            for (int j = 0; j < (path.set.size() - 1); j++) {
                Point a = path.set.get(j),
                        b = path.set.get(j + 1);

                shapeRenderer.line(scale * a.x, scale * a.y,
                        scale * b.x, scale * b.y);
            }
        }
    }
}

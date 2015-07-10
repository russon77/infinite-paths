package net.noviden.towerdefense.MapTransformation;

import net.noviden.towerdefense.Map;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;

public class RandomTransformation extends Transformation {

    public void transform(Map pMap, float pDelta) {
        float tmpX, tmpY;

        for (Path path : pMap.getPaths()) {
            for (Point point : path.set) {
                tmpX = point.x + ((-1.0f) + (2.0f * (float) Math.random())) * pDelta;

                if (tmpX >= 0.0f && tmpX <= pMap.dimensions.width) {
                    point.x = tmpX;
                }

                tmpY = point.y + ((-1.0f) + (2.0f * (float) Math.random())) * pDelta;
                if (tmpY >= 0.0f && tmpY <= pMap.dimensions.height) {
                    point.y = tmpY;
                }
            }
        }
    }
}

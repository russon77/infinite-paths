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

package net.noviden.towerdefense.MapCreator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import net.noviden.towerdefense.Maps.Map;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Point;

public class MapThumbnail {

    /*
     * pixmap -> texture -> sprite -> spriteDrawable
     */
    public static SpriteDrawable createThumbnail(Map map, int dims) {

        Pixmap pixmap = new Pixmap(dims, dims, Pixmap.Format.RGB565);

        // draw border around pixmap
        pixmap.setColor(Color.PURPLE);
        pixmap.drawRectangle(0,0,pixmap.getWidth(), pixmap.getHeight());

        // draw paths
        pixmap.setColor(Color.RED);

        for (Path path : map.getGenericPaths()) {
            for (int j = 0; j < path.set.size() - 1; j++) {
                Point a = path.set.get(j),
                        b = path.set.get(j + 1);

                int ax = (int) (a.x * dims), ay = (int) (a.y * dims),
                    bx = (int) (b.x * dims), by = (int) (b.y * dims);

                pixmap.drawLine(ax, ay, bx, by);
            }
        }

        Texture texture = new Texture(pixmap);

        Sprite sprite = new Sprite(texture);

        SpriteDrawable spriteDrawable = new SpriteDrawable(sprite);

        return spriteDrawable;
    }
}

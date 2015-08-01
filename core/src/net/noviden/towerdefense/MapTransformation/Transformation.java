package net.noviden.towerdefense.MapTransformation;

import net.noviden.towerdefense.Maps.Map;

public interface Transformation {
     enum Events {
        UNIT_DEATH,
    }

    void transform(Map pMap, float delta);
}

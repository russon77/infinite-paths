package net.noviden.towerdefense.MapTransformation;

import net.noviden.towerdefense.Map;

public interface Transformation {
     enum Events {
        UNIT_DEATH,
    }

    void transform(Map pMap, float delta);
}

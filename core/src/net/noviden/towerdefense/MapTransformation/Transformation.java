package net.noviden.towerdefense.MapTransformation;

import net.noviden.towerdefense.Map;

public abstract class Transformation {
    public enum Events {
        UNIT_DEATH,
    }

    public abstract void transform(Map pMap, float delta);
}

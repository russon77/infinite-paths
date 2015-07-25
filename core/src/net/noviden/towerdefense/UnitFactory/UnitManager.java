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


package net.noviden.towerdefense.UnitFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.noviden.towerdefense.GameSettings;
import net.noviden.towerdefense.MapSettings;
import net.noviden.towerdefense.Path;
import net.noviden.towerdefense.Player;

import java.util.ArrayList;

public class UnitManager {

    final MapSettings _settings;

    private static final int[][] DEFAULT_SPAWN_PATTERN =
            {
                    {6}
            }; /*
            {
                    {2,2,2,2,2,2,2,2},
                    {2,3,2,3,2,3,2,3},
                    {3,3,3,3,3,3,3,3},
                    {3,4,3,4,3,4,3,4},
                    {4,4,4,4,4,4,4,4},
                    {4,5,4,5,4,5,4,5},
                    {5,5,5,5,5,5,5,5},
                    {5,6,5,6,5,6,5,6},
                    {6,6,6,6,6,6,6,6}
            };*/

    private int spawnIndex;
    private int waveIndex;
    private int[][] _spawnPattern;

    private static final float PRE_WAVE_COOLDOWN_TIME = 3.0f;
    private static final float BASE_COOLDOWN = 2.0f;

    private Path path;

    public ArrayList<Unit> units;

    // represents wave / difficulty
    private int level;

    private float cooldownTimer;
    private float gameTime;

    // TODO each type of unit has its own sound effects?
    private Sound _unitDeathSoundEffect;

    public UnitManager(Path path, final MapSettings settings) {
        this.cooldownTimer = PRE_WAVE_COOLDOWN_TIME;
        this.units = new ArrayList<Unit>();
        this.path = path;
        this.level = 0;
        this.gameTime = 0.0f;
        this.spawnIndex = this.waveIndex = 0;

        _settings = settings;

        _spawnPattern = settings.getWaves();
        if (_spawnPattern == null) {
            _spawnPattern = DEFAULT_SPAWN_PATTERN;
        }

        _unitDeathSoundEffect = Gdx.audio.newSound(Gdx.files.internal("sounds/zapTwoTone.mp3"));
    }

    public void act(float deltaTime, Player player) {
        // increase gametime
        gameTime += deltaTime;

        // decrease cooldownTimer
        cooldownTimer -= deltaTime;
        if (cooldownTimer < 0.0f) {
            // spawn a new unit and go on cooldown
            spawnUnit();
            cooldownTimer = _settings.getValue(MapSettings.UNIT_SPAWN_RATE_KEY);
        }

        // process all units, calling `act` on each
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);

            unit.act(deltaTime);

            if (unit.isDead()) {
                // update player fields based on cause of death and remove unit

                if (player != null) {
                    if (unit.reachedEndOfPath()) {
                        player.decreaseHealth(unit.getDamage());
                    } else {
                        player.addResources(unit.getWorth());
                        player.increaseScore(unit.getWorth());
                        player.increaseNumUnitsKilled();
                    }
                }

                // break down the unit into its follow up unit
                Unit unitToSpawn = unit.getNextUnitToSpawn();
                if (unitToSpawn != null) {
                    units.add(unitToSpawn);
                }

                // play sound and delete unit
                if (_unitDeathSoundEffect != null) {
                    _unitDeathSoundEffect.play(GameSettings.getSoundVolume());
                }

                units.remove(i);
            }
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        for (Unit unit : units) {
            unit.draw(shapeRenderer);
        }
    }

    private void spawnUnit() {
        // every 15 seconds units double in health/damage
        float unitHealth = _settings.getValue(MapSettings.UNIT_INITIAL_HEALTH_KEY) *
                    (1.0f + (gameTime / 15.0f)),
                unitDamage = _settings.getValue(MapSettings.UNIT_INITIAL_DAMAGE_KEY) *
                        (1.0f + (gameTime / 15.0f)),
                    unitSpeed = _settings.getValue(MapSettings.UNIT_INITIAL_SPEED_KEY) *
                            (1.0f + (gameTime / 120.0f));

        Unit unitToSpawn = null;

        if (spawnIndex >= _spawnPattern[waveIndex].length) {
            // advance to next wave
            waveIndex++;
            spawnIndex = 0;
        }

        if (waveIndex >= _spawnPattern.length) {
            // continuously spawn the last wave
            waveIndex--;
        }

        switch (_spawnPattern[waveIndex][spawnIndex]) {
            case 2: unitToSpawn = new Unit(unitHealth, unitDamage, unitSpeed, path);
                break;
            case 3: unitToSpawn = new TriangleUnit(unitHealth, unitDamage, unitSpeed, path);
                break;
            case 4: unitToSpawn = new SquareUnit(unitHealth, unitDamage, unitSpeed, path);
                break;
            case 5: unitToSpawn = new PentagonUnit(unitHealth, unitDamage, unitSpeed, path);
                break;
            case 6: unitToSpawn = new HexagonUnit(unitHealth, unitDamage, unitSpeed, path);
                break;
        }

        if (unitToSpawn != null) {
            units.add(unitToSpawn);
            spawnIndex++;
        }
    }
}

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

import com.badlogic.gdx.files.FileHandle;

import net.noviden.towerdefense.TurretFactory.BaseTurret;

import java.io.Writer;

public class MapWriter {

    public static void writeMapToFile(Map pMap, FileHandle pFileHandle) {
        try {
            Writer writer = pFileHandle.writer(false);

            writeSettingsToWriter(pMap.getSettings(), writer);

            for (Path path : pMap.getGenericPaths()) {
                writePathToWriter(path, writer);
            }

            writer.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    private static void writePathToWriter(Path pPath, Writer pWriter) {
        try {
            pWriter.write("STARTPATH\n");

            for (Point point : pPath.set) {
                String s = "" + point.x + "," + point.y;
                pWriter.write(s + "\n");
            }

            pWriter.write("ENDPATH\n\n");

        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Error writing map paths to file.");
        }

        return;
    }

    private static void writeSettingsToWriter(MapSettings pMapSettings, Writer pWriter) {
        String[] keys =
                {
                        MapSettings.PLAYER_INITIAL_HEALTH_KEY,
                        MapSettings.PLAYER_INITIAL_RESOURCES_KEY,

                        MapSettings.UNIT_INITIAL_SPEED_KEY,
                        MapSettings.UNIT_INITIAL_HEALTH_KEY,
                        MapSettings.UNIT_INITIAL_DAMAGE_KEY,
                        MapSettings.UNIT_SPAWN_RATE_KEY
                };

        try {
            pWriter.write("STARTSETTINGS\n\n");

            pWriter.write("# Initial Values\n");
            for (String s : keys) {
                writeSetting(s, "" + pMapSettings.getValue(s), pWriter);
            }

            pWriter.write("\n");

            pWriter.write("# Disabled Turret Types\n");
            for (BaseTurret.Type type : BaseTurret.Type.values()) {
                pWriter.write(
                        type.toString() + "=" + pMapSettings.isTurretTypeDisabled(type) + "\n");
            }

            pWriter.write("\n");

//            pWriter.write("# Waves\n");

            // TODO waves

            pWriter.write("ENDSETTINGS\n\n");

        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Error writing map settings to file.");
        }

        return;
    }

    private static void writeSetting(String pKey, String pVal, Writer pWriter) {
        try {

            pWriter.write(pKey + "="  + pVal + "\n");

        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Error writing setting " + pKey + " to file.");
        }

        return;
    }
}

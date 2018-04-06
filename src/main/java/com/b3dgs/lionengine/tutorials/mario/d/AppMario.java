/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.adlmidi.AdlMidiFormat;
import com.b3dgs.lionengine.audio.wav.WavFormat;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.graphic.engine.Loader;

/**
 * Program starts here.
 */
public class AppMario
{
    /** Application name. */
    private static final String NAME = "Mario";
    /** Application version. */
    private static final Version VERSION = Version.create(1, 0, 0);

    /**
     * Main function.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        EngineAwt.start(NAME, VERSION, AppMario.class);
        AudioFactory.addFormat(new WavFormat());
        AudioFactory.addFormat(new AdlMidiFormat());
        Loader.start(Config.windowed(Scene.NATIVE.get2x()), Scene.class);
    }
}

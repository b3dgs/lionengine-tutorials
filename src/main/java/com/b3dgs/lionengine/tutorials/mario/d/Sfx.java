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

import java.util.Locale;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.core.Medias;

/**
 * Handle the SFX.
 */
enum Sfx
{
    /** Jump. */
    JUMP,
    /** Die. */
    CRUSH;

    /** Audio file extension. */
    private static final String AUDIO_FILE_EXTENSION = ".wav";
    /** Sound volume. */
    private static final int VOLUME = 15;

    /** Sounds list composing the effect. */
    private final Audio sound;

    /**
     * Constructor.
     */
    Sfx()
    {
        final Media media = Medias.create("sfx", name().toLowerCase(Locale.ENGLISH) + AUDIO_FILE_EXTENSION);
        sound = AudioFactory.loadAudio(media);
        sound.setVolume(VOLUME);
    }

    /**
     * Play the sound effect.
     */
    public void play()
    {
        sound.play();
    }
}

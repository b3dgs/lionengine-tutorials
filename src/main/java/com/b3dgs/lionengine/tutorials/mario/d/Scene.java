/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.tutorials.mario.d;

import java.io.IOException;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.adlmidi.AdlMidi;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.KeyboardAwt;
import com.b3dgs.lionengine.game.feature.SequenceGame;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Game loop designed to handle our world.
 */
class Scene extends SequenceGame
{
    /** Native resolution. */
    public static final Resolution NATIVE = new Resolution(320, 240, 60);
    private static final int VOLUME = 30;
    private static final Media LEVEL = Medias.create(World.FOLDER_MAP, "level.lvl");

    /**
     * Import and save the level.
     */
    private static void importAndSave()
    {
        final Services services = new Services();
        final MapTile map = services.create(MapTileGame.class);
        map.create(Medias.create(World.FOLDER_MAP, "level.png"));

        final MapTilePersister mapPersister = map.addFeatureAndGet(new MapTilePersisterModel(services));
        try (FileWriting output = new FileWriting(LEVEL))
        {
            mapPersister.save(output);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception, "Error on saving map !");
        }
    }

    private final AdlMidi music = AudioFactory.loadAudio(Medias.create("music", "music.midi"), AdlMidi.class);

    /**
     * Create world.
     * 
     * @param context The context reference.
     */
    Scene(Context context)
    {
        super(context, NATIVE, World::new);

        getInputDevice(Keyboard.class).addActionPressed(KeyboardAwt.ESCAPE, this::end);
    }

    @Override
    public void load()
    {
        if (!LEVEL.exists())
        {
            importAndSave();
        }
        world.loadFromFile(LEVEL);
        music.setVolume(VOLUME);
        music.play();
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        music.stop();
        Engine.terminate();
    }
}

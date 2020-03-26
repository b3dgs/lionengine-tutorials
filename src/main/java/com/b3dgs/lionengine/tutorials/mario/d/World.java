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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollisionModel;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * World implementation.
 */
class World extends WorldGame
{
    static final String FOLDER_MAP = "map";
    static final String FOLDER_ENTITY = "entity";
    private static final ColorRgba BACKGROUND_COLOR = new ColorRgba(107, 136, 255);
    private static final Media MARIO = Medias.create(FOLDER_ENTITY, "Mario.xml");
    private static final Media GOOMBA = Medias.create(FOLDER_ENTITY, "Goomba.xml");

    private final MapTile map = services.create(MapTileGame.class);
    private final MapTilePersister mapPersister = map.addFeatureAndGet(new MapTilePersisterModel(services));
    private final MapTileGroup mapGroup = map.addFeatureAndGet(new MapTileGroupModel());
    private final MapTileCollision mapCollision = map.addFeatureAndGet(new MapTileCollisionModel(services));

    /**
     * Create world.
     * 
     * @param services The services reference.
     */
    World(Services services)
    {
        super(services);

        services.add(getInputDevice(InputDeviceDirectional.class));

        map.addFeature(new MapTileViewerModel(services));
        handler.addComponent(new ComponentCollision());
        camera.setIntervals(16, 0);
    }

    @Override
    public void render(Graphic g)
    {
        fill(g, BACKGROUND_COLOR);
        super.render(g);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        mapPersister.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        handler.add(map);

        mapPersister.load(file);
        mapGroup.loadGroups(Medias.create(FOLDER_MAP, "groups.xml"));
        mapCollision.loadCollisions(Medias.create(FOLDER_MAP, "formulas.xml"),
                                    Medias.create(FOLDER_MAP, "collisions.xml"));

        final Featurable mario = factory.create(MARIO);
        handler.add(mario);

        final CameraTracker tracker = new CameraTracker(services);
        camera.setLimits(map);
        tracker.track(mario);
        handler.add(tracker);

        for (int i = 0; i < 5; i++)
        {
            final Featurable goomba = factory.create(GOOMBA);
            goomba.getFeature(GoombaUpdater.class).respawn(500 + i * 50);
            handler.add(goomba);
        }
    }
}

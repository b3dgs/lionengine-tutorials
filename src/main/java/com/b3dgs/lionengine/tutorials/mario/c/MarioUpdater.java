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
package com.b3dgs.lionengine.tutorials.mario.c;

import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Mario updating implementation.
 */
class MarioUpdater extends FeatureModel implements Refreshable, TileCollidableListener
{
    private static final int GROUND = 32;

    private final Camera camera;

    @FeatureGet private StateHandler handler;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Body body;
    @FeatureGet private TileCollidable tileCollidable;
    @FeatureGet private MarioModel model;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public MarioUpdater(Services services, Setup setup)
    {
        super();

        camera = services.get(Camera.class);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        handler.changeState(StateIdle.class);

        respawn();
    }

    /**
     * Respawn the mario.
     */
    public void respawn()
    {
        transformable.teleport(400, GROUND);
        camera.resetInterval(transformable);
        model.getJump().setDirection(DirectionNone.INSTANCE);
        body.resetGravity();
    }

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);
        mirrorable.update(extrp);
        model.getMovement().update(extrp);
        model.getJump().update(extrp);
        body.update(extrp);
        tileCollidable.update(extrp);

        if (transformable.getY() < 0)
        {
            respawn();
        }

        final SpriteAnimated surface = model.getSurface();
        surface.setMirror(mirrorable.getMirror());
        surface.update(extrp);
        surface.setLocation(camera, transformable);
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.Y == axis && transformable.getY() < transformable.getOldY())
        {
            body.resetGravity();
        }
    }
}

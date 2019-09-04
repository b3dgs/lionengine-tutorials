/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.state.State;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Entity updating implementation.
 */
class EntityUpdater extends FeatureModel implements Refreshable
{
    private static final int GROUND = 31;

    /** Entity configurer. */
    protected final Setup setup;

    @FeatureGet private StateHandler handler;
    @FeatureGet private EntityModel model;
    @FeatureGet private Transformable transformable;
    @FeatureGet private TileCollidable tileCollidable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Body body;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public EntityUpdater(Services services, Setup setup)
    {
        super(services, setup);

        this.setup = setup;
    }

    /**
     * Make the entity jump.
     */
    public void jump()
    {
        body.resetGravity();
        changeState(StateJump.class);
        model.getJump().setDirection(0.0, 6.0);
    }

    /**
     * Check the current entity state.
     * 
     * @param state The state to check.
     * @return <code>true</code> if it is this state, <code>false</code> else.
     */
    public boolean isState(Class<? extends State> state)
    {
        return handler.isState(state);
    }

    /**
     * Respawn the entity.
     * 
     * @param x The horizontal location.
     */
    public void respawn(int x)
    {
        mirrorable.mirror(Mirror.NONE);
        transformable.teleport(x, GROUND);
        model.getJump().setDirection(DirectionNone.INSTANCE);
        body.resetGravity();
        collidable.setEnabled(true);
        tileCollidable.setEnabled(true);
        changeState(StateIdle.class);
    }

    /**
     * Change the current state.
     * 
     * @param next The next state.
     */
    public void changeState(Class<? extends State> next)
    {
        handler.changeState(next);
    }

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);
        model.getMovement().update(extrp);
        model.getJump().update(extrp);
        transformable.moveLocation(extrp, body, model.getMovement(), model.getJump());
        tileCollidable.update(extrp);
        handler.postUpdate();
        mirrorable.update(extrp);

        final SpriteAnimated surface = model.getSurface();
        surface.setMirror(mirrorable.getMirror());
        surface.update(extrp);
    }
}

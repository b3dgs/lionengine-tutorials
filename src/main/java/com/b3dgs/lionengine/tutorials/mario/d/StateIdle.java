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

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;
import com.b3dgs.lionengine.game.feature.state.StateChecker;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;

/**
 * Idle state implementation.
 */
class StateIdle extends StateAbstract implements TileCollidableListener
{
    private final AtomicBoolean canJump = new AtomicBoolean(false);
    private final Transformable transformable;
    private final TileCollidable tileCollidable;
    private final Animator animator;
    private final Animation animation;
    private final Force movement;
    private final Force jump;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The associated animation.
     */
    public StateIdle(EntityModel model, Animation animation)
    {
        super();

        this.animation = animation;
        transformable = model.getFeature(Transformable.class);
        tileCollidable = model.getFeature(TileCollidable.class);
        animator = model.getSurface();
        movement = model.getMovement();
        jump = model.getJump();

        addTransition(StateIdle.class, () -> model.getInput().getHorizontalDirection() != 0);
        addTransition(StateWalk.class, () -> model.getInput().getHorizontalDirection() != 0);
        addTransition(StateJump.class, new StateChecker()
        {
            @Override
            public boolean getAsBoolean()
            {
                return model.getInput().getVerticalDirection() > 0 && canJump.get();
            }

            @Override
            public void exit()
            {
                Sfx.JUMP.play();
                jump.setDirection(0.0, 8.0);
                canJump.set(false);
            }
        });
    }

    @Override
    public void enter()
    {
        tileCollidable.addListener(this);
        movement.setDestination(0.0, 0.0);
        movement.setVelocity(0.3);
        movement.setSensibility(0.01);
        animator.play(animation);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(this);
    }

    @Override
    public void update(double extrp)
    {
        // Nothing to do
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.Y == axis && transformable.getY() < transformable.getOldY())
        {
            canJump.set(true);
        }
    }
}

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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.game.state.StateAbstract;
import com.b3dgs.lionengine.game.state.StateChecker;

/**
 * Walk state implementation.
 */
class StateWalk extends StateAbstract implements TileCollidableListener
{
    private final AtomicBoolean horizontalCollide = new AtomicBoolean(false);
    private final AtomicBoolean canJump = new AtomicBoolean(false);

    private final Force movement;
    private final Force jump;
    private final Mirrorable mirrorable;
    private final Animator animator;
    private final Animation animation;
    private final Transformable transformable;
    private final TileCollidable tileCollidable;
    private final EntityModel model;

    /** Played flag. */
    private boolean played;

    /**
     * Create the state.
     * 
     * @param featurable The featurable reference.
     * @param animation The associated animation.
     */
    public StateWalk(Featurable featurable, Animation animation)
    {
        super(EntityState.WALK);

        this.animation = animation;
        mirrorable = featurable.getFeature(Mirrorable.class);
        tileCollidable = featurable.getFeature(TileCollidable.class);
        transformable = featurable.getFeature(Transformable.class);

        model = featurable.getFeature(EntityModel.class);
        animator = model.getSurface();
        movement = model.getMovement();
        jump = model.getJump();

        addTransition(EntityState.IDLE,
                      () -> horizontalCollide.get()
                            || model.getInput().getHorizontalDirection() == 0
                               && model.getInput().getVerticalDirection() == 0);
        addTransition(EntityState.TURN,
                      () -> model.getInput().getHorizontalDirection() < 0 && movement.getDirectionHorizontal() > 0
                            || model.getInput().getHorizontalDirection() > 0 && movement.getDirectionHorizontal() < 0);
        addTransition(EntityState.JUMP, new StateChecker()
        {
            @Override
            public boolean check()
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
        movement.setVelocity(0.5);
        movement.setSensibility(0.1);
        tileCollidable.addListener(this);
        played = false;
        horizontalCollide.set(false);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(this);
    }

    @Override
    public void update(double extrp)
    {
        if (!played && movement.getDirectionHorizontal() != 0)
        {
            animator.play(animation);
            played = true;
        }

        final double side = model.getInput().getHorizontalDirection();
        movement.setDestination(side * 3, 0);
        animator.setAnimSpeed(Math.abs(movement.getDirectionHorizontal()) / 12.0);

        if (side < 0 && movement.getDirectionHorizontal() < 0)
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (side > 0 && movement.getDirectionHorizontal() > 0)
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.X == axis)
        {
            movement.setDirection(DirectionNone.INSTANCE);
            horizontalCollide.set(true);
        }
        else if (Axis.Y == axis && transformable.getY() < transformable.getOldY())
        {
            canJump.set(true);
        }
    }
}

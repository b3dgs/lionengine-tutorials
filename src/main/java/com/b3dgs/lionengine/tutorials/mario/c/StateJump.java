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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * Jump state implementation.
 */
class StateJump extends StateAbstract implements TileCollidableListener
{
    private final Force jump;
    private final Mirrorable mirrorable;
    private final Animator animator;
    private final Animation animation;
    private final Transformable transformable;
    private final TileCollidable tileCollidable;
    private final Force movement;
    private final InputDeviceDirectional input;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The associated animation.
     */
    public StateJump(MarioModel model, Animation animation)
    {
        super();

        this.animation = animation;
        mirrorable = model.getFeature(Mirrorable.class);
        transformable = model.getFeature(Transformable.class);
        tileCollidable = model.getFeature(TileCollidable.class);
        animator = model.getSurface();
        movement = model.getMovement();
        jump = model.getJump();
        input = model.getInput();

        addTransition(StateIdle.class, () -> jump.getDirectionVertical() == 0);
    }

    @Override
    public void enter()
    {
        movement.setVelocity(0.5);
        movement.setSensibility(0.1);
        animator.play(animation);
        jump.setDirection(0.0, 8.0);
        tileCollidable.setEnabled(false);
        tileCollidable.addListener(this);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(this);
    }

    @Override
    public void update(double extrp)
    {
        final double side = input.getHorizontalDirection();
        movement.setDestination(side * 3, 0);
        if (movement.getDirectionHorizontal() != 0)
        {
            mirrorable.mirror(movement.getDirectionHorizontal() < 0 ? Mirror.HORIZONTAL : Mirror.NONE);
        }
        if (transformable.getY() < transformable.getOldY())
        {
            tileCollidable.setEnabled(true);
        }
    }

    @Override
    public void notifyTileCollided(Tile tile, CollisionCategory category)
    {
        if (Axis.Y == category.getAxis())
        {
            jump.setDirection(DirectionNone.INSTANCE);
        }
    }
}

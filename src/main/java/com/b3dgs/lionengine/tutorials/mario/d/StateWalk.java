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

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * Walk state implementation.
 */
class StateWalk extends StateAbstract implements TileCollidableListener
{
    private static final String CATEGORY_KNEE_LEFT = "knee_l";
    private static final String CATEGORY_KNEE_RIGHT = "knee_r";

    private final AtomicBoolean collideX = new AtomicBoolean();
    private final AtomicBoolean collideY = new AtomicBoolean();

    private final Force movement;
    private final Mirrorable mirrorable;
    private final Animator animator;
    private final Animation animation;
    private final TileCollidable tileCollidable;
    private final InputDeviceDirectional input;

    /** Played flag. */
    private boolean played;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The associated animation.
     */
    StateWalk(EntityModel model, Animation animation)
    {
        super();

        this.animation = animation;

        mirrorable = model.getFeature(Mirrorable.class);
        tileCollidable = model.getFeature(TileCollidable.class);
        animator = model.getSurface();
        movement = model.getMovement();
        input = model.getInput();

        addTransition(StateIdle.class,
                      () -> collideX.get() || input.getHorizontalDirection() == 0 && input.getVerticalDirection() == 0);
        addTransition(StateTurn.class,
                      () -> input.getHorizontalDirection() < 0 && movement.getDirectionHorizontal() > 0
                            || input.getHorizontalDirection() > 0 && movement.getDirectionHorizontal() < 0);
        addTransition(StateJump.class, () -> input.getVerticalDirection() > 0);
        addTransition(StateFall.class, () -> !collideY.get());
    }

    @Override
    public void enter()
    {
        tileCollidable.addListener(this);
        played = false;
        collideX.set(false);
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(this);
    }

    @Override
    public void update(double extrp)
    {
        collideY.set(false);

        if (!played && movement.getDirectionHorizontal() != 0)
        {
            animator.play(animation);
            played = true;
        }

        final double side = input.getHorizontalDirection();
        movement.setDestination(side * EntityModel.SPEED_X, 0);
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
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if (Axis.X == category.getAxis())
        {
            // Allow to exit collision when moving on the opposite
            if (!(CATEGORY_KNEE_LEFT.equals(category.getName())
                  && input.getHorizontalDirection() < 0
                  && movement.getDirectionHorizontal() <= 0)
                && !(CATEGORY_KNEE_RIGHT.equals(category.getName())
                     && input.getHorizontalDirection() > 0
                     && movement.getDirectionHorizontal() >= 0))
            {
                tileCollidable.apply(result);
                collideX.set(true);
            }
        }
        else if (Axis.Y == category.getAxis())
        {
            tileCollidable.apply(result);
            collideY.set(true);
        }
    }
}

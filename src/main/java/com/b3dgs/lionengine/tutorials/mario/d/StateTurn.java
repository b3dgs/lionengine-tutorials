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
import com.b3dgs.lionengine.game.feature.state.StateChecker;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * Turn state implementation.
 */
class StateTurn extends StateAbstract implements TileCollidableListener
{
    private static final String CATEGORY_KNEE_LEFT = "knee_l";
    private static final String CATEGORY_KNEE_RIGHT = "knee_r";

    private final AtomicBoolean collideX = new AtomicBoolean();
    private final AtomicBoolean collideY = new AtomicBoolean();

    private final Force movement;
    private final Mirrorable mirrorable;
    private final TileCollidable tileCollidable;
    private final Animator animator;
    private final Animation animation;
    private final InputDeviceDirectional input;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The associated animation.
     */
    StateTurn(EntityModel model, Animation animation)
    {
        super();

        this.animation = animation;

        animator = model.getSurface();
        movement = model.getMovement();
        mirrorable = model.getFeature(Mirrorable.class);
        tileCollidable = model.getFeature(TileCollidable.class);
        input = model.getInput();

        addTransition(StateIdle.class, new StateChecker()
        {
            @Override
            public boolean getAsBoolean()
            {
                return input.getHorizontalDirection() == 0
                       && movement.getDirectionHorizontal() == 0
                       && input.getVerticalDirection() == 0;
            }

            @Override
            public void exit()
            {
                mirrorable.mirror(mirrorable.getMirror() == Mirror.HORIZONTAL ? Mirror.NONE : Mirror.HORIZONTAL);
            }
        });
        addTransition(StateIdle.class,
                      () -> collideX.get() || input.getHorizontalDirection() == 0 && input.getVerticalDirection() == 0);
        addTransition(StateWalk.class,
                      () -> (input.getHorizontalDirection() < 0 && movement.getDirectionHorizontal() < 0
                             || input.getHorizontalDirection() > 0 && movement.getDirectionHorizontal() > 0)
                            && input.getVerticalDirection() == 0);
        addTransition(StateJump.class, () -> input.getVerticalDirection() > 0);
        addTransition(StateFall.class, () -> !collideY.get());
    }

    @Override
    public void enter()
    {
        animator.play(animation);
        movement.setVelocity(0.28);
        movement.setSensibility(0.005);
        tileCollidable.addListener(this);
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

        final double side = input.getHorizontalDirection();
        movement.setDestination(side * 2, 0);
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

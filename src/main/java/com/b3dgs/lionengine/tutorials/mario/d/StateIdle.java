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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;

/**
 * Idle state implementation.
 */
class StateIdle extends StateAbstract implements TileCollidableListener
{
    private final TileCollidable tileCollidable;
    private final Animator animator;
    private final Animation animation;
    private final Force movement;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The associated animation.
     */
    StateIdle(EntityModel model, Animation animation)
    {
        super();

        this.animation = animation;
        tileCollidable = model.getFeature(TileCollidable.class);
        animator = model.getSurface();
        movement = model.getMovement();

        addTransition(StateWalk.class, () -> model.getInput().getHorizontalDirection() != 0);
        addTransition(StateJump.class, () -> model.getInput().getVerticalDirection() > 0);
    }

    @Override
    public void enter()
    {
        tileCollidable.addListener(this);
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
        movement.setDestination(0.0, 0.0);
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        tileCollidable.apply(result);
    }
}

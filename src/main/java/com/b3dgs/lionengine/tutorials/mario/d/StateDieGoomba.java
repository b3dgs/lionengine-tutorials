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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;

/**
 * Goomba die state implementation.
 */
class StateDieGoomba extends StateAbstract
{
    private final EntityModel model;
    private final Force movement;
    private final Animator animator;
    private final Animation animation;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The associated animation.
     */
    StateDieGoomba(EntityModel model, Animation animation)
    {
        super();

        this.model = model;
        this.animation = animation;
        animator = model.getSurface();
        movement = model.getMovement();
    }

    @Override
    public void enter()
    {
        animator.play(animation);
        movement.setDestination(0.0, 0.0);
    }

    @Override
    public void update(double extrp)
    {
        if (AnimState.FINISHED == animator.getAnimState())
        {
            model.getFeature(Identifiable.class).destroy();
        }
    }
}

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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;

/**
 * Mario die state implementation.
 */
class StateDieMario extends StateAbstract
{
    private final Force movement;
    private final Force jump;
    private final Animator animator;
    private final Animation animation;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The associated animation.
     */
    public StateDieMario(EntityModel model, Animation animation)
    {
        super();

        this.animation = animation;
        animator = model.getSurface();
        movement = model.getMovement();
        jump = model.getJump();
    }

    @Override
    public void enter()
    {
        animator.play(animation);
        movement.setDestination(0.0, 0.0);
        jump.setDirection(0.0, 9.0);
    }

    @Override
    public void update(double extrp)
    {
        // Nothing to do
    }
}

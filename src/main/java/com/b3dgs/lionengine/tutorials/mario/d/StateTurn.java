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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;
import com.b3dgs.lionengine.game.feature.state.StateChecker;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * Turn state implementation.
 */
class StateTurn extends StateAbstract
{
    private final Mirrorable mirrorable;
    private final Force movement;
    private final Animator animator;
    private final Animation animation;
    private final EntityModel model;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The associated animation.
     */
    public StateTurn(EntityModel model, Animation animation)
    {
        super();

        this.model = model;
        this.animation = animation;
        mirrorable = model.getFeature(Mirrorable.class);
        animator = model.getSurface();
        movement = model.getMovement();

        addTransition(StateIdle.class, new StateChecker()
        {
            @Override
            public boolean getAsBoolean()
            {
                final InputDeviceDirectional input = model.getInput();
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
        addTransition(StateWalk.class,
                      () -> (model.getInput().getHorizontalDirection() < 0 && movement.getDirectionHorizontal() < 0
                             || model.getInput().getHorizontalDirection() > 0 && movement.getDirectionHorizontal() > 0)
                            && model.getInput().getVerticalDirection() == 0);
        addTransition(StateJump.class, new StateChecker()
        {
            @Override
            public boolean getAsBoolean()
            {
                return model.getInput().getVerticalDirection() > 0;
            }

            @Override
            public void exit()
            {
                mirrorable.mirror(mirrorable.getMirror() == Mirror.HORIZONTAL ? Mirror.NONE : Mirror.HORIZONTAL);
            }
        });
    }

    @Override
    public void enter()
    {
        animator.play(animation);
        movement.setVelocity(0.28);
        movement.setSensibility(0.005);
    }

    @Override
    public void update(double extrp)
    {
        final double side = model.getInput().getHorizontalDirection();
        movement.setDestination(side * 2, 0);
    }
}

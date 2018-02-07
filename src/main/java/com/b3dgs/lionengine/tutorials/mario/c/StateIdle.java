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
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.StateTransition;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.state.StateGame;
import com.b3dgs.lionengine.game.state.StateTransitionInputDirectionalChecker;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * Idle state implementation.
 */
class StateIdle extends StateGame
{
    private final Animator animator;
    private final Animation animation;
    private final Force movement;

    /**
     * Create the state.
     * 
     * @param featurable The featurable reference.
     * @param animation The associated animation.
     */
    public StateIdle(Featurable featurable, Animation animation)
    {
        super(MarioState.IDLE);

        this.animation = animation;

        final MarioModel model = featurable.getFeature(MarioModel.class);
        animator = model.getSurface();
        movement = model.getMovement();

        addTransition(new TransitionIdleToWalk());
        addTransition(new TransitionIdleToJump());
    }

    @Override
    public void enter()
    {
        movement.setDestination(0.0, 0.0);
        movement.setVelocity(0.3);
        movement.setSensibility(0.01);
        animator.play(animation);
    }

    @Override
    public void update(double extrp)
    {
        // Nothing to do
    }

    /**
     * Transition from {@link StateIdle} to {@link StateWalk}.
     */
    private class TransitionIdleToWalk extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionIdleToWalk()
        {
            super(MarioState.WALK);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return input.getHorizontalDirection() != 0;
        }
    }

    /**
     * Transition from {@link StateIdle} to {@link StateJump}.
     */
    private class TransitionIdleToJump extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionIdleToJump()
        {
            super(MarioState.JUMP);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return input.getVerticalDirection() > 0;
        }
    }
}

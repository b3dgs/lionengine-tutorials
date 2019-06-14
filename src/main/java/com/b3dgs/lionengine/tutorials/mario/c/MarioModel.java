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
package com.b3dgs.lionengine.tutorials.mario.c;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * Mario model implementation.
 */
@FeatureInterface
class MarioModel extends FeatureModel
{
    private static final double GRAVITY = 10.0;

    private final Force movement = new Force();
    private final Force jump = new Force();
    private final SpriteAnimated surface;

    private final InputDeviceDirectional keyboard;
    private final SourceResolutionProvider source;

    @FeatureGet private Body body;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public MarioModel(Services services, Setup setup)
    {
        super();

        source = services.get(SourceResolutionProvider.class);
        keyboard = services.get(InputDeviceDirectional.class);

        final FramesConfig frames = FramesConfig.imports(setup);
        surface = Drawable.loadSpriteAnimated(setup.getSurface(), frames.getHorizontal(), frames.getVertical());
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(0, -1);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        movement.setVelocity(1.0);

        jump.setSensibility(0.1);
        jump.setVelocity(0.5);

        body.setGravity(GRAVITY);
        body.setGravityMax(GRAVITY);
        body.setDesiredFps(source.getRate());
    }

    /**
     * Get the movement force.
     * 
     * @return The movement force.
     */
    public Force getMovement()
    {
        return movement;
    }

    /**
     * Get the jump force.
     * 
     * @return The jump force.
     */
    public Force getJump()
    {
        return jump;
    }

    /**
     * Get the surface representation.
     * 
     * @return The surface representation.
     */
    public SpriteAnimated getSurface()
    {
        return surface;
    }

    /**
     * Get input.
     * 
     * @return The input.
     */
    public InputDeviceDirectional getInput()
    {
        return keyboard;
    }
}

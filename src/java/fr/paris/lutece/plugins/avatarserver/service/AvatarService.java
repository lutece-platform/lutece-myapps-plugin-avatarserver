/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.avatarserver.service;

import fr.paris.lutece.plugins.avatarserver.business.Avatar;
import fr.paris.lutece.plugins.avatarserver.business.AvatarHome;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * Avatar Service
 */
public final class AvatarService
{
    private static final String PROPERTY_SIZE = "avatarserver.avatar.size";
    private static final int DEFAULT_SIZE = 100;

    /** Private constructor */
    private AvatarService(  )
    {
    }

    /**
     * Create an instance of the avatar class
     * @param avatar The instance of the Avatar which contains the informations to store
     * @return The  instance of avatar which has been created with its primary key.
     */
    public static Avatar create( Avatar avatar )
    {
        avatar.setHash( HashService.getHash( avatar.getEmail(  ) ) );
        avatar.setValue( ImageService.resizeImage( avatar.getValue(  ), getSize(  ) ) );

        return AvatarHome.create( avatar );
    }

    /**
     * Update an instance of the avatar class
     * @param avatar The instance of the Avatar which contains the informations to store
     * @return The  instance of avatar which has been created with its primary key.
     */
    public static Avatar update( Avatar avatar )
    {
        avatar.setHash( HashService.getHash( avatar.getEmail(  ) ) );
        avatar.setValue( ImageService.resizeImage( avatar.getValue(  ), getSize(  ) ) );

        return AvatarHome.update( avatar );
    }

    /**
     * Get the avatar size
     * @return The size
     */
    private static int getSize(  )
    {
        return AppPropertiesService.getPropertyInt( PROPERTY_SIZE, DEFAULT_SIZE );
    }
}

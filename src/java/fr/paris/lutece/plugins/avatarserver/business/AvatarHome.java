/*
 * Copyright (c) 2002-2016, Mairie de Paris
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
package fr.paris.lutece.plugins.avatarserver.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;

/**
 * This class provides instances management methods (create, find, ...) for Avatar objects
 */
public final class AvatarHome
{
    // Static variable pointed at the DAO instance
    private static IAvatarDAO _dao = SpringContextService.getBean( "avatarserver.avatarDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "avatarserver" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private AvatarHome( )
    {
    }

    /**
     * Returns an instance of a avatar whose identifier is specified in parameter
     * 
     * @param strHash
     *            The avatar hash created with the email address
     * @return an instance of Avatar
     */
    public static Avatar findByHash( String strHash )
    {
        return _dao.selectByHash( strHash, _plugin );
    }

    /**
     * Create an instance of the avatar class
     * 
     * @param avatar
     *            The instance of the Avatar which contains the informations to store
     * @return The instance of avatar which has been created with its primary key.
     */
    public static Avatar create( Avatar avatar )
    {
        _dao.insert( avatar, _plugin );

        return avatar;
    }

    /**
     * Update of the avatar which is specified in parameter
     * 
     * @param avatar
     *            The instance of the Avatar which contains the data to store
     * @return The instance of the avatar which has been updated
     */
    public static Avatar update( Avatar avatar )
    {
        _dao.store( avatar, _plugin );

        return avatar;
    }

    /**
     * Remove the avatar whose identifier is specified in parameter
     * 
     * @param nAvatarId
     *            The avatar Id
     */
    public static void remove( int nAvatarId )
    {
        _dao.delete( nAvatarId, _plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a avatar whose identifier is specified in parameter
     * 
     * @param nKey
     *            The avatar primary key
     * @return an instance of Avatar
     */
    public static Avatar findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the avatar objects and returns them in form of a collection
     * 
     * @return the collection which contains the data of all the avatar objects
     */
    public static Collection<Avatar> getAvatarsList( )
    {
        return _dao.selectAvatarsList( _plugin );
    }
}

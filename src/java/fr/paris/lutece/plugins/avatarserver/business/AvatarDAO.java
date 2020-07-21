/*
 * Copyright (c) 2002-2020, City of Paris
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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class provides Data Access methods for Avatar objects
 */
public final class AvatarDAO implements IAvatarDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_avatar ) FROM avatarserver_avatar";
    private static final String SQL_QUERY_SELECT = "SELECT id_avatar, email, mime_type, file_value, hash_email FROM avatarserver_avatar WHERE id_avatar = ?";
    private static final String SQL_QUERY_SELECT_BY_HASH = "SELECT id_avatar, email, mime_type, file_value, hash_email FROM avatarserver_avatar WHERE hash_email = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO avatarserver_avatar ( id_avatar, email,mime_type,file_value, hash_email ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM avatarserver_avatar WHERE id_avatar = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE avatarserver_avatar SET id_avatar = ?, email = ?, mime_type = ?, file_value = ?, hash_email = ?  WHERE id_avatar = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_avatar, email, mime_type, file_value, hash_email FROM avatarserver_avatar";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
            daoUtil.executeQuery( );

            int nKey = 1;

            if ( daoUtil.next( ) )
            {
                nKey = daoUtil.getInt( 1 ) + 1;
            }

            daoUtil.free( );

            return nKey;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Avatar avatar, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            avatar.setId( newPrimaryKey( plugin ) );
            daoUtil.setInt( 1, avatar.getId( ) );
            daoUtil.setString( 2, avatar.getEmail( ) );
            daoUtil.setString( 3, avatar.getMimeType( ) );
            daoUtil.setBytes( 4, avatar.getValue( ) );
            daoUtil.setString( 5, avatar.getHash( ) );
            daoUtil.executeUpdate( );
            daoUtil.free( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Avatar load( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            Avatar avatar = null;

            if ( daoUtil.next( ) )
            {
                avatar = new Avatar( );
                avatar.setId( daoUtil.getInt( 1 ) );
                avatar.setEmail( daoUtil.getString( 2 ) );
                avatar.setMimeType( daoUtil.getString( 3 ) );
                avatar.setValue( daoUtil.getBytes( 4 ) );
                avatar.setHash( daoUtil.getString( 5 ) );
            }

            daoUtil.free( );

            return avatar;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nAvatarId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nAvatarId );
            daoUtil.executeUpdate( );
            daoUtil.free( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Avatar avatar, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            daoUtil.setInt( 1, avatar.getId( ) );
            daoUtil.setString( 2, avatar.getEmail( ) );
            daoUtil.setString( 3, avatar.getMimeType( ) );
            daoUtil.setBytes( 4, avatar.getValue( ) );
            daoUtil.setString( 5, avatar.getHash( ) );
            daoUtil.setInt( 6, avatar.getId( ) );
            daoUtil.executeUpdate( );
            daoUtil.free( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Avatar> selectAvatarsList( Plugin plugin )
    {
        Collection<Avatar> avatarList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Avatar avatar = new Avatar( );

                avatar.setId( daoUtil.getInt( 1 ) );
                avatar.setEmail( daoUtil.getString( 2 ) );
                avatar.setMimeType( daoUtil.getString( 3 ) );
                avatar.setValue( daoUtil.getBytes( 4 ) );
                avatar.setHash( daoUtil.getString( 5 ) );

                avatarList.add( avatar );
            }

            daoUtil.free( );
        }

        return avatarList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Avatar selectByHash( String strHash, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_HASH, plugin ) )
        {
            daoUtil.setString( 1, strHash );
            daoUtil.executeQuery( );

            Avatar avatar = null;

            if ( daoUtil.next( ) )
            {
                avatar = new Avatar( );
                avatar.setId( daoUtil.getInt( 1 ) );
                avatar.setEmail( daoUtil.getString( 2 ) );
                avatar.setMimeType( daoUtil.getString( 3 ) );
                avatar.setValue( daoUtil.getBytes( 4 ) );
                avatar.setHash( daoUtil.getString( 5 ) );
            }

            daoUtil.free( );

            return avatar;
        }
    }
}

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

import java.util.Arrays;

import fr.paris.lutece.test.LuteceTestCase;

public class AvatarBusinessTest extends LuteceTestCase
{
    private final static int IDAVATAR = 1;
    private final static String EMAIL1 = "Email1";
    private final static String EMAIL2 = "Email2";
    private final static String MIME1 = "image/png";
    private final static String MIME2 = "image/jpg";
    private final static String HASH1 = "1234";
    private final static String HASH2 = "567890";
    private final static String AVATARIMAGE1 = "AvatarImage1";
    private final static String AVATARIMAGE2 = "AvatarImage2";

    public void testBusiness( )
    {
        // Initialize an object
        Avatar avatar = new Avatar( );
        avatar.setId( IDAVATAR );
        avatar.setEmail( EMAIL1 );
        avatar.setMimeType( MIME1 );
        avatar.setHash( HASH1 );
        avatar.setValue( AVATARIMAGE1.getBytes( ) );

        // Create test
        AvatarHome.create( avatar );

        Avatar avatarStored = AvatarHome.findByPrimaryKey( avatar.getId( ) );
        assertEquals( avatarStored.getId( ), avatar.getId( ) );
        assertEquals( avatarStored.getEmail( ), avatar.getEmail( ) );
        assertEquals( avatarStored.getMimeType( ), avatar.getMimeType( ) );
        assertEquals( avatarStored.getHash( ), avatar.getHash( ) );
        assertTrue( Arrays.equals( avatarStored.getValue( ), avatar.getValue( ) ) );

        // Update test
        avatar.setId( IDAVATAR );
        avatar.setEmail( EMAIL2 );
        avatar.setMimeType( MIME2 );
        avatar.setHash( HASH2 );
        avatar.setValue( AVATARIMAGE2.getBytes( ) );
        AvatarHome.update( avatar );
        avatarStored = AvatarHome.findByPrimaryKey( avatar.getId( ) );
        assertEquals( avatarStored.getId( ), avatar.getId( ) );
        assertEquals( avatarStored.getEmail( ), avatar.getEmail( ) );
        assertEquals( avatarStored.getMimeType( ), avatar.getMimeType( ) );
        assertEquals( avatarStored.getHash( ), avatar.getHash( ) );
        assertTrue( Arrays.equals( avatarStored.getValue( ), avatar.getValue( ) ) );

        // List test
        AvatarHome.getAvatarsList( );

        // Delete test
        AvatarHome.remove( avatar.getId( ) );
        avatarStored = AvatarHome.findByPrimaryKey( avatar.getId( ) );
        assertNull( avatarStored );
    }
}

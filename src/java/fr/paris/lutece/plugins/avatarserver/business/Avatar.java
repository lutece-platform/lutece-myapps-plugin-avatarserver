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

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * This is the business class for the object Avatar
 */
public class Avatar
{
    // Variables declarations
    private byte [ ] _byValue;
    private String _strMimeType;
    private String _strHash;
    private int _nIdAvatar;

    // @NotEmpty( message = "#i18n{avatarserver.validation.avatar.Email.notEmpty}" )
    @NotEmpty( message = "#i18n{portal.validation.message.notEmpty}" )
    // @Size( max = 255 , message = "#i18n{avatarserver.validation.avatar.Email.size}" )
    @Size( max = 255, message = "#i18n{portal.validation.message.sizeMax}" )
    @Email( message = "#i18n{portal.validation.message.email}" )
    private String _strEmail;

    /**
     * Returns the IdAvatar
     *
     * @return The IdAvatar
     */
    public int getId( )
    {
        return _nIdAvatar;
    }

    /**
     * Sets the IdAvatar
     *
     * @param nIdAvatar
     *            The IdAvatar
     */
    public void setId( int nIdAvatar )
    {
        _nIdAvatar = nIdAvatar;
    }

    /**
     * Returns the Email
     *
     * @return The Email
     */
    public String getEmail( )
    {
        return _strEmail;
    }

    /**
     * Sets the Email
     *
     * @param strEmail
     *            The Email
     */
    public void setEmail( String strEmail )
    {
        _strEmail = strEmail;
    }

    /**
     * get the icon file value
     *
     * @return the icon file value
     */
    public byte [ ] getValue( )
    {
        return _byValue;
    }

    /**
     * set the icon file value
     *
     * @param value
     *            the file value
     */
    public void setValue( byte [ ] value )
    {
        _byValue = value;
    }

    /**
     * the icon mime type
     *
     * @return the icon mime type
     */
    public String getMimeType( )
    {
        return _strMimeType;
    }

    /**
     * set the icon mime type
     *
     * @param mimeType
     *            the icon mime type
     */
    public void setMimeType( String mimeType )
    {
        _strMimeType = mimeType;
    }

    /**
     * the hash
     *
     * @return the hash
     */
    public String getHash( )
    {
        return _strHash;
    }

    /**
     * set the hash
     *
     * @param hash
     *            the hash
     */
    public void setHash( String hash )
    {
        _strHash = hash;
    }
}

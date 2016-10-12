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
package fr.paris.lutece.plugins.avatarserver.web;

import fr.paris.lutece.plugins.avatarserver.business.Avatar;
import fr.paris.lutece.plugins.avatarserver.business.AvatarHome;
import fr.paris.lutece.plugins.avatarserver.service.AvatarService;
import fr.paris.lutece.plugins.avatarserver.service.HashService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.http.MultipartUtil;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * PostAvatar Servlet.
 * This servlet let upload an avatar for the current connected user
 */
public class PostAvatarServlet extends HttpServlet
{
    private static final String PARAMETER_IMAGE = "image";
    private static final String PARAMETER_RETURN_URL = "return_url";
    private static int _nRequestSizeMax = 200000;
    private static int _nSizeThreshold = -1;

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        ServletOutputStream out = resp.getOutputStream( );
        out.println( "Only POST is allowed" );
        out.flush( );
        out.close( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        ServletOutputStream out = response.getOutputStream( );

        try
        {
            MultipartHttpServletRequest multiPartRequest = MultipartUtil.convert( _nSizeThreshold, _nRequestSizeMax, true, request );
            FileItem imageSource = multiPartRequest.getFile( PARAMETER_IMAGE );
            String strReturnUrl = multiPartRequest.getParameter( PARAMETER_RETURN_URL );

            LuteceUser user = SecurityService.getInstance( ).getRemoteUser( request );
            if ( user != null )
            {
                String strEmail = user.getEmail( );
                String strHash = HashService.getHash( strEmail );
                Avatar avatar = AvatarHome.findByHash( strHash );
                boolean bCreate = false;

                if ( avatar == null )
                {
                    avatar = new Avatar( );
                    bCreate = true;
                }
                avatar.setEmail( strEmail );
                avatar.setHash( strHash );
                avatar.setValue( imageSource.get( ) );
                avatar.setMimeType( imageSource.getContentType( ) );

                if ( bCreate )
                {
                    AvatarService.create( avatar );
                }
                else
                {
                    AvatarService.update( avatar );
                }

                out.println( "Avatar successfully posted!" );
                if( strReturnUrl != null )
                {
                    response.sendRedirect( strReturnUrl );
                }
            }
            else
            {
                out.println( "No user connected!" );
                response.sendError( HttpServletResponse.SC_UNAUTHORIZED );
            }
        }
        catch( FileUploadException ex )
        {
            out.println( "Error uploading avatar : " + ex.getMessage( ) );
            AppLogService.error( "Error uploading avatar : " + ex.getMessage( ), ex );
            response.sendError( HttpServletResponse.SC_BAD_REQUEST );
        }
        catch( UserNotSignedException ex )
        {
            out.println( "Error uploading avatar : " + ex.getMessage( ) );
            AppLogService.error( "Error uploading avatar : " + ex.getMessage( ), ex );
            response.sendError( HttpServletResponse.SC_UNAUTHORIZED );
        }
        finally
        {
            out.flush( );
            out.close( );
        }
    }
}

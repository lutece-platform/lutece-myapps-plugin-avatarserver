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
import fr.paris.lutece.plugins.avatarserver.service.ImageService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.xpages.XPage;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * AvatarServer App
 */
@RequestScoped
@Named( "avatarserver.xpage.avatarserver" )
@Controller( xpageName = "avatarserver", pageTitleI18nKey = "avatarserver.xpage.pageTitle", pagePathI18nKey = "avatarserver.xpage.pagePath", securityTokenEnabled = true )
public class AvatarServerApp extends MVCApplication
{
    private static final long serialVersionUID = 1L;
    private static final String TEMPLATE_AVATAR = "skin/plugins/avatarserver/update_avatar.html";
    private static final String MARK_AVATAR = "avatar";
    private static final String MARK_AVATAR_ID = "avatar_id";
    private static final String MARK_EMAIL = "email";
    private static final String MARK_BACK_URL = "url_back";

    private static final String VIEW_HOME = "home";
    private static final String ACTION_UPDATE_AVATAR = "updateAvatar";

    private static final String PARAMETER_IMAGE = "avatar_image";
    private static final String MESSAGE_MISSING_FILE = "avatarserver.xpage.message.missingFile";
    private static final String MESSAGE_INVALID_IMAGE = "avatarserver.message.invalidImage";

    @Inject
    @ConfigProperty( name = "avatarserver.update_avatar.afterUpdateUrl", defaultValue = "Portal.jsp?page=mydashboard" )
    private String _strAfterUpdateUrl;

    @Inject
    @ConfigProperty( name = "avatarserver.update_avatar.backUrl", defaultValue = "jsp/site/Portal.jsp?page=mydashboard" )
    private String _strBackUrl;

    /**
     * Update avatar view
     * 
     * @param request
     *            The HTTP request
     * @param model
     *            The model
     * @return The XPage
     * @throws UserNotSignedException
     *             if the user is not signed
     */
    @View( value = VIEW_HOME, defaultView = true, securityTokenAction = ACTION_UPDATE_AVATAR )
    public XPage getUpdateAvatar( HttpServletRequest request, Models model ) throws UserNotSignedException
    {
        LuteceUser user = SecurityService.isAuthenticationEnable( ) ? SecurityService.getInstance( ).getRegisteredUser( request ) : null;

        if ( user == null )
        {
            throw new UserNotSignedException( );
        }

        String strEmail = user.getEmail( );
        String strHash = HashService.getHash( strEmail );
        Avatar avatar = AvatarHome.findByHash( strHash );

        if ( avatar != null )
        {
            model.put( MARK_AVATAR_ID, avatar.getId( ) );
            model.put( MARK_AVATAR, avatar );
        }

        model.put( MARK_EMAIL, strEmail );
        model.put( MARK_BACK_URL, _strBackUrl );

        return getXPage( TEMPLATE_AVATAR, request.getLocale( ), model );
    }

    /**
     * Do update avatar action
     * 
     * @param request
     *            The HTTP request
     * @return The next XPage
     * @throws UserNotSignedException
     *             if the user is not signed
     */
    @Action( ACTION_UPDATE_AVATAR )
    public XPage doUpdateAvatar( HttpServletRequest request ) throws UserNotSignedException
    {
        LuteceUser user = SecurityService.isAuthenticationEnable( ) ? SecurityService.getInstance( ).getRegisteredUser( request ) : null;

        if ( user == null )
        {
            throw new UserNotSignedException( );
        }

        MultipartItem imageSource = ( request instanceof MultipartHttpServletRequest ) ? ( (MultipartHttpServletRequest) request ).getFile( PARAMETER_IMAGE )
                : null;

        if ( imageSource == null || imageSource.getSize( ) == 0 )
        {
            addError( MESSAGE_MISSING_FILE, request.getLocale( ) );

            return redirectView( request, VIEW_HOME );
        }

        if ( !ImageService.isImage( imageSource.get( ) ) )
        {
            addError( MESSAGE_INVALID_IMAGE, request.getLocale( ) );

            return redirectView( request, VIEW_HOME );
        }

        Avatar avatar = AvatarHome.findByHash( HashService.getHash( user.getEmail( ) ) );

        if ( avatar != null )
        {
            avatar.setValue( imageSource.get( ) );
            avatar.setMimeType( imageSource.getContentType( ) );
            AvatarService.update( avatar );
        }
        else
        {
            avatar = new Avatar( );
            avatar.setEmail( user.getEmail( ) );
            avatar.setValue( imageSource.get( ) );
            avatar.setMimeType( imageSource.getContentType( ) );
            AvatarService.create( avatar );
        }

        return redirect( request, _strAfterUpdateUrl );
    }
}

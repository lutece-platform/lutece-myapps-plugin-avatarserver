/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
import fr.paris.lutece.plugins.avatarserver.service.HashService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * AvatarServer App
 */
@Controller( xpageName = "avatarserver", pageTitleI18nKey = "avatarserver.xpage.pageTitle" )
public class AvatarServerApp extends MVCApplication
{
    private static final String TEMPLATE_AVATAR = "skin/plugins/avatarserver/update_avatar.html";
    private static final String VIEW_HOME = "home";
    private static final String MARK_AVATAR = "avatar";
    private static final String MARK_AVATAR_ID = "avatar_id";
    private static final String MARK_EMAIL = "email";
    private static final String MARK_BACK_URL = "url_back";
    private static final String PROPERTY_BACK_URL = "avatarserver.update_avatar.backUrl";
    

    @View( value = VIEW_HOME, defaultView = true )
    public XPage getDashboards( HttpServletRequest request ) throws UserNotSignedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        LuteceUser user = SecurityService.isAuthenticationEnable( ) ? SecurityService.getInstance( )
                .getRegisteredUser( request ) : null;
        if ( user == null )
        {
            throw new UserNotSignedException( );
        } 

        String strEmail = user.getEmail();
        String strHash = HashService.getHash( strEmail );
        Avatar avatar = AvatarHome.findByHash( strHash );
        String strBackUrl = AppPropertiesService.getProperty( PROPERTY_BACK_URL );
        
        Map<String, Object> model = getModel();
        if( avatar != null )
        {
            model.put( MARK_AVATAR_ID , avatar.getId() );
        }
        model.put( MARK_AVATAR , avatar );
        model.put( MARK_EMAIL , strEmail );
        model.put( MARK_BACK_URL , strBackUrl );
        
        return getXPage( TEMPLATE_AVATAR, request.getLocale( ), model );
    }
}
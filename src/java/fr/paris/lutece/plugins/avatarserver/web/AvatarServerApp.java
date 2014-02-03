/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.paris.lutece.plugins.avatarserver.web;

import fr.paris.lutece.plugins.avatarserver.business.Avatar;
import fr.paris.lutece.plugins.avatarserver.business.AvatarHome;
import fr.paris.lutece.plugins.avatarserver.service.HashService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 * AvatarServer App
 */
@Controller( xpageName = "avatarserver", pageTitleI18nKey = "avatarserver.xpage.pageTitle" )
public class AvatarServerApp extends MVCApplication
{
    private static final String TEMPLATE_AVATAR = "skin/plugins/avatarserver/modify_avatar.html";
    private static final String VIEW_HOME = "home";
    private static final String MARK_AVATAR = "avatar";
    private static final String MARK_AVATAR_ID = "avatar_id";
    private static final String MARK_EMAIL = "email";
    

    @View( value = VIEW_HOME, defaultView = true )
    public XPage getDashboards( HttpServletRequest request ) throws UserNotSignedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        LuteceUser luteceUser = SecurityService.isAuthenticationEnable( ) ? SecurityService.getInstance( )
                .getRegisteredUser( request ) : null;
        if ( luteceUser == null )
        {
            throw new UserNotSignedException( );
        } 

        String strEmail = luteceUser.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL);
        
        try
        {
            Method m = luteceUser.getClass().getMethod( "getEmail", (Class<?>) null);
            strEmail = (String) m.invoke(luteceUser, (Object) null);
            System.out.println( "************** Email '" + strEmail + "'" );
        }
        catch (NoSuchMethodException ex)
        {
            System.out.println( "************** GetEmail error " + ex.getMessage() );
        }
        catch (SecurityException ex)
        {
            System.out.println( "************** GetEmail error " + ex.getMessage() );
        }

        String strHash = HashService.getHash( strEmail );
        Avatar avatar = AvatarHome.findByHash( strHash );
        
        Map<String, Object> model = getModel();
        if( avatar != null )
        {
            model.put( MARK_AVATAR_ID , avatar.getId() );
        }
        model.put( MARK_AVATAR , avatar );
        model.put( MARK_EMAIL , strEmail );
        
        return getXPage( TEMPLATE_AVATAR, request.getLocale( ), model );
    }
}
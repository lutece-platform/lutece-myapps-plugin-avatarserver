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
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Avatar features ( manage, create, modify, remove )
 */
@SessionScoped
@Named( "avatarserverAvatarJspBean" )
@Controller( controllerJsp = "ManageAvatars.jsp", controllerPath = "jsp/admin/plugins/avatarserver/", right = AvatarJspBean.RIGHT_MANAGEAVATARSERVER, securityTokenEnabled = true )
public class AvatarJspBean extends MVCAdminJspBean
{
    public static final String RIGHT_MANAGEAVATARSERVER = "AVATARSERVER_MANAGEMENT";

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE_MANAGE_AVATARS = "/admin/plugins/avatarserver/manage_avatars.html";
    private static final String TEMPLATE_CREATE_AVATAR = "/admin/plugins/avatarserver/create_avatar.html";
    private static final String TEMPLATE_MODIFY_AVATAR = "/admin/plugins/avatarserver/modify_avatar.html";

    private static final String PARAMETER_ID_AVATAR = "id_avatar";
    private static final String PARAMETER_IMAGE = "avatar_image";

    private static final String PROPERTY_PAGE_TITLE_MANAGE_AVATARS = "avatarserver.manage_avatars.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_AVATAR = "avatarserver.modify_avatar.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_AVATAR = "avatarserver.create_avatar.pageTitle";
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "avatarserver.listAvatars.itemsPerPage";

    private static final String MARK_AVATAR_LIST = "avatar_list";
    private static final String MARK_AVATAR = "avatar";
    private static final String JSP_MANAGE_AVATARS = "jsp/admin/plugins/avatarserver/ManageAvatars.jsp";

    private static final String MESSAGE_CONFIRM_REMOVE_AVATAR = "avatarserver.message.confirmRemoveAvatar";
    private static final String MESSAGE_MISSING_FILE = "avatarserver.xpage.message.missingFile";
    private static final String MESSAGE_INVALID_IMAGE = "avatarserver.message.invalidImage";
    private static final String MESSAGE_DUPLICATE_EMAIL = "avatarserver.message.duplicateEmail";
    private static final String MESSAGE_AVATAR_NOT_FOUND = "avatarserver.message.avatarNotFound";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "avatarserver.model.entity.avatar.attribute.";

    private static final String VIEW_MANAGE_AVATARS = "manageAvatars";
    private static final String VIEW_CREATE_AVATAR = "createAvatar";
    private static final String VIEW_MODIFY_AVATAR = "modifyAvatar";
    private static final String VIEW_CONFIRM_REMOVE_AVATAR = "confirmRemoveAvatar";

    private static final String ACTION_CREATE_AVATAR = "createAvatar";
    private static final String ACTION_MODIFY_AVATAR = "modifyAvatar";
    private static final String ACTION_REMOVE_AVATAR = "removeAvatar";

    private static final String INFO_AVATAR_CREATED = "avatarserver.info.avatar.created";
    private static final String INFO_AVATAR_UPDATED = "avatarserver.info.avatar.updated";
    private static final String INFO_AVATAR_REMOVED = "avatarserver.info.avatar.removed";

    @Inject
    @Pager( listBookmark = MARK_AVATAR_LIST, defaultItemsPerPage = PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, baseUrl = JSP_MANAGE_AVATARS )
    private IPager<Avatar, Avatar> _pager;

    private Avatar _avatar;

    /**
     * Returns the Manage avatars page
     *
     * @param request
     *            The HTTP request
     * @param model
     *            The model
     * @return The page
     */
    @View( value = VIEW_MANAGE_AVATARS, defaultView = true )
    public String getManageAvatars( HttpServletRequest request, Models model )
    {
        _avatar = null;

        List<Avatar> listAvatars = new ArrayList<>( AvatarHome.getAvatarsList( ) );
        _pager.withListItem( listAvatars ).populateModels( request, model, getLocale( ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_AVATARS, TEMPLATE_MANAGE_AVATARS, model );
    }

    /**
     * Returns the form to create a avatar
     *
     * @param request
     *            The Http request
     * @param model
     *            The model
     * @return the html code of the avatar form
     */
    @View( VIEW_CREATE_AVATAR )
    public String getCreateAvatar( HttpServletRequest request, Models model )
    {
        _avatar = ( _avatar != null ) ? _avatar : new Avatar( );

        model.put( MARK_AVATAR, _avatar );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_AVATAR, TEMPLATE_CREATE_AVATAR, model );
    }

    /**
     * Process the data capture form of a new avatar
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_AVATAR )
    public String doCreateAvatar( HttpServletRequest request )
    {
        _avatar = ( _avatar != null ) ? _avatar : new Avatar( );
        populate( _avatar, request );

        MultipartItem imageSource = getImage( request );

        if ( !validateBean( _avatar, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_AVATAR );
        }

        String strError = checkImageAndEmail( imageSource, _avatar );

        if ( strError != null )
        {
            addError( strError, getLocale( ) );

            return redirectView( request, VIEW_CREATE_AVATAR );
        }

        _avatar.setValue( imageSource.get( ) );
        _avatar.setMimeType( imageSource.getContentType( ) );

        AvatarService.create( _avatar );
        _avatar = null;
        addInfo( INFO_AVATAR_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_AVATARS );
    }

    /**
     * Manages the removal form of a avatar whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @View( value = VIEW_CONFIRM_REMOVE_AVATAR, securityTokenAction = ACTION_REMOVE_AVATAR )
    public String getConfirmRemoveAvatar( HttpServletRequest request )
    {
        int nId = NumberUtils.toInt( request.getParameter( PARAMETER_ID_AVATAR ), -1 );

        if ( AvatarHome.findByPrimaryKey( nId ) == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_AVATAR_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_AVATAR ) );
        url.addParameter( PARAMETER_ID_AVATAR, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_AVATAR, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a avatar
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage avatars
     */
    @Action( ACTION_REMOVE_AVATAR )
    public String doRemoveAvatar( HttpServletRequest request )
    {
        int nId = NumberUtils.toInt( request.getParameter( PARAMETER_ID_AVATAR ), -1 );
        AvatarHome.remove( nId );
        addInfo( INFO_AVATAR_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_AVATARS );
    }

    /**
     * Returns the form to update info about a avatar
     *
     * @param request
     *            The Http request
     * @param model
     *            The model
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_AVATAR )
    public String getModifyAvatar( HttpServletRequest request, Models model )
    {
        int nId = NumberUtils.toInt( request.getParameter( PARAMETER_ID_AVATAR ), -1 );

        if ( _avatar == null || _avatar.getId( ) != nId )
        {
            _avatar = AvatarHome.findByPrimaryKey( nId );
        }

        if ( _avatar == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_AVATAR_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        model.put( MARK_AVATAR, _avatar );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_AVATAR, TEMPLATE_MODIFY_AVATAR, model );
    }

    /**
     * Process the change form of a avatar
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_AVATAR )
    public String doModifyAvatar( HttpServletRequest request )
    {
        int nId = NumberUtils.toInt( request.getParameter( PARAMETER_ID_AVATAR ), -1 );

        if ( _avatar == null || _avatar.getId( ) != nId )
        {
            _avatar = AvatarHome.findByPrimaryKey( nId );
        }

        if ( _avatar == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_AVATAR_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        populate( _avatar, request );

        MultipartItem imageSource = getImage( request );

        if ( !validateBean( _avatar, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_AVATAR, PARAMETER_ID_AVATAR, _avatar.getId( ) );
        }

        String strError = checkImageAndEmail( imageSource, _avatar );

        if ( strError != null )
        {
            addError( strError, getLocale( ) );

            return redirect( request, VIEW_MODIFY_AVATAR, PARAMETER_ID_AVATAR, _avatar.getId( ) );
        }

        _avatar.setValue( imageSource.get( ) );
        _avatar.setMimeType( imageSource.getContentType( ) );

        AvatarService.update( _avatar );
        _avatar = null;
        addInfo( INFO_AVATAR_UPDATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_AVATARS );
    }

    /**
     * Checks the uploaded image and the uniqueness of the email of an avatar
     *
     * @param imageSource
     *            The uploaded image, null when no file was sent
     * @param avatar
     *            The avatar to store
     * @return the key of the error message, or null when both are valid
     */
    private static String checkImageAndEmail( MultipartItem imageSource, Avatar avatar )
    {
        if ( imageSource == null )
        {
            return MESSAGE_MISSING_FILE;
        }

        if ( !ImageService.isImage( imageSource.get( ) ) )
        {
            return MESSAGE_INVALID_IMAGE;
        }

        Avatar existing = AvatarHome.findByHash( HashService.getHash( avatar.getEmail( ) ) );

        if ( existing != null && existing.getId( ) != avatar.getId( ) )
        {
            return MESSAGE_DUPLICATE_EMAIL;
        }

        return null;
    }

    /**
     * Returns the uploaded image of the request
     *
     * @param request
     *            The Http request
     * @return the uploaded image, or null when no file was sent
     */
    private static MultipartItem getImage( HttpServletRequest request )
    {
        if ( !( request instanceof MultipartHttpServletRequest ) )
        {
            return null;
        }

        MultipartItem imageSource = ( (MultipartHttpServletRequest) request ).getFile( PARAMETER_IMAGE );

        return ( imageSource != null && imageSource.getSize( ) > 0 ) ? imageSource : null;
    }
}

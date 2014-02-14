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
import fr.paris.lutece.plugins.avatarserver.service.AvatarService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.fileupload.FileItem;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Avatar features ( manage,
 * create, modify, remove )
 */
@Controller(controllerJsp = "ManageAvatars.jsp", controllerPath = "jsp/admin/plugins/avatarserver/", right = "AVATARSERVER_MANAGEMENT")
public class AvatarJspBean extends ManageAvatarserverJspBean
{

    ////////////////////////////////////////////////////////////////////////////
    // Constants
    // templates
    private static final String TEMPLATE_MANAGE_AVATARS = "/admin/plugins/avatarserver/manage_avatars.html";
    private static final String TEMPLATE_CREATE_AVATAR = "/admin/plugins/avatarserver/create_avatar.html";
    private static final String TEMPLATE_MODIFY_AVATAR = "/admin/plugins/avatarserver/modify_avatar.html";

    // Parameters
    private static final String PARAMETER_ID_AVATAR = "id_avatar";
    private static final String PARAMETER_IMAGE = "avatar_image";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_AVATARS = "avatarserver.manage_avatars.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_AVATAR = "avatarserver.modify_avatar.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_AVATAR = "avatarserver.create_avatar.pageTitle";

    // Markers
    private static final String MARK_AVATAR_LIST = "avatar_list";
    private static final String MARK_AVATAR = "avatar";
    private static final String JSP_MANAGE_AVATARS = "jsp/admin/plugins/avatarserver/ManageAvatars.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_AVATAR = "avatarserver.message.confirmRemoveAvatar";
    private static final String PROPERTY_DEFAULT_LIST_AVATAR_PER_PAGE = "avatarserver.listAvatars.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "avatarserver.model.entity.avatar.attribute.";

    // Views
    private static final String VIEW_MANAGE_AVATARS = "manageAvatars";
    private static final String VIEW_CREATE_AVATAR = "createAvatar";
    private static final String VIEW_MODIFY_AVATAR = "modifyAvatar";

    // Actions
    private static final String ACTION_CREATE_AVATAR = "createAvatar";
    private static final String ACTION_MODIFY_AVATAR = "modifyAvatar";
    private static final String ACTION_REMOVE_AVATAR = "removeAvatar";
    private static final String ACTION_CONFIRM_REMOVE_AVATAR = "confirmRemoveAvatar";

    // Infos
    private static final String INFO_AVATAR_CREATED = "avatarserver.info.avatar.created";
    private static final String INFO_AVATAR_UPDATED = "avatarserver.info.avatar.updated";
    private static final String INFO_AVATAR_REMOVED = "avatarserver.info.avatar.removed";

    // Session variable to store working values
    private Avatar _avatar;

    /**
     * Returns the Manage avatars page
     *
     * @param request The HTTP request
     * @return The page
     */
    @View(value = VIEW_MANAGE_AVATARS, defaultView = true)
    public String getManageAvatars(HttpServletRequest request)
    {
        _strCurrentPageIndex = Paginator.getPageIndex(request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex);
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt(PROPERTY_DEFAULT_LIST_AVATAR_PER_PAGE, 50);
        _nItemsPerPage = Paginator.getItemsPerPage(request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage);

        UrlItem url = new UrlItem(JSP_MANAGE_AVATARS);
        String strUrl = url.getUrl();
        List<Avatar> listAvatars = (List<Avatar>) AvatarHome.getAvatarsList();

        // PAGINATOR
        LocalizedPaginator paginator = new LocalizedPaginator(listAvatars, _nItemsPerPage, strUrl,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale());

        Map<String, Object> model = getModel();

        model.put(MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage);
        model.put(MARK_PAGINATOR, paginator);
        model.put(MARK_AVATAR_LIST, paginator.getPageItems());

        return getPage(PROPERTY_PAGE_TITLE_MANAGE_AVATARS, TEMPLATE_MANAGE_AVATARS, model);
    }

    /**
     * Returns the form to create a avatar
     *
     * @param request The Http request
     * @return the html code of the avatar form
     */
    @View(VIEW_CREATE_AVATAR)
    public String getCreateAvatar(HttpServletRequest request)
    {
        _avatar = (_avatar != null) ? _avatar : new Avatar();

        Map<String, Object> model = getModel();
        model.put(MARK_AVATAR, _avatar);

        return getPage(PROPERTY_PAGE_TITLE_CREATE_AVATAR, TEMPLATE_CREATE_AVATAR, model);
    }

    /**
     * Process the data capture form of a new avatar
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action(ACTION_CREATE_AVATAR)
    public String doCreateAvatar(HttpServletRequest request)
    {
        populate(_avatar, request);

        MultipartHttpServletRequest multiPartRequest = (MultipartHttpServletRequest) request;
        FileItem imageSource = multiPartRequest.getFile(PARAMETER_IMAGE);

        _avatar.setValue(imageSource.get());
        _avatar.setMimeType(imageSource.getContentType());

        // Check constraints
        if (!validateBean(_avatar, VALIDATION_ATTRIBUTES_PREFIX))
        {
            return redirectView(request, VIEW_CREATE_AVATAR);
        }

        AvatarService.create(_avatar);
        _avatar = null;
        addInfo(INFO_AVATAR_CREATED, getLocale());

        return redirectView(request, VIEW_MANAGE_AVATARS);
    }

    /**
     * Manages the removal form of a avatar whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action(ACTION_CONFIRM_REMOVE_AVATAR)
    public String getConfirmRemoveAvatar(HttpServletRequest request)
    {
        int nId = Integer.parseInt(request.getParameter(PARAMETER_ID_AVATAR));
        UrlItem url = new UrlItem(getActionUrl(ACTION_REMOVE_AVATAR));
        url.addParameter(PARAMETER_ID_AVATAR, nId);

        String strMessageUrl = AdminMessageService.getMessageUrl(request, MESSAGE_CONFIRM_REMOVE_AVATAR,
                url.getUrl(), AdminMessage.TYPE_CONFIRMATION);

        return redirect(request, strMessageUrl);
    }

    /**
     * Handles the removal form of a avatar
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage avatars
     */
    @Action(ACTION_REMOVE_AVATAR)
    public String doRemoveAvatar(HttpServletRequest request)
    {
        int nId = Integer.parseInt(request.getParameter(PARAMETER_ID_AVATAR));
        AvatarHome.remove(nId);
        addInfo(INFO_AVATAR_REMOVED, getLocale());

        return redirectView(request, VIEW_MANAGE_AVATARS);
    }

    /**
     * Returns the form to update info about a avatar
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View(VIEW_MODIFY_AVATAR)
    public String getModifyAvatar(HttpServletRequest request)
    {
        int nId = Integer.parseInt(request.getParameter(PARAMETER_ID_AVATAR));

        if (_avatar == null)
        {
            _avatar = AvatarHome.findByPrimaryKey(nId);
        }

        Map<String, Object> model = getModel();
        model.put(MARK_AVATAR, _avatar);

        return getPage(PROPERTY_PAGE_TITLE_MODIFY_AVATAR, TEMPLATE_MODIFY_AVATAR, model);
    }

    /**
     * Process the change form of a avatar
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action(ACTION_MODIFY_AVATAR)
    public String doModifyAvatar(HttpServletRequest request)
    {
        populate(_avatar, request);

        MultipartHttpServletRequest multiPartRequest = (MultipartHttpServletRequest) request;
        FileItem imageSource = multiPartRequest.getFile(PARAMETER_IMAGE);

        _avatar.setValue(imageSource.get());
        _avatar.setMimeType(imageSource.getContentType());

        // Check constraints
        if (!validateBean(_avatar, VALIDATION_ATTRIBUTES_PREFIX))
        {
            return redirect(request, VIEW_MODIFY_AVATAR, PARAMETER_ID_AVATAR, _avatar.getId());
        }

        AvatarService.update(_avatar);
        _avatar = null;
        addInfo(INFO_AVATAR_UPDATED, getLocale());

        return redirectView(request, VIEW_MANAGE_AVATARS);
    }


}

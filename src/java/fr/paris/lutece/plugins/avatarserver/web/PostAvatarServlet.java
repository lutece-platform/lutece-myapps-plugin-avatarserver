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
package fr.paris.lutece.plugins.avatarserver.web;

import fr.paris.lutece.plugins.avatarserver.business.Avatar;
import fr.paris.lutece.plugins.avatarserver.business.AvatarHome;
import fr.paris.lutece.plugins.avatarserver.service.AvatarService;
import fr.paris.lutece.plugins.avatarserver.service.HashService;
import fr.paris.lutece.plugins.avatarserver.service.HttpUtils;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.http.MultipartUtil;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;

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
	private static final String PROPERTY_AUTHORIZED_DOMAINS = "avatarserver.post_servlet.authorized_domains";
	private static final String PROPERTY_ACCESS_CONTROL_METHODS = "avatarserver.post_servlet.access_control.methods";
	private static final String PROPERTY_ACCESS_CONTROL_CREDENTIALS = "avatarserver.post_servlet.access_control.credentials";
	private static final String PROPERTY_SIZE_MAX = "avatarserver.post_servlet.size.max";
	private static final String PROPERTY_SIZE_THRESHOLD = "avatarserver.post_servlet.size.threshold";
	private static final String ACCESS_CONTROL_METHODS = AppPropertiesService.getProperty( PROPERTY_ACCESS_CONTROL_METHODS );
	private static final String ACCESS_CONTROL_CREDENTIALS = AppPropertiesService.getProperty( PROPERTY_ACCESS_CONTROL_CREDENTIALS );
	private static int _nRequestSizeMax = AppPropertiesService.getPropertyInt( PROPERTY_SIZE_MAX, -1 );
	private static int _nSizeThreshold = AppPropertiesService.getPropertyInt( PROPERTY_SIZE_THRESHOLD, -1 );
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc }
	 */
	@Override
	protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws AppException
	{

		ServletOutputStream out;
		try
		{
			out = resp.getOutputStream( );
			out.println( "Only POST is allowed" );
			out.flush( );
			out.close( );
		}
		catch (IOException e)
		{
			throw new AppException( "IOException", e );
		}


	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, AppException
	{
		try
		{
			ServletOutputStream out = response.getOutputStream( );

			try
			{
				MultipartHttpServletRequest multiPartRequest = MultipartUtil.convert( _nSizeThreshold, _nRequestSizeMax, true, request );
				FileItem imageSource = multiPartRequest.getFile( PARAMETER_IMAGE );
				String strReturnUrl = multiPartRequest.getParameter( PARAMETER_RETURN_URL );

				LuteceUser user = null;
				if ( SecurityService.isAuthenticationEnable() ) {
					user = SecurityService.getInstance( ).getRemoteUser( request );
				}
				if ( user != null )
				{
					String strEmail = user.getEmail( );
					String strHash = HashService.getHash( strEmail );
					Avatar avatar = AvatarHome.findByHash( strHash );
					boolean bCreate = false;

					String strAuthorizedDomains = AppPropertiesService.getProperty( PROPERTY_AUTHORIZED_DOMAINS );
					String strOriginDomain = HttpUtils.getHeaderOrigin( request );

					if( StringUtils.isNotEmpty( strOriginDomain ) ) {
						HttpUtils.setAccessControlHeaders( response , ACCESS_CONTROL_METHODS , strOriginDomain , ACCESS_CONTROL_CREDENTIALS );
					}

					if( StringUtils.isEmpty( strOriginDomain ) || HttpUtils.isValidDomain( strOriginDomain, strAuthorizedDomains ))
					{
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
						out.println( "Request sent from an unauthorized domain : " + strOriginDomain );
						AppLogService.info( "AvatarServer : request sent from an unauthorized domain : " + strOriginDomain );
						response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
					}
				}
				else
				{
					out.println( "User not signed!" );
					response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
				}
			}
			catch( FileUploadException ex )
			{
				out.println( "Error uploading avatar : " + ex.getMessage( ) );
				AppLogService.error( "AvatarServer : Error uploading avatar : " + ex.getMessage( ), ex );
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
			}
			catch( UserNotSignedException ex )
			{
				out.println( "Error uploading avatar : User not signed" );
				response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
			}
			finally
			{
				out.flush( );
				out.close( );
			}
		}
		catch( IOException e ) 
		{
			throw new AppException( "IOException",e );
		}
	}
}

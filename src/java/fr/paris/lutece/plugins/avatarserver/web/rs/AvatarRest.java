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
package fr.paris.lutece.plugins.avatarserver.web.rs;

import fr.paris.lutece.plugins.avatarserver.business.Avatar;
import fr.paris.lutece.plugins.avatarserver.business.AvatarHome;
import fr.paris.lutece.plugins.rest.service.RestConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

/**
 * Avatar REST service
 */
@ApplicationScoped
@Path( RestConstants.BASE_PATH + "avatarserver/" )
public class AvatarRest
{
    private static final Logger LOGGER = LogManager.getLogger( RestConstants.REST_LOGGER );

    @Inject
    @ConfigProperty( name = "avatarserver.default.avatar.url", defaultValue = "../../images/local/skin/plugins/avatarserver/avatar.jpg" )
    private String _strDefaultAvatarUrl;

    /**
     * Return the avatar for a given hash
     * 
     * @param strHash
     *            The hash
     * @return The avatar's image, or a redirection to the default avatar
     */
    @GET
    @Path( "/{hash}" )
    @Produces( "image/*" )
    public Response getAvatar( @PathParam( "hash" ) String strHash )
    {
        LOGGER.debug( "Avatar requested for hash = {}", strHash );

        Avatar avatar = AvatarHome.findByHash( strHash );

        if ( avatar != null )
        {
            return Response.ok( avatar.getValue( ), avatar.getMimeType( ) ).header( "Content-Disposition", "inline; filename=\"avatar\"" ).build( );
        }

        return Response.status( Response.Status.FOUND ).header( HttpHeaders.LOCATION, _strDefaultAvatarUrl ).build( );
    }
}

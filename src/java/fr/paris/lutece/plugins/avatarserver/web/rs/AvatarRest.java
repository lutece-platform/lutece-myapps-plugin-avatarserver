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
package fr.paris.lutece.plugins.avatarserver.web.rs;

import com.sun.jersey.api.Responses;

import fr.paris.lutece.plugins.avatarserver.business.Avatar;
import fr.paris.lutece.plugins.avatarserver.business.AvatarHome;
import fr.paris.lutece.plugins.rest.service.RestConstants;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;


/**
 *
 * @author martinlo
 */
@Path( RestConstants.BASE_PATH + "avatarserver/" )
public class AvatarRest
{
    @Context
    HttpHeaders header;
    @Context
    HttpServletResponse response;
    private Logger _logger = Logger.getLogger( RestConstants.REST_LOGGER );

    @GET
    @Path( "/{hash}" )
    @Produces( "image/*" )
    public String getPackageList( @PathParam( "hash" )
    String strHash ) throws IOException
    {
        Avatar avatar = AvatarHome.findByHash( strHash );

        if ( avatar != null )
        {
            response.setContentType( "images/jpg" );
            response.setHeader( "Content-Type", avatar.getMimeType(  ) );
            response.setHeader( "Content-Disposition", "inline; filename=\"" + "avatar" + "\"" );

            OutputStream out = response.getOutputStream(  );
            out.write( avatar.getValue(  ) );
            out.close(  );

            return "";
        }

        throw new WebApplicationException( Responses.NOT_FOUND );
    }
}

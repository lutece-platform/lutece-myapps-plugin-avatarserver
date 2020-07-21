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
package fr.paris.lutece.plugins.avatarserver.service;

import fr.paris.lutece.portal.service.util.AppLogService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * HttpUtils
 */
public class HttpUtils
{
    private static final String HEADER_ACCESS_CONTROL_METHODS = "Access-Control-Allow-Methods";
    private static final String HEADER_ACCESS_CONTROL_ORIGIN = "Access-Control-Allow-Origin";
    private static final String HEADER_ACCESS_CONTROL_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher( );

    public static final String HEADER_ORIGIN = "origin";

    /**
     * Get the origin header
     * @param request The HTTP request
     * @return the header value
     */
    public static String getHeaderOrigin( HttpServletRequest request )
    {
        return request.getHeader( HEADER_ORIGIN );
    }

    /**
     * Check if a domain is valid according a list of patterns
     * @param strDomain The domain
     * @param strValidPatterns A list of valid domain patterns separated by a comma.
     * @return true if valid
     */
    public static boolean isValidDomain( String strDomain, String strValidPatterns )
    {
        boolean bValid = false;
        
        if( !StringUtils.isEmpty( strValidPatterns ) )
        {

            String[] aAuthorizedDomains = strValidPatterns.split( "," );

            for( String strAuthorizedDomain : aAuthorizedDomains )
            {
                if ( PATH_MATCHER.match( strAuthorizedDomain.trim() , strDomain.trim() ) )
                {
                    bValid = true;
                    break;
                }
            }
        }
        if( !bValid )
        {
            AppLogService.info( "AvatarServer : request rent from an invalid domain : " + strDomain );
        }
        return bValid;
    }

    /**
     * Set access control headers of a given response object
     * @param response The response
     * @param strMethods The Methods header value
     * @param strOrigin The Origin header value
     * @param strCredentials The Credentials header value
     */
    public static void setAccessControlHeaders( HttpServletResponse response, String strMethods, String strOrigin, String strCredentials )
    {
        response.addHeader( HEADER_ACCESS_CONTROL_METHODS, strMethods );
        response.addHeader( HEADER_ACCESS_CONTROL_ORIGIN, strOrigin );
        response.addHeader( HEADER_ACCESS_CONTROL_CREDENTIALS, strCredentials );
    }

}

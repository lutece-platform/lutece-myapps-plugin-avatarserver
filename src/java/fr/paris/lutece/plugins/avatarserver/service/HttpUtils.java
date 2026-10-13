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

import java.util.Arrays;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * HTTP Utils
 */
public final class HttpUtils
{
    private static final String HEADER_ACCESS_CONTROL_METHODS = "Access-Control-Allow-Methods";
    private static final String HEADER_ACCESS_CONTROL_ORIGIN = "Access-Control-Allow-Origin";
    private static final String HEADER_ACCESS_CONTROL_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String PATH_SEPARATOR = "/";
    private static final String ANY_SEGMENTS = "**";
    private static final String ANY_SEGMENT = "*";

    public static final String HEADER_ORIGIN = "origin";

    /**
     * Private constructor
     */
    private HttpUtils( )
    {
    }

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

        if ( !StringUtils.isEmpty( strValidPatterns ) )
        {
            String [ ] aAuthorizedDomains = strValidPatterns.split( "," );

            for ( String strAuthorizedDomain : aAuthorizedDomains )
            {
                if ( matchPath( strAuthorizedDomain.trim( ), strDomain.trim( ) ) )
                {
                    bValid = true;
                    break;
                }
            }
        }
        if ( !bValid )
        {
            AppLogService.info( "AvatarServer : request rent from an invalid domain : {}", strDomain );
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

    /**
     * Matches a path against an Ant-style pattern: segments are separated by '/', '**' as a whole segment matches any number of segments, '*' matches
     * any characters within a segment and '?' one character
     * @param strPattern The pattern
     * @param strPath The path
     * @return true if the path matches the pattern
     */
    static boolean matchPath( String strPattern, String strPath )
    {
        if ( strPath.startsWith( PATH_SEPARATOR ) != strPattern.startsWith( PATH_SEPARATOR ) )
        {
            return false;
        }

        return matchSegments( tokenize( strPattern ), 0, tokenize( strPath ), 0, strPattern.endsWith( PATH_SEPARATOR ), strPath.endsWith( PATH_SEPARATOR ) );
    }

    /**
     * Splits a path into its non empty segments
     * @param strPath The path
     * @return The segments
     */
    private static String [ ] tokenize( String strPath )
    {
        return Arrays.stream( strPath.split( PATH_SEPARATOR ) ).filter( s -> !s.isEmpty( ) ).toArray( String [ ]::new );
    }

    /**
     * Matches the pattern segments from a position against the path segments from a position
     * @param patternSegments The pattern segments
     * @param nPattern The position in the pattern segments
     * @param pathSegments The path segments
     * @param nPath The position in the path segments
     * @param bPatternTrailing true if the pattern ends with a separator
     * @param bPathTrailing true if the path ends with a separator
     * @return true if the remaining segments match
     */
    private static boolean matchSegments( String [ ] patternSegments, int nPattern, String [ ] pathSegments, int nPath, boolean bPatternTrailing,
            boolean bPathTrailing )
    {
        if ( nPattern == patternSegments.length )
        {
            return nPath == pathSegments.length
                    && ( bPatternTrailing == bPathTrailing || ( nPattern > 0 && ANY_SEGMENTS.equals( patternSegments [nPattern - 1] ) ) );
        }

        if ( nPath == pathSegments.length && nPattern == patternSegments.length - 1 && ANY_SEGMENT.equals( patternSegments [nPattern] ) && bPathTrailing
                && Arrays.stream( patternSegments, 0, nPattern ).noneMatch( ANY_SEGMENTS::equals ) )
        {
            return true;
        }

        if ( ANY_SEGMENTS.equals( patternSegments [nPattern] ) )
        {
            for ( int i = nPath; i <= pathSegments.length; i++ )
            {
                if ( matchSegments( patternSegments, nPattern + 1, pathSegments, i, bPatternTrailing, bPathTrailing ) )
                {
                    return true;
                }
            }

            return false;
        }

        return nPath < pathSegments.length && matchSegment( patternSegments [nPattern], pathSegments [nPath] )
                && matchSegments( patternSegments, nPattern + 1, pathSegments, nPath + 1, bPatternTrailing, bPathTrailing );
    }

    /**
     * Matches one segment against a pattern segment with '*' and '?' wildcards
     * @param strPattern The pattern segment
     * @param strSegment The path segment
     * @return true if the segment matches
     */
    private static boolean matchSegment( String strPattern, String strSegment )
    {
        StringBuilder sbRegex = new StringBuilder( );
        StringBuilder sbLiteral = new StringBuilder( );

        for ( char c : strPattern.toCharArray( ) )
        {
            if ( c == '*' || c == '?' )
            {
                if ( sbLiteral.length( ) > 0 )
                {
                    sbRegex.append( Pattern.quote( sbLiteral.toString( ) ) );
                    sbLiteral.setLength( 0 );
                }
                sbRegex.append( c == '*' ? ".*" : "." );
            }
            else
            {
                sbLiteral.append( c );
            }
        }

        if ( sbLiteral.length( ) > 0 )
        {
            sbRegex.append( Pattern.quote( sbLiteral.toString( ) ) );
        }

        return Pattern.compile( sbRegex.toString( ), Pattern.DOTALL ).matcher( strSegment ).matches( );
    }
}

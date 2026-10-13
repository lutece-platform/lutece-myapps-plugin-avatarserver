/*
 * Copyright (c) 2002-2026, Mairie de Paris
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests of the authorized domain matching
 */
public class HttpUtilsTest
{
    private static final String DEFAULT_PATTERNS = "http*://**localhost**,http*://**paris.mdp,http*://**paris.fr";

    /**
     * Tests the origins accepted by the default patterns
     */
    @Test
    public void testAuthorizedOrigins( )
    {
        assertTrue( HttpUtils.isValidDomain( "http://localhost:8080", DEFAULT_PATTERNS ) );
        assertTrue( HttpUtils.isValidDomain( "https://www.paris.fr", DEFAULT_PATTERNS ) );
        assertTrue( HttpUtils.isValidDomain( "https://paris.fr", DEFAULT_PATTERNS ) );
        assertTrue( HttpUtils.isValidDomain( "http://a.b.paris.mdp", DEFAULT_PATTERNS ) );
    }

    /**
     * Tests the origins refused by the default patterns
     */
    @Test
    public void testRefusedOrigins( )
    {
        assertFalse( HttpUtils.isValidDomain( "http://evil.com", DEFAULT_PATTERNS ) );
        assertFalse( HttpUtils.isValidDomain( "https://paris.fr.evil.com", DEFAULT_PATTERNS ) );
        assertFalse( HttpUtils.isValidDomain( "null", DEFAULT_PATTERNS ) );
        assertFalse( HttpUtils.isValidDomain( "https://www.paris.fr", "" ) );
    }

    /**
     * Tests the Ant-style segment rules
     */
    @Test
    public void testSegments( )
    {
        assertTrue( HttpUtils.matchPath( "http://*.a.com", "http://x.y.a.com" ) );
        assertTrue( HttpUtils.matchPath( "http:/**/z", "http://x/y/z" ) );
        assertTrue( HttpUtils.matchPath( "/x/**", "/x/" ) );
        assertTrue( HttpUtils.matchPath( "/x/*", "/x/" ) );
        assertFalse( HttpUtils.matchPath( "http*://**paris.fr", "https://x.paris.fr/" ) );
        assertFalse( HttpUtils.matchPath( "/x/**/*", "/x/" ) );
    }
}

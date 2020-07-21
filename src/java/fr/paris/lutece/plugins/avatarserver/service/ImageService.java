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
package fr.paris.lutece.plugins.avatarserver.service;

import fr.paris.lutece.portal.service.util.AppLogService;

import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.awt.Color;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

/**
 * Image Service
 */
public final class ImageService
{
    /** Parameter JPG */
    private static final String PARAMETER_JPG = "jpg";

    /** Private constructor */
    private ImageService( )
    {
    }

    /**
     * Resize an image with the default quality
     * 
     * @param byteArray
     *            the original byte array
     * @param width
     *            the new width
     * @return the resize byte array
     */
    public static byte [ ] resizeImage( byte [ ] byteArray, int width )
    {
        return resizeImage( byteArray, width, AvatarService.getQuality( ) );
    }

    /**
     * Resize an image
     * 
     * @param byteArray
     *            the original byte array
     * @param width
     *            the new width
     * @param quality
     *            the quality between 0.0 and 1.0
     * @return the resize byte array
     */
    public static byte [ ] resizeImage( byte [ ] byteArray, int width, float quality )
    {
        try
        {
            // Crop image if needed
            ByteArrayInputStream in = new ByteArrayInputStream( byteArray );
            ByteArrayOutputStream out = new ByteArrayOutputStream( );
            BufferedImage image = ImageIO.read( in );

            // Replace transparent background with white and drop alpha channel
            // Otherwise, the rest of the code would swap channels and this would lead to a red tint on images
            if ( image.getColorModel( ).hasAlpha( ) )
            {
                BufferedImage newImage = new BufferedImage( image.getWidth( ), image.getHeight( ), BufferedImage.TYPE_INT_RGB );
                newImage.createGraphics( ).drawImage( image, 0, 0, Color.WHITE, null );
                image = newImage;
            }

            BufferedImage resizedImage;
            resizedImage = Scalr.resize( image, Scalr.Mode.FIT_TO_WIDTH, width );

            // Boilerplate to be able to set the quality
            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName( PARAMETER_JPG ).next( );
            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam( );
            jpgWriteParam.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
            jpgWriteParam.setCompressionQuality( quality );
            jpgWriter.setOutput( new MemoryCacheImageOutputStream( out ) );
            IIOImage outputImage = new IIOImage( resizedImage, null, null );
            jpgWriter.write( null, outputImage, jpgWriteParam );
            jpgWriter.dispose( );
            out.flush( );

            return out.toByteArray( );
        }
        catch( IOException ex )
        {
            AppLogService.error( "Avatar - Image Service - Error resizing image : " + ex.getMessage( ), ex );
        }

        return byteArray;
    }
}

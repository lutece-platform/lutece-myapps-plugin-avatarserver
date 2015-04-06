/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.avatarserver.web;

import fr.paris.lutece.plugins.avatarserver.business.Avatar;
import fr.paris.lutece.plugins.avatarserver.business.AvatarHome;
import fr.paris.lutece.plugins.avatarserver.service.AvatarService;
import fr.paris.lutece.plugins.avatarserver.service.HashService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.http.MultipartUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

/**
 * PostAvatar Servlet
 * 
 */
public class PostAvatarServlet extends HttpServlet
{
    private static final String PARAMETER_IMAGE = "image";
    private static final String PARAMETER_EMAIL = "email";
    private static final String PARAMETER_RETURN_URL = "return_url";
    
    private static int _nRequestSizeMax = 200000;
    private static int _nSizeThreshold = -1;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        ServletOutputStream out = resp.getOutputStream();
        out.println("Only POST is allowed");
        out.flush();
        out.close();
    }
   
    

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        ServletOutputStream out = response.getOutputStream();
        try
        {
            MultipartHttpServletRequest multiPartRequest = MultipartUtil.convert( _nSizeThreshold, _nRequestSizeMax, true , request );
            FileItem imageSource = multiPartRequest.getFile( PARAMETER_IMAGE );
            String strEmail = multiPartRequest.getParameter( PARAMETER_EMAIL );
            String strHash = HashService.getHash( strEmail );
            String strReturnUrl = multiPartRequest.getParameter( PARAMETER_RETURN_URL );
            
            
            Avatar avatar = AvatarHome.findByHash( strHash );
            boolean bCreate = false;
            if( avatar == null )
            {
                avatar = new Avatar();
                bCreate = true;
            }
            avatar.setEmail( strEmail );
            avatar.setHash( strHash );
            avatar.setValue( imageSource.get(  ) );
            avatar.setMimeType( imageSource.getContentType(  ) );
            
            if( bCreate )
            {
                AvatarService.create( avatar );
            }
            else
            {
                AvatarService.update( avatar );
            }
            out.println("Avatar successfully posted!");
            response.sendRedirect( strReturnUrl );
        }
        catch (FileUploadException ex)
        {
            out.println("Error uploading avatar : " + ex.getMessage() );
            AppLogService.error( "Error uploading avatar : " + ex.getMessage() , ex );
        }
        finally
        {
            out.flush();
            out.close();
        }
    }
  
}

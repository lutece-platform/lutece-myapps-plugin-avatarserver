<%@ page errorPage="../../ErrorPagePortal.jsp" %>

<jsp:useBean id="avatarserver" scope="session" class="fr.paris.lutece.plugins.avatarserver.web.AvatarJspBean" />

<%
    response.sendRedirect( avatarserver.doUpdateAvatar( request ) );
%>


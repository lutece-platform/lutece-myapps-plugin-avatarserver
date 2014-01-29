<jsp:useBean id="manageavatarserverAvatar" scope="session" class="fr.paris.lutece.plugins.avatarserver.web.AvatarJspBean" />
<% String strContent = manageavatarserverAvatar.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>

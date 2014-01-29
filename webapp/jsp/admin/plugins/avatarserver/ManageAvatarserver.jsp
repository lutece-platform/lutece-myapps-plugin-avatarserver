<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="manageavatarserver" scope="session" class="fr.paris.lutece.plugins.avatarserver.web.ManageAvatarserverJspBean" />

<% manageavatarserver.init( request, manageavatarserver.RIGHT_MANAGEAVATARSERVER ); %>
<%= manageavatarserver.getManageAvatarserverHome ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>

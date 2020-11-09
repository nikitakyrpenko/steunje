<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<table class="admNavPanel" cellspacing="0" cellpadding="0" align="center" border="0">
	<tr> 
		<td class="admNamebar" width="33">&#160;</td>
		<td class="admNamebar" width="*">
    		NAME
		</td>
		<td class="admNamebar" width="20%">
			PROPERTY 1
		</td>
		<td class="admNamebar" width="20%">
			PROPERTY 2
		</td>
		<td class="admNamebar" width="20%">
			PROPERTY 3
		</td>
		<td class="admNamebar" colspan="2" width="66">
			ACTION
		</td>
	</tr>
 <c:forEach var="order" items="${navigator.chunk}" varStatus="status"> 
	<tr>
		<td class="admWhiteTD">
			<!-- icon image --><img class="admCenter" src="images/media_catalog/folder.gif"/>
		</td>
		<td class="admMainTD">
			<a class="admAnchor">
				Name
			</a>
		</td>
		<td class="admLightTD">Property 1</td>
		<td class="admDarkTD">Property 2</td>
		<td class="admLightTD">Property 3</td>
		<td class="admDarkTD">
			<a href=""><img src="/images/docFile.gif" border="0" alt="" /></a>
		</td>
		<td class="admLightTD">
			<a href=""><img src="/images/docFile.gif" border="0" alt="" /></a>
		</td>
	</tr>
</c:forEach> 
</table>
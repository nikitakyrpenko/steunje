<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<div class="admCenter">
    	<table cellspacing="0" cellpadding="0" border="0">
            <tr>
    	 	<td class="admBackLogo">
				<form action="login.html" method="post" enctype="application/x-www-form-urlencoded" onsubmit="return validateLoginForm(this);">
    			<table cellspacing="0" cellpadding="0" border="0">
<!-- POSSIBLE ERRORS OUTPUT - Start -->
		<c:choose>
			<c:when test="${requestScope.status == 'login_not_found'}">
					<tr>
    				      <td colspan="3" class="admCenter admErrorMessage"><fmt:message key="login.error" /></td>
    			    </tr>
			</c:when>        
			<c:when test="${requestScope.status == 'reseller_profile_not_found'}">
					<tr>
    				      <td colspan="3" class="admCenter admErrorMessage"><fmt:message key="login.profileError" /></td>
    			    </tr>
			</c:when>                
			<c:when test="${requestScope.status == 'user_not_found_in_SiS'}">
					<tr>
    				      <td colspan="3" class="admCenter admErrorMessage"><fmt:message key="login.remoteError" /></td>
    			    </tr>
			</c:when>                
		</c:choose>
    			    <tr>
    				      <td colspan="3" class="admCenter admErrorMessage">&#160;</td>
    			    </tr>
<!-- POSSIBLE ERRORS OUTPUT - Finish -->
    			    <tr>
    				      <td class="admNavbarImg admBottomLogin"><img  height="25" width="10" alt="" src="/images/navBarLeft.gif"/></td>
    				      <td class="admNavbar admBottomLogin"><label for="login">&#160;<fmt:message key="field.Login"/>&#160;:&#160;&#160;</label></td>
    				      <td class="admBottomLogin">
    				          <input class="admTextArea admWidth100" type="text" name="login" id="login"  maxlength="20" value="<c:out value="${requestScope.login}"/>"/>
    				      </td>
    			    </tr>
    			    <tr>
    			      <td class="admNavbarImg admBottomLogin"><img  height="25" width="10" alt="" src="/images/navBarLeft.gif"/></td>
    			      <td class="admNavbar admBottomLogin"><label for="pwd">&#160;<fmt:message key="field.Password"/>&#160;:&#160;&#160;</label></td>
    			      <td class="admBottomLogin">
							<input class="admTextArea admWidth100" name="pwd" id="pwd"   type="password" maxlength="20" redisplay="false"/>
							<input type="hidden" name="password" value=""/>
    			      </td>
    			    </tr>
					<tr>
    			      <td class="admNavbarImg admBottomLogin"><img  height="25" width="10" alt="" src="/images/navBarLeft.gif"/></td>
    			      <td class="admNavbar admBottomLogin">&#160;Interface in&#160;:&#160;&#160;</td>
    			      <td class="admBottomLogin">
						<select class="interfaceLanguage" name="interfaceLanguage" id="interface" onchange='getLang();'>
		                    <option value="en"><fmt:message key="field.English"/></option>   
        	            	<option value="nl"><fmt:message key="field.Dutch"/></option>   
	            	        <option value="fr"><fmt:message key="field.French"/></option>
    	            	    <option value="de"><fmt:message key="field.German"/></option>
        	            	<option value="es"><fmt:message key="field.Spanish"/></option>
            		        <option value="it"><fmt:message key="field.Italian"/></option>
							<option value="pt"><fmt:message key="field.Portuguese"/></option>   
		                    <option value="pl"><fmt:message key="field.Polish"/></option>                         
        	            </select>
            	        <script> document.all["interfaceLanguage"].value = "<c:out value='${resolvedInterfaceLanguage}'/>"; </script>
					  </td>
					</tr>
    				<tr>
    				  <td class="admNavbarImg"><img  height="25" width="10" alt="" src="/images/navBarLeft.gif"/></td>
    			      <td class="admNavbar admBackRight admRight" colspan="2" >
						<input type="submit"  class="admNavbarInp" name="submit" value="<<fmt:message key="field.Submit"/>>"/>
    			      </td>
    				</tr>
	 			</table>
				</form>
    		</td>
    	    </tr>
			<tr>
    	 		<td class="admCenter admCopyright">
					<!-- COPYRIGHT INFO - Start -->
					<%@ include file="template_copyright_char.html" %>
					<!-- COPYRIGHT INFO - Finish -->
				</td>
    	    </tr>
      </table>
</div>
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java">

	<xsl:template match="negeso:wizard-page[@id='REDIRECT_TO_ISSUER']">
		<script>
			window.location.href='<xsl:value-of select="@issuerAuthenticationURL"/>';
		</script>
	</xsl:template>
		
	<xsl:template match="negeso:wizard-page[@id='CHOOSE_BANK_PAGE']">
	<xsl:choose>
        <xsl:when  test="not(//negeso:errors)">            
		   <xsl:apply-templates select="//negeso:article"/>
		    <div class="pmContent pmContentFirst">
		        <xsl:apply-templates select="." mode="cart_items"/>
		    </div>
				<select name="issuerId" id="bankChooser">
			            <option value="0">
			                <xsl:call-template name="add-constant-info">
			                    <xsl:with-param name="dict" select="$dict_pm_module" />
			                    <xsl:with-param name="name" select="'PM_CHOOSE_YOUR_BANK'" />
			                </xsl:call-template>
			                ----
			                <xsl:value-of
			                    select="java:getString($dict_pm_module, 'PM_CHOOSE_YOUR_BANK')" />
			                ----
			            </option>
			            <xsl:for-each select="negeso:issuers/negeso:issuer">
				            <option value="{@id}">
								<xsl:choose>
									<xsl:when test="@id='0'">
										<xsl:call-template name="add-constant-info">
											<xsl:with-param name="dict" select="$dict_pm_module" />
											<xsl:with-param name="name" select="'PM_OTHER_BANK'" />
										</xsl:call-template>
										--------
										<xsl:value-of select="java:getString($dict_pm_module, 'PM_OTHER_BANK')" />
										--------
									</xsl:when>
									<xsl:otherwise>
									    <xsl:value-of select="@name" />
									</xsl:otherwise>
								</xsl:choose>
				            </option>
				        </xsl:for-each>
				</select>
			<br/><br/><br/>
	       <div >
	       </div>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="negeso:errors"/>
            <xsl:call-template name="pm_shopping_cart_links"/>
        </xsl:otherwise>
    </xsl:choose>
	</xsl:template>
</xsl:stylesheet>
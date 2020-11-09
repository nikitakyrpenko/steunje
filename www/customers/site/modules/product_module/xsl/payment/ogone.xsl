<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java">

	<xsl:template match="negeso:wizard-page[@id='OGONE_REDIRECT']">
		<script type="text/javascript">
			$(document).ready(function(){ 
				document.wizard_form.action = '<xsl:value-of select="@ogone_url" />';
				document.wizard_form.submit();
			});
		</script>
		<input type="hidden" name="PSPID">
			<xsl:attribute name="value">
					<xsl:value-of select="@pspid" />
				</xsl:attribute>
		</input>
		<input type="hidden" name="orderID">
			<xsl:attribute name="value">
					<xsl:value-of select="@order_id" />
				</xsl:attribute>
		</input>
		<input type="hidden" name="amount">
			<xsl:attribute name="value">
					<xsl:value-of select="@amount" />
				</xsl:attribute>
		</input>
		<input type="hidden" name="currency">
			<xsl:attribute name="value">
					<xsl:value-of select="@currency" />
				</xsl:attribute>
		</input>
		<input type="hidden" name="language">
			<xsl:attribute name="value">
					<xsl:value-of select="@language" />
				</xsl:attribute>
		</input>
		<input type="hidden" name="SHASign">
			<xsl:attribute name="value">
					<xsl:value-of select="@sha_sign" />
				</xsl:attribute>
		</input>
		<input type="submit" id="submit1" name="submit1" />
	</xsl:template>

</xsl:stylesheet>
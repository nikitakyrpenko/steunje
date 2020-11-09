<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">


    <xsl:include href="/xsl/negeso_body.xsl"/>
    <xsl:include href="/customers/site/xsl/site.xsl"/>
    
    <xsl:template match="/negeso:page">
        <xsl:apply-templates select="//negeso:page" mode="adminLogin">
            <xsl:with-param name="loginMode" select="'admin'"/>
        </xsl:apply-templates>
    </xsl:template>

</xsl:stylesheet>

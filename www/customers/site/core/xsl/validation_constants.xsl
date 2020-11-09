<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL common functions for Site Core
 
  @version		2008.01.02
  @author		Rostislav 'KOTT' Brizgunov
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java"
>


  <xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
  <xsl:template name="validationConstants">
    <script  type="text/javascript">
      var fmConsts = {};
      fmConsts.FINAL_REQUIER = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.FINAL_REQUIER')"/>';
      fmConsts.FINAL_EMAIL = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.FINAL_EMAIL')"/>';
      fmConsts.NUMERIC = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.NUMERIC')"/>';
      fmConsts.TIME_DATE = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.TIME_DATE')"/>';
      fmConsts.PHONE = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.PHONE')"/>';
      fmConsts.LOCAL = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.LOCAL')"/>';
      fmConsts.LOCAL_EMAIL = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.LOCAL_EMAIL')"/>';
      fmConsts.NUMERIC_0 = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.NUMERIC_0')"/>';
      fmConsts.NUMERIC_1 = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.NUMERIC_1')"/>';
      fmConsts.NUMERIC_2 = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.NUMERIC_2')"/>';
      fmConsts.NUMERIC_3 = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.NUMERIC_3')"/>';
      fmConsts.NUMERIC_4 = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.NUMERIC_4')"/>';
      fmConsts.NUMERIC_5 = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.NUMERIC_5')"/>';
      fmConsts.LOCAL_TIME_DATE = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.LOCAL_TIME_DATE')"/>';
      fmConsts.LOCAL_PHONE = '<xsl:value-of select="java:getString($dict_common, 'VALIDATION.LOCAL_PHONE')"/>';

    </script>
  </xsl:template>


</xsl:stylesheet>
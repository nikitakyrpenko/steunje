<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                exclude-result-prefixes="fo">
    <xsl:template match="order">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="my-page" page-height="29.7cm" page-width="21cm" margin-top="0cm" margin-bottom="0cm" margin-left="1cm" margin-right="1cm">
                    <fo:region-body margin-top="3cm" margin-bottom="3cm"/>
                    <fo:region-before region-name="first-page-header" extent="11in" display-align="before" />
                    <fo:region-after region-name="first-page-footer" extent="11in" display-align="after" />
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="my-page">
                <fo:static-content flow-name="first-page-header">
                    <fo:block margin-bottom="3cm"><fo:external-graphic src='url(./logo.png)'/></fo:block>
                </fo:static-content>
                <fo:static-content flow-name="first-page-footer">
                    <fo:block padding-top="0.3cm" border-top-color="black" border-top-style="solid" border-top-width="medium" font-family="Helvetica" font-size="3mm" text-align="center">Baten Trading Company, Steenoven 48, 5626 DK Eindhoven, NL.</fo:block>
                    <fo:block margin-bottom="0.3cm"  font-family="Helvetica" font-size="3mm" text-align="center">T:040-2621307 @:info@batentrading.nl</fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block margin-bottom="1cm">
                        <fo:table>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Factuuradres</fo:block>
                                        <fo:block font-family="Helvetica" font-size="4mm"><xsl:value-of select="./customer/@login" /></fo:block>
                                        <fo:block font-family="Helvetica" font-size="4mm"><xsl:value-of select="./customer/@userName" /></fo:block>
                                        <fo:block font-family="Helvetica" font-size="4mm"><xsl:value-of select="./shipping/@addressLine" /></fo:block>
                                        <fo:block font-family="Helvetica" font-size="4mm"><xsl:value-of select="./shipping/@zipCode" /></fo:block>
                                        <fo:block font-family="Helvetica" font-size="4mm"><xsl:value-of select="./shipping/@city" />, <xsl:value-of select="./shipping/@address" /></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Afleveradres</fo:block>
                                        <fo:block font-family="Helvetica" font-size="4mm"><xsl:value-of select="./customer/@login" /></fo:block>
                                        <fo:block font-family="Helvetica" font-size="4mm"><xsl:value-of select="./customer/@userName" /></fo:block>
                                        <fo:block font-family="Helvetica" font-size="4mm"><xsl:value-of select="./billing/@addressLine" /></fo:block>
                                        <fo:block font-family="Helvetica" font-size="4mm"><xsl:value-of select="./billing/@zipCode" /></fo:block>
                                        <fo:block font-family="Helvetica" font-size="4mm"><xsl:value-of select="./billing/@city" />, <xsl:value-of select="./billing/@address" /></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                    <fo:block  margin-bottom="1cm">
                        <fo:table>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Afleverconditie: <fo:inline font-weight="normal"><xsl:value-of select="@deliveryType" /></fo:inline></fo:block>
                                        <fo:block font-weight="bold" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Orderdatum: <fo:inline font-weight="normal"><xsl:value-of select="@orderDate" /></fo:inline></fo:block>
                                        <fo:block font-weight="bold" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Transaction id: <fo:inline font-weight="normal"><xsl:value-of select="@transactionId" /></fo:inline></fo:block>
                                        <fo:block font-weight="bold" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Besteld door: <fo:inline font-weight="normal"><xsl:value-of select="./customer/@email" /></fo:inline></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                    <fo:table>
                        <fo:table-column column-number="1" column-width="14%"/>
                        <fo:table-column column-number="2" column-width="38%"/>
                        <fo:table-column column-number="3" column-width="16%"/>
                        <fo:table-column column-number="4" column-width="10%"/>
                        <fo:table-column column-number="5" column-width="10%"/>
                        <fo:table-column column-number="6" column-width="12%"/>
                        <fo:table-header>
                            <fo:table-row border-bottom-color="black" border-bottom-style="solid" border-bottom-width="medium">
                                <fo:table-cell>
                                    <fo:block font-weight="bold" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Productcode</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-weight="bold" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Omschrijving</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-weight="bold" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Prijs excl.BTW</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-weight="bold" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Korting</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-weight="bold" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Aantal</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-weight="bold" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Bedrag</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body>
                            <xsl:for-each select="./item">
                                <fo:table-row margin-bottom="0.3cm" border-bottom-color="black" border-bottom-style="solid" border-bottom-width="medium">
                                    <fo:table-cell>
                                        <fo:block margin-top="0.3cm" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">
                                            <xsl:value-of select="@id" />
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block margin-top="0.3cm" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">
                                            <xsl:value-of select="@title" />
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block margin-top="0.3cm" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">
                                            <xsl:value-of select="@price" />
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block margin-top="0.3cm" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">
                                            <xsl:value-of select="@discount" />
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block margin-top="0.3cm" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">
                                            <xsl:value-of select="@quantity" />
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block margin-top="0.3cm" margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">
                                            <xsl:value-of select="@total" />
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                    <fo:block margin-top="0.3cm" font-weight="bold" font-family="Helvetica" font-size="4mm">Toevoeging/Opmerking:</fo:block>
                    <fo:block border-bottom-color="black" border-bottom-style="solid" border-bottom-width="medium" margin-top="0.3cm" padding-bottom="0.3cm" font-family="Helvetica" font-size="4mm"><xsl:value-of select="@comment" /></fo:block>
                    <fo:block font-style="italic" margin-bottom="1cm" margin-top="0.3cm" font-weight="bold" font-family="Helvetica" font-size="4mm">Alle getoonde prijzen zijn excl. BTW</fo:block>
                    <fo:table>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block margin-bottom="0.3cm" font-weight="bold" font-family="Helvetica" font-size="4mm">Info</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block margin-bottom="0.3cm" font-weight="bold" font-family="Helvetica" font-size="4mm">Berekening</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block margin-bottom="0.3cm" margin-right="0.4cm" font-family="Helvetica" font-size="4mm">Uw persoonlijke gegevens worden vertrouwelijk behandeld en zullen niet verder worden verwerkt op een wijze die onverenigbaar is  met het doel waarvoor ze zijn  verkregen Uw persoonlijke gegevens worden uitsluitend aan derden verstrekt nadat u hiervoor uw
                                        nadrukkelijke toestemming hebt gegeven. Prijs is ter indicatie. Uiteindelijke prijs kan afwijken van de order i.v.m. backorders</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:table>
                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell>
                                                    <fo:block margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Subtotaal excl. BTW:</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell>
                                                    <fo:block margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm" text-align="right"><xsl:value-of select="@total" /></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                            <fo:table-row>
                                                <fo:table-cell>
                                                    <fo:block margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Franco:</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell>
                                                    <fo:block margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm" text-align="right"><xsl:value-of select="@deliveryCost" /></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                            <fo:table-row>
                                                <fo:table-cell>
                                                    <fo:block margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm">BTW:</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell>
                                                    <fo:block margin-bottom="0.3cm" font-family="Helvetica" font-size="4mm" text-align="right"><xsl:value-of select="@VAT" /></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                            <fo:table-row>
                                                <fo:table-cell>
                                                    <fo:block border-bottom-color="black" border-bottom-style="solid" border-bottom-width="medium" margin-bottom="0.3cm" padding-bottom="0.3cm" font-family="Helvetica" font-size="4mm">Subtotaal incl. BTW:</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell>
                                                    <fo:block border-bottom-color="black" border-bottom-style="solid" border-bottom-width="medium" margin-bottom="0.3cm" padding-bottom="0.3cm" font-family="Helvetica" font-size="4mm" text-align="right"><xsl:value-of select="@totalWithVAT" /></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                            <fo:table-row>
                                                <fo:table-cell>
                                                    <fo:block margin-bottom="0.3cm" font-weight="bold"  font-family="Helvetica" font-size="4mm">Totaal incl. BTW:</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell>
                                                    <fo:block margin-bottom="0.3cm" font-weight="bold"  font-family="Helvetica" font-size="4mm" text-align="right"><xsl:value-of select="@totalWithVAT" /></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>
                                    </fo:table>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:output encoding="UTF-8" indent="yes" method="xml" standalone="no" omit-xml-declaration="no"/>
    <xsl:template match="invoice">
        <fo:root language="EN">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4-portrail" page-height="297mm" page-width="210mm" margin-top="5mm" margin-bottom="5mm" margin-left="5mm" margin-right="5mm">
                    <fo:region-body margin-top="65mm" margin-bottom="20mm"/>
                    <fo:region-before region-name="xsl-region-before" extent="25mm" display-align="before" precedence="true"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="A4-portrail">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:table table-layout="fixed" width="100%" font-size="10pt" border-color="black" border-width="0.4mm" border-style="solid">
                        <fo:table-column column-width="proportional-column-width(20)"/>
                        <fo:table-column column-width="proportional-column-width(45)"/>
                        <fo:table-column column-width="proportional-column-width(20)"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        Invoice Id: <xsl:value-of select="@id"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" display-align="center">
                                    <fo:block font-size="150%">
                                        INVOICE
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        Date: <xsl:value-of select="date"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                    <fo:block>
                         &#160;
                    </fo:block>
                    <fo:table table-layout="fixed" width="100%" font-size="10pt" border-color="black" border-width="0.4mm" border-style="solid">
                        <fo:table-column column-width="proportional-column-width(40)"/>
                        <fo:table-column column-width="proportional-column-width(20)"/>
                        <fo:table-column column-width="proportional-column-width(40)"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="10mm">
                                    <fo:block>
                                        Invoice to:
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" display-align="center">
                                    <fo:block/>
                                </fo:table-cell>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        Invoice from:
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="10mm">
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" display-align="center">
                                    <fo:block>
                                        &#160;
                                    </fo:block> 
                                </fo:table-cell>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="10mm">
                                    <fo:block>
                                        Name: <xsl:value-of select="invoice-to/name"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" display-align="center">
                                    <fo:block/>
                                </fo:table-cell>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        Name: <xsl:value-of select="invoice-from/name"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="10mm">
                                    <fo:block>
                                        Address: <xsl:value-of select="invoice-to/address-line"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" display-align="center">
                                    <fo:block/>
                                </fo:table-cell>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        Address: <xsl:value-of select="invoice-from/address-line"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="10mm">
                                    <fo:block>
                                        City: <xsl:value-of select="invoice-to/city"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" display-align="center">
                                    <fo:block/>
                                </fo:table-cell>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        City: <xsl:value-of select="invoice-from/city"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="10mm">
                                    <fo:block>
                                        Postalcode: <xsl:value-of select="invoice-to/postalcode"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" display-align="center">
                                    <fo:block/>
                                </fo:table-cell>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        Postalcode: <xsl:value-of select="invoice-from/postalcode"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="10mm">
                                    <fo:block>
                                        Country: Spain
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" display-align="center">
                                    <fo:block/>
                                </fo:table-cell>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        Country: Spain
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="10mm">
                                    <fo:block/>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" display-align="center">
                                    <fo:block/>
                                </fo:table-cell>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        Nif: <xsl:value-of select="invoice-from/nif"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="10mm">
                                    <fo:block>
                                        Email: <xsl:value-of select="invoice-to/email"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" display-align="center">
                                    <fo:block/>
                                </fo:table-cell>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        Email: <xsl:value-of select="invoice-from/email"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell text-align="left" display-align="center" padding-left="10mm">
                                    <fo:block>
                                        Telephone: <xsl:value-of select="invoice-to/telephone"/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="center" display-align="center">
                                    <fo:block/>
                                </fo:table-cell>
                                <fo:table-cell text-align="left" display-align="center" padding-left="2mm">
                                    <fo:block>
                                        Telephone: <xsl:value-of select="invoice-from/telephone"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body" border-collapse="collapse" reference-orientation="0">
                    <fo:block-container>
                            <fo:block text-align="center">RENT MY STUFF</fo:block>
                            <fo:block text-align="right">from: <xsl:value-of select="order/dates/start-rent-day"/>&#160; to <xsl:value-of select="order/dates/end-rent-day"/></fo:block>
                    </fo:block-container>
                    <fo:table table-layout="fixed" width="100%" font-size="10pt" border-color="black" border-width="0.35mm" border-style="solid" text-align="center" display-align="center" space-after="5mm">
                        <fo:table-column column-width="proportional-column-width(12)"/>
                        <fo:table-column column-width="proportional-column-width(40)"/>
                        <fo:table-column column-width="proportional-column-width(5)"/>
                        <fo:table-column column-width="proportional-column-width(22)"/>
                        <fo:table-column column-width="proportional-column-width(24)"/>
                        <fo:table-column column-width="proportional-column-width(20)"/>
                        <fo:table-column column-width="proportional-column-width(25)"/>
                        <fo:table-column column-width="proportional-column-width(25)"/>
                        <fo:table-footer>
                            <fo:table-row height="12mm">
                                <fo:table-cell>
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        Taxes applied: 
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                       <xsl:value-of select="taxes-applied"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row height="12mm">
                                <fo:table-cell>
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        &#160;
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        Total Price: 
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                       <xsl:value-of select="total-invoice-price"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-footer>
                        <fo:table-body font-size="95%">
                            <fo:table-row height="8mm">
                                <fo:table-cell>
                                    <fo:block>Id</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Product</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Days</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Price per day</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Discount per days</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Unit Price</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Discount</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Total Price</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <xsl:for-each select="order/product">
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="id"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="product-name"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="days"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="price_per_day"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="discount_per_days"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="unit-price"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="discount"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="total-price"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                        
                    </fo:table>
                    
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
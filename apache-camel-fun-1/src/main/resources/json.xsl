<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet
        version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
    <xsl:output indent="no" omit-xml-declaration="yes" method="text" encoding="utf-8"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/UserQuote">
        <xsl:text>{ "id" : </xsl:text>
        <xsl:value-of select="@Id"/>
        <xsl:text>, "user" : "</xsl:text>
        <xsl:value-of select="@Name"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="@Surname"/>
        <xsl:text>", "quote" : "</xsl:text>
        <xsl:value-of select="Quote"/>
        <xsl:text>" }</xsl:text>
    </xsl:template>
</xsl:stylesheet>
<?xml version="1.0" encoding="utf-8" ?> 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>
    <xsl:template match="rss/channel">
		<div class="csep">
			<div class="ctitle"><a href="{link}" target="_blank"><xsl:value-of select="title" /></a></div>
			<xsl:apply-templates select="item [position() &lt; 4]" />
		</div>
    </xsl:template>
    <xsl:template match="item" >
		<div class="slLinkWrapper">
			<a href="{link}" target="_blank"><xsl:value-of select="title" /></a>
			<br/>
			<xsl:value-of disable-output-escaping="yes" select="description" />
		</div>
	</xsl:template>
</xsl:stylesheet>

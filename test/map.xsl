<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE stylesheet [
  <!ENTITY tab "<xsl:text>&#9;</xsl:text>">
  <!ENTITY cr "<xsl:text>
</xsl:text>">
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0" xmlns:xalan="http://xml.apache.org/xslt">
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />

	<xsl:template match="/cell">	
	digraph G {
	rankdir="LR";
	subgraph cluster0 {
		label = "cell:<xsl:value-of select="@name" />";
		URL="cell.html";
		shape=record;
		color="#CCCCFF";
		style=filled;
		node [style=filled,color=white];
		ref_<xsl:value-of select="generate-id(.)"/>[URL="dmgr.html",shape=record,color="#FFFF3E",label="dmgr \n TBD"];
				<xsl:apply-templates select="dmgr" />
				<xsl:apply-templates select="cluster" />
			}
	}
	</xsl:template>

	<xsl:template match="dmgr">
	<!--  DMGR: <xsl:value-of select="@host" /> -->
	</xsl:template>

	<xsl:template match="cluster">
			subgraph cluster<xsl:value-of select="generate-id(.)"/> {
			 label = "cluster:<xsl:value-of select="@name" />";
			 color="#9999FF";
			 style=filled;
			 node [style=filled,color=white];
			 <!--  ref_<xsl:value-of select="generate-id(.)"/>[URL="cluster01.html",shape=record,label="cluster=<xsl:value-of select="@name" />"]; -->
		<xsl:apply-templates select="node" />
		}
	</xsl:template>

	<xsl:template match="node">
				 subgraph cluster<xsl:value-of select="generate-id(.)"/> {
			 	  label = "node:<xsl:value-of select="@name" />";
				  color="#6666FF";
				  style=filled;
				  node [style=filled,color=white];
			 	  <!-- ref_<xsl:value-of select="generate-id(.)"/>[URL="node01.html",shape=record,label="node=<xsl:value-of select="@name" />"]; -->
				  
				  <xsl:apply-templates select="server" />				
        		}
	</xsl:template>

	<xsl:template match="server">
			ref_<xsl:value-of select="generate-id(.)"/>[URL="server01.html",shape=record,color="#FFFF3E",label="<xsl:value-of select="@name" /> \n TBD"];
	</xsl:template>

</xsl:stylesheet>

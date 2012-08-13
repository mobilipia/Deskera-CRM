/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.krawler.portal.tools;

import com.krawler.portal.util.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import org.apache.tools.ant.DirectoryScanner;
public class SourceFormatter {
    private static final Log logger = LogFactory.getLog(SourceFormatter.class);
    	public static String stripImports(
			String content, String packageDir, String className)
		throws IOException {

		int x = content.indexOf("import ");

		if (x == -1) {
			return content;
		}

		int y = content.indexOf("{", x);

		y = content.substring(0, y).lastIndexOf(";") + 1;

		String imports = _formatImports(content.substring(x, y));

		content =
			content.substring(0, x) + imports +
				content.substring(y + 1, content.length());

		Set<String> classes = ClassUtil.getClasses(
			new StringReader(content), className);

		classes.add("_getMarkup");
		classes.add("_performBlockingInteraction");

		x = content.indexOf("import ");

		y = content.indexOf("{", x);

		y = content.substring(0, y).lastIndexOf(";") + 1;

		imports = content.substring(x, y);

		StringBuilder sb = new StringBuilder();

		BufferedReader br = new BufferedReader(new StringReader(imports));

		String line = null;

		while ((line = br.readLine()) != null) {
			if (line.indexOf("import ") != -1) {
				int importX = line.indexOf(" ");
				int importY = line.lastIndexOf(".");

				String importPackage = line.substring(importX + 1, importY);
				String importClass = line.substring(
					importY + 1, line.length() - 1);

				if (!packageDir.equals(importPackage)) {
					if (!importClass.equals("*")) {
						if (classes.contains(importClass)) {
							sb.append(line);
							sb.append("\n");
						}
					}
					else {
						sb.append(line);
						sb.append("\n");
					}
				}
			}
		}

		imports = _formatImports(sb.toString());

		content =
			content.substring(0, x) + imports +
				content.substring(y + 1, content.length());

		return content;
	}

	private static void _checkXSS(String fileName, String jspContent) {
		Matcher matcher = _xssPattern.matcher(jspContent);

		while (matcher.find()) {
			boolean xssVulnerable = false;

			String jspVariable = matcher.group(1);

			String inputVulnerability =
				" type=\"hidden\" value=\"<%= " + jspVariable + " %>";

			if (jspContent.indexOf(inputVulnerability) != -1) {
				xssVulnerable = true;
			}

			String anchorVulnerability = " href=\"<%= " + jspVariable + " %>";

			if (jspContent.indexOf(anchorVulnerability) != -1) {
				xssVulnerable = true;
			}

			String inlineStringVulnerability1 = "'<%= " + jspVariable + " %>";

			if (jspContent.indexOf(inlineStringVulnerability1) != -1) {
				xssVulnerable = true;
			}

			String inlineStringVulnerability2 = "(\"<%= " + jspVariable + " %>";

			if (jspContent.indexOf(inlineStringVulnerability2) != -1) {
				xssVulnerable = true;
			}

			String inlineStringVulnerability3 = " \"<%= " + jspVariable + " %>";

			if (jspContent.indexOf(inlineStringVulnerability3) != -1) {
				xssVulnerable = true;
			}

			String documentIdVulnerability = ".<%= " + jspVariable + " %>";

			if (jspContent.indexOf(documentIdVulnerability) != -1) {
				xssVulnerable = true;
			}

			if (xssVulnerable) {
				logger.debug(
					"(xss): " + fileName + " (" + jspVariable + ")");
			}
		}
	}

	public static String _formatImports(String imports) throws IOException {
		if ((imports.indexOf("/*") != -1) ||
			(imports.indexOf("*/") != -1) ||
			(imports.indexOf("//") != -1)) {

			return imports + "\n";
		}

		List<String> importsList = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new StringReader(imports));

		String line = null;

		while ((line = br.readLine()) != null) {
			if (line.indexOf("import ") != -1) {
				if (!importsList.contains(line)) {
					importsList.add(line);
				}
			}
		}

		importsList = ListUtil.sort(importsList);

		StringBuilder sb = new StringBuilder();

		String temp = null;

		for (int i = 0; i < importsList.size(); i++) {
			String s = importsList.get(i);

			int pos = s.indexOf(".");

			pos = s.indexOf(".", pos + 1);

			if (pos == -1) {
				pos = s.indexOf(".");
			}

			String packageLevel = s.substring(7, pos);

			if ((i != 0) && (!packageLevel.equals(temp))) {
				sb.append("\n");
			}

			temp = packageLevel;

			sb.append(s);
			sb.append("\n");
		}

		return sb.toString();
	}

	private static String _formatJavaContent(String fileName, String content)
		throws IOException {

		boolean longLogFactoryUtil = false;

		StringBuilder sb = new StringBuilder();

		BufferedReader br = new BufferedReader(new StringReader(content));

		int lineCount = 0;

		String line = null;

		while ((line = br.readLine()) != null) {
			lineCount++;

			if (line.trim().length() == 0) {
				line = StringPool.BLANK;
			}

			line = StringUtil.trimTrailing(line);

			line = StringUtil.replace(
				line,
				new String[] {
					"* Copyright (c) 2000-2008 Liferay, Inc.",
					"* Copyright 2008 Sun Microsystems Inc."
				},
				new String[] {
					"* Copyright (c) 2000-2009 Liferay, Inc.",
					"* Copyright 2009 Sun Microsystems Inc."
				});

			sb.append(line);
			sb.append("\n");

			line = StringUtil.replace(line, "\t", "    ");

			String excluded = _exclusions.getProperty(
				StringUtil.replace(fileName, "\\", "/") + StringPool.AT +
					lineCount);

			if (excluded == null) {
				excluded = _exclusions.getProperty(
					StringUtil.replace(fileName, "\\", "/"));
			}

			if ((excluded == null) && ((line.length() - 1) > 79) &&
				(!line.startsWith("import "))) {

				if (line.contains(
						"private static Log _log = LogFactoryUtil.getLog(")) {

					longLogFactoryUtil = true;
				}

				logger.debug("> 80: " + fileName + " " + lineCount);
			}
		}

		br.close();

		String newContent = sb.toString();

		if (newContent.endsWith("\n")) {
			newContent = newContent.substring(0, newContent.length() -1);
		}

		if (longLogFactoryUtil) {
			newContent = StringUtil.replace(
				newContent, "private static Log _log =",
				"private static Log _log =\n\t\t");
		}

		return newContent;
	}

	private static String _formatJSPContent(String fileName, String content)
		throws IOException {

		StringBuilder sb = new StringBuilder();

		BufferedReader br = new BufferedReader(new StringReader(content));

		String line = null;

		while ((line = br.readLine()) != null) {
			if (line.trim().length() == 0) {
				line = StringPool.BLANK;
			}

			line = StringUtil.trimTrailing(line);

			sb.append(line);
			sb.append("\n");
		}

		br.close();

		content = sb.toString();

		if (content.endsWith("\n")) {
			content = content.substring(0, content.length() -1);
		}

		content = _formatTaglibQuotes(fileName, content, StringPool.QUOTE);
		content = _formatTaglibQuotes(fileName, content, StringPool.APOSTROPHE);

		return content;
	}

	private static String _formatTaglibQuotes(
		String fileName, String content, String quoteType) {

		String quoteFix = StringPool.APOSTROPHE;

	    if (quoteFix.equals(quoteType)) {
	    	quoteFix = StringPool.QUOTE;
	    }

	    Pattern pattern = Pattern.compile(_getTaglibRegex(quoteType));

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			int x = content.indexOf(quoteType + "<%=", matcher.start());
			int y = content.indexOf("%>" + quoteType, x);

			while ((x != -1) && (y != -1)) {
				String result = content.substring(x + 1, y + 2);

				if (result.indexOf(quoteType) != -1) {
					int lineCount = 1;

					char contentCharArray[] = content.toCharArray();

					for (int i = 0; i < x; i++) {
						if (contentCharArray[i] == CharPool.NEW_LINE) {
							lineCount++;
						}
					}

					if (result.indexOf(quoteFix) == -1) {
						StringBuilder sb = new StringBuilder();

						sb.append(content.substring(0, x));
						sb.append(quoteFix);
						sb.append(result);
						sb.append(quoteFix);
						sb.append(content.substring(y + 3, content.length()));

						content = sb.toString();
					}
					else {
						logger.debug(
							"taglib: " + fileName + " " + lineCount);
					}
				}

				x = content.indexOf(quoteType + "<%=", y);

				if (x > matcher.end()) {
					break;
				}

				y = content.indexOf("%>" + quoteType, x);
			}
		}

		return content;
	}

	private static String _getCopyright() throws IOException {
		try {
			return _fileUtil.read("copyright.txt");
		}
		catch (Exception e1) {
			try {
				return _fileUtil.read("../copyright.txt");
			}
			catch (Exception e2) {
				return _fileUtil.read("../../copyright.txt");
			}
		}
	}

//	private static String[] _getPluginJavaFiles() {
//		String basedir = "./";
//
//		List<File> list = new ArrayList<File>();
//
//		DirectoryScanner ds = new DirectoryScanner();
//
//		ds.setBasedir(basedir);
//		ds.setExcludes(
//			new String[] {
//				"**\\model\\*Clp.java", "**\\model\\*Model.java",
//				"**\\model\\*Soap.java", "**\\model\\impl\\*ModelImpl.java",
//				"**\\service\\*Service.java", "**\\service\\*ServiceClp.java",
//				"**\\service\\*ServiceFactory.java",
//				"**\\service\\*ServiceUtil.java",
//				"**\\service\\ClpSerializer.java",
//				"**\\service\\base\\*ServiceBaseImpl.java",
//				"**\\service\\http\\*JSONSerializer.java",
//				"**\\service\\http\\*ServiceHttp.java",
//				"**\\service\\http\\*ServiceJSON.java",
//				"**\\service\\http\\*ServiceSoap.java",
//				"**\\service\\persistence\\*Finder.java",
//				"**\\service\\persistence\\*Persistence.java",
//				"**\\service\\persistence\\*PersistenceImpl.java",
//				"**\\service\\persistence\\*Util.java"
//			});
//		ds.setIncludes(new String[] {"**\\*.java"});
//
//		ds.scan();
//
//		list.addAll(ListUtil.fromArray(ds.getIncludedFiles()));
//
//		return list.toArray(new String[list.size()]);
//	}

//	private static String[] _getPortalJavaFiles() {
//		String basedir = "./";
//
//		List<File> list = new ArrayList<File>();
//
//		DirectoryScanner ds = new DirectoryScanner();
//
//		ds.setBasedir(basedir);
//		ds.setExcludes(
//			new String[] {
//				"**\\classes\\*", "**\\jsp\\*", "**\\tmp\\**",
//				"**\\EARXMLBuilder.java", "**\\EJBXMLBuilder.java",
//				"**\\PropsKeys.java", "**\\InstanceWrapperBuilder.java",
//				"**\\ServiceBuilder.java", "**\\SourceFormatter.java",
//				"**\\UserAttributes.java", "**\\WebKeys.java",
//				"**\\*_IW.java", "**\\XHTMLComplianceFormatter.java",
//				"**\\portal-service\\**\\model\\*Model.java",
//				"**\\portal-service\\**\\model\\*Soap.java",
//				"**\\model\\impl\\*ModelImpl.java",
//				"**\\portal\\service\\**", "**\\portal-client\\**",
//				"**\\portal-web\\classes\\**\\*.java",
//				"**\\portal-web\\test\\**\\*Test.java",
//				"**\\portlet\\**\\service\\**", "**\\tools\\ext_tmpl\\**",
//				"**\\wsrp\\service\\**"
//			});
//		ds.setIncludes(new String[] {"**\\*.java"});
//
//		ds.scan();
//
//		list.addAll(ListUtil.fromArray(ds.getIncludedFiles()));
//
//		ds = new DirectoryScanner();
//
//		ds.setBasedir(basedir);
//		ds.setExcludes(
//			new String[] {
//				"**\\tools\\ext_tmpl\\**", "**\\*_IW.java",
//				"**\\test\\**\\*PersistenceTest.java"
//			});
//		ds.setIncludes(
//			new String[] {
//				"**\\com\\liferay\\portal\\service\\ServiceContext*.java",
//				"**\\model\\BaseModel.java",
//				"**\\model\\impl\\BaseModelImpl.java",
//				"**\\service\\base\\PrincipalBean.java",
//				"**\\service\\http\\*HttpTest.java",
//				"**\\service\\http\\*SoapTest.java",
//				"**\\service\\http\\TunnelUtil.java",
//				"**\\service\\impl\\*.java", "**\\service\\jms\\*.java",
//				"**\\service\\permission\\*.java",
//				"**\\service\\persistence\\BasePersistence.java",
//				"**\\service\\persistence\\BatchSession*.java",
//				"**\\service\\persistence\\*FinderImpl.java",
//				"**\\service\\persistence\\impl\\BasePersistenceImpl.java",
//				"**\\portal-impl\\test\\**\\*.java",
//				"**\\portal-service\\**\\liferay\\counter\\**.java",
//				"**\\portal-service\\**\\liferay\\documentlibrary\\**.java",
//				"**\\portal-service\\**\\liferay\\lock\\**.java",
//				"**\\portal-service\\**\\liferay\\mail\\**.java",
//				"**\\util-bridges\\**\\*.java"
//			});
//
//		ds.scan();
//
//		list.addAll(ListUtil.fromArray(ds.getIncludedFiles()));
//
//		return list.toArray(new String[list.size()]);
//	}

	private static String _getTaglibRegex(String quoteType) {
		StringBuilder sb = new StringBuilder();

		sb.append("<(");

		for (int i = 0; i < _TAG_LIBRARIES.length; i++) {
			sb.append(_TAG_LIBRARIES[i]);
			sb.append(StringPool.PIPE);
		}

		sb.deleteCharAt(sb.length() - 1);
		sb.append("):([^>]|%>)*");
		sb.append(quoteType);
		sb.append("<%=[^>]*");
		sb.append(quoteType);
		sb.append("[^>]*%>");
		sb.append(quoteType);
		sb.append("([^>]|%>)*>");

		return sb.toString();
	}

	private static void _readExclusions() throws IOException {
		_exclusions = new Properties();

		ClassLoader classLoader = SourceFormatter.class.getClassLoader();

		String sourceFormatterExclusions = System.getProperty(
			"source-formatter-exclusions",
			"com/liferay/portal/tools/dependencies/" +
				"source_formatter_exclusions.properties");

		URL url = classLoader.getResource(sourceFormatterExclusions);

		if (url == null) {
			return;
		}

		InputStream is = url.openStream();

		_exclusions.load(is);

		is.close();
	}

	private static final String[] _TAG_LIBRARIES = new String[] {
		"c", "html", "jsp", "liferay-portlet", "liferay-security",
		"liferay-theme", "liferay-ui", "liferay-util", "portlet", "struts",
		"tiles"
	};

	private static FileImpl _fileUtil = FileImpl.getInstance();
	private static Properties _exclusions;
	private static Pattern _xssPattern = Pattern.compile(
		"String\\s+([^\\s]+)\\s*=\\s*ParamUtil\\.getString\\(");

}

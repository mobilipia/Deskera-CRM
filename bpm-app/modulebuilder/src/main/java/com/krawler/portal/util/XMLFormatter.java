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

package com.krawler.portal.util;


import java.io.IOException;
import java.io.StringReader;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * <a href="XMLFormatter.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 * @author Alan Zimmerman
 *
 */
public class XMLFormatter {

	public static String fixProlog(String xml) {

		// LEP-1921

		if (xml != null) {
			char[] charArray = xml.toCharArray();

			for (int i = 0; i < charArray.length; i++) {
				if (charArray[i] == '<') {
					if (i != 0) {
						xml = xml.substring(i, xml.length());
					}

					break;
				}
			}
		}

		return xml;
	}

	public static String fromCompactSafe(String xml) {
		return StringUtil.replace(xml, "[$NEW_LINE$]", "\n");
	}

	public static String toCompactSafe(String xml) {
		return StringUtil.replace(xml, "\n", "[$NEW_LINE$]");
	}

	public static String toString(String xml)
		throws DocumentException, IOException {

		return toString(xml, StringPool.TAB);
	}

	public static String toString(String xml, String indent)
		throws DocumentException, IOException {

		SAXReader reader = new SAXReader();

		Document doc = reader.read(new StringReader(xml));

		return toString(doc, indent);
	}

	public static String toString(Branch branch) throws IOException {
		return toString(branch, StringPool.TAB);
	}

	public static String toString(Branch branch, String indent)
		throws IOException {

		return toString(branch, StringPool.TAB, false);
	}

	public static String toString(
			Branch branch, String indent, boolean expandEmptyElements)
		throws IOException {

		ByteArrayMaker bam = new ByteArrayMaker();

		OutputFormat format = OutputFormat.createPrettyPrint();

		format.setExpandEmptyElements(expandEmptyElements);
		format.setIndent(indent);
		format.setLineSeparator("\n");

		XMLWriter writer = new XMLWriter(bam, format);

		writer.write(branch);

		String content = bam.toString(StringPool.UTF8);

		// LEP-4257

		//content = StringUtil.replace(content, "\n\n\n", "\n\n");

		if (content.endsWith("\n\n")) {
			content = content.substring(0, content.length() - 2);
		}

		if (content.endsWith("\n")) {
			content = content.substring(0, content.length() - 1);
		}

		while (content.indexOf(" \n") != -1) {
			content = StringUtil.replace(content, " \n", "\n");
		}

		return content;
	}

}

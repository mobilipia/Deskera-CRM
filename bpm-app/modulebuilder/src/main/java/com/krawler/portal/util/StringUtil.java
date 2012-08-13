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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <a href="StringUtil.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 * @author Sandeep Soni
 * @author Ganesh Ram
 *
 */
public class StringUtil {
    private static final Log logger = LogFactory.getLog(StringUtil.class);
	public static String add(String s, String add) {
		return add(s, add, StringPool.COMMA);
	}

	public static String add(String s, String add, String delimiter) {
		return add(s, add, delimiter, false);
	}

	public static String add(
		String s, String add, String delimiter, boolean allowDuplicates) {

		if ((add == null) || (delimiter == null)) {
			return null;
		}

		if (s == null) {
			s = StringPool.BLANK;
		}

		if (allowDuplicates || !contains(s, add, delimiter)) {
			StringBuilder sb = new StringBuilder();

			sb.append(s);

			if (com.krawler.common.util.StringUtil.isNullOrEmpty(s) || s.endsWith(delimiter)) {
				sb.append(add);
				sb.append(delimiter);
			}
			else {
				sb.append(delimiter);
				sb.append(add);
				sb.append(delimiter);
			}

			s = sb.toString();
		}

		return s;
	}

	public static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(
				0x0100 + (bytes[i] & 0x00FF)).substring(1);

			if (hex.length() < 2) {
				sb.append("0");
			}

			sb.append(hex);
		}

		return sb.toString();
	}

	public static boolean contains(String s, String text) {
		return contains(s, text, StringPool.COMMA);
	}

	public static boolean contains(String s, String text, String delimiter) {
		if ((s == null) || (text == null) || (delimiter == null)) {
			return false;
		}

		StringBuilder sb = null;

		if (!s.endsWith(delimiter)) {
			sb = new StringBuilder();

			sb.append(s);
			sb.append(delimiter);

			s = sb.toString();
		}

		sb = new StringBuilder();

		sb.append(delimiter);
		sb.append(text);
		sb.append(delimiter);

		String dtd = sb.toString();

		int pos = s.indexOf(dtd);

		if (pos == -1) {
			sb = new StringBuilder();

			sb.append(text);
			sb.append(delimiter);

			String td = sb.toString();

			if (s.startsWith(td)) {
				return true;
			}

			return false;
		}

		return true;
	}

	public static int count(String s, String text) {
		if ((s == null) || (text == null)) {
			return 0;
		}

		int count = 0;

		int pos = s.indexOf(text);

		while (pos != -1) {
			pos = s.indexOf(text, pos + text.length());

			count++;
		}

		return count;
	}

	public static boolean endsWith(String s, char end) {
		return endsWith(s, (new Character(end)).toString());
	}

	public static boolean endsWith(String s, String end) {
		if ((s == null) || (end == null)) {
			return false;
		}

		if (end.length() > s.length()) {
			return false;
		}

		String temp = s.substring(s.length() - end.length(), s.length());

		if (temp.equalsIgnoreCase(end)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static String extractChars(String s) {
		if (s == null) {
			return StringPool.BLANK;
		}

		StringBuilder sb = new StringBuilder();

		char[] c = s.toCharArray();

		for (int i = 0; i < c.length; i++) {
			if (Validator.isChar(c[i])) {
				sb.append(c[i]);
			}
		}

		return sb.toString();
	}

	public static String extractDigits(String s) {
		if (s == null) {
			return StringPool.BLANK;
		}

		StringBuilder sb = new StringBuilder();

		char[] c = s.toCharArray();

		for (int i = 0; i < c.length; i++) {
			if (Validator.isDigit(c[i])) {
				sb.append(c[i]);
			}
		}

		return sb.toString();
	}

	public static String extractFirst(String s, String delimiter) {
		if (s == null) {
			return null;
		}
		else {
			String[] array = split(s, delimiter);

			if (array.length > 0) {
				return array[0];
			}
			else {
				return null;
			}
		}
	}

	public static String extractLast(String s, String delimiter) {
		if (s == null) {
			return null;
		}
		else {
			String[] array = split(s, delimiter);

			if (array.length > 0) {
				return array[array.length - 1];
			}
			else {
				return null;
			}
		}
	}

	public static String highlight(String s, String keywords) {
		return highlight(s, keywords, "<span class=\"highlight\">", "</span>");
	}

	public static String highlight(
		String s, String keywords, String highlight1, String highlight2) {

		if (s == null) {
			return null;
		}

		// The problem with using a regexp is that it searches the text in a
		// case insenstive manner but doens't replace the text in a case
		// insenstive manner. So the search results actually get messed up. The
		// best way is to actually parse the results.

		//return s.replaceAll(
		//	"(?i)" + keywords, highlight1 + keywords + highlight2);

		StringBuilder sb = new StringBuilder(StringPool.SPACE);

		StringTokenizer st = new StringTokenizer(s);

		while (st.hasMoreTokens()) {
			String token = st.nextToken();

			if (token.equalsIgnoreCase(keywords)) {
				sb.append(highlight1);
				sb.append(token);
				sb.append(highlight2);
			}
			else {
				sb.append(token);
			}

			if (st.hasMoreTokens()) {
				sb.append(StringPool.SPACE);
			}
		}

		return sb.toString();
	}

	public static String insert(String s, String insert, int offset) {
		if (s == null) {
			return null;
		}

		if (insert == null) {
			return s;
		}

		if (offset > s.length()) {
			offset = s.length();
		}

		StringBuilder sb = new StringBuilder(s);

		sb.insert(offset, insert);

		return sb.toString();
	}

	public static String lowerCase(String s) {
		if (s == null) {
			return null;
		}
		else {
			return s.toLowerCase();
		}
	}

	public static boolean matches(String s, String pattern) {
		String[] array = pattern.split("\\*");

		for (int i = 0; i < array.length; i++) {
			int pos = s.indexOf(array[i]);

			if (pos == -1) {
				return false;
			}

			s = s.substring(pos + array[i].length());
		}

		return true;
	}

	public static String merge(boolean[] array) {
		return merge(array, StringPool.COMMA);
	}

	public static String merge(boolean[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			sb.append(String.valueOf(array[i]).trim());

			if ((i + 1) != array.length) {
				sb.append(delimiter);
			}
		}

		return sb.toString();
	}

	public static String merge(double[] array) {
		return merge(array, StringPool.COMMA);
	}

	public static String merge(double[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			sb.append(String.valueOf(array[i]).trim());

			if ((i + 1) != array.length) {
				sb.append(delimiter);
			}
		}

		return sb.toString();
	}

	public static String merge(float[] array) {
		return merge(array, StringPool.COMMA);
	}

	public static String merge(float[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			sb.append(String.valueOf(array[i]).trim());

			if ((i + 1) != array.length) {
				sb.append(delimiter);
			}
		}

		return sb.toString();
	}

	public static String merge(int[] array) {
		return merge(array, StringPool.COMMA);
	}

	public static String merge(int[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			sb.append(String.valueOf(array[i]).trim());

			if ((i + 1) != array.length) {
				sb.append(delimiter);
			}
		}

		return sb.toString();
	}

	public static String merge(long[] array) {
		return merge(array, StringPool.COMMA);
	}

	public static String merge(long[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			sb.append(String.valueOf(array[i]).trim());

			if ((i + 1) != array.length) {
				sb.append(delimiter);
			}
		}

		return sb.toString();
	}

	public static String merge(short[] array) {
		return merge(array, StringPool.COMMA);
	}

	public static String merge(short[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			sb.append(String.valueOf(array[i]).trim());

			if ((i + 1) != array.length) {
				sb.append(delimiter);
			}
		}

		return sb.toString();
	}

	public static String merge(Collection<?> col) {
		return merge(col, StringPool.COMMA);
	}

	public static String merge(Collection<?> col, String delimiter) {
		if (col == null) {
			return null;
		}

		return merge(col.toArray(new Object[col.size()]), delimiter);
	}

	public static String merge(Object[] array) {
		return merge(array, StringPool.COMMA);
	}

	public static String merge(Object[] array, String delimiter) {
		if (array == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			sb.append(String.valueOf(array[i]).trim());

			if ((i + 1) != array.length) {
				sb.append(delimiter);
			}
		}

		return sb.toString();
	}

	public static String randomize(String s) {
//    		return Randomizer.getInstance().randomize(s);
                return "sdfsdf";
	}

	public static String read(ClassLoader classLoader, String name)
		throws IOException {

		return read(classLoader, name, false);
	}

	public static String read(ClassLoader classLoader, String name, boolean all)
		throws IOException {

		if (all) {
			StringBuilder sb = new StringBuilder();

			Enumeration<URL> enu = classLoader.getResources(name);

			while (enu.hasMoreElements()) {
				URL url = enu.nextElement();

				InputStream is = url.openStream();

				String s = read(is);

				if (s != null) {
					sb.append(s);
					sb.append(StringPool.NEW_LINE);
				}

				is.close();
			}

			return sb.toString().trim();
		}
		else {
			InputStream is = classLoader.getResourceAsStream(name);

			String s = read(is);

			is.close();

			return s;
		}
	}

	public static String read(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();

		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = null;

		while ((line = br.readLine()) != null) {
			sb.append(line).append('\n');
		}

		br.close();

		return sb.toString().trim();
	}

	public static String remove(String s, String remove) {
		return remove(s, remove, StringPool.COMMA);
	}

	public static String remove(String s, String remove, String delimiter) {
		if ((s == null) || (remove == null) || (delimiter == null)) {
			return null;
		}

		if (!com.krawler.common.util.StringUtil.isNullOrEmpty(s) && !s.endsWith(delimiter)) {//Validator.isNotNull(s)
			s += delimiter;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(delimiter);
		sb.append(remove);
		sb.append(delimiter);

		String drd = sb.toString();

		sb = new StringBuilder();

		sb.append(remove);
		sb.append(delimiter);

		String rd = sb.toString();

		while (contains(s, remove, delimiter)) {
			int pos = s.indexOf(drd);

			if (pos == -1) {
				if (s.startsWith(rd)) {
					int x = remove.length() + delimiter.length();
					int y = s.length();

					s = s.substring(x, y);
				}
			}
			else {
				int x = pos + remove.length() + delimiter.length();
				int y = s.length();

				sb = new StringBuilder();

				sb.append(s.substring(0, pos));
				sb.append(s.substring(x, y));

				s = sb.toString();
			}
		}

		return s;
	}

	public static String replace(String s, char oldSub, char newSub) {
		if (s == null) {
			return null;
		}

		return s.replace(oldSub, newSub);
	}

	public static String replace(String s, char oldSub, String newSub) {
		if ((s == null) || (newSub == null)) {
			return null;
		}

		// The number 5 is arbitrary and is used as extra padding to reduce
		// buffer expansion

		StringBuilder sb = new StringBuilder(s.length() + 5 * newSub.length());

		char[] charArray = s.toCharArray();

		for (char c : charArray) {
			if (c == oldSub) {
				sb.append(newSub);
			}
			else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	public static String replace(String s, String oldSub, String newSub) {
		if ((s == null) || (oldSub == null) || (newSub == null)) {
			return null;
		}

		int y = s.indexOf(oldSub);

		if (y >= 0) {

			// The number 5 is arbitrary and is used as extra padding to reduce
			// buffer expansion

			StringBuilder sb = new StringBuilder(
				s.length() + 5 * newSub.length());

			int length = oldSub.length();
			int x = 0;

			while (x <= y) {
				sb.append(s.substring(x, y));
				sb.append(newSub);

				x = y + length;
				y = s.indexOf(oldSub, x);
			}

			sb.append(s.substring(x));

			return sb.toString();
		}
		else {
			return s;
		}
	}

	public static String replace(String s, String[] oldSubs, String[] newSubs) {
		if ((s == null) || (oldSubs == null) || (newSubs == null)) {
			return null;
		}

		if (oldSubs.length != newSubs.length) {
			return s;
		}

		for (int i = 0; i < oldSubs.length; i++) {
			s = replace(s, oldSubs[i], newSubs[i]);
		}

		return s;
	}

	public static String replace(
		String s, String[] oldSubs, String[] newSubs, boolean exactMatch) {

		if ((s == null) || (oldSubs == null) || (newSubs == null)) {
			return null;
		}

		if (oldSubs.length != newSubs.length) {
			return s;
		}

		if (!exactMatch) {
			replace(s, oldSubs, newSubs);
		}
		else {
			for (int i = 0; i < oldSubs.length; i++) {
				s = s.replaceAll("\\b" + oldSubs[i] + "\\b" , newSubs[i]);
			}
		}

		return s;
	}

	public static String replaceFirst(String s, char oldSub, char newSub) {
		if (s == null) {
			return null;
		}

		return s.replaceFirst(String.valueOf(oldSub), String.valueOf(newSub));
	}

	public static String replaceFirst(String s, char oldSub, String newSub) {
		if ((s == null) || (newSub == null)) {
			return null;
		}

		return s.replaceFirst(String.valueOf(oldSub), newSub);
	}

	public static String replaceFirst(String s, String oldSub, String newSub) {
		if ((s == null) || (oldSub == null) || (newSub == null)) {
			return null;
		}

		return s.replaceFirst(oldSub, newSub);
	}

	public static String replaceFirst(
		String s, String[] oldSubs, String[] newSubs) {

		if ((s == null) || (oldSubs == null) || (newSubs == null)) {
			return null;
		}

		if (oldSubs.length != newSubs.length) {
			return s;
		}

		for (int i = 0; i < oldSubs.length; i++) {
			s = replaceFirst(s, oldSubs[i], newSubs[i]);
		}

		return s;
	}

	/**
	 * Returns a string with replaced values. This method will replace all text
	 * in the given string, between the beginning and ending delimiter, with new
	 * values found in the given map. For example, if the string contained the
	 * text <code>[$HELLO$]</code>, and the beginning delimiter was
	 * <code>[$]</code>, and the ending delimiter was <code>$]</code>, and the
	 * values map had a key of <code>HELLO</code> that mapped to
	 * <code>WORLD</code>, then the replaced string will contain the text
	 * <code>[$WORLD$]</code>.
	 *
	 * @param		s the original string
	 * @param		begin the beginning delimiter
	 * @param		end the ending delimiter
	 * @param		values a map of old and new values
	 * @return		a string with replaced values
	 */
	public static String replaceValues(
		String s, String begin, String end, Map<String, String> values) {

		if ((s == null) || (begin == null) || (end == null) ||
			(values == null) || (values.size() == 0)) {

			return s;
		}

		StringBuilder sb = new StringBuilder(s.length());

		int pos = 0;

		while (true) {
			int x = s.indexOf(begin, pos);
			int y = s.indexOf(end, x + begin.length());

			if ((x == -1) || (y == -1)) {
				sb.append(s.substring(pos, s.length()));

				break;
			}
			else {
				sb.append(s.substring(pos, x + begin.length()));

				String oldValue = s.substring(x + begin.length(), y);

				String newValue = values.get(oldValue);

				if (newValue == null) {
					newValue = oldValue;
				}

				sb.append(newValue);

				pos = y;
			}
		}

		return sb.toString();
	}

	public static String reverse(String s) {
		if (s == null) {
			return null;
		}

		char[] c = s.toCharArray();
		char[] reverse = new char[c.length];

		for (int i = 0; i < c.length; i++) {
			reverse[i] = c[c.length - i - 1];
		}

		return new String(reverse);
	}

	public static String safePath(String path) {
		return replace(path, StringPool.DOUBLE_SLASH, StringPool.SLASH);
	}

	public static String shorten(String s) {
		return shorten(s, 20);
	}

	public static String shorten(String s, int length) {
		return shorten(s, length, "...");
	}

	public static String shorten(String s, String suffix) {
		return shorten(s, 20, suffix);
	}

	public static String shorten(String s, int length, String suffix) {
		if ((s == null) || (suffix == null)) {
			return null;
		}

		if (s.length() > length) {
			for (int j = length; j >= 0; j--) {
				if (Character.isWhitespace(s.charAt(j))) {
					length = j;

					break;
				}
			}

			StringBuilder sb = new StringBuilder();

			sb.append(s.substring(0, length));
			sb.append(suffix);

			s =  sb.toString();
		}

		return s;
	}

	public static String[] split(String s) {
		return split(s, StringPool.COMMA);
	}

	public static String[] split(String s, String delimiter) {
		if ((com.krawler.common.util.StringUtil.isNullOrEmpty(s)) || (delimiter == null) ||
			(delimiter.equals(StringPool.BLANK))) {

			return new String[0];
		}

		s = s.trim();

		if (!s.endsWith(delimiter)) {
			StringBuilder sb = new StringBuilder();

			sb.append(s);
			sb.append(delimiter);

			s = sb.toString();
		}

		if (s.equals(delimiter)) {
			return new String[0];
		}

		List<String> nodeValues = new ArrayList<String>();

		if (delimiter.equals(StringPool.NEW_LINE) ||
			delimiter.equals(StringPool.RETURN)) {

			try {
				BufferedReader br = new BufferedReader(new StringReader(s));

				String line = null;

				while ((line = br.readLine()) != null) {
					nodeValues.add(line);
				}

				br.close();
			}
			catch (IOException ioe) {
                            logger.warn(ioe.getMessage(), ioe);
//				_log.error(ioe.getMessage());
			}
		}
		else {
			int offset = 0;
			int pos = s.indexOf(delimiter, offset);

			while (pos != -1) {
				nodeValues.add(new String(s.substring(offset, pos)));

				offset = pos + delimiter.length();
				pos = s.indexOf(delimiter, offset);
			}
		}

		return nodeValues.toArray(new String[nodeValues.size()]);
	}

	public static boolean[] split(String s, boolean x) {
		return split(s, StringPool.COMMA, x);
	}

	public static boolean[] split(String s, String delimiter, boolean x) {
		String[] array = split(s, delimiter);
		boolean[] newArray = new boolean[array.length];

		for (int i = 0; i < array.length; i++) {
			boolean value = x;

			try {
				value = Boolean.valueOf(array[i]).booleanValue();
			}
			catch (Exception e) {
                            logger.warn(e.getMessage(), e);
			}

			newArray[i] = value;
		}

		return newArray;
	}

	public static double[] split(String s, double x) {
		return split(s, StringPool.COMMA, x);
	}

	public static double[] split(String s, String delimiter, double x) {
		String[] array = split(s, delimiter);
		double[] newArray = new double[array.length];

		for (int i = 0; i < array.length; i++) {
			double value = x;

			try {
				value = Double.parseDouble(array[i]);
			}
			catch (Exception e) {
                            logger.warn(e.getMessage(), e);
			}

			newArray[i] = value;
		}

		return newArray;
	}

	public static float[] split(String s, float x) {
		return split(s, StringPool.COMMA, x);
	}

	public static float[] split(String s, String delimiter, float x) {
		String[] array = split(s, delimiter);
		float[] newArray = new float[array.length];

		for (int i = 0; i < array.length; i++) {
			float value = x;

			try {
				value = Float.parseFloat(array[i]);
			}
			catch (Exception e) {
                            logger.warn(e.getMessage(), e);
			}

			newArray[i] = value;
		}

		return newArray;
	}

	public static int[] split(String s, int x) {
		return split(s, StringPool.COMMA, x);
	}

	public static int[] split(String s, String delimiter, int x) {
		String[] array = split(s, delimiter);
		int[] newArray = new int[array.length];

		for (int i = 0; i < array.length; i++) {
			int value = x;

			try {
				value = Integer.parseInt(array[i]);
			}
			catch (Exception e) {
                            logger.warn(e.getMessage(), e);
			}

			newArray[i] = value;
		}

		return newArray;
	}

	public static long[] split(String s, long x) {
		return split(s, StringPool.COMMA, x);
	}

	public static long[] split(String s, String delimiter, long x) {
		String[] array = split(s, delimiter);
		long[] newArray = new long[array.length];

		for (int i = 0; i < array.length; i++) {
			long value = x;

			try {
				value = Long.parseLong(array[i]);
			}
			catch (Exception e) {
                            logger.warn(e.getMessage(), e);
			}

			newArray[i] = value;
		}

		return newArray;
	}

	public static short[] split(String s, short x) {
		return split(s, StringPool.COMMA, x);
	}

	public static short[] split(String s, String delimiter, short x) {
		String[] array = split(s, delimiter);
		short[] newArray = new short[array.length];

		for (int i = 0; i < array.length; i++) {
			short value = x;

			try {
				value = Short.parseShort(array[i]);
			}
			catch (Exception e) {
                            logger.warn(e.getMessage(), e);
			}

			newArray[i] = value;
		}

		return newArray;
	}

	public static boolean startsWith(String s, char begin) {
		return startsWith(s, (new Character(begin)).toString());
	}

	public static boolean startsWith(String s, String start) {
		if ((s == null) || (start == null)) {
			return false;
		}

		if (start.length() > s.length()) {
			return false;
		}

		String temp = s.substring(0, start.length());

		if (temp.equalsIgnoreCase(start)) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Return the number of starting letters that s1 and s2 have in common
	 * before they deviate.
	 *
	 * @param		s1 the first string
	 * @param		s2 the second string
	 *
	 * @return		the number of starting letters that s1 and s2 have in common
	 *				before they deviate
	 */
	public static int startsWithWeight(String s1, String s2) {
		if ((s1 == null) || (s2 == null)) {
			return 0;
		}

		char[] charArray1 = s1.toCharArray();
		char[] charArray2 = s2.toCharArray();

		int i = 0;

		for (; (i < charArray1.length) && (i < charArray2.length); i++) {
			if (charArray1[i] != charArray2[i]) {
				break;
			}
		}

		return i;
	}

	public static String stripBetween(String s, String begin, String end) {
		if ((s == null) || (begin == null) || (end == null)) {
			return s;
		}

		StringBuilder sb = new StringBuilder(s.length());

		int pos = 0;

		while (true) {
			int x = s.indexOf(begin, pos);
			int y = s.indexOf(end, x + begin.length());

			if ((x == -1) || (y == -1)) {
				sb.append(s.substring(pos, s.length()));

				break;
			}
			else {
				sb.append(s.substring(pos, x));

				pos = y + end.length();
			}
		}

		return sb.toString();
	}

	public static String trim(String s) {
		return trim(s, null);
	}

	public static String trim(String s, char c) {
		return trim(s, new char[] {c});
	}

	public static String trim(String s, char[] exceptions) {
		if (s == null) {
			return null;
		}

		char[] charArray = s.toCharArray();

		int len = charArray.length;

		int x = 0;
		int y = charArray.length;

		for (int i = 0; i < len; i++) {
			char c = charArray[i];

			if (_isTrimable(c, exceptions)) {
				x = i + 1;
			}
			else {
				break;
			}
		}

		for (int i = len - 1; i >= 0; i--) {
			char c = charArray[i];

			if (_isTrimable(c, exceptions)) {
				y = i;
			}
			else {
				break;
			}
		}

		if ((x != 0) || (y != len)) {
			return s.substring(x, y);
		}
		else {
			return s;
		}
	}

	public static String trimLeading(String s) {
		return trimLeading(s, null);
	}

	public static String trimLeading(String s, char c) {
		return trimLeading(s, new char[] {c});
	}

	public static String trimLeading(String s, char[] exceptions) {
		if (s == null) {
			return null;
		}

		char[] charArray = s.toCharArray();

		int len = charArray.length;

		int x = 0;
		int y = charArray.length;

		for (int i = 0; i < len; i++) {
			char c = charArray[i];

			if (_isTrimable(c, exceptions)) {
				x = i + 1;
			}
			else {
				break;
			}
		}

		if ((x != 0) || (y != len)) {
			return s.substring(x, y);
		}
		else {
			return s;
		}
	}

	public static String trimTrailing(String s) {
		return trimTrailing(s, null);
	}

	public static String trimTrailing(String s, char c) {
		return trimTrailing(s, new char[] {c});
	}

	public static String trimTrailing(String s, char[] exceptions) {
		if (s == null) {
			return null;
		}

		char[] charArray = s.toCharArray();

		int len = charArray.length;

		int x = 0;
		int y = charArray.length;

		for (int i = len - 1; i >= 0; i--) {
			char c = charArray[i];

			if (_isTrimable(c, exceptions)) {
				y = i;
			}
			else {
				break;
			}
		}

		if ((x != 0) || (y != len)) {
			return s.substring(x, y);
		}
		else {
			return s;
		}
	}

	public static String upperCase(String s) {
		if (s == null) {
			return null;
		}
		else {
			return s.toUpperCase();
		}
	}

	public static String upperCaseFirstLetter(String s) {
		char[] chars = s.toCharArray();

		if ((chars[0] >= 97) && (chars[0] <= 122)) {
			chars[0] = (char)(chars[0] - 32);
		}

		return new String(chars);
	}

	public static String valueOf(Object obj) {
		return String.valueOf(obj);
	}

	public static String wrap(String text) {
		return wrap(text, 80, StringPool.NEW_LINE);
	}

	public static String wrap(String text, int width, String lineSeparator) {
		if (text == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		try {
			BufferedReader br = new BufferedReader(new StringReader(text));

			String s = StringPool.BLANK;

			while ((s = br.readLine()) != null) {
				if (s.length() == 0) {
					sb.append(lineSeparator);
				}
				else {
					String[] tokens = s.split(StringPool.SPACE);
					boolean firstWord = true;
					int curLineLength = 0;

					for (int i = 0; i < tokens.length; i++) {
						if (!firstWord) {
							sb.append(StringPool.SPACE);
							curLineLength++;
						}

						if (firstWord) {
							sb.append(lineSeparator);
						}

						sb.append(tokens[i]);

						curLineLength += tokens[i].length();

						if (curLineLength >= width) {
							firstWord = true;
							curLineLength = 0;
						}
						else {
							firstWord = false;
						}
					}
				}
			}
		}
		catch (IOException ioe) {
                    logger.warn(ioe.getMessage(), ioe);
//			_log.error(ioe.getMessage());
		}

		return sb.toString();
	}

	private static boolean _isTrimable(char c, char[] exceptions) {
		if ((exceptions != null) && (exceptions.length > 0)) {
			for (int i = 0; i < exceptions.length; i++) {
				if (c == exceptions[i]) {
					return false;
				}
			}
		}

		return Character.isWhitespace(c);
	}

//	private static Log _log = LogFactoryUtil.getLog(StringUtil.class);

}

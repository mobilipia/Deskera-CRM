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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * <a href="ListUtil.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class ListUtil {

	public static List copy(List master) {
		if (master == null) {
			return null;
		}

		return new ArrayList(master);
	}

	public static void copy(List master, List copy) {
		if ((master == null) || (copy == null)) {
			return;
		}

		copy.clear();

		Iterator itr = master.iterator();

		while (itr.hasNext()) {
			Object obj = itr.next();

			copy.add(obj);
		}
	}

	public static void distinct(List list) {
		distinct(list, null);
	}

	public static void distinct(List list, Comparator comparator) {
		if ((list == null) || (list.size() == 0)) {
			return;
		}

		Set<Object> set = null;

		if (comparator == null) {
			set = new TreeSet<Object>();
		}
		else {
			set = new TreeSet<Object>(comparator);
		}

		Iterator<Object> itr = list.iterator();

		while (itr.hasNext()) {
			Object obj = itr.next();

			if (set.contains(obj)) {
				itr.remove();
			}
			else {
				set.add(obj);
			}
		}
	}

	public static List fromArray(Object[] array) {
		if ((array == null) || (array.length == 0)) {
			return new ArrayList();
		}

		List list = new ArrayList(array.length);

		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}

		return list;
	}

	public static List fromCollection(Collection c) {
		if ((c != null) && (c instanceof List)) {
			return (List)c;
		}

		if ((c == null) || (c.size() == 0)) {
			return new ArrayList();
		}

		List list = new ArrayList(c.size());

		Iterator itr = c.iterator();

		while (itr.hasNext()) {
			list.add(itr.next());
		}

		return list;
	}

	public static List fromEnumeration(Enumeration enu) {
		List list = new ArrayList();

		while (enu.hasMoreElements()) {
			Object obj = enu.nextElement();

			list.add(obj);
		}

		return list;
	}

	public static List fromFile(String fileName) throws IOException {
		return fromFile(new File(fileName));
	}

	public static List fromFile(File file) throws IOException {
		List list = new ArrayList();

		BufferedReader br = new BufferedReader(new FileReader(file));

		String s = StringPool.BLANK;

		while ((s = br.readLine()) != null) {
			list.add(s);
		}

		br.close();

		return list;
	}

	public static List fromString(String s) {
		return fromArray(StringUtil.split(s, StringPool.NEW_LINE));
	}

	public static List sort(List list) {
		return sort(list, null);
	}

	public static List sort(List list, Comparator comparator) {
//		if (list instanceof UnmodifiableList) {
//			list = copy(list);
//		}
//
//		Collections.sort(list, comparator);

		return list;
	}

	public static List subList(List list, int start, int end) {
		List newList = new ArrayList();

		int normalizedSize = list.size() - 1;

		if ((start < 0) || (start > normalizedSize) || (end < 0) ||
			(start > end)) {

			return newList;
		}

		for (int i = start; i < end && i <= normalizedSize; i++) {
			newList.add(list.get(i));
		}

		return newList;
	}

	public static List<Boolean> toList(boolean[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<Boolean> list = new ArrayList<Boolean>(array.length);

		for (boolean value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Double> toList(double[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<Double> list = new ArrayList<Double>(array.length);

		for (double value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Float> toList(float[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<Float> list = new ArrayList<Float>(array.length);

		for (float value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Integer> toList(int[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<Integer> list = new ArrayList<Integer>(array.length);

		for (int value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Long> toList(long[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<Long> list = new ArrayList<Long>(array.length);

		for (long value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Short> toList(short[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<Short> list = new ArrayList<Short>(array.length);

		for (short value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Boolean> toList(Boolean[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<Boolean> list = new ArrayList<Boolean>(array.length);

		for (Boolean value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Double> toList(Double[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<Double> list = new ArrayList<Double>(array.length);

		for (Double value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Float> toList(Float[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<Float> list = new ArrayList<Float>(array.length);

		for (Float value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Integer> toList(Integer[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<Integer> list = new ArrayList<Integer>(array.length);

		for (Integer value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Long> toList(Long[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<Long> list = new ArrayList<Long>(array.length);

		for (Long value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<Short> toList(Short[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<Short> list = new ArrayList<Short>(array.length);

		for (Short value : array) {
			list.add(value);
		}

		return list;
	}

	public static List<String> toList(String[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.EMPTY_LIST;
		}

		List<String> list = new ArrayList<String>(array.length);

		for (String value : array) {
			list.add(value);
		}

		return list;
	}

	public static String toString(List list, String param) {
		return toString(list, param, StringPool.COMMA);
	}

	public static String toString(List list, String param, String delimiter) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			Object bean = list.get(i);

//			Object value = BeanPropertiesUtil.getObject(bean, param);
//
//			if (value == null) {
//				value = StringPool.BLANK;
//			}
//
//			sb.append(value.toString());

			if ((i + 1) != list.size()) {
				sb.append(delimiter);
			}
		}

		return sb.toString();
	}

}

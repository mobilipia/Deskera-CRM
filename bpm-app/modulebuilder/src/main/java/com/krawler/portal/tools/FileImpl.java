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

import com.krawler.portal.util.ByteArrayMaker;
import com.krawler.portal.util.FileComparator;
import com.krawler.portal.util.GetterUtil;
import com.krawler.portal.util.StringPool;
import com.krawler.portal.util.StringUtil;
import com.krawler.portal.util.Time;
import com.krawler.portal.util.Validator;
import com.krawler.portal.util.PwdGenerator;
import com.krawler.portal.util.SystemProperties;
//import com.liferay.util.lucene.JerichoHTMLTextExtractor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

import java.nio.channels.FileChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jackrabbit.extractor.MsExcelTextExtractor;
import org.apache.jackrabbit.extractor.MsPowerPointTextExtractor;
import org.apache.jackrabbit.extractor.MsWordTextExtractor;
import org.apache.jackrabbit.extractor.OpenOfficeTextExtractor;
import org.apache.jackrabbit.extractor.PdfTextExtractor;
import org.apache.jackrabbit.extractor.PlainTextExtractor;
import org.apache.jackrabbit.extractor.RTFTextExtractor;
import org.apache.jackrabbit.extractor.TextExtractor;
import org.apache.jackrabbit.extractor.XMLTextExtractor;
//
//import org.mozilla.intl.chardet.nsDetector;
//import org.mozilla.intl.chardet.nsPSMDetector;
public class FileImpl implements com.krawler.portal.util.File {
    private static final Log logger = LogFactory.getLog(FileImpl.class);
        public static FileImpl getInstance() {
		return _instance;
	}

	public void copyDirectory(String sourceDirName, String destinationDirName) {
		copyDirectory(new File(sourceDirName), new File(destinationDirName));
	}

	public void copyDirectory(File source, File destination) {
		if (source.exists() && source.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdirs();
			}

			File[] fileArray = source.listFiles();

			for (int i = 0; i < fileArray.length; i++) {
				if (fileArray[i].isDirectory()) {
					copyDirectory(
						fileArray[i],
						new File(destination.getPath() + File.separator
							+ fileArray[i].getName()));
				}
				else {
					copyFile(
						fileArray[i],
						new File(destination.getPath() + File.separator
							+ fileArray[i].getName()));
				}
			}
		}
	}

	public void copyFile(String source, String destination) {
		copyFile(source, destination, false);
	}

	public void copyFile(String source, String destination, boolean lazy) {
		copyFile(new File(source), new File(destination), lazy);
	}

	public void copyFile(File source, File destination) {
		copyFile(source, destination, false);
	}

	public void copyFile(File source, File destination, boolean lazy) {
		if (!source.exists()) {
			return;
		}

		if (lazy) {
			String oldContent = null;

			try {
				oldContent = read(source);
			}
			catch (Exception e) {
                            logger.warn(e.getMessage(), e);
				return;
			}

			String newContent = null;

			try {
				newContent = read(destination);
			}
			catch (Exception e) {
                            logger.warn(e.getMessage(), e);
			}

			if ((oldContent == null) || !oldContent.equals(newContent)) {
				copyFile(source, destination, false);
			}
		}
		else {
			if ((destination.getParentFile() != null) &&
				(!destination.getParentFile().exists())) {

				destination.getParentFile().mkdirs();
			}

			try {
				FileChannel srcChannel =
					new FileInputStream(source).getChannel();
				FileChannel dstChannel =
					new FileOutputStream(destination).getChannel();

				dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

				srcChannel.close();
				dstChannel.close();
			}
			catch (IOException ioe) {
                            logger.warn(ioe.getMessage(), ioe);
//				_log.error(ioe.getMessage());
			}
		}
	}

	public File createTempFile() {
		return createTempFile(null);
	}

	public File createTempFile(String extension) {
		StringBuilder sb = new StringBuilder();

		sb.append(SystemProperties.get(SystemProperties.TMP_DIR));
		sb.append(StringPool.SLASH);
		sb.append(Time.getTimestamp());
		sb.append(PwdGenerator.getPassword(PwdGenerator.KEY2, 8));

		if (Validator.isNotNull(extension)) {
			sb.append(StringPool.PERIOD);
			sb.append(extension);
		}

		return new File(sb.toString());
	}

	public boolean delete(String file) {
		return delete(new File(file));
	}

	public boolean delete(File file) {
		if (file.exists()) {
			return file.delete();
		}
		else {
			return false;
		}
	}

	public void deltree(String directory) {
		deltree(new File(directory));
	}

	public void deltree(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] fileArray = directory.listFiles();

			for (int i = 0; i < fileArray.length; i++) {
				if (fileArray[i].isDirectory()) {
					deltree(fileArray[i]);
				}
				else {
					fileArray[i].delete();
				}
			}

			directory.delete();
		}
	}

	public boolean exists(String fileName) {
		return exists(new File(fileName));
	}

	public boolean exists(File file) {
		return file.exists();
	}

	public String extractText(InputStream is, String fileExt) {
		String text = null;

		try {
			fileExt = GetterUtil.getString(fileExt).toLowerCase();

			TextExtractor extractor = null;

			String contentType = null;
			String encoding = System.getProperty("encoding");

			if (fileExt.equals(".doc")) {
				extractor = new MsWordTextExtractor();

				contentType = "application/vnd.ms-word";
			}
//			else if (fileExt.equals(".htm") || fileExt.equals(".html")) {
//				extractor = new JerichoHTMLTextExtractor();
//
//				contentType = "text/html";
//			}
			else if (fileExt.equals(".odb") || fileExt.equals(".odf") ||
					 fileExt.equals(".odg") || fileExt.equals(".odp") ||
					 fileExt.equals(".ods") || fileExt.equals(".odt")) {

				extractor = new OpenOfficeTextExtractor();

				contentType = "application/vnd.oasis.opendocument.";

				if (fileExt.equals(".odb")) {
					contentType += "database";
				}
				else if (fileExt.equals(".odf")) {
					contentType += "formula";
				}
				else if (fileExt.equals(".odg")) {
					contentType += "graphics";
				}
				else if (fileExt.equals(".odp")) {
					contentType += "presentation";
				}
				else if (fileExt.equals(".ods")) {
					contentType += "spreadsheet";
				}
				else if (fileExt.equals(".odt")) {
					contentType += "text";
				}
			}
			else if (fileExt.equals(".pdf")) {
				extractor = new PdfTextExtractor();

				contentType = "application/pdf";
			}
			else if (fileExt.equals(".ppt")) {
				extractor = new MsPowerPointTextExtractor();

				contentType = "application/vnd.ms-powerpoint";
			}
			else if (fileExt.equals(".rtf")) {
				extractor = new RTFTextExtractor();

				contentType = "application/rtf";
			}
			else if (fileExt.equals(".txt")) {
				extractor = new PlainTextExtractor();

				contentType = "text/plain";
			}
			else if (fileExt.equals(".xls")) {
				extractor = new MsExcelTextExtractor();

				contentType = "application/vnd.ms-excel";
			}
			else if (fileExt.equals(".xml")) {
				extractor = new XMLTextExtractor();

				contentType = "text/xml";
			}

			if (extractor != null) {
//				if (_log.isInfoEnabled()) {
//					_log.info(
//						"Using extractor " + extractor.getClass().getName() +
//							" for extension " + fileExt);
//				}

				StringBuilder sb = new StringBuilder();

				BufferedReader reader = new BufferedReader(
					extractor.extractText(is, contentType, encoding));

				int i;

				while ((i = reader.read()) != -1) {
					sb.append((char)i);
				}

				reader.close();

				text = sb.toString();
			}
			else {
//				if (_log.isInfoEnabled()) {
//					_log.info("No extractor found for extension " + fileExt);
//				}
			}
		}
		catch (Exception e) {
                    logger.warn(e.getMessage(), e);
//			_log.error(e);
		}

//		if (_log.isDebugEnabled()) {
//			_log.debug("Extractor returned text:\n\n" + text);
//		}

		if (text == null) {
			text = StringPool.BLANK;
		}

		return text;
	}

	public String getAbsolutePath(File file) {
		return StringUtil.replace(
			file.getAbsolutePath(), StringPool.BACK_SLASH, StringPool.SLASH);
	}

	public byte[] getBytes(File file) throws IOException {
		if ((file == null) || !file.exists()) {
			return null;
		}

		FileInputStream is = new FileInputStream(file);

		byte[] bytes = getBytes(is, (int)file.length());

		is.close();

		return bytes;
	}

	public byte[] getBytes(InputStream is) throws IOException {
		return getBytes(is, -1);
	}

	public byte[] getBytes(InputStream is, int bufferSize) throws IOException {
		ByteArrayMaker bam = null;

		if (bufferSize <= 0) {
			bam = new ByteArrayMaker();
		}
		else {
			bam = new ByteArrayMaker(bufferSize);
		}

		boolean createBuffered = false;

		try {
			if (!(is instanceof BufferedInputStream)) {
				is = new BufferedInputStream(is);

				createBuffered = true;
			}

			int c = is.read();

			while (c != -1) {
				bam.write(c);

				c = is.read();
			}
		}
		finally {
			if (createBuffered) {
				is.close();
			}
		}

		bam.close();

		return bam.toByteArray();
	}

	public String getExtension(String fileName) {
		if (fileName == null) {
			return null;
		}

		int pos = fileName.lastIndexOf(StringPool.PERIOD);

		if (pos != -1) {
			return fileName.substring(pos + 1, fileName.length()).toLowerCase();
		}
		else {
			return null;
		}
	}

	public String getPath(String fullFileName) {
		int pos = fullFileName.lastIndexOf(StringPool.SLASH);

		if (pos == -1) {
			pos = fullFileName.lastIndexOf(StringPool.BACK_SLASH);
		}

		String shortFileName = fullFileName.substring(0, pos);

		if (Validator.isNull(shortFileName)) {
			return StringPool.SLASH;
		}

		return shortFileName;
	}

	public String getShortFileName(String fullFileName) {
		int pos = fullFileName.lastIndexOf(StringPool.SLASH);

		if (pos == -1) {
			pos = fullFileName.lastIndexOf(StringPool.BACK_SLASH);
		}

		String shortFileName =
			fullFileName.substring(pos + 1, fullFileName.length());

		return shortFileName;
	}

	public boolean isAscii(File file) throws IOException {
		boolean ascii = true;

//		nsDetector detector = new nsDetector(nsPSMDetector.ALL);
//
//		BufferedInputStream bis = new BufferedInputStream(
//			new FileInputStream(file));
//
//		byte[] buffer = new byte[1024];
//
//		int len = 0;
//
//		while ((len = bis.read(buffer, 0, buffer.length)) != -1) {
//			if (ascii) {
//				ascii = detector.isAscii(buffer, len);
//
//				if (!ascii) {
//					break;
//				}
//			}
//		}
//
//		detector.DataEnd();

		return ascii;
	}

	public String[] listDirs(String fileName) {
		return listDirs(new File(fileName));
	}

	public String[] listDirs(File file) {
		List<String> dirs = new ArrayList<String>();

		File[] fileArray = file.listFiles();

		for (int i = 0; (fileArray != null) && (i < fileArray.length); i++) {
			if (fileArray[i].isDirectory()) {
				dirs.add(fileArray[i].getName());
			}
		}

		return dirs.toArray(new String[dirs.size()]);
	}

	public String[] listFiles(String fileName) {
		if (Validator.isNull(fileName)) {
			return new String[0];
		}

		return listFiles(new File(fileName));
	}

	public String[] listFiles(File file) {
		List<String> files = new ArrayList<String>();

		File[] fileArray = file.listFiles();

		for (int i = 0; (fileArray != null) && (i < fileArray.length); i++) {
			if (fileArray[i].isFile()) {
				files.add(fileArray[i].getName());
			}
		}

		return files.toArray(new String[files.size()]);
	}

	public void mkdirs(String pathName) {
		File file = new File(pathName);

		file.mkdirs();
	}

	public boolean move(String sourceFileName, String destinationFileName) {
		return move(new File(sourceFileName), new File(destinationFileName));
	}

	public boolean move(File source, File destination) {
		if (!source.exists()) {
			return false;
		}

		destination.delete();

		return source.renameTo(destination);
	}

	public String read(String fileName) throws IOException {
		return read(new File(fileName));
	}

	public String read(File file) throws IOException {
		return read(file, false);
	}

	public String read(File file, boolean raw) throws IOException {
		FileInputStream fis = new FileInputStream(file);

		byte[] bytes = new byte[fis.available()];

		fis.read(bytes);

		fis.close();

		String s = new String(bytes, StringPool.UTF8);

		if (raw) {
			return s;
		}
		else {
			return StringUtil.replace(
				s, StringPool.RETURN_NEW_LINE, StringPool.NEW_LINE);
		}
	}

	public String replaceSeparator(String fileName) {
		return StringUtil.replace(
			fileName, StringPool.BACK_SLASH, StringPool.SLASH);
	}

	public File[] sortFiles(File[] files) {
		if (files == null) {
			return null;
		}

		Arrays.sort(files, new FileComparator());

		List<File> directoryList = new ArrayList<File>();
		List<File> fileList = new ArrayList<File>();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				directoryList.add(files[i]);
			}
			else {
				fileList.add(files[i]);
			}
		}

		directoryList.addAll(fileList);

		return directoryList.toArray(new File[directoryList.size()]);
	}

	public String stripExtension(String fileName) {
		if (fileName == null) {
			return null;
		}

		int pos = fileName.lastIndexOf(StringPool.PERIOD);

		if (pos != -1) {
			return fileName.substring(0, pos);
		}
		else {
			return fileName;
		}
	}

	public List<String> toList(Reader reader) {
		List<String> list = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(reader);

			String line = null;

			while ((line = br.readLine()) != null) {
				list.add(line);
			}

			br.close();
		}
		catch (IOException ioe) {
                    logger.warn(ioe.getMessage(), ioe);
		}

		return list;
	}

	public List<String> toList(String fileName) {
		try {
			return toList(new FileReader(fileName));
		}
		catch (IOException ioe) {
			return new ArrayList<String>();
		}
	}

	public Properties toProperties(FileInputStream fis) {
		Properties props = new Properties();

		try {
			props.load(fis);
		}
		catch (IOException ioe) {
                    logger.warn(ioe.getMessage(), ioe);
		}

		return props;
	}

	public Properties toProperties(String fileName) {
		try {
			return toProperties(new FileInputStream(fileName));
		}
		catch (IOException ioe) {
			return new Properties();
		}
	}

	public void write(String fileName, String s) throws IOException {
		write(new File(fileName), s);
	}

	public void write(String fileName, String s, boolean lazy)
		throws IOException {

		write(new File(fileName), s, lazy);
	}

	public void write(String fileName, String s, boolean lazy, boolean append)
		throws IOException {

		write(new File(fileName), s, lazy, append);
	}

	public void write(String pathName, String fileName, String s)
		throws IOException {

		write(new File(pathName, fileName), s);
	}

	public void write(String pathName, String fileName, String s, boolean lazy)
		throws IOException {

		write(new File(pathName, fileName), s, lazy);
	}

	public void write(
			String pathName, String fileName, String s, boolean lazy,
			boolean append)
		throws IOException {

		write(new File(pathName, fileName), s, lazy, append);
	}

	public void write(File file, String s) throws IOException {
		write(file, s, false);
	}

	public void write(File file, String s, boolean lazy)
		throws IOException {

		write(file, s, lazy, false);
	}

	public void write(File file, String s, boolean lazy, boolean append)
		throws IOException {

		if (file.getParent() != null) {
			mkdirs(file.getParent());
		}

		if (lazy && file.exists()) {
			String content = read(file);

			if (content.equals(s)) {
				return;
			}
		}

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream(file, append), StringPool.UTF8));

		bw.write(s);

		bw.close();
	}

	public void write(String fileName, byte[] bytes) throws IOException {
		write(new File(fileName), bytes);
	}

	public void write(File file, byte[] bytes) throws IOException {
		if (file.getParent() != null) {
			mkdirs(file.getParent());
		}

		FileOutputStream fos = new FileOutputStream(file);

		fos.write(bytes);

		fos.close();
	}

	public void write(String fileName, InputStream is) throws IOException {
		write(fileName, getBytes(is));
	}

	public void write(File file, InputStream is) throws IOException {
		write(file, getBytes(is));
	}

//	private static Log _log = LogFactoryUtil.getLog(FileImpl.class);

	private static FileImpl _instance = new FileImpl();

}

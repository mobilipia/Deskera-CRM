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

package com.krawler.common.util;

/**
 *
 * @author sagar
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressService {
  public static void gzipFile(String from, String to) throws IOException {
    FileInputStream in = new FileInputStream(from);
    GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(to));
    byte[] buffer = new byte[4096];
    int bytesRead;
    while ((bytesRead = in.read(buffer)) != -1)
      out.write(buffer, 0, bytesRead);
    in.close();
    out.close();
  }

  /** Zip the contents of the directory, and save it in the zipfile */
  public static void zipDirectory(String dir, String zipfile)
      throws IOException, IllegalArgumentException {
    // Check that the directory is a directory, and get its contents
    File d = new File(dir);
    if (!d.isDirectory())
      throw new IllegalArgumentException("Not a directory:  "
          + dir);
    String[] entries = d.list();
    byte[] buffer = new byte[4096]; // Create a buffer for copying
    int bytesRead;

    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));

    for (int i = 0; i < entries.length; i++) {
      File f = new File(d, entries[i]);
      if (f.isDirectory())
        continue;//Ignore directory
      FileInputStream in = new FileInputStream(f); // Stream to read file
      ZipEntry entry = new ZipEntry(f.getName()); // Make a ZipEntry
      out.putNextEntry(entry); // Store entry
      while ((bytesRead = in.read(buffer)) != -1)
        out.write(buffer, 0, bytesRead);
      in.close();
    }
    out.close();
  }

  public static void main(String args[]) throws IOException {
    String from = "/home/sagar/store/CRM/companybackup/Demo_2011-01-05";
    File f = new File(from);
    boolean directory = f.isDirectory(); // Is it a file or directory?

    CompressService.zipDirectory(from, from + ".zip");
//    CompressService.gzipFile(from, from + ".gz");
  }
}

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
package com.krawler.esp.handlers;

import com.krawler.common.util.StringUtil;

public class FileHandler {     

     public static int getSizeKb(String size) {
        int no = ((Integer.parseInt(size)) / 1024);
        if (no >= 1) {
            return no;
        } else {
            return 1;
        }
    }
    public static String getFileImage(String fileName,int flag) {
            String image = "";
            String ext = "";
            if(fileName.lastIndexOf(".") != -1) {
                ext = fileName.substring(fileName.lastIndexOf("."));
                if(!StringUtil.isNullOrEmpty(ext)) {
                    ext = ext.substring(1);
                    ext = ext.toLowerCase().trim();
                    if (ext.equals("avi") ||
                            ext.equals("wmv") ||
                            ext.equals("mpeg")) {
                        image = "video";
                    } else if (ext.equals("ogg") ||
                            ext.equals("wma") ||
                            ext.equals("mp3")) {
                        image = "audio";
                    } else if (ext.equals("pdf")) {
                        image = "pdf";
                    } else if (ext.equals("doc") ||
                            ext.equals("docx")) {
                        image = "word";
                    } else if (ext.equals("txt")) {
                        image = "text";
                    } else if (ext.equals("html") ||
                            ext.equals("css") ||
                            ext.equals("jsp") ||
                            ext.equals("php")) {
                        image = "web";
                    } else if (ext.equals("tar") ||
                            ext.equals("rar") ||
                            ext.equals("zip") ||
                            ext.equals("gzip")) {
                        image = "zip";
                    } else if (ext.equals("jpg") ||
                            ext.equals("jpeg") ||
                            ext.equals("png") ||
                            ext.equals("svg")) {
                        image = "image";
                    } else {
                        image = "file";
                    }
                } else {
                    image = "file";
                }
            }
            else {
                image = "file";
            }

            if(flag == 1) {
                image += "60.png";
            } else if(flag == 2){
                image += "16.png";
            }
            return image;
   }

}

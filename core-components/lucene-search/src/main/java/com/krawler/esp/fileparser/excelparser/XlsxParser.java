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

package com.krawler.esp.fileparser.excelparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.log4j.Logger;


/**
 *
 * @author sagar
 */
public class XlsxParser {

    private static final Logger logger = Logger.getLogger(XlsxParser.class);

    public String extractText(String filepath) throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(filepath);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFExcelExtractor es = new XSSFExcelExtractor(workbook);
            sb.append(es.getText());

        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return sb.toString();
    }

//public static void main(String args[]){
//    try {
//        String plainText = extractTextXlsx("/home/sagar/Desktop/Leads.xls");
//        System.out.println(plainText);
//
////        String plainText = extractText("/home/sagar/Desktop/Test.docx");
////        System.out.println(plainText);
//    } catch(Exception e) {
//        logger.warn(e.getMessage(), e);
//    }
//}
    
}

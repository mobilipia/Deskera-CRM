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
package com.krawler.esp.fileparser.wordparser;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.List;
import java.util.ArrayList;

//import org.apache.poi.hdgf.extractor.VisioTextExtractor;
//import org.apache.poi.hslf.extractor.PowerPointExtractor;
//import org.apache.poi.hwpf.extractor.WordExtractor;
//import org.apache.poi.poifs.filesystem.POIFSFileSystem;
//import org.apache.poi.ss.extractor.ExcelExtractor;

import org.apache.commons.logging.Log;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.POITextExtractor;

import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.POIOLE2TextExtractor;
import org.apache.log4j.Logger;

public class DocxParser {
    
      private static final Logger logger = Logger.getLogger(DocxParser.class);

      public String extractText(String filepath) {
            StringBuilder sb = new StringBuilder();

            ZipFile docxfile = null;
            try{
              docxfile = new ZipFile(filepath);
            }catch(Exception e){
              // file corrupt or otherwise could not be found
              logger.warn(e.getMessage(), e);
              return sb.toString();
            }
            InputStream in = null;
            try{
              ZipEntry ze = docxfile.getEntry("word/document.xml");
              in = docxfile.getInputStream(ze);
            }catch(NullPointerException nulle){
              System.err.println("Expected entry word/document.xml does not exist");
              logger.warn(nulle.getMessage(), nulle);
              return sb.toString();
            }catch(IOException ioe){
              logger.warn(ioe.getMessage(), ioe);
              return sb.toString();
            }
            Document document = null;
            try{
              DocumentBuilderFactory factory =
              DocumentBuilderFactory.newInstance();
              DocumentBuilder builder =
              factory.newDocumentBuilder();
              document = builder.parse(in);
            }catch(ParserConfigurationException pce){
              logger.warn(pce.getMessage(), pce);
              return sb.toString();
            }catch(SAXException sex){
              sex.printStackTrace();
              return sb.toString();
            }catch(IOException ioe){
              logger.warn(ioe.getMessage(), ioe);
              return sb.toString();
            }finally{
              try{
                docxfile.close();
              }catch(IOException ioe){
                System.err.println("Exception closing file.");
                logger.warn(ioe.getMessage(), ioe);
              }
            }
            NodeList list = document.getElementsByTagName("w:t");
            List<String> content = new ArrayList<String>();
            for(int i=0;i<list.getLength();i++){
              Node aNode = list.item(i);
              content.add(aNode.getFirstChild().getNodeValue());
            }
            for(String s : content){
                sb.append(s);
            }

            return sb.toString();
      }

    public String extractTextDocx(String filepath) throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(filepath);
            POIOLE2TextExtractor poitex = ExtractorFactory.createExtractor(new POIFSFileSystem(fis));
            sb.append(poitex.getText());

    //        XWPFDocument doc = new XWPFDocument(fis);
    //        XWPFWordExtractor ex = new XWPFWordExtractor(doc);
    //        sb.append(ex.getText());

        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return sb.toString();
    }

//public static String extractText(String filepath) throws FileNotFoundException, IOException {
//    StringBuilder sb = new StringBuilder();
//    FileInputStream fis = new FileInputStream(filepath);
//    POIFSFileSystem fileSystem = new POIFSFileSystem(fis);
//    // Firstly, get an extractor for the Workbook
//    POIOLE2TextExtractor oleTextExtractor =
//       ExtractorFactory.createExtractor(fileSystem);
//    // Then a List of extractors for any embedded Excel, Word, PowerPoint
//    // or Visio objects embedded into it.
//    POITextExtractor[] embeddedExtractors =
//       ExtractorFactory.getEmbededDocsTextExtractors(oleTextExtractor);
//    for (POITextExtractor textExtractor : embeddedExtractors) {
//       // If the embedded object was an Excel spreadsheet.
//       if (textExtractor instanceof ExcelExtractor) {
//          ExcelExtractor excelExtractor = (ExcelExtractor) textExtractor;
//          sb.append(excelExtractor.getText());
//       }
//       // A Word Document
//       else if (textExtractor instanceof WordExtractor) {
//          WordExtractor wordExtractor = (WordExtractor) textExtractor;
//          String[] paragraphText = wordExtractor.getParagraphText();
//          for (String paragraph : paragraphText) {
//             sb.append(paragraph);
//          }
//          // Display the document's header and footer text
//          sb.append("Footer text: " + wordExtractor.getFooterText());
//          sb.append("Header text: " + wordExtractor.getHeaderText());
//       }
//       // PowerPoint Presentation.
//       else if (textExtractor instanceof PowerPointExtractor) {
//          PowerPointExtractor powerPointExtractor =
//             (PowerPointExtractor) textExtractor;
//          sb.append("Text: " + powerPointExtractor.getText());
//          sb.append("Notes: " + powerPointExtractor.getNotes());
//       }
//       // Visio Drawing
//       else if (textExtractor instanceof VisioTextExtractor) {
//          VisioTextExtractor visioTextExtractor =
//             (VisioTextExtractor) textExtractor;
//          sb.append("Text: " + visioTextExtractor.getText());
//       }
//    }
//    return sb.toString();
//}

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

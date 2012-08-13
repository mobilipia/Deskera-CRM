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
package com.krawler.esp.servlets;

import com.krawler.common.util.StringUtil;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
    import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
/**
 *
 * @author krawler
 */
public class exportExcel{
	private static final int MAX_CELL_WIDTH = 65280;
	private static Log log = LogFactory.getLog(exportExcel.class);
   private HSSFWorkbook wb;
   private boolean outputreport = false;
   private CellStyle dateCellStyle;
   private Calendar cal = Calendar.getInstance();
   CreationHelper createHelper; 
   public exportExcel(){
    wb = new HSSFWorkbook();
    dateCellStyle=wb.createCellStyle();
    createHelper = wb.getCreationHelper();
    dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
   }
   
   public void setTimeZone(TimeZone tz){
	   cal.setTimeZone(tz);
   }


   public void setOutput(boolean flag){
       this.outputreport = flag;
   }
    /** Processes requests for both HTTP GET and POST methods.
     * @param request servlet request
     * @param response servlet response
     */

      
       /** outs xls file with the name specified on the response output stream.
     * @param response servlet response.
     * @param rs java.sql.ResultSet object.
     * @param ht java.util.Hashtable object for column header where key is actual columnname as in ResultSet rs.
     * @param sheettitle String to display as a sheet title.
     * @param filename String filename for the xls file <strong>(without extension)</strong>.
     * @return void
     */

        public HashMap extractData(JSONObject obj) throws JSONException{
            HashMap hm = new HashMap();
            if(obj.has("data")){
                JSONArray jArr=obj.getJSONArray("data");
                hm.put("data", jArr);
                if(jArr.length()>0)
                    hm.put("header", jArr.getJSONObject(0).names());
                else
                    hm.put("header", new JSONArray());
            } else if(obj.has("coldata")){
                JSONArray jArr=obj.getJSONArray("coldata");
                hm.put("data", jArr);
                if(jArr.length()>0)
                    hm.put("header", jArr.getJSONObject(0).names());
                else
                    hm.put("header", new JSONArray());
            }
            return hm;
        }
        public void exportexcel(HttpServletResponse response, JSONObject jobj, java.util.Hashtable ht, String sheetTitle, String fileName,JSONArray hdr ,JSONArray xlshdr,String heading,String[] xtypeArr,com.krawler.spring.exportFunctionality.exportDAOImpl exportDao) throws ServletException, IOException {
            try {
                response.setContentType("application/vnd.ms-excel");
                if(!StringUtil.isNullOrEmpty(heading)){
                    fileName = heading + fileName;
                }
                response.setHeader("Content-Disposition", "attachement; filename=" + fileName + ".xls");
                HSSFSheet sheet = wb.createSheet(sheetTitle);
                CellStyle cs = wb.createCellStyle();
                cs.setWrapText(true);

                HSSFHeader hh = sheet.getHeader();
                int j = 1;
                int width = 0;
                int maxrowno=0;
                HSSFRow row1 = sheet.createRow((short) maxrowno);
                HashMap hm=extractData(jobj);
                JSONArray jarr = (JSONArray)hm.get("data");
                JSONObject tempObj;
                for(int k =0 ; k < jarr.length() ; k++) {
                    tempObj = jarr.getJSONObject(k);
                    HSSFRow row = sheet.createRow((short) j);
                    int cellcount = 0;
                    for (int i = 0; i < hdr.length(); i++) {
                        Object str = tempObj.optString(hdr.getString(i) ,"");
                        try {
                            if(xtypeArr.length > 0) {
                                str = convertValue(tempObj.optString(hdr.getString(i) ,""),xtypeArr[i]);
                            }
                        } catch(Exception e) {

                        }
                        if (ht.containsValue(hdr.getString(i))) {
                            if (j == maxrowno+1) {
                                HSSFCell cell1 = row1.createCell(cellcount);
                                cell1.setCellStyle(cs);

                                width = xlshdr.getString(i).length() * 325;
                                if (width > sheet.getColumnWidth(cellcount)) {
                                    sheet.setColumnWidth(cellcount, width);
                                }
                                HSSFFont font = wb.createFont();
                                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                                HSSFRichTextString hst = new HSSFRichTextString(xlshdr.getString(i));
                                hst.applyFont(font);
                                cell1.setCellValue(hst);

                            }
                            HSSFCell cell = row.createCell(cellcount);
                            cell.setCellStyle(cs);
                            
                            
                            if(str instanceof Date){
                            	cal.setTime((Date)str);
                            	cell.setCellValue(cal);
                            	cell.setCellStyle(this.dateCellStyle);
                            	width=4500;
                            }else if(str instanceof Number){
                            	cell.setCellValue(((Number)str).doubleValue());
                            	width=4500;
                        	}else{
                            	String colvalue = str.toString();
                            	cell.setCellValue(new HSSFRichTextString(colvalue));
                            	width = colvalue.length() * 325;
                            }
                            
                            width = Math.min(width, MAX_CELL_WIDTH);
                            
                            if (width > sheet.getColumnWidth(cellcount)) {
                                sheet.setColumnWidth(cellcount, width);
                            }
                            
                            cellcount++;
                        }
                    }
                    j++;

                }

                ConfigReader cr=ConfigReader.getinstance();
                String dirpath=cr.get("store");
                String path = dirpath+"baitheader.png";

//                this.addimage(path,HSSFWorkbook.PICTURE_TYPE_PNG, wb, sheet,0,0,0,0,0,0,12,4);
                if (true) {
                    OutputStream out = response.getOutputStream();
                    wb.write(out);
                    out.close();
                }
            } catch (JSONException ex) {
            Logger.getLogger(exportExcel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
            Logger.getLogger(exportExcel.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    	public Object convertValue(String value, String xtype) {
    		Object val=value;
    		try {
    			if ("datefield".equals(xtype) && value.length() > 0) {
    				val = new Date(Long.parseLong(value));
    			}
    			if ("numberfield".equals(xtype) && value.length() > 0) {
    				val = Double.parseDouble(value);
    			}
    		} catch (Exception e) {
    			log.warn("Cannot format value", e);
    		}
    		return val;
    	}
    	
       public void addimage(String imagepath,int pictype,HSSFWorkbook wb,HSSFSheet sheet,int dx1,int dy1,int dx2, int dy2,int col1,int row1,int col2,int row2)
                throws IOException{
            FileInputStream fimage=null;
            ByteArrayOutputStream bos=null;
            try{
                fimage=new FileInputStream(imagepath);
                bos = new ByteArrayOutputStream( );
                int c;
                while ( (c = fimage.read()) != -1)
                    bos.write( c );
            }catch(IOException e){
                e.printStackTrace();
                System.out.println(e);
            }
            finally{
                fimage.close();
            }

            int imgindex=wb.addPicture(bos.toByteArray(),pictype);
            HSSFPatriarch patriarch=sheet.createDrawingPatriarch();
            HSSFClientAnchor anchor;
            anchor=new HSSFClientAnchor(dx1,dy1,dx2,dy2,(short)col1,row1,(short)col2,row2);
            anchor.setAnchorType(2);
            patriarch.createPicture(anchor, imgindex);
        }


    protected void processRequest(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/vnd.ms-excel");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("new sheet");

        // Create a row and put some cells in it. Rows are 0 based.
        HSSFRow row     = sheet.createRow(0);
        HSSFCell cell   = row.createCell(0);
        cell.setCellValue(1);

        // Or do it on one line.
        row.createCell(1).setCellValue(1.2);

        row.createCell(3).setCellValue(true);
        // Write the output
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
    }
}

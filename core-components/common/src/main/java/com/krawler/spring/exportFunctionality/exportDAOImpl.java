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
package com.krawler.spring.exportFunctionality;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import java.net.URLDecoder;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import static com.krawler.esp.web.resource.Links.UnprotectedLoginPageFull;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.crm.fontsettings.FontContext;
import com.krawler.crm.fontsettings.FontFamily;
import com.krawler.crm.fontsettings.FontFamilySelector;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class exportDAOImpl {
	private static final Log log = LogFactory.getLog(exportDAOImpl.class);
    private HibernateTemplate hibernateTemplate;
    private storageHandlerImpl storageHandlerImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private EnglishNumberToWords EnglishNumberToWordsOjb = new EnglishNumberToWords();

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }
    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj1) {
        this.storageHandlerImplObj = storageHandlerImplObj1;
    }
    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
  
    private static FontFamilySelector fontFamilySelector=new FontFamilySelector();
    private static String imgPath = "";
    private static String companyName = "";
    private static com.krawler.utils.json.base.JSONObject config = null;
    private PdfPTable header = null;
    private PdfPTable footer = null;
    private static final long serialVersionUID = -8401651817881523209L;
    private DateFormat dFmt = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat tFmt = new SimpleDateFormat("HH:mm");
    private static String errorMsg = "";
    private String tdiff;
    private static final Log LOGGER = LogFactory.getLog(exportDAOImpl.class);
    static{
    	FontFamily fontFamily=new FontFamily();
    	fontFamily.addFont(FontContext.HEADER_NOTE, FontFactory.getFont("Helvetica", 10, Font.BOLD, Color.GRAY));
    	fontFamily.addFont(FontContext.FOOTER_NOTE, FontFactory.getFont("Helvetica", 12, Font.BOLD, Color.GRAY));
    	fontFamily.addFont(FontContext.LOGO_TEXT, FontFactory.getFont("Times New Roman", 14, Font.NORMAL, Color.BLACK));
    	fontFamily.addFont(FontContext.REPORT_TITLE, FontFactory.getFont("Times New Roman", 20, Font.BOLD, Color.BLACK));
    	fontFamily.addFont(FontContext.SMALL_TEXT, FontFactory.getFont("Times New Roman", 12, Font.NORMAL, Color.BLACK));
    	fontFamily.addFont(FontContext.TABLE_HEADER, FontFactory.getFont("Times New Roman", 14, Font.BOLD, Color.BLACK));
    	fontFamily.addFont(FontContext.TABLE_DATA, FontFactory.getFont("Times New Roman", 12, Font.NORMAL, Color.BLACK));
    	fontFamily.addFont(FontContext.NOTE_TEXT, FontFactory.getFont("Times New Roman", 12, Font.BOLD, Color.BLACK));
    	fontFamilySelector.addFontFamily(fontFamily);
    	
    	File[] files;
		try {
			File f = new File(exportDAOImpl.class.getClassLoader().getResource("fonts").toURI());
			files = f.listFiles(new FilenameFilter() {				
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".ttf");
					}
				});
		} catch (Exception e1) {
			log.warn("error: "+e1.getMessage());
			files = new File[]{};
		}
	for(File file:files){
		try {
				BaseFont bfnt = BaseFont.createFont(file.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				fontFamily=new FontFamily();
				fontFamily.addFont(FontContext.HEADER_NOTE, new Font(bfnt, 10, Font.BOLD, Color.GRAY));
		    	fontFamily.addFont(FontContext.FOOTER_NOTE, new Font(bfnt, 12, Font.BOLD, Color.GRAY));
		    	fontFamily.addFont(FontContext.LOGO_TEXT, new Font(bfnt, 14, Font.NORMAL, Color.BLACK));
		    	fontFamily.addFont(FontContext.REPORT_TITLE, new Font(bfnt, 20, Font.BOLD, Color.BLACK));
		    	fontFamily.addFont(FontContext.SMALL_TEXT, new Font(bfnt, 12, Font.NORMAL, Color.BLACK));
		    	fontFamily.addFont(FontContext.TABLE_HEADER, new Font(bfnt, 14, Font.BOLD, Color.BLACK));
		    	fontFamily.addFont(FontContext.TABLE_DATA, new Font(bfnt, 12, Font.NORMAL, Color.BLACK));
		    	fontFamily.addFont(FontContext.NOTE_TEXT, new Font(bfnt, 12, Font.BOLD, Color.BLACK));
		    	fontFamilySelector.addFontFamily(fontFamily);
			} catch (Exception e) {
					log.warn("Font ("+file.getName()+") not available : "+e.getMessage());
			}
	}  	
   
  } 

    public class EndPage extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Rectangle page = document.getPageSize();
                try {
                    getHeaderFooter(document);
                } catch (ServiceException ex) {
                    log.warn("Error exporting file:"+ex.getMessage(), ex);
                }
                // Add page header
                header.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                header.writeSelectedRows(0, -1, document.leftMargin(), page.getHeight() - 10, writer.getDirectContent());

                // Add page footer
                footer.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin() - 5, writer.getDirectContent());

                // Add page border
                if (config.getBoolean("pageBorder")) {
                    int bmargin = 8;  //border margin
                    PdfContentByte cb = writer.getDirectContent();
                    cb.rectangle(bmargin, bmargin, page.getWidth() - bmargin * 2, page.getHeight() - bmargin * 2);
                    cb.setColorStroke(Color.LIGHT_GRAY);
                    cb.stroke();
                }

            } catch (JSONException e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response, JSONObject jobj) throws ServiceException, IOException {
        ByteArrayOutputStream baos = null;
        String filename = request.getParameter("name");
        String heading=request.getParameter("heading");
        String fileType = null;
        JSONObject grid = null;
        JSONArray gridmap = null;
        try {
        	this.tdiff = sessionHandlerImplObj.getTimeZoneDifference(request);
        	//populateDateFormats(request);	//commented as suggested by Sagar Ahire to use fixed format instead of User's selected
            fileType = request.getParameter("filetype");
            if (request.getParameter("gridconfig") != null) {
                grid = new JSONObject(request.getParameter("gridconfig"));
                gridmap = grid.getJSONArray("data");
            }
            if (StringUtil.equal(fileType, "csv")) {
                createCsvFile(request, response, jobj);
            } else if (StringUtil.equal(fileType, "pdf")) {
                baos = getPdfData(gridmap, request, jobj);
                writeDataToFile(filename,heading,fileType, baos, response);
            } else if (StringUtil.equal(fileType, "xls")) {
               createXlsFile(request, response, jobj);
            } else if (StringUtil.equal(fileType, "print")) {
                createPrinPriviewFile(request, response, jobj);
            }
        } catch (ServiceException ex) {
            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>alert('Failed to Download Document. "+errorMsg+"');</script>");
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (Exception ex) {
            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>alert('Failed to Download Document. "+errorMsg+"');</script>");
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
    }

    public void writeDataToFile(String filename,String heading, String fileType, ByteArrayOutputStream baos, HttpServletResponse response) throws ServiceException {
    	try {
            if(!StringUtil.isNullOrEmpty(heading)){
                filename = heading + filename;
            }
            response.setHeader("Content-Disposition", "attachment; filename=\""+filename + "." + fileType + "\"");
            response.setContentType("application/octet-stream");
            response.setContentLength(baos.size());
            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
            try {
                response.getOutputStream().println("{\"valid\": false}");
            } catch (IOException ex) {
            	log.warn("Error exporting file:"+ex.getMessage(), ex);
            }
        }
    }

    public void addComponyLogo(Document d, HttpServletRequest request) throws ServiceException {
        try {
            PdfPTable table = new PdfPTable(1);
            imgPath = getImgPath(request);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.setWidthPercentage(50);
            PdfPCell cell = null;
            try {
                Image img = Image.getInstance(imgPath);
                cell = new PdfPCell(img);
            } catch (Exception e) {
                companyName = sessionHandlerImplObj.getCompanyName(request);
                cell = new PdfPCell(new Paragraph(fontFamilySelector.process(companyName, FontContext.LOGO_TEXT)));
            }
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            d.add(table);
        } catch (Exception e) {
            throw ServiceException.FAILURE("exportDAOImpl.addComponyLogo", e);
        }
    }

    public String getImgPath(HttpServletRequest req) throws SessionExpiredException {
        String requestedFileName = "";
        String companyId = null;
        try {
            companyId = sessionHandlerImplObj.getCompanyid(req);
        } catch (Exception ee) {
        }
        if (StringUtil.isNullOrEmpty(companyId)) {
            String domain = URLUtil.getDomainName(req);
            if (!StringUtil.isNullOrEmpty(domain)) {
                companyId = sessionHandlerImplObj.getCompanyid(req);
                requestedFileName = "/original_" + companyId + ".png";
            } else {
                requestedFileName = "logo.gif";
            }
        } else {
            requestedFileName = companyId + ".png";
        }
        String fileName = storageHandlerImplObj.GetProfileImgStorePath() + requestedFileName;
        return fileName;
    }

    public void addTitleSubtitle(Document d) throws ServiceException {
        try {
            java.awt.Color tColor = new Color(Integer.parseInt(config.getString("textColor"), 16));
            PdfPTable table = new PdfPTable(1);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.setWidthPercentage(100);
            table.setSpacingBefore(6);

            //Report Title
            PdfPCell cell = new PdfPCell(new Paragraph(fontFamilySelector.process(config.getString("title"),FontContext.REPORT_TITLE,tColor)));//fontBold));
            cell.setBorder(0);
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            //Report Subtitle(s)
            String[] SubTitles = config.getString("subtitles").split("~");// '~' as separator
            for (int i = 0; i < SubTitles.length; i++) {
                cell = new PdfPCell(new Paragraph(fontFamilySelector.process(SubTitles[i], FontContext.REPORT_TITLE,tColor)));
                cell.setBorder(0);
                cell.setBorderWidth(0);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
            table.setSpacingAfter(6);
            d.add(table);

            //Separator line
            PdfPTable line = new PdfPTable(1);
            line.setWidthPercentage(100);
            PdfPCell cell1 = null;
            cell1 = new PdfPCell(new Paragraph(""));
            cell1.setBorder(PdfPCell.BOTTOM);
            line.addCell(cell1);
            d.add(line);
        } catch (Exception e) {
            throw ServiceException.FAILURE("exportDAOImpl.addTitleSubtitle", e);
        }
    }

    public int addTable(int stcol, int stpcol, int strow, int stprow, JSONArray store, String[] colwidth2, String[] colHeader, String[] widths, String[] align,String[] xtype, Document document) throws ServiceException {
        try {
            java.awt.Color tColor = new Color(Integer.parseInt(config.getString("textColor"), 16));
            PdfPTable table;
            float[] tcol;
            tcol = new float[colHeader.length + 1];
            tcol[0] = 40;
            for (int i = 1; i < colHeader.length + 1; i++) {
                tcol[i] = Float.parseFloat(widths[i - 1]);
            }
            table = new PdfPTable(colHeader.length + 1);
            table.setWidthPercentage(tcol, document.getPageSize());

            table.setSpacingBefore(15);
          
            PdfPCell h2 = new PdfPCell(new Paragraph(fontFamilySelector.process("No.",FontContext.TABLE_HEADER,tColor)));
            if (config.getBoolean("gridBorder")) {
                h2.setBorder(PdfPCell.BOX);
            } else {
                h2.setBorder(0);
            }
            h2.setPadding(4);
            h2.setBorderColor(Color.GRAY);
            h2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(h2);
            PdfPCell h1 = null;
            for (int hcol = stcol; hcol < colwidth2.length; hcol++) {
                h1 = new PdfPCell(new Paragraph(fontFamilySelector.process(colHeader[hcol],FontContext.TABLE_HEADER,tColor)));
                h1.setHorizontalAlignment(Element.ALIGN_CENTER);
                if (config.getBoolean("gridBorder")) {
                    h1.setBorder(PdfPCell.BOX);
                } else {
                    h1.setBorder(0);
                }
                h1.setBorderColor(Color.GRAY);
                h1.setPadding(4);
                table.addCell(h1);
            }
            table.setHeaderRows(1);

            for (int row = strow; row < stprow; row++) {
                h2 = new PdfPCell(new Paragraph(fontFamilySelector.process(String.valueOf(row + 1),FontContext.TABLE_DATA,tColor)));
                if (config.getBoolean("gridBorder")) {
                    h2.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                } else {
                    h2.setBorder(0);
                }
                h2.setPadding(4);
                h2.setBorderColor(Color.GRAY);
                h2.setHorizontalAlignment(Element.ALIGN_CENTER);
                h2.setVerticalAlignment(Element.ALIGN_CENTER);
                table.addCell(h2);

                JSONObject temp = store.getJSONObject(row);
                for (int col = 0; col < colwidth2.length; col++) {
                    String str = temp.optString((colwidth2[col]),"");
                    try {
                        if(xtype.length > 0) {
                            str = formatValue(temp.optString((colwidth2[col]),""),xtype[col]);
                        }
                    } catch(Exception e) {

                    }
                    Paragraph para = new Paragraph(fontFamilySelector.process(str,FontContext.TABLE_DATA,tColor));
                    h1 = new PdfPCell(para);
                    if (config.getBoolean("gridBorder")) {
                        h1.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                    } else {
                        h1.setBorder(0);
                    }
                    h1.setPadding(4);
                    h1.setBorderColor(Color.GRAY);

                    if (!align[col].equals("right") && !align[col].equals("left")) {
                        h1.setHorizontalAlignment(Element.ALIGN_CENTER);
                        h1.setVerticalAlignment(Element.ALIGN_CENTER);
                    } else if (align[col].equals("right")) {
                        h1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        h1.setVerticalAlignment(Element.ALIGN_RIGHT);
                    } else if (align[col].equals("left")) {
                        h1.setHorizontalAlignment(Element.ALIGN_LEFT);
                        h1.setVerticalAlignment(Element.ALIGN_LEFT);
                    }
                    table.addCell(h1);
                }
            }
            document.add(table);
            document.newPage();
        } catch (Exception e) {
            throw ServiceException.FAILURE("exportDAOImpl.addTable", e);
        }
        return stpcol;
    }

	public void getHeaderFooter(Document document) throws ServiceException {
        try {
            java.awt.Color tColor = new Color(Integer.parseInt(config.getString("textColor"), 16));
            java.util.Date dt = new java.util.Date();
            String date = "yyyy-MM-dd";
            java.text.SimpleDateFormat dtf = new java.text.SimpleDateFormat(date);
            dtf.setTimeZone(TimeZone.getTimeZone("GMT"+this.tdiff));
            String DateStr = dtf.format(dt);
            
            // -------- header ----------------
            header = new PdfPTable(3);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{20, 60,20});
            String HeadDate = "";
            if (config.getBoolean("headDate")) {
                HeadDate = DateStr;
            }
            PdfPCell headerDateCell = new PdfPCell(fontFamilySelector.process(HeadDate, FontContext.SMALL_TEXT,tColor));//fontSmallRegular));
            headerDateCell.setBorder(0);
            headerDateCell.setPaddingBottom(4);
            headerDateCell.setHorizontalAlignment(PdfCell.ALIGN_LEFT);
            header.addCell(headerDateCell);

            PdfPCell headerNotecell = new PdfPCell(fontFamilySelector.process(config.getString("headNote"), FontContext.HEADER_NOTE,tColor));
            headerNotecell.setBorder(0);
            headerNotecell.setPaddingBottom(4);
            headerNotecell.setHorizontalAlignment(PdfCell.ALIGN_CENTER);
            header.addCell(headerNotecell);

            String HeadPager = "";
            if (config.getBoolean("headPager")) {
                HeadPager = String.valueOf(document.getPageNumber());//current page no
            }
            PdfPCell headerPageNocell = new PdfPCell(fontFamilySelector.process(HeadPager,FontContext.HEADER_NOTE,tColor));// fontSmallRegular));
            headerPageNocell.setBorder(0);
            headerPageNocell.setPaddingBottom(4);
            headerPageNocell.setHorizontalAlignment(PdfCell.ALIGN_RIGHT);
            header.addCell(headerPageNocell);

            PdfPCell headerSeparator = new PdfPCell(new Phrase(""));
            headerSeparator.setBorder(PdfPCell.BOX);
            headerSeparator.setPadding(0);
            headerSeparator.setColspan(3);
            header.addCell(headerSeparator);
            // -------- header end ----------------

            // -------- footer  -------------------
            footer = new PdfPTable(3);
            PdfPCell footerSeparator = new PdfPCell(new Phrase(""));
            footerSeparator.setBorder(PdfPCell.BOX);
            footerSeparator.setPadding(0);
            footerSeparator.setColspan(3);
            footer.addCell(footerSeparator);
            footer.setWidthPercentage(100);
            footer.setWidths(new float[]{20, 60,20});
            String PageDate = "";
            if (config.getBoolean("footDate")) {
                PageDate = DateStr;
            }
            PdfPCell pagerDateCell = new PdfPCell(fontFamilySelector.process(PageDate, FontContext.SMALL_TEXT,tColor));//fontSmallRegular));
            pagerDateCell.setBorder(0);
            pagerDateCell.setHorizontalAlignment(PdfCell.ALIGN_LEFT);
            footer.addCell(pagerDateCell);

            PdfPCell footerNotecell = new PdfPCell(fontFamilySelector.process(config.getString("footNote"),FontContext.FOOTER_NOTE,tColor));// fontSmallRegular));
            footerNotecell.setBorder(0);
            footerNotecell.setHorizontalAlignment(PdfCell.ALIGN_CENTER);
            footer.addCell(footerNotecell);

            String FootPager = "";
            if (config.getBoolean("footPager")) {
                FootPager = String.valueOf(document.getPageNumber());//current page no
            }
            PdfPCell footerPageNocell = new PdfPCell(fontFamilySelector.process(FootPager,FontContext.SMALL_TEXT,tColor));// fontSmallRegular));
            footerPageNocell.setBorder(0);
            footerPageNocell.setHorizontalAlignment(PdfCell.ALIGN_RIGHT);
            footer.addCell(footerPageNocell);
        // -------- footer end   -----------
        } catch (Exception e) {
            throw ServiceException.FAILURE("exportDAOImpl.getHeaderFooter", e);
        }
    }

    public ByteArrayOutputStream processInvoiceGenerateRequest(HttpServletRequest request, HttpServletResponse response,
            JSONObject jobj, Map<String, Object> DataInfo, Company company, String currencyid,JSONArray productDetails
            , boolean isWriteToResponse, String fileType, int mode, String letterHead, String preText, String postText) throws IOException, ServiceException {
        ByteArrayOutputStream baos = null;
        String filename = DataInfo.get("filename").toString();
        String heading=DataInfo.get("heading").toString();
        JSONObject grid = null;
        JSONArray gridmap = null;
        try {
        	this.tdiff = sessionHandlerImplObj.getTimeZoneDifference(request);
        	//populateDateFormats(request);	//commented as suggested by Sagar Ahire to use fixed format instead of User's selected
            if (request.getParameter("gridconfig") != null) {
                grid = new JSONObject(request.getParameter("gridconfig"));
                gridmap = grid.getJSONArray("data");
            }
            if (StringUtil.equal(fileType, "csv")) {
                createCsvFile(request, response, jobj);
            } else if (StringUtil.equal(fileType, "pdf")) {
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                formatter.setTimeZone(TimeZone.getTimeZone("GMT"+this.tdiff));
                baos = getInvoicePdfData(request, jobj,DataInfo, company, formatter, currencyid, productDetails, mode,letterHead,preText, postText);
                if(isWriteToResponse)
                    writeDataToFile(filename,heading,fileType, baos, response);
            } else if (StringUtil.equal(fileType, "xls")) {
               createXlsFile(request, response, jobj);
            } else if (StringUtil.equal(fileType, "print")) {
                createPrinPriviewFile(request, response, jobj);
            }
        } catch (ServiceException ex) {
            ex.printStackTrace();
            errorMsg = ex.getMessage();
            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>alert('Failed to Download Document. "+errorMsg+"');</script>");
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            errorMsg = ex.getMessage();
            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>alert('Failed to Download Document. "+errorMsg+"');</script>");
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            return baos;
        }
    }

    public ByteArrayOutputStream getInvoicePdfData(HttpServletRequest request, JSONObject obj,
            Map<String, Object> DataInfo, Company com, DateFormat formatter,String currencyid, JSONArray productDetails, int mode,String letterHead, String preText, String postText) throws ServiceException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = null;
        Document document = null;
        try {
            config = new com.krawler.utils.json.base.JSONObject(DataInfo.get("config").toString());
            String baseUrl = URLUtil.getRequestPageURL(request, UnprotectedLoginPageFull);
            Rectangle rec = null;
            if (config.getBoolean("landscape")) {
                Rectangle recPage = new Rectangle(PageSize.A4.rotate());
                recPage.setBackgroundColor(new java.awt.Color(Integer.parseInt(config.getString("bgColor"), 16)));
                document = new Document(recPage, 15, 15, 30, 30);
                rec = document.getPageSize();
            } else {
                Rectangle recPage = new Rectangle(PageSize.A4);
                recPage.setBackgroundColor(new java.awt.Color(Integer.parseInt(config.getString("bgColor"), 16)));
                document = new Document(recPage, 15, 15, 30, 30);
                rec = document.getPageSize();
            }
            writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new EndPage());
            document.open();
            if (config.getBoolean("showLogo")) {
                addComponyLogo(document, request);
            }
            
            if(!StringUtil.isNullOrEmpty(letterHead))
            {
            	PdfPTable letterHeadTable = new PdfPTable(1);
            	getHtmlCell(letterHead,letterHeadTable,baseUrl);
            	letterHeadTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            	document.add(letterHeadTable);
            }

            addTitleSubtitle(document);
            createInvoicePdf(document, mode, DataInfo, com, formatter, currencyid, productDetails,preText,postText,baseUrl);
        } catch (DocumentException ex) {
            errorMsg = ex.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.getInvoicePdfData", ex);
        } catch (JSONException e){
            errorMsg = e.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.getInvoicePdfData", e);
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = e.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.getInvoicePdfData", e);
        } finally {
            if (document != null) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
        return baos;
    }
    
    public ByteArrayOutputStream getPdfData(JSONArray gridmap, HttpServletRequest request, JSONObject obj) throws ServiceException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = null;
        Document document = null;
        try {
            String colHeader = "";
            String colHeaderFinal = "";
            String fieldListFinal = "";
            String fieldList = "";
            String width = "";
            String align = "";
            String xtype = "";
            String alignFinal = "";
            String xtypeFinal = "";
            String widthFinal = "";
            String colHeaderArrStr[] = null;
            String dataIndexArrStr[] = null;
            String widthArrStr[] = null;
            String alignArrStr[] = null;
            String xtypeArrStr[] = null;
            String htmlCode = "";
            String advStr = "";
            int strLength = 0;
            float totalWidth = 0;

            config = new com.krawler.utils.json.base.JSONObject(request.getParameter("config"));
            if (request.getParameter("searchJson") != null && !request.getParameter("searchJson").equals("")) {
                JSONObject json = new JSONObject(request.getParameter("searchJson"));
                JSONArray advSearch = json.getJSONArray("root");
                for (int i = 0; i < advSearch.length(); i++) {
                    JSONObject key = advSearch.getJSONObject(i);
                    String value = "";
                    String name = key.getString("columnheader");
                    name = URLDecoder.decode(name);
                    name.trim();
                    if (name.contains("*")) {
                        name = name.substring(0, name.indexOf("*") - 1);
                    }
                    if (name.contains("(") && name.charAt(name.indexOf("(") + 1) == '&') {
                        htmlCode = name.substring(name.indexOf("(") + 3, name.length() - 2);
                        char temp = (char) Integer.parseInt(htmlCode, 10);
                        htmlCode = Character.toString(temp);
                        if (htmlCode.equals("$")) {
                            String currencyid = sessionHandlerImpl.getCurrencyID(request);
                            String currency = currencyRender(key.getString("combosearch"), currencyid);
                            name = name.substring(0, name.indexOf("(") - 1);
                            name = name + "(" + htmlCode + ")";
                            value = currency;
                        } else {
                            name = name.substring(0, name.indexOf("(") - 1);
                            value = name + " " + htmlCode;
                        }
                    } else {
                        value = key.getString("combosearch");
                    }
                    advStr += name + " : " + value + ",";
                }
                advStr = advStr.substring(0, advStr.length() - 1);
                config.remove("subtitles");
                config.put("subtitles", "Filtered By: " + advStr);
            }
            if (request.getParameter("frm") != null && !request.getParameter("frm").equals("")) {
                KWLDateFormat dateFormat = (KWLDateFormat) hibernateTemplate.load(KWLDateFormat.class, sessionHandlerImplObj.getDateFormatID(request));
                String prefDate = dateFormat.getJavaForm();
                Date from = new Date(Long.parseLong(request.getParameter("frm")));
                Date to = new Date(Long.parseLong(request.getParameter("to")));
                config.remove("subtitles");
                String timeFormatId = sessionHandlerImplObj.getUserTimeFormat(request);
                String timeZoneDiff = sessionHandlerImplObj.getTimeZoneDifference(request);
                config.put("subtitles", "Filtered By: From : " + authHandler.getPrefDateFormatter(timeFormatId, timeZoneDiff,prefDate).format(from) + " To : " + authHandler.getPrefDateFormatter(timeFormatId, timeZoneDiff, prefDate).format(to));
            }

            Rectangle rec = null;
            if (config.getBoolean("landscape")) {
                Rectangle recPage = new Rectangle(PageSize.A4.rotate());
                recPage.setBackgroundColor(new java.awt.Color(Integer.parseInt(config.getString("bgColor"), 16)));
                document = new Document(recPage, 15, 15, 30, 30);
                rec = document.getPageSize();
                totalWidth = rec.getWidth();
            } else {
                Rectangle recPage = new Rectangle(PageSize.A4);
                recPage.setBackgroundColor(new java.awt.Color(Integer.parseInt(config.getString("bgColor"), 16)));
                document = new Document(recPage, 15, 15, 30, 30);
                rec = document.getPageSize();
                totalWidth = rec.getWidth();
            }

            writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new EndPage());
            document.open();
            if (config.getBoolean("showLogo")) {
                addComponyLogo(document, request);
            }

            addTitleSubtitle(document);

                if (gridmap != null) {
                    for (int i = 0; i < gridmap.length(); i++) {
                        JSONObject temp = gridmap.getJSONObject(i);
                        colHeader += URLDecoder.decode(temp.getString("title"),"utf-8");
                        if (colHeader.indexOf("*") != -1) {
                            colHeader = colHeader.substring(0, colHeader.indexOf("*") - 1) + ",";
                        } else {
                            colHeader += ",";
                        }
                        fieldList += temp.getString("header").replace("$$", "#") + ",";// handled case for custom report. Because dataindex field have "#" symbol and while exporting data URL will break if having # symbol. So replaced # with $$ at JS side and reverted this change at Java side
                        if (!config.getBoolean("landscape")) {
                            int totalWidth1 = (int) ((totalWidth / gridmap.length()) - 5.00);
                            width += "" + totalWidth1 + ",";  //resize according to page view[potrait]
                        } else {
                            width += temp.getString("width") + ",";
                        }
                        if (temp.optString("align").equals("")) {
                            align += "none" + ",";
                        } else {
                            align += temp.getString("align") + ",";
                        }
                        if (temp.optString("xtype").equals("")) {
                            xtype += "none" + ",";
                        } else {
                            xtype += temp.getString("xtype") + ",";
                        }
                    }
                    strLength = colHeader.length() - 1;
                    colHeaderFinal = colHeader.substring(0, strLength);
                    strLength = fieldList.length() - 1;
                    fieldListFinal = fieldList.substring(0, strLength);
                    strLength = width.length() - 1;
                    widthFinal = width.substring(0, strLength);
                    strLength = align.length() - 1;
                    alignFinal = align.substring(0, strLength);
                    strLength=xtype.length() - 1;
                    xtypeFinal = xtype.substring(0, strLength);
                    colHeaderArrStr = colHeaderFinal.split(",");
                    dataIndexArrStr = fieldListFinal.split(",");
                    widthArrStr = widthFinal.split(",");
                    alignArrStr = alignFinal.split(",");
                    xtypeArrStr = xtypeFinal.split(",");
                } else {
                    fieldList = request.getParameter("header");
                    colHeader = URLDecoder.decode(request.getParameter("title"));
                    width = request.getParameter("width");
                    align = request.getParameter("align");
                    xtype = request.getParameter("xtype");
                    colHeaderArrStr = colHeader.split(",");
                    dataIndexArrStr = fieldList.split(",");
                    widthArrStr = width.split(",");
                    alignArrStr = align.split(",");
                    xtypeArrStr = xtype.split(",");
                }

                JSONArray store = null;
                if (obj.isNull("coldata")) {
                    store = obj.getJSONArray("data");
                } else {
                    store = obj.getJSONArray("coldata");
                }
                addTable(0, colHeaderArrStr.length, 0, store.length(), store, dataIndexArrStr, colHeaderArrStr, widthArrStr, alignArrStr,xtypeArrStr, document);

        } catch (DocumentException ex) {
            errorMsg = ex.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.getPdfData", ex);
        } catch (JSONException e){
            errorMsg = e.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.getPdfData", e);
        } catch (Exception e) {
            errorMsg = e.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.getPdfData", e);
        } finally {
            if (document != null) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
        return baos;
    }

    public ByteArrayOutputStream getPdfData(JSONArray gridmap, String configStr, String titleStr, String headerStr, String widthStr, String alignStr,String xtypeStr, JSONObject obj) throws ServiceException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = null;
        Document document = null;
        try {
            String colHeader = "";
            String colHeaderFinal = "";
            String fieldListFinal = "";
            String fieldList = "";
            String width = "";
            String align = "";
            String xtype = "";
            String alignFinal = "";
            String xtypeFinal = "";
            String widthFinal = "";
            String colHeaderArrStr[] = null;
            String dataIndexArrStr[] = null;
            String widthArrStr[] = null;
            String alignArrStr[] = null;
            String xtypeArrStr[] = null;
            String htmlCode = "";
            String advStr = "";
            int strLength = 0;
            float totalWidth = 0;

            config = new com.krawler.utils.json.base.JSONObject(configStr);
            Rectangle rec = null;
            if (config.getBoolean("landscape")) {
                Rectangle recPage = new Rectangle(PageSize.A4.rotate());
                recPage.setBackgroundColor(new java.awt.Color(Integer.parseInt(config.getString("bgColor"), 16)));
                document = new Document(recPage, 15, 15, 30, 30);
                rec = document.getPageSize();
                totalWidth = rec.getWidth();
            } else {
                Rectangle recPage = new Rectangle(PageSize.A4);
                recPage.setBackgroundColor(new java.awt.Color(Integer.parseInt(config.getString("bgColor"), 16)));
                document = new Document(recPage, 15, 15, 30, 30);
                rec = document.getPageSize();
                totalWidth = rec.getWidth();
            }

            writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new EndPage());
            document.open();
//            if (config.getBoolean("showLogo")) {
//                addComponyLogo(document, request);
//            }

            addTitleSubtitle(document);

            if (gridmap != null) {
                for (int i = 0; i < gridmap.length(); i++) {
                    JSONObject temp = gridmap.getJSONObject(i);
                    colHeader += URLDecoder.decode(temp.getString("title"),"utf-8");
                    if (colHeader.indexOf("*") != -1) {
                        colHeader = colHeader.substring(0, colHeader.indexOf("*") - 1) + ",";
                    } else {
                        colHeader += ",";
                    }
                    fieldList += temp.getString("header") + ",";
                    if (!config.getBoolean("landscape")) {
                        int totalWidth1 = (int) ((totalWidth / gridmap.length()) - 5.00);
                        width += "" + totalWidth1 + ",";  //resize according to page view[potrait]
                    } else {
                        width += temp.getString("width") + ",";
                    }
                    if (temp.getString("align").equals("")) {
                        align += "none" + ",";
                    } else {
                        align += temp.getString("align") + ",";
                    }
                    if (temp.getString("xtype").equals("")) {
                    	xtype += "none" + ",";
                    } else {
                    	xtype += temp.getString("xtype") + ",";
                    }
                }
                strLength = colHeader.length() - 1;
                colHeaderFinal = colHeader.substring(0, strLength);
                strLength = fieldList.length() - 1;
                fieldListFinal = fieldList.substring(0, strLength);
                strLength = width.length() - 1;
                widthFinal = width.substring(0, strLength);
                strLength = align.length() - 1;
                alignFinal = align.substring(0, strLength);
                xtypeFinal = xtype.substring(0, strLength);
                colHeaderArrStr = colHeaderFinal.split(",");
                dataIndexArrStr = fieldListFinal.split(",");
                widthArrStr = widthFinal.split(",");
                alignArrStr = alignFinal.split(",");
                xtypeArrStr = xtypeFinal.split(",");
            } else {
                fieldList = headerStr;
                colHeader = URLDecoder.decode(titleStr);
                width = widthStr;
                align = alignStr;
                xtype = xtypeStr;
                colHeaderArrStr = colHeader.split(",");
                dataIndexArrStr = fieldList.split(",");
                widthArrStr = width.split(",");
                alignArrStr = align.split(",");
                xtypeArrStr = xtype.split(",");
            }

            JSONArray store = null;
            if (obj.isNull("coldata")) {
                store = obj.getJSONArray("data");
            } else {
                store = obj.getJSONArray("coldata");
            }
            addTable(0, colHeaderArrStr.length, 0, store.length(), store, dataIndexArrStr, colHeaderArrStr, widthArrStr, alignArrStr,xtypeArrStr, document);

        } catch (DocumentException ex) {
            errorMsg = ex.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.getPdfData", ex);
        } catch (JSONException e){
            errorMsg = e.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.getPdfData", e);
        } catch (Exception e) {
            errorMsg = e.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.getPdfData", e);
        } finally {
            if (document != null) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
        return baos;
    }

    public void createCsvFile(HttpServletRequest request, HttpServletResponse response, JSONObject obj) throws ServiceException {
        try {
            String headers[] = null;
            String titles[] = null;
            JSONArray repArr = null;
            String xtype[]=null;

            if (request.getParameter("header") != null) {
                String head = request.getParameter("header").replace("$$", "#");
                String xt=request.getParameter("xtype");
                String tit = request.getParameter("title");
                tit = URLDecoder.decode(tit);
                headers = (String[]) head.split(",");
                xtype=xt.split(",");
                titles = (String[]) tit.split(",");
            } else {
                headers = (String[]) obj.get("header");
                xtype = (String[]) obj.get("xtype");
                titles = (String[]) obj.get("title");
            }
            StringBuilder reportSB = new StringBuilder();
           if (obj.isNull("coldata")) {
                repArr = obj.getJSONArray("data");
            } else {
                repArr = obj.getJSONArray("coldata");
            }
            for (int h = 0; h < headers.length; h++) {
                String val= StringEscapeUtils.escapeCsv(titles[h]);
                if (h < headers.length - 1) {
                    reportSB.append(StringEscapeUtils.escapeCsv(titles[h])).append(',');
                } else {
                    reportSB.append(StringEscapeUtils.escapeCsv(titles[h])).append('\n');
                }
            }
            for (int t = 0; t < repArr.length(); t++) {
                JSONObject temp = repArr.getJSONObject(t);
                for (int h = 0; h < headers.length; h++) {
                    String str = temp.optString(headers[h] ,"");
                    try {
                        if(xtype.length > 0) {
                            str = formatValue(temp.optString(headers[h] ,""),xtype[h]);
                        }
                    } catch(Exception e) {

                    }
                    if (h < headers.length - 1) {
                        reportSB.append(StringEscapeUtils.escapeCsv(str)).append(',');
                    } else {
                        reportSB.append(StringEscapeUtils.escapeCsv(str)).append('\n');
                    }
                }
            }
            String heading=request.getParameter("heading");
            String fname = request.getParameter("name");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(reportSB.toString().getBytes());
            os.close();
            if(!StringUtil.isNullOrEmpty(heading)){
                fname = heading + fname;
            }
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fname + ".csv\"");
            response.setContentType("application/octet-stream");
            response.setContentLength(os.size());
            response.getOutputStream().write(os.toByteArray());
            response.getOutputStream().flush();
        } catch (IOException ex) {
            errorMsg = ex.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.createCsvFile : " + ex.getMessage(), ex);
        } catch (JSONException e) {
            errorMsg = e.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.createCsvFile : " + e.getMessage(), e);
        } catch (Exception e) {
            errorMsg = e.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.createCsvFile : " + e.getMessage(), e);
        }
    }

    public void createCsvFileForBackup(String header, String title, String filename, String destinationDirectory, JSONObject obj) throws ServiceException {
        try {
            String headers[] = null;
            String titles[] = null;
            JSONArray repArr = null;

            if (header != null) {
                String head = header;
                String tit = title;
                tit = URLDecoder.decode(tit);
                headers = (String[]) head.split(",");
                titles = (String[]) tit.split(",");
            } else {
                headers = (String[]) obj.get("header");
                titles = (String[]) obj.get("title");
            }
            StringBuilder reportSB = new StringBuilder();

            if (obj.isNull("coldata")) {
                repArr = obj.getJSONArray("data");
            } else {
                repArr = obj.getJSONArray("coldata");
            }
            for (int h = 0; h < headers.length; h++) {
                if (h < headers.length - 1) {
                    reportSB.append("\"" + titles[h] + "\",");
                } else {
                    reportSB.append("\"" + titles[h] + "\"\n");
                }
            }
            for (int t = 0; t < repArr.length(); t++) {
                JSONObject temp = repArr.getJSONObject(t);
                for (int h = 0; h < headers.length; h++) {
                    if (h < headers.length - 1) {
                        reportSB.append("\"" + temp.optString(headers[h],"") + "\",");
                    } else {
                        reportSB.append("\"" + temp.optString(headers[h],"") + "\"\n");
                    }
                }
            }

            String ext = ".csv";
            try {
                java.io.FileOutputStream failurefileOut = new java.io.FileOutputStream(destinationDirectory + "/" + filename+ext);
                failurefileOut.write(reportSB.toString().getBytes());
                failurefileOut.flush();
                failurefileOut.close();
            } catch (Exception ex) {
                System.out.println("\nError file write [success/failed] " + ex);
            }
        } catch (JSONException e) {
            errorMsg = e.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.createCsvFile : " + e.getMessage(), e);
        } catch (Exception e) {
            errorMsg = e.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.createCsvFile : " + e.getMessage(), e);
        }
    }

    public void downloadFile(String filename, String destinationDirectory, HttpServletResponse response) {
        try {
            File intgfile = new File(destinationDirectory);
            byte[] buff = new byte[(int) intgfile.length()];

            try {
                FileInputStream fis = new FileInputStream(intgfile);
                int read = fis.read(buff);
            } catch (IOException ex) {
                filename = "file_not_found.txt";
            }

            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentType("application/octet-stream");
            response.setContentLength(buff.length);
            response.getOutputStream().write(buff);
            response.getOutputStream().flush();
        } catch (IOException ex) {
            LOGGER.warn("Unable To Download File :" + ex.toString());
        } catch (Exception ex) {
            LOGGER.warn("Unable To Download File :" + ex.toString());
        }

    }

    public void createXlsFile(HttpServletRequest request, HttpServletResponse response, JSONObject obj) throws ServiceException {
        try {
            com.krawler.esp.servlets.exportExcel exXls = new com.krawler.esp.servlets.exportExcel();
            exXls.setTimeZone(TimeZone.getTimeZone("GMT"+this.tdiff));
            String name = request.getParameter("name");
            String header = request.getParameter("header").replace("$$", "#");
            String xtype=request.getParameter("xtype");
            String xlsheader = request.getParameter("title");
            String heading=request.getParameter("heading");
            String[] headerArr = header.split(",");
            String[] xlsheaderArr = xlsheader.split(",");
            String[] xtypeArr = xtype.split(",");
            
            JSONArray headerjArr = new JSONArray();
            JSONArray xlsheaderjArr = new JSONArray();
            java.util.Hashtable ht = new java.util.Hashtable();
            for(int i = 0 ; i < headerArr.length ; i++){
                ht.put(i,headerArr[i] );
                headerjArr.put(i, URLDecoder.decode(headerArr[i],"UTF-8"));
                xlsheaderjArr.put(i, URLDecoder.decode(xlsheaderArr[i],"UTF-8"));
            }

            exXls.exportexcel(response, obj, ht, name, name,headerjArr,xlsheaderjArr,heading,xtypeArr,this);
        } catch (IOException ex) {
            errorMsg = ex.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.createXlsFile : " + ex.getMessage(), ex);
        } catch (JSONException e) {
            errorMsg = e.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.createXlsFile : " + e.getMessage(), e);
        } catch (Exception e) {
            errorMsg = e.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.createXlsFile : " + e.getMessage(), e);
        }

    }

    public void createPrinPriviewFile(HttpServletRequest request, HttpServletResponse response, JSONObject obj) throws ServiceException {

        try {
            String headers[] = null;
            String titles[] = null;
            String xtypes[] = null;
            String[] str=null;
            StringBuilder newtitle=new StringBuilder();
            JSONArray repArr = new JSONArray();
            String searchjson = request.getParameter("searchJson");
            JSONObject json = null;
            JSONArray advSearch=null;
            String htmlCode="";
            String advStr="<ol>";
//            User userid = (User) session.load(User.class, AuthHandler.getUserid(request));
//            String  startdate = remoteapi.getUserDateFormatter1(userid, session, KWLDateFormat.DATE_PART).format(new Date());
            String startdate=formatDate(System.currentTimeMillis(),true,false);
            if(!StringUtil.isNullOrEmpty(searchjson) && !StringUtil.equal(searchjson, "undefined")){
                json = new JSONObject(request.getParameter("searchJson"));
                advSearch=json.getJSONArray("root");
                for(int i=0;i<advSearch.length();i++) {
                    JSONObject key =advSearch.getJSONObject(i);
                    String value="";
                    String name = key.getString("columnheader");
                    name=URLDecoder.decode(name);
                    name.trim();
                    if(name.contains("*"))
                        name=name.substring(0,name.indexOf("*")-1);
                    if(name.contains("(") && name.charAt(name.indexOf("(")+1)=='&') {
                        htmlCode= name.substring(name.indexOf("(")+3,name.length()-2);
                        char temp=  (char) Integer.parseInt(htmlCode,10);
                        htmlCode=Character.toString(temp);
                        if(htmlCode.equals("$")) {
                            String currencyid = sessionHandlerImpl.getCurrencyID(request);
                            String currency = currencyRender(key.getString("combosearch"), currencyid);
                            name=name.substring(0, name.indexOf("(")-1);
                            name=name+"("+htmlCode+")";
                            value = currency;
                        } else {
                            name=name.substring(0, name.indexOf("(")-1);
                            value=name+" "+htmlCode;
                        }
                    } else
                        value=key.getString("combosearch");
                     advStr+="<li><font size=\"2\">"+name+" : "+value+"</font></li>";
                }
                advStr+="</ol>";
            }
            
             //To modify the Heading of document to be printed ..String 'newtitle' is manipulated.
            String name = request.getParameter("name");
            String heading = request.getParameter("heading");
            if(!StringUtil.isNullOrEmpty(heading)){
                name = heading + name;
            }
            String maintitle=name;
            String title=request.getParameter("titlename");
            str=maintitle.split("(?=\\p{Upper})");	
            if(!StringUtil.isNullOrEmpty(title)){
            	newtitle.append(title);
            }else{
            	for(int i=0;i<str.length;i++){
                	newtitle.append(str[i]+" ");
            	}
            }
            newtitle.trimToSize();
            String ashtmlString = "<html> " +
                                        "<head>" +
                                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>" +
                                        "<title>"+request.getParameter("name")+"</title>" +
                                        "<style type=\"text/css\">@media print {button#print {display: none;}}</style>"+
                                        "</head>" +
                                        "<body style = \"font-family: Tahoma, Verdana, Arial, Helvetica, sans-sarif;\">" +

                                            "<center><div style='padding-bottom: 5px; padding-right: 5px;'>" +
                                                "<h3> "+newtitle+" </h3>" +
                                            "</div></center>";

            ashtmlString += "<div>" +
						    "<b><font size=\"2\">Generated On : </b>"+startdate+"</font>"+
                            "</div></br>";
            if(!StringUtil.isNullOrEmpty(searchjson) && !StringUtil.equal(searchjson, "undefined")){
                ashtmlString += "<div>" +
								"<b><font size=\"2\">Selection Criteria : </b></font>"+advStr+
                                "</div>";
            }

            String atempstr = "<DIV style='page-break-after:always'></DIV>";

            if (request.getParameter("header") != null) {
                String head = request.getParameter("header").replace("$$", "#");
                String xtype=request.getParameter("xtype");
                String tit = request.getParameter("title");
                tit=URLDecoder.decode(tit,"utf-8");
                headers = (String[]) head.split(",");
                titles = (String[]) tit.split(",");
                xtypes=xtype.split(",");
            } else {
                headers = (String[]) obj.get("header");
                titles = (String[]) obj.get("title");
                xtypes = (String[]) obj.get("xtype");
            }
            StringBuilder reportSB = new StringBuilder();

            if (obj.isNull("coldata")) {
                if(obj.has("data"))
                   repArr = obj.getJSONArray("data");
            } else {
                repArr = obj.getJSONArray("coldata");
            }

            for (int t = 0; t < repArr.length(); t++) {
                if(t!=0){
                    ashtmlString+="</br></br>";
                }
                ashtmlString+="<center>";
                ashtmlString += "<table cellspacing=0 border=1 cellpadding=2 width='100%' style='font-size:9pt'>";
                ashtmlString +="<tr>";
                for (int hCnt = -1; hCnt < titles.length; hCnt++) {
                    if(hCnt==-1)
                        ashtmlString +="<th>S No.</th>";
                    else
                        ashtmlString +="<th>"+titles[hCnt]+"</th>";
                }
                ashtmlString +="</tr>";
               for (int h = 0; h < 15; h++) {
                    if(repArr.length() - t != 0) {
                        String recordData = "<tr><td align=\"center\">"+(t+1)+"</td>";
                        JSONObject temp = repArr.getJSONObject(t);
                        for (int hCnt = 0; hCnt < headers.length; hCnt++) {
                            String str1 = temp.optString(headers[hCnt] ,"");
                            try {
                                if(xtypes.length > 0) {
                                    str1 = formatValue(temp.optString(headers[hCnt] ,""),xtypes[hCnt]);
                                }
                            } catch(Exception e) {

                            }
                            if(temp.has(headers[hCnt].toString()))
                                recordData +="<td>"+str1+"&nbsp;</td>";
                            else
                                recordData +="<td>&nbsp;</td>";
                        }
                        ashtmlString += recordData + "</tr>";
                        t++;
                    } else {
                        atempstr="";
                    }
                }
                ashtmlString += "</table>";
                ashtmlString += "</center>";
                if(t!=repArr.length()-1) {
                    ashtmlString += atempstr;
                }
                t--;
            }
            ashtmlString +="<div style='float: left; padding-top: 3px; padding-right: 5px;'>" +
                                    "<button id = 'print' title='Print Invoice' onclick='window.print();' style='color: rgb(8, 55, 114);' href='#'>Print</button>" +
                                "</div>" ;
            ashtmlString +="</body>" +
            "</html>";
            String fname = request.getParameter("name");
            response.getOutputStream().write(ashtmlString.getBytes());
            response.getOutputStream().flush();
        } catch (SessionExpiredException ex) {
            errorMsg = ex.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.createPrinPriviewFile : " + ex.getMessage(), ex);
        } catch (IOException ex) {
            errorMsg = ex.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.createPrinPriviewFile : " + ex.getMessage(), ex);
        } catch (JSONException ex) {
            errorMsg = ex.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.createPrinPriviewFile : " + ex.getMessage(), ex);
        } catch (Exception ex) {
            errorMsg = ex.getMessage();
            throw ServiceException.FAILURE("exportDAOImpl.createPrinPriviewFile : " + ex.getMessage(), ex);
        }
    }

    public String currencyRender(String currency, String currencyid) throws SessionExpiredException {
        KWLCurrency cur = (KWLCurrency) hibernateTemplate.load(KWLCurrency.class, currencyid);
        String symbol = cur.getHtmlcode();
        char temp = (char) Integer.parseInt(symbol, 16);
        symbol = Character.toString(temp);
        float v = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        if (currency.equals("")) {
            return symbol;
        }
        v = Float.parseFloat(currency);
        String fmt = decimalFormat.format(v);
        fmt = symbol + fmt;
        return fmt;
    }

    public String currencyRender(String currency, KWLCurrency cur) throws SessionExpiredException {
        String symbol = "";
        try{
            char temp = (char) Integer.parseInt(cur.getHtmlcode(), 16);
            symbol = Character.toString(temp);
        }catch(Exception e){
            symbol=cur.getHtmlcode();
        }
        float v = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        if (currency.equals("")) {
            return symbol;
        }
        v = Float.parseFloat(currency);
        String fmt = decimalFormat.format(v);
        fmt = symbol +" "+ fmt;
        return fmt;
    }

    private PdfPCell calculateDiscount(double disc, KWLCurrency cur) throws SessionExpiredException {
        PdfPCell cell = null;
        if (disc==0) {
            cell = new PdfPCell(new Paragraph(fontFamilySelector.process("--", FontContext.NOTE_TEXT)));
        } else {
            cell = new PdfPCell(new Paragraph(fontFamilySelector.process(currencyRender(String.valueOf(disc),cur), FontContext.TABLE_DATA)));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(15);
            cell.setPadding(5);
        }
        return cell;
    }
    
    private void populateDateFormats(HttpServletRequest request) throws DataAccessException, SessionExpiredException{
    	KWLDateFormat kdf = (KWLDateFormat)hibernateTemplate.get(KWLDateFormat.class,sessionHandlerImplObj.getDateFormatID(request));
    	String tfid = sessionHandlerImplObj.getUserTimeFormat(request);
    	String tmp=kdf.getJavaForm();
    	int pos = kdf.getJavaSeperatorPosition();
    	dFmt = new SimpleDateFormat(tmp.substring(0,pos));
    	tFmt = new SimpleDateFormat(tmp.substring(pos));
    }
    
	public String formatValue(String value, String xtype) {
		try {
			if ("datefield".equals(xtype) && value.length() > 0) {
				value = formatDate(Long.parseLong(value), true, false);
			}
			if ("timefield".equals(xtype) && value.length() > 0) {
				value = formatDate(Long.parseLong(value), false, true);
			}
		} catch (Exception e) {
			LOGGER.warn("Cannot format value", e);
		}
		return value;
	}
    protected String formatDate(long millisec, boolean showDate, boolean showTime){
    	return formatDate(millisec, showDate, showTime, TimeZone.getTimeZone("GMT"+this.tdiff));
    }

    protected String formatDate(long millisec, boolean showDate, boolean showTime, TimeZone tz){
    	String fmt="";
    	dFmt.setTimeZone(tz);
    	tFmt.setTimeZone(tz);
    	if(showDate)
    		fmt += dFmt.format(millisec);
    	if(showTime)
    		fmt += tFmt.format(millisec);
    	return fmt;
    }

    public void setHeaderFooter(Document doc, String headerText) {
        HeaderFooter footer = new HeaderFooter(new Phrase("  ", FontFactory.getFont("Helvetica", 8, Font.NORMAL, Color.BLACK)), true);
        footer.setBorderWidth(0);
        footer.setBorderWidthTop(1);
        footer.setAlignment(HeaderFooter.ALIGN_RIGHT);
        doc.setFooter(footer);
        HeaderFooter header = new HeaderFooter(new Phrase(headerText, FontFactory.getFont("Helvetica", 14, Font.BOLD, Color.BLACK)), false);
        doc.setHeader(header);
    }

    public void createInvoicePdf(Document document, int mode, Map<String, Object> DataInfo, Company com, DateFormat formatter,
            String currencyid, JSONArray productDetails, String preText,String postText,String baseUrl) throws DocumentException, JSONException {
        // mode 1 = quotation
        try {
                KWLCurrency currencyObj = (KWLCurrency) hibernateTemplate.load(KWLCurrency.class, currencyid);
                config = new com.krawler.utils.json.base.JSONObject(DataInfo.get("config").toString());
                PdfPTable tab2 = null;
                PdfPTable tab3 = null;
                PdfPTable mainTable = new PdfPTable(1);
                mainTable.setWidthPercentage(100);
                String invno = "";
                String theader = "";
                double quotationDisc = 0;
                double quotationtaxamount = 0;
                String quotationtaxname ="";
                double totalAmount = 0;
                double quotationtaxpercent =0;
                Date entryDate = null;
                        String customerName = "";
                        String shipTo = "";
                        String memo = "";
        //                itr = idresult.getEntityList().iterator();

                if (mode == 1) {
                    theader = "Quotation";
                    invno = DataInfo.containsKey("invno") ? DataInfo.get("invno").toString() : "";
                    entryDate = DataInfo.containsKey("entrydate") ? (Date)DataInfo.get("entrydate") : new Date();
                    customerName = DataInfo.containsKey("customername") ? DataInfo.get("customername").toString() : "";
                    shipTo = DataInfo.containsKey("address") ? DataInfo.get("address").toString() : "";
                    memo = DataInfo.containsKey("memo") ? DataInfo.get("memo").toString() : "";
                    quotationDisc =  DataInfo.containsKey("quotationdisc") ? Double.parseDouble(DataInfo.get("quotationdisc").toString()) : 0;
                    quotationtaxamount =  DataInfo.containsKey("quotationtax") ? Double.parseDouble(DataInfo.get("quotationtax").toString()) : 0;
                    quotationtaxname =  DataInfo.containsKey("quotationtaxname") ? DataInfo.get("quotationtaxname").toString() : "";
                    quotationtaxpercent = DataInfo.containsKey("quotationtaxpercent") ? Double.parseDouble(DataInfo.get("quotationtaxpercent").toString()) : 0;
                    totalAmount = DataInfo.containsKey("totalamount") ? Double.parseDouble(DataInfo.get("totalamount").toString()) : 0;
                }
                String company[] = new String[4];
                company[0] = com.getCompanyName();
                company[1] = com.getAddress();
                company[2] = com.getEmailID();
                company[3] = com.getPhoneNumber();

                PdfPTable table1 = new PdfPTable(2);
                table1.setWidthPercentage(100);
                table1.setWidths(new float[]{50, 50});

                tab2 = new PdfPTable(1);
                PdfPCell invCell = null;
                invCell=createCell(theader,FontContext.TABLE_HEADER,Element.ALIGN_RIGHT,0,5);
                tab2.addCell(invCell);
                PdfPCell cel2 = new PdfPCell(tab2);
                cel2.setBorder(0);
                table1.addCell(cel2);

                PdfPCell mainCell11 = new PdfPCell(table1);
                mainCell11.setBorder(0);
                mainCell11.setPadding(10);
                mainTable.addCell(mainCell11);

                PdfPTable userTable2 = new PdfPTable(2);
                userTable2.setWidthPercentage(100);
                userTable2.setWidths(new float[]{60, 40});

                tab3 = getCompanyInfo(company);

                PdfPTable tab4 = new PdfPTable(2);
                tab4.setWidthPercentage(100);
                tab4.setWidths(new float[]{30, 70});

                PdfPCell cell2=createCell(theader+" No.",FontContext.TABLE_HEADER,Element.ALIGN_LEFT,0,5);
                tab4.addCell(cell2);
//                String invno = mode != StaticValues.AUTONUM_BILLINGINVOICE ? inv.getInvoiceNumber() : inv1.getBillingInvoiceNumber();
                cell2 = createCell(": " + invno, FontContext.SMALL_TEXT, Element.ALIGN_LEFT, 0, 5);
                tab4.addCell(cell2);
                
                cell2 = createCell("DATE  ", FontContext.SMALL_TEXT, Element.ALIGN_LEFT, 0, 5);
                tab4.addCell(cell2);
                cell2 = createCell(": " + formatter.format(entryDate), FontContext.SMALL_TEXT, Element.ALIGN_LEFT, 0, 5);
                tab4.addCell(cell2);

                PdfPCell cell1 = new PdfPCell(tab3);
                cell1.setBorder(0);
                userTable2.addCell(cell1);
                cel2 = new PdfPCell(tab4);
                cel2.setBorder(0);
                userTable2.addCell(cel2);

                PdfPCell mainCell12 = new PdfPCell(userTable2);
                mainCell12.setBorder(0);
                mainCell12.setPadding(10);
                mainTable.addCell(mainCell12);

                PdfPTable tab5 = new PdfPTable(2);
                tab5.setWidthPercentage(100);
                tab5.setWidths(new float[]{50, 50});
                PdfPCell cell3 = createCell("To, ", FontContext.TABLE_DATA, Element.ALIGN_LEFT, 0, 5);
                tab5.addCell(cell3);
                cell3 = createCell("", FontContext.TABLE_DATA, Element.ALIGN_LEFT, 0, 0);
                tab5.addCell(cell3);
//                cell3 = createCell("", fontRegularNormal, Element.ALIGN_LEFT, 0, 0);
//                tab5.addCell(cell3);
                
                cell3=createCell(customerName, FontContext.TABLE_DATA,Element.ALIGN_LEFT,0,5);
                tab5.addCell(cell3);
                cell3 = createCell("", FontContext.TABLE_DATA, Element.ALIGN_LEFT, 0, 0);
                tab5.addCell(cell3);
                cell3 = createCell(shipTo, FontContext.TABLE_DATA, Element.ALIGN_LEFT, 0, 5);
                tab5.addCell(cell3);

                PdfPCell mainCell14 = new PdfPCell(tab5);
                mainCell14.setBorder(0);
                //mainCell14.setPadding(10);
                mainTable.addCell(mainCell14);
                getHtmlCell(preText.trim(),mainTable,baseUrl);
                getHtmlCell("<br>",mainTable,baseUrl);
                getHtmlCell("<br>",mainTable,baseUrl);
                String[] header = {"S.No.","PRODUCT", "DESCRIPTION", "QUANTITY", "UNIT PRICE", "DISCOUNT","TAX", "LINE TOTAL"};
                PdfPTable table = getBlankTable();
                PdfPCell invcell = null;
                
                for (int i = 0; i < header.length; i++) {
                    invcell = new PdfPCell(new Paragraph(fontFamilySelector.process(header[i],FontContext.TABLE_HEADER)));
                    invcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invcell.setBackgroundColor(Color.LIGHT_GRAY);
                    invCell.setBorder(0);
                    invcell.setPadding(3);
                    table.addCell(invcell);
                }
                addTableRow(mainTable, table); //Break table after adding header row
                table = getBlankTable();

                String prodName = "";
                String subtotal = "";
                String quantity = "";
                String rate = "";
                String description="";
                String discount = "";
                String prodtax = "";
                int index=0;
                double total = 0;
                for(int cnt=0; cnt<productDetails.length(); cnt++) {
                    JSONObject productInfo = productDetails.getJSONObject(cnt);
                    prodName = productInfo.getString("productname");
                    description=productInfo.getString("description");
                    quantity = productInfo.getString("quantity");
                    rate = productInfo.getString("orderrate");
                    subtotal = productInfo.getString("amount");
                    discount = productInfo.getString("prdiscountamount");
                    prodtax = productInfo.getString("taxamount");

                    invcell = createCell((++index)+".", FontContext.TABLE_DATA, Element.ALIGN_RIGHT, Rectangle.LEFT + Rectangle.RIGHT, 5);
                    table.addCell(invcell);
                    invcell = createCell(prodName, FontContext.TABLE_DATA, Element.ALIGN_LEFT, Rectangle.LEFT + Rectangle.RIGHT, 5);
                    table.addCell(invcell);
                    invcell = createCell(description, FontContext.TABLE_DATA, Element.ALIGN_LEFT, Rectangle.LEFT + Rectangle.RIGHT, 5);
                    table.addCell(invcell);
                    invcell = createCell(quantity, FontContext.TABLE_DATA, Element.ALIGN_CENTER, Rectangle.LEFT + Rectangle.RIGHT, 5);
                    table.addCell(invcell);
                    invcell = createCell(currencyRender(rate, currencyObj), FontContext.TABLE_DATA, Element.ALIGN_RIGHT, Rectangle.LEFT + Rectangle.RIGHT, 5);
                    table.addCell(invcell);
                    invcell = createCell(currencyRender(subtotal, currencyObj), FontContext.TABLE_DATA, Element.ALIGN_RIGHT, Rectangle.LEFT + Rectangle.RIGHT, 5);
                    table.addCell(invcell);
                    invcell = createCell(currencyRender(prodtax, currencyObj), FontContext.TABLE_DATA, Element.ALIGN_CENTER, Rectangle.LEFT + Rectangle.RIGHT, 5);
                    table.addCell(invcell);
                    invcell = createCell(currencyRender(subtotal, currencyObj), FontContext.TABLE_DATA, Element.ALIGN_RIGHT, Rectangle.LEFT + Rectangle.RIGHT, 5);
                    table.addCell(invcell);
                    
                    total += Double.valueOf(subtotal);

                    addTableRow(mainTable, table); //Break table after adding detail's row
                    table = getBlankTable();
                }

                for (int j = 0; j < 98; j++) {
                    invcell = new PdfPCell(new Paragraph(fontFamilySelector.process("", FontContext.TABLE_DATA)));//fontRegularBold));
                    invcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invcell.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
                    table.addCell(invcell);
                }
                addTableRow(mainTable, table); //Break table after adding extra space
                table = getBlankTable();

                cell3 = createCell("SUB TOTAL", FontContext.NOTE_TEXT, Element.ALIGN_RIGHT, Rectangle.TOP, 5);
                cell3.setColspan(7);
                table.addCell(cell3);

                cell3 = createCell(currencyRender(String.valueOf(total), currencyObj), FontContext.TABLE_DATA, Element.ALIGN_RIGHT, 15, 5);
                table.addCell(cell3);

                cell3 = createCell("DISCOUNT(-)", FontContext.NOTE_TEXT, Element.ALIGN_RIGHT, 0, 5);
                cell3.setColspan(7);
                table.addCell(cell3);
                cell3 = calculateDiscount(quotationDisc, currencyObj);
                table.addCell(cell3);

                StringBuffer taxNameStr = new StringBuffer();
                if(!StringUtil.isNullOrEmpty(quotationtaxname)){
                    taxNameStr.append(quotationtaxname);
                    taxNameStr.append(" ");
                    taxNameStr.append(quotationtaxpercent);
                    taxNameStr.append("% (+)");
                } else {
                    taxNameStr.append("TAX (+)");
                }
                cell3 = createCell(taxNameStr.toString(), FontContext.NOTE_TEXT, Element.ALIGN_RIGHT, 0, 5);
                cell3.setColspan(7);
                table.addCell(cell3);
                cell3 = createCell(currencyRender(String.valueOf(quotationtaxamount), currencyObj), FontContext.TABLE_DATA, Element.ALIGN_RIGHT, 15, 5);
                table.addCell(cell3);

                cell3 = createCell("TOTAL", FontContext.NOTE_TEXT, Element.ALIGN_RIGHT, 0, 5);
                cell3.setColspan(7);
                table.addCell(cell3);
                cell3 = createCell(currencyRender(String.valueOf(totalAmount), currencyObj), FontContext.TABLE_DATA, Element.ALIGN_RIGHT, 15, 5);
                table.addCell(cell3);

                addTableRow(mainTable, table);

                String netinword = EnglishNumberToWordsOjb.convert(Double.parseDouble(String.valueOf(totalAmount)), currencyObj);
                String currencyname = currencyObj.getName();

                cell3 = createCell("Amount (in words) : " + currencyname + " " + netinword + " Only.", FontContext.HEADER_NOTE, Element.ALIGN_LEFT, Rectangle.LEFT + Rectangle.RIGHT + Rectangle.BOTTOM + Rectangle.TOP, 5);

                PdfPTable table2 = new PdfPTable(1);
                table2.addCell(cell3);
                PdfPCell mainCell62 = new PdfPCell(table2);
                mainCell62.setBorder(0);
                mainCell62.setPadding(10);
                mainTable.addCell(mainCell62);
                
                PdfPTable helpTable = new PdfPTable(new float[]{8, 92});
                helpTable.setWidthPercentage(100);
                Phrase phrase1 = fontFamilySelector.process("Memo:  ",FontContext.NOTE_TEXT);
                Phrase phrase2 = fontFamilySelector.process(memo,FontContext.TABLE_DATA);//fontRegularBold);
                PdfPCell pcell1 = new PdfPCell(phrase1);
                PdfPCell pcell2 = new PdfPCell(phrase2);
                pcell1.setBorder(0);
                pcell1.setPadding(10);
                pcell1.setPaddingRight(0);
                pcell2.setBorder(0);
                pcell2.setPadding(10);
                helpTable.addCell(pcell1);
                helpTable.addCell(pcell2);

                PdfPCell mainCell61 = new PdfPCell(helpTable);
                mainCell61.setBorder(0);
                mainTable.addCell(mainCell61);
                getHtmlCell("<br>",mainTable,baseUrl);
                getHtmlCell("<br>",mainTable,baseUrl);
                getHtmlCell(postText.trim(),mainTable,baseUrl);
                document.add(mainTable);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

	public void getHtmlCell(String htmlString,PdfPTable mainTable ,String baseUrl) {
		StringReader strReader = new StringReader(htmlString.replaceAll("src=\"[^\"]*?video.jsp", "src=\""+baseUrl + "video.jsp"));
		StyleSheet styles = new StyleSheet();
		try {
			for (Object ele : HTMLWorker.parseToList(strReader, styles)) {
				 //Phrase phraseStr = new Phrase((Phrase)ele);
				PdfPCell cell = new PdfPCell();
				cell.addElement((Element)ele);
				cell.setBorder(0);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				mainTable.addCell(cell);
			}
		} catch (Exception e) {
			log.debug("Cannot read html string: " + e.getMessage());
		}
	}

    public PdfPTable getBlankTable() throws DocumentException{
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{5, 20, 15, 10, 15,10, 10,15});
        return table;
    }

    private PdfPCell createCell(String string, FontContext fontctx, int ALIGN_RIGHT, int i, int padd) {
        PdfPCell cell = new PdfPCell(new Paragraph(fontFamilySelector.process(string, fontctx)));
        cell.setHorizontalAlignment(ALIGN_RIGHT);
        cell.setBorder(i);
        cell.setPadding(padd);
        return cell;
    }

    public PdfPTable getCompanyInfo(String com[]) {
        PdfPTable tab1 = new PdfPTable(1);
        tab1.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell cell = new PdfPCell(new Paragraph(fontFamilySelector.process(com[0], FontContext.TABLE_DATA)));//fontBoldMedium));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(0);
        tab1.addCell(cell);
        for (int i = 1; i < com.length; i++) {
            cell = new PdfPCell(new Paragraph(fontFamilySelector.process(com[i],FontContext.TABLE_DATA)));// fontRegularBold));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(0);
            tab1.addCell(cell);
        }
        return tab1;
    }

    public void addTableRow(PdfPTable container, PdfPTable table) {
        PdfPCell tableRow = new PdfPCell(table);
        tableRow.setBorder(0);
        tableRow.setPaddingRight(10);
        tableRow.setPaddingLeft(10);
        container.addCell(tableRow);
    }
    public class EnglishNumberToWords {

        private final String[] tensNames = {
            "", " Ten", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", " Seventy", " Eighty", " Ninety"
        };
        private final String[] numNames = {
            "", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine", " Ten", " Eleven", " Twelve",
            " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen", " Eighteen", " Nineteen"
        };

        private String convertLessThanOneThousand(int number) {
            String soFar;
            if (number % 100 < 20) {
                soFar = numNames[number % 100];
                number /= 100;
            } else {
                soFar = numNames[number % 10];
                number /= 10;
                soFar = tensNames[number % 10] + soFar;
                number /= 10;
            }
            if (number == 0) {
                return soFar;
            }
            return numNames[number] + " Hundred" + soFar;
        }

        private String convertLessOne(int number, KWLCurrency currency) {
            String soFar;
            String val = "";//currency.getAfterDecimalName();
            if (number % 100 < 20) {
                soFar = numNames[number % 100];
                number /= 100;
            } else {
                soFar = numNames[number % 10];
                number /= 10;
                soFar = tensNames[number % 10] + soFar;
                number /= 10;
            }
            if (number == 0) {
                return " And " + soFar +" "+ val;
            }
            return " And " + numNames[number] +" "+ val + soFar;
        }

        public String convert(Double number, KWLCurrency currency) {
            if (number == 0) {
                return "Zero";
            }
            String snumber = Double.toString(number);
            String mask = "000000000000.00";
            DecimalFormat df = new DecimalFormat(mask);
            snumber = df.format(number);
            int billions = Integer.parseInt(snumber.substring(0, 3));
            int millions = Integer.parseInt(snumber.substring(3, 6));
            int hundredThousands = Integer.parseInt(snumber.substring(6, 9));
            int thousands = Integer.parseInt(snumber.substring(9, 12));
            int fractions = Integer.parseInt(snumber.substring(13, 15));
            String tradBillions;
            switch (billions) {
                case 0:
                    tradBillions = "";
                    break;
                case 1:
                    tradBillions = convertLessThanOneThousand(billions) + " Billion ";
                    break;
                default:
                    tradBillions = convertLessThanOneThousand(billions) + " Billion ";
            }
            String result = tradBillions;

            String tradMillions;
            switch (millions) {
                case 0:
                    tradMillions = "";
                    break;
                case 1:
                    tradMillions = convertLessThanOneThousand(millions) + " Million ";
                    break;
                default:
                    tradMillions = convertLessThanOneThousand(millions) + " Million ";
            }
            result = result + tradMillions;

            String tradHundredThousands;
            switch (hundredThousands) {
                case 0:
                    tradHundredThousands = "";
                    break;
                case 1:
                    tradHundredThousands = "One Thousand ";
                    break;
                default:
                    tradHundredThousands = convertLessThanOneThousand(hundredThousands) + " Thousand ";
            }
            result = result + tradHundredThousands;
            String tradThousand;
            tradThousand = convertLessThanOneThousand(thousands);
            result = result + tradThousand;
            String paises;
            switch (fractions) {
                case 0:
                    paises = "";
                    break;
                default:
                    paises = convertLessOne(fractions, currency);
            }
            result = result + paises; //to be done later
            result = result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
//            result = result.substring(0, 1).toUpperCase() + result.substring(1).toLowerCase(); // Make first letter of operand capital.
            return result;
        }
    }
}

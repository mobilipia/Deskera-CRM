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

import com.krawler.common.service.ServiceException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class genericFileUpload {
    private static final Log logger = LogFactory.getLog(genericFileUpload.class);
	protected Map parameters = new HashMap();
	protected boolean isUploaded;
	protected String Ext;
        public String ErrorMsg ="";
	public void doPost(List fileItems, String filename,
			String destinationDirectory) {
		File destDir = new File(destinationDirectory);
		Ext = "";
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		DiskFileUpload fu = new DiskFileUpload();
		fu.setSizeMax(-1);
		fu.setSizeThreshold(4096);
		fu.setRepositoryPath(destinationDirectory);
		for (Iterator i = fileItems.iterator(); i.hasNext();) {
			FileItem fi = (FileItem) i.next();
			if (!fi.isFormField()) {
				String fileName = null;
				try {
					fileName = new String(fi.getName().getBytes(), "UTF8");
					if (fileName.contains("."))
						Ext = fileName.substring(fileName.lastIndexOf("."));
					if (fi.getSize() != 0) {
						this.isUploaded = true;
						File uploadFile = new File(destinationDirectory + "/"
								+ filename + Ext);
						fi.write(uploadFile);
						imgResize(destinationDirectory + "/" + filename + Ext,
								100, 100, destinationDirectory + "/" + filename
										+ "_100");
						imgResize(destinationDirectory + "/" + filename + Ext,
								35, 35, destinationDirectory + "/" + filename);

					} else {
						this.isUploaded = false;
					}
                                } catch (Exception e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}

	}

    
        public void doPostCompay(List fileItems, String filename,
			String destinationDirectory) {
		File destDir = new File(destinationDirectory);
		Ext = "";
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		DiskFileUpload fu = new DiskFileUpload();
		fu.setSizeMax(-1);
		fu.setSizeThreshold(4096);
		fu.setRepositoryPath(destinationDirectory);
		for (Iterator i = fileItems.iterator(); i.hasNext();) {
			FileItem fi = (FileItem) i.next();
			if (!fi.isFormField()) {
				String fileName = null;
				try {
					fileName = new String(fi.getName().getBytes(), "UTF8");
					if (fileName.contains("."))
						Ext = fileName.substring(fileName.lastIndexOf("."));
					if (fi.getSize() != 0) {
                                            	this.isUploaded = true;
						File uploadFile = new File(destinationDirectory + "/"
								+ "temp_"+filename + Ext);
						fi.write(uploadFile);
                                                imgResizeCompany(destinationDirectory + "/" +"temp_"+filename + Ext,
								0, 0, destinationDirectory + "/original_" + filename,true);
						imgResizeCompany(destinationDirectory + "/" + "temp_"+filename + Ext,
								130, 25, destinationDirectory + "/" + filename,false);
//						imgResize(destinationDirectory + "/" + filename + Ext,
//								0, 0, destinationDirectory + "/original_" + filename);
                                                uploadFile.delete();
					} else {
						this.isUploaded = false;
					}
                                } catch (Exception e) {
                                        this.ErrorMsg = "Problem occured while uploading logo";
					logger.warn(e.getMessage(), e);
				}
			}
		}
	}

    public void uploadFile(FileItem fi,String destdir,String fileid) throws ServiceException{
        try {
            String fileName = new String(fi.getName().getBytes(), "UTF8");
            File uploadFile = new File(destdir + "/" + fileName);
            fi.write(uploadFile);
            imgResizeCompany(destdir + "/" + fileName,
								16,16, destdir + "/" + fileid+ "_16",false);//False is pass to indicate that image should be resize and then store.
            imgResizeCompany(destdir + "/" + fileName,
								32, 32,destdir + "/" + fileid+ "_32",false);
            uploadFile.delete();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            this.ErrorMsg = "Problem occured while uploading logo";
            
        }
    }


	public boolean isUploaded() {
		return isUploaded;
	}

	public String getExt() {
		return ".jpg";
	}
        public String getCompanyImageExt() {
		return ".png";
	}
        public final void imgResizeCompany(String imagePath, int Width, int Height,
			String fName,boolean ori) throws IOException {
		try {
			// Get a path to the image to resize.
			// ImageIcon is a kluge to make sure the image is fully
			// loaded before we proceed.
			Image sourceImage = new ImageIcon(Toolkit.getDefaultToolkit()
					.getImage(imagePath)).getImage();
                        int imageWidth = sourceImage.getWidth(null);
                        int imageHeight = sourceImage.getHeight(null);
                        if(ori){
                            Width = imageWidth;
                            Height = imageHeight;
                        }else{
                            Width = imageWidth<Width?imageWidth:Width;
                            Height = imageHeight<Height?imageHeight:Height;
                            float imageRatio = ((float)imageWidth/(float)imageHeight);
                            float framemageratio = ((float)Width/(float)Height);
                            if(imageRatio>framemageratio){
                                float value = Width / imageRatio;
                                Height = (int)value;

                            }else{
                                float value = Height * imageRatio;
                                Width = (int)value;
                            }
                        }
                        BufferedImage resizedImage = this.scaleCompanyImage(sourceImage, Width,
					Height);
			ImageIO.write(resizedImage, "PNG", new File(fName + ".png"));
                        sourceImage.flush();
		} catch (Exception e) {
                    this.ErrorMsg = "Problem occured while uploading logo";
			logger.warn(e.getMessage(), e);
		}
	}

	public final void imgResize(String imagePath, int Width, int Height,
			String fName) throws IOException {
		try {
			// Get a path to the image to resize.
			// ImageIcon is a kluge to make sure the image is fully
			// loaded before we proceed.
			Image sourceImage = new ImageIcon(Toolkit.getDefaultToolkit()
					.getImage(imagePath)).getImage();

			BufferedImage resizedImage = this.scaleImage(sourceImage, Width,
					Height);
			ImageIO.write(resizedImage, "jpeg", new File(fName + ".jpg"));
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}

	private BufferedImage scaleImage(Image sourceImage, int width, int height) {
		ImageFilter filter = new ReplicateScaleFilter(width, height);
		ImageProducer producer = new FilteredImageSource(sourceImage
				.getSource(), filter);
		Image resizedImage = Toolkit.getDefaultToolkit().createImage(producer);

		return this.toBufferedImage(resizedImage);
	}

	private BufferedImage toBufferedImage(Image image) {
		image = new ImageIcon(image).getImage();
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics g = bufferedImage.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bufferedImage;
	}
        private BufferedImage scaleCompanyImage(Image sourceImage, int width, int height) {
		ImageFilter filter = new ReplicateScaleFilter(width, height);
		ImageProducer producer = new FilteredImageSource(sourceImage
				.getSource(), filter);
		Image resizedImage = Toolkit.getDefaultToolkit().createImage(producer);

		return this.toBufferedCompanyImage(resizedImage);
	}
        private BufferedImage toBufferedCompanyImage(Image image){
            	image = new ImageIcon(image).getImage();
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufferedImage.createGraphics();
//		 java.awt.Color transparent = new java.awt.Color(255, 255, 255, 1);
//                g.setColor(transparent);
//                int rule = java.awt.AlphaComposite.SRC_OVER;
//                        
//                java.awt.AlphaComposite ac = java.awt.AlphaComposite.getInstance(rule,1);
//                g.setComposite(ac);
//		g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bufferedImage;
        }
        public static void main(String args[]){
            try{
                genericFileUpload a  = new genericFileUpload();
                a.imgResizeCompany("/home/mosin/images/pics/aragorn.jpg",1300,1000,"/home/mosin/images/pics/test",true);
            }catch(Exception e){
                logger.warn(e.getMessage(), e);
            }
        }
}

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
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.swing.ImageIcon;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;


public class FileUploadHandler {
    public HashMap getItems(HttpServletRequest request) throws ServiceException{
        HashMap itemMap=null;
		try {
                FileItemFactory factory = new DiskFileItemFactory(4096,new File("/tmp"));
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setSizeMax(10485760);//10 mb
                List fileItems = upload.parseRequest(request);
                Iterator iter = fileItems.iterator();
                itemMap=new HashMap();
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();

                    if (item.isFormField()) {
                        itemMap.put(item.getFieldName(), item.getString("UTF-8"));
                    } else {
                        itemMap.put(item.getFieldName(), item);
                    }
                }
        } catch (Exception e) {
                e.printStackTrace();
                throw ServiceException.FAILURE("FileUploadHandler.getItems", e);
        }
        return itemMap;
    }

    public void uploadFile(FileItem fileItem, String fileName, String destinationDirectory) throws ServiceException {
		try {
            File destDir = new File(destinationDirectory);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            fileItem.write(new File(destinationDirectory, fileName));
        } catch (Exception e) {
                throw ServiceException.FAILURE("FileUploadHandler.uploadFile", e);
        }
    }

    public void uploadImage(FileItem fileItem, String fileName, String destinationDirectory, int width, int height, boolean company, boolean original) throws ServiceException {
		try {
            File destDir = new File(destinationDirectory);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            File temp=new File(destinationDirectory, "temp_"+fileItem.getName());
            fileItem.write(temp);
            String fName=(fileName.contains(".")?fileName.substring(0,fileName.lastIndexOf(".")):fileName);
            imgResize(temp.getAbsolutePath(), width, height,destinationDirectory+"/"+fName, company, original);
            temp.delete();
        } catch (Exception e) {
                throw ServiceException.FAILURE("FileUploadHandler.uploadImage", e);
        }
    }

    public static String getImageExt() {
		return ".jpg";
	}

    public static String getCompanyImageExt() {
		return ".png";
	}


	public final void imgResize(String sourcePath, int Width, int Height,
			String destPath, boolean isCompany, boolean ori) throws IOException {
		try {
            String ext=getImageExt();String type="jpeg";int typeRGB=BufferedImage.TYPE_INT_RGB;
			Image sourceImage = new ImageIcon(Toolkit.getDefaultToolkit()
					.getImage(sourcePath)).getImage();
            if(isCompany){
                ext=getCompanyImageExt();
                type="PNG";
                typeRGB=BufferedImage.TYPE_INT_ARGB;
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
            }

			BufferedImage resizedImage = this.scaleImage(sourceImage, Width,
					Height, typeRGB);
			ImageIO.write(resizedImage, type, new File(destPath+ext));
		} catch (Exception e) {
			Logger.getInstance(FileUploadHandler.class).error(e, e);
		}
	}

	private BufferedImage scaleImage(Image sourceImage, int width, int height, int typeRGB) {
		ImageFilter filter = new ReplicateScaleFilter(width, height);
		ImageProducer producer = new FilteredImageSource(sourceImage
				.getSource(), filter);
		Image resizedImage = Toolkit.getDefaultToolkit().createImage(producer);

		return this.toBufferedImage(resizedImage, typeRGB);
	}

	private BufferedImage toBufferedImage(Image image, int type) {
		image = new ImageIcon(image).getImage();
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
				image.getHeight(null), type);
		Graphics g = bufferedImage.createGraphics();
        if(type==BufferedImage.TYPE_INT_RGB){
            g.setColor(Color.white);
            g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
        }
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bufferedImage;
	}
}

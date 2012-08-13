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
package com.krawler.br.spring;

import com.krawler.br.BusinessProcess;
import com.krawler.br.ProcessException;
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.modules.ModuleDefinition;
import com.krawler.br.modules.ModuleBag;
import com.krawler.br.modules.ModuleProperty;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class RConverterImpl implements RConverter {
    ModuleBag mb;
    DateFormat df;

    public void setModuleBag(ModuleBag mb) {
        this.mb = mb;
    }

    @Override
    public Map convert(HttpServletRequest request, BusinessProcess process) throws ProcessException{
        try {
            df = authHandler.getDateFormatter(request);
        } catch (SessionExpiredException ex) {
            throw new ProcessException(ex);
        }
        if (ServletFileUpload.isMultipartContent(request)) {
            return convertWithFile(request, process.getInputParams());
        } else {
            return convert(request, process.getInputParams());
        }
    }

    public Map convert(HttpServletRequest request, Map reqParams) throws ProcessException {
        Iterator<String> itr = reqParams.keySet().iterator();
        Map m = new HashMap();
        while (itr.hasNext()) {
            String key = itr.next();
            OperationParameter o = (OperationParameter) reqParams.get(key);
            m.put(key, getParam(request, key, o));
        }
        return m;
    }

    private Object getValue(Object obj, ModuleDefinition md) throws ProcessException {
        Object retobj = obj;
        switch (md.getType()) {
            case JSON:
                Iterator itr = md.getPropertyNames().iterator();
                JSONObject jobj;
                try {
                    jobj = obj instanceof String ? new JSONObject((String) obj) : (JSONObject) obj;

                    while (itr.hasNext()) {
                        String pname = (String) itr.next();
                        ModuleProperty mp = md.getProperty(pname);
                        Object temp = jobj.opt(pname);
                        if (temp == null) {
                            continue;

                        }
                        List l = new ArrayList();
                        if (mp.isMulti()) {
                            JSONArray jArr = (JSONArray) temp;
                            for (int i = 0; i < jArr.length(); i++) {
                                l.add(getValue(jArr.get(i), mb.getModuleDefinition(md.getProperty(pname).getType())));
                            }
                        } else {
                            l.add(getValue(temp, mb.getModuleDefinition(md.getProperty(pname).getType())));
                        }
                        jobj.put(pname, convertToMultiType(l, mp.getMulti(), mp.getType()));
                    }
                    if (obj instanceof String) {
                        retobj = jobj;
                        
                    }
                } catch (JSONException ex) {
                    throw new ProcessException("can't convert from string to json", ex);
                }
                break;
            case MAP:
                itr = md.getPropertyNames().iterator();
                Map map = new HashMap();
                try {
                    jobj = obj instanceof String ? new JSONObject((String) obj) : (JSONObject) obj;

                    while (itr.hasNext()) {
                        String pname = (String) itr.next();
                        ModuleProperty mp = md.getProperty(pname);
                        Object temp = jobj.opt(pname);
                        if (temp == null) {
                            continue;

                        }
                        List l = new ArrayList();
                        if (mp.isMulti()) {
                            JSONArray jArr = (JSONArray) temp;
                            for (int i = 0; i < jArr.length(); i++) {
                                l.add(getValue(jArr.get(i), mb.getModuleDefinition(md.getProperty(pname).getType())));
                            }
                        } else {
                            l.add(getValue(temp, mb.getModuleDefinition(md.getProperty(pname).getType())));
                        }
                        map.put(pname, convertToMultiType(l, mp.getMulti(), mp.getType()));
                    }
                    retobj = map;
                } catch (JSONException ex) {
                    throw new ProcessException("can't convert from string to json", ex);
                }
                break;
            case SIMPLE:
                if (obj instanceof String) {
                    retobj = getSimpleValue(md.getClassName(), (String) obj);
                    
                }
                break;
            case POJO:
                itr = md.getPropertyNames().iterator();
                try {
                    Object pojo = Class.forName(md.getClassName()).newInstance();

                    jobj = obj instanceof String ? new JSONObject((String) obj) : (JSONObject) obj;

                    while (itr.hasNext()) {
                        String pname = (String) itr.next();
                        ModuleProperty mp = md.getProperty(pname);
                        Object temp = jobj.opt(pname);
                        if (temp == null) {
                            continue;

                        }
                        List l = new ArrayList();
                        if (mp.isMulti()) {
                            JSONArray jArr = (JSONArray) temp;
                            for (int i = 0; i < jArr.length(); i++) {
                                l.add(getValue(jArr.get(i), mb.getModuleDefinition(md.getProperty(pname).getType())));
                            }
                        } else {
                            l.add(getValue(temp, mb.getModuleDefinition(md.getProperty(pname).getType())));
                        }
                        try {
                            PropertyUtils.setProperty(pojo, pname, convertToMultiType(l, mp.getMulti(), mp.getType()));
                        } catch (IllegalAccessException ex) {
                            throw new ProcessException("property not accessible : " + md.getName() + "." + pname);
                        } catch (InvocationTargetException ex) {
                            throw new ProcessException("property not available : " + md.getName() + "." + pname);
                        } catch (NoSuchMethodException ex) {
                            throw new ProcessException("property not available : " + md.getName() + "." + pname);
                        }
                    }
                    retobj = pojo;
                } catch (InstantiationException ex) {
                    throw new ProcessException("module not available : " + md.getName());
                } catch (IllegalAccessException ex) {
                    throw new ProcessException("module not available : " + md.getName());
                } catch (ClassNotFoundException ex) {
                    throw new ProcessException("module not available : " + md.getName());
                } catch (JSONException ex) {
                    throw new ProcessException("can't convert from string to json", ex);
                }
                break;
            default:
                throw new ProcessException("conversion from string to " + md.getType() + " not supported");
        }
        return retobj;
    }

    private Object getSimpleValue(String cls, String valStr) {
        Object val = null;
        if (cls.equals(ModuleBag.PRIMITIVE.BOOLEAN.className())) {
            val = Boolean.parseBoolean(valStr);
            
        } else if (cls.equals(ModuleBag.PRIMITIVE.BYTE.className())) {
            val = Byte.parseByte(valStr);
            
        } else if (cls.equals(ModuleBag.PRIMITIVE.CHAR.className())) {
            val = valStr.charAt(0);
            
        } else if (cls.equals(ModuleBag.PRIMITIVE.SHORT.className())) {
            val = Short.parseShort(valStr);
            
        } else if (cls.equals(ModuleBag.PRIMITIVE.INT.className())) {
            val = Integer.parseInt(valStr);
            
        } else if (cls.equals(ModuleBag.PRIMITIVE.LONG.className())) {
            val = Long.parseLong(valStr);
            
        } else if (cls.equals(ModuleBag.PRIMITIVE.FLOAT.className())) {
            val = Float.parseFloat(valStr);
            
        } else if (cls.equals(ModuleBag.PRIMITIVE.DOUBLE.className())) {
            val = Double.parseDouble(valStr);
            
        } else if (cls.equals(ModuleBag.PRIMITIVE.STRING.className())) {
            val = valStr;
            
        } else if (cls.equals(ModuleBag.PRIMITIVE.DATE.className())) {
            try {
                val = df.parse(valStr);
            } catch (Exception ex) {
                val = null;
            }
        }
        return val;
    }

    private Object convertToMultiType(List l, ModuleProperty.MULTI multi, String type) {
        if (multi == null) {
            if (l.isEmpty()) {
                return null;
                
            }
            return l.get(0);
        }
        switch (multi) {
            case ARRAY:
                Class cls;
                try {
                    cls = Class.forName(mb.getModuleDefinition(type).getClassName());
                } catch (Exception ex) {
                    cls = Object.class;
                }
                return l.toArray((Object[]) Array.newInstance(cls, 0));
            case SET:
                return new HashSet(l);
            case JSONARRAY:
                return new JSONArray(l);
            default:
                return l;
        }
    }

    private Object getParam(HttpServletRequest request, String key, OperationParameter ap) throws ProcessException {
        ModuleDefinition md = mb.getModuleDefinition(ap.getType());
        String[] params = request.getParameterValues(key);

        List l = new ArrayList();
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                l.add(getValue(params[i], md));
            }
        }
        return convertToMultiType(l, ap.getMulti(), ap.getType());
    }

    public Map convertWithFile(HttpServletRequest request, Map reqParams) throws ProcessException {
        HashMap itemMap = new HashMap(), tmpMap=new HashMap();
        try {
            FileItemFactory factory = new DiskFileItemFactory(4096, new File("/tmp"));
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(1000000);
            List fileItems = upload.parseRequest(request);
            Iterator iter = fileItems.iterator();
            
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                String key = item.getFieldName();
                OperationParameter o = (OperationParameter) reqParams.get(key);
                if (reqParams.containsKey(key)) {
                    if (item.isFormField()) {
                        putItem(tmpMap, key, getValue(item.getString("UTF-8"), mb.getModuleDefinition(o.getType())));
                    } else {
                        File destDir = new File(StorageHandler.GetProfileImgStorePath());
                        if (!destDir.exists()) {
                            destDir.mkdirs();
                        }

                        File f = new File(destDir, UUID.randomUUID()+"_"+item.getName());
                        try {
                            item.write(f);
                        } catch (Exception ex) {
                            Logger.getLogger(RConverterImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        putItem(tmpMap, key, f.getAbsolutePath());
                    }
                }
            }

            iter = tmpMap.keySet().iterator();

            while(iter.hasNext()){
                String key = (String)iter.next();
                OperationParameter o = (OperationParameter) reqParams.get(key);
                itemMap.put(key, convertToMultiType((List)tmpMap.get(key), o.getMulti(), o.getType()));
            }

        } catch (FileUploadException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } catch (UnsupportedEncodingException e) {
        	throw new ProcessException(e.getMessage(), e);
		}
        return itemMap;
    }

    private void putItem(HashMap itemMap, String key, Object value) {
        List l;
        if(itemMap.containsKey(key)){
            l = (List)itemMap.get(key);
        }else{
            l = new ArrayList();
            itemMap.put(key, l);
        }
        l.add(value);
    }
}

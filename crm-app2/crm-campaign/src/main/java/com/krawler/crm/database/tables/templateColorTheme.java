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
package com.krawler.crm.database.tables;

public class templateColorTheme implements java.io.Serializable{

    private String id;
    private String theme;
    private String background;
    private String headerbackground;
    private String headertext;
    private String footerbackground;
    private String footertext;
    private String bodybackground;
    private String bodytext;
    private com.krawler.crm.database.tables.colorThemeGroup groupid;
    private int deleted;

    public String getId(){
        return this.id;
    }

    public String getTheme(){
        return this.theme;
    }

    public String getBackground(){
        return this.background;
    }

    public String getHeaderbackground(){
        return this.headerbackground;
    }

    public String getHeadertext(){
        return this.headertext;
    }

    public String getFooterbackground(){
        return this.footerbackground;
    }

    public String getFootertext(){
        return this.footertext;
    }

    public String getBodybackground(){
        return this.bodybackground;
    }

    public String getBodytext(){
        return this.bodytext;
    }

    public int getDeleted(){
        return this.deleted;
    }

    public com.krawler.crm.database.tables.colorThemeGroup getGroupid() {
        return this.groupid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public void setHeaderbackground(String header_back) {
        this.headerbackground = header_back;
    }

    public void setHeadertext(String header_text) {
        this.headertext = header_text;
    }

    public void setFooterbackground(String footer_back) {
        this.footerbackground = footer_back;
    }

    public void setFootertext(String footer_text) {
        this.footertext = footer_text;
    }
    public void setBodybackground(String footer_text) {
        this.bodybackground = footer_text;
    }
    public void setBodytext(String footer_text) {
        this.bodytext = footer_text;
    }

    public void setDeleted(int del) {
        this.deleted = del;
    }

    public void setGroupid(com.krawler.crm.database.tables.colorThemeGroup grp) {
        this.groupid = grp;
    }
}

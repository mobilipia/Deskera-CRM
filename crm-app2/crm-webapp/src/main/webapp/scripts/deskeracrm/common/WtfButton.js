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
Wtf.common.WtfButton = function(config){
    Wtf.common.WtfButton.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.common.WtfButton, Wtf.Component, {
    width: 72,
    height: 91,
    imgWidth: 35,
    imgHeight: 35,
    initialized: false,
    imgObj: null,
    zoomLevel: 0.11,
    hoverWidth: null,
    hoverHeight: null,
    diffX: null,
    diffY: null,
    flag: 0,
    imgX: 0,
    imgY: 0,
    initComponent: function(){
        Wtf.common.WtfButton.superclass.initComponent.call(this);
        this.addEvents({
            "clicked": true
        });
    },
    onRender: function(){
        this.elDom = Wtf.get(this.renderTo).createChild({
            tag: 'div',
            cls: 'wtfbutton'
        });
        this.image = Wtf.get(this.elDom.dom.appendChild(document.createElement('img')));
        this.label = Wtf.get(this.elDom.dom.appendChild(document.createElement('span')));
        this.labelShadow = Wtf.get(this.elDom.dom.appendChild(document.createElement('span')));
        this.image.dom.src = this.imgSrc;
        this.image.dom.alt = this.caption;

        this.imgX = Math.ceil(this.width / 2 - this.imgWidth / 2);
        this.imgY = Math.ceil(this.width / 2 - this.imgHeight / 2 - 15);
        this.image.setXY([this.image.getX() + this.imgX, this.image.getY() + this.imgY + 15]);

        this.shadow = new Wtf.common.Shadow({
            offset: 5
        });
        this.shadow.show(this.image);

        this.label.dom.innerHTML = this.caption;
        this.label.dom.className = "label";
        this.labelShadow.dom.innerHTML = this.caption;
        this.labelShadow.dom.className = "labelShadow";

        this.elDom.addListener('mouseover', this.onHover, this);
        this.elDom.addListener('mouseout', this.onOut, this);
        this.elDom.addListener('click', this.onClick, this);

        this.elDom.addListener('click', this.onClick, this);

        this.imgObj = this.image;
        this.shadow.realign(this.imgX, this.imgY + 15, this.imgWidth, this.imgHeight);

        this.hoverWidth = Math.ceil(this.imgWidth + this.imgWidth * this.zoomLevel);
        this.hoverHeight = Math.ceil(this.imgHeight + this.imgWidth * this.zoomLevel);
        this.diffX = Math.ceil(this.hoverWidth - this.imgWidth);
        this.diffY = Math.ceil(this.hoverHeight - this.imgHeight);
        this.imgX = this.imgObj.getX();
        this.imgY = this.imgObj.getY();
        this.topMove = this.diffX / 2;
        this.leftMove = this.diffY / 2;

        this.elDom.dom.style.display = "none";
    },
    onHover: function(e, el, opt){
        if (!(Wtf.isIE && e.within(this.elDom, true))) {
            this.elDom.dom.style.background = "url(../../images/background1.png) no-repeat 0 0";
            if (Wtf.isIE)
                this.elDom.dom.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='../../images/background1.png', sizingMethod='crop')";
            this.imgObj.dom.style.width = this.hoverWidth + "px";
            this.imgObj.dom.style.height = this.hoverHeight + "px";
            this.imgObj.dom.style.left = parseInt(this.imgObj.dom.style.left.split("px")[0]) - this.leftMove + "px";
            this.imgObj.dom.style.top = parseInt(this.imgObj.dom.style.top.split("px")[0]) - this.topMove + "px";
        }
    },
    onOut: function(e, el, opt){
        if (!(Wtf.isIE && e.within(this.elDom, true))) {
            this.elDom.dom.style.background = "";
            if (Wtf.isIE)
                this.elDom.dom.style.filter = "";
            this.imgObj.dom.style.width = this.imgWidth + "px";
            this.imgObj.dom.style.height = this.imgHeight + "px";
            this.imgObj.dom.style.left = parseInt(this.imgObj.dom.style.left.split("px")[0]) + this.leftMove + "px";
            this.imgObj.dom.style.top = parseInt(this.imgObj.dom.style.top.split("px")[0]) + this.topMove + "px";
        }
    },
    onClick: function(e, el, opt){
        e.preventDefault();
//kuldeep        mainPanel.loadTab(this.href, "   " + this.id, this.caption, "navareadashboard", this.tabtype, true);
    },
    addData: function(imgSrc, caption, id, href, tabtype){
        this.elDom.dom.style.display = "block";
        this.image.dom.src = imgSrc;
        this.image.dom.alt = caption;
        this.id = id;
        this.href = href;
        this.tabtype = tabtype;
        this.label.dom.innerHTML = Wtf.util.Format.ellipsis(caption, 20);
        this.labelShadow.dom.innerHTML = Wtf.util.Format.ellipsis(caption, 20);
        this.caption = caption;

    },
    removeButton: function(){
        this.elDom.dom.parentNode.removeChild(this.elDom.dom);
    },
    hideButton: function(){
        this.elDom.dom.style.display = "none";
    }
})

Wtf.reg('wtfbutton', Wtf.common.WtfButton);

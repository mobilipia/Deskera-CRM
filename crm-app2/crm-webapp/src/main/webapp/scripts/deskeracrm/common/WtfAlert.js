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
Wtf.ux.WtfAlert = function(config){
    Wtf.ux.WtfAlert.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.ux.WtfAlert, Wtf.Component, {

    initComponent: function(){
        Wtf.ux.WtfAlert.superclass.initComponent.call(this);
    },

    onRender: function(){
        this.elDom = Wtf.get(this.renderTo).createChild({
            tag: 'div',
            cls: 'wtfalert'
        });
        this.image = Wtf.get(this.elDom.dom.appendChild(document.createElement('img')))
        this.label = Wtf.get(this.elDom.dom.appendChild(document.createElement('span')))
        this.labelShadow = Wtf.get(this.elDom.dom.appendChild(document.createElement('span')))
        this.image.dom.alt = this.caption;

        this.shadow = new Wtf.common.Shadow({
            offset: 5
        })
		this.shadow.setZIndex(-1);
        this.shadow.show(this.image)

        this.label.dom.innerHTML = this.caption
        this.label.dom.className = "label"
        this.labelShadow.dom.innerHTML = this.caption
        this.labelShadow.dom.className = "labelShadow"

        this.elDom.addListener('mouseover', this.onHover, this)
        this.elDom.addListener('mouseout', this.onOut, this)
        this.elDom.dom.style.display = "none";
    },

    onHover: function(e, el, opt){
        if (!(Wtf.isIE && e.within(this.elDom, true))) {
            this.labelShadow.dom.style.background = "#FBD76D url(../../images/alertbackground.png) repeat-y 0 0";
            if (Wtf.isIE)
                this.labelShadow.dom.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='../../images/alertbackground.png', sizingMethod='crop')";
        }
    },

    onOut: function(e, el, opt){
        if (!(Wtf.isIE && e.within(this.elDom, true))) {
            this.labelShadow.dom.style.background = "";
            if (Wtf.isIE)
                this.labelShadow.dom.style.filter = "";
        }
    },

    onClick: function(e, el, opt){
        e.preventDefault();
    },

    addData: function(imgSrc, caption, id, href){
        this.elDom.dom.style.display = "block";
        this.image.dom.src = imgSrc;
        this.image.dom.alt = caption;
        this.id = id;
        this.label.dom.innerHTML = caption;
        this.labelShadow.dom.innerHTML = caption;
        this.caption = caption;
    },

    removeButton: function(){
        this.elDom.dom.parentNode.removeChild(this.elDom.dom);
    },

    hideButton: function(){
        this.elDom.dom.style.display = "none";
    }
});

Wtf.reg('wtfalert', Wtf.ux.WtfAlert);

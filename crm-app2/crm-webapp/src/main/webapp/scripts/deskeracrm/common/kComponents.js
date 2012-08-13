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
Wtf.common.Shadow = function(conf){
    Wtf.apply(this, conf);
    Wtf.common.Shadow.superclass.constructor.call(this, conf);
}
Wtf.extend(Wtf.common.Shadow, Wtf.Shadow,{
    show : function(target){
        target = Wtf.get(target);
        if(!this.el){
            this.el = Wtf.Shadow.Pool.pull();
            if(this.el.dom.nextSibling != target.dom){
                this.el.insertBefore(target);
            }
        }
//        this.el.setStyle("z-index", this.zIndex || parseInt(target.getStyle("z-index"), 10)-1);
        this.el.setStyle("z-index", this.zIndex || target.getStyle("z-index"));
        if(Wtf.isIE){
            this.el.dom.style.filter="progid:DXImageTransform.Microsoft.alpha(opacity=50) progid:DXImageTransform.Microsoft.Blur(pixelradius="+(this.offset)+")";
        }
        this.realign(
            target.getLeft(true),
            target.getTop(true),
            target.getWidth(),
            target.getHeight()
        );
        this.el.dom.style.display = "block";
    }
});


/*
Wtf.common.Menu = function(conf){
    Wtf.apply(this, conf);
    Wtf.common.Menu.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.common.Menu, Wtf.menu.Menu, {
    autoWidth : function(){
        var el = this.el, ul = this.ul;
        if(!el){
            return;
        }
        var w = this.width;
        if(w){
            el.setWidth(w);
        }else if(Wtf.isIE6 || Wtf.isIE7){
            el.setWidth(this.minWidth);
            var t = el.dom.offsetWidth;
            el.setWidth(ul.getWidth()+el.getFrameWidth("lr"));
        }
    }
});
*/
/*
Wtf.common.Button = function(conf){
    Wtf.apply(this, conf);
    Wtf.common.Button.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.common.Button, Wtf.Button, {
    initComponent : function(){
        Wtf.Button.superclass.initComponent.call(this);
        this.addEvents(
            "click",
            "toggle",
            'mouseover',
            'mouseout',
            'menushow',
            'menuhide',
            'menutriggerover',
            'menutriggerout'
        );
        if(this.menu){
            this.menu = new Wtf.common.Menu({items: this.menu});
        }
        if(typeof this.toggleGroup === 'string'){
            this.enableToggle = true;
        }
    }
});
*/

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
Wtf.moduleRenderer = function(conf){
    Wtf.apply(this, conf);

    this.events = {
        "updateRendererStore": true
    };

    if (this.type == "new"){
        this.buttonTitle="Create";
        this.title="New renderer";
    }else{
        this.buttonTitle="Edit";
        this.title="Edit renderer";
    }

    if (this.rendererValue.match("&#43;") != null){
        this.rendererValue=this.rendererValue.replace(/&#43;/g,"+");
    }
    Wtf.moduleRenderer.superclass.constructor.call(this, {
        title:this.title ,
        iconCls: 'iconwin',
        height: 220,
        width: 325,
        modal: true,
        resizable: false,
        bodyStyle: "backgroung-color: white;",
        items: [this.rendererForm=new Wtf.form.FormPanel({
            layout: 'form',
            border: false,
            bodyStyle: "padding: 10px;",
            items: [this.rendererName=new Wtf.form.TextField({
                width: 180,
                fieldLabel: "Name",
                value:this.rendererNameValue,
                allowBlank:false,
                regex: nameRegex,
                regexText : regexText,
                maxLength:20
            }),this.renderer=new Wtf.form.TextArea({
                height: 80,
                width: 180,
                fieldLabel: "Renderer",
                value:this.rendererValue,
                allowBlank:false
            })]
        })],
        buttons: [{
            text: this.buttonTitle,
            scope : this,
            handler: this.createeditRenderer,
            disabled:this.editButtondisabled
        },{
            text: "Cancel",
            scope : this,
            handler: function(){
                this.close();
            }
        }]
    });
}

Wtf.extend(Wtf.moduleRenderer, Wtf.Window, {
    createeditRenderer:function(){
        var name=this.rendererName.getValue();
        var value=this.renderer.getValue();
        var action;
        
        if (this.rendererForm.form.isValid()){
            if (this.buttonTitle == "Edit"){
                action=15;
            }else{
                action=13;
            }
            Wtf.Ajax.requestEx({
                url: Wtf.req.rbuild+'report.do',
                method:'GET',
                params: {
                    action: action,
                    id:this.rendererId,
                    name:name,
                    value:value.replace(/\+/g,"&#43;")
                }
                },
                this,
                function(resp) {
                    var resultObj = eval('('+resp+')');
                    if(resultObj.success) {
                        this.fireEvent("updateRendererStore",this.type,resultObj.id,name,value);
                        if (action == 13){
                            msgBoxShow(32,2);
                        }else{
                            msgBoxShow(35,2);
                        }
                        this.close();
                    }else{
                         (action == 13)?msgBoxShow(33,1):msgBoxShow(36,1);
                    }
                },
                function() {
                    //panel.enable();
            });
        }else{
            //msgBoxShow(34,1);
        }
    }
});



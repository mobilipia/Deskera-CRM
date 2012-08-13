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
Wtf.common.WtfButtonConfigWindow = function(conf){
    Wtf.apply(this, conf);
    conf.buttons = [{
        text: "Ok",
        handler: this.buttonClicked,
        scope: this
    },{
        text: "Cancel",
        handler: this.buttonClicked,
        scope: this
    }];
    Wtf.common.WtfButtonConfigWindow.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.common.WtfButtonConfigWindow, Wtf.Window, {
    modal: true,
    layout: "border",
    initComponent: function(conf){
        Wtf.common.WtfButtonConfigWindow.superclass.initComponent.call(this, conf);
        this.addEvents({
            "okClicked": true,
            "cancelClicked": true
        });
    },
    onRender: function(conf){
        Wtf.common.WtfButtonConfigWindow.superclass.onRender.call(this, conf);
        this.createConfigForm();
        this.createHandlerPanel();
        this.createConfigGrid();
        this.add(this.configForm);
        this.add(this.handlerPanel);
        this.add(this.confGrid);
    },
    afterShow: function(conf){
        Wtf.common.WtfButtonConfigWindow.superclass.afterShow.call(this, conf);
        this.setHandlerType("js");
    },
    createHandlerPanel: function(){
        this.handlerPanel = new Wtf.Panel({
            region: 'center',
            bodyStyle: "background: white none repeat scroll 0% !important",
            frames: false,
            border: false,
            layout: "form"
        });
        this.buttonHandler = this.handlerPanel.add(new Wtf.form.TextArea({
            itemCls: "configForm",
            width: 240,
            labelStyle: "width: 115px",
            height: 110,
            id: "handlerText",
            allowBlank: false,
            fieldLabel: "Handler Defination"
        }));
        this.paramRec = new Wtf.data.Record.create([
            { name: "pname" },
            { name: "value" },
            { name: "index" }
        ]);
        var cm = new Wtf.grid.ColumnModel([{
            header: "Name",
            editor: new Wtf.form.TextField({
                allowBlank: false
            }),
            dataIndex: "pname"
        },{
            header: "Value",
            editor: new Wtf.form.TextField({
                allowBlank: false
            }),
            dataIndex: "value"
        },{
            width: 30,
            header: "Delete",
            renderer: function(){
                return "<img src='images/Cancel.gif' class='deleteButtonConf'>"
            }
        }]);
        this.paramStore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, this.paramRec)
        });
        this.paramGrid = new Wtf.grid.EditorGridPanel({
            colModel: cm,
            clicksToEdit: 1,
            store: this.paramStore,
            autoScroll: true,
            title: "Params",
            region: "south",
            height: 130,
            hidden: true,
            viewConfig: {
                forceFit: true
            },
            tbar: [{
                text: "New Param",
                scope: this,
                handler: function(){
                    var newParam = new this.paramRec({
                        pname: "",
                        value: ""
                    });
                    this.paramStore.add(newParam);
                }
            }]
        });
        this.paramGrid.on("cellclick", function(obj, ri, ci, e){
            if(e.target.tagName == "IMG"){
                var store = obj.getStore();
                store.remove(store.getAt(ri));
            }
        }, this);
        this.jspFileName = this.handlerPanel.add(new Wtf.form.TextField({
            itemCls: "configForm",
            hidden: true,
            id: 'jspName',
            width: 240,
            labelStyle: "width: 115px",//"width: 85px",
            allowBlank: false,
            fieldLabel: "JSP File"
        }));
        this.handlerPanel.add(this.paramGrid);
    },
    setHandlerType: function(type){
        if(type == "js"){
            this.paramGrid.hide();
            if(this.jspFileName.container)
                this.jspFileName.container.dom.parentNode.style.display = "none";
            this.jspFileName.hide();
            if(this.buttonHandler.container)
                this.buttonHandler.container.dom.parentNode.style.display = "block";
            this.buttonHandler.show();
        } else {
            this.paramGrid.show();
            if(this.jspFileName.container)
                this.jspFileName.container.dom.parentNode.style.display = "block";
            this.jspFileName.show();
            if(this.buttonHandler.container)
                this.buttonHandler.container.dom.parentNode.style.display = "none";
            this.buttonHandler.hide();
        }
    },
    createConfigForm: function(){
        this.configForm = new Wtf.Panel({
            region: "north",
            height: 130,
            layout: "form",
            labelWidth: 50,
            border: false,
            bodyStyle: "background: white none repeat scroll 0% !important",
            tbar: [{
                text: "Add",
                scope: this,
                handler: this.addNewButtonConfig
            }]
        });
        if(!this.listenerWin){
            this.buttonText = this.configForm.add(new Wtf.form.TextField({
                itemCls: "configForm",
                width: 240,
                labelStyle: "width: 115px",//"width: 85px",
                allowBlank: false,
                fieldLabel: "Button Text",
                regex:nameRegex,
                regexText : regexText,
                maxLength:20
            }));
            this.scriptRadio = new Wtf.form.Radio({
                itemCls: "configFormRadio",
                cls: "radioButton",
                name: "ButtonType",
                labelStyle: "width: 80px",
                checked: true,
                allowBlank:false,
                fieldLabel: "Java Script"
            });
            var jspRadio = new Wtf.form.Radio({
                itemCls: "configFormRadio",
                cls: "radioButton",
                name: "ButtonType",
                allowBlank:false,
                fieldLabel: "JSP"
            });
            this.scriptRadio.on("change", function(obj, newval, oldVal){
                if(newval){
                    this.setHandlerType("js");
                }
            }, this);
            jspRadio.on("change", function(obj, newval, oldVal){
                if(newval){
                    this.setHandlerType("jsp");
                }
            }, this);
            var buttonType = new Wtf.form.FieldSet({
                title: "Button Type",
                cls: "configFormFieldset",
                labelWidth: 70,

                items:[this.scriptRadio ,jspRadio]
            });
            var typeFeidlSet = this.configForm.add(buttonType);
        } else {
            this.eventCombo = this.configForm.add(new Wtf.form.ComboBox({
                displayField: 'name',
                itemCls: "configForm",
                width: 240,
                labelStyle: "width: 115px",
                valueField: 'value',
                mode: 'local',
                fieldLabel: "Event",
                typeAhead: false,
                forceSelection:true,
                store: this.eventStore
            }));
        }
    },
    addNewButtonConfig: function(){
        if(!this.listenerWin){
            var buttonText = this.buttonText.getValue().trim();
            var funcDef = this.getButtonDef();//this.buttonHandler.getValue().trim();
            if(this.buttonText.validate()){
                if (funcDef != ""){
                    var temp = new this.confRec({
                        caption: buttonText,
                        type: this.scriptRadio.getValue() ? "js" : "jsp",
                        functext: funcDef
                    });
                    this.configStore.add(temp);
                    this.buttonText.reset();
                    this.buttonHandler.reset();
                } else {
                    msgBoxShow(["Error", "Please specify the handler defination."])
                }
            }
        } else {
            var event = this.eventCombo.getValue().trim();
            var funcDef = this.buttonHandler.getValue().trim();
            if(event != "" && funcDef != ""){
                var temp = new this.confRec({
                    caption: event,
                    functext: funcDef
                });
                this.configStore.add(temp);
                this.eventCombo.reset();
                this.buttonHandler.reset();
            } else {
                msgBoxShow(["Error", "Please specify the button text and the handler defination."])
            }
        }
    },
    getButtonDef: function(){
        var def = "";
        if(this.scriptRadio.getValue()){
            def = this.buttonHandler.getValue().trim();
//            def = 
        } else {
            def = this.jspFileName.getValue().trim() + "?";
            for(var cnt = 0; cnt < this.paramStore.getCount(); cnt++){
                var temp = this.paramStore.getAt(cnt);
                def += temp.data["pname"] + "=" + temp.data["value"] + "&";
            }
            def = def.substring(0, (def.length - 1));
        }
        return def;
    },
    createConfigGrid: function(){
        if(!this.listenerWin){
            this.confRec = new Wtf.data.Record.create([
                { name: "caption" },
                { name: "functext" },
                { name: "type" },
                { name: "index" }
            ]);
        } else {
            this.confRec = new Wtf.data.Record.create([
                { name: "caption" },
                { name: "functext" },
                { name: "index" }
            ]);
        }
        var cm = new Wtf.grid.ColumnModel([{ 
            header: this.listenerWin == true ? "Event" : "Button Text",
            dataIndex: "caption"
        },{
            header: "Handler",
            dataIndex: "functext"
        }/*,{
            header: "Type",
            dataIdex: "type",
            hidden: this.listenerWin == true ? true : false,
        }*/,{
            width: 30,
            header: "Delete",
            renderer: function(){
                return "<img src='images/Cancel.gif' class='deleteButtonConf'>"
            }
        }]);
        this.configStore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, this.confRec)
        });
        this.confGrid = new Wtf.grid.GridPanel({
            colModel: cm,
            title: "Buttons",
            store: this.configStore,
            autoScroll: true,
            region: "south",
            height: 110,
            viewConfig: {
                forceFit: true
            }
        });
        this.confGrid.on("cellclick", function(obj, ri, ci, e){
            if(e.target.tagName == "IMG"){
                var store = obj.getStore();
                store.remove(store.getAt(ri));
            }
        }, this);
        if(this.defaultConf !== null && this.defaultConf !== undefined && this.defaultConf != ""){
            var dButton = eval("(" + this.defaultConf + ")");
            for(var cnt = 0; cnt < dButton.length; cnt++){
                var temp = new this.confRec({
                    caption: dButton[cnt].text,
                    type: dButton[cnt].type,
                    functext: dButton[cnt].handler
                });
                this.configStore.add(temp);
            }
        }
    },
    buttonClicked: function(obj){
        if(obj.text == "Ok"){
            var buttonConfig = this.getButtonConf();
            this.fireEvent("okClicked", this, buttonConfig);
        } else {
            this.fireEvent("cancelClicked", this)
        }
    },
    getButtonConf: function(){
        if(!this.listenerWin){
            var conf = {};
            for(var cnt = 0; cnt < this.configStore.getCount(); cnt++){
                var temp = {};
                var rec = this.configStore.getAt(cnt);
                temp.text = rec.data["caption"];
                temp.handler = rec.data["functext"];
                temp.type = rec.data["type"];
                conf[cnt] = temp;
            }
        } else {
            var conf = "{";
            for(var cnt = 0; cnt < this.configStore.getCount(); cnt++){
                var rec = this.configStore.getAt(cnt);
                conf += rec.data["caption"] + ":" + rec.data["functext"].replace(/\n/g, "") + ",";
            }
            if(conf != "{")
                conf = conf.substring(0, (conf.length - 1)) + "}";
        }
        return conf;
    }
});

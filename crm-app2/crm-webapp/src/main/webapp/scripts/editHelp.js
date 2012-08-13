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
Wtf.editHelp=function(config) {
    Wtf.editHelp.superclass.constructor.call(this,config);
};

Wtf.extend(Wtf.editHelp, Wtf.Window, {

    initComponent : function(config) {
        this.winHeight = 584;
        this.recID=null;
        this.topTitle = "Edit ToolTips";
        this.opt = "Choose module you want to edit tooltip/first-run help.";
        this.title="Edit ToolTips";
        this.iconCls='pwnd favwinIcon';
        this.height= this.winHeight;
        this.width= 610;
        this.modal=true;
        this.layout="table";
        this.layoutConfig= {
            columns: 1
        };
        this.comboReader =  new Wtf.data.Record.create([
            {
                name: 'id',
                type: 'string'
            },
            {
                name: 'name',
                type: 'string'
            }
        ]);
        var moduleStore= new Wtf.data.Store({
            url: "Common/FirstRunHelp/getModule.do",
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, this.comboReader),
            autoLoad:false
        });
        
        this.moduleCombo=new Wtf.form.ComboBox({
            fieldLabel: 'Module',
            id:'module1',
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: moduleStore,
            displayField: 'name',
            typeAhead: true,
            allowBlank:false,
            valueField:'id',
            anchor:'100%'
        });
        
        moduleStore.load();

       this.moduleCombo.on('select',function(combo,rec,indx){
            this.compStore.load({
                params:{
                    mod:combo.getValue()
                    }
                });
            this.compCombo.enable();
            Wtf.getCmp('savebttn').enable();
        },this);
        
        this.compStore= new Wtf.data.Store({
                url: "Common/FirstRunHelp/getComponents.do",
                reader: new Wtf.data.KwlJsonReader({
                    root:'data'
                }, this.comboReader),
                autoLoad:false
        });
        
        this.compCombo=new Wtf.form.ComboBox({
            fieldLabel: 'Component',
            id:'comp2',
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: this.compStore,
            displayField: 'name',
            typeAhead: true,
            allowBlank:false,
            valueField:'id',
            anchor:'100%'
        });
        
        this.compCombo.disable();

        this.compCombo.on('select',function(combo,rec,indx){
                Wtf.Ajax.requestEx({
                    url: "Common/FirstRunHelp/getCompDetails.do",
                    params:{
                        name:combo.getValue()
                    }
                },this,
                function(res) {
                    this.titleEditor.setValue(res.data[0].title);
                    this.descEditor.setValue(res.data[0].desc);
                    this.recID=res.data[0].id;
                },
                function(res) {
                    ResponseAlert(301);
                });
        },this);
        
        this.titleEditor = new Wtf.form.HtmlEditor({
            border: false,
            enableLists: false,
            enableSourceEdit: true,
            enableAlignments: true,
            fieldLabel:'Title',
            height:20,
            width:470
        });
        this.descEditor = new Wtf.form.HtmlEditor({
            border: false,
            enableLists: false,
            enableSourceEdit: true,
            enableAlignments: true,
            fieldLabel:'Description',
            height:20,
            width:470
        });

        this.resizable=false;
        this.items= [{
            width :601,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html : getTopHtml(this.topTitle ,this.opt,'../../images/help.gif')
        },{
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:5px 5px 0px 5px;',
            layout: 'form',
            width : 601,
            height: 440,
            items: [this.moduleCombo,this.compCombo,this.titleEditor,this.descEditor]
        }];
        this.buttons= [{
            text:"Save",
            id:'savebttn',
            tooltip:{text:'Click to save the changes made by you.'},
            scope:this,
            disabled:true,
            handler:function() {
                 Wtf.Ajax.requestEx({
                    url: "Common/FirstRunHelp/saveCompDetails.do",
                    params:{
                        title:this.titleEditor.getValue(),
                        desc:this.descEditor.getValue(),
                        id:this.recID
                    }
                },this,
                function(res) {
                    this.titleEditor.setValue("");
                    this.descEditor.setValue("");
                    ResponseAlert(300);
                },
                function(res) {
                    ResponseAlert(301);
                });
            }
        },{
            text: "Cancel",
            scope:this,
            handler: function(){
                this.close();
            }
        }];
    Wtf.editHelp.superclass.initComponent.call(this,config);
    }
    
});




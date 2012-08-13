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
Wtf.customReport=function(config){
    this.autoScroll=true;
    this.border=false;
    this.width='99%';
    this.bodyStyle = 'background:white;';
    Wtf.apply(this,config);
    Wtf.form.Field.prototype.msgTarget = "under";
    var defConf = {
        ctCls: 'reportfieldContainer',
        labelStyle: 'font-size:11px; text-align:right;'
    };
    this.attachheight=130;
    this.hfheight=150;
    this.count=1;
    this.hfieldset=new Wtf.Panel({
        columnWidth: 0.59,
        border: false,
        height : this.attachheight,
        items:[{
            xtype:'fieldset',
            title: 'Header Fields',
            cls: "customFieldSet",
            defaults : defConf,
            autoHeight : true,
            items:[
                this.headernote=new Wtf.form.TextField({
                    fieldLabel:'Header Note',
                    labelSeparator:'',
                    maxLength:40,
                    validator:WtfGlobal.validateHTField,
                    maxLengthText:'You cannot enter more than 40 characters',
                    emptyText:'Insert Note'
                }),
                this.reporttitle = new Wtf.form.TextField({
                    fieldLabel:'Report Title',
                    labelSeparator:'',
                    maxLength:40,
                    validator:WtfGlobal.validateHTField,
                    maxLengthText:'You cannot enter more than 40 characters',
                    emptyText:'Insert Title'
                })]
            }
        ]
    });
    if(this.editTemplateConfig!=undefined) {
        var configure = eval('('+ this.editTemplateConfig +')');
        var bgvalue="#"+configure['bgColor'];
        var tvalue="#"+configure['textColor'];
    } else {
       bgvalue="#FFFFFF";
       tvalue="#000000";
    }
    this.bclrPicker=new Wtf.Panel({
        border:false,
        html:' <div id = "bimg_div'+this.id+'" style="cursor:pointer; height:12px; width:12px; margin:auto; padding:auto; border:thin solid; border-color:'+tvalue
                +'; background-color:'+bgvalue+';" onclick=\"showPaletteBg(\''+this.id+'\')\"></div>'
    });

    this.tclrPicker=new Wtf.Panel({
        border:false,
        html:'<div id = "timg_div'+this.id+'" style="cursor:pointer; height:12px; width:12px; margin:auto; padding:auto; border:thin solid; border-color:'+tvalue
                +'; background-color:'+tvalue+';" onclick=\"showPaletteTxt(\''+this.id+'\')\"></div>'
    });
    this.tcc=tvalue;
    this.bcc=bgvalue;

    this.fpager= new Wtf.form.Checkbox({
                    name:'pager',
                    boxLabel:'Paging',
                    labelSeparator:'',
                    listeners:{check:this.checkfPager, scope:this}
    });
    this.hpager= new Wtf.form.Checkbox({
                    name:'pager',
                    boxLabel:'Paging',
                    labelSeparator:'',
                    listeners:{check:this.checkhPager, scope:this}
    });
    this.hdater= new Wtf.form.Checkbox({
                    name:'dater',
                    boxLabel:'Date',
                    labelSeparator:'',
                    listeners:{check:this.checkhDater, scope:this}
    });
    this.fdater=new Wtf.form.Checkbox({
                    name:'dater',
                    boxLabel:'Date',
                    labelSeparator:'',
                    listeners:{check:this.checkfDater, scope:this}
    });

    
    this.customForm=new Wtf.FormPanel({
        fileUpload: true,
        autoScroll: true,
        border: false,
        width:'100%',
        frame:false,
        method :'POST',
        scope: this,
        labelWidth: 40,
        items:[{
            border: false,
            html: '<center><div style="padding-top:10px;color:#154288;font-weight:bold"> Customize report by selecting your preferences.</div><hr style = "width:95%;"></center>'
        },{
            layout:'column',
            border: false,
            items:[this.hfieldset,{
                columnWidth: 0.20,
                border: false,
                bodyStyle : 'margin-left:50%;margin-top:15%;',
                items:[this.hdater]
                },{
                columnWidth: 0.19,
                border: false,
                bodyStyle : 'margin-left:15%;margin-top:15%;',
                items:[this.hpager]
            }]
        },{ 
            border: false,
            html: '<center><hr style = "width:95%;"></center>'
        },{
            layout: 'column',
            border: false,
            items:[{
                columnWidth: 0.49,
                border: false,
                items:[{
                    xtype:'fieldset',
                    title: 'Page Border',
                    cls: "customFieldSet",
                    defaults : defConf,
                    autoHeight : true,
                    items:[
                        this.pborder = new Wtf.form.Radio({
                        id:'pbordertrue'+this.id,
                        name:'pborder',
                        inputValue :'true',
                        boxLabel:'With Border',
                        labelSeparator:'',
                        checked:true
                        }),
                    
                        this.pnoborder = new Wtf.form.Radio({
                        name:'pborder',
                        inputValue :'false',
                        labelSeparator:'',
                        boxLabel:'No Border'
                        })
                    ]
                },{
                    xtype:'fieldset',
                    title: 'Data and Grid Border',
                    cls: "customFieldSet",
                    defaults : defConf,
                    autoHeight : true,
                    items:[
                        this.dborder = new Wtf.form.Radio({
                        id:'gridbordertrue'+this.id,
                        name:'dborder',
                        inputValue :'true',
                        boxLabel:'With Border',
                        labelSeparator:'',
                        checked:true
                    }),
                        this.dnoborder = new Wtf.form.Radio({
                        name:'dborder',
                        inputValue :'false',
                        labelSeparator:'',
                        boxLabel:'No Border'
                    })]
                },
                {
                    xtype:'fieldset',
                    title: 'Select Background Color',
                    cls: "customFieldSet",
                    id: this.id + 'bcolorPicker',
                    autoHeight : true,
                    items:[this.bclrPicker]
                }]
            },{
                columnWidth: 0.49,
                border: false,
                items:[{
                    xtype:'fieldset',
                    title: 'Page View',
                    cls: "customFieldSet",
                    defaults : defConf,
                    autoHeight : true,
                    items:[
                        this.potrait = new Wtf.form.Radio({
                        name:'pview',
                        inputValue :'false',
                        boxLabel:'Potrait',
                        labelSeparator:''
                    }),
                        this.landscape = new Wtf.form.Radio({
                        name:'pview',
                        id:'pageviewtrue'+this.id,
                        inputValue :'true',
                        labelSeparator:'',
                        boxLabel:'Landscape',
                        checked:true
                    })]
                },{
                    xtype:'fieldset',
                    title: 'Company Logo',
                    cls: "customFieldSet",
                    defaults : defConf,
                    autoHeight : true,
                    items:[
                        this.hidelogo = new Wtf.form.Radio({
                        name:'complogo',
                        inputValue :'false',
                        boxLabel:'Hide Logo',
                        labelSeparator:'',
                        checked:true
                    }),
                        this.showlogo = new Wtf.form.Radio({
                        name:'complogo',
                        id:'companylogo'+this.id,
                        inputValue :'true',
                        labelSeparator:'',
                        boxLabel:'Show Logo'
                    })]
                },
                {
                    xtype:'fieldset',
                    title: 'Select Text Color',
                    cls: "customFieldSet",
                    id:this.id+'tcolorPicker',
                    autoHeight : true,
                    items:[this.tclrPicker]
                }              
                ]
            }]
        },
        {
            border: false,
            html: '<center><hr style = "width:95%;"></center>'
        },{
            layout:'column',
            border: false,
            items:[{
                columnWidth: 0.59,
                border: false,
                items:[{
                    xtype:'fieldset',
                    title: 'Footer Fields',
                    cls: "customFieldSet",
                    defaults : defConf,
                    autoHeight : true,
                    items:[
                        this.footernote = new Wtf.form.TextField({
                            id:'footernote'+this.id,
                            fieldLabel:'Footer Note',
                            maxLength:40,
                            validator:WtfGlobal.validateHTField,
                            maxLengthText:'You cannot enter more than 40 characters',
                            labelSeparator:'',
                            emptyText:'Insert Note'
                        })]
                }]
            },{
                columnWidth: 0.20,
                border: false,
                bodyStyle : 'margin-left:55%;margin-top:15%;',
                items:[this.fdater]
            },{
                columnWidth: 0.20,
                border: false,
                bodyStyle : 'margin-left:15%;margin-top:15%;',
                items:[this.fpager]
            }]
        },
        {
            border: false,
            html: '<center><hr style = "width:95%;"></center>'
        }],
        buttons:[
        {
            xtype:'button',
            text:'<b>View Template List<b>',
            cls:'exportpdfbut',
            scope:this,
            handler:function(){
                this.ownerCt.ownerCt.ownerCt.remove(this.ownerCt.ownerCt); //remove current tab [Report Layout Builder]
                if(mainPanel.activeTab.mainTab != undefined) {
                    if(typeof mainPanel.activeTab.mainTab.activeTab.exportfile=="undefined") {
                        mainPanel.activeTab.exportfile("pdf"); // maintabs export
                    } else {
                        mainPanel.activeTab.mainTab.activeTab.exportfile("pdf"); // subtabs export
                    }
                } else {
                    mainPanel.activeTab.items.items[0].exportfile("pdf"); // for reports export
                }
            }
        }, 
        {
            xtype:'button',
            text:'<b>Export Report<b>',
            cls:'exportpdfbut',
            hidden:this.editTemplateConfig!=undefined?false:true,
            scope:this,
            handler:function(){
                if (this.customForm.getForm().isValid()) {
                    this.exportPdf();
                    this.ownerCt.ownerCt.ownerCt.remove(this.ownerCt.ownerCt);
                } else
                    WtfComMsgBox(788,0);
            }
        },
        {
            xtype:'button',
            text:'<b>Save Template<b>',
            cls:'exportpdfbut',
            scope:this,
            handler:function(){
                if (this.customForm.getForm().isValid())
                    if(this.editTemplateConfig==undefined) {
                        this.saveTemplate();
                    } else {
                        this.overwriteTemplate(0);
                    }
                else
                    WtfComMsgBox(788,0);
            }
        },
        {
            xtype:'button',
            text:'<b>Save Template and Export Report</b>',
            cls:'exportpdfbut',
            scope:this,
            hidden:this.editTemplateConfig!=undefined?false:true,
            handler:function(){
                if (this.customForm.getForm().isValid())
                    if(this.editTemplateConfig==undefined) {
                        this.saveTemplate();
                    } else {
                        this.overwriteTemplate(1);
                        this.exportPdf();
                        this.ownerCt.ownerCt.ownerCt.remove(this.ownerCt.ownerCt);
                    }
                 else
                    WtfComMsgBox(788,0);
            }
        }]
    });

    if(this.editTemplateConfig!=undefined)
            this.editForm(this.editTemplateConfig);
    Wtf.customReport.superclass.constructor.call(this,config);
}  
Wtf.extend(Wtf.customReport,Wtf.Panel,{ 
    onRender: function(conf){
        Wtf.customReport.superclass.onRender.call(this, conf);
        if(this.reportType==2)
            Wtf.getCmp(this.id + 'adjustColWidth').hide();
        this.add(this.customForm);
    },
    editForm: function(configstr) {
        var config = eval('('+configstr +')');
        this.headernote.setValue(config['headNote']);
        this.reporttitle.setValue(config['title']);
        this.footernote.setValue(config['footNote']);
        this.fpager.setValue(config['footPager']);
        this.hpager.setValue(config['headPager']);
        this.hdater.setValue(config['headDate']);
        this.fdater.setValue(config['footDate']);
        if(config['pageBorder']=="true")
            this.pborder.setValue(true);
        else
            this.pnoborder.setValue(true);
        if(config['gridBorder']=="true")
            this.dborder.setValue(true);
        else
            this.dnoborder.setValue(true);
        if(config['landscape']=="true")
            this.landscape.setValue(true);
        else {
            this.landscape.setValue(false);
            this.potrait.setValue(true);
        }
        if(config['showLogo']=="true")
            this.showlogo.setValue(true);
        else
            this.hidelogo.setValue(true);
    },
    removesubtitle:function(){
        this.attachheight -=25;
        this.hfieldset.setHeight(this.attachheight);
        if(this.count>5)
            document.getElementById('subtitlelink'+this.id).style.display='block';
        this.count--;
        if(this.count==1)
            document.getElementById('subtitlelink'+this.id).innerHTML = "Add Subtitle";
        this.doLayout();
    },
    checkhDater:function(cbox,checked){
        if(checked)
        this.fdater.reset();
    },
    checkfDater:function(cbox,checked){
        if(checked)
        this.hdater.reset();
    },
    checkhPager:function(cbox,checked){
        if(checked)
        this.fpager.reset();
    },
    checkfPager:function(cbox,checked){
        if(checked)
        this.hpager.reset();
    },
    saveTemplate:function(){
    if(this.editTemplateConfig==undefined) {
        var nameField = new Wtf.form.TextField({
            fieldLabel:'Name',
            id:'repTemplateName',
            validator: WtfGlobal.validateUserName,
            allowBlank: false,
            width:255
        });
        var descField = new Wtf.form.TextArea({
            id:'repDescField',
            height: 187,
            hideLabel:true,
            cls:'descArea',
            fieldClass : 'descLabel',
            width:356
        });
        
        var Template = new Wtf.Window({
                title: 'New Report Template',
                width: 390,
                layout: 'border',
                iconCls : 'iconwin',
                modal: true,
                height: 330,
                frame: true,
                border:false,
                items:[{
                    region: 'north',
                    height: 45,
                    width: '95%',
                    id:'northRegion',
                    border:false,
                    items:[{
                        layout:'form',
                        border:false,
                        labelWidth:100,
                        frame:true,
                        items:[nameField]
                    }]
                },{
                    region: 'center',
                    width: '95%',
                    height:'100%',
                    id: 'centerRegion',
                    layout:'fit',
                    border:false,
                    items:[{
                        xtype:'fieldset',
                        title:"Description",
                        cls: 'textAreaDiv',
                        labelWidth:0,
                        frame:false,
                        border:false,
                        items:[descField]
                    }]
                }],
                buttons:[{
                    text:'Save',
                    handler: function() {
                        if(!nameField.isValid()) 
                          WtfComMsgBox(['New Template','Please fill all the essential fields.']);
                         else {
                            this.saveReportTemplate(Template,nameField,descField);
                            this.ownerCt.ownerCt.ownerCt.remove(this.ownerCt.ownerCt);
                            if(mainPanel.activeTab.mainTab != undefined) {
                                if(typeof mainPanel.activeTab.mainTab.activeTab.exportfile=="undefined") {
                                    mainPanel.activeTab.exportfile("pdf"); // maintabs export
                                } else {
                                    mainPanel.activeTab.mainTab.activeTab.exportfile("pdf"); // subtabs export
                                }
                            } else {
                                mainPanel.activeTab.items.items[0].exportfile("pdf"); // for reports export
                            }
                        }
                    },
                    scope: this
                },{
                    text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                    handler:function() {
                        Template.close();
                    }
                }]
            });
            Template.show();
        }
    },
    overwriteTemplate: function(type) {
        Wtf.Ajax.requestEx({
            url: "Common/ExportPdfTemplate/editReportTemplate.do",
            params: {
                action: 3,
                data: this.generateData(),
                userid:loginid,
                edit:this.overwriteflag
            },
            method:'POST'
        },
        this,
        function(res) {
            if(res.success) {
                if(type==0)
                    ResponseAlert(2);
                else
                    ResponseAlert(250);
            }
        },
        function() {
            WtfComMsgBox(787,1);
        });
    }, 
    saveReportTemplate:function(win, nameField, descField){
        var tname = WtfGlobal.HTMLStripper(nameField.getValue());
        var description = WtfGlobal.HTMLStripper(descField.getValue());
        if(tname == null && tname == "") {
            WtfComMsgBox(789,1);
            return;
        }
        Wtf.Ajax.requestEx({
            url: "Common/ExportPdfTemplate/saveReportTemplate.do",
            params: {
                name: tname,
                data: this.generateData(),
                desc: description,
                userid:loginid
            },
            method:'POST'
        },
        this,
        function(res) {
            if(res.success)
                ResponseAlert(1);
        },
        function() {
            WtfComMsgBox(791,1);
        });
        win.close();
    },  
    exportPdf:function() {
        var data=this.generateData();
        var url = this.url+"?config="+data+"&reportid="+this.reportid+"&name="+this.name+"&filetype=pdf&gridconfig="
                    +encodeURIComponent(this.gridconfig)+"&mapid="+this.mapid+"&year="+this.year+"&flag="+this.flag;
		if(this.selectExport != undefined) {
            url += "&selectExport="+this.selectExport;
        }
        if(this.comboName != undefined && this.comboValue != undefined) {
            url += "&comboName="+this.comboName+"&filterCombo="+this.comboValue+"&comboDisplayValue="+this.comboDisplayValue;
        }
        if(this.field != undefined) {
            url += "&field="+this.field+"&direction="+this.dir;
        }
		if(this.searchJson=="" && (this.frm=="" || this.to =="")) {
            url += "&isarchive=false&isconverted=0&transfered=0";
        } else if(this.searchJson!="") {
            url += "&searchJson="+encodeURIComponent(this.searchJson)+"&isarchive=false&isconverted=0&transfered=0";
        } else if(this.frm!="" && this.to !="") {
            url += "&frm=" +this.frm+"&to="+this.to+"&cd="+this.cd+"";
        }
        Wtf.get('downloadframe').dom.src = url;
    },   
    generateData:function(){
         var subtitles="";
         var tboxes=this.hfieldset.findByType('textfield');
         var headNote=WtfGlobal.ScriptStripper(WtfGlobal.HTMLStripper(tboxes[0].getValue()));
         var title=WtfGlobal.ScriptStripper(WtfGlobal.HTMLStripper(tboxes[1].getValue()));
         var sep="";
         for(i=2; i<tboxes.length; i++){
            subtitles += sep + WtfGlobal.ScriptStripper(WtfGlobal.HTMLStripper(tboxes[i].getValue()));
            sep="~";
         }
         var headDate=this.hdater.getValue();
         var headPager=this.hpager.getValue();
         var footDate=this.fdater.getValue();
         var footPager=this.fpager.getValue();
         var footNote=WtfGlobal.ScriptStripper(WtfGlobal.HTMLStripper(Wtf.getCmp('footernote'+this.id).getValue()));

         var pb=Wtf.getCmp('pbordertrue'+this.id). getGroupValue();
         var gb=(Wtf.getCmp('gridbordertrue'+this.id). getGroupValue());
         var pv=(Wtf.getCmp('pageviewtrue'+this.id). getGroupValue());
         var cl=(Wtf.getCmp('companylogo'+this.id). getGroupValue());
         var tColor = this.tcc.substring(1);
         var bColor = this.bcc.substring(1);
         var data = '{"landscape":"'+pv+'","pageBorder":"'+pb+'","gridBorder":"'+gb+'","title":"'+title +'","subtitles":"'+subtitles +'","headNote":"'+headNote+'","showLogo":"'+cl +'","headDate":"'+headDate+'","footDate":"'+footDate+'","footPager":"'+footPager+'","headPager":"'+headPager+'","footNote":"'+footNote+'","textColor":"'+tColor+'","bgColor":"'+bColor+'"}';
         return data;
    },

    showColorPanelBg: function(obj) {
        var colorPicker = new Wtf.menu.ColorItem({
            id: 'coloritem'
        });

        var contextMenu = new Wtf.menu.Menu({
            id: 'contextMenu',
            items: [ colorPicker ]
        });
        contextMenu.showAt(Wtf.get(this.id + 'bcolorPicker').getXY());
        colorPicker.on('select', function(palette, selColor){
                this.bcc= '#' + selColor;
                Wtf.get("bimg_div"+this.id).dom.style.backgroundColor = this.bcc;
        },this);
    },
    showColorPanelTxt: function(obj) {
        var colorPicker = new Wtf.menu.ColorItem({
            id: 'coloritem'
        });
        var contextMenu = new Wtf.menu.Menu({
            id: 'contextMenu',
            items: [ colorPicker ]
        });
        contextMenu.showAt(Wtf.get(this.id + 'tcolorPicker').getXY());
        colorPicker.on('select', function(palette, selColor){
                this.tcc= '#' + selColor;
                Wtf.get("timg_div"+this.id).dom.style.backgroundColor = this.tcc;
        },this);
    }
});

function Addsubtitle(objid){
    Wtf.getCmp(objid).Addsubtitle();
}

function removesubtitle(objid,thisid){
    Wtf.getCmp(objid).ownerCt.remove(Wtf.getCmp(objid),true);
    Wtf.getCmp(thisid).removesubtitle();
}
function showPaletteBg(cid){
        Wtf.getCmp(cid).showColorPanelBg(Wtf.get("bimg_div"+cid));
}
function showPaletteTxt(cid){
        Wtf.getCmp(cid).showColorPanelTxt(Wtf.get("timg_div"+cid));
}

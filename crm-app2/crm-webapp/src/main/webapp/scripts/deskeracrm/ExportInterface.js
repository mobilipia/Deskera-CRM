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
Wtf.ExportInterface=function(config) {
    Wtf.ExportInterface.superclass.constructor.call(this,config);
};

Wtf.extend(Wtf.ExportInterface, Wtf.Window, {

    initComponent : function(config) {
        this.winHeight = 424;
        if(Wtf.isIE6)
            this.winHeight = 413;
        this.topTitle = WtfGlobal.getLocaleText({key:"crm.exportinterface.title",params:[this.type]});//"Export "+this.type+" file ";
        this.opt = WtfGlobal.getLocaleText({key:"crm.exportinterface.details",params:[this.type]});//"You can Choose columns and adjust width to export to a "+this.type+" file.";
        if(this.type=="print"){
            this.topTitle = WtfGlobal.getLocaleText("crm.exportinterface.printfile");//"Print file ";
            this.opt = WtfGlobal.getLocaleText("crm.exportinterface.printfile");//"Print file.";
        }
        
        this.colSM = new Wtf.grid.CheckboxSelectionModel({
            width: 25
        });
        this.colCM = new Wtf.grid.ColumnModel([ this.colSM,{
            header: WtfGlobal.getLocaleText("crm.customreport.header.column"),//"Column",
            dataIndex: "title"
        },{
            header: "title",
            dataIndex: "header",
            hidden:true
        },{
            header: "index",
            dataIndex: "index",
            hidden:true
        },{
            header: "align",
            dataIndex: "align",
            hidden:true
        },{
            header: "xtype",
            dataIndex: "xtype",
            hidden:true
        },{
            header: WtfGlobal.getLocaleText("crm.exportinterface.width"),//"Width",
            hidden:((this.type=="csv")?true:false),
            dataIndex: 'width',
            editor: new Wtf.form.NumberField({
                allowBlank: false,
                minValue: 30
            })
        }]);
        this.headerField = new Wtf.form.TextField({
            labelSeparator:'',
            width: 180,
            emptyText: mainPanel.getActiveTab().title 
        });
        this.colG = new Wtf.grid.EditorGridPanel({
            store: this.pdfDs,
            border: false,
            layout: "fit",
            width : 328,
            height:270,
            viewConfig: {
                forceFit: true
            },
            cm: this.colCM,
            autoScroll: true,
            clicksToEdit: 1,
            sm: this.colSM
        });
        var a1,a2,a3,a4=undefined,a5,flag=true,flag2=0;
        this.colSM.on('beforerowselect',function(obj,ri,keepExisting,record){
            if(obj.getSelections().length==1){
                a4= obj.getSelections(); //1
            }else{
                a4= undefined; //1
            }
            if(obj.getSelections().length==0)
            {
                flag=false;
            }

        });
        this.colSM.on('rowselect',function(obj,ri,keepExisting,record){
            a1= obj.getSelections(); //1
            if(a1.length > 1){
                a2=a1;//2
                flag=true;
            }
        });        
        this.colG.on('beforeedit',function(obj){
            flag2=1;
                 if(a4==undefined && flag==true){
                    this.colSM.selectRecords(a2);
                 }
                     else{
                    if(flag==true)
                        this.colSM.selectRecords(a4);
                 else{
                    }
                 }
        },this);        
        this.colG.on("render", function(obj){
            obj.getSelectionModel().selectAll();
        }, this);
        this.title=WtfGlobal.getLocaleText("crm.editor.exportBTN");//"Export";
        if(this.type=="print"){
            this.title=WtfGlobal.getLocaleText("crm.editor.printBTN");//"Print";
        }
        this.iconCls='pwnd favwinIcon';
        this.height= this.winHeight;
        this.width= 350;
        this.modal=true;
        this.layout="table";
        this.layoutConfig= {
            columns: 1
        };
        this.resizable=false;
        var img ="pdf60.png"
        if(this.type=="csv"){
            img = "csv-icon.jpg";
        } else if(this.type=="xls"){
            img = "excel-icon.jpg";
        } else if(this.type=="print"){
            img = "TXT52.png";
        }
        this.items= [{
            height: 75,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html : getTopHtml(this.topTitle ,this.opt,'../../images/'+img)
        },{
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:5px 5px 0px 5px;',
            layout: 'fit',
            width : 338,
            items: [this.colG]
        }];
        this.buttons= [{
            text:"<< "+WtfGlobal.getLocaleText("crm.common.previousbtn"),//"<< Previous",
            tooltip:{text:WtfGlobal.getLocaleText("crm.exportinterface.previousbtnttip")},//'Move back to report template.'},
            scope:this,
            hidden:((this.type=="pdf")?false:true),
            handler:function() {
                this.hide();
                this.parent.show();
            }
        },{
            text: ((this.type=="print")?WtfGlobal.getLocaleText("crm.editor.printBTN"):WtfGlobal.getLocaleText("crm.editor.exportBTN")),
            tooltip:((this.type=="print")?WtfGlobal.getLocaleText("crm.editor.printBTN"):{text:'Export as pdf/csv/xls file.'}),
            scope: this,
            handler: function() {
            var selCol = this.colSM.getSelections();
            if(selCol.length > 0){
                var header = [];
                var title = [];
                var xlsheader = [];
                var width = [];
                var xtype = [];
                var indx = [];
                var align=[];
                var k = 0;
                var flag=0;

                for(var i = 0; i < selCol.length; i++) {
                    var recData = selCol[i].data;
                    header.push(recData.header);
                    xlsheader.push(recData.xlsheader);
                    var recTitle =recData.title;
                    if(recTitle.indexOf('(')!=-1 && recTitle.substring(recTitle.indexOf("(")+1,recTitle.indexOf("(")+2)!=="%") {
                        recData.title=recData.title.substring(0,recData.title.indexOf('(')-1);
                    }
                    if(recData.title.indexOf('*')!=-1) {
                        recData.title=recData.title.substring(0,recData.title.length-1);
                        title.push(recData.title);
                    } else
                        title.push(encodeURI(recTitle));
                    width.push(recData.width);
                    if(recData.xtype==''){
                    xtype.push('none');
                    }else
                    	xtype.push(recData.xtype);
                    indx.push(recData.index);
                    if(recData.align=='')
                        align.push('none');
                    else 
                        align.push(recData.align);
                    

                }
                k = indx.length;
                for(i = 0; i < k; i++) {   //sort based on index
                    for(var j = i+1; j < k; j++) {
                        if(indx[i] > indx[j]) {
                            var temp = header[i];
                            header[i] = header[j];
                            header[j] = temp;

                            temp = xlsheader[i];
                            xlsheader[i] = xlsheader[j];
                            xlsheader[j] = temp;

                            temp = title[i];
                            title[i] = title[j];
                            title[j] = temp;

                            temp = width[i];
                            width[i] = width[j];
                            width[j] = temp;

                            temp = align[i];
                            align[i] = align[j];
                            align[j] = temp;
                            
                            temp = xtype[i];
                            xtype[i] = xtype[j];
                            xtype[j] = temp;
                        }
                    }
                }
                if(this.type == "pdf") {
                    var sum = 0;
                    var selLength = selCol.length;
                    var pdfWidth=800; //landscape
                    if(this.pageLayout!="true")
                        pdfWidth=570; //potrait
                    var max = Math.floor(pdfWidth/selLength);
                    max=Math.floor(max/10)*10;
                    for(i=0;i<selLength;i++)
                    {
                    	sum+=width[i];
                    }
                    if(sum > pdfWidth){
                     flag=1;
                     WtfComMsgBox([WtfGlobal.getLocaleText("crm.exportinterface.exportreporttitle"),WtfGlobal.getLocaleText({key:"crm.exportinterface.maxwidthmsg",params:[max]})]);//"For the selected columns maximum width for fields is "+max]);
                    }
                    else {
                    for(i = 0; i < selLength; i++)
                       width[i]= (width[i]*pdfWidth)/sum;
                    }
                }
                if(flag == 0) {
                    this.close();
                    var url=this.url+"?config="+this.configstr+"&reportid="+this.name+"&name="+this.name+"&filetype=" +this.type
                                +"&header="+header+"&title="+encodeURIComponent(title)+"&width="+width+"&xtype="+xtype+"&align="+align+"&mapid="+this.mapid+"&year="+this.year+"&xlsheader="+xlsheader+"&flag="+this.flag+"&listID="+this.TLID+"&titlename="+(this.titlename||"");
					if(this.selectExport != undefined) {
                        url += "&selectExport="+this.selectExport;
                    }
                    if(this.extraConfig != undefined) {
                        url += "&extraconfig="+encodeURIComponent(this.extraConfig);
                    }
                    if(this.comboName != undefined && this.comboValue != undefined) {
                        url += "&comboName="+this.comboName+"&filterCombo="+this.comboValue+"&comboDisplayValue="+this.comboDisplayValue;
                    }
                    if(this.goalid!="") {
                        url += "&goalid="+this.goalid;
                    }
                    if(this.heading!="" && this.heading!=undefined) {
                        url += "&heading="+this.heading;
                    }
                    if(this.emailmarketid!="") {
                        url += "&emailmarketid="+this.emailmarketid;
                    }
                    if(this.bouncereportcombo != undefined) {
                        url += "&bouncereportcombo="+this.bouncereportcombo;
                    }
                    if(this.field != undefined) {
                        url += "&field="+this.field+"&direction="+this.dir;
                    }
                    if(this.userCombo != undefined) {
                        url += "&userCombo="+this.userCombo;
                    }
                    if(this.ss!=undefined && this.ss!="") {
                        url += "&ss="+this.ss;
                    }
                    if(this.json=="" && (this.fromdate=="" || this.todate =="")) {
                        url += "&isarchive=false&isconverted=0&transfered=0";
                    } else if(this.json!="") {
                        url += "&searchJson="+this.json+"&isarchive=false&isconverted=0&transfered=0";
                    } else if(this.fromdate!="" && this.todate !="") {
                        url += "&frm=" +this.fromdate+"&to="+this.todate+"&cd="+this.cd+"";
                    }
                    if(this.type == "print") {
                        window.open(url, "mywindow","menubar=1,resizable=1,scrollbars=1");
                    } else
                        Wtf.get('downloadframe').dom.src  = url;
                }
               } else {
                WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.exportinterface.selcolmsg")],1);// "Select at-least one column to display."], 1);
            }
         }
    },{ 
        text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//"Cancel",
        scope:this,
        handler: function(){
            this.close();
        }
    }];
    Wtf.ExportInterface.superclass.initComponent.call(this,config);
    }
 }); 
        
 

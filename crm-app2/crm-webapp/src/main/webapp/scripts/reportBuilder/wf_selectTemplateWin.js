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
Wtf.selectTempWin=function(config){
    Wtf.apply(this,config);
    back =this;
    var templateRec = Wtf.data.Record.create([
        {
            name: 'tempid',
            mapping:'tempid'
        },{
            name: 'tempname',
            mapping:'tempname'
        },{
            name: 'description',
            mapping:'description'
        },{
            name: 'configstr',
            mapping:'configstr'
        }
    ]);

    var template_ds = new Wtf.data.Store({
        url:  "Common/ExportPdfTemplate/getAllReportTemplate.do",
        method: 'GET',
        reader: new Wtf.data.KwlJsonReader({
            root: 'data'
        },templateRec)
    });

    var namePanel = new Wtf.grid.GridPanel({
        id:'templateName',
        autoScroll: true,
        enableColumnResize:false,
        border:false,
        viewConfig:{
            forceFit:true
        },
        cm: new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer, {
                header:'Name',
                dataIndex: 'tempname'
            }]),
        ds: template_ds,
        height:180
    });

    namePanel.on('cellclick',function(gridObj, ri, ci, e){
        var config = gridObj.getStore().getAt(ri).data['configstr'];
        this.templateid = gridObj.getStore().getAt(ri).data['tempid'];

        var configstr = eval('('+config+')');
        var title = configstr["title"];
        var subtitle =configstr["subtitles"];
        var starr = subtitle.split("~");
        var subtitles = "";
        for(var i=0;i< starr.length;i++)
            subtitles += "<div>"+starr[i]+"</div>";

        var textColor = "#"+configstr["textColor"];
        var bgColor ="#"+configstr["bgColor"];

        var headdate = configstr["headDate"]=="true"?"<small>2009/01/01</small>":"";
        var footdate = configstr["footDate"]=="true"?"<small>2009/01/01</small>":"";

        var headnote = configstr["headNote"];
        var footnote = configstr["footNote"];

        var headpager = configstr["headPager"]=="true"?"1":"";
        var footpager = configstr["footPager"]=="true"?"1":"";

        var pageborder = configstr["pageBorder"]=="true"?"border:thin solid #666;":"";
        var gridborder = configstr["gridBorder"]=="true"?"1":"0";
        var displaylogo = configstr["showLogo"]=="true"?"block":"none";

        var pagelayoutPR = "height:380px;width:270px;margin:auto;";
        var pagelayoutLS = "height:270px;width:380px;margin:57px auto;";
        var pagelayout = configstr["landscape"]=="true"?pagelayoutLS:pagelayoutPR;

        this.layout = configstr["landscape"];
        var reportPreview = "<div style=\""+pagelayout+"align:center;color:"+textColor+";font-family:arial;padding:5px;font-size:12px;background:"+bgColor+";border-right:4px solid #DDD;border-bottom:4px solid #888\">" +
        "<div style=\""+pageborder+"height:99%;width:99%;\">" +
        "<div style=\"border-bottom:thin solid #666;margin:0 2px;height:6%;width:98%;\">" +
        "<table border=0 width=100% style=\"font-size:12px\">" +
        "<tr><td align=\"left\" width=25%>"+headdate+"</td><td align=\"center\" >"+headnote+"</td><td align=\"right\" width=25%>"+headpager+"</td></tr>" +
        "</table>" +
        "</div>" +
        "<div style=\"margin:0 2px;height:86%;width:98%;text-align:center;overflow:hidden;\">" +
        "<div style=\"border-bottom:thin solid #666;\">" +
        "<div style=\"display:"+displaylogo+";position:absolute;font-size:16px;margin:1px 0 0 1px\"><b>Deskera</b></div>" +
        "<div style=\"display:"+displaylogo+";position:absolute;color:#8080FF;font-size:16px\"><b>Deskera</b><sup><small><small><small>TM</small></small></small></sup></div>" +
        "<br/><div style=\"font-size:13px\"><b>"+title+"</b></div>" +
        subtitles + "<br/>"+
        "</div>" +
        "<table border="+gridborder+" width=90% cellspacing=0 style=\"font-size:12px;margin:5px auto;\">" +
        "<tr><td align=\"center\" width=10%><b>No.</b></td><td align=\"center\" width=20%><b>Index</b></td><td align=\"center\" width=45%><b>Task Name</b></td><td align=\"right\" width=25%><b>Resources</b></td></tr>" +
        "<tr><td align=\"center\">1.</td><td align=\"center\">31</td><td align=\"center\">Gather info.</td><td align=\"right\" >Thomas</td></tr>" +
        "<tr><td align=\"center\">2.</td><td align=\"center\">56</td><td align=\"center\">Documentation</td><td align=\"right\" >Jane,Alice</td></tr>" +
        "<tr><td align=\"center\">3.</td><td align=\"center\">78</td><td align=\"center\">Planning</td><td align=\"right\" >Darin</td></tr>" +
        "<tr><td align=\"center\">4.</td><td align=\"center\">90</td><td align=\"center\">Coding</td><td align=\"right\" >John</td></tr>" +
        "<tr><td align=\"center\">5.</td><td align=\"center\">111</td><td align=\"center\">Implemention</td><td align=\"right\">John</td></tr>" +
        "<tr><td align=\"center\">6.</td><td align=\"center\">112</td><td align=\"center\">Submission</td><td align=\"right\">John</td></tr>" +
        "</table>" +
        "</div>" +
        "<div style=\"border-top:thin solid #666;margin:0 2px;height:6%;width:98%;\">" +
        "<table border=0 width=100% style=\"font-size:12px\">" +
        "<tr><td align=\"left\" width=25%>"+footdate+"</td><td align=\"center\" >"+footnote+"</td><td align=\"right\" width=25%>"+footpager+"</td></tr>" +
        "</table>" +
        "</div>" +
        "</div>" +
        "</div>";


        var reportTmp = new Wtf.Template(reportPreview);
        reportTmp.overwrite(Wtf.getCmp("layoutpreview").body);
        back.smTmp = namePanel.getSelectionModel();
        back.configstr=back.smTmp.getSelected().data['configstr'];

    },this);

    template_ds.load();

    var templatePanel = new Wtf.Panel({
        id:'templatePanel',
        layout:'border',
        border:false,
        width:500,
        items:[{
            region:'center',
            width:'50%',
            border:false,
            layout:'fit',
            height:'100%',
            items:[namePanel]
        },{
            region:'east',
            width:410,
            border:false,
            layout: 'fit',
            height:'100%',
            bodyStyle:"background:#EEEEEE",
            items:[{
                layout:'fit',
                xtype:'fieldset',
                cls: 'textAreaDiv',
                preventScrollbars:false,
                frame:true,
                border:false,
                id:'layoutpreview',
                html:"<div style='font-size:14px;margin-top:175px;text-align:center;'>Select a Template to preview.</div>"
            }]
        }]
    });

    var configstr="";

    this.templateWindow = new Wtf.Window({
        title:'Existing Report Templates',
        modal:true,
        iconCls: "pwnd favwinIcon",
        layout:'fit',
        items:[templatePanel],
        resizable:true,
        autoDestroy:true,
        height:600,
        width:600,
        buttons:[{
            text:'Select Columns',
            tooltip:{text:'Choose columns to be exported'},
            scope:this,
            handler:function() {
                var smTmpcheck = namePanel.getSelectionModel();
                if(smTmpcheck.getCount()<1){
                    WtfComMsgBox(792,0);
                    return;
                } else {
                    this.templateWindow.hide();
                    this.CallToExportInterface();
                }
            }
        },{

            text:'Export',
            tooltip:{text:'Export as pdf file using selected report template'},
            handler:function() {
                var smTmp = namePanel.getSelectionModel();
                if(this.type == "pdf") {
                    var sum = 0;
                    var selLength = this.storeToload.getCount();
                    var pdfWidth=800; //landscape
                    if(this.pageLayout!="true")
                        pdfWidth=570; //potrait
                    var max = Math.floor(pdfWidth/selLength);
                    max=Math.floor(max/10)*10;
                    for(var i = 0; i < selLength; i++)
                        sum += this.storeToload.data.items[i].data.width;
                   if(sum > pdfWidth){
                        this.CallToExportInterface();
                        this.templateWindow.hide();
                    } else {
                        if(smTmp.getCount()<1){
                            WtfComMsgBox(792,0);
                            return;
                        } else {
                            configstr=smTmp.getSelected().data['configstr'];
                            var url = this.url+"?config="+this.configstr+"&reportid="+this.name+"&name="+this.name+"&filetype="
                                        + this.type+"&gridconfig="+encodeURIComponent(this.gridConfig)+"&mapid="+this.mapid+"&year="+this.year+"&flag="+this.flag;
                            if(this.selectExport != undefined) {
                                url += "&selectExport="+this.selectExport;
                            }
                            if(this.extraConfig != undefined) {
                                url += "&extraconfig="+this.extraConfig;
                            }
                            if(this.comboName != undefined && this.comboValue != undefined) {
                                url += "&comboName="+this.comboName+"&filterCombo="+this.comboValue+"&comboDisplayValue="+this.comboDisplayValue;
                            }
                            if(this.field != undefined) {
                                url += "&field="+this.field+"&direction="+this.dir;
                            }
                            if(this.ss!=undefined && this.ss!="") {
                                url += "&ss="+this.ss;
                            }
                            if(this.heading!="" && this.heading!=undefined) {
                                url += "&heading="+this.heading;
                            }
                            if(this.json=="" && (this.fromdate=="" || this.todate =="")) {
                                url += "&isarchive=false&isconverted=0&transfered=0";
                            } else if(this.json!="") {
                                url += "&searchJson="+encodeURIComponent(this.json)+"&isarchive=false&isconverted=0&transfered=0";
                            } else if(this.fromdate!="" && this.todate !="") {
                                url += "&frm=" +this.fromdate+"&to="+this.todate+"&cd="+this.cd+"";
                            }
                            Wtf.get('downloadframe').dom.src = url;
                            this.templateWindow.close();
                        }
                        this.templateWindow.close();
                    }
                }
                
            },
            scope: this
        },{
            text:'Edit',
            tooltip:{text:'Edit selected report template'},
            handler: function() {
                var smTmp = namePanel.getSelectionModel();
                if(smTmp.getCount()<1){
                    WtfComMsgBox(792,0);
                    return;
                } else
                    this.newEditTemplate(smTmp.getSelected().data['configstr']);
            },
            scope:this
        },{
            text:'Delete',
            tooltip:{text:'Delete selected report template'},
            handler:function(){
                var smTmp = namePanel.getSelectionModel();
                if(smTmp.getCount()<1){
                    WtfComMsgBox(792, 0);
                    return;
                } else {
                    Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), 'Are you sure to delete the selected template?',function(btn) {
                        if(btn =='yes') {
                            Wtf.Ajax.requestEx({
                                url: "Common/ExportPdfTemplate/deleteReportTemplate.do",
                                params: {
									action: 2,
                                    deleteflag:this.templateid,
                                    userid:loginid
                                },
                                method:'POST'
                            },
                            this,
                            function(res) {
                                if(res.success)
                                    ResponseAlert(0);
                            },
                            function() {
                                WtfComMsgBox(794,1);
                            });
                            this.templateWindow.close();
                        }
                    },this); 
                }
            }, 
            scope:this
        },{
            text:'Create New',
            tooltip:{text:'Create new report template before exporting'},
            handler:function() {
               this.newEditTemplate(undefined);
            },
            scope: this
        },
        {
            text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
            handler:function() {
                this.templateWindow.close();
            },
            scope: this
        }]
    });
    this.templateWindow.show();

    Wtf.selectTempWin.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.selectTempWin,Wtf.Window,{
    onRender: function(conf){
        Wtf.selectTempWin.superclass.onRender.call(this, conf);
        this.add(this.templateWindow);
    },
    newEditTemplate: function(configstr) {
        var custForm=new Wtf.customReport({
            id:'custForm'+this.id + this.tabtitle,
            reportGrid:this.grid,
            reportid:this.name,
            name:this.name,
            filetype:this.filetype,
            gridconfig:this.gridConfig,
            searchJson:this.json,
            frm:this.fromdate,
            to:this.todate,
            cd:this.cd,
            year:this.year,
            reportType:1,
            url:this.url,
			selectExport:this.selectExport,
            field:this.field,
            dir:this.dir,
            mapid:this.mapid,
            flag:this.flag,
			comboName:this.comboName,
            comboValue:this.comboValue,
            comboDisplayValue:this.comboDisplayValue,
            editTemplateConfig:configstr,
            overwriteflag:this.templateid
        });
        var eobj = Wtf.getCmp(this.id + "_buildReport"+ this.tabtitle);
        if(eobj === undefined){
            eobj = new Wtf.reportBuilder.builderPanel({
                title: "<span wtf:qtip='Template Layout Builder'>"+Wtf.util.Format.ellipsis("Template Layout Builder",18)+"</span>",
                iconCls:"pwndCRM template_builder",
                id: this.id + "_buildReport" + this.tabtitle,
                closable: true,
                autoScroll: true,
                formCont: custForm
            });
            mainPanel.add(eobj);
        }
        this.templateWindow.close();
        mainPanel.setActiveTab(eobj);
        mainPanel.doLayout();
    },
    CallToExportInterface: function () {
        var expt =new Wtf.ExportInterface({
            type:"pdf",
            parent:this.templateWindow,
            name:this.name,
            cd:1,
            mapid:this.mapid,
            ss : this.ss,
            json:this.json,
            fromdate:this.fromdate,
            todate:this.todate,
            year:this.year,
            url:this.url==undefined?"../../exportmpx.jsp":this.url,
            selectExport:this.selectExport,
            field:this.field,
            dir:this.dir,
            pdfDs:this.storeToload,
            configstr:this.configstr,
            comboName:this.comboName,
            comboValue:this.comboValue,
            comboDisplayValue:this.comboDisplayValue,
            pageLayout:this.layout,
            flag:this.flag
        });
        expt.show();

    }
});

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
Wtf.form.FnComboBox=function(config){
    this.initial="REC";
    Wtf.form.FnComboBox.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.form.FnComboBox,Wtf.form.ComboBox,{
    addNewDisplay:WtfGlobal.getLocaleText("crm.editor.addnewBTN")+"...",//"Add New...",
    initComponent:function(config){
        Wtf.form.FnComboBox.superclass.initComponent.call(this, config);
        this.addNewID=this.initial+this.store.id;
        if(this.valueField&&this.valueField===this.displayField)
            this.addNewID=this.addNewDisplay;
        this.addLastEntry(this.store);
        this.store.on('load',this.addLastEntry,this);
        this.on('beforeselect',this.callFunction, this);
        if(this.hirarchical){
            this.tpl=new Wtf.XTemplate('<tpl for="."><div class="x-combo-list-item">{[this.getDots(values.level)]}{'+this.displayField+'}</div></tpl>',{
                getDots:function(val){
                    var str="";
                    for(var i=0;i<val;i++)
                        str+="....";
                    return str;
                }
            })
        }
    },
    
    onRender : function(ct, position){
        Wtf.form.FnComboBox.superclass.onRender.call(this, ct, position);
        if(this.addNewFn==undefined)return;
        this.anButton = this.wrap.createChild({tag: "img", src: Wtf.BLANK_IMAGE_URL, cls:"combo-addnew"});
        this.initAddNewButton();
        if(!this.width){
            this.wrap.setWidth(this.el.getWidth()+this.anButton.getWidth());
        }
    },

    initAddNewButton:function(){
        this.anButton.on("click", function(){
                if(this.disabled)return;
                if(this.isExpanded())
                    this.collapse();
                this.addNewFn();
            }, this, {preventDefault:true});
    },

    onResize : function(w, h){
        Wtf.form.FnComboBox.superclass.onResize.call(this, w, h);
        if(this.addNewFn==undefined)return;
        if(typeof w == 'number'){
            this.el.setWidth(this.adjustWidth('input', w -this.trigger.getWidth() - this.anButton.getWidth()));
        }
        this.wrap.setWidth(this.el.getWidth()+this.trigger.getWidth()+this.anButton.getWidth());
    },

    addLastEntry:function(s){
        var recid=s.find(this.valueField||this.displayField,this.addNewID);
        if(recid==-1){
            var comboRec=Wtf.data.Record.create(s.fields);
            var rec=new comboRec({});
            s.insert(0,rec);
            rec.beginEdit();
            rec.set(this.valueField||this.displayField, this.addNewID);
            rec.set(this.displayField, this.addNewDisplay);
            rec.endEdit();
        }
    },

    callFunction:function(c,r){
        if(r.data[this.valueField]==this.addNewID){
            this.collapse();
            this.addNewFn();
            return false;
        }
    }
});


function setPanelIcon(tabpanel,panel,moduleid){
    var el = Wtf.get(tabpanel.getTabEl(panel));
    el.addClass('x-tab-with-icon');
    var src='images/store/?recordid='+moduleid+'&size=16&'+Math.random();
    el.child('span:last').dom.style.backgroundImage = 'url(' + src  +')!important';
}




    function addConfig(moduleid){
//
        var configTypeRec = Wtf.data.Record.create([{
            name: 'id',
            type: 'string'
            }, {
            name: 'configtype',
            type: 'string'
            }, {name:'delete'}]);

        var configTypeReader = new  Wtf.data.KwlJsonReader1({
                root: "data"
            }, configTypeRec);


        var configTypeStore = new Wtf.data.Store({
            url: Wtf.req.mbuild+'form.do',
            baseParams: {
                action:31,
                reportid:moduleid
            },
            reader: configTypeReader
        });
        configTypeStore.load();

        var gridds = new Wtf.data.Store({
            url: Wtf.req.mbuild+'form.do',
            baseParams: {
                action:32,
                reportid:moduleid
            },
            reader: new Wtf.data.KwlJsonReader({
                    root:"data"},
                configTypeRec)
        });
        gridds.load();
        gridds.on("load",function(){
            configTypeStore.load();
            configTypeCombo.setValue("");
        },this)

        var gridcm = new Wtf.grid.ColumnModel([
                new Wtf.grid.RowNumberer(),
                {
                    dataIndex: 'id',
                    hidden: true
                },{
                    header: "Config Type",
                    dataIndex: 'configtype'
            },{
                header: WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
                width: 65,
                dataIndex:'delete',
                renderer: function(val) {
                      return "<img class='deleteConfig' src='images/Cancel.gif' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Delete Config'></img>";
                }
            }]);

            var configTypeCombo = new Wtf.form.ComboBox({
                    valueField: 'id',
                    displayField: 'configtype',
                    store: configTypeStore,
                    editable: false,
                    typeAhead: true,
                    mode: 'local',
                    width:150,
                    triggerAction: 'all',
                    selectOnFocus: true,
                    emptyText: 'Select standard config option'
                })
            var cofigGrid = new Wtf.grid.GridPanel({
                                store: gridds,
                                cm: gridcm,
                                border: false,
                                clicksToEdit: 1,
                                viewConfig: {
                                    forceFit: true
                                }
                            });
         var ConfigWin = new Wtf.Window({
            title:'Add Config Window',
            layout:'fit',
            modal: true,
            iconCls: 'winicon',
            height:300,
            width:350,
            scope: this,
            items:[this.poppanel = new Wtf.Panel({
                layout: 'fit',
                cls: 'backcolor',
                border: false,
                tbar: ['-',' Standard Config Option: ',configTypeCombo  ,'-',{
                    text: 'Add',
                    tooltip: {
                        title: WtfGlobal.getLocaleText("crm.ADDTEXT"),//'Add',
                        text: 'Click to add new Role'
                    },
                    handler: function() {
                        gridds.load({
                           params:{
                               add:true,
                               configid: configTypeCombo.getValue()
                           }
                        });
//                        this.gridds.on("")
                    },
                    scope: this
                },'-'],
                items: [ new Wtf.Panel({
                            layout: 'fit',
                            border: false,
                            items: [cofigGrid]
                        })]
                })]
        });
        ConfigWin.show();
        cofigGrid.on('cellclick', function(gd, ri, ci, e) {
             var event = e;
            if(event.target.className == "deleteConfig") {
                 gridds.load({
                           params:{
                               deleteconfig : true,
                               configid: gd.store.data.items[ri].get("id")
                           }
                        });
            }
        })


    }

    function assignModulePermissions(moduleid){
//        var taskid = Wtf.getCmp(this.getType(id)).refId;
        var accessPanel  = Wtf.getCmp(moduleid+ this.id);
        if(!accessPanel){
            accessPanel = new Wtf.MainAuthPanel({
                id:moduleid+ this.id,
                layout: "fit",
                border: false,
                closable: true,
                taskid: moduleid,//Wtf.getCmp(this.getType(id)).refId,
                title: "Manage Access Rights"
            })
            mainPanel.add(accessPanel);
        }
        mainPanel.setActiveTab(accessPanel);
        mainPanel.doLayout();
    }



function openDocListWin(moduleid,recid,permsObj){
    var doclistwin = new Wtf.docListWindow({
        wizard:false,
        closeAction : 'hide',
        layout: 'fit',
        moduleid : moduleid,
        id:"doclistWin"+moduleid+recid,
        recid : recid,
        permsObj:permsObj,
        autoScroll:true/*,
        autoHeight: true*/
    });
    doclistwin.show();
}


Wtf.docListWindow = function(config){
    Wtf.apply(this, config);

    this.subRec = Wtf.data.Record.create([
        { name: "link"},
        { name: "docid"},
        { name: "docpath"}
    ]);

    this.doclinkStore = new Wtf.data.Store({
        url :Wtf.req.mbuild+'form.do',
        baseParams :{
            action: 30,
            moduleid: this.moduleid,
            recid : this.recid
        },
        autoLoad :true,
        reader: new Wtf.data.KwlJsonReader({
            root: 'data'
        }, this.subRec)
    });

    this.doclinkStore.on("load",function(){
        Wtf.getCmp('doclinkDataView').refresh();
        Wtf.get('doccount_'+this.recid).dom.innerHTML = "("+ this.doclinkStore.getCount() +")";
    },this);


    var textfield = new Wtf.form.TextField({
            labelSeparator:'',
            name: 'attach' + (this.count++),
            inputType: 'file'
        });
    this.hiddenmoduleid = new Wtf.form.Hidden({
        name:'moduleid'
    });
     this.hiddenRecid = new Wtf.form.Hidden({
        name:'recid'
    });
    this.hiddenmoduleid.setValue(this.moduleid);
    this.hiddenRecid.setValue(this.recid);    
    this.top = new Wtf.Panel({
//        layout:'fit',
        region: 'north',
        height:125,
        autoScroll:true,
        bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',

        items:[{
                xtype: 'dataview',
                id: 'doclinkDataView',
                autoScroll:true,
                itemSelector: "doclink",
                tpl: new Wtf.XTemplate('<div class="listpanelcontent"><tpl for=".">{[this.f(values)]}</tpl></div>', {
                    f: function(val) {
//                                "&nbsp&nbsp<a href='javascript:void(0)' title='Delete' onclick='deleteFile(\"" + val.docid + "\",\""+val.reftable + "/" + val.docpath+"\")'>delete</a>
//                                var htmlstr ="";
//                                if(val.subid!='0')
//                                    htmlstr = "<div><a href='#' class='mailTo' onclick='subscribeInvoice(\""+val.subid+"\")'><span class='holidaySpan' style = 'width:150px;'>" + Date.parseDate(val.billdate,'Y-m-d h:i:s.0' ).format(WtfGlobal.getDateFormat()) + "</span></a>";
//                                else
//                                    htmlstr = "<div><span class='holidaySpan' style = 'width:150px;'>" + Date.parseDate(val.billdate,'Y-m-d h:i:s.0' ).format(WtfGlobal.getDateFormat()) + "</span>";
//                                htmlstr += "<span class='holidaySpan' style = 'width:90px;'>"+val.numproj+"</span>"+
//                                    "<span class='holidaySpan' style = 'width:70px;'>"+val.rate+ "</span>"+
//                                    "<span class='holidaySpan' style = 'width:90px;'>"+val.frequency+ "</span>"
//                                if(val.flag == '1')
//                                    htmlstr +="<img src='images/Delete.png' class='holidayDelete' onclick=\"cancelSubsci(this,'" + this.scope.id+"')\" id='del_"+val.subid+"' title='Cancel Subscription'>";
//                                htmlstr +="<div><span class='holidayDiv' style = 'width:450px !important;'></span></div></div>";
//                                return htmlstr;
                          var htmlStr = "";
//                          var deldocperm = eval('('+this.scope.permsObj[6][8]+')');
                          var deldocperm = eval('('+this.scope.permsObj[Wtf.mbuild.permActions.deleteDocument]+')');
                          if(val.link!="" && checktabperms(deldocperm.permgrid,deldocperm.perm))
                             htmlStr = val.link+"&nbsp&nbsp<a href='javascript:void(0)' title='Delete' onclick='deleteFile(\"" + val.docid + "\",\"" + val.docpath+"\",\""+this.scope.id+"\")'>delete</a><br/><br/>";
                          else
                             htmlStr = val.link+"<br/><br/>";
                         
                         return htmlStr;
                    },
                    scope: this
                }),
                store: this.doclinkStore
            }]
    });
     this.attachheight = 75;
     this.count = 1;
//     var adddocperm = eval('('+this.permsObj[5][7]+')');
      var adddocperm = eval('('+this.permsObj[Wtf.mbuild.permActions.addDocument]+')');
      this.attachPanel = {
                            xtype : 'panel',
                            border: false,
                            id:'attachmentlink',
                            html:checktabperms(adddocperm.permgrid,adddocperm.perm)?"<a id = 'attachmentlink"+this.id+"'class='attachmentlink' href=\"#\" onclick=\"Attachfile(\'"+this.id+"\')\">Upload a file</a>":""
                        };
    this.center = new Wtf.FormPanel({
                    frame: false,
                    region: 'center',
//                    layout:'fit',
                    bodyStyle : 'margin-top:20px;margin-left:15px;font-size:12px;',
                    url: Wtf.req.mbuild+'form.do?action=29',
                    border: false,
                    labelWidth:0,
                    autoScroll:true,
                    fileUpload: true,

//                    height: 200,
                    layout: 'form',
                    items: [ this.attachPanel,this.hiddenmoduleid, this.hiddenRecid]
                });
    this.innerPanel = new Wtf.Panel({
                            layout:'border',
                            border:false,
                            bodyStyle : 'background:#f1f1f1;font-size:10px;',
                            height:350,
                            width:350,
                            items:[this.top,this.center]
                    });

    Wtf.docListWindow.superclass.constructor.call(this, {
        title : 'Document List',
        bodyStyle : 'background:#f1f1f1;',
        items:this.innerPanel,
        resizable:false,
        width : 350,
        heigth:350,
        buttons:[{
            text    : "Upload",
            scope   : this,
            handler : this.uploadClick
        },{
            text    : "Close",
            scope   : this,
            handler : function(){
                this.close();
            }
        }]
    });
};

Wtf.extend(Wtf.docListWindow, Wtf.Window, {
    usernames: [],
    userIds: [],
    uploadClick:function() {
        this.center.form.submit({
            scope: this,
            params:[{
                    moduleid:this.moduleid,
                    recid:this.recid
            }],
            success: function(result,action){
                this.doclinkStore.reload();
                while(this.count!=1){
                    this.count--;
                    Wtf.getCmp('fileattach'+this.count+this.id).destroy();

                }
                document.getElementById('attachmentlink'+this.id).innerHTML = "Upload a file";
                this.doLayout();

            },
            failure: function(frm, action){

            }
        });
    },
     removeFile:function(){
        this.attachheight -=25;
//        this.center.ownerCt.setHeight(this.attachheight);
//        this.center.setHeight(this.attachheight);
//        if(this.count>5)
//            document.getElementById('attachmentlink'+this.id).style.display='block';
        this.count--;
        if(this.count==1)
            document.getElementById('attachmentlink'+this.id).innerHTML = "Upload a file";
        this.doLayout();
    },
    Attachfile:function(){

        var textfield = new Wtf.form.TextField({
                fieldLabel: '',
                labelSeparator:'',
                name: 'attach'+(this.count),
                inputType: 'file'
            });
//            this.attachheight = this.attachheight+25;
            var pid = 'fileattach'+this.count+this.id;
            this.center.insert(this.count++,new Wtf.Panel({id : pid,cls:'fileattachremove',border:false,html:'<a href=\"#\" class ="attachmentlink" style ="margin-left:5px" onclick=\"removefile(\''+pid+'\',\''+this.id+'\')\">Remove</a>',
                                    items:textfield})
                        );
//            this.center.ownerCt.setHeight(this.attachheight);
//            this.center.setHeight(this.attachheight);
            //this.attachPanel.html = "<a href=\"#\" onclick=\"Attachfile(\'"+this.id+"\')\">Attach another file</a>";
            document.getElementById('attachmentlink'+this.id).innerHTML = "Upload another file";
            //this.attachPanel.el.dom.childNodes[0].childNodes[0].childNodes[0].innerHTML="Attach another file";
//        if(this.count>5)
//            document.getElementById('attachmentlink'+this.id).style.display='none';

        this.doLayout();
    },
    deleteFile:function(docid,docpath){
        this.doclinkStore.load({
            params:{
                    deleteFile:true,
                    docid:docid,
                    docpath:docpath
            }
        });
    },
    onRender: function(config){
        Wtf.docListWindow.superclass.onRender.call(this, config);
    }

});

function Attachfile(objid){
        Wtf.getCmp(objid).Attachfile();
}


function removefile(objid,thisid){
        Wtf.getCmp(objid).destroy();
        Wtf.getCmp(thisid).removeFile();
}
function deleteFile(docid,docpath,thisid){
        Wtf.getCmp(thisid).deleteFile(docid,docpath);
}



 function getConfigButton(scope,configInfo,buttArray,permsObj){
        if(configInfo.stdconfig){
            for(var i = 0; i<configInfo.stdconfig.length;i++){
//                var addcommperm = eval('('+permsObj[3][5]+')');
//                var adddocperm = eval('('+permsObj[5][7]+')');
                var addcommperm = eval('('+permsObj[Wtf.mbuild.permActions.addComment]+')');
                var adddocperm = eval('('+permsObj[Wtf.mbuild.permActions.addDocument]+')');
                if(configInfo.stdconfig[i].id==2 && checktabperms(adddocperm.permgrid,adddocperm.perm)){              
                    buttArray.push(new Wtf.Toolbar.Button({
                        text : "Add Documents",
                        scope : scope,
                        disabled: false,
                        handler : function() {
                            if(scope.modulegrid.getSelectionModel().getSelections().length==1){
                                var recId = scope.modulegrid.getSelectionModel().getSelected().get("id");
                                openDocListWin(scope.moduleid,recId,permsObj);
                            }else{
                                msgBoxShow(['Error', 'Select atleast one record'], Wtf.MessageBox.OK);
                            }
                        }
                    }));
                }else if(configInfo.stdconfig[i].id==1 && checktabperms(addcommperm.permgrid,addcommperm.perm)){
                    buttArray.push(new Wtf.Toolbar.Button({
                        text : WtfGlobal.getLocaleText("crm.common.addnewcomment"),//"Add New Comment",
                        scope : this,
                        disabled: false,
                        handler : function() {
                          if (scope.modulegrid.getSelectionModel().getSelections().length==1){
                            var recId = scope.modulegrid.getSelectionModel().getSelected().get("id");
                            var  commentWin = new Wtf.newCommentWindow({
                                title:WtfGlobal.getLocaleText("crm.common.addnewcomment"),//"Add New Comment",
                                closable:true,
                                border:false,
                                modal:true,
                                width : 340,
                                height: 250,
                                iconCls : 'win',
                                layout: "fit",
                                recordid : recId,
                                moduleid : scope.moduleid,
                                resizable: false,
                                gridstore:scope.modulegrid.store
                            });
                            commentWin.show();
                        }else{
                            msgBoxShow(['Error', 'Select one record'], Wtf.MessageBox.OK);
                        }
                    }
                   }));
                }else if(configInfo.stdconfig[i].id==3){
                    buttArray.push(new Wtf.Toolbar.Button({
                        text :WtfGlobal.getLocaleText("crm.editor.exportBTN"),// "Export",
                          iconCls: 'pwnd exporticon',
                        tooltip: {
                            title:WtfGlobal.getLocaleText("crm.common.exportdata"),//'Export Data',
                            text: WtfGlobal.getLocaleText("crm.common.exportbtnttip.clicktoexp")//"Click to export data"
                        },
                        scope : this,
                        disabled: false,
                        menu: [ new Wtf.Action({
                                    text: WtfGlobal.getLocaleText("crm.editor.export.csv"),//'Export to CSV file',
                                    iconCls: 'pwnd exporticon',
                                    handler : function() {
                                        exportmoduleData(scope.moduleid,scope.modulename,"csv",true, scope.taskid);
                                    },
                                    scope: this
                                })/*,
                                new Wtf.Action({
                                    text: 'Export to PDF file',
                                    iconCls: 'pwnd exporticon',
                                    handler: function() {
                                        alert("in export PDF functionality");
                                    },
                                    scope: this
                                })*/
                        ]
                    }));
                }

            }
        }
        return buttArray;
    }

    function exportmoduleData(moduleid,filename,filetype,moduleflag,taskid){//moduleflag is used to determine wether that module is made from module builder or report builder
         //moduleflag = true ? moduleBuilder?reportbuilder
         Wtf.get('downloadframe').dom.src = 'exportData.jsp?moduleid=' +moduleid  + "&filetype=" + filetype+"&filename=" + filename+"&moduletype="+moduleflag+"&taskid="+taskid;
     }

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
Wtf.AccountTree = function(config){
    var childArr = [];

    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.view)) {
        childArr.push({
                "id":"accountnode",
                "text":"<span wtf:qtip='"+WtfGlobal.getLocaleText({key:"crm.mymails.add",params:['Account']})+"' wtf:qtitle='"+WtfGlobal.getLocaleText("crm.mymails.addaccount")+"?'> "+WtfGlobal.getLocaleText("crm.ACCOUNT.plural")+"<\/span>", "level":"1", "nodetype":"1", "nodemode":"0", "expanded":false,"mode":"21"
            });
    }
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.view)) {
        childArr.push({
                "id":"leadnode","text":"<span wtf:qtip='"+WtfGlobal.getLocaleText({key:"crm.mymails.add",params:['Lead']})+"' wtf:qtitle='"+WtfGlobal.getLocaleText("crm.mymails.addlead")+"?' class='dashboardcontent' > "+WtfGlobal.getLocaleText("crm.LEAD.plural")+" <\/span>","level":"2","nodetype":"2","nodemode":"5","expanded":false,"mode":"22"
            });
    }
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.view)) {
        childArr.push({
                "id":"productnode","text":"<span wtf:qtip='"+WtfGlobal.getLocaleText({key:"crm.mymails.add",params:['Product']})+"' wtf:qtitle='"+WtfGlobal.getLocaleText("crm.mymails.addproduct")+"?' > "+WtfGlobal.getLocaleText("crm.PRODUCT.plural")+" <\/span>","level":"3","nodetype":"1","nodemode":"0", "expanded":false,"mode":"23"
            });
    }
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.view)) {
        childArr.push({
                "id":"campaignnode","text":"<span class='dashboardcontent' wtf:qtip='"+WtfGlobal.getLocaleText({key:"crm.mymails.add",params:['Campaign']})+"' wtf:qtitle='"+WtfGlobal.getLocaleText("crm.campaign.createcampaignwin.tophtml.title")+"?' > "+WtfGlobal.getLocaleText("crm.CAMPAIGN.plural")+" <\/span>","level":"4","count":5,"nodetype":"1","nodemode":"0", "expanded":false,"mode":"24"
            });
    }
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Document, Wtf.Perm.Document.doc)) {
        childArr.push({
                "id":"documentnode","text":"<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mymails.mydocs.ttip")+"' wtf:qtitle='"+WtfGlobal.getLocaleText("crm.dashboard.mydocuments")+"'> "+WtfGlobal.getLocaleText("crm.dashboard.mydocuments")+"  <\/span>","level":"5", "expanded":false,"mode":"25"
             });
    }
    Wtf.apply(this,config,{
            root: new Wtf.tree.AsyncTreeNode({
        "text":"<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mymails.quickview.ttip")+"'>"+WtfGlobal.getLocaleText("crm.dashboard.westpanel.quickview")+"<\/span>",
        "level":"0",
        "nodetype":"1",
        "nodemode":"0",
        "children":childArr,
        "expanded":true,"mode":"0"
    })
    });
    Wtf.AccountTree.superclass.constructor.call(this,config);

};

Wtf.extend(Wtf.AccountTree, Wtf.tree.TreePanel, {
    autoWidth       : true,
    autoHeight      : true,
    border          : false,
    selModel        : new Wtf.tree.DefaultSelectionModel(),
    loader          : new Wtf.tree.TreeLoader({
        dataUrl: Wtf.req.springBase + Wtf.req.tree + 'getTree.do',
        preloadChildren:false, // send request on expand
        baseParams:{
            mode:'0',
            valreq:'0'
        }
    }),

    initComponent: function(){
        Wtf.AccountTree.superclass.initComponent.call(this);
        this.on('click', function(node) {
            this.openTab(node);
        }, this);
        this.accid="";
        this.tempnode=null;

       var contextMenu = new Wtf.menu.Menu({
            items:[{
                text    : WtfGlobal.getLocaleText("crm.mymails.addaccount"),//'Add Account',
                iconCls : 'pwndCRM accounttreeicon',
                scope   : this,
                handler : this.popup

            }
            ]
            });

        this.on('contextmenu', function(node, e) {
            e.preventDefault();
            this.tempnode=node;
            if(node.attributes.level==1) {
                if(node.attributes.mode==1) {
                    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.manage)){
                        var contextMenu1 = new Wtf.menu.Menu({
                            items:[{
                                text    : WtfGlobal.getLocaleText("crm.profileview.addactivitybtn"),//'Add Activity',
                                iconCls : 'pwndCRM todolisttreepane',
                                scope   : this,
                                handler : this.addact

                            }
                            ]
                        });
                        contextMenu1.node = node;
                        contextMenu1.showAt(e.getXY());
                        this.treeContextMenuNode = node;
                        this.accid=node.parentNode.attributes.nodeid;
                    }
                } else {
                    if(node.attributes.mode==2) {
                        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.manage)){
                            var contextMenu2 = new Wtf.menu.Menu({
                                items:[{
                                    text    : WtfGlobal.getLocaleText("crm.account.toptoolbar.addoppbtn"),//'Add Opportunity',
                                    iconCls :'pwndCRM opportunitytreeIcon',
                                    scope   : this,
                                    handler : this.addopp
                                }]
                            });
                            contextMenu2.node = node;
                            contextMenu2.showAt(e.getXY());
                            this.treeContextMenuNode = node;
                            this.accid=node.parentNode.attributes.nodeid;
                        }
                    } else {
                        if(node.attributes.mode==3) {
                            if(!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.manage)){
                                var contextMenu3 = new Wtf.menu.Menu({
                                    items:[{
                                        text    : WtfGlobal.getLocaleText("crm.contact.addcontact"),//'Add Contact',
                                        iconCls : 'pwndCRM contactstreeIcon',
                                        scope   : this,
                                        handler : this.addcon
                                    }]
                                });
                                contextMenu3.node = node;
                                contextMenu3.showAt(e.getXY());
                                this.treeContextMenuNode = node;
                                this.accid=node.parentNode.attributes.nodeid;
                            }
                        } else {
                            if(node.attributes.mode==4) {
                                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.manage)){
                                    var contextMenu4 = new Wtf.menu.Menu({
                                        items:[{
                                            text    : WtfGlobal.getLocaleText("crm.case.addcase"),//'Add Case',
                                            iconCls : 'pwndCRM casetreeIcon',
                                            scope   : this,
                                            handler : this.addcas
                                        }]
                                    });
                                    contextMenu4.node = node;
                                    contextMenu4.showAt(e.getXY());
                                    this.treeContextMenuNode = node;
                                    this.accid=node.parentNode.attributes.nodeid;
                                }
                            } else {
                                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.manage)){
                                    contextMenu.node = node;
                                    contextMenu.showAt(e.getXY());
                                    this.treeContextMenuNode = node;
                                }
                            }
                        }
                    }
                }
            }
            if(node.attributes.level==2) {
                if(node.attributes.mode==8) {
                    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.manage)){
                        var contextMenu5 = new Wtf.menu.Menu({
                            items:[{
                                text    : WtfGlobal.getLocaleText("crm.profileview.addactivitybtn"),//'Add Activity',
                                iconCls : 'pwndCRM todolisttreepane',
                                scope   : this,
                                handler : this.addleadact

                            }
                            ]
                        });
                        contextMenu5.node = node;
                        contextMenu5.showAt(e.getXY());
                        this.treeContextMenuNode = node;
                        this.accid=node.parentNode.attributes.accid;
                    }
                } else {
                    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.manage)){
                        var contextMenu6 = new Wtf.menu.Menu({
                            items:[{
                                text    : WtfGlobal.getLocaleText("crm.mymails.addlead"),//'Add Lead',
                                iconCls : 'pwndCRM leadtreeicon',
                                scope   : this,
                                handler : this.popuplead
                            }]
                        });
                        contextMenu6.node = node;
                        contextMenu6.showAt(e.getXY());
                        this.treeContextMenuNode = node;
                        this.accid=node.parentNode.attributes.accid;
                    }
                }
            }
            if(node.attributes.level==3) {
                   if(!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.manage)){
                    var contextMenu7 = new Wtf.menu.Menu({
                        items:[{
                            text    : WtfGlobal.getLocaleText("crm.mymails.addproduct"),//'Add Product',
                            iconCls : "pwndCRM producttreeicon",
                            scope   : this,
                            handler : this.popupproduct
                        }]
                    });
                    contextMenu7.node = node;
                    contextMenu7.showAt(e.getXY());
                    this.treeContextMenuNode = node;
                }
            }
            if(node.attributes.level==4) {
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.manage)){
                    var contextMenu8 = new Wtf.menu.Menu({
                        items:[{
                            text    : WtfGlobal.getLocaleText("crm.campaign.createcampaignwin.tophtml.title"),//'Add Campaign',
                            iconCls : 'pwndCRM campaigntreeicon',
                            scope   : this,
                            handler : this.popupcamp
                        }]
                    });
                    contextMenu8.node = node;
                    contextMenu8.showAt(e.getXY());
                    this.treeContextMenuNode = node;
                }
            }
            if(node.attributes.level==0) {
                var contextMenu9 = new Wtf.menu.Menu({
                    items:[{
                        text    : WtfGlobal.getLocaleText("crm.refresh"),//'Refresh',
                        iconCls :'pwndCRM reset',
                        scope   : this,
                        handler : function(node) {
                            //var node = this.treeContextMenuNode;
                            this.getLoader().baseParams={mode:'26'};
                            this.getLoader().load(node);
                            node.expand()
                        }.createDelegate(this,[node])
                        }]
                    });
                contextMenu9.node = node;
                contextMenu9.showAt(e.getXY());
                this.treeContextMenuNode = node;
            }
            if(node.attributes.level==5) {
                var contextMenu11 = new Wtf.menu.Menu({
                    items:[{
                        text    : WtfGlobal.getLocaleText("crm.refresh"),//'Refresh',
                        iconCls :'pwndCRM reset',
                        scope   : this,
                        handler : function() {
                            var node = this.treeContextMenuNode;
                            this.getLoader().baseParams.mode = "25";
                        }
                    }]
                    });
                contextMenu11.node = node;
                contextMenu11.showAt(e.getXY());
                this.treeContextMenuNode = node;
            }
        }, this);

    },
    addact:function(){

          chktaskstatusload();
          var exportRecord = new Wtf.data.Record.create([
            {
                name: "name",
                mapping:'cname'
            },{
                name: "id",
                mapping:'cid'
            }
            ]);
            var exportRecordReader = new Wtf.data.KwlJsonReader1({
                root: "data",
                totalProperty: 'count'
            }, exportRecord);

           this.exportDS = new Wtf.data.Store({
                url: Wtf.calReq.cal + "getCalendarlist.do",
                reader: exportRecordReader,
                method: 'POST',
                baseParams: {
                    userid: loginid
                },
                autoLoad:false
            });

            this.exportDS.load();
            this.exportDS.add(new exportRecord({name:Wtf.DEFAULT_CALENDAR, id:Wtf.DEFAULT_CALENDAR}));
            this.calendar=  new Wtf.form.ComboBox({
                fieldLabel: WtfGlobal.getLocaleText("crm.calendar.calendar"),//"Calendar",
                id:this.id+'calendarid',
                selectOnFocus:true,
                triggerAction: 'all',
                mode: 'local',
                store: this.exportDS,
                displayField: 'name',
                typeAhead: true,
                allowBlank:false,
                forceSelection:true,
                valueField:'id',
                msgTarget:'side',
                width: 230,
                value:Wtf.DEFAULT_CALENDAR
            });
            
          this.flagStore = new Wtf.data.SimpleStore({
            fields: ['id','name'],
            data : [
            ["Task","Task"],
            ["Event","Event"]
            ]
        });

        this.starttimeCombo = new Wtf.form.TimeField({
            fieldLabel: '',
            id: "starttime"+this.id,
            name: 'starttime1',
            minValue: WtfGlobal.setDefaultMinValueTimefield(),
            maxValue: WtfGlobal.setDefaultMaxValueTimefield(),
            labelSeparator:"",
            width: 230,
            format:WtfGlobal.getLoginUserTimeFormat(),
            value:WtfGlobal.setDefaultValueTimefield()

        })

        this.endtimeCombo = new Wtf.form.TimeField({
            fieldLabel: '',
            id: "endtime"+this.id,
            name: 'endtime1',
            labelSeparator:"",
            minValue: WtfGlobal.setDefaultMinValueTimefield(),
            maxValue: WtfGlobal.setDefaultMaxValueTimefield(),
            width: 230,
            format:WtfGlobal.getLoginUserTimeFormat(),
            value:WtfGlobal.setDefaultValueEndTimefield()

        })
        var scheduleTypeStore = new Wtf.data.SimpleStore({
            fields: ["id", "value"],
            data: [["0","Does not repeat"],["1", "Daily"], ["2", "Weekly"], ["3", "Monthly"]]
        });

        this.scheduleTypeCombo = new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("crm.spreadsheet.scheduletypecombo"),//"Schedule Type",
            store: scheduleTypeStore,
            displayField: "value",
            valueField: "id",
            width: 230,
            allowBlank: false,
            msgTarget:'side',
            forceSelection:true,
            mode: "local",
            id:this.id+"scheduleType",
            triggerAction: "all",
            emptyText:WtfGlobal.getLocaleText("crm.goalsettings.combo.emptytxt")//"--Please Select--"
        });

        this.allDayCheckBox= new Wtf.form.Checkbox({
            boxLabel:" ",
            name:'rectype',
            checked:false,
            inputValue:'false',
            width: 50,
            fieldLabel:WtfGlobal.getLocaleText("crm.common.allday")//"All Day"
        })
        this.subject = new Wtf.form.TextField({
                fieldLabel:WtfGlobal.getLocaleText("crm.case.defaultheader.subject")+'*',//'Subject *',
                id:'subject'+this.id,
                width:230,
                allowBlank:false,
                msgTarget:'side',
                maxLength:512,
                xtype:'striptextfield'
        })
        this.taskCheck=new Wtf.form.FieldSet({
            title: WtfGlobal.getLocaleText("crm.ACTIVITY"),//'Activity',
            autoHeight:true,
            defaultType: 'textfield',
            frame:false,
            labelWidth:90,
            items :[
                   this.flag=new Wtf.form.ComboBox({
                    fieldLabel: WtfGlobal.getLocaleText("crm.report.activity.taskorevent")+'*',//'Task/Event* ',
                    id:this.id+'actflag',
                    selectOnFocus:true,
                    triggerAction: 'all',
                    mode: 'local',
                    store: this.flagStore,
                    displayField: 'name',
                    emptyText:WtfGlobal.getLocaleText("crm.goalsettings.combo.emptytxt"),//"-- Please Select --",
                    typeAhead: true,
                    allowBlank:false,
                    forceSelection:true,
                    valueField:'id',
                    msgTarget:'side',
                    width:230
            }),
            this.subject,
            this.startDate = new Wtf.form.DateField({
                fieldLabel:WtfGlobal.getLocaleText("crm.calendar.eventdetails.starttime"),//'Start Time',
                format:WtfGlobal.getOnlyDateFormat(),
                readOnly:true,
                offset:Wtf.pref.tzoffset,
                id:'startdate'+this.id,
                allowBlank:false,
                msgTarget:'side',
                value:new Date(),
                width: 230
            }),
            this.starttimeCombo,
            this.endDate = new Wtf.form.DateField({
                fieldLabel:WtfGlobal.getLocaleText("crm.calendar.eventdetails.endtime"),//'End Time',
                format:WtfGlobal.getOnlyDateFormat(),
                readOnly:true,
                offset:Wtf.pref.tzoffset,
                id:'enddate'+this.id,
                allowBlank:false,
                msgTarget:'side',
                value:new Date(),
                width: 230
            }),this.endtimeCombo,
            this.calendar,
            this.allDayCheckBox
           ]
        });


        this.accountCheck=new Wtf.form.FieldSet({
            title: WtfGlobal.getLocaleText("crm.common.reccurence"),//'Recurrence',
            autoHeight:true,
            defaultType: 'textfield',
            frame:false,
            labelWidth:90,
            items :[
            this.scheduleTypeCombo,
            this.neverCheckBox= new Wtf.form.Checkbox({
                boxLabel:" ",
                name:'rectype',
                checked:true,
                inputValue:'false',
                width: 50,
                fieldLabel:WtfGlobal.getLocaleText("crm.common.endsnever")//"Ends Never"
            }),this.untilDate = new Wtf.form.DateField({
                fieldLabel:WtfGlobal.getLocaleText("crm.common.tilldate"),//'Till Date',
                format:WtfGlobal.getOnlyDateFormat(),
                readOnly:true,
                id:'tilldat'+this.id,
                allowBlank:false,
                msgTarget:'side',
                width: 230
            })
           ]
        });

        this.form1=new Wtf.form.FormPanel({
            border:false,
            items:[

            this.taskCheck,
            this.accountCheck

            ]

        });
        var accName= this.tempnode.parentNode.attributes.text;
        this.win=new Wtf.Window({
            height:540,
            width:430,
            iconCls: "pwnd favwinIcon",
            title:WtfGlobal.getLocaleText("crm.ACTIVITY"),//"Activity",
            modal:true,
            id:'actquickinsert',
            shadow:true,
            resizable:false,
            buttonAlign:'right',
            buttons: [{ text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                        scope:this,
                        handler:function(){
                    if(this.form1.getForm().isValid()) {
                        this.saveact();

                    } else{
                        ResponseAlert(152);
                    }
                }
            },{
                text: WtfGlobal.getLocaleText("crm.CLOSE"),//,
                scope:this,
                handler:function(){
                    this.fireEvent('close');
                    this.win.close();
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText("crm.ACTIVITY"),WtfGlobal.getLocaleText({key:"crm.ACTIVITY",params:[accName]}) ,"../../images/activity1.gif")
            },{
                region : 'center',
                border : false,

                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 20px 10px 20px',
                layout : 'fit',
                items :[   this.form1   ]
            }]

        });
        this.win.show();
        this.flag.setValue("Event");
        this.scheduleTypeCombo.setValue("0");
        this.untilDate.disable();
        this.neverCheckBox.disable();
        this.allDayCheckBox.on('check',function(){
                if(this.allDayCheckBox.getValue()){
                    this.starttimeCombo.disable();
                    this.starttimeCombo.setValue("");
                    this.endtimeCombo.disable();
                    this.endtimeCombo.setValue("");
                }
                else {
                    this.starttimeCombo.enable();
                    this.starttimeCombo.setValue("8:00 AM");
                    this.endtimeCombo.enable();
                    this.endtimeCombo.setValue("9:00 AM");
                }
        },this);

        this.neverCheckBox.on('check',function(){
            if(this.neverCheckBox.getValue()){
                this.untilDate.disable();
                this.untilDate.setValue("");
            } else{
                this.untilDate.enable();
                this.untilDate.setValue(new Date());
            }
        },this);
        this.scheduleTypeCombo.on("select",function(a,b,c){
            if(c==0){
               this.untilDate.disable();
               this.untilDate.setValue("");
               this.neverCheckBox.disable();
            } else {
               if(!this.neverCheckBox.getValue()){
                 this.untilDate.enable();
               }
               this.neverCheckBox.enable();
            }

        },this)
        this.startDate.on('change',function(){
                var startdatefield = Wtf.getCmp('startdate'+this.id);
                var enddatefield = Wtf.getCmp('enddate'+this.id);
                var startdate=startdatefield.getValue();
                var enddate=enddatefield.getValue();
                if(startdate!=enddate){
                    enddatefield.setValue(startdate);
                }
        },this);
    },
    saveact:function(){
            var flag=Wtf.getCmp('treeactflag').getValue();
            var relatedname=this.accid;
            this.saveflag=true;
            var subObj=Wtf.getCmp('subject'+this.id);
            var subject = subObj.getValue();
            if(subject.trim()==""){
                subObj.setValue("");
                subObj.allowBlank=false;
                ResponseAlert(155);
                return;
            }
            var finalStr = createQuickinsertActivityJSON(this,'Account',relatedname,flag,subject);
            if(this.saveflag) {
                Wtf.commonWaitMsgBox("Saving data...");

                Wtf.Ajax.requestEx({
    //                url:Wtf.req.base + 'crm.jsp',
                    url: Wtf.req.springBase+'Activity/action/saveActivity.do',
                    params:{
                        jsondata:finalStr,
                        type:1,
                        flag:82
                    }
                },
                this,
                function(res) {
                    var temp=Wtf.getCmp('actquickinsert');
                    temp.close();
                    Wtf.updateProgress();
                    ResponseAlert(3);
                    var nnode=new Wtf.tree.AsyncTreeNode({
                        text:flag,
                        nodeid:res.ID,
                        leaf:true
                    });
                    this.tempnode.appendChild(nnode);
                    this.getLoader().baseParams={mode:'0',expandaccount:true,idexpand:this.accid};
                    this.getLoader().load(this.getRootNode());

                    var obj = Wtf.getCmp(Wtf.moduleWidget.activity);
                    if(obj!=null){
                        obj.callRequest("","",0);
                        Wtf.refreshUpdatesAll();
                    }
                },
                function(res) {
                    WtfComMsgBox(202,1);
                }
                )
            }
    },
    addopp:function(){
        var flag=0;
        this.form1 = new Wtf.quickadd({
                    dashcomp:Wtf.moduleWidget.opportunity,
                    configType:"Opportunity",
                    compid:"opportunitycomptree",
                    border: false,
                    paramObj:{flag:23,auditEntry:1},
                    url:Wtf.req.springBase+'Opportunity/action/saveOpportunities.do',
                    actionCode:4,
                    jsonstr:{oppid:'0',oppownerid:loginid,accountnameid:this.accid}
                })
        this.form1.on("closeform",function(){
            if(flag!=0)
            this.oppwin.setHeight(180+this.form1.objresponse.data.length*40);
        },this);

        this.form1.on("aftersave",function(res,jsondata,flag){
            this.oppwin.close();
            Wtf.updateProgress();
            var jobj = jsondata;
            var nnode=new Wtf.tree.AsyncTreeNode({
                text:jobj.oppname,
                nodeid:res.ID,
                leaf:true

            });
            this.getLoader().baseParams={mode:'0',expandaccount:true,idexpand:this.accid,expandopp:true};
            this.getLoader().load(this.getRootNode());
            var lgrid=Wtf.getCmp('OpportunityHomePanel');
            if(lgrid) {
                lgrid.EditorStore.reload();
            }
            lgrid=Wtf.getCmp('oppAccountTab'+this.accid);
            if(lgrid) {
                lgrid.EditorStore.reload();
            }
        },this);
        var oppName= this.tempnode.parentNode.attributes.text;
        this.oppwin=new Wtf.Window({
            height:260,
            width:400,
            id:'oppwinquickinsert',
            iconCls: "pwnd favwinIcon",
            title:"Opportunity",
            modal:true,
            shadow:true,
            resizable:false,
            buttonAlign:'right',
            buttons: [{
                text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                scope:this,
                handler:function(){
                    this.form1.saveobj();
                }
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                scope:this,
                handler:function(){
                    this.oppwin.close();
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml("Add Opportunity", "Add Opportunity for Account "+oppName,"../../images/opportunity1.gif")
            },{
                region : 'center',
                border : false,

                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
                layout : 'fit',
                items :[   this.form1   ]
            }]

        });
        flag=1;
        this.oppwin.show();
    },

   addcon:function()
    {
        var flag=0;
        this.form1 = new Wtf.quickadd({
                    dashcomp:Wtf.moduleWidget.contact,
                    configType:"Contact",
                    compid:"contactcomptree",
                    border: false,
                    paramObj:{flag:22,auditEntry:1},
                    url:Wtf.req.springBase+'Contact/action/saveContacts.do',
                    actionCode:3,
                    jsonstr:{contactid:'0',contactownerid:loginid ,accountid:this.accid}
                })
        this.form1.on("closeform",function(){
            if(flag!=0)
            this.win.setHeight(220+this.form1.objresponse.data.length*40);
        },this);

        this.form1.on("aftersave",function(res,jsondata,flag){
            this.win.close();
               Wtf.updateProgress();
                var jobj = jsondata;
                var nnode=new Wtf.tree.AsyncTreeNode({
                    text:jobj.firstname+" "+jobj.lastname,
                    nodeid:res.ID,
                    leaf:true
                });
                this.getLoader().baseParams={mode:'0',expandaccount:true,idexpand:this.accid,expandcontact:true};
                this.getLoader().load(this.getRootNode());
                var lgrid=Wtf.getCmp('ContactHomePanel');
                if(lgrid) {
                    lgrid.EditorStore.reload();
                }
                lgrid=Wtf.getCmp('contactAccountTab'+this.accid);
                if(lgrid) {
                    lgrid.EditorStore.reload();
                }
        },this);

        var conName= this.tempnode.parentNode.attributes.text;
        this.win=new Wtf.Window({
            height:250,
            width:400,
            id:'quickinsert',
            iconCls: "pwnd favwinIcon",
            title:WtfGlobal.getLocaleText("crm.CONTACT"),//"Contact",
            modal:true,
            shadow:true,
            resizable:false,
            buttonAlign:'right',
            buttons: [{
                text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                scope:this,
                handler:function(){
                    this.form1.saveobj();
                }
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                scope:this,
                handler:function(){
                    this.win.close()
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText("crm.CONTACT"), WtfGlobal.getLocaleText({key:"crm.contact.addcontact.tophtml",params:[conName]}),"../../images/contacts3.gif")
            },{
                region : 'center',
                border : false,

                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
                layout : 'fit',
                items :[   this.form1   ]
            }]

        });
        flag=1;
        this.win.show();


    },

    addcas:function () {
            var flag=0;
        this.form1 = new Wtf.quickadd({
                    dashcomp:Wtf.moduleWidget.cases,
                    configType:"Case",
                    compid:"casescomptree",
                    border: false,
                    paramObj:{flag:33},
                    url:Wtf.req.springBase+'Case/action/saveCases.do',
                    actionCode:5,
                    jsonstr:{caseid:'0',caseownerid:loginid,accountnameid:this.accid}
                })
        this.form1.on("closeform",function(){
             if(flag!=0)
            this.conwin.setHeight(200+this.form1.objresponse.data.length*40);
        },this);
        this.form1.on("aftersave",function(res,jsondata,flag){
            this.conwin.close();
            Wtf.updateProgress();
            var jobj = jsondata;
            var nnode=new Wtf.tree.AsyncTreeNode({
                text:jobj.casename,
                nodeid:res.ID,
                leaf:true
            });
            this.tempnode.appendChild(nnode);
            this.getLoader().baseParams={mode:'0',expandaccount:true,idexpand:this.accid,expandcase:true};
            this.getLoader().load(this.getRootNode());
            var lgrid=Wtf.getCmp('CaseHomePanel');
            if(lgrid) {
                lgrid.EditorStore.reload();
            }
            lgrid=Wtf.getCmp('accountCaseTab'+this.accid);
            if(lgrid) {
                lgrid.EditorStore.reload();
            }
        },this);
        var casName= this.tempnode.parentNode.attributes.text;
        this.conwin=new Wtf.Window({
            height:280,
            width:400,
            id:'conquickinsert',
            iconCls: "pwnd favwinIcon",
            title:WtfGlobal.getLocaleText("crm.CASE"),//"Case",
            modal:true,
            shadow:true,
            resizable:false,
            buttonAlign:'right',
            buttons: [{
                text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                scope:this,
                handler:function(){
                    this.form1.saveobj();
                }
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                scope:this,
                handler:function(){
                    this.conwin.close()
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText("crm.CASE"), WtfGlobal.getLocaleText({key:"crm.case.addcase.tophtml",params:[casName]}),"../../images/cases.gif")
            },{
                region : 'center',
                border : false,

                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
                layout : 'fit',
                items :[   this.form1   ]
            }]

        });
        flag=1;
        this.conwin.show();

    },

   
    popup:function(){
        var flag=0;
        this.accountPanel = new Wtf.quickadd({
                    dashcomp:Wtf.moduleWidget.account,
                    configType:"Account",
                    compid:"accountcomptree",
                    border: false,
                    paramObj:{flag:21},
                    url:Wtf.req.springBase+"Account/action/saveAccounts.do",
                    actionCode:2,
                    jsonstr:{accountid:'0',accountownerid:loginid }
                })
        this.accountPanel.on("aftersave",this.saveAccount,this);
        this.accountPanel.on("closeform",function(){
            if(flag!=0)
            this.win.setHeight(200+this.accountPanel.objresponse.data.length*40);
        },this);
        this.win=new Wtf.Window({
            height:200,
            width:400,
            id:'quickinsert',
            iconCls: "pwnd favwinIcon",
            title:WtfGlobal.getLocaleText("crm.ACCOUNT"),//"Account",
            modal:true,
            shadow:true,
            //resizable:false,
            buttonAlign:'right',
            buttons: [{
                text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                scope:this,
                handler:function(){
                    this.accountPanel.saveobj();
                }
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                scope:this,
                handler:function(){
                    this.win.close()
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText("crm.mymails.addaccount"), WtfGlobal.getLocaleText("crm.mymails.addaccount"),"../../images/accounts.gif")
            },{
                region : 'center',
                border : false,

                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
                layout : 'fit',
                items :[   this.accountPanel   ]
            }]

        });
        flag=1;
        this.win.show();
    },
    saveAccount:function(res,jsondata,flag){
        if(this.win!=null)
            this.win.close();
        var jobj = jsondata;
        if(typeof jsondata == "string")
            jobj = eval('('+jsondata+')');
        this.tempnode = this.getNodeById("accountnode");
        if(!this.tempnode.isExpanded()) {
            this.tempnode.expand();
        } else {
            var ID = jobj.accountid;
            if(res)
                ID = res.ID;
            var parent=this.getNodeById("accountnode");
            var child=parent.findChild('id',jobj.accountid);
            if(child!=null){
                child.setText(jobj.accountname);
                if(jobj.validflag && jobj.validflag==0) {
                    parent.removeChild(child);
                } else {
                    child.setText(jobj.accountname);
                }
            } else{
                var nnode=new Wtf.tree.AsyncTreeNode({
                    text:jobj.accountname,
                    nodeid:ID,
                    id:ID,
                    iconCls:'pwndCRM accounttreeicon',
                    mode:'11',
                    level:'1',
                    expanded:true
                });
               var childnode=new Wtf.tree.AsyncTreeNode({
                    text:WtfGlobal.getLocaleText("crm.ACTIVITY.plural"),//'Activities',
                    mapid:ID,
                    iconCls:'pwndCRM todolisttreepane',
                    mode:'1',
                    expanded:true,
                    leaf:true,
                    level:'1'
                });
                var childnode1=new Wtf.tree.AsyncTreeNode({
                    text:WtfGlobal.getLocaleText("crm.OPPORTUNITY.plural"),//'Opportunities',
                    mapid:ID,
                    iconCls:'pwndCRM opportunitytreeIcon',
                    mode:'2',
                    leaf:true,
                    expanded:true,
                    level:'1'
                });
                var childnode2=new Wtf.tree.AsyncTreeNode({
                    text:'Contacts',
                    mapid:ID,
                    iconCls:'pwndCRM contactstreeIcon',
                    mode:'3',
                    leaf:true,
                    expanded:true,
                    level:'1'
                });
                var childnode3=new Wtf.tree.AsyncTreeNode({
                    text:WtfGlobal.getLocaleText("crm.CASE.plural"),//'Cases',
                    mapid:ID,
                    iconCls:'pwndCRM casetreeIcon',
                    mode:'4',
                    leaf:true,
                    expanded:true,
                    level:'1'
                });

                var ibefore=this.getSelectionModel().select(this.tempnode.childNodes[0]);
                this.tempnode.insertBefore(nnode,ibefore);

                nnode.appendChild(childnode);
                nnode.appendChild(childnode1);
                nnode.appendChild(childnode2);
                nnode.appendChild(childnode3);
            }
            var txt=this.tempnode.attributes.text;
//                var arrObj=[];
//                var arrObj1=[];
//                var res=[] ;
//                arrObj=txt.split("(");
//                res=arrObj[1].split(")");
//                arrObj1=txt.split(")");
//                var estr=""
//                var ct;
//                for(var i=1;i<arrObj1.length;i++)
//                {
//                    estr=estr+")"+arrObj1[i];
//                }
//                ct=res[0];
//
//                if(ct>=10)
//                {
//                    var lastnode=this.getSelectionModel().select(this.tempnode.childNodes[10]);
//                    if(lastnode)
//                        this.tempnode.removeChild(lastnode);
//                }
//                ct=(ct*1)+1;
//                var newtext=arrObj[0]+"("+ct +estr;
//                var newtext=arrObj[0];
//                var newtext=txt;
//                if((res[0]*1)==10&&ct==11)
//                {
//                    var st=" <span class='treeSpan' onclick='addAccountTab()'>(More...)</span> ";
                var st=" <span class='treeSpan' onclick='addAccountTab()'></span> ";
                txt=txt+st;
//                }
            this.tempnode.setText(txt);
        }
        var lgrid=Wtf.getCmp('AccountHomePanel');
        if(flag!=1&&lgrid) {
            lgrid.EditorStore.reload();
        }
    },

    addleadact:function()
    {
   //     chktasktypeload();
        chktaskstatusload();
        var exportRecord = new Wtf.data.Record.create([
            {
                name: "name",
                mapping:'cname'
            },{
                name: "id",
                mapping:'cid'
            }
            ]);
            var exportRecordReader = new Wtf.data.KwlJsonReader1({
                root: "data",
                totalProperty: 'count'
            }, exportRecord);

           this.exportDS = new Wtf.data.Store({
                url: Wtf.calReq.cal + "getCalendarlist.do",
                reader: exportRecordReader,
                method: 'POST',
                baseParams: {
                    userid: loginid
                },
                autoLoad:false
            });

            this.exportDS.load();

            this.calendar=  new Wtf.form.ComboBox({
                fieldLabel: WtfGlobal.getLocaleText("crm.calendar.calendar"),//"Calendar",
                id:this.id+'calendarid',
                selectOnFocus:true,
                triggerAction: 'all',
                mode: 'local',
                store: this.exportDS,
                displayField: 'name',
                typeAhead: true,
                allowBlank:false,
                forceSelection:true,
                valueField:'id',
                msgTarget:'side',
                width: 230,
                value:Wtf.DEFAULT_CALENDAR
            });
        this.flagStore = new Wtf.data.SimpleStore({
            fields: ['id','name'],
            data : [
            ["Task","Task"],
            ["Event","Event"]
            ]
        });

        this.starttimeCombo = new Wtf.form.TimeField({
            fieldLabel: '',
            id: "starttime"+this.id,
            name: 'starttime1',
            minValue: WtfGlobal.setDefaultMinValueTimefield(),
            maxValue: WtfGlobal.setDefaultMaxValueTimefield(),
            labelSeparator:"",
            width: 230,
            format:WtfGlobal.getLoginUserTimeFormat(),
            value:WtfGlobal.setDefaultValueTimefield()

        })

        this.endtimeCombo = new Wtf.form.TimeField({
            fieldLabel: '',
            id: "endtime"+this.id,
            name: 'endtime1',
            labelSeparator:"",
            minValue: WtfGlobal.setDefaultMinValueTimefield(),
            maxValue: WtfGlobal.setDefaultMaxValueTimefield(),
            width: 230,
            format:WtfGlobal.getLoginUserTimeFormat(),
            value:WtfGlobal.setDefaultValueEndTimefield()

        })
        var scheduleTypeStore = new Wtf.data.SimpleStore({
            fields: ["id", "value"],
            data: [["0","Does not repeat"],["1", "Daily"], ["2", "Weekly"], ["3", "Monthly"]]
        });

        this.scheduleTypeCombo = new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("crm.spreadsheet.scheduletypecombo"),//"Schedule Type",
            store: scheduleTypeStore,
            displayField: "value",
            valueField: "id",
            width: 230,
            allowBlank: false,
            forceSelection:true,
            msgTarget:'side',
            mode: "local",
            id:this.id+"scheduleType",
            triggerAction: "all",
            emptyText:"--Please Select--"
        });

        this.allDayCheckBox= new Wtf.form.Checkbox({
            boxLabel:" ",
            name:'rectype',
            checked:false,
            inputValue:'false',
            width: 50,
            fieldLabel:"All Day"
        })

        this.subject = new Wtf.form.TextField({
                fieldLabel:WtfGlobal.getLocaleText("crm.case.defaultheader.subject")+'*',//'Subject *',
                id:'subject'+this.id,
                width:230,
                allowBlank:false,
                msgTarget:'side',
                maxLength:512,
                xtype:'striptextfield'
        })
        this.taskCheck=new Wtf.form.FieldSet({
            title: WtfGlobal.getLocaleText("crm.ACTIVITY"),//'Activity',
            autoHeight:true,
            defaultType: 'textfield',
            frame:false,
            labelWidth:90,
            items :[
                   this.flag=new Wtf.form.ComboBox({
                    fieldLabel: WtfGlobal.getLocaleText("crm.report.activity.taskorevent"),//'Task/Event',
                    id:this.id+'actflag',
                    selectOnFocus:true,
                    triggerAction: 'all',
                    mode: 'local',
                    store: this.flagStore,
                    displayField: 'name',
                    emptyText:WtfGlobal.getLocaleText("crm.goalsettings.combo.emptytxt"),//"-- Please Select --",
                    typeAhead: true,
                    allowBlank:false,
                    forceSelection:true,
                    valueField:'id',
                    msgTarget:'side',
                    width:230
            }),
            this.subject,
            this.startDate = new Wtf.form.DateField({
                fieldLabel:WtfGlobal.getLocaleText("crm.calendar.eventdetails.starttime"),//'Start Time',
                format:WtfGlobal.getOnlyDateFormat(),
                readOnly:true,
                id:'startdate'+this.id,
                allowBlank:false,
                msgTarget:'side',
                value:new Date(),
                width: 230
            }),
            this.starttimeCombo,
            this.endDate = new Wtf.form.DateField({
                fieldLabel:WtfGlobal.getLocaleText("crm.calendar.eventdetails.endtime"),//'End Time',
                format:WtfGlobal.getOnlyDateFormat(),
                readOnly:true,
                id:'enddate'+this.id,
                allowBlank:false,
                msgTarget:'side',
                value:new Date(),
                width: 230
            }),this.endtimeCombo,
            this.calendar,
            this.allDayCheckBox
           ]
        });


        this.accountCheck=new Wtf.form.FieldSet({
            title: WtfGlobal.getLocaleText("crm.common.reccurence"),//'Recurrence',
            autoHeight:true,
            defaultType: 'textfield',
            frame:false,
            labelWidth:90,
            items :[
            this.scheduleTypeCombo,
            this.neverCheckBox= new Wtf.form.Checkbox({
                boxLabel:" ",
                name:'rectype',
                checked:true,
                inputValue:'false',
                width: 50,
                fieldLabel:WtfGlobal.getLocaleText("crm.common.endsnever")//"Ends Never"
            }),this.untilDate = new Wtf.form.DateField({
                fieldLabel:WtfGlobal.getLocaleText("crm.common.tilldate"),//'Till Date',
                format:WtfGlobal.getOnlyDateFormat(),
                readOnly:true,
                id:'tilldat'+this.id,
                allowBlank:false,
                msgTarget:'side',
                width: 230
            })
           ]
        });

        this.form1=new Wtf.form.FormPanel({
            border:false,
            items:[

            this.taskCheck,
            this.accountCheck

            ]

        });
        var actName= this.tempnode.parentNode.attributes.text;
        this.win=new Wtf.Window({
            height:540,
            width:430,
            iconCls: "pwnd favwinIcon",
            title:WtfGlobal.getLocaleText("crm.ACTIVITY"),//"Activity",
            modal:true,
            id:'quickinsert',
            shadow:true,
            resizable:false,
            buttonAlign:'right',
            buttons: [{ text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                        scope:this,
                        handler:function(){
                    if(this.form1.getForm().isValid()) {
                        this.saveleadact();

                    } else{
                        ResponseAlert(152);
                    }
                }
            },{
                text: WtfGlobal.getLocaleText("crm.CLOSE"),//,
                scope:this,
                handler:function(){
                    this.fireEvent('close');
                    this.win.close();
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText("crm.ACTIVITY"),WtfGlobal.getLocaleText({key:"crm.ACTIVITY",params:[accName]}) ,"../../images/activity1.gif")
            },{
                region : 'center',
                border : false,

                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 20px 10px 20px',
                layout : 'fit',
                items :[   this.form1   ]
            }]

        });
        this.win.show();
        this.flag.setValue("Event");
        this.scheduleTypeCombo.setValue("0");
        this.untilDate.disable();
        this.neverCheckBox.disable();
        this.allDayCheckBox.on('check',function(){
                if(this.allDayCheckBox.getValue()){
                    this.starttimeCombo.disable();
                    this.starttimeCombo.setValue("");
                    this.endtimeCombo.disable();
                    this.endtimeCombo.setValue("");
                }
                else {
                    this.starttimeCombo.enable();
                    this.starttimeCombo.setValue("8:00 AM");
                    this.endtimeCombo.enable();
                    this.endtimeCombo.setValue("9:00 AM");
                }
        },this);

        this.neverCheckBox.on('check',function(){
            if(this.neverCheckBox.getValue()){
                this.untilDate.disable();
                this.untilDate.setValue("");
            } else{
                this.untilDate.enable();
                this.untilDate.setValue(new Date());
            }
        },this);
        this.scheduleTypeCombo.on("select",function(a,b,c){
            if(c==0){
               this.untilDate.disable();
               this.untilDate.setValue("");
               this.neverCheckBox.disable();
            } else {
               if(!this.neverCheckBox.getValue()){
                 this.untilDate.enable();
               }
               this.neverCheckBox.enable();
            }

        },this)
        this.startDate.on('change',function(){
                var startdatefield = Wtf.getCmp('startdate'+this.id);
                var enddatefield = Wtf.getCmp('enddate'+this.id);
                var startdate=startdatefield.getValue();
                var enddate=enddatefield.getValue();
                if(startdate!=enddate){
                    enddatefield.setValue(startdate);
                }
        },this);
    },

    saveleadact:function() {
            var flag=Wtf.getCmp('treeactflag').getValue();
            var relatedname=this.accid;
            this.saveflag=true;
            var subObj=Wtf.getCmp('subject'+this.id);
            var subject = subObj.getValue();
            if(subject.trim()==""){
                subObj.setValue("");
                subObj.allowBlank=false;
                ResponseAlert(155);
                return;
            }
            var finalStr = createQuickinsertActivityJSON(this,'Lead',relatedname,flag,subject);
            if(this.saveflag){
                Wtf.commonWaitMsgBox("Saving data...");
                Wtf.Ajax.requestEx({
    //                url:Wtf.req.base +'crm.jsp',
                    url: Wtf.req.springBase+'Activity/action/saveActivity.do',
                    params:{
                        jsondata:finalStr,
                        type:1,
                        flag:82
                    }
                },
                this,
                function(res) {

                    var temp=Wtf.getCmp('quickinsert');
                    temp.close();
                    Wtf.updateProgress();
                    ResponseAlert(8);

                    var nnode=new Wtf.tree.AsyncTreeNode({
                        text:flag,
                        nodeid:res.ID,
                        leaf:true
                     });
                    if(!this.tempnode.isExpanded())
                        this.tempnode.expand();
                    this.tempnode.appendChild(nnode);
                    this.getLoader().baseParams={mode:'0',expandlead:true,idexpand:this.accid,expandleadact:true};
                    this.getLoader().load(this.getRootNode());
                },
                function(res) {
                    WtfComMsgBox(202,1);
                })
            }
            
    },

    popuplead:function(){
        var flag=0;
        this.leadPanel = new Wtf.quickadd({
                    dashcomp:Wtf.moduleWidget.lead,
                    configType:"Lead",
                    compid:"leadcomptree",
                    border: false,
                    paramObj:{flag:20,auditEntry:1},
                    url: Wtf.req.springBase+'Lead/action/saveLeads.do',
                    actionCode:1,
                    jsonstr:{leadid:'0',leadownerid:loginid}

        })
        this.leadPanel.on("aftersave",this.saveLead,this);
        this.leadPanel.on("closeform",function(){
                if(flag!=0)
            this.win.setHeight(180+(this.leadPanel.objresponse.data.length>3?this.leadPanel.objresponse.data.length:3)*30);
        },this);
        this.win=new Wtf.Window({
            height:240,
            width:400,
            id:'quickinsert',
            iconCls: "pwnd favwinIcon",
            title:WtfGlobal.getLocaleText("crm.LEAD"),//"Lead",
            modal:true,
            shadow:true,
          //  resizable:false,
            buttonAlign:'right',
            buttons: [{
                text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                scope:this,
                handler:function(){
                    this.leadPanel.saveobj();
                }
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                scope:this,
                handler:function(){
                    this.win.close()
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml("Add Lead", "Add Lead","../../images/leads.gif")
            },{
                region : 'center',
                border : false,
                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
                layout : 'fit',
                items :[   this.leadPanel   ]
            }]

        });
        flag=1;
        this.win.show();
    },
    saveLead:function(res,jsondata,flag){
        if(this.win!=null)
            this.win.close();
        var jobj = jsondata;
        if(typeof jsondata == "string")
            jobj = eval('('+jsondata+')');
        this.tempnode = this.getNodeById("leadnode");
        if(!this.tempnode.isExpanded()) {
                this.tempnode.expand();
        } else {
            var ID = jobj.leadid;
            if(res)
                ID = res.ID;
            var child=this.tempnode.findChild('id',jobj.leadid);
            if(child!=null){
                if(jobj.validflag && jobj.validflag==0) {
                    this.tempnode.removeChild(child);
                } else
                    child.setText(jobj.lastname);
            }else if(jobj.validflag==1) {
                var nnode=new Wtf.tree.AsyncTreeNode({
                    text:jobj.lastname,
                    accid:ID,
                    id:ID,
                    iconCls:'pwndCRM leadtreeicon',
                    mode:'5',
                    level:'2',
                    leaf:false,
                    expanded:true
                });
                var childnode=   new Wtf.tree.AsyncTreeNode({
                    text:WtfGlobal.getLocaleText("crm.ACTIVITY.plural"),//'Activities',
                    mapid:ID,
                    iconCls:'pwndCRM todolisttreepane',
                    mode:'8',
                    leaf:true,
                    level:'2',
                    expanded:true
                });
                var ibefore=this.getSelectionModel().select(this.tempnode.childNodes[0]);
                this.tempnode.insertBefore(nnode,ibefore);
                nnode.appendChild(childnode);
            }
            var txt=this.tempnode.attributes.text;
//                var arrObj=[];
//                var arrObj1=[];
//                var res=[];
//                arrObj=txt.split("(");
//                res=arrObj[1].split(")");
//                arrObj1=txt.split(")");
//                var estr=""
//                var ct;
//                for(var i=1;i<arrObj1.length;i++)
//                {
//                    estr=estr+")"+arrObj1[i];
//                }
//                ct=res[0];
//                if(ct>=10)
//                {
//                    var lastnode=this.getSelectionModel().select(this.tempnode.childNodes[10]);
//                    if(lastnode)
//                        this.tempnode.removeChild(lastnode);
//                }
//                ct=(ct*1)+1;
//              //  var newtext=arrObj[0]+"("+ct +estr;
//                var newtext=arrObj[0];
//                if((res[0]*1)==10&&ct==11)
//                {
            //    var st=" <span class='treeSpan' onclick='addLeadTab()'>(More...)</span> ";
                var st=" <span class='treeSpan' onclick='addLeadTab()'></span> ";
                txt=txt+st;
//                }
            this.tempnode.setText(txt);
        }
        var lgrid=Wtf.getCmp('LeadHomePanel');
        if(flag!=1&&lgrid)
        {
            lgrid.EditorStore.reload();
        }
    },
    popupproduct:function(){
        var flag=0;
        this.ProductPanel = new Wtf.quickadd({
                    dashcomp:Wtf.moduleWidget.product,
                    configType:"Product",
                    compid:"productcomptree",
                    border: false,
                    paramObj:{flag:31,auditEntry:1},
                    url:Wtf.req.springBase+'Product/action/saveProducts.do',
                    actionCode:7,
                    jsonstr:{productid:'0',ownerid:loginid}
                })
        this.ProductPanel.on("aftersave",this.saveProduct,this);
        this.ProductPanel.on("closeform",function(){
            if(flag!=0)
            this.prowin.setHeight(180+this.ProductPanel.objresponse.data.length*40);
        },this);
           this.prowin=new Wtf.Window({
            height:210,
            width:400,
            id:'proquickinsert',
            iconCls: "pwnd favwinIcon",
            title:"Product",
            modal:true,
            shadow:true,
           // resizable:false,
            buttonAlign:'right',
            buttons: [{
                text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                scope:this,
                handler:function(){
                    this.ProductPanel.saveobj();
                }
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                scope:this,
                handler:function(){
                    this.prowin.close()
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml("Add Product", "Add Product","../../images/Products.gif")
            },{
                region : 'center',
                border : false,

                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
                layout : 'fit',
                items :[   this.ProductPanel   ]
            }]
        });
        flag=1;
        this.prowin.show();
    },
    saveProduct:function(res,jsondata,flag){
        if(this.prowin!=null)
            this.prowin.close();
        var jobj = jsondata;
        if(typeof jsondata == "string")
            jobj = eval('('+jsondata+')');
        var tnode = this.getNodeById("productnode");
        if(!tnode.isExpanded()) {
                tnode.expand();
        } else {
            var child=tnode.findChild('id',jobj.productid);
            if(child!=null) {
                if(jobj.validflag && jobj.validflag==0) {
                    tnode.removeChild(child);
                } else {
                    child.setText(jobj.pname);
                }
            } else if(jobj.validflag==1) {
                var ID = jobj.productid;
                if(res)
                    ID = res.ID;
                var nnode=new Wtf.tree.AsyncTreeNode({
                    text:jobj.pname,
                    accid:ID,
                    id:ID,
                    iconCls:'pwndCRM producttreeicon',
                    mode:'6',
                    level:'3',
                    leaf:true,
                    expanded:true
                });
                var node=this.getRootNode();
                //var mnode=node.findChild('mode','0');
                var ibefore=this.getSelectionModel().select(tnode.childNodes[0]);
                tnode.insertBefore(nnode,ibefore);
                tnode.expand(true);
            }
            var txt=tnode.attributes.text;
//                var arrObj=[];
//                var arrObj1=[];
//                var res=[];
//                arrObj=txt.split("(");
//                res=arrObj[1].split(")");
//                arrObj1=txt.split(")");
//                var estr=""
//                var ct;
//                for(var i=1;i<arrObj1.length;i++)
//                {
//                    estr=estr+")"+arrObj1[i];
//                }
//                ct=res[0];
//                if(ct>=10)
//                {
//                    var lastnode=this.getSelectionModel().select(tnode.childNodes[10]);
//                    if(lastnode)
//                        tnode.removeChild(lastnode);
//                }
//                ct=(ct*1)+1;
//               // var newtext=arrObj[0]+"("+ct +estr;
//                var newtext=arrObj[0];
//                if((res[0]*1)==10&&ct==11)
//                {
                var st=" <span class='treeSpan' onclick='addProductMasterTab()'></span> ";
                txt=txt+st;
//                }
            tnode.setText(txt);
        }
        var lgrid=Wtf.getCmp('ProductHomePanel');
        if(flag!=1&&lgrid) {
            lgrid.EditorStore.reload();
        }
    },
    
    saveDocument:function(res,fname){
        var tnode = this.getNodeById("documentnode");
        if(tnode){
            if(!tnode.isExpanded())
                tnode.expand();
            var nnode=new Wtf.tree.AsyncTreeNode({
                text:fname,
                nodeid:res,
                iconCls:'pwndCRM doctreeicon',
                mode:'25',
                level:'5',
                leaf:true
            });
            var ibefore=this.getSelectionModel().select(tnode.childNodes[0]);
            tnode.insertBefore(nnode,ibefore);
            var txt=tnode.attributes.text;
            var st=" <span class='treeSpan' onclick='loadDocumentPage()'></span> ";
            txt=txt+st;
            tnode.setText(txt);
        }
    },
    popupcamp:function(){
        var flag=0;
        this.campPanel = new Wtf.quickadd({
                    dashcomp:Wtf.moduleWidget.campaign,
                    configType:"Campaign",
                    compid:"campcomptree",
                    border: false,
                    paramObj:{flag:20,auditEntry:1},
                    url:Wtf.req.springBase+'Campaign/action/saveCampaigns.do',
                    actionCode:0,
                    jsonstr:{isCampaignNameEdit:true, campaignid:'0',campaignownerid:loginid}

                })
        this.campPanel.on("aftersave",this.savecamp,this);
        this.campPanel.on("closeform",function(){
                if(flag!=0)
            this.win.setHeight(180+this.campPanel.objresponse.data.length*40);
    },this);
        this.win=new Wtf.Window({
            height:270,
            width:400,
            id:'quickinsert',
            iconCls: "pwnd favwinIcon",
            title:WtfGlobal.getLocaleText("crm.CAMPAIGN"),//"Campaign",
            modal:true,
            shadow:true,
            resizable:false,
            buttonAlign:'right',
            buttons: [{
                text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                scope:this,
                handler:function(){
                    this.campPanel.saveobj();
                }
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                scope:this,
                handler:function(){
                    this.win.close()
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText("crm.campaign.createcampaignwin.tophtml.title"), WtfGlobal.getLocaleText("crm.campaign.createcampaignwin.tophtml.title"),"../../images/Campaigns.gif")
            },{
                region : 'center',
                border : false,

                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
                layout : 'fit',
                items :[   this.campPanel   ]
            }]

        });
         flag=1;
        this.win.show();
    },
    savecamp:function(res,jsondata,flag)
    {
        if(this.win!=null)
            this.win.close();
        var jobj = jsondata;
        if(typeof jsondata == "string")
            jobj = eval('('+jsondata+')');
        this.tempnode = this.getNodeById("campaignnode");
        if(!this.tempnode.isExpanded()) {
            this.tempnode.expand();
        } else {
            var child=this.tempnode.findChild('id',jobj.campaignid);
            if(child!=null){
                child.setText(jobj.campaignname);
                if(jobj.validflag && jobj.validflag==0) {
                    this.tempnode.removeChild(child);
                }
            }else if(jobj.validflag==1) {
                var ID = jobj.productid;
                if(res)
                    ID = res.ID;
                var nnode=new Wtf.tree.AsyncTreeNode({
                    text:jobj.campaignname,
                    accid:ID,
                    id:ID,
                    iconCls:'pwndCRM campaigntreeicon',
                    mode:'7',
                    leaf:true,
                    expanded:true
                });
                var ibefore=this.getSelectionModel().select(this.tempnode.childNodes[0]);
                this.tempnode.insertBefore(nnode,ibefore);
                this.tempnode.expand(true);
            }
            var txt=this.tempnode.attributes.text;
//                var arrObj=[];
//                var arrObj1=[];
//                var res=[];
//                arrObj=txt.split("(");
//                res=arrObj[1].split(")");
//                arrObj1=txt.split(")");
//                var estr=""
//                var ct;
//                for(var i=1;i<arrObj1.length;i++) {
//                    estr=estr+")"+arrObj1[i];
//                }
//                ct=res[0];
//                if(ct>=10) {
//                    var lastnode=this.getSelectionModel().select(this.tempnode.childNodes[10]);
//                    if(lastnode)
//                        this.tempnode.removeChild(lastnode);
//                }
//                ct=(ct*1)+1;
//
//              //  var newtext=arrObj[0]+"("+ct +estr;
//                var newtext=arrObj[0];
//                if((res[0]*1)==10&&ct==11)
//                {
                var st=" <span class='treeSpan' onclick='addCampaignTab()'></span> ";
                txt=txt+st;
//                }
            this.tempnode.setText(txt);
        }
        var lgrid=Wtf.getCmp('CampaignHomePanel');
        if(flag!=1&&lgrid) {
            lgrid.EditorStore.reload();
        }
    },

    afterRender : function() {
        Wtf.AccountTree.superclass.afterRender.call(this);
        this.getLoader().on("beforeload", function(treeLoader, node) {
            if(node.attributes.mode==undefined) {
                this.getLoader().baseParams.mode=0;
            }
            else {
                this.getLoader().baseParams.mode = node.attributes.mode;
                this.getLoader().baseParams.mapid = node.attributes.mapid;
            }
        }, this);
    },

    openTab : function(node) {
        var actNode = null;
        if(node.attributes.mode != undefined && node.attributes.mode != '') {
            actNode = node;
        } else {
            actNode = node.parentNode;
        }
        var modeval = parseInt(actNode.attributes.mode);
        var obj={};
        if(modeval>0) {
            obj['text'] = actNode.parentNode.text;
        } else {
        }
        obj['mode'] = modeval;
        obj['accid'] = actNode.attributes.mapid;
        this.openMenuLink(obj,node);
    },

    openMenuLink : function(obj,node) {
        switch(obj.mode) {
            case 11 :
                var panel=Wtf.getCmp('AccountTabPanID');
                if(panel==null) {
                   addAccountTab(node.attributes.id);
                }
                break;
            case 1 :
                var accpanel=Wtf.getCmp('AccountHomePanel');
                if(accpanel==null) {
                   addAccountTab();
                }
                var tabid = 'accountActTab'+obj.accid;
                var title = Wtf.util.Format.ellipsis(obj.text+"'s Activity",18);
                panel=Wtf.getCmp(tabid);
               if(panel==null)
                {
                    panel= new Wtf.activityEditor({
                        title:"<div wtf:qtip=\""+ obj.text+"'s Activity"+"\"wtf:qtitle='Accounts'>"+title+"</div>",
                        id : tabid,
                        layout : 'fit',
                        border : false,
                        closable : true,
                        scope : this,
                        urlFlag : 150,
                        Node:node,
                        mapid:obj.accid,
                        iconCls : getTabIconCls(Wtf.etype.todo),
                        Rrelatedto:'Account',
                        relatedtonameid : obj.accid,
                        newFlag : 2,
                        highLightId:node.attributes.nodeid,
                        RelatedRecordName:obj.text,
                        mainTab:Wtf.getCmp('AccountHomePanel').mainTab,
                        modName : "AccountActivity",
                        customParentModName : "Activity"
                    });

                     Wtf.getCmp('AccountHomePanel').addExternalPanel(panel);
                } else {
                     Wtf.highLightGlobal(node.attributes.nodeid,panel.EditorGrid,panel.EditorStore,"activityid");
                }
                    mainPanel.setActiveTab(Wtf.getCmp('AccountHomePanel'));
                    Wtf.getCmp('AccountHomePanel').mainTab.setActiveTab(panel);
                    Wtf.getCmp('AccountHomePanel').doLayout();
                break;
            case 2 :
                var accpanel=Wtf.getCmp('AccountHomePanel');
                if(accpanel==null) {
                   addAccountTab();
                }
                tabid = 'oppAccountTab'+obj.accid;
                title = Wtf.util.Format.ellipsis(obj.text+"'s Opportunities",18);
                panel=Wtf.getCmp(tabid);
                if(panel==null)
                {
                    panel= new Wtf.opportunityEditor({
                        title:"<div wtf:qtip=\""+ obj.text+"'s Opportunities"+"\"wtf:qtitle='Accounts'>"+ title+"</div>",
                        id : tabid,
                        layout : 'fit',
                        newFlag:2,
                        border : false,
                        closable : true,
                        iconCls : getTabIconCls(Wtf.etype.opportunity),
                        scope : this,
                        urlFlag : 62,
                        Node:node,
                        mapid : obj.accid,
                        relatedName:"Account",
                        modName : "AccountOpportunity",
                        RelatedRecordName:obj.text,
                        highLightId:node.attributes.nodeid,
                        submainTab:Wtf.getCmp('AccountHomePanel').mainTab,
                        subTab:true,
                        customParentModName : "Opportunity",
                        archivedParentName : "Opportunity"
                    });
                    Wtf.getCmp('AccountHomePanel').addExternalPanel(panel);
                } else {
                     Wtf.highLightGlobal(node.attributes.nodeid,panel.EditorGrid,panel.EditorStore,"oppid");
                }
                    mainPanel.setActiveTab(accpanel);
                    Wtf.getCmp('AccountHomePanel').mainTab.setActiveTab(panel);
                    Wtf.getCmp('AccountHomePanel').doLayout();
                break;
            case 3 :
                var accpanel=Wtf.getCmp('AccountHomePanel');
                if(accpanel==null) {
                   addAccountTab();
                }
                tabid = 'contactAccountTab'+obj.accid;
                title = Wtf.util.Format.ellipsis(obj.text+"'s Contacts",18);
                panel=Wtf.getCmp(tabid);
                if(panel==null)
                {
                    panel= new Wtf.contactEditor({
                        title:"<div wtf:qtip=\""+ obj.text+"'s Contacts"+"\"wtf:qtitle='Accounts'>"+title+"</div>",
                        id:tabid,
                        scope:this,
                        closable : true,
                        Node:node,
                        mapid:obj.accid,
                        newFlag:2,
                        iconCls:getTabIconCls(Wtf.etype.contacts),
                        urlFlag:60,
                        highLightId:node.attributes.nodeid,
                        relatedName:"Account",
                        modName : "AccountContact",
                        customParentModName : "Contact",
                        RelatedRecordName:obj.text,
                        layout:'fit',
                        submainTab:Wtf.getCmp('AccountHomePanel').mainTab,
                        subTab:true
                    });
                    Wtf.getCmp('AccountHomePanel').addExternalPanel(panel);
                } else {
                     Wtf.highLightGlobal(node.attributes.nodeid,panel.EditorGrid,panel.EditorStore,"contactid");
                }
                    mainPanel.setActiveTab(accpanel);
                    Wtf.getCmp('AccountHomePanel').mainTab.setActiveTab(panel);
                    Wtf.getCmp('AccountHomePanel').doLayout();
                break;
            case 4 :
                var accpanel=Wtf.getCmp('AccountHomePanel');
                if(accpanel==null) {
                   addAccountTab();
                }
                tabid = 'accountCaseTab'+obj.accid;
                title = Wtf.util.Format.ellipsis(obj.text+"'s Cases",18);
                panel=Wtf.getCmp(tabid);
                if(panel==null) {
                    panel= new Wtf.caseEditor({
                        border : false,
                        title:"<div wtf:qtip=\""+ obj.text+"'s Cases"+"\"wtf:qtitle='Accounts'>"+title+"</div>",
                        layout : 'fit',
                        closable : true,
                        scope : this,
                        urlFlag : 64,
                        Node:node,
                        accid : obj.accid,
                        id : tabid,
                        iconCls : getTabIconCls(Wtf.etype.cases),
                        newFlag : 2,
                        highLightId:node.attributes.nodeid,
                        relatedName:"Account",
                        modName : "AccountCase",
                        customParentModName : "Case",
                        RelatedRecordName:obj.text,
                        submainTab:Wtf.getCmp('AccountHomePanel').mainTab,
                        subTab:true,
                        archivedParentName : "Case"
                    });
                    Wtf.getCmp('AccountHomePanel').addExternalPanel(panel);
                } else {
                     Wtf.highLightGlobal(node.attributes.nodeid,panel.EditorGrid,panel.EditorStore,"caseid");
                }
                    mainPanel.setActiveTab(accpanel);
                    Wtf.getCmp('AccountHomePanel').mainTab.setActiveTab(panel);
                    Wtf.getCmp('AccountHomePanel').doLayout();
                break;
            case 5 :
                panel=Wtf.getCmp('LeadTabPanID');
                if(panel==null) {
                    addLeadTab(node.attributes.accid);
                }
                break;
            case 6 :
                panel=Wtf.getCmp('ProductTabPanID');
                if(panel==null) {
                    addProductMasterTab(node.attributes.accid);
                }
                break;
            case 7 :
                panel=Wtf.getCmp('CampaignTabPanID');
                if(panel==null) {
                    addCampaignTab(node.attributes.accid);
                }
                break;
            case 8 :
                var leadpanel=Wtf.getCmp('LeadHomePanel');
                if(leadpanel==null) {
                    addLeadTab(node.attributes.accid);
                }
                tabid = 'LeadTabPanID'+obj.accid;
                title = Wtf.util.Format.ellipsis(obj.text+"'s Activity",18);
                panel=Wtf.getCmp(tabid);
                if(panel==null) {
                    panel= new Wtf.activityEditor({
                        title:"<div wtf:qtip=\""+ obj.text+"'s Activity"+"\"wtf:qtitle='Leads'>"+title+"</div>",
                        id : tabid,
                        layout : 'fit',
                        border : false,
                        closable : true,
                        scope : this,
                        urlFlag : 151,
                        Node:node,
                        mapid:obj.accid,
                        Rrelatedto:'Lead',
                        iconCls : getTabIconCls(Wtf.etype.todo),
                        relatedtonameid : obj.accid,
                        newFlag : 2,
                        highLightId:node.attributes.nodeid,
                        mainTab:Wtf.getCmp('LeadHomePanel').mainTab,
                        RelatedRecordName:obj.text,
                        customParentModName : "Activity",
                        modName : "AccountActivity"
                    });
                    Wtf.getCmp('LeadHomePanel').addExternalPanel(panel);
                } else {
                     Wtf.highLightGlobal(node.attributes.nodeid,panel.EditorGrid,panel.EditorStore,"activityid");
                }
                    mainPanel.setActiveTab(leadpanel);
                    Wtf.getCmp('LeadHomePanel').mainTab.setActiveTab(panel);
                    Wtf.getCmp('LeadHomePanel').doLayout();
                break;
            case 25:
                panel=Wtf.getCmp('doc-mydocs');
                if(panel==null) {
                    if(node.text == " My Documents") {
                        loadDocumentPage();
                    }
                } else if (node.attributes.tag) {
                    var tabpanel = Wtf.getCmp("tabdocument");
                    var docPanel = Wtf.getCmp('doc-mydocs');
                    this.nodetext=node.text
                    mainPanel.activate(tabpanel);
                    docPanel.clrfilterBttn.enable();

                    var docStore = docPanel.grid1.getStore();
                    var docGrid = docPanel.grid1;
                    docPanel.quickSearchTF.setValue("");
                    docPanel.tagSearchTF.setValue(node.text);
                    docPanel.toolbar.searchField=docPanel.tagSearchTF;
                    docPanel.searchType.setValue(0);
                    docPanel.hideShowColumns(false);
                    docGrid.getView().refresh();
                    docStore.baseParams.searchType=docPanel.searchType.getValue();

                    docGrid.getStore().reload({
                        params:{
                            start:0,
                            limit:20,
                            tag:node.text
                        }
                    });
                    var spanele;
                    var div = docPanel.divele;
                    div.innerHTML = "";
                    div.style.display = 'block';
                    docPanel.editable = 1;
                    docPanel.enablefilter=1;
                    spanele = document.createElement("span");
                    spanele.className = 'spanelement';
                    spanele.innerHTML = 'Searched by Tag name :'+this.nodetext;
                    spanele.id = 'span' + 1;
                    spanele.style.color = "#15428b";
                    div.appendChild(spanele);
                }
                break;
        }
    }
});

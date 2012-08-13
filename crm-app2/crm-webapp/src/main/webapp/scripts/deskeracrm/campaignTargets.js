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
Wtf.campaignTargetList=function(config){
    Wtf.campaignTargetList.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.campaignTargetList,Wtf.Panel,{
    closable:true,
    layout: 'fit',
    border:false,
    iconCls:'targetlistIcon',
    initComponent:function(config){
        Wtf.campaignTargetList.superclass.initComponent.call(this,config);
        this.getNorthGrid();
        this.getCenterGrid();        
        this.main= new Wtf.Panel({
            layout:'border',
            border:false,
            items:[this.northGrid,this.centerGrid]
        });
        this.add(this.main);
    },

    getNorthGrid:function(){
        this.getTargetBarConfig();
        var record1 = new Wtf.data.Record.create([
            {name:'listid'},
            {name:'ctid'},
            {name:'listname'},
            {name:'description'},
            {name:'createdon', type:'date',dateFormat:'time'}
        ]);
        var reader1 = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },record1);
        this.store1 = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getCampaignTarget.do',
            baseParams:{
                flag:21,
                campID:this.campaignid
            },
            method:'post',
            reader:reader1
        });
        this.store1.load({params:{start:0,limit:15}});
        var column1 = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.CheckboxSelectionModel(),
            {
                header:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.targetlistBTN"),//'Target List',
                dataIndex:'listname'
            },{
                header:WtfGlobal.getLocaleText("crm.account.defaultheader.desc"),//'Description',
                dataIndex:'description'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon"),//'Created On',
                dataIndex:'createdon',
                renderer:WtfGlobal.onlyDateRendererTZ
        }]);
        var selectionModel = new Wtf.grid.CheckboxSelectionModel({singleSelect:true});
        this.northGrid = new Wtf.KwlGridPanel({
            store: this.store1,
            region:'north',
            height:200,
            cm: column1,
            sm: selectionModel,
            border : false,
            loadMask : true,
            displayInfo:true,
            viewConfig: {
                forceFit:true,
                emptyText:'<div style="padding-left:20px;">Looks like you have not added any Target List. Here are some tips to get started.<br>\n\
                           <ul><li type="disc">Add a pre-defined \'Target List\' by selecting it from the \'Add more Targets\' drop-down and clicking \'Add\' button</li>\n\
                           <li type="disc">Create a new Target List by clicking \'Create Target List\'</li><ul></div> '
            },
            tbar:['-',WtfGlobal.getLocaleText("crm.targetlists.addmoretargetlists")+':',this.targetListCombo,'-',this.addTargetListBtn,this.delTargetBtn, '-', this.NewTargetListBtn, this.ViewTargetListBtn],
            searchEmptyText:WtfGlobal.getLocaleText("crm.targetlist.search.mtytxt"),//"Search by Target List",
            serverSideSearch : true,
            searchField:"listname"
        });

        this.northGrid.selModel.on('selectionchange', this.recordSelect, this);
    },

    getCenterGrid:function(){
        var record2 = new Wtf.data.Record.create([
                {name:'id'},
                {name:'name'},
                {name:'emailid'},
                {name:'phone'},
                {name:'relatedto'},
                {name:'relatedid'}
        ]);

        var reader2 = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },record2);

        this.store2 = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getTargetListTargets.do',
            method:'post',
            reader:reader2
        });
        var checkBoxSM =  new Wtf.grid.CheckboxSelectionModel();
        var column2 = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
            {
                header: WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),//'Name',
                dataIndex:'name'
            },{
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.email"),//'Email',
                dataIndex:'emailid',
                renderer:WtfGlobal.renderEmailTo
            },{
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.phone"),//'Phone',
                dataIndex:'phone',
                renderer:WtfGlobal.renderContactToCall
            }
            ]);
        
        this.centerGrid = new Wtf.grid.GridPanel({
            store: this.store2,
            cm: column2,
            region:'center',
            sm : checkBoxSM,
            border : false,
            loadMask : true,
            searchEmptyText: WtfGlobal.getLocaleText("crm.mtytext.searchbyname"),//"Search by Name",
            searchField:"name",
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("crm.targetlists.detailgrid.emtytxt")//'Select a target list to view its details here.'
            },
            bbar :this.pag=new Wtf.PagingToolbar({
                pageSize: 15,
                border : false,
                id : "paggintoolbar"+this.id,
                store: this.store2,
                plugins : this.pPageSizeObj = new Wtf.common.pPageSize({
                    id : "pPageSize_"+this.id
                }),
                autoWidth : true,
                displayInfo:true,
                displayMsg: 'Displaying targets {0} - {1} of {2}',
                emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg")//"No results to display"
            })
        });
        
    },
    recordSelect:function(sm){
        var s = sm.getSelections();
        var lid = "";
        if(s.length==1){
            lid = sm.getSelected().get('listid');
        }
        this.store2.baseParams = {listID:lid,flag:7};
        this.store2.load({
            params:{
                start:0,
                limit:15
            }
            });
       if(s.length==1 && this.store2.getCount()<1){
           this.centerGrid.getView().emptyText=WtfGlobal.getLocaleText("crm.emailmarketing.urlreport.mtygrid.watermark");//"There is no record to show";
       } else{
           this.centerGrid.getView().emptyText=WtfGlobal.getLocaleText("crm.targetlists.detailgrid.emtytxt");//'Select a target list to view its details here.';
       }
    },
    getTargetBarConfig:function(){

        this.ViewTargetListBtn= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.targetlists.viewtargetlistBTN"),//"View Target List",
            scope:this,
            tooltip:{text:WtfGlobal.getLocaleText("crm.targetlists.viewtargetlistBTN.ttip")},//'Define the list of recipients for your campaign configurations. Import e-mail addresses from leads, contacts or targets easily.'},
            iconCls:"targetlistIcon",
            handler:this.viewTargetListHandler
        });

        this.NewTargetListBtn= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.targetlists.createnewlist"),//"Create new Target List",
            tooltip:{text:WtfGlobal.getLocaleText("crm.targetlists.toptoolbar.newBTN.ttip")},//'Add New Target List.'},
            scope:this,
            iconCls:"pwnd newTargetListEmailMarketing",
            handler:this.newTargetListHandler
        });

        this.targetRecord = new Wtf.data.Record.create([
                {name:'listid'},
                {name:'listname'}
        ]);
        var targetReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.targetRecord);

        this.targetStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getUnAssignCampaignTarget.do',
            baseParams:{
                flag:23,
                campID:this.campaignid
            },
            method:'post',
            reader:targetReader
        });
        this.targetStore.load();

        this.targetListCombo = new Wtf.form.ComboBox({
            emptyText:WtfGlobal.getLocaleText("crm.targetlists.seltargetlist.mtytxt"),//"Select Target List",
            store:this.targetStore,
            valueField:'listid',
            displayField:'listname',
            typeAhead: true,
            mode: 'local',
            triggerAction: 'all',
            width: 250,
            selectOnFocus: true
        });

        this.addTargetListBtn = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN"),//"Add",
            scope:this,
            tooltip:{text:WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN.seltlistttip")},//'Add selected Target List.'},
            iconCls:"pwnd addIcon",
            handler: function() {
                var LISTID = this.targetListCombo.getValue();
                var index = this.targetStore.findBy(function(record){
                    if(record.data.listid == LISTID)
                        return true;
                    else return false;
                },this);
                if(index>-1) {
                    var rec = this.targetStore.getAt(index);
                    this.targetStore.remove(rec);
                    this.addDelTarget(this.campaignid, rec.data.listid, rec.data.ctid, 1);
                    this.targetListCombo.clearValue();
                    if(this.storeTarget!=undefined)
                        this.storeTarget.load();
                } else {
                    if(this.targetStore.getCount()>0)
                        WtfComMsgBox(605,0)
                    else{
                        if(this.store1.getCount()>0){
                            WtfComMsgBox(606,0)
                        } else {
                            WtfComMsgBox(607,0)
                        }
                    }
                }
            }
        });
        this.delTargetBtn = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
            scope:this,
            tooltip:{text:WtfGlobal.getLocaleText("crm.targetlists.toptoolbar.deletebtn.ttip")},//'Delete selected Target List.'},
            iconCls:"pwnd deleteButtonIcon",
            handler: this.deleteTargetHandler
        });
    }, 
    newTargetListHandler : function() {
            var newtargetform = null;
            var newtargetWin = new Wtf.Window({
                width:400,
                height:270,
                resizable:false,
                layout:'border',
                iconCls: 'pwnd favwinIcon',
                title: WtfGlobal.getLocaleText("crm.targetlists.createnewlist"),//'Create Target List',
                modal:true,
                scope : this,
                id:'newTargetWin',
                items:[{
                    region : 'north',
                    height : 75,
                    border : false,
                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                    html: getTopHtml(WtfGlobal.getLocaleText("crm.targetlists.toptoolbar.newBTN.ttip"),WtfGlobal.getLocaleText("crm.targetlists.createnewlist"),"../../images/target-list-wind-icon.jpg")
                },{
                    region : 'center',
                    border : false,
                    bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
                    layout : 'fit',
                    items:[newtargetform = new Wtf.form.FormPanel({
                        buttonAlign:'right',
                        border:false,
                        items:[this.tname = new Wtf.ux.TextField({
                            fieldLabel: WtfGlobal.getLocaleText("crm.mydocuments.header.name")+'*',//'Name* ',
                            allowBlank : false,
                            maxLength:255,
                            name:'name',
                            width:230,
                            xtype:'striptextfield'
                        }),{
                            xtype:'textarea',
                            fieldLabel:'Description',
                            name:'desc',
                            maxLength:1024,
                            width:230
                        },{
                            xtype:'hidden',
                            name:'listid'
                        }],
                        buttons:[{
                            text:WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//'Save',
                            scope : this,
                            title:WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//'Save',
                            handler:function(){
                                if(this.tname.getValue().trim()==""){
                                    ResponseAlert(63);
                                    return ;
                                }
                                if(newtargetform.form.isValid()==false){
                                	WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.lead.webtoleadform.entervalinputmsg")]);
                                	return;
                                }
                                e.disable();
                                var tempObj = newtargetform.form.getValues();
                                tempObj.listid=null,
                                Wtf.Ajax.requestEx({
                                    url: Wtf.req.springBase+'emailMarketing/action/saveTargetListTargets.do',
                                    params:tempObj
                                },this,
                                function(res) {
                                    if(res.success == true) {
                                    	e.enable();
                                        Wtf.getCmp("newTargetWin").close();
                                        var record;
                                        var mode = 0;
                                        var listID = res.listid;
                                        var tlId = 'targetListTabnewedit'+mode+listID;
                                        var targetListTab = Wtf.getCmp(tlId );
                                        if(targetListTab == null) {
                                            targetListTab = new Wtf.targetListWin({
                                                mode : mode,
                                                record : record,
                                                id : tlId,
                                                listID : listID,
                                                TLID : listID,
                                                store:this.targetStore,
                                                listname : Encoder.htmlDecode(tempObj.name),
                                                description : Encoder.htmlDecode(tempObj.desc),
                                                mainTab:this.mainTab,
                                                iconCls: (mode==0?"pwnd newTargetListEmailMarketingWin":"pwnd editTargetListEmailMarketingWin")
                                            })
                                            targetListTab.on("close",function(){
                                                this.targetStore.load();
                                            },this);
                                            this.mainTab.add(targetListTab);
                                        }
                                        this.mainTab.setActiveTab(targetListTab);
                                        this.mainTab.doLayout();
                                    }
                                });

                            }
                        },{
                            text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                            handler:function(){
                                Wtf.getCmp("newTargetWin").close();
                            }
                        }]
                    })]
                }]
        }).show();


    },

    viewTargetListHandler : function() {
        var tlId = 'targetlistgridfromcamp';
        var targetComp = Wtf.getCmp(tlId );
        if(targetComp==null) {
            targetComp=new Wtf.targetListDetails({
                title:WtfGlobal.getLocaleText("crm.targetlists.title.plural"),//'Target Lists',
                id:tlId,
                comboStore:this.targetStore,
                mainTab:this.mainTab
            })
            this.mainTab.add(targetComp);
        }
        this.mainTab.setActiveTab(targetComp);
        this.mainTab.doLayout();
    },

    deleteTargetHandler:function(){
        var sm = this.northGrid.getSelectionModel();
        var s = sm.getSelections();
        if(s.length != 1){
            ResponseAlert(66);
            return;
        }
        Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("crm.targetlists.delmsg.title"),//"Delete Target",
            msg: WtfGlobal.getLocaleText("crm.targetlists.deletetargetlist.confirmmsg"),//'Delete selected Target List?',
            buttons: Wtf.MessageBox.OKCANCEL,
            animEl: 'upbtn',
            scope:this,
            fn:function(btn){
                if(btn=="ok") {
                    var ctID = sm.getSelected().get('ctid');
                    var targID = sm.getSelected().get('listid');
                    this.targetStore.insert(0,s[0]);
                    this.addDelTarget(this.campaignid, targID, ctID, 2);
                }
            },
            icon: Wtf.MessageBox.QUESTION
        },this);
        
    },
    
    addDelTarget:function(campID, targID, ctID, type){
        Wtf.Ajax.requestEx({
            url: Wtf.req.springBase+'emailMarketing/action/saveCampaignTarget.do',
            params:{
                type:type,
                campID:campID,
                targID:targID,
                ctID:ctID,
                flag:22
            }
        },
        this,
        function(res) {
            this.store1.load({
                params:{
                    start:0,
                    limit:15
                }
                });
                if(this.storeTarget!=undefined)
                    this.storeTarget.load();
        },
        function() {
//            WtfComMsgBox(152,1);
        })

    }

});

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
var PortalPersonalMessages = {};
PortalPersonalMessages.TopicStore = function(config){
    PortalPersonalMessages.TopicStore.superclass.constructor.call(this, {
        proxy: new Wtf.data.HttpProxy({
            url: "Common/MailIntegration/mailIntegrate.do"
        }),
        
        reader: new Wtf.data.JsonReader({
            root: 'data',
            totalProperty: 'totalCount'
        }, Wtf.data.Record.create([{
            name: 'date'
        }, {
            name: 'flagged'
        }, {
            name: 'ieId'
        }, {
            name: 'subject'
        }, {
            name: 'post_text'
        }, {
            name: 'from'
        }, {
            name: 'poster_id'
        }, {
            name: 'seen'
        }, {
            name: 'imgsrc'
        }, {
            name: 'senderid'
        }, {
            name: 'status'
        }, {
            name: 'to_addrs'
        }, {
            name: 'uid'
        },{
            name: 'mbox'
        },{
            name: 'attachment'
        },{
            name: 'cached'
        }]))
    });
};

Wtf.extend(PortalPersonalMessages.TopicStore, Wtf.data.Store, {
    msgLmt: 20,
    remoteSort : true,
    loadForum: function(folderid, mbox, mailaction){
        this.baseParams = {
            action:	'EmailUIAjax',
            emailUIAction:	mailaction,
            forceRefresh:	false,
            ieId:	folderid,
            mbox:	mbox,
            module:	'Emails',
            to_pdf:	true
        };
        this.load({
            params: {
                start: 0,
                limit: 20
            }
        });
    },
    
    loadSearch: function(searchtext, folder_id, mailflag, loginid, limit,currFolderId){
        this.baseParams = {
            searchtext: searchtext,
            folder_id: folder_id,
            mailflag: mailflag,
            loginid: loginid,
            flag:currFolderId
        };
        this.load({
            params: {
                start: 0,
                limit: limit || this.msgLmt
            }
        });
    },
    loadRefresh: function(flag, mailflag, loginid, cCursor,limit){
        this.baseParams = {
            flag: flag,
            mailflag: mailflag,
            loginid: loginid
        };
        this.load({
            params: {
                start: cCursor,
                limit: limit || this.msgLmt
            }
        });
    }
});
Wtf.PortalMailPanel= function(config){
    this.mailPageLimit=  new Wtf.common.pPageSize();
    this.dst = new PortalPersonalMessages.TopicStore({mailPageLimit:this.mailPageLimit});
    this.portalmail_sm1 = new Wtf.grid.CheckboxSelectionModel();
    this.portalmail_grid1 = new Wtf.grid.GridPanel({
        ds: this.dst,
        cm: new Wtf.grid.ColumnModel([this.portalmail_sm1, {
            header: WtfGlobal.getLocaleText("crm.case.defaultheader.subject"),//"Subject",
            width: 120,
            sortable: true,
            dataIndex: 'subject',
            renderer:this.subRenderer
        }, {
            header: WtfGlobal.getLocaleText("crm.frommail.label"),//"From",
            width: 120,
            sortable: true,
            dataIndex: 'from'
        }, {
            header: WtfGlobal.getLocaleText("crm.tomail.label"),//"To",
            width: 120,
//            sortable: true,
            dataIndex: 'to_addrs'
        }, {
            header: WtfGlobal.getLocaleText("crm.mymails.header.received"),//"Received",
            //renderer:this.dateRenderer,//Wtf.util.Format.dateRenderer('Y-m-d h:i a'),
            width: 115,
            sortable: true,
            dataIndex: 'date'
        }]),
        loadMask: true,
        sm: this.portalmail_sm1,
        id: 'grid123',
        border: false,
        viewConfig:{
                forceFit:true
            },
        bbar: this.ptb = new Wtf.PagingToolbar({
            id: 'draftspt',
            pageSize: 20,
            store: this.dst,
            displayInfo: true,
            emptyMsg: WtfGlobal.getLocaleText("crm.mymails.msgmtygrid.mtytxt")//"No messages to display"
        })

    });
    this.MessagePanel1 = new Wtf.MessagePanel({
        id: "emails"
    });
   
    this.MessagePanel1.on("UpdateMailDstore", this.loadMailDstore,this);
    Wtf.PortalMailPanel.superclass.constructor.call(this, { activeTab: 0,
        id: 'tabmailtab_pmpanel',
        enableTabScroll: true,
        border: false,
        items: {
            id: 'tabmailtab_tab1',
            title: WtfGlobal.getLocaleText("crm.mymails.msgs.title"),//'Messages',
            iconCls:"pwnd pmsgicon",
            layout: 'border',
            items: [{
                region: 'center',
                collapsible: true,
                id: "tabmailtab_pmNorth",
                border: false,
                split: true,
                layout: 'fit',
                items:[this.portalmail_grid1],
                tbar: this.createMailFunctions()
            }, {
                region: 'south',
                id: "tabmailtab_pmCenter",
                collapsible: true,
                border: false,
                split: true,
                height: 250,
                layout: 'fit',
                items: this.MessagePanel1
            }]
        }
    });

    this.dst.on("beforeload",function(store, options){
        if(store.baseParams.action != 'EmailUIAjax') {
            this.ptb.loading.enable();
            return false;
        }
    },this);
    
    this.on("tabchange", function(p, t){
        t.doLayout();
    },this)
};

Wtf.extend(Wtf.PortalMailPanel, Wtf.TabPanel, {
    leftTree:null,
    afterRender:function(){
        Wtf.PortalMailPanel.superclass.afterRender.call(this);
        Wtf.getCmp("mailsearchtextbox").on("render",function(textfield){
            textfield.el.on("keyup", this.txtsearchKeyPress,this);
        },this)
// ToDo  this.portalmail_grid1.on('rowcontextmenu', this.onMailGridContextmenu,this);
        this.portalmail_grid1.on('rowdblclick', this.gridrowDoubleClick,this);
        this.portalmail_grid1.on("cellclick", this.onClickHandle1,this);
        this.portalmail_grid1.getSelectionModel().on('rowselect',this.rowSelectionChange,this);
        this.portalmail_grid1.getSelectionModel().on('rowdeselect',this.rowDeselect,this);
//        this.portalmail_grid1.on('sortchange',this.sortchange,this);
    },
    dtaskMail:new Wtf.util.DelayedTask(this.searchmails),
    txtsearchKeyPress:function(e){
        this.txt = e.getTarget().value;
        
        this.dtaskMail.cancel();
        this.dtaskMail.delay(500, this.searchmails,this);
    },
    subRenderer:function(value){
        return WtfGlobal.URLDecode(value);
    },
    dateRenderer:function (value){
        return value.format(WtfGlobal.getDateFormat());
         //value;
    },
    ImageReturn:function (data){
        if (data) 
            return "<img class='starImgDiv' star=0 src='../../images/FlagRed.gif'></img>";
        else 
            return "<img class='starImgDiv' star=1 src='../../images/FlagGrey.gif'></img>";
    },
    inboxPublishHandler:function(){
        if(this.portalmail_folderid==0){
            var temp=eval('('+msg.data.data+')');
            var temp1=Wtf.decode(temp.data[0]).data;
            var ds = Wtf.getCmp('grid123').getStore();
            ds.reload();
        }
    },
    loadMailDstore:function(details,ID,attachment){
        var index = this.searchRecIndex(ID);
        var record = this.dst.getAt(index);
        record.set('post_text', details);
        record.set('attachment', attachment);
        record.set('seen', true);
        
//        for (var i = 0; i < this.dst.getCount(); i++) {
//            var record = this.dst.getAt(i);
//            var recId = record.data['post_id'];
//            if (recId == id) {
//                if(details==""&& Attachment==""){
//                    details = " ";
//                    Attachment = " ";
//                }
//                if(record.data.readflag==false){
//                    record.set('readflag', true);
//                }
//                record.set('post_text', details);
//                record.set('Attachment', Attachment);
//                break;
//            }
//        }
    },

   searchRecIndex : function(ID) {
        var index = this.dst.findBy(function(record) {
            if(record.get("uid")==ID)
                return true;
            else
                return false;
        });
        if(index == -1)
            return null;
        return index;
   },

   inboxPublishHandler:function(msg) {
        if(this.portalmail_folderid==0){
            var temp=eval('('+msg.data.data+')');
            var temp1=Wtf.decode(temp.data[0]).data;
            var ds = this.portalmail_grid1.getStore();
            ds.reload();
        }

    },
    loadingDisplayNo:function(str){
        return '<div style="float: left; width:100%"><div style="float: left;">' + str + '</div><div style="float: right; color: rgb(0,0,0); margin-left: 20px; font-weight: normal;">No messages to display</div></div>';
    },
    searchmails:function(){
        this.enablemailtoolbarbtns();
        var searchstring = encodeURIComponent(document.getElementById('mailsearchtextbox').value.trim());
        if(searchstring.length > 0){
            this.portalmail_grid1.setTitle("Search results");
            var view = '';
            var folder_id = this.portalmail_folderid;
            this.dst.loadSearch(searchstring, folder_id, "searchmails", loginid,this.mailPageLimit.combo.getValue(),this.portalmail_folderid);

            this.dst.on("loadexception", function exp(){
                msgBoxShow(4, 1);
            },this);

            this.portalmail_grid1.store = this.dst;
            view = this.portalmail_grid1.getView();
            this.dst.on("load", function(a, b, c){
                if (b.length == 0) {
                    this.portalmail_grid1.setTitle(this.loadingDisplayNo(this.portalmail_titleflag));
                }
                else {
                    this.portalmail_grid1.getStore().groupBy("folder");
                }
            },this)
            searchFlag=true;
        }
        else{
            if(searchFlag){
                this.dst.loadForum(this.portalmail_folderid, "fetch", loginid);
                searchFlag=false;
                this.dst.on("load", function(a, b, c){
                        var view = this.portalmail_grid1.getView();
                        this.portalmail_grid1.store.clearGrouping();
                        if (this.portalmail_folderid == '0') {   
                            for (var i = 0; i < a.getCount(); i++) {
                                if(b[i].data['readflag']==false){
                                    view.getCell(i, 1).firstChild.style.fontWeight = "bold";
                                    view.getCell(i,  2).firstChild.style.fontWeight = "bold";
                                    view.getCell(i,  3).firstChild.style.fontWeight = "bold";
                            }
                        }        
                    }
                },this)
            }
        }
    },
    createMailFunctions:function(){
        this.portalmail_actionMenu = new Wtf.menu.Menu({
            id: 'portalmail_actionMenu',
            items: [{
                text: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.inbox"),//'Inbox',
                id: "0",
                icon: "pwnd inboxIcon"
            },new Wtf.menu.Separator({})]
        });
        this.portalmail_actionMenu.on('itemclick', this.folderClick,this);
        return ([new Wtf.Action({
                text:  WtfGlobal.getLocaleText("crm.mymails.acitonmenu.composemail"),//'Compose Email',
                id: 'compMail',
                scope:this,
                handler: function(){
                    this.handleCompose();
                },
                tooltip: {
                    title: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.composemail.ttip.title"),//'Compose',
                    text: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.composemail.ttip.detail")//'Compose new message, in case of multiple recepients, use comma (,) or semicolon (;) to separate e-mail address.'
                },
                iconCls: 'pwnd compose'
            }),new Wtf.Action({
                text: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.checkmail"),//'Check Email',
                id: 'checkEmail',
                scope:this,
                hidden:true,
                handler: function(){
                    var node = lefttree.getSelectionModel().getSelectedNode();
                    node.fireEvent('click',node,this);
                    portalmail_mainPanel.setAjaxTimeout();
                    portalmail_mainPanel.checkEmailProgress(node.id.split('_')[1], undefined, false);
                },
                tooltip: {
                    title:  WtfGlobal.getLocaleText("crm.mymails.acitonmenu.checkmail"),//'Check Email',
                    text:  WtfGlobal.getLocaleText("crm.mymails.acitonmenu.checkmail.ttip")//'Email messages are received automatically whenever you click on Check Email. If there is any new email, then it will be added to the list. If the password is changed in the email account user will not receive email until the password is not updated in Deskera CRM.'
                },
                iconCls: 'pwnd chkmail'
            }),new Wtf.Action({
                text: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.syncmail"),//'Synchronize Email',
                id: 'syncmail',
                scope:this,
                hidden:true,
                handler: function(){

                    var node = lefttree.getSelectionModel().getSelectedNode();
                    node.fireEvent('click',node,this);
                    portalmail_mainPanel.setAjaxTimeout();
                    portalmail_mainPanel.checkEmailProgress(node.id.split('_')[1], undefined, true);
                },
                tooltip: {
                    title: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.syncmail"),//'Synchronize Email',
                    text: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.syncmail.ttip")//'Changes made in email account would be reflected when user clicks on Synchronize Email. For example if the user deletes email from his mailbox and clicks on Synchronize Email the email would be deleted in Deskera CRM.'
                },
                iconCls: 'pwnd chkmail'
            }), new Wtf.Action({
                text: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.reply"),//'Reply',
                id: 'btnreplyto',
                scope:this,
                hidden : true,
                handler: function(){
                    this.createReplyWindow()
                },
                tooltip: {
                    title:  WtfGlobal.getLocaleText("crm.mymails.acitonmenu.reply"),//'Reply',
                    text:  WtfGlobal.getLocaleText("crm.mymails.acitonmenu.reply.ttip")//'Reply to selected message.'
                },
                iconCls: 'pwnd outbox'
            }), new Wtf.Action({
                text: WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//'Delete',
                handler: this.DeleteMails,
                scope:this,
                disabled : true,
                id: 'btndelete',
                tooltip: {
                    title: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.delemail"),//''Delete email(s)',
                    text: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.delemail.ttip")//'Delete selected email(s).'
                },
                iconCls: 'pwnd deleteButtonIcon'
            }), new Wtf.Toolbar.Button({
                text: WtfGlobal.getLocaleText("crm.mymails.quickcreate"),//'Quick Create',
                iconCls: 'pwndCRM settings',
                scope:this,
                disabled:true,
                tooltip: {
                    title: WtfGlobal.getLocaleText("crm.mymails.quickcreate"),//'Quick Create',
                    text: WtfGlobal.getLocaleText("crm.mymails.quickcreate.ttip")//'Quickly create application entity from email.'
               },
                id: 'btnqcreate',
                menu: {
                    items: [{
                        text: WtfGlobal.getLocaleText("crm.LEAD"),//'Lead',
                        scope:this,
                        handler: this.showQuickCreateWin.createDelegate(this,[{
                    		dashcomp:Wtf.moduleWidget.lead,
                    		configType:"Lead",
                            compid:"mailleadcomp",
                            paramObj:{flag:20,auditEntry:1},
                            actionCode:1,
                            url:Wtf.req.springBase+'Lead/action/saveLeads.do',
                            jsonstr:{leadid:'0',leadownerid:loginid}
                    	}]),
                        icon: "../../images/contactimage.png"
                    }]
                }
            }), moveto = new Wtf.Toolbar.Button({
                text: WtfGlobal.getLocaleText("crm.mymails.toptoolbar.moveto"),//'Move To',
                iconCls: 'pwnd sendmsg',
                scope:this,
                hidden : true,
                tooltip: {
                    title: WtfGlobal.getLocaleText("crm.mymails.toptoolbar.moveto.ttip.title"),//'Move Messages',
                    text: WtfGlobal.getLocaleText("crm.mymails.toptoolbar.moveto.ttip.detail")//'Move selected messages.'
                },
                id: 'MoveFolders',
                menu: this.portalmail_actionMenu
            }), moreactions = new Wtf.Toolbar.Button({
                text: WtfGlobal.getLocaleText("crm.mymails.toptoolbar.moreactions"),//'More Actions',
                iconCls: 'pwnd settings',
                scope:this,
                hidden : true,
                tooltip: {
                    title: WtfGlobal.getLocaleText("crm.mymails.toptoolbar.moreactions"),//'More Actions',
                    text: WtfGlobal.getLocaleText("crm.mymails.toptoolbar.moreactions.ttip")//'Perform more actions.'
                },
                id: 'btnmoreactions',
                menu: {
                    items: [{
                        text: WtfGlobal.getLocaleText("crm.mymails.toptoolbar.moreactions.addflag"),//'Add flag',
                        scope:this,
                        handler: this.addstarClick,
                        icon: "../../images/FlagRed16.png"
                    }, {
                        text: WtfGlobal.getLocaleText("crm.mymails.toptoolbar.moreactions.removeflag"),//'Remove flag',
                        scope:this,
                        handler: this.removestarClick,
                        icon: "../../images/FlagGrey16.png"
                    }]
                }
            }),new Wtf.form.TextField({
                id: "mailsearchtextbox",
                emptyText:WtfGlobal.getLocaleText("crm.audittrail.searchBTN"),// 'Search',
                width: 150,
                hidden : true,
                height: 19
            })]);
    },
    handleCompose:function(){
        var composePanel = Wtf.getCmp("composeMessagePanel");
        if(composePanel === undefined){
            composePanel = new Wtf.ReplyWindow({
                uLabel: WtfGlobal.getLocaleText("crm.tomail.label"),//'To',
                bLabel: WtfGlobal.getLocaleText("crm.case.defaultheader.subject"),//'Subject',
                tdisabled: false,
//                replytoId: '-1',
                userId: loginid,
                groupId: "",
                id: 'composeMessagePanel',
                title: WtfGlobal.getLocaleText("crm.composemail.title"),//'Compose Message',
                closable: true,
                firstReply: "",
                uFieldValue: "",
                bFieldValue: "",
                type: "Mail",
                sendFlag: "newmsg",
                composeMail: 1,
                mailDS: this.portalmail_grid1.getStore(),
                emailid : ''
            });
            composePanel.insertStore.on("load", this.handleInsertMail,this);
            this.add(composePanel);
        }
        this.setActiveTab(composePanel);
    },
    getfolderGroup:function(value){
        var resultHead=WtfGlobal.getLocaleText("crm.none");//"None";
        switch(value){
             case "0":
                resultHead=WtfGlobal.getLocaleText("crm.mymails.acitonmenu.inbox");//"Inbox";
                break;
            case "1":
                resultHead=WtfGlobal.getLocaleText("crm.mymails.sentmails");//"Sent Mail";
                break;
            case "2":
                resultHead = WtfGlobal.getLocaleText("crm.mymails.deleted");//"Deleted";
                break;
            case "3":
                resultHead = WtfGlobal.getLocaleText("crm.mymails.draftlabel");//"Drafts";
                break;
            default : 
                var node = Wtf.getCmp("folderview").getNodeById(value);
                if(node){
                    resultHead= node.text;
                 }
                 break;
            }
        return resultHead;
    },
    handleInsertMail:function(obj, rec, opt){
        if(rec[0]){
            if (rec[0].data['Success'].match('Success')) {
                msgBoxShow(141, 0);
                this.panelObj.handleClose();
            }
            else if (rec[0].data['Success'].match('Fail')) {
                    msgBoxShow(142, 1);
            }  
            else if (rec[0].data['Success'].match('Draft')) {
                    msgBoxShow(147, 0);
                    this.panelObj.handleClose();
            }  
            else if (rec[0].data['Success'].match('userfail')) {
                    msgBoxShow(['Delivery Failure', 'Message to user '+rec[0].data['Subject'] +' is invalid.'], 1);
            }  
         }   
         this.dst.reload();
    },
    onMailGridContextmenu:function(grid, rowindex, e){
        this.portalmail_sm1.selectRow(rowindex);
        var menu = null;
        
        if (!menu) {
            menu = new Wtf.menu.Menu({
                id: 'context12',
                items: [{
                    text: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.reply"),//'Reply',
                    id: 'cntxbtnreplyto',
                    scope:this,
                    handler: this.createReplyWindow,
                    iconCls: 'pwnd outboxCx'
                }, {
                    text: WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//'Delete',
                    handler: this.DeleteMails,
                    id: 'cntxbtndelete',
                    scope:this,
                    iconCls: 'pwnd delicon'
                }, {
                    text: WtfGlobal.getLocaleText("crm.mymails.mailgridmenu.restoremsg"),//'Restore Message',
                    iconCls: 'msgRestore',
                    id: 'cntxbtnrestore',
                    scope:this,
                    handler: this.handleContextRestore
                }, {
                    text: WtfGlobal.getLocaleText("crm.mymails.toptoolbar.moveto"),//'Move To',
                    iconCls: 'pwnd sendmsgwt',
                    id: 'cntxbtnmoveto',
                    scope:this,
                    // <-- submenu by nested config object
                    menu: this.portalmail_actionMenu
                }, {
                    text: WtfGlobal.getLocaleText("crm.mymails.toptoolbar.moreactions"),//'More Actions',
                    iconCls: 'pwnd settingswt',
                    id: 'cntxbtnmoreactions',
                    // <-- submenu by nested config object
                    menu: {
                        items: [{
                            text: WtfGlobal.getLocaleText("crm.mymails.toptoolbar.moreactions.addflag"),//'Add flag',
                            scope:this,
                            handler: this.addstarClick,
                            icon: "../../images/FlagRed16.png"
                        }, {
                            text: WtfGlobal.getLocaleText("crm.mymails.toptoolbar.moreactions.removeflag"),//'Remove flag',
                            scope:this,
                            handler: this.removestarClick,
                            icon: "../../images/FlagGrey16.png"
                        }]
                    }
                }]
            })
        }
        menu.showAt(e.getXY());
        e.preventDefault();

        this.updateCntxButtonStatus(this.dst.getAt(rowindex).get("folder"));
    },
    updateCntxButtonStatus:function(folderid){
        var replyBtn = Wtf.getCmp('cntxbtnreplyto');
        var delBtn = Wtf.getCmp('cntxbtndelete');
        var moveBtn = Wtf.getCmp('cntxbtnmoveto');
        var moreAct = Wtf.getCmp('cntxbtnmoreactions');
        var restBtn = Wtf.getCmp('cntxbtnrestore');
        var zeroBtn =  Wtf.getCmp('0');
        replyBtn.enable();
        delBtn.disable();
        moveBtn.disable();
        moreAct.disable();
        restBtn.disable();
        zeroBtn.disable();
        if (folderid == '0') {
            replyBtn.enable();
            delBtn.enable();
            moveBtn.enable();
            moreAct.enable();
        }
        else 
            if (folderid == '2') {
                replyBtn.disable();
                delBtn.enable();
                restBtn.enable();
            }
            else 
                if (folderid == '4') {
                    moreAct.enable();
                }
                else 
                    if (folderid == '1') {
                        delBtn.enable();
                        moreAct.enable();
                    }else if(folderid == '3'){
                        delBtn.enable();
                        zeroBtn.enable();
                    }
                    else {
                        delBtn.enable();
                        moveBtn.enable();
                        zeroBtn.enable();
                    }
    },
    folderClick:function(item, e){
        var folderid = item.id;
        if(!(this.portalmail_folderid==folderid)){
            if(this.portalmail_sm1.getSelections().length>0){
                Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), WtfGlobal.getLocaleText("crm.mymails.movemailmsg"), function(btn){
                    if (btn == 'yes') {
                        this.UpdateFolderID(folderid);
                    }
                },this);
            } else {
                msgBoxShow(145, 1);
            }
        } else {
            msgBoxShow(146, 1);
        }
    },
    createReplyWindow:function(){
        var selMail = this.portalmail_sm1.getSelections();
        if(selMail.length==1){
            var record = selMail[0];
            if(record.data.deskSuperuser=='true'){
                msgBoxShow(173, 1);
                return;
            }
            var replyObj = Wtf.getCmp(record.data['post_id'] + "_replyPanel");
            if(replyObj === undefined){
                replyObj= new Wtf.ReplyWindow({
                    uLabel: WtfGlobal.getLocaleText("crm.mymails.emails.replytolabel"),//'Reply To',
                    bLabel: WtfGlobal.getLocaleText("crm.case.defaultheader.subject"),//'Subject',
                    tdisabled: true,
                    title:WtfGlobal.getLocaleText("crm.mymails.acitonmenu.reply"),//'Reply',
                    id: record.data['post_id'] + "_replyPanel",
                    closable: true,
                    layout: 'fit',
                    replytoId: record.data['post_id'],
                    userId: loginid,
                    groupId: "",
                    firstReply: "",
                    uFieldValue: record.data['poster_id'],
                    details : "<br><br><br><br><br><br><br><br><-----------------Original Message-----------------><br><br><br>On "+record.data['post_time']+", "+record.data['post_fullname']+" wrote: <br><br>"+ WtfGlobal.URLDecode(record.data['post_text']),
                    bFieldValue: "Re: "+ WtfGlobal.URLDecode(record.data['post_subject']),
                    type: "Mail",
                    sendFlag:"reply",
                    fid:this.portalmail_folderid,
                    composeMail:5
                });
                replyObj.insertStore.on("load", this.handleInsertMail,this);
                this.add(replyObj);
            }
            this.setActiveTab(replyObj);
        } else {
            msgBoxShow(145, 1);
        }
   
    },
    showQuickCreateWin:function(props){
    	if(this.portalmail_sm1.getCount()!=1) {
    		msgBoxShow([WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.common.singlerecselectmsg")],1);
    		return;
    	}
    	
    	var rec = this.portalmail_sm1.getSelected();
    	var leadPanel = new Wtf.quickadd(Wtf.applyIf(props||{},{
            treeflag:true,
            border: false,
            getallfields:true,
            defaults:{
        		labelWidth:150,
        		autoScroll:true,
        		bodyStyle:'font-size:10px;padding:8px 10px 0px 10px',
                defaults: {
	                anchor: '90%',
	                xtype:'striptextfield',
	                msgTarget: 'side'
	            }
        	}
        }));
    	
    	leadPanel.on('closeform',this.populateVal.createDelegate(this,[rec]),this);

        var win= new Wtf.Window({
            border:false,
            modal:true,
            items:[{
                region: 'north',
                height: 75,
                border: false,
                bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtmlReqField(WtfGlobal.getLocaleText("crm.mymails.createleadwin.title"),WtfGlobal.getLocaleText("crm.mymails.createleadwin.htmldetail"),'')
            },{
                region: 'center',
                border: false,
                bodyStyle: 'background:#f1f1f1;',
                layout:'fit',
                items:leadPanel
            }],
            layout:'border',
            width:450,
            height:500,           
            buttonAlign:'right',
            buttons: [{
                text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                scope:leadPanel,
                handler:function(){
                        this.saveobj();
                }
            }]
        });
        leadPanel.on("aftersave",function(){this.close();},win);

    	win.show();
    },
    
    populateVal:function(rec){
    	var val = rec.get('from');
    	var patt=/(\w*\s*)(\w*\s*)*&lt;(.+)&gt;/g;
    	var fname=val.replace(patt,"$1");
    	var lname=val.replace(patt,"$2");
    	if(Wtf.isEmpty(lname)){
    		lname = fname;
    		fname = "";
    	}
    	var email=val.replace(patt,"$3");
    	var fld = Wtf.getCmp("firstnamemailleadcomp");
    	if(fld)fld.setValue(fname);
    	fld = Wtf.getCmp("lastnamemailleadcomp");
    	if(fld)fld.setValue(lname);
    	fld = Wtf.getCmp("emailmailleadcomp");
    	if(fld)fld.setValue(email);
    },
    DeleteMails:function(){
        if(this.portalmail_sm1.getSelections().length>0){
            var delstr=WtfGlobal.getLocaleText("crm.mymails.deletemailmsg");//"Are you sure you want to delete selected email(s)?";
            if(this.portalmail_folderid==2)
                delstr=WtfGlobal.getLocaleText("crm.mymails.delmsg");//'Are you sure you want to permanently delete selected messages?';
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), delstr, function(btn){
                if (btn == 'yes') {
                    this.deleteMails();
                }
            }, this);
        } else {         
             WtfComMsgBox(1,1);
        }
    },

    deleteMails:function(){
        if(this.portalmail_sm1.getSelections().length>0) {
            var selArray = Array();
            selArray = this.portalmail_sm1.getSelections();
            var jsonData = "[";
            for (var cnt = 0; cnt< selArray.length; cnt++) {
                var rowobj = selArray[cnt];
                jsonData += "\""+encodeURIComponent(rowobj.data.uid) + "\",";
            }
            jsonData = jsonData.substring(0, jsonData.length - 1) + "]";
            var ieId = 'null';
            var folder = 'krawler::Emails';
            if(lefttree.getSelectionModel().getSelectedNode().id.indexOf("inbox_")>=0) {
                folder = "INBOX";
                ieId = lefttree.getSelectionModel().getSelectedNode().id.split("_")[1];
            }
                
            Wtf.Ajax.requestEx({
                method: 'POST',
                url: "Common/MailIntegration/mailIntegrate.do",
                params: {
                    action:'EmailUIAjax',
                    emailUIAction:'markEmail',
                    ieId : ieId,
                    folder : folder,
                    krawler_body_only:true,
                    type:'deleted',
                    uids :jsonData,
                    module:'Emails',
                    to_pdf:true
                }
            }, this, function(result, req){
                var nodeobj = eval("(" + result + ")").data;
                var storeobj = this.portalmail_grid1.getStore();
                storeobj.reload();
//                    if(folder_id == 2){
//                        for(var cnt = 0; cnt < nodeobj.length; cnt++){
//                            var pid = nodeobj[cnt].post_id;
//                            if(Wtf.getCmp("emailsTab" + pid)!=null){
//                                this.remove(Wtf.getCmp("emailsTab" + pid));
//                            } if(Wtf.getCmp(pid + "_replyPanel") !== null){
//                                this.remove(Wtf.getCmp(pid + "_replyPanel"));
//                            }
//                        }
//                    }
                this.MessagePanel1.clearContents();
            }, function(result, req){
            });
        } else {
             WtfComMsgBox(1,1);
        }
    },
    
    UpdateFolderID:function(folder_id){
           var last_folder_id = this.portalmail_folderid;

            if(this.portalmail_sm1.getSelections().length>0){
                var ds = this.portalmail_grid1.getStore();
                var selArray = Array();
                selArray = this.portalmail_sm1.getSelections();
                var jsonData = "{data:[";
                for (i = 0; i < selArray.length; i++) {
                    var rowobj = selArray[i];
                    jsonData += "{'post_id':'" + encodeURIComponent(rowobj.get('post_id')) + "'},";
                }
                jsonData = jsonData.substring(0, jsonData.length - 1) + "]}";
                Wtf.Ajax.requestEx({
                    method: 'POST',
                    url: Wtf.req.prt + 'getmail.jsp',
                    params: {
                        mailflag: 'movemails',
                        last_folder_id: last_folder_id,
                        dest_folder_id: folder_id,
                        post_id: jsonData
                    }
                }, this, function(result, req){
                    var nodeobj = eval("(" + result + ")").data;
                    var storeobj = this.portalmail_grid1.getStore();
                    storeobj.reload();
                    if(folder_id == 2){
                        for(var cnt = 0; cnt < nodeobj.length; cnt++){
                            var pid = nodeobj[cnt].post_id;
                            if(Wtf.getCmp("emailsTab" + pid)!=null){
                                this.remove(Wtf.getCmp("emailsTab" + pid));
                            } if(Wtf.getCmp(pid + "_replyPanel") !== null){
                                this.remove(Wtf.getCmp(pid + "_replyPanel"));
                            }
                        }
                    }
                    this.MessagePanel1.clearContents();
                }, function(result, req){
                });
            } else {
                 msgBoxShow(145, 1);
            }            
    },
    handleContextRestore:function(){
        this.RestoreMsg(this.portalmail_sm1.getSelected().data['post_id']);
    },
    RestoreMsg:function(postid){
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), WtfGlobal.getLocaleText("crm.mymails.restoreselmsgsconfirm"), function(btn){//'Are you sure you want to restore selected messages?', function(btn){
            if (btn == 'yes') {
                var ds = this.portalmail_grid1.getStore();
                Wtf.Ajax.requestEx({
                        method: 'POST',
                        url: Wtf.req.prt + 'getmail.jsp',
                        params: {
                            mailflag: 'restoremsg',
                            post_id: postid
                        }
                }, this, function(result, req){
                    if (postid == result) {
                        ds.remove(ds.getAt(ds.find('post_id', postid)));
                        msgBoxShow(149, 0);
                    }
                    else {
                        msgBoxShow(4, 1);
                    }
                }, function(result, req){
                    msgBoxShow(4, 1);
                });
            }
        }, this);
    },
    addstarClick:function(){
        this.handleStarChange(true);
    },
    removestarClick:function(){
        this.handleStarChange(false);
    },
    changeStar:function(flag,jsonData,row,col){
        Wtf.Ajax.requestEx({
                        method: 'GET',
                        url: Wtf.req.prt + 'getmail.jsp',
                        params: {
                        mailflag: 'starchange',
                        post_id: jsonData,
                        flag: flag
                    }
                    }, this, function(result, req){
                        var rowArr = row.toString().split(",");
                        for(var i=0;i<rowArr.length;i++){
                            targetImg = this.portalmail_grid1.getView().getCell(rowArr[i],col).firstChild.firstChild;
                            if(flag==true){
                                targetImg.src = "../../images/FlagRed.gif";
                                targetImg.setAttribute("star", 0);
                            }else{
                                targetImg.src = "../../images/FlagGrey.gif";
                                targetImg.setAttribute("star", 1);
                            }
                        }
                        
                    }, function(result, req){
                 });
    },
    handleStarChange:function(flag){
        if(this.portalmail_sm1.getSelections().length>0){   
            var grd = this.portalmail_grid1;
            var bt = grd.getBottomToolbar();
            var cCursor = bt.cursor;

            var flag1 = this.portalmail_folderid;
            var title = this.portalmail_titleflag;

            var selArray = Array();
            selArray = this.portalmail_sm1.getSelections();
            var num="";
            var jsonData = "{\"data\":[";
            for (i = 0; i < selArray.length; i++) {
                var rowobj = selArray[i];
                jsonData += "{\"post_id\":\"" + encodeURIComponent(rowobj.get('post_id')) + "\"},";
                rowobj.set('flag',flag);
                num += this.dst.find('post_id', rowobj.get('post_id'))+",";
            }
            jsonData = jsonData.substring(0, jsonData.length - 1) + "]}";
            num = num.substring(0,num.length-1);
            this.changeStar(flag,jsonData,num,4);
         } else {
             msgBoxShow(145, 1);
         }
    },
    
    gridrowDoubleClick:function(obj, rowIndex, e){
        var ds = this.portalmail_grid1.getStore();
        var selRecData = ds.getAt(rowIndex).data;
        var postid = selRecData.uid;
        var postSub = WtfGlobal.URLDecode(selRecData.subject);
        if(postSub === undefined || postSub=="") {
            postSub="[No Subject]";
        }
        var tabid = "emailsTab" + postid;
        var tab = this.getComponent(tabid);
        if (tab) {
            this.setActiveTab(tab);
        }
        else {
            var MessagePanel2 = new Wtf.MessagePanel({
                id: "emails" + postid
            });
            if(lefttree.getSelectionModel().getSelectedNode().id.indexOf("draft") != -1) {
                if(selRecData.cached) {
                    var draftID = selRecData.uid + "_draftPanel";
                    var rObj = Wtf.getCmp(draftID);
                    if(rObj === undefined){
                        rObj = new Wtf.ReplyWindow({
                        	uLabel: WtfGlobal.getLocaleText("crm.tomail.label"),//'To',
                            bLabel: WtfGlobal.getLocaleText("crm.case.defaultheader.subject"),//'Subject',
                            title:WtfGlobal.getLocaleText("crm.mymails.draftlabel"),//'Draft',
                            tabWidth:150,
                            closable:true,
                            tdisabled: false,
                            replytoId:selRecData.to_addrs,
                            id: draftID,
    //                        userId: loginid,
                            groupId: "",
                            firstReply: "",
                            uFieldValue: selRecData.from.replace("&lt;","<").replace("&gt;",">"),
                            bFieldValue: WtfGlobal.URLDecode(selRecData.subject),
                            type: "Mail",
                            sendFlag: "newmsg",
                            composeMail:1,
                            isDraft : true,
                            fid:this.portalmail_folderid,
                            details:WtfGlobal.URLDecode(selRecData.post_text),
                            emailid : selRecData.uid,
                            attachment : selRecData.attachment,
                            mailDS: this.portalmail_grid1.getStore()
                        });
                        rObj.insertStore.on("loadsuccess", this.handleInsertMail,this);
                        this.add(rObj);
                    }
                    this.setActiveTab(rObj);
                } else {
                   Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),//"Alert",
                        msg: WtfGlobal.getLocaleText("crm.mymails.mail.loadmsg"),//Fetching mail... Please wait",
                        buttons: Wtf.MessageBox.OK,
                        animEl: 'mb9',
                        icon: Wtf.MessageBox.INFO
                    });
                }
            } else {
                this.add({
                    id: "emailsTab" + postid,
                    title: postSub,
                    closable: true,
                    layout: 'fit',
                    tbar: this.createMailToolbar(postid,selRecData),
                    items: MessagePanel2
                }).show();
                MessagePanel2.setData1("", "", "", '<div class="loading-indicator">&#160;Fetching Mail...</div>', "");
                MessagePanel2.setData(selRecData.subject.trim(), selRecData.from, selRecData.date, "",selRecData.uid,'',selRecData.to_addrs);
//                if (this.portalmail_folderid == '1')
//                    MessagePanel2.setFromText('To:', 'Sent on:');
//                else {
                    MessagePanel2.setFromText(WtfGlobal.getLocaleText("crm.frommail.label")+':', WtfGlobal.getLocaleText("crm.mymails.receivedon")+':');
                    MessagePanel2.setToText(WtfGlobal.getLocaleText("crm.tomail.labe")+':');
//                    this.MessagePanel1.setData(selectRec.subject, selectRec.from,selectRec.date,'',selectRec.uid,'',selectRec.to_addrs);
//                }
                var detail = selRecData.post_text;//+ds.getAt(rowIndex).get('Attachment');
                if (detail == "") {
//                    MessagePanel2.messageId = postid;
//                    MessagePanel2.topicstore.loadForum(ds.getAt(rowIndex).get('post_id'), "-1", "mail","");
                    this.getSingleMessage(selRecData,MessagePanel2);
                }
                else {
                    MessagePanel2.loadCacheData(detail);
                    this.checkForOpenedTab(selRecData.uid,detail,MessagePanel2);
                }
//                if(this.portalmail_folderid == "0") {
//                    var draftsMenu = Wtf.menu.MenuMgr.get("portalmail_actionMenuForPMsg" + postid).items.items[0];
//                    if(draftsMenu.text.trim() == "Drafts")
//                        draftsMenu.disable();
//                }
//                this.addExistingFoldersForMsgMenu(postid);
            }
        }
    },
    
    createMailToolbar:function(postid,selectedRecData){
        var actionarr = Array();
//        var folderid = this.dst.getAt(this.dst.find('post_id', postid)).get("folder");
//        if (folderid != '2') {
            //******************** Action menu for perticular msg toolbar *********************
            var portalmail_actionMenuForPMsg = new Wtf.menu.Menu({
                id: 'portalmail_actionMenuForPMsg' + postid,
                scope:this,
                items: [{
                    text: WtfGlobal.getLocaleText("crm.mymails.draftlabel"),//'Drafts',
                    scope:this,
                    handler: function(){
                        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), WtfGlobal.getLocaleText("crm.mymails.movemailmsg"), function(btn){
                            if (btn == 'yes') {
                                this.UpdateFolderIDForPerMsg(postid, 3);
                                //Wtf.Msg.alert('Message Move', 'Message has been moved successfully.');
                            }
                        },this);
                    },
                    icon: "../../images/mail_generic.png"
                }]
            });

//            if (folderid == '0' || (folderid != '1' && folderid != '4')) {
                actionarr.push(new Wtf.Action({
                    text: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.reply"),//'Reply',
                    id: 'btnreplyto1' + postid,
                    scope:this,
                    disabled : true,
                    handler: function(){
                        this.createReplyWindowForPMsg(postid,selectedRecData)
                    },
                    tooltip: {
                        title: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.reply"),//'Reply',
                        text: WtfGlobal.getLocaleText("crm.mymails.acitonmenu.reply.ttip")//'Reply to selected message.'
                    },
                    iconCls: 'pwnd outbox'
                }))
//            }

            actionarr.push(new Wtf.Action({
                text: WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//'Delete',
                scope:this,
                hidden:true,
                handler: function(){
            		Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), WtfGlobal.getLocaleText("crm.mymails.delmsg"), function(btn){
                            if (btn == 'yes') {
                            this.UpdateFolderIDForPerMsg(postid, 2);
                            msgBoxShow(148, 0);
                        }
                    }, this);
                },
                id: 'btndelete1',
                tooltip: {
                    title: WtfGlobal.getLocaleText("crm.mymails.delmsg.ttip.title"),//'Delete Messages',
                    text: WtfGlobal.getLocaleText("crm.mymails.delmsg.ttip.detail")//'Delete selected messages.'
                },
                iconCls: 'pwnd delicon'
            }))
//            if(folderid!=1 && folderid!= 2 && folderid != 3 && folderid != 4){
//                actionarr.push(moveto1 = new Wtf.Toolbar.MenuButton({
//                    text: 'Move To',
//                    iconCls: 'pwnd sendmsg',
//                    tooltip: {
//                        title: 'Move Messages',
//                        text: 'Move selected messages'
//                    },
//                    id: 'MoveFolders' + postid,
//                    menu: portalmail_actionMenuForPMsg
//                }))
//            }
//        } else {
//            actionarr.push(new Wtf.Action({
//                text: 'Delete Forever',
//                id: 'btndelforever1',
//                scope:this,
//                handler: function(){
//                    this.deleteMsgForever(postid)
//                },
//                tooltip: {
//                    title: 'Delete Message Forever',
//                    text: 'Delete selected message forever'
//                },
//                iconCls: 'pwnd delicon'
//            }))
//            actionarr.push(new Wtf.Action({
//                text: 'Restore Message',
//                id: 'btnrestoremsg1',
//                handler: function(){
//                    this.RestoreMsg(postid);
//                },
//                scope: this,
//                tooltip: {
//                    title: 'Restore Message',
//                    text: 'Restore selected message'
//                },
//                iconCls: 'pwnd sendmsg'
//            }))
//        }
        return actionarr;
    },
    createReplyWindowForPMsg:function(postid,selectedRecData){
        var rObj = Wtf.getCmp(postid + "_replyPanel");
        var replyTo ="";
        var uFieldVal ="";
        if(lefttree.getSelectionModel().getSelectedNode() && lefttree.getSelectionModel().getSelectedNode().id.split("_")[0]=="sentmails") { // sent mail folder
            replyTo = selectedRecData.to_addrs.replace("&lt;","<").replace("&gt;",">");
            uFieldVal = selectedRecData.from.replace("&lt;","<").replace("&gt;",">");
        } else { // inbox folder
            replyTo = selectedRecData.from.replace("&lt;","<").replace("&gt;",">");
            uFieldVal = selectedRecData.to_addrs.replace("&lt;","<").replace("&gt;",">");
        }

        if(rObj === undefined){
            rObj = new Wtf.ReplyWindow({
            	uLabel: WtfGlobal.getLocaleText("crm.mymails.emails.replytolabel"),//'Reply To',
                bLabel: WtfGlobal.getLocaleText("crm.case.defaultheader.subject"),//'Subject',
                tdisabled: true,
                replytoId:  replyTo,
//                userId: loginid,
                title:WtfGlobal.getLocaleText("crm.mymails.acitonmenu.reply"),//'Reply',
                id: postid + "_replyPanel",
                closable: true,
                layout: 'fit',
                groupId: "",
                firstReply: "",
                uFieldValue: uFieldVal,
                bFieldValue: "Re:"+ (selectedRecData.subject!==undefined || selectedRecData.subject.lenth>0) ? WtfGlobal.URLDecode(selectedRecData.subject):"",
                type: "Mail",
                details:"<br><br><br><br><br><br><br><br><-----------------Original Message-----------------><br><br><br>On "+selectedRecData.date+", "+selectedRecData.from+" wrote: <br><br>"+ WtfGlobal.URLDecode(selectedRecData.post_text),
                sendFlag: "reply",
                fid:this.portalmail_folderid,
                composeMail:5,
                emailid : selectedRecData.uid
            });
            rObj.insertStore.on("load", this.handleInsertMail);
            this.add(rObj);
        }
        this.setActiveTab(rObj);
    },
    addExistingFoldersForMsgMenu:function(postid){
        if (this.portalmail_folderid != '2') {
            //***************** Take user folders ******************************                
            Wtf.Ajax.requestEx({
                url: Wtf.req.prt + 'getmailfolders.jsp',
                params: {
                    loginid: loginid
                }},
                this,
                function(result, req){
                    var nodeobj = eval("(" + result + ")");
                    for (var j = 0; j < nodeobj.length; j++) {
                        var folderid = nodeobj[j].folderid;
                        var foldernametext = nodeobj[j].foldername;
                        Wtf.menu.MenuMgr.get("portalmail_actionMenuForPMsg" + postid).add({
                            text: foldernametext,
                            id: folderid,
                            handler: function(e){
                                folderid = e.id;
                                Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), WtfGlobal.getLocaleText("crm.mymails.movemailmsg"), function(btn){
                                    if (btn == 'yes') {
                                        this.UpdateFolderIDForPerMsg(postid, folderid);
                                    }
                                })
                            },
                            icon: "lib/resources/images/default/tree/folder.gif"
                        });
                    }
                });
        }
    },
    UpdateFolderIDForPerMsg:function(postid, folder_id){
        var last_folder_id = this.portalmail_folderid;
        var ds = this.portalmail_grid1.getStore();
        var jsonData = "{data:[";
        jsonData += "{'post_id':'" + encodeURIComponent(postid) + "'}";
        jsonData += "]}";
        Wtf.Ajax.requestEx({
            method: 'POST',
            url: Wtf.req.prt + 'getmail.jsp',
            params: {
                mailflag: 'movemails',
                last_folder_id: last_folder_id,
                dest_folder_id: folder_id,
                post_id: jsonData
            }
        }, this, function(result, req){
               var nodeobj = eval("(" + result + ")");
                var storeobj = this.portalmail_grid1.getStore();
                storeobj.reload();
        }, function(result, req){
        });
    },
    
    onClickHandle1:function (grid,row,col,e) {    
         if(e.getTarget("img[class='starImgDiv']")) {
            var flag = "";
            var targetImg  = grid.getView().getCell(row,col).firstChild.firstChild;
            var rowobj = this.dst.getAt(row);
            if (targetImg.src.match('../../images/FlagGrey.gif')) {
                flag = true;
            } else {
                flag = false;
            }
            var jsonData = "{data:[";
            jsonData += "{'post_id':'" + encodeURIComponent(rowobj.data.post_id) + "'}";
            jsonData += "]}";
            rowobj.set('flag',flag);
            this.changeStar(flag,jsonData,row,col);
             this.portalmail_sm1.deselectRow(row);
             if(this.portalmail_folderid==4){
                 this.dst.reload();
             }
        }
    },
    updateButtonStatus:function(count,folderid){
        if (folderid == '0') {
            if (count == 1) 
                Wtf.getCmp('btnreplyto').enable();
            else 
                Wtf.getCmp('btnreplyto').disable();
            Wtf.getCmp('btndelete').enable();
            Wtf.getCmp('MoveFolders').enable();
            Wtf.getCmp('btnmoreactions').enable();
            Wtf.getCmp('0').disable();
        } else if (folderid == '2') {
            this.enablemailtoolbarbtns();
            Wtf.getCmp('btndelete').enable();
        } else if (folderid== '4') {
            Wtf.getCmp('btnreplyto').disable();
            Wtf.getCmp('btndelete').disable();
            Wtf.getCmp('MoveFolders').disable();
            Wtf.getCmp('btnmoreactions').enable();
        } else if (folderid == '1') {
            Wtf.getCmp('btnreplyto').disable();
            Wtf.getCmp('btndelete').enable();
            Wtf.getCmp('MoveFolders').disable();
            Wtf.getCmp('btnmoreactions').enable();
        } else if(folderid == '3') {
            Wtf.getCmp('btnreplyto').disable();
            Wtf.getCmp('btndelete').enable();
            Wtf.getCmp('MoveFolders').disable();
            //Wtf.getCmp('btnmoreactions').disable();
            Wtf.getCmp('0').enable();
        } else {
            Wtf.getCmp('btnreplyto').enable();
            Wtf.getCmp('btndelete').enable();
            Wtf.getCmp('MoveFolders').enable();
            Wtf.getCmp('btnmoreactions').enable();
            Wtf.getCmp('0').enable();
        } 
    },
    enablemailtoolbarbtns:function(){
        Wtf.getCmp('btnreplyto').disable();
        Wtf.getCmp('btndelete').disable();
        Wtf.getCmp('MoveFolders').disable();
        Wtf.getCmp('btnmoreactions').disable();
    },
    beforeRowselect:function(sm,rowindex,kexisting,rec) {
        var folderid = rec.get("folder");
        if(folderid){
        }
    },

    rowSelectionChange:function(sm,rowIndex,rec){
        /*if(sm.getSelections().length > 0 ){
            Wtf.getCmp("btndelete").enable();
            Wtf.getCmp("MoveFolders").enable();
            Wtf.getCmp("btnmoreactions").enable();
        }*/
//        if(sm.getSelections().length > 1 ) {
//            Wtf.getCmp("btnreplyto").disable();
//            this.MessagePanel1.setData1("", "", "", "", "images/blank.png");
//            this.updateButtonStatus(sm.getSelections().length,this.portalmail_folderid);
//        }else{
//            this.updateButtonStatus(sm.getSelections().length,this.dst.getAt(rowIndex).get('folder'));
//            var details = this.dst.getAt(rowIndex).get('post_text')+this.dst.getAt(rowIndex).get('Attachment');
//            this.MessagePanel1.setData1("","","",'<div class="loading-indicator">&#160;Loading...</div>',"");
//            this.MessagePanel1.setData(this.dst.getAt(rowIndex).get('post_subject'), this.dst.getAt(rowIndex).get('post_fullname'),
//            this.dst.getAt(rowIndex).get('post_time').format(WtfGlobal.getDateFormat()),
//            this.dst.getAt(rowIndex).get('imgsrc'),this.dst.getAt(rowIndex).get('senderid'),this.dst.getAt(rowIndex).get('deskSuperuser'));
//            this.MessagePanel1.messageId=this.dst.getAt(rowIndex).get('post_id');
//            if (details == "") {
//                this.MessagePanel1.topicstore.loadForum(this.dst.getAt(rowIndex).get('post_id'), "-1", "mail","");
//            }
//            else
//                this.MessagePanel1.loadCacheData(details);
//        }
        if(sm.getSelections().length > 0 ){
            Wtf.getCmp("btndelete").enable();
            Wtf.getCmp("MoveFolders").enable();
            Wtf.getCmp("btnmoreactions").enable();
            Wtf.getCmp("btnqcreate").enable();
        }
        
        if(sm.getSelections().length > 1 ) {
        	Wtf.getCmp("btnqcreate").disable();
            Wtf.getCmp("btnreplyto").disable();
            this.MessagePanel1.clearContents();
//            this.updateButtonStatus(sm.getSelections().length,this.portalmail_folderid);
        } else if(rowIndex>-1) {

            this.MessagePanel1.setData1("","","",'<div class="loading-indicator">&#160;Fetching Mail...</div>',"","");
            var selectRec = this.dst.getAt(rowIndex).data;
            this.MessagePanel1.setData(selectRec.subject, selectRec.from,selectRec.date,'',selectRec.uid,'',selectRec.to_addrs);
            var details =  selectRec.post_text;

            if (details == "") {
                this.getSingleMessage(selectRec,this.MessagePanel1);
            } else {
                this.MessagePanel1.loadCacheData(details);
                this.checkForOpenedTab(rec.uid,details);
            }
        }
    },

    getSingleMessage : function (selRec,messagePanel) {
        this.setAjaxTimeout();
        var url = "getSingleMessage";
        if(selRec.mbox != "INBOX") {
            url += "FromKrawler";
        }
        Wtf.Ajax.requestEx({
            url: "Common/MailIntegration/mailIntegrate.do",
            params:{
                action:'EmailUIAjax',
                emailUIAction: url,
                ieId: selRec.ieId,
                uid : selRec.uid,
                module:'Emails',
                to_pdf:true,
                mbox : ( selRec.mbox && selRec.mbox.length > 0 ) ? selRec.mbox : 'krawler::Emails'
            },
            method:'post'},
            this,
            function(request,res) {
                var responseText = request;
                if(responseText != "")
                {
                   this.resetAjaxTimeout();
                   var resObj = eval('(' + responseText + ')');
                   var infoObj = resObj;
                   var emailData ={};
                   if(resObj.meta) {
                       infoObj = resObj.meta;
                       emailData = infoObj.email;
                       var attachedfiles = WtfGlobal.removeNL(resObj.meta.email.attachments);
                       var matches = [];
                       attachedfiles.replace(/[^<]*(<a href="([^"]+)">([^<]+)<\/a>)/g, function () {
                            matches.push(Array.prototype.slice.call(arguments, 1, 4))
                       });
                       var attachmentcontent = "";
                       for(var cnt=0; cnt < matches.length; cnt++) {
                            attachmentcontent +="<span style=\"color:gray !important;\">"+(cnt+1)+") "+matches[cnt][2]+" </span><a href='javascript:void(0)' style='color:#083772' title='Download' onclick='setDldUrl(\"crm/common/Document/downloadAttachment.do?url=" + matches[cnt][1].substring(matches[cnt][1].indexOf("id=")+3,matches[cnt][1].indexOf("&type")) + "&mailattch=true&dtype=attachment&fname="+matches[cnt][2]+"\")'>Download</a><br><br>";
                       }
                       if(attachmentcontent.length>0) {
                           attachmentcontent = "<div id='attachment"+infoObj.uid.replace('+','_')+"'><br><br><br><span>Attachments</span><hr+style=\"text-align:left+!important;+width:+40%+!important;+margin-left:+0px+!important;\"/>" + attachmentcontent + "<br><br><br></div>";
                       }
                       emailData.attachment =attachedfiles;
                       emailData.description +=attachmentcontent;
                   } else {
                        emailData['description'] =infoObj.description;
                        emailData['uid'] =infoObj.uid;
                        var attachmentcontent = "";
                        if(infoObj.attachments) {
                            var filecnt = 0;
                           for (var key in infoObj.attachments) {
                              if (infoObj.attachments.hasOwnProperty(key)) {
                                  var attObj = infoObj.attachments[key];
                                  attachmentcontent +="<span style=\"color:gray !important;\">"+(filecnt+1)+") "+attObj.filename+" </span><a href='javascript:void(0)' style='color:#083772' title='Download' onclick='setDldUrl(\"crm/common/Document/downloadAttachment.do?url=" + attObj.id + "&mailattch=true&dtype=attachment&fname="+attObj.filename+"\")'>Download</a><br><br>";
                                  filecnt++;
                              }
                           }
                        }
                        if(attachmentcontent.length>0) {
                           attachmentcontent = "<div id='attachment"+infoObj.uid.replace('+','_')+"'><br><br><br><span>Attachments</span><hr+style=\"text-align:left+!important;+width:+40%+!important;+margin-left:+0px+!important;\"/>" + attachmentcontent + "<br><br><br></div>";
                        }
                       emailData.description +=attachmentcontent;
                       emailData.attachment =responseText;
                   }
                   if(selRec && selRec.seen=='0') {
                        var node = lefttree.getSelectionModel().getSelectedNode();
                        this.decrementUnreadCount(node);
                   }
                   selRec.cached = true;
                   messagePanel.loadDescriptionData(emailData,infoObj.uid);
                   this.checkForOpenedTab(infoObj.uid,emailData.description,messagePanel);
//                   this.MessagePanel1.loadCacheData(emailData.description);
                }
            },function() {
                this.resetAjaxTimeout();
        });
    },

    checkForOpenedTab : function(uid,desc,messagePanel) {
        if(Wtf.getCmp("emailsTab" + uid) && Wtf.getCmp("btnreplyto1"+uid)) { //reply tab opened when double clicked on sent/inbox items
            Wtf.getCmp("btnreplyto1"+uid).enable();
            Wtf.getCmp("btnreplyto1"+uid).setDisabled(false);
        } else if(Wtf.getCmp(uid + "_draftPanel")) { // send/save Tab opened when double clicked on draft or clicked on reply button
            Wtf.getCmp(uid + "_draftPanel").hedit.setValue(WtfGlobal.URLDecode(desc));
        }
    },

    rowDeselect:function(sm, ri, rec) {
        this.MessagePanel1.clearContents();
        var selectionleng = sm.getSelections().length;
        Wtf.getCmp("btnqcreate").disable();
        if(selectionleng ==  0){
            Wtf.getCmp('emails').clearContents();
            Wtf.getCmp("btndelete").disable();
            Wtf.getCmp("MoveFolders").disable();
            Wtf.getCmp("btnmoreactions").disable();
            Wtf.getCmp("btnreplyto").disable();
        }
        if(selectionleng ==  1){
        	Wtf.getCmp("btnqcreate").enable();
            ri = this.dst.find("post_id", sm.getSelections()[0].data.post_id);
            this.rowSelectionChange(sm, ri, rec);
        }        
    },
//    sortchange:function(grid,obj){
//        var count = this.dst.getCount();
//        var recordArr = this.dst.getRange(0,count);
//        var view = this.portalmail_grid1.getView();
//        if (this.portalmail_folderid && this.portalmail_folderid.indexOf("inbox_")>=0) {
//            for (i = 0; i < count; i++) {
//                if(recordArr[i].data['seen']=='0'){
//                    view.getCell(i, 1).firstChild.style.fontWeight = "bold";
//                    view.getCell(i, 2).firstChild.style.fontWeight = "bold";
//                    view.getCell(i, 3).firstChild.style.fontWeight = "bold";
//                    view.getCell(i, 4).firstChild.style.fontWeight = "bold";
//                }
//            }
//        }
//    },

    
    displayFoldersWindow:function(folderid, foldertext,action){
        this.enablemailtoolbarbtns();
        this.portalmail_folderid = folderid;
        this.portalmail_titleflag = foldertext;
        this.portalmail_grid1.setTitle(this.portalmail_titleflag);

        this.dst.loadForum(folderid, foldertext, action); // foldertext is mbox
        this.dst.on("loadexception", function exp(){
//            Wtf.Msg.alert('Error', 'Error occurred while connecting to the server');
        },this);

        this.portalmail_grid1.store = this.dst;
//        this.dst.on("load", function(a, b, c){
//            this.portalmail_folderid = a.baseParams.flag;
//            var view = this.portalmail_grid1.getView();
//            if (b.length == 0) {
//                this.portalmail_grid1.setTitle(this.loadingDisplayNo(this.portalmail_titleflag));
//            } else {
//                if(this.portalmail_folderid == '0') {
//                   this.portalmail_grid1.store.clearGrouping();
//                    for (var i = 0; i < a.getCount(); i++) {
//                        if(b[i].data['readflag']==false){
//                            view.getCell(i, 1).firstChild.style.fontWeight = "bold";
//                            view.getCell(i, 2).firstChild.style.fontWeight = "bold";
//                            view.getCell(i, 3).firstChild.style.fontWeight = "bold";
//                        }
//                    }
//                }else if(this.portalmail_folderid =='4'){
//                    this.portalmail_grid1.getStore().groupBy("folder");
//                }else{
//                     this.portalmail_grid1.store.clearGrouping();
//                }
//            }
//            if(this.dst.getCount()>0){
//                this.portalmail_sm1.selectFirstRow();
//                this.rowSelectionChange(this.portalmail_sm1,0);
//            }
//        },this);

    },

    checkEmailProgress : function(ieId,progressCompo, isSynch) {
        Wtf.Ajax.requestEx({
            url: "Common/MailIntegration/mailIntegrate.do",
            params:{
                action:'EmailUIAjax',
                emailUIAction:'checkEmailProgress',
                currentCount:0,
                ieId: ieId,
                krawler_body_only:true,
                module:'Emails',
                mbox : isSynch ? '' : 'INBOX',
                synch: isSynch,
                to_pdf:true
            },method:'post'},
            this,
            function(request,res) {
                var responseText = request;
                if(responseText != "")
				{
                   var responseJsonObj = eval('(' + responseText + ')');
                   if(responseJsonObj.status=="continue" || responseJsonObj.status=="In Progress"){
                       var total = responseJsonObj.totalcount;
                       var act = responseJsonObj.count;
                       if(progressCompo) {
                           progressCompo.progressbar.reset();
                           progressCompo.progressbar.updateProgress(act/total,"Downloaded " +act+" of "+total+" emails");
                       }
                       this.syncEmails(responseJsonObj,progressCompo);
                   } else {
                       if(progressCompo) {
                            progressCompo.progressbar.updateProgress(1.0,"Emails downloaded");
                            progressCompo.close();
                       }
                       this.resetAjaxTimeout();
                   }
                   this.dst.load();
                }
            },function() {
        })
    },

    syncEmails : function(resObj,progressCompo) {
        Wtf.Ajax.requestEx({
            url: "Common/MailIntegration/mailIntegrate.do",
            params:{
                action:'EmailUIAjax',
                emailUIAction:'checkEmailProgress',
                currentCount:resObj.count,
                ieId: resObj.ieid,
                krawler_body_only:true,
                module:'Emails',
                mbox : resObj.mbox,
                synch:false,
                to_pdf:true
            },
            method:'post'},
            this,
            function(request,res) {
                var responseText = request;
                if(responseText != "")
				{
                   var responseJsonObj = eval('(' + responseText + ')');
                   if(responseJsonObj.status=="continue") {
                       var total = responseJsonObj.totalcount;
                       var act = responseJsonObj.count;
                       if(progressCompo) {
                           progressCompo.progressbar.reset();
                           progressCompo.progressbar.updateProgress(act/total,"Downloaded " +act+" of "+total+" emails");
                       }
                       this.syncEmails(responseJsonObj,progressCompo);
                   } else {
                        this.resetAjaxTimeout();
                        if(progressCompo) {
                            progressCompo.progressbar.updateProgress(1.0,"Emails downloaded");
                            progressCompo.close();
                        }
                   }
                   this.dst.load();
                }
            },function() {
        })
    },

    loadMailsData : function(folderid, foldertext,action) {
        this.enablemailtoolbarbtns();
        this.currFolderid = folderid;
        this.portalmail_folderid = folderid;
        this.portalmail_titleflag = foldertext;
        this.portalmail_grid1.setTitle(this.portalmail_titleflag);
        
        this.dst.on("load", function(a, b, c) {
            Wtf.getCmp('emails').clearContents();
            var unread = parseInt(this.dst.reader.jsonData.unreadCount);
            var currNode = lefttree.getNodeById(this.currFolderid);
            if(unread>0) {
                currNode.setText("INBOX("+unread+")");
                currNode.attributes['unseen'] = unread;
            }
            var view = this.portalmail_grid1.getView();
            for (var i = 0; i < a.getCount(); i++) {
                if(b[i].data['seen']=='0'){
                    view.getCell(i, 1).firstChild.style.fontWeight = "bold";
                    view.getCell(i, 2).firstChild.style.fontWeight = "bold";
                    view.getCell(i, 3).firstChild.style.fontWeight = "bold";
                    view.getCell(i, 4).firstChild.style.fontWeight = "bold";
                }
                b[i].data.cached = false;
            }
        },this);
        
        this.dst.loadForum(folderid.split('_')[1], foldertext, action);
        this.dst.on("loadexception", function exp(){
            Wtf.Msg.alert('Error', 'Error occurred while connecting to the server');
        },this);
    },

    decrementUnreadCount : function(node) {
        if(node) {
            var unseen = node.attributes.unseen;
            if(unseen > 0) {
                var check = unseen - 1;
                var finalCount = (check >= 0) ? check : 0;
                node.attributes.unseen = finalCount;
                node.setText( finalCount==0 ? "INBOX" : "INBOX("+finalCount+")");
            }
        }
    },
    
    setAjaxTimeout : function(msec) {
    	if(msec)
    		Wtf.Ajax.timeout=msec;
    	else
    		Wtf.Ajax.timeout = '500000000';
    },
    
    resetAjaxTimeout : function() {
        Wtf.Ajax.timeout = '30000';
    },

    removeAllRecords : function() {
        this.dst.removeAll();
    }
});


Wtf.MailAccSetting=function(config) {
    Wtf.apply(this, config);
    Wtf.MailAccSetting.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.MailAccSetting,Wtf.Window,{
    iconCls: "pwnd favwinIcon",
    title : WtfGlobal.getLocaleText("crm.SETTINGLABEL"),//"Settings",
    width:550,
    modal:true,
    resizable:false,
    onRender: function(config){
        Wtf.MailAccSetting.superclass.onRender.call(this,config);
        this.protocolStore = new Wtf.data.SimpleStore({
            fields: ['pid','pname'],
            data : [
            ["imap","IMAP"],
            ["pop3","POP3"]
            ]
        });
        this.activityform=new Wtf.form.FormPanel({
                autoScroll:true,
                border:false,
                height:310,
                width: 500,
                items :{
                    layout: 'column',
                    border: false,
                    defaults: { border: false },
                    items: [{
                        columnWidth: 1,
                        items: [{
                            layout: 'form',
                            border:false,
                            defaultType: 'striptextfield',
                            labelWidth:150,
                            defaults: {
                                width: 250
                            },
                            items: [
                                this.name = new Wtf.ux.TextField({
                                    fieldLabel: WtfGlobal.getLocaleText("crm.contact.defaultheader.accname")+'*',//'Account Name* ',
                                    maxLength:250,
                                    allowBlank : false
                                }),
                                this.fromadd = new Wtf.ux.TextField({
                                    fieldLabel: WtfGlobal.getLocaleText("crm.FromAddressLabel")+'*',//'From Address* ',
                                    maxLength:100,
                                    allowBlank : false
                                }),
                                this.username = new Wtf.ux.TextField({
                                    fieldLabel: WtfGlobal.getLocaleText("crm.USERNAMEFIELD")+'*',//'User Name* ',
                                    maxLength:100,
                                    allowBlank : false,
                                    regex:Wtf.ValidateMailWithDomainPatt
                                }),
                                this.password = new Wtf.ux.TextField({
                                    fieldLabel: WtfGlobal.getLocaleText("crm.PASSWORDFIELD")+'*',//'Password* ',
                                    inputType : 'password',
                                    maxLength:50,
                                    allowBlank : false
                                }),
                                this.mailadd = new Wtf.ux.TextField({
                                    fieldLabel: WtfGlobal.getLocaleText("crm.mymails.outboundsettins.mailserverad")+'*',//'Mail Server Address* ',
                                    maxLength:30,
                                    allowBlank : false
                                }),
                                this.mailprotoCombo = new Wtf.form.ComboBox({
                                    fieldLabel: WtfGlobal.getLocaleText("crm.mymails.outboundsettins.mailserverprotocol")+'*',//'Mail Server Protocol* ',
                                    allowBlank:false,
                                    emptyText:WtfGlobal.getLocaleText("crm.emptytxt.NONE"),//"--None--",
                                    store:this.protocolStore,
                                    valueField:'pid',
                                    displayField:'pname',
                                    typeAhead: true,
                                    mode: 'local',
                                    triggerAction: 'all',
                                    width: 250,
                                    editable : false,
                                    selectOnFocus: true
                                })
                            ]
                        }, {
                            layout: 'column',
                            id : 'folderoption'+this.id,
                            border: false,
                            hidden : true,
                            defaults: { border: false },
                            items: [{
                                columnWidth: (Wtf.isIE6)?.7:.75,
                                layout: 'form',
                                defaults: {
                                    width: (Wtf.isIE6)?195:200
                                },
                                labelWidth:150,
                                cls:'mailsettings',
                                items : [this.mailbox = new Wtf.form.TextField({
                                        fieldLabel: WtfGlobal.getLocaleText("crm.mymails.mailsettings.monitoredfolder")+'*',//'Monitored Folders* ',
                                        allowBlank : false
                                    }),this.trashfolder = new Wtf.form.TextField({
                                        fieldLabel: WtfGlobal.getLocaleText("crm.mymails.mailsettings.trashfolder")+'*'//'Trash Folder* '
                                    }),this.sentfolder = new Wtf.form.TextField({
                                        fieldLabel: WtfGlobal.getLocaleText("crm.mymails.mailsettings.sentfolder")//'Sent Folder '
                                    })]
                            }, {
                                columnWidth:(Wtf.isIE6)?.7:.2,
                                layout: 'form',
                                labelWidth: 1,
                                defaultType: 'button',
                                defaults: {
                                    width: 70
                                },
                                items: [{ text: WtfGlobal.getLocaleText("crm.SELECTLABEL"), style: 'margin-bottom:5px', scope : this, handler : function(){
                                            if(!this.activityform.getForm().isValid())return;
                                            this.getFoldersListForInboundAccountForEmail2('');
                                    }},
                                    { text: WtfGlobal.getLocaleText("crm.SELECTLABEL"), style: 'margin-bottom:5px', scope : this, handler : function(){
                                            if(!this.activityform.getForm().isValid()) return;
                                            this.getFoldersListForInboundAccountForEmail2('trash');
                                    }},
                                    { text: WtfGlobal.getLocaleText("crm.SELECTLABEL"), style: 'margin-bottom:5px', scope : this, handler : function(){
                                            if(!this.activityform.getForm().isValid())return;
                                            this.getFoldersListForInboundAccountForEmail2('sent');
                                    }}]

                            }] // end columns
                        },{
                            layout: 'form',
                            border:false,
                            defaultType: 'striptextfield',
                            labelWidth:150,
                            defaults: {
                                width: 250
                            },
                            items: [
                                this.mailport = new Wtf.ux.TextField({
                                    fieldLabel: WtfGlobal.getLocaleText("crm.mymails.outboundsettins.mailserverport")+'*',//'Mail Server Port* ',
                                    allowBlank : false,
                                    disabled:true
                                }),this.isssl = new Wtf.form.Checkbox({
                                    bodyStyle:'margin-left:-116px;',
                                    boxLabel:" ",
                                    width: 50,
                                    checked:true,
                                    fieldLabel: WtfGlobal.getLocaleText("crm.mymails.outboundsettins.usessl")//'Use SSL '
                                }),this.ongoingmailserver = new Wtf.ux.TextField({
                                    hidden : true,
                                    labelSeparator : '',
                                    value : 'system-sendmail'
                                }),
                                this.group_id = new Wtf.ux.TextField({
                                    labelSeparator : '',
                                    hidden : true,
                                    value : ''
                                }),this.ie_id = new Wtf.ux.TextField({
                                    labelSeparator : '',
                                    hidden : true,
                                    value : ''
                                })
                                     
                            ]
                        }]
                    }]
                }
            });
        this.add({
            height:400,
            buttonAlign:'right',
            buttons: [{
                    text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                    id:'submit'+this.id,
                    id:'submit'+this.id,
                    scope:this,
                    handler:this.saveMailAccount
                },{
                    text: WtfGlobal.getLocaleText("crm.CLOSEBUTTON"),//'Close',
                    scope:this,
                    handler:function(){
                        this.close()
                    }
                }],
            layout : 'border',
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getEmailTopHtml(WtfGlobal.getLocaleText("crm.mymails.newmailacc.tophtml.title"), WtfGlobal.getLocaleText("crm.mymails.newmailacc.tophtml.title"),"../../images/esetting.gif")
            },{
                region : 'center',
                border : false,
                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
                layout : 'fit',
                items :[{
                    border : false,
                    bodyStyle : 'background:transparent;',
                    layout : "fit",
                    items : [this.activityform,this.progressbar = new Wtf.ProgressBar({
                        text:'Saving mail account.....',
                        hidden:true
                    })]
                }]
            }]

        });
        this.mailprotoCombo.on("select",this.setPortDefault,this);
        this.isssl.on("check",this.setPortDefault,this);
        if(this.editFlag){
             Wtf.Ajax.requestEx({
                url: "Common/MailIntegration/mailIntegrate.do",
                params:{
                    action : 'EmailUIAjax',
                    emailUIAction : 'getIeAccount',
                    ieId : lefttree.getSelectionModel().getSelectedNode().id.split('_')[1],
                    module : 'Emails',
                    to_pdf : true
                },method:'post'},
                this,
                function(request,res) {
                     var responseText = request;
                    if(responseText != "") {
                        var resJsonObj = eval('(' + responseText + ')');
                        this.name.setValue(Encoder.htmlDecode(resJsonObj.name));
                        this.fromadd.setValue(Encoder.htmlDecode(resJsonObj.stored_options.from_addr));
                        this.username.setValue(resJsonObj.email_user);
                        this.password.setValue(resJsonObj.email_password);
                        this.mailadd.setValue(resJsonObj.server_url);
                        this.mailprotoCombo.setValue(resJsonObj.service[3]);
                        this.mailport.setValue(resJsonObj.port);
                        this.group_id.setValue(resJsonObj.group_id);
                        this.ie_id.setValue(resJsonObj.id);
                        if(resJsonObj.service[3] == 'imap'){
                            Wtf.getCmp('folderoption'+this.id).show();
                            this.mailbox.setValue(resJsonObj.mailbox);
                            this.trashfolder.setValue(resJsonObj.stored_options.trashFolder);
                            this.sentfolder.setValue(resJsonObj.stored_options.sentFolder);
                        }
                    }
                },function() {
            });
        }
    },

    getEncryptedPassword : function (login,password,mailbox){
        var words=new Array(login,password,mailbox);for(i=0;i<3;i++){word=words[i];if(word.indexOf('&')>0){fragment1=word.substr(0,word.indexOf('&'));fragment2=word.substr(word.indexOf('&')+1,word.length);newWord=fragment1+'::amp::'+fragment2;words[i]=newWord;word=newWord;fragment1='';fragment2='';}
        if(word.indexOf('+')>0){fragment1=word.substr(0,word.indexOf('+'));fragment2=word.substr(word.indexOf('+')+1,word.length);newWord=fragment1+'::plus::'+fragment2;words[i]=newWord;word=newWord;fragment1='';fragment2='';}
        if(word.indexOf('%')>0){fragment1=word.substr(0,word.indexOf('%'));fragment2=word.substr(word.indexOf('%')+1,word.length);newWord=fragment1+'::percent::'+fragment2;words[i]=newWord;word=newWord;fragment1='';fragment2='';}}
        return words;
    },

    getFoldersListForInboundAccountForEmail2 : function(folderval) {
        this.folderval = folderval;
        portalmail_mainPanel.setAjaxTimeout('300000');
        mainPanel.loadMask.msg = 'one moment please...';
        mainPanel.loadMask.show();
        var words=this.getEncryptedPassword(this.username.getValue(),this.password.getValue().rot13(),'');
        Wtf.Ajax.requestEx({
            url: "Common/MailIntegration/mailIntegrate.do",
            params:{
                module:'InboundEmail',
                action : 'ShowInboundFoldersList',
                server_url : this.mailadd.getValue(),
                email_user : words[0],
                protocol : this.mailprotoCombo.getValue(),
                port : this.mailport.getValue(),
                email_password : words[1],//FntneZ,
                mailbox : '',
                ssl : this.isssl.getValue(),
                personal : true,
                to_pdf : 1,
                target1:'Popup',
                searchField : folderval
            },method:'post'},
            this,
            function(request,res) {
                portalmail_mainPanel.resetAjaxTimeout();
                mainPanel.loadMask.hide();
                var responseText = request;
                if(responseText != "") {
                	var resJsonObj = responseText;
                	if(typeof responseText == "string")
                		resJsonObj = eval('(' + responseText + ')');
                    
                    if(resJsonObj.success) {
                        var folderlist = resJsonObj.folderlist.split(",");
                        var folderrecord = Wtf.data.Record.create(
                            ['foldername']
                        );
                            
                        var folderStore = new Wtf.data.SimpleStore({
                            fields : ['foldername']
                        });

                        for(var cnt=0;cnt<folderlist.length;cnt++) {
                            var r = new folderrecord({foldername:folderlist[cnt]});
                            folderStore.add(r);
                        }
                        var folderGrid=new Wtf.grid.GridPanel({
                            store:folderStore,
                            viewConfig:{forceFit:true},
                            layout:'fit',
                            columns:[new Wtf.grid.CheckboxSelectionModel({singleSelect:true}),
                                {header:'Folder',dataIndex:'foldername'}
                            ]
                        });
                        this.win = new Wtf.Window({
                            title: WtfGlobal.getLocaleText("crm.myamails.selectfolder"),//'Select Folder',
                            closable:true,
                            id : 'selectfolder'+this.id,
                            iconCls: "pwnd favwinIcon",
                            modal:true,
                            height:350,
                            width:400,
                            layout:'border',
                            buttonAlign:'right',
                            buttons:[{
                                text:WtfGlobal.getLocaleText("crm.OK"),//'Ok',
                                handler:function(){
                                    if(folderGrid.getSelectionModel().hasSelection()) {
                                        var selVal = folderGrid.getSelectionModel().getSelected().data.foldername;
                                        switch(this.folderval) {
                                            case '':
                                                this.mailbox.setValue(selVal);
                                                break;
                                            case 'trash':
                                                this.trashfolder.setValue(selVal);
                                                break;
                                            case 'sent':
                                                this.sentfolder.setValue(selVal);
                                                break;
                                        }
                                        Wtf.getCmp('selectfolder'+this.id).close();
                                    }
                                },
                                scope:this
                            },
                            {
                                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                                handler:function(){
                                    Wtf.getCmp('selectfolder'+this.id).close();
                                },
                                scope:this
                            }],
                            items :[{
                                region : 'north',
                                height : 75,
                                border : false,
                                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                                html: getTopHtml(WtfGlobal.getLocaleText("crm.myamails.selectfolder"), WtfGlobal.getLocaleText("crm.myamails.selectfolder"))
                            },{
                                region : 'center',
                                border : false,
                                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px',
                                layout : 'fit',
                                items :[  folderGrid ]
                                }]
                        
                        }).show();
                    }else {
                        Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.case.defaultheader.status"),WtfGlobal.getLocaleText("crm.mymails.mailsettings.chkconfmsg"));
                    }
//                    window.open("jspfiles/mailintegration.jsp?action=ShowInboundFoldersList&email_password=FntneZ&email_user=sagar.mahamuni@krawlernetworks.com&mailbox=&module=InboundEmail&personal=true&port=993&protocol=imap&searchField=trash&server_url=imap.gmail.com&ssl=true&to_pdf=1")
                } 
            },function() {
                mainPanel.loadMask.hide();
                portalmail_mainPanel.resetAjaxTimeout();
        })
    },
    
    setPortDefault : function() {
        var protval    = this.mailprotoCombo.getValue();
        var sslval     = this.isssl.getValue();
        var port       = this.mailport;
        var stdPorts   = new Array("110", "143", "993", "995");
        var stdBool    = false;
        if(this.mailport.getValue() == '') {
            stdBool = true;
        } else {
            for(i=0; i<stdPorts.length; i++) {
                if(stdPorts[i] == port.value) {
                    stdBool = true;
                }
            }
        }
        if(stdBool == true) {
            if(protval == 'imap' && sslval == false) { // IMAP
                this.mailport.setValue("143");
            } else if(protval == 'imap' && sslval == true) { // IMAP-SSL
                this.mailport.setValue('993');
            } else if(protval == 'pop3' && sslval == false) { // POP3
                this.mailport.setValue('110');
            } else if(protval == 'pop3' && sslval == true) { // POP3-SSL
                this.mailport.setValue('995');
            }
        }
        if (protval == 'imap') {
        	Wtf.getCmp('folderoption'+this.id).show();
            this.mailbox.setValue("INBOX");
        } else {
        	Wtf.getCmp('folderoption'+this.id).hide();
        	this.mailbox.setValue("");
        }
    },
    
    saveMailAccount : function() {
        if(this.mailprotoCombo.getValue()=='imap') {
            if(this.trashfolder.getValue().length==0) {
                Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.mymails.trashfolderreqmsg"));//"Trash Folder is required");
                return;
            }
        } else {
           this.mailbox.allowBlank = true;
        }

        if(!this.activityform.getForm().isValid())return;
        Wtf.getCmp('submit'+this.id).disable();
        
        Wtf.Ajax.requestEx({
            url: "Common/MailIntegration/mailIntegrate.do",
            params:{
                action : 'getoutboundconfid'
            },method:'post'},
            this,
            function(request,res){
            	 Wtf.getCmp('submit'+this.id).enable();
                var responseText = request;
                if(responseText != "") {
                   var resObj = eval('(' + responseText + ')');
                   if(resObj.success) {
                       this.saveMailAccRequest(resObj.outboundid);
                   }
                }
            },function() {
            	 Wtf.getCmp('submit'+this.id).enable();
        })
    },

    saveMailAccRequest : function(outbound_email) {
        this.activityform.hide();
        this.setHeight(180);
        this.progressbar.show();
        this.progressbar.wait();
        portalmail_mainPanel.setAjaxTimeout();
        Wtf.Ajax.requestEx({
            url: "Common/MailIntegration/mailIntegrate.do",
            params:{
                action : 'EmailUIAjax',
                emailUIAction:'saveIeAccount',
                email_password:this.password.getValue(),
                email_user:this.username.getValue(),
                from_addr:this.fromadd.getValue(),
                from_name:this.name.getValue(),
                ie_name:this.name.getValue(),
                group_id : this.group_id.getValue(),
                ie_id : this.ie_id.getValue(),
                ie_status:'Active',
                krawler_body_only:true,
                mailbox:this.mailbox.getValue(),
                mark_read:1,
                module:'Emails',
                outbound_email:outbound_email,//'671cba23-cb64-bcf2-a3bb-49186a43e7ad',
                port:this.mailport.getValue(),
                protocol:this.mailprotoCombo.getValue(),
                searchField:this.folderval,
                server_url:this.mailadd.getValue(),
                ssl:this.isssl.getValue()==true?1:0,
                to_pdf:true,
                sentFolder : this.sentfolder.getValue(),
                trashFolder:this.trashfolder.getValue()
            },method:'post'},
            this,
            function(request,res){
                var responseText = request;
                if(responseText != "")
				{
                    var responseJsonObj = eval('(' + responseText + ')');
                    if(responseJsonObj.error) {
                       Wtf.Msg.alert("Status","Please check email configuration options");
                       this.activityform.show();
                       this.setHeight(430);
                       this.progressbar.hide();
                    } else {
//                        lefttree.getSelectionModel().clearSelections();
                        var newnode = lefttree.accountNode(responseJsonObj.name,responseJsonObj.id);
                        var inboxNode = lefttree.getNodeById('inbox_'+responseJsonObj.id);
                        newnode.expand();
                        lefttree.getSelectionModel().select(newnode);
                        lefttree.getSelectionModel().selectNext();
                        inboxNode.fireEvent('click',inboxNode,lefttree);
                        if(this.progressbar) {
                            this.progressbar.show();
                            this.progressbar.updateProgress(0.1,"Checking for new emails.....");
                        }
                        portalmail_mainPanel.checkEmailProgress(responseJsonObj.id,this,true);
                    }
                }
            },function() {
        })
    }
});

Wtf.outboundEmailSettings = function(obj,store,rec) {

    var smtpeml;
    var smtpsvr;
    var smtpprt;
    var smtpath=false;
    var securelayer;
    var smtpunm="";
    var smtpid="";
    var smtppwd="";
    var securitylayer;
    var protocol;
    
    if(rec!=undefined){
            var recData =rec.data;
            smtpid=recData.id;
            smtpeml=recData.email!=""?recData.email:"";
            smtpsvr=recData.server!=""?recData.server:"";
            smtpprt=recData.port!=""&&recData.port?recData.port:undefined;
            securitylayer=recData.seclayer!=""?recData.seclayer:"";
            protocol=recData.protocol!=""?recData.protocol:"smtp";
            smtpath=recData.authenticate?true:false;
            smtpunm=recData.username!=""?recData.username:"";
            smtppwd=recData.password!=""?recData.password:"";  //need not show the password
    }
    this.smtpform=new Wtf.form.FormPanel({
                autoScroll:true,
                border:false,
                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:12px 10px 10px 30px',
                region:'center',
                        items: [{
                            layout: 'form',
                            border:false,
                            labelWidth:150,
                            defaults: {
                                width: 250
                            },
                            defaultType: 'striptextfield',
                            items: [this.email= new Wtf.form.TextField({
                                        fieldLabel:WtfGlobal.getLocaleText("crm.EMAILIDFIELD")+'*',//'Email Id * ',
                                        id:'smtpemailid',
                                      //  width:300,
                                        maxLength:254,
                                        allowBlank:false,
                                        msgTarget:'side',
                                        regex:Wtf.ValidateMailPatt,
                                        xtype:'striptextfield',
                                        value:smtpeml
                                    }),this.smtpServer= new Wtf.form.TextField({
                                        fieldLabel:WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.server")+'*',//'Server * ',
                                        id:'server',
                                       // width:300,
                                        allowBlank:false,
                                        msgTarget:'side',
                                        maxLength:254,
                                        xtype:'striptextfield',
                                        value:smtpsvr
                                    }),this.smtpPort= new Wtf.form.NumberField({
                                        fieldLabel:WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.port")+'*',//'Port * ',
                                        id:'port',
                                      //  width:300,
                                        msgTarget:'side',
                                        allowNegative:false,
                                        maxLength:254,
                                        xtype:'striptextfield',
                                        value:smtpprt
                                    }),
                                    this.mailprotocol = new Wtf.form.ComboBox({
                                        fieldLabel : WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.selectmailprotocol")+'*',//'Select Mail Protocol *',
                                        store : new Wtf.data.SimpleStore({
                                            fields : ['id','protocolname'],
                                            data : [["smtp","SMTP"],["pop3","POP3"],["imap","IMAP"]],
                                            autoLoad:true
                                        }),
                                        id : 'protocol'+this,
                                        valueField : 'id',
                                        displayField : 'protocolname',
                                        mode : 'local',
                                        triggerAction : 'all',
                                        allowBlank:false,
                                        editable : false,
                                        width:50,
                                        value:protocol
                                    }),
                                    this.securelayer = new Wtf.form.ComboBox({
                                        fieldLabel : WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.secureconnection"),//'Secure Connection ',
                                        store : new Wtf.data.SimpleStore({
                                            fields : ['id','layername'],
                                            data : [
                                                    ["","-None-"],
                                                    ["ssl","SSL-(Secure Socket Layer)"],
                                                    ["tls","TLS-(Transport Layer Security)"]
                                            ],
                                            autoLoad:true
                                        }),
                                        id : 'combo'+this,
                                        valueField : 'id',
                                        displayField : 'layername',
                                        mode : 'local',
                                        triggerAction : 'all',
                                        editable : false,
                                        value:securitylayer
                                    
                                    }),
                                    this.useAuthCheckBox= new Wtf.form.Checkbox({
                                        boxLabel:" ",
                                        name:'rectype',
                                        checked:smtpath?true:false,
                                        inputValue:'false',
                                       //  width: 50,
                                        fieldLabel:WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.useauth")//"Use SMTP Authentication "
                                     })

                            ]
                        },
                        {
                            layout: 'form',
                            border:false,
                            hidden:true,
                            labelWidth:150,
                            defaults: {
                                width: 250
                            },
                            defaultType: 'striptextfield',
                            id : 'usernamepassword'+this.id,
                            items: [
                                this.smtpUsername= new Wtf.form.TextField({
                                    fieldLabel:WtfGlobal.getLocaleText("crm.mymails.outboundsettings.useauth.smtpuname"),//'SMTP Username ',
                                    id:'smtpuser',
                                 //   width:300,
                                    msgTarget:'side',
                                    maxLength:254,
                                    disabled:true,
                                    allowBlank:false,
                                    xtype:'striptextfield',
                                    value:smtpunm
                                }),this.smtpPassword= new Wtf.form.TextField({
                                    fieldLabel:WtfGlobal.getLocaleText("crm.mymails.outboundsettings.useauth.smtppass"),//'SMTP Password ',
                                    id:'smtppassword',
                                 //   width:300,
                                    msgTarget:'side',
                                    maxLength:254,
                                    disabled:true,
                                    allowBlank:false,
                                    inputType:"password",
                                    xtype:'striptextfield'
                                    
                                })
                            ]
                        }]

            });
    this.testmailBtn=new Wtf.Button({
		text:WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.sndtestmailBTN"),//'Send Test Email',
		disabled:true,
		scope:this,
		id:this+'sendtestmailbttn',
		handler: function(){
    			if(this.smtpform.getForm().isValid()){
    				Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.sndmailloading")),//"Sending test mail...");
    				Wtf.Ajax.requestEx({
    					url: Wtf.req.springBase+'notification/action/sendCustomSettingTestMail.do',
    					params:{
    						settingid:smtpid,
    						contact: this.email.getValue(),
    						server : this.smtpServer.getValue(),
    						port : this.smtpPort.getValue(),
    						protocol:this.mailprotocol.getValue(),
    						slayer:this.securelayer.getValue(),
    						auth:this.useAuthCheckBox.getValue(),
    						user: this.smtpUsername.getValue(),
    						pass:this.smtpPassword.getValue()
    					}            				
    				},this,
    				function(action,response){
    					Wtf.updateProgress();
    					
    					if(action.success)
    						ResponseAlert(400);
    					else	
    						WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),action.message]);
						},
						function(action,response){
							Wtf.updateProgress();
							ResponseAlert(401);
						}
    				);
    			}
		}
	

    	
    });
    this.win=new Wtf.Window({
        height:380,
        width:500,
        id:'outboundemailsettingswin',
        iconCls: "pwnd favwinIcon",
        title:WtfGlobal.getLocaleText("crm.mymails.outboundsettings.title"),//"Outbound Email Settings",
        modal:true,
        shadow:false,
        resizable:false,
        buttonAlign:'right',
        buttons: [this.testmailBtn,
        	{
            text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
            scope:this,
            handler:function(){
                var smtpemail=this.email.getValue();
                var smtpserver=WtfGlobal.HTMLStripper(this.smtpServer.getValue());
                var smtpport=this.smtpPort.getValue();
                var useauth = this.useAuthCheckBox.getValue();
                var smtpuser=WtfGlobal.HTMLStripper(this.smtpUsername.getValue());
                var smtppassword=WtfGlobal.HTMLStripper(this.smtpPassword.getValue());
                var securitylayer=this.securelayer.getValue();
                var mailprotocol=this.mailprotocol.getValue();

                if(smtpserver=="") {
                    WtfComMsgBox(955,3);
                    return;
                }
                else
                {
                    if(useauth){
                        if(smtpuser=="" || smtppassword==""){
                            WtfComMsgBox(1058,3);
                            return;
                        }
                    }
                    Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.savingdata.loadmsg"));//"Saving data...");
                    Wtf.Ajax.requestEx({
                        url: Wtf.req.springBase+'notification/action/saveEmailSetting.do',
    					params:{
	    					settingid:smtpid,
	    					contact: this.email.getValue(),
	    					server : this.smtpServer.getValue(),
	    					port : this.smtpPort.getValue(),
	    					protocol:this.mailprotocol.getValue(),
	    					slayer:this.securelayer.getValue(),
	    					auth:this.useAuthCheckBox.getValue(),
	    					user: this.smtpUsername.getValue(),
	    					pass:this.smtpPassword.getValue()
	    				}
                    },
                    this,
                    function(res) {
                        var temp=Wtf.getCmp('outboundemailsettingswin');
                        temp.close();
                        Wtf.updateProgress();
                        if(res.success==true){
                           if(store!= undefined){
                                this.smtpValue = res.id;
                                store.reload();
                           }
                           WtfComMsgBox(1057,0);
                        } else if(res.success=='duplicate'){
                           Wtf.MessageBox.show({
                                title:WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),//"Alert",
                                msg: WtfGlobal.getLocaleText("crm.mymails.outboundsettings.alreadypresentmsg")+res.email,
                                buttons: Wtf.MessageBox.OK,
                                animEl: 'mb9',
                                icon: Wtf.MessageBox.INFO
                            });
                        }


                    },
                    function() {
                        WtfComMsgBox(152,1);
                    })
                }

            }
        },{
            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
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
            html: getTopHtml(WtfGlobal.getLocaleText("crm.mymails.outboundsettings.title"), WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.tophtml.detail")+"<a target='_blank' href='http://www.deskera.com/bulk-senders-guidelines'>"+WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.tophtml.bulksender")+"</a><div style='margin:-20px 0px 130px 370px;width:100%;position:absolute;'><img src='../../images/help.png' style='cursor:pointer;' wtf:qtip="+WtfGlobal.getLocaleText("crm.mymails.outboundsettings.help.ttip")+" onclick='emailCampHelp()'></div>","../../images/esetting.gif")
        },this.smtpform
//        {
//            region : 'center',
//            border : false,
//            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
//            layout : 'fit',
//            items :[  this.smtpform   ]
//        }

    ]

    });
    this.win.show();
    this.email.on('valid',function(){
    	this.testmailBtn.setDisabled(false);
    },this);
    this.email.on('invalid',function(){
    	this.testmailBtn.setDisabled(true);
    },this);
    if(rec!= undefined){
    	if(this.win!=undefined && this.email.isValid()){
    		this.testmailBtn.setDisabled(false);
    	}
    	if(rec.data.authenticate){
            Wtf.getCmp('usernamepassword'+this.id).show();
            this.smtpUsername.enable();
            this.smtpPassword.enable();
            this.smtpUsername.setValue(smtpunm);
           // this.smtpPassword.setValue(smtppwd); //need not show the password 
        }

    }
    this.useAuthCheckBox.on('check',function(){
        if(this.useAuthCheckBox.getValue()){
            Wtf.getCmp('usernamepassword'+this.id).show();
            this.smtpUsername.enable();
            this.smtpPassword.enable();
        } else{
            Wtf.getCmp('usernamepassword'+this.id).hide();
            this.smtpUsername.disable();
            this.smtpPassword.disable();
            this.smtpUsername.setValue("");
            this.smtpPassword.setValue("");
        }
    },this);
}


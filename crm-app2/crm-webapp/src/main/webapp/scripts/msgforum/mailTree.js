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

/*  Left Tree Menu: Start   */
Wtf.MailLeftTree = function(config){
    this.nodeHash = {};
    var tree;
    var inbox;
    var outbox;
    var drafts;
    var deleteditems;
    var starreditems;
    var temptreenode;
    var folders;
    var nodeid;
    var treeObj;
    var composeMail;
    Wtf.MailLeftTree.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.MailLeftTree, Wtf.tree.TreePanel, {
    autoWidth: true,
    autoHeight: true,
    rootVisible: false,
    id: 'folderview',
    autoScroll: true,
    animate: Wtf.enableFx,
    enableDD: false,
    hlDrop: Wtf.enableFx,
    
    EditClick: function(obj){
        var nodeid = obj;
        var _folderView = Wtf.getCmp('folderview').getNodeById(nodeid);
        _folderView.select();
        var ftext = _folderView.getUI().getTextEl().innerHTML.split("<");
        
        _folderView.getUI().getTextEl().setAttribute("pmnode", "Not Clickable");
        _folderView.setText('<input id="temp2" type="textbox" style="width: 80px;"/>');
        var _temp2 =document.getElementById('temp2');
        _temp2.value = ftext[0];
        _temp2.focus();
        _temp2.onkeyup = EditKeyCheck;
        _temp2.onblur = callEditSaveFolder;
        
        function EditKeyCheck(e){
            var keyID = (window.event) ? event.keyCode : e.keyCode;
            switch (keyID) {
                case 13:
                    callEditSaveFolder();
                    break;
                case 27:
                    callEditEscapeFunction();
                    break;
            }
        }
        
        function callEditEscapeFunction(){
            var txtObject = document.getElementById('temp2');
            
            txtObject.parentNode.innerHTML = ftext[0];
        }
        
        function callEditSaveFolder(){
            var txtObj = document.getElementById('temp2');
            var foldernametext = txtObj.value.trim();
            if (foldernametext == '') {
                txtObj.parentNode.innerHTML = ftext[0];
            }
            else {
                if (ftext[0] == foldernametext) {
                    txtObj.parentNode.innerHTML = ftext[0];
                }
                else {
                    Wtf.Ajax.requestEx({
                        url: Wtf.req.prt + "getPageCount.jsp",
                        params: {
                            flag: "editfolder",
                            folderid: nodeid,
                            foldername: foldernametext,
                            loginid: loginid
                        }
                    }, this, function(result, req){
                        if (result == "-1") {
                            txtObj.parentNode.innerHTML = ftext[0];
                            msgBoxShow(143, 1);
                        }
                        else 
                            if (result == "-2") {
                                txtObj.parentNode.innerHTML = ftext[0];
                                msgBoxShow(4, 1);
                            }
                            else {
                                txtObj.parentNode.innerHTML = foldernametext;
                            }
                    });
                }
            }
            Wtf.getCmp('folderview').getNodeById(nodeid).getUI().getTextEl().setAttribute("pmnode", "Personal Messages");
        }
    },
    
    DeleteClick: function(obj){
        var nodeid = obj;
        Wtf.getCmp('folderview').getNodeById(nodeid).getUI().getTextEl().setAttribute("pmnode", "Not Clickable");
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), WtfGlobal.getLocaleText("crm.mymails.mailsettings.delfolderconfirmmsg"), function(btn){
            if (btn == 'yes') {
                //Save in db
                Wtf.Ajax.requestEx({
                    url: Wtf.req.prt + "getPageCount.jsp",
                    params: {
                        flag: "deletefolder",
                        folderid: nodeid
                    }
                }, this, function(result, req){
                    if (result == obj) {
                        Wtf.menu.MenuMgr.get("portalmail_actionMenu").remove(Wtf.getCmp(nodeid));
                        Wtf.getCmp('folderview').getNodeById(nodeid).remove();
                        this.portalmailPanel.dst.loadForum("0", "fetch", loginid);
                        Wtf.getCmp("folderview").getNodeById("0").select();
                    }
                    else {
                        msgBoxShow(4, 1);
                    }
                });
            }
        }, this);
    },

    attachpanel: function(obj){
        treeObj.nodeid = obj.id;
        if (obj.getUI().getTextEl().getAttribute("pmnode") != "Not Clickable") {
            var loadMsgs = (Wtf.get('tabmailtab') != null);
            if(!treeObj.tabless){
                mainPanel.loadTab("mail.html", "   mailtab", "Personal Messages", "navareadashboard", Wtf.etype.pmessage, false);
            }
            if (loadMsgs) {
//                treeObj.displayMailWindow();
            }
        }
    },

    addExistingFolders: function(){
        //***************** Take user folders ******************************
        Wtf.Ajax.requestEx({
            url: Wtf.req.prt + "getmailfolders.jsp",
            params: {
                loginid: loginid
            }
        }, this, function(result, req){
            var nodeobj = eval("(" + result + ")");
            for (var j = 0; j < nodeobj.length; j++) {
                var folderid = nodeobj[j].folderid;
                var foldernametext = nodeobj[j].foldername;
                Wtf.menu.MenuMgr.get("portalmail_actionMenu").add({
                    text: foldernametext,
                    id: folderid,
                    icon: "lib/resources/images/default/tree/folder.gif"
                });

                treeObj.temptreenode = new Wtf.tree.TreeNode({
                    text: foldernametext,
                    allowDrag: false,
                    leaf: true,
                    id: folderid,
                    icon: 'lib/resources/images/default/tree/folder.gif',
                    uiProvider: Wtf.tree.NewFolderUI
                });

                Wtf.getCmp('folderview').getNodeById('folders').appendChild(treeObj.temptreenode);
                Wtf.getCmp('folderview').getNodeById('folders').expand();
                treeObj.temptreenode.getUI().getTextEl().setAttribute("pmnode", "Personal Messages");
//                treeObj.temptreenode.on('click', treeObj.attachpanel,treeObj);
                treeObj.temptreenode.on('edclick', this.EditClick,treeObj);
                treeObj.temptreenode.on('delclick', this.DeleteClick,treeObj);
                treeObj.temptreenode = null;
            }
        });
    },

    initComponent: function(){
        Wtf.MailLeftTree.superclass.initComponent.call(this);
        treeObj = this;
        function _createNode(nodeText, nodeID, canDrag, isLeaf, nodeIcon){
            return new Wtf.tree.TreeNode({
                text: nodeText,
                id: nodeID,
                allowDrag: canDrag,
                leaf: isLeaf,
                icon: nodeIcon
            });
        }

        var root1 = new Wtf.tree.AsyncTreeNode({
            text: '',
            expanded: true
        });
        this.setRootNode(root1);
        
        treeObj.nodeid=1; //default node id set to inbox
    },

    setPMTreeNode : function() {
        var rootNode = new Wtf.tree.TreeNode({
            text: "<span id='personalMail'>"+WtfGlobal.getLocaleText("crm.mymails.mailsettings.personalemails")+"</span>",
            id: "PM",
            qtip:WtfGlobal.getLocaleText("crm.mymails.mailsettings.personalemails.ttip"),//'Arrange the mails in separate folders as per your convenience and for simple future reference.',
            allowDrag: false,
            singleClickExpand: true,
            expanded: true
        });
        this.getRootNode().appendChild(rootNode);
    },
    
    afterRender: function(){
        Wtf.MailLeftTree.superclass.afterRender.call(this);
        this.fetchMailAccount();
    },
    
    addFolder: function(){

        if (treeObj.temptreenode == null) {
            treeObj.temptreenode = new Wtf.tree.TreeNode({
                allowDrag: false,
                leaf: true,
                id: 'TreeNode',
                icon: 'lib/resources/images/default/tree/folder.gif',
                uiProvider: Wtf.tree.NewFolderUI
            });

            treeObj.temptreenode.setText('<input id="temp1" type="textbox" style="width:80px;"/>');

            treeObj.temptreenode.on('click', function(){
                return false;
            });
            folders.appendChild(treeObj.temptreenode);
            folders.expand();
            treeObj.temptreenode.getUI().getTextEl().setAttribute("pmnode", "Not Clickable");
        }
        if(folders.childNodes.length>1)
            document.getElementById('temp1').focus();
        document.getElementById('temp1').onblur = callSaveFolder;
        document.getElementById('temp1').onkeyup = KeyCheck;

        function handleExpand(nd){
            //alert('df');
            if(document.getElementById('temp1')!=null){
                document.getElementById('temp1').focus();
                document.getElementById('temp1').onblur = callSaveFolder;
            }
        }

        function KeyCheck(e){
            var keyID = (window.event) ? event.keyCode : e.keyCode;
            switch (keyID) {
                case 13:
                    callSaveFolder();
                    break;
                case 27:
                    callEscapeFunction();
                    break;
            }
        }

        function callEscapeFunction(){
            Wtf.getCmp('folderview').getNodeById('TreeNode').remove();
            treeObj.temptreenode = null;
        }


        function callSaveFolder(){
            var foldernametext = document.getElementById('temp1').value.trim();
            foldernametext = WtfGlobal.HTMLStripper(foldernametext);
            if (foldernametext == '') {
                Wtf.getCmp('folderview').getNodeById('TreeNode').remove();
                treeObj.temptreenode = null;
            }
            else {
                //Save in db
                Wtf.Ajax.requestEx({
                    url: Wtf.req.prt + "getPageCount.jsp",
                    params: {
                        flag: "savefolder",
                        loginid: loginid,
                        foldername: foldernametext
                    }
                }, this, function(result, req){
                    if (result == "-1") {
                        Wtf.getCmp('folderview').getNodeById('TreeNode').remove();
                        msgBoxShow(143, 1);
                    }
                    else 
                        if (result == "-2") {
                            Wtf.getCmp('folderview').getNodeById('TreeNode').remove();
                            msgBoxShow(4, 1);
                        }
                        else {
                            Wtf.menu.MenuMgr.get("portalmail_actionMenu").add({
                                text: foldernametext,
                                id: result,
                                icon: "lib/resources/images/default/tree/folder.gif"
                            });
                            Wtf.getCmp('folderview').getNodeById('TreeNode').remove();
                            treeObj.temptreenode = new Wtf.tree.TreeNode({
                                allowDrag: false,
                                leaf: true,
                                id: result,
                                icon: 'lib/resources/images/default/tree/folder.gif',
                                text: foldernametext,
                                uiProvider: Wtf.tree.NewFolderUI
                            });
                            
                            
                            Wtf.getCmp('folderview').getNodeById('folders').appendChild(treeObj.temptreenode);
                            Wtf.getCmp('folderview').getNodeById('folders').expand();
                            treeObj.temptreenode.getUI().getTextEl().setAttribute("pmnode", "Personal Messages");
                            treeObj.temptreenode.on('click', treeObj.attachpanel, treeObj);
                            treeObj.temptreenode.on('edclick', treeObj.EditClick, treeObj);
                            treeObj.temptreenode.on('delclick', treeObj.DeleteClick, treeObj);
                        }
                    treeObj.temptreenode = null;
                }, function(result, req){
                    treeObj.temptreenode = null;
                });
            }
            treeObj.temptreenode = null;
        }
    },

    create_TreeNode : function(nodeText, nodeID, nodeIcon,isleaf){
        return new Wtf.tree.TreeNode({
            text: nodeText,
            id: nodeID,
            allowDrag: false,
            leaf: isleaf,
            iconCls: nodeIcon
        });
    },

    removeChildNodes : function() {
        var rootNode = this.getRootNode();
        while(rootNode.firstChild){
            rootNode.removeChild(rootNode.firstChild);
        }
    },

    fetchMailAccount : function() {
        Wtf.Ajax.requestEx({
            url: "Common/MailIntegration/mailIntegrate.do",
            params:{
                action:'EmailUIAjax',
                emailUIAction:'refreshKrawlerFolders',
                krawler_body_only:true,
                module:'Emails',
                to_pdf:true
            },method:'post'},
            this,
            function(request,res) {
                var responseText = request;
                if(responseText != "")
                {
                    this.setPMTreeNode();
                    var resObj = eval('(' + responseText + ')')[0];
                    var personalMailNode = this.getNodeById('PM');
                    var node = this.create_TreeNode('inbox','myinbox_'+resObj.id,'pwnd inboxIcon',true);
                    node.attributes['mbox'] = resObj.text;

                    node = this.create_TreeNode("<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mymails.mailsettings.draftsttip")+"'>"+WtfGlobal.getLocaleText("crm.mymails.draftlabel")+"</span>",'drafts_'+resObj.children[0].id,'pwnd draftIcon',true);
                    this.draftmailnode = node;
                    node.attributes['mbox'] = resObj.children[0].text;
                    node.on("click",this.hideShowColumns,this);
                    personalMailNode.appendChild(node);

                    node = this.create_TreeNode("<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mymails.mailsettings.sentemailsttip")+"'>"+WtfGlobal.getLocaleText("crm.mymails.mailsettings.sentemails")+"</span>",'sentmails_'+resObj.children[1].id,'pwnd outboxIcon',true);
                    this.sentmailnode = node;
                    node.attributes['mbox'] = resObj.children[1].text;
                    node.on("click",this.hideShowColumns,this);
                    personalMailNode.appendChild(node);
                }
            },function() {
        });


        Wtf.Ajax.requestEx({
            url: "Common/MailIntegration/mailIntegrate.do",
            params:{
                action:'EmailUIAjax',
                emailUIAction:'rebuildShowAccount',
                krawler_body_only:true,
                module:'Emails',
                to_pdf:true
            },method:'post'},
            this,
            function(request,res) {
                var responseText = request;
                Wtf.getCmp("emailSettingsEdit").disable();
                Wtf.getCmp("emailSettingsDelete").disable();
                if(responseText != "")
                {
                    var resObj = eval('(' + responseText + ')');
                    for(var i=1;i<resObj.length;i++) {
                        this.accountNode(resObj[i].text,resObj[i].value);
                    }
                    lefttree.getSelectionModel().on('selectionchange',function(selmodel,node){
                        if(node.id.substring(0,5) == 'inbox'){
                            Wtf.getCmp("checkEmail").show();
                            Wtf.getCmp("syncmail").show();
                            Wtf.getCmp("emailSettingsEdit").enable();
                            Wtf.getCmp("emailSettingsDelete").enable();
                        }else{
                            Wtf.getCmp("checkEmail").hide();
                            Wtf.getCmp("syncmail").hide();
                            Wtf.getCmp("emailSettingsEdit").disable();
                            Wtf.getCmp("emailSettingsDelete").disable();
                        }
                    },this);
                }

            },function() {
        });
    },

    accountNode : function(ObjText,ObjVal) {
        var nodeId = 'mail_'+ObjVal;
        var objNode = this.getNodeById(nodeId);
        if(objNode) {
             objNode.remove();
        }
        var mailuser = this.create_TreeNode(ObjText,nodeId,false);
        
        var mailinbox = this.create_TreeNode("<span wtf:qtip='View all the messages you receive at one place, flag/unflag them or move them to your preferred folder effectively.'>INBOX</span>",'inbox_'+ObjVal,'pwnd inboxIcon',true);
        mailuser.appendChild(mailinbox);
        mailinbox.on("click",this.hideShowColumns,this);
        var contextMenu = new Wtf.menu.Menu({items:[{
            text    : WtfGlobal.getLocaleText("crm.mymails.mailsettings.checkmail"),//'Check Mail',
            iconCls :'pwnd emailIcon',
            scope   : this,
            handler : function(e) {
                var node = this.treeContextMenuNode;
                node.fireEvent('click',node,this);
                portalmail_mainPanel.setAjaxTimeout();
                portalmail_mainPanel.checkEmailProgress(node.id.split('_')[1], undefined, false);
            }
        },{
            text    : WtfGlobal.getLocaleText("crm.mymails.mailsettings.synchronizemail"),//'Synchronize Mail',
            iconCls :'pwnd synMail',
            scope   : this,
            handler : function(e) {
                var node = this.treeContextMenuNode;
                node.fireEvent('click',node,this);
                portalmail_mainPanel.setAjaxTimeout();
                portalmail_mainPanel.checkEmailProgress(node.id.split('_')[1], undefined, true);
            }
        }]});
        mailinbox.on('contextmenu', function(node, e) {
            e.preventDefault();
            contextMenu.node = node;
            contextMenu.showAt(e.getXY());
            this.treeContextMenuNode = node;
        }, this);
        this.root.appendChild(mailuser);
        return mailuser;
    },
    
    setFolderViewSelection : function(ieId) {
        var idArr =[];
        idArr[0] = ieId;
        Wtf.Ajax.requestEx({
            url: "Common/MailIntegration/mailIntegrate.do",
            params:{
                action:'EmailUIAjax',
                emailUIAction:'setFolderViewSelection',
                ieIdShow:eval('('+idArr+')'),
                module:'Emails',
                to_pdf:true
            },method:'post'},
            this,
            function(request,res){
                var responseText = request;
                if(responseText != "")
                {
                    var resObj = eval('(' + responseText + ')');
                    this.getNodeById('inbox_'+resObj.nodes.children.ieId).setText(resObj.nodes.children.children.text);
                }
            },function() {
        });
    },
    
    hideShowColumns : function(obj) {
        treeObj.nodeid = obj.id;
        var tab = this.portalmailPanel.getComponent('tabmailtab_tab1');
        if (tab) {
            this.portalmailPanel.setActiveTab(tab);
        }
        var _cm = this.portalmailPanel.portalmail_grid1.getColumnModel();
        var ftext = "";
        switch (treeObj.nodeid.split('_')[0]) {
            case 'drafts'://3
                Wtf.getCmp('emails').setFromText(WtfGlobal.getLocaleText("crm.frommail.label")+':', WtfGlobal.getLocaleText("crm.mymails.savedat")+':');
                Wtf.getCmp('emails').setToText(WtfGlobal.getLocaleText("crm.tomail.label"));
                _cm.setColumnHeader(2, WtfGlobal.getLocaleText("crm.frommail.label"));
                _cm.setColumnHeader(3, WtfGlobal.getLocaleText("crm.tomail.label"));
                _cm.setColumnHeader(4,WtfGlobal.getLocaleText("crm.mymails.savedat"));
                ftext = obj.attributes.mbox;//Wtf.getCmp('folderview').getNodeById(treeObj.nodeid).getUI().getTextEl().innerHTML;
                //this.portalmailPanel.displayFoldersWindow(treeObj.nodeid, ftext);
                this.portalmailPanel.loadMailsData(treeObj.nodeid, ftext,'getMessageListKrawlerFoldersXML');
                break;
            case '4':
                Wtf.getCmp('emails').setFromText(WtfGlobal.getLocaleText("crm.mymails.from_to")+':', WtfGlobal.getLocaleText("crm.mymails.receivedon_senton")+':');
                _cm.setColumnHeader(2, WtfGlobal.getLocaleText("crm.mymails.from_to"));
                _cm.setColumnHeader(3, WtfGlobal.getLocaleText("crm.mymails.receivedon_senton"));
                ftext = Wtf.getCmp('folderview').getNodeById(treeObj.nodeid).getUI().getTextEl().innerHTML;
                this.portalmailPanel.displayFoldersWindow(treeObj.nodeid, ftext);
                break;
            case '2':
                Wtf.getCmp('emails').setFromText(WtfGlobal.getLocaleText("crm.mymails.from_to")+':', WtfGlobal.getLocaleText("crm.mymails.receivedon_senton")+':');
                _cm.setColumnHeader(2, WtfGlobal.getLocaleText("crm.mymails.from_to"));
                _cm.setColumnHeader(3, WtfGlobal.getLocaleText("crm.mymails.receivedon_senton"));
                ftext = Wtf.getCmp('folderview').getNodeById(treeObj.nodeid).getUI().getTextEl().innerHTML;
                this.portalmailPanel.displayFoldersWindow(treeObj.nodeid, ftext);
                break;
            case 'sentmails'://1
                Wtf.getCmp('emails').setFromText(WtfGlobal.getLocaleText("crm.frommail.label")+':', WtfGlobal.getLocaleText("crm.mymails.receivedon")+':');
                Wtf.getCmp('emails').setToText(WtfGlobal.getLocaleText("crm.tomail.label")+':');
//                _cm.setColumnHeader(2, "To");
                _cm.setColumnHeader(4, WtfGlobal.getLocaleText("crm.mymails.senton"));//"Sent on");
                ftext = obj.attributes.mbox;//Wtf.getCmp('folderview').getNodeById(treeObj.nodeid).getUI().getTextEl().innerHTML;
                this.portalmailPanel.loadMailsData(treeObj.nodeid, ftext,'getMessageListKrawlerFoldersXML');
//                this.portalmailPanel.displayFoldersWindow(treeObj.nodeid, ftext);
                break;
            case 'inbox':
            case 'myinbox':
                Wtf.getCmp('emails').setFromText(WtfGlobal.getLocaleText("crm.frommail.label")+':', WtfGlobal.getLocaleText("crm.mymails.receivedon")+':');
                Wtf.getCmp('emails').setToText(WtfGlobal.getLocaleText("crm.tomail.label")+':');
//                _cm.setColumnHeader(2, "From");
                _cm.setColumnHeader(4, WtfGlobal.getLocaleText("crm.mymails.receivedon"));
                ftext = "INBOX";//Wtf.getCmp('folderview').getNodeById(treeObj.nodeid).getUI().getTextEl().innerHTML.split("<");
                this.portalmailPanel.loadMailsData(treeObj.nodeid, ftext,'getMessageListXML');
                break;
        }
//        this.portalmailPanel.MessagePanel1.setData1("", "", "", "", "images/blank.png");
    }
});
/*  Left Tree Menu: End   */

/*  WtfPMsgTreeUI: Start    */
Wtf.tree.NewFolderUI = function(node){
    this.node = node;
    this.rendered = false;
    this.animating = false;
    this.wasLeaf = true;
    this.addEvents = {
        'edclick': true,
        'delclick': true
    }
    this.ecc = 'x-tree-ec-icon x-tree-elbow';
};

Wtf.extend(Wtf.tree.NewFolderUI, Wtf.tree.TreeNodeUI, {
    renderElements: function(n, a, targetNode, bulkRender){
    
        this.indentMarkup = n.parentNode ? n.parentNode.ui.getChildIndent() : '';
        var cb = typeof a.checked == 'boolean';
        var href = a.href ? a.href : Wtf.isGecko ? "" : "#";
        var buf = ['<li class="x-tree-node"><table wtf:tree-node-id="', n.id, '" class="x-tree-node-el x-tree-node-leaf ', a.cls, '" cellspacing="0" cellpadding="0" ><tbody><tr><td>', this.indentMarkup, '</td><td ><img src="', Wtf.BLANK_IMAGE_URL, '" class="x-tree-ec-icon x-tree-elbow" /><img src="', a.icon || this.emptyIcon, '" class="x-tree-node-icon', (a.icon ? " x-tree-node-inline-icon" : ""), (a.iconCls ? " " + a.iconCls : ""), '" unselectable="on" /></td><td class="chip"><a hidefocus="on"  class="x-tree-node-anchor" tabIndex="1" ', a.hrefTarget ? ' target="' + a.hrefTarget + '"' : "", '>', '<span  unselectable="on">', n.text, '</span></td><td onmousedown="" ><img title="edit folder" id="edit', n.id, '" style="cursor: pointer;" do="edit" src="../../images/edit12.gif"/><img title="delete folder" id="del', n.id, '" style="cursor: pointer;" do="delete" src="../../images/stop12.gif"/></td></tr></tbody></table><ul class="x-tree-node-ct" style="display:none;"></ul></li>'].join('');
        
        var index = 2;
        this.wrap = Wtf.DomHelper.insertHtml("beforeEnd", n.parentNode.ui.ctNode, buf);
        
        
        this.elNode = this.wrap.childNodes[0].childNodes[0].rows[0];
        this.ctNode = this.wrap.parentNode;
        var cs = this.elNode.childNodes;
        
        this.indentNode = cs[0].firstChild;
        this.ecNode = cs[1].firstChild;
        this.iconNode = cs[1].lastChild;
        
        this.anchor = cs[index].firstChild;
        
        this.textNode = cs[index].firstChild.firstChild;
        Wtf.get("edit" + n.id).on("click", function(){
            this.fireEvent("edclick", this.node.id);
        }, this);
        Wtf.get("del" + n.id).on("click", function(){
            this.fireEvent("delclick", this.node.id);
        }, this);
    }
    
    
});
/*  WtfPMsgTreeUI: End  */

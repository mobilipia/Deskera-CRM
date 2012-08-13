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
Wtf.docs.com.Grid = function(config){

    Wtf.apply(this, config);

    back = this;
    this.defaultPageSize = 20;

    this.groupingView = new Wtf.grid.GroupingView({
        forceFit: true,
        showGroupName: false,
        enableGroupingMenu: false,
        emptyText:WtfGlobal.emptyGridRenderer("There are no results to display"),
        groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})',
        hideGroupedColumn: false
    });

    this.tagSearchTF = new Wtf.KWLTagSearch({
        id: 'tagdocquicksearch',
        width: 200,
        emptyText:WtfGlobal.getLocaleText("crm.mydocuments.toptoolbar.tagsearch"),//'Search by Tag Name',
        tagSearch:true
    });    
    this.defaultSearchType = 1; //Search By Document Name
    var comboval1=WtfGlobal.getLocaleText("crm.mydocuments.searchtypecombo.bydocname");
    var comboval2=WtfGlobal.getLocaleText("crm.mydocuments.searchtypecombo.bycontent");
    this.searchTypeStore = new Wtf.data.SimpleStore({
        fields: ['value','name'],
        data : [
            [0,comboval1],//WtfGlobal.getLocaleText("crm.mydocuments.searchtypecombo.bydocname")]
            [1,comboval2]//WtfGlobal.getLocaleText("crm.mydocuments.searchtypecombo.bycontent")]
        ]
    });
    this.searchType= new Wtf.form.ComboBox({
        store: this.searchTypeStore,
        valueField:'value',
        displayField:'name',
        mode: 'local',
        triggerAction: 'all',
        emptyText:'Select Search Type',
        typeAhead:true,
        selectOnFocus:true,
        allowBlank:false,
        width: 180,
        forceSelection: true,
        value: this.defaultSearchType
   });
   this.searchType.on("select", function(cmb, rec, ind){
        this.ds.baseParams.searchType=this.searchType.getValue();
        this.hideShowColumns(this.searchType.getValue()==1);
        this.grid1.getView().refresh();
        this.ds.load({
            params: {
                start:0,
                limit:this.pP.combo.value
            }
        });
   },this);

    this.resetBttn=new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.RESETBUTTON"),//'Reset',
        scope: this,
        disabled: false,
        tooltip: {text: WtfGlobal.getLocaleText("crm.editor.toptoolbar.resetBTN.ttip")},//'Click to remove any filter settings or search criteria and view all records.'},
        iconCls:'pwndCRM reset',
        handler: this.handleResetClick
    });

    Wtf.ownerStore.load();

    this.addOwners=new Wtf.Toolbar.Button({
        id:"tempButton",
        text : WtfGlobal.getLocaleText("crm.mydocuments.toptoolbar.managepermBTN"),//"Manage Permission",
        pressed: false,
        scope : this,
        tooltip: {text: WtfGlobal.getLocaleText("crm.mydocuments.toptoolbar.managepermBTN.ttip")},//"Select a document and click on 'Manage Permission' to add/modify permission for the users."},
        iconCls:'pwnd addowners',
        handler : this.addDocumentPermission
    });

    var x=[{
        name: 'name',
        type:'string'
    },{
        name: 'relatedto',
        type:'string'
    },{
        name: 'relatedname',
        type:'string'
    },{
        name: 'size',
        type:'float'
    },{
        name: 'type',
        type:'string'
    },{
        name: 'uploadeddate',
        type:'date'
    },{
        name: 'Tags',
        type:'string'
    },{
        name:'docid',
        type:'string'
    },{
        name: 'author',
        type:'string'
    },{
        name:'uploadername',
        type:'string'
    },{
        name:'Summary'
    },{
        name:'userid',
        type:'string'
    }];

    var fields = Wtf.data.Record.create(x);

    this.reader = new Wtf.data.KwlJsonReader({
        totalProperty: 'totalCount',
        root: 'data'
    },fields);

    this.sm = new Wtf.grid.CheckboxSelectionModel();
    this.sm2 = new Wtf.grid.CheckboxSelectionModel();
    this.sm2.on("selectionchange",function(){
        var selarr = this.sm2.getSelections();
        if(selarr.length>0)
            this.delbutton.setDisabled(false);
        else
            this.delbutton.setDisabled(true);
    },this);
    this.sm2.id = "chk";
    this.txtareaid = config.id + 'edittag';
    this.divele = document.createElement("div");
    this.divele.id = 'divTag' + config.id;
    this.divele.style.width = '60%';
    this.divele.style.height = '60%';
    this.divele.style.display = 'none';
    this.myTextArea = document.createElement("textarea");
    this.myTextArea.id = this.txtareaid;
    this.myTextArea.style.display = 'none';
    this.myTextArea.style.fontSize = '70px';
    this.txtboxid = config.id + 'tagtextbox';
    this.bttnid = 'tagbttn';
    this.delbttid = config.id + 'del_butt';
    this.downbttid = config.id + 'down_butt';
    this.showbttid = config.id + 'show_butt';
    this.ver = config.id + 'ver_butt';
    this.addFilesid = config.id + 'addFile';
    this.tabtabpanelid = config.id + 'tabtabpanel';
    this.revlist = config.id+'revlist_butt';

    this.textbox = new Wtf.ux.TextField({
        id: this.txtboxid,
        cls: 'tagTextBox',
        maxLength:30,
        width: 100
    });
    this.bttn = new Wtf.Button({
        id: this.bttnid,
        text: WtfGlobal.getLocaleText("crm.mydocuments.addtagBTN"),//"Add Tag",
        tooltip:{
            text:WtfGlobal.getLocaleText("crm.mydocuments.addtagBTN.ttip")//'Add tags for quick search of documents.'
        },
        cls: 'bttn',
        disabled: true
    });
    this.ds = new Wtf.data.GroupingStore({
        url: "crm/common/Document/getDocumentList.do",
        reader: this.reader,
        baseParams: {
            searchType:this.defaultSearchType
        },
        sortInfo: {
            field: 'name',
            direction: "DESC"
        }
    });
    this.toolbar=new Wtf.PagingSearchToolbar({
        pageSize: this.defaultPageSize,
        searchField:this.tagSearchTF,
        width:350,
        id: "doc_pagingtoolbar",
        store: this.ds,
        plugins:this.pP = new Wtf.common.pPageSize({
            id: "pPageSize_" + this.id
        })
    });
    this.cm = new Wtf.grid.ColumnModel([this.sm,
        new Wtf.grid.RowNumberer({allowIncreament:true,rowspan:1}),
        {
            id: 'Name',
            header: "<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mydocuments.header.name")+"'>"+WtfGlobal.getLocaleText("crm.mydocuments.header.name")+"</span>",
            dataIndex: 'name',
            sortable: true,
            groupable: true,
            groupRenderer: WtfGlobal.nameRenderer
        },{
            id:'Related_to',
            header:"<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mydocuments.header.relatedto")+"'>"+WtfGlobal.getLocaleText("crm.mydocuments.header.relatedto")+"</span>",
            dataIndex:'relatedto',
            sortable: true,
            groupable: true,
            groupRenderer: WtfGlobal.relatedtoRenderer
        },{
            id:'Related_Name',
            header:"<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mydocuments.header.relatedname")+"'>"+WtfGlobal.getLocaleText("crm.mydocuments.header.relatedname")+"</span>",
            dataIndex:'relatedname',
            sortable: true,
            groupable: true,
            groupRenderer: WtfGlobal.nameRenderer
        },{
            header: "<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mydocuments.header.contentsummary")+"'>"+WtfGlobal.getLocaleText("crm.mydocuments.header.contentsummary")+"</span>",
            dataIndex: 'Summary',
            sortable: true,
            groupable: true,
            renderer:function(val){
                return "<span wtf:qtip=\""+val+"\">"+Wtf.util.Format.ellipsis(val, 30)+"</span>";
            }
        },{
            header: "<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mydocuments.header.size")+"'>"+WtfGlobal.getLocaleText("crm.mydocuments.header.size")+"</span>",
            dataIndex: 'size',
            sortable: true,
            align: 'right',
            groupable: true,
            renderer:WtfGlobal.sizetypeRenderer,
            groupRenderer: WtfGlobal.sizeRenderer
        },{
            header: "<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mydocuments.header.type")+"'>"+WtfGlobal.getLocaleText("crm.mydocuments.header.type")+"</span>",
            dataIndex: 'type',
            sortable: true,
            groupable: true,
            renderer:function(val){
                return(val);
            }
        },{
            id: 'Date_Modified',
            header: "<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mydocuments.header.uploadedon")+"'>"+WtfGlobal.getLocaleText("crm.mydocuments.header.uploadedon")+"</span>",
            dataIndex: 'uploadeddate',
            align:'center',
            sortable: true,
            renderer: WtfGlobal.onlyDateRenderer,
            groupable: true,
            groupRenderer: WtfGlobal.dateFieldRenderer
        },{
            header: "<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mydocuments.header.owner")+"'>"+WtfGlobal.getLocaleText("crm.mydocuments.header.owner")+"</span>",
            dataIndex: 'author',
            sortable: true,
            groupable: true
        },{
            header:"<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mydocuments.header.uploadedby")+"'>"+WtfGlobal.getLocaleText("crm.mydocuments.header.uploadedby")+"</span>",
            dataIndex:'uploadername',
            sortable:true,
            groupable: true,
            groupRenderer: WtfGlobal.nameRenderer

        },{
            header:"<span wtf:qtip='"+WtfGlobal.getLocaleText("crm.mydocuments.header.download")+"'>"+WtfGlobal.getLocaleText("crm.mydocuments.header.download")+"</span>",
            dataIndex:'abc',
            renderer:function(a,b,c,d,e,f){
                var docid  = c.json.docid;
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Document, Wtf.Perm.Document.doc_down)){
                    return "<a href='javascript:void(0)' id='downloadlink' title='Download' onclick='setDldUrl(\"crm/common/Document/downloadDocuments.do?url=" + docid + "&mailattch=true&dtype=attachment\")'><div class='pwnd downloadIcon' > </div></a>";
                }
                else{
                    return "<a href='javascript:void(0)' id='downloadlink' title='Download' onclick='ResponseAlert(550)'><div class='pwnd downloadIcon' > </div></a>";
                }
            }
        }
        ]);

    this.cm.defaultSortable = true;
    this.hideShowColumns(this.defaultSearchType==1);
    this.help=getHelpButton(this,32);
    this.quickSearchTF = new Wtf.KWLTagSearch({
        id: 'docquicksearch',
        width: 200,
        emptyText:WtfGlobal.getLocaleText("crm.mydocuments.quicksearch.mtytxt")//'Search Text'
    });

    var toolBarArr = [this.searchType, " ", this.quickSearchTF, "-", this.resetBttn, '->',this.help];
    if(Wtf.URole.roleid == Wtf.AdminId ){
        toolBarArr = [this.searchType, " ", this.quickSearchTF, "-", this.resetBttn,'-',this.addOwners, '->',this.help];
    }
    this.quickSearchTF.on('SearchComplete', function() {
        this.tagSearchTF.setValue("");
        this.toolbar.searchField=this.quickSearchTF;
        this.hideShowColumns(this.searchType.getValue()==1);
        this.divele.innerHTML = "";
        this.grid1.getView().refresh();
    }, this);

    this.tagSearchTF.on('SearchComplete', function() {
        this.quickSearchTF.setValue("");
        this.toolbar.searchField=this.tagSearchTF;
        this.searchType.setValue(0)
        this.hideShowColumns(false);
        this.grid1.getView().refresh();

        var spanele;
        var div = this.divele;
        div.innerHTML = "";
        div.style.display = 'block';
        this.editable = 1;
        spanele = document.createElement("span");
        spanele.className = 'spanelement';
        var val = this.tagSearchTF.getValue();
        if( val == undefined || val.trim() ==""){
            spanele.innerHTML = "";
            this.clrfilterBttn.disable();
        } else {
            spanele.innerHTML = 'Searched by Tag name: '+val;
            this.clrfilterBttn.enable();
        }

        spanele.id = 'span' + 1;
        spanele.style.color = "#15428b";
        div.appendChild(spanele);

    }, this);

    Wtf.docs.com.Grid.superclass.constructor.call(this, {
        layout: 'border',
        items: [
        this.grid1 = new Wtf.grid.GridPanel({
            border: false,
            region: 'center',
            id: 'topic-grid' + config.id,
            store: this.ds,
            layout:'fit',
            view: this.groupingView,
            autoScroll:true,
            cm: this.cm,
            sm: this.sm2,
            scope:this,
            trackMouseOver: true,
            loadMask: {
                msg: 'Loading Documents...'
            },
            tbar:toolBarArr,
            bbar:this.bBar(config)
        }),{
            region: 'south',
            minHeight: 75,
            height: 100,
            id: this.tabtabpanelid,
            title: WtfGlobal.getLocaleText("crm.mydocuments.southregiontitle.addtag"),//'Add Tags',
            iconCls: "pwndCRM tag",
           // frame: true,
            border: false,
            items: [this.divele, this.myTextArea, this.textbox, this.bttn],
            split: true
        }]
    });
    this.ds.on('load',function(){
        this.quickSearchTF.StorageChanged(this.ds);
        this.tagSearchTF.StorageChanged(this.ds);
        Wtf.updateProgress();
    },this);

    this.ds.on("datachanged",function(){
        this.quickSearchTF.setPage(this.pP.combo.value);
        this.tagSearchTF.setPage(this.pP.combo.value);
    },this);
};

Wtf.extend(Wtf.docs.com.Grid, Wtf.Panel, {

    loadMask: null,
    txtboxid: '',
    editable: 0,
    gridrowindex: '',
    root: null,
    defaultTag: null,
    tagsArray: null,
    tempSpans: null,
    spanlength: null,
    mainTree: null,
    flagForTreeClick: 0,
    flagForReloadTree: 0,
    regx : '^([\'"]?)\\s*([\\w]+[(/|\\\{1})]?)*[\\w]\\1$',
    tagregx: "\\w+|(([\'\"])\\s*([\\w][\\s[\\\\|\\/]\\w]*)\\s*\\2)|([\\w][\\s[\\\\|\\/]\\w]*)",
    patt1 : /(['"])\s*([\w]+[\s\\|\/\w]*)\s*\1/g,
    /*
        *  regx contains string capture by [\'|\"] this group followed by any number of whitespaces and more than one number of alphanumeric cha
         * then string capture by ([\\s\\\\\\/]*\\w+) this group followed by any number of whitespaces then
         * backrefreance to capture groupnumber 1 which is ([\'|\"])
    */
    patt2 : /([\w][\\|\/\w]*)/g,
    /*
    * capture the string which start with alphanumeric charector and it contains zero or more occurance of string captute by
    * (\\w+([\\\\\\/]*\\w+)*
    */
    bBar:function(config){
        return [
            this.toolbar,'-','->',toggleBttn = new Wtf.Button({
            id: 'addfilebtnmain',
            text: WtfGlobal.getLocaleText("crm.activitydetailpanel.addfilesBTN"),//'Add Files',
            iconCls: 'pwndCRM showgrp',
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.mydocuments.addfilesbtn.ttip")//'Click here to upload documents.'
            },
            handler:function(){
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Document,Wtf.Perm.Document.doc_up) ){
                    var uploadDocWin = new Wtf.UploadFile({
                        idX:this.id,
                        recid:loginid,
                        scope:this,
                        mapid:"-1",
                        isDetailPanel:false,
                        isrelated:0
                    });
                    uploadDocWin.show();
                } else {
                    ResponseAlert(["Alert","You do not have required permission to upload document."]);
                }
            }
        }),this.delbutton = new Wtf.Button({
            id: 'delfilebtnmain',
            text: WtfGlobal.getLocaleText("crm.mydocuments.deletefilesBTN"),//'Delete File(s)',
            disabled:true,

            iconCls: 'pwndCRM showgrp',
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.mydocuments.deletefilesBTN.ttip")//'Click here to delete document(s).'
            },
            scope:this,
            handler:function(){
                var selarr = this.sm2.getSelections();
                var docids = "";
                var delsuccess = 0;
                var delPerm = 0;
                var tob=Wtf.getCmp('tree');
                var duration = 60;
                var moduleNode=tob.getNodeById('documentnode');
                if(moduleNode){
                    this.grid1.getSelectionModel().clearSelections();
                    if(!moduleNode.isExpanded())
                        moduleNode.expand();
                    Wtf.onlyhighLightRecordLoop(this.grid1, "FF0000", duration, selarr, "doc");
                    for(var i=0; i<selarr.length; i++) {
                        var node=moduleNode.findChild('nodeid',selarr[i].get('docid'));
                        var nodeIndex = moduleNode.indexOf(node);
                        if(nodeIndex != -1) {
                            if(!moduleNode.isExpanded())
                                moduleNode.expand();
                            Wtf.highLightTreeNode(node,'FF0000',duration);
                        }
                    }
                }
                var msg = "You can delete a document only if you have uploaded it or you are an admin. You cannot delete document(s) ";
                for(var ctr=0;ctr<selarr.length;ctr++){
                    docids += selarr[ctr].get('docid')+",";
                      if(Wtf.URole.roleid == Wtf.AdminId ){
                          if(WtfGlobal.EnableDisable(Wtf.UPerm.Document,Wtf.Perm.Document.doc_del) ){
                              delsuccess = 1;
                              delPerm = 2;
                              msg = "You do not have required permission to delete document(s). ";
                          } 
                      } else {
                           if(selarr[ctr].get("userid")==loginid){
                                if(WtfGlobal.EnableDisable(Wtf.UPerm.Document,Wtf.Perm.Document.doc_del) ){
                                      delsuccess = 1;
                                      delPerm = 2;
                                      msg = "You do not have required permission to delete document(s). ";
                                } 
                           }else if(delPerm < 2){
                              delsuccess = 1;
                              delPerm = 1;
                              msg +="<b>'"+selarr[ctr].get("name")+"'</b>,";
                           }
                      }
                }
                docids = docids.substring(0,docids.length-1);
                msg = msg.substring(0,msg.length-1);
                if(delPerm==1){
                    msg +=". Please unselect the above document(s) and try again.";
                }
                if(delsuccess == 1){
                    Wtf.MessageBox.show({
                    title: "Error",
                    msg: msg,
                    buttons: Wtf.MessageBox.OK,
                    animEl: 'mb9',
                    scope:this,
                    icon: Wtf.MessageBox.INFO
                    })
                }else{
                    Wtf.MessageBox.show({
                        title: "Confirm",
                        msg: "Are you sure you want to delete "+selarr.length+" document(s)?",
                        buttons: Wtf.MessageBox.YESNO,
                        animEl: 'mb9',
                        scope:this,
                        icon: Wtf.MessageBox.INFO,
                        fn:function(btn,text){
                            if(btn=="yes"){

                                Wtf.Ajax.requestEx({
                                url: "crm/common/Document/deletedocument.do",
                                params:{
                                    docid:docids
                                }
                                },
                                this,
                                function(res) {
                                    Wtf.getCmp('delfilebtnmain').disabled = true;
                                    this.divele.innerHTML = '';
                                    this.ds.reload();
                                    for(var i=0; i<selarr.length; i++) {
                                        var node=moduleNode.findChild('nodeid',selarr[i].get('docid'));
                                        var nodeIndex = moduleNode.indexOf(node);
                                        if(nodeIndex != -1) {
                                            if(!moduleNode.isExpanded())
                                                moduleNode.expand();
                                            moduleNode.removeChild(node);
                                        }
                                    }
                                });
                            }else{
                                if(moduleNode){
                                    this.grid1.getSelectionModel().clearSelections();
                                    Wtf.onlyhighLightRecordLoop(this.grid1, "ffffff", duration, selarr, "doc");
                                    for(var i=0; i<selarr.length; i++) {
                                        var node=moduleNode.findChild('nodeid',selarr[i].get('docid'));
                                        var nodeIndex = moduleNode.indexOf(node);
                                        if(nodeIndex != -1) {
                                            if(!moduleNode.isExpanded())
                                                moduleNode.expand();
                                            Wtf.highLightTreeNode(node,'ffffff',duration);
                                        }
                                    }
                                }
                            }
                        }

                    })
                }
            }
        }),
        toggleBttn = new Wtf.Button ({
            id: 'toggleBttn32',
            text: WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.showingrpsBTN"),//'Show in Groups',
            iconCls: 'pwndCRM showgrp',
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.showingrpsBTN.ttip")//'Helps you to make separate folders for your documents according to the file name.'
            },
            enableToggle: true
        }),'-',
        {
            text: WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu"),//'Sort',
            iconCls: 'pwnd arrange',
            id:'sortdoc',
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.ttip")//'Helps you sort in ascending/descending order.'
            },
            menu:
            [sortBy1 = new Wtf.Action({
                text:WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.name"),//'Name',
                id: "sortBy1",
                iconCls: 'pwnd name',
                tooltip:{
                    text:WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.name.ttip")//"Sort 'Document Name' in ascending/descending order."
                },
                scope: this,
                handler: function(){
                    this.setCheckedIcon(1, this.grid1);
                }
            }, this), sortBy2 = new Wtf.Action({
                text: WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.relatedto"),//'Related to',
                id: "sortBy2",
                tooltip:{
                    text:WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.relatedto.ttip")//"Sort 'Related To' in ascending/descending order."
                },
                iconCls: 'pwndCRM relatedto',
                scope: this,
                handler: function(){
                    this.setCheckedIcon(2, this.grid1);
                }
            }, this), sortBy3 = new Wtf.Action({
                text: WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.relatedname"),//'Related Name',
                id: "sortBy3",
                tooltip:{
                    text:WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.relatedname.ttip")//"Sort 'Related Name' in ascending/descending order."
                },
                iconCls: 'pwndCRM relatedname',
                scope: this,
                handler: function(){
                    this.setCheckedIcon( 3, this.grid1);
                }
            }, this), sortBy4 = new Wtf.Action({
                text: WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.size"),//"Size",
                id: "sortBy4",
                tooltip:{
                    text:WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.size.ttip")//"Sort 'Document Size' in ascending/descending order'."
                },
                iconCls: 'pwnd size',
                scope: this,
                handler: function(){
                    this.setCheckedIcon(5, this.grid1);
                }
            }, this), sortBy5 = new Wtf.Action({
                text: WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.type"),//'Type',
                id: "sortBy5",
                tooltip:{
                    text:WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.type.ttip")//"Sort 'Document Type' in ascending/descending order'."
                },
                iconCls: 'pwndCRM type',
                scope: this,
                handler: function(){
                    this.setCheckedIcon(6, this.grid1);
                }
            }, this), sortBy6 = new Wtf.Action({
                text: WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.uploadeddate"),//'Uploaded Date',
                id: "sortBy6",
                tooltip:{
                    text:WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.uploadeddate.ttip")//"Sort 'Uploaded Date' in ascending/descending order."
                },
                iconCls: 'pwndCRM uploadedate',
                scope: this,
                handler: function(){
                    this.setCheckedIcon( 7, this.grid1);
                }
            }, this), sortBy7 = new Wtf.Action({
                text: WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.owner"),//'Owner',
                id: "sortBy7",
                tooltip:{
                    text:WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.owner.ttip")//"Sort 'Document Owner' in ascending/descending order."
                },
                iconCls: 'pwndCRM author',
                scope: this,
                handler: function(){
                    this.setCheckedIcon(8, this.grid1);
                }
            }, this)]
        },'-',
        this.tagSearchTF,'-',
        this.clrfilterBttn= new Wtf.Button({
            text: WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.clearfilter"),//'Clear Filter',
            iconCls:'pwnd clearfilter',
            scope:this,
            disabled:true,
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.clearfilter.ttip")//'Loads all documents after Tag search.'
            },
            handler:function(){
                this.handleResetClick();
                this.clrfilterBttn.disable();
            }
        })
        ]
    },
    hideShowColumns : function(isContentSearch){
        if(isContentSearch){
            this.cm.setHidden(3, true);
            this.cm.setHidden(4, true);
            this.cm.setHidden(5, false);
        } else {
            this.cm.setHidden(3, false);
            this.cm.setHidden(4, false);
            this.cm.setHidden(5, true);
        }
    },
    handleResetClick:function(){
        this.quickSearchTF.setValue("");
        this.toolbar.searchField=this.quickSearchTF;

        this.tagSearchTF.setValue("");
        this.divele.innerHTML = "";

        this.searchType.reset();
        this.ds.baseParams.searchType=this.searchType.getValue();

        this.hideShowColumns(this.searchType.getValue()==1);
        this.grid1.getView().refresh();

        this.ds.load({
            params: {
                start:0,
                limit:this.pP.combo.value
            }
        });
    },
    addDocumentPermission:function(){
        var s=this.grid1.getSelectionModel().getSelections();
        
        if(s.length==1) {
            var rec = this.grid1.getSelectionModel().getSelected();
            var docid = rec.data.docid;
            

            Wtf.Ajax.requestEx({
                url: "crm/common/Document/getExistingDocOwners.do",
                params:{
                    docid:docid
                }
            },this,
            function(res) {
                var subOwnersWin = new Wtf.AddSubOwners({
                    idX:this.id2,
                    grid:this.grid,
                    recid:docid,
                    keyid:this.keyid,
                    mapid:this.mapid,
                    store:this.Store,
                    ownerstore : Wtf.ownerStore,
                    module:Wtf.DOCUMENT_MODULE,
                    ownerid:this.ownerid,
                    ownerinfo:res,
                    selectedRec:this.selectedRec,
                    isDetailPanel:false
                });
                subOwnersWin.show();
            },
            function(res) {

            });

        } else {
            WtfComMsgBox(400,0);
        }
    },
    setCheckedIcon: function (index, grid){
        grid.fireEvent('headerclick', grid,index+1);
    },
    afterRender: function(config){
        Wtf.docs.com.Grid.superclass.afterRender.call(this, config);

        this.initPage();
        this.defaultTag = new Array();
        this.defaultTag = ['shared','uncategorized','shared/'];
        this.tagsArray = [];
        this.tempSpans = [];
        this.spanlength = [];

        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
        this.ds.load({
            params: {
                start:0,
                limit:this.defaultPageSize
            }
        });

        this.grid1.on("sortchange", function(b, bd){
            if (toggleBttn.pressed)
                this.grid1.getStore().groupBy(bd.field);
        }, this);

        this.grid1.on("headerclick", function(obj, ci, e){
            this.setCheckedIconForActoin(ci, obj);
        }, this);
    },
    setCheckedIconForActoin: function(ci, obj){
        var iconArray = ['','pwnd name','pwndCRM relatedto','pwndCRM relatedname','pwnd size','pwndCRM type','pwndCRM uploadedate','pwndCRM author'];
        if(ci > 0) {
            for(var cnt = 1; cnt <= 7; cnt++){
                Wtf.getCmp("sortBy" + cnt).setIconClass(iconArray[cnt]);
            }
        }
        this.tooltipChanges(ci);
    },
    tooltipChanges : function (sortindex){
        sortindex--;
        var tooltipGroupby = 'Helps you to make separate folders for your documents according to the ';
        switch(sortindex){
           case 1:
                tooltipGroupby = tooltipGroupby+'file name.';
                break;
           case 2:
                tooltipGroupby = tooltipGroupby+'Related to.';
                break;
           case 3:
                tooltipGroupby = tooltipGroupby+'Related Name.';
                break;
           case 4:
                tooltipGroupby = tooltipGroupby+'file size.';
                break;
           case 5:
                tooltipGroupby = tooltipGroupby+'file type.';
                break;
           case 6:
                tooltipGroupby = tooltipGroupby+'uploaded date.';
                break;
           case 7:
                tooltipGroupby = tooltipGroupby+'owner.';
                break;
        }
        toggleBttn.setTooltip(tooltipGroupby)
    },
    initPage: function(){
        Wtf.EventManager.addListener(this.myTextArea, 'keydown', this.handleTextSubmit, this);
        this.grid1.on('rowclick', this.onDocGridRowClick, this);
        this.grid1.on('rowcontextmenu', this.onDocGridRowContextMenu, this);
        toggleBttn.on('toggle', this.onGroupBttnClick, this);
        this.textbox.on('specialkey', this.specialKey, this);
    },
    stringToArray:function(arrayString,delimetor){
        return arrayString.split(delimetor);
    },
    onDocGridRowClick: function(obj, index, e){
        if (this.flagForTreeClick == 0) {
            var tagname = [];
            Wtf.getCmp(this.textbox.id).setVisible(true);
            Wtf.getCmp(this.bttn.id).setVisible(true);
            Wtf.EventManager.addListener(this.textbox.id, 'keyup', this.handleKeyPress, this);
            Wtf.EventManager.addListener(this.bttn.id, 'click', this.handleBttnClick, this);

            if(this.grid1.getSelectionModel().getSelections().length==1)
            {
                for(var a = 0; a < this.grid1.getSelectionModel().getCount();a++){
                    var rec = this.grid1.getSelectionModel().getSelections()[a];
                    var temp = rec.data['Tags'];
                    tagname =  tagname.concat(this.stringToArray(rec.data['Tags'],','));
                }
                tagname = tagname.sort();
                var tagArray1 = new Array();
                tagArray1 = tagname;
                var spanele;
                var div = this.divele;
                div.innerHTML = "";
                div.style.display = 'block';
                this.editable = 1;
                for (var i = 0; i < tagArray1.length; i++) {
                    spanele = document.createElement("span");
                    spanele.className = 'spanelement';
                    spanele.innerHTML = tagArray1[i];
                    spanele.id = 'span' + i;
                    spanele.style.color = "#15428b";
                    div.appendChild(spanele);
                    Wtf.EventManager.addListener("span" + i, 'mouseover', this.handleMouseOver, this);
                    Wtf.EventManager.addListener("span" + i, 'mouseout', this.handleMouseOut, this);
                    Wtf.EventManager.addListener("span" + i, 'click', this.handleMouseClick, this);
                }
            } else {
                div = this.divele;
                while(div.hasChildNodes()) {
                    while ( div.childNodes.length >= 1 ) {
                        div.removeChild( div.firstChild );
                    }
                }
            }
        }
    },
    onGroupBttnClick: function(bttnobj, isPressed){
        if (isPressed)
            this.grid1.getStore().groupBy(this.grid1.getStore().getSortState().field);
        else
            this.grid1.store.clearGrouping();
    },
    handleTextSubmit: function(e){
        var cal = e.getKey();
        if (cal == 13) {
            var t = Wtf.getCmp(this.txtboxid);
            var te = this.myTextArea;
            te.style.display = 'none';
            var div = this.divele;
            div.style.display = 'block';
            t.focus();
        }
    },
    handleKeyPress: function(obj){
        var textval = this.textbox.getValue();
        if (textval == '' || (this.editable == 0))
            this.bttn.disable();
        else
            this.bttn.enable();
    },
    checkSystemTag:function(tag){
        if(this.defaultTag.indexOf(tag.toLowerCase())!=-1){
            return false;
        }else if(this.defaultTag.indexOf(tag.toLowerCase().substr(0,tag.indexOf('/')+1))!=-1){
            return false;
        }
        return true;
    },
    _fillTmpArray: function(tagstr){
        var tagarr = tagstr.split(',');
        var arr = [];
        for (var q = 0; q < tagarr.length; q++) {
            if (!this.checkSystemTag(tagarr[q])) {
                arr.push(tagarr[q]);
                break;
            }
        }
        return arr;
    },
    checkForDuplicateEntry:function(arr){
        var temparr = arr.join(',').toLowerCase().split(',');
        for(var i=0;i<arr.length;i++){
            temparr.shift();
            if(temparr.indexOf(arr[i].toLowerCase())>-1 || !this.checkSystemTag(arr[i])){
                arr[i] = '-';
            }
        }
        while(arr.length!=arr.remove('-').length)
            arr.remove('-');
        return arr;
    },
    handleBttnClick: function(obj, e){
        var flagbtclick = 0;
        var flagbtclick1 = 1;
        if (this.bttn.disabled == false) {
            var spanele;
            var div = this.divele;
            div.style.display = 'block';
            var text = this.textbox.getValue();
            if(text.length>this.textbox.maxLength){
                WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.lead.webtoleadform.entervalinputmsg")]);
                Wtf.getCmp(this.txtboxid).setValue("");
                Wtf.getCmp(this.txtboxid).markInvalid();
                Wtf.getCmp(this.txtboxid).allowBlank=false;
                return;
            }
            text = text.trim();
            var check = text.match(this.patt1);
            if(check==null){
                check = text.match(/^([\w][\s]*[\\|\/\w\s]*)$/g);
            }else{
                text = text.replace(/'|"/g,"");
            }
            var docidarr = "";
            if (check != null && this.checkSystemTag(text.toLowerCase())) {
                text = text.replace(/\/+/g, "/");
                if(text.trim().match(/\s+/g)!=null){
                    text = "'"+text+"'";
                }
                var selectedRow = this.grid1.getSelectionModel().getSelections();
                if(this.grid1.getSelectionModel().getSelections().length!=0)
                {
                    for(var a = 0; a < this.grid1.getSelectionModel().getCount();a++){
                        var rec = selectedRow[a];
                        var temp = rec.data['docid'];
                        var tagname1 = rec.data['Tags'].split(',');
                        for (i = 0; i < tagname1.length; i++) {
                            if(tagname1[i].toLowerCase() == text.toLowerCase()) {
                                flagbtclick = 1;
                                flagbtclick1 = 0;
                            }
                        }
                        if(flagbtclick==0){
                            docidarr += temp+",";
                        }
                        flagbtclick=0;
                    }
                    docidarr = docidarr.substr(0,docidarr.length-1);

                    if(this.checkSystemTag(text)){
                        if(flagbtclick1 == 1){
                            spanele = document.createElement("span");
                            spanele.className = 'spanelement';
                            var child = div.getElementsByTagName('span');
                            spanele.id = 'span' + child.length;
                            spanele.innerHTML = text;
                            spanele.style.color = "#15428b";
                            div.appendChild(spanele);
                            Wtf.EventManager.addListener(spanele.id, 'mouseover', this.handleMouseOver, this);
                            Wtf.EventManager.addListener(spanele.id, 'mouseout', this.handleMouseOut, this);
                            Wtf.EventManager.addListener(spanele.id, 'click', this.handleMouseClick, this);
                        }
                        this.textbox.setValue("");
                        var docidarr1 = docidarr.split(',');
                        var tagarray=[];
                        for(var l = 0; l<docidarr1.length; l++){
                            if(docidarr1[l]!=''){
                                rec = this.grid1.store.find("docid", docidarr1[l], 0, false, true);
                                var rec1 = this.grid1.store.getAt(rec);
                                if(rec1.data['Tags']!='')
                                    tagarray.push(docidarr1[l]+',,'+rec1.data['Tags']+','+ text);
                                else
                                    tagarray.push(docidarr1[l]+',,'+text);
                            }
                        }
                        Wtf.Ajax.requestEx({
                            url: "crm/common/Document/addTag.do",
                            params:{
                                tag:tagarray,
                                newTag:text
                            }
                        },
                        this,
                        function(res) {
                            this.ds.baseParams.searchType=this.searchType.getValue();
                            this.grid1.getView().refresh();
                            this.ds.load({
                                params: {
                                    start:0,
                                    limit:this.pP.combo.value
                                }
                            });
                        });
                        Wtf.getCmp('tree').getLoader().baseParams={
                            mode:'0'
                        };
                        Wtf.getCmp('tree').getLoader().load(Wtf.getCmp('tree').root);
                        Wtf.getCmp('tree').root.expand();
                        back.ds.load();
                    }
                } else {
                    ResponseAlert(["Alert","No document selected to attach a tag."]);
                }
            } else {
                ResponseAlert(["Error", "A tag can contain only alphanumeric characters"]);
                this.textbox.setValue("");
            }
            this.textbox.setValue("");
        }
        this.bttn.setDisabled(true);
        this.divele.innerHTML = "";
    },
    specialKey: function(obj, e){
        var cal = e.getKey();
        if (cal == 13) {
            var textval = this.textbox.getValue();
            if (textval != '') {
                this.handleBttnClick();
            }
        }
    },
    handleMouseOver: function(e){
        var span = e.getTarget();
        span.style.backgroundColor = 'White';
        span.style.cursor = 'pointer';
    },
    handleMouseOut: function(e){
        var span = e.getTarget();
        span.style.backgroundColor = '';
    },
    handleMouseClick: function(obj, e){
        var event = e;
        var innerhtml = event.innerHTML.replace(/'/g,"");

        this.quickSearchTF.setValue("");
        this.tagSearchTF.setValue(innerhtml);
        this.toolbar.searchField=this.tagSearchTF;
        this.searchType.setValue(0);
        this.hideShowColumns(false);
        this.grid1.getView().refresh();
        this.ds.baseParams.searchType=this.searchType.getValue();

        this.grid1.getStore().reload({
            params:{
                start:0,
                limit:this.pP.combo.value,
                tag:innerhtml
            }
        });
        this.divele.innerHTML = '';

        var spanele;
        var div = this.divele;
        div.innerHTML = "";
        div.style.display = 'block';
        this.editable = 1;
        spanele = document.createElement("span");
        spanele.className = 'spanelement';
        spanele.innerHTML = 'Searched by Tag name :'+innerhtml;
        spanele.id = 'span' + 1;
        spanele.style.color = "#15428b";
        div.appendChild(spanele);

        this.clrfilterBttn.enable();
        this.grid1.getSelectionModel().clearSelections();

    }
}); 

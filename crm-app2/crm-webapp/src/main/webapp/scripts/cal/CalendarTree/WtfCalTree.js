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

Wtf.CalendarTree  = function(config){
    Wtf.apply(this,config);
    this.nodeHash = {};
    this.containerScroll=true;
    this.border=false;
    this.bodyStyle = "padding:10px;";
    this.rootVisible= true;
    this.lines= false;
    this.agendaStorereder = Wtf.data.Record.create(["cid","cname","description","location","timezone","colorcode","caltype","isdefault","userid","timestamp","permissionlevel","exportCal", "deleteflag"]);
    this.treeRoot = new Wtf.tree.TreeNode({
        id: this.parentid + 'rn',
        cls: 'calroot',
        expanded: true,
        text: WtfGlobal.getLocaleText("crm.calendar.calendartext"),//'Calendars',
        cls: 'takeleft',
        singleClickExpand: false
    });
    this.setRootNode(this.treeRoot);
    this.agendaStore = new Wtf.data.Store({
        url:this.url,
        baseParams:{
            action:0,
            userid:this.ownerid.userid,
            caltype:this.ownerid.type,
//            loginid: loginid,
            latestts:"1970-01-01 00:00:00"
        },
        reader:new Wtf.data.KwlJsonReader1({
            root:'data'
        },this.agendaStorereder)
    });

    this.agendaStore.on("loadexception",function(){
        Wtf.updateProgress();
    },this);
    this.agendaStore.load();
    this.agendaStore.on("load",this.loadTree,this);
    
//  this.datePicker.on("select",this.DateSelected,this);
    this.addEvents({
        "treecheckchange": true,
        "changecolor": true,
        "deletecalendar": true,
        "calendarsettings": true
    });

    Wtf.CalendarTree.superclass.constructor.call(this);
}

Wtf.extend(Wtf.CalendarTree,Wtf.tree.TreePanel,{
    onRender: function(config){
        Wtf.CalendarTree.superclass.onRender.call(this,config);
        this.getSelectionModel().on("beforeselect",this.selectChanged,this);
    },

    loadTree: function (obj,rec,opt){
        var nodeCreated=true;
        for (var cnt=0;cnt<this.agendaStore.getTotalCount();cnt++) {
            var agenData = this.agendaStore.getAt(cnt).data;
            var calname = name = Wtf.util.Format.ellipsis(agenData.cname,15);
            if(agenData.cid=="1")
                nodeCreated= false
            if(this.treeRoot.ui.getEl().offsetHeight != 0){
                var tnode=this.treeRoot.appendChild(new Wtf.tree.TreeNode({
                    text: calname,
                    allowDrop: false,
                    allowDrag: false,
                    checked: (!nodeCreated),
                    icon: "lib/resources/images/default/s.gif",
                    iconCls: 'imgchange',
                    cls: 'treenodeclass',
                    qtip : agenData.description,
                    qtipTitle : agenData.cname,
                    id: agenData.cid,
                    uiProvider: Wtf.tree.TableTreeUI,
                    colorIndex: parseInt(agenData.colorcode),
                    deleteflag : agenData.deleteflag
                }));
                if(tnode)
                    tnode.attributes.request=false;
                nodeCreated=true;
            }
        }
        if(this.treeRoot.ui.getEl().offsetHeight!=0){
        this.defaultNode = this.getNodeById(this.agendaStore.getAt(0).data["cid"]);
        if(this.defaultNode)
           this.defaultNode.attributes.request = false;
        this.calcontrol = Wtf.getCmp(this.calControl);
        if(this.defaultNode)
           this.calcontrol.startCalEventBot(this.defaultNode.id);
//            this.calcontrol.onWorkWeekViewClick();
        this.calcontrol.onDayViewClick();
        this.calcontrol.CalculatingTotalCalendar();        
        }
        this.attachListeners();
    },

    makeContextMenu: function(nodeobj){
        this.contextMenu = null ;
        var colorPicker = new Wtf.menu.ColorItem({
            style:"padding-bottom:5px;",
            id: this.parentid + 'coloritem',
            colors: ["CC3333", "DD4477", "994499", "6633CC", "336699", "3366CC", "22AA99", "329262", "109618", "66AA00", "AAAA11", "D6AE00", "EE8800", "DD5511", "A87070", "8C6D8C", "627487", "7083A8", "5C8D87", "898951", "B08B59"]
        });

        this.contextMenu = new Wtf.menu.Menu({
            id: this.parentid + 'contextMenu',
            items: [{
                id: this.parentid + 'Delete',
                iconCls: 'pwnd delicon',
                hidden : (this.getSelectionModel().getSelectedNode().attributes.deleteflag == '1') ? true : false,
                text: WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//'Delete',
                scope: this,
                handler: function(){
                    if(this.treeRoot.firstChild == this.getSelectionModel().getSelectedNode()){
                        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), WtfGlobal.getLocaleText("crm.calendar.cannotdeletedefaultcalmsg"), function(btn){
                            if(btn == "yes"){
                                var msg = {};
                                msg.events = true;
                                msg.id = this.treeRoot.firstChild.id;
                                this.fireEvent("deletecalendar",msg.id);
                            }
                        },this);
                    }
                    else{
                        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),WtfGlobal.getLocaleText("crm.confirmdeletemsg"), function(btn){
                            if (btn == "yes") {
                                var msg = {};
                                msg.events = false;
                                msg.id = this.getSelectionModel().getSelectedNode().id;
//                                this.getSelectionModel().getSelectedNode().remove();
//                                this.fireEvent("deletecalendar",this,msg);
                                this.fireEvent("deletecalendar",msg.id);
                            }
                        },this);
                    }
                }
            }, {
                id: this.parentid + 'Edit',
                iconCls: 'dpwnd editiconwt',
                text: WtfGlobal.getLocaleText("crm.SETTINGLABEL"),//'Settings',
                scope: this,
                handler: function(){
                    this.fireEvent("calendarsettings",this.getSelectionModel().getSelectedNode().id);
                }
            }, {
                id: this.parentid + 'thisonly',
                text: WtfGlobal.getLocaleText("crm.calendar.displayonlythiscalmsg"),//'Display only this calendar',
                iconCls: 'dpwnd checked',
                scope: this,
                handler: function(){
                    var s = this.getSelectionModel().getSelectedNode();
                    if(nodeobj.getOwnerTree() != null)
                        var sm = nodeobj.getOwnerTree().treeRoot.childNodes;
                    else
                        sm = this.treeRoot.childNodes;
                    for(var i=0;i<sm.length;i++){
                        sm[i].ui.checkbox.checked = false;
                        sm[i].attributes.checked = false;
                        this.fireEvent("treecheckchange",this,sm[i],false);
                    }
                    s.ui.checkbox.checked = true;
                    s.attributes.checked=true;
                    this.fireEvent("treecheckchange",this,s,true);
                 }
            }, {
                id: this.parentid + 'exportthisonly',
                text: WtfGlobal.getLocaleText("crm.calendars.exportparticularcaloption"),//'Export this calendar',
                iconCls: 'dpwnd calexporticon',
                scope: this,
                handler: function(){
                    var s = this.getSelectionModel().getSelectedNode();
                    var exportURL1 = Wtf.pagebaseURL + "exportICS.ics?cid=" + s.id;
                    if(s.id == '1')
                        exportURL1 += "&uid="+loginid
                    var iCalLink1 = "<a href='javascript:document.exportLinkForm1.exportLinkField1.select()'>Select URL</a>";
                    iCalLink1= iCalLink1+"<form name='exportLinkForm1'><textarea readonly='' name='exportLinkField1' style='width:500px;margin-left:45px; background:white' onclick='javascript:document.exportLinkForm1.exportLinkField1.select()'>"+exportURL1+"</textarea>";
                    var msgExport1 = WtfGlobal.getLocaleText("crm.calendar.exportcal.msg")+"<br/>"+iCalLink1;//"Please use the following address to access your calendar from other applications. You can copy and paste this into any calendar product that supports the iCal format.<br/>"+iCalLink1;
                    calMsgBoxShow([WtfGlobal.getLocaleText("crm.calendar.calendaraddress"), msgExport1], 0);
                 }
            }/*,{
                id: this.parentid + 'rssthiscalonly',
                text: 'RSS feed for this calendar',
                iconCls: 'pwnd rssfeedicon',
                scope: this,
                handler: function(){
                    var s = this.getSelectionModel().getSelectedNode();
                    window.open(Wtf.pagebaseURL+"feed.rss?m=events&c="+s.id,'_blank');
                 }
            }*/,
            '-',
            colorPicker
            ]
        });
        colorPicker.on('select', function(palette, selColor){
            var colorIndex = palette.colors.indexOf(selColor);
            var node = this.getSelectionModel().getSelectedNode();
            /*node.getUI().SetBackColor(colorIndex);*/
            this.fireEvent("changecolor",this,node,colorIndex);
        },this);
    },

    attachListeners: function(){
        this.on('checkchange', function(node, e){
            node.select();
            this.fireEvent("treecheckchange",this,node,e);
        },this);
        this.on('contextmenu', function(nodeobj,x,y){
            if (nodeobj != this.treeRoot) {
                this.getNodeById(nodeobj.id).select();
                    this.makeContextMenu(nodeobj);

                this.contextMenu.showAt([x + 11, y]);
                var t=this.agendaStore.find("cid",nodeobj.id);
                var perm=1;
                if(t!=-1){
                    perm=this.agendaStore.getAt(t).data["permissionlevel"];
                }
                var calType = this.agendaStore.getAt(t).data["caltype"];
                var delBtn = Wtf.getCmp(this.parentid+'Delete');
                var edBtn = Wtf.getCmp(this.parentid+'Edit');
                var clBtn = Wtf.getCmp(this.parentid+'coloritem');
                if(calType == 3 && !this.archived) {
                    // For holiday calendar
                    delBtn.enable();
                    edBtn.disable();
                    clBtn.enable();
                }
                else if(perm!="" || this.archived) {
                    delBtn.disable();
                    edBtn.disable();
                    clBtn.disable();
                }
                else{
                    delBtn.enable();
                    edBtn.enable();
                    clBtn.enable();
                }
            }
        },this);
        
        this.on('click', function(node, e){
            var calback = this.getNodeById(node.id).ui;
            calback.getTextEl().style.background = "transparent none repeat scroll 0%";
            calback.getTextEl().style.color = "#000000";
        },this);
    },
    selectChanged : function() {
  //          this.calcontrol = Wtf.getCmp(this.parentid +'calctrl');
//            this.calcontrol.onDayViewClick();
            //var parentTab = Wtf.getCmp(this.parentTabId);
            //if(!this.calcontrol){
               /* this.calcontrol = new Wtf.cal.control({
                        id: this.parentid +'calctrl',
                        title:'My Calendar',
                        tabType:Wtf.etype.cal,
                        iconCls:getTabIconCls(Wtf.etype.cal),
                        closable: true,
                        border: false,
                        ownerid: {type:0,userid:loginid},
                        myToolbar: true,
                        calTabId: 'tabmycal',
                        layout: "fit",
                        url: Wtf.calReq.cal + "caltree.jsp",
                        calTree:this,
                        calendar:this.datePicker,
                        mainCal:true

                    });
                    parentTab.add(this.calcontrol);
                    parentTab.doLayout();
                    parentTab.setActiveTab(this.calcontrol);
                    this.calcontrol.on("destroy",function(obj){
                        var chkNode = obj.calTree.getChecked();
                        for(var i=0;i<chkNode.length;i++){
                            chkNode[i].attributes.request = false;
                        }
                        obj.calTree.getSelectionModel().clearSelections();
                        obj.calTree.calcontrol = null;

                    });*/
          //  }
            //else{
                //parentTab.setActiveTab(this.calcontrol);
            //}

    },
    DateSelected : function() {
        if(!this.calcontrol)
            this.selectChanged();
        else {
            var parentTab = Wtf.getCmp(this.parentTabId);
            parentTab.setActiveTab(this.calcontrol);
        }
    }
 });
 /*//FILE:=============================createcal.js==========================*/

 Wtf.cal.createCal=function(MainPanel){
    this.parent = MainPanel;
    this.selNodeId=null;
    this.colorInd="";
    this.calRec = Wtf.data.Record.create([
    {name: 'cid'},
    {name: 'cname'},
    {name: 'description'},
    {name: 'location'},
    {name: 'timezone'},
    {name: 'colorcode'},
    {name: 'caltype'},
    {name: 'isdefault'},
    {name: 'userid'},
    {name: 'timestamp'}
]);

 Wtf.cal.createCal.superclass.constructor.call(this);
};
Wtf.extend(Wtf.cal.createCal,Wtf.Component,{

createcal : function(e){
    var parentId = this.parent.id;
    var formitems = Wtf.getCmp(parentId+'inner1').form.items.items;
    if(!(Wtf.getCmp(parentId+'inner1').form.isValid())){
    	if(formitems[1].getValue().length>formitems[1].maxLength){
        	WtfComMsgBox(['Alert',"Please enter valid inputs for the mandatory fields.<br/>Value in the field exceeding to max length."]);
        	return;
        }else
       calMsgBoxShow(['Invalid Input', "Please enter the required fields"], 1);
       return;
    }
    var Grid  = Wtf.getCmp(parentId+'gridpanel');
    var ds = Grid.getStore();
    var permissionString="";

    for(var i=0;i<ds.getCount();i++){
        var recdata = ds.getAt(i).data;
        permissionString += recdata.userid;
        permissionString += "_"+recdata.resourcename+",";
    }
    var treenodes = this.parent.calTree.treeRoot.childNodes.length;
    var currentTree = this.parent.calTree.tree;
    var name = Wtf.get(parentId+'calname').getValue();
    var duplicate=false;
    for(var i=0;i<this.parent.calTree.treeRoot.childNodes.length;i++){
        var nodeattr = this.parent.calTree.treeRoot.childNodes[i].attributes;
    	var nodeName = nodeattr.text;
    	if(name==nodeName && this.selNodeId!=nodeattr.id){
    		
    		duplicate=true;
    		break;
    	}
    }
//    name = Wtf.cal.utils.HTMLScriptStripper(name);
    name = WtfGlobal.HTMLStripper(name);
    var description = Wtf.get(parentId+'des').getValue();
//    description = Wtf.cal.utils.HTMLScriptStripper(description);
    description = WtfGlobal.HTMLStripper(description);
    var TZ = Wtf.getCmp(parentId+'timezone').value;
    var timezone = (TZ== "< Select a time zone >")
                  ?""
                  :TZ;
    var CNT = Wtf.getCmp(parentId+'country').value;
    var country = (CNT== "< Select a country >")
                  ?""
                  :CNT;
    if(this.colorInd==""){
        this.colorInd=0;
    }
    if(duplicate==true){
    	Wtf.MessageBox.alert('Error','Calendar with name "'+name+'" already exists');
    }else if(name == "" || timezone == ""){// || country == "")
        Wtf.MessageBox.alert('Entry Missing','Please provide values for mandatory fields (*)');
        Wtf.getCmp(this.parent.id+'calname').markInvalid();
        Wtf.getCmp(this.parent.id+'timezone').markInvalid();}
    //
    ////kapil
//    if(name == "")
//        Wtf.MessageBox.alert('Invalid Entry','Please enter the calendar name!');
    else {
        var exportCal = Wtf.getCmp(this.parent.id+'exportcal').getValue();
        if(!this.selNodeId){
            // commented by SM
            var cRecord = [name, description, country, timezone, this.colorInd, this.parent.calTree.ownerid.type, "0", this.parent.calTree.ownerid.userid, permissionString, exportCal];
//            var cRecord = [name, description, country, timezone, this.colorInd, 0, "0", 1, permissionString, exportCal];
            this.parent.insertCalendar(cRecord);
        }
        else{
            var isdefault="0";
            var t=this.parent.calTree.agendaStore.find("cid",this.selNodeId);
            if(t!=-1)
                isdefault=this.parent.calTree.agendaStore.getAt(t).data["isdefault"];
            // commented by SM
//            var cRecord = [this.selNodeId, name, description, country, timezone, this.colorInd, this.parent.calTree.ownerid.type, isdefault, this.parent.calTree.ownerid.userid, permissionString, exportCal];
            var cRecord = [this.selNodeId, name, description, country, timezone, this.colorInd, 0, isdefault, this.parent.calTree.ownerid.userid, permissionString, exportCal];
            this.parent.updateCalendar(cRecord);
        }
        this.parent.addCalendarTab1();
        this.selNodeId=null;
    }
},
CreateCalendar : function(nodeId){
    this.parent.add(new Wtf.Panel({
        frame:true,
        layout: "fit",
        deferredRender:true ,
        id: this.parent.id+'createCalForm',
        border:false,
        items: [{
            bodyStyle: "position: relative;",
            autoScroll:true,
            id: this.parent.id+'innerpanel'
        }]
    }));

    this.Addfield();
    this.Addbuttons();
    this.clearCalFormFields();
    if(nodeId)
        this.showSettingsForm(nodeId);
},
clearCalFormFields : function(){
    this.parent.formview ='CreateCal';
    var parentId = this.parent.id;
    if(Wtf.getCmp(parentId+'calname').getValue().trim().length>0)
        Wtf.getCmp(parentId+'calname').setValue("");
    Wtf.getCmp(parentId+'des').setValue("");
    Wtf.getCmp(parentId+'selectColor').select("CC3333");
//    Wtf.getCmp(parentId+'person').setValue("< Select the user name >");
    if(Wtf.getCmp(parentId+'timezone').getValue()!=undefined && Wtf.timezoneName) {
        Wtf.getCmp(parentId+'timezone').setValue(Wtf.timezoneName);
    }
    Wtf.getCmp(parentId+'country').setValue(Wtf.countryName);
    var Grid =  Wtf.getCmp(parentId+'gridpanel');
    Grid.getStore().removeAll();
    Wtf.getCmp(parentId+'create').setText(WtfGlobal.getLocaleText("crm.calendar.createcalendar.title"));//'Create Calendar');
    Wtf.getCmp(parentId+'exportcal').setValue('true');
},


showSettingsForm : function(nodeId){
    this.parent.formview ='CreateCal';
    if(nodeId){
        this.selNodeId=nodeId;
        if(this.parent.calTree.agendaStore){
            var rec= new this.calRec();
            var t=this.parent.calTree.agendaStore.find("cid",this.selNodeId);
            if(t!=-1){
                var recData=this.parent.calTree.agendaStore.getAt(t).data;
                var cRecord = [recData.cid, recData.cname, recData.description, recData.location, recData.timezone, recData.colorcode, recData.caltype, recData.isdefault, recData.userid, recData.timestamp, recData.exportCal];
                this.displaySettingValues(cRecord);

//                var calPerm=rec.data["permissionlevel"];
//                if(calPerm!=""||calPerm>1){
//                    Wtf.getCmp(MainPanel.id+'sharingfield').hide();
//                }
//                else{
//                    Wtf.getCmp(MainPanel.id+'sharingfield').show();
//                }      
            }
        }
    }
},
Addbuttons : function(){
    Wtf.getCmp(this.parent.id+"inner1").add(new Wtf.Panel({
        id:this.parent.id+'addbuttons',
        items:[
            {
                xtype : "button",
                cls:'button1',
                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                id:this.parent.id+'cancel',
                scope:this.parent,
                handler:function(){
//                    clearCalFormFields(this);
                    this.addCalendarTab1();
                    this.selNodeId=null;
                }
            },
            {
                xtype : "button",
                cls:'button11',
                text:WtfGlobal.getLocaleText("crm.calendar.createcalendar.title"),//'Create Calendar',
                id:this.parent.id+'create',
                handler:this.createcal,
                scope:this
            }
        ]
    }));
    this.parent.doLayout();
},


Addfield : function(){
   /* var storet = new Wtf.data.SimpleStore({
        fields: ['id', 'name'],
        data : Wtf.form.ComboBox.timezone
    });	*/
    var stores = new Wtf.data.SimpleStore({
        fields: ['abbr', 'state'],
        data : Wtf.form.ComboBox.sharing
    });
    
     if(!Wtf.StoreMgr.containsKey("alltimeZone")){
        Wtf.timezoneStore.load();
        
    }
    getCountryName();

//    Wtf.Ajax.request({
//        url: '../../jspfiles/cal/caltree.jsp',
//        params:{
//            action:7
//        },
//        method:'POST',
//        scope:this,
//        success: function(result, req){
//            var obj = Wtf.decode(result.responseText);
//            Wtf.getCmp(this.parent.id+'timezone').setValue(obj.timezone);
//            Wtf.getCmp(this.parent.id+'country').setValue(obj.country);//
//        },
//        failure:function(result, req){
//
//        }
//   });

    var authorityStore = new Wtf.data.SimpleStore({
        fields:['abbr','resourcename'],
        data:Wtf.form.ComboBox.resourcename
    });

    Wtf.ux.comboBoxRenderer = function(combo) {
        return function(value) {
            var idx = combo.store.find(combo.valueField, value);
            var rec = combo.store.getAt(idx);
            return rec.get(combo.displayField);
        };
    }

    var combo = new Wtf.form.ComboBox({
        selectOnFocus:true,
//        typeAhead: true,
        triggerAction: 'all',
        editable: false,
        mode: 'local',
        store: authorityStore,
        displayField: 'resourcename',
        valueField:'abbr'
    });

    var mailgrid = new Wtf.grid.EditorGridPanel({
        id:this.parent.id+'gridpanel',
        clicksToEdit:1,
        selModel: new Wtf.grid.RowSelectionModel(),
        ds: new Wtf.data.Store({
            id: this.parent.id + "datastore",
            reader: new Wtf.data.ArrayReader({},[{
                name: 'Email'
            },{
                name:'userid'
            },{
                name:'resourcename'
            },{
                name:'setbutton'
            }])
        }),
        cm:new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),// "Name",
                dataIndex:'fullname'
            },
            {
                header:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.email"),//"Email Address",
                dataIndex:'Email'
            },{
                header:WtfGlobal.getLocaleText("crm.usermanagement.msg.titlePermission"),//"Permission",
                dataIndex: 'resourcename',
                editor: combo,
                renderer:Wtf.ux.comboBoxRenderer(combo)
            },{
                header:WtfGlobal.getLocaleText("crm.calendar.createcalendar.useridfield"),//"User Id",
                dataIndex:'userid',
                hidden:true
            },{
                header:WtfGlobal.getLocaleText("crm.DELETEBUTTON"),// "Delete",
                renderer:this.setbutton,
                dataIndex:'setbutton',
                width:20
            }
        ]),
        viewConfig:{
            forceFit:true,
            autoFill:true
        },
        height: 95
    });
    //var contacts=Wtf.getCmp('contactsview');

    Wtf.getCmp(this.parent.id+'innerpanel').add(new Wtf.FormPanel({
        id: this.parent.id+'inner1',
        labelWidth: 130,
        layout:'form',
        border: false,
        items:[{
                html: "<span style=\"float:right; margin-right:30px;\">( * indicates required fields )</span>"
               },{
            xtype: 'fieldset',
            title:WtfGlobal.getLocaleText("crm.audittrail.header.details"),//'Details',
            border: false,
            autoHeight: true,
            items:[{
                        fieldLabel: WtfGlobal.getLocaleText("crm.calendar.calendarname")+'*',//'Calendar Name* ',
                        allowBlank: false,
                        maxLength:50,
                        xtype: 'textfield',
                        id : this.parent.id+'calname',
                        width:'98%',
                        tabIndex:1,
                        anchor:'71%'
                   },{
                        xtype: 'textarea',
                        fieldLabel:WtfGlobal.getLocaleText("crm.contact.defaultheader.desc"),// 'Description ',
                        id:this.parent.id+'des',
                        width:'98%',
                        tabIndex:1,
                        anchor:'71%',
                        maxLength:100
                   },{
                        xtype : 'combo',
                        tabIndex:1,
                        fieldLabel:WtfGlobal.getLocaleText("crm.calendar.calendardetails.country"),// 'Country ',
//                        allowBlank: false,
                        id:this.parent.id+'country',
                        store:Wtf.countryStore,
                        displayField:'name',
                        //emptyText:'< Select a country >',
                        valueField:'id',
                        forceSelection : true,
                        typeAhead: true,
                        mode: 'local',
                        triggerAction: 'all',
                        selectOnFocus:true,
                        anchor:'71%'
                   },{
                        xtype : 'combo',
                        tabIndex:1,
                        allowBlank: false,
                        fieldLabel:WtfGlobal.getLocaleText("crm.calendar.calendardetails.timezone")+'*',// 'Time Zone* ',
                        id:this.parent.id+'timezone',
//                        emptyText:'< Select a time zone >',
                        store: Wtf.timezoneStore,
                        displayField:'name',
                        maxHeight:200,
                        valueField:'id',
                        forceSelection : true,
                        typeAhead : true,
                        disabled:true,
//                        value:Wtf.pref.Timezoneid,
                        mode: 'local',
                        triggerAction: 'all',
                        selectOnFocus:true,
                        anchor:'71%'
                   },{
                        xtype : 'panel',
                        layout:'column',
                        border: false,
                        items:[{
                                layout:'form',
                                width:135,
                                border: false,
                                items:{
                                    labelWidth: 130,
                                    fieldLabel:WtfGlobal.getLocaleText("crm.calendar.calendardetails.color"),//'Color ',
                                    id:"textField",
                                    hideField:true,
                                    xtype: 'textfield'
                                }
                               },{
                                border: false,
                                width:150,
                                items:[
                                        new Wtf.ColorPalette({
                                            cls:'palette',
                                            value:"CC3333",
                                            id : this.parent.id+'selectColor',
                                            colors: ["CC3333", "DD4477", "994499", "6633CC", "336699", "3366CC", "22AA99", "329262", "109618", "66AA00", "AAAA11", "D6AE00", "EE8800", "DD5511", "A87070", "8C6D8C", "627487", "7083A8", "5C8D87", "898951", "B08B59"]
                                    })]
                            }]
                   },{
                        fieldLabel: WtfGlobal.getLocaleText("crm.calendar.calendardetails.exportcalendar"),//'Export Calendar ',
                        allowBlank: false,
                        maxLength:50,
                        xtype: 'checkbox',
                        checked: true,
                        fieldClass: 'calChkbox',
                        id : this.parent.id+'exportcal',
                        tabIndex:1
                   }
                ]}
        // sharing code start
        /*{
            xtype: 'fieldset',
            title:'Sharing',
            height:170,
            id:this.parent.id+'sharingfield',
            items:[{
                id:this.parent.id+'pqrs',
                layout: 'column',
                items: [{
                    columnWidth:0.715,
                    labelWidth:130,
                    layout:'form',
                    items:[{
                            xtype : 'combo',
                            editable:false,
                            fieldLabel: 'User Name',
                            emptyText:'< Select the user name >',
                            id:this.parent.id+'person',
                            store: calContacts_Store,
                            displayField:'fullname',
                            valueField:'userid',
                            mode: 'local',
                            triggerAction: 'all',
                            selectOnFocus:true,
                            anchor:'100%'
                      }]
                },{
                    columnWidth:0.1,
                    layout:'form',
                    items:[new Wtf.Template(
                        '<table id='+this.parent.id+'addedperson><tr>',
                        '<td><img  id='+this.parent.id+'addperson src="images/tabicons_02.gif" title="ADD PERSON" class = "addpersonbutton""></img></td></tr></table>'
                    )]
                }]
            },{
                border: false,
                id: this.parent.id + 'mailGridPanel',
                height:110,
                layout:'border',
                items:[{
                    height:100,
                    region:'center',
                    layout:'fit',
                    items:mailgrid}]
            }]
        }*/ // sharing code end
        ]
    }));
//    if(this.parent.calTree.ownerid.type==2){
//        Wtf.getCmp(this.parent.id+'sharingfield').hide();
//    }
    this.parent.doLayout();
    Wtf.getCmp(this.parent.id+'gridpanel').on('afteredit', this.remGridClass, this);
//    Wtf.get(this.parent.id+'addperson').on("click",this.addshareperson,this);
    Wtf.get('textField').hide();
    Wtf.getCmp(this.parent.id+'selectColor').on('select', function(palette, selColor){
        this.colorInd = palette.colors.indexOf(selColor);
    },this);
    var calName = Wtf.getCmp(this.parent.id+'calname');
    calName.on("change", function(){
        calName.setValue(WtfGlobal.HTMLStripper(calName.getValue()));
    },this);

//    Wtf.getCmp(this.parent.id+'country').on("select",function(a,b,c){
//        var rec = Wtf.countryStore.getAt(c);
//        if(rec.data.timezone)
//            Wtf.getCmp(this.parent.id+'timezone').setValue(rec.data.timezone + rec.data.name);
//        else{
//            Wtf.getCmp(this.parent.id+'timezone').setValue("(GMT-08:00)" + rec.data.name);
//        }
//    }, this)
},

remGridClass:function(e){
    Wtf.get(Wtf.getCmp(this.parent.id+'gridpanel').getView().getCell(e.row, e.column)).removeClass('x-grid3-dirty-cell');
},
setbutton : function(){
    return("<img src='images/Delete.gif'  id=_delbutton  title="+WtfGlobal.getLocaleText("crm.calendar.calendardetails.deletepersonbtn")+"class='xbtn'></img>");
},

addshareperson : function(){
    var mailGridData = Wtf.getCmp(this.parent.id+"gridpanel").store;
    var validRegExp = /^[^@]+@[^@]+.[a-z]{2,}$/i;
    var strid = Wtf.getCmp(this.parent.id+'person').getValue();
    var fullname = Wtf.get(this.parent.id+'person').getValue();
        if(fullname == '< Select the user name >'){
            msgBoxShow(89, 1);
        return 0;
    }
    //var contacts=Wtf.getCmp('contactsview');
    var t=calContacts_Store.find("userid",strid);
    var rec=calContacts_Store.getAt(t);
    if(rec){
        var strEmail = rec.data["emailid"];
       // search email text for regular exp matches
        if (strEmail.search(validRegExp) == -1) {
            msgBoxShow(90, 1);
            return 0;
        }
        var TopicRecord = Wtf.data.Record.create(
            {name: 'fullname'},
            {name: 'Email'},
            {name: 'userid'},
            {name: 'resourcename'},
            {name: 'setbutton'}
        );
        var tr = new TopicRecord({
            fullname:fullname,
            Email:strEmail,
            userid:strid,
            resourcename:1,
            setbutton:''
        });

        strEmail = this.checkRepeatMail(strEmail);
        if(strEmail==null){
            mailGridData.add(tr);
         }
         Wtf.get('_delbutton').on("click",this.deleteClick,this);
     }
},

checkRepeatMail : function(strEmail){
    var mailGridData = Wtf.getCmp(this.parent.id+"gridpanel").store;
    for(var i=0;i<mailGridData.getCount();i++){
        if(strEmail == mailGridData.getAt(i).data['Email']){
            calMsgBoxShow(5, 1);
            //Wtf.Msg.alert('Error', 'Email Address is already present');
            return(strEmail);
        }
    }
    strEmail = null;
    return(strEmail);
},

deleteClick : function(e) {

    var Grid = Wtf.getCmp(this.parent.id+"gridpanel");
    var rowselectmodel = Grid.getSelectionModel();
    var rowselect = rowselectmodel.getSelections();
    var mailGridData = Grid.getStore();
    Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),WtfGlobal.getLocaleText("crm.confirmdeletemsg"),function(btn){
        if(btn == 'yes'){
            mailGridData.remove(rowselect[0]);
        }
    });
},

displaySettingValues : function(rec){
    var parentId = this.parent.id;
    Wtf.getCmp(parentId+'create').setText(WtfGlobal.getLocaleText("crm.calendar.calendardetails.editcal"));
    Wtf.getCmp(parentId+'calname').setValue(rec[1]);
    Wtf.getCmp(parentId+'des').setValue(rec[2]);

    if(!Wtf.StoreMgr.containsKey("country")) {
        Wtf.countryStore.on("load", function() {
            Wtf.getCmp(parentId+'country').setValue(rec[3]);
        });

    } else {
        Wtf.getCmp(parentId + "country").setValue(rec[3]);
    }

    if(!Wtf.StoreMgr.containsKey("alltimeZone")) {
        Wtf.timezoneStore.on("load", function() {
            Wtf.StoreMgr.add("alltimeZone",Wtf.timezoneStore);
            Wtf.getCmp(parentId+'timezone').setValue(Wtf.pref.Timezoneid);
        });
    } else {
        Wtf.getCmp(parentId + "timezone").setValue(Wtf.pref.Timezoneid);
    }
    
    Wtf.getCmp(parentId + "timezone").setValue(Wtf.pref.Timezoneid);
    var colorfield = ["CC3333", "DD4477", "994499", "6633CC", "336699", "3366CC", "22AA99", "329262", "109618", "66AA00", "AAAA11", "D6AE00", "EE8800", "DD5511", "A87070", "8C6D8C", "627487", "7083A8", "5C8D87", "898951", "B08B59"];
    Wtf.getCmp(parentId+'selectColor').select(colorfield[rec[5]]);
//    if(rec[10])
        Wtf.getCmp(parentId+'exportcal').setValue('true');
//    else
//        Wtf.getCmp(parentId+'exportcal').setValue('false');
    Wtf.Ajax.requestEx({
            method: 'GET',
            url:  Wtf.calReq.cal + 'getcalpermission.do',
            params: ({
            cid: this.selNodeId,
            caltype:this.parent.calTree.ownerid.type
        })},
        this,
        function(result, req){
            var nodeobj = result;//eval("(" + result + ")");
            var Grid =  Wtf.getCmp(this.parent.id+'gridpanel');
            var mailStore = Grid.getStore();
            var mailRecord = Wtf.data.Record.create([{name: 'fullname'},{name: 'Email'},{name: 'userid'},{name: 'resourcename'}, {name: 'setbutton'}]);
           // var contacts=Wtf.getCmp('contactsview');
            for(var i=0;i<nodeobj.data.length;i++){
                var contactCheck = calContacts_Store.find("userid",nodeobj.data[i].userid);
                if(contactCheck!=-1){
                    var _data = calContacts_Store.getAt(contactCheck).data;
                    var p = new mailRecord({
                        fullname: _data.fullname,
                        Email : _data.emailid,
                        userid: _data.userid,
                        resourcename:nodeobj.data[i].permissionlevel,
                        setbutton:''
                    })
                    mailStore.add(p);
                }
                if(Wtf.get('_delbutton')) {
                     Wtf.get('_delbutton').on("click",this.deleteClick,this);
                }

        }},
        function(result, req){
            msgBoxShow(4, 1);
           //Wtf.Msg.alert('Error', 'Error occurred while connecting to the server');
    });
//    Wtf.Ajax.request({
//        url: Wtf.calReq.cal + 'caltree.jsp',
//        method: 'GET',
//        params: ({
//            action: 4,
//            cid: this.selNodeId,
//            caltype:this.parent.calTree.ownerid.type
//        }),
//        scope: this,
//        success: function(result, req){
//            var nodeobj = eval("(" + result.responseText.trim() + ")");
//            var Grid =  Wtf.getCmp(this.parent.id+'gridpanel');
//            var mailStore = Grid.getStore();
//            var mailRecord = Wtf.data.Record.create([{name: 'fullname'},{name: 'Email'},{name: 'userid'},{name: 'resourcename'}, {name: 'setbutton'}]);
//           // var contacts=Wtf.getCmp('contactsview');
//            for(var i=0;i<nodeobj.data.length;i++){
//                var contactCheck = calContacts_Store.find("userid",nodeobj.data[i].userid);
//                if(contactCheck!=-1){
//                    var p = new mailRecord({
//                        fullname: calContacts_Store.getAt(contactCheck).get("fullname"),
//                        Email : calContacts_Store.getAt(contactCheck).get("emailid"),
//                        userid: calContacts_Store.getAt(contactCheck).get("userid"),
//                        resourcename:nodeobj.data[i].permissionlevel,
//                        setbutton:''
//                    })
//                    mailStore.add(p);
//                }
//                if(Wtf.get('_delbutton')) {
//                     Wtf.get('_delbutton').on("click",this.deleteClick,this);
//                }
//
//            }
//        },
//        failure: function(){
//            Wtf.Msg.alert('Error', 'Error occurred while connecting to the server');
//        }
//    });
}
})/*================================createcal.js================================*/

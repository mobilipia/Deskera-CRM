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
Wtf.AboutView = function(config){
    Wtf.apply(this, config);
    Wtf.AboutView.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.AboutView, Wtf.Panel, {
    initComponent: function(){
        Wtf.AboutView.superclass.initComponent.call(this);

        this.image1 = document.createElement('img');
        if(Wtf.isGecko)
            this.image1.className = "tabimageG";
        else
            this.image1.className = "tabimage";
        this.UserTagurlpath = null;
        this.UserTagfields = null;
        this.view17 = new Wtf.DataView({
            itemSelector: 'div.thumb-wrap',
            style: 'overflow:auto;width:auto;height:auto;margin-left:8px;',
            multiSelect: true,
            autoHeight:true,
            loadingText: WtfGlobal.getLocaleText("crm.profileview.load")//'Collecting data...'
        });

        this.view16 = new Wtf.DataView({
            itemSelector: 'div.thumb-wrap',
            style: 'overflow:auto;width:auto;height:auto;margin-left:8px;',
            multiSelect: true,
            loadingText: WtfGlobal.getLocaleText("crm.profileview.load")// 'Collecting data...'
        });

        this.tagbutton2 = document.createElement('img');
        this.tagbutton2.src = '../../images/tag_green.gif';
        this.tagbutton2.title =  WtfGlobal.getLocaleText("crm.profileview.edittags");//"Edit Tags";
        this.tagbutton2.className = 'addtagbutton';

        this.view18 = new Wtf.DataView({
            itemSelector: 'a.taga',
            style: 'overflow:auto',
            multiSelect: true,
            autoHeight:true,
            emptyText: '<span class="tagtitle">Tags:</span>',
            loadingText: 'Loading',
            tpl: new Wtf.XTemplate('<span class="tagtitle">Tags: </span>', '<tpl for=".">', '<a class="taga" href="" onClick="javascript: invokeTagSearch(this);">{tagname}</a>', '</tpl>')
        });
    },

    getHtml: function(){
        var str = '';
        str = '<div><span style="font-size:2.0em;font-weight:bold;float:left;">'+Wtf.util.Format.ellipsis(Wtf.getCmp('as').getActiveTab().title, 18)+'</span>'
        if(this.WhichAbout == 'project'){
            str += '<span id="'+this.currId+'_addSpan" class="addToDashLink" style="display:block;">[ <a href="#" onClick=\'addProjectWidgetOnDash(\"addSpan\",\"'+this.currId+'\",\"true\")\' wtf:qtip="Add this project\'s widget on Dashboard">Add to Dashboard</a> ]</span></div>'+
            '<span id="'+this.currId+'_removeSpan" class="addToDashLink" style="display:none;">[ <a href="#" onClick=\'addProjectWidgetOnDash(\"removeSpan\",\"'+this.currId+'\",\"false\")\' wtf:qtip="Remove this project\'s widget from Dashboard">Remove from Dashboard</a> ]</span></div>';
        } else {
            str += '';
        }
        return str;
    },

    onRender: function(config){
        Wtf.AboutView.superclass.onRender.call(this, config);
        Wtf.get(this.tagbutton2).addListener("click", this.showTagWindow, this);
        this.toolItems = [];
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Document, Wtf.Perm.Document.doc_up)) {
            this.toolItems.push(this.document = new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("crm.activitydetailpanel.addfilesBTN"),// 'Add Files',
                id: "addfiles"+this.moduleName,
                iconCls: 'pwnd doctabicon',
                scope: this,
                handler : function() {
                    this.activityDetails.Addfiles();
                }
            })
            );
        }

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Comments, Wtf.Perm.Comments.comment_a)) {
            this.toolItems.push(this.comment=new Wtf.Toolbar.Button({
                text :WtfGlobal.getLocaleText("crm.activitydetailpanel.addCommentBTN"),//  "Add Comment",
                id:"comment"+this.moduleName,
                pressed: false,
                scope : this,
                iconCls:'pwnd addcomment',
                handler : function() {
                    this.activityDetails.addComment();
                }
            })
            );
        }
        
        
        this.ActivityBtn = new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("crm.profileview.addactivitybtn"),//"Add Activity",
            id:"activity"+this.moduleName,
            pressed: false,
            scope : this,
            iconCls:getTabIconCls(Wtf.etype.todo),
            handler : function() {
                this.addActivity();
            }
        })

        this.PhoneBtn = new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("crm.profileview.addphonecall"),//"Add Phone Call",
            id:"phonecall"+this.moduleName,
            pressed: false,
            scope : this,
            iconCls:"pwndCRM phonecallActivityicon",
            handler : function() {
                this.addPhoneCall();
            }
        })
        if(this.moduleName == "Lead") {
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.manage)) {
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.view)) {
                    this.toolItems.push(this.ActivityBtn);
                    this.toolItems.push(this.PhoneBtn);
                }
                this.toolItems.push(Wtf.editDetailButton(this));   
            // Fix Me - add admin role check like in List view i.e Wtf.URole.roleid == Wtf.AdminId
            //                this.toolItems.push(new Wtf.Toolbar.Button({
            //                    text : "Add Custom Column",
            //                    id:"customColumn"+this.moduleName,
            //                    pressed: false,
            //                    scope : this,
            //                    iconCls:'pwnd addCustomColumn',
            //                    handler : function() {
            //                        this.addCustomColumn(2);
            //                    }
            //                })
            //                );
            }
        } else if(this.moduleName == "Contact") {
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.manage)) {
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.view)) {
                    this.toolItems.push(this.ActivityBtn);
                    this.toolItems.push(this.PhoneBtn);
                }
                this.toolItems.push(Wtf.editDetailButton(this));
            // Fix Me - add admin role check like in List view i.e Wtf.URole.roleid == Wtf.AdminId
            //                this.toolItems.push(new Wtf.Toolbar.Button({
            //                    text : "Add Custom Column",
            //                    id:"customColumn"+this.moduleName,
            //                    pressed: false,
            //                    scope : this,
            //                    iconCls:'pwnd addCustomColumn',
            //                    handler : function() {
            //                        this.addCustomColumn(6);
            //                    }
            //                })
            //                );
            }
        } else if(this.moduleName == "Account") {
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.manage)) {
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.view)) {
                    this.toolItems.push(this.ActivityBtn);
                    this.toolItems.push(this.PhoneBtn);
                }
                this.toolItems.push(Wtf.editDetailButton(this));
            // Fix Me - add admin role check like in List view i.e Wtf.URole.roleid == Wtf.AdminId
            //                this.toolItems.push(new Wtf.Toolbar.Button({
            //                    text : "Add Custom Column",
            //                    id:"customColumn"+this.moduleName,
            //                    pressed: false,
            //                    scope : this,
            //                    iconCls:'pwnd addCustomColumn',
            //                    handler : function() {
            //                        this.addCustomColumn(1);
            //                    }
            //                })
            //                );
            }
        } else if(this.moduleName == "Case") {
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.manage)) {
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.view)) {
                    this.toolItems.push(this.ActivityBtn);
                    this.toolItems.push(this.PhoneBtn);
                }
                this.toolItems.push(Wtf.editDetailButton(this));            // Fix Me - add admin role check like in List view i.e Wtf.URole.roleid == Wtf.AdminId
            //                this.toolItems.push(new Wtf.Toolbar.Button({
            //                    text : "Add Custom Column",
            //                    id:"customColumn"+this.moduleName,
            //                    pressed: false,
            //                    scope : this,
            //                    iconCls:'pwnd addCustomColumn',
            //                    handler : function() {
            //                        this.addCustomColumn(1);
            //                    }
            //                })
            //                );
            }
        }  else if(this.moduleName == "Opportunity") {
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.manage)) {
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.view)) {
                    this.toolItems.push(this.ActivityBtn);
                    this.toolItems.push(this.PhoneBtn);
                }
            }
        }

        if(this.moduleName == "Account"  && Wtf.createProject ) {
            this.toolItems.push(this.addProject=new Wtf.Toolbar.Button({
                text :  WtfGlobal.getLocaleText("crm.profileview.createproject"),//"Create Project",
                id:"projectAccount"+this.moduleName,
                pressed: false,
                scope : this,
                tooltip: {
                    text: WtfGlobal.getLocaleText({key:"crm.profileview.viewprofilettip", params:[this.moduleName]})// 'Select a ' + this.moduleName + ' to view details'
                },
                iconCls:'pwnd addProject',
                handler : function() {
                    var projectwin = new Wtf.AddAccProject({
                        idX:this.id,
                        grid:this.grid,
                        recid:this.recid,
                        keyid:this.getKeyID(),
                        store:this.Store,
                        module:this.moduleName,
                        selectedRec:this.selected,
                        isDetailPanel:this.isDetailPanel
                    });
                    projectwin.show();
                }
            })
            );
        }
        if((this.moduleName=="Account" ||this.moduleName=="Opportunity" ||this.moduleName=="Lead") && this.contactsPermission) {
            this.toolItems.push(this.showcontactsButton=new Wtf.Toolbar.Button({
                text :WtfGlobal.getLocaleText("crm.CONTACT.plural"),// "Contacts",
                pressed: false,
                scope : this,
                tooltip: {text:WtfGlobal.getLocaleText({key:"crm.editor.lead.companyContactsBTN.ttip", params:[this.moduleName]})},// 'Select a '+this.moduleName+' to record related contacts.'},
                iconCls:getTabIconCls(Wtf.etype.contacts),
                disabled:true,
                handler : function() {
                    showContacts(this.selected,this.recid,this.moduleScope,this.isDetailPanel);
                }
            })
            );
        }
        
        this.activityDetails = new Wtf.ActivityDetailPanel({
            keyid:this.getKeyID(),
            grid:this.grid,
            Store:this.Store,
            mapid:this.mapid,
            border:false,
            id2:this.id2,
            moduleName:this.moduleName,
            detailPanelFlag:false,
            detailFlag: this.moduleName == "Lead" ? true : null,
            profileFlag:true,
//            createProject:Wtf.createProject,
//            viewProject:this.viewProject,
            contactsPermission:this.contactsPermission,
            selectedRec:this.selected,
            isDetailPanel:this.isDetailPanel
        });

        this.add({
            layout:"table",
            layoutConfig: {
                columns: 2
            },
            autoScroll:true,
            bodyStyle:'overflow-y: scroll',
            bbar:new Wtf.Toolbar(this.toolItems),
            items:[{
                colspan: 1,
                width:600,
                border:false,
                items:this.view17,
                bodyStyle:"margin-bottom:50px"
            },

            {
                colspan: 1,
                width:600,
                border:false,
                items: this.view16,
                bodyStyle:"margin-bottom:50px"
            },

            {
                colspan: 2,
                height: (Wtf.isIE6 || Wtf.isIE7)?1100:"",
                width:1200,
                border:false,
                layout:"fit",
                items:this.activityDetails
            }
            ]
        
        });
        
        var task = new Wtf.util.DelayedTask(function() {
            this.loadcustomdata();
            getDocsAndCommentList(this.selected, this.getKeyID(),this.id,undefined,this.moduleName,true,"email",undefined,this.contactsPermission);
        }, this);
        task.delay(20);
    },  
  
    getKeyID: function() {
        var keyid = "";
        switch(this.moduleName) {
            case "Lead":keyid = 'leadid';
                break;
            case "Contact":keyid = 'contactid';
                break;
            case "Account":keyid = 'accountid';
                break;
            case "Case":keyid = 'caseid';
                break;
        }
        return keyid;
    },
    
    setImage: function(path){
        if (path.match("/store")) {
            var index = path.lastIndexOf(".");
            var fpath = path.substr(0, index);
            var ext = path.substr(index);
            path = fpath + "_100" + ext;
        }
        this.image1.src = path;
    },

    showTagWindow: function(){
        if (!this.tagwin) {
            this.tagwin = new Wtf.TagWindow({
                cls: "tagwindow",
                border: false,
                resizable: false,
                closable: false
            });
            this.tagwin.on('savetags', function(tagstr){
                Wtf.Ajax.requestEx({
                    url: this.UserTagurlpath,
                    params: {
                        t: tagstr,
                        u: 1
                    }
                }, this, function(result, req){
                    this.view18.store.loadData(eval("(" + result + ")"));
                });
            }, this);
            this.tagwin.on('close', function(){
                this.tagwin = null;
            }, this);
            var tagarr = [];
            this.view18.store.each(function(el){
                tagarr.push(el.data.tagname);
            }, this);
            this.tagwin.setmytagstore(tagarr);
            var xy = Wtf.get(this.tagbutton2).getXY();
            this.tagwin.setPagePosition(xy);
            this.tagwin.setTagText(tagarr.join(' '));
            this.tagwin.show();
        }
        else {
            this.tagwin.close();
        }
    },
    formatdate : function(templateCount,customFields, customValues){
        var col = this.cm.findColumn("Custom_"+customFields[templateCount]);
        if(col!=null && col!=undefined && col.xtype=="datefield" && typeof(customValues[templateCount])!="string"){
            customValues[templateCount] = customValues[templateCount].format(WtfGlobal.getOnlyDateFormat());
        }
    },
    setAboutDetails: function(Abouturlpath, fields, values, customFields, customValues, refreshFlag,fieldCols,isEdit){
        var str = this._getStore(Abouturlpath, 'data', fields);
        var customColumnlength = customFields.length;
        var fieldsLength =  fields.length-1;
        var len = Math.ceil(customColumnlength/2);
        var leftTemplateStr = "<table border='0' width='100%' style='padding-left:5%;padding-top:6%;'>{[this.fetchTemplateInner(values)]}";
        for(var templateCount=0 ; templateCount < len ; templateCount++) {
            if(customValues[templateCount] == null || customValues[templateCount] == "") {
                customValues[templateCount] = " - ";
            }
            this.formatdate(templateCount,customFields, customValues);
            leftTemplateStr += "<tr><td class='leadDetailTD' style='color:#15428B;'>"+customFields[templateCount]+": </td><td class='leadDetailTD'>"+customValues[templateCount]+"</td></tr>";
        }
        leftTemplateStr += "</tpl></table>";
        this.templ = new Wtf.XTemplate(leftTemplateStr,{
            fetchTemplateInner:function(val) {
                var subowners;
                if(val[0]['subowners'] == " - " || val[0]['subowners'] == "") {
                    subowners = "";
                } else {
                    subowners = ", " + val[0]['subowners'];
                }
                if(val[0]['subowners'] == undefined){
                    subowners="";
                }
                var template = "";
                var leftLen = Math.ceil(fieldsLength/2);
                if(isEdit){
                    for(var i = 0 ; i < leftLen ; i++) {
                        if(val[0][fieldCols[i]]==null || val[0][fieldCols[i]]==""){
                            val[0][fieldCols[i]]=" - ";
                        }
                        if(fields[i] == "Owner" || fields[i] == "Account Owner") {
                            template += " <tr><td class='leadDetailTD' style='color:#15428B;'>"+fields[i]+": </td><td class='leadDetailTD' style='width:50%'><b>"+val[0][fieldCols[i]]+"</b>"+subowners+"</td></tr>";
                        } else {
                            template += "<tr><td class='leadDetailTD' style='color:#15428B;'>"+fields[i]+": </td><td class='leadDetailTD'>"+val[0][fieldCols[i]]+"</td></tr>";
                        }
                    }
                } else {
                    if(val[0][fields[i]]==null || val[0][fields[i]]==""){
                        val[0][fields[i]]=" - ";
                    }
                    for(var i = 0 ; i < leftLen ; i++) {
                        if(fields[i] == "Owner" || fields[i] == "Account Owner") {
                            template += " <tr><td class='leadDetailTD' style='color:#15428B;'>"+fields[i]+": </td><td class='leadDetailTD' style='width:50%'><b>"+val[0][fields[i]]+"</b>"+subowners+"</td></tr>";
                        } else {
                            template += "<tr><td class='leadDetailTD' style='color:#15428B;'>"+fields[i]+": </td><td class='leadDetailTD'>"+val[0][fields[i]]+"</td></tr>";
                        }
                    }
                }
                return template;
            }
        }); 
        this.view17.tpl = this.templ;
        if(refreshFlag) {
            var Obj = {};
            for (var cnt = 0; cnt<fields.length;cnt++) {
                Obj[fields[cnt]] = values[cnt] == null || values[cnt] == "" ? " - " : values[cnt];
            }
            var rec = new this.tplRecord(Obj);
            str.insert(0, rec);
            this.view17.setStore(str);

            str = this._getStore(Abouturlpath, 'data', fields);
        }
        var rightTemplateStr = "<tpl for='.'><table border='0' width='100%' style='padding-left:5%;padding-top:6%;'>{[this.fetchTemplateInner(values)]}";
        for(templateCount=len ; templateCount < customColumnlength ; templateCount++) {
            if(customValues[templateCount] == null || customValues[templateCount] == "") {
                customValues[templateCount] = " - ";
            }
            this.formatdate(templateCount,customFields, customValues);
            rightTemplateStr += "<tr><td class='leadDetailTD' style='color:#15428B;'>"+customFields[templateCount]+": </td><td class='leadDetailTD'>"+customValues[templateCount]+"</td></tr>";
        }
        rightTemplateStr += "</table></tpl>";
        this.templ = new Wtf.XTemplate(rightTemplateStr,{
            fetchTemplateInner:function(val) { 
                var subowners;
                if(val[0]['subowners'] == " - " || val[0]['subowners'] == "") {
                    subowners = "";
                } else {
                    subowners = ", " + val[0]['subowners'];
                }
                var template = "";
                var rightLen = Math.ceil(fieldsLength/2) + 1;
                if(isEdit){
                    for(var i = rightLen ; i < fieldsLength ; i++) {
                        if(val[0][fieldCols[i]]==null || val[0][fieldCols[i]]==""){
                            val[0][fieldCols[i]]=" - ";
                        }
                        if(fields[i] == "Owner" || fields[i] == "Account Owner") {
                            template += " <tr><td class='leadDetailTD' style='color:#15428B;'>"+fields[i]+": </td><td class='leadDetailTD' style='width:50%'><b>"+val[0]['Lead Owner']+"</b>"+subowners+"</td></tr>";
                        } else {
                            template += "<tr><td class='leadDetailTD' style='color:#15428B;'>"+fields[i]+": </td><td class='leadDetailTD'>"+val[0][fieldCols[i]]+"</td></tr>";
                        }
                    }
                } else {
                    for(var i = rightLen ; i < fieldsLength ; i++) {
                        if(val[0][fields[i]]==null || val[0][fields[i]]==""){
                            val[0][fields[i]]=" - ";
                        }
                        if(fields[i] == "Owner" || fields[i] == "Account Owner") {
                            template += " <tr><td class='leadDetailTD' style='color:#15428B;'>"+fields[i]+": </td><td class='leadDetailTD' style='width:50%'><b>"+val[0][fields[i]]+"</b>"+subowners+"</td></tr>";
                        } else {
                            template += "<tr><td class='leadDetailTD' style='color:#15428B;'>"+fields[i]+": </td><td class='leadDetailTD'>"+val[0][fields[i]]+"</td></tr>";
                        }
                    }

                }
                return template;
            }
        });
        this.view16.tpl = this.templ;
        if(refreshFlag) {
            str.insert(0, rec);
            this.view16.setStore(str);
        }
    }, 

    _getStore: function(u, r, f){
        var recordarr = [];
        for(var i =0;i<f.length;i++){
            recordarr.push(f[i]);
        }
        this.tplRecord = Wtf.data.Record.create(recordarr);

        return new Wtf.data.Store({
            url: u,
            autoLoad: false,
            reader: new Wtf.data.KwlJsonReader({
                root: 'data'
            },this.tplRecord)
        });
    },

    setTagDetails: function(Tagurlpath, Tagurlfields){
        var str = this._getStore(Tagurlpath, 'tags', Tagurlfields);
        str.load();
        this.view18.setStore(str);
        this.UserTagurlpath = Tagurlpath;
        this.UserTagfields = Tagurlfields;
    },
    loadcustomdata : function() {
        this.setAboutDetails("", this.fields,this.values, this.customField, this.customValues, true,this.fieldCols);
    }, 
    addCustomColumn : function(moduleid){
        addCustomColumn(this);
    },
    validateSelection : function(combo,record,index){
        return record.get('hasAccess' );
    },
    
    getLeadDataViewStore : function(rec,updatedon){
    	var store = new Wtf.data.SimpleStore({ 
            fields:[{
                name:'Type'
            },{
                name:'Last Name'
            },{
                name:'First Name'
            },{
                name:'Email'
            },{
                name:'Lead Owner'
            },{
                name:'Company'
            },{
                name:'Created On'
            },{
                name:'Updated On'
            },{
                name:'Product'
            },{
                name:'Expected Revenue('+WtfGlobal.getCurrencySymbol()+')'
            },{
                name:'Price('+WtfGlobal.getCurrencySymbol()+')'
            },{
                name:'Designation'
            },{
                name:'Lead Status'
            },{
                name:'Rating'
            },{
                name:'Lead Source'
            },{
                name:'Industry'
            },{
                name:'Phone'
            },{
                name:'Address'
            },{
                name:'subowners'
            }]
        });
        var temp = [];
        temp.push(rec.type?searchValueField(Wtf.LeadTypeStore,rec.type,'id','name'):rec.type);
        temp.push(rec.lastname);
        temp.push(rec.firstname);
        temp.push(rec.email);
        temp.push(rec.leadowner);
        temp.push(rec.leadownerid);
        temp.push(rec.companyname);
        temp.push(WtfGlobal.onlyDateRenderer(new Date(rec.createdon)));
        temp.push(WtfGlobal.onlyDateRenderer(new Date(updatedon)));
        temp.push(rec.productid?searchValueFieldMultiSelect(Wtf.productStore,rec.productid,'id','name'):rec.productid);
        temp.push(rec.revenue);
        temp.push(rec.price);
        temp.push(rec.title);
        temp.push(rec.leadstatus);
        temp.push(rec.rating);
        temp.push(rec.leadsource);
        temp.push(rec.leadsourceid);
        temp.push(rec.industry);
        temp.push(rec.phone);
        temp.push(rec.addstreet);
        temp.push(this.record.subowners);
        this.customValuesClone = this.customValues;
        this.editcustomclonevalues(rec,temp);
        store.loadData([temp]);
        return store;
    },
    editcustomclonevalues : function(rec,temp){
        for(var i=0;i<this.customField.length;i++) {
            var fieldname = "Custom_"+this.customField[i];
            temp.push(rec[fieldname]);
            if(rec[fieldname]) {
                this.customValues[i]=rec[fieldname];
                var col = this.cm.findColumn(fieldname);
                if(col.xtype=="combo"){
                    this.customValuesClone[i] = searchValueField(GlobalComboStore["cstore"+col.fieldid],this.customValuesClone[i],'id','name')
                }else if(col.xtype=="select"){
                    this.customValuesClone[i] = searchValueFieldMultiSelect(GlobalComboStore["cstore"+col.fieldid],this.customValuesClone[i],'id','name')
                }
            }
        }
    },
    editcustomrecordvalues : function(rec,recordEdit){
        for(var i=0;i<this.customField.length;i++) {
            var fieldname = "Custom_"+this.customField[i];
            if(rec[fieldname]) {
                if(this.cm.findColumn(fieldname).xtype=="select"){
                    recordEdit[fieldname]=this.editForm.getForm().findField(fieldname).getValue();
                } else{
                    recordEdit[fieldname]=rec[fieldname];
                }
            }
        }
    },
    
    getAccountDataViewStore : function(rec,updatedon){
        var store = new Wtf.data.SimpleStore({
            fields:[{
                name:'Account Name'
            },{
                name:'Email'
            },{
                name:'Account Owner'
            },{
                name:'Revenue ('+WtfGlobal.getCurrencySymbol()+')'
            },{
                name:'Product'
            },{
                name:'Price ('+WtfGlobal.getCurrencySymbol()+')'
            },{
                name:'Type'
            },{
                name:'Industry'
            },{
                name:'Phone'
            },{
                name:'Website'
            },{
                name:'Description'
            },{
                name:'Account Creation Date'
            },{
                name:'Account Updated Date'
            },{
                name:'subowners'
            }]
        });
        var temp = [];
        temp.push(rec.accountname);
        temp.push(rec.email);
        temp.push(rec.accountownerid?searchValueField(Wtf.accountOwnerStore,rec.productid,'id','name'):rec.accountowner);
        temp.push(rec.revenue);
        temp.push(rec.productid?searchValueFieldMultiSelect(Wtf.productStore,rec.productid,'id','name'):rec.productid);
        temp.push(rec.price);
        temp.push(rec.accounttypeid?searchValueField(Wtf.accountTypeStore,rec.accounttypeid,'id','name'):rec.accounttype); 
        temp.push(rec.industryid?searchValueField(Wtf.industryStore,rec.industry,'id','name'):rec.industry);
        temp.push(rec.phone);
        temp.push(rec.website);
        temp.push(rec.description);
        temp.push(WtfGlobal.onlyDateRenderer(new Date(rec.createdon)));
        temp.push(WtfGlobal.onlyDateRenderer(new Date(updatedon)));
        temp.push(this.record.subowners);
        this.customValuesClone = this.customValues;
        this.editcustomclonevalues(rec,temp);
        store.loadData([temp]);
        return store;
    },
    
    getCaseDataViewStore : function(rec,updatedon){
        var store = new Wtf.data.SimpleStore({
            fields:[{
                name:'Subject'
            },{
                name:'Description'
            },{
                name:'Case Name'
            },{
                name:'Owner'
            },{
                name:'Status'
            },{
                name:'Assigned To'
            },{
                name:'Priority'
            },{
                name:'Type'
            },{
                name:'Account Name'
            },{
                name:'contacts'
            },{
                name:'Product Name'
            },{
                name:'Case Creation Date'
            },{
                name:'Case Updated Date'
            }]
        });

    var temp = [];
    temp.push(rec.subject);
    temp.push(rec.description);
    temp.push(rec.casename);
    temp.push(rec.caseownerid?searchValueField(Wtf.caseOwnerStore,rec.caseownerid,'id','name'):rec.caseowner);
    temp.push(rec.casestatusid?searchValueField(Wtf.caseStatusStore,rec.casestatusid,'id','name'):rec.casestatus);
    temp.push(rec.caseassignedto?searchValueField(Wtf.caseAssignedUserStore,rec.caseassignedto,'id','name'):rec.caseassignedto);
    temp.push(rec.casepriority?searchValueField(Wtf.cpriorityStore,rec.casepriority,'id','name'):rec.casepriority);
    temp.push(rec.casetype);
    temp.push(rec.accountname);
    temp.push(rec.contactname);
    temp.push(searchValueFieldMultiSelect(Wtf.productStore,rec.productnameid,'id','name'));
    temp.push(WtfGlobal.onlyDateRenderer(new Date(rec.createdon)));
    temp.push(WtfGlobal.onlyDateRenderer(new Date(updatedon)));
    this.customValuesClone = this.customValues;
    this.editcustomclonevalues(rec,temp);
    store.loadData([temp]);
    return store;
 },
 
 getContactDataViewStore : function(rec,updatedon){
     var store = new Wtf.data.SimpleStore({
         fields:[{
             name:'First Name'
         },{
             name:'Last Name'
         },{
             name:'Contact Owner'
         },{
             name:'Account Name'
         },{
             name:'Designation'
         },{
             name:'Lead Source'
         },{
             name:'Industry'
         },{
             name:'Email'
         },{
             name:'Phone'
         },{
             name:'Mobile'
         },{
             name:'Address'
         },{
             name:'Description'
         },{
             name:'Contact Creation Date'
         },{
             name:'Contact Updated Date'
         },{
             name:'subowners'
         }]
     });
     var temp = [];
     temp.push(rec.firstname);
     temp.push(rec.lastname);
     temp.push(rec.contactownerid?searchValueField(Wtf.contactOwnerStore,rec.contactownerid,'id','name'):rec.contactowner);
     temp.push(rec.relatedname);
     temp.push(rec.title);
     temp.push(rec.leadsourceid?searchValueField(Wtf.lsourceStore,rec.leadsourceid,'id','name'):rec.leadsource);
     temp.push(rec.industryid?searchValueField(Wtf.industryStore,rec.industryid,'id','name'):rec.industry);
     temp.push(rec.email);
     temp.push(rec.phoneno);
     temp.push(rec.mobileno);
     temp.push(rec.street);
     temp.push(rec.description);
     temp.push(WtfGlobal.onlyDateRenderer(new Date(rec.createdon)));
     temp.push(WtfGlobal.onlyDateRenderer(new Date(updatedon)));
     temp.push(this.record.subowners);
     this.customValuesClone = this.customValues;
     this.editcustomclonevalues(rec,temp);
     store.loadData([temp]);
	 return store;
 },

editcustomrecordvalues : function(rec,recordEdit){
     for(var i=0;i<this.customField.length;i++) {
         var fieldname = "Custom_"+this.customField[i];
         if(rec[fieldname]) {
             if(this.cm.findColumn(fieldname).xtype=="select"){
                 recordEdit[fieldname]=this.editForm.getForm().findField(fieldname).getValue();
             } else{
                 recordEdit[fieldname]=rec[fieldname];
             }
         }
     }
 },
 
    addActivity : function() {
        
        this.flagStore = new Wtf.data.SimpleStore({
            fields: ['id','name'],
            data : [
            ["Task","Task"],
            ["Event","Event"]
            ]
        });

        this.subject = new Wtf.form.TextField({
            fieldLabel:'Subject *',
            id:'subject'+this.id,
            width:230,
            allowBlank:false,
            msgTarget:'side',
            maxLength:512,
            xtype:'striptextfield'
        })
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
            fieldLabel: "Type * ",
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
            emptyText:WtfGlobal.getLocaleText("crm.goalsettings.combo.emptytxt")//"--Please Select--"
        });

        this.allDayCheckBox= new Wtf.form.Checkbox({
            boxLabel:" ",
            name:'rectype',
            checked:false,
            inputValue:'false',
            width: 50,
            fieldLabel:WtfGlobal.getLocaleText("crm.common.allday")//"All Day "
        })
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
            fieldLabel:WtfGlobal.getLocaleText("crm.calendar.calendar"),//"Calendar",
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
        this.taskCheck=new Wtf.form.FieldSet({
            title: WtfGlobal.getLocaleText("crm.ACTIVITY"),//'Activity',
            autoHeight:true,
            defaultType: 'textfield',
            frame:false,
            labelWidth:90,
            items :[
            this.flag=new Wtf.form.ComboBox({
                fieldLabel:WtfGlobal.getLocaleText("crm.report.activity.taskorevent")+'*',// 'Task/Event * ',
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
                fieldLabel:WtfGlobal.getLocaleText("crm.calendar.eventdetails.starttime")+ ' *',//'Start Time * ',
                format:WtfGlobal.getOnlyDateFormat(),
                offset:Wtf.pref.tzoffset,
                readOnly:true,
                id:'startdate'+this.id,
                allowBlank:false,
                msgTarget:'side',
                value:new Date(),
                width: 230
            }),
            this.starttimeCombo,
            this.endDate = new Wtf.form.DateField({
                fieldLabel:WtfGlobal.getLocaleText("crm.calendar.eventdetails.endtime")+ ' *',//'End Time * ',
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
                fieldLabel:WtfGlobal.getLocaleText("crm.common.endsnever")//"Ends Never "
            }),this.untilDate = new Wtf.form.DateField({
                fieldLabel:WtfGlobal.getLocaleText("crm.common.tilldate"),//'Till Date ',
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

        this.win=new Wtf.Window({
            height:570,
            width:440,
            iconCls: "pwnd favwinIcon",
            title: WtfGlobal.getLocaleText("crm.ACTIVITY"),//"Activity",
            modal:true,
            id:'actquickinsert',
            shadow:true,
            resizable:false,
            buttonAlign:'right',
            buttons: [{ text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                scope:this,
                handler:function(){
                    if(this.form1.getForm().isValid()) {
                        this.saveActivity();
                        
                    } else{
                        ResponseAlert(152);
                    }
                }
            },{
                text:  WtfGlobal.getLocaleText("crm.CLOSEBUTTON"),//'Close',
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
                html: getTopHtml(WtfGlobal.getLocaleText("crm.ACTIVITY"), WtfGlobal.getLocaleText("crm.ACTIVITY")+' '+this.recname,"../../images/activity1.gif")
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

    saveActivity : function() {
        var flag=Wtf.getCmp(this.id+'actflag').getValue();
        var relatedto=this.moduleName;
        this.saveflag=true;
        var relatedname=this.recid;
        var subObj=Wtf.getCmp('subject'+this.id);
        var subject = subObj.getValue();
        if(subject.trim()==""){
            subObj.setValue("");
            subObj.allowBlank=false;
            ResponseAlert(155);
            return;
        }
        var finalStr = createQuickinsertActivityJSON(this,relatedto,relatedname,flag,subject);
        if(this.saveflag && finalStr!=undefined && finalStr!=null){
            this.win.close();
            var paramObj = {};
            Wtf.commonWaitMsgBox("Saving data...");
            paramObj['flag'] = 82;
            paramObj['jsondata'] = finalStr;
            paramObj['type'] = 1;
            Wtf.Ajax.requestEx({
                //                url:Wtf.req.base +'crm.jsp',
                url: Wtf.req.springBase+'Activity/action/saveActivity.do',
                params:paramObj
            },this,
            function(res) {
                var obj = Wtf.getCmp(Wtf.moduleWidget.activity);
                if(obj!=null) {
                    obj.callRequest("","",0);
                    Wtf.refreshUpdatesAll();
                }
                Wtf.updateProgress();
                getDocsAndCommentList(this.selected, this.getKeyID(),this.id, true,this.moduleName,true,"email",undefined,this.contactsPermission);
                ResponseAlert(8);
            },
            function(res){
                WtfComMsgBox(202,1);
            })
        }

    },
    addPhoneCall:function(){

        this.phonecallstartDate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("crm.calendar.eventdetails.starttime")+ ' *',//'Start Time *',
            format:WtfGlobal.getOnlyDateFormat(),
            offset:Wtf.pref.tzoffset,
            readOnly:true,
            id:'startdate'+this.id,
            allowBlank:false,
            msgTarget:'side',
            value:new Date(),
            width: 230
        })
        this.phonecallstarttimeCombo = new Wtf.form.TimeField({
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

        this.phonecallendtimeCombo = new Wtf.form.TimeField({
            fieldLabel:WtfGlobal.getLocaleText("crm.calendar.eventdetails.endtime")+ ' *',//'End Time *',
            id: "endtime"+this.id,
            name: 'endtime1',
            minValue: WtfGlobal.setDefaultMinValueTimefield(),
            maxValue: WtfGlobal.setDefaultMaxValueTimefield(),
            width: 230,
            format:WtfGlobal.getLoginUserTimeFormat(),
            value:WtfGlobal.setDefaultValueEndTimefield()

        })

        this.phonecallphoneno = new Wtf.ux.TextField({
            fieldLabel:  WtfGlobal.getLocaleText("crm.lead.defaultheader.phone"),//'Phone No ',
            name: 'phonecallphoneno',
            width:230,
            //  regex:Wtf.PhoneRegex,
            maxLength:30,
            id:'phonecallphoneno'+this.id
        });
        
        this.phonecalldescription = new Wtf.form.TextArea({
            fieldLabel: WtfGlobal.getLocaleText("crm.ACTIVITY"),//'Notes',
            id:'phonecalldescription'+this.id,
            width:230,
            msgTarget:'side',
            maxLength:512,
            xtype:'striptextfield'
        })


        this.phonecallform1=new Wtf.form.FormPanel({
            border:false,
            items:[

            this.phonecallstartDate,
            this.phonecallstarttimeCombo,
            this.phonecallendtimeCombo,
            this.phonecallphoneno,
            this.phonecalldescription

            ]

        });

        this.phonecallwin=new Wtf.Window({
            height:360,
            width:440,
            iconCls: "pwnd favwinIcon",
            title:WtfGlobal.getLocaleText("crm.ACTIVITY"),//"Activity",
            modal:true,
            id:'phonecallactquickinsert',
            shadow:true,
            resizable:false,
            buttonAlign:'right',
            buttons: [{ text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                scope:this,
                handler:function(){
                    if(this.phonecallform1.getForm().isValid()) {
                        this.savePhoneCallActivity();

                    } else{
                        ResponseAlert(152);
                    }
                }
            },{
                text:WtfGlobal.getLocaleText("crm.CLOSEBUTTON"),// 'Close',
                scope:this,
                handler:function(){
                    this.fireEvent('close');
                    this.phonecallwin.close();
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText("crm.profileview.addphonecall"),WtfGlobal.getLocaleText({key:"crm.profileview.addphcal.tophtml", params:[this.recname]}), "../../images/add-phone-call.jpg")
            },{
                region : 'center',
                border : false,

                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 20px 10px 20px',
                layout : 'fit',
                items :[   this.phonecallform1   ]
            }]

        });
        this.phonecallwin.show();

    },
    savePhoneCallActivity: function(){
        var flag=WtfGlobal.getLocaleText("crm.profileview.phonecall");//"Phone Call";
        this.saveflag=true;
        var subject = WtfGlobal.HTMLStripper(Wtf.getCmp('phonecalldescription'+this.id).getValue());
        var relatedto=this.moduleName;
        var relatedname=this.recid;
        var finalStr = createQuickinsertActivityJSON(this,relatedto,relatedname,flag,subject);
        if(this.saveflag){
            this.phonecallwin.close();
            var paramObj = {};
            Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.savingdata.loadmsg")),//"Saving data...");
            paramObj['flag'] = 82;
            paramObj['jsondata'] = finalStr;
            paramObj['type'] = 1;
            Wtf.Ajax.requestEx({
                //                url:Wtf.req.base +'crm.jsp',
                url: Wtf.req.springBase+'Activity/action/saveActivity.do',
                params:paramObj
            },this,
            function(res) {
                var obj = Wtf.getCmp(Wtf.moduleWidget.activity);
                if(obj!=null) {
                    obj.callRequest("","",0);
                    Wtf.refreshUpdatesAll();
                }
                Wtf.updateProgress();
                getDocsAndCommentList(this.selected, this.getKeyID(),this.id, true,this.moduleName,true,"email",undefined,this.contactsPermission);
                ResponseAlert(8);
            },
            function(res){
                WtfComMsgBox(202,1);
            })
        }
    }
});



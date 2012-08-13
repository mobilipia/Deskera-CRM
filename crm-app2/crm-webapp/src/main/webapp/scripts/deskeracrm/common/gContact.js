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

Wtf.commonAuthenticationWindow =  function(obj,store,moduleName,mapid) {

    if(moduleName=="Lead") {
        this.form1=new Wtf.form.FormPanel({
            border:false,
            items:[{
                fieldLabel:WtfGlobal.getLocaleText("crm.EMAILIDFIELD"),//'Email Id',
                id:'authentication_username'+obj.id,
                width:200,
                allowBlank:false,
                msgTarget:'side',
               // vtype:"email",
               regex:Wtf.ValidateMailPatt,
                xtype:'striptextfield'
            },
            password=new Wtf.form.TextField({
                fieldLabel: WtfGlobal.getLocaleText("crm.PASSWORDFIELD"),//"Password",
                id:'authentication_password'+obj.id,
                allowBlank:false,
                msgTarget:'side',
                inputType:"password",
                width:200
            })]
        });
    }else if(moduleName=="Contact") {

        this.accStore = new Wtf.data.Store({
            url:Wtf.req.springBase+"common/GoogleContacts/getAllAccounts.do",
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, Wtf.ComboReader),
            baseParams:{
                common:'1'
            },
            autoLoad:true
        });

        this.form1=new Wtf.form.FormPanel({
            border:false,
            items:[{
                fieldLabel:WtfGlobal.getLocaleText("crm.EMAILIDFIELD"),//'Email Id',
                id:'authentication_username'+obj.id,
                width:200,
                allowBlank:false,
                msgTarget:'side',
                regex:Wtf.ValidateMailPatt,
                xtype:'striptextfield'
            },
            password=new Wtf.form.TextField({
                fieldLabel: WtfGlobal.getLocaleText("crm.PASSWORDFIELD"),//"Password",
                id:'authentication_password'+obj.id,
                allowBlank:false,
                msgTarget:'side',
                inputType:"password",
                width:200
            }),this.accCombo=new Wtf.form.ComboBox({
                fieldLabel: WtfGlobal.getLocaleText("crm.ACCOUNT"),//"Account",
                id:'authentication_account'+obj.id,
                selectOnFocus:true,
                triggerAction: 'all',
                mode: 'local',
                store: this.accStore,
                displayField: 'name',
                emptyText:WtfGlobal.getLocaleText("crm.goalsettings.combo.emptytxt"),//"-- Please Select --",
                typeAhead: true,
                allowBlank:false,
                valueField:'id',
                msgTarget:'side',
                anchor:'100%',
                width:200

            })]
        });
    } else {
        this.form1=new Wtf.form.FormPanel({
            border:false,
            items:[{
                fieldLabel:WtfGlobal.getLocaleText("crm.EMAILIDFIELD"),//'Email Id',
                id:'authentication_username'+obj.id,
                width:200,
                allowBlank:false,
                msgTarget:'side',
                regex:Wtf.ValidateMailPatt,
                xtype:'striptextfield'
            },
            password=new Wtf.form.TextField({
                fieldLabel: WtfGlobal.getLocaleText("crm.PASSWORDFIELD"),//"Password",
                id:'authentication_password'+obj.id,
                allowBlank:false,
                msgTarget:'side',
                inputType:"password",
                width:200
            })]
        });
    }

    this.selectAllAuthentication= new Wtf.form.Checkbox({
        boxLabel:WtfGlobal.getLocaleText("crm.common.importall"),//"Import All",
        inputType:'radio',
        name:'rectype',
        checked:true,
        inputValue:'false',
        id:"selectAllAuthentication"+obj.id,
        width: 100
    })
    this.selectSomeAuthentication= new Wtf.form.Checkbox({
        boxLabel:WtfGlobal.getLocaleText("crm.common.selectsome"),//"Select Some",
        width: 100,
        inputType:'radio',
        id:"selectSomeAuthentication"+obj.id,
        inputValue:'true',
        name:'rectype'
    })
    this.TypeFormAuthentication=new Wtf.form.FormPanel({
        autoScroll:true,
        border:false,
        labelWidth:100,
        style: "background: #f1f1f1;padding-left: 35px;padding-top: 0px;padding-right: 30px;",
        layout:'column',
        items:[{border:false,columnWidth:.5,items:this.selectAllAuthentication},{border:false,columnWidth:.5,items:this.selectSomeAuthentication}]
    })

    this.authwin=new Wtf.Window({
        height:moduleName=="Target"?260:290,
        width:400,
        id:'authentication_window'+obj.id,
        iconCls: "pwnd favwinIcon",
        title:"Authentication",
        modal:true,
        shadow:true,
        scope:this,
        resizable:false,
        buttonAlign:'right',
        buttons: [{
            text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
            scope:this,
            handler:function() {
                var username=Wtf.getCmp('authentication_username'+obj.id).getValue();
                var password=Wtf.getCmp('authentication_password'+obj.id).getValue();
                var selectAllAuthentication=Wtf.getCmp("selectAllAuthentication"+obj.id).getValue();

                var account="";
                if(username==""){
                        WtfComMsgBox(1054,0);
                        return;
                }if(password==""){
                        WtfComMsgBox(1055,0);
                        return;
                }
                if(moduleName=="Contact"){
                    account=Wtf.getCmp('authentication_account'+obj.id).getValue();
                    if(account==""){
                        WtfComMsgBox(1053,0);
                        return;
                    }
                }

                var jsondata = "";
                Wtf.commonWaitMsgBox("Fetching Google Contacts...");
                jsondata+="{'username':'" + username + "',";
                jsondata+="'password':'"+password+"'},";

                var trmLen = jsondata.length - 1;
                var finalStr = jsondata.substr(0,trmLen);

                Wtf.Ajax.timeout=300000;
                Wtf.Ajax.requestEx({
                    url:Wtf.req.springBase+"common/GoogleContacts/getGoogleContacts.do",
                    params:{
                        jsondata:finalStr,
                        importAll:selectAllAuthentication,
                        account : account,
                        moduleName:moduleName,
                        username:username,
                        flag:800,
                        mapid:mapid
                    }
                },this,
                function(res) {
                    Wtf.updateProgress();
                    if(res.importAll){ // when user select to import all Google contacts
                        this.authwin.close();
                        store.reload();
                        ResponseAlert(353);
                    } else {
                        if(res.data!=undefined) {
                            this.authwin.close();

                            var storeData = res.data;
                            var recContact = Wtf.data.Record.create([
                                      {name: 'firstName', mapping: 0},
                                      {name: 'lastName', mapping: 1},
                                      {name: 'email', mapping: 2},
                                      {name: 'phone', mapping: 3},
                                      {name: 'address', mapping: 4}
                            ]);

                            this.listds = new Wtf.ux.data.JsonPagingStore({
                                lastOptions: {params:{start: 0,limit: 20}},
                                totalRecords : 'records',
                                successProperty: 'success',
                                reader:new Wtf.data.ArrayReader({},recContact),
                                fields: [{
                                    name:"firstName"
                                },{
                                    name:"lastName"
                                },{
                                    name:"email"
                                },{
                                    name:"phone"
                                },{
                                    name:"address"
                                }]
                            });

                            this.listds.loadData(res.data);
                            this.listds.load({params:{start:0,limit:20}});
                            var sm = new Wtf.grid.CheckboxSelectionModel({
                                width:25
                            });

                            this.googleCM = new Wtf.grid.ColumnModel([
                                new Wtf.grid.CheckboxSelectionModel,
                                {
                                    header: WtfGlobal.getLocaleText("crm.lead.defaultheader.fname"),//"First Name",
                                    width:50,
                                    dataIndex: 'firstName'
                                },{
                                    header: WtfGlobal.getLocaleText("crm.contact.defaultheader.lname"),//"Last Name",
                                    width:50,
                                    dataIndex: 'lastName'
                                },{
                                    header: WtfGlobal.getLocaleText("crm.EMAILIDFIELD"),//"Email Id",
                                    dataIndex: 'email',
                                    width:80,
                                    renderer:function(val){
                                        return "<div wtf:qtip=\""+val+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.EMAILIDFIELD")+"'>"+val+"</div>"
                                    }
                                },{
                                    header: WtfGlobal.getLocaleText("crm.lead.defaultheader.phone"),//"Phone",
                                    width:50,
                                    dataIndex: 'phone'
                                },{
                                    header: WtfGlobal.getLocaleText("crm.lead.defaultheader.address"),//"Address",
                                    dataIndex: 'address',
                                    renderer:function(val){
                                        return "<div wtf:qtip=\""+val+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.lead.defaultheader.address")+"'>"+val+"</div>"
                                    }
                                }]);

                            this.contactGrid= new Wtf.grid.GridPanel({
                                id:"google_contact_grid"+obj.id,
                                store:this.listds,
                                scope:this,
                                cm:this.googleCM,
                                sm : sm,
                                border : false,
                                viewConfig: {
                                    forceFit:true
                                },
                                bbar: new Wtf.PagingToolbar({
                                  pageSize: 20,
                                  store: this.listds,
                                  displayInfo: true,
								  emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg")//"No results to display",
                                })

                            });

                            this.gContactWin = new Wtf.Window({
                                resizable: false,
                                scope: this,
                                layout: 'border',
                                modal:true,
                                width: 600,
                                height:500,
                                iconCls: 'pwnd favwinIcon',
                                id: 'import_window_gContacts'+obj.id,
                                title: WtfGlobal.getLocaleText("crm.lead.importGcontact"),//'Google Contacts',
                                items:[{
                                    region : 'north',
                                    height : 70,
                                    border : false,
                                    id:'googleContacts_North_panel'+obj.id,
                                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                                    html : getTopHtml(WtfGlobal.getLocaleText("crm.lead.importGcontact"),WtfGlobal.getLocaleText({key:"crm.lead.Gcontactofuser",params:[username]}),'../../images/leads.gif')
                                },{
                                    region:'center',
                                    layout:'fit',
                                    items:[this.contactGrid]
                                }],
                                buttons: [
                                {
                                    text: WtfGlobal.getLocaleText("crm.dashboard.import"),//"Import",
                                    id: 'importBttn_googleContact'+obj.id,
                                    type: 'submit',
                                    scope: this,
                                    handler: function(){
                                        var s=this.contactGrid.getSelectionModel().getSelections();
                                        if(s.length>0){
                                            Wtf.saveGoogleContact(s,store,this.gContactWin,moduleName,account,username,this.listds,mapid);
                                        } else {
                                            WtfComMsgBox(1051,0);
                                            return;
                                        }

                                    }
                                },{
                                    text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                                    id:'canbttn_googleContact'+obj.id,
                                    scope:this,
                                    handler:function() {
                                        this.gContactWin.close();
                                    }
                                }]
                            });

                            this.gContactWin.show();
                        } else {
                            WtfComMsgBox(1050,0);
                            Wtf.getCmp('authentication_username'+obj.id).setValue("");
                            Wtf.getCmp('authentication_password'+obj.id).setValue("");
                        }
                    }
                    Wtf.Ajax.timeout=30000;
                },
                function(res){
                    Wtf.updateProgress();
                    ResponseAlert(352);
                    Wtf.Ajax.timeout=30000;
                })

            }
        },{
            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
            scope:this,
            handler:function(){
                this.authwin.close()
            }
        }],
        layout : 'border',
        items :[{
            region : 'north',
            height : 75,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml("Authentication", "Please provide your Google Account details. ","../../images/import.png")
        },{
            region : 'center',
            border : false,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
            layout : 'fit',
            items :[this.form1]
        },{
            region : 'south',
            height : 40,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            layout : 'fit',
            items :this.TypeFormAuthentication
        }]
    });

    this.authwin.show();

    this.selectSomeAuthentication.on("check",function(){
        this.selectAllAuthentication=false;
    },this)

    this.selectAllAuthentication.on("check",function(){
        this.selectSomeAuthentication=false;
    },this)
}

Wtf.saveGoogleContact =  function(s,store,gcontactWin,moduleName,account,username,gcontactStore,mapid){
    var googleContactJson=""
    for(var i = 0 ; i < s.length ; i++){
        googleContactJson+="{'firstName':'" + s[i].data.firstName + "',";
        googleContactJson+="'lastName':'" + s[i].data.lastName + "',";
        googleContactJson+="'address':'" + s[i].data.address + "',";
        googleContactJson+="'phone':'" + s[i].data.phone.trim() + "',";
        googleContactJson+="'email':'" + s[i].data.email + "'},";
    }
    for(var j=0 ;j < s.length ;j++){
        gcontactStore.remove(s[j]);
    }

    gcontactStore.load({params:{start:0,limit:20}});
    var finalJson = googleContactJson.substring(0, (googleContactJson.length -1))
    Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.lead.importGcontact.waitmsg"));//"Importing Google Contacts...");
    Wtf.Ajax.requestEx({
        url:Wtf.req.springBase+"common/GoogleContacts/saveGoogleContacts.do",
        params:{
            jsondata:finalJson,
            flag:801,
            moduleName:moduleName,
            account:account,
            username:username,
            mapid:mapid
        }
    },
    this,
    function(res)
        {
            Wtf.updateProgress();
            if(res.success){
                ResponseAlert(351);
            } else {
               ResponseAlert(352);
            }
            Wtf.MessageBox.show({
                title: "Google Contacts",
                msg: s.length+" record(s) imported successfully. Do you want to continue importing more records?",
                buttons: Wtf.MessageBox.YESNO,
                animEl: 'mb9',
                scope:this,
                icon: Wtf.MessageBox.INFO,
                fn:function(btn,text){
                    if(btn=="yes"){
                        store.reload();
                    } else {
                        gcontactWin.close();
                        store.reload();
                    }
                    // StoreManager (Global Store) reload
                    Wtf.ModuleGlobalStoreReload(moduleName)
                }
            });
        },
    function()
    {
        Wtf.updateProgress();
        ResponseAlert(352);

    });

}

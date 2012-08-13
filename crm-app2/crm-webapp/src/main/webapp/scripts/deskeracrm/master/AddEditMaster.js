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
Wtf.AddEditMaster = function (config){
    Wtf.apply(this,config);
    Wtf.AddEditMaster.superclass.constructor.call(this,{
        buttons:[
                {
                    text:WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//"Save",
                    handler:function (){
                        this.saveProjectDetail();
                    },
                    scope:this
                },
                {
                    text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//"Cancel",
                    handler:function (){
                        this.close();
                    },
                    scope:this
                }
            ]
    });
}

Wtf.extend(Wtf.AddEditMaster,Wtf.Window,{
    initComponent:function (){
        Wtf.AddEditMaster.superclass.initComponent.call(this);
        this.GetNorthPanel();
        this.GetAddEditForm();

        this.mainPanel = new Wtf.Panel({
            layout:"border",
            items:[
                this.northPanel,
                this.AddEditForm
            ]
        });

        this.add(this.mainPanel);
    },
    GetNorthPanel:function (){
        var wintitle=this.action+' Master Field';
        var windetail='';
        var image='';
        if(this.action=="Edit"){
            windetail=WtfGlobal.getLocaleText("crm.masterconfig.AddEditWintitle.EditMasterData.detail");//'Edit the master field information';
            image='../../images/project.gif';
        } else {
            windetail=WtfGlobal.getLocaleText("crm.masterconfig.AddEditWintitle.AddMasterData.detail");//'Fill up the information to add master field';
            image='../../images/project.gif';
        }
        this.northPanel = new Wtf.Panel({
            region:"north",
            height:75,
            border:false,
            bodyStyle:"background-color:white;padding:8px;border-bottom:1px solid #bfbfbf;",
            html:getTopHtml(wintitle,windetail,image)
        });
    },
    GetAddEditForm:function (){
        this.parentRec = new Wtf.data.Record.create([
            {name:"id"},
            {name:"name"}
        ]);

        this.parentReader = new Wtf.data.KwlJsonReader({
            root:"data",
            totalProperty:"count"
        },this.parentRec);

        this.parentStore = new Wtf.data.Store({
            url: "Common/CRMManager/getMasterComboData.do",
            reader:this.parentReader
        });

        this.parentStore.load({params:{common:'1'}});

        this.parentCombo1 = new Wtf.form.ComboBox({
            triggerAction:"all",
            mode:"local",
            typeAhead:true,
            store:this.parentStore,
            displayField:"name",
            width:200,
            valueField:"id",
            fieldLabel:"Parent",
            hiddenName:"parentid"
        });
        this.parentStore.on("load",function (){
            if(this.action == "Edit"){
                if(this.rec.get("parentid") != 0){
                    this.parentCombo1.setValue(this.rec.get("parentid"));
                }
            }
        },this);

        this.AddEditForm = new Wtf.form.FormPanel({
            region:"center",
            border:false,
            bodyStyle:"background-color:#f1f1f1;padding:15px",
            url: "Common/CRMManager/addEditMasterData.do",
            items:[
                {
                    xtype:"textfield",
                    fieldLabel:WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),//"Name",
                    width:200,
                    name:"name",
                    value:(this.action == "Edit")?this.rec.get("name"):""
                },
                (this.parentid != 0)?this.parentCombo1:""
            ]
        });
    }, 
    saveProjectDetail:function (){
        if(this.AddEditForm.form.isValid()){
            this.AddEditForm.form.submit({
                params:{
                    flag:26,
                    action:this.action,
                    id:(this.action == "Edit")?this.rec.get("id"):""
                },
                success:function(){
                    Wtf.MessageBox.show({
                        title:"Status",
                        msg:(this.action == "Edit")?"Master field has been edited successfully":"Master field has been added successfully",
                        icon:Wtf.MessageBox.INFO,
                        buttons:Wtf.MessageBox.OK
                    });
                    this.close();
                    this.store.load({
                        params:{
                            start:0,
                            limit:25
                        }
                    })
                },
                failure:function (){
                    Wtf.MessageBox.show({
                        title:"Status",
                        msg:(this.action == "Edit")?"Error while editing master field":"Error while adding master field",
                        icon:Wtf.MessageBox.ERROR,
                        buttons:Wtf.MessageBox.OK
                    });
                },
                scope:this
            })
        }
    }
});

//---------------------------------------------------Add Master Data------------------------------------------------

Wtf.AddEditMasterData = function (config){
    Wtf.apply(this,config);
    Wtf.AddEditMasterData.superclass.constructor.call(this,{
        buttons:[
                {
                    text:WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//"Save",
                    id:"add_master_field_save",
                    handler:function (){
                        Wtf.getCmp("add_master_field_save").disable();
                        this.saveProjectDetail();
                    },
                    scope:this
                },
                {
                    text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//"Cancel",
                    handler:function (){
                        this.close();
                    },
                    scope:this
                }
            ]
    });
}

Wtf.extend(Wtf.AddEditMasterData,Wtf.Window,{
    initComponent:function (){
        Wtf.AddEditMasterData.superclass.initComponent.call(this);
        this.GetNorthPanel();
        this.GetAddEditForm();

        this.mainPanel = new Wtf.Panel({
            layout:"border",
            items:[
                this.northPanel,
                this.AddEditForm
            ]
        });

        this.add(this.mainPanel);
    },
    GetNorthPanel:function (){
        var wintitle=this.action=="Add"? WtfGlobal.getLocaleText("crm.masterconfig.AddEditWintitle.AddMasterData"):WtfGlobal.getLocaleText("crm.masterconfig.AddEditWintitle.EditMasterData");
        var windetail='';
        if(this.action=="Edit"){
            windetail=WtfGlobal.getLocaleText("crm.masterconfig.AddEditWintitle.EditMasterData.detail");//'Edit the master field information';
        } else {
            windetail=WtfGlobal.getLocaleText("crm.masterconfig.AddEditWintitle.AddMasterData.detail");//'Fill up the information to add master field';
        }
        
        this.northPanel = new Wtf.Panel({
            region:"north",
            height:75,
            border:false,
            bodyStyle:"background-color:white;padding:8px;border-bottom:1px solid #bfbfbf;",
            html:getTopHtml(wintitle,windetail)
        });
    },
    GetAddEditForm:function (){
        this.parentRec = new Wtf.data.Record.create([
            {name:"id"},
            {name:"name"},
            {name:"configid"},
            {name:"parentid"},
            {name:"isEdit"},
            {name:"percentStage"},
            {name:"itemsequence"}
        ]);

        this.parentReader = new Wtf.data.KwlJsonReader({
            root:"data",
            totalProperty:"count"
        },this.parentRec);

        this.parentStore = new Wtf.data.Store({
            url:"Common/CRMManager/getComboData.do",
            reader:this.parentReader
        });

        this.parentStore.load({
            params:{
                configid:this.parentid,
                common:'1'
            }
        });

        if(this.parentid == 0){
            this.parentCombo = {
                xtype:"hidden",
                fieldLabel:"Parent",
                name:"parentid",
                value:0
            };
        } else {
            this.parentCombo = new Wtf.form.ComboBox({
                triggerAction:"all",
                mode:"local",
                typeAhead:true,
                store:this.parentStore,
                width:200,
                displayField:"name",
                valueField:"id",
                fieldLabel:"Parent",
                hiddenName:"parentid"
            });
        }
        this.parentStore.on("load",function (){
            if(this.action == "Edit"){
                if(this.rec.get("parentid") != 0){
                    this.parentCombo.setValue(this.rec.get("parentid"));
                }
            }
        },this);
        var percentStage = undefined;
        if(this.rec != undefined && this.rec.get("percentStage") != "") {
            percentStage = this.rec.get("percentStage");
        }
        var disableField= false;
        if(this.rec != undefined && (((this.rec.json.mainid=="443dd38f-1c39-43c3-8f41-6d490dcf8302" || this.rec.json.mainid=="98e4ed03-259b-4d62-8c06-e0fb630170f8" || this.rec.json.mainid== "00962c0b-42c3-4640-b3c7-8be8ea922ed3") && this.masterRec.get("name")=="Case Status") || (this.rec.json.mainid=="f01e5a6f-7011-4e2d-b93e-58b5c6270239" && (this.masterRec.get("name")=="Lead Status")))){
            disableField= true;            
        }
        var comboname = this.masterRec.get("name");
        var numFieldLabel=""
        var hide = true;
        if(comboname=="Opportunity Stage"){
            numFieldLabel = "Weightage in Pipeline (%) "+WtfGlobal.addLabelHelp("It is the weightage assigned to Opportunity Stage which calculates pipeline value/ amount in Opportunity Pipeline Report.");
            hide = false;
        } else if(comboname=="Lead Status"){
            numFieldLabel = "Weightage in Pipeline (%) "+WtfGlobal.addLabelHelp("It is the weightage assigned to Lead Status which calculates pipeline value/ amount in Lead Pipeline Report.");
            hide = false;
        } else if (comboname=="Case SLA"){
            numFieldLabel = "Hours "+WtfGlobal.addLabelHelp("It defines the SLA in hours. E.g. - 1 Hour = 1, 1 Day = 24, 1 Week = 168 etc.");
            hide = false;
        }
        this.AddEditForm = new Wtf.form.FormPanel({
            region:"center",
            border:false,
            bodyStyle:"background-color:#f1f1f1;padding:15px",
            url: "Common/CRMManager/addEditMasterData.do",
            items:[
                 this.name= new Wtf.ux.TextField({
                    fieldLabel:WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),//"Name",
                    width:200,
                    maxLength:this.maxlength,
                    name:"name",
                    allowBlank:false,
                    value:(this.action == "Edit")?this.rec.get("name"):"",
                    disabled:this.action == "Edit"?disableField:false
                }),
                    
                new Wtf.form.NumberField({
                    fieldLabel:numFieldLabel,
                    width:200,
                    maxLength:3,
                    maxValue:100, 
                    id:'stage_percent',
                    name:"percentStage",
                    hideLabel:hide,
                    hidden:hide,
                    value:(this.action == "Edit")?percentStage:undefined
                }),  

                this.parentCombo
            ]
        });
    },    
    saveProjectDetail:function (){
        if(this.AddEditForm.form.isValid() && this.name.getValue().trim()!=""){
            this.AddEditForm.form.submit({
                params:{
                    configid:this.configid,
                    customflag:this.customflag,
                    action:this.action,
                    percentStage:Wtf.getCmp('stage_percent').getValue(),
                    sequence:(this.action == "Edit")?this.rec.get("itemsequence"):this.store.getCount()+1,
                    id:(this.action == "Edit")?this.rec.get("id"):""
                },
                success:function(a,res){
                    var message = eval('('+res.response.responseText+')').data.data.msg;
                    if(message!=undefined){
                        Wtf.MessageBox.show({
                            title:"Status",
                            msg:message,
                            icon:Wtf.MessageBox.INFO,
                            buttons:Wtf.MessageBox.OK
                        });
                        this.close();
                    }else {
                        var msg = (this.action == "Edit")?WtfGlobal.getLocaleText("crm.masterconfig.Edit.success.msg")/*"Master field has been edited successfully."*/:WtfGlobal.getLocaleText("crm.masterconfig.Add.success.msg")/*"Master field has been added successfully."*/;
                        if(this.customflag == 1) {
                            msg += WtfGlobal.getLocaleText("crm.masterconfig.addedit.customcolumn.msg1")+' '/*"<BR /> Please close the "*/+this.masterRec.get("modulename")+' '+WtfGlobal.getLocaleText("crm.masterconfig.addedit.customcolumn.msg2")/*"'s tab and reopen it to reflect the changes."*/;
                        }
                        Wtf.MessageBox.show({
                            title:WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),//"Status",
                            msg:msg,
                            icon:Wtf.MessageBox.INFO,
                            buttons:Wtf.MessageBox.OK
                        });
                        this.close();
                        Wtf.loadMasterStore(this.configid);
                        this.store.load({
                            params:{
                                configid:this.configid,
                                comboname:this.masterRec.get('name'),
                                customflag:this.masterRec.get('customflag')
                            }
                        });
                    }
                    
                },
                failure:function (){
                    Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),//"Status",
                        msg:(this.action == "Edit")?WtfGlobal.getLocaleText("crm.masterconfig.Edit.error.msg")/*"Error while editing master field"*/:WtfGlobal.getLocaleText("crm.masterconfig.Add.error.msg")/*"Error while adding master field"*/,
                        icon:Wtf.MessageBox.ERROR,
                        buttons:Wtf.MessageBox.OK
                    });
                },
                scope:this
            })
        }else{
            this.name.setValue("");
            this.name.allowBlank=false;
            WtfComMsgBox(61,0);
        }
    }
}); 


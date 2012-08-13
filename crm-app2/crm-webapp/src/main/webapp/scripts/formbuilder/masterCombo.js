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
Wtf.configMasterGrid = function(config){
    Wtf.apply(this, config);
    this.typeStore = new Wtf.data.SimpleStore({
        fields :['id', 'name'],
        data:[/*[0,'Text Field'],[1,'Number Field'],[2,'Checkbox'],[3,'Date Field'],*/[4,'Dropdown'],/*[5,'Rich TextBox'],[6,'Text Area'],[7,'MultiSelect Combobox'],[8,'File Upload']*/]
    });

    this.reader = new Wtf.data.KwlJsonReader({
        root: 'data'},
         [{
            name: 'configid'
        },{
            name: 'configtype'
        },{
            name: 'fieldname'
        }]
    );

//    this.ds = new Wtf.data.GroupingStore({
    this.ds = new Wtf.data.Store({
        url: Wtf.req.mcombo+"getConfig.do",
        reader: this.reader
//        sortInfo: {
//            field: 'formtype',
//            direction: "ASC"
//        },
//        groupField:'formtype'
    });

    this.ds.baseParams = {
        flag: 2,
        type:''
    };

    this.ds.on("load",function(store,rec,opt){
        //this.quickPanelSearchTFQ.StorageChanged(store);
    },this);

    this.sm = new Wtf.grid.CheckboxSelectionModel();

    this.cm = new Wtf.grid.ColumnModel([new Wtf.grid.RowNumberer({}),this.sm, {
        header: "Config Type",
        dataIndex: 'configtype',
        width: 150,
        renderer: function(val) {
            if(val==4){
                return "Drop Down";
            }
//            else if(val==7){
//                return "Multi Select Combo-Box";
//            }
        }
    }, {
        header: 'Fieldname',
        dataIndex: 'fieldname',
        width: 150
    }
    ]);
    this.cm.defaultSortable = true;

this.cloneRecord = Wtf.data.Record.create([{
                name: 'name',
                type: 'string'
            }, {
                name: 'configid',
                type: 'string'
            }
]);

this.cloneReader = new Wtf.data.KwlJsonReader({
    root: "data"
}, this.cloneRecord);

this.cloneStore = new Wtf.data.Store({
    proxy: new Wtf.data.HttpProxy({
        url: Wtf.req.mcombo+"getMasterAttributes.do"
    }),
    reader: this.cloneReader
});
this.cloneStore.load({
    params:{
        flag:7,
        mode:'clone'
    }
});

this.addC = new Wtf.Toolbar.Button({
                    text: "Add Config",
                    scope: this,
                    handler:function(){this.addConfig(true);}
});

this.editC = new Wtf.Toolbar.Button({
                    text: "Edit Config",
                    scope: this,
                    disabled:true,
                    handler:function(){this.addConfig(false);}
});

this.delC = new Wtf.Toolbar.Button({
                    text: "Delete Config",
                    scope: this,
                    disabled:true,
                    handler:function(){this.delConfig(true);}
});

this.setM = new Wtf.Toolbar.Button({
                    text:'Set Master',
                    scope:this,
                    handler:this.masterwin
});
this.cloneBttn=new Wtf.Toolbar.Button({
                    text:'Clone Master',
                    scope:this,
                    handler:function(){
                        this.addconfig1();
                    }
                })

    Wtf.configMasterGrid.superclass.constructor.call(this, {
        layout: 'fit',
        items: [{
            layout: 'fit',
            border: false,
            //autoWidth: true,
            items: [this.grid = new Wtf.grid.GridPanel({
                border: false,
                region: 'center',
                store: this.ds,
//                view: this.groupingView,
                //enableColumnHide: false,
                sm: this.sm,
                cm: this.cm,
                viewConfig: {
                    autoFill: true,
                    forceFit:true
                },
                loadMask: {
                    msg: 'Loading...'
                },
                bbar: ["-",this.addC,this.delC,this.editC,this.setM,"-",this.cloneBttn]
            })]
        }]
    });
    this.grid.on("sortchange", function(b, bd){
             //this.grid.getStore().groupBy(bd.field);
    }, this);
    this.sm.on("selectionchange",this.disableBttns,this);
    this.ds.load();
};

Wtf.extend(Wtf.configMasterGrid, Wtf.Panel, {
    addconfig1:function(){
    this.win1 = new Wtf.Window({
              title:'Clone',
              layout:'fit',
              iconCls: 'winicon',
              modal: true,
              height:220,
              width:350,
              scope: this,
              buttons: [{
                  text: "Add",
                  handler: function() {
                      Wtf.Ajax.requestEx({
                            method: 'POST',
                            url: Wtf.req.mcombo+'insertConfig.do',
                            params: {
                                flag:3,
                                configid:'clone',
                                configtype:this.qType.getValue(),
                                fieldname: this.quesField.getValue()
                            }},this,
                             function(request) {
                                msgBoxShow(['Success', 'Config option added successfully.'], Wtf.MessageBox.INFO);
                                this.win1.close();
                                this.ds.load();
                                this.cloneStore.load({
                                    params:{
                                        flag:7,
                                        mode:'clone'
                                    }
                                });
                           },function(){
                                msgBoxShow(['Error', 'Error connecting to server.'], Wtf.MessageBox.ERROR);
                           }
                    )
                  },
                  scope: this
              },{
                text:"Cancel",
                scope:this,
                handler:function(){
                   this.win1.close();
                }
               }],
              items:[
                  this.pPanel = new Wtf.Panel({
                      layout: 'fit',
                      border: false,
                      items: this.inP = new Wtf.Panel({
                          layout: 'border',
                          border: false,
                          items: [{
                                  region: 'north',
                                  border: false,
                                  height: 70,
                                  cls:'windowHeader',
                                  html:getHeader('images/createuser.gif',' Config','Select a config type.')
                              },{
                                  region: 'center',
                                  layout: 'fit',
                                  bodyStyle:"background:#f1f1f1;",
                                  border: false,
                                  items: [
                                      this.addForm = new Wtf.form.FormPanel({
                                          url: "jspfiles/admin/feedback.jsp",
                                          region: "center",
                                          bodyStyle: "padding: 10px;",
                                          border: false,
                                          labelWidth: 120,
                                          height: 60,
                                          buttonAlign: 'right',
                                          items: [
                                             this.qType = new Wtf.form.ComboBox({
                                              valueField: 'name',
                                              displayField: 'name',
                                              store: this.cloneStore,
                                              fieldLabel:'Clone',
                                              editable: false,
                                              //name: 'type',
                                              allowBlank: false,
                                              anchor: '95%',
                                              mode: 'local',
                                              triggerAction: 'all',
                                              selectOnFocus: true,
                                              emptyText: 'Select clone type'
                                          }), this.quesField = new Wtf.form.TextField({
                                              fieldLabel: 'Fieldname',
                                              scope: this,
                                              anchor: '95%',
                                              allowBlank: false,
                                              name: 'question',
                                              maxLength: 256
                                          })
                                        ]
                                      })
                                  ]
                              }

                          ]
                      })
                  })
              ]
          })
          this.win1.show();
    },

    masterwin:function(){
        this.attributeRecord = Wtf.data.Record.create([{
                name: 'name',
                type: 'string'
            }, {
                name: 'configid',
                type: 'string'
            }
        ]);

        this.attributeReader = new Wtf.data.KwlJsonReader({
            root: "data"
        }, this.attributeRecord);

        this.attributeStore = new Wtf.data.Store({
            proxy: new Wtf.data.HttpProxy({
                url:  Wtf.req.mcombo+"getMasterAttributes.do"
            }),
            reader: this.attributeReader
        });
        this.attributeStore.load({
            params:{
                flag:7,
                 mode:'master'
            }
        });


        this.attributeCombo= new Wtf.form.ComboBox({
            triggerAction: 'all',
            store:this.attributeStore,
            mode:'local',
            width: 240,
            listWidth:'240',
            readOnly : true,
            displayField:'name',
            fieldLabel : 'Attribute',
            hiddenName  : 'configid',
            allowBlank:false,
            valueField:'configid',
            emptyText:'Select an Attribute'
        });
        this.mastersm = new Wtf.grid.CheckboxSelectionModel();
        this.mastercm = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),this.sm,
            {
                header: "Master Record",
                dataIndex: 'masterdata',
                editor: new Wtf.form.TextField({allowBlank: false,
                            maxLength: 100,
                            items:[{ text: 'Delete',
                            tooltip: {
                            title: 'Delete',
                            text: 'Click to delete record'}}]})
        }]);

        this.masterReader = new Wtf.data.Record.create([
            {name: 'masterid'},
            {name: 'masterdata'}
        ]);

        this.masterds = new Wtf.data.Store({
            url: Wtf.req.mcombo+"getMaster.do",
            reader: new Wtf.data.KwlJsonReader({
                        root:"data"}, this.masterReader)
        });
        this.masterWin = new Wtf.Window({
                id: 'master' + this.id,
                title:'Master Record',
                layout:'fit',
                iconCls: 'winicon',
                modal: true,
                height:400,
                width:600,
                scope: this,
                items:[this.poppanel = new Wtf.Panel({
                    id: 'masterpanel' + this.id,
                    layout: 'fit',
                    cls: 'backcolor',
                    border: false,
                    tbar: [this.attributeCombo,'-','New Record: ', this.masterText = new Wtf.form.TextField({
                            fieldLabel: 'New Master Record',
                            anchor: '95%',
                            maxLength: 100,
                            allowBlank: false,
                            id: this.id + 'masterText'
                    }),'-',{
                        text: 'Add',
                        tooltip: {
                            title: 'Add',
                            text: 'Click to add new record'
                        },
                        handler: function() {
                            if(this.attributeCombo.getValue().length == 0){
                                return;
                            }
                            if(this.masterText.getValue().length == 0 || !this.masterText.validate()){
                                return;
                            }
                            Wtf.Ajax.requestEx({
                            url: Wtf.req.mcombo+'insertMaster.do',
                            method: 'POST',
                            params: {
                                        flag: 5,
                                        masterid:"",
                                        configid:this.attributeCombo.getValue(),
                                        masterdata:this.masterText.getValue()
                                    }
                            },this,
                            function(response, e){
                                this.masterText.setValue("");
                                this.masterText.focus();
                                this.masterds.reload();
                            })
                          },
                        scope: this
                        },'-',
                        this.delmaster = new Wtf.Toolbar.Button({
                            text: 'Delete',
                            disabled:true,
                            tooltip: {
                            title: 'Delete',
                            text: 'Click to delete record'
                            },
                             handler: function() {
                            this.delConfig(false);
                        },
                            scope:this
                        })],
                    items: [this.addmaster = new Wtf.Panel({
                        id: 'addmaster' + this.id,
                        layout: 'fit',
                        border: false,
                        items: [this.masterGrid = new Wtf.grid.EditorGridPanel({
                            id: 'mastergrid' + this.id,
                            store: this.masterds,
                            sm:this.mastersm,
                            cm: this.mastercm,
                            border: false,
                            clicksToEdit: 1,
                            viewConfig: {
                                forceFit: true
                            }
                    })]
                })]
                })]
            })
            this.mastersm.on("selectionchange",this.handleBttns,this);
            this.masterWin.show();
            this.attributeCombo.on('select',this.masterload,this);
            this.masterGrid.on('afteredit',this.roleAfterEdit,this)
            this.masterds.on('loadException',this.masteronload,this);
    },
    handleBttns: function(obj){
            if(obj.getCount() > 0){
                this.delmaster.enable();
            }else
                this.delmaster.disable();
    },

    addConfig: function(flag1){
        //alert(this.sm.getSelected().get("configtype"))
                 this.win1 = new Wtf.Window({
                              title:'Config',
                              layout:'fit',
                              iconCls: 'winicon',
                              modal: true,
                              height:220,
                              width:350,
                              scope: this,
                              buttons: [{
                                  text: flag1 ? "Add": "Update",
                                  handler: function() {
                                    if (this.addForm.form.isValid()){
                                          Wtf.Ajax.requestEx({
                                                method: 'POST',
                                                url: Wtf.req.mcombo+'insertConfig.do',
                                                params: {
                                                    flag:3,
                                                    configid: flag1 ?  "config":this.sm.getSelected().get("configid"),
                                                    configtype: this.qType.getValue(),
                                                    fieldname: this.quesField.getValue()
                                                }},this,
                                                 function(response,option) {
                                                    var responseObj = response;
                                                    if(responseObj.success=='msg'){
                                                        var title=responseObj.title;
                                                        var msg =responseObj.msg;
                                                        msgBoxShow([title, msg], Wtf.MessageBox.INFO);
                                                    }
                                                    else{
                                                        msgBoxShow(['Success', 'Config option '+(flag1? 'added':'edited')+' successfully.'], Wtf.MessageBox.INFO);
                                                    }
                                                    this.win1.close();
                                                    this.ds.load();
                                               },function(){
                                                    msgBoxShow(['Error', 'Error connecting to server.'], Wtf.MessageBox.ERROR);
                                               }
                                            )
                                    }
                                  },
                                  scope: this
                              },{
                                text:"Cancel",
                                scope:this,
                                handler:function(){
                                   this.win1.close();
                                }
                               }],
                              items:[
                                  this.pPanel = new Wtf.Panel({
                                      layout: 'fit',
                                      border: false,
                                      items: this.inP = new Wtf.Panel({
                                          layout: 'border',
                                          border: false,
                                          items: [{
                                                  region: 'north',
                                                  border: false,
                                                  height: 70,
                                                  cls:'windowHeader',
                                                  html:getHeader('images/createuser.gif',' Config','Select a config type.')
                                              },{
                                                  region: 'center',
                                                  
                                                  bodyStyle:"background:#f1f1f1;",
                                                  border: false,
                                                  items: [
                                                      this.addForm = new Wtf.form.FormPanel({
                                                          url: "jspfiles/admin/feedback.jsp",
                                                          
                                                          bodyStyle: "padding: 10px;",
                                                          border: false,
                                                          labelWidth: 120,
                                                          height: 60,
                                                          
                                                          items: [this.qType = new Wtf.form.ComboBox({
                                                              valueField: 'id',
                                                              displayField: 'name',
                                                              store: this.typeStore,
                                                              fieldLabel: 'Config Type',
                                                              editable: false,
                                                              value:4,
                                                              //name: 'type',
                                                              allowBlank: false,
                                                              anchor: '95%',
                                                              mode: 'local',
                                                              triggerAction: 'all',
                                                              selectOnFocus: true,
                                                              emptyText: 'Select config type...'
                                                          }),
                                                          this.quesField = new Wtf.form.TextField({
                                                              fieldLabel: 'Fieldname',
                                                              scope: this,
                                                              allowBlank: false,
                                                              anchor: '95%',
                                                              name: 'question',
                                                              value:flag1 ? null:(this.sm.getSelected().get("fieldname")),
                                                              maxLength: 256,
                                                              regex: nameRegex,
                                                              regexText : regexText
                                                          })
                                                         ]
                                                      })
                                                  ]
                                              }

                                          ]
                                      })
                                  })
                              ]
                          })
                          this.win1.show();
                    },

    delConfig: function(p){
        Wtf.Msg.show({
            title:(p==true)?'Delete Config?':'Delete Master?',
            msg: 'Selected data will be deleted! Do you want to continue?',
            buttons: Wtf.Msg.YESNO,
            fn:(p==true)?this.confirmDelete:this.deletemaster,
            scope:this,
            //animEl: 'elId',
            icon: Wtf.MessageBox.QUESTION
        });
    },
      deletemaster: function(obj){
         if(obj == 'yes'){
            var delid = [];
                for(var i = 0;i < this.mastersm.getSelections().length;i++){
                    delid.push(this.mastersm.getSelections()[i].get("masterid"));
                }
            var delidArr = Wtf.encode(delid);
            Wtf.Ajax.requestEx({
                url:Wtf.req.mcombo+'deleteConfig.do',
                params:{
                    delid:delidArr,
                    mode:'master',
                    flag:4
                },
            method:'POST'},
            this,
            function(responseObj,options){
//                var responseObj = eval('('+response+')');
                  if(responseObj.success=='msg'){
                     var title=responseObj.title;
                     var msg =responseObj.msg;
                     msgBoxShow([title, msg], Wtf.MessageBox.INFO);
                  }
                else{
                    msgBoxShow(['Success', 'Master data deleted successfully.'], Wtf.MessageBox.INFO);
                }
                    this.masterds.reload();
                },function(){
                msgBoxShow(['Error', 'Error connecting to server.'], Wtf.MessageBox.ERROR);
            })
       }

    },
    confirmDelete:function(obj){
        if(obj == 'yes'){
            var delid = [];
                for(var i = 0;i < this.sm.getSelections().length;i++){
                    delid.push(this.grid.getSelections()[i].data['configid']);
                }
            var delidArr = Wtf.encode(delid);
            Wtf.Ajax.requestEx({
                url:Wtf.req.mcombo+'deleteConfig.do',
                params:{
                    delid:delidArr,
                    mode:'config',
                    flag:4
                },
            method:'POST'},
            this,
            function(respobj,options){
//                 var respobj = eval('('+response+')');
                    if(respobj.success!=null){
                        if(respobj.success=='msg'){
                            var title=respobj.title;
                            var msg=respobj.msg;
                        msgBoxShow([title,msg],Wtf.MessageBox.INFO);
                    }
                }
                else{
                    msgBoxShow(['Success', 'Config option deleted successfully.'], Wtf.MessageBox.INFO);
                }
                this.ds.reload();
            },function(){
                msgBoxShow(['Error', 'Error connecting to server.'], Wtf.MessageBox.ERROR);
            })
       }
    },

    disableBttns: function(obj){
        if(obj.getCount() > 1){
            this.delC.enable();
            this.editC.disable();
        }
        else if(obj.getCount() == 1){
            this.editC.enable();
            this.delC.enable();
        }

        else{
            this.delC.disable();
            this.editC.disable();
        }
    },

    roleAfterEdit: function(e) {
        Wtf.Ajax.requestEx({
            url: Wtf.req.mcombo+'insertMaster.do',
            method: 'POST',
            params: {
                flag: 5,
                masterid:e.record.data.masterid,
                configid:this.attributeCombo.getValue(),
                masterdata:e.value
            }},
            this,
            function(respobj, e){
//                var respobj = eval('('+response+')');
                    if(respobj.success!=null){
                        if(respobj.success=='msg'){
                            var title=respobj.title;
                            var msg=respobj.msg;
                        msgBoxShow([title,msg],Wtf.MessageBox.INFO);
                        this.masterds.reload();
                    }
                }
            },function(){
        })
    },
    masteronload:function(){
        this.masterds.removeAll();
    },
    masterload:function(){
        this.masterds.load({
            params:{
                flag:6,
                configid:this.attributeCombo.getValue()
            }
        })
    },
    onRender: function(config) {
        Wtf.configMasterGrid.superclass.onRender.call(this, config);
    }

});

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
Wtf.common.Features=function(config){
    Wtf.common.Features.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.common.Features, Wtf.Panel, {
    layout:'border',
    defaults:{split:true,border:false},
    initComponent:function(config){
        Wtf.apply(this,{
            bbar:[{
                text:'Add Feature',
                handler:function(){this.showFeatureForm(false)},
                scope:this
            },{
                text:'Edit Feature',
                handler:function(){this.showFeatureForm(true)},
                scope:this
            },{
                text:'Delete Feature',
                handler:this.deleteFeature,
                scope:this
            },'->',{
                text:'Add Activity',
                handler:function(){this.showActivityForm(false)},
                scope:this
            },{
                text:'Edit Activity',
                handler:function(){this.showActivityForm(true)},
                scope:this
            },{
                text:'Delete Activity',
                handler:this.deleteActivity,
                scope:this
            }]
        });
        Wtf.common.Features.superclass.initComponent.call(this,config);
    },
    onRender:function(config){
        Wtf.common.Features.superclass.onRender.call(this,config);
        this.featureRecord=new Wtf.data.Record.create(['featureid','featurename','displayfeaturename']);
        this.featureStore = new Wtf.data.Store({
            url: Wtf.req.base+'UserManager.jsp',
            baseParams:{
                mode:1
            },
            reader: new Wtf.data.KwlJsonReader({
                        root: 'data'
                    },this.featureRecord)
        });
        this.featureGrid=new Wtf.grid.GridPanel({
            region:'west',
            width:'40%',
            store:this.featureStore,
            sm:new Wtf.grid.RowSelectionModel({singleSelect:true}),
            viewConfig:{forceFit:true},
            layout:'fit',
            columns:[
                {header:'Feature Name',dataIndex:'featurename'},
                {header:'Feature Display Name',dataIndex:'displayfeaturename'}
            ]
        });

        this.activityRecord=new Wtf.data.Record.create(['activityid','featureid','activityname','displayactivityname']);
        this.activityStore = new Wtf.data.Store({
            url: Wtf.req.base+'UserManager.jsp',
            baseParams:{
                mode:2
            },
            reader: new Wtf.data.KwlJsonReader({
                        root: 'data'
                    },this.activityRecord)
        });
        this.activityGrid=new Wtf.grid.GridPanel({
            region:'center',
            store:this.activityStore,
            sm:new Wtf.grid.RowSelectionModel({singleSelect:true}),
            layout:'fit',
            viewConfig:{forceFit:true},
            columns:[
                {header:'Activity Name',dataIndex:'activityname'},
                {header:'Activity Display Name',dataIndex:'displayactivityname'}
            ]
        });

        this.add(this.featureGrid);
        this.add(this.activityGrid);
        this.featureGrid.on('rowclick',this.filterActivities, this);

        this.featureStore.on('load',this.loadActivities, this);

        this.featureStore.load();
    },

    loadActivities:function(){
        this.activityStore.on('load',function(){
            this.activityStore.filter('featureid',/^[\0]*$/);
        }, this);
        this.activityStore.load();
    },

    filterActivities:function(){
        var featureid= this.featureGrid.getSelectionModel().getSelected().get('featureid');
        this.activityStore.filter('featureid',featureid);
    },

    showFeatureForm:function(isEdit){
        var rec=null;
        if(isEdit){
            if(this.featureGrid.getSelectionModel().hasSelection()==false){
                Wtf.MessageBox.alert("Edit Feature", "Please select a feature to edit");
                return;
            }

            rec = this.featureGrid.getSelectionModel().getSelected();
        }

        this.createFeatureWindow(rec,isEdit);
    },

    createFeatureWindow:function(rec,isEdit){
        this.form=new Wtf.form.FormPanel({
            url: Wtf.req.base+'UserManager.jsp?mode=3',
            labelWidth: 125,
            border:false,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 10px',
            defaults: {width: 175},
            defaultType: 'textfield',
            items:[{
                fieldLabel:'Feature Name',
                name:'featurename',
                maskRe:/[A-Za-z_]+/,
                allowBlank:false
            },{
                fieldLabel:'Feature Display Name',
                name:'displayfeaturename',
                allowBlank:false
            }]
        });
        this.form.add({xtype:'hidden', name:'featureid'})
        var head="";
        if(isEdit==false)
             {
             head="Add";
             }
        else
             {
             head="Edit";
            }
        this.win=new Wtf.Window({
            title: (isEdit?'Edit':'Add')+' Feature',
            closable:true,
            iconCls: "pwnd favwinIcon",
            modal:true,
            height:250,
            width:400,
            layout:'border',
            buttonAlign:'right',
            buttons:[{
                text:'Save',
                handler:function(){
                    this.form.getForm().submit({
                        waitMsg:'Saving Feature...',
                        scope:this,
                        success:function(f,a){this.win.close();this.genSuccessResponse(eval('('+a.response.responseText+')'))},
                        failure:function(f,a){this.win.close();this.genFailureResponse(eval('('+a.response.responseText+')'))}
                    });

                    },
                    scope:this
                },
                {
                text:'Close',
                handler:function(){

                        this.win.close();
                },
                scope:this
            }
            ],
        items :[{
            region : 'north',
            height : 75,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(" "+head +" Feature", ""+head +" Feature ")
        },{
            region : 'center',
            border : false,

            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
            layout : 'fit',
            items :[   this.form   ]
            }]

        });this.win.show();
        if(isEdit)this.form.getForm().loadRecord(rec);
    },

    genSuccessResponse:function(response){
        Wtf.Msg.alert('Permission',response.msg);
        if(response.success==true)this.featureStore.reload();
    },

    genFailureResponse:function(response){
        var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        Wtf.Msg.alert('Permission',msg);
    },

    deleteFeature:function(){
        if(this.featureGrid.getSelectionModel().hasSelection()==false){
            Wtf.MessageBox.alert("Feature deletion", "Please select a feature to delete");
            return;
        }
        Wtf.MessageBox.confirm("Feature deletion", "Are you sure to delete the selected feature?",function(btn){
            if(btn!="yes") return;
            var rec = this.featureGrid.getSelectionModel().getSelected();
            Wtf.Ajax.requestEx({
                url: Wtf.req.base+'UserManager.jsp',
                params: {
                    mode:5,
                    featureid:rec.get('featureid')
                }
            },this,this.genSuccessResponse,this.genFailureResponse);
        },this);

    },

    showActivityForm:function(isEdit){
        var rec=null;
        if(isEdit){
            if(this.activityGrid.getSelectionModel().hasSelection()==false){
                Wtf.MessageBox.alert("Edit Activity", "Please select an activity to edit");
                return;
            }

            rec = this.activityGrid.getSelectionModel().getSelected();
        }

        this.createActivityWindow(rec,isEdit);
    },

    createActivityWindow:function(rec,isEdit){
        this.aform=new Wtf.form.FormPanel({
         //   frame:true,
            url: Wtf.req.base+'UserManager.jsp?mode=4',
            labelWidth: 125,
            autoHeight:true,
            border:false,
            bodyStyle:'padding:5px 5px 0',
            autoWidth:true,
            defaults: {width: 175},
            defaultType: 'textfield',
            items:[{
                fieldLabel:'Activity Name',
                name:'activityname',
                maskRe:/[A-Za-z_]+/,
                allowBlank:false
            },{
                fieldLabel:'Activity Display Name',
                name:'displayactivityname',
                allowBlank:false
            },new Wtf.form.ComboBox({
                fieldLabel:'Feature',
                hiddenName:'featureid',
                store:this.featureStore,
                mode:'local',
                valueField:'featureid',
                displayField:'displayfeaturename',
                disableKeyFilter:true,
                triggerAction:'all',
                forceSelection:true,
                readOnly:isEdit,
                allowBlank:false
            })]
//            buttons:[{
//                text:'Save',
//                handler:function(){
//                    this.aform.getForm().submit({
//                        waitMsg:'Saving Activity...',
//                        scope:this,
//                        success:function(f,a){this.awin.close();this.genSuccessResponse(eval('('+a.response.responseText+')'))},
//                        failure:function(f,a){this.awin.close();this.genFailureResponse(eval('('+a.response.responseText+')'))}
//                    });
//
//                },
//                scope:this
//            }]
        });
        this.aform.add({xtype:'hidden', name:'activityid'})

        var head="";
        if(isEdit==false)
             {
             head="Add";
             }
        else
             {
             head="Edit";
            }
        this.awin=new Wtf.Window({
            title: (isEdit?'Edit':'Add')+' Activity',
            closable:true,
            height:280,
            width:400,
            iconCls: "pwnd favwinIcon",
            layout:'border',
            buttonAlign:'right',
            buttons:[{
                text:'Save',
                handler:function(){
                    this.aform.getForm().submit({
                        waitMsg:'Saving Activity...',
                        scope:this,
                        success:function(f,a){this.awin.close();this.genSuccessResponse(eval('('+a.response.responseText+')'))},
                        failure:function(f,a){this.awin.close();this.genFailureResponse(eval('('+a.response.responseText+')'))}
                    });

                },
                    scope:this
                },
                {
                text:'Close',
                handler:function(){

                        this.awin.close();
                },
                scope:this
            }
            ],
        items :[{
            region : 'north',
            height : 75,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(" "+ head+" Activity", ""+head +" Activity ")
        },{
            region : 'center',
            border : false,

            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
            layout : 'fit',
            items :[   this.aform   ]
            }]
        });this.awin.show();
        if(isEdit)this.aform.getForm().loadRecord(rec);
    },

    deleteActivity:function(){
        if(this.activityGrid.getSelectionModel().hasSelection()==false){
            Wtf.MessageBox.alert("Activity deletion", "Please select an activity to delete");
            return;
        }
        Wtf.MessageBox.confirm("Activity deletion", "Are you sure to delete the selected activity?",function(btn){
            if(btn!="yes") return;
            var rec = this.activityGrid.getSelectionModel().getSelected();
            Wtf.Ajax.requestEx({
                url: Wtf.req.base+'UserManager.jsp',
                params: {
                    mode:6,
                    activityid:rec.get('activityid')
                }
            },this,this.genSuccessResponse,this.genFailureResponse);
        },this);
    }
});

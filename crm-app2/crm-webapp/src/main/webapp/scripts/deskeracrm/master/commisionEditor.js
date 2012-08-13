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
Wtf.CommisionEditor = function(config){
    this.isPercentStore = new Wtf.data.SimpleStore({
        fields:[{
            name:'name'
        },{
            name:'value',
            type:'boolean'
        }],
        data:[['Percentage',true],['Flat',false]]
    });
    var permData = [];
    var arr = ["1",Wtf.goaltype.nooflead];
    permData.push(arr);
    arr = ["2",Wtf.goaltype.leadrevenue];
    permData.push(arr);

    this.goalTypeStore = new Wtf.data.SimpleStore({
        fields: ['id','name'],
        data : permData
    });

    this.goalTypeCombo = new Wtf.form.ComboBox({
        triggerAction:'all',
        mode: 'local',
        valueField:'id',
        displayField:'name',
        store:this.goalTypeStore,
        value:false,
        typeAhead: true,
        forceSelection: true,
        name:'goaltype',
        hiddenName:'goaltype'
    });
    arr = [];permData = [];
    arr = ["1","Company Financial Year"];
    permData.push(arr);
//    arr = ["2","Employee Anniversary"];
//    permData.push(arr);

    this.periodStore = new Wtf.data.SimpleStore({
        fields: ['id','name'],
        data : permData
    });

    this.periodCombo = new Wtf.form.ComboBox({
        triggerAction:'all',
        mode: 'local',
        valueField:'id',
        displayField:'name',
        store:this.periodStore,
        value:false,
        typeAhead: true,
        forceSelection: true,
        name:'period',
        hiddenName:'period'
    });

    this.isPercent= new Wtf.form.ComboBox({
        triggerAction:'all',
        mode: 'local',
        valueField:'value',
        displayField:'name',
        store:this.isPercentStore,
        value:false,
        typeAhead: true,
        forceSelection: true,
        name:'ispercent',
        hiddenName:'ispercent'
    });
    this.val=new Wtf.form.NumberField({
        allowBlank: false,
        allowNegative:false,
        maxLength:10
    })
    this.cm=[{
        header: "Plan Name",
        dataIndex: 'name',
        editor:new Wtf.form.TextField({
            allowBlank:false,
            maxLength:50
        })
    },{
        header: "Commission Mode",
        dataIndex: 'ispercent',
        renderer:Wtf.comboBoxRenderer(this.isPercent),
        editor:this.isPercent
    },{
        header: "Value",
        dataIndex: 'value',
        editor:this.val/*,
        renderer: function(value, cell, row, rowIndex, colIndex, ds) {
            if(ds.getAt(rowIndex).data.ispercent) {
                value = value.toFixed(0) + '% '
            }
            return value;
        }*/
    },{
        header: "Goal Type",
        dataIndex: 'goaltype',
        renderer:Wtf.comboBoxRenderer(this.goalTypeCombo),
        editor:this.goalTypeCombo
    },{
        header: "Target",
        dataIndex: 'target',
        editor:new Wtf.form.NumberField({
            allowBlank:false,
            allowNegative:false,
            maxLength:10
        }),
        renderer: function(value, cell, row, rowIndex, colIndex, ds) {
            if(row.data.goaltype==2) {
                return WtfGlobal.currencyRenderer(value);
            } else
                return '<div class="currency">'+value+'</div>';

        }
    },{
        header: "Period",
        dataIndex: 'goalperiod',
        renderer:Wtf.comboBoxRenderer(this.periodCombo),
        editor:this.periodCombo
    }];
    this.record = new Wtf.data.Record.create([{
        name: 'id'
    },{
        name: 'name'
    },{
        name: 'ispercent',
        type:'boolean'
    },{
        name: 'value'
    },{
        name: 'goaltype'
    },{
        name: 'goalperiod'
    },{
        name: 'target'
    }]);
    this.store = new Wtf.data.Store({
        reader: new Wtf.data.KwlJsonReader({
            root: "data"
        },this.record),
//        url:Wtf.req.base+'crm.jsp',
        url:Wtf.req.springBase+"Integration/AccountIntegration/getCommision.do",
        baseParams:{
            flag:700
        }
    });

    this.store.load();

    var btnArr=[];
    btnArr.push({
        text: 'Update',
        scope: this,
        handler:this.addArr.createDelegate(this)
    });
    btnArr.push({
        text: 'Close',
        scope: this,
        handler: function(){
            this.close();
        }
    });
    Wtf.apply(this,{
        buttons: btnArr
    },config);
    Wtf.CommisionEditor.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.CommisionEditor, Wtf.Window, {
    closable: true,
    rowDeletedIndexArr:null,
    rowIndexArr:null,
    modal: true,
    iconCls: "pwnd favwinIcon",
    width: 600,
    record:null,
    height: 350,
    resizable: false,
    layout: 'border',
    buttonAlign: 'right',
    initComponent: function(config){
        Wtf.CommisionEditor.superclass.initComponent.call(this, config);
        this.cm.push({
            width:50,
            header:'Action',
            renderer:this.deleteRenderer.createDelegate(this)
        });
    },

    onRender: function(config){
        this.rowDeletedIndexArr=[];
        this.rowIndexArr=[];
        Wtf.CommisionEditor.superclass.onRender.call(this, config);
        this.createDisplayGrid();
        this.add({
            region: 'north',
            height: 75,
            border: false,
            bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(this.title,"Create and Update "+this.title,this.headerImage)
        },{
            region: 'center',
            border: false,
            baseCls:'bckgroundcolor',
            layout: 'fit',
            items:this.grid

        });
        this.addEvents({
            'update':true
        });
        this.store=this.grid.getStore();
        this.grid.on('rowclick',this.processRow,this);
        this.grid.on('afteredit',this.addGridRec,this);
        this.grid.on('validateedit',this.checkDuplicate,this);
        // this.grid.on('beforeedit',this.checkrecord,this);
        this.store.on('load',this.addGridRec,this);
    },

    createDisplayGrid:function(){
        var selectionModel = new Wtf.grid.MultiSelectionModel();
        this.grid = new Wtf.grid.EditorGridPanel({
            selModel: selectionModel,
            layout:'fit',
            clicksToEdit:1,
            store: this.store,
            cm: new Wtf.grid.ColumnModel(this.cm),
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true
            }
        });
    },
    getdeletedArr:function(grid,index,rec){
        var store=grid.getStore();
        var fields=store.fields;
        var recarr=[];
        for(var j=0;j<fields.length;j++){
            var value=rec.data[fields.get(j).name];
            switch(fields.get(j).type){
                case "auto": value="'"+value+"'"; break;
                case "date": value="'"+WtfGlobal.convertToGenericDate(value)+"'";break;
            }
            recarr.push(fields.get(j).name+":"+value);
        }
        recarr.push("modified:"+rec.dirty);
        this.rowDeletedIndexArr.push("{"+recarr.join(",")+"}");
    },
    processRow:function(grid,rowindex,e){
        if(e.getTarget(".delete-gridrow")){
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), 'Are you sure you want to delete?', function(btn){
                if(btn!="yes") return;
                var store=grid.getStore();
                var rec=store.getAt(rowindex);
                this.getdeletedArr(grid,rowindex,rec);
                store.remove(store.getAt(rowindex));
                this.addGridRec();
            }, this);
        }
    },

    checkDuplicate:function(e) {
        var event = e;
        var val = event.value;
        var recData = event.record.data;
        var isCondTrue = false;
        if(event.field == 'ispercent' && val && recData.value > 100) {
            isCondTrue = true;
        } else if(event.field == 'value' && recData.ispercent && val > 100) {
            isCondTrue = true;
        }
        if(isCondTrue) {
            e.cancel = true;
            WtfComMsgBox(["Info","Invalid percentage (must be between 0 and 100)"],0);
            return;
        }
    },
    addArr:function(){
        var editedarr=[];
        for(var i=0;i<this.store.getCount();i++){
            var   rec=this.store.getAt(i);
            if(rec.dirty){
                editedarr.push(i);
            }
        }
        this.rowIndexArr=editedarr;
        this.update(editedarr);
    },
    addGridRec:function(){
        var size=this.store.getCount();
        if(size>0){
            var lastRec=this.store.getAt(size-1);
            var cm=this.grid.getColumnModel();
            var count = cm.getColumnCount();
            for(var i=0;i<count-1;i++){
                if(lastRec.data[cm.getDataIndex(i)].length<=0)
                    return;
            }
        }
        var rec=this.record;
        rec = new rec({});
        rec.beginEdit();
        var fields=this.store.fields;
        for(var x=0;x<fields.length;x++){
            var value="";
            rec.set(fields.get(x).name, value);
        }
        rec.endEdit();
        rec.commit();
        this.store.add(rec);
    },

    deleteRenderer:function(v,m,rec){
        var flag=true;
        var cm=this.grid.getColumnModel();
        var count = cm.getColumnCount();
        for(var i=0;i<count-1;i++){
            if(rec.data[cm.getDataIndex(i)].length<=0){
                flag=false;
                break;
            }
        }
        if(flag){
            return "<div class='pwnd delete-gridrow'></div>";
        }
        return "";
    },

    update:function(arr){
        var rec;
        rec={
            data:this.getJSONArray(arr),
            deleteddata:"["+this.rowDeletedIndexArr.join(',')+"]"
        };
        if(rec.deleteddata=="[]"&&rec.data=="[]"){
            if(arr!="")
                WtfComMsgBox(["Alert","Please complete the details"], 1);
            return;
        }

        rec.flag=702;
        Wtf.Ajax.requestEx({
            url:Wtf.req.springBase+"Integration/AccountIntegration/saveCommision.do",
            params: rec
        },this,this.genSuccessResponse,this.genFailureResponse);
    },

    getJSONArray:function(arr){
        return WtfGlobal.getJSONArray(this.grid,false,arr);
    },

    genSuccessResponse:function(response){
        if(response.success) {
            if(response.data) {
                var resdata = eval('('+response.data+')');
                var planname = "";
                for(var cnt=0;cnt<resdata.length;cnt++)
                    planname += resdata[cnt].name+", ";
                if(planname.length>0) {
                    planname = planname.trim();
                    planname = planname.substr(0,planname.length-1);
                    WtfComMsgBox([this.title,"Commission(s) updated successfully. But "+planname+" has already been used to generate commission calculations and so can't be deleted."],0);
                    this.store.reload();
                    this.rowDeletedIndexArr = [];
                    this.rowIndexArr=[];
                } else {
                    this.fireEvent('update',this);
                    WtfComMsgBox([this.title,"Commission(s) updated successfully."],0);
                    this.close();
                }
            }
        }
    },

    genFailureResponse:function(response){
        var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        WtfComMsgBox([this.title,msg],1);
        this.close();
    }

});

//---------------------------------------------------------------------------------------------------------------------------------------


Wtf.CommisionPlanWin = function(config){
    this.isPercentStore = new Wtf.data.SimpleStore({
        fields:[{
            name:'name'
        },{
            name:'value',
            type:'boolean'
        }],
        data:[['Percentage',true],['Flat',false]]
    });
    var permData = [];
    var arr = ["1",Wtf.goaltype.nooflead];
    permData.push(arr);
    arr = ["2",Wtf.goaltype.leadrevenue];
    permData.push(arr);

    this.goalTypeStore = new Wtf.data.SimpleStore({
        fields: ['id','name'],
        data : permData
    });

    this.goalTypeCombo = new Wtf.form.ComboBox({
        triggerAction:'all',
        mode: 'local',
        valueField:'id',
        displayField:'name',
        store:this.goalTypeStore,
        value:false,
        typeAhead: true,
        forceSelection: true,
        name:'goaltype',
        hiddenName:'goaltype'
    });
    arr = [];permData = [];
    arr = ["1","Company Financial Year"];
    permData.push(arr);
//    arr = ["2","Employee Anniversary"];
//    permData.push(arr);

    this.periodStore = new Wtf.data.SimpleStore({
        fields: ['id','name'],
        data : permData
    });

    this.periodCombo = new Wtf.form.ComboBox({
        triggerAction:'all',
        mode: 'local',
        valueField:'id',
        displayField:'name',
        store:this.periodStore,
        value:false,
        typeAhead: true,
        forceSelection: true,
        name:'period',
        hiddenName:'period'
    });

    this.isPercent= new Wtf.form.ComboBox({
        triggerAction:'all',
        mode: 'local',
        valueField:'value',
        displayField:'name',
        store:this.isPercentStore,
        value:false,
        typeAhead: true,
        forceSelection: true,
        name:'ispercent',
        hiddenName:'ispercent'
    });

    this.yearStore = new Wtf.data.SimpleStore({
        fields:[{
            name:'name'
        },{
            name:'id'
        }],
        data:[['2007','2007'],['2008','2008'],['2009','2009'],['2010','2010'],['2011','2011'],['2012','2012']]
    });

    this.yearCombo = new Wtf.form.ComboBox({
        triggerAction:'all',
        mode: 'local',
        valueField:'id',
        displayField:'name',
        store:this.yearStore,
        value:(new Date()).format("Y"),
        typeAhead: true,
        forceSelection: true,
        name:'id',
        hiddenName:'id'
    });

    var planRec = Wtf.data.Record.create([{
        name: 'planid'
    },{
        name: 'name'
    },{
        name: 'value'
    },{
        name: 'ispercent'
    },{
        name: 'goaltype'
    },{
        name: 'goalperiod'
    },{
        name: 'target'
    }]);
    this.planStore = new Wtf.data.Store({
        url:Wtf.req.springBase+"Integration/AccountIntegration/getCommision.do",
        baseParams: {
            flag: 700
        },
        reader: new Wtf.data.KwlJsonReader({
            root: 'data'
        }, planRec)
    });
    this.planStore.load();

    this.planCombo = new Wtf.form.ComboBox({
        triggerAction:'all',
        emptyText:'< Select plan >',
        mode: 'local',
        valueField:'planid',
        displayField:'name',
        store:this.planStore,
        typeAhead: true,
        forceSelection: true,
        name:'planid',
        hiddenName:'planid'
    });

    this.planCombo.on("select",function(obj,rec,index) {
        if(this.grid.getSelectionModel().getSelected()) {
            var gridrec = this.grid.getSelectionModel().getSelected().data;
            var recData = rec.data;
            gridrec.ispercent = recData.ispercent;
            gridrec.value = recData.value ;
            gridrec.goaltype = recData.goaltype ;
            gridrec.target = recData.target ;
            gridrec.goalperiod = recData.goalperiod;
        }
    },this);

    this.selectionModel = new Wtf.grid.CheckboxSelectionModel({singleSelect:true});
    this.cm=[this.selectionModel,
    {
        header: "From Year",
        dataIndex: 'year',
        renderer:Wtf.comboBoxRenderer(this.yearCombo),
        editor : this.yearCombo
    },{
        header: "Plan Name",
        dataIndex: 'planid',
        renderer:Wtf.comboBoxRenderer(this.planCombo),
        editor : this.planCombo
    },{
        header: "Commission Mode",
        dataIndex: 'ispercent',
        renderer:Wtf.comboBoxRenderer(this.isPercent)
    },{
        header: "Value",
        dataIndex: 'value'
    },{
        header: "Goal Type",
        dataIndex: 'goaltype',
        renderer:Wtf.comboBoxRenderer(this.goalTypeCombo)
    },{
        header: "Target",
        dataIndex: 'target'
    },{
        header: "Period",
        dataIndex: 'goalperiod',
        renderer:Wtf.comboBoxRenderer(this.periodCombo)
    }];

    this.record = new Wtf.data.Record.create([{
        name: 'id'
    },{
        name: 'planid'
    },{
        name: 'ispercent',
        type:'boolean'
    },{
        name: 'value'
    },{
        name: 'goaltype'
    },{
        name: 'goalperiod'
    },{
        name: 'target'
    },{
        name: 'year'
    },{
        name: 'isactive'
    }]);
    this.store = new Wtf.data.Store({
        reader: new Wtf.data.KwlJsonReader({
            root: "data"
        },this.record),
        url:Wtf.req.springBase+"Integration/AccountIntegration/userCommissionPlans.do",
        baseParams:{
            flag:701
        }
    });

    var btnArr=[];
    btnArr.push({
        text: 'Update',
        scope: this,
        handler:this.update.createDelegate(this)
    });
    btnArr.push({
        text: 'Close',
        scope: this,
        handler: function(){
            this.close();
        }
    });
    Wtf.apply(this,{
        buttons: btnArr
    },config);

    Wtf.CommisionPlanWin.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.CommisionPlanWin, Wtf.Window, {
    closable: true,
    rowDeletedIndexArr:null,
    rowIndexArr:null,
    modal: true,
    iconCls: "pwnd favwinIcon",
    width: 600,
    record:null,
    height: 350,
    resizable: false,
    layout: 'border',
    buttonAlign: 'right',
    initComponent: function(config){
        Wtf.CommisionPlanWin.superclass.initComponent.call(this, config);
        this.cm.push({
            width:50,
            header:'Action',
            renderer:this.deleteRenderer.createDelegate(this)
        });
    },
    
    deleteRenderer:function(v,m,rec){
        var flag=true;
        var cm=this.grid.getColumnModel();
        var count = cm.getColumnCount();
        for(var i=1;i<count-1;i++){
            if(rec.data[cm.getDataIndex(i)].length<=0){
                flag=false;
                break;
            }
        }
        if(flag){
            return "<div class='pwnd delete-gridrow'></div>";
        }
        return "";
    },

    getdeletedArr:function(grid,index,rec){
        var store=grid.getStore();
        var fields=store.fields;
        var recarr=[];
        for(var j=0;j<fields.length;j++){
            var value=rec.data[fields.get(j).name];
            switch(fields.get(j).type){
                case "auto": value="'"+value+"'"; break;
                case "date": value="'"+WtfGlobal.convertToGenericDate(value)+"'";break;
            }
            recarr.push(fields.get(j).name+":"+value);
        }
        recarr.push("modified:"+rec.dirty);
    },

    processRow:function(grid,rowindex,e){
        if(e.getTarget(".delete-gridrow")){
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), 'Are you sure you want to delete?', function(btn){
                if(btn!="yes") return;
                var store=grid.getStore();
                var rec=store.getAt(rowindex);
                this.getdeletedArr(grid,rowindex,rec);
                store.remove(store.getAt(rowindex));
                this.addGridRec();
            }, this);
        }
    },
    
    onRender: function(config){
        this.store.on('load',this.addGridRec,this);
        this.store.baseParams.userid = this.userid;
        this.store.load();
        this.rowDeletedIndexArr=[];
        this.rowIndexArr=[];
        Wtf.CommisionPlanWin.superclass.onRender.call(this, config);
        this.createDisplayGrid();
        this.add({
            region: 'north',
            height: 75,
            border: false,
            bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(this.title,this.title+" to <b>"+this.userfullname+"</b>",this.headerImage)
        },{
            region: 'center',
            border: false,
            baseCls:'bckgroundcolor',
            layout: 'fit',
            items:this.grid
        });
        this.store=this.grid.getStore();
    },

    createDisplayGrid:function() {
        var selectionModel = new Wtf.grid.MultiSelectionModel();
        this.addNew = new Wtf.Toolbar.Button({
            scope:this,
            text:"Add New",
            iconCls:"pwnd addIcon",
            tooltip:{text:'Add new record by clicking here.'},
            handler:function(){
                this.addGridRec();
            }
        });
        this.grid = new Wtf.grid.EditorGridPanel({
            layout:'fit',
            selModel: selectionModel,
            store: this.store,
            clicksToEdit:1,
            tbar : [this.addNew],
//            sm : this.selectionModel,
            cm: new Wtf.grid.ColumnModel(this.cm),
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true
            }
        });
        this.grid.on('rowclick',this.processRow,this);
        this.grid.on('validateedit', this.GridValidateEdit, this);
        this.grid.on('afteredit', this.GridAfterEdit, this);
    },

    GridValidateEdit : function (e) {
        var event = e;
        var val = event.value;
        var recData = event.record.data;
        if(event.field == 'planid' && recData.year.length > 0) {
            var records = [];
            var cnt = 0;
            this.store.queryBy(function(record) {
                if(record.get("planid")==val) {
                    records[cnt++]=record;
                }
            });
            for(var incnt =0; incnt < records.length; incnt++) {
                if(recData.year==records[incnt].data.year) {
                    e.cancel = true;
                    WtfComMsgBox(["Info","Already assigned same plan for year "+recData.year],0);
                    return;
                }
            }
        } else if(event.field == 'year' && event.record.data.planid.length > 0) {
            var records = [];
            var cnt = 0;
            this.store.queryBy(function(record) {
                if(record.get("year")==val) {
                    records[cnt++]=record;
                }
            });
            for(var incnt =0; incnt < records.length; incnt++) {
                if(recData.planid==records[incnt].data.planid) {
                    e.cancel = true;
                    WtfComMsgBox(["Info","Already assigned same plan for year "+val],0);
                    return;
                }
            }
        }
    },

    GridAfterEdit : function (e) {
        var event = e;
        var val = event.value;
        if(event.field == 'planid') {
            var index =  this.planStore.findBy(function(record) {
                if(record.get("planid")==val)
                    return true;
                else
                    return false;
            });
            if(index != -1) {
                var recData = event.record.data;
                var plandata = this.planStore.getAt(index).data;
                recData.ispercent = plandata.ispercent;
                recData.value = plandata.value ;
                recData.goaltype = plandata.goaltype ;
                recData.target = plandata.target ;
                recData.goalperiod = plandata.goalperiod;
                event.record.commit();
            }
        }
    },

    selectGridRec:function(){
        var size=this.store.getCount();
        for(var cnt=0;cnt<size;cnt++){
            var lastRec=this.store.getAt(cnt).data;
            if(lastRec.isactive) {
                this.grid.getSelectionModel().selectRow(cnt);
                break;
            }
        }
    },

    addGridRec:function(){
        var size=this.store.getCount();
        if(size>0){
            var lastRec=this.store.getAt(size-1);
            var cm=this.grid.getColumnModel();
            var count = cm.getColumnCount();
            for(var cnt =1;cnt<count-1;cnt++){
                if(lastRec.data[cm.getDataIndex(cnt)].length<=0)
                    return;
            }
        }
        var rec=this.record;
        rec = new rec({});
        rec.beginEdit();
        var fields=this.store.fields;
        for(var x=0;x<fields.length;x++){
            var value="";
            rec.set(fields.get(x).name, value);
        }
        rec.endEdit();
        rec.commit();
        this.store.add(rec);
    },

    update:function() {
        var records = [];
        var cnt = 0;
        this.store.queryBy(function(record) {
            if(record.get("id").length==0) {
                records[cnt++]=record;
            }
        });
        if(records.length>0) {
            var rec;
            var year = "";
            var planid = "";
            for(cnt=0;cnt<records.length;cnt++) {
                year += records[cnt].data.year+",";
                planid += records[cnt].data.planid+",";
            }
            rec={
                userid:this.userid,
                year : year.substr(0,year.length-1),
                planid: planid.substr(0,planid.length-1)
            };

            rec.flag=703;
            Wtf.Ajax.requestEx({
                url:Wtf.req.springBase+"Integration/AccountIntegration/assignCommisionPlan.do",
                params: rec
            },this,this.genSuccessResponse,this.genFailureResponse);
        }
    },

    genSuccessResponse:function(response){
        WtfComMsgBox([this.title,response.msg],0);
        if(response.success){
            this.fireEvent('update',this);
            this.store.reload();
        }
        this.close();
    },

    genFailureResponse:function(response){
        var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        WtfComMsgBox([this.title,msg],1);
        this.close();
    }
});


/*-----------------------------------------------------------------------------------------------------------------------------------------*/

Wtf.CommisionViewWin = function(config){
    this.isPercentStore = new Wtf.data.SimpleStore({
        fields:[{
            name:'name'
        },{
            name:'value',
            type:'boolean'
        }],
        data:[['Percentage',true],['Flat',false]]
    });
    var permData = [];
    var arr = ["1",Wtf.goaltype.nooflead];
    permData.push(arr);
    arr = ["2",Wtf.goaltype.leadrevenue];
    permData.push(arr);

    this.goalTypeStore = new Wtf.data.SimpleStore({
        fields: ['id','name'],
        data : permData
    });

    this.goalTypeCombo = new Wtf.form.ComboBox({
        triggerAction:'all',
        mode: 'local',
        valueField:'id',
        displayField:'name',
        store:this.goalTypeStore,
        value:false,
        typeAhead: true,
        forceSelection: true,
        name:'goaltype',
        hiddenName:'goaltype'
    });
    arr = [];permData = [];
    arr = ["1","Company Financial Year"];
    permData.push(arr);
//    arr = ["2","Employee Anniversary"];
//    permData.push(arr);

    this.periodStore = new Wtf.data.SimpleStore({
        fields: ['id','name'],
        data : permData
    });

    this.periodCombo = new Wtf.form.ComboBox({
        triggerAction:'all',
        mode: 'local',
        valueField:'id',
        displayField:'name',
        store:this.periodStore,
        value:false,
        typeAhead: true,
        forceSelection: true,
        name:'period',
        hiddenName:'period'
    });

    this.isPercent= new Wtf.form.ComboBox({
        triggerAction:'all',
        mode: 'local',
        valueField:'value',
        displayField:'name',
        store:this.isPercentStore,
        value:false,
        typeAhead: true,
        forceSelection: true,
        name:'ispercent',
        hiddenName:'ispercent'
    });

    this.yearStore = new Wtf.data.SimpleStore({
        fields:[{
            name:'name'
        },{
            name:'id'
        }],
        data:[['2007','2007'],['2008','2008'],['2009','2009'],['2010','2010'],['2011','2011'],['2012','2012']]
    });

    this.yearCombo = new Wtf.form.ComboBox({
        triggerAction:'all',
        mode: 'local',
        valueField:'id',
        displayField:'name',
        store:this.yearStore,
        value:(new Date()).format("Y"),
        typeAhead: true,
        forceSelection: true,
        name:'id',
        hiddenName:'id'
    });

    this.selectionModel = new Wtf.grid.CheckboxSelectionModel({singleSelect:true});
    this.cm=[this.selectionModel,
    {
        header: "Plan Name",
        dataIndex: 'name'
    },{
        header: "Commission Mode",
        dataIndex: 'ispercent',
        renderer:Wtf.comboBoxRenderer(this.isPercent)
    },{
        header: "Value",
        dataIndex: 'value'
    },{
        header: "Goal Type",
        dataIndex: 'goaltype',
        renderer:Wtf.comboBoxRenderer(this.goalTypeCombo)
    },{
        header: "Target",
        dataIndex: 'target'
    },{
        header: "Achived",
        dataIndex: 'achived'
    },{
        header: "Percentage Achived",
        dataIndex: 'percentachiv',
        renderer:function(val) {
            var value;
            if(typeof val == 'number') {
                value = val;//.toFixed(0);

            } else {
                value = parseFloat(val);//.toFixed(0);
            }
            if(value < 100)
                return "<span style=\"color:red !important;\">"+value+"%</span>"
            else
                return "<span style=\"color:green !important;\">"+value+"%</span>"
        }
    },{
        header: "Period",
        dataIndex: 'goalperiod',
        renderer:Wtf.comboBoxRenderer(this.periodCombo)
    }];

    this.record = new Wtf.data.Record.create([{
        name: 'id'
    },{
        name: 'name'
    },{
        name: 'ispercent',
        type:'boolean'
    },{
        name: 'value'
    },{
        name: 'goaltype'
    },{
        name: 'goalperiod'
    },{
        name: 'target'
    },{
        name: 'achived'
    },{
        name: 'percentachiv'
    },{
        name: 'isactive'
    }]);
    this.store = new Wtf.data.Store({
        reader: new Wtf.data.KwlJsonReader({
            root: "data"
        },this.record),
        url:Wtf.req.springBase+"Integration/AccountIntegration/viewUserCommission.do",
        baseParams:{
            flag:704
        }
    });

//    var btnArr=[];
//    btnArr.push({
//        text: 'Update',
//        scope: this,
//        handler:this.update.createDelegate(this)
//    });
//    btnArr.push({
//        text: 'Close',
//        scope: this,
//        handler: function(){
//            this.close();
//        }
//    });
//    Wtf.apply(this,{
//        buttons: btnArr
//    },config);

    Wtf.CommisionViewWin.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.CommisionViewWin, Wtf.Window, {
    closable: true,
    rowDeletedIndexArr:null,
    rowIndexArr:null,
    modal: true,
    iconCls: "pwnd favwinIcon",
    width: 600,
    record:null,
    height: 350,
    resizable: false,
    layout: 'border',
    buttonAlign: 'right',

    onRender: function(config) {
        this.store.baseParams.userid = this.userid;
        this.store.baseParams.year = this.yearCombo.getValue();
        this.store.load();
        this.rowDeletedIndexArr=[];
        this.rowIndexArr=[];
        Wtf.CommisionViewWin.superclass.onRender.call(this, config);
        this.createDisplayGrid();
        this.add({
            region: 'north',
            height: 75,
            border: false,
            bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(this.title,this.title+" to <b>"+this.userfullname+"</b>",this.headerImage)
        },{
            region: 'center',
            border: false,
            baseCls:'bckgroundcolor',
            layout: 'fit',
            items:this.grid

        });
        this.yearCombo.on("select",function(obj,rec,index) {
            this.store.baseParams.year = rec.data.id;
            this.store.load();
        },this);
        this.store=this.grid.getStore();
    },

    createDisplayGrid:function() {
        this.grid = new Wtf.grid.GridPanel({
            layout:'fit',
            store: this.store,
            sm : this.selectionModel,
            tbar : ['Commission For Year: ',this.yearCombo],
            cm: new Wtf.grid.ColumnModel(this.cm),
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true
            }
        });
    }
});


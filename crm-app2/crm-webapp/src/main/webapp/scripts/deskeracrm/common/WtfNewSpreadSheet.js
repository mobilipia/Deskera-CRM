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
Wtf.SpreadSheetGrid = function(config) {
    Wtf.apply(this, config);
    this.ssDBid = -1;
    this.rules = [];
    this.undoHistory=[];
    this.redoHistory=[];
    this.comboStoreCache={};
    this.selectflag=0;
    this.createSelModel();
    this.updateStoreConfig();
    this.createColModel();
    this.createSsTbar();
    this.tbar = this.getSsTbar();
    this.createSpreadSheetLook();
    this.bbar1 = (this.pagingFlag ? new Wtf.PagingSearchToolbar({
        pageSize: 25,
        searchField:this.quickSearchTF,
        parentGridObj:this.parentGridObj,
        id: "pagingtoolbar" + this.id,
        store: this.store,
        displayInfo: true,
        emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//"No results to display",
        plugins:this.pP = new Wtf.common.pPageSize({
            id: "pPageSize_" + this.id,
            recordsLimit: 100,
            spreadSheet:true
        })
    }):'');

	this.store.on('load',this.insertNewRecord,this);

    this.store.on("datachanged",function(){
        if(this.pagingFlag && this.pP.combo)
        	this.quickSearchTF.setPage(this.pP.combo.value);
    },this);

    Wtf.SpreadSheetGrid.superclass.constructor.call(this, {
            store        :   this.store,
            isEditor     :   this.isEditor==false?false:true,
            cm           :   (this.cmArray?this.getColModel():this.cm),
            selModel     :   this.getSelModel(),
            viewConfig: {
                                forceFit: true
                            },
            view         :   this.getSpreadSheetLook(),
            bbar         :   this.bbar1,
            plugins		 :   this.colArr[0]
    });
};

Wtf.SpreadSheetGrid.AUDIT_KEY = "auditstr";
Wtf.SpreadSheetGrid.VALID_KEY = "validflag";
Wtf.SpreadSheetGrid.CUSTOM_KEY = "customfield";
Wtf.SpreadSheetGrid.AUTOCUSTOM_KEY = "autocustomfield";
Wtf.SpreadSheetGrid.STATUS_PROCESSING = -1;
Wtf.SpreadSheetGrid.STATUS_INVALID = 0;
Wtf.SpreadSheetGrid.STATUS_VALID = 1;
Wtf.SpreadSheetGrid.NEW_ID = "0";

Wtf.extend(Wtf.SpreadSheetGrid, Wtf.grid.EditorGridPanel, {
    layout:'fit',
    //loadMask:true,
    border:false,
    stripeRows:true,
    selColIndex : -1,
    selType : "None",
    cls : "spreadsheetcls",
    ruleCSS:{},
    sCol:3,
    eCol:0,
    onRender: function(config){
    	Wtf.SpreadSheetGrid.superclass.onRender.call(this,config);
//    	if(!this.isDetailPanel)
//    		this.parentGridObj.bwrap.mask("Loading SpreadSheet...","x-mask-loading");
    },
    initComponent: function(config){
    	this.addEvents('beforeupdate','afterupdate');
        if(!this.isDetailPanel)
            this.getMyConfig();
        Wtf.SpreadSheetGrid.superclass.initComponent.call(this,config);
        this.on('render',function(){this.colMenu.getEl().addClass('menuHeight');},this.spreadSheetLook);
        this.on('statesave',this.updateSheetState,this);
        this.on("validateedit",this.validateRecord,this);
        this.on("afteredit",this.completeEditRecord,this);
        
        if(!this.isEditor){
        	this.on("beforeedit",function(e){
        		e.cancel = true;
        		return false;
        	},this);
        }
        
        	
        
        //this.on("beforeedit",this.checkRecordEditable,this);// Sagar M - commented to allow cursor move to next cell 
        //this.reloadStore.defer(10,this);
    },

    createSelModel:function(){
        this.selModel = new Wtf.grid.MultiCellSelectionModel({unselectable:true});
        this.selModel.on('colselect',function(sm, i){this.selColIndex=i;}, this);
    },

    updateStoreConfig:function(){
        var colModelArray = GlobalColumnModel[this.moduleid];
        if(colModelArray){
           for(var cnt = 0;cnt < colModelArray.length;cnt++){
               var fieldname = colModelArray[cnt].fieldname;
               var newField = new Wtf.data.Field({
                   name:fieldname.replace(".",""),
                   sortDir:'ASC',
                   type:colModelArray[cnt].fieldtype == 3 ?  'date' : (colModelArray[cnt].fieldtype == 2?'float':'auto'),
                   dateFormat:colModelArray[cnt].fieldtype == 3 ?  'time' : undefined
               });
               this.store.fields.items.push(newField);
               this.store.fields.map[fieldname]=newField;
               this.store.fields.keys.push(fieldname);
           }
           this.store.reader = new Wtf.data.KwlJsonReader(this.store.reader.meta, this.store.fields.items);
       }
       var sort=GlobalSortModel[this.moduleName];
       if(sort){
    	   this.store.setDefaultSort(sort.field,sort.direction);
    	   this.store.extraSortInfo={xtype:sort.xtype,xfield:sort.xfield,iscustomcolumn:sort.iscustomcolumn};
       }
    },

    createColModel:function(){
        this.colArr = [];
        var rowNum = new Wtf.RowNumbererWithNew({allowIncreament:true, width:30,unselectable:true});
        this.colArr.push(rowNum);
        this.colArr.push(this.getSelModel());
        this.colArr.push.apply(this.colArr, this.cmArray);
        this.appendCustomColumn(this.colArr,GlobalColumnModel[this.moduleid]);
        Wtf.each(this.colArr,function(c){
        	c.headerName=this.getHeaderName(c.header);
        	if(!c.renderer){
	        	switch(c.xtype){
	        	case 'combo':
                        if(c.editor.searchStoreCombo){
                        	this.comboStoreCache[c.dataIndex] = c.editor;
                            c.renderer = this.getComboNameRenderer(c.editor);
                        }else
                            c.renderer = Wtf.ux.comboBoxRenderer(c.editor);
	        		  break;
	        	case 'select':c.renderer =this.getSelectComboRenderer(c.editor);
	        		  break;
	        	}
        	}
        	if(c.renderer){
        		c.renderer=c.renderer.createWtfSequence(this.styleRenderer,this);
        	}else{
        		c.renderer=this.styleRenderer.createDelegate(this);
        	}
        },this);
        this.colModel = new Wtf.grid.ColumnModel(this.colArr);
        this.loadMainStore();
    },

    styleRenderer:function(v,m,r,i,j,s,rv){
		m.cellStyle="";
		var rv1=Wtf.util.Format.stripTags(rv);
		var style=r.data.cellStyle?(r.data.cellStyle[m.id]?r.data.cellStyle[m.id]:""):"";
		if(style.indexOf(':')>=0){
			m.cellStyle=style;
		}else{
			m.css+=" "+style;
		}
		var tmp="";
		var c=this.colModel.config[j];
		if(c.hidden||c.unselectable)
			return v;
		var mval = v;
		if(rv1&&(c.xtype=='combo'||c.xtype=='select'||c.xtype=='datefield'||c.xtype=='timefield'))
			 mval=rv1;
		for(var rulename in this.ruleCSS.rules){
			if(this.ruleCSS.rules[rulename](v,rv1||v,c.xtype)==true)
				 tmp+= " "+rulename;
		}
		m.css += tmp;
		return mval;
	},

    appendCustomColumn:function(colArr,ccm){
        if(ccm){
         for(var cnt=0;cnt<ccm.length;cnt++){
             var tempObj=null;
             var colModelObj = ccm[cnt];
             var fieldtype = colModelObj.fieldtype;
             var fieldid = colModelObj.fieldid;
             var editorObj = {
                 xtype:WtfGlobal.getXType(fieldtype),
                 maxLength:colModelObj.maxlength,
                 required:colModelObj.isessential,
                 store:null,
                 useDefault:true
             };
             switch(fieldtype){
             case 3:editorObj['format']=WtfGlobal.getOnlyDateFormat();
            	 break;
             case 4:
             case 7:
            	 editorObj.store=this.getCCStore(fieldid,{
                     url:'crm/common/fieldmanager/getCustomCombodata.do',
                     baseParams : {
                         fieldid : fieldid,
                         _dc : Math.random(),
                         valreq:'0'
                     },
                     reader: new Wtf.data.JsonReader({
                         root:'data'
                     }, GlobalComboReader)
                 });
            	 break;
             case 8:
            	 editorObj.store=this.getCCStore(fieldid,{
                     url: 'Common/CRMManager/getComboData.do',
                     baseParams:{
                         comboname:colModelObj.comboname,
                         common:'1'
                     },
                     reader: new Wtf.data.KwlJsonReader({
                         root:'data'
                     },  Wtf.ComboReader)
                 },colModelObj.comboid);
            	 break;
             }

             var dbname = fieldtype==7?colModelObj.refcolumn_number:colModelObj.column_number;
             var header = colModelObj.fieldlabel+(colModelObj.isessential ? " *":"");
//             var denied = this.moduleid!=1&&fieldtype==3;
             tempObj = {
                 header:header,
                 tip:header,
                 id:'custom_field'+fieldid,
                 fieldid:fieldid,
                 editor: colModelObj.iseditable=="true" ? this.getEditor(editorObj): undefined,
                 dataIndex:colModelObj.fieldname.replace(".",""),
                 pdfwidth:60,
                 dbname:dbname,//Custom field column in which value is saved.
                 iscustomcolumn:true,
                 sortable:colModelObj.iseditable=="true"?true:false,
                 xtype:WtfGlobal.getXType(fieldtype),
                 fieldtype:fieldtype,
                 refdbname : colModelObj.column_number//Custom field column in which sort(single) value for multi select dropdown is saved.
             };
             switch(fieldtype){
             case 3: tempObj['renderer']=WtfGlobal.onlyDateRendererTZ;
            	 break;
             case 5: tempObj['renderer']=WtfGlobal.getTimeFieldRenderer;
            	 break;
             case 2:
            	 if(colModelObj.fieldname.toLowerCase().endsWith("currency"))
            		 tempObj['renderer']=WtfGlobal.currencyRenderer;
            	 else
            		 tempObj['renderer']=WtfGlobal.zeroRenderer;
            	 break;
             }

             colArr.push(tempObj);
         }
       }
   },

   loadMainStore:function(s){
	   for(var x=0;this.cstStore&&x<this.cstStore.length;x++){
		   if(this.cstStore[x]==s){
			   this.cstStore.splice(x,1);
			   break;
		   }
	   }
	   if(!this.cstStore||this.cstStore.length<=0){
		   if(this.parentGridObj.searchparam){
			   this.parentGridObj.showAdvanceSearch.defer(10,this.parentGridObj);
            }else { // spreadsheet configuration already fetched... so need to load master store and then main store with some delay
                new Wtf.util.DelayedTask(function() {
                    this.store.load({
                        params:{
                            start:0,
                            limit:25
		   }
                        });
                },this).delay(1);
	   }
        }
   },

   getSelectComboRenderer : function(combo){
       return function(value) {
           var idx;
           var rec;
           var valStr="";
           if (value != undefined && value != "") {
               var valArray = value.split(",");
               for (var i=0;i < valArray.length;i++ ){
                   idx = combo.store.find(combo.valueField, valArray[i]);
                   if(idx != -1){
                       rec = combo.store.getAt(idx);
                       valStr+=rec.get(combo.displayField)+", ";
                   }
               }
               if(valStr != ""){
                   valStr=valStr.substring(0, valStr.length -2);
                   valStr="<div wtf:qtip=\""+valStr+"\">"+Wtf.util.Format.ellipsis(valStr,27)+"</div>";
               }
           }
           return valStr;
       };
   },
   getHeaderName : function(header){
       var headerName = WtfGlobal.HTMLStripper(header);
       var indx=headerName.indexOf('(');
       if(indx!=-1) {
               headerName = headerName.substring(0,headerName.indexOf('('));
       }
       headerName = headerName.replace("*","");
       return headerName;
   },

   getCCStore:function(fieldid, storeObj, comboid){
        store = GlobalComboStore["cstore"+fieldid];
        if(store==null){
            switch(comboid){
		   case Wtf.common.productModuleID: store = Wtf.productStore;break;
                /*
            * Below stores are not in Used
            */
                //		   case Wtf.common.leadModuleID: store = Wtf.leadStore;break;
                //		   case Wtf.common.contactModuleID: store = Wtf.contactStore;break;
                //		   case Wtf.common.caseModuleID: store = Wtf.caseStore;break;
                //		   case Wtf.common.oppModuleID: store = Wtf.opportunityStore;break;
                case Wtf.common.userModuleID:
                    store = Wtf.allUsersStore;Wtf.allUsersStore.load();
                    break;
                default:
                    store = new Wtf.data.Store(storeObj);
                    this.cstStore = this.cstStore||[];
                    this.cstStore.push(store);
                    store.on('load', this.loadMainStore,this,{
                        single:true
                    });
                    store.load();
            }
            GlobalComboStore["cstore"+fieldid] = store;
        }
        return store;
    },

   getEditor : function(eObj){
       var editor = null;
       if(eObj.xtype == "combo") {
           if(eObj.useDefault==true){
        	   Wtf.applyIf(eObj,{
        		   selectOnFocus:true,
                   triggerAction:'all',
                   mode:'local',
                   valueField:'id',
                   displayField:'name',
                   typeAhead:true,
                   tpl:Wtf.comboTemplate     		   
        	   });   
           }
           if(eObj.searchStoreCombo && eObj.searchStoreCombo == true) {
                eObj.mode = 'remote';
                if(!eObj.loadOnSelect) {
                    eObj.minChars = 2;
                    eObj.triggerClass ='dttriggerForTeamLead';
                }
                eObj.spreadSheetCombo = true;
                eObj.listeners = {
                    'beforeselect' : function(combo, record, index){
                        if(combo.searchStoreCombo) {
                        	if(this.EditorGrid.getSelectionModel().getSelectedCells().length!=0)
                        		combo.originalOldId = this.EditorGrid.getStore().getAt(this.EditorGrid.getSelectionModel().getSelectedCells()[0][0]).data[combo.comboFieldDataIndex+'id'];
                        	else
                        		combo.originalOldId = record.data.name;
                        }
                    },
                    scope:this
                }
           }
           editor = new Wtf.form.ComboBox(eObj);
           editor.on('beforeselect',Wtf.SpreadSheetGrid.prototype.validateSelection,this);
       }else if(eObj.xtype == "select") {
           if(eObj.useDefault==true){
               eObj.selectOnFocus = true;
               eObj.forceSelection = true;
               eObj.multiSelect = true;
               eObj.triggerAction = 'all';
               eObj.mode = 'local';
               eObj.valueField = 'id';
               eObj.displayField = 'name';
               eObj.typeAhead = true;
               eObj.tpl= Wtf.comboTemplate;
           }
           eObj.spreadSheetCombo = true;
           editor = new Wtf.common.Select(eObj);
           editor.on('beforeselect',Wtf.SpreadSheetGrid.prototype.validateSelection,this);
       } else if(eObj.xtype == "textfield") {
           editor = new Wtf.form.TextField(eObj);
       } else if(eObj.xtype == "numberfield") {
           editor = new Wtf.form.NumberField(eObj);
       } else if(eObj.xtype == "datefield") {
           eObj.readOnly=true;
           eObj.offset=Wtf.pref.tzoffset;
           editor = new Wtf.form.DateField(eObj);
       } else if(eObj.xtype == "timefield") {
           if(eObj.useDefault==true) {
               eObj.value = WtfGlobal.setDefaultValueTimefield();
               eObj.format=WtfGlobal.getLoginUserTimeFormat();
           }
           editor = new Wtf.form.TimeField(eObj);
       } else if(eObj.xtype == "textarea") {
           editor = new Wtf.form.TextArea(eObj);
       }else if(eObj.xtype == "checkbox"){
           editor = new Wtf.form.Checkbox(eObj);
       }

       return editor;
   },

   getComboNameRenderer : function(combo){
        return function(value,metadata,record,row,col,store) {
            var idx = combo.store.find(combo.valueField, value);
            var fieldIndex = combo.comboFieldDataIndex;
            if(idx == -1) {
                if(record.data[combo.comboFieldDataIndex] && record.data[fieldIndex].length>0) {
                    return record.data[fieldIndex];
                }
                else
                    return "";
            }
            var rec = combo.store.getAt(idx);
            var displayField = rec.get(combo.displayField);
            record.set(fieldIndex+"id", value);
            //record.data[fieldIndex] = displayField;
            return displayField;
        }
    },
   createSpreadSheetLook:function(){
        this.spreadSheetLook = new Wtf.grid.GridView({
            forceFit : false,
            moduleid:this.moduleid,
            id:this.id,
            gridObjScope: this,
            modulename:this.moduleName,
            templates:{
        		hcell:new Wtf.Template(
                    '<td class="x-grid3-hd x-grid3-cell x-grid3-td-{id}" style="{style}"><div Wtf:qtip="{tip}" {attr} class="x-grid3-hd-inner x-grid3-hd-{id}" unselectable="on" style="{istyle}">', this.enableHdMenu ? '<a class="x-grid3-hd-btn" href="#"></a>' : '',
                    '{value}<img class="x-grid3-sort-icon" src="', Wtf.BLANK_IMAGE_URL, '" />',
                    "</div></td>"
                ),
                cell:new Wtf.Template(
                        '<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style} {cellStyle}" tabIndex="0" {cellAttr}>',
                        '<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on" {attr}>{value}</div>',
                        "</td>"
                        )
        	},
        	renderHeaders:this.renderHeaders,
            parentGridObj:this.parentGridObj,
            onHeaderClick:Wtf.emptyFn
        });
        this.spreadSheetLook.handleHdMenuClick = this.spreadSheetLook.handleHdMenuClick.createInterceptor(this.storeExtraInfo);
    },


    storeExtraInfo:function(item, idx){
    	if(item.id=="asc"||item.id=="desc"){
    		var column=this.cm.config[this.hdCtxIndex];
    		this.ds.extraSortInfo={xtype : column.xtype, xfield : column.dbname,iscustomcolumn:!!column.iscustomcolumn};
    	}
    },

    validateSelection : function(combo,record,index){
       return record.get('hasAccess' );
    },

   getSsTbar:function(){
        return this.sstbar;
   },

   createSsTbar:function(){
        this.createStyleButtons();

        this.clearFormatBut = new Wtf.Toolbar.Button({
            tooltip:"Clear formatting.",
            iconCls:'sheetBar clear-format-img',
            scope : this,
            handler : function(but, e){
               // this.clearFormatHandler();
               var cellArray =this.getSelectedCells();
               var classArray = ["ssBold","ssStrikeThrough","ssAlignright","ssAlignleft","ssAligncenter",
                                 "ssTextColor-000000","ssTextColor-993300","ssTextColor-333300","ssTextColor-003300","ssTextColor-003366",
                                 "ssTextColor-000080","ssTextColor-333399","ssTextColor-333333","ssTextColor-800000","ssTextColor-FF6600",
                                 "ssTextColor-808000","ssTextColor-008000","ssTextColor-008080","ssTextColor-0000FF","ssTextColor-666699",
                                 "ssTextColor-808080","ssTextColor-FF0000","ssTextColor-FF9900","ssTextColor-99CC00","ssTextColor-339966",
                                 "ssTextColor-33CCCC","ssTextColor-3366FF","ssTextColor-800080","ssTextColor-969696","ssTextColor-FF00FF",
                                 "ssTextColor-FFCC00","ssTextColor-FFFF00","ssTextColor-00FF00","ssTextColor-00FFFF","ssTextColor-00CCFF",
                                 "ssTextColor-993366","ssTextColor-C0C0C0","ssTextColor-FF99CC","ssTextColor-FFCC99","ssTextColor-FFFF99",
                                 "ssTextColor-CCFFCC","ssTextColor-CCFFFF","ssTextColor-99CCFF","ssTextColor-CC99FF","ssTextColor-FFFFFF",
                                 "ssBGColor-000000","ssBGColor-993300","ssBGColor-333300","ssBGColor-003300","ssBGColor-003366",
                                 "ssBGColor-000080","ssBGColor-333399","ssBGColor-333333","ssBGColor-800000","ssBGColor-FF6600",
                                 "ssBGColor-808000","ssBGColor-008000","ssBGColor-008080","ssBGColor-0000FF","ssBGColor-666699",
                                 "ssBGColor-808080","ssBGColor-FF0000","ssBGColor-FF9900","ssBGColor-99CC00","ssBGColor-339966",
                                 "ssBGColor-33CCCC","ssBGColor-3366FF","ssBGColor-800080","ssBGColor-969696","ssBGColor-FF00FF",
                                 "ssBGColor-FFCC00","ssBGColor-FFFF00","ssBGColor-00FF00","ssBGColor-00FFFF","ssBGColor-00CCFF",
                                 "ssBGColor-993366","ssBGColor-C0C0C0","ssBGColor-FF99CC","ssBGColor-FFCC99","ssBGColor-FFFF99",
                                 "ssBGColor-CCFFCC","ssBGColor-CCFFFF","ssBGColor-99CCFF","ssBGColor-CC99FF","ssBGColor-FFFFFF"];
               this.applyCSS(cellArray,undefined,classArray);
            }
        });
        //this.clearFormatBut.setDisabled(true);

        this.boldBut = new Wtf.Toolbar.Button({
            tooltip:"Bold",
            iconCls:'sheetBar bold-img',
            scope : this,
            handler : function(bold, e){
               // this.extraStyleHandler("font-weight", "bold");
               var cellArray =this.getSelectedCells();
               this.applyCSS(cellArray,"ssBold");

            }
        });
      //  this.boldBut.setDisabled(true);

        this.strikeBut = new Wtf.Toolbar.Button({
            tooltip:"Strikethrough",
            iconCls:'sheetBar strike-img',
            scope : this,
            handler : function(strike, e){
                //this.extraStyleHandler("text-decoration", "line-through");
                var cellArray =this.getSelectedCells();
                this.applyCSS(cellArray,"ssStrikeThrough");
            }
        });
      //  this.strikeBut.setDisabled(true);

        this.alignBut = new Wtf.Toolbar.Button({
            tooltip:"Align",
            iconCls:'sheetBar align-palette-left-img',
            menu: this.alignMenu
        });
     //   this.alignBut.setDisabled(true);

        this.textColorBut = new Wtf.Toolbar.Button({
            tooltip:"Text color",
            iconCls:'sheetBar text-color-but-img',
            menu: this.textColorMenu
        });
        //this.textColorBut.setDisabled(true);
        this.bgColorBut = new Wtf.Toolbar.Button({
            tooltip:"Background color",
            iconCls:'sheetBar bg-color-but-img',
            menu: this.bgColorMenu
        });
        //this.bgColorBut.setDisabled(true);
        this.undoBut = new Wtf.Toolbar.Button({
            tooltip:"Undo formatting",
            iconCls:'sheetBar undo-img',
            scope : this,
            handler :function(){this.undoOperation(this.undoHistory, this.redoHistory);}
        });

        this.undoBut.setDisabled(true);

        this.redoBut = new Wtf.Toolbar.Button({
            tooltip:"Redo formatting",
            iconCls:'sheetBar redo-img',
            scope : this,
            handler :function(){this.undoOperation(this.redoHistory, this.undoHistory);}
        });

        this.redoBut.setDisabled(true);

        this.ruleWin = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.spreadsheet.conditionalcolorcodingBTN"),//"Conditional Color Coding",
            iconCls:"applyRulesIcon",
            tooltip:WtfGlobal.getLocaleText("crm.spreadsheet.conditionalcolorcodingBTN.ttip"),//"Change color based on rules.",
            scope:this,
            handler: function(){
                var ruleWin = new Wtf.SpreadSheet.RuleWindow({
                    sSheet : this,
                    grid : this.getGrid()
                });
                ruleWin.show();
                ruleWin.on('ruleApply', this.saveMyRuleHandler, this);
            }
        });

        this.customizeHeader = new Wtf.Action({
            text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.customizeheader"),//"Customize Header",
            iconCls:"pwnd customizeHeader",
            handler:function (){

                this.customizeHeaderWin=new Wtf.customizeHeader({
                    scope:this,
                    modulename:this.moduleName
                });
                this.customizeHeaderWin.on("aftersave",function(id,iscustomHeader){
                    if(!this.isDetailPanel) {
                        delete GlobalSpreadSheetConfig[this.moduleName];
                        delete GlobalQuickSearchEmptyText[this.moduleName];
                    	this.getMyConfig(iscustomHeader);
                    }
                        this.store.reload();
                },this);

                this.customizeHeaderWin.show();

            },
            scope:this
        });

        this.customFormulae = new Wtf.Action({
            text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.addcustomformula"),//"Add Custom Formula",
            iconCls:"pwnd customizeHeader",
            handler:function (){
                if(this.selColIndex != -1) {
                    var fieldid = this.colModel.config[this.selColIndex].id;
                    if(fieldid.substr(0, 12) == "custom_field") {
                        var colModelConfigArray = this.colModel.config[this.selColIndex];
                        if(colModelConfigArray.fieldtype == "9") { // if auto no
                            WtfComMsgBox(["Status", "You cannot set formula to Auto-Number field."]);
                            return;
                        }
                        if(colModelConfigArray.sheetEditor== undefined && colModelConfigArray.editor == undefined) {
                         WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.managecolumnmenu.customformula.allRedyApplied")]);
                         return;
                        }
                        var sheetEditor = colModelConfigArray.sheetEditor||colModelConfigArray.editor.field;
                        if(sheetEditor != undefined && (sheetEditor.xtype == "combo"
                                || sheetEditor.xtype == "datefield" || sheetEditor.xtype == "timefield")) {
                             WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.managecolumnmenu.customformula.combomsg")]);//"You cannot set formula to the Combo, Date or Time fields."]);
                             return;
                        }

//                        callExpressionManager({
//                            ismandatory : colModelConfigArray.mandatory,
//                            fieldlabel:colModelConfigArray.dataIndex,
//                            modulename:this.moduleName,
//                            moduleid:this.moduleid,
//                            createFieldFlag : false,
//                            fieldid:fieldid.substr(12,fieldid.length),
//                            spreadsheet : this
//                        });
                        new Wtf.SpreadSheet.FormulaeWindow({
                            id:this.id+'addCustomFormulae',
                            cm:this.colModel,
                            ismandatory : colModelConfigArray.mandatory,
                            fieldlabel:colModelConfigArray.dataIndex,
                            modulename:this.moduleName,
                            moduleid:this.moduleid,
                            createFieldFlag : false,
                            fieldid:fieldid.substr(12,fieldid.length),
                            spreadsheet : this
                        });
                    } else {
                        WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.managecolumnmenu.customformula.msg")]);//"You can add formula to only custom columns. <br/> Please select the custom column."]);
                    }
                } else {
                    WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.managecolumnmenu.customformula.msg")]);//"You can add formula to only custom columns. <br/> Please select the custom column."]);
                }
            },
            scope:this
        });

        this.delCustomColumn = new Wtf.Action({
            text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.deletecustomcol"),//"Delete Custom Column",
            tooltip:{text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.deletecustomcol.ttip")},//'Delete selected custom column.'},
            iconCls:"pwnd customizeHeader",
            handler:function(){
            	deleteCustomColumn(this);
            },
            scope:this
        });

        this.addCustomColumn = new Wtf.Action({
            text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.addcustomcol"),//"Add Custom Column",
            tooltip:{text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.addcustomcol.ttip")},//'Add new custom column.'},
            iconCls:"pwnd customizeHeader",
            handler:function (){
                addCustomColumn(this);
            },
            scope:this
        });
        this.editCustomColumn = new Wtf.Action({
            text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.editcustomcol"),//"Edit Custom Column",
            tooltip:{text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.editcustomcol.ttip")},//'Edit new custom column.'},
            iconCls:"pwnd customizeHeader",
            handler:function (){
                if(this.selColIndex != -1) {
                    var colModelConfigArray = this.colModel.config[this.selColIndex];
                    var sheetEditor = colModelConfigArray.sheetEditor||colModelConfigArray.editor!=null?colModelConfigArray.editor.field:colModelConfigArray;

                    var fieldid = colModelConfigArray.id;
                    if(fieldid.substr(0, 12) == "custom_field") {
                        if(sheetEditor != undefined && (sheetEditor.xtype == "combo" || sheetEditor.xtype == "select" )  ) {
                            WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.managecolumnmenu.customformula.cannoteditcombomsg")]);//"You cannot edit the Combo fields."]);
                            return;
                        }else if(sheetEditor == undefined && colModelConfigArray.xtype == "numberfield") {
                            WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.managecolumnmenu.customformula.cannoteditfieldmsg")]);//"You cannot edit the selected field as formula is set to that field."]);
                            return;
                        }else if( colModelConfigArray.fieldtype=="9") {
                            WtfComMsgBox(["Alert", "Sorry! You can't edit the Auto Number field."]);
                            return;
                        }
                        EditCustomColumn(this,colModelConfigArray);
                    }else {
                        WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.managecolumnmenu.editcustomcol.msg")]);//"You can edit only custom columns. <br/> Please select the custom column."]);
                   }
                }else {
                     WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"),WtfGlobal.getLocaleText("crm.managecolumnmenu.editcustomcol.msg")]);// "You can edit only custom columns. <br/> Please select the custom column."]);
                }
            },
            scope:this
        });
        var menuArray=[this.customizeHeader];
        if(this.moduleName != "Campaign"){
            menuArray.push(this.addCustomColumn);
            menuArray.push(this.editCustomColumn);
            menuArray.push(this.delCustomColumn);
            menuArray.push(this.customFormulae);
        }
        this.customize=new Wtf.Toolbar.Button({
            iconCls: "pwnd customizeHeader",
            text:WtfGlobal.getLocaleText("crm.spreadsheet.managecolumnmenu"),//"Manage Columns",
            menu: menuArray
        });

        this.selectedCellText = new Wtf.form.TextField({
            maxLength:85,
            style : 'text-align:center;',
            width:200,
            readOnly:true
        });
        this.SSEditorText = new Wtf.form.TextField({
            maxLength:300,
            width:210,
            readOnly:true
        });
        this.refreshButton = new Wtf.Toolbar.Button({
            scope:this,
            iconCls:"pwndCRM refreshButton",
            tooltip:{text:WtfGlobal.getLocaleText("crm.spreadsheet.refreshBTN.ttip")},//'Click to refresh.'},
            handler:function(){this.store.reload();}
        });

        this.sstbar = [];
        this.sstbar.push(this.selectedCellText);
        this.sstbar.push("-");
        this.sstbar.push(this.SSEditorText);
        this.sstbar.push("-");
        this.sstbar.push(this.clearFormatBut);
        this.sstbar.push("-");
        this.sstbar.push(this.boldBut);
        this.sstbar.push(this.strikeBut);
        //        this.sstbar.push(this.blinkBut);
        this.sstbar.push("-");
        this.sstbar.push(this.alignBut);
        this.sstbar.push(this.textColorBut);
        this.sstbar.push(this.bgColorBut);
        this.sstbar.push("-");
        this.sstbar.push(this.undoBut);
        this.sstbar.push(this.redoBut);
        this.sstbar.push(this.ruleWin);
        this.sstbar.push("-");
        if(Wtf.URole.roleid == Wtf.AdminId){
            var modName = this.parentGridObj.modName;
            if(modName!="AccountActivity" && modName!="CampaignActivity" && modName!="CaseActivity" && modName!="ContactActivity" && modName!="LeadActivity"  && modName!="OpportunityActivity"){
                if(this.parentGridObj.newFlag==2){
                    this.sstbar.push(this.customize);
                    this.sstbar.push("-");
                }
            }
        }
          this.sstbar.push("->");
         this.sstbar.push(this.refreshButton);
    },
    createStyleButtons : function(){
        this.alignMenu = new Wtf.menu.AlignMenu();
        this.alignMenu.on('select',function(cm, style){
            var cellArray = this.getSelectedCells();
            var removeCssClass = ["ssAlignright","ssAlignleft","ssAligncenter"];
            this.applyCSS(cellArray,"ssAlign"+style,removeCssClass);
        },this);


        this.textColorMenu = new Wtf.menu.ColorMenu({
            allowReselect : true
        });
        this.textColorMenu.on('select',function(cm, color){
        	var cssClass = "ssTextColor-"+color;
            var cellArray = this.getSelectedCells();
            var removeCssClass = ["ssTextColor-000000","ssTextColor-993300","ssTextColor-333300","ssTextColor-003300","ssTextColor-003366",
                                  "ssTextColor-000080","ssTextColor-333399","ssTextColor-333333","ssTextColor-800000","ssTextColor-FF6600",
                                  "ssTextColor-808000","ssTextColor-008000","ssTextColor-008080","ssTextColor-0000FF","ssTextColor-666699",
                                  "ssTextColor-808080","ssTextColor-FF0000","ssTextColor-FF9900","ssTextColor-99CC00","ssTextColor-339966",
                                  "ssTextColor-33CCCC","ssTextColor-3366FF","ssTextColor-800080","ssTextColor-969696","ssTextColor-FF00FF",
                                  "ssTextColor-FFCC00","ssTextColor-FFFF00","ssTextColor-00FF00","ssTextColor-00FFFF","ssTextColor-00CCFF",
                                  "ssTextColor-993366","ssTextColor-C0C0C0","ssTextColor-FF99CC","ssTextColor-FFCC99","ssTextColor-FFFF99",
                                  "ssTextColor-CCFFCC","ssTextColor-CCFFFF","ssTextColor-99CCFF","ssTextColor-CC99FF","ssTextColor-FFFFFF"];
            this.applyCSS(cellArray,cssClass,removeCssClass);

        },this);


        this.bgColorMenu = new Wtf.menu.ColorMenu({
            allowReselect : true
        });
        this.bgColorMenu.on('select',function(cm, color){
        	var cssClass = "ssBGColor-"+color;
            var cellArray = this.getSelectedCells();
            var removeCssClass = ["ssBGColor-000000","ssBGColor-993300","ssBGColor-333300","ssBGColor-003300","ssBGColor-003366",
                                  "ssBGColor-000080","ssBGColor-333399","ssBGColor-333333","ssBGColor-800000","ssBGColor-FF6600",
                                  "ssBGColor-808000","ssBGColor-008000","ssBGColor-008080","ssBGColor-0000FF","ssBGColor-666699",
                                  "ssBGColor-808080","ssBGColor-FF0000","ssBGColor-FF9900","ssBGColor-99CC00","ssBGColor-339966",
                                  "ssBGColor-33CCCC","ssBGColor-3366FF","ssBGColor-800080","ssBGColor-969696","ssBGColor-FF00FF",
                                  "ssBGColor-FFCC00","ssBGColor-FFFF00","ssBGColor-00FF00","ssBGColor-00FFFF","ssBGColor-00CCFF",
                                  "ssBGColor-993366","ssBGColor-C0C0C0","ssBGColor-FF99CC","ssBGColor-FFCC99","ssBGColor-FFFF99",
                                  "ssBGColor-CCFFCC","ssBGColor-CCFFFF","ssBGColor-99CCFF","ssBGColor-CC99FF","ssBGColor-FFFFFF"];
            this.applyCSS(cellArray,cssClass,removeCssClass);

        },this);

    },
    undoOperation : function(undoHistory, redoHistory){
        if (undoHistory.length > 0) {     	
            var rows=undoHistory.pop();        
            var row;
            var recordIndex;
            var record;
            var oldStyle=[];

            for(var i=0; i < rows.length;i++ ){
                row = rows[i];
                recordIndex = this.store.find(this.keyid,row.id);
                if(recordIndex != -1){
                    record = this.store.getAt(recordIndex);
                    oldStyle.push({
        				id:record.data[this.keyid],
                		cellStyle:Wtf.decode(Wtf.encode(record.data.cellStyle))
        			});
                    record.data.cellStyle=Wtf.decode(Wtf.encode(row.cellStyle));
                }else{
                	rows.splice(i,1);
                }
            }
            if(redoHistory.length == Wtf.UNDO_ARRAY_LENGTH){
            	redoHistory.shift();
            }
            redoHistory.push(oldStyle);
            
            this.view.refresh(false);
            this.saveStyleInDB(rows);
        }
        this.enableDisableUndoButt();
        this.enableDisableRedoButt();        
    },
//==============================================================================================================================//
    getSelectedCells : function() {
    	return this.selModel.getSelectedCells();
    },

    applyCSS : function(cellArray, newClassName, oldClassName){
    	var cn = oldClassName instanceof Array?oldClassName.join('|'):oldClassName;
    	var patt=new RegExp('(?:^|\\s+)(' + cn + ')(?:\\s+|$)', "g");
    	var oldStyle=[];
    	var tmp=[];
        for(var i=0 ; i< cellArray.length ; i++){
        	var r = this.store.getAt(cellArray[i][0]);
        	r.data.cellStyle = r.data.cellStyle || {};
        	var s = r.data.cellStyle[this.colModel.getColumnId(cellArray[i][1])];s=s||"";
        	if(s.indexOf(':')>=0)
        		s = newClassName;
        	else{
        		while(patt.test(s))s=s.replace(patt, " ");
        		if(newClassName){
        			var ncn = newClassName instanceof Array?newClassName.join(' '):newClassName;
        			s += " "+newClassName;
        		}
        	}
        	if(tmp.indexOf(r)<0){
        		tmp.push(r);       		
    			oldStyle.push({
    				id:r.data[this.keyid],
            		cellStyle:Wtf.decode(Wtf.encode(r.data.cellStyle))
    			});
        	}
        	r.data.cellStyle[this.colModel.getColumnId(cellArray[i][1])]=s;
        }

        if(this.undoHistory.length == Wtf.UNDO_ARRAY_LENGTH){
        	this.undoHistory.shift();
        }
        this.undoHistory.push(oldStyle);
        this.redoHistory = [];

        this.view.suspendEvents();
        this.store.fireEvent('datachanged', this.store);
        this.view.resumeEvents();
        this.selModel.syncView();
        for(var i=0;i<tmp.length;i++){
        	tmp[i]={
        		id:tmp[i].data[this.keyid],
        		cellStyle:Wtf.decode(Wtf.encode(tmp[i].data.cellStyle))
        	};
        }
        this.saveStyleInDB(tmp);
        
        this.enableDisableUndoButt();
        this.enableDisableRedoButt();
    },

    enableDisableUndoButt : function(){
        if (this.undoHistory.length > 0) {
            this.undoBut.setDisabled(false);
        }else{
            this.undoBut.setDisabled(true);
        }
    },
    enableDisableRedoButt : function(){
        if (this.redoHistory.length > 0) {
            this.redoBut.setDisabled(false);
        }else{
            this.redoBut.setDisabled(true);
        }
    },
    
    saveStyleInDB:function(sel) {
    	var moduleName = this.moduleName;
        var jsonstr = Wtf.encode(sel);
        if(jsonstr.length > 0){
            if(moduleName.indexOf('Activity') != -1 ){
                moduleName ='ActivityMaster';
            }else if( moduleName.indexOf('Archived') != -1 ){
                moduleName =moduleName.substring(0, index);
            }else if(moduleName.endsWith("Contact")){
                moduleName="Contact";
            }else if(moduleName.endsWith("Opportunity")){
                moduleName="Opportunity";
            }else if(moduleName.endsWith("Case")){
                moduleName="Case";
            }
            
            Wtf.Ajax.requestEx({
                url: "Common/Spreadsheet/saveModuleRecordStyle.do",
                params:{
                    jsondata : jsonstr,
                    module : moduleName,
                    action : 11
                }
            },
            this,
            function(res) {

            },
            function() {

            });
        }
    },
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    getGrid:function(){
        return this;
    },
    getSelModel:function(){
        return this.selModel;
    },

    getColModel:function(){
        return this.colModel;
    },

    getSpreadSheetLook:function(){
        return this.spreadSheetLook;
    },

    setMyConfig : function(data,headers){
        var columns;
    	var cm = this.getGrid().getColumnModel();
    	cm.suspendEvents();
        if(data.state && data.state.columns) {
            columns = data.state.columns||[];
            for(var i=0;i<columns.length;i++){
                var col = cm.getColumnById(columns[i].id);
                if(col) {
                    Wtf.apply(col,columns[i],{hidden:false});
                    var oldIndex = cm.getIndexById(columns[i].id);
                    if(oldIndex != i){
                        cm.moveColumn(oldIndex, i);
                    }
                }
            }
        }
    	for(var i=0;i<cm.config.length;i++){
			Wtf.each(headers.slice(0), function(h,x,arr){
				 var header=this.header;
				  var currency = header.trim().split("(");
	                var currency1;
	                if(currency.length>1)
	                    currency1= currency[1].split(")");
	                var indx=this.headerName.indexOf('(');
    			if(h.oldheader.trim() == this.headerName.trim()){
					if(currency[1]!=null){
						this.header = h.newheader+" ("+currency1[0]+")";
					}else{
					this.header = h.newheader.trim();
					}

					if(h.ismandotory){
						this.mandatory = h.ismandotory;
						this.header +=" *";
					} else {
                                            this.mandatory = false;
                                        }
					arr.splice(x,1);
					return false;
				}
			}, cm.config[i]);
    	}
    	cm.resumeEvents();
        if(columns){
            this.getGrid().reconfigure(this.getGrid().getStore(),this.getGrid().getColumnModel());
            var sort = data.state.sort;
            if(Wtf.ServerSideSort && sort && sort.xfield){
                GlobalSortModel[this.moduleName]=sort;
            }
        }
        WtfGlobal.setEmptyTextForQuickSearchField(this.quickSearchTF,GlobalQuickSearchEmptyText[this.moduleName]);

        this.getSpreadSheetLook().refresh(true);
    },

    getMyConfig : function(iscustomHeader){
        var module = this.moduleName;
        if(this.archivedParentName!=undefined){
            module=this.archivedParentName;
        }else{
            if(module.endsWith("Contact")){
                module="Contact";
            }
        }
        if(GlobalSpreadSheetConfig[module]) {
            new Wtf.util.DelayedTask(function() {
                try {
                    var res = GlobalSpreadSheetConfig[module];
                    var data = res.data[0];
                    var header = res.Header;
                    this.setMyConfig(data,header);
                    if(data.rules)
                        this.updateStylesheet(data.rules.rules);
                    if(res.data[1] && !iscustomHeader)
                        this.getReportMenu(res.data[1]);
                    this.moduleHeader = header;
                } catch (e) {
                    clog(e);
                }
            },this).delay(10);
        } else {
        Wtf.Ajax.requestEx({
            url: "Common/Spreadsheet/getSpreadsheetConfig.do",
            params:{
                action : 5,
                module : module
            }
        },
        this,
        function(res) {
            if(!GlobalSpreadSheetConfig[module]) {
                GlobalSpreadSheetConfig[module] = res;
                getQuickSearchEmptyText(res.Header,module);
            }
            var data = res.data[0];
            var header = res.Header;
            this.setMyConfig(data,header);
            if(data.rules)
                this.updateStylesheet(data.rules.rules);
            if(res.data[1] && !iscustomHeader)
                this.getReportMenu(res.data[1]);
            this.moduleHeader = header;
//            this.parentGridObj.bwrap.unmask();
        },
        function(res) {
//        	this.parentGridObj.bwrap.unmask();
        });
        }
    },
    updateStylesheet:function(rules){
    	Wtf.util.CSS.removeStyleSheet("ss_"+subDomain+"_"+this.moduleid);
    	this.view.rules=rules;
    	if(!(rules instanceof Array))return;
    	this.ruleCSS={
        	ruleText:"",
        	pre:"rule_"+subDomain+"_"+this.moduleid,
        	rules:{},
        	rulesTpl:{
        		0:function(patt){return function(value, rval, xtype){return rval&&rval.indexOf&&rval.indexOf(patt)>=0;};},
        		1:function(patt){return function(value, rval, xtype){return rval&&rval.indexOf&&rval.indexOf(patt)<0;};},
        		2:function(patt){return function(value, rval, xtype){return rval==patt;};},
        		3:function(patt){return function(value, rval, xtype){return xtype=="numberfield"&&value*1<patt;};},
        		4:function(patt){return function(value, rval, xtype){return xtype=="numberfield"&&value*1>patt;};},
        		5:function(patt){return function(value, rval, xtype){return xtype=="datefield"&&value.getTime&&value.getTime()<patt;};},
        		6:function(patt){return function(value, rval, xtype){return xtype=="datefield"&&value.getTime&&value.getTime()>patt;};}
        	}
        };

        Wtf.each(rules,this.setRulesCSS, this.ruleCSS);
        Wtf.util.CSS.createStyleSheet(this.ruleCSS.ruleText,"ss_"+subDomain+"_"+this.moduleid);
        this.view.refresh();
    },

    setRulesCSS:function(rule, i){
    	this.rules[this.pre+"_"+i]=this.rulesTpl[rule.combo](rule.search);
    	var tmp = "";
    	if(rule.bCheck==true){
    		tmp+="background-color:#"+rule.bgPanel+" !important;";
    	}
    	if(rule.tCheck==true){
    		tmp+="color:#"+rule.txtPanel+" !important;";
    	}
    	if(tmp.length>0)
    		this.ruleText+="."+this.pre+"_"+i+"{"+tmp+"}";
    },

    renderHeaders : function(){
        var cm = this.cm, ts = this.templates;
        var ct = ts.hcell;
        var cb = [], sb = [], p = {};
        for(var i = 0, len = cm.getColumnCount(); i < len; i++){
            p.id = cm.getColumnId(i);
            p.value = cm.getColumnHeader(i) || "";
            p.style = this.getColumnStyle(i, true);
            p.tip = cm.config[i].tip;
            if(cm.config[i].align == 'right'){
                p.istyle = 'padding-right:16px';
            }
            cb[cb.length] = ct.apply(p);
        }
        return ts.header.apply({
            cells: cb.join(""),
            tstyle:'width:'+this.getTotalWidth()+';'
        });
    },

    saveMyRuleHandler : function(ruleWin, rules, delFlag){
      Wtf.Ajax.requestEx2({
          url: "Common/Spreadsheet/saveSpreadsheetConfig.do",
          params:{
              action : 6,
              cid : this.ssDBid,
              module : this.moduleName,
              rules : (rules==""?rules:Wtf.encode({rules:rules}))
          },
          rules:rules,
	      scope:this,
	      success:function(res,data) {
                  delete GlobalSpreadSheetConfig[this.moduleName];
                  delete GlobalQuickSearchEmptyText[this.moduleName];
	          this.updateStylesheet(data.rules);
	      }
      });

  },

  updateSheetState : function(grid, state){
      for(var i = 0; i < state.columns.length; i++){
          if(state.columns[i].id=='checker'){
              state.columns[i].width=18;
          }
      }

      GlobalSortModel[this.moduleName]=state.sort;
      var module = this.moduleName;
      Wtf.Ajax.requestEx({
          url: "Common/Spreadsheet/saveSpreadsheetConfig.do",
          params:{
              action : 7,
              cid : this.ssDBid,
              module : module,
              state : Wtf.encode(state)
          }
      },
      this,
      function(res) {
          delete GlobalSpreadSheetConfig[this.moduleName];
          delete GlobalQuickSearchEmptyText[this.moduleName];
    	//TODO display error msg if can't save state
      },
      function(res) {
    	//TODO display error msg can't save state
      });
  },

  getCustomColumnData:function(rData,isMassordetails){
      jsondata = ",customfield:[]";
      var GlobalcolumnModel=GlobalColumnModel[this.moduleid];
      var isEmpty=true;
      if(GlobalcolumnModel){
          jsondata =',customfield:[{';
          for(var cnt = 0;cnt<GlobalcolumnModel.length;cnt++){
              var fieldname = GlobalcolumnModel[cnt].fieldname;
              var refcolumn_number = GlobalcolumnModel[cnt].refcolumn_number;
              var column_number = GlobalcolumnModel[cnt].column_number;
              var fieldid = GlobalcolumnModel[cnt].fieldid;
              var fieldtype = GlobalcolumnModel[cnt].fieldtype;
              if(isMassordetails&&(rData[fieldname]=="" || rData[fieldname]==undefined)){
                  continue;
              }
              if(cnt > 0){
                  jsondata +="},{";
              }
               var recData = "";
              if(fieldname.indexOf('.')>=0){
                  recData = rData[fieldname.replace(".","")];
              }else{
                  recData = rData[fieldname];
              }
              if(GlobalcolumnModel[cnt].fieldtype=="3" && recData!=""){
                  var daterec =recData;
                  if(recData!=undefined && recData!="" ){
                	  daterec =new Date(recData).getTime();
                  }
                  jsondata +="\"refcolumn_name\": \""+refcolumn_number+"\",\"fieldname\": \""+fieldname+"\",\""+column_number+"\": \""+daterec+"\",\""+fieldname+"\": \""+column_number+"\",\"filedid\":\""+fieldid+"\",\"xtype\":\""+fieldtype+"\"";
              }else if(GlobalcolumnModel[cnt].fieldtype=="5"){  // Time Field
                  if(recData!=undefined && recData!="" ){
                      recData =  recData;
                  }
                  jsondata +="\"refcolumn_name\": \""+refcolumn_number+"\",\"fieldname\": \""+fieldname+"\",\""+column_number+"\": \""+recData+"\",\""+fieldname+"\": \""+column_number+"\",\"filedid\":\""+fieldid+"\",\"xtype\":\""+fieldtype+"\"";
              }else
                  jsondata +="\"refcolumn_name\": \""+refcolumn_number+"\",\"fieldname\": \""+fieldname+"\",\""+column_number+"\": \""+recData+"\",\""+fieldname+"\": \""+column_number+"\",\"filedid\":\""+fieldid+"\",\"xtype\":\""+fieldtype+"\"";
          }
          jsondata +='}]';
      }
      return jsondata;
  },
  getCustomField:function(){
      var field = [];
      var colModelArray = GlobalColumnModel[this.moduleid];
      if(colModelArray) {
          for(var cnt = 0;cnt<colModelArray.length;cnt++) {
              field.push(colModelArray[cnt].fieldlabel);
          }
      }
      return field;
  },
  getCustomValues:function(rData){
      var values = [];
      var colModelArray = GlobalColumnModel[this.moduleid];
      if(colModelArray) {
          for(var cnt = 0;cnt<colModelArray.length;cnt++) {
              var colModelObj = colModelArray[cnt];
              if(colModelObj!=undefined){
                   var fieldname=colModelArray[cnt].fieldname.replace(".","");
                   var fieldid = colModelObj.fieldid;
                  if(GlobalComboStore["cstore"+fieldid]) {
                      if(colModelObj.fieldtype=="7"){ // for multiselet combo
                          var store = GlobalComboStore["cstore"+fieldid];
                          var pushValue=searchValueFieldMultiSelect(store,rData[fieldname],'id','name');

                          values.push(pushValue);
                      }
                      else {
                          store = GlobalComboStore["cstore"+fieldid];
                          var num = store.find("id", rData[fieldname]);
                          if(num != -1) {
                              values.push(store.getAt(num).get('name'));
                          } else {
                              values.push("");
                          }
                      }
                  } else {
                      values.push(rData[fieldname]);
                  }
              }
          }
      }
      return values;
  },

  getEmptyCustomFields:function(gridRec){
      var colModelArray = GlobalColumnModel[this.moduleid];
      if(colModelArray) {
           for(var cnt = 0;cnt<colModelArray.length;cnt++) {
               gridRec[colModelArray[cnt].fieldname]='';
           }
       }
       return gridRec;
   },

  getReportMenu : function(reports){
       var report;
       var reportArr = [];
       var temp;
       for(var i=0;i<reports.length;i++){
           report=reports[i];
           temp = new Wtf.Action({
               text: report.link,
               tooltip:{
                   text:report.tooltip
                   },
               iconCls:getTabIconCls(Wtf.etype.reportsMenuIcon),
               handler: function(e) {
                   this.openReportTab(parseInt(e.initialConfig.reportid));
               },
               scope: this,
               initialConfig : {
                 reportid : report.id
               }
           });
           reportArr.push(temp);
       }
       if(reportArr.length != 0){
           var tbar=this.getTopToolbar();
           var btnPos = tbar.items.length;
           if(tbar.items.items[tbar.items.length-1].iconCls=="helpButton")
               btnPos = tbar.items.length -1;
           else {
               tbar.add('->');
               btnPos++;
           }
           tbar.insertButton(btnPos-2,new Wtf.Toolbar.Button({
               iconCls:getTabIconCls(Wtf.etype.reports),
               tooltip: {text: WtfGlobal.getLocaleText({key:"crm.REPORTSBTN.ttip", params:[this.moduleName]})},//"Click to view reports related to "+this.moduleName+"."},
               scope: this,
               text:WtfGlobal.getLocaleText("crm.REPORTSBTN"),//"Reports",
               
               menu: reportArr
           }));
           tbar.insertButton(tbar.items.length-2,new Wtf.Toolbar.Separator());
       }
   },

   openReportTab :  function(name){
       var mainTab="";
       if(this.parentGridObj.subTab && this.parentGridObj.submainTab != undefined){
           mainTab=this.parentGridObj.submainTab;
       }else{
           mainTab=this.parentGridObj.mainTab;
       }
       var val=Object;
       val=getPaneldetails(name,mainTab.id,this.parentGridObj.initialConfig.Rrelatedto,this.parentGridObj.initialConfig.relatedtonameid);
       var title = Wtf.util.Format.ellipsis(val.title,18);
       var panel=Wtf.getCmp(val.id);


       if(panel==null) {
           panel= new Wtf.Panel({
               title:"<span wtf:qtip=\'"+val.tooltip+"\'>"+title+"</span>",
               id:val.id,
               layout:'fit',
               border:false,
               closable:true,
               iconCls:getTabIconCls(Wtf.etype.reports),
               items:new Wtf.AllReportTab({
                   scope:this,
                   head:val.head,
                   layout:'fit',
                   id:val.InnerID,
                   border:false,
                   stageflag:val.stageflag,
                   sourceflag:val.sourceflag,
                   details:val

               })
           });
           mainTab.add(panel);
       }
       mainTab.setActiveTab(panel);
       mainTab.doLayout();
   },

   checkRecordEditable:function(e){
	   if(e.record.get(Wtf.SpreadSheetGrid.VALID_KEY)==Wtf.SpreadSheetGrid.STATUS_PROCESSING){
		   e.cancel=true;
	   }
   },

   validateRecord:function(e){
	   if(e.row==0 && e.value.trim && e.value.trim()=="")
		   e.cancel=true;
       if(typeof e.value=="string" ){
           e.value = WtfGlobal.HTMLStripper(e.value);
       }
   },

    applyCacheStores : function(e) {
		for ( var field in this.comboStoreCache) {
			if (e.field == field) {
				var combo = this.comboStoreCache[field];
				var rec = combo.store.getAt(combo.store.find(combo.valueField, e.value));
				e.record.data[combo.comboFieldDataIndex] = rec.get(combo.displayField);
                e.originalOldId = combo.originalOldId;
				break;
			}
		}
	},

   completeEditRecord:function(e){
	   this.applyCacheStores(e);
	   if(e.row == 0){
		   var count = this.store.getCount();
	       if(this.pagingFlag&&count>this.getBottomToolbar().pageSize){
	    	   count--;
	    	   this.store.remove(this.store.getAt(count));
	       }
	       this.store.totalLength++;
		   this.insertNewRecord();

	       if(this.pagingFlag)
	    	   this.pP.updatePagingMsg(count, this.store.getTotalCount());
	   }
	   var Record = this.store.reader.recordType,f = Record.prototype.fields, fi = f.items, fl = f.length;
	   var values = {},customMapping={};
	   for(var j = 0; j < fl; j++){
           f = fi[j];
           if(!Wtf.isEmpty(f.customMapping)){
        	   customMapping[f.name]=f.customMapping;
           }
           if(!Wtf.isEmpty(f.defValue))
        	   values[f.name]=f.convert((typeof f.defValue == "function"?f.defValue.call():f.defValue));
	   }
	   this.applyValuesIf(e.record, values);
	   this.markRecordValidation(e.record);
       var originalVal = e.originalValue;
       if(e.originalOldId)
           originalVal = e.originalOldId;
	   var auditStr=Wtf.SpreadSheet.constructAuditStr(this,e.row, e.column, e.value, originalVal,e.record);
	   this.saveRecord(e.record,auditStr,customMapping,e.field);
   },

    markRecordValidation:function(rec){
	    var cm = this.colModel.config;
        for(var i=0;i<cm.length;i++){
            if(cm[i].mandatory){
        	    var val = rec.data[cm[i].dataIndex];
                if(Wtf.isEmpty(val)||(val.trim&&val.trim()=="")||(cm[i].xtype=="combo"&&val=="99")){
            	    rec.set(Wtf.SpreadSheetGrid.VALID_KEY,Wtf.SpreadSheetGrid.STATUS_INVALID);
                    return;
                }
            }
        }
        rec.set(Wtf.SpreadSheetGrid.VALID_KEY,Wtf.SpreadSheetGrid.STATUS_VALID);
    },

    applyValuesIf:function(record,values){
        for(var key in values){
    	    record.set(key,Wtf.value(record.get(key),values[key]));
        }
    },

   insertNewRecord:function(){
	   if(!this.allowedNewRecord)
		   return;
	   var Record = this.store.reader.recordType,f = Record.prototype.fields, fi = f.items, fl = f.length;
	   var values = {},blankObj={};
	   for(var j = 0; j < fl; j++){
           f = fi[j];
           blankObj[f.name]='';
           if(!Wtf.isEmpty(f.newValue))
        	   values[f.name]=f.convert((typeof f.newValue == "function"?f.newValue.call():f.newValue));
       }
	   blankObj[Wtf.SpreadSheetGrid.VALID_KEY]=null;
	   blankObj[this.keyid]=Wtf.SpreadSheetGrid.NEW_ID;
	   var rec = new Record(blankObj);
	   this.store.insert(0, rec);
	   rec.beginEdit();
	   this.applyValuesIf(rec,values);
	   rec.endEdit();
   },

    saveRecord:function(record, auditStr, customMapping,field){
	    var changedRec = record.getChanges();
        for(var key in changedRec){
            if(changedRec[key].getTime){
        	    changedRec[key] = changedRec[key].getTime();
            }
            changedRec[Wtf.value(customMapping[key],key)] = changedRec[key];
        }
        changedRec[Wtf.SpreadSheetGrid.AUDIT_KEY]=auditStr;
        changedRec[this.keyid]=record.data[this.keyid];
        changedRec[Wtf.SpreadSheetGrid.CUSTOM_KEY]=Wtf.decode(this.getCustomColumnData(record.data).substring(13));
        e = {record:record,json:changedRec,url:this.updateURL};
        if(this.fireEvent('beforeupdate',e)===false)
        	return;

        var valid = record.get(Wtf.SpreadSheetGrid.VALID_KEY);
        record.set(Wtf.SpreadSheetGrid.VALID_KEY,Wtf.SpreadSheetGrid.STATUS_PROCESSING);
        changedRec[Wtf.SpreadSheetGrid.VALID_KEY]=valid;
        changedRec["dirtyfield"]=field;
	    Wtf.Ajax.requestEx2({
	        url : e.url,
	        params : {
                jsondata:Wtf.encode(changedRec),
                type:this.newFlag,
                flag:20
            },
	        success : this.onAjaxSuccess,
	        failure : this.onAjaxFailure,
	        scope : this,
	        record:record,
	        valid:valid
	    });
    },

    onAjaxSuccess:function(resp, data){
    	if(resp.ID!=undefined){
    		data.record.set(this.keyid,resp.ID);
    		data.record.set(Wtf.SpreadSheetGrid.VALID_KEY,data.valid);
    		data.record.set("totalcomment",0);
            if(resp[Wtf.SpreadSheetGrid.AUTOCUSTOM_KEY]) {
                var autoNoArray = resp[Wtf.SpreadSheetGrid.AUTOCUSTOM_KEY];
                for(var cnt=0; cnt<autoNoArray.length; cnt++) {
                    for (var key1 in autoNoArray[cnt]) {
                        data.record.set(key1,autoNoArray[cnt][key1]);
                    }
                }
            }
    		data.record.commit();
    		this.fireEvent('afterupdate',resp,data);
    	}else{
    		data.record.reject();
    	}
    },
	onAjaxFailure:function(resp, data){
    	WtfComMsgBox(12,1);
    	data.record.reject();
    }
});

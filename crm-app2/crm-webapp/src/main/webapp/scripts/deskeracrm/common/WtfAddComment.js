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
Wtf.AddComment = function(config) {
    Wtf.apply(this, config);

    Wtf.AddComment.superclass.constructor.call(this,{
        title :  this.mode=="Edit"? WtfGlobal.getLocaleText("crm.common.editComment"):WtfGlobal.getLocaleText("crm.activitydetailpanel.addCommentBTN"),//"Add Comment" ,
        closable : true,
        modal : true,
        iconCls : 'pwnd favwinIcon',
        width : 570,
        height: 490,
        resizable :false,
        buttonAlign : 'right',
        buttons :[{
            text : this.mode=="Edit"? WtfGlobal.getLocaleText("crm.common.editComment"):WtfGlobal.getLocaleText("crm.activitydetailpanel.addCommentBTN"),
            scope : this,
            handler:function(){
                var jsondata ={};
                var commentid = this.commentId!=undefined?this.commentId:"";
                this.Comment.syncValue()
                var str = this.Comment.getValue();
                if(str==""){
                    ResponseAlert(101)
                    return;
                }
                jsondata = {
					leadid : this.recid,
					mapid : this.mapid,
					commentid : commentid,
					comment : str
				};
                var commentStr=Wtf.encode(jsondata);
                Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.common.addingcommentloadmsg"));//"Adding comment...");
                Wtf.Ajax.requestEx({
                    url:"Common/Comment/addComments.do",
                    params:{
                        jsondata:commentStr,
                        modulename:this.moduleName   //required for comet request
                    }
                },this,
                function(res) {
                    this.close();
                    if(!this.isDetailPanel){
                        var selected = this.selectedRec;
                        var rowindex = this.grid.getStore().indexOf(selected);
                        var colindex = this.grid.getColumnModel().getColumnCount();
                        var cell = this.grid.getView().getCell(rowindex, colindex-1);
                        if(this.mode=="Add"){
                            var c = 0;
                            if(selected.get("totalcomment")!=undefined){
                                c = selected.get("totalcomment");
                            }
                            var count = parseInt(c, 10)+1;
                            selected.set("totalcomment",count);
                            ResponseAlert(44);
                        
                        }else
                        	ResponseAlert(93);  
                        this.grid.getSelectionModel().selectRow(rowindex);
                    }
                    var obj=null;
                    switch(this.moduleName){
                    case Wtf.crmmodule.lead:obj=Wtf.getCmp(Wtf.moduleWidget.lead);break;
                    case Wtf.crmmodule.campaign:obj=Wtf.getCmp(Wtf.moduleWidget.campaign);break;
                    case Wtf.crmmodule.account:obj=Wtf.getCmp(Wtf.moduleWidget.account);break;
                    case Wtf.crmmodule.contact:obj=Wtf.getCmp(Wtf.moduleWidget.contact);break;
                    case Wtf.crmmodule.contact:obj=Wtf.getCmp(Wtf.moduleWidget.contact);break;
                    case Wtf.crmmodule.opportunity:opportunity=Wtf.getCmp(Wtf.moduleWidget.opportunity);break;
                    case Wtf.crmmodule.cases:obj=Wtf.getCmp(Wtf.moduleWidget.cases);break;
                    case Wtf.crmmodule.activity:obj=Wtf.getCmp(Wtf.moduleWidget.activity);break;
                    case Wtf.crmmodule.topactivity:obj=Wtf.getCmp(Wtf.moduleWidget.topactivity);break;
                    case Wtf.crmmodule.product:obj=Wtf.getCmp(Wtf.moduleWidget.product);break;
                    }if(obj!=null){
                    	 obj.callRequest("","",0);
                    } 
                    Wtf.updateProgress()
                    Wtf.refreshUpdatesAll();                 
                    getDocsAndCommentList(this.selectedRec, this.keyid,this.idX, true,this.moduleName,this.profileFlag,'email');
                }, 
                function(res) {
                    Wtf.updateProgress();
                    ResponseAlert(45);
                });
            }
               
        },{
            text : WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
            scope : this,
            handler : function() {
                this.close();
            }
        }],
        layout : 'border',
        items :[{
            region : 'north',
            height : 75,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html :getTopHtml(WtfGlobal.getLocaleText("crm.lead.defaultheader.comment"),this.mode=="Edit"?WtfGlobal.getLocaleText("crm.common.editComment"):WtfGlobal.getLocaleText("crm.activitydetailpanel.addCommentBTN"),"../../images/comment.gif")
        },
            this.createCourseForm = new Wtf.form.FormPanel({
                baseCls: 'x-plain',
                region : 'center',
                border : false,
                layout:'fit',
                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 20px 20px 20px;',
                lableWidth : 150,
                autoScroll:false,
                defaultType: 'textfield',
                items : [
                this.Comment = new Wtf.newHTMLEditor({
                    border: false,
                    enableLists: false,
                    enableSourceEdit: false,
                    enableAlignments: true,
                    hiddenflag:false,
                    hideLabel: true
                    
                })]
            })]
    });
    if(this.mode=="Edit" && this.comment!=undefined){
        this.Comment.setValue(this.comment);
    }

}
 
Wtf.extend(Wtf.AddComment, Wtf.Window, {

    });



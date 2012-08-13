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
Wtf.ActivityDetailPanel=function(config)
    {
        config.id=config.id2+'CRMupdownCompo';
        config.panTitle = "<div class='dpanellinks' id='topPanel"+config.id2+"'> &nbsp; &nbsp; &nbsp;\n\
                           <a class='CRMdetailPanel' href='#gotoDocuments"+config.id2+"' > <img src='../../images/document12.gif' class='imgMidVA'/></a> \n\
                           <a class='CRMdetailPanel' href='#gotoDocuments"+config.id2+"' wtf:qtip='View uploaded files for the selected "+config.moduleName+"'> "+WtfGlobal.getLocaleText("crm.DOCUMENTS")+"</a>  &nbsp; &nbsp; &nbsp; &nbsp;  \n\
                           <a class='CRMdetailPanel' href='#gotoComments"+config.id2+"' > <img  src='../../images/comment12.gif' class='imgMidVA'/></a> \n\
                           <a class='CRMdetailPanel' href='#gotoComments"+config.id2+"' wtf:qtip='View comments for the selected "+config.moduleName+"'> "+WtfGlobal.getLocaleText("crm.otherdetail.comments")+"</a>   &nbsp; &nbsp; &nbsp; &nbsp;  \n\
                           <a class='CRMdetailPanel' href='#gotoActivity"+config.id2+"' > <img src='../../images/recent-update12.gif' class='imgMidVA'/></a> \n\
                           <a class='CRMdetailPanel' href='#gotoActivity"+config.id2+"' wtf:qtip='Track recent activities associated with the selected "+config.moduleName+"'>"+ WtfGlobal.getLocaleText("crm.otherdetail.recentactivity")+"</a> &nbsp; &nbsp; \n\
                           </div> &nbsp;<br> <p><hr></p>";
        if(config.moduleName == 'Account' && Wtf.viewProject){
            config.panAccProjectsTitle = "<hr style='margin-left:3%;' color='#CFCFCF;'><div style='margin:2% 2% 2% 3%'><span class='dpTitleHead'> <img src='../../images/Notes16.gif' class='imgMidVA'/> "+ WtfGlobal.getLocaleText("crm.detailpanel.projects")+" :  </span><br></div>";
            config.newAccProjectsWithPerm = "<div style='margin-left:4%'>  {projectnames} </div> <br/>";
            if(Wtf.createProject) {
               config.noAccProjectsWithPerm ="<div style='margin-left:4%;color:#000000;'> <span id='{msgDiv}' style='height:auto;display:block;overflow:auto;'>"+ WtfGlobal.getLocaleText("crm.detailpanel.projects.noprojectmsg")+" <a href=\"#\" class='linkCls'  onclick='addAccProjects(\""+config.id+"\")'>"+ WtfGlobal.getLocaleText("crm.detailpanel.projects.addprojectmsg")+"</a></span></div><br><br>";
            }
            config.noAccProjects = "<div style='margin-left:4%;color:#000000;'> <span id='{msgDiv}' style='height:auto;display:block;overflow:auto;'>"+ WtfGlobal.getLocaleText("crm.detailpanel.projects.noprojectmsg")+"</span></div>";
        }
        if((config.moduleName=="Account" ||config.moduleName=="Opportunity" ||config.moduleName=="Lead") && config.contactsPermission) {
            config.panContactsTitle = "<hr style='margin-left:3%;' color='#CFCFCF;'><div style='margin:2% 2% 2% 3%'><span class='dpTitleHead'> <img src='../../images/contactimage.png' class='imgMidVA'/>  "+ WtfGlobal.getLocaleText("crm.CONTACT.plural")+" :  </span><br></div>";
            config.newContactsWithPerm = "<div style='margin-left:4%;color:#000000;  !important;'>  {contacts} </div> <br/>";
            config.noContacts = "<div style='margin-left:4%;color:#000000;'> <span id='{msgDiv}' style='height:auto;display:block;overflow:auto;'>"+ WtfGlobal.getLocaleText("crm.detailpanel.addcontactnocontact")+"</span></div>";
        }
        config.panDocTitle = "<hr style='margin-left:3%;' color='#CFCFCF;'><div style='margin:2% 2% 2% 3%'><span class='dpTitleHead'> <img src='../../images/document12.gif' class='imgMidVA'/> "+ WtfGlobal.getLocaleText("crm.detailpanel.uploadedfile.title")+"</span><br></div>";
        config.panCommTitle = "<br><hr style='margin-left:3%;' color='#CFCFCF;'><br><div style='margin:3px 2% 2% 3%'><span class='dpTitleHead'> <img src='../../images/comment12.gif' class='imgMidVA'/>  "+ WtfGlobal.getLocaleText("crm.otherdetail.comments")+" :  </span><br></div>";
        config.panAuditTitle = "<br><hr style='margin-left:3%;'color='#CFCFCF;'><br><div style='margin:3px 2% 2% 3%'><span class='dpTitleHead'> <img src='../../images/recent-Activity.gif' class='imgMidVA'/>"+WtfGlobal.getLocaleText("crm.otherdetail.recentactivity")+" :  </span><br></div>";
        config.panEmailTitle = "<br><hr style='margin-left:3%;' color='#CFCFCF;'><br><div style='margin:3px 2% 2% 3%'><span class='dpTitleHead'> <img src='../../images/email.png' class='imgMidVA'/>  "+WtfGlobal.getLocaleText("crm.otherdetail.recentemails")+" :  </span><br></div>";

        config.newDocContentWithPerm = "<div style='margin-left:4%'><span class='dpGray'> {srno})\n\
                 <a style=\"color:#000000;font-weight:bold; !important;\"  href='javascript:void(0)' title='Click to Download {Name} ' onclick='setDldUrl(\"crm/common/Document/downloadDocuments.do?url={docid}&mailattch=true&dtype=attachment\")'>{Name}</a>&nbsp;\n\
                 <a href='javascript:void(0)' title='Click to Download {Name} ' onclick='setDldUrl(\"crm/common/Document/downloadDocuments.do?url={docid}&mailattch=true&dtype=attachment\")'> <img src='../../images/download12.gif' class='imgMidVA'/> </a> \n\
                 {deleteimg} ( {Size} ) : Uploaded by <span style=\"color:#000000; !important;\">{uploadedby}</span> , on {uploadedon} </span> </div> <br><br> ";
        config.newDocContentWithNoPerm = "<div style='margin-left:4%'><span class='dpGray' > {srno}) <span style=\"color:#000000;font-weight:bold; !important;\"  > {Name} </span> ( {Size}K ) : Uploaded by <span style=\"color:#15428B;  !important;\">{uploadedby}</span> , on {uploadedon} </span> </div> <br><br> ";
        config.newCommContentWithPerm = "<br/><span style=\"color:#15428B;  !important;padding-left:24px;\"> {addedby}</span> <span style=\"color:gray !important;\"> on {postedon} </span>:{deleteimg} {comment}<br/>";
        config.newAuditContentWithPerm = "<img src={imgsrc} height={height} width={width} style='margin-bottom:-{marginbottom}px;margin-left:4%'><div style='padding-left:6.5%'><span class='dpGray'> {action} by <span style=\"color:#000000;  !important;\">{user}</span> on {[WtfGlobal.dateTimeRendererTZ(new Date(values.time))]} , Details : </span> {details} </div> <br>";
        config.newEmailContentWithPerm = "<img src='{imgsrc}' wtf:qtip='{folder}' height='15px' width='15px' style='margin-bottom:-15px;margin-left:4%'><div style='padding-left:7%'><span class='dpGray'> <a  href='javascript:void(0)' onclick='emailDetail(\"{folder}\",\"{docid}\",\"{subject}\",\"{fromaddr}\",\"{toaddr}\",\"{senddate}\",\"{ie_id}\")'><span style=\"color:#15428B;  !important;\">{subject}</span></a> , on {time} , Details : </span> {details} </div> <br>";
        config.noPerm = "<div style='margin-left:4%;color:#000000;'> <div id='{msgDiv}' style='height:auto;display:block;overflow:auto;'>"+ WtfGlobal.getLocaleText("crm.otherdetails.insufficientpermissiontoview")+"</div></div><br><br>";
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Document, Wtf.Perm.Document.doc_up)) {
            config.noDoc = "<div style='margin-left:4%;color:#000000;'> <span id='{msgDiv}' style='height:auto;display:block;overflow:auto; '>"+ WtfGlobal.getLocaleText("crm.detailpanel.uploadfiles.nodocmsg")+"<a href=\"#\" class='linkCls'  onclick='callAddDocument(\""+config.id+"\")'>"+ WtfGlobal.getLocaleText("crm.otherdetails.adddocnowmsg")+"</a></span></div><br><br>";
        } else{
            config.noDoc = "<div style='margin-left:4%;color:#000000;'> <span id='{msgDiv}' style='height:auto;display:block;overflow:auto; '>"+ WtfGlobal.getLocaleText("crm.detailpanel.uploadfiles.nodocmsg")+"</span></div>";
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Comments, Wtf.Perm.Comments.comment_a)) {
            config.noComment = "<div style='margin-left:4%;color:#000000;'> <span id='{msgDiv}' style='height:auto;display:block;overflow:auto;'>"+ WtfGlobal.getLocaleText("crm.otherdetails.nocommentsaddedmsg")+" <a href=\"#\" class='linkCls'  onclick='callAddComment(\""+config.id+"\")'>"+ WtfGlobal.getLocaleText("crm.otherdetails.adddocnowmsg")+"</a></span></div><br><br>";
        } else {
            config.noComment = "<div style='margin-left:4%;color:#000000;'> <span id='{msgDiv}' style='height:auto;display:block;overflow:auto;'>"+ WtfGlobal.getLocaleText("crm.otherdetails.nocommentsaddedmsg")+"</span></div>";
        }

        config.noActivity = "<div style='margin-left:4%;color:#000000;'> <span id='{msgDiv}' style='height:auto;display:block;overflow:auto;'>"+ WtfGlobal.getLocaleText("crm.otherdetails.noactivitydonemsg")+"</span></div><br><br>";
        config.noEmail = "<div style='margin-left:4%;color:#000000;'> <span id='{msgDiv}' style='height:auto;display:block;overflow:auto;'>"+ WtfGlobal.getLocaleText("crm.otherdetails.noemailmsg")+"</span></div><br><br>";
        config.selectValid = "<div style='margin-left:4%;height:90%;width:90%;'> <div id='{msgDiv}' style='height: auto;display:block;overflow:auto;'>"+ WtfGlobal.getLocaleText("crm.otherdetails.slevalrecmsg")+"</div></div>";
        config.Failed = "<div style='margin-left:4%;color:red;'> <div id='{msgDiv}' style='height:auto;display:block;overflow:auto;'>"+ WtfGlobal.getLocaleText("crm.otherdetails.failedconnectionmsg")+"</div></div>";

 Wtf.ActivityDetailPanel.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.ActivityDetailPanel,Wtf.Panel,{
    layout: "fit",
    commentFailed : "<div style='margin:3px;height:20%;width:90%;'><div id='{msgDiv}' style='height: auto;display:block;overflow:auto; margin-left:150px;'>"+ WtfGlobal.getLocaleText("crm.otherdetails.failtoloadcomments")+"</div></div>",
    docsFailed : "<div style='margin:3px;height:20%;width:90%;'><div id='{msgDiv}' style='height: auto;display:block;overflow:auto; margin-left:150;'>"+ WtfGlobal.getLocaleText("crm.otherdetails.failtoloaddocs")+"</div></div>",
    initialMsg : "<div style='margin:3px;height:90%;width:90%;'><div id='{msgDiv}' style='height: auto;display:block;overflow:auto; margin-left:150px;'>"+ WtfGlobal.getLocaleText("crm.otherdetails.slevalrecmsg")+"</div></div>",
    noPerm_up: "<span style=\"color:#000000; font-weight:bold; !important;\"> "+ WtfGlobal.getLocaleText("crm.detailpanel.uploadedfile.title")+"</span><br><br><div style='margin:3px;height:20%;width:20%;'><div id='{msgDiv}' style='height: auto;display:block;overflow:auto; margin-left:10px;'>"+ WtfGlobal.getLocaleText("crm.otherdetails.insufficientpermissiontoview")+"</div></div>",
    noPerm_com: "<span style=\"color:#000000; font-weight:bold; !important;\">  "+ WtfGlobal.getLocaleText("crm.otherdetail.comments")+" :  </span><br><br><div style='margin:3px;height:20%;width:20%;'><div id='{msgDiv}' style='height: auto;display:block;overflow:auto; margin-left:10px;'>"+ WtfGlobal.getLocaleText("crm.otherdetails.insufficientpermissiontoview")+"</div></div>",

    initEvents : function(){
        Wtf.ActivityDetailPanel.superclass.initEvents.call(this);
        this.body.on('click', this.onClick, this);
    },

    onRender: function(config){
        Wtf.ActivityDetailPanel.superclass.onRender.call(this,config);
        this.toolItems = new Array();
        this.messagePanelContentTemplate = new Wtf.Template("");
        var moduleName="";
        if(this.moduleName=="Account" ||this.moduleName=="Opportunity" ||this.moduleName=="Activity")
            moduleName="an "+this.moduleName;
        else
            moduleName="a "+this.moduleName;

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Document, Wtf.Perm.Document.doc_up)) {
          this.toolItems.push(this.document = new Wtf.Toolbar.Button({

            text:WtfGlobal.getLocaleText("crm.activitydetailpanel.addfilesBTN"),//'Add Files',
            id: "addfiles"+this.moduleName,
            iconCls: 'pwnd doctabicon',
            scope: this,
            tooltip: {text: WtfGlobal.getLocaleText({key:"crm.activitydetailpanel.addfilesBTN.ttip", params:[moduleName]})},//'Select '+moduleName+' to add files.'},
            handler : function() {
                this.Addfiles();
            }
          })
          );
        }

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Comments, Wtf.Perm.Comments.comment_a)) {
          this.toolItems.push(this.comment=new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("crm.activitydetailpanel.addCommentBTN"),//"Add Comment",
            id:"comment"+this.moduleName,
            pressed: false,
            scope : this,
            tooltip: {text: WtfGlobal.getLocaleText({key:"crm.activitydetailpanel.addCommentBTN.ttip", params:[moduleName]})},// 'Select '+moduleName+' to add comments.'},
            iconCls:'pwnd addcomment',
            handler : function() {
                this.addComment();
            }
          })
          );
        }
        this.detailtoolbar=new Wtf.Toolbar({
            items:this.toolItems
        });

        this.center = {
            region:'center',
            layout:'fit',
            border: false,
            id:this.id2+"dloadpanelcenter",
            margins:'0 5 0 15',
            html: this.messagePanelContentTemplate.applyTemplate({
                msgDiv: "msgDiv_"
            })
        };

        this.dloadpanel= new Wtf.Panel({
            id:this.id+"downloadpanel",
            closable: true,
            split: true,
            border: false,
//            bbar:this.detailtoolbar,
            bodyStyle: "background:#FFFFFF;",
            layout: "fit",
            items: [this.center]
        });

        this.add(this.dloadpanel);
        this.disableButt();
    },
    enableButt:function(){
        if(!this.detailPanelFlag) {
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.Document, Wtf.Perm.Document.doc_up)) {
              this.document.enable();
            }
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.Comments, Wtf.Perm.Comments.comment_a)) {
              this.comment.enable();
            }
        }
    },

    disableButt:function(){
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Document, Wtf.Perm.Document.doc_up)) {
          this.document.disable();
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Comments, Wtf.Perm.Comments.comment_a)) {
          this.comment.disable();
        }
    },

    addComment:function(commentId,comment){
            var mode = "Add";
            if(commentId!=undefined){
                mode = "Edit";
            }
            var commentWin = new Wtf.AddComment({
                idX:this.id2,
                mode: mode,
                comment : comment,
                commentId : commentId,
                grid:this.grid,
                recid:this.selectedRec.get(this.keyid),
                keyid:this.keyid,
                mapid:this.mapid,
                store:this.Store,
                detailFlag:this.detailFlag,
                moduleName:this.moduleName,
                profileFlag:this.profileFlag,
                selectedRec:this.selectedRec,
                isDetailPanel:this.isDetailPanel
            });
            commentWin.show();
    },

    addComments:function(dat){
        var commentWin = new Wtf.AddComment({
            idX:dat.id,
            grid:dat.grid,
            recid:dat.recid,
            keyid:dat.keyid,
            mapid:dat.mapid,
            store:dat.store,
            record:dat.record,
            detailFlag:this.detailFlag,
            moduleName:this.moduleName,
            profileFlag:this.profileFlag
        });
        commentWin.show();
    },

    deleteComment : function(commentId) {
        var s=this.grid.getSelectionModel().getSelections();
        if(s.length==1) {
            this.chkModule();
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),WtfGlobal.getLocaleText("crm.confirmdeletemsg") , function(btn){
                if (btn == "yes") {
                    Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.common.deletingcommentmsg"));//"Deleting comment...");
                    Wtf.Ajax.requestEx({
                        url:"Common/Comment/deleteOriginalComment.do",
                        params:{
                            id:commentId,
                            moduleName:this.moduleName
                        }
                    },this,
                    function(res) {
                        // update total count
                        var selected = this.selectedRec;
                        var rowindex = this.grid.getStore().indexOf(selected);
                        var colindex = this.grid.getColumnModel().getColumnCount();
                        var cell = this.grid.getView().getCell(rowindex, colindex-1);
                        var c = 0;
                        if(selected.get("totalcomment")!=undefined){
                            c = selected.get("totalcomment");
                        }
                        var count = parseInt(c, 10)-1;
                        selected.set("totalcomment",count);
                        this.grid.getSelectionModel().selectRow(rowindex);
                        // end

                        Wtf.updateProgress();
                        ResponseAlert(89);
                        getDocsAndCommentList(this.selectedRec, this.keyid,this.id2, true,this.moduleName,this.profileFlag,'email');
                    },
                    function(res) {
                        Wtf.updateProgress();
                        ResponseAlert(90);
                    });


                }
            },this);

        } else {
            WtfComMsgBox(400,0);
        }
    },
    deleteDocument : function(documentId,documentName) {
        var s=this.grid.getSelectionModel().getSelections();
        if(s.length==1) {
            this.chkModule();
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), WtfGlobal.getLocaleText("crm.confirmdeletemsg"), function(btn){
                if (btn == "yes") {
                    Wtf.commonWaitMsgBox("Deleting document...");
                    Wtf.Ajax.requestEx({
                        url:"crm/common/Document/deleteDocumentFromModule.do",
                        params:{
                    	docid:documentId,
                    	recid:this.selRecordid,
                        mapid:this.mapid,
                        docName:documentName
                            
                        }
                    },this,
                    function(res) {
                        // update total count
                        var selected = this.selectedRec;
                        var rowindex = this.grid.getStore().indexOf(selected);
                        var obj=null;
                        switch(this.moduleName){
                        case Wtf.crmmodule.lead:obj=Wtf.getCmp(Wtf.moduleWidget.lead);break;
                        case Wtf.crmmodule.campaign:obj=Wtf.getCmp(Wtf.moduleWidget.campaign);break;
                        case Wtf.crmmodule.account:obj=Wtf.getCmp(Wtf.moduleWidget.account);break;
                        case Wtf.crmmodule.contact:obj=Wtf.getCmp(Wtf.moduleWidget.contact);break;
                        case Wtf.crmmodule.opportunity:opportunity=Wtf.getCmp(Wtf.moduleWidget.opportunity);break;
                        case Wtf.crmmodule.cases:obj=Wtf.getCmp(Wtf.moduleWidget.cases);break;
                        case Wtf.crmmodule.activity:obj=Wtf.getCmp(Wtf.moduleWidget.activity);break;
                        case Wtf.crmmodule.topactivity:obj=Wtf.getCmp(Wtf.moduleWidget.topactivity);break;
                        case Wtf.crmmodule.product:obj=Wtf.getCmp(Wtf.moduleWidget.product);break;
                        }if(obj!=null){
                        	 obj.callRequest("","",0);
                        } 
                        Wtf.refreshUpdatesAll();   
                        Wtf.updateProgress();
                        ResponseAlert(94);
                        getDocsAndCommentList(this.selectedRec, this.keyid,this.id2, true,this.moduleName,this.profileFlag,'email');
                    },
                    function(res) {
                        Wtf.updateProgress();
                        ResponseAlert(95);
                    });

                    
                }
            },this);
            
        } else {
            WtfComMsgBox(400,0);
        }
    },
    
    addAccProjects :  function(){
         var projectwin = new Wtf.AddAccProject({
                idX:this.id2,
                grid:this.grid,
                recid:this.selectedRec.get(this.keyid),
                keyid:this.keyid,
                store:this.Store,
                module:this.moduleName,
                selectedRec:this.selectedRec,
                isDetailPanel:this.isDetailPanel
         });
         projectwin.show();
    },
    Addfiles:function() {
        var uploadDocWin = new Wtf.UploadFile({
            idX:this.id2,
            grid:this.grid,
            recid:this.selectedRec.get(this.keyid),
            keyid:this.keyid,
            mapid:this.mapid,
            scope:this,
            detailFlag:this.detailFlag,
            moduleName:this.moduleName,
            profileFlag:this.profileFlag,
            selectedRec:this.selectedRec,
            isDetailPanel:this.isDetailPanel
        });
        uploadDocWin.show();
    }, 
    chkModule:function() {
        var record = this.grid.getSelectionModel().getSelected();
        this.selRecordid=record.get(this.keyid);
    },
    onClick: function(e, target){
            
            if(target.className == 'CRMdetailPanel'){
                e.stopEvent();
                this.scrollToSection(target.href.split('#')[1]);
            } else if(target.parentNode.className == 'CRMdetailPanel') {
                e.stopEvent();
                this.scrollToSection(target.parentNode.href.split('#')[1]);
            }
    },
    scrollToSection : function(id){
        var el = Wtf.getDom(id);
        if(el){
            var body = Wtf.getCmp(this.center.id).body;
            var top = (Wtf.fly(el).getOffsetsTo(body)[1]) + body.dom.scrollTop;
            body.dom.scrollTop = top-15;
            Wtf.fly(el).next('span').pause(.2).highlight('#8DB2E3', {
                attr:'color'
            });
        }
    }

});

function callAddComment(compoId) {
    Wtf.getCmp(compoId).addComment();
}

function callAddDocument(compoId){
    Wtf.getCmp(compoId).Addfiles();
}
function addAccProjects(compoId){
    Wtf.getCmp(compoId).addAccProjects();
}

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
function getDocsAndCommentList(selected, keyid, id, refresh,module, loadMask, email,ownerid,contactsPermission,selectionLengthFlag)
{
    var moduleObj=Wtf.getCmp(id);
    var commentlist="";
    if(selected) {
        //var selected = grid.getSelectionModel().getSelected();
        var Recid = selected.get(keyid);
        var emailid = selected.get(email);
        var updownCompE = Wtf.getCmp(id+'CRMupdownCompo');
        var enableContactsButton=true;
        var isMainOwner = false;
        var valid = selected.get('validflag');
        if(selectionLengthFlag==undefined || selectionLengthFlag==0){
            docCommentEnable(moduleObj,updownCompE,isMainOwner,enableContactsButton);
        }
       // if(valid==1){
            
            var mapid = Recid;
            if(module == 'Lead' || module == 'Opportunity' || module =="Account" || module=="Contact" ){
                if(selected.get(ownerid) == loginid){
                    isMainOwner = true;
                }
                if(module == "Opportunity"){
                    mapid=selected.get('accountnameid');
                }
                if(module == "Lead"){
                    if(selected.get('type') == '0'){
                       enableContactsButton = false;
                    }
                }
            }
            if(selectionLengthFlag==undefined || selectionLengthFlag==0){
            	if(module!="Product")
            		enableButt(moduleObj,updownCompE,isMainOwner,enableContactsButton);
            }
           
            var downUpPanel = Wtf.getCmp(id+"dloadpanelcenter");
            var panTitleTpl = new Wtf.XTemplate(  '' );
            panTitleTpl.overwrite(downUpPanel.body,{});
            if(selected.data.dpcontent == undefined || refresh == true || loadMask){
                if(loadMask) {
                    Wtf.commonWaitMsgBox("Retrieving "+module+" details...");
                }
                if(Wtf.getCmp("tempButton")!=undefined){
                var but=Wtf.getCmp("tempButton");
                but.setTooltip(module+" has been selected to Add/Modify owner");
}
                Wtf.Ajax.requestEx({
                    url:Wtf.req.springBase+"common/DetailPanel/getDetails.do",
//                    url:"Common/DetailPanel/getDetails.do",
                    params:{
                        recid:Recid,
                        module:module,
                        email:emailid,
                        flag:256,/////////////flag for case
                        detailFlag:module == 'Lead' && loadMask ? true : null,
                        mapid:mapid
                    }
                },this,
                function(res) {
                    if(loadMask) {
                        Wtf.updateProgress();
                    }
                    selected.data.dpcontent = res;
                    if(selected.data.dpcontent.commData!=undefined)
                        commentlist = selected.data.dpcontent.commData.commList;
                    overwriteDetailPanel(res, updownCompE, downUpPanel, loadMask,contactsPermission);

                },
                function(res) {
                    if(loadMask) {
                        Wtf.updateProgress();
                    }
                    var tpl0= new Wtf.XTemplate(  updownCompE.Failed  );
                    tpl0.overwrite(downUpPanel.body,{});

                });
            }
            else {
                if(selected.data.dpcontent.commData!=undefined)
                    commentlist = selected.data.dpcontent.commData.commList;
				overwriteDetailPanel(selected.data.dpcontent, updownCompE, downUpPanel, loadMask,contactsPermission);
            }
//        }
//        else{
//            disableButt(moduleObj);    
//
//            var tpl= new Wtf.Template("<div style='margin:3px;height:90%;width:90%;'>", "<div id='{msgDiv}' style='height: auto;display:block;overflow:auto; margin-left:10px;'>Please select a valid record to see the details.</div></div>");
//            tpl.overwrite(Wtf.getCmp(id+"dloadpanelcenter").body,'');
//        }
    }
    else if(moduleObj!=undefined){
        disableButt(moduleObj);
        var tpl1= new Wtf.Template("<div style='margin:3px;height:90%;width:90%;'>", "<div id='{msgDiv}' style='height: auto;display:block;overflow:auto; margin-left:10px;'>Please select a record to see the details.</div></div>");
        if(Wtf.getCmp(id+"dloadpanelcenter")!=undefined)
            tpl1.overwrite(Wtf.getCmp(id+"dloadpanelcenter").body,'');
    }
    return commentlist;
}
smileyStore = new Array(':)', ':(', ';)', ':D', ';;)', '&gt;:D&lt;', ':-/', ':x', ':&gt;&gt;', ':P', ':-*', '=((', ':-O', 'X(', ':&gt;', 'B-)', ':-S', '#:-S', '&gt;:)', ':((', ':))', ':|', '/:)', '=))', 'O:-)', ':-B', '=;', ':-c');
function smiley(tdiv, emoticon){
    tdiv.innerHTML = tdiv.innerHTML.replace(emoticon, '<img src=images/smiley' + (smileyStore.indexOf(emoticon) +1) + '.gif style=display:inline;vertical-align:text-top;></img>');
}

function parseSmiley(str){
    str = unescape(str);
    var tdiv = document.createElement('div');
    var arr = [];
    arr = str.match(/(:\(\()|(:\)\))|(:\))|(:x)|(:\()|(:P)|(:D)|(;\))|(;;\))|(&gt;:D&lt;)|(:-\/)|(:&gt;&gt;)|(:-\*)|(=\(\()|(:-O)|(X\()|(:&gt;)|(B-\))|(:-S)|(#:-S)|(&gt;:\))|(:\|)|(\/:\))|(=\)\))|(O:-\))|(:-B)|(=;)|(:-c)/g);
    if (arr == null) {
        tdiv.innerHTML = str;
    } else {
        var i;
        tdiv.innerHTML = str;
        for (i = 0; i < arr.length; i++) {
            smiley(tdiv, arr[i]);
        }
    }
    return tdiv.innerHTML;
}
function overwriteDetailPanel(res, updownCompE, downUpPanel, loadMask,contactsPermission) {

    /*  Create Documents Div   */
    var DataTpl = new Wtf.XTemplate(  updownCompE.panDocTitle );
    DataTpl.overwrite(downUpPanel.body,{});
    if(res.docData) {
        if(res.docData.docList.length==0){
            DataTpl= new Wtf.XTemplate( updownCompE.noDoc );
            DataTpl.append(downUpPanel.body,{});
        }
        else {
            if(res.docData.docPerm) {
                DataTpl= new Wtf.XTemplate( updownCompE.newDocContentWithPerm );
                for( var i1 = 0; i1 < res.docData.docList.length; i1++ ) {
                	if(res.docData.docList[i1].uploadedby==this._fullName)
                	res.docData.docList[i1]['deleteimg'] = "<img onclick ='callDeleteDocument(\""+updownCompE.id+"\",\""+res.docData.docList[i1].docid+"\",\""+res.docData.docList[i1].Name+"\")' src='../../images/Cancel.gif' wtf:qtitle='Delete Document' wtf:qtip='Click to delete document.' style='height: 12px; width: 12px;float:right;cursor:pointer' >";
                    DataTpl.append(downUpPanel.body,res.docData.docList[i1]);
                }
            } else {
                DataTpl= new Wtf.XTemplate( updownCompE.newDocContentWithNoPerm );
                for( var i4 = 0; i4 < res.docData.docList.length; i4++ ) {
                    DataTpl.append(downUpPanel.body,res.docData.docList[i4]);
                }
            }
        }
    } else {
        DataTpl= new Wtf.XTemplate( updownCompE.noPerm );
        DataTpl.append(downUpPanel.body, {});
    }

    /*  Create Comments Div   */
    DataTpl = new Wtf.XTemplate(  updownCompE.panCommTitle );
    DataTpl.append(downUpPanel.body,{});
    if(res.commData) {
        if(res.commData.commPerm) {
            if(res.commData.commList.length==0){
                DataTpl= new Wtf.XTemplate( updownCompE.noComment );
                DataTpl.append(downUpPanel.body,{});
            } else {
                DataTpl= new Wtf.XTemplate(  updownCompE.newCommContentWithPerm  );
                for(var i2 = 0; i2 < res.commData.commList.length; i2++) {
                    res.commData.commList[i2].comment = parseSmiley(unescape(res.commData.commList[i2].comment));
                    if(res.commData.commList[i2].deleteflag)
                        res.commData.commList[i2]['deleteimg'] = "<img onclick ='callDeleteComment(\""+updownCompE.id+"\",\""+res.commData.commList[i2].commentid+"\")' src='../../images/Cancel.gif' wtf:qtitle='Delete Comment' wtf:qtip='Click to delete comment.' style='height: 12px; width: 12px;float:right;cursor:pointer'><img onclick ='callEditComment(\""+updownCompE.id+"\",\""+res.commData.commList[i2].commentid+"\",\""+escape(res.commData.commList[i2].comment)+"\")' src='../../images/edit.gif'  wtf:qtitle='Edit Comment' wtf:qtip='Click to edit comment.' style='padding : 0px 20px ;height: 12px; width: 12px;float:right;cursor:pointer'>";
                    DataTpl.append(downUpPanel.body,res.commData.commList[i2]);
                }
            }
        } else {
            DataTpl= new Wtf.XTemplate(  updownCompE.newCommContentWithNoPerm  );
            DataTpl.append(downUpPanel.body, {});
        }
    } else {
        DataTpl= new Wtf.XTemplate( updownCompE.noPerm );
        DataTpl.append(downUpPanel.body, {});
    }

    /*  Create Recent Activity Div   */
    DataTpl = new Wtf.XTemplate(  updownCompE.panAuditTitle );
    DataTpl.append(downUpPanel.body,{});
    if(res.auditData) {
        if(res.auditData.auditList!=undefined){
            if(res.auditData.auditList.length==0){
                DataTpl= new Wtf.XTemplate( updownCompE.noActivity );
                DataTpl.append(downUpPanel.body,{});
            } else {
                DataTpl= new Wtf.XTemplate(  updownCompE.newAuditContentWithPerm  );
                for(var i3 = 0; i3 < res.auditData.auditList.length; i3++) {
                    res.auditData.auditList[i3].details = unescape(res.auditData.auditList[i3].details);
                    DataTpl.append(downUpPanel.body,res.auditData.auditList[i3]);
                }
            }
        }
    }  else {
        DataTpl= new Wtf.XTemplate( updownCompE.noPerm );
        DataTpl.append(downUpPanel.body, {});
    }

    if(res.subownersData) { /*  Create Owners Div   */
        DataTpl = new Wtf.XTemplate(  updownCompE.panSubOwnersTitle );
        DataTpl.append(downUpPanel.body,{});

        var ownerList=res.subownersData.ownerList;
        if(ownerList.length==0){
            if(res.subownersData.addOwnerPerm) {
                DataTpl= new Wtf.XTemplate( updownCompE.noSubOwnersWithPerm );
                DataTpl.append(downUpPanel.body,{});
            }else{
                DataTpl= new Wtf.XTemplate( updownCompE.noSubOwnersWithNoPerm );
                DataTpl.append(downUpPanel.body,{});
            }
        }else{
            DataTpl= new Wtf.XTemplate(  updownCompE.newSubOwnersWithPerm  );
            for(i2 = 0; i2 < ownerList.length; i2++){
                ownerList[i2].owners = unescape(ownerList[i2].owners);
                DataTpl.append(downUpPanel.body,ownerList[i2]);
            }
        }
    }
    if(res.projData && isDemo) { /*  Create Account Projects Div   */
        DataTpl = new Wtf.XTemplate(  updownCompE.panAccProjectsTitle );
        DataTpl.append(downUpPanel.body,{});

        var projList=res.projData.projList;
        if(projList.length==0){
            if(res.projData.addProjectPerm) {
                DataTpl= new Wtf.XTemplate( updownCompE.noAccProjectsWithPerm );
                DataTpl.append(downUpPanel.body,{});
            }else{
                DataTpl= new Wtf.XTemplate( updownCompE.noAccProjects );
                DataTpl.append(downUpPanel.body,{});
            }
        }else{
            DataTpl= new Wtf.XTemplate(  updownCompE.newAccProjectsWithPerm  );
            for(i2 = 0; i2 < projList.length; i2++){
                projList[i2].projectnames = unescape(projList[i2].projectnames);
                DataTpl.append(downUpPanel.body,projList[i2]);
            }
        }
    }
    if(res.contactsData && contactsPermission) { /*  Create Owners Div   */
        DataTpl = new Wtf.XTemplate(  updownCompE.panContactsTitle );
        DataTpl.append(downUpPanel.body,{});

        var contactList=res.contactsData.contactList;
        if(contactList.length==0){
            DataTpl= new Wtf.XTemplate( updownCompE.noContacts );
            DataTpl.append(downUpPanel.body,{});
        }else{
            DataTpl= new Wtf.XTemplate(  updownCompE.newContactsWithPerm  );
            for(i2 = 0; i2 < contactList.length; i2++){
                contactList[i2].contacts = unescape(contactList[i2].contacts);
                DataTpl.append(downUpPanel.body,contactList[i2]);
            }
        }
    }
    /*  Create Email Div   */
    if(loadMask && this.moduleName != "Account") {
        DataTpl = new Wtf.XTemplate(  updownCompE.panEmailTitle );
        DataTpl.append(downUpPanel.body,{});
        if(res.emailData) {
            if(res.emailData.emailList.length==0){
                DataTpl= new Wtf.XTemplate(updownCompE.noEmail);
                DataTpl.append(downUpPanel.body,{});
            } else {
                DataTpl= new Wtf.XTemplate(updownCompE.newEmailContentWithPerm);
                for(var i3 = 0; i3 < res.emailData.emailList.length; i3++) {
                    res.emailData.emailList[i3].email = unescape(res.emailData.emailList[i3].email);
                    DataTpl.append(downUpPanel.body,res.emailData.emailList[i3]);
                }
            }
        }  else {
            DataTpl= new Wtf.XTemplate(updownCompE.noPerm);
            DataTpl.append(downUpPanel.body, {});
        }
    }
}

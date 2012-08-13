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
Wtf.linkPanel = function(conf){
    Wtf.apply(this, conf);
    Wtf.linkPanel.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.linkPanel, Wtf.Panel, {
    border: false,
    baseCls: 'linkPanelBorder',
    initComponent: function(config){
        Wtf.linkPanel.superclass.initComponent.call(this, config);
        this.addEvents({
            "linkClicked": true
        });
    },
    onRender: function(conf){
        Wtf.linkPanel.superclass.onRender.call(this, conf);
        if(this.nameForDisplay) {
            this.filename = document.createElement("span");
            this.filename.style.textAlign = "float:left !important;";
            this.filename.id = "filename" + this.id;
            this.filename.innerHTML = this.nameForDisplay;
            this.add(this.filename);
        }
        
        this.link = document.createElement("a");
        this.link.id = "panelLink" + this.id;
        this.link.onclick = this.linkClick.createDelegate(this, [this.link]);
        this.link.innerHTML = this.text;
        this.add(this.link);
    },

    afterRender: function() {
        this.doLayout();
    },

    linkClick: function(){
        this.fireEvent("linkClicked", this.link, this);
    },
    setLinkText: function(text){
        this.link.innerHTML = text;
    },
    hideLink: function(){
        this.link.style.display = 'none';
    },
    showLink: function(){
        this.link.style.display = 'block';
    }
});


/*  Wtf.TopicStore: Start*/
Wtf.TopicStore = function(){
    var Readerrecord =  Wtf.data.Record.create(['type','uid','ieId']);
    Wtf.TopicStore.superclass.constructor.call(this, {
        remoteSort: true,
        proxy: new Wtf.data.HttpProxy({
            url: "Common/MailIntegration/mailIntegrate.do"
        }),
        reader: new Wtf.data.JsonReader({
            root: 'meta'
        },Readerrecord)
    });
};
Wtf.extend(Wtf.TopicStore, Wtf.data.Store, {
    initComponent: function(conf) {
        Wtf.TopicStore.superclass.initComponent.call(this, conf);
        this.events = { 'dataloaded': true }
    },
    loadForum: function(emailactio,ieid,uid,mbox){
        this.baseParams = {
            action : 'EmailUIAjax',
            module: 'Emails',
            emailUIAction : emailactio,
            ieId: ieid,
            uid : uid,
            to_pdf : true,
            mbox : mbox
        };
        this.on('load', function(){
            this.fireEvent("dataloaded");
        }, this)
        this.load();
    }
});
/*  Wtf.TopicStore: End*/

/*  Wtf.MessagePanel: Start */
Wtf.MessagePanel = function(config){
    Wtf.apply(this, config);
    this.topicstore = new Wtf.TopicStore({});
    this.messageId = null;
    this.topicstore.on("load", this.loadData, this);
    this.topicstore.on("loadexception", this.handleException, this);
    this.messagePanelDetailsTemplate = new Wtf.Template("<div id='{divImg}' style='width:10%; autoHeight:true; float:left;'>", "<img id='{imgDiv}' style='float:left;height:45px; width:65px 'src='../../images/s.gif'></img>", "</div>", "<ul id='{dataDiv}'>", "<li><span class='head-label'>"+WtfGlobal.getLocaleText("crm.case.defaultheader.subject")+":</span>", "<span id='{subjectDiv}'></span>","</li>","<li style='float:left;width:55%;'><span id='{msgfrom}' class='head-label'>{fromtext}:</span>", "<span id='{fromDiv}'></span>","</li>" ,"<li style='float:left;width:35%;'><span id='{msgdate}' class='head-label'>{recdtext}:</span>", "<span id='{receivedOn}'></span></li></li>","<li style='float:left;'><span id='{msgto}' class='head-label'>{totext}</span>", "<span id='{toDiv}'></span>","</li></ul>", "</div>");
    this.messagePanelContentTemplate = new Wtf.Template("<div style='margin:3px;height:90%;width:90%;'>", "<div id='{msgDiv}' style='height: auto;display:block;overflow:auto; margin-left:10px;'>"+WtfGlobal.getLocaleText("crm.mymails.inbox.mtytxt")+"</div></div>");

    Wtf.MessagePanel.superclass.constructor.call(this, {
        id: config.id,
        closable: true,
        split: true,
        border: false,
        bodyStyle: "background:#FFFFFF;border: solid 4px #5b84ba;",
        layout: "border",
        items: [{
            region:'north',
            cls:'messagePanelHeader',
            border: false,
            height:60,
            html: this.messagePanelDetailsTemplate.applyTemplate({
                divImg: "divImg_" + config.id,
                imgDiv: "imgDiv_" + config.id,
                dataDiv: "dataDiv_" + config.id,
                subjectDiv: "subjectDiv_" + config.id,
                msgfrom: "msgfrom_" + config.id,
                fromDiv: "fromDiv_" + config.id,
                msgto: "msgto_" + config.id,
                toDiv: "toDiv_" + config.id,
                msgdate: "msgDate_" + config.id,
                receivedOn: "receivedOn_" + config.id,
                fromtext: WtfGlobal.getLocaleText("crm.frommail.label"),//"From",
                recdtext: WtfGlobal.getLocaleText("crm.mymails.receivedon")//"Received On"
            })
        }, {
            region:'center',
            cls:'messagePanelBody',
            border: false,
            html: this.messagePanelContentTemplate.applyTemplate({
                msgDiv: "msgDiv_" + config.id
            })
        }]
    });
    this.addEvents = {
        "UpdateDstore": true,
        "UpdateMailDstore": true
    };
};

Wtf.extend(Wtf.MessagePanel, Wtf.Panel, {
    loadData: function(obj, rec, opt){
        var recData = rec[0].email;
//        document.getElementById('msgDiv_' + this.id).innerHTML =
//            Wtf.util.Format.stripScripts((this.parseSmiley(WtfGlobal.URLDecode(recData.Details)))+WtfGlobal.URLDecode(recData.Attachment));
//        this.fireEvent("UpdateMailDstore", recData.Details,recData.Attachment,recData.ID);
    },

    loadDescriptionData: function(emailData,uid){
        document.getElementById('msgDiv_' + this.id).innerHTML =
                Wtf.util.Format.stripScripts((this.parseSmiley(emailData.description.length>0?WtfGlobal.URLDecode(emailData.description):"")));
        this.fireEvent("UpdateMailDstore", emailData.description,uid,emailData.attachment);
    },

    parseSmiley:function(str){
        str = unescape(str);
        var tdiv = document.createElement('div');
        var arr = [];
        arr = str.match(/(:\(\()|(:\)\))|(:\))|(:x)|(:\()|(:P)|(:D)|(;\))|(;;\))|(&gt;:D&lt;)|(:-\/)|(:&gt;&gt;)|(:-\*)|(=\(\()|(:-O)|(X\()|(:&gt;)|(B-\))|(:-S)|(#:-S)|(&gt;:\))|(:\|)|(\/:\))|(=\)\))|(O:-\))|(:-B)|(=;)|(:-c)/g);
//        if (arr == null) {
            tdiv.innerHTML = str;
//        } else {
//            var i;
//            var smileyStr;
//            tdiv.innerHTML = str;
//            for (i = 0; i < arr.length; i++) {
//                this.smiley(tdiv, arr[i]);
//            }
//        }
        return tdiv.innerHTML;
    },
    smiley: function(tdiv, emoticon){
        tdiv.innerHTML = tdiv.innerHTML.replace(emoticon, '<img src=../../images/smiley' + (smileyStore.indexOf(emoticon) +1) + '.gif style=display:inline;vertical-align:text-top;></img>');
    },
    setData: function(sub, from, received, imgsrc, senderid,superadmin,to){
        document.getElementById('subjectDiv_' + this.id).innerHTML = "<span style='font-weight:bold;'>"+ ((sub==undefined || sub==null) ? "" : WtfGlobal.URLDecode(sub))+"</span>";
//        var fromDivString = (superadmin!=null && superadmin=='true')? from:"<a class=\"attachmentlink\" href='#' onclick='javascript:openprofile(\"" + senderid + "\",\"" + from + "\",\"" + imgsrc + "\")'>" + from + "</a>";
        document.getElementById('fromDiv_' + this.id).innerHTML = "<span style=\"margin:14px;\">"+from+"</span>";
        document.getElementById('receivedOn_' + this.id).innerHTML = received;
        document.getElementById('toDiv_' + this.id).innerHTML = "<span style=\"margin-top:14px;margin-left:29px;\">"+((to==undefined || to==null) ? "" : WtfGlobal.URLDecode(to))+"</span>";
        var imgdiv = document.getElementById('divImg_' + this.id).getElementsByTagName('img')[0];
        if (imgsrc == "")
            imgdiv.src = '../../images/defaultuser.png';
        else
            imgdiv.src = '../../images/store/' + imgsrc;
    },

    loadCacheData: function(msgData){
        document.getElementById('msgDiv_' + this.id).innerHTML = Wtf.util.Format.stripScripts(this.parseSmiley(WtfGlobal.URLDecode(msgData)));//Wtf.util.Format.htmlDecode(parseSmiley(unescape(msgData))));
    },

    setData1: function(sub, from, received, text, imgsrc,to){
        document.getElementById('msgDiv_' + this.id).innerHTML = Wtf.util.Format.htmlDecode(WtfGlobal.URLDecode(text));
        document.getElementById('subjectDiv_' + this.id).innerHTML = "<span style='font-weight:bold;'>"+WtfGlobal.URLDecode(sub)+"</span>";
        document.getElementById('fromDiv_' + this.id).innerHTML = from;
        document.getElementById('toDiv_' + this.id).innerHTML = "<span style=\"margin-top:14px;margin-left:29px;\">"+WtfGlobal.URLDecode(to)+"</span>";
        document.getElementById('receivedOn_' + this.id).innerHTML = received;
        document.getElementById('divImg_' + this.id).getElementsByTagName('img')[0].src = imgsrc;
    },

    setFromText: function(fromtxt, recdtxt){
        document.getElementById('msgfrom_' + this.id).innerHTML = fromtxt;
        document.getElementById("msgDate_" + this.id).innerHTML = recdtxt;
    },
    setToText: function(totxt){
        document.getElementById('msgto_' + this.id).innerHTML = totxt;
    },
    clearContents: function(){
        document.getElementById("msgDiv_" + this.id).innerHTML = "";
        document.getElementById('subjectDiv_' + this.id).innerHTML = "";
        document.getElementById('fromDiv_' + this.id).innerHTML = "";
        document.getElementById('receivedOn_' + this.id).innerHTML = "";
        document.getElementById('toDiv_' + this.id).innerHTML = "";
        document.getElementById('divImg_' + this.id).getElementsByTagName('img')[0].src = '../../images/s.gif';
    },
    handleException:function() {
        var obj = document.getElementById('msgDiv_' + this.id);
        if(this.topicstore.reader.jsonData === undefined)
            obj.innerHTML="<img src='lib/resources/images/default/window/icon-warning.gif' height='16px' width='16px' />"+WtfGlobal.getLocaleText("crm.mymails.loadmsgerrormsg");//Unable to load message..Please try again later";
        else
            obj.innerHTML="";
    }
});
/*  Wtf.MessagePanel: End*/

/*  WtfMsgEditor: Start */
Wtf.ReplyWindow = function(config){

    Wtf.apply(this, config);

    this.sendBtn = new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.mymails.sendBTN"),// 'Send',
        tooltip: {
            title: WtfGlobal.getLocaleText("crm.mymails.sendBTN"),//'Send',
            text: WtfGlobal.getLocaleText("crm.mymails.sendmessagetip")//'Send message.'
        },
        iconCls: 'pwnd outbox',
        disabled : true,
        id: 'sendBtnid'
    });
    this.sendBtn.on('click', function() {
        if(!isValidEmail(this.field1.getValue())) {
        	//The address in the '"+this.field1.initialConfig.fieldLabel+"' field was not recognized.<br/>Please make sure that all addresses are properly formed.
            Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText({key:"crm.mymails.wrongmessageerror",params:[this.field1.initialConfig.fieldLabel]}));
            return;
        }
        this.field2.validate();
        if(this.field2.isValid())
            this.handleSend();
    }, this);
    this.addEvents({
        "beforeClosewindow": true,
        "loadsuccess": true
    });
    this.saveBtn = new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//'Save',
        hidden:(this.isHideSave)?this.isHideSave:false,
        tooltip: {
            title: WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//'Save',           
            text: WtfGlobal.getLocaleText("crm.msgforum.savetodrafstip")//'Save to drafts.'
        },
        iconCls : 'pwnd saveBtn',
        disabled : true,
        id: 'saveBtnid'
    });
    this.saveBtn.on('click', this.handleSave, this);

    this.closeBtn = new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("crm.CLOSEBUTTON"),//'Close',
        tooltip: {
            title:WtfGlobal.getLocaleText("crm.CLOSEBUTTON"),// 'Close',
            text: WtfGlobal.getLocaleText("crm.msgforum.closetabtip")//'Close tab.'
        },
        iconCls: 'pwnd closeButtonIcon',
        id: 'closeBtnid'
    });
    this.closeBtn.on('click', this.handleClose, this);


    this.tButnArr = [];
    this.tButnArr.push(this.sendBtn);
    if (config.composeMail == 1 || config.composeMail == 5) {
        this.composeMailFlag = true;
        this.tButnArr.push(this.saveBtn);
    }
    this.tButnArr.push(this.closeBtn);
    var insertStoreReaderrecord =  Wtf.data.Record.create([{name:'Success'},
        {name:'ID'},
        {name:'Subject'},
        {name:'Received',type:'date'},
        {name:'From'},
        {name:'Details'},
        {name:'Flag'},
        {name:'Image'}]);
    this.insertStore = new Wtf.data.Store({
        proxy: new Wtf.data.HttpProxy({
            url: Wtf.req.prt + "insertNewMsg.jsp"
        }),
        panelObj: this,
        reader: new Wtf.data.KwlJsonReader({
            root: "data"
        },insertStoreReaderrecord)
    });
    this.insertStore.on("loadexception", function(){
        msgBoxShow(4, 1);
        this.ownerCt.remove(this);
        this.sendBtn.enable();
        this.closeBtn.enable();
        this.saveBtn.enable();
    }, this);
    this.insertStore.on("load", this.handleLoad, this);

    this.uvalue = config.uFieldValue;

    this.projFlag = false;
    if (config.projectFlag)
        this.projFlag = true;
    if (this.uvalue == "NewTopic" && config.projectFlag != true) {
        this.uvalue = Wtf.getCmp('sub' + config.id1.substring(4)).ownerCt.comtitle;
    } else if (this.uvalue == "NewTopic" && config.projectFlag) {
        this.uvalue = Wtf.getCmp('subtabpanelcomprojectTabs_' + config.id1.substring(4)).ownerCt.comtitle;
    }

    this.userds = new Wtf.data.JsonStore({
            url: "Common/MailIntegration/mailIntegrate.do?action=EmailUIAjax&emailUIAction=fillComposeCache&krawler_body_only=true&module=Emails&to_pdf=true",
            root:'fromAccounts',
            fields : ['text','value']
        });
    this.userds.on("load",function(){
        var Rec = this.userds.queryBy(function(rec){
            if(rec.data.text  == this.uvalue.replace("<","(").replace(">",")"))
                return true;
            else
                return false;
        },this);

        if(Rec.length>0)
            this.fromUserCombo.setValue(Rec.get(0).data.value);
    },this);
    this.userds.load();
    this.fromUserCombo = new Wtf.form.FnComboBox({
        fieldLabel : WtfGlobal.getLocaleText("crm.frommail.label"),//'From',
        store: this.userds,
        addNewDisplay :  WtfGlobal.getLocaleText("crm.mymails.addnewac"),//'Add New Email Account...',
        displayField: 'text',
        valueField: 'value',
        width : 400,
        typeAhead: true,
        mode: 'local',
        forceSelection: true,
        tabIndex:1,
        addNewFn : this.createNewEntry.createDelegate(this),
        emptyText: WtfGlobal.getLocaleText("crm.targetlists.clicktoseltext"),// "Click to select",
        editable: true,
        triggerAction: 'all',
        selectOnFocus: true
    });
   
   this.field1 = new Wtf.form.TextField({
        fieldLabel: config.uLabel,
        name: config.uLabel,
        value: config.replytoId,
        tabIndex:2,
        disabled: config.tdisabled,
        width: '95%',
        emptyText: WtfGlobal.getLocaleText("crm.mymails.addnewacc.mtytxt")//'Give comma (,) after each email address to send email to severals'
    });
    this.field1.on("change", function(){
        this.field1.setValue(WtfGlobal.HTMLStripper(this.field1.getValue()));
    }, this);

    this.field2 = new Wtf.ux.TextField({
        fieldLabel: config.bLabel,
        name: config.bLabel,
        value: config.bFieldValue,
        maxLength:255,
        tabIndex:3,
        maxLengthText: WtfGlobal.getLocaleText({key:"crm.mymails.conftxtmaxlengthtxt",params:[config.bLabel]}),//"The maximum length allowed for "+config.bLabel+" is 255 characters",
        width:'95%'
    });
    this.field2.on("change", function(){
        this.field2.setValue(WtfGlobal.HTMLStripper(this.field2.getValue()));
    }, this);
    
    this.attachPanel = new Wtf.linkPanel({
        text: WtfGlobal.getLocaleText("crm.mymails.attachfiletxt"),// "Attach a file",
        height: 20
    });
    this.attachPanel.on("linkClicked", this.Attachfile, this);
    this.hedit = new Wtf.newHTMLEditor({
        height: 240,
        border: false,
        enableLists: false,
        hiddenflag:true,
        enableSourceEdit: false,
        enableAlignments: true,
        hideLabel: true,
        tabIndex:4

	});
    this.hiddenHtml= new Wtf.form.Hidden({
        name:'ptxt'
    });
    this.hiddentitle= new Wtf.form.Hidden({
        name:'title'
    });
    this.hiddenHtml.hidden = true;
    this.draftSendFlag = false;
    if(config.details!=null)
         this.hedit.setValue(WtfGlobal.URLDecode(config.details));
    if (config.composeMail == 5) {
        this.draftSendFlag = true;
    }

    this.type = config.type;
    this.sendflag = config.sendFlag;
    this.replyToId = config.replytoId;
    this.userId = config.userId;
    this.groupId = config.groupId;
    this.firstReply = config.firstReply;
    this.fid = config.fid;

    this.count = 1;
    this.attachheight = 120;
    var disable = config.tdisabled?"disabled":"";
    this.resPanel = new Wtf.Panel({
        border :false,
        height:'20px',
       html:'<label style="width:80px !important;float:left;">'+ config.uLabel+':</label><input class="idcheckedEnabled" type="text"  style="width: 85%" '+disable+' value="'+ this.uvalue+'" /><br/>'+
            '<div style="margin-top: 2pt; height: 18pt;"><label style="width:80px !important;float:left;">'+ config.bLabel+':</label><input type="text" style="width: 85%" value="'+config.bFieldValue+'"  name="'+config.bLabel+'" /><br /></div>'
    });
    this.smartInputFloater = {
        xtype : 'panel',
        border: false,
        id:'smartInputFloater12',
        html: '<table id="smartInputFloater" style = "left: 64pt;top: 20pt"class="floater" cellpadding="0" cellspacing="0"><tr><td id="smartInputFloaterContent" nowrap="nowrap">'
            +'<\/td><\/tr><\/table>'
    };

    this.top = new Wtf.Panel({
        frame: false,
        bodyStyle: 'padding:5px 5px 0',
        border: false,
        fileUpload: true,
        height: this.attachheight,
        layout: 'form',
        items: [/*this.field3,this.resPanel,*/this.fromUserCombo,this.field1/*this.resPanel,*//* autoPanel*/,this.field2, this.attachPanel, /*this.smartInputFloater,*/this.hiddenHtml,this.hiddentitle]
    });
    
    this.field2.on("specialkey",function(field,eventObj){
        var keycode = eventObj.getKey();
        if(keycode ==  eventObj.TAB){
                this.hedit.activated=false;
                this.hedit.onEditorEvent();
        }
    },this)
    Wtf.ReplyWindow.superclass.constructor.call(this, {
        layout: 'border',
        plain: true,
        border: false,
        iconCls: 'pwnd composemail',
        items: [{
            region: 'center',
            border: false,
            layout: 'fit',
            items: [this.hedit]
        }, {
            region: 'north',
            layout: 'fit',
            border: false,
            height: this.attachheight,
            items: [this.top]
        }],
        resizable: true,
        tbar: this.tButnArr
    });
    this.on("destroy", function(){
        if(this.hedit.smileyWindow !== undefined)
            this.hedit.smileyWindow.destroy();
        if(Wtf.getCmp("emails" + this.replyToId)!=null){
            Wtf.getCmp("emails" + this.replyToId).topicstore.loadForum(this.replyToId, "-1", "mail","");
        }
    }, this);

    this.hedit.on("initialize", function(obj){
        this.attachmentID = 'attachment'+this.emailid.replace('+','_');
        var task = new Wtf.util.DelayedTask(function() {
            if(this.composeMail == 1) // if Draft
                this.draftAttachment(this.attachment);
            var rg = new RegExp("id='"+this.attachmentID+"\\b");
            this.hedit.setValue(this.hedit.getValue().replace(rg, "id='del_"+this.attachmentID));
            var childNodes = this.hedit.doc.childNodes[0].childNodes[1].childNodes;
            for(var cnt=0; cnt<childNodes.length;cnt++) {
                var search = new RegExp('del_'+this.attachmentID, 'gi');
                if(childNodes[cnt].id && childNodes[cnt].id.match(search) && childNodes[cnt].id.match(search)[0] == 'del_'+this.attachmentID) {
                    childNodes[cnt].innerHTML="";
                }
            }
            this.sendBtn.enable();
            if(this.composeMail == 1 || this.composeMail == 5)
                this.saveBtn.enable();
        }, this);
        task.delay(500);
    },this);
}

Wtf.extend(Wtf.ReplyWindow, Wtf.Panel, {

    createNewEntry: function() {
        new Wtf.MailAccSetting({editFlag:false}).show();
    },
    
    removeFile:function(linkDom, linkPanel){
         Wtf.Ajax.requestEx({
            url: "Common/MailIntegration/mailIntegrate.do",
            method:'post',
            params:{
                action:'EmailUIAjax',
                emailUIAction : 'removeUploadedAttachment',
                file:linkPanel.guid,
                krawler_body_only:true,
                module:'Emails',
                to_pdf:true
            }},
            this,
            function(request,res){
            },function() {
        });
        this.top.remove(linkPanel, true);
        this.attachheight -=27;
        this.top.ownerCt.setHeight(this.attachheight);
        this.top.setHeight(this.attachheight);
        this.count--;
        if(this.top.items.length < 11)
            this.attachPanel.showLink();
        if(this.top.items.length==6)
            this.attachPanel.setLinkText( WtfGlobal.getLocaleText("crm.mymails.attachfiletxt"));//"Attach a file");
        this.doLayout();
    },
    Attachfile:function(){
        var uploadDocWin = new Wtf.attachFile({
            scope:this
        });
        uploadDocWin.show();
    },

    draftAttachment : function(attachementInfo) {
        if(attachementInfo && attachementInfo.length>0) {
            var infoObj = eval('(' + attachementInfo + ')');
            if(infoObj.attachments) {
                for (var key in infoObj.attachments) {
                  if (infoObj.attachments.hasOwnProperty(key)) {
                      var attObj = infoObj.attachments[key];
                      var fileInfo = {
                          guid :attObj.id,
                          name : attObj.filename,
                          nameForDisplay : attObj.filename,
                          isdraft : true
                      };
                      this.onSuccessAttached(fileInfo, false)
                  }
                }
            }
        }
    },
    
    onSuccessAttached : function(fileInfo, isNewFile) {
        var pid = 'fileattach_' + (isNewFile ? "new_":"old_")+ this.count;
        this.count++;
        var lp = new Wtf.linkPanel({
            cls:'fileattachremove',
            id : pid,
            border:false,
            text: WtfGlobal.getLocaleText("crm.goals.header.remove"),//"Remove",
            guid : fileInfo.isdraft ? fileInfo.guid : fileInfo.guid + fileInfo.name,
            nameForDisplay:fileInfo.nameForDisplay
        });
        this.top.insert(this.count, lp);
        lp.on("linkClicked", this.removeFile, this);
        this.attachheight = this.attachheight+27;
        this.top.ownerCt.setHeight(this.attachheight);
        this.top.setHeight(this.attachheight);
        this.attachPanel.setLinkText("Attach another file");
        if(this.top.items.length >= 11)
            this.attachPanel.hideLink();
        this.doLayout();
    },
//    handleLoad: function(obj, rec, opt){
//        this.fireEvent("loadsuccess", obj, rec, opt);
//    	this.sendBtn.enable();
//        this.closeBtn.enable();
//        this.saveBtn.enable();
//        this.ownerCt.remove(this);
//        if(this.replyds!=null){
//            this.replyds.reload();
//        }
//    },
    handleClose: function(bobj, e){
        if(this.replyds!=null){
            this.replyds.reload();
        }
        if(this.ownerCt) {
            this.fireEvent("beforeClosewindow", this);
            this.ownerCt.remove(this);
        }
    },
    sendMail:function(reptoName,draftFlag) {
        mainPanel.loadMask.msg = 'sending mail...';
        mainPanel.loadMask.show();
        this.hedit.syncValue();
        var msg = this.hedit.getValue();
        var attachDivIndex = msg.indexOf("<div id='"+this.attachmentID + "_1'");
        if(attachDivIndex > -1) {
            msg =  msg.substr(0, attachDivIndex);
        }
        msg = msg.replace(/<STRONG>/gi,"<b>");
        msg = msg.replace(/<\/STRONG>/gi,"</b>");
        msg = msg.replace(/<em>/gi,"<i>");
        msg = msg.replace(/<\/em>/gi,"</i>");
        this.hiddenHtml.setValue(msg);
        this.hiddentitle.setValue(this.field2.getValue());
        
        Wtf.Ajax.requestEx({
            url: "Common/MailIntegration/mailIntegrate.do",
            method:'post',
            params:{
                action:'EmailUIAjax',
                emailUIAction : 'sendEmail',
                addressFrom1:this.fromUserCombo.getValue(),
                fromAccount : this.fromUserCombo.getValue(),
                saveToKrawler :1,
                addressTo1: reptoName,
                sendTo :  reptoName,
                composeType : draftFlag,
                krawler_body_only:true,
                attachments : this.isDraft?'':this.getAttachedFiles(),//this.getAttachedFiles(),
                module:'Emails',
                sendCharset : 'ISO-8859-1',
                sendDescription : this.hiddenHtml.getValue(),
                sendSubject : this.hiddentitle.getValue(),
                subject1 :this.hiddentitle.getValue(),
                setEditor :1,
                email_id : this.composeMail == 1 ? this.emailid:'',
                templateAttachments : this.isDraft?this.getTemplateAttachedFiles():'',// 1 for draft
                to_pdf:true
            }},
            this,
            function(request,res){
                mainPanel.loadMask.hide();
                if(this.forwardFlag==1)
                	 ResponseAlert(96);
                if(lefttree && lefttree.sentmailnode!=null) {
                    lefttree.sentmailnode.fireEvent('click',lefttree.sentmailnode,lefttree);
                }
                this.handleClose();
            },function() {
                mainPanel.loadMask.hide();
        });

//        this.top.form.submit({
//            url:Wtf.req.prt + "insertNewMsg.jsp?type="+this.type+"&sendflag="+this.sendflag+"&repto="+ reptoName+"&userId="+this.userId+"&groupId="+ this.groupId+"&firstReply=" +this.firstReply+"&draft="+ draftFlag+"&fid="+ this.fid,
//            waitMsg :'sending...',
//            scope:this,
//            success: function (res, request) {
//              if(request.result.data.data[0].Success){
//                    if (request.result.data.data[0].Success.match('Success')) {
//                        msgBoxShow(141, 0);
//                        this.handleClose();
//                    }
//                    else if (request.result.data.data[0].Success.match('Fail')) {
//                        if(request.result.data.data[0].msg!=null){
//                            msgBoxShow(['Error',request.result.data.data[0].msg], 1);
//                        }else{
//                            msgBoxShow(142, 1);
//                        }
//                         this.handleClose();
//                    }
//                    else if (request.result.data.data[0].Success.match('Draft')) {
//                            msgBoxShow(147, 0);
//                            this.handleClose();
//                    }
//                    else if (request.result.data.data[0].Success.match('userfail')) {
//                            msgBoxShow(['Delivery Failure', 'Message to user '+request.result.data.data[0].Subject +' is invalid.'], 1);
//                             this.handleClose();
//                    }
//                 }
//            },
//            failure: function ( result, request) {
//              this.handleClose();
//            }
//        },this)

        if(this.mailDS)
            this.mailDS.reload();
    },

    getAttachedFiles : function () {
      // attached files
        var attachments = "";
        for(var cnt=1;cnt<=5;cnt++) {
            var pid = 'fileattach_new_'+cnt;
            if(Wtf.getCmp(pid)) {
                if(attachments.length >0)
                    attachments +="::";
                attachments += Wtf.getCmp(pid).guid;
            }
        }
        return attachments;
    },

    getTemplateAttachedFiles : function () {
      // attached files
        var attachments = "";
        for(var cnt=1;cnt<=5;cnt++) {
            var pid = 'fileattach_old_'+cnt;
            if(Wtf.getCmp(pid)) {
                if(attachments.length >0)
                    attachments +="::";
                attachments += Wtf.getCmp(pid).guid;
            }
        }
        return attachments;
    },
    handleSend: function(bobj, edfd) {
        var draftFlag = '';
        var reptoName = this.replyToId;
        if (this.composeMail==5) {
            draftFlag = 'reply';
            reptoName = this.field1.getValue();
        } else if (this.composeMail==1) {
            reptoName = this.field1.getValue();
        }
        if(this.fromUserCombo.getValue()=="") {
            WtfComMsgBox(457,1);
            return false;
        }
        if(!(this.field1.isValid())||this.field1.getValue()=="") {
            WtfComMsgBox(456,1);
            return false;
        }
        this.sendBtn.disable();
        this.closeBtn.disable();
        this.saveBtn.disable();
        if(this.field2.getValue()=="") {
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), 'Send message without subject?',
                function(btn) {
                  if (btn == "yes") {
                      this.sendMail(reptoName,draftFlag);
                  }
                  else {
                    this.sendBtn.enable();
                    this.closeBtn.enable();
                    this.saveBtn.enable();
                    return false;
                  }
                }, this);
        } else this.sendMail(reptoName,draftFlag);
    },

    handleSave: function(bobj, edfd){
        if(this.fromUserCombo.getValue()=="") {
            WtfComMsgBox(471,1);
            return false;
        }
        this.closeBtn.disable();
        this.saveBtn.disable();
        this.sendBtn.disable();
        var reptoName = this.replyToId;
      //  if(this.replyToId=='-1' || this.replyToId.trim().length==0){ //this is send to now...
            reptoName = this.field1.getValue();
        //}
        mainPanel.loadMask.msg = 'saving mail...';
        mainPanel.loadMask.show();
        this.hedit.syncValue();
        this.hiddenHtml.setValue(encodeURIComponent(this.hedit.getValue()));
        this.hiddentitle.setValue(encodeURIComponent(this.field2.getValue()))
        Wtf.Ajax.requestEx({
            url: "Common/MailIntegration/mailIntegrate.do",
            params:{
                action:'EmailUIAjax',
                emailUIAction : 'sendEmail',
                addressFrom1:this.fromUserCombo.getValue(),
                fromAccount : this.fromUserCombo.getValue(),
                saveDraft:true,
                saveToKrawler :1,
                addressTo1: reptoName,
                sendTo :  reptoName,
                attachments : this.getAttachedFiles(),
                krawler_body_only:true,
                module:'Emails',
                sendCharset : 'ISO-8859-1',
                templateAttachments : this.getTemplateAttachedFiles(),
                sendDescription : this.hiddenHtml.getValue(),
                sendSubject : this.hiddentitle.getValue(),
                subject1 :this.hiddentitle.getValue(),
                setEditor :1,
                email_id : this.composeMail==1? this.emailid :"",
                to_pdf:true
            },
            method:'post'},
            this,
            function(request,res) {
                mainPanel.loadMask.hide();
                if(lefttree.sentmailnode!=null) {
                    lefttree.sentmailnode.fireEvent('click',lefttree.draftmailnode,lefttree);
                }
                this.handleClose();
                var responseText = request;
                if(responseText != "")
                {

                }
            },function() {

        });
//         this.top.form.submit({
//            url:Wtf.req.prt + "insertNewMsg.jsp?type="+this.type+"&sendflag="+this.sendflag+"&repto="+ reptoName+"&userId="+this.userId+"&groupId="+ this.groupId+"&firstReply=" +this.firstReply+"&draft=1"+"&fid="+ this.fid,
//            waitMsg :'Saving message to draft...',
//            scope:this,
//            success: function (res, request) {
//              if(request.result.data.data[0].Success){
//                    if (request.result.data.data[0].Success.match('Success')) {
//                        msgBoxShow(141, 0);
//                        this.handleClose();
//                    }
//                    else if (request.result.data.data[0].Success.match('Fail')) {
//                        if(request.result.data.data[0].msg!=null){
//                            msgBoxShow(['Error',request.result.data.data[0].msg], 1);
//                        }
//                        msgBoxShow(142, 1);
//                         this.handleClose();
//                    }
//                    else if (request.result.data.data[0].Success.match('Draft')) {
//                            msgBoxShow(147, 0);
//                            this.handleClose();
//                    }
//                    else if (request.result.data.data[0].Success.match('userfail')) {
//                            msgBoxShow(['Delivery Failure', 'Message to user '+request.result.data.data[0].Subject +' is invalid.'], 1);
//                             this.handleClose();
//                    }
//                 }
//            },
//            failure: function ( result, request) {
//              this.handleClose();
//            }
//        },this)

    }
});
function openprofile(theId, from, imgsrc){
    mainPanel.loadTab("user.html", "   " + theId, from, "navareadashboard", Wtf.etype.user, true);
}

function Attachfile(objid){
    Wtf.getCmp(objid).Attachfile();
}

function removefile(objid,thisid){
    Wtf.getCmp(objid).destroy();
    Wtf.getCmp(thisid).removeFile();
}

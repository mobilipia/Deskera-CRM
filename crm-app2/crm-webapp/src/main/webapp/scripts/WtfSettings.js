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
Wtf.namespace("Wtf","Wtf.common", "Wtf.docs.com","Wtf.reportBuilder","Wtf.mbuild","Wtf.customReportBuilder","Wtf.ux.grid","Wtf.account");
Wtf.req = {
    base: "../../jspfiles/",
    tree : "crmTree/",
    widget : "Dashboard/",
    springBase: "crm/",
    mbuild:"mb/mbuild/",
    rbuild:"mb/rbuild/",
    accessR:"mb/accessR/",
    mcombo:'mb/mcombo/',
    customReport: "crm/CustomReport/"
};

Wtf.moduleWidget = {
    lead: "lead_drag",
    campaign : 'campaign_drag',
    account : "account_drag",
    contact : "contact_drag",
    opportunity : "opportunity_drag",
    cases : "case_drag",
    activity : "activity_drag",
    topactivity : "top_activity_drag",
    product : "product_drag",
    campaignreport : "campaign_reports_drag",
    advancesearch:"DSBAdvanceSearch",
    customReports:"crm_custom_reports"
};
Wtf.Constants = {


lblBPM : "BPM<span class='red'>(beta)<span>",
lblBPMDesc: "Create forms dynamically",
lblBPMTitle:"BPM",
lblModuleMasterConfig : "BPM Master Configuration",
lblModuleMasterDesc:"BPM Master configuration settings."

}

Wtf.StoreManagerKeys = {
    title : "title",
    parentaccount : "parentaccount",
    contactstore : "contactstore",
    leadsource : "leadsource",
    leadstatus : "leadstatus",
    relatedtonamestore : "relatedToNameStore",
    productstore : "productStore",
    accounttype : "accounttype",
    industry : "industry",
    opportunitystage : "opportunitystage",
    opportunitytype : "opportunitytype",
    region : "region",
    caseorigin : "caseorigin",
    casepriority : "casepriority",
    casestatus : "casestatus",
    productcategory : "productcategory",
    taskstatus : "taskstatus",
    leadrating : "leadrating"
}
Wtf.crmmodule = {
    campaign: "Campaign",
    lead : "Lead",
    account : "Account",
    contact : "Contact",
    opportunity : "Opportunity",
    cases : "Case",
    activity : "Activity",
    topactivity : "Top Activity",
    product : "Product"
};
Wtf.DEFAULT_CALENDAR = "CRM Activities";
Wtf.DOCUMENT_MODULE = "Documents";
// to store published data
Wtf.commetData = {};
Wtf.goaltype = {
    nooflead: "No of Leads",
    leadrevenue : "Total revenue from closed leads",
    noofaccount : "No of Accounts",
    accountrevenue : "Total revenue from accounts",
    noofopportunity : "No of Opportunities",
    opprevenue : "Total sales amount from opportunities"
};
Wtf.UNDO_ARRAY_LENGTH = 50;
Wtf.TAB_TITLE_LENGTH = 19;
Wtf.PhoneInvalidText='Not a valid phone number.Please enter in the format <b> 123-4567 or 123-456-7890 (dashes optional) </b>';
Wtf.MaxLengthText='Maximum length for field is ';
Wtf.UrlInvalidText='This field should be a URL in the format <b>"http:/'+'/www.domain.com" </b>';
Wtf.InvalidMobPhoneText='Please Enter Valid Mobile Number';
Wtf.EmailInvalidText='This field should be an e-mail address in the format <b>user@domain.com</b>';
Wtf.NumberInvalidText='Please enter a valid Number';
Wtf.BLANK_IMAGE_URL = "../../lib/resources/images/default/s.gif";
Wtf.DEFAULT_USER_URL = "../../images/defaultuser.png";
Wtf.DEFAULT_COMPANY_URL = "../../images/deskera/logo.gif";
//Wtf.ValidateMailPatt = /^([a-zA-Z0-9_\-\.+]+)@(([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})$/;
Wtf.ValidateMailPatt = /^[\w-]+([\w!#$%&'*+\/=?^`{|}~-]+)*(\.[\w!#$%&'*+\/=?^`{|}~-]+)*@[\w-]+(\.[\w-]+)*(\.[\w-]+)$/;
Wtf.ValidateMailWithDomainPatt = /^[\w-]+([\w!#$%&'*\\+\/=?^`{|}~-]+)*(\.[\w!#$%&'*\\+\/=?^`{|}~-]+)*@[\w-]+(\.[\w-]+)*(\.[\w-]+)$/;
Wtf.ValidateUserid = /^\w+$/;
Wtf.ValidateUserName = /^[\w\s\'\"\.\-]+$/;
Wtf.ValidateCustomColumnName = function(colname){
    var patt = new RegExp(/^[\w\s\'\"\-\/]+$/);
    return patt.test(colname);
}
Wtf.validateHeadTitle = /^[\w\s\'\"\.\-\,\~\!\@\$\^\*\(\)\{\}\[\])]+$/;
Wtf.validateNumber = /^[0-9]+$/;
Wtf.DomainPatt = /[ab]\/([^\/]*)\/(.*)/;
Wtf.PhoneRegex= /^([^-])(\(?\+?[0-9]*\)?)?[0-9_\- \(\)]*$/;//^(\(?\+?[0-9]*\)?)?[0-9_\- \(\)]*$/;
Wtf.AdminId = "ff8080812235ee49012236133c090002";
Wtf.ManagerId = "ff8080812235ee4901223619e6070003";
Wtf.EmployeeId = "ff80808122361c6a01223661e34b0001";
Wtf.userModuleId = "users";
Wtf.emptyTextForDateField = WtfGlobal.getLocaleText("crm.date.mtytxt");//" -- Select Date --";
Wtf.move_to_lead_id = "movetoleadquickadd";
Wtf.move_to_lead_win = "move_lead_from_campaign_win";
Wtf.move_to_lead_fname = "campaign_to_lead_fname";
Wtf.move_to_lead_lname = "campaign_to_lead_lname";
Wtf.move_to_lead_email = "campaign_to_lead_email";
Wtf.etype = {
    user: 0,
    comm: 1,
    proj: 2,
    home: 3,
    docs: 4,
    cal: 5,
    forum: 6,
    pmessage: 7,
    pplan: 8,
    adminpanel: 9,
    todo: 10,
    search: 11,
    delet:12,
    save:13,
    graphs:14,
    reports:15,
    archived:16,
    opportunity:17,
    addopportunity:18,
    piechart:19,
    cases:20,
    addcases:21,
    removeIcon:22,
    account:23,
    addaccount:24,
    product:25,
    addproduct:26,
    contacts:27,
    favwinIcon:28,
    convertIcon:29,
    lead:30,
    addlead:31,
    campaign:32,
    addcampaign:33,
    addactivity:34,
    createUserIcon:35,
    editUserIcon:36,
    deleteUserIcon:37,
    changepwdIcon:38,
    resetpwdIcon:39,
    orgaTab:40,
    archivedMenuIcon:41,
    auditTrail:42,
    userTab:43,
    bpm:44,
    bpmmaster:45,
    refreshbutton : 46,
    add : 47

};

Wtf.StoreMgr.chkStore=function(storeId,func){
    var s = Wtf.StoreMgr.lookup(storeId);
    if(!s){
        clog('Unregistered store ('+storeId+')');
        return;
    }
    if(s.lastOptions){
        func();
    }else{
        Wtf.StoreMgr.callBackList = Wtf.StoreMgr.callBackList || new Wtf.util.MixedCollection();
        var list = Wtf.StoreMgr.callBackList.get(s.storeId)||[];
        list.push(func);
        Wtf.StoreMgr.callBackList.add(s.storeId,list);
        s.on('load', Wtf.StoreMgr.callBack);
        s.load();
    }
}

Wtf.StoreMgr.callBack=function(store){
    var funcList=Wtf.StoreMgr.callBackList.get(store.storeId);
    store.un('load',Wtf.StoreMgr.callBack);
    Wtf.StoreMgr.callBackList.removeKey(store.storeId);
    Wtf.each(funcList, function(func){
        func();
    });

}

var bHasChanged = false;
Wtf.Perm = {};
Wtf.UPerm = {};
Wtf.CRMAdmin = false;
this.countryRec = new Wtf.data.Record.create([
{
    name: 'id'
},
{
    name: 'name'
},
{
    name: 'timezone'
}
]);
this.timezoneRec = new Wtf.data.Record.create([
{
    name: 'id'
},
{
    name: 'name'
}
]);
Wtf.validateUserName = function(value){
    return Wtf.ValidateUserName.test(value);
}
Wtf.countryStore = new Wtf.data.Store({
    url:"calendar/common/getcountry.do",
    reader: new Wtf.data.KwlJsonReader({
        root: "data"
    },this.countryRec),
    baseParams:{
        mode:20
    }
});
Wtf.timezoneStore = new Wtf.data.Store({
    url:"Common/KwlCommonTables/getAllTimeZones.do",
    reader: new Wtf.data.KwlJsonReader({
        root: "data"
    },this.timezoneRec),
    baseParams:{
        mode:16
    },
    autoLoad:false
});
Wtf.callWithStore = new Wtf.data.SimpleStore({
    fields: ['id','name'],
    data : [
    [0,"--None--"],
    [1,"Skype"]
    ]
});

function getRelatedToForQuote() {
    var permData = [];
    var arr=[];
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.manage)){
        arr = [Wtf.common.accountModuleID,Wtf.crmmodule.account];
        permData.push(arr);
    }
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.manage)){
        arr = [Wtf.common.leadModuleID,Wtf.crmmodule.lead];
        permData.push(arr);
    }
    /*if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.manage)){
        arr = [Wtf.common.oppModuleID,Wtf.crmmodule.opportunity];
        permData.push(arr);
    }
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.manage)){
        arr = [Wtf.common.contactModuleID,Wtf.crmmodule.contact];
        permData.push(arr);
    }*/
    return new Wtf.data.SimpleStore({
        fields: ['moduleid','modulename'],
        data : permData
    });
}

function getTopHtml(text, body,img){
    if(img===undefined) {
        img = '../../images/createuser.png';
    }
    var str =  "<div style = 'width:100%;height:100%;position:relative;float:left;'>"
    +"<div style='float:left;height:100%;width:auto;position:relative;'>"
    +"<img src = '"+img+"'  class = 'adminWinImg'></img>"
    +"</div>"
    +"<div style='float:left;height:100%;width:60%;position:relative;'>"
    +"<div style='font-size:12px;font-style:bold;float:left;margin:15px 0px 0px 10px;width:100%;position:relative;'><b>"+text+"</b></div>"
    +"<div style='font-size:10px;float:left;margin:8px 0px 10px 10px;width:100%;position:relative;'>"+body+"</div>"
    +"</div>"
    +"</div>" ;
    return str;
}

function getEmailTopHtml(text, body,img){
    if(img===undefined) {
        img = '../../images/createuser.png';
    }
    var str =  "<div style = 'width:100%;height:100%;position:relative;float:left;'>"
    +"<div style='float:left;height:100%;width:auto;position:relative;'>"
    +"<img src = '"+img+"'  class = 'adminWinImg'></img>"
    +"</div>"
    +"<div style='float:left;height:100%;width:60%;position:relative;'>"
    +"<div style='font-size:12px;font-style:bold;float:left;margin:15px 0px 0px 10px;width:100%;position:relative;'><b>"+text+"</b></div>"
    +"<div style='font-size:10px;float:left;margin:8px 0px 10px 10px;width:100%;position:relative;'>"+body+"</div>"
    +"<div style='margin:54px 0px 20px 437px;width:100%;position:absolute;'><img src='../../images/help.png' style='cursor:pointer;' wtf:qtip='Click here to get help on Email settings.' onclick='emailHelp()'></div>"
    +"</div>"
    +"</div>" ;
    return str;
}

function emailHelp(){
    var emailHelp = new Wtf.Window({
        title:'Help',
        id:'emailHelpWin',
        closable: true,
        modal: true,
        iconCls: "pwnd favwinIcon",
        width: 660,
        height:527,
        resizable: false,
        layout: 'border',
        buttonAlign: 'right',
        renderTo: document.body,
        buttons: [{
            text: WtfGlobal.getLocaleText("crm.CLOSE"),//,
            scope: this,
            handler: function(){
                emailHelp.close();
            }
        }],
        items:[{
            region: 'north',
            height: 75,
            border: false,
            bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml('Email Help','How do I set my E-mail account?','../../images/help.gif')
        },{
            region: 'center',
            border: false,
            bodyStyle: 'background:#f1f1f1;font-size:10px;',
            autoScroll:true,
            html:getEmailHelpContent()
        }]
    });
    emailHelp.show();
}
function emailCampHelp(){
    var emailCampHelp = new Wtf.Window({
        title:'Help',
        id:'emailHelpWin',
        closable: true,
        modal: true,
        iconCls: "pwnd favwinIcon",
        width: 660,
        height:527,
        resizable: false,
        layout: 'border',
        buttonAlign: 'right',
        renderTo: document.body,
        buttons: [{
            text: WtfGlobal.getLocaleText("crm.CLOSE"),//,
            scope: this,
            handler: function(){
                emailCampHelp.close();
            }
        }],
        items:[{
            region: 'north',
            height: 75,
            border: false,
            bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml('Outbound Email Help','How do I set my Outbound E-mail?','../../images/help.gif')
        },{
            region: 'center',
            border: false,
            bodyStyle: 'background:#f1f1f1;font-size:10px;',
            autoScroll:true,
            html:getEmailCampHelpContent()
        }]
    });
    emailCampHelp.show();
}
function getTopHtmlReqField(text, body,img,para){
    if(img===undefined || img=='') {
        img = '../../images/createuser.png';
    }
    var str =  "<div style = 'width:100%;height:100%;position:relative;float:left;'>"
    +"<div style='float:left;height:100%;width:auto;position:relative;'>"
    +"<img src = "+img+"  class = 'adminWinImg'></img>"
    +"</div>"
    +"<div style='float:left;height:100%;width:60%;position:relative;'>"
    +"<div style='font-size:12px;font-style:bold;float:left;margin:15px 0px 0px 10px;width:100%;position:relative;'><b>"+text+"</b></div>"
    +"<div style='font-size:10px;float:left;margin:6px 0px 10px 10px;width:100%;position:relative;'>"+body+"</div>";
    if(para!=undefined)
        str+="<div style='font-size:10px;margin:60px 0px 20px 250px;width:100%;position:absolute;'>"+para+"</div>";
    else
        str+="<div style='font-size:10px;margin:60px 0px 20px 250px;width:100%;position:absolute;'>* indicates Required Field </div>";
    str+="</div>"+"</div>" ;
    return str;
}

function getHelpContent() {
    var str ="<div style='float:left;height:100%;width:97%;position:relative;'>"
    +"<div style='font-size:12px;float:left;margin:4px 0px 10px 10px;width:100%;position:relative;'>"+
    "<ul style='padding-left:15px;'><li type='disc'>Access some useful tips to <b>Get you started</b>, especially if you are a new user. Click on the <img src='../../images/help.png'> icon to view tips.</li>"+
    "<br/><li type='disc'>Unsure about how to use a button or link? Relax! Just point the mouse on it. Count one Mississippi, two...  and a message explaining the button or link will appear (as shown below). No need to search anywhere else! </li> <br/><center><img src='../../images/helpscreen.png'></center><br/>"+
    "<br/><li type='disc'><a class='helplinks' href='mailto:support@deskera.com' target='_blank'>Contact Deskera Support</a>. We are here to help you!</li><br/><li type='disc'>Discuss on <a id='forum' href='http://forum.deskera.com/' target='_blank' class='helplinks'>Deskera Forum</a>. Find useful tips and tricks on <a id='blog' href='http://blog.deskera.com/' target='_blank' class='helplinks'>Deskera Blog</a>.</li></ul>"+
    "</div>"+"</div>" ;
    return str;
}

function getEmailHelpContent() {
    var str ="<div style='font-size:13px;float:left;height:100%;width:97%;position:relative;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;You can set up your email account easily in Deskera. Just follow these steps to get started:"
    +"<div style='font-size:12px;float:left;margin:4px 0px 10px 10px;width:100%;position:relative;'>"+
    "<ul style='padding-left:15px;'><br/><li type='disc'><b>Enter Account Name :</b> Enter a name for your e-mail account. This name is displayed in your Personal Email List. Use a unique name to distinguish it from other E-mail accounts such as 'My Gmail Account'</li>"+
    "<br/><li type='disc'><b>Enter From Name :</b> Enter a name you wish to display as sender's name in all your outgoing e-mails such as 'John from DIA'</li>"+
    "<br/><li type='disc'><b>Enter UserName :</b> Enter the Username you use to login your favorite e-mail account along with the domain name such as john.smith@gmail.com for your Gmail account</li>"+
    "<br/><li type='disc'><b>Enter Password :</b> Provide the password you use to enter your e-mail account. Your personal settings are secure with Deskera</li>"+
    "<br/><li type='disc'><b>Enter Mail Server Address :</b> Provide the incoming mail server address such as imap.gmail.com for your Gmail account. If you are unsure about this information, please check your email server provider settings for configuring an <a href='http://en.wikipedia.org/wiki/Email_client' target='_blank' class='helplinks'>email client</a>. Here are links to some popular e-mail provider settings: <a href='http://mail.google.com/support/bin/answer.py?answer=78799' target='_blank' class='helplinks'>Gmail Settings here</a>, <a href='http://help.yahoo.com/l/us/yahoo/mail/original/mailplus/pop/pop-14.html' target='_blank' class='helplinks'>Find Yahoo Mail Settings here</a>, <a href='http://windowslivehelp.com/solution.aspx?solutionid=a485233f-206d-491e-941b-118e45a7cf1b' target='_blank' class='helplinks'>Find Hotmail Settings here</a></li>"+
    "<br/><li type='disc'><b>Enter Mail Server Protocol :</b> Choose the protocol <a></a>/<a></a> provided by your e-mail service provider for setting up an e-mail client such as IMAP for setting up your Gmail account</li>"+
    "<br/><ul style='padding-left:20px;'><li type='circle'><b>Map folders for your IMAP account :</b> Click on Select to view the folders available in your IMAP account. Choose one of them to sync it with your Trash folder and Sent folder as shown below</li><br/><center><img src='../../images/emailhelp.jpg'></center></ul></ul>"+
    "</div>"+"</div>" ;
    return str;
}
function getEmailCampHelpContent() {
    var str ="<div style='font-size:13px;float:left;height:100%;width:97%;width:100%;position:relative;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp&nbsp;&nbsp;You can set up your Outbound email account easily in Deskera. Just follow these steps to get started:"
    +"<div style='font-size:12px;float:left;margin:4px 0px 10px 10px;width:100%;position:relative;'>"+
    "<ul style='padding-left:15px;'><br/><li type='disc'><b>Enter Email_Id : </b>Enter a name for your e-mail account. This name is displayed in your Personal Email List. Use a unique name to distinguish it from other E-mail accounts such as 'My Gmail Account'.</li>"+
     "<br/><li type='disc'><b>Enter Server : </b>The name of your SMTP Server must also be obtained from your ISP, corporate systems administrator or your mail hosting company. It may or may not be the same as your POP3 Server name. It will usually start with mail or smtp, like this:mail.my-isp-or-company.comsmtp.my-isp-or-company.com.</li>"+
    "<br/><li type='disc'><b>Enter Port : </b>It depends on the SMTP Server. E.g. for gmail SMTP would be 465.</li>"+
    "<br/><li type='disc'><b>Select Mail Protocol : </b>The mail client and mail server can exchange information with each other by choosing any one from SMTP, POP3 & IMAP.</li>"+
    "<br/><li type='disc'><b>Secure Connection : </b>Select a secure connection to provide a secure channel over this insecure network.</li>"+
    "<br/><li type='disc'><b>Enter SMTP UserName : </b>Enter the Username of the Email Id, you have entered to authenticate</li>"+
    "<br/><li type='disc'><b>Enter SMTP Password : </b>Provide the password you use to enter your Email Id. Your personal settings are secure with Deskera</li>"+
    "<br/><center><img src='../../images/emailCamphelp.jpg'></center>"+
    "</div>"+"</div>" ;
    return str;
}
//function deleteHoliday(obj, admin){
//    Wtf.MessageBox.confirm('Confirm', 'Are you sure you would like to delete the holiday?', function(btn){
//        if(btn == "yes")
//            Wtf.getCmp(admin).deleteHoliday(obj.id.substring(4));
//    },
//    this);
//}
//
//function cancelHoliday(){
//    Wtf.get("addHoliday").dom.style.display = 'none';
//}
//function addHoliday(admin){
//    Wtf.getCmp(admin).addHoliday();
//}

function setDldUrl(u){
    document.getElementById('downloadframe').src = u;
}

Wtf.apply(Wtf.form.VTypes, {
    daterange : function(val, field) {
        var date = field.parseDate(val);

        if(!date){
            return;
        }
        if (field.startDateField && (!this.dateRangeMax || (date.getTime() != this.dateRangeMax.getTime()))) {
            var start = Wtf.getCmp(field.startDateField);
            start.setMaxValue(date);
            start.validate();
            this.dateRangeMax = date;
        }
        else if (field.endDateField && (!this.dateRangeMin || (date.getTime() != this.dateRangeMin.getTime()))) {
            var end = Wtf.getCmp(field.endDateField);
            end.setMinValue(date);
            end.validate();
            this.dateRangeMin = date;
        }
        /*
         * Always return true since we're only using this vtype to set the
         * min/max allowed values (these are tested for after the vtype test)
         */
        return true;
    },

    password : function(val, field) {
        if (field.initialPassField) {
            var pwd = Wtf.getCmp(field.initialPassField);
            return (val == pwd.getValue());
        }
        return true;
    },

    passwordText : 'Passwords do not match'
});

Wtf.comboTemplate = new Wtf.XTemplate('<tpl for="."><div wtf:qtip="{[values.hasAccess === false ? "You do not have sufficient permissions to access this data" : "" ]}" class="{[values.hasAccess === false ? "x-combo-list-item disabled-record" : "x-combo-list-item"]}">',
                                                    '{name}',
                                                '</div></tpl>');
Wtf.comboBoxRenderer = function(combo) {
    return function(value) {
        var idx = combo.store.find(combo.valueField, value);
        if(idx == -1)
            return "";
        var rec = combo.store.getAt(idx);
        return rec.get(combo.displayField);
    };
}

Wtf.commentRenderer = function(value,totalcommentcount,newcommentcount) {
    var s="";
    if(totalcommentcount > 0) {
        if(newcommentcount>0){
            s+="<span style='padding-left:1px; float:left; cursor:pointer'><img id = 'TaskNotes_'  class = \'clicktoshowcomment\' src='../../images/addcomment.png' height='12px' wtf:qtitle='New Comment(click to view comment list).' wtf:qtip='"+totalcommentcount+" comment(s) and "+newcommentcount+" new comment(s).' ><span style='position:absolute;' >"+totalcommentcount;
        } else{
            s+="<span style='padding-left:1px; float:left; cursor:pointer'><img id = 'TaskNotes_'  class = \'clicktoshowcomment\' src='../../images/comment.png' height='12px' wtf:qtitle='Comment(click to view comment list).' wtf:qtip='"+totalcommentcount+" comment(s).' ><span style='position:absolute;' >"+totalcommentcount;
        }
        s +="</span></span>";
    }
    return s;
}

Wtf.masterReader = new Wtf.data.Record.create([
    {name:"id"},
    {name:"name"},
    {name:"parentid"},
    {name:"customflag"},
    {name:"modulename"},
    {name:"maxlength"}
]);

Wtf.masterStore = new Wtf.data.Store({
            url: "Common/CRMManager/getMasterComboData.do?customflag=1",
            reader: new Wtf.data.KwlJsonReader({
                root:'data',
                totalProperty:"totalCount"
            }, Wtf.masterReader),
            baseParams:{
                common:'1'
            },
            autoLoad:false
});

Wtf.ComboReader =  new Wtf.data.Record.create([
{
    name: 'id',
    type: 'string'
},
{
    name: 'name',
    type: 'string'
},
{
    name: 'mainid',
    type: 'string'
},
{
    name: 'hasAccess'
}
]);

Wtf.productcomboReader = new Wtf.data.Record.create([
{
    name: 'id',
    type: 'string'
},
{
    name: 'name',
    type: 'string'
},
{
    name: 'unitprice',
    type: 'string'
},
{
    name: 'hasAccess'
},{name: 'productid'},{name: 'desc'},{name: 'productname'},{name: 'salespricedatewise'}
]);
Wtf.productStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'Product/action/getProductname.do',
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, Wtf.productcomboReader),
            baseParams:{
                common:'1'
            },
            autoLoad:false
});

 Wtf.XReader = new Wtf.data.Record.create([
        {
            name: 'id',
            type: 'string'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'relatedto',
            type: 'string'
        },
        {
            name: 'relatednameid',
            type: 'string'
        },
        {
            name: 'productid',
            type: 'string'
        },
        {
            name: 'hasAccess'
        },
        {
            name: 'isarchive'
        }
        ]);

//Wtf.parentaccountstore=new Wtf.data.Store({
//
//            url: Wtf.req.springBase+'Case/action/getAllAccounts.do',
//            reader: new Wtf.data.KwlJsonReader({
//                root:'data'
//            }, Wtf.XReader),
//            autoLoad:false
//});

//Wtf.contactstore=new Wtf.data.Store({
////            url: Wtf.req.base +'crm.jsp?flag=15',
//            url: Wtf.req.springBase+'Contact/action/getContactname.do',
//            reader: new Wtf.data.KwlJsonReader({
//                root:'data'
//            },
//            Wtf.XReader),
////            baseParams:{
////                common:'1'
////            },
//            autoLoad:false
//});

Wtf.contactStoreSearch=new Wtf.data.Store({
    url: Wtf.req.springBase+'Contact/action/getContactname.do',
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    },
    Wtf.XReader),
    autoLoad:false
});

Wtf.parentaccountstoreSearch=new Wtf.data.Store({
    url: Wtf.req.springBase+'Case/action/getAllAccounts.do',
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.XReader),
    autoLoad:false
});

Wtf.caseStatusStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Case Status',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});
Wtf.caseStatusStore.on("load",function(){
    Wtf.StoreMgr.add(Wtf.StoreManagerKeys.casestatus,Wtf.caseStatusStore)
})

Wtf.viewStoreType = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Campaign Type',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.viewStoreStatus = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Campaign Status',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    },  Wtf.ComboReader),
    autoLoad:false
});

Wtf.ownerStore = new Wtf.data.Store({
    url: 'Common/User/getOwner.do',
    baseParams:{
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.leadOwnerStore = new Wtf.data.Store({
    url: 'Common/User/getOwner.do',
    baseParams:{
        common:'1',
        module :Wtf.crmmodule.lead
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.accountOwnerStore = new Wtf.data.Store({
    url: 'Common/User/getOwner.do',
    baseParams:{
        common:'1',
        module :Wtf.crmmodule.account
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.contactOwnerStore = new Wtf.data.Store({
    url: 'Common/User/getOwner.do',
    baseParams:{
        common:'1',
        module :Wtf.crmmodule.contact
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.caseOwnerStore = new Wtf.data.Store({
    url: 'Common/User/getOwner.do',
    baseParams:{
        common:'1',
        module :Wtf.crmmodule.cases
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.caseAssignedUserStore = new Wtf.data.Store({
    url: 'Common/User/getOwner.do',
    baseParams:{
        common:'1',
        module :Wtf.crmmodule.cases,
        allUsers:true,
        noneFlag:true
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.allUsersStore = new Wtf.data.Store({
    url: 'Common/User/getOwner.do',
    baseParams:{
        common:'1',
        module :Wtf.crmmodule.cases,
        allUsers:true,
        noneFlag:false
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.opportunityOwnerStore = new Wtf.data.Store({
    url: 'Common/User/getOwner.do',
    baseParams:{
        common:'1',
        module :Wtf.crmmodule.opportunity
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.productOwnerStore = new Wtf.data.Store({
    url: 'Common/User/getOwner.do',
    baseParams:{
        common:'1',
        module :Wtf.crmmodule.product
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.campaignOwnerStore = new Wtf.data.Store({
    url: 'Common/User/getOwner.do',
    baseParams:{
        common:'1',
        module :Wtf.crmmodule.campaign
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.titleStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Title',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    },  Wtf.ComboReader),
    autoLoad:false
});

Wtf.lsourceStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Lead Source',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});
Wtf.lsourceStore.on("load",function(){
    Wtf.StoreMgr.add(Wtf.StoreManagerKeys.leadsource,Wtf.lsourceStore)
})

//Wtf.relatedToNameStore = new Wtf.data.Store({
//    url: Wtf.req.springBase+'Contact/action/getAllAccounts.do',
//    reader: new Wtf.data.KwlJsonReader({
//        root:'data'
//    }, Wtf.ComboReader),
//    baseParams:{
//                common:'1'
//            },
//    autoLoad:false
//});

Wtf.relatedToNameStoreSearch = new Wtf.data.Store({
    url: Wtf.req.springBase+'Contact/action/getAllAccounts.do',
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.lstatusStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Lead Status',
        common:'1',
        moduleReq:true // to filter Qualified status in Lead Status Combo : Kuldeep Singh
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});
Wtf.lstatusStore.on("load",function(){
    Wtf.StoreMgr.add(Wtf.StoreManagerKeys.leadstatus,Wtf.lstatusStore);
})

Wtf.lratingStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Lead Rating',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});
Wtf.lratingStore.on("load",function(){
    Wtf.StoreMgr.add(Wtf.StoreManagerKeys.leadrating,Wtf.lratingStore);
})

Wtf.industryStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Industry',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});
Wtf.industryStore.on("load",function(){
   Wtf.StoreMgr.add(Wtf.StoreManagerKeys.industry,Wtf.industryStore)
})

Wtf.productcategorystore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Product Category',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.productmanufacturerstore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Manufacturer',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});
// Account

Wtf.noofempStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'No Of Employees',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.accountTypeStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Account Type',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});
Wtf.accountTypeStore.on("load",function(){
	Wtf.StoreMgr.add(Wtf.StoreManagerKeys.accounttype,Wtf.accountTypeStore)
})
// Opportunity

Wtf.opptypeStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Opportunity Type',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});
Wtf.opptypeStore.on("load",function(){
    Wtf.StoreMgr.add(Wtf.StoreManagerKeys.opportunitytype,Wtf.opptypeStore);
})
Wtf.regionStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Region',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});
Wtf.regionStore.on("load",function(){
    Wtf.StoreMgr.add(Wtf.StoreManagerKeys.region,Wtf.regionStore);
})
function createOppStageStore() {
    var oppstageStoreConfig = {
        url: 'Common/CRMManager/getComboData.do',
        baseParams:{
            comboname:'Opportunity Stage',
            common:'1'
        },
        reader: new Wtf.data.KwlJsonReader({
            root:'data'
        }, Wtf.ComboReader),
        autoLoad:false
    };

    if(companyid == "4522e036-0a0d-4bee-8bdb-2dd935467ae7") {
        oppstageStoreConfig['sortInfo'] = {
            field: "id",
            direction: "ASC"
        };
    }
    Wtf.oppstageStore = new Wtf.data.Store(oppstageStoreConfig );
    Wtf.oppstageStore.on("load",function(){
        Wtf.StoreMgr.add(Wtf.StoreManagerKeys.opportunitystage,Wtf.oppstageStore)
    })
}

Wtf.currencyStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Currency',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

// Case

Wtf.caseoriginStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Case Type'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.caseoriginStore.on("load",function(){
    Wtf.StoreMgr.add(Wtf.StoreManagerKeys.caseorigin,Wtf.caseoriginStore)
})

Wtf.cpriorityStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Priority',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    },Wtf.ComboReader),
    autoLoad:false
});

Wtf.cpriorityStore.on("load",function(){
    Wtf.StoreMgr.add(Wtf.StoreManagerKeys.casepriority,Wtf.cpriorityStore);
})
// Activity


Wtf.typeStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Task Type',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.statusStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Task Status',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.reminderStore = new Wtf.data.Store({
    url: 'Common/CRMManager/getComboData.do',
    baseParams:{
        comboname:'Reminder',
        common:'1'
    },
    reader: new Wtf.data.KwlJsonReader({
        root:'data'
    }, Wtf.ComboReader),
    autoLoad:false
});

Wtf.permFeatureStore = new Wtf.data.Store({
    url: 'Common/PermissionHandler/getFeatureList.do',
    reader: new Wtf.data.KwlJsonReader({
        root: 'data'
    },new Wtf.data.Record.create(
        ['featureid','featurename','displayfeaturename']
        )),
    autoLoad:false
});

Wtf.permActivityStore = new Wtf.data.Store({
    url: 'Common/PermissionHandler/getActivityList.do',
    reader: new Wtf.data.KwlJsonReader({
        root: 'data'
    },new Wtf.data.Record.create(
        ['featureid','activityid','activityname','displayactivityname']
        )),
    autoLoad:false
});

Wtf.LeadTypeStore = new Wtf.data.SimpleStore({   // Lead Types
    fields: ['id','name'],
    data : [
        ["0","Individual"],
        ["1","Company"]
    ]
});

Wtf.common.Uid = function(_userid, _type){
    this.userid = _userid;
    this.type = _type;
}
function chkownerload(crmModule)
{
    if(crmModule==undefined)
       crmModule = "";
   switch(crmModule) {
        case Wtf.crmmodule.campaign:
            Wtf.campaignOwnerStore.load();
            break;
        case Wtf.crmmodule.lead:
            Wtf.leadOwnerStore.load();
            break;
        case Wtf.crmmodule.account:
            Wtf.accountOwnerStore.load();
            break;
        case Wtf.crmmodule.contact:
            Wtf.contactOwnerStore.load();
            break;
        case Wtf.crmmodule.opportunity:
            Wtf.opportunityOwnerStore.load();
            break;
        case Wtf.crmmodule.cases:
            Wtf.caseOwnerStore.load();
            Wtf.caseAssignedUserStore.load();
            break;
        case Wtf.crmmodule.product:
            Wtf.productOwnerStore.load();
            break;
        default :  Wtf.ownerStore.load({params:{
                module :crmModule
            }});
            break;
   }
}

function getModuleOwnerStore(crmModule) {
    if(crmModule==undefined)
       crmModule = "";
   switch(crmModule) {
        case Wtf.crmmodule.campaign:
            return Wtf.campaignOwnerStore;
            break;
        case Wtf.crmmodule.lead:
            return Wtf.leadOwnerStore;
            break;
        case Wtf.crmmodule.account:
            return Wtf.accountOwnerStore;
            break;
        case Wtf.crmmodule.contact:
            return Wtf.contactOwnerStore;
            break;
        case Wtf.crmmodule.opportunity:
            return Wtf.opportunityOwnerStore;
            break;
        case Wtf.crmmodule.cases:
            return Wtf.caseOwnerStore;
            break;
        case Wtf.crmmodule.product:
            return Wtf.productOwnerStore;
            break;
        default : return Wtf.ownerStore;
            break;
   }
}
function chktitleload()
{
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.title)){
        Wtf.titleStore.load();
        Wtf.StoreMgr.add(Wtf.StoreManagerKeys.title,Wtf.titleStore)
    }
}

//function chkparentaccountstoreload()
//{
//    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.parentaccount)){
//        Wtf.parentaccountstore.load();
//        Wtf.StoreMgr.add(Wtf.StoreManagerKeys.parentaccount,Wtf.parentaccountstore);
//    }
//}

//function chkcontactstorestoreload()
//{
//    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.contactstore)){
//        Wtf.contactstore.load();
//        Wtf.StoreMgr.add(Wtf.StoreManagerKeys.contactstore,Wtf.contactstore);
//    }
//}

function chkleadsourceload() {
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.leadsource)) {
        Wtf.lsourceStore.load();
    }
}
//function chkrelatedToNameStoreload()
//{
//    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.relatedtonamestore)){
//        Wtf.relatedToNameStore.load();
//        Wtf.StoreMgr.add(Wtf.StoreManagerKeys.relatedtonamestore,Wtf.relatedToNameStore)
//    }
//}
function chkproductStoreload()
{
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.productstore)){
        Wtf.productStore.load();
        Wtf.StoreMgr.add(Wtf.StoreManagerKeys.productstore,Wtf.productStore)
    }
}
function chknoofempStoreload()
{
    if(!Wtf.StoreMgr.containsKey("noofempStore")){
        Wtf.noofempStore.load();
        Wtf.StoreMgr.add("noofempStore",Wtf.noofempStore)
    }
}
function chkaccounttypeload() {
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.accounttype)) {
        Wtf.accountTypeStore.load();
    }
}

function chkindustryload() {
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.industry)) {
        Wtf.industryStore.load();
    }
}

function chkoppstageload() {
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.opportunitystage)) {
        Wtf.oppstageStore.load();
    }
}

function chkpriorityload() {
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.casepriority)) {
        Wtf.cpriorityStore.load();
    }
}

function chkstatusload() {
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.casestatus)) {
        Wtf.caseStatusStore.load();
    }
}

function chkviewStoreTypeload()
{
    if(!Wtf.StoreMgr.containsKey("viewtype")){
        Wtf.viewStoreType.load();
        Wtf.StoreMgr.add("viewtype",Wtf.viewStoreType)
    }
}

function chkviewStoreStatusload()
{
    if(!Wtf.StoreMgr.containsKey("viewstatus")){
        Wtf.viewStoreStatus.load();
        Wtf.StoreMgr.add("viewstatus",Wtf.viewStoreStatus)
    }
}

function chkproductcategorystoreload()
{
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.productcategory)){
        Wtf.productcategorystore.load();
        Wtf.StoreMgr.add(Wtf.StoreManagerKeys.productcategory,Wtf.productcategorystore)
    }
}

function chklstatusStoreload() {
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.leadstatus)) {
        Wtf.lstatusStore.load();
    }
}

function chklratingStoreload() {
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.leadrating)) {
        Wtf.lratingStore.load();
    }
}

function chkopptypeStoreload() {
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.opportunitytype)){
        Wtf.opptypeStore.load();
    }
}

function chkregionStoreload() {
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.region)){
        Wtf.regionStore.load();
    }
}

function chkcaseoriginStoreload() {
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.caseorigin)) {
        Wtf.caseoriginStore.load();
    }
}
function chktasktypeload()
{
    if(!Wtf.StoreMgr.containsKey("tasktype")){
        Wtf.typeStore.load();
        Wtf.StoreMgr.add("tasktype",Wtf.typeStore)
    }
}

function chktaskstatusload()
{
    if(!Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.taskstatus)){
        Wtf.statusStore.load();
        Wtf.StoreMgr.add(Wtf.StoreManagerKeys.taskstatus,Wtf.statusStore)
    }
}

function chkreminderload()
{
    if(!Wtf.StoreMgr.containsKey("reminder")){
        Wtf.reminderStore.load();
        Wtf.StoreMgr.add("reminder",Wtf.reminderStore)
    }
}

function chkPermFeatureLoad()
{
    if(!Wtf.StoreMgr.containsKey("permfeature")){
        Wtf.permFeatureStore.load();
        Wtf.StoreMgr.add("permfeature",Wtf.permFeatureStore)
    }
}
function chkPermActivityLoad()
{
    if(!Wtf.StoreMgr.containsKey("permactivity")){
        Wtf.permActivityStore.load();
        Wtf.StoreMgr.add("permactivity",Wtf.permActivityStore)
    }
}
function showChart(id1,dataflag,swf,xmlpath,frm,to,view, reportId){
    if(Wtf.getCmp(id1)){
        Wtf.getCmp(id1).on("render", function(){
            var pid = Wtf.getCmp(id1).body.dom.id;			
            if(xmlpath.indexOf("&year") != -1) {
//                data= Wtf.req.base + "charData.jsp?mode=551&flag="+dataflag+xmlpath.substring(xmlpath.indexOf("&year"),xmlpath.length);
            } else if(frm != undefined && to != undefined) {
                if(view == undefined)
                    view = "2";
                if(dataflag.indexOf("?") != -1) {
                    dataflag += "&frm="+encodeURIComponent(frm.toString())+"&to="+encodeURIComponent(to.toString())+"&view="+view+"&reportid="+reportId;
                } else {
                    dataflag += "?frm="+encodeURIComponent(frm.toString())+"&to="+encodeURIComponent(to.toString())+"&view="+view+"&reportid="+reportId;
                }
                if(view == "1") {
                    var year = "";
                    var d;
                    if(frm != "" && to != ""){
                        d = new Date(frm.toString())
                        year = d.getFullYear();
                    } else {
                        d = new Date();
                        year = d.getFullYear();
                    }
                    if(reportId == 11 || reportId == 52 || reportId == 53 || reportId == 54 || reportId == 42 || reportId == 39) {
                        xmlpath += "&year="+year;
                    }
                }
            }
            if(reportId==52 ||reportId==11 ||reportId==39 ||reportId==42 ||reportId==53 ||reportId==54){
                if(reportId==52){
                    xmlpath += "&title1=Converted Leads into Account";
                } else if(reportId==11){
                    xmlpath += "&title1=Converted Leads";
                } else if(reportId==39){
                    xmlpath += "&title1=Qualified Leads&show_legend=true";
                } else if(reportId==42){
                    xmlpath += "&title1=Contacted Leads";
                } else if(reportId==53){
                    xmlpath += "&title1=Converted Leads into Opportunity";
                } else if(reportId==54){
                    xmlpath += "&title1=Converted Leads into Contacts";
                }
                xmlpath += "&title2=Total no of Leads";
                xmlpath += "&doublebar=Lead";
            }
            createNewChart(swf,'krwpie', '100%', '100%', '8', '#FFFFFF', xmlpath,dataflag, pid);
        }, this); 
    }
}
function globalModuleChart(id,id1,swf1,dataflag1,mainid,xmlpath1,id2,swf2,dataflag2,xmlpath2,frm,to){//,nondeleted,deleted){
    var reportPanel =Wtf.getCmp(id);
    if(reportPanel==null){
        reportPanel = new Wtf.Panel({
            id: id,
            border : false,
            title : WtfGlobal.getLocaleText("crm.report.chartview"),//"Chart View",
            autoScroll:true,
            layout:'border',
            closable: true,
            style:'padding:20px',
            defaults:{border:false},
         //   iconCls:(Wtf.isChrome?'accountingbase chartChrome':'accountingbase chart'),
            items:[new Wtf.Panel({
                id:"msgid"+id1,
                region:"south",
                width:10,
                baseCls:"chartmsg",
  //              html:"<b>Note:</b> Amount in <span class='currency-view'>"+WtfGlobal.getCurrencySymbol()+"  ("+WtfGlobal.getCurrencyName()+")</span>",
                border : false,
                frame:false
            }),new Wtf.Panel({
                    id:id1,
                    region:"center",
                    defaults:{border:false},
                    border : false,
                frame:false
            })
            ,new Wtf.Panel({
                id:id2,
                region:"east",
                width:600,
                defaults:{border:false},
                border : false,
                frame:false
            })
        ]
        });
        showChart(id1,dataflag1,swf1,xmlpath1,frm,to)//,nondeleted,deleted);
        showChart(id2,dataflag2,swf2,xmlpath2,frm,to);
        Wtf.getCmp(mainid).add(reportPanel);
    }
    Wtf.getCmp(mainid).setActiveTab(reportPanel);
    Wtf.getCmp(mainid).doLayout();
}
function checkDates(frmdate,todate){
    var fromDate    =frmdate.getValue()?frmdate.getValue().getTime():"";
    var toDate    = todate.getValue()?todate.getValue().getTime():"";
    var proceed = true;
    var msg = "";
    if(fromDate == "" || toDate == "" ) {
        msg = 15;
        proceed = false;
    }else if(fromDate > toDate ) {
        msg = 16;
        todate.setValue("");
        proceed = false;
    }
    if(msg){
        ResponseAlert(msg);
    }
    return proceed;
}
function globalChart(id,id1,swf,dataflag,mainid,xmlpath,id2,swf2,dataflag2,xmlpath2,tipTitle, reportId, frm, to,baseobj)
{
    var variancereportPanel =Wtf.getCmp(id);
    this.viewChart = "pie";
    if(id2.indexOf('chart view')!=-1)
        var title= id2;
    else if(tipTitle!=undefined){
        var maintitle=Wtf.util.Format.ellipsis(tipTitle,16);
        title = "<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Campaign Mail Status'>"+maintitle+"</div>";
    }
    else
        title=undefined;

    var tbarItems = [];

    this.barChartButton = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.report.barchartbtn"),//,
            scope:this,
            tooltip:WtfGlobal.getLocaleText("crm.report.barchartbtn.ttip"),//'Get the graphical bar view. ',
            iconCls:'barchartIcon',
            handler:function(){
                this.viewChart = "bar";
                clickChartView(this.viewChart,id1,dataflag2,swf2,xmlpath2,frm,to, undefined, reportId)
            }
        });

        this.pieChartButton =new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.report.piechartbtn"),//,
            scope:this,
            tooltip:WtfGlobal.getLocaleText("crm.report.piechartbtn.ttip"),//'Get the graphical pie view. ',
            iconCls:'piechartIcon',
            handler:function(){
                this.viewChart = "pie";
                clickChartView(this.viewChart,id1,dataflag,swf,xmlpath,frm,to);
            }
        });
        
    if(reportId == 10 || reportId == 17) {
        this.from = new Wtf.Toolbar.TextItem('From');
        this.to = new Wtf.Toolbar.TextItem('To');

        this.fromPeriod = new Wtf.form.DateField({
            emptyText:WtfGlobal.getLocaleText("crm.date.mtytxt"),// --Select Date",
            width: 130,
            value: frm != ""?frm:"",
            scope:this
        });

        this.toPeriod = new Wtf.form.DateField({
            emptyText:WtfGlobal.getLocaleText("crm.date.mtytxt"),// --Select Date--",
            width: 130,
            value: to != ""?to:"",
            scope:this
        });

        this.fil = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.FILTERBUTTON"),//"Filter",
            scope:this,
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.importlog.toptoolbar.fetchBTN.ttip")//"Choose a date range using 'From' and 'To' fields to filter records created in the specified time duration."
            },
            iconCls:'pwnd addfilter',
            handler:function(){
                frm = this.fromPeriod.getValue();
                to = this.toPeriod.getValue();
                      //      todate = Wtf.formatDate(toDate.add(Date.DAY,1).add(Date.SECOND,-1),0);
                if(frm!=""&&to!=""){
                    frm =  Wtf.formatDate(frm,0);
                    to = Wtf.formatDate(to,0);
                }
                if(checkDates(this.fromPeriod,this.toPeriod)) {
//                    frm =  Wtf.formatDate(frm,0);
//                    to = Wtf.formatDate(to,0);
                    if(this.viewChart == "bar")
                        clickChartView(this.viewChart,id1,dataflag2,swf2,xmlpath2,frm,to, undefined, reportId)
                    else {
                        clickChartView(this.viewChart,id1,dataflag,swf,xmlpath,frm,to, undefined, reportId);
                    }
                }
            }
        }); 

        this.reset = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.RESETBUTTON"),//"Reset",
            scope:this,
            iconCls:'pwndCRM reset',
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.editor.toptoolbar.resetBTN.ttip")//'Click to remove any filter settings and view all records.'
            },
            handler:function(){
                this.fromPeriod.setValue("");
                this.toPeriod.setValue("");
                frm="";
                to="";
                if(this.viewChart == "bar")
                    clickChartView(this.viewChart,id1,dataflag2,swf2,xmlpath2,frm,to, undefined, reportId)
                else {
                    clickChartView(this.viewChart,id1,dataflag,swf,xmlpath,frm,to, undefined, reportId);
                }
            }

        });
         
        tbarItems = [this.pieChartButton,'-',this.barChartButton,'->',this.from,this.fromPeriod,'-',this.to,this.toPeriod,'-',this.fil,'-',this.reset ];
    }
    
    if(reportId != 57 && reportId != 56 && reportId != 17 && reportId != 10 &&  reportId != 11 && reportId != 52 && reportId != 53 && reportId != 54 && reportId != 42 && reportId != 39){
        tbarItems = [this.pieChartButton,'-',this.barChartButton];
    }
    
    if(reportId == 11 || reportId == 52 || reportId == 53 || reportId == 54 || reportId == 42 || reportId == 39) {
        this.from = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.fromdate.label"));
        this.to = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.todate.label"));

        this.fromPeriod = new Wtf.form.DateField({
            emptyText:WtfGlobal.getLocaleText("crm.date.mtytxt"),// --Select Date--",
            width: 130,
            value: frm != ""?frm:"",
            scope:this
        });
        
        this.toPeriod = new Wtf.form.DateField({
            emptyText:WtfGlobal.getLocaleText("crm.date.mtytxt"),// --Select Date--",
            width: 130,
            value: to != ""?to:"",
            scope:this
        });
        
        this.view = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.view"));
        
        var viewStore = new Wtf.data.SimpleStore({
            fields:['id','name'],
            data: [["1",WtfGlobal.getLocaleText("crm.reportlink.contactedleads.weekdayview")],
            ["2",WtfGlobal.getLocaleText("crm.reportlink.contactedleads.monthalyview")],
            ["3",WtfGlobal.getLocaleText("crm.reportlink.contactedleads.quarterlyview")],
            ["4",WtfGlobal.getLocaleText("crm.reportlink.contactedleads.yearlyview")]],
            autoLoad: true
        });
        this.viewCombo=new Wtf.form.ComboBox({
            id:reportId+'viewCombo',
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: viewStore,
            displayField: 'name',
            editable:false,
            typeAhead: true,
            allowBlank:false,
            value:2,
            valueField:'id',
            anchor:'100%',
            emptyText:'--Select View--',
            width:120
        });
        
        this.viewCombo.on("select",function(combo){
            var view = combo.getValue();
            frm = this.fromPeriod.getValue();
            to = this.toPeriod.getValue();
            if(frm!=""&&to!=""){
                frm =  Wtf.formatDate(frm,0);
                to = Wtf.formatDate(to,0);
            }
            if(frm==""&&to==""){
                clickChartView('bar',id1,dataflag2,swf2,xmlpath2,frm,to, view, reportId)
            }            
            else if(checkDates(this.fromPeriod,this.toPeriod)){
//                frm =  Wtf.formatDate(frm,0);
//                to = Wtf.formatDate(to,0);
                clickChartView('bar',id1,dataflag2,swf2,xmlpath2,frm,to, view, reportId)
            }
        },this);  

        frm = this.fromPeriod.getValue();
        to = this.toPeriod.getValue();

        this.fil = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.FILTERBUTTON"),//"Filter",
            scope:this,
            tooltip: {
                text:WtfGlobal.getLocaleText("crm.importlog.toptoolbar.fetchBTN.ttip")// "Choose a date range using 'From' and 'To' fields to filter records created in the specified time duration."
            },
            iconCls:'pwnd addfilter',
            handler:function(){
                var view = this.viewCombo.getValue();
                frm = this.fromPeriod.getValue();
                to = this.toPeriod.getValue();
                if(frm!=""&&to!=""){
                    frm =  Wtf.formatDate(frm,0);
                    to = Wtf.formatDate(to,0);
                }
                    if(checkDates(this.fromPeriod,this.toPeriod)){
                        clickChartView('bar',id1,dataflag2,swf2,xmlpath2,frm,to, view, reportId)
                    }
                }            
        });

        this.reset = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.RESETBUTTON"),//"Reset",
            scope:this,
            iconCls:'pwndCRM reset',
            tooltip: {
                text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.resetBTN.ttip") //'Click to remove any filter settings and view all records.'
            },
            handler:function(){
                this.viewCombo.setValue(2);
                var view = this.viewCombo.getValue();
                this.fromPeriod.setValue("");
                this.toPeriod.setValue("");
                frm="";
                to="";
                clickChartView('bar',id1,dataflag2,swf2,xmlpath2,frm,to, view, reportId)
            }

        });
        tbarItems = ['->',this.view,this.viewCombo,'-',this.from,this.fromPeriod,'-',this.to,this.toPeriod,'-',this.fil,'-',this.reset ];
    }
    if(variancereportPanel==null){
        var htmltemp = undefined;

        variancereportPanel = new Wtf.Panel({
            id: id,
            border : false,
            title : title==undefined?WtfGlobal.getLocaleText("crm.report.chartview"):title,
            autoScroll:true,
            closable: true,
            iconCls:getTabIconCls(Wtf.etype.piechart),
            items:[new Wtf.Panel({
                id:id2,
                height:20,
                bodyStyle:'background-color:white;',
                defaults:{
                    border:false
                },
                html:htmltemp,
                border : false,
                tbar:tbarItems.length != 0 ?tbarItems:undefined,
                frame:false
            }),new Wtf.Panel({
                id:id1,
                height:600,
                bodyStyle:'background-color:white;',
                defaults:{border:false},
                border : false,
                frame:false
            })]
        });

//        variancereportPanel.on('activate',function(){if(baseobj!=undefined||baseobj!=null)baseobj.bbar.hide()},this)
//        variancereportPanel.on('deactivate',function(){if(baseobj!=undefined||baseobj!=null)baseobj.bbar.show()},this)
//        variancereportPanel.on('close',function(){if(baseobj!=undefined||baseobj!=null)baseobj.bbar.show()},this)
        if(swf2=='false')
            Wtf.getCmp(id2).hide();
        Wtf.getCmp(mainid).add(variancereportPanel);
    }
    if(reportId == 11 || reportId == 52 || reportId == 53 || reportId == 54 || reportId == 42 || reportId == 39 || reportId==57 ) {
        showChart(id1,dataflag2,swf2,xmlpath2,frm,to, undefined, reportId);
    } else {
        showChart(id1,dataflag,swf,xmlpath,frm,to, undefined, reportId);
    }
    Wtf.getCmp(mainid).setActiveTab(variancereportPanel);
    Wtf.getCmp(mainid).doLayout();
}

function clickChartView(name,id,dataflag,swf,xmlpath,frm,to,view, reportId) {
	if((frm!=undefined && to!=undefined)&&(frm!="" && to!="")){
		frm=new Date(frm).getTime();
		to=new Date(to).getTime();
	}
	if(frm == undefined) {
        if(name=='pie')
            Wtf.get('chartPreviewText').dom.innerHTML="You are viewing Pie Chart.";
        else
            Wtf.get('chartPreviewText').dom.innerHTML="You are viewing Bar Chart.";
    }
    var pid = Wtf.getCmp(id).body.dom.id;
    if(xmlpath.indexOf("&year") != -1) {
//        data= Wtf.req.base + "charData.jsp?mode=551&flag="+dataflag+xmlpath.substring(xmlpath.indexOf("&year"),xmlpath.length);
    } else if(frm != undefined && to != undefined) {
        if(view == undefined) {
            view = "2";
        }
        if(dataflag.indexOf("?") != -1) {
            dataflag += "&frm="+encodeURIComponent(frm.toString())+"&to="+encodeURIComponent(to.toString())+"&view="+view+"&reportid="+reportId;
        } else {
            dataflag += "?frm="+encodeURIComponent(frm.toString())+"&to="+encodeURIComponent(to.toString())+"&view="+view+"&reportid="+reportId;
        }
        if(view == "1") {
            var year = "";
            var d;
            if(frm != "" && to != ""){
                d = new Date(frm.toString())
                year = d.getFullYear();
            } else {
                d = new Date();
                year = d.getFullYear();
            }
            if(reportId == 11 || reportId == 52 || reportId == 53 || reportId == 54 || reportId == 42 || reportId == 39 ) {
                xmlpath += "&year="+year;
            }
        }
    }
    if(reportId==52 ||reportId==11 ||reportId==39 ||reportId==42 ||reportId==53 ||reportId==54){
        if(reportId==52){
            xmlpath += "&title1=Converted Leads into Account&show_legend=true";
        } else if(reportId==11){
            xmlpath += "&title1=Converted Leads&show_legend=true";
        } else if(reportId==39){
            xmlpath += "&title1=Qualified Leads&show_legend=true&show_legend=true";
        } else if(reportId==42){
            xmlpath += "&title1=Contacted Leads&show_legend=true";
        } else if(reportId==53){
            xmlpath += "&title1=Converted Leads into Opportunity&show_legend=true";
        } else if(reportId==54){
            xmlpath += "&title1=Converted Leads into Contacts&show_legend=true";
        }
        xmlpath += "&title2=Total no of Leads";
        xmlpath += "&doublebar=ConvertedLeadstoAccount";
    }
    createNewChart(swf,'krwpie', '100%', '100%', '8', '#FFFFFF', xmlpath,dataflag, pid);
}

function chkalltimeZoneload(){

    if(!Wtf.StoreMgr.containsKey("alltimeZone")){
        Wtf.timezoneStore.load();
        Wtf.StoreMgr.add("alltimeZone",Wtf.timezoneStore)
    }
}

function getCountryName(){
    if(!Wtf.StoreMgr.containsKey("country")){
        Wtf.countryStore.load();
        Wtf.countryStore.on("load", function() {
           Wtf.StoreMgr.add("country",Wtf.countryStore);
        });
    }
}

//function reloadAccountStore() {
//    Wtf.parentaccountstore.reload();
//    Wtf.relatedToNameStore.reload();
//}
//function reloadContactStore() {
//    Wtf.contactstore.reload();
//}
function reloadProductStore() {
    Wtf.productStore.reload();
}

Wtf.ModuleGlobalStoreReload = function(moduleName) {
    if(moduleName=="Product") {
        reloadProductStore();
    } /*else if(moduleName=="Account") {
        reloadAccountStore();
    } else if(moduleName=="Contact") {
        reloadContactStore();
    }*/
}

Wtf.dfRec = new Wtf.data.Record.create ([
{
    name:'formatid'
},
{
    name:'name'
}
]);

Wtf.dfStore=new Wtf.data.Store({
    url: "Common/KwlCommonTables/getAllDateFormats.do",
    baseParams:{
        mode:37
    },
    reader: new Wtf.data.KwlJsonReader({
        root: "data"
    },Wtf.dfRec),
    autoLoad:false
});

Wtf.tfStore=new Wtf.data.SimpleStore({
    fields:['timeformatid','name'],
    data: [
    [1,"12 Hour Format"],
    [2,"24 Hour Format"]]
});

function chkalldateFormatload(){

    if(!Wtf.StoreMgr.containsKey("alldateFormatload")){
        Wtf.dfStore.load();
        Wtf.StoreMgr.add("alldateFormatload",Wtf.dfStore)
    }
}

function openUpdate(objId,linkedobj) {
    Wtf.getCmp(objId).showUpdates(linkedobj);
}

function openGraph(objId,linkedobj) {
    Wtf.getCmp(objId).showChart(linkedobj);
}

// TODO
//function openBarGraph(objId,linkedobj) {
//    Wtf.getCmp(objId).showBarChart(linkedobj);
//}

function openQuickAdd(objId,linkedobj,code) {
    Wtf.getCmp(objId).showQuickAdd(linkedobj,code);
}
String.prototype.endsWith = function(str) {
    return (this.match(str+"$")==str)
}

String.prototype.replaceLast = function (what, replacement) {
    return this.split(' ').reverse().join(' ').replace(new RegExp(what), replacement).split(' ').reverse().join(' ');
};

function isValidEmail(emailStr){if(emailStr.length==0){return true;}
var lastChar=emailStr.charAt(emailStr.length-1);if(!lastChar.match(/[^\.]/i)){return false;}
var emailArr=emailStr.split(/[,;]/);for(var i=0;i<emailArr.length;i++){var emailAddress=emailArr[i];if(emailAddress.trim()!=''){if(!/^\s*[\w.%+\-&']+@([A-Z0-9-]+\.)*[A-Z0-9-]+\.[A-Z]{2,4}\s*$/i.test(emailAddress) &&
      !/^.*<[A-Z0-9._%+\-&']+?@([A-Z0-9-]+\.)*[A-Z0-9-]+\.[A-Z]{2,4}>\s*$/i.test(emailAddress)){return false;}}}
return true;}

//Modulebuilder settings

var nameRegex=/^([a-zA-Z_-]+\w*\s*\w*)*$/;

var fieldLabelRegex=/^([a-zA-Z_\/.*-]+\w*\s*\w*)*$/;
var columnNameRegex=/^([a-zA-Z_]+\w*\s*\w*)*$/;
var regexText='Please enter in correct format';

function msgBoxShow(choice, type){
    var strobj = [];
    switch (choice) {
        case 1:
            strobj = ["Error", "Enter points between 1 to 999"];
            break;
        case 2:
            strobj = ["Error", "Enter time limit between 1 to 60 seconds"];
            break;
        case 3:
            strobj = ["Error", "Please fill in all choices"];
            break;
        case 4:
            strobj = ["Error", "Error occurred while connecting to the server"];
            break;
       case 5:
            strobj = ["Error", "Error occurred at server"];
            break;
       case 6:
            strobj = ["Success", "Form submitted successfully"];
            break;
       case 7:
            strobj = ["Success", "Form saved successfully"];
            break;
       case 8:
            strobj = ["Success", "Form deleted successfully"];
            break;
       case 9:
            strobj = ["Failure", "Some error occured while deleting the form"];
            break;
       case 10:
            strobj = ["Success", "Module deleted successfully"];
            break;
       case 11:
            strobj = ["Success", "The template has been saved"];
            break;
        case 12:
            strobj = ["Error", "Could not save template. Please try again."];
            break;
        case 13:
            strobj = ["Success", "Report deleted successfully"];
            break;
        case 14:
            strobj = ["Success", "Config options saved successfully."];
            break;
         case 15:
            strobj = ["Warning", "Please select a transaction to search"];
            break;
        case 26:
            strobj = ["Error", "Select a valid holiday date" ];
            break;
        case 27:
            strobj = ["Error", "Enter a valid holiday description" ];
            break;
        case 28:
            strobj = ["Error", "Error occured at server side while updating record" ];
            break;
        case 29:
            strobj = ["Info", "Please select atleast one record" ];
            break;
        case 30:
            strobj = ["Success", "Module Configuration deleted successfully"];
            break;
        case 31:
            strobj = ["Success", "Configured Tab successfully" ];
            break;
        case 32:
            strobj = ["Success", "Renderer function created succussfully" ];
            break;
        case 33:
            strobj = ["Error", "Error in creating renderer function " ];
            break;
        case 34:
            strobj = ["Error", "Both name & renderer are required  " ];
            break;
        case 35:
            strobj = ["Success", "Renderer function edited succussfully" ];
            break;
        case 36:
            strobj = ["Error", "Error in editing renderer function "];
            break;
        case 37:
            strobj = ["Error", "Please enter valid search filter" ];
            break;
        case 38:
            strobj = ["Error", "Please enter atleast one filter" ];
            break;
        case 39:
            strobj = ["Error", "Box label already exist" ];
            break;
        case 40:
            strobj = ["Error", "Please enter valid box label" ];
            break;
        case 41:
            strobj = ["Error", "Either fieldlabel is invalid or there are no box labels" ];
            break;
        case 42:
            strobj = ["Success", "Notification deleted successfully"];
            break;
        case 43:
            strobj = ["Success", "Permissions assigned successfully"];
            break;
        case 44:
            strobj = ["Success", "Rule Deleted successfully"];
            break;
        case 45:
            strobj = ["Failure", "Error Occured at server side while deleting rule"];
            break;
        case 46 :
            strobj = ["Info","Dashboard configured successfully"];
            break;
        case 47 :
            strobj = ["Info","Module link added to Dashboard successfully."];
            break;
        default:
            strobj = [choice[0], choice[1]];
            break;
    }
	var iconType = Wtf.MessageBox.INFO;
	if(type == 0)
	    iconType = Wtf.MessageBox.WARNING;
    if(type == 1)
	    iconType = Wtf.MessageBox.ERROR;
    else if(type == 2)
         iconType = Wtf.MessageBox.INFO;
    Wtf.MessageBox.show({
        title: strobj[0],
        msg: strobj[1],
        buttons: Wtf.MessageBox.OK,
        animEl: 'mb9',
        icon: iconType
    });
}

function getColumnName(name,Key){
    return "mb_"+Key+"_"+name;
}

// store for gridsummary plugin
var summaryTypeStore = new Wtf.data.SimpleStore({
    fields :['id', 'name'],
    data:[['None','None'],['sum','sum'],['count','count'],['max','max'],['min','min'],['average','average']]
});

Wtf.ux.comboBoxRenderer = function(combo) {
    return function(value) {
        var idx = combo.store.find(combo.valueField, value);
        if(idx == -1)
            return "";
        var rec = combo.store.getAt(idx);
        return rec.get(combo.displayField);
    };
}

function getHeader(img,myTitle,description){
    var str =  "<div style = 'width:100%;height:100%;position:relative;float:left;'>"
                    +"<div style='float:left;height:100%;width:auto;position:relative;'>"
                    +"<img src = "+img+" style = 'width:40px;height:52px;margin:5px 5px 5px 5px;'></img>"
                    +"</div>"
                    +"<div style='float:left;height:100%;width:60%;position:relative;'>"
                    +"<div style='font-size:12px;font-style:bold;float:left;margin:15px 0px 0px 10px;width:100%;position:relative;'><b>"+myTitle+"</b></div>"
                    +"<div style='font-size:10px;float:left;margin:15px 0px 10px 10px;width:100%;position:relative;'>"+description+"</div>"
                    +"</div>"
                    +"</div>" ;
    return str;
}
/**********Module builder functions start ************/
Wtf.mbuild.permActions = {
    button: 1,
    addRecord: 2,
    editRecord: 3,
    deleteRecord: 4,
    addComment: 5,
    deleteComment: 6,
    addDocument: 7,
    deleteDocument: 8,
    mtabView: 9
};
var _reportHardcodeStr = "X_X";
var gridConfig = false;

Wtf.getDateFormat = function() {
    return "Y-m-d";
    //return Wtf.pref.DateFormat;
}

//Move row up/down in grid
function changeseq(seq,flag, gridid){
    var store =Wtf.getCmp(gridid).getStore();
    var index1 = store.find('seq',seq);
    var orgseq = seq;
    if(index1>-1){
        if(flag=="1"){
            seq++;
        }else if(flag=="0"){
            seq--;
        }
        var record1 = store.getAt(index1);
        var index2 = store.find('seq',seq);
        if(index2>-1){
            var record2 = store.getAt(index2);
            store.remove(record1);
            store.remove(record2);
            if(flag=="0"){
                store.insert(index2,record1);
                store.insert(index1,record2);
            }else if(flag=="1"){
                store.insert(index1,record2);
                store.insert(index2,record1);
            }
            record1.set('seq',seq);
            record2.set('seq',orgseq);
        }
    }
 }

Array.prototype.inArray = function (value){
// Returns true if the passed value is found in the
// array. Returns false if it is not.
    var i;
    for (i=0; i < this.length; i++){
        if (this[i] == value){
            return true;
        }
    }
    return false;
};

function URLDecode(str) {
    str = str.replace(new RegExp('\\+','g'),' ');
    return unescape(str);
}
//This function is used in the "Wtf.ux.grid.GridSummary" Component
Wtf.getValidNumberOrDefault = function(value , defaultval){
    var val = 0;
    try {
        val =  Wtf.num(isNaN(parseFloat(value)) ? 0 : parseFloat(value),defaultval);
    } catch(ex){
        val = 0;
    } finally {
        return val;
    }
}
// store for gridsummary plugin
var summaryTypeStore = new Wtf.data.SimpleStore({
    fields :['id', 'name'],
    data:[['None','None'],['sum','sum'],['count','count'],['max','max'],['min','min'],['average','average']]
});
// to check whether grid summary is allowed on column
function isAllowed(combo,record,index,xtype){
    var isallowed=false;
    if (xtype == 'Text' || xtype=='Number(Integer)' || xtype=='Number(Float)' || xtype=='textfield' || xtype=='numberfield'){
        isallowed=true;
        if ((xtype == 'Text' || xtype == 'textfield') && record.data.id != 'count' && record.data.id != 'None'  ){
            isallowed=false;
            msgBoxShow(['Error', "Cannot do "+ record.data.name+" on Text"], Wtf.MessageBox.Error);
        }

    }else{
        msgBoxShow(['Error', "Not an allowed column"], Wtf.MessageBox.Error);
    }
    return isallowed;
}

function validateedit(e){
    var fieldLabel = e.value;
    if(fieldLabel.match(fieldLabelRegex)==null || fieldLabel == 'Label'){
        msgBoxShow(["Invalid Entry","Please enter valid fieldLabel"],0);
        return false;
    }else{
        return true;
    }
}

function strformat(str){
        str=str.replace(/\-/g,'');// remove -
        str=str.replace(/\*/g,'');// remove *
        str=str.replace(/\//g,'');// remove /
        str=str.replace(/\./g,'') // remove .
        str = str.replace(/\s{2,}/g," "); // strip concecutive spaces
        str = str.replace(/^\s/,"");//trim leading space
        str = str.toLowerCase();
        // replace spaces with underscore
        str = str.replace(/\s/g,"_");
        return str;
//        return str.trim().toLowerCase().replace(" ","_")
}


var processStoreRec = new Wtf.data.Record.create([{
                    name:'processname'

                },{
                    name:'processid'

                }]);
Wtf.processStore = new Wtf.data.Store({
      url:'Workflow?action=4',
     reader:new Wtf.data.KwlJsonReader1({root:'data'},processStoreRec)
})

var moduleStoreRec = new Wtf.data.Record.create([{
                    name:'modulename'

                },{
                    name:'moduleid'

                },{
                    name:'tablename'
                }]);
Wtf.moduleStore = new Wtf.data.Store({
      url:Wtf.req.mbuild+'form.do',
      storeId:"mbmodulestore",
      baseParams:{
          action:4,
          ss:"",
          start:0,
          limit:10000
      },
     reader:new Wtf.data.KwlJsonReader({root:'data'},moduleStoreRec)
});


function openModuleUITab(moduleid){//configField is an object with following properties  cmpId,moduleName,moduleId,containerId
    Wtf.Ajax.requestEx({
        url: Wtf.req.rbuild+"report.do",
        params: {
            action: 28,
            reportids: moduleid,
            taskids: '4028808429a619170129a6a625cc000a' //temp id will be used for process states later on - task denotes a step in workflow activity
        }
    }, this, function(response){
        var obj1 = response;
        var reportObj = obj1.reportdata;
        for(var ctr = 0; ctr < reportObj.length; ctr++) {
            var obj = reportObj[ctr];
            var formData = {};
            if(obj.jdata !== undefined){
                formData = eval("(" + obj.jdata + ")");
            }
            var recIndex = Wtf.moduleStore.find("moduleid",moduleid);
            if(recIndex != -1){
                var rec = Wtf.moduleStore.getAt(recIndex);
                var tablename = rec.get("tablename");
            }
            //var recperm=[{"2":["{\"permgrid\":52,\"perm\":0}"]},{"3":["{\"permgrid\":52,\"perm\":1}"]},{"4":["{\"permgrid\":52,\"perm\":2}"]},{"5":["{\"permgrid\":52,\"perm\":3}"]},{"6":["{\"permgrid\":52,\"perm\":4}"]},{"7":["{\"permgrid\":52,\"perm\":5}"]},{"8":["{\"permgrid\":52,\"perm\":6}"]}];
            var configFields={
                ismodule: !obj.isreport,
                data: formData,
                cmpId: obj.taskid + obj.reportid,
                tablename:tablename,
                moduleName: obj.reportname,
                moduleId: obj.reportid,
                taskid: obj.taskid,
                containerId: mainPanel.id,
                isFilter: false,
                filterfield: '',
                filterValue: '',
                permsObj : eval("(" + obj.perms + ")").recordperm
//                permsObj : recperm//eval("(" + obj.perms + ")").recordperm
            };

            openGridModule(configFields);
        }
    },
    function(response){

    });
}
function openGridModule(configFields){//configField is an object with following properties  cmpId,moduleName,moduleId,containerId

    var ismodule = configFields.ismodule?configFields.ismodule:false;
    var data= configFields.data?configFields.data:'';
    var cmpId= configFields.cmpId?configFields.cmpId:'';
    var moduleName= configFields.moduleName?configFields.moduleName:'';
    var moduleId= configFields.moduleId?configFields.moduleId:'';
    var taskid= configFields.taskid?configFields.taskid:'';
    var containerId= configFields.containerId?configFields.containerId:'';
    var isFilter= configFields.isFilter?configFields.isFilter:false;
    var filterfield= configFields.filterfield?configFields.filterfield:'';
    var filterValue= configFields.filterValue?configFields.filterValue:'';


     var reportComp = Wtf.getCmp(cmpId);
                if(!reportComp) {
                    if(!ismodule){
                        reportComp = new Wtf.reportDisplayGrid({
                                title : moduleName,
                                reportname : moduleName,
                                id : cmpId,
                                reportid : moduleId,
                                closable : true,
                                filtervalue : filterValue,
                                isFilter : isFilter,
                                filterfield : filterfield,
                                taskid : taskid,
                                permsObj : configFields.permsObj,
                                tablename:configFields.tablename,
                                layout:'fit'
                         });
                    }else{
                         reportComp = new Wtf.OpenModule ({
                                        formitems : data,
                                        title : moduleName,
                                        modulename :moduleName,
                                        id : cmpId,
                                        moduleid : moduleId,
                                        closable : true,
                                        taskid : taskid,
                                        filtervalue : filterValue,
                                        isFilter : isFilter,
                                        filterfield : filterfield,
                                        permsObj : configFields.permsObj,
                                        tablename:configFields.tablename,
                                        layout:'fit'
                                    });
            //                        mainPanel.loadComp(moduleComp, "tabmodule"+mid,gd.getStore().getAt(ri).data.modulename, "navareadashboard", Wtf.etype.course,false);


                    }
                    Wtf.getCmp(containerId).add(reportComp);
                }
                Wtf.getCmp(containerId).setActiveTab(reportComp);
                Wtf.getCmp(containerId).doLayout();
}

/*********Module Builder function end*********/

/* Product Type Master
+--------------------------------------+--------------------+
| id                                   | name               |
+--------------------------------------+--------------------+
| e4611696-515c-102d-8de6-001cc0794cfa | Inventory Assembly |
| d8a50d12-515c-102d-8de6-001cc0794cfa | Inventory Part     |
| f071cf84-515c-102d-8de6-001cc0794cfa | Non-Inventory Part |
| 4efb0286-5627-102d-8de6-001cc0794cfa | Service            |
+--------------------------------------+--------------------+
 * */
//var configFields={ismodule:false,data:'',cmpId:'',moduleName:'',moduleId:'',containerId:'',isFilter:false,filterfield:'',filterValue:'',permsObj:'{}'};

function HTMLStripper(val){
    var str = Wtf.util.Format.stripTags(val);
    return str.replace(/"/g, '').trim();
};

function ScriptStripper(val){
    var str = Wtf.util.Format.stripScripts(val);
    if (str)
        return str.replace(/"/g, '');
    else
        return str;
};

Wtf.ux.comboBoxRendererStore = function(store, valueField, displayField) {
    return function(value) {
        var idx = store.find(valueField, value);
        if(idx == -1)
            return "";
        var rec = store.getAt(idx);
        return rec.get(displayField);
    };
};
Wtf.defaultWidth=170;
Wtf.isEditable=function(moduleName,obj){
	var isEditableGrid=true;
	if(obj!=undefined &&obj.archivedFlag==1){
		isEditableGrid=false;
	}
	 switch(moduleName){
     case  Wtf.crmmodule.contact:if(WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.manage)){
 									isEditableGrid=false;
 									}
     								break;
     case Wtf.crmmodule.product :if(WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.manage)){
 									isEditableGrid=false;
 									}
     								break;
     case Wtf.crmmodule.lead :if(WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.manage)){
 								isEditableGrid=false;
 								}
     							break;
     case Wtf.crmmodule.cases : if(WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.manage)){
 					  				isEditableGrid=false;
 					  				}
                     				break;
     case Wtf.crmmodule.account : if(WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.manage)){
 									isEditableGrid=false;
 									}
     								break;
     case Wtf.crmmodule.campaign : if(WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.manage)){
 									isEditableGrid=false;
 									}
     								break;
     case Wtf.crmmodule.activity : if(WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.manage)){
 									isEditableGrid=false;
 									}
                     				break;
     case Wtf.crmmodule.opportunity : if(WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.manage)){
    	 								isEditableGrid=false;
     									}
 	             						break;
 	            
	 		}
	
	
	
	 return isEditableGrid;
}

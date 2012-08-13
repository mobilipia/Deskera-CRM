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
Wtf.KWLSearchBar = function(config){
    Wtf.KWLSearchBar.superclass.constructor.call(this, config);
}
Wtf.extend(Wtf.KWLSearchBar, Wtf.Panel, {
    searchid: "all",
    SearchOn: false,
//    timer: null,
    txt: null,
    searchds: null,
    searchds1: null,
    textSearchFlag: false,
    Noflag: 0,
    tabpanel: null,
    searchgrid: null,
    searchgrid1: null,
    peoplepanel: null,
    peoplepanel1: null,
    communitypanel: null,
    communitypanel1: null,
    accountpanel: null,
    accountypanel1: null,
    opportunitypanel: null,
    opportunitypanel1: null,
    leadpanel: null,
    leadpanel1: null,
    contactpanel: null,
    contactpanel1: null,
    casepanel: null,
    casepanel1: null,
    projectpanel: null,
    projectpanel1: null,
    KGridAll: null,
    KGridOnlyDoc: null,
    div1: null,
    AllSearchPanel: null,
    snippet: '<div><img src="{imgico}"/><div class="snippet">{title}</div><br><br/><div class = "fileinfo", style = "top : 27px;">{type}</div><div class = "fileinfo", style= "top : 38px;">{size}Kb</div><span  class="txtSearchSnippet">{descp}</span></div>',
    tpl: null,
    mainpaneltab: null,
    searchcolModel: null,
    SearchAllDocGrid: null,
    ds: null,
    contentEl:'serchForIco',
    layout:'form',
    timer:new Wtf.util.DelayedTask(this.getSearchResult),
    onRender: function(config){
        Wtf.KWLSearchBar.superclass.onRender.call(this, config);
        this.add(new Wtf.form.TextField({
            id: "textSearch",
            width: 200,
            height: 21,
            colspan: 3,
            emptyText: WtfGlobal.getLocaleText("crm.audittrail.searchBTN"),//'Search',
            cls:'searchInput',
            bodyStyle:'padding-left:24px !important;'

        }));
        this.add(new Wtf.Toolbar.MenuButton({
            text: '',
            renderTo:'serchForIco',
            id: 'searchBtn',
            iconCls:'searchall',
            ctCls:'searchleftbutton',
            colspan: 1,
            menu: {
                items: [
                {
                    text: WtfGlobal.getLocaleText("crm.CAMPAIGN.plural"),//'Campaigns ',
                    handler: this.onItemClick,
                    scope: this,
                    iconCls: 'pwndCRM campaignSearch',
                    id: 'cam'
                },
                {
                    text: WtfGlobal.getLocaleText("crm.LEAD.plural"),//'Leads ',
                    handler: this.onItemClick,
                    scope: this,
                    iconCls: 'pwndCRM leadSearch',
                    id: 'lea'
                },
                {
                    text: WtfGlobal.getLocaleText("crm.CONTACT.plural"),//'Contacts ',
                    handler: this.onItemClick,
                    scope: this,
                    iconCls: 'pwndCRM contactsTabIconSearch',
                    id: 'con'
                },
                {
                    text: WtfGlobal.getLocaleText("crm.PRODUCT.plural"),//'Products  ',
                    handler: this.onItemClick,
                    scope: this,
                    iconCls: 'pwndCRM productSearch',
                    id: 'pro'
                },
                {
                    text: WtfGlobal.getLocaleText("crm.ACCOUNT.plural"),//'Accounts ',
                    handler: this.onItemClick,
                    scope: this,
                    iconCls: 'pwndCRM accountSearch',
                    id: 'acc'
                },
                {
                    text: WtfGlobal.getLocaleText("crm.OPPORTUNITY.plural"),//'Opportunities ',
                    handler: this.onItemClick,
                    scope: this,
                    iconCls: 'pwndCRM opportunitySearchIcon',
                    id: 'opp'
                },
                {
                    text: WtfGlobal.getLocaleText("crm.CASE.plural"),//'Cases ',
                    handler: this.onItemClick,
                    scope: this,
                    iconCls: 'pwndCRM caseIconSearch',
                    id: 'cas'
                },
                {
                    text: WtfGlobal.getLocaleText("crm.PEOPLE"),//'People    ',
                    handler: this.onItemClick,
                    scope: this,
                    iconCls: 'pwnd searchpeople',
                    id: 'pep'
                }, 
                {
                    text: WtfGlobal.getLocaleText("crm.DOCUMENTS"),//'Documents ',
                    handler: this.onItemClick,
                    scope: this,
                    iconCls: 'pwnd searchdoc',
                    id: 'doc'
                }, '-', {
                    text:WtfGlobal.getLocaleText("crm.COMBOEMPTYTXT.all"),// 'All       ',
                    cls: "selected",
                    handler: this.onItemClick,
                    scope: this,
                    iconCls: 'searchall',
                    id: 'all'
                }/*, '-', {
                    //TODO:feature to be implemented
                    text: 'Advanced Search...   ',
                    handler: this.onItemClick,
                    scope: this
                }*/]
            }
        }));
         this.add(new Wtf.Toolbar.Button({
            text: '',
            id: 'searchBtn1',
            iconCls:'btnsearch',
            ctCls:'searchrightbutton',
            scope: this,
            handler: this.onButtonClick

        }));
            Wtf.getCmp("textSearch").on('render', function(e){
            Wtf.EventManager.addListener("textSearch", 'keyup', this.txtsearchKeyPress, this);
            Wtf.EventManager.addListener("textSearch", 'keyup', this.txtsearchTitleRefresh, this);
            Wtf.getCmp('textSearch').on('specialkey', this.specialKeyPressed, this);
            Wtf.getCmp('textSearch').on('focus', function(){this.setValue("");});
            Wtf.getCmp("searchBtn").on("mouseover",this.btnHover);
            Wtf.getCmp("searchBtn1").on("mouseover",this.btnHover);
            Wtf.getCmp("searchBtn1").on("mouseout",this.btnOut);
            Wtf.getCmp("searchBtn").on("mouseout",this.btnOut);
        }, this);
        this.template = new Wtf.XTemplate(
            '<tpl for=".">',
                '<div class="workspace" style="width:98%!important;">',
                '<div class="workspace-vote"><img src="images/{img}"/></div>',
                '<div class="workspace-text">',
                '<h2 class="work-title" ><strong><a href="#" style="font-size:12px" onclick="javascript:openModuleTab(\'{moduleName}\',\'{id}\');">{name}</a></strong></h2>',
                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.lead.defaultheader.owner")+': <strong>{owners}</strong></div>',
                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon")+':<strong>{createdon}</strong></div>',
                '</div>',
                '</div>',
            '</tpl>'
            );
        this.tpl = new Wtf.Template(this.snippet);
        this.tpl.compile();
        this.searchcolModel = new Wtf.grid.ColumnModel([{
            header: WtfGlobal.getLocaleText("crm.mydocuments.header.name")+"     ",//"Name     ",
            sortable: true,
            dataIndex: 'FileName',
            width: 100,
            scope: this,
            renderer: function(value, p, record){
                var img = MimeBasedValue(record.data.Type,true);
                return String.format("<img src={0} style='height:16px;width:16px;margin:0px 4px 0 0;vertical-align:text-top;'/>{1}", img, value);
            }
        }, {
            header: WtfGlobal.getLocaleText("crm.mydocuments.header.size")+"     ",//"Size     ",
            sortable: true,
            dataIndex: 'Size',
            width: 100,
            renderer: function(value, p, record){
                //return String.format("<span>{0}Kb</span>", Math.round(value / 1024));
                return value+ " KB";
            }
        }, {
            header:WtfGlobal.getLocaleText("crm.mydocuments.header.type")+"    ",// "Type    ",
            sortable: true,
            dataIndex: 'Type',
            width: 100
// kuldeep           renderer: function(value, p, record){
// kuldeep               return String.format("<span>{0}</span>", MimeBasedValue(value,false));
// kuldeep           }
        }, {
            header:WtfGlobal.getLocaleText("crm.mydocuments.header.uploadedon")+"     ",// "Uploaded Date     ",
            sortable: true,
            renderer: Wtf.util.Format.dateRenderer('n/j/Y g:i A'),
            dataIndex: 'DateModified',
            width: 100
        }, {
            header: WtfGlobal.getLocaleText("crm.mydocuments.header.owner")+"	",//"Owner	",
            sortable: true,
            dataIndex: 'Author',
            width: 100
        }]);
    },

    onButtonClick: function(e){
        this.SearchOn = true;
        var tab = Wtf.getCmp("tabsearchBtn");
        if (!tab) {
            Wtf.getCmp("as").add(this.mainpaneltab = new Wtf.ux.ContentPanel({
                id: "tabsearchBtn",
                layout: 'fit',
                title: WtfGlobal.getLocaleText("crm.search"),//"CRM Search",
                iconCls:"pwnd globalsearchtabpane"
            }));
            this.mainpaneltab.on('render', this.mainpaneltabrendered, this);
            Wtf.getCmp("as").setActiveTab(this.mainpaneltab);
            this.mainpaneltab.on("destroy",function(){
                //Wtf.getCmp("textSearch").reset();
            });

//            if (Wtf.getCmp('textSearch').getValue() != "") {
//                this.txt = Wtf.getCmp('textSearch').getValue();
//                this.getSearchResult();
//            }
        }
        else {
            Wtf.getCmp("as").activate("tabsearchBtn");
            if (Wtf.getCmp('textSearch').getValue() != "") {
                this.txt = Wtf.getCmp('textSearch').getValue();
                this.switchdisplay(this.searchid);
                this.getSearchResult();
            }
            //Wtf.getCmp("as").activate("tabsearchBtn");
            this.switchdisplay(this.searchid);
        }
        this.txtsearchTitleRefresh();
    },

    onItemClick: function(e){
        var sbttn = Wtf.getCmp('searchBtn');
        var txts = Wtf.getCmp("textSearch");
        this.searchid = e.id;
        var menuItem = e.parentMenu.items.items;
        for(var i = 0; i <menuItem.length; i++) {
            if(menuItem[i].el.dom.firstChild.style !== undefined) {
                menuItem[i].el.dom.firstChild.style.border = "0";
            }
        }
        switch (e.id) {
            case "pep":
                sbttn.setIconClass("pwnd searchpeople");
                txts.emptyText= WtfGlobal.getLocaleText("crm.globalsearch.people.mtytext");//'Search for People';
//                e.parentMenu.items.items[0].el.dom.firstChild.style.border = "1px outset #222";
                break;
            case "pro":
                sbttn.setIconClass("pwndCRM productSearch");
                txts.emptyText= WtfGlobal.getLocaleText("crm.globalsearch.product.mtytext");//'Search for Products';
//                e.parentMenu.items.items[1].el.dom.firstChild.style.border = "1px outset #222";
                break;
            case "cam":
                sbttn.setIconClass("pwndCRM campaignSearch");
                txts.emptyText= WtfGlobal.getLocaleText("crm.globalsearch.campaign.mtytext");//'Search for Campaigns';
//                e.parentMenu.items.items[2].el.dom.firstChild.style.border = "1px outset #222";
                break;
            case "acc":
                sbttn.setIconClass("pwndCRM accountSearch");
                txts.emptyText= WtfGlobal.getLocaleText("crm.globalsearch.account.mtytext");//'Search for Accounts';
//                e.parentMenu.items.items[2].el.dom.firstChild.style.border = "1px outset #222";
                break;
            case "opp":
                sbttn.setIconClass("pwndCRM opportunitySearchIcon");
                txts.emptyText= WtfGlobal.getLocaleText("crm.globalsearch.opportunity.mtytext");//'Search for Opportunities';
//                e.parentMenu.items.items[2].el.dom.firstChild.style.border = "1px outset #222";
                break;
            case "lea":
                sbttn.setIconClass("pwndCRM leadSearch");
                txts.emptyText= WtfGlobal.getLocaleText("crm.globalsearch.lead.mtytext");//'Search for Leads';
//                e.parentMenu.items.items[2].el.dom.firstChild.style.border = "1px outset #222";
                break;
            case "con":
                sbttn.setIconClass("pwndCRM contactsTabIconSearch");
                txts.emptyText= WtfGlobal.getLocaleText("crm.globalsearch.contact.mtytext");//'Search for Contacts';
//                e.parentMenu.items.items[2].el.dom.firstChild.style.border = "1px outset #222";
                break;
            case "cas":
                sbttn.setIconClass("pwndCRM caseIconSearch");
                txts.emptyText= WtfGlobal.getLocaleText("crm.globalsearch.case.mtytext");//'Search for Cases';
//                e.parentMenu.items.items[2].el.dom.firstChild.style.border = "1px outset #222";
                break;
            case "doc":
                sbttn.setIconClass("pwnd searchdoc");
                txts.emptyText= WtfGlobal.getLocaleText("crm.globalsearch.document.mtytext");//'Search in Documents';
//                e.parentMenu.items.items[3].el.dom.firstChild.style.border = "1px outset #222";
                break;
            case "all":
                sbttn.setIconClass("searchall");
                txts.emptyText= WtfGlobal.getLocaleText({key:"crm.globalsearch.searchincompany.mtytext",params:[companyName]});//'Search on '+companyName;
//                e.parentMenu.items.items[3].el.dom.firstChild.style.border = "1px outset #222";
                break;
        }
        e.el.dom.firstChild.style.border = "1px outset #222";
         txts.reset();
    },

    txtsearchKeyPress: function(e){
        
        var tab = Wtf.getCmp("tabsearchBtn");
        if (tab) {
            if (this.SearchOn) {
                this.txt = e.getTarget().value;
                this.timer.cancel();
                this.timer.delay(1000,this.getSearchResult,this);

            }
            Wtf.getCmp("as").activate("tabsearchBtn");
            if (Wtf.getCmp("SearchTabPanel"))
                this.switchdisplay(this.searchid);
        }
        else {
            this.SearchOn = false;
            Wtf.getCmp("as").add(this.mainpaneltab = new Wtf.ux.ContentPanel({
                id: "tabsearchBtn",
                layout: 'fit',
                title: WtfGlobal.getLocaleText("crm.search"),//"CRM Search",
                iconCls:"pwnd globalsearchtabpane"
            }));
            this.mainpaneltab.on('render', this.mainpaneltabrendered, this);
            Wtf.getCmp("as").setActiveTab(this.mainpaneltab);
            this.mainpaneltab.on("destroy",function(){
               // Wtf.getCmp("textSearch").reset();
            });
        }
    },

    txtsearchTitleRefresh: function(e){
        if (Wtf.getCmp("SearchTabPanel")) {
            var allSearchtitle = WtfGlobal.getLocaleText("crm.globalsearch.allsearch.paneltitle")+":";//'All Search Results for: '
            var projecttitle = WtfGlobal.getLocaleText("crm.globalsearch.productresultsfor.title")+":";//'Product Results for: ';
            var peopletitle =  WtfGlobal.getLocaleText("crm.globalsearch.productresultsfor.title")+":";//'People Results for: ';
            var doctitle = WtfGlobal.getLocaleText("crm.globalsearch.productresultsfor.title")+":";//'Documents Results for: '
            var campaigntitle = WtfGlobal.getLocaleText("crm.globalsearch.productresultsfor.title")+":";//'Campaign Results for: '
            var accounttitle = WtfGlobal.getLocaleText("crm.globalsearch.productresultsfor.title")+":";//'Account Results for: '
            var opportunitytitle = WtfGlobal.getLocaleText("crm.globalsearch.productresultsfor.title")+":";//'Opportunity Results for: '
            var leadtitle = WtfGlobal.getLocaleText("crm.globalsearch.productresultsfor.title")+":";//'Lead Results for: '
            var contacttitle = WtfGlobal.getLocaleText("crm.globalsearch.productresultsfor.title")+":";//'Contact Results for: '
            var casetitle = WtfGlobal.getLocaleText("crm.globalsearch.productresultsfor.title")+":";//'Case Results for: '
            var stripvalue = WtfGlobal.HTMLStripper(Wtf.getCmp('textSearch').getValue());
            switch (this.searchid) {
                case 'all':
                    if (this.AllSearchPanel) {
                        this.AllSearchPanel.ResetTitle(allSearchtitle+stripvalue);
                    }
                    break;
                case 'pep':
                    if (this.peoplepanel1) {
                        this.peoplepanel1.ResetTitle(peopletitle + stripvalue);
                    }
                    break;
                case 'cam':
                    if (this.communitypanel1) {
                        this.communitypanel1.ResetTitle(campaigntitle + stripvalue);
                    }
                    break;
                case 'acc':
                    if (this.accountpanel1) {
                        this.accountpanel1.ResetTitle(accounttitle + stripvalue);
                    }
                    break;
                case 'opp':
                    if (this.opportunitypanel1) {
                        this.opportunitypanel1.ResetTitle(opportunitytitle + stripvalue);
                    }
                    break;
                case 'lea':
                    if (this.leadpanel1) {
                        this.leadpanel1.ResetTitle(leadtitle + stripvalue);
                    }
                    break;
                case 'con':
                    if (this.contactpanel1) {
                        this.contactpanel1.ResetTitle(contacttitle + stripvalue);
                    }
                    break;
                case 'cas':
                    if (this.casepanel1) {
                        this.casepanel1.ResetTitle(casetitle + stripvalue);
                    }
                    break;
                case 'pro':
                    if (this.projectpanel1) {
                        this.projectpanel1.ResetTitle(projecttitle + stripvalue);
                    }
                    break;
                case 'doc':
                    if (this.KGridOnlyDoc) {
                        this.KGridOnlyDoc.ResetTitle(doctitle + stripvalue);
                    }
                    break;
            }
        }
    },

    specialKeyPressed: function(f, e){
        if (e.getKey() == 13) {
            this.SearchOn = true;
        }
    },

    getSearchResult: function(){
        this.txt = this.txt.trim();
        //this.txt = this.txt.replace("\"", "");
        if (this.txt != "") {
            switch (this.searchid) {
                case "all":
                    this.Noflag = 4;
                    this.getData("all", false, "AllSearchPanel");
                    break;
                case "pep":
                    this.Noflag = 1;
                    this.getData("user", false, "peoplepanel1");
                    break;
                case "cam":
                    this.Noflag = 1;
                    this.getData("cam", false, "communitypanel1");
                    break;
                case "pro":
                    this.Noflag = 1;
                    this.getData("pro", false, "projectpanel1");
                    break;
                case "doc":
                    this.Noflag = 1;
                    this.getData("docs", true, "gridpaneldocsearch1");
                    break;
                case "acc":
                    this.Noflag = 1;
                    this.getData("acc", true, "accountpanel1");
                    break;
                case "opp":
                    this.Noflag = 1;
                    this.getData("opp", true, "opportunitypanel1");
                    break;
                case "lea":
                    this.Noflag = 1;
                    this.getData("lea", true, "leadpanel1");
                    break;
                case "con":
                    this.Noflag = 1;
                    this.getData("con", true, "contactpanel1");
                    break;
                case "cas":
                    this.Noflag = 1;
                    this.getData("cas", true, "casepanel1");
                    break;
            }
        }
        else {
            var emptyobj = {
              data : []
            };
            switch (this.searchid) {
                case "all":
                    Wtf.getCmp('AllSearchPanel').dataViewStore.loadData(emptyobj);
                    break;
                case "pep":
                    Wtf.getCmp('peoplepanel1').dataViewStore.loadData(emptyobj);
                    break;
                case "cam":
                    Wtf.getCmp('communitypanel1').dataViewStore.loadData(emptyobj);
                    break;
                case "pro":
                    Wtf.getCmp('projectpanel1').dataViewStore.loadData(emptyobj);
                    break;
                case "doc":
                    Wtf.getCmp('gridpaneldocsearch1').dataViewStore.loadData(emptyobj);
                    break;
                case "acc":
                    Wtf.getCmp('accountpanel1').dataViewStore.loadData(emptyobj);
                    break;
                case "opp":
                    Wtf.getCmp('opportunitypanel1').dataViewStore.loadData(emptyobj);
                    break;
                case "lea":
                    Wtf.getCmp('leadpanel1').dataViewStore.loadData(emptyobj);
                    break;
                case "con":
                    Wtf.getCmp('contactpanel1').dataViewStore.loadData(emptyobj);
                    break;
                case "cas":
                    Wtf.getCmp('casepanel1').dataViewStore.loadData(emptyobj);
                    break;
            }
        }
    },

    getData: function(type, urlflag, component){
            var comp = Wtf.getCmp(component);
            var stripval = WtfGlobal.HTMLStripper(this.txt).trim();
            Wtf.getCmp("textSearch").setValue(stripval);
            Wtf.commonWaitMsgBox("Searching...");
            Wtf.Ajax.requestEx({
                method: 'GET',
                url:Wtf.req.springBase+"common/GlobalSearch/globalQuickSearch.do",
                params: {
                    type: type,
                    keyword: WtfGlobal.HTMLStripper(this.txt)
                }
            },
            this,
            function(obj, req){
//                 var obj = eval('(' + result + ')');
                 var i;
//                 if(component=="gridpaneldocsearch1"){
//                     for(i=0;i<obj.data.length;i++){
//                        obj.data[i]["Summary"] = unescape(obj.data[i]["Summary"]);
//                     }
//                 }
//                 if(component=="AllSearchPanel"){
//                     if(obj.data.length > 0) {
//                        if(obj.data.length){
//                            for(i=0;i<obj.data[0]["docs"].length;i++){
//                                obj.data[0]["docs"][i]["Summary"] = unescape(obj.data[0]["docs"][i]["Summary"]);
//                            }
//                        }
//                    }
//                 }
                comp.dataViewStore.loadData(obj, "data");
                Wtf.updateProgress();
                //comp.ResetTitle(Wtf.getCmp('textSearch').getValue());
            },
            function(){
                Wtf.updateProgress();
            });
    },

    mainpaneltabrendered: function(r){
        this.mainpaneltab.add(this.tabpanel = new Wtf.TabPanel({
            id: 'SearchTabPanel',
            tabWidth: 'auto',
            enableTabScroll: true,
            border: false
        }));
        this.switchdisplay(this.searchid);
        this.SearchOn = true;
        this.txt = Wtf.getCmp("textSearch").getValue();
        this.timer.cancel();
        this.timer.delay(1000,this.getSearchResult,this);
    },

    switchdisplay: function(searchid){
        var searchDS;
        var dsReader;
        switch (searchid) {
            case 'all':
                if (document.getElementById('SearchTabPanel__SearchAll') == null) {
                    var dsAllSearch = new Wtf.data.JsonReader({
                            root : "data",
                            fields: [{name: 'docs'},
                            {name: 'docContents'},
                            {name: 'contact'},
                            {name: 'account'},
                            {name: 'campaign'},
                            {name: 'lead'},
                            {name: 'opportunity'},
                            {name: 'product'},
                            {name: 'casearr'},
                            {name: 'user'}]
                   });
                    var searchDSAllSearch = new Wtf.data.Store({
                        reader : dsAllSearch
                    });
                    var tplAllSearch = new Wtf.XTemplate(
                                '<tpl for=".">',
                                    '<div id = "all-search-div">',
                                        '<tpl if="this.isHeading(docs) == false">',
                                            '<h2 class="workspace all-search-header" ><span style="font-size:15px;padding:2px 0px 0px 6px !important;float:left;"><strong>'+WtfGlobal.getLocaleText("crm.DOCUMENTS")+'</span></strong></h2>',
                                            '<tpl for="docs">',
                                                '<div class="workspace" style="width:98%!important;">',
                                                    '<div class="workspace-vote"><img src="images/{fileimage}"/></div>',
                                                    '<div class="workspace-text">',
                                                    '<h2 class="work-title" ><strong><a href="#" style="font-size:12px" onclick="javascript:setDldUrl(\'crm/common/Document/downloadDocuments.do?url={docid}&mailattch=true&dtype=attachment\');">{name}</a></strong></h2>',
                                                     '<div class="by-name search-summary">{Summary}</div>',
                                                      '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.globalsearch.allsearch.contributor")+'<strong>{author}</strong></div>',
                                                      '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.globalsearch.allsearch.uploadedon")+'<strong>{uploadeddate}</strong></div>',
                                                      '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.mydocuments.header.size")+': <strong>{size}</strong></div>',
                                                      '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.globalsearch.allsearch.tags")+':<strong>{Tags}</strong></div>',
                                                    '</div>',
                                                '</div>',
                                             '</tpl>',
                                         '</tpl>',
                                     '</div>',
                                    '<div id = "all-search-div">',
                                        '<tpl if="this.isHeading(docContents) == false">',
                                            '<h2 class="workspace all-search-header" ><span style="font-size:15px;padding:2px 0px 0px 6px !important;float:left;"><strong>Document Content</span></strong></h2>',
                                            '<tpl for="docContents">',
                                                '<div class="workspace" style="width:98%!important;">',
                                                    '<div class="workspace-vote"><img src="images/{fileimage}"/></div>',
                                                    '<div class="workspace-text">',
                                                    '<h2 class="work-title" ><strong><a href="#" style="font-size:12px" onclick="javascript:setDldUrl(\'crm/common/Document/downloadDocuments.do?url={docid}&mailattch=true&dtype=attachment\');">{name}</a></strong></h2>',
                                                     '<div class="by-name search-summary">{Summary}</div>',
                                                      '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.globalsearch.allsearch.contributor")+'<strong>{author}</strong></div>',
                                                      '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.globalsearch.allsearch.uploadedon")+'<strong>{uploadeddate}</strong></div>',
                                                      '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.mydocuments.header.size")+': <strong>{size}</strong></div>',
                                                      '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.globalsearch.allsearch.tags")+':<strong>{Tags}</strong></div>',
                                                    '</div>',
                                                '</div>',
                                             '</tpl>',
                                         '</tpl>',
                                     '</div>',
                                    '<div id = "all-search-div">',
                                        '<tpl if="this.isHeading(contact) == false">',
                                            '<h2 class="workspace all-search-header" ><span style="font-size:15px;padding:2px 0px 0px 6px !important;float:left;"><strong>Contacts</span></strong></h2>',
                                            '<tpl for="contact">',
                                                '<div class="workspace" style="width:98%!important;">',
                                                '<div class="workspace-vote"><img src="images/{img}"/></div>',
                                                '<div class="workspace-text">',
                                                '<h2 class="work-title" ><strong><a href="#" style="font-size:12px" onclick="javascript:openModuleTab(\'{moduleName}\',\'{id}\');">{name}</a></strong></h2>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.lead.defaultheader.owner")+': <strong>{owners}</strong></div>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon")+': <strong>{createdon}</strong></div>',
                                                '</div>',
                                                '</div>',
                                             '</tpl>',
                                         '</tpl>',
                                     '</div>',
                                    '<div id = "all-search-div">',
                                        '<tpl if="this.isHeading(account) == false">',
                                            '<h2 class="workspace all-search-header" ><span style="font-size:15px;padding:2px 0px 0px 6px !important;float:left;"><strong>Accounts</span></strong></h2>',
                                            '<tpl for="account">',
                                                '<div class="workspace" style="width:98%!important;">',
                                                '<div class="workspace-vote"><img src="images/{img}"/></div>',
                                                '<div class="workspace-text">',
                                                '<h2 class="work-title" ><strong><a href="#" style="font-size:12px" onclick="javascript:openModuleTab(\'{moduleName}\',\'{id}\');">{name}</a></strong></h2>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.lead.defaultheader.owner")+': <strong>{owners}</strong></div>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon")+': <strong>{createdon}</strong></div>',
                                                '</div>',
                                                '</div>',
                                             '</tpl>',
                                         '</tpl>',
                                     '</div>',
                                    '<div id = "all-search-div">',
                                        '<tpl if="this.isHeading(campaign) == false">',
                                            '<h2 class="workspace all-search-header" ><span style="font-size:15px;padding:2px 0px 0px 6px !important;float:left;"><strong>Campaigns</span></strong></h2>',
                                            '<tpl for="campaign">',
                                                '<div class="workspace" style="width:98%!important;">',
                                                '<div class="workspace-vote"><img src="images/{img}"/></div>',
                                                '<div class="workspace-text">',
                                                '<h2 class="work-title" ><strong><a href="#" style="font-size:12px" onclick="javascript:openModuleTab(\'{moduleName}\',\'{id}\');">{name}</a></strong></h2>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.lead.defaultheader.owner")+': <strong>{owners}</strong></div>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon")+': <strong>{createdon}</strong></div>',
                                                '</div>',
                                                '</div>',
                                             '</tpl>',
                                         '</tpl>',
                                     '</div>',
                                    '<div id = "all-search-div">',
                                        '<tpl if="this.isHeading(lead) == false">',
                                            '<h2 class="workspace all-search-header" ><span style="font-size:15px;padding:2px 0px 0px 6px !important;float:left;"><strong>Leads</span></strong></h2>',
                                            '<tpl for="lead">',
                                                '<div class="workspace" style="width:98%!important;">',
                                                '<div class="workspace-vote"><img src="images/{img}"/></div>',
                                                '<div class="workspace-text">',
                                                '<h2 class="work-title" ><strong><a href="#" style="font-size:12px" onclick="javascript:openModuleTab(\'{moduleName}\',\'{id}\');">{name}</a></strong></h2>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.lead.defaultheader.owner")+': <strong>{owners}</strong></div>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon")+': <strong>{createdon}</strong></div>',
                                                '</div>',
                                                '</div>',
                                             '</tpl>',
                                         '</tpl>',
                                     '</div>',
                                    '<div id = "all-search-div">',
                                        '<tpl if="this.isHeading(product) == false">',
                                            '<h2 class="workspace all-search-header" ><span style="font-size:15px;padding:2px 0px 0px 6px !important;float:left;"><strong>Products</span></strong></h2>',
                                            '<tpl for="product">',
                                                '<div class="workspace" style="width:98%!important;">',
                                                '<div class="workspace-vote"><img src="images/{img}"/></div>',
                                                '<div class="workspace-text">',
                                                '<h2 class="work-title" ><strong><a href="#" style="font-size:12px" onclick="javascript:openModuleTab(\'{moduleName}\',\'{id}\');">{name}</a></strong></h2>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.lead.defaultheader.owner")+': <strong>{owners}</strong></div>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon")+': <strong>{createdon}</strong></div>',
                                                '</div>',
                                                '</div>',
                                             '</tpl>',
                                         '</tpl>',
                                     '</div>',
                                     '<div id = "all-search-div">',
                                        '<tpl if="this.isHeading(opportunity) == false">',
                                            '<h2 class="workspace all-search-header" ><span style="font-size:15px;padding:2px 0px 0px 6px !important;float:left;"><strong>Opportunities</span></strong></h2>',
                                            '<tpl for="opportunity">',
                                                '<div class="workspace" style="width:98%!important;">',
                                                '<div class="workspace-vote"><img src="images/{img}"/></div>',
                                                '<div class="workspace-text">',
                                                '<h2 class="work-title" ><strong><a href="#" style="font-size:12px" onclick="javascript:openModuleTab(\'{moduleName}\',\'{id}\');">{name}</a></strong></h2>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.lead.defaultheader.owner")+': <strong>{owners}</strong></div>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon")+': <strong>{createdon}</strong></div>',
                                                '</div>',
                                                '</div>',
                                             '</tpl>',
                                         '</tpl>',
                                     '</div>',
                                    '<div id = "all-search-div">',
                                        '<tpl if="this.isHeading(casearr) == false">',
                                            '<h2 class="workspace all-search-header" ><span style="font-size:15px;padding:2px 0px 0px 6px !important;float:left;"><strong>Cases</span></strong></h2>',
                                            '<tpl for="casearr">',
                                                '<div class="workspace" style="width:98%!important;">',
                                                '<div class="workspace-vote"><img src="images/{img}"/></div>',
                                                '<div class="workspace-text">',
                                                '<h2 class="work-title" ><strong><a href="#" style="font-size:12px" onclick="javascript:openModuleTab(\'{moduleName}\',\'{id}\');">{name}</a></strong></h2>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.lead.defaultheader.owner")+': <strong>{owners}</strong></div>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon")+': <strong>{createdon}</strong></div>',
                                                '</div>',
                                                '</div>',
                                             '</tpl>',
                                         '</tpl>',
                                     '</div>',
                                    '<div id = "all-search-div">',
                                        '<tpl if="this.isHeading(user) == false">',
                                            '<h2 class="workspace all-search-header" ><span style="font-size:15px;padding:2px 0px 0px 6px !important;float:left;"><strong>Users</span></strong></h2>',
                                            '<tpl for="user">',
                                                '<div class="workspace" style="width:98%!important;">',
                                                '<div class="workspace-vote"><img src="images/{img}"/></div>',
                                                '<div class="workspace-text">',
                                                '<h2 class="work-title" ><strong><span style="font-size:12px" class=\"no-pointer\");">{name}</span></strong></h2>',
                                                '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon")+': <strong>{createdon}</strong></div>',
                                                '</div>',
                                                '</div>',
                                             '</tpl>',
                                         '</tpl>',
                                     '</div>',
                                '</tpl>',{
                                    isHeading: function(showdata){
                                        return showdata.length == 0;
                                    }
                                }
                    );
                    this.AllSearchPanel = new Wtf.common.KWLListPanel({
                        title: WtfGlobal.getLocaleText("crm.globalsearch.allsearch.paneltitle"),//'All Search Results for ',
                        id: 'AllSearchPanel',
                        autoLoad: false,
                        autoScroll: true,
                        layout : "fit",
                        paging : false,
                        dataViewStore : searchDSAllSearch
                    });
                    this.AllSearch = new Wtf.DataView({
                        store: searchDSAllSearch,
                        tpl: tplAllSearch,
                        autoHeight:true,
                        emptyText: '&nbsp&nbsp'+WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),// No results to display',
                        overClass:'x-view-over',
                        itemSelector:'div.thumb-wrap'
                    });
                    this.AllSearchPanel.on("render",function(){
                        document.getElementById(this.AllSearchPanel.body.id).className = "overflowX-hidden";
                    },this);
                    this.AllSearch.on("render",function(){
                        document.getElementById(this.AllSearch.getEl().id).className = "all-search-view-div";
                    },this);
                    this.AllSearchPanel.add(this.AllSearch);
                    this.AllSearchPanel.doLayout();

                }
                this.DisplayTab(WtfGlobal.getLocaleText("crm.onesearch"), "searchalltab", "SearchAll", this.AllSearchPanel);
                break;
            case 'pep':
                if (document.getElementById('SearchTabPanel__SearchPeople') == null) {
                     dsReader = new Wtf.data.JsonReader({
                        root: "data",
                        fields: [
                        {
                            name: 'id'
                        },

                        {
                            name: 'name'
                        },

                        {
                            name: 'owners'
                        },

                        {
                            name: 'createdon'
                        },

                        {
                            name: 'img'
                        },
                        {
                            name: 'moduleName'
                        }
                        ]
                    });
                    searchDS = new Wtf.data.Store({
                        reader : dsReader
                    });

                    this.peoplepanel1 = new Wtf.common.KWLListPanel({
                        title: WtfGlobal.getLocaleText("crm.globalsearch.peopleresultsfor.title"),//'People Results for ',
                        id: 'peoplepanel1',
                        autoLoad: false,
                        autoScroll: true,
                        layout : "fit",
                        paging : false,
                        dataViewStore : searchDS
                    });
                    var template = new Wtf.XTemplate(
                        '<tpl for=".">',
                        '<div class="workspace" style="width:98%!important;">',
                        '<div class="workspace-vote"><img src="images/{img}"/></div>',
                        '<div class="workspace-text">',
                        '<h2 class="work-title" ><strong><span style="font-size:12px" class=\"no-pointer\");">{name}</span></strong></h2>',
                        '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon")+': <strong>{createdon}</strong></div>',
                        '</div>',
                        '</div>',
                        '</tpl>'
                    );

                    this.SearchOnlyUser = new Wtf.DataView({
                        store: searchDS,
                        tpl: template,
                        autoHeight:true,
                        style:"margin:10px!important;width:97%;padding-left:5px",
                        emptyText: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//No results to display
                        overClass:'x-view-over',
                        itemSelector:'div.thumb-wrap'
                    });
                     this.peoplepanel1.on("render",function(){
                        document.getElementById(this.peoplepanel1.body.id).className = "overflowX-hidden";
                    },this);
                    this.peoplepanel1.add(this.SearchOnlyUser);
                    this.peoplepanel1.doLayout();
                }
                this.DisplayTab(WtfGlobal.getLocaleText("crm.peoplesearch"), "pwnd userTabIcon1", "SearchPeople", this.peoplepanel1);
                break;
            case 'cam':
                if (document.getElementById('SearchTabPanel__SearchCommunity') == null) {
                     dsReader = new Wtf.data.JsonReader({
                        root: "data",
                        fields: [
                        {
                            name: 'id'
                        },

                        {
                            name: 'name'
                        },

                        {
                            name: 'owners'
                        },

                        {
                            name: 'createdon'
                        },

                        {
                            name: 'img'
                        },
                        {
                            name: 'moduleName'
                        }
                        ]
                    });
                    searchDS = new Wtf.data.Store({
                        reader : dsReader
                    });
                    this.communitypanel1 = new Wtf.common.KWLListPanel({
                        title: WtfGlobal.getLocaleText("crm.globalsearch.campaignresultsfor.title"),//'Campaign Results for ',
                        id: 'communitypanel1',
                        autoScroll: true,
                        autoLoad: false,
                        layout : "fit",
                        paging : false,
                        dataViewStore : searchDS
                    });
                    this.SearchOnlyCampaign = new Wtf.DataView({
                        store: searchDS,
                        tpl: this.template,
                        autoHeight:true,
                        style:"margin:10px!important;width:97%;padding-left:5px;",
                        emptyText: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//'No results to display',
                        overClass:'x-view-over',
                        itemSelector:'div.thumb-wrap'
                    });
                     this.communitypanel1.on("render",function(){
                        document.getElementById(this.communitypanel1.body.id).className = "overflowX-hidden";
                    },this);
                    this.communitypanel1.add(this.SearchOnlyCampaign);
                    this.communitypanel1.doLayout();
                }
                this.DisplayTab(WtfGlobal.getLocaleText("crm.campaignsearch"), "pwndCRM campaign", "SearchCommunity", this.communitypanel1);
                break;
            case 'acc':
                if (document.getElementById('SearchTabPanel__SearchAccount') == null) {
                     dsReader = new Wtf.data.JsonReader({
                        root: "data",
                        fields: [
                        {
                            name: 'id'
                        },

                        {
                            name: 'name'
                        },

                        {
                            name: 'owners'
                        },

                        {
                            name: 'createdon'
                        },

                        {
                            name: 'img'
                        },
                        {
                            name: 'moduleName'
                        }
                        ]
                    });
                    searchDS = new Wtf.data.Store({
                        reader : dsReader
                    });
                    this.accountpanel1 = new Wtf.common.KWLListPanel({
                        title: WtfGlobal.getLocaleText("crm.globalsearch.acountresultsfor.title"),//'Account Results for ',
                        id: 'accountpanel1',
                        autoLoad: false,
                        autoScroll: true,
                        layout : "fit",
                        paging : false,
                        dataViewStore : searchDS
                    });
                    this.SearchOnlyAccount = new Wtf.DataView({
                        store: searchDS,
                        tpl: this.template,
                        autoHeight:true,
                        style:"margin:10px!important;width:97%;padding-left:5px;",
                        emptyText:WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),// 'No results to display',
                        overClass:'x-view-over',
                        itemSelector:'div.thumb-wrap'
                    });
                     this.accountpanel1.on("render",function(){
                        document.getElementById(this.accountpanel1.body.id).className = "overflowX-hidden";
                    },this);
                    this.accountpanel1.add(this.SearchOnlyAccount);
                    this.accountpanel1.doLayout();
                }
                this.DisplayTab(WtfGlobal.getLocaleText("crm.accountsearch"), "pwndCRM account", "SearchAccount", this.accountpanel1);
                break;
             case 'opp':
                if (document.getElementById('SearchTabPanel__SearchOpportunity') == null) {
                     dsReader = new Wtf.data.JsonReader({
                        root: "data",
                        fields: [
                        {
                            name: 'id'
                        },

                        {
                            name: 'name'
                        },

                        {
                            name: 'owners'
                        },

                        {
                            name: 'createdon'
                        },

                        {
                            name: 'img'
                        },
                        {
                            name: 'moduleName'
                        }
                        ]
                    });
                    searchDS = new Wtf.data.Store({
                        reader : dsReader
                    });
                    this.opportunitypanel1 = new Wtf.common.KWLListPanel({
                        title: WtfGlobal.getLocaleText("crm.globalsearch.opportunityresultsfor.title"),//'Opportunity Results for ',
                        id: 'opportunitypanel1',
                        autoLoad: false,
                        autoScroll: true,
                        layout : "fit",
                        paging : false,
                        dataViewStore : searchDS
                    });
                    this.SearchOnlyOpportunity = new Wtf.DataView({
                        store: searchDS,
                        tpl: this.template,
                        autoHeight:true,
                        style:"margin:10px!important;width:97%;padding-left:5px;",
                        emptyText: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//'No results to display',
                        overClass:'x-view-over',
                        itemSelector:'div.thumb-wrap'
                    });
                     this.opportunitypanel1.on("render",function(){
                        document.getElementById(this.opportunitypanel1.body.id).className = "overflowX-hidden";
                    },this);
                    this.opportunitypanel1.add(this.SearchOnlyOpportunity);
                    this.opportunitypanel1.doLayout();
                }
                this.DisplayTab(WtfGlobal.getLocaleText("crm.opportunitysearch"), "pwndCRM opportunityTabIcon", "SearchOpportunity", this.opportunitypanel1);
                break;
            case 'lea':
                if (document.getElementById('SearchTabPanel__SearchLead') == null) {
                     dsReader = new Wtf.data.JsonReader({
                        root: "data",
                        fields: [
                        {
                            name: 'id'
                        },

                        {
                            name: 'name'
                        },

                        {
                            name: 'owners'
                        },

                        {
                            name: 'createdon'
                        },

                        {
                            name: 'img'
                        },
                        {
                            name: 'moduleName'
                        }
                        ]
                    });
                    searchDS = new Wtf.data.Store({
                        reader : dsReader
                    });
                    this.leadpanel1 = new Wtf.common.KWLListPanel({
                        title: WtfGlobal.getLocaleText("crm.globalsearch.leadresultsfor.title"),//'Lead Results for ',
                        id: 'leadpanel1',
                        autoLoad: false,
                        autoScroll: true,
                        layout : "fit",
                        paging : false,
                        dataViewStore : searchDS
                    });
                    this.SearchOnlyLead = new Wtf.DataView({
                        store: searchDS,
                        tpl: this.template,
                        autoHeight:true,
                        style:"margin:10px!important;width:97%;padding-left:5px;",
                        emptyText: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//'No results to display',
                        overClass:'x-view-over',
                        itemSelector:'div.thumb-wrap'
                    });
                     this.leadpanel1.on("render",function(){
                        document.getElementById(this.leadpanel1.body.id).className = "overflowX-hidden";
                    },this);
                    this.leadpanel1.add(this.SearchOnlyLead);
                    this.leadpanel1.doLayout();
                }
                this.DisplayTab(WtfGlobal.getLocaleText("crm.leadsearch"), "pwndCRM lead", "SearchLead", this.leadpanel1);
                break;
            case 'con':
                if (document.getElementById('SearchTabPanel__SearchContact') == null) {
                     dsReader = new Wtf.data.JsonReader({
                        root: "data",
                        fields: [
                        {
                            name: 'id'
                        },

                        {
                            name: 'name'
                        },

                        {
                            name: 'owners'
                        },

                        {
                            name: 'createdon'
                        },

                        {
                            name: 'img'
                        },
                        {
                            name: 'moduleName'
                        }
                        ]
                    });
                    searchDS = new Wtf.data.Store({
                        reader : dsReader
                    });
                    this.contactpanel1 = new Wtf.common.KWLListPanel({
                        title: WtfGlobal.getLocaleText("crm.globalsearch.contactresultsfor.title"),//'Contact Results for ',
                        id: 'contactpanel1',
                        autoLoad: false,
                        autoScroll: true,
                        layout : "fit",
                        paging : false,
                        dataViewStore : searchDS
                    });
                    this.SearchOnlyContact = new Wtf.DataView({
                        store: searchDS,
                        tpl: this.template,
                        autoHeight:true,
                        style:"margin:10px!important;width:97%;padding-left:5px;",
                        emptyText: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//'No results to display',
                        overClass:'x-view-over',
                        itemSelector:'div.thumb-wrap'
                    });
                     this.contactpanel1.on("render",function(){
                        document.getElementById(this.contactpanel1.body.id).className = "overflowX-hidden";
                    },this);
                    this.contactpanel1.add(this.SearchOnlyContact);
                    this.contactpanel1.doLayout();
                }
                this.DisplayTab(WtfGlobal.getLocaleText("crm.contactsearch"), "pwndCRM contactsTabIcon", "SearchContact", this.contactpanel1);
                break;
            case 'cas':
                if (document.getElementById('SearchTabPanel__SearchCase') == null) {
                     dsReader = new Wtf.data.JsonReader({
                        root: "data",
                        fields: [
                        {
                            name: 'id'
                        },

                        {
                            name: 'name'
                        },

                        {
                            name: 'owners'
                        },

                        {
                            name: 'createdon'
                        },

                        {
                            name: 'img'
                        },
                        {
                            name: 'moduleName'
                        }
                        ]
                    });
                    searchDS = new Wtf.data.Store({
                        reader : dsReader
                    });
                    this.casepanel1 = new Wtf.common.KWLListPanel({
                        title: WtfGlobal.getLocaleText("crm.globalsearch.caseresultsfor.title"),//'Case Results for ',
                        id: 'casepanel1',
                        autoLoad: false,
                        autoScroll: true,
                        layout : "fit",
                        paging : false,
                        dataViewStore : searchDS
                    });
                    this.SearchOnlyCase = new Wtf.DataView({
                        store: searchDS,
                        tpl: this.template,
                        autoHeight:true,
                        style:"margin:10px!important;width:97%;padding-left:5px;",
                        emptyText: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//'No results to display',
                        overClass:'x-view-over',
                        itemSelector:'div.thumb-wrap'
                    });
                     this.casepanel1.on("render",function(){
                        document.getElementById(this.casepanel1.body.id).className = "overflowX-hidden";
                    },this);
                    this.casepanel1.add(this.SearchOnlyCase);
                    this.casepanel1.doLayout();
                }
                this.DisplayTab(WtfGlobal.getLocaleText("crm.casesearch"), "pwndCRM caseIcon", "SearchCase", this.casepanel1);
                break;
            case 'pro':
                if (document.getElementById('SearchTabPanel__SearchProject') == null) {
                     dsReader = new Wtf.data.JsonReader({
                        root: "data",
                        fields: [
                        {
                            name: 'id'
                        },

                        {
                            name: 'name'
                        },

                        {
                            name: 'owners'
                        },

                        {
                            name: 'createdon'
                        },

                        {
                            name: 'img'
                        },
                        {
                            name: 'moduleName'
                        },
                        {
                            name: 'moduleName'
                        }
                        ]
                    });
                    searchDS = new Wtf.data.Store({
                        reader : dsReader
                    });
                    this.projectpanel1 = new Wtf.common.KWLListPanel({
                        title: WtfGlobal.getLocaleText("crm.globalsearch.productresultsfor.title"),//'Product Results for ',
                        id: 'projectpanel1',
                        autoLoad: false,
                        autoScroll: true,
                        layout : "fit",
                        paging : false,
                        dataViewStore : searchDS
                    });
                    this.SearchOnlyProduct = new Wtf.DataView({
                        store: searchDS,
                        tpl: this.template,
                        autoHeight:true,
                        style:"margin:10px!important;width:97%;padding-left:5px;",
                        emptyText: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//'No results to display',
                        overClass:'x-view-over',
                        itemSelector:'div.thumb-wrap'
                    });
                     this.projectpanel1.on("render",function(){
                        document.getElementById(this.projectpanel1.body.id).className = "overflowX-hidden";
                    },this);
                    this.projectpanel1.add(this.SearchOnlyProduct);
                    this.projectpanel1.doLayout();
                }
                this.DisplayTab(WtfGlobal.getLocaleText("crm.productsearch"), "pwndCRM product", "SearchProject", this.projectpanel1);
                break;
            case 'doc':
                   dsReader = new Wtf.data.JsonReader({
                        root: "data",
                        fields: [
                            {
                                name: 'name'
                            },{
                                name: 'size'
                            },{
                                name: 'Tags'
                            },{
                                name: 'Type'
                            },{
                                name: 'uploadeddate'
                            },{
                                name: 'RevisionNumber'
                            },{
                                name: 'author'
                            },{
                                name: 'Summary'
                            },{
                                name: 'docid'
                            },{
                                name: 'fileimage'
                            }]
                })
                var searchdocDs = new Wtf.data.Store({
                    reader: dsReader
                });

                if (document.getElementById('SearchTabPanel__Searchdocument') == null) {
                    this.KGridOnlyDoc = new Wtf.common.KWLListPanel({
                        title: WtfGlobal.getLocaleText("crm.globalsearch.documentresultsfor.title"),//"Documents Results for ",
                        id: 'gridpaneldocsearch1',
                        paging: false,
                        autoLoad: false,
                        layout: "fit",
                        dataViewStore : searchdocDs,
                        autoScroll: true
                    });
                    var tpl = new Wtf.XTemplate(
                        '<tpl for=".">',
                            '<div class="workspace" style="width:98%!important;">',
								'<div class="workspace-vote"><img src="images/{fileimage}"/></div>',
								'<div class="workspace-text">',
								'<h2 class="work-title" ><strong><a href="#" style="font-size:12px" onclick="javascript:setDldUrl(\'crm/common/Document/downloadDocuments.do?url={docid}&mailattch=true&dtype=attachment\');">{name}</a></strong></h2>',
                                  '<div class="by-name search-summary">{Summary}</div>',
                                  '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.globalsearch.allsearch.contributor")+'<strong>{author}</strong></div>',
								  '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.globalsearch.allsearch.uploadedon")+'<strong>{uploadeddate}</strong></div>',
								  '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.mydocuments.header.size")+': <strong>{size} </strong></div>',
                                  '<div class="search-params workspace-dust" style="width:auto!important;">'+WtfGlobal.getLocaleText("crm.globalsearch.allsearch.tags")+':<strong>{Tags}</strong></div>',
								'</div>',
							  '</div>',
                        '</tpl>'
                    );
                    this.SearchOnlyDocGrid = new Wtf.DataView({
                        store: searchdocDs,
                        tpl: tpl,
                        autoHeight:true,
                        style:"margin:10px!important;width:97%;padding-left:5px;",
                        emptyText:WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),// 'No results to display',
                        overClass:'x-view-over',
                        itemSelector:'div.thumb-wrap'
                    });
                    this.KGridOnlyDoc.on("render",function(){
                        document.getElementById(this.KGridOnlyDoc.body.id).className = "overflowX-hidden";
                    },this);
                    this.KGridOnlyDoc.add(this.SearchOnlyDocGrid);
                    this.KGridOnlyDoc.doLayout();
                }
                this.DisplayTab(WtfGlobal.getLocaleText("crm.documentsearch"), "pwnd doctabicon", "Searchdocument", this.KGridOnlyDoc);
                break;
        }
    },

    DisplayTab: function(title, iconCls, id, items){
        if (document.getElementById('SearchTabPanel__' + id) == null) {
            var tab = this.tabpanel.add({
                title: title,
                iconCls: iconCls,
                id: id,
                layout: 'fit',
                border: false,
                frame: false,
                items: [items]

            });
            this.tabpanel.setActiveTab(id);
            this.tabpanel.doLayout();
        }
        else {
            Wtf.getCmp("SearchTabPanel").activate(id);
        }
    },

    hidewindow: function(){
        if (document.getElementById("win1") != null) {
            Wtf.getCmp("win1").hide();
        }
    },

    searchpop: function(obj, rindex, eventobj){
        var record = this.ds.getAt(rindex);
        var a = record.get("Summary");
        var b = record.get("FileName");
        var c = record.get("Type");
        var size = record.get("Size");
        eventobj.preventDefault();
        var target = eventobj.getTarget();

        var oldContainPane = Wtf.getCmp("containpane");
        if(oldContainPane)      // if containpane already present then destroy it and create again as template could not be overwritten
            oldContainPane.destroy();

        new Wtf.Panel({
            id: "containpane",
            frame: true,
            hideBorders: true,
            baseCls: "sddsf",
            header: false,
            headerastext: false
        });

        var oldWin1 = Wtf.getCmp("win1");
        if(oldWin1)             // if win1 already present then destroy it and create again
            oldWin1.destroy();

        new Wtf.Window({
            id: "win1",
            animateTarget: obj,
            width: 350,
            height: 200,
            plain: true,
            shadow: true,
            header: false,
            closable: false,
            border: false,
            items: Wtf.getCmp("containpane")
        }).show();

        this.tpl.insertAfter('containpane', {
            imgico: MimeBasedValue(c,true),
            type: MimeBasedValue(unescape(c),false),
            title: unescape(b),
            //size: Math.round(size / 1024),
            size: size,
            descp: unescape(a)
        });

        Wtf.getCmp("win1").setPagePosition(eventobj.getPageX(), eventobj.getPageY());
    },
/* Kuldeep
     onDocGridRowContextMenu: function(grid, num, e){

        e.preventDefault();

        var open = new Wtf.Action({
            text: 'Open',
            iconCls: 'dpwnd Openfile',
            scope:this,
            handler: function(){
//                grid.fireEvent('rowdblclick', grid, grid.getSelectionModel().getSelected());
                this.displayContent(grid)
            }
        });
        var gridMenu = new Wtf.menu.Menu({
                //id: 'gridMenu',
                items: [open]
        });
        grid.getSelectionModel().selectRow(num);
     //   grid.fireEvent('rowclick', grid, num, e);
        rownum = num;
        var posnX = e.getPageX();
        var posnY = e.getPageY();
        gridMenu.showAt([posnX, posnY]);
        return false;
    },
   // rowdblclick:function(a,b){
        displayContent:function(a){
        var selectedRow = a.getSelectionModel().getSelected();
        if(!docScriptLoaded){
            WtfGlobal.loadScript("../../scripts/minified/document.js?v=19");
            docScriptLoaded = true;
        }
        Wtf.Ajax.requestEx({
                    url: Wtf.req.doc + "file-releated/filecontent/filedownloadchk.jsp",
                    params:{
                        docid:selectedRow.get('DocumentId')
                    }},
                    this,
                    function(resp,option){
                        var respText = eval('('+resp+')');
                        if(respText.download=="no"){
                            //Wtf.getCmp("as").loadTab(Wtf.req.doc + 'file-releated/filecontent/fileContent.jsp?url=' + selectedRow.get('DocumentId'), 'tabfcontent'+selectedRow.get('DocumentId'),selectedRow.get('FileName'), '',Wtf.etype.docs);
                             var fileContent = Wtf.getCmp('tabfcontent'+selectedRow.get('DocumentId')+selectedRow.get('RevisionNumber'));
                            if(fileContent==null){
                                fileContent= new Wtf.FilecontentTab({
                                    url: selectedRow.get('DocumentId'),
                                    id: 'tabfcontent'+selectedRow.get('DocumentId')+selectedRow.get('RevisionNumber'),
                                    parentid:"as",
                                    title:selectedRow.get('FileName'),
                                    fileType:selectedRow.get('Type'),
                                    RevisionNumber:selectedRow.get('RevisionNumber')
                                });
                                Wtf.getCmp("as").add(fileContent);
                                Wtf.getCmp("as").doLayout();
                            }
                            Wtf.getCmp("as").activate(fileContent);
//                            filecontentTab(selectedRow.get('DocumentId'),'tabfcontent'+selectedRow.get('DocumentId')+selectedRow.get('RevisionNumber'),selectedRow.get('FileName'),selectedRow.get('Type'),selectedRow.get('RevisionNumber'));
                        }
                        else{
                            setDownloadUrl(selectedRow.get('DocumentId'));
                        }
                    });
    }, kuldeep*/
    btnHover : function(){
        Wtf.getCmp("textSearch").addClass("searchInput-over");
        if(this.id=="searchBtn"){
            Wtf.getCmp("searchBtn1").addClass("x-btn-over");
        }else{
            Wtf.getCmp("searchBtn").addClass("x-btn-over");
        }

    },
    btnOut : function(){
        Wtf.getCmp("textSearch").removeClass("searchInput-over");
        if(this.id=="searchBtn"){
            Wtf.getCmp("searchBtn1").removeClass("x-btn-over");
        }else{
            Wtf.getCmp("searchBtn").removeClass("x-btn-over");
        }

    },

    setDocumentValue: function(value){
        this.document = value;
        var obj = Wtf.getCmp("doc");
        if(value)
            obj.show();
        else
            obj.hide();
    },

    setDocumentViewValue: function(value) {
        this.documentView = value;
        var obj = Wtf.getCmp("doc");
        if(value)
            obj.show();
        else
            obj.hide();
    }
});

function MimeBasedValue(value,imgFlag){
    var type;
    switch (value.toLowerCase()) {
        case "microsoft excel document":
            if(imgFlag){
                type = "../../images/XLS.png";
            }else{
                type = "Microsoft Excel Document";
            }
            break;
        case "application/msword":
            if(imgFlag){
                type = "../../images/word.gif";
            }else{
                type = "Microsoft Word Document";
            }
            break;
        case "pdf Document":
            if(imgFlag){
                type = "../../images/PDF.gif";
            }else{
                type = "PDF Document";
            }
            break;
        case "text/plain":
            if(imgFlag){
                type = "../../images/TXT52.png";
            }else{
                type = "Plain Text File";
            }
            break;
        case "text/xml":
            if(imgFlag){
                type = "../../images/XML52.png";
            }else{
                type = "XML File";
            }
            break;
        case "text/css":
            if(imgFlag){
                type = "../../images/CSS52.png";
            }else{
                type = "CSS File";
            }
            break;
        case "html file":
            if(imgFlag){
                type = "../../images/HTML52.png";
            }else{
                type = "HTML File";
            }
            break;
        case "text/cs":
            if(imgFlag){
                type = "../../images/TXT52.png";
            }else{
                type = "C# Source File";
            }
            break;
        case "text/x-javascript":
            if(imgFlag){
                type = "../../images/TXT52.png";
            }else{
                type = "JavaScript Source";
            }
            break;
       default:
            if(imgFlag){
                type="../../images/TXT52.png"
            }else{
                type="File"
            }
    }
    return type;
}

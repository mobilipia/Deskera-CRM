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
Wtf.AboutViewForProfile = function(config){
    Wtf.apply(this, config);
    Wtf.AboutViewForProfile.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.AboutViewForProfile, Wtf.Panel, {
    initComponent: function(){
        Wtf.AboutViewForProfile.superclass.initComponent.call(this);

        this.image1 = document.createElement('img');
        if(Wtf.isGecko)
            this.image1.className = "tabimageG";
        else
            this.image1.className = "tabimage";
        this.UserTagurlpath = null;
        this.UserTagfields = null;
        this.view17 = new Wtf.DataView({
            itemSelector: 'div.thumb-wrap',
            style: 'overflow:auto;width:auto;height:auto;margin-left:8px;',
            multiSelect: true,
            loadingText: 'Collecting data...'
        });

        this.tagbutton2 = document.createElement('img');
        this.tagbutton2.src = '../../images/tag_green.gif';
        this.tagbutton2.title = "Edit Tags";
        this.tagbutton2.className = 'addtagbutton';

        this.view18 = new Wtf.DataView({
            itemSelector: 'a.taga',
            multiSelect: true,
            emptyText: '<span class="tagtitle">Tags:</span>',
            loadingText: 'Loading',
            tpl: new Wtf.XTemplate('<span class="tagtitle">Tags: </span>', '<tpl for=".">', '<a class="taga" href="" onClick="javascript: invokeTagSearch(this);">{tagname}</a>', '</tpl>')
        });
    },

    getHtml: function(){
        var str = '';
        str = '<div><span style="font-size:2.0em;font-weight:bold;float:left;">'+Wtf.util.Format.ellipsis(Wtf.getCmp('as').getActiveTab().title, 18)+'</span>'
        if(this.WhichAbout == 'project'){
            var index = projId.indexOf(this.currId);
            if(isToAppendArray[index] == 0)
                str += '<span id="'+this.currId+'_addSpan" class="addToDashLink" style="display:block;">[ <a href="#" onClick=\'addProjectWidgetOnDash(\"addSpan\",\"'+this.currId+'\",\"true\")\' wtf:qtip="Add this project\'s widget on Dashboard">Open Widget on Dashboard</a> ]</span></div>'+
                '<span id="'+this.currId+'_removeSpan" class="addToDashLink" style="display:none;">[ <a href="#" onClick=\'addProjectWidgetOnDash(\"removeSpan\",\"'+this.currId+'\",\"false\")\' wtf:qtip="Remove this project\'s widget from Dashboard">Remove Widget from Dashboard</a> ]</span></div>';
            else
                str += '<span id="'+this.currId+'_addSpan" class="addToDashLink" style="display:none;">[ <a href="#" onClick=\'addProjectWidgetOnDash(\"addSpan\",\"'+this.currId+'\",\"true\")\' wtf:qtip="Add this project\'s widget on Dashboard">Open Widget on Dashboard</a> ]</span></div>'+
                '<span id="'+this.currId+'_removeSpan" class="addToDashLink" style="display:block;">[ <a href="#" onClick=\'addProjectWidgetOnDash(\"removeSpan\",\"'+this.currId+'\",\"false\")\' wtf:qtip="Remove this project\'s widget from Dashboard">Remove Widget from Dashboard</a> ]</span></div>';
        } else {
            str += '';
        }
        return str;
    },

    onRender: function(config){
        Wtf.AboutViewForProfile.superclass.onRender.call(this, config);      
        this.add({
            bodyStyle: "background-color:#FFFFFF; margin:0 0 0 0; padding:0 0 0 0; color:#000000; font-size:12px;",
            border: false,
            layoutConfig: {
                animate: true
            },
            layout: "column",
            items: [ {
                columnWidth: 0.7,
                border: false,
                cls:'homeInfo',
                items: [{
                    bodyStyle: "height:auto; overflow:hidden; margin-left:5px; padding-bottom:5px;",
                    html: this.getHtml(),
                    border: false,
                    id: this.id.substr(0, this.id.indexOf('profileName'))+'projInfo'
                }, this.view17
                ]
            },{
                columnWidth: 0.2,
                border: false,
                bodyStyle: "padding-left:10px;padding-top:7px;",
                layout: "fit"
               // contentEl: this.image1
            }]
        });
        this.add({bodyStyle: "background-color:#FFFFFF; margin:-8px 0 0 0; padding:0 0 0 0; color:#000000; font-size:12px;",
            border: false,
            layoutConfig: {
                animate: true
            },
            layout: "column",
            items: [{
                bodyStyle: "margin-top:5px;height:auto; overflow:auto;",
                border: false,
                width: '100%',
                items:[
                    {
                    bodyStyle: "height:20px; overflow:hidden; margin-top:5px; padding-top:10px; padding-bottom:6px;",
                    border: false,
                    layout: "column",
                    items: [{
                        width: 18,
                        border: false
                        //contentEl: this.tagbutton2
                    }, {
                        columnWidth: 0.5,
                        border: false,
                        items: this.view18
                    }]
                }]
            }]
        });
        
        this.doLayout();
    },

    setImage: function(path){
        if (path.match("/store")) {
            var index = path.lastIndexOf(".");
            var fpath = path.substr(0, index);
            var ext = path.substr(index);
            path = fpath +ext;
        }
        this.image1.src = path;
    },
  

    setAboutDetails: function(Abouturlpath, Abouturlfields, status){
        var str = this._getStore(Abouturlpath, 'data', Abouturlfields);
        str.on("load",function(str){            
            this.view17.setStore(str);
            
        },this);
         
        if(this.WhichAbout == 'user'){
            
            str.load();
            this.templ = new Wtf.XTemplate('<tpl for=".">',
                '<div class="tagsMainDiv"><span id="tagsspan1" class="tagsspan" style="padding-right:7% !important;">User Name : </span><span id="ts1" class="tagsdiv1">{username}</span>',
                '<br clear="all"></div><div class="tagsMainDiv"><span id="tagsspan2" class="tagsspan" style="padding-right:7% !important;">First Name : </span><span id="ts2" class="tagsdiv1">{fname}</span>',
                '<br clear="all"></div><div class="tagsMainDiv"><span id="tagsspan2" class="tagsspan" style="padding-right:7% !important;">Last Name : </span><span id="ts2" class="tagsdiv1">{lname}</span>',
                '<br clear="all"></div><div class="tagsMainDiv"><span id="tagsspan2" class="tagsspan" style="padding-right:8% !important;">E-mail ID : </span><span id="ts2" class="tagsdiv1">{emailid}</span>',
                '<br clear="all"></div><div class="tagsMainDiv"><span id="tagsspan3" class="tagsspan" style="padding-right:9% !important;">Address : </span><span id="ts3" class="tagsdiv1">{address}</span>',
                '<br clear="all"></div><div class="tagsMainDiv"><span id="tagsspan3" class="tagsspan" style="padding-right:12% !important;">Role : </span><span id="ts3" class="tagsdiv1">{rolename}</span>',
                '<br clear="all"></div><div class="tagsMainDiv"><span id="tagsspan4" style="color:#15428B;float:left;font-weight:bold;margin:2px 0;padding-top:4px;padding-right:6%;">Contact No. : </span><span id="ts4" class="tagsdiv1">{contactno}</span>',
                '<br clear="all"></div><span id="tagsspan4" style="color:#15428B;float:left;font-weight:bold;margin:2px 0;padding-top:4px;padding-right:8%;">About Me : </span><span id="ts4" class="tagsdiv1">{aboutuser}</span>',
                '</tpl>');
        }

        this.view17.tpl = this.templ;      
        
    },

    _getStore: function(u, r, f){

      
        var recordarr = [];
        for(var i =0;i<f.length;i++){
            recordarr.push(f[i]);
        }
        var tplRecord = new Wtf.data.Record.create(recordarr);
        if(r != 'tags')
            this.tplRecord = tplRecord;
        return new Wtf.data.Store({
            url: u,
            autoLoad: false,
            reader: new Wtf.data.KwlJsonReader({
                root: 'data'
            },tplRecord)
        });
    },

    setTagDetails: function(Tagurlpath, Tagurlfields){
        var str = this._getStore(Tagurlpath, 'tags', Tagurlfields);
        str.load();
        this.view18.setStore(str);
        this.UserTagurlpath = Tagurlpath;
        this.UserTagfields = Tagurlfields;
    }
});
/*  WtfAboutView: End   */

/*  WtfProfileView: Start   */
Wtf.ProfileView = function(config){
    Wtf.apply(this, config);
   
};

Wtf.extend(Wtf.ProfileView, Wtf.Panel, {
    buttonPosition: 1,
    initComponent: function(){
        Wtf.ProfileView.superclass.initComponent.call(this);
        this.about = new Wtf.AboutViewForProfile({
            id: this.id + 'profileName',
            archived: false,
            currId: this.uid.userid,
            border: false,
            WhichAbout: (this.uid.type == Wtf.etype.proj) ? 'project' : 'user',
            projectIndex: this.projectIndex
        });     
        this.loadcustomdata();
    },
 
    onRender: function(config){
        Wtf.ProfileView.superclass.onRender.call(this, config);        
        this.ComponentMainPanel = this.add({
            layout: "fit",
             bodyStyle: "background-color:#FFFFFF; height:auto;overflow-y:auto;",           
                    items: [{
                        bodyStyle: 'padding:16px 16px 0 16px;border-right:1px solid #CEDFF5;',
                        border: false,
                        autoHeight: true,
                        items: this.about
                    }]
        });

        this.ComponentMainPanel.doLayout();
  
        if(this.uid.type == Wtf.etype.proj && typeof this.projectIndex == 'number'){
            var task = new Wtf.util.DelayedTask(function(){
                this.loadcustomdata();
            }, this);
            task.delay(20);
        }
    },

    loadcustomdata: function(){
        this.setIdentity(loginid, this.uid.userid);
        this.doLayout();
    },

    loaddata: function(){
        this.loadcustomdata();
    },

    setLayoutDetails: function(jspuserdet, arg1, arg2,whichabout){
            this.about.WhichAbout = 'user';
            this.about.setAboutDetails(jspuserdet , ["username","fname","lname","emailid", "address", "contactno", "aboutuser","contactno","rolename"], this.connstatus);
    },
    setIdentity: function(){
        this.setLayoutDetails('Common/ProfileHandler/getAllUserDetails.do?&lid='+this.uid.userid, 'fname', 'lname','user');
    }
});

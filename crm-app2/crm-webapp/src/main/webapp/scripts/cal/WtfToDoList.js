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
Wtf.TodoList = function(config){
    Wtf.apply(this,config);
    this.nodeHash = {};
    this.url = config.url;
    if(!this.url)
        this.url = Wtf.req.prj + 'todolistmanager.jsp';
    this.groupForm = null;
    this.taskform=null;
    this.taskform1=null;
    this.newTaskWindow=null;
    this.priorityCombo=null;
    this.taskcomp_imgpath = '../../images/tick.png';
    this.taskincom_imgpath = '../../images/tickcdis.png';
    this.normal_task = '../../images/normal1.png';
    this.high_task = '../../images/todo_high.png';
    this.low_task = '../../images/todo_low.png';
    this.hightitle = "High Priority Task";
    this.lowtitle = "Low Priority Task";
    this.normaltitle = "Normal Priority Task";
    this.assignedCombo=null;
    this.taskNameField = null;
    this.roleid = config.roleid;
    if(!this.archived){
        this.sinNotification = new Wtf.menu.Item({
            iconCls:'pwnd outbox',
            text : "Notify Selected",
            allowDomMove:false,
            disabled: true,
            scope : this,
            handler : this.sendNotification
        });
        this.allNotification = new Wtf.menu.Item({
            iconCls:'pwnd outbox',
            text : "Notify All",
            scope : this,
            disabled: true,
            handler : this.sendNotificationtoAll
        });
        this.tbar= [{
            iconCls:'pwnd todolistpane',
            text:'Add To new Group',
            id: 'todoAddGroup',
            tooltip: {title: 'Add To Do Group',text: Wtf.Help.addtodogroup},
            handler: this.addTaskGroup,
            scope:this
        },{
            iconCls:'addnew',
            text:'Add To Do',
            id: 'todoAddTodo',
            tooltip: {title: 'Add To Do',text: Wtf.Help.addtodo},
            handler: this.addTask,
            scope:this
        },
          this.deletebutton = new Wtf.Toolbar.Button({
            iconCls:'pwnd deliconwt',
            tooltip: {title: 'Delete To Do',text: 'Click to delete selected to do.'},
            text:'Delete To Do',
            disabled: true,
            scope:this,
            id: 'todoDelete',
            hidden : this.roleid == 3 ? false :true,
            handler: this.confirmTaskDelete//this.deleteSelected
        }),new Wtf.Toolbar.Button({
            text:'Import Outlook Task',
            iconCls:'pwnd importiconToDo',
            id : 'importtodo',
            tooltip: {title:'Import To Do',text: "Click to import Outlook Task."},
            scope:this,
            menu : {
                items: [{
                        text: 'Import through CSV (.csv format)',
                        tooltip: {
                            text:Wtf.Help.importtodo
                        },
                        iconCls: 'pwnd importicon',
                        scope: this,
                        handler: this.importFromCSV
                    }]
            }
            /*menu: [
                    new Wtf.Action({
                        text: 'Import through CSV (.csv format)',
                        iconCls: 'pwnd importicon',
                        scope: this,
                        handler: this.importFromCSV
                    })
                ]*/
          //  handler: this.importFromCSV
        }),"-",
            "<a href=\""+Wtf.pagebaseURL+"feed.rss?m=todos&p="+config.userid+"\" target='_blank'><img id=\"todoRss\" class=\"rssimgMid\" alt=\"\" src=\"../../images/FeedIcon16.png\" Wtf:qtip='"+Wtf.Help.rsstodo+"'/>","-"
        ];
    }
    this.listRoot = new Wtf.tree.TreeNode({
        draggable:false,
        id:'root_todo'+config.id
    });
    this.setRootNode(this.listRoot);
    if(config.groupType == Wtf.etype.proj){
        this.memberstorerecord = Wtf.data.Record.create([
                {name: 'name'}, {name: 'id'}
        ]);
        this.memberstore = new Wtf.data.Store({
            method: 'GET',
            id: 'tempstore',
            url:'../../jspfiles/project/getProjectMembers.jsp?login='+config.userid+'&pageno=0&pageSize=10000',
            reader : new Wtf.data.KwlJsonReader1({
                root: 'data'
             },this.memberstorerecord)
        });
        this.groupstorerecord = Wtf.data.Record.create([
                {name:'id',mapping:'id'},
        		{name:'name',mapping:'name'}
        ]);
        this.groupstore = new Wtf.data.SimpleStore({
               id    : 'temp1store',
               fields: [
						 {name: 'id'},
           				 {name: 'name'}
                       ]
        });
        this.taskpri = [["High"],["Normal"],["Low"]];
        this.prioritystore = new Wtf.data.SimpleStore({
               id    : 'pristore',
               fields: ['priority'],
               data: this.taskpri
        });
     }
     var jRecord = Wtf.data.Record.create([
        {name: 'taskname'},
        {name: 'taskid'},
        {name: 'parentId'},
        {name: 'status'},
        {name: 'taskorder'},
        {name: 'assignedto'},
        {name: 'leafflag'},
        {name: 'duedate'},
        {name: 'description'},
        {name: 'priority'}
    ]);
    this.reader = new Wtf.data.KwlJsonReader1({
        root:"data"
    },jRecord);
    this.ds1 = new Wtf.data.Store({
        url:this.url,
        baseParams: {action:1,userid:config.userid,grouptype:config.groupType},
        reader:this.reader
    });
    this.selModel=new Wtf.tree.MultiSelectionModel({
        id: 'test' + this.id
    }),
    this.ds1.on("load",this.dataRefresh,this);
    this.loadflag = true;
    this.append = true;
    Wtf.TodoList.superclass.constructor.call(this);
    if(!this.archived){
        this.on("movenode",this.nodeMoved,this);
        this.on("beforemovenode",this.beforeNodeMove,this);
        this.getSelectionModel().on('selectionchange',this.selectionChange,this);
        this.on('contextmenu', this.contextMenu, this);
    }
}
Wtf.extend(Wtf.TodoList,Wtf.tree.TreePanel,{
    afterRender: function() {
        Wtf.TodoList.superclass.afterRender.call(this);
        this.memberstore.load();
        },
    dataRefresh:function(ds,record,obj){
        if(this.ds1.data.length == 0){
            this.addEmptyText();
        }
//        } else {
//            this.removeEmptyText()
//        }
        if(this.loadflag ){
            if(this.getRootNode().childNodes != null){
                if(this.getRootNode().childNodes.length > 0){
                        var nodelen = this.getRootNode().childNodes.length;
                        for(var i = 0; i < nodelen; i++){
                            this.getRootNode().childNodes[0].remove();
                        }
                }
            }
            this.loadflag = false;
            if(this.memberstore.find("name", '-') == -1){
                var none = new this.memberstorerecord({
                            name:'-',
                            id:'0'
                });
                this.memberstore.add(none);
            }
            if(this.groupstore.find("name", '-') == -1){
                var none1 = new this.groupstorerecord({
                            id:'0',
                            name:'-'
                });
                this.groupstore.add(none1);
            }
            this.isDataLoad=true;
            for(var i=0;i<record.length;i++) {
                var _data = record[i].data;
                var nodetxt =  _data.taskname;
                var nodeid = _data.taskid;
                var nodestate = parseInt(_data.status);
                var Torder = parseInt(_data.taskorder);
                var parentn = _data.parentId;
                var assigned = record[i].get("assignedto");
                var leafflag = true;
                var task_priority = _data.priority;
                var dd = _data.duedate;
                if(typeof record[i].get("leafflag") == "boolean")
                    leafflag = record[i].get("leafflag");
                else
                    leafflag = record[i].get("leafflag") == '0' ? false : true;
                var status=false;
                parentn = this.getNodeById(parentn);
                var nodeclass = "todoNode";
                if(!parentn)
                    parentn = this.listRoot;
                if(!leafflag){
                    nodeclass = "groupNode";
                    var newgroup=new this.groupstorerecord({
 								id   : nodeid,
								name : nodetxt
					});
                    this.groupstore.add(newgroup);
                }
                var src = nodestate==0?this.taskincom_imgpath:this.taskcomp_imgpath;
                var title = nodestate==1?'Mark as incomplete':'Mark as complete';
                var d = new Date();
                var today = new Date();
                d.setFullYear(parseInt(dd.substr(0,4)),(parseInt(dd.substr(5,2),10))-1,parseInt(dd.substr(8,2),10));
                var node_priority_part = this.setPriorityImg(task_priority);
                var node_part = "<img id = 'mark"+nodeid+"' class='cancel' src='"+src+"' onclick= \"markasComplete('" + nodeid + "','"+ this.id + "')\"style=\"margin-left:5px;vertical-align:middle;margin-right:10px;\" title='"+title+"'></img>"
                if(assigned!=""){
                    var memberid = this.memberstore.getAt(this.memberstore.find("id",assigned));
                    var assignedmember= memberid.data["name"];
                    if(d < today && dd.indexOf("1970")==-1){
                        nodetxt = node_priority_part + node_part + "<span id='span"+nodeid+"' style=\"color:red;\">"+nodetxt+"["+assignedmember+"]</span>";
                    }else{
                        nodetxt = node_priority_part + node_part + "<span id='span"+nodeid+"' style=\"color:black;\">"+nodetxt+"["+assignedmember+"]</span>";
                    }
                }else{
                    if(d < today && dd.indexOf("1970")==-1){
                        nodetxt = node_priority_part + node_part + "<span id='span"+nodeid+"' style=\"color:red;\">"+nodetxt+"</span>";
                    }else{
                        nodetxt = node_priority_part + node_part + "<span id='span"+nodeid+"' style=\"color:black;\">"+nodetxt+"</span>";
                    }
                }
                if(nodestate == 1)
                    status = true;
                else
                    status = false;
                var tempnode = new Wtf.tree.TreeNode({
                    id:nodeid,
                    text:nodetxt,
                    tname:nodetxt,
                    draggable: !this.archived,
                    iconCls:nodeclass,
                    parentnode:_data.parentId,
                    leaf:leafflag,
                    nodestate:nodestate,
                    Torder:Torder,
                    assignedTo:assigned,
                    duedate:_data.duedate,
                    priority:task_priority,
                    desc:_data.description
                });
                parentn.insertBefore(tempnode,parentn.item(Torder));
                parentn.expand();
                if(nodestate==1)
                    tempnode.ui.getTextEl().lastChild.style.textDecoration = "line-through";
                if(!this.archived)
                    tempnode.on("dblclick",this.editNode,this);
            }
        }
        if(!this.archived){
            if(record.length>0)
                this.allNotification.enable();
            else
                this.allNotification.disable();
        }
    },

    addEmptyText: function(){
        //emptytodo
       Wtf.DomHelper.append(this.body.dom, '<div id="empty" class="emptyGridText">No To-Do to display. <br><a href="#" onClick=\'getTodo(\"'+this.id+'\")\'>Start by Adding New ToDo Here</a></div>');

    },
    removeEmptyText: function(){
        //emptytodo
       Wtf.get(this.body.dom.childNodes[0]).remove();

    },
    contextMenu: function(node, e){
        node.select();
        menu = new Wtf.menu.Menu({
            id: 'todotreeMenu',
            items: [{
                text: 'Edit',
                id: 'name',
                iconCls: 'pwnd renameicon',
                scope:this,
                handler: this.editNodeOncontextMenu
            }, new Wtf.Action({
                text: "Delete",
                iconCls: 'pwnd delicon',
                scope:this,
                handler: this.confirmTaskDelete//this.deleteSelected
            })]
        });
        menu.showAt(e.getXY());
    },
    editNodeOncontextMenu:function(){
        var node_select = this.getSelectionModel().getSelectedNodes()[0];
        if(node_select.isLeaf())
            this.editnode1(node_select);
        else
            this.editGroup(node_select);
    },
    beforeNodeMove: function(tree,tnode,oldp,newp,num){
        if(!tnode.isLeaf() && newp != this.listRoot)
            return false;
    },

    confirmTaskDelete: function(){
        Wtf.MessageBox.confirm('Confirm', 'Are you sure?', function(btn){
            if (btn == "yes") { this.deleteSelected(); }
            else return ;
        }, this);
    },
    deleteSelected: function(){
        var nodes = this.getSelectionModel().getSelectedNodes();//this.getChecked();
        for(var i=0;i<nodes.length;i++){
            if(!nodes[i].attributes.leaf){
                var removegroup = this.groupstore.getAt(this.groupstore.find("id",nodes[i].id));
                this.groupstore.remove(removegroup);
            }
        }
        var nodeid = "";
        for(var i=0;i<nodes.length;i++){
            nodeid += nodes[i].attributes.id+",";
        }
        if(nodeid!=""){
             nodeid = nodeid.substr(0,nodeid.lastIndexOf(","));
              this.deletebutton.disable();
             Wtf.Ajax.requestEx({
                method: 'POST',
                url:this.url,
                params: ({
                    action:4,
                    taskid:nodeid
                })},
                this,
                function(result, req){
                    this.ds1.load();
                    while(nodes.length>0){
                        var tempNode = nodes.pop();
                        try{
                            tempNode.remove();
                        }catch(e){
                            clog(e);
                        }
                    }
                    this.deletebutton.disable();
                },
                function(result, req){

                });
        }
    }, 
    editNode:function(tnode,evt){
        if(tnode.isLeaf())
            this.editnode1(tnode);
        else
            this.editGroup(tnode);
    }, 
    editGroup : function(node){
        this.makeForm(1);
        this.taskNameField.setValue(WtfGlobal.HTMLStripper(node.text));
        this.newTaskWindow = new Wtf.Window({
            width:300,
            resizable : false,
            id : 'editToDoGr'+this.id,
            modal:true,
            iconCls : 'iconwin',
            title:'Edit To Do Group',
            buttons: [{
                anchor : '90%',
                id : 'save',
                text: WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                handler:this.editsinglegroup,
                scope:this
            },{
                anchor : '90%',
                id : 'editWinClose'+this.id,
                text: 'Close',
                handler:function() {
                    Wtf.getCmp('editToDoGr'+this.id).close();
                },
                scope:this
            }],
            items:[this.groupForm]
        }).show();
    },
    editnode1 : function(node){
        this.makeForm1(2,node.id);
        var nodetxt = WtfGlobal.HTMLStripper(node.text);
        var nid = node.id;
        if(!Wtf.get('newtodo'+nid)){
            var newtext = "<div style='margin: 10px; border-top: 1px solid black;border-bottom: 1px solid black;'><div id='newtodo"+nid+"' style=\"display:none;padding-top: 10px;padding-bottom:10px;margin-left:100px;\" onclick=\"return false;\"></div></div>";
            node.setText(node.text + newtext);
            Wtf.getCmp('tf'+ node.id).setValue(nodetxt.split("[")[0]);
            var dd = node.attributes.duedate;
            var tem = dd.split(" ")[0];
            var d1 = tem.replace(/\//g,"-");
            Wtf.getCmp('dd'+node.id).format = 'Y-m-d';
            if(dd.indexOf("1970") == -1 || dd.indexOf("1969") == -1){
                 Wtf.getCmp('dd'+node.id).setValue(d1);
            }
            var taskid = this.ds1.getAt(this.ds1.find("taskid",node.attributes.id));
            var pid = taskid.data["parentId"];
            var pid1 = node.parentNode.id;
            if(pid){
                Wtf.getCmp('Groupcombo'+node.id).setValue(pid1);
            }
            var de = WtfGlobal.HTMLStripper(taskid.data["description"]);
            Wtf.getCmp('ta'+node.id).setValue(de);
            var priority = node.attributes.priority;
            Wtf.getCmp('pricombo'+node.id).setValue(priority);
            var assign = taskid.data["assignedto"];
            if(assign){
                 Wtf.getCmp('combo'+node.id).setValue(assign);
            }
            this.newTaskWindow = new Wtf.Panel({
                width:900,
                resizable : false,
                modal:true,
                frame:true,
                iconCls : 'iconwin',
                baseCls :'todoouterpanel',
                id : 'editToDoTask'+nid,
                layout:'fit',
                renderTo:'newtodo'+nid,
                buttonAlign:'center',
                buttons: [{
                    anchor : '90%',
                    id : 'save'+nid,
                    text: WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                    handler:this.editsinglenode1,
                    scope:this
                },{
                    anchor : '90%',
                    id : 'editWinClose'+nid,
                    text: 'Close',
                    handler:function() {
                        var node = this.getNodeById(nid);
                        var task_id = this.ds1.getAt(this.ds1.find("taskid",node.attributes.id));
                        var taskname = task_id.data["taskname"];
                        var assignedTo = task_id.data["assignedto"];
                        if(assignedTo!=""){
                                var assigned = this.memberstore.getAt(this.memberstore.find("id",assignedTo)).data["name"];
                                this.setNodeText(node,taskname+"["+assigned+"]");
                        }else{
                                this.setNodeText(node,taskname);
                        }
                        if(node.attributes.nodestate==1){
                                node.ui.getTextEl().lastChild.style.textDecoration="line-through";
                        }
                        var dd = task_id.data["duedate"];
                        var d = new Date();
                        var today = new Date();
                        d.setFullYear(parseInt(dd.substr(0,4)),(parseInt(dd.substr(5,2),10))-1,parseInt(dd.substr(8,2),10));
                        if(d < today && dd.indexOf("1970")==-1){
                                node.ui.getTextEl().lastChild.style.color="red";
                        }
                   },
                   scope:this
                }],
                items:[this.taskform1]
            });
            Wtf.get("newtodo"+nid).dom.style.display="block";
        }
    },
    editsinglegroup:function(){
        var node = this.getSelectionModel().getSelectedNodes()[0];
        var taskname = WtfGlobal.HTMLStripper(this.taskNameField.getValue());
        if(taskname!=""){
            this.setNodeText(node,taskname)
            if(node.attributes.nodestate==1){
                node.ui.getTextEl().lastChild.style.textDecoration="line-through";
            }
            node.attributes.tname = taskname;
            this.updatedb_NewNode(2,node);
            Wtf.getCmp('editToDoGr'+this.id).close();
         
        }
    },
    editsinglenode1:function(a){
        var pressnode=a.id.substr(4);
        var task_name = Wtf.getCmp('tf'+pressnode);
        var task_desc = Wtf.getCmp('ta'+pressnode);
        if(task_name.isValid() && task_desc.isValid()){
            var taskname = WtfGlobal.HTMLStripper(task_name.getValue());
            if(taskname!=""){
            var assigned ="";
            var due ="";
            var dued = Wtf.getCmp('dd'+pressnode).getValue();
            if(dued){
                due =  dued.format('Y/m/d H:i:s');
            }else{
                due = "1970/01/01 00:00:00";
            }
            var desc =  WtfGlobal.HTMLStripper(task_desc.getValue());
            var node = this.getNodeById(pressnode);
            var newParentId = Wtf.getCmp('Groupcombo'+pressnode).getValue();
            var newparent = this.getNodeById(newParentId);
            var priority = Wtf.getCmp('pricombo'+pressnode).getValue();
            node.attributes.priority = priority;
            var getMember = Wtf.getCmp('combo'+pressnode).getValue();
            var oldParentId = node.parentNode.attributes.id;
            if(oldParentId!=newParentId && newParentId){
                if(newParentId=="0"){
                    this.listRoot.appendChild(node);
                } else{
                    newparent.appendChild(node);
                    this.listRoot.removeChild(node);
                    newparent.appendChild(node);
                    node.parentNode.attributes.id = newparent.attributes.id;
                    newparent.expand();
                    if(newparent.attributes.nodestate == 1){
                        node.attributes.nodestate = 1;
                    }
               }
            }
            if(getMember!="0" &&  getMember){
                 var assignedval = getMember;
                 assigned = this.memberstore.getAt(this.memberstore.find("id",assignedval)).data["name"];
                 this.setNodeText(node,taskname+"["+assigned+"]");
             }else{
                this.setNodeText(node,taskname);
             }
             var today=new Date();
             if(dued && dued.getTime()<today.getTime()){
                    node.ui.getTextEl().lastChild.style.color="red";
             }else{
                    node.ui.getTextEl().lastChild.style.color="black";
             }
             if(node.attributes.nodestate==1){
                    node.ui.getTextEl().lastChild.style.textDecoration="line-through";
             }
             node.attributes.assignedTo = assignedval;
             node.attributes.tname = taskname;
             node.attributes.duedate = due;
             node.attributes.desc = desc;
             this.updatedb1(2,node);
            }
        }
    },
    updatedb_NewNode : function(actiontype,node){
        var tid = node.attributes.id;
        var parentid = node.parentNode.attributes.id;
        if(parentid == this.listRoot.id)
        {
            parentid = "";
        }
        var torder = node.parentNode.indexOf(node);
        Wtf.Ajax.requestEx({
            method: 'POST',
            url:this.url,
            params: ({
                taskname:node.attributes.tname,
                localid:tid,
                taskorder:torder,
                status:node.attributes.nodestate,
                parentId:parentid,
                taskid:tid,
                desc:node.attributes.desc,
                priority:node.attributes.priority,
                userid:this.userid,
                grouptype:this.groupType,
                leafflag:node.isLeaf(),
                action:actiontype,
                duedate:"",
                assignedto:""
            })
         },
         this,
         function(result, req){
            var data = eval("(" + result + ")");
            if(data.data[0].remoteid!=""){
                switch(actiontype){
                    case 3:
                        var tnode = this.getNodeById(data.data[0].localid);
                        var imgsrc = node.attributes.nodestate==0?this.taskincom_imgpath:this.taskcomp_imgpath;
                        tnode.attributes.id = data.data[0].remoteid;
                        var pri_node = this.setPriorityImg(node.attributes.priority);
                        var nodetext = pri_node + "<img id = 'mark"+data.data[0].localid+"' class='cancel' src='"+imgsrc+"' onclick= \"markasComplete1('" + data.data[0].localid + "','"+ this.id + "','"+ data.data[0].remoteid + "')\"style=\"margin-left:5px;vertical-align:middle;margin-right:10px;\" title='Click to mark as complete'></img><span>"+tnode.text+"</span>";
                        tnode.setText(nodetext)
                        if(node.attributes.nodestate == 1){
                            tnode.ui.getTextEl().lastChild.style.textDecoration="line-through";
                        }
                        break;
                }
            } else {
                for(var cnt = this.listRoot.childNodes.length; cnt>0; cnt--)
                {
                    this.listRoot.removeChild(this.listRoot.childNodes[cnt-1]);
                }
                msgBoxShow(59, 1);
            }
            this.ds1.load();
        },
        function(result, req){
            msgBoxShow(4, 1);
        }
      );
    },  
    updatedb1 : function(actiontype,node){
    //TODO:update node status on db query success[shri]
    var tid = node.attributes.id;
    var parentid = node.parentNode.attributes.id;
    if(parentid == this.listRoot.id){
            parentid = "";
    }
    var torder = node.parentNode.indexOf(node);
    Wtf.Ajax.requestEx({
            method: 'POST',
            url:this.url,
            params: ({
                taskname:node.attributes.tname,
                localid:tid,
                taskorder:torder,
                status:node.attributes.nodestate,
                parentId:parentid,
                taskid:tid,
                desc:node.attributes.desc,
                userid:this.userid,
                duedate:node.attributes.duedate,
                grouptype:this.groupType,
                assignedto:node.attributes.assignedTo,
                leafflag:node.isLeaf(),
                priority:node.attributes.priority,
                action:actiontype
            })},
            this,
            function(result, req){
                var data = eval("(" + result + ")");
                if(data.data[0].remoteid!=""){
                switch(actiontype){
                    case 3:
                          var tnode = this.getNodeById(data.data[0].localid);
                          var imgsrc = node.attributes.nodestate==0?this.taskincom_imgpath:this.taskcomp_imgpath;
                          tnode.attributes.id = data.data[0].remoteid;
                          var pri_node = this.setPriorityImg(node.attributes.priority);
                          var nodetext = pri_node + "<img id = 'mark"+data.data[0].localid+"' class='cancel' src='"+imgsrc+"' onclick= \"markasComplete1('" + data.data[0].localid + "','"+ this.id + "','"+ data.data[0].remoteid + "')\"style=\"margin-left:5px;vertical-align:middle;margin-right:10px;\" title='Click to mark as complete'></img><span>"+tnode.text+"</span>";
                          tnode.setText(nodetext);
                          if(node.attributes.nodestate == 1){
                                tnode.ui.getTextEl().lastChild.style.textDecoration="line-through";
                          }
                          break;
                    }
                }
                else{
                    for(var cnt = this.listRoot.childNodes.length; cnt>0; cnt--)
                    {
                        this.listRoot.removeChild(this.listRoot.childNodes[cnt-1]);
                    }
                    msgBoxShow(59, 1);
                }
                this.ds1.reload();
            },
            function(result, req){
                msgBoxShow(4, 1);
            }
        );
    },
    makeForm :function(flag){
         this.taskNameField = new Wtf.form.TextField({
            fieldLabel : 'To Do* ',
            anchor : '99%',
            allowBlank:false,
            maxLength:100
        });
        if(flag==0)
         {
                    this.taskform = new Wtf.FormPanel({
                    labelWidth: 100,
                    labelAlign : 'left',
                    border:false,
                    autoWidth:true,
                    bodyStyle:'padding:5px 5px 0',
                    layout : 'form',
                    anchor : '90%',
                    defaultType: 'textfield',
                    buttonAlign :'right',
                    items: [this.taskNameField]
                });
                if(this.groupType == Wtf.etype.proj){
                    this.assignedGroupCombo1 = new Wtf.form.ComboBox({
                        fieldLabel :'To Do Group ',
                        store : this.groupstore,
                        anchor : '99%',
                        displayField : 'name',
                        id:'Groupcombo1',
                        valueField : 'id',
                        typeAhead:true,
                        mode: 'local',
                        emptyText: 'Please Select Group',
                        triggerAction: 'all',
                        forceSelection : true,
                        selectOnFocus:true,
                        allowBlank : true
                    });
                    this.taskform.add(this.assignedGroupCombo1);
               }
            }else{
            this.taskNameField.fieldLabel="Group Name* ";
            this.groupForm = new Wtf.FormPanel({
                labelWidth: 100,
                labelAlign : 'left',
                border:false,
                bodyStyle:'padding:5px 5px 0',
                layout : 'form',
                anchor : '100%',
                defaultType: 'textfield',
                buttonAlign :'right',
                items: [this.taskNameField]
            });
          }
          this.taskNameField.on("change", function(){
          this.taskNameField.setValue(WtfGlobal.HTMLStripper(this.taskNameField.getValue()));
           },this);
    },
    makeForm1 :function(flag,no){
        if(this.groupType == Wtf.etype.proj){
            this.taskNameField1 = new Wtf.form.TextField({
            fieldLabel : 'To Do* ',
            anchor : '99%',
            id:'tf'+no,
            allowBlank:false,
            maxLength:100,
            autoWidth:true
        });
        this.dueDate= new Wtf.form.DateField({
                        fieldLabel: 'Due Date of Task ',
                        name: 'due',
                        cls:'to-Do-duedate',
                        ctCls:'to-Do-duedateCont',
                        id:'dd'+no,
                        emptyText: 'Select Date',
                        anchor : '99%',
                        allowBlank:true,
                        readOnly:true
        });
        this.desc = new Wtf.form.TextArea({
                        fieldLabel:"Description ",
                        cls:'toDoTextarea',
                        anchor : '99%',
                        id:'ta'+no,
                        allowBlank:true,
                        maxlength:200
        });
        this.subform1 = new Wtf.form.FormPanel({
            width:380,
            baseCls: 'toDoPanel',
           items:[this.taskNameField1,this.dueDate,this.desc]
        });
        this.assignedCombo = new Wtf.form.ComboBox({
                fieldLabel :'Assigned to ',
                store : this.memberstore,
                anchor : '90%',
                cls:'todoCombo',
                ctCls:'todoComboCont',
                displayField : 'name',
                id:'combo'+no,
                valueField : 'id',
                mode: 'local',
                listWidth:175,
                typeAhead:true,
                triggerAction: 'all',
                emptyText: 'Please Select AssignTo',
                forceSelection : true,
                selectOnFocus:true,
                allowBlank : true
         });
         this.assignedGroupCombo = new Wtf.form.ComboBox({
                        fieldLabel :'To Do Group ',
                        store : this.groupstore,
                        anchor : '90%',
                        cls:'todoCombo',
                        ctCls:'todoComboCont',
                        displayField : 'name',
                        id:'Groupcombo'+no,
                        valueField : 'id',
                        mode: 'local',
                        listWidth:175,
                        emptyText: 'Please Select Group',
                        typeAhead:true,
                        triggerAction: 'all',
                        forceSelection : true,
                        selectOnFocus:true,
                        allowBlank : true
         });
         this.priorityCombo = new Wtf.form.ComboBox({
                        fieldLabel :'Priority ',
                        store : this.prioritystore,
                        anchor : '90%',
                        cls:'todoCombo',
                        ctCls:'todoComboCont',
                        displayField : 'priority',
                        id:'pricombo'+no,
                        listWidth:175,
                        mode: 'local',
                        typeAhead:true,
                        emptyText: 'Set Priority',
                        triggerAction: 'all',
                        forceSelection : true,
                        selectOnFocus:true,
                        allowBlank : true
         });
         this.subform2 = new Wtf.form.FormPanel({
            layout:'form',
            baseCls: 'toDoPanel1',
            labelWidth: 80,
            width:305,
            items:[this.assignedCombo,this.assignedGroupCombo,this.priorityCombo]
         })
         this.taskform1 = new Wtf.Panel({
                    labelAlign : 'left',
                    border:false,
                    baseCls:'todoinnerpanel',
                    frame:true,
                    bodyStyle:'padding:5px 5px 0',
                    layout : 'column',
                    anchor : '100%',
                    buttonAlign :'center',
                    items: [this.subform1,this.subform2]
         });
        }
        Wtf.getCmp('tf'+no).on("change", function(){
            Wtf.getCmp('tf'+no).setValue(WtfGlobal.HTMLStripper(Wtf.getCmp('tf'+no).getValue()));
        },this);
        Wtf.getCmp('ta'+no).on("change", function(){
            Wtf.getCmp('ta'+no).setValue(WtfGlobal.HTMLStripper(Wtf.getCmp('ta'+no).getValue()));
        },this);
     }, 
    createNode:function(){
        if(this.taskNameField.isValid()){
            var taskname = this.taskNameField.getValue();
            var dued="1970/01/01 00:00:00";
            var tempnode = new Wtf.tree.TreeNode({
                text:""+taskname,
                tname:taskname,
                iconCls:'todoNode',
                leaf:true,
                nodestate:0,
                Torder:0,
                duedate:dued,
                assignedTo:"",
                parentnode:"",
                priority:"Normal"
            });
            this.taskNameField.reset();
            this.taskNameField.focus();
            var GroupSelect = this.assignedGroupCombo1.getValue();
            if(GroupSelect && GroupSelect!="0"){
                var newparent = this.getNodeById(GroupSelect);
                newparent.appendChild(tempnode);
                if(newparent.attributes.nodestate == 1){
                    tempnode.attributes.nodestate = 1;
                }
                tempnode.parentNode.attributes.id = newparent.attributes.id;
                newparent.expand();
            }else{
                this.listRoot.appendChild(tempnode);
            }
            tempnode.attributes.tname = taskname;
            var Torder=this.listRoot.indexOf(tempnode);
            tempnode.on("checkchange",this.nodeCheckChange,this);
            tempnode.on("dblclick", this.editNode, this);
            this.updatedb_NewNode(3,tempnode);
        }
    },
    createGroup:function(){
        var groupname = this.taskNameField.getValue();
        if(this.taskNameField.isValid()){
            var tempnode = new Wtf.tree.TreeNode({
                text:groupname,
                iconCls:'groupNode',
                leaf:false,
                nodestate:0,
                Torder:0,
                priority:"Normal"
            });
            this.taskNameField.reset();
            this.taskNameField.focus();
            this.listRoot.appendChild(tempnode);
            tempnode.attributes.tname = groupname;
            var newTaskGroup = new this.groupstorerecord({
                            id:tempnode.attributes.id,
                            name:groupname
            });
            this.groupstore.add(newTaskGroup);
            var Torder=this.listRoot.indexOf(tempnode);
            tempnode.on("checkchange",this.nodeCheckChange,this);
            tempnode.on("beforeinsert",this.groupbinsert,this);
            tempnode.on("beforeappend",this.groupbinsert,this);
            this.updatedb_NewNode(3,tempnode);
        }
    },
    groupbinsert:function(tree,tnode,oldn,refnode){
        if(!oldn.isLeaf()){
            return false;
        }
    },
    addTask : function(){
            if(Wtf.get('empty')){
                Wtf.get('empty').remove();
            }
            this.makeForm(0);
            if(this.getSelectionModel().getSelectedNodes().length > 0){
                var select = this.getSelectionModel().getSelectedNodes()[0];
                if(!select.attributes.leaf && select){
                    var group_id = this.groupstore.getAt(this.groupstore.find("id",select.id));
                    var sg = group_id.data["id"];
                   this.assignedGroupCombo1.setValue(sg);
                }
            }
            this.newTaskWindow = new Wtf.Window({
                width:300,
                resizable : false,
                modal:true,
                id : 'addToDoTask'+this.id,
                iconCls : 'iconwin',
                title:'Add New To Do',
                keys:{
                    key:[10,13],
                    fn:this.createNode,
                    scope:this
                },
                buttons: [{
                    anchor : '90%',
                    id : 'save',
                    text: 'Add',
                    handler:this.createNode,
                    scope:this
                },{
                    anchor : '90%',
                    id : 'close'+this.id,
                    text: 'Close',
                    scope:this,
                    handler:function(){
                        Wtf.getCmp('addToDoTask'+this.id).close();
                        this.ds1.reload();
                    }
                }],
                items:[this.taskform]
            }).show();
    },
    addTaskGroup : function(){
            if(Wtf.get('empty')){
                Wtf.get('empty').remove();
            }
            this.makeForm(1);
            this.newTaskWindow = new Wtf.Window({
                width:300,
                iconCls : 'iconwin',
                resizable : false,
                id : 'addToDoGr'+this.id,
                modal:true,
                title:'Add New To Do Group',
                keys:{
                    key:[10,13],
                    fn:this.createGroup,
                    scope:this
                },
                buttons: [{
                    anchor : '90%',
                    id : 'save',
                    text: 'Add',
                    handler:this.createGroup,
                    scope:this
                },{
                    anchor : '90%',
                    id : 'close'+this.id,
                    text: 'Close',
                    scope:this,
                    handler:function(){
                        Wtf.getCmp('addToDoGr'+this.id).close();
                        this.ds1.reload();
                   }
                }],
                items:[this.groupForm]
            }).show();
            this.taskNameField.reset();
    },
    nodeCheckChange:function(node,chkd){
        if(chkd){
            node.ui.getTextEl().lastChild.style.textDecoration="line-through";
            node.attributes.nodestate = 1;
            if(!node.isLeaf())
                this.updatedb1(7,node);
            else
                this.updatedb1(2,node);
                node.eachChild(function(cnode){
                    cnode.ui.getTextEl().lastChild.style.textDecoration="line-through";
                    this.setImagSource(cnode.id,false);
                    cnode.attributes.nodestate = 1;
                    cnode.ui.toggleCheck(true);
                },this);
        }
        if(!chkd){
            node.attributes.nodestate = 0;
            node.ui.getTextEl().lastChild.style.textDecoration="none";
            if(!node.isLeaf())
                this.updatedb1(7,node);
            else
                this.updatedb1(2,node);
                node.eachChild(function(cnode){
                    cnode.ui.getTextEl().lastChild.style.textDecoration="none";
                    this.setImagSource(cnode.id,true);
                    cnode.attributes.nodestate = 0;
                    cnode.ui.toggleCheck(false);
                },this);
        }
        var nodes = this.getChecked();
    },
    selectionChange: function(a,nodeArray){
        if(nodeArray.length <=0){
            this.deletebutton.disable();
            this.sinNotification.disable();
        }else{
           this.deletebutton.enable();
           this.sinNotification.enable();
        }
    },
    nodeMoved : function(tree,tnode,oldp,newp,num){
      if(tnode.isLeaf()){
      var taskname = "",nodeid = "",duedate = "",priority = "",nodestate = "",parentnode = "",leaf = "",assignedTo = "",torder = "",description = "";
      var tname = this.ds1.getAt(this.ds1.find("taskid",tnode.attributes.id)).data["taskname"];
      tnode.attributes.tname = tname;
      tnode.attributes.duedate = tnode.attributes.duedate.split(".")[0];
      if(newp.attributes.nodestate){
          tnode.attributes.nodestate =1;
          tnode.ui.getTextEl().lastChild.style.textDecoration="line-through";
          this.setImagSource(tnode.id,0);
      }
      newp.eachChild(function(cnode){
            var child_id = this.ds1.getAt(this.ds1.find("taskid",cnode.attributes.id));
            var tname = child_id.data["taskname"];
            cnode.attributes.tname = tname;
            cnode.attributes.duedate = cnode.attributes.duedate.split(".")[0];
            taskname += cnode.attributes.tname + ",";
            nodeid += cnode.attributes.id + ",";
            duedate += cnode.attributes.duedate + ",";
            priority += priority = cnode.attributes.priority + ",";
            nodestate += cnode.attributes.nodestate + ",";
            parentnode += cnode.parentNode.attributes.id + ",";
            leaf += cnode.isLeaf() +",";
            torder += cnode.parentNode.indexOf(cnode) + ",";
      },this)
      Wtf.Ajax.requestEx({
            method: 'POST',
            url:this.url,
            params: ({
                taskname:taskname,
                localid:nodeid,
                task_order:torder,
                tstatus:nodestate,
                parentId:parentnode,
                taskid:nodeid,
                userid:this.userid,
                duedate:duedate,
                grouptype:this.groupType,
                leafflag:leaf,
                priority:priority,
                action:6
            })},
            this,
            function(result, req){
                this.ds1.reload();
             },
            function(result, req){
                msgBoxShow(4, 1);
            }
        );
      }
    }, 
    sendNotificationtoAll:function(){
      var todorec = this.ds1.data.items;
      var idstr = "";
      var assignidstr = "";
      var notificationflag = true;
      if(todorec.length>0){
        for(var ctr=0;ctr<todorec.length;ctr++) {
            if(todorec[ctr].data.assignedto!="" ){
                if(todorec[ctr].data.status==0){
                    idstr +="'"+ todorec[ctr].data.taskid+"',";
                    assignidstr += todorec[ctr].data.assignedto+",";
                }else{
                    notificationflag = false;
                }
            }
        }
      }
      if(idstr!=""){
          idstr = idstr.substr(0,idstr.lastIndexOf(","));
          assignidstr = assignidstr.substr(0,assignidstr.lastIndexOf(","));
          this.notify(idstr,assignidstr,notificationflag);
      }else{
          msgBoxShow(164,1);
      }
   },
   sendNotification:function(){
      var ab = this.getSelectionModel().selNodes;
      var idstr = "";
      var assignidstr = "";
      var rec;
      var notificationflag = true;
      for(var ctr=0;ctr<ab.length;ctr++) {
        rec = this.ds1.getAt(this.ds1.find("taskid",ab[ctr].attributes.id));
        if(rec.data.assignedto!="" ){
            if(rec.data.status == 0){
                idstr += "'"+rec.data.taskid+"',";
                assignidstr += rec.data.assignedto+",";
            }else{
                notificationflag = false;
            }
        }else{
            var i = 0;
            while(ab[ctr].childNodes[i]!=null){
                rec = this.ds1.getAt(this.ds1.find("taskid",ab[ctr].childNodes[i].attributes.id));
                if(rec.data.status == 0){
                    idstr += "'"+rec.data.taskid+"',";
                    assignidstr += rec.data.assignedto+",";
                }else{
                    notificationflag = false;
                }
                i++;
            }
        }
      }
      if(idstr!=""){
          idstr = idstr.substr(0,idstr.lastIndexOf(","));
          assignidstr = assignidstr.substr(0,assignidstr.lastIndexOf(","));
          this.notify(idstr,assignidstr,notificationflag);
      }else{
          msgBoxShow(164,1);
      }
   },  
   notify:function(idstr,assignidstr,notificationflag){
      Wtf.Ajax.requestEx({
        method:'POST',
        url: Wtf.req.prj + 'todolistmanager.jsp',
        params:{
            action:5,
            userid:this.userid,
            grouptype:this.groupType,
            idstr:idstr,
            assignidstr:assignidstr
        }
    }, this, function(result, req){
        if(result == "typeError"){
            msgBoxShow(183,0);
        } else{
            if(notificationflag){
                msgBoxShow(166,0);
            }else{
                msgBoxShow(165,0);
            }
        }
    });
  },

  markComplete:function(nodeid){
   var node = this.getNodeById(nodeid);
   var taskid = this.ds1.getAt(this.ds1.find("taskid",node.attributes.id));
   var tname = taskid.data["taskname"];
      if(!this.archived){
          node.attributes.tname = tname;
          var check = false;
          node.attributes.duedate = node.attributes.duedate.split(".")[0]
          check = node.attributes.nodestate==1?false:true;
          this.setImagSource(nodeid,!(check));
          this.nodeCheckChange(node,check)
      }
  },
  
  markComplete1:function(nodeid,remoteid){
      var taskid = this.ds1.getAt(this.ds1.find("taskid",remoteid))
      var tname = taskid.data["taskname"];
      if(!this.archived){
          var node = this.getNodeById(nodeid);
          node.attributes.tname = tname;
          var check = false;
          if(node.attributes.leaf){
            node.attributes.duedate = node.attributes.duedate.split(".")[0]
          }
          check = node.attributes.nodestate==1?false:true;
          this.setImagSource(nodeid,!(check));
          this.nodeCheckChange(node,check)
      }
  },
  setPriorityImg:function(priority){
      var pri_src = "";
      var title1 = ""
      if(priority=="High"){
          pri_src = this.high_task;
          title1 = this.hightitle;
      }else if(priority=="Low"){
          pri_src = this.low_task;
          title1 = this.lowtitle;
      }else{
          pri_src = this.normal_task;
          title1 = this.normaltitle;
      }
      var pri_node = "<img class='priority' src='"+pri_src+"' style=\"margin-left:10px;vertical-align:middle;margin-right:5px;\" title='"+title1+"'></img>"
      return pri_node;
  },
  setImagSource:function(nodeid,completestatus){
      var src = (completestatus)?this.taskincom_imgpath:this.taskcomp_imgpath;
      var title = (completestatus)?'Mark as complete':'Mark as incomplete';
      var getMark = Wtf.get("mark"+nodeid);
      getMark.dom.src=src;
      getMark.dom.title=title;
  },
  setNodeText:function(node,textvalue){
      var imgsrc = node.attributes.nodestate==0?this.taskincom_imgpath:this.taskcomp_imgpath;
      var title = node.attributes.nodestate==0?'Mark as complete':'Mark as incomplete';
      var pri_node = this.setPriorityImg(node.attributes.priority);
      var nodetext = pri_node + "<img id = 'mark"+node.id+"' class='cancel' src='"+imgsrc+"' onclick= \"markasComplete('" + node.id + "','"+ this.id + "')\"style=\"margin-left:5px;vertical-align:middle;margin-right:10px;\" title='"+title+"'></img><span>"+textvalue+"</span>";
      node.setText(nodetext);
  },
  importFromCSV:function(){
       this.UploadPanel1 = new Wtf.FormPanel({
            width:'100%',
            frame:true,
            method :'POST',
            scope: this,
            fileUpload : true,
            waitMsgTarget: true,
            items:[{
                bodyStyle: 'padding:5px',
                items: [{
                    layout: 'form',
                    items:[{
                        xtype : 'textfield',
                        id:'browseBttn',
                        inputType:'file',
                        labelStyle: "width:80px;",
                        fieldLabel:'File name ',
                        name: 'test'
                    }]
                }]
            },{
                layout: 'column',
                id: 'importRadio',
                labelWidth: 110,
                cls: 'radContainer',
                items: [{
                    columnWidth: 0.5,
                    layout: 'fit',
                    height : 20,
                    items: [{
                        xtype : 'radio',
                        boxLabel:'Append',
                        height : 20,
                        checked: true,
                        id:'ap',
                        name:'nam'
                    }]
                },{
                    columnWidth: 0.5,
                    layout: 'fit',
                    height : 20,
                    items: [{
                        xtype : 'radio',
                        boxLabel:'Overwrite',
                        height : 25,
                        id:'ov',
                        name:'nam'
                    }]
                }]
            }
        ]},
        this);
        this.upWin1 = new Wtf.Window({
            resizable: false,
            scope: this,
            layout: 'fit',
            modal:true,
            width: 420,
            height: 200,
            iconCls: 'iconwin',
            id: 'uploadwindow',
            title: 'Import To Do Task',
            items: this.UploadPanel1,
            buttons: [{
                text: 'Import',
                id: 'submitPicBttn',
                type: 'submit',
                scope: this,
                handler: function(){
                    this.importCSVFile();
                }
            },{
                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                id:'canbttn1',
                scope:this,
                handler:function() {
                    this.upWin1.close();
                }
            }]
        },this);
        this.upWin1.show();
  },
  importCSVFile :function(){
        var parsedObject = document.getElementById('browseBttn').value;

        var extension =parsedObject.substr(parsedObject.lastIndexOf(".")+1);
        var patt = new RegExp("csv","i");
        var userchoice = Wtf.getCmp("ov").getValue() == true ? 1 : 0;
        if(userchoice == 1){
            this.append = false;
        }
        this.groupstore.removeAll();
        if(patt.test(extension)) {
            this.UploadPanel1.form.submit({
                url:'../../importToDoTask?&appendchoice='+userchoice+'&projectid='+this.userid+'&action=1',
                waitMsg :'importing...',
                scope:this,
                success: function (result, request) {
                    this.upWin1.close();
                    var obj = eval('('+request.response.responseText+')');
                    this.showHeaderMappingWindow(obj, userchoice);
                },
                failure: function ( result, request) {
                    this.upWin1.close();
                    var obj = eval('('+request.response.responseText+')');
                    msgBoxShow([0,"Error during uploading CSV file"], 1);
                }
            },this);
            this.upWin1.buttons[0].disable();
            this.upWin1.buttons[1].disable();
        } else
            msgBoxShow(56, 1);
  },
  
  showHeaderMappingWindow :function(res, userchoice){
        var headerlist = [
            [ 0 , 'Task Name' ],
            [ 1 , 'Due Date' ],
            [ 2 , 'Description' ],
            [ 3 , 'Priority' ],
            [ 4 , 'Status' ],
            [ 5 , '-' ]  //for extra any unmapped column
            ];
        var headerds = new Wtf.data.SimpleStore({
            fields: [
                { name:"headerindex" },
                { name:"headername" }
            ]
        });
        Wtf.ux.comboBoxRenderer = function(combo) {
            return function(value) {
                var idx = combo.store.find(combo.valueField, value);
                if(idx == -1)
                    return "-";//false;//"";
                var rec = combo.store.getAt(idx);
                return rec.get(combo.displayField);
            };
        };
        headerds.loadData(headerlist);
        var headerCombo = new Wtf.form.ComboBox({
            store: headerds,
            displayField: 'headername',
            emptyText: "<Select a column>",
            valueField: 'headerindex',
            mode: 'local',
            forceSelection: true,
            editable: true,
            typeAhead:true,
            triggerAction: 'all',
            selectOnFocus: true
        });
        var listds = new Wtf.data.JsonStore({
            fields: [{
                name:"header"
            },{
                name:"index"
            }]
        });
        listds.loadData(res.Headers);
        var listcm = new Wtf.grid.ColumnModel([{
            header: "Imported To Do Task Attributes",
            dataIndex: 'header'
        },{
            header: "Existing Attributes",
            dataIndex: 'index',
            editor: headerCombo,
            renderer : Wtf.ux.comboBoxRenderer(headerCombo)
        }]);
        var haderMapgrid= new Wtf.grid.EditorGridPanel({
            region:'center',
            id:'headerlist' + this.id,
            clicksToEdit : 1,
            store: listds,
            cm: listcm,
            border : false,
            width: 434,
            loadMask : true,
            viewConfig: {
                forceFit:true
            }
        });
        this.headerMapWin = new Wtf.Window({
            resizable: false,
            scope: this,
            layout: 'border',
            modal:true,
            width: 400,
            height: 415,
            iconCls: 'iconwin',
            id: 'importcsvwindow',
            title: 'Map Task Attributes',
            items:[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html : getTopHtml("Map To Do Task Attributes","Map To do task attributes with already present attributes","../../images/exportcsv40_52.gif")
            }, haderMapgrid],
            buttons: [{
                text: 'Import',
                type: 'submit',
                scope: this,
                handler: function(){
                    var mappedHeaders = '';
                    var headerArray = new Array();
                    var comboCount = headerds.getCount()-1; //mapping-combo records count escape last record for unmapped column
                    for(j=0;j<listds.getCount();j++)
                        headerArray[j] = 0;
                    for(var j=0;j<listds.getCount();j++){
                        var index = listds.getAt(j).get("index");
                        if(index < comboCount){ //consider only mapping-combo records, skip other
                            headerArray[index]=headerArray[index]+1;
                            var rec = headerCombo.store.getAt(index);
                            if(rec != undefined ) 
                                mappedHeaders += "\""+rec.get(headerCombo.displayField)+"\":"+j+",";
                        }
                    }
                    mappedHeaders = mappedHeaders.substr(0, mappedHeaders.length-1);
                    mappedHeaders = "{"+mappedHeaders+"}";
                    var mismatch = 0;
                    for(j=0;j<comboCount;j++){  //mapping-combo record count
                        if(headerArray[j]>1){   //for one to one mapping use " != 1"
                            mismatch = 1;
                            break;
                        }
                    }
                    if(mismatch == 1){
                        msgBoxShow(["Error", "Please Check Headers Mappings."], 1);
                        return;
                    }
                    if(headerArray[0]==0){ //headerArray[1] -> start date mapping compulsory
                        msgBoxShow(["Error", "Please Map taskname"], 1);
                        return;
                    }
                    Wtf.Ajax.request({
                        method: 'POST',
                        url: '../../importToDoTask',
                        params: ({
                            mappedheader : mappedHeaders,
                            append : userchoice,
                            projectid : this.userid,
                            filename : res.FileName,
                            action : 2
                        }),
                        scope: this,
                        success: function(result, request){
                            this.headerMapWin.close();
                            this.loadflag = true;
                            if(Wtf.get('empty')){
                                Wtf.get('empty').remove();
                            }
                            this.ds1.load();
                            var obj = eval('('+result.responseText+')');
                            if(obj.success)
                                msgBoxShow(["Success", obj.msg], 0);
                            else
                                msgBoxShow([0,"Can not upload CSV file."], 1);
                        },
                        failure: function(result, req){
                            this.headerMapWin.close();
                            msgBoxShow(["Error", "Can not upload CSV file."], 1);
                        }
                    });
                }
            },{
                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                scope:this,
                handler:function() {
                    this.headerMapWin.close();
                }
            }]
        }),
        this.headerMapWin.show();
                }
                });

function markasComplete(nodeid,objid){
  if(!Wtf.get('newtodo'+nodeid)){
    Wtf.getCmp(objid).markComplete(nodeid);
  }
}

function markasComplete1(nodeid,objid,remoteid){
    if(!Wtf.get('newtodo'+nodeid)){
     Wtf.getCmp(objid).markComplete1(nodeid,remoteid);
    }
}
function getTodo(id){
    //Wtf.getCmp(id).removeEmptyText();
    //Wtf.DomHelper.append(Wtf.getCmp(id).body.dom, '');
    
    Wtf.getCmp(id).addTask();
}

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
Wtf.docs.com.Tree = function(config){

    Wtf.apply(this, config);
    Wtf.docs.com.Tree.superclass.constructor.call(this, {
        baseCls: 'docscom',
        width: '100%',
        minSize: 175,
        maxSize: 400,
        baseCls: 'docscom',
        collapsible: true,
        margins: '0 0 5 5',
        cmargins: '0 5 5 5',
        rootVisible: true,
        lines: true,
        root :  new Wtf.tree.TreeNode({
            id: 'root',
            text: this.rootText,
            expanded: true,
            iconCls: 'back_image_open'
        }),
        autoScroll: true,
        collapseFirst: false,
        baseCls: 'treebac',
        border: true
    });
    
    this.ge = new Wtf.tree.TreeEditor(this, {
        allowBlank: false,
        container: this.getEl,
        completeOnEnter: true,
        ignoreNoChange: true,
        cancelOnEsc: true,
        updateEl: true,
        blankText: 'A name is required',
        selectOnFocus: true
    }, this);
    
    this.ge.on('beforestartedit', this.beforeStartEdit, this);
    this.ge.on('startedit', this.startEdit, this);
    this.ge.on('beforecomplete', this.beforeComplete, this);
    this.ge.on('complete', this.onComplete, this);
    this.on('contextmenu', this.contextMenu, this);
    this.on('expandnode', function(node, e){
        node.ui.getIconEl().className = "x-tree-node-icon back_image_open";
    }, this);
    
    this.on('collapsenode', function(node, e){
        node.ui.getIconEl().className = "x-tree-node-icon back_image_close";
    }, this);
    this.addEvents = ({
        'onDeleteComplete': true
    });
};

Wtf.extend(Wtf.docs.com.Tree, Wtf.tree.TreePanel, {
    ge: null,
    initComponent: function(config){
        Wtf.docs.com.Tree.superclass.initComponent.call(this, config);
        this.addEvents({
            'deleteNode': true
        });
    },
    
    setRootNode1: function(nodeText){
        var node = new Wtf.tree.TreeNode({
            id: 'root',
            text: nodeText,
            expanded: true,
            iconCls: 'back_image_open'
        });
        return node;
    },
    
    contextMenu: function(node, e){
        menu = new Wtf.menu.Menu({
            id: 'treeMenu',
            items: [{
                text: 'Rename',
                id: 'name',
                iconCls: 'pwnd renameicon',
                scope:this,
                handler: renameNode
            }, new Wtf.Action({
                text: "Delete",
                iconCls: 'pwnd delicon',
                scope:this,
                handler: deleteNode
            })]
        });
        var abcd = this.grid;
        var user = this.userid;
        var pcid = this.pcid;
        var groupid = this.groupid;
        if(node!=this.root)
            if((node.getDepth() == 1 && this.defaultTag.indexOf(node.text.toLowerCase().split(' (')[0]) == -1)||node.getDepth() != 1)
                if(node.getDepth() > 1){
                     var depth = node.getDepth();
                     var tempNode = node;
                     do{
                        tempNode = tempNode.parentNode;     
                    }while(tempNode.getDepth()!=1);
                    if(this.defaultTag.indexOf(tempNode.text.match('([\\w]+[\\s]*)*')[0].trim().toLowerCase())== -1){
                         menu.showAt(e.getXY());
                    }
                }else{
                     menu.showAt(e.getXY());
                }
        Wtf.destroy('treeMenu');
        
        function renameNode(){
        
            this.getSelectionModel().select(node);
            //this.fireEvent('click', node);
            this.ge.triggerEdit(this.getSelectionModel().getSelectedNode());
        }
        
        function deleteNode(){
                      
            var root = node.getOwnerTree().root;
            var parentnode = node;
            var str = '';
            var nodeText = '';
            var nodocs = node.text.match('\\s[(](\\d+)[)]');
            if (node.getDepth() == 0) {
                nodeText = parentnode.text;
            }
            else {
                for (var i = (node.getDepth()); i > 0; i--) {
                    nodeText += parentnode.text.split(' (')[0] + '/';
                    parentnode = parentnode.parentNode;
                }
                nodeText = nodeText.substr(0, nodeText.length - 1);
                str = nodeText.split('/');
                nodeText = '';
                for (var j = (str.length - 1); j >= 0; j--) {
                    nodeText += str[j] + '/';
                }
                nodeText = nodeText.substr(0, nodeText.length - 1);
                Wtf.destroy('treeMenu');
            }
      
            Wtf.destroy('treeMenu');
            for (var i = 0; i < abcd.getStore().getCount(); i++) {
                var a = abcd.getStore().getAt(i);
                var b = this.delTag(abcd.getStore().getAt(i).data['Tags'], nodeText);
                a.set('Tags', b);
                
            }
           
            
            var tag = nodeText;
            tag = tag.trim();
            if (tag.length == 0) 
                tag = "Uncategorized";
            
            if(nodocs==null){
                this.breakTree(tag,this.root);
            }else{
                for(var k = 0; k< nodocs[1] ; k++)
                    this.breakTree(tag,this.root)
            }    
            /*var arrString = tag.split("/");
            var flag = 1;
            var temp = 0;
            var arrtemp = [];
            for (var k = 0; k < arrString.length; k++) {
                flag = 1;
                for (var i = 0; i < root.childNodes.length; i++) {
                    //var string = root.childNodes[i].text.split("-");
                    var string = root.childNodes[i].text.split(" (");
                    if (string[0] == arrString[k]) {
                        root = root.childNodes[i];
                        flag = 0;
                        var cnt = k + 1;
                        if (cnt == arrString.length) {
                            if (root.getOwnerTree() != null) 
                                root.remove();
                        }
                    }
                }
            }*/
            
            this.fireEvent('deleteNode', nodeText);
            Wtf.destroy('treeMenu');
            Wtf.Ajax.requestEx({
                url: Wtf.req.doc + "tree/deleteTag.jsp",
                params: {
                    tagname: nodeText,
                    groupid: groupid,
                    pcid:pcid
                }},
                this,
                function(result, req){
                   if(eval('('+result+')')["res"]==1)
                    Wtf.MessageBox.show({
                            title: 'ERROR',
                            msg: 'A problem occurred while deleting tags',
                            buttons: Wtf.MessageBox.OK,
                            animEl: 'ok',
                            icon: Wtf.MessageBox.ERROR
                    });
                }
            );
            
            
        }
    },
     delTag:function(str, sub){
                var str1 = str.split(",");
                var res = "";
                for (var i = 0; i < str1.length; i++) {
                    if (str1[i] != sub) {
                        if (i == str1.length - 1) 
                            res += str1[i] + "";
                        else 
                            res += str1[i] + ",";
                    }
                }
                
                var res2 = "";
                var res1 = res.split(",");
                for (i = 0; i < res1.length; i++) {
                
                   if (res1[i].substring(0, sub.length + 1) != sub + "/") {
                            if (i == res1.length - 1) 
                            res2 += res1[i] + "";
                        else 
                            res2 += res1[i] + ",";
                    }
                }
                if(res2=="" && sub!="" ){
                    res2 = 'Uncategorized';
                    this.makeTree(res2,this.root);
                }
                var len = res2.length - 1;
                if (res2[len] == ",") 
                    return res2.substring(0, len);
                else 
                    return res2;
            },
    beforeStartEdit: function(ge, boundel, value){
        if (value == this.root.text) {
            return false;
        }else if(this.getSelectionModel().getSelectedNode().getDepth() == 1 && this.defaultTag.indexOf(value.match('([\\w]+[\\s]*)*')[0].trim().toLowerCase())!= -1){
            return false;
        }else if(this.getSelectionModel().getSelectedNode().getDepth() > 1){
            var depth = this.getSelectionModel().getSelectedNode().getDepth();
            var tempNode = this.getSelectionModel().getSelectedNode();
            do{
                tempNode = tempNode.parentNode;     
            }while(tempNode.getDepth()!=1);
           
            if(this.defaultTag.indexOf(tempNode.text.match('([\\w]+[\\s]*)*')[0].trim().toLowerCase())!= -1){
                return false;
            }

            
        }    
    },
    
    startEdit: function(el, value){
    
        this.ge.setValue(value.replace(/\s[(]\d+[)]/g, ""));
    },
    
    beforeComplete: function(ge, value, startValue){
        if (value != startValue) {
          //  var str = '^([\'"]?)\\s*([^(,\'"\\\/\$@!#%^&*()+=)]+[(/|\\\{1})]?)*[^/|\\\,\'"\\\/\$@!#%&*()+=]\\1$';
            //var str = '^([\'"]?)\\s*([\\w]+[(/|\\\{1})]?)*[\\w]\\1$';
            var str = "^[\\w+\\s*]*$";
            var flag = 0;
            value = value.trim();
            
            var matchValue = value.match(str);
            if (matchValue != null  && this.defaultTag.indexOf(value.toLowerCase())== -1) {
                    value = matchValue[0];
                    value = value.replace(/\/+/g, "/");

                    var depth = this.getSelectionModel().getSelectedNode().getDepth();

                    var node = this.getSelectionModel().getSelectedNode();
                    path = '';
                    path += this.getSelectionModel().getSelectedNode().text.replace(/\s[(]\d+[)]/g, "") + '/';
                   // path += this.getSelectionModel().getSelectedNode().text.match('([\\w]+[\\s]*)*')[0].trim() + '/';
                    while (depth > 1) {
                        node = node.parentNode;

                        path += node.text.split(' (')[0] + '/';

                        depth--;
                    }
                    path = path.substr(0, path.length - 1);
                    var newpath = path.split('/');
                    path = '';
                    for (var i = (newpath.length - 1); i >= 0; i--) {
                        path += newpath[i] + '/';
                    }
                    path = path.substr(0, path.length - 1);

                    depth = this.getSelectionModel().getSelectedNode().getDepth();
                    if (depth > 1) {
                        var finalnewpath = path.substr(0, path.lastIndexOf('/')) + '/' + value;
                        var oldpath = path.substr(0, path.lastIndexOf('/')) + '/' + startValue;
                    }
                    else {
                        var finalnewpath = value;
                        var oldpath =  startValue;
                    }


                    Wtf.Ajax.requestEx({
                        url: Wtf.req.doc + "tree/editTagTree.jsp",
                        params: {
                            oldTabName: oldpath.split(' (')[0],
                            newTabName: finalnewpath,
                            groupid: this.groupid,
                            pcid: this.pcid    
                        }},
                        this,
                        function(result, req){
                             if(eval('('+result+')')["res"]==0){
                               for (var i = 0; i < this.grid.getStore().getCount(); i++) {
                                    var a = this.grid.getStore().getAt(i);
                                    var b = this.editTreeNode(this.grid.getStore().getAt(i).data['Tags'], oldpath.split(' (')[0], finalnewpath);
                                    a.set('Tags', b);
                                }
                             }
                             else{
                                    Wtf.MessageBox.show({
                                        title: 'Error',
                                        msg: 'A problem occurred while loading tags',
                                        buttons: Wtf.MessageBox.OK,
                                        animEl: 'ok',
                                        icon: Wtf.MessageBox.ERROR
                                    });

                             }
                               
                        }
                      
                    );
                }
                else {
                    this.ge.setValue(startValue.match('([\\w]+[\\s]*)*')[0].trim());
                    return false;
                }
                       
            //value += startValue.match('([\\w]+[\\s]*)*')[0].trim();
            value +=  startValue.replace(/\s[(]\d+[)]/g, "");
        }
        
    },
    mergeNode : function(parentNode , childNode, value) {
        var flag =1;
        var childsArray = new Array();
        var nodeText2;
        var no=0;
        for(var i=0; i < parentNode.childNodes.length; i++){
            //if(parentNode.childNodes[i].text.match('([\\w]+[\\s]*)*')[0].trim().toLowerCase() == value.toLowerCase()){
            if(value.match('^'+parentNode.childNodes[i].text.match('([\\w]+[\\s]*)*')[0].trim()+'$','i')!=null){    
                this.fireEvent('onDeleteComplete');
               /* flag = 0;
                for(var j = 0; j< childNode.childNodes.length ; j++){
                     if(!this.mergeNode(parentNode.childNodes[i],childNode.childNodes[j],childNode.childNodes[j].text))
                        parentNode.childNodes[i].appendChild(childNode.childNodes[j]);
                }

                if( childNode.text.split(" (").length == 2){
                     var nodeText1 = childNode.text.split(" (")[1];
                     no = parseInt(nodeText1.substr(0, nodeText1.length - 1));
                }     

                if(parentNode.childNodes[i].text.split(' (').length == 2){
                    nodeText2 = parentNode.childNodes[i].text.split(" (")[1];
                    var no2 = parseInt(nodeText2.substr(0, nodeText2.length - 1));
                    if(no==0)
                        no2 = no2+1
                    else
                        no2 = no+no2;    
                    nodeText2 = parentNode.childNodes[i].text.split(" (")[0] +" (" + no2.toString() + ")";
                 }else{
                    if(no==0)
                        no = no+2;
                    else
                        no = no+1;
                    nodeText2 = parentNode.childNodes[i].text+" (" + no.toString() + ")";
                 }
                 parentNode.childNodes[i].setText(nodeText2);
                 childNode.remove();*/
                 return true;
             }
         }
        return false; 
    }, 
    
    onComplete: function(ge, value, startValue){
        if (value.replace(/\s[(]\d+[)]/g, "") != startValue.replace(/\s[(]\d+[)]/g, "")) {
            var childsArray = new Array();
            var nodeText2;
            var no=0;
         
              if(this.mergeNode(this.getSelectionModel().getSelectedNode().parentNode,this.getSelectionModel().getSelectedNode(),value))
                    return false;
              

  
                if(startValue.match('\\s[(]\\d+[)]') != null){
                this.getSelectionModel().getSelectedNode().setText(value +  startValue.match('\\s[(]\\d+[)]'));
                return false;
               }
        
        }
        if(value.match('^'+startValue.match('([\\w]+[\\s]*)*')[0].trim()+'$','i')!=null){
            this.getSelectionModel().getSelectedNode().setText(startValue);
            return false;
        }
        
    },
    
    editTreeNode : function(oldArr,oldTagName,newTagName){
        var srtarr = oldArr.split(',');
        var newarr = "";
        for(var i=0; i< srtarr.length; i++){
            if(srtarr[i] == oldTagName){
               srtarr[i] =  newTagName;
            }else if(srtarr[i].substr(0,oldTagName.length).toLowerCase()==oldTagName.toLowerCase()) {
               srtarr[i] = newTagName +srtarr[i].substr(oldTagName.length);
            }
            newarr += srtarr[i]+',';
        }
        newarr = newarr.substr(0,newarr.length-1);
        return newarr;  
    },
    tagClean:function(tagName){
       var tagStrOut = tagName.replace(/\s*\/\s*/g, "/");
       tagStrOut = tagStrOut.replace(/^['"]\s+|\s+['"]$/g, "'");
       tagStrOut = tagStrOut.replace(/\s+/g, " ");
       return tagStrOut.trim();
    },
    clearTree: function(root){
        var child = root.childNodes;
        while(child.length > 0){
            child[0].remove();
        }
    },
    makeTree: function(tag, root){
        if(root.childNodes!=null)
        {
            tag = this.tagClean(tag);
            tag = tag.replace(/'|"/g,"");
            var arrString = tag.split("/");
            var flag = 1;
            for (var k = 0; k < arrString.length; k++) {
                if (arrString != "") {
                    var node = new Wtf.tree.TreeNode({
                        text: arrString[k],
                        lines: true,
                        iconCls: 'back_image_close'
                    });
                    flag = 1;
                    for (var i = 0; i < root.childNodes.length; i++) {
                        var string = root.childNodes[i].text.split(" (");
                        if (string[0].toLowerCase() == arrString[k].toLowerCase()) {
                            root = root.childNodes[i];
                            flag = 0;
                            var cnt = k + 1;


                            if (root.text.split(" (").length == 1) {

                                var nodeText = arrString[k] + " " + "(2)";
                                root.setText(nodeText);
                            }
                            else {

                                var nodeText1 = root.text.split(" (")[1];
                                var no = parseInt(nodeText1.substr(0, nodeText1.length - 1));
                                no = no + 1;
                                nodeText1 = root.text.split(" (")[0] + " (" + no.toString() + ")";
                                root.setText(nodeText1);

                            }
                            break;
                        }
                    }
                    if (flag == 1) {
                        root.appendChild(node);
                        root = node;
                    }
                }
            }
        }
    },
    
    breakTree: function(tag, root){
        if(root.childNodes!=null)
        {
        tag = this.tagClean(tag);
        tag = tag.replace(/'|"/g,"");
        tag = tag.trim();
        if (tag.length == 0) 
            tag = "Uncategorized";
        
        var arrString = tag.split("/");
        var flag = 1;
        var temp = 0;
        var arrtemp = [];
        
        for (var k = 0; k < arrString.length; k++) {
            flag = 1;
            for (var i = 0; i < root.childNodes.length; i++) {
                var string = root.childNodes[i].text.split(" (");
                if (string[0].toLowerCase() == arrString[k].toLowerCase()) {
                    root = root.childNodes[i];
                    flag = 0;
                    var cnt = k + 1;
                    
                    if (root.text.split(" (").length == 1) {
                        if (root.getOwnerTree() != null) 
                            root.remove();
                        
                    }
                    else {
                        var nodeText1 = root.text.split(" (")[1];
                        
                        var no = parseInt(nodeText1.substr(0, nodeText1.length - 1));
                        no = no - 1;
                        if (no > 1) {
                        
                            nodeText1 = root.text.split(" (")[0] + " (" + no.toString() + ")";
                            root.setText(nodeText1);
                        }
                        else 
                            if (no == 1) {
                                var nodeText = root.text.split(" (")[0];
                                root.setText(nodeText);
                            }
                    }
                    
                
                }
            }
        }
        }
    },
    
    breakTree2: function(tag, root){
        tag = tag.trim();
        if (tag.length == 0) 
            tag = "Uncategorized";
        
        var arrString = tag.split("/");
        var flag = 1;
        var temp = 0;
        var arrtemp = [];
        for (var k = 0; k < arrString.length; k++) {
            flag = 1;
            for (var i = 0; i < root.childNodes.length; i++) {
               
                var string = root.childNodes[i].text.split(" (");
                if (string[0].toLowerCase() == arrString[k].toLowerCase()) {
                    root = root.childNodes[i];
                    flag = 0;
                    var cnt = k + 1;
                    if (cnt == arrString.length) {
                        if (root.getOwnerTree() != null) 
                            root.remove();
                    }
                }
            }
        }
    }
});



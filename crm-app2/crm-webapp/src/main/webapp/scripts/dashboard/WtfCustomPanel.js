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


Wtf.mbuild.WtfCustomPanel = function(config){
    Wtf.mbuild.WtfCustomPanel.superclass.constructor.call(this, config);
    this.oldinnerHtml  ="";
    this.o1  ="";
    this.o2  ="";
    this.o3  ="";
    this.cmbcnt=1;
    this.config1.emptyText='No result found';
    this.config1.tableClassName='datagridonDB';
    this.config1.tableHeader='';
}
Wtf.extend(Wtf.mbuild.WtfCustomPanel, Wtf.Panel,{
    onRender: function(config){
       Wtf.mbuild.WtfCustomPanel.superclass.onRender.call(this, config);

       for(var count = 0;count<this.config1.length;count++){
               this.count = count;
               this.newObj = this.config1[count];
               this.callRequest();
       }
    },

    writeTemplateToBody:function(innerHTML,lib,ss,pgsize){
       var temp1 = innerHTML ;
       var temp3 ='';
    
       if(this.config1.length == 4 ) { // for Knowledge Compass, template changes
           
           temp3 = this.o1 +this.o2 + this.o3 + temp1;
           temp3 = ''
                            +'<div style="background-color:#DFE8F6;padding:7px;float:left;width:99%">'
                            + this.o1 +this.o2 + this.o3 +"<div class='search_div' style='margin-right:43px !important;margin-top:-43px !important;padding:2px !important;' onclick='showResults()'>Search</div>"+temp1
                            +'</div>';
            if(!this.o1) {    // saves the previous templates of combos
               this.o1=lib;
            }else if(!this.o2){
               this.o2=lib;
            } else if(!this.o3){
              this.o3=lib;
            }
       } else
       if(this.config1.length >1)
           {
               var temp2 = this.oldinnerHtml;
               temp3 = '<div style="background-color:#DFE8F6;padding:7px;float:left;width:97%">'
                            +((this.newObj.isSearch)?this.addSearchBar1():"")
                            +'<div style="background-color:#ffffff;padding:1%;float:left;width:95.5%">'
                            + temp2 + lib
                            +((this.newObj.isPaging)?this.paging(pgsize):"")
                            +'</div>'
                            +'</div>';
       }else{
           temp3 = temp1;
       }
       var temp=new Wtf.Template(temp3);
       this.oldinnerHtml += lib;
       temp.overwrite(this.body);
    },

    addSearchBar1:function(searchss){
        var valueStr="";
        if(searchss){
            valueStr = "value = "+searchss;
        }
        var a1 =this.id;
        return('<div style="height:24px;width:97.5%;background-image:url(images/search-field-bg.gif);margin-bottom:6px;">'
           +'<div id="searchdiv\"'+a1+'\" class="search_div" onclick="btnpressed(\''+a1+'\')">Search</div>'
           +'<div class="searchspacer">&nbsp;</div>'
           +'<div style="width: 85%;overflow:hidden;">'
           +'<input '+valueStr+' onkeypress=\"javascript:if(event.keyCode==13)btnpressed(\''+a1+'\');\" style="background-color: transparent;float:left;border:none; padding-top:3px; height:21px; border-left:solid 1px #a0bcda;width:100%;" type="text" id="search'+a1+'" />'
           +'</div>'
           +'</div>');
    },

    paging:function(numpages,searchss){
            var a =this.id;
            if(!searchss)
                {
                 searchss="";
                }
            var pager = '<div style="float:left;cursor:pointer">';
            var activepage='<div id="'+a+'0" class="pagination-div active-pagination" onclick="pagingRedirect(\''+a+'\',0,'+this.count+',\''+searchss+'\','+this.panelcount+');">1</div>';

            pager+=activepage;
            for(var i=1;i<numpages;i++){
                pager+= '<div id="'+a+i+'" class="pagination-div deactive-pagination" onclick="pagingRedirect(\''+a+'\','+i+','+this.count+',\''+searchss+'\','+this.panelcount+');">'+(i+1)+'</div>';
            }
//            if(this.newObj.WorkspaceLinks != null)
//                {
//                  pager=pager+'</div></div>'+this.newObj.WorkspaceLinks;
                  pager=pager+'</div>';
//                }
            return(pager);
    },

    callRequest:function(url,searchss,pager){
        if(!this.newObj.autoLoad){
            
            this.panelcount = (this.newObj.numRecs)?this.newObj.numRecs:this.panelcount;
            var headerHtml = this.newObj.headerHtml;
            var mytestpl=this.newObj.template;
            var xtooltip = this.newObj.xtooltip;
            var formatField = this.newObj.formatField;
            var prefixImage = this.newObj.prefixImage;
            var imageField = this.newObj.imageField;
            var tpl_tool_tip = "";
            var autoHide = "";
            var closable = "";
            var height = "";
            var emptyText = "";
            var shwCombo=this.newObj.isCombo;
            if(this.newObj.emptyText != null){
                emptyText = this.emptyText;
            }

            if(this.newObj.tool_tip != null){
                tpl_tool_tip = this.newObj.tool_tip.tpl_tool_tip;
                autoHide = this.newObj.tool_tip.autoHide;
                closable = this.newObj.tool_tip.closable;
                height = this.newObj.tool_tip.height;
            }

             Wtf.Ajax.requestEx({
                 url: (url)?url:this.newObj.url+"?limit="+this.panelcount+"&start=0&searchString=",
                params: this.newObj.paramsObj
             },
             this,
             function(result, req) {
                    var innerHTML="";
                    var obj = result; //eval('('+result.responseText+')');
                    
                    if(xtooltip && formatField){
                        this.newObj.formatField = formatField;
                        this.formatSpecifiedField(obj);
                    }
                    if(prefixImage && imageField){
                        this.newObj.imageField = imageField;
                        this.formatFileName(obj);
                    }
                    var lib=this.getDataString(obj,mytestpl,headerHtml,tpl_tool_tip,autoHide,closable,height,emptyText,shwCombo);
                    innerHTML=this.getPagingString(lib,obj,searchss);
                    var pgsize=Math.ceil(obj.count/this.panelcount);
                    this.writeTemplateToBody(innerHTML,lib,searchss,pgsize);
                    mytestpl = "";
                        this.togglePageCss(this.id,pager);
                    if(url && this.isSearch){
                        document.getElementById("search"+this.id).value=searchss;
                    }
                },
                function(result, req){
                    alert(result);
                    mytestpl = "";
                }
             );
        }
    },

    getDataString:function(obj, tpl, headerHtml,tpl_tool_tip,autoHide,closable,height,emptyText,shwCombo) {
        var lib= "";
        
        if(this.newObj.isTable == true)
            {

                lib = lib +"<table class='"+this.newObj.tableClassName+"' border='0' cellspacing=0 width='100%' style='float:left;margin:0px;'>";
                if(this.newObj.tableHeader != null)
                    {

                        lib = lib +this.newObj.tableHeader;
                    }
            }else{
                lib = lib +"<div class='content-wrapper'>";
            }
                lib = lib +headerHtml;

            if(shwCombo) {
                    lib = lib  +'<select id="cmb'+this.cmbcnt+'" style="width:200px;margin-right:3px;margin-left:5px;"><option  value="">Select Value --<option>';
                    this.cmbcnt++;
            }
            
            for(var i=0;i<obj.data.length;i++){
                if(this.newObj.isToolTip == true){
                    var target = "KCUser"+obj.data[i].userid;
                    createtooltip1(target,tpl_tool_tip,autoHide,closable,height);
                }
                if(obj.data.length==0)
                    {
                        alert(1);
                        lib = emptyText;
                    }
                if(this.pagingflag){
                    if(obj.count == -1){
                        this.config1[0].isPaging = false;
                        this.config1[0].WorkspaceLinks = '';
                        if(this.timeid){
                            clearTimeout(this.timeid);
                        }
                    }
                    else{
                        this.config1[0].isPaging = true;
                        this.config1[0].WorkspaceLinks = signoutLinks;
                        if(this.timeid){
                            clearTimeout(this.timeid);
                        }
                        var  time=parseInt(Wtf.GS.td,10);
                        var  timedelay = time * 60 * 1000;
                        this.timeid = this.doSearch.defer(timedelay,this,[this.url,'']);
                    }
                }
                lib = lib  + tpl.applyTemplate(obj.data[i])
            }
            
            if(shwCombo)
                    lib = lib  +'</select>';

            if(this.newObj.isTable == true)
                {
                        lib = lib +"</table><br style='clear:both'/>";
                }else{
                        lib = lib +"</div><br style='clear:both'/>";
                }
            return lib;
    },

getPagingString:function(lib,obj,searchss) {
        this.panelcount = (this.newObj.numRecs)?this.newObj.numRecs:this.panelcount;
        
        var tmpHTML = "";
        if(obj.count!=0){

         var links = " ";
        if(this.newObj.WorkspaceLinks != null){
            links = this.newObj.WorkspaceLinks;
        }
            var pgsize=Math.ceil(obj.count/this.panelcount);
            tmpHTML = '<div class="portlet-body">'
                            +'<div style="background-color:#DFE8F6;padding:7px;float:left;width:97%">'
                            +((this.newObj.isSearch)?this.addSearchBar1(searchss):"")
                            +'<div style="background-color:#ffffff;padding:1%;float:left;width:95.5%">'
                            +lib
                            +((this.newObj.isPaging)?this.paging(pgsize,searchss):"")
                            +'</div>'+links
                            +'</div>'
                        +'</div>';
        }else{
            if(this.newObj.emptyText!=null)
                {
                    links = " ";
                    if(this.newObj.WorkspaceLinks != null){
                        links = this.newObj.WorkspaceLinks;
                    }
                    tmpHTML = '<div class="portlet-body">'
                            +'<div style="background-color:#DFE8F6;padding:7px;float:left;width:97%">'
                            +((this.newObj.isSearch)?this.addSearchBar1():"")
                            +'<div style="background-color:#ffffff;padding:1%;float:left;width:95.5%">'
                            +this.newObj.emptyText
                            +'</div>'+ links
                            +'</div>'
                            +'</div>';

                }
        }
         return tmpHTML;
    },

    
    doPaging:function(url,offset,searchstr,pager,subPan){
        url+="?limit="+this.panelcount+"&start="+offset+"&searchString="+searchstr;
        this.callRequest(url,searchstr,pager);
    },

    togglePageCss: function(panelid,pager){
        var clickedDiv = document.getElementById(panelid+pager);
        var prevDiv;
        for(var x=0;x<pager;x++){
          prevDiv = document.getElementById(panelid+(x));
          if(prevDiv)
             prevDiv.className="pagination-div deactive-pagination";
        }
        if(clickedDiv)
            clickedDiv.className="pagination-div active-pagination";

    },

    doSearch: function(url,searchstr){
        var str = this.newObj.url;
        var myArr = str.split('?');
        var newUrl=myArr[0]+"?limit="+this.panelcount+"&start=0&searchString="+searchstr;
        this.callRequest(newUrl,searchstr);
    },

    formatSpecifiedField : function(obj){
        for(i=0;i<obj.data.length;i++){
            for(j=0;j<this.newObj.formatField.length;j++){
                obj.data[i][this.newObj.formatField[j]] = getFormattedDate(obj.data[i][this.newObj.formatField[j]]);
            }
        }
    },
    
    formatFileName : function(obj){
        for(var i = 0 ; i < obj.data.length; i++){
            if(obj.data[i].docName != undefined && obj.data[i].docName != null && obj.data[i].docName != ""){
                var imageClass = getimage(obj.data[i].docName);
                obj.data[i].imageClass = imageClass;
            }
        }
    }
});

function showResults() {
    
    var cmbstr="";
        if((document.getElementById('cmb1')) && (document.getElementById('cmb1').value !='')) {
           cmbstr=cmbstr+document.getElementById('cmb1').value+",";
        }
        if((document.getElementById('cmb2')) && (document.getElementById('cmb2').value !='')) {
            cmbstr=cmbstr+document.getElementById('cmb2').value+",";
        }
        if((document.getElementById('cmb3')) && (document.getElementById('cmb3').value !='')) {
            cmbstr=cmbstr+document.getElementById('cmb3').value+",";
        }
    
    Wtf.getCmp('DSBKnowledgeCampus').doSearch("jspfiles/knowledgeUni/workspace.jsp",cmbstr);
}



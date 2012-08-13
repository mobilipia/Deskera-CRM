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
WtfGlobal = {
	getCurrentTime:function(){
		return new Date();
	},
	getEventDefaultStartTime:function(){
		return new Date().clearTime().add(Date.HOUR, 8);
	},
	getEventDefaultEndTime:function(){
		return new Date().clearTime().add(Date.HOUR, 9);
	},
    getCookie: function(c_name){
        if (document.cookie.length > 0) {
            c_start = document.cookie.indexOf(c_name + "=");
            if (c_start != -1) {
                c_start = c_start + c_name.length + 1;
                c_end = document.cookie.indexOf(";", c_start);
                if (c_end == -1)
                    c_end = document.cookie.length;
                return unescape(document.cookie.substring(c_start, c_end));
            }
        }
        return "";
    },
    getSelectComboRendererStore:function(store, valueField, displayField){
       return function(value) {
           var idx;
           var rec;
           var valStr="";
           if (value != undefined && value != "") {
               var valArray = value.split(",");
               for (var i=0;i < valArray.length;i++ ){
                   idx = store.find(valueField, valArray[i]);
                   if(idx != -1){
                       rec = store.getAt(idx);
                       valStr+=rec.get(displayField)+", ";
                   }
               }
               if(valStr != ""){
                   valStr=valStr.substring(0, valStr.length -2);
                   valStr="<div wtf:qtip=\""+valStr+"\">"+Wtf.util.Format.ellipsis(valStr,27)+"</div>";
                }
            }
            return valStr;
        }
    },
    fetchAutoNumber:function(from, fn, scope){
        Wtf.Ajax.requestEx({
            url:Wtf.req.springBase+"common/quotation/getNextAutoNumber.do",
//            url:"ACCCompanyPref/getNextAutoNumber.do",
            params:{
                mode:83,
                from:from
            }
        }, scope,function(resp){
            if(resp.success)
                fn.call(scope,resp)
            else{
                WtfComMsgBox(["Alert",resp.msg],resp.success*2+1);
            }
        });
    },
selectCode:function (itemid)
     {
         itemid = "weblead_code";
	// Get ID of code block
	var e = document.getElementById(itemid);

	// Not IE
	if (window.getSelection)
	{
		var s = window.getSelection();
		// Safari
		if (s.setBaseAndExtent)
		{
			s.setBaseAndExtent(e, 0, e, e.innerText.length - 1);
		}
		// Firefox and Opera
		else
		{
			var r = document.createRange();
			r.selectNodeContents(e);
			s.removeAllRanges();
			s.addRange(r);
		}
	}
	// Some older browsers
	else if (document.getSelection)
	{
		var s = document.getSelection();
		var r = document.createRange();
		r.selectNodeContents(e);
		s.removeAllRanges();
		s.addRange(r);
	}
	// IE
	else if (document.selection)
	{
		var r = document.body.createTextRange();
		r.moveToElementText(e);
		r.select();
	}
},

    nameRenderer: function(value){
        var resultval = value.substr(0, 1);
        var patt1 = new RegExp("^[a-zA-Z]");
        if (patt1.test(resultval)) {
            return resultval.toUpperCase();
        }
        else
            return "Others";
    },
    enableDisableBtnArr:function(btnArr,grid,singleSelectArr,multiSelectArr){
        var multi = !grid.getSelectionModel().hasSelection();
        var single = (grid.getSelectionModel().getCount()!=1);
        for(var i=0;i<multiSelectArr.length;i++)
            btnArr[multiSelectArr[i]].setDisabled(multi);
        for(i=0;i<singleSelectArr.length;i++)
            btnArr[singleSelectArr[i]].setDisabled(single);
    },
    delaytasks:function(editorstore,params){
        var delayTask = new Wtf.util.DelayedTask(function(){
            if(params){
             editorstore.load({
                params:params
            });
            }else{
            editorstore.load();
            }
        },this);
        delayTask.delay(1000);
    },
    emptyGridRenderer: function(value) {
        return "<div class='grid-link-text'><center>"+value+"</center></div>";
    },
    setAjaxReqTimeout : function() {
        Wtf.Ajax.timeout = '90000';
    },

    resetAjaxReqTimeout : function() {
        Wtf.Ajax.timeout = '30000';
    },
    addLabelHelp:function(HelpText){
        return "<span wtf:qtip=\""+HelpText+"\" class=\"formHelpButton\">&nbsp;&nbsp;&nbsp;&nbsp;</span>";
    },
    getXType: function(fieldType){
        switch(fieldType){

            case 1:
                return "textfield";
                break;


           case 2:
               return "numberfield";
               break;

           case 3:
               return "datefield";
               break;


           case 4:
                return "combo";
               break;


           case 5:
                  return "timefield";
               break;

           case 6:
                  return "checkbox";
              break;
           case 7:
                  return "select";

               break;
           case 8:
                return "combo";
               break;
           case 9:
                return "autono";
               break;


        }
    },
    relatedtoRenderer: function(value){
        var resultval = value.substr(0, 1);
        if(value == "Leads")
            return "Leads";
        if(value == "Campaign")
            return "Campaign";
        if(value == "Account")
            return "Accounts";
        if(value == "Contacts")
            return "Contacts";
        if(value == "Product")
            return "Product";
        if(value == "Opportunity")
            return "Opportunity";
        if(value == "Cases")
            return "Cases";
        if(value == "Activity")
            return "Activity";
        else
            return "Others";
    },
    relatedtoIdRenderer: function(val){ // for target list related id
         if(val==1){
            return "Lead";
        } else if(val==2){
            return "Contact";
        } else if(val==3){
            return "User";
        }else if(val==4){
            return "Target";
        }
    },
    replaceAll : function(txt, replace, with_this) {
        return txt.replace(new RegExp(replace, 'g'),with_this);
    },
    setEmptyTextForQuickSearchField : function (quickSearchField,emptyText){

        if(quickSearchField.getValue().trim()==""){

            quickSearchField.setRawValue(emptyText);
            quickSearchField.el.addClass(quickSearchField.emptyClass);
            quickSearchField.el.blur();
        }
        quickSearchField.emptyText=emptyText;

    },
    getDaysDiff : function(enddate,startdate) {
        var one_day = 1000*60*60*24;
        var diff = Math.ceil((enddate.getTime()-startdate.getTime())/one_day);
        return diff;
    },
    goalTypeRenderer : function (val) {
        var tooltip="";
        switch(val){
            case 1:tooltip=WtfGlobal.getGoalTypeToolTip(Wtf.goaltype.nooflead);
                break;
            case 2:tooltip=WtfGlobal.getGoalTypeToolTip(Wtf.goaltype.leadrevenue);
                break;
            case 3:tooltip=WtfGlobal.getGoalTypeToolTip(Wtf.goaltype.noofaccount);
                break;
            case 4:tooltip=WtfGlobal.getGoalTypeToolTip(Wtf.goaltype.accountrevenue);
                break;
            case 5:tooltip=WtfGlobal.getGoalTypeToolTip(Wtf.goaltype.noofopportunity);
                break;
            case 6:tooltip=WtfGlobal.getGoalTypeToolTip(Wtf.goaltype.opprevenue);
                break;
        }
        return tooltip;
    },
    getGoalTypeToolTip : function(val){
        var tooltip = "<div wtf:qtip=\""+val+"\"wtf:qtitle='Goal Type'>"+val+"</div>";
        return tooltip;
    },
    goalStatusRenderer : function (value,row) {
        var related = row.data.relatedto;
        if(related==2 || related==4 || related==6){
            return WtfGlobal.currencyRenderer(value);
        } else
            return '<div class=\'currency\'>'+value+'</div>';

    },
    goalPercentageAchivedRenderer : function (val) {
        var value;
        if(val=='0'){
            return 0+"%";
        }
        else{

            if(typeof val == 'number'){
                value = val.toFixed(0) + '% '
                if(value < 100 || val<100){
                    if(val<100 && val>99){
                        return '<span style=\'color:red !important;\'>99%</span>';
                    }else {
                        return '<span style=\'color:red !important;\'>'+value+'</span>';
                    }
                }

                else
                    return '<span style=\'color:green !important;\'>'+value+'</span>';
            } else {
                value=val
            }

            return value;
        }

    },

    timeCompare: function(obj,val1,val2){
    	//returns 1 =>if val1 is max
    	// 2 => val2 is max;
    	// 0 => if val1==val2
    	var str1=new Array();
		var str2=new Array();
		str1=val1.split(" ");
		str2=val2.split(" ");

    	if(obj.logintimeformat==1){
    		var t1=parseInt(str1[0]=str1[0].replace(":",""))%1200;
    		var t2=parseInt(str2[0]=str2[0].replace(":",""))%1200;
    		if(str1[1]==str2[1]){
    			if(t1>t2)
    				return 1;
    			else if(t1<t2)
    				return 2;
    			else
    				return 0;
    		}
    		else{
    			if(str1[1]=="AM" && str2[1]=="PM")
    				return 2;
    			else if(str1[1]=="PM" && str2[1]=="AM")
    				return 1;
    			else
    				return 0;
        	}
    	}
    	else{
    		  var t1=parseInt(str1[0]);
    		  var t2=parseInt(str2[0]);
    		if(t1>t2)
    			return 1;
    		else if(t1< t2)
    			return 2;
    		else
    			return 0;
    	}
    },
    sizeRenderer: function(value){
        var text = "";
        var sizeinKB = value;
        if (sizeinKB >= 1 && sizeinKB < 1024) {
            text = "Small";
        } else if (sizeinKB > 1024 && sizeinKB < 102400) {
            text = "Medium";
        } else if (sizeinKB > 102400 && sizeinKB < 1048576) {
            text = "Large";
        } else {
            text = "Gigantic";
        }
        return text;
    },
    sizetypeRenderer: function(value){
        var i = 0;
        var text = "";
        var size = value;
        while(size > 1024){
            size/=1024;
            i++;
        }

        switch(i){
            case 0:text=size+" Bytes";break;
            case 1:text=WtfGlobal.decimalFormat(size)+" KB";break;
            case 2:text=WtfGlobal.decimalFormat(size)+" MB";break;
            case 3:text=WtfGlobal.decimalFormat(size)+" GB";break;
            case 4:text=WtfGlobal.decimalFormat(size)+" TB";break;
        }
        return text;
    },

    decimalFormat: function(val){
        val = ""+Math.round(val*100)/100;
        var i=val.indexOf(".");
        if(i<0)val+=".00";
        else if(val.length-i==2)val+="0";
        return val;
    },

    zeroRenderer: function(val){
        if((val=="")&&(val==undefined)){
        	return "";
        }
        else if(val=="0"){
        	return "0";
        } else {
        	return val;
        }
    },

    dateFieldRenderer: function(value){
        var text = "";
        if (value) {
            var dt = new Date();
            if ((value.getMonth() == dt.getMonth()) && (value.getYear() == dt.getYear())) {
                if (dt.getDate() == value.getDate()) {
                    text = "Today";
                } else if (value.getDate() == (dt.getDate() - 1))
                    text = "Yesterday";
                else if (value.getDate() <= (dt.getDate() - 7) && value.getDate() > (dt.getDate() - 14))
                    text = "Last Week";
            } else if ((value.getMonth() == (dt.getMonth() - 1)) && (value.getYear() == dt.getYear()))
                text = "Last Month";
            else if ((value.getYear() == (dt.getYear() - 1)))
                text = "Last Year";
            else
                text = "Older";
        } else
            text = "None";
        return text;
    },

    permissionRenderer: function(value, rec){
        var text = value.toLowerCase();
        switch (text) {
            case "everyone":
                text = "Everyone on deskEra";
                break;
            case "connections":
                text = "All Connections";
                break;
            case "none":
                text = "Private";
                break;
            default:
                text = "Selected Connections";
                break;
        }
        return text;
    },

	HTMLStripper: function(val){
        var str = Wtf.util.Format.stripTags(val);
        return str.trim();
    },
    JSONBuilder : function(keyArray, valArray){
        var obj ={};
        for(var i=0; i< keyArray.length; i++){
            obj[keyArray[i]]=valArray[i];
        }
        return Wtf.encode(obj);
        
    },
    ScriptStripper: function(str){
        str = Wtf.util.Format.stripScripts(str);
        if (str)
            return str.replace(/"/g, '');
        else
            return str;
    },

    URLDecode: function(str){
        if(str) {
            str=str.replace(new RegExp('\\+','g'),' ');
            return unescape(str);
        }
        return str;
    },

    getDateFormat: function() {
        return Wtf.pref.DateFormat;
    },

    getSeperatorPos: function() {
        return Wtf.pref.seperatorpos;
    },

    getOnlyDateFormat: function() {
        var pos=WtfGlobal.getSeperatorPos();
        var fmt=WtfGlobal.getDateFormat();
        if(pos<=0)
            return "Y-m-d";
        return fmt.substring(0,pos);
    },
    loginUserTimeRendererTZ: function(v) {
    	if(!v||!(v instanceof Date)) return v;
    	return new Date(v.getTime()+1*(v.getTimezoneOffset()*60000+Wtf.pref.tzoffset)).format(WtfGlobal.getLoginUserTimeFormat());
    },

    getLoginUserTimeFormat: function() {
        if(logintimeformat==2)
            return 'Hi \\Hr\\s';
        else
            return 'g:i A';
    },
    setDefaultValueTimefield: function() {
        if(logintimeformat==2)
            return "0800 Hrs";
        else
            return "8:00 AM";
    },
    setDefaultValueEndTimefield: function() {
        if(logintimeformat==2)
            return "0900 Hrs";
        else
            return "9:00 AM";
    },
    setDefaultMinValueTimefield: function() {
        if(logintimeformat==2)
            return '0000 Hrs';
        else
            return '12:00am';
    },
    setDefaultMaxValueTimefield: function() {
        if(logintimeformat==2)
            return "2345 Hrs";
        else
            return "11:00pm";
    },
    convertValueTimeFormat: function(timevalue) {
        var time = WtfGlobal.replaceAll("'"+timevalue+"'","([^\\d])","");
        var temptime = time.substr(0,2);
        var temptime1 = time.substr(2,4);
        if(temptime>12){
            temptime = temptime-12;
            time = temptime+":"+temptime1+" PM";
        } else if(temptime==12){
            time = temptime+":"+temptime1+" PM";
        } else {
            time = temptime+":"+temptime1+" AM";
        }
        return time;
    },
    getTimeFieldRenderer : function(val){
    	var val1=val;
       if(val&&val!=""){
            var index = val.indexOf("Hrs")
            if(logintimeformat==2 && index < 0){
            	if(val1.indexOf("PM")>0){
            		if(val1.charAt(0)=="0"){
            		var temptime = val1.substr(0,2);
            		}else{
            			var temptime = val1.substr(0,1);
            		}
            		var temptime1 = val1.substr(2,4);
            		var num = new Number(temptime); 
            			num=num==12?num:num+12;
            			val1=num+temptime1+" PM";
            	}
                var time = WtfGlobal.replaceAll("'"+val1+"'","([^\\d])","");
                for(var i = time.length ; i < 4 ; i++){
                    time="0"+time;
                }
                val = time+" Hrs";
            } else if(logintimeformat==1 && index > 0){
                val = WtfGlobal.convertValueTimeFormat(val);
            }
       }
        return val;
    },
    getOnlyTimeFormat: function() {
        var pos=WtfGlobal.getSeperatorPos();
        var fmt=WtfGlobal.getDateFormat();
        if(pos>=fmt.length)
            return "H:i:s";
        return fmt.substring(pos);
    },

    dateRenderer: function(v) {
        if(!v) return v;
        return v.format(WtfGlobal.getDateFormat());
    },

    dateTimeRenderer: function(v) {
        if(!v) return v;
        var vd = new Date(v);
        return vd.format(WtfGlobal.getDateFormat());
    },

    onlyTimeRenderer: function(v) {
        if(!v) return v;
        return v.format(WtfGlobal.getOnlyTimeFormat());
    },

    onlyDateRenderer: function(v) {
        if(!v) return v;
        return v.format(WtfGlobal.getOnlyDateFormat());
    },
    onlyTimeRendererTZ: function(v) {
    	if(!v||!(v instanceof Date)) return v;
    	return new Date(v.getTime()+1*(v.getTimezoneOffset()*60000+Wtf.pref.tzoffset)).format(WtfGlobal.getOnlyTimeFormat());
    },

    onlyDateRendererTZ: function(v) {
    	if(!v||!(v instanceof Date)) return v;
    	return new Date(v.getTime()+1*(v.getTimezoneOffset()*60000+Wtf.pref.tzoffset)).format(WtfGlobal.getOnlyDateFormat());
    },

    dateTimeRendererTZ: function(v) {
    	if(!v||!(v instanceof Date)) return v;
    	return new Date(v.getTime()+1*(v.getTimezoneOffset()*60000+Wtf.pref.tzoffset)).format(WtfGlobal.getOnlyDateFormat()+' '+WtfGlobal.getLoginUserTimeFormat());
    },

    convertToGenericDate:function(value){
        if(!value) return "";
        return value.format("M d, Y h:i:s A");
    },
    convertToGenericTime:function(value){
        if(!value) return "";
        return value.format("h:i A");
    },

    convertToOnlyDate:function(value){
        if(!value) return value;
        return value.format("M d, Y ");
    },

    getTimeZone: function() {
        return Wtf.pref.Timezone;
    },

    getCurrencyName: function() {
        return Wtf.pref.CurrencyName;
    },

    getCurrencySymbol: function() {
        return Wtf.pref.CurrencySymbol;
    },

    linkRenderer: function(value) {
        return "<a class='jumplink' href='#'>"+value+"</a>";
    },

    currencyRenderer: function(value) {

        var v = WtfGlobal.getCurrencySymbolWithValue(value);
        return '<div class=\'currency\'>'+v+'</div>';

    },
    getCurrencySymbolWithValue: function (value){

        var v=parseFloat(value);
        if(isNaN(v))
            return "";
        v = (Math.round((v-0)*100))/100;
        v = (v == Math.floor(v)) ? v + ".00" : ((v*10 == Math.floor(v*10)) ? v + "0" : v);
        v = String(v);
        var ps = v.split('.');
        var whole = ps[0];
        var sub = ps[1] ? '.'+ ps[1] : '.00';
        var r = /(\d+)(\d{3})/;
        while (r.test(whole)) {
            whole = whole.replace(r, '$1' + ',' + '$2');
        }
        v = whole + sub;
        if(v.charAt(0) == '-'){
            v= '-'+ WtfGlobal.getCurrencySymbol() + v.substr(1);
        }else{
            v= WtfGlobal.getCurrencySymbol() +" "+v;
        }

        return v;
    },

    currencySummaryRenderer: function(value) {
        return '<font color="red">'+WtfGlobal.currencyRenderer(value)+'<font>';
    },

    validateEmail: function(value){
        return Wtf.ValidateMailPatt.test(value);
    },

    renderEmailTo: function(value,p,record){
        return "<div class='mailTo'><a onclick=\"handleComposeForModule('"+value+"')\">"+(value?value:'&nbsp;')+"</a></div>";
    },

    validateHTField:function(value){
      return Wtf.validateHeadTitle.test(value.trim());
    },

    renderContactToSkype: function(value,p,record){
        return "<div class='mailTo'><a href=skype:"+value+"?call>"+value+"</a></div>";
    },

    renderContactToCall: function(value,p,record){
        var val="";
        var value1="";
        var value2="";
        var stringval="";
        if(value!=""){
          //var patt1=/\(*?[0-9]+\)*?\-*?[0-9]+/;
            var patt1=/\(*?[0-9]+\)*?[ \- ]*?[0-9]+[ \- ]*?[0-9]+/;
            if(value.match==undefined){
                value = value.toString();
            }
            while(value.match(patt1)){
            val=(value.match(patt1)!=null)?value.match(patt1):"";
            if(val!=""){
                var extrsvalue1=value.split(val);
                value1=extrsvalue1[0];
                value=extrsvalue1[1]?extrsvalue1[1]:"";
            }else{
                value1=value;
            }
            stringval+=String.format(callwithStr,val,val,value1);
            }
            stringval+=value;
        }

        return "<div class='mailTo'>"+stringval+"</div>";
    },

    renderValidFlagAndComment: function(value, css, record, row, column, store) {
        var str="";
        var imgSrc="";
        var height="";
        var tip="";
        var clas="";
        var id="";
        var recdata = store.getAt(row).data;
        if(value == 1) {
           str=Wtf.commentRenderer(value,recdata.totalcomment,recdata.commentcount);
           return str;
        } else if(value == null) {
            imgSrc = "../../images/indent.gif";
            height = "12px";
            tip = " wtf:qtitle='Add' wtf:qtip='Click on a field to add new record.' ";
            id="addnew" + record.store.baseParams.flag;
        } else if(value == -1) {
            imgSrc = "../../images/loading.gif";
            height = "12px";
            tip = " wtf:qtitle='Add' wtf:qtip='Saving record.' ";
            id="save" + record.store.baseParams.flag;
        } else if(value == 0){
            imgSrc = "../../images/FlagRed.png";
            height = "12px";
            tip = " wtf:qtitle='Incomplete information(click to view required mandatory fields).' wtf:qtip=' Please fill all the essential fields (marked with an asterisk *).' ";
            id="flag";
            clas = "class = \'showMandatoryFields\'";
            str="<span style='padding-left:1px; float:left;'>";
        }
        str += "<img src='"+imgSrc+"' id='"+id+"' "+clas+" "+tip+" height='"+height+"' style='cursor:pointer'></img>";
        return str;
    },

    validateUserid: function(value){
        return Wtf.ValidateUserid.test(value);
    },

    validateUserName: function(value){
        return Wtf.ValidateUserName.test(value.trim());
    },

    getInstrMsg: function(msg){
        return "<span style='font-size:10px !important;color:gray !important;'>"+msg+"</span>"
    },

    chkFirstRun: function(){
        return WtfGlobal.getCookie("lastlogin") == "1990-01-01 00:00:00.0";
    },

    EnableDisable: function(userpermcode, permcode){
        if(permcode==null){
            clog("Some Permission are undefined.\n"+userpermcode+"\n"+showCallStack());

        }
        if (userpermcode && permcode) {
            if ((userpermcode & permcode) == permcode)
                return false;
        }
        return true;
    },

    enableDisableTbBtnArr: function(btnArr,grid,singleSelectArr,multiSelectArr, defaultArr){
        var count = grid.getSelectionModel().getCount();
        var multi = false;
        var defaultTB = false;
        var singleValid = false;
        if(count==1) {
            var rec = grid.getSelectionModel().getSelected();
            var validF = rec.data.validflag;
            if(validF == 1) {
                singleValid = true;
            } else {
                if(validF == 0){
                    multi = true;
                } else {
                    defaultTB = true;
                }
            }
        } else if(count > 1) {
            multi = true;
        } else {
            singleValid = false;
            multi = false;
            defaultTB = true;
        }
        for(i=0;i<singleSelectArr.length;i++)
                btnArr[singleSelectArr[i]].setDisabled(true);

        if(multi) {
            for(var i=0;i<multiSelectArr.length;i++)
                btnArr[multiSelectArr[i]].setDisabled(false);
        }
        if(singleValid) {
            for(i=0;i<singleSelectArr.length;i++)
                btnArr[singleSelectArr[i]].setDisabled(false);
        }
        if(defaultTB) {
            for(i=0;i<defaultArr.length;i++)
                btnArr[defaultArr[i]].setDisabled(false);
        }
    },

    getLocaleText:function(key, basename, def){
        var base=window[basename||"messages"];
        var params=[].concat(key.params||[]);
        key = key.key||key;
        if(base){
            if(base[key]){
                    params.splice(0, 0, base[key]);
                    return String.format.apply(this,params);
            }else
                    clog("Locale spacific text not found for ["+key+"]");
        }else{
        	if(basename!=undefined){
        		clog("Locale spacific base ("+basename+") not available");
        	}
        }
        return def||key;
    },

    loadScript: function(src, callback, scope){
        var scriptTag = document.createElement("script");
        scriptTag.type = "text/javascript";
        if(typeof callback == "function"){
        	scriptTag.onreadystatechange= function () {
        		      if (this.readyState == 'complete')
        		    	  callback.call(scope || this || window);
        		   }
        	scriptTag.onload= callback.createDelegate(scope || this || window);
        }
        scriptTag.src = src;
        document.getElementsByTagName("head")[0].appendChild(scriptTag);
    },

    loadStyleSheet: function(ref){
        var styleTag = document.createElement("link");
        styleTag.setAttribute("rel", "stylesheet");
        styleTag.setAttribute("type", "text/css");
        styleTag.setAttribute("href", ref);
        document.getElementsByTagName("head")[0].appendChild(styleTag);

    },
    getJSONArray:function(grid, includeLast, idxArr){
        var indices="";
        if(idxArr)
            indices=":"+idxArr.join(":")+":";
        var store=grid.getStore();
        var arr=[];
        var fields=store.fields;
        var len=store.getCount()-1;
        if(includeLast)len++;
        for(var i=0;i<len;i++){
            if(idxArr&&indices.indexOf(":"+i+":")<0) continue;
            var rec=store.getAt(i);
            var recarr={};
            for(var j=0;j<fields.length;j++){
                var value=rec.data[fields.get(j).name];
                switch(fields.get(j).type){
                    case "date":value=WtfGlobal.convertToGenericDate(value);break;
                }
                recarr[fields.get(j).name]=value;
            }
            recarr["modified"]=rec.dirty;
            arr.push(recarr);
        }
        return Wtf.encode(arr);
    },
    
// remove new line from string i'e \n , \t and \r
    removeNL :function(s) {
        if(typeof s  == 'string')
            return s.replace(/[\n\r\t]/g,'');
        else
            return s;
    },

    convertToUserTimezone : function (value) {
        var time1 = new Date(value);
        var timeZoneArray = Wtf.TimeZoneDiff.split(":");
        var tzdiff = 0;
        tzdiff = parseInt(timeZoneArray[0]);
        if(parseInt(timeZoneArray[1])>0)
            tzdiff += (parseInt(timeZoneArray[1]) / 60);
        var gmtX = Math.floor(tzdiff*60*60*1000);
        time1.setTime(time1.getTime() + gmtX);
        return time1;
    },

    // added functions for invoice/quotation
    addCurrencySymbolOnly: function(value,symbol,isCheckCenterAlign) {
        symbol=((symbol==undefined||symbol==null||symbol=="")?WtfGlobal.getCurrencySymbol():symbol);
        symbol=((symbol.data!=undefined&&symbol.data['currencysymbol']!=null&&symbol.data['currencysymbol']!=undefined&&symbol.data['currencysymbol']!="")?symbol.data['currencysymbol']:symbol);
        var isCenterAlign=(isCheckCenterAlign==undefined?false:isCheckCenterAlign[0]);
        symbol=(symbol==undefined?WtfGlobal.getCurrencySymbol():symbol);
        var v=parseFloat(value);
        if(isNaN(v)) return value;
           v= WtfGlobal.conventInDecimal(v,symbol)
            if(isCenterAlign)
                 return '<div>'+v+'</div>';
        return '<div class="currency">'+v+'</div>';
    },

    conventInDecimal: function(v,symbol) {
            v = (Math.round((v-0)*100))/100;
            v = (v == Math.floor(v)) ? v + ".00" : ((v*10 == Math.floor(v*10)) ? v + "0" : v);
            v = String(v);
            var ps = v.split('.');
            var whole = ps[0];
            var sub = ps[1] ? '.'+ ps[1] : '.00';
            var r = /(\d+)(\d{3})/;
            while (r.test(whole)) {
                whole = whole.replace(r, '$1' + ',' + '$2');
            }
            v = whole + sub;
            if(v.charAt(0) == '-') {
//                v= '-'+symbol + " " + v.substr(1);
                v= "(<label style='color:red'>"+symbol + " " + v.substr(1)+"</label>)";
            } else
                v=symbol + " " + v;
            return v;
    }

};


/*  WtfHTMLEditor: Start    */
Wtf.newHTMLEditor = function(config){
    Wtf.apply(this, config);
    this.createLinkText = 'Please enter the URL for the link:';
    this.defaultLinkValue = 'http:/'+'/';
    this.smileyel = null;
    this.SmileyArray = [" ", ":)", ":(", ";)", ":D", ";;)", ">:D<", ":-/", ":x", ":>>", ":P", ":-*", "=((", ":-O", "X(", ":>", "B-)", ":-S", "#:-S", ">:)", ":((", ":))", ":|", "/:)", "=))", "O:-)", ":-B", "=;", ":-c", ":)]", "~X("];
    this.tpl = new Wtf.Template('<div id="{curid}smiley{count}" style="float:left; height:20px; width:20px; background: #ffffff;padding-left:4px;padding-top:4px;"  ><img id="{curid}smiley{count}" src="{url}" style="height:16px; width:16px"></img></div>');
this.tbutton = new Wtf.Toolbar.Button({
        minWidth: 30,
        disabled:true,
        hidden:this.hiddenflag,
        enableToggle: true,
        iconCls: 'smiley'
    });
    this.eventSetFlag=false;
    this.tbutton.on("click", this.handleSmiley, this);
    this.smileyWindow = new Wtf.Window({
        width: 185,
        height: 116,
        minWidth: 200,
        plain: true,
        cls: 'replyWind',
        shadow: false,
        buttonAlign: 'center',
        draggable: false,
        header: false,
        closable  : true,
        closeAction : 'hide',
        resizable: false
    });
    this.smileyWindow.on("deactivate", this.closeSmileyWindow, this);
    Wtf.newHTMLEditor.superclass.constructor.call(this, {});
    this.on("render", this.addSmiley, this);
    this.on("activate", this.enableSmiley, this);
    this.on("hide", this.hideSmiley, this);
    this.on("render", this.addCSS, this);
}

Wtf.extend(Wtf.newHTMLEditor, Wtf.form.HtmlEditor, {
    enableSmiley:function(){
        this.tbutton.enable();
    },
    hideSmiley: function(){
        if(this.smileyWindow !== undefined && this.smileyWindow.el !== undefined)
            this.smileyWindow.hide();
    },
    addSmiley: function(editorObj){
        editorObj.getToolbar().addSeparator();
        editorObj.getToolbar().addButton(this.tbutton);

    },
    addCSS:function(){
    	if(Wtf.isIE){
    		this.iframe.contentDocument.activeElement.style.padding='0px';
    	}
    },
     createLink : function(){
        var url = prompt(this.createLinkText, this.defaultLinkValue);
        if(url && url != 'http:/'+'/'){
            var tmpStr = url.substring(0,7);
            if(tmpStr!='http:/'+'/')
                url = 'http:/'+'/'+url;
            this.win.focus();
            var selTxt = "";
             if (Wtf.isIE) {
                var range = this.doc.selection.createRange();
                selTxt = (range.text).trim();
                range.text="";
            }else {
               selTxt = this.doc.getSelection();
            }
            selTxt = selTxt =="" ? url : selTxt;
            if(this.SmileyArray.join().indexOf(selTxt)==-1) {
                this.insertAtCursor("<a href = '"+url+"' target='_blank'>"+selTxt+"</a>");
                this.deferFocus();
            } else {
                msgBoxShow(170,1);
            }
        }
    },
    //  FIXME: ravi: When certain smilies are used in a pattern, the resultant from this function does not conform to regex used to decode smilies in messenger.js.

    writeSmiley: function(e){
        var obj=e;
        this.insertAtCursor(this.SmileyArray[obj.target.id.substring(this.id.length + 6)]+" ");
        this.smileyWindow.hide();
        this.tbutton.toggle(false);
    },

    handleSmiley: function(buttonObj, e){
        if(this.tbutton.pressed) {
            this.smileyWindow.setPosition(e.getPageX(), e.getPageY());
            this.smileyWindow.show();
            if(!this.eventSetFlag){
                for (var i = 1; i < 29; i++) {
                    var divObj = {
                        url: '../../images/smiley' + i + '.gif',
                        count: i,
                        curid: this.id
                    };
                    this.tpl.append(this.smileyWindow.body, divObj);
                    this.smileyel = Wtf.get(this.id + "smiley" + i);
                    this.smileyel.on("click", this.writeSmiley, this);
                    this.eventSetFlag=true;
                }
            }
        } else {
            this.smileyWindow.hide();
            this.tbutton.toggle(false);
        }
    },

    closeSmileyWindow: function(smileyWindow){
        this.smileyWindow.hide();
        this.tbutton.toggle(false);
    }
});

// Call stack code
function showCallStack(){
var f=showCallStack,result="Call stack:\n";

while((f=f.caller)!==null){
var sFunctionName = f.toString().match(/^function (\w+)\(/)
sFunctionName = (sFunctionName) ? sFunctionName[1] : 'anonymous function';
result += sFunctionName;
result += getArguments(f.toString(), f.arguments);
result += "\n";

}
return result;
}


function getArguments(sFunction, a) {
var i = sFunction.indexOf(' ');
var ii = sFunction.indexOf('(');
var iii = sFunction.indexOf(')');
var aArgs = sFunction.substr(ii+1, iii-ii-1).split(',')
var sArgs = '';
for(var i=0; i<a.length; i++) {
var q = ('string' == typeof a[i]) ? '"' : '';
sArgs+=((i>0) ? ', ' : '')+(typeof a[i])+' '+aArgs[i]+':'+q+a[i]+q+'';
}
return '('+sArgs+')';
}

Wtf.taskDetail = Wtf.extend(Wtf.Component, {

	tplMarkup: ['<div id="fcue-360" class="fcue-outer" style="position: absolute; z-index:2000000; left: 188px; top: 12px;">'+
            '<div class="fcue-inner'+(Wtf.isIE6*1||'')+'">'+
                '<div class="fcue-t'+(Wtf.isIE6*1||'')+'"></div>'+
                '<div class="fcue-content">'+
                        '<a onclick="closeCue();" href="#" id="fcue-close"></a>'+
                    '<div class="ft ftnux"><p>'+
                        '</p><span id="titlehelp" style="font-weight:bold;">Welcome Help Dialog</span><p></p><span id="titledesc">sssdd</span>'+
                        '<div class="buttonHelp"><p></p>'+
                        '<a class="cta-1 cta button1" style="display:none;" id="closeID" onclick="closeCue();" href="javascript:;"><img src="../../images/close.jpg" width="80" height="30"></a>'+
                        '<a class="cta-1 cta button1" style="display:none;" id="helptipsID" onclick="goToNextCue();" href="javascript:;"><img src="../../images/help-tip.jpg" width="80" height="30"></a>'+
                        '<a class="cta-1 cta button1" id="nextID" onclick="goToNextCue();" href="javascript:;"><img src="../../images/next.gif" width="80" height="30"></a>'+
                        '<a class="cta-1 cta button1" id="previousID" onclick="goToPrevCue();" href="javascript:;"><img src="../../images/previous.gif" width="80" height="30"></a>'+
                        '</div>'+
                    '</div>'+
                '</div>'+
            '</div>'+
            '<div class="fcue-b'+(Wtf.isIE6*1||'')+'">'+
                '<div></div>'+
            '</div>'+
            '</div>',
        // left - top
  		'<div id="fcue-360" class="fcue-outer" style="position: absolute; z-index:2000000; left: 188px; top: 12px;">'+
            '<div class="fcue-inner'+(Wtf.isIE6*1||'')+'">'+
                '<div class="fcue-t'+(Wtf.isIE6*1||'')+'"></div>'+
                '<div class="fcue-content">'+
                  '<a onkeypress="" onclick="closeCue();" href="#" id="fcue-close"></a>'+
                    '<div class="ft ftnux"><p>'+
                        '</p></p><span id="titlehelp" style="font-weight:bold;">Welcome Help Dialog</span><p></p><span id="titledesc">sssdd</span>'+
                        '<div><div ><p></p>'+
                        '<a class="cta-1 cta button1"  style="display:none;" id="closeID" onclick="closeCue();" href="javascript:;"><img src="../../images/close.jpg" width="80" height="30"></a></div>'+
                        '<a class="cta-1 cta button1" id="nextID" onclick="goToNextCue();" href="javascript:;"><strong class="i"><img src="../../images/next.gif" width="80" height="30"></a>'+
                        '<a class="cta-1 cta button1" id="previousID" onclick="goToPrevCue();" href="javascript:;"><img src="../../images/previous.gif" width="80" height="30"></a>'+'<span>&nbsp;</span>'+
                        '</div>'+
                    '</div>'+
                '</div>'+
            '</div>'+
            '<div class="fcue-b'+(Wtf.isIE6*1||'')+'">'+
                '<div></div>'+
            '</div>'+
            '<div class="fcue-pnt'+(Wtf.isIE6*1||'')+' fcue-pnt-lf-t'+(Wtf.isIE6*1||'')+'">'+
            '</div>'+
        '</div>',
        // left - bottom
        '<div id="fcue-360" class="fcue-outer" style="position: absolute; z-index:2000000; left: 188px; top: 12px;">'+
            '<div class="fcue-inner'+(Wtf.isIE6*1||'')+'">'+
                '<div class="fcue-t'+(Wtf.isIE6*1||'')+'"></div>'+
                '<div class="fcue-content">'+
                 '<a onclick="closeCue();" href="#" id="fcue-close"></a>'+
                    '<div class="ft ftnux"><p>'+
                        '</p></p><span id="titlehelp" style="font-weight:bold;">Welcome Help Dialog</span><p></p><span id="titledesc">sssdd</span>'+
                        '<div><div ><p></p>'+
                        '<a class="cta-1 cta button1"  style="display:none;" id="closeID" onclick="closeCue();" href="javascript:;"><img src="../../images/close.jpg" width="80" height="30"></a></div>'+
                        '<a class="cta-1 cta button1" id="nextID" onclick="goToNextCue();" href="javascript:;"><strong class="i"><img src="../../images/next.gif" width="80" height="30"></a>'+
                        '<a class="cta-1 cta button1" id="previousID" onclick="goToPrevCue();" href="javascript:;"><img src="../../images/previous.gif" width="80" height="30"></a>'+'<span>&nbsp;</span>'+
                        '</div>'+
                    '</div>'+
                '</div>'+
            '</div>'+
            '<div class="fcue-b'+(Wtf.isIE6*1||'')+'">'+
                '<div></div>'+
            '</div>'+
            '<div class="fcue-pnt'+(Wtf.isIE6*1||'')+' fcue-pnt-lf-b">'+
            '</div>'+
        '</div>',

        // 3 : bottom - left
        '<div id="fcue-360" class="fcue-outer" style="position: absolute; z-index:2000000; left: 188px; top: 12px;">'+
            '<div class="fcue-inner'+(Wtf.isIE6*1||'')+'">'+
                '<div class="fcue-t'+(Wtf.isIE6*1||'')+'"></div>'+
                '<div class="fcue-content">'+
                  '<a onclick="closeCue();" href="#" id="fcue-close"></a>'+
                    '<div class="ft ftnux"><p>'+
                        '</p></p><span id="titlehelp" style="font-weight:bold;">Welcome Help Dialog</span><p></p><span id="titledesc">sssdd</span>'+
                        '<div><div ><p></p>'+
                        '<a class="cta-1 cta button1" style="display:none;" id="closeID" onclick="closeCue();" href="javascript:;"><img src="../../images/close.jpg" width="80" height="30"></a></div>'+
                        '<a class="cta-1 cta button1" id="nextID" onclick="goToNextCue();" href="javascript:;"><strong class="i"><img src="../../images/next.gif" width="80" height="30"></a>'+
                        '<a class="cta-1 cta button1" id="previousID" onclick="goToPrevCue();" href="javascript:;"><img src="../../images/previous.gif" width="80" height="30"></a>'+'<span>&nbsp;</span>'+
                        '</div>'+
                    '</div>'+
                '</div>'+
            '</div>'+
            '<div class="fcue-b'+(Wtf.isIE6*1||'')+'">'+
                '<div></div>'+
            '</div>'+
            '<div id="pointerdiv" class="fcue-pnt'+(Wtf.isIE6*1||'')+' fcue-pnt-bm-l'+(Wtf.isIE6*1||'')+'">'+
            '</div>'+
        '</div>',
        // bottom - right
        '<div id="fcue-360" class="fcue-outer" style="position: absolute; z-index:2000000; left: 188px; top: 12px;">'+
            '<div class="fcue-inner'+(Wtf.isIE6*1||'')+'">'+
                '<div class="fcue-t'+(Wtf.isIE6*1||'')+'"></div>'+
                '<div class="fcue-content">'+
                  '<a onclick="closeCue();" href="#" id="fcue-close"></a>'+
                    '<div class="ft ftnux"><p>'+
                        '</p></p><span id="titlehelp" style="font-weight:bold;">Welcome Help Dialog</span><p></p><span id="titledesc">sssdd</span>'+
                        '<div><div ><p></p>'+
                        '<a class="cta-1 cta button1" style="display:none;" id="closeID" onclick="closeCue();" href="javascript:;"><img src="../../images/close.jpg" width="80" height="30"></a></div>'+
                        '<a class="cta-1 cta button1" id="nextID" onclick="goToNextCue();" href="javascript:;"><strong class="i"><img src="../../images/next.gif" width="80" height="30"></a>'+
                        '<a class="cta-1 cta button1" id="previousID" onclick="goToPrevCue();" href="javascript:;"><img src="../../images/previous.gif" width="80" height="30"></a>'+'<span>&nbsp;</span>'+
                        '</div>'+
                    '</div>'+
                '</div>'+
            '</div>'+
            '<div class="fcue-b'+(Wtf.isIE6*1||'')+'">'+
                '<div></div>'+
            '</div>'+
            '<div id="pointerdiv" class="fcue-pnt'+(Wtf.isIE6*1||'')+' fcue-pnt-bm-r'+(Wtf.isIE6*1||'')+'">'+
            '</div>'+
        '</div>',
        // top - left
        '<div id="fcue-360" class="fcue-outer" style="position: absolute; z-index:2000000; left: 188px; top: 12px;">'+
            '<div class="fcue-inner'+(Wtf.isIE6*1||'')+'">'+
                '<div class="fcue-t'+(Wtf.isIE6*1||'')+'"></div>'+
                '<div class="fcue-content">'+
                  '<a onclick="closeCue();" href="#" id="fcue-close"></a>'+
                    '<div class="ft ftnux"><p>'+
                        '</p></p><span id="titlehelp" style="font-weight:bold;">Welcome Help Dialog</span><p></p><span id="titledesc">sssdd</span>'+
                        '<div><div ><p></p>'+
                        '<a class="cta-1 cta button1" style="display:none;" id="closeID" onclick="closeCue();" href="javascript:;"><img src="../../images/close.jpg" width="80" height="30"></a></div>'+
                        '<a class="cta-1 cta button1" id="nextID" onclick="goToNextCue();" href="javascript:;"><img src="../../images/next.gif" width="80" height="30"></a>'+
                        '<a class="cta-1 cta button1" id="previousID" onclick="goToPrevCue();" href="javascript:;"><img src="../../images/previous.gif" width="80" height="30"></a>'+'<span>&nbsp;</span>'+
                        '</div>'+
                    '</div>'+
                '</div>'+
            '</div>'+
            '<div class="fcue-b'+(Wtf.isIE6*1||'')+'">'+
                '<div></div>'+
            '</div>'+
            '<div class="fcue-pnt'+(Wtf.isIE6*1||'')+' fcue-pnt-t-l'+(Wtf.isIE6*1||'')+'">'+
            '</div>'+
        '</div>',
        // top - right
        '<div id="fcue-360" class="fcue-outer" style="position: absolute; z-index:2000000; left: 188px; top: 12px;">'+
            '<div class="fcue-inner'+(Wtf.isIE6*1||'')+'">'+
                '<div class="fcue-t'+(Wtf.isIE6*1||'')+'"></div>'+
                '<div class="fcue-content">'+
                  '<a onclick="closeCue();" href="#" id="fcue-close"></a>'+
                    '<div class="ft ftnux"><p>'+
                        '</p></p><span id="titlehelp" style="font-weight:bold;">Welcome Help Dialog</span><p></p><span id="titledesc">sssdd</span>'+
                        '<div><div ><p></p>'+
                        '<a class="cta-1 cta button1" id="nextID" onclick="goToNextCue();" href="javascript:;"><img src="../../images/next.gif" width="80" height="30"></a>'+
                        '<a class="cta-1 cta button1" id="previousID" onclick="goToPrevCue();" href="javascript:;"><img src="../../images/previous.gif" width="80" height="30"></a>'+'<span>&nbsp;</span>'+
                        '<a class="cta-1 cta button1"  style="display:none;" id="closeID" onclick="closeCue();" href="javascript:;"><img src="../../images/close.jpg" width="80" height="30"></a></div>'+
                        '</div>'+
                    '</div>'+
                '</div>'+
            '</div>'+
            '<div class="fcue-b'+(Wtf.isIE6*1||'')+'">'+
                '<div></div>'+
            '</div>'+
            '<div class="fcue-pnt'+(Wtf.isIE6*1||'')+' fcue-pnt-t-r">'+
            '</div>'+
        '</div>'],

	startingMarkup: 'Please select a module to see details',
    id : 'helpdialog',
    helpIndex : 0,
	initComponent: function(config) {
		Wtf.taskDetail.superclass.initComponent.call(this, config);
	},

	welcomeHelp: function(flag) {
        if(document.getElementById('fcue-360-mask'))
        	document.getElementById('fcue-360-mask').style.display="block";
        var data = _helpContent[this.helpIndex];
        var compid = data.compid;
        if(flag==undefined)
            flag=1;
        if(compid=="") {
            var len=_helpContent.length;
            this.tpl = new Wtf.Template(this.tplMarkup[0]);
            var ht = this.tpl.append(document.body,{});
            document.getElementById('titlehelp').innerHTML =(WtfGlobal.getLocaleText(data.titlelocalekey)!=null && WtfGlobal.getLocaleText(data.titlelocalekey)!="")?WtfGlobal.getLocaleText(data.titlelocalekey):"";
            document.getElementById('titledesc').innerHTML =(WtfGlobal.getLocaleText(data.localekey)!=null && WtfGlobal.getLocaleText(data.localekey)!="")?WtfGlobal.getLocaleText(data.localekey):"";
            Wtf.get('fcue-360').setXY([500,250]);
            var ele1 = document.getElementById('fcue-360');
             var helpDiv = Wtf.get('fcue-360');
            var helpSize = helpDiv.getSize();
            if(Wtf.isIE6)
            ele1.children[1].style.width = (helpSize.width-22);
            ele1.style.visibility ="visible";
            if(ele1.children) {
                ele1.children[0].style.height = (helpSize.height-20);
                ele1.children[0].style.visibility ="visible";
            }
            if(this.helpIndex == len-2){
                document.getElementById("nextID").style.display ="none";
                document.getElementById("previousID").style.display="inline";
                document.getElementById("helptipsID").style.display="inline";
                document.getElementById("closeID").style.display="inline";
            }
            else if(this.helpIndex==0 && len==1) {  // For Help message that has only one tip
                document.getElementById("nextID").style.display ="none";
                document.getElementById("previousID").style.display="none";
                document.getElementById("helptipsID").style.display="none";
                document.getElementById("closeID").style.display="inline";
            }
            else if(this.helpIndex==0) {
                document.getElementById("nextID").style.visibility ="visible";
                document.getElementById("previousID").style.visibility ="hidden";
            } else if(this.helpIndex == len-1){
                document.getElementById("nextID").style.display ="none";
                document.getElementById("previousID").style.display="inline";
                document.getElementById("helptipsID").style.display="none";
                document.getElementById("closeID").style.display="inline";
            } else {
                document.getElementById("nextID").style.visibility ="visible";
                document.getElementById("previousID").style.visibility ="visible";
            }
        } else
            this.nextPrevious(flag);
	},

    updateToNextDetail: function() {
        this.helpIndex = this.helpIndex+1;
        this.welcomeHelp(1);
    },

    updateToPrevDetail: function() {
        this.helpIndex = this.helpIndex-1;
        this.welcomeHelp(2);
	},

    blankDetail : function() {
        this.bltpl.overwrite(this.body,"");
	},

    getTemplateIndex : function(comppos) {
        var index = 0;
        var xPos = comppos[0];
        var yPos = comppos[1];
        var flag = 0;
        var myWidth = 0, myHeight = 0;
        if( typeof( window.innerWidth ) == 'number' ) {
            //Non-IE
            myWidth = window.innerWidth;
            myHeight = window.innerHeight;
        } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
            //IE 6+ in 'standards compliant mode'
            myWidth = document.documentElement.clientWidth;
            myHeight = document.documentElement.clientHeight;
        } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
            //IE 4 compatible
            myWidth = document.body.clientWidth;
            myHeight = document.body.clientHeight;
        }

        if(xPos<20) { // extreme left
            flag = 1;
        } else if(xPos>(myWidth-370)) { // extreme right
            flag = 2;
        }
        if(yPos<100) {
            if(flag == 1) {
                index = 1; // left top corner
            } else if(flag == 2){
                index = 6; // top right corner
            } else {
                index = 5;
            }
        } else if(yPos>(myHeight-400)) {
            if(flag == 1) {// bottom left corner
                index = 3;
            } else if(flag == 2) {// bottom right corner
                index = 4;
            } else
                index = 3; // bottom left corner
        } else if(yPos<(myHeight/2)) {
            if(flag==1)
                index = 1;
            else if(flag==2)
                index = 6;
            else index = 5;
        } else if(yPos>(myHeight/2)) {
            if(flag==1)
                index = 2;
            else
                index = 3;
        }
        return index;
    },

    nextPrevious:function(flag){
        var len=_helpContent.length;
        var data = _helpContent[this.helpIndex];

        if(Wtf.get(data['compid'])==null) {
            if(flag==1)
                this.updateToNextDetail();
            else
                this.updateToPrevDetail();
            return;
        } else if(Wtf.get(data['compid']).getXY()[0]==0 && Wtf.get(data['compid']).getXY()[1]==0){
            if(flag==1) {
                if(this.helpIndex==0)
                    this.firstCollapse=1;
                this.updateToNextDetail();
            } else
                this.updateToPrevDetail();
            return;
        }
        var comppos = Wtf.get(data['compid']).getXY();

        if(data['modeid'] == "1") {
            var dash = Wtf.getCmp('portal_container').body;
            dash.dom.scrollTop = comppos[1] - 70;
        } else if(mainPanel.activeTab.EditorGrid!=null && data['compid'].indexOf('addnew')!=-1) {  // crm modules
            var store = mainPanel.activeTab.EditorStore;
            var storeLen = store.getCount();
            var rowEl = mainPanel.activeTab.EditorGrid.getView().getRow(0);
            var gBody = mainPanel.activeTab.EditorGrid.getView().scroller;
            var a = (Wtf.fly(rowEl).getOffsetsTo(gBody)[1]) + gBody.dom.scrollTop;
            gBody.dom.scrollTop = a;
        }
        comppos = Wtf.get(data['compid']).getXY();

        var index = this.getTemplateIndex(comppos);

        this.tpl = new Wtf.Template(this.tplMarkup[index]);
        var ht = this.tpl.append(document.body,{});
        document.getElementById('titlehelp').innerHTML = WtfGlobal.getLocaleText(data.titlelocalekey);
        document.getElementById('titledesc').innerHTML = WtfGlobal.getLocaleText(data.localekey);
        var helpDiv = Wtf.get('fcue-360');
        var helpSize = helpDiv.getSize();
        var pos = comppos;

        switch(index) {
            case 1: //left-top
                pos[1] -= 35;
                pos[0] += 60;
                break;
            case 2: //left-bottom
                break;
            case 3: //bottom-left
                pos[1] -= (helpSize.height);
                pos[0] -= 30
                document.getElementById('pointerdiv').style.top = ((helpSize.height-20)+'px');// 22px - bottom div height
                break;
            case 4: //bottom-right
                pos[1] -= (helpSize.height);
                pos[0] -= (helpSize.width-22-32); // 22px - left div width and 32px - pointer position at inner side
                document.getElementById('pointerdiv').style.top = ((helpSize.height-20)+'px');// 22px - bottom div height
                break;
            case 5: //top - left
                pos[1] += 38;
                break;
            case 6: //top - right
                pos[1] += 35;
                pos[0] -= 310;
                break;
        }
        helpDiv.setXY(pos);
        var ele1 = document.getElementById('fcue-360');
        ele1.style.visibility ="visible";
        if(ele1.children) {
            ele1.children[0].style.height = (helpSize.height-20);
            ele1.children[0].style.visibility ="visible";
        }
        if(this.helpIndex == len-1) {
            if(this.firstCollapse == 0) {
                document.getElementById("nextID").style.display ="none";
                document.getElementById("previousID").style.cssFloat="left";
                document.getElementById("closeID").style.display="inline";
            } else {
                document.getElementById("nextID").style.display ="none";
                document.getElementById("previousID").style.display="none";
                document.getElementById("closeID").style.display="inline";
            }
        } else if(this.helpIndex == 0 || this.firstCollapse == 1 || this.prevCollapse == this.helpIndex) {
            document.getElementById("nextID").style.visibility ="visible";
            document.getElementById("previousID").style.visibility ="hidden";
            this.prevCollapse = this.helpIndex;
            this.firstCollapse=0;
        } else{
            document.getElementById("nextID").style.visibility ="visible";
            document.getElementById("previousID").style.visibility ="visible";
        }
    }

});

function closeCue () {
    Wtf.get('fcue-360').remove();
    if(document.getElementById('fcue-360-mask'))document.getElementById('fcue-360-mask').style.display="none";
}

function goToNextCue() {
    closeCue();
    Wtf.getCmp('helpdialog').updateToNextDetail();
}

function goToPrevCue() {
    closeCue();
    Wtf.getCmp('helpdialog').updateToPrevDetail();
}

function isEmpty(str) {
    if(typeof str=="string")
        return (!str || 0 === str.length);
    else
        return false;
}

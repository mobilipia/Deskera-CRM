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

function htmlEncode(value){
    	return !value ? value : String(value).replace(/&/g, "&amp;").replace(/>/g, "&gt;").replace(/</g, "&lt;").replace(/"/g, "&quot;");
}

function ScriptStripper(str)
{
	var newstr=str.replace(/<\/?[^>]+>/gi, "");
	return newstr;
}

function stripTags(str)
{
	var newstr=str.replace(/(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/ig, "");
	return newstr;
}

function isHTMLString(val){
	var patt=new RegExp(/<\/?[^>]+>/gi);
	if(patt.test(val))
		return true;
	else
		return false;
}

function commentHTMLCheck(){
	var str=document.forms["addcommentform"]["addcomment"].value;
	if(isHTMLString(str)){
		document.forms["addcommentform"]["addcomment"].value=htmlEncode(ScriptStripper(stripTags(str)));
		alert("No HTML script or tag allowed");
		return false;
	}
	if(str==""){
		alert("Comment should not be empty");
		return false;
	}
	else	
		return true;
	
}

function validatenewcaseForm()
{
	var subject=document.forms["addnewcase"]["subject"].value;
	var description=document.forms["addnewcase"]["description"].value;
	var fi = document.getElementById("attachment");
	var max_size=10485760; //10mb
	if (subject==null || subject.trim()=="")
	{
		alert("Subject must be filled out or valid input");
		return false;
	}
	if(isHTMLString(subject) || isHTMLString(description)){
		if(isHTMLString(subject)){
			document.forms["addnewcase"]["subject"].value=htmlEncode(ScriptStripper(stripTags(subject))).trim();
			document.forms["addnewcase"]["subject"].focus();
		}
		if(isHTMLString(description)){
			document.forms["addnewcase"]["description"].value=htmlEncode(ScriptStripper(stripTags(description))).trim();
			document.forms["addnewcase"]["subject"].focus();
		}
		alert("No HTML script or tag allowed");
	return false;
	}
	if(subject.length>100 || description.length>250){
		if(subject.length>100){
			alert("Subject length should not be more than 100 characters");
		}
		if(description.length>250){
			alert("Description length should not be more than 250 characters");
		}
		return false;
	}
	else if (fi.files.length != 0 && fi.files[0].size > max_size)//less than or equal to 10 mb
	{
		alert("File Size is Exceeding.\nPlease attach file upto 10 mb only.");
		return false;
	}
	return true;
}

function validatechangepasswordForm(){
	if(!confirmpasswordval("newpass","newpassagain"))
		return false;
	if(!checkvalidpassword("newpass","curpass"))
		return false;
	return true;
}

function show_upload_win(ob){
	if(ob=='show')
		document.getElementById('uploadWin').style.display='block';
	else
		document.getElementById('uploadWin').style.display='none';
}


function hideError(el_id){
	document.getElementById(el_id).style.display='none';
}


function checkvalidpassword(new_p,cur_p){
	var curpass=document.getElementById(cur_p).value;
	var new_pass=document.getElementById(new_p).value;
	if(new_pass=="" || curpass =="" || ( new_pass=="" && curpass=="")){
		alert("Password should not be empty");
		return false;
	}
	if(curpass==new_pass){
		alert("Current and new password should not be same");
		return false;
	}
	if(new_pass.length<4 && new_pass.length>0){
		alert("Password should be minimum 4 letters");
		return false;
	}
	return true;
}

function confirmpasswordval(np1,np2){
	var p1=document.getElementById(np1).value;
	var p2=document.getElementById(np2).value;
	if(p1!=p2){
		alert("The passwords you entered do not match");
		document.getElementById("newpass").value="";
		document.getElementById("newpassagain").value="";
	return false;
	}
	return true;
}

function clearAll(txt1,txt2,txt3){
	
	document.getElementById(txt1).value="";
	document.getElementById(txt2).value="";
	document.getElementById(txt3).value="";
}
function validateFilesize()
{
	var fi = document.getElementById("attachment");
	var max_size=10485760; //10mb
	if(fi.value==null || fi.value==""){
		alert("Value must be filled out");
		return false;
	}
	else if (fi.value != null && fi.files[0].size > max_size)//less than or equal to 10 mb
  	{
  		alert("File Size is Exceeding.\nPlease attach file upto 10 mb only.");
  		return false;
  	}
	else
  		return true;
}

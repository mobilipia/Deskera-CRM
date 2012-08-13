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
var http = getHTTPObject();

var LOGIN_PREFIX = "remoteapi.jsp";

var NORMAL_STATE = 4;

function getHTTPObject(){
    var http_object;
    if (!http_object && typeof XMLHttpRequest != 'undefined') {
        try {
            http_object = new XMLHttpRequest();
        }
        catch (e) {
            http_object = false;
        }
    }
    return http_object;
}

function trimStr(str){
    return str.replace(/^\s*|\s*$/g, '');
}

function testFunction(action){
    var p = "action=" + action + "&data=" + getTestParam(action);
    http.open('POST', LOGIN_PREFIX, true);
    http.setRequestHeader("Content-length", p.length);
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    http.onreadystatechange = handleResponse;
    http.send(p);
}

function getTestParam(action){
    var param = "{}";
    var element="";
    var regEx = /\s/g;
    var email_check=document.apiform.email_check[0].checked;
    var commit_check=document.apiform.commit_check[0].checked;
    var element1="",element2="",element3="",element4="",element5="",element6="",element7="",element8="";
    switch(action) {
        case 0: //Company Exist
            element = document.getElementById('C_E_companyCheck').value;
           if(!element.replace(regEx,"") == "") {
                param = '{test:true,companyid:"'+element+'"}';
            }
            break;

        case 1: //User Exist
            element = document.getElementById('U_E_userIdCheck').value;
            if(!element.replace(regEx,"") == ""){
                param = '{test:true,subdomain:demo,userid:"'+element+'"}';
            }else{
                element = document.getElementById('U_E_username').value;
            if(!element.replace(regEx,"") == ""){
                param = '{test:true,subdomain:demo,username:"'+element+'"}';
                }
            }
            break;

        case 2: //Create User
            element1 = document.getElementById('C_U_username').value;
            element2 = document.getElementById('C_U_email').value;
            element3 = document.getElementById('C_U_firstName').value;
            element4 = document.getElementById('C_U_lastName').value;
            element5 = document.getElementById('C_U_companyid').value;
            element6 = document.getElementById('C_U_userid').value;
            element8 = document.getElementById('C_U_password').value;
            if(!element1.replace(regEx,"")=="" && !element2.replace(regEx,"")==""
                    && !element3.replace(regEx,"")=="" && !element4.replace(regEx,"")==""
                        && !element5.replace(regEx,"")=="" && !element6.replace(regEx,"")==""
                            && !element8.replace(regEx,"")=="" )
                param = '{companyname:"'+element7+'",password:"'+element8+'" ,username:"'+element1+'",userid:"'+element6+'",fname:"'+element3+'",lname:"'+element4+'",emailid:"'+element2+'",companyid:"'+element5+'",sendmail:'+email_check+',iscommit:'+commit_check+'}';
            break;

       case 3: //Create Comapany
            element1 = document.getElementById('C_C_companyname').value;
            element2 = document.getElementById('C_C_email').value;
            element4 = document.getElementById('C_C_firstName').value;
            element5 = document.getElementById('C_C_password').value;
            element7 = document.getElementById('C_C_username').value;
            element8 = document.getElementById('C_C_companyid').value;
            element3 = document.getElementById('C_C_userid').value;
            element6 = document.getElementById('C_C_subdomain').value;
            if(!element1.replace(regEx,"")=="" && !element2.replace(regEx,"")=="" 
                &&  !element4.replace(regEx,"")=="" &&  !element5.replace(regEx,"")==""
                &&  !element7.replace(regEx,"")=="")
              param = '{companyname:"'+element1+'",companyid:"'+element8+'",emailid:"'+element2+'",fname:"'+element4+'",password:"'+element5+'",username:"'+element7+'",userid:"'+element3+'",subdomain:"'+element6+'",sendmail:"'+email_check+'",iscommit:'+commit_check+'}';
            break;

        case 4: //Delete User
            element1 = document.getElementById('D_U_userid').value;
            if(!element1.replace(regEx,"")=="") {
                 param = '{userid:"'+element1+'",iscommit:'+commit_check+'}';
            }
            break;

        case 5: //Assign Role
            element1 = document.getElementById('A_R_userid').value;
            element2 = document.getElementById('A_R_role').value;
            if(!element1.replace(regEx,"")=="")
                param = '{role:"'+element2+'",userid:"'+element1+'",iscommit:'+commit_check+'}';
            break;

        case 6: //Activate User
            element1 = document.getElementById('A_U_userid').value;
            if(!element1.replace(regEx,"")=="") {
                 param = '{userid:"'+element1+'",iscommit:'+commit_check+'}';
            }
            break;

        case 7: //Deactivate User
            element1 = document.getElementById('De_U_userid').value;
            if(!element1.replace(regEx,"")=="") {
                 param = '{userid:"'+element1+'",iscommit:'+commit_check+'}';
            }
            break;
    }
    return param;
}

function handleResponse(){
    if(http.readyState == NORMAL_STATE) {
        if(http.responseText && http.responseText.length > 0) {
            var results = eval("(" + trimStr(http.responseText) + ")");
            var dom = "";
            var responseMessage = "";
            switch(results.action){
                case '0':
                    dom = document.getElementById("companyCheck_result");
                    break;
                case '1':
                    dom = document.getElementById("userCheck_result");
                    break;
                case '2':
                    dom = document.getElementById("userCreate_result");
                    break;
                case '3':
                    dom = document.getElementById("companyCreate_result");
                    break;
                case '4':
                    dom = document.getElementById("deleteUser_result");
                    break;
                case '5':
                    dom = document.getElementById("assignRole_result");
                    break;
                case '6':
                    dom = document.getElementById("activateUser_result");
                    break;
                case '7':
                    dom = document.getElementById("deactivateUser_result");
                    break;
            }
            if(dom !== undefined){
                if(results.success) {
                    switch(results.infocode){
                    case "m01":
                        responseMessage = "Company exists.";
                        break;
                    case "m02":
                        responseMessage = "Company doesn't exist.";
                        break;
                    case "m03":
                        responseMessage = "User exists.";
                        break;
                    case "m04":
                        responseMessage = "User doesn't exist.";
                        break;
                    case "m05":
                        responseMessage = "User created successfully.";
                        break;
                    case "m06":
                        responseMessage = "Company created successfully.";
                        break;
                    case "m07":
                        responseMessage = "User deleted successfully.";
                        break;
                    case "m08":
                        responseMessage = "Role assigned successfully.";
                        break;
                    case "m09":
                        responseMessage = "User Activated Successfully.";
                        break;
                    case "m10":
                        responseMessage = "User Deactivated Successfully.";
                        break;
                    }
                    dom.innerHTML = responseMessage;
                } else {
                    switch(results.errorcode){
                        case "e01":
                            responseMessage = "Insufficient data.";
                            break;
                        case "e02":
                            responseMessage = "Error connecting to server.";
                            break;
                        case "e03":
                            responseMessage = "User with same username already exists.";
                            break;
                        case "e04":
                            responseMessage = "Company does not exist.";
                            break;
                        case "e05":
                            responseMessage = "Error while sending mail.";
                            break;
                        case "e06":
                            responseMessage = "User doesn't exist.";
                            break;
                        case "e07":
                            responseMessage = "User Id Already present.";
                            break;
                        case "e08":
                            responseMessage = "Company Id Already present.";
                            break;
                        case "e09":
                            responseMessage = "Email-ID already exists.";
                            break;
                        default:
                           if(results.errormsg!=null)
                                responseMessage = results.errormsg;
                            break;
                    }
                    dom.innerHTML = responseMessage;
               }
            }
        }
    }
}

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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title id="Deskeracrmtitle">CRM</title>
		<script type="text/javascript">
		/*<![CDATA[*/
			function _r(url){ window.top.location.href = url;}
		/*]]>*/
		</script>
<!-- css -->
		<link rel="stylesheet" type="text/css" href="http://apps.deskera.com/lib/resources/css/wtf-all.css">
		<link rel="stylesheet" type="text/css" href="../../style/crm.css?v=3"/>
	<!--[if lte IE 6]>
		<link rel="stylesheet" type="text/css" href="../../style/ielte6hax.css" />
	<![endif]-->
	 <!--[if IE 7]>
            <link rel="stylesheet" type="text/css" href="../../style/ie7hax.css" />
    <![endif]-->
    <!--[if gte IE 8]>
            <link rel="stylesheet" type="text/css" href="../../style/ie8hax.css" />
    <![endif]-->
<!-- /css -->
		<link rel="shortcut icon" href="../../images/deskera/deskera.png"/>
	</head>
	<body>
		<div id="loading-mask" style="width:100%;height:100%;background:#c3daf9;position:absolute;z-index:20000;left:0;top:0;">&#160;</div>
		<div id="loading">
			<div class="loading-indicator"><img src="../../images/loading.gif" style="width:16px;height:16px; vertical-align:middle" alt="Loading" />&#160;Loading...</div>
		</div>
<!-- js -->
		<script type="text/javascript" src="http://apps.deskera.com/lib/adapter/wtf/wtf-base.js"></script>
		<script type="text/javascript" src="http://apps.deskera.com/lib/wtf-all.js"></script>
		<script type="text/javascript" src="crm/common/wtf-lang-locale.js"></script>
        <script type="text/javascript" src="crm/common/msgs/messages.js"></script>
        <script type="text/javascript" src="../../scripts/crm.js?v=3"></script>
		
		<script type="text/javascript">
		/*<![CDATA[*/
			PostProcessLoad = function(){
				setTimeout(function(){Wtf.get('loading').remove(); Wtf.get('loading-mask').fadeOut({remove: true});}, 250);
				Wtf.EventManager.un(window, "load", PostProcessLoad);
			}
			Wtf.EventManager.on(window, "load", PostProcessLoad);
		/*]]>*/
		</script>
<!-- /js -->
<!-- html -->
		<div id="header" style="position: relative;">
			<img id="companyLogo" src="http://apps.deskera.com/b/<%=com.krawler.common.util.URLUtil.getDomainName(request)%>/images/store/?company=true" alt="logo"/>
                        <img src="../../images/crm-right-logo.gif" alt="crm" style="float:left;margin-left:4px;margin-top:1px;" />
			<div class="userinfo">
				<span id="whoami"></span><br /><a href="#" onclick="signOut('signout');"><script>document.write(WtfGlobal.getLocaleText("crm.dashboard.signout"));</script></a>&nbsp;&nbsp;<a href="#" onclick="showPersonProfile();" wtf:qtip='Manage your personal details and settings.'><script>document.write(WtfGlobal.getLocaleText("crm.dashboard.myaccount"));</script></a>&nbsp;&nbsp;<a href="#" onclick="showPersnProfile1();"wtf:qtip='Change your password.'><script>document.write(WtfGlobal.getLocaleText("crm.dashboard.changepassword"));</script></a>&nbsp;&nbsp;<a href="#" onclick="loadMailPage();" wtf:qtip='Get instant access to your Inbox here.'><script>document.write(WtfGlobal.getLocaleText("crm.dashboard.mymails"));</script></a>&nbsp;&nbsp;<a href="#" onclick="loadCalTab();" wtf:qtip='Get instant access to your Calendar here.'><script>document.write(WtfGlobal.getLocaleText("crm.dashboard.calendar"));</script></a>&nbsp;&nbsp;<a href="#"  id="organisationlink" onclick="loadOrganizationPage();" wtf:qtip='Effortlessly create an organization chart to clearly identify user hierarchy levels in the organization.'><script>document.write(WtfGlobal.getLocaleText("crm.dashboard.myorganization"));</script></a>
            </div>
			<div id="serchForIco"></div>
            <div id="fbLikeButton"><iframe src="https://www.facebook.com/plugins/like.php?app_id=142534032495014&amp;href=www.deskera.com&amp;send=false&amp;layout=button_count&amp;width=225&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=21" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:47px; height:21px;" allowTransparency="true"></iframe></div>
			<div id="searchBar"></div>
			<div id="navareaaccounts"></div>
			<div id="shortcuts" class="shortcuts">
			</div>
		</div>
		<div id='centerdiv'></div>
        <div id="fcue-360-mask" class="wtf-el-mask" style="display:none;z-index:1999999;opacity:0.3;">&nbsp;</div>
		<div style="display:none;">
			<iframe id="downloadframe"></iframe>
		</div>
                <input id="cursor_bin" type="text" style="display:none;"/>
<!-- /html -->
	</body>
</html>

--------------To include this module in any project -------------
 <value>/com/krawler/esp/hibernate/impl/mb_configmaster.hbm.xml</value>
include these mapping
    for hibernate.cfg.xml -

            <!-- module builder mapping files -->
            <mapping resource="com/krawler/esp/hibernate/impl/comments.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_btnpermmap.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_buttonConf.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_comboFilterConfig.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_configmaster.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_configmasterdata.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_dashboard.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_dashlinks.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_dashportlet.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_docs.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_docsmap.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_forms.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_gridconfig.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_linkgroup.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_moduleConfigMap.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_modulegr.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_permactions.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_permgrmaster.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_permmaster.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_processChart.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_reportlist.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_rolegrmaster.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_rolemaster.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_roleperm.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/mb_stdConfigs.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/pm_conditionMaster.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/pm_derivationmaster.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/pm_taskderivationmap.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/pm_taskmaster.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/prereq.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/prereqgroup.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/prereqgroupmap.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/prereqmap.hbm.xml"/>
            <mapping resource="com/krawler/esp/hibernate/impl/renderer.hbm.xml"/>

    or for applicationContaxt.xml -

  <!-- module builder mapping files -->
            <value>/com/krawler/esp/hibernate/impl/comments.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_btnpermmap.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_buttonConf.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_comboFilterConfig.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_configmaster.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_configmasterdata.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_dashboard.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_dashlinks.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_dashportlet.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_docs.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_docsmap.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_forms.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_gridconfig.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_linkgroup.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_moduleConfigMap.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_modulegr.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_permactions.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_permgrmaster.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_permmaster.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_processChart.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_reportlist.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_rolegrmaster.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_rolemaster.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/userrolemapping.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_roleperm.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_stdConfigs.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/pm_conditionMaster.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/pm_derivationmaster.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/pm_taskderivationmap.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/pm_taskmaster.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/prereq.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/prereqgroup.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/prereqgroupmap.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/prereqmap.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/renderer.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/pm_triggertype.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/pm_derivationtriggermap.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/pm_tasksteptriggermap.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/pm_taskstepmap.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/pm_triggermaster.hbm.xml</value>
            <value>/com/krawler/esp/hibernate/impl/mb_configmaster.hbm.xml</value>

In WtfMain-ex.js
-------------
addToXCuts("#", "Modules","loadModulePage()",'0',"Configure modules settings.");
addToXCuts("#", "Master Config","loadMastreConfigPage()",'0',"Configure master config settings.");
(add the above line in top links generator function)

function loadModulePage(){
    mainPanel.loadTab("../../modulesHome.html","   allModules","Module Builder" ,"navareadashboard",Wtf.etype.modulebuilder);
}

function loadMastreConfigPage(){
    mainPanel.loadTab("../../configMaster.html","   configmaster","Master Config" ,"navareadashboard",Wtf.etype.modulebuilder);
}

In WtfSettings.js
-----------------
    mbuild:"workflow/mbuild/"
    rbuild:"workflow/rbuild/"
    mcombo:"workflow/mcombo/"

(add these line in 'Wtf.req' object)

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
            strobj = ["Success", "Module Configuration deleted successfully"
];
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
var gridConfig = false;
==========================================================

In bean definitions xml(dispatcher-servlet.xml) file
----------------------------

    <bean id="reportBuilderDAO" class="com.krawler.formbuilder.servlet.ReportBuilderDaoImpl">
        <property name="sessionFactory" ref="mySessionFactory" />
        <property name="sessionHandlerImpl" ref="sessionHandlerdao" />
    </bean>
    <bean id="moduleBuilderDAO" class="com.krawler.formbuilder.servlet.ModuleBuilderDaoImpl">
        <property name="reportDao" ref="reportBuilderDAO" />
        <property name="sessionFactory" ref="mySessionFactory" />
        <property name="sessionHandlerImpl" ref="sessionHandlerdao" />
    </bean>
    <bean id="masterComboDAO" class="com.krawler.formbuilder.servlet.MasterComboDaoImpl">
        <property name="sessionFactory" ref="mySessionFactory" />
    </bean>
    <bean id="masterComboController" class="com.krawler.formbuilder.servlet.MasterComboController">
        <property name="masterComboDao" ref="masterComboDAO" />
    </bean>
    <bean id="moduleBuilderController" class="com.krawler.formbuilder.servlet.ModuleBuilderController">
        <property name="moduleDao" ref="moduleBuilderDAO" />
    </bean>
    <bean id="reportBuilderController" class="com.krawler.formbuilder.servlet.ReportBuilderController">
        <property name="reportDao" ref="reportBuilderDAO" />
    </bean>
    <bean id="urlMapping" class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
        <property name="mappings">
            <props>
                <prop key="/mbuild/*.jsp">moduleBuilderController</prop>
                <prop key="/rbuild/*.jsp">reportBuilderController</prop>
                <prop key="/mcombo/*.jsp">masterComboController</prop>
            </props>
        </property>
    </bean>

changes in Wtfkwljsonreader.

#23 - var json = response.responseText.trim();
    + var json = response.responseText;
                this.responseText = response.responseText;

#30 -  if(typeof(o.data) =="string" )
    +  o = o.data;
#31 - o = eval('(' + o.data + ')');
    + }
#35 - o.metaData
    + o.data.metaData
#37 - o = o.data;
    + this.meta = o.data.metaData;


/*inside Wtf.MainEx.js*/


Wtf.realroles = [];
Wtf.rolesets = [];
Wtf.roleperms =  [];

function SetModulePermissions(loginid) {
    Wtf.Ajax.requestEx({
        url: Wtf.req.accessR+'accessRight.do',
        method:'post',
        params: {
            action : 23,
            userid : loginid
        }
        },
        this,
        function(results, responseText) {
            Wtf.realroles = [];
            Wtf.rolesets = [];
            Wtf.roleperms =  [];

            var o = results.roleperms;
            var oS = "[";
            if(o && o.length > 0){
                    for( i = 0; i <  o.length; i++){
                            if(i == 0)
                                    oS += ('"' + o[i].rolegroupid + '"');
                            else
                                    oS += (',"' + o[i].rolegroupid + '"');
                    }
            }
            oS += "]";
//            SetCookie("perms", oS);
            if(oS&&oS!="") {
                Wtf.roleperms = eval("(" + oS + ")");
            }
            var p = results.realroles;
            var oP = "[";
            if(p && p.length > 0){
                    for( j = 0; j <  p.length; j++){
                            if(j == 0)
                                    oP += ('"' + p[j].val + '"');
                            else
                                    oP += (',"' + p[j].val + '"');
                    }
            }
            oP += "]";
            if(oP&&oP!="") {
                Wtf.realroles = eval("(" + oP + ")");
            }
//            SetCookie("realroles", oP);

            Wtf.each(results.roleperms, function(perm){
                Wtf.roleperms.push(perm.rolegroupid);
                Wtf.each( perm.roleset , function(rset){
                        Wtf.rolesets[rset.roleid] = Wtf.rolesets[rset.roleid] || [];
                        Wtf.rolesets[rset.roleid][rset.permgrid] = Wtf.rolesets[rset.roleid][rset.permgrid] || {};
                        (Wtf.rolesets[rset.roleid][rset.permgrid]).pE = rset.permvaledit;
                        (Wtf.rolesets[rset.roleid][rset.permgrid]).pV = rset.permvalview;
                });
            }, this);
        },
        function() {
        }
    );
}
function getCookie(c_name){
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
}

function checktabperms(permgrid,perm){
    var permedit = 0;
    var permview = 0;
    var result = "";
    for(var ctr=0;ctr<Wtf.realroles.length;ctr++){
        if(Wtf.rolesets[Wtf.realroles[ctr]]!=undefined&&Wtf.rolesets[Wtf.realroles[ctr]][permgrid]!=undefined){
            permedit = (Wtf.rolesets[Wtf.realroles[ctr]][permgrid]).pE;
            permview = (Wtf.rolesets[Wtf.realroles[ctr]][permgrid]).pV;
        }
        else{
            permedit = 0;
            permview = 0;
        }

        if(permview & Math.pow(2,perm)){
            if(permedit & Math.pow(2,perm)){
                result = 'edit';
                break;
            }else{
                result = 'view';
            }
        }else{
            if(permedit & Math.pow(2,perm)){
                result = 'edit';
                break;
            }else{
                result = false;
            }
        }
    }
    return result;
}
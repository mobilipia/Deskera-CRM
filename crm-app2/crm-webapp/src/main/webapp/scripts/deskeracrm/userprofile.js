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
Wtf.userProfile=function(config)
{

	 Wtf.userProfile.superclass.constructor.call(this,config);

}
Wtf.extend( Wtf.userProfile,Wtf.Window,{

	initComponent:function(config)
	{
			 Wtf.userProfile.superclass.initComponent.call(this,config);

                    this.detailpan=new Wtf.Panel({
                                        height:100,
                                        border:false,
                                        bodyStyle:'background-color:white'


                                         });
                  this.personalPan=new Wtf.Panel({
                                    bodyStyle:'padding:50px 8px 8px 50px',
                                    border:false,
                                    layout : "form",
                                    scope:this,
                                
                                    items:[{
                                                        xtype:'panel',
                                                        border:false,
                                                        paging:false,
                                                        width:'100%',
                                                        //            id:'name_'+this.id,
                                                        autoLoad : false
                                                    },
                                                    this.fname = new Wtf.form.TextField({
                                                        fieldLabel: 'First Name ',
                                                        id:this.id+'fname',
                                                        allowBlank:false,
                                                        width:'70%'
                                                       
                                                       
                                                    }),
                                                    this.lname=new Wtf.form.TextField({
                                                        fieldLabel: 'Last Name  ',
                                                        id:this.id+'lname',
                                                        //              name:'lname',
                                                        width:'70%'
                                                    }),
                                                    this.designation=new Wtf.form.TextField({
                                                        fieldLabel: 'Designation ',
                                                        id:this.id+'designation',
                                                        //              name:'lname',
                                                        width:'70%'
                                                    }),
                                                    this.phone=new Wtf.form.TextField({
                                                        fieldLabel: 'Phone No ',
                                                        id:this.id+'phone',
                                                        //              name:'lname',
                                                        width:'70%'
                                                    }),
                                                    this.email=new Wtf.form.TextField({
                                                        fieldLabel: 'Email Id ',
                                                        id:this.id+'email',
                                                        //              name:'lname',
                                                        regex:Wtf.ValidateMailPatt,
                                                        width:'70%'
                                                    }),this.address=new Wtf.form.TextArea({
                                                        fieldLabel: 'Address ',
                                                        id:this.id+'address',
                                                        //              name:'company',
                                                        width:'70%'
                                                    })]
                                                });

                  this.additionalPan=new Wtf.Panel({
                                    bodyStyle:'padding:50px 8px 8px 50px',
                                    border:false,
                                    layout : "form",
                                    items:[{
                                                        xtype:'panel',
                                                        border:false,
                                                        paging:false,
                                                        width:'100%',
                                                        //            id:'name_'+this.id,
                                                        autoLoad : false
                                                    },this.aboutuser=new Wtf.form.TextArea({
                                                        fieldLabel: 'About me ',
                                                        id:this.id+'aboutuser',
                                                        //              name:'company',
                                                        width:'70%'
                                                    }),
                                                    this.fax=new Wtf.form.TextField({
                                                        fieldLabel: 'Fax  ',
                                                        id:this.id+'fax',
                                                        //              name:'lname',
                                                        width:'70%'
                                                    }),
                                                    this.panNo=new Wtf.form.TextField({
                                                        fieldLabel: 'PAN No ',
                                                        id:this.id+'panno',
                                                        //              name:'lname',
                                                        width:'70%'
                                                    }),
                                                    this.ssnNo=new Wtf.form.TextField({
                                                        fieldLabel: 'SSN No ',
                                                        id:this.id+'ssnno',
                                                        //              name:'lname',
                                                        width:'70%'
                                                    }),
                                                    this.otherPhone=new Wtf.form.TextField({
                                                        fieldLabel: 'Other Contact No',
                                                        id:this.id+'otherphone',
                                                        //              name:'lname',
                                                        width:'70%'
                                                    })]
                                                });

                  this.userform=new Wtf.Panel({
                                            height:500,
                                            border:false,
                                            scope:this,
                                            items:[this.tabpanel=new Wtf.TabPanel({

                                                        activeTab:0,
                                                        height:435,
                                                        scope:this,
                                                        enableTabScroll:true,
                                                        items:[{
                                                                     xtype:"panel",
                                                                     border:false,
                                                                     layout:'fit',
                                                                     title:"Personal Information",
                                                                     scope:this,
                                                                     items:[this.personalPan]
                                                                     
                                                                },{
                                                                    xtype:"panel",
                                                                    border:false,
                                                                    title:"Additional Information",
                                                                    layout:'fit',
                                                                    items:[this.additionalPan]
                                                                }]
                                                   })]
                                         });

                    this.userpan=new Wtf.Panel({
                                        height:600,
                                        border:false,
                                        layout:'border',
                                        scope:this,
                                        items:[
                                                        {
                                                            region:'north',
                                                            height:100,
                                                            items:[this.detailpan]
                                                        },
                                                        {
                                                            region:'center',
                                                            height:500,
                                                            layout:'fit',
                                                            scope:this,
                                                            items:[this.userform]

                                                                
                                                        }
                                                 ]


                                                });
                    this.userproWin=new Wtf.Window({
                        title:'Update Profile',
                        height:600,
                        width:400,
                        frame:true,
                        border:false,
                        resizable:false,
                        scope:this,
                        id:this.id+'userpro',
                       items:[this.userpan],
                        buttons:[
                                        {
                                            text:'Update',
                                            scope:this,
                                            handler:this.updateuserpro
                                        },
                                        {
                                            text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                                            scope:this,
                                            handler:function()
                                                        {
                                                            Wtf.getCmp(this.id+'userpro').destroy();
                                                        }
                                           
                                        }
                                    ]

                    })
                    this.userproWin.show();
                    this.getProfileinfo();


       

},

getProfileinfo:function()
{
          Wtf.Ajax.request({
            url: Wtf.req.base + 'crm.jsp',
            params:{
     //           jsondata:finalStr,
            //    type:this.flag,////////type for new or edit
                flag:45/////////////flag for case
              //  userid:1
            },
            scope:this,
            method:'post',
            success:function(request,res)
            {
                var responseTextt = request.responseText.trim();
                if(responseTextt != "")
                {
                    var responseJsonObj = eval('(' + responseTextt + ')');
                    if(responseJsonObj.success)
                    {


                    //    Wtf.MessageBox.alert("Success", "Successfully retreived.");
                          this.fname.setValue(responseJsonObj.data[0].fname);
                          this.lname.setValue(responseJsonObj.data[0].lname);
                          this.designation.setValue(responseJsonObj.data[0].designation);
                          this.phone.setValue(responseJsonObj.data[0].contactno);
                          this.email.setValue(responseJsonObj.data[0].emailid);
                          this.address.setValue(responseJsonObj.data[0].address);
                          this.aboutuser.setValue(responseJsonObj.data[0].aboutuser);
                          this.fax.setValue(responseJsonObj.data[0].fax);
                          this.panNo.setValue(responseJsonObj.data[0].panno);
                          this.ssnNo.setValue(responseJsonObj.data[0].ssnno);
                          this.otherPhone.setValue(responseJsonObj.data[0].altcontactno);


                    }
                }
                else{
                    Wtf.MessageBox.alert("Failed"," Failed to retreive.");
                }
            },
            failure:function()
            {
                Wtf.MessageBox.alert("Failed"," Failed to retreive.");
            }
        })
},

updateuserpro:function()
{
        var jsondata = "";
       
        jsondata+="{'fname':'" + this.fname.getValue() + "',";
        jsondata+="'lname':'" + this.lname.getValue() + "',";
        jsondata+="'emailid':'" + this.email.getValue() + "',";
        jsondata+="'address':'" + this.address.getValue() + "',";
        jsondata+="'designation':'" + this.designation.getValue() + "',";
        jsondata+="'contactno':'" + this.phone.getValue() + "',";
        jsondata+="'aboutuser':'" + this.aboutuser.getValue() + "',";
        jsondata+="'fax':'" + this.fax.getValue() + "',";
        jsondata+="'altcontactno':'" + this.otherPhone.getValue() + "',";
        jsondata+="'panno':'" + this.panNo.getValue() + "',";
        jsondata+="'ssnno':'" + this.ssnNo.getValue() + "'},";

        var trmLen = jsondata.length - 1;
        var finalStr = jsondata.substr(0,trmLen);

        Wtf.Ajax.request({
            url: Wtf.req.base + 'crm.jsp',
            params:{
                jsondata:finalStr,
                flag:44/////////////flag for case
              
            },
            scope:this,
            method:'post',
            success:function(request,res)
            {
                var responseTextt = request.responseText.trim();
                if(responseTextt != "")
                {
                    var responseJsonObj = eval('(' + responseTextt + ')');
                    if(responseJsonObj.success)
                    {
                 
                        Wtf.MessageBox.alert("Success", "Successfully Saved.");
                    }
                }
                else{
                    Wtf.MessageBox.alert("Failed","Save Failed.");
                }
            },
            failure:function()
            {
                Wtf.MessageBox.alert("Failed","Save Failed.");
            }
        })
},

    onRender:function(config)
	{

		 Wtf.userProfile.superclass.onRender.call(this,config);


	}// end of onRender


});//end of extend

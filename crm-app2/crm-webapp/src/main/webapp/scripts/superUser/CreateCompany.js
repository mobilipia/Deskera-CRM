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
   Wtf.common.CreateCompany=function(config){
  this.Form=new Wtf.form.FormPanel({
        baseParams:{mode:1},
        url: 'signup.jsp',
        border:false,
        region:'center',
        style: "padding-Left:10px;padding-top:10px;padding-right: 10px;",
        layout:'form',
        items:[{
                        xtype:"textfield",
                        name:"fname",
                        width:160,
                        allowBlank:false,
                        fieldLabel:"Name"
                    },{
                        xtype:"textarea",
                        name:"address",
                        width:160,
                        allowBlank:false,
                        fieldLabel:"Address"
                    },{
                        xtype:"textfield",
                        name:"e",
                        width:160,
                        allowBlank:false,
                        fieldLabel:"E Mail",
                        regex:Wtf.ValidateMailPatt
                    },{
                        xtype:"textfield",
                        name:"u",
                        width:160,
                        allowBlank:false,
                        fieldLabel:"User Name"
                    },this.pass=new Wtf.form.TextField({
                        xtype:"textfield",
            name:"pass",
                        allowBlank:false,
                        inputType:"password",
                        width:160,
            fieldLabel:"Password",
            id:'companyadminpass',
            vtype:"password"
//            listeners:{
//                                'change':{
//                                    fn:function(val){
//                                        if(val.length>0)
//                                            this.hiddenPas.setValue(hex_sha1(val));
//                                    }
//                                }
//                            }
                    }),this.repass=new Wtf.form.TextField({
                        xtype:"textfield",
                        name:"newpassword2",
                        width:160,
                        allowBlank:false,
            fieldLabel:"Reenter password",
                        inputType:"password",
            vtype:"password",
            id:'recompanyadminpass',
            initialPassField: 'companyadminpass'
                    }),{
                        xtype:"textfield",
                        name:"c",
                        width:160,
                        allowBlank:false,
                        fieldLabel:"Company Name"
                    },{
            xtype:"hidden",
                        name:"cdomain",
                        width:160,
                        allowBlank:false,
            hideLabel:true,
            hidden:true,
            value:"demo"
        },this.hiddenPass=new Wtf.form.Hidden({
            xtype:"hidden",
            name:"p",
            hideLabel:true
        })]
    });
    Wtf.apply(this,{
        layout : "fit",
        iconCls:"deskeralogo",
        items:[{
            border:false,
            layout:'border',
                    iconCls:"deskeralogo",
                    items:[ {
                                region: 'north',
                                height: 68,
                                border: false,
                                bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
                                html: getTopHtml('Create Company','Create Company')
                            }, {
                                region: 'center',
                                border: false,
                                bodyStyle: 'background:#f1f1f1;font-size:10px;',
                                layout: 'fit',
            items:[this.Form]
                            }
                    ]
        }],
          buttons:[{
            text:'Create',
            scope:this,
            handler:this.saveReceipt.createDelegate(this)
        },{
            text:'Close',
            scope:this,
            handler:function(){this.close()}
        }]
    },config)

    this.pass.on('change',this.enablePwd,this);
    this.repass.on('change',this.checkPwd,this);
    Wtf.common.CreateCompany.superclass.constructor.call(this,config);
        this.addEvents({
            'update':true
        });
}
  Wtf.extend(Wtf.common.CreateCompany,Wtf.Window,{
    enablePwd:function(a,val){
         if(val.length<4)
              Wtf.Msg.show({
                                                title:'Error',
                msg: 'Password should be more than four character',
                                                animEl: 'elId',
                                                icon: Wtf.MessageBox.ERROR
                                        });
    },
    checkPwd:function(a,val){
         if(val!=this.pass.getValue()){
                Wtf.Msg.show({
                        title:'Error',
                        msg: 'Enter Same Password',
                        buttons: Wtf.Msg.OK,
                        animEl: 'elId',
                        icon: Wtf.MessageBox.ERROR
                });a.setValue("");
          }
          if(val.length>0)
              this.hiddenPass.setValue(hex_sha1(val));
    },
     ShowCheckDetails:function(combo,rec){
         if(rec.get('requiredetail')){
            this.ReceiptSouthForm.show() ;
            this.ReceiptSouthForm.doLayout();
        }
        else{
            this.ReceiptSouthForm.hide();
            this.bank.setValue("");
            this.CheckNo.setValue("");
            this.Description.setValue("");
            this.Account.setValue("");
        }
    },
    GetRecNo:function(){
        return(this.recInitial.getValue()+this.recNo.getValue())
    },
    setbalanceDue:function(){
        this.BalanceDue.setValue(this.Receivable.getValue()-this.Received.getValue());
    },
    saveReceipt:function(){ 
////       var rec=this.Form.getForm().getValues();

////        //rec.newpassword2=null;
////
////
////       rec.mode=1;
////        Wtf.MessageBox.confirm("Create Company", "Are you sure to create company?",function(btn){
////            if(btn!="yes") return;
////            Wtf.Ajax.requestEx({
////                url: 'signup.jsp',
////                params: rec
////         },this,this.genSuccessResponse,this.genFailureResponse);
////        },this);

       //  if(this.pass.getValue().length>0)rec.p=hex_sha1(this.pass.getValue());
       if(!this.Form.getForm().isValid())return ;
         this.Form.getForm().submit({
                waitMsg:'Saving  company information ',
                success:function(f,a){this.genSuccessResponse(eval('('+a.response.responseText+')'))},
                failure:function(f,a){this.genFailureResponse(eval('('+a.response.responseText+')'))},
                scope:this
            });




    },
    genSuccessResponse:function(response){

        if(response.data=="msg:{succcess: true}"){
            Wtf.Msg.alert('create company',"Company created");
            this.fireEvent('update',this);
            this.Form.getForm().reset();
            this.close();
            if(this.aLoad){this.ds.load();}
            bHasChanged = true;                        
        }else{
            if(response.error=="msg:{companyname failure}"){
                Wtf.Msg.alert('create company',"Company name already exists");
            }else if(response.error=="msg:{userid failure}"){
                Wtf.Msg.alert('create company',"User name already exists");
        }
        }
            //this.close();
    }, 
    genFailureResponse:function(response){
       var msg="Failed to make connection with Web Server ( Connection lost )";
    //   if(response.data.msg)msg=response.data.msg;
       bHasChanged = true;
       Wtf.Msg.alert('create company',msg);
       this.close();
       if(this.aLoad){this.ds.load();}
    }
});


/*   var rec=this.Form.getForm().getValues();
        if(this.pass.getValue().length>0)rec.p=hex_sha1(this.pass.getValue());
        //rec.newpassword2=null;
        rec.mode=1;
        Wtf.MessageBox.confirm("Create Company", "Are you sure to create company?",function(btn){
            if(btn!="yes") return;
            Wtf.Ajax.requestEx({
                url: 'signup.jsp',
                params: rec
         },this,this.genSuccessResponse,this.genFailureResponse);
        },this);
        */

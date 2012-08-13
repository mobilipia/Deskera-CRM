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
package client;

import com.krawler.br.BusinessProcess;
import com.krawler.br.ProcessBag;
import com.krawler.br.ProcessException;
import com.krawler.br.SimpleProcessBag;
import com.krawler.br.decorators.json.*;
import com.krawler.br.decorators.xml.*;
import com.krawler.br.exp.Scope;
import com.krawler.br.operations.OperationDefinitionParser;
import com.krawler.br.operations.XmlOperationDefinitionParser;
import com.krawler.br.loader.KwlClassLoader;
import com.krawler.br.modules.ModuleDefinition;
import com.krawler.br.modules.ModuleDefinitionParser;
import com.krawler.br.modules.ModuleBag;
import com.krawler.br.modules.ModuleProperty;
import com.krawler.br.modules.SimpleModuleBag;
import com.krawler.br.modules.XmlModuleDefinitionParser;
import com.krawler.br.nodes.BProcess;
import com.krawler.br.nodes.NodeParser;
import com.krawler.br.nodes.exp.TextExpressionParser;
import com.krawler.br.nodes.xml.*;
import com.krawler.br.nodes.json.*;
import com.krawler.br.operations.OperationDefinition;
import com.krawler.br.operations.OperationBag;
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.operations.SimpleOperationBag;
import com.krawler.br.utils.JsonFactory;
import com.krawler.br.utils.SourceFactory;
import com.krawler.br.utils.XmlFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import junit.framework.TestCase;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class JSONLogicTest extends TestCase {
    ModuleBag mBag;
    OperationBag oBag;
    ProcessBag pBag;
    NodeParser xmlParser, jsonParser;
    SourceFactory defaultFactory;
    @Override
    protected void setUp() throws Exception {
            /**
             * Loading module definitions
             */
            // create module factory from the specified XML resource
            SimpleModuleBag mf = new SimpleModuleBag();
            // create Module definition parser for the spplied XMLFactory
            ModuleDefinitionParser mp = new XmlModuleDefinitionParser();
            // Load all Definitions into the factory by the supplied module definition parser

            Properties p = new Properties();
            p.setProperty("path", "client/trial_moduleEx.xml");
            SourceFactory src = new XmlFactory(p,mp);

            mf.load(src);

            /**
             * Loading Operation definitions
             */
            // create operation factory from the specified XML resource
            SimpleOperationBag af = new SimpleOperationBag();
            // associate module factory to the operation factory
            af.setModuleBag(mf);
            // create operation definition parser for the spplied XMLFactory
            OperationDefinitionParser ap =new XmlOperationDefinitionParser();


            // Provide all loaders available
            HashMap hm = new HashMap();
            hm.put("class",new KwlClassLoader());
            // set all loaders available with the operation factory
            af.setLoaders(hm);
            // Load all Definitions into the factory by the supplied operation definition parser
            p = new Properties();
            p.setProperty("path", "client/trial_activityEx.xml");
            src = new XmlFactory(p,ap);
            af.addHelpers();
            af.load(src);

            /**
             * Loading process bag
             */
             // create Process Bag from the specified XML resource
            SimpleProcessBag pb = new SimpleProcessBag();

            mBag = mf;
            oBag = af;
            pBag = pb;

            constructXmlParser();
            constructJsonParser();
            p = new Properties();
            p.setProperty("path", "client/trial_businesslogicEx.xml");
            this.defaultFactory = new XmlFactory(p,xmlParser);
    }

    private void constructXmlParser(){
        if(xmlParser==null){
           // build operand parser chain
            XmlExpressionParser opParser =new XmlExpressionParser(null);
            TextExpressionParser txtParser = new TextExpressionParser();
            txtParser.setModuleBag(mBag);
            opParser.setExpParser(txtParser);
            XmlDecoratorParser dp=null;
            dp=new XmlForkDecoratorParser(dp);
            dp.setOperandParser(opParser);
            dp=new XmlNextDecoratorParser(dp);
            dp.setOperandParser(opParser);
            dp=new XmlStatementDecoratorParser(dp);
            dp.setOperandParser(opParser);
            dp=new XmlConditionalDecoratorParser(dp);
            dp.setOperandParser(opParser);
            dp=new XmlRepetitiveDecoratorParser(dp);
            dp.setOperandParser(opParser);
            dp=new XmlResultDecoratorParser(dp);
            dp.setOperandParser(opParser);
            dp=new XmlArgsDecoratorParser(dp);
            dp.setOperandParser(opParser);
            /**
             * Create Node Parser chain
             */
            // create operation node parser
            XmlActivityNodeParser onp = new XmlActivityNodeParser(null);
            // associate the operation factory
            onp.setOperationBag(oBag);
            onp.setOperandParser(opParser);
            onp.setDecoratorParser(dp);
            // create elseif ladder node parser
            XmlElIfLadderNodeParser enp = new XmlElIfLadderNodeParser(onp);
            enp.setOperationBag(oBag);
            enp.setOperandParser(opParser);
            enp.setDecoratorParser(dp);
            // create switch node parser
            XmlSwitchNodeParser snp = new XmlSwitchNodeParser(enp);
            snp.setOperationBag(oBag);
            snp.setOperandParser(opParser);
            snp.setDecoratorParser(dp);
            // create process node parser
            XmlProcessNodeParser pnp = new XmlProcessNodeParser(snp);
            pnp.setOperationBag(oBag);
            pnp.setOperandParser(opParser);
            pnp.setDecoratorParser(dp);

            xmlParser = pnp;
        }
    }

    private void constructJsonParser(){
        if(jsonParser==null){
           // build operand parser chain
            JsonVariableParser vp = new JsonVariableParser(null);
            vp.setModuleBag(mBag);
            JsonExpressionParser opParser =new JsonExpressionParser(new JsonConstantParser(vp));
            opParser.setExpParser(new TextExpressionParser());
            JsonDecoratorParser dp=null;
            dp=new JsonForkDecoratorParser(dp);
            dp.setOperandParser(opParser);
            dp=new JsonNextDecoratorParser(dp);
            dp.setOperandParser(opParser);
            dp=new JsonStatementDecoratorParser(dp);
            dp.setOperandParser(opParser);
            dp=new JsonConditionalDecoratorParser(dp);
            dp.setOperandParser(opParser);
            dp=new JsonRepetitiveDecoratorParser(dp);
            dp.setOperandParser(opParser);
            dp=new JsonResultDecoratorParser(dp);
            dp.setOperandParser(opParser);
            dp=new JsonArgsDecoratorParser(dp);
            dp.setOperandParser(opParser);

            /**
             * Create Node Parser chain
             */
            // create operation node parser
            JsonActivityNodeParser onp = new JsonActivityNodeParser(null);
            // associate the operation factory
            onp.setOperationBag(oBag);
            onp.setOperandParser(opParser);
            onp.setDecoratorParser(dp);
            // create elseif ladder node parser
            JsonElIfLadderNodeParser enp = new JsonElIfLadderNodeParser(onp);
            enp.setOperationBag(oBag);
            enp.setOperandParser(opParser);
            enp.setDecoratorParser(dp);
            // create switch node parser
            JsonSwitchNodeParser snp = new JsonSwitchNodeParser(enp);
            snp.setOperationBag(oBag);
            snp.setOperandParser(opParser);
            snp.setDecoratorParser(dp);
            // create process node parser
            JsonProcessNodeParser pnp = new JsonProcessNodeParser(snp);
            pnp.setOperationBag(oBag);
            pnp.setOperandParser(opParser);
            pnp.setDecoratorParser(dp);

            jsonParser = pnp;
        }
    }
    public void testGetProcess() {
//        try {
            // fetch business process by providing head of parser chain and the process id
            /**
             * if the process exists, then it will be created, and if it has any subsequent nodes then those nodes will be
             * created by recursive parsing. for creating any operation, the operation definitions should be present
             */
//
//            HashMap prm=new HashMap();
//            Properties pr = new Properties();
//            pr.setProperty("json", "{'vkg':{'type':'process','init':'a','localvars':[{'name':'y',module:'int',value:{name:'vishnu'}}],'invars':[{'name':'x',module:'int',multi:'list'}],'outvar':{name:'asfa',module:'string'},'nodelist':[{'id':'a','invoke':'add','next':'b','fork':'','condition':'','output':'','variables':[{'name':'a','value':{'type':'var','disabled':false,'detail':{'path':'/root/current/x/a','indices':{'0':[{'type':'var','detail':{'path':'/root/current/a/b/c'}}]}}}},{'name':'b','value':{'type':'const','disabled':false,'detail':{'type':'int','val':'5'}}}]},{'id':'b','invoke':'add','next':'c','fork':'','condition':'','variables':[{'name':'a','value':{'type':'const','disabled':false,'detail':{'type':'int','val':'5'}}},{'name':'b','value':{'type':'const','disabled':false,'detail':{'type':'int','val':'5'}}}]},{'id':'c','invoke':'add','next':'','fork':'','condition':{'type':'expr','disabled':true,'detail':{'left':{'type':'var','disabled':false,'detail':{'path':'/root/current/b'}},'operator':'gt','right':{'type':'const','disabled':false,'detail':{'type':'int','val':'5'}}}},'variables':[{'name':'a','value':{'type':'const','disabled':false,'detail':{'type':'int','val':'5'}}},{'name':'b','value':{'type':'const','disabled':false,'detail':{'type':'int','val':'15'}}}]}]}}");
//            SourceFactory src = new JsonFactory(pr,jsonParser,defaultFactory);
//            BusinessProcess p= pBag.getProcess(src,"vkg");
//            // create hashmap for the input parameters of process
//            HashMap param = new HashMap();
//            param.put("a",2);
        // execute the process by supplying the parameter
        /**
         * the execution of the process will begin from its init node.
         * and if the node has any fork or next type node then they will
         * be executed accordingly.
         */
        // p.execute(param);
//        } catch (ProcessException ex) {
//            ex.printStackTrace();
//            fail(ex.getMessage());
//        }
    }

    public void testSaveProcess() {
        try {
            // fetch business process by providing head of parser chain and the process id
            /**
             * if the process exists, then it will be created, and if it has any subsequent nodes then those nodes will be
             * created by recursive parsing. for creating any operation, the operation definitions should be present
             */

            HashMap prm=new HashMap();
            Properties pr = new Properties();
            pr.setProperty("json", "{'vkg':{'type':'process','init':'a','localvars':[{'name':'y',module:'int',value:{name:'vishnu'}}],'invars':[{'name':'x',module:'int',multi:'list'}],'outvar':{name:'asfa'},'nodelist':[{id:'testnode1',invoke:'getProducts',output:{detail:'a'},variables:[{name:'aa',type:'expression',detail:'aaa=a+3;'}],prestatements:[{type:'expression',detail:'y=3+1;y=2+3;'}],poststatements:[{type:'expression',detail:'y=2+1;y=2+3;'}],repeat:[{maxlimit:300,detail:'a+b=2',currentindex:{detail:'y'},currentelement:{detail:'y[1]'}}],condition:{type:'expression',detail:'a+b==1'}}]}}");
            SourceFactory src = new JsonFactory(pr,jsonParser,defaultFactory);
            BusinessProcess p= pBag.getProcess(src,"vkg");
            pr = new Properties();
            pr.setProperty("path", "client/demo_businesslogicEx.xml");
            SourceFactory src1 = new XmlFactory(pr,xmlParser,defaultFactory);
            pBag.addProcess(src1, p);
            src1.save();
        } catch (ProcessException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    public void testConvertProcess() {
        try {
            // fetch business process by providing head of parser chain and the process id
            /**
             * if the process exists, then it will be created, and if it has any subsequent nodes then those nodes will be
             * created by recursive parsing. for creating any operation, the operation definitions should be present
             */

            HashMap prm=new HashMap();
            Properties pr = new Properties();
            pr.setProperty("json", "{'vkg':{'type':'process','init':'a','localvars':[{'name':'y',module:'int',value:{name:'vishnu'}}],'invars':[{'name':'x',module:'int',multi:'list'}],'outvar':{name:'asfa'},'nodelist':[{id:'testnode1',invoke:'getProducts',output:{detail:'a'},variables:[{name:'aa',type:'expression',detail:'a+3'}],prestatements:[{type:'expression',detail:'y=3+1;y=2+3;'}],poststatements:[{type:'expression',detail:'y=2+1;y=2+3;'}],repeat:[{maxlimit:300,detail:'a+b=2',currentindex:{detail:'y'},currentelement:{detail:'y[1]'}}],condition:{type:'expression',detail:'a+b==1'}}]}}");
            JsonFactory src = new JsonFactory(pr,jsonParser,defaultFactory);
            BProcess p= (BProcess)pBag.getProcess(src,"vkg");
            //pr = new Properties();
            //pr.setProperty("path", "client/demo_businesslogicEx.xml");
            //SourceFactory src1 = new XmlFactory(pr,xmlParser,defaultFactory);
            p.setSourceid("new");
            pBag.addProcess(src, p);
            
            System.out.println(src.getJSONObject().toString(4));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    public void testGetOperationNames(){
        Iterator itr = oBag.getOperationIDs().iterator();
        while(itr.hasNext()){
            OperationDefinition oDef=oBag.getOperationDefinition((String)itr.next());
            System.err.println("Operations : "+oDef.getName());
            int count = oDef.getParameterCount();

            for (int i = 0; i < count; i++) {
                OperationParameter op = oDef.getInputParameter(i);
                System.err.println("\tParam: "+op.getName());
            }
        }
    }
    public void testGetModuleNames(){
        Iterator itr = mBag.getModuleNames().iterator();
        while(itr.hasNext()){
            ModuleDefinition mDef=mBag.getModuleDefinition((String)itr.next());
            System.err.println("Modules : "+mDef.getName());
            Iterator itr1 = mDef.getPropertyNames().iterator();
            while(itr1.hasNext()){
                ModuleProperty mp = mDef.getProperty((String)itr1.next());
                System.err.println("\tProperty: "+mp.getName());
            }
        }
    }
    public void testGetProcessesNames(){
        try {
        Properties pr = new Properties();
        pr.setProperty("json", "{process:[{id:'vkg',init:'a',nodelist:[{id:'a', invoke:'add', next:'b', fork:'', condition:'', variables:[]}, {id:'b', invoke:'add', next:'c', fork:'', condition:'', variables:[]}, {id:'c', invoke:'add', next:'', fork:'', condition:{type:'cond', disabled:true, detail:{left:{type:'var', disabled:false, detail:{val:'/root/po/b/acc/ratingid'}}, operator:'eq', right:{type:'const', disabled:false, detail:{type:'string', val:'asasa'}}}}, variables:[{name:'leadid', type:'expression', disabled:false, detail:{val:'/root/po/companyid'}}]}]}]}");
        SourceFactory src = new JsonFactory(pr,jsonParser,defaultFactory);
        Iterator itr = pBag.getProcessIDs(src).iterator();
        while(itr.hasNext()){
            System.err.println("Process : "+itr.next());
        }
        }catch(ProcessException pe){
            pe.printStackTrace();
            fail(pe.getMessage());
        }
    }
}

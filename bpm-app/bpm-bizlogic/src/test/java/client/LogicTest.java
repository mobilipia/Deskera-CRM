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
import com.krawler.br.decorators.xml.*;
import com.krawler.br.exp.Scope;
import com.krawler.br.loader.KwlClassLoader;
import com.krawler.br.modules.*;
import com.krawler.br.nodes.NodeParser;
import com.krawler.br.nodes.exp.TextExpressionParser;
import com.krawler.br.nodes.xml.*;
import com.krawler.br.operations.*;
import com.krawler.br.utils.SourceFactory;
import com.krawler.br.utils.XmlFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class LogicTest extends TestCase {
    ModuleBag mBag;
    OperationBag oBag;
    ProcessBag pBag;
    NodeParser np;
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
            // build operand parser chain
            XmlExpressionParser opParser =new XmlExpressionParser(null);
            TextExpressionParser txtParser = new TextExpressionParser();
            txtParser.setScopes(new Scope[]{getScope("s")});
            txtParser.setModuleBag(mf);
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
            // create Process Bag from the specified XML resource
            SimpleProcessBag pb = new SimpleProcessBag();

            /**
             * Create Node Parser chain
             */
            // create operation node parser
            XmlActivityNodeParser onp = new XmlActivityNodeParser(null);
            // associate the operation factory
            onp.setOperationBag(af);
            onp.setOperandParser(opParser);
            onp.setDecoratorParser(dp);
            // create elseif ladder node parser
            XmlElIfLadderNodeParser enp = new XmlElIfLadderNodeParser(onp);
            enp.setOperationBag(af);
            enp.setOperandParser(opParser);
            enp.setDecoratorParser(dp);
            // create switch node parser
            XmlSwitchNodeParser snp = new XmlSwitchNodeParser(enp);
            snp.setOperationBag(af);
            snp.setOperandParser(opParser);
            snp.setDecoratorParser(dp);
            // create process node parser
            XmlProcessNodeParser pnp = new XmlProcessNodeParser(snp);
            pnp.setOperationBag(af);
            pnp.setOperandParser(opParser);
            pnp.setDecoratorParser(dp);
            mBag = mf;
            oBag = af;
            pBag = pb;
            np = pnp;
    }
    public void testExecuteProcess() {
        try {
            // fetch business process by providing head of parser chain and the process id
            /**
             * if the process exists, then it will be created, and if it has any subsequent nodes then those nodes will be
             * created by recursive parsing. for creating any operation, the operation definitions should be present
             */

            HashMap prm=new HashMap();
            Properties pr = new Properties();
            pr.setProperty("path", "client/trial_businesslogicEx.xml");
            SourceFactory src = new XmlFactory(pr,np);
            BusinessProcess p= pBag.getProcess(src,"table");
            // create hashmap for the input parameters of process
            HashMap param = new HashMap();
            param.put("a",2);
            param.put("b",4);
            // execute the process by supplying the parameter
            /**
             * the execution of the process will begin from its init node.
             * and if the node has any fork or next type node then they will
             * be executed accordingly.
             */
            p.execute(param);
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            fail(ex.getMessage());
        }
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
            pr.setProperty("path", "client/trial_businesslogicEx.xml");
            SourceFactory src = new XmlFactory(pr,np);
            BusinessProcess p= pBag.getProcess(src,"table");
            Properties pr1=new Properties();
            pr1.setProperty("path", "client/trial1_businesslogicEx.xml");
            SourceFactory csrc = new XmlFactory(pr1,np,src);
            pBag.addProcess(csrc, p);
            csrc.save();
        } catch (Throwable ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    private Scope getScope(String ident) {
        return new Scope() {
            private String identity;
            private HashMap hm = new HashMap();

            public Scope setScope(String identity){
                this.identity=identity;
                return this;
            }

            @Override
            public Object getScopeValue(String key) throws ProcessException {
                return hm.get(key);
            }

            @Override
            public void setScopeValue(String key, Object val) throws ProcessException {
                hm.put(key, val);
            }

            @Override
            public void removeScopeValue(String key) throws ProcessException {
                hm.remove(key);
            }

            @Override
            public String getScopeModuleName(String key) {
                Object o=hm.get(key);
                if("a".equals(key))
                    return ModuleBag.PRIMITIVE.LONG.tagName();
                else if("b".equals(key))
                    return ModuleBag.PRIMITIVE.INT.tagName();
                else if("c".equals(key))
                    return ModuleBag.PRIMITIVE.FLOAT.tagName();
                else if("t".equals(key))
                    return ModuleBag.PRIMITIVE.BOOLEAN.tagName();
                else if("name".equals(key))
                    return ModuleBag.PRIMITIVE.STRING.tagName();
                return null;
            }

            @Override
            public String getIdentity() {
                return identity;
            }
        }.setScope(ident);
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
        pr.setProperty("path", "client/trial_businesslogicEx.xml");
        SourceFactory src = new XmlFactory(pr,np);
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

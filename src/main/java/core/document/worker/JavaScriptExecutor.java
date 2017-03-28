package core.document.worker;

import java.util.Hashtable;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import loader.Loader;

import org.apache.log4j.Logger;

import action.api.ScriptApi;

public class JavaScriptExecutor extends ScriptApi {
    private static final Logger log = Logger.getLogger(JavaScriptExecutor.class);   

    String script = "";
    
    public JavaScriptExecutor(String s){
        if(s.trim().substring(0, 3).equalsIgnoreCase("js:"))
            script = s.substring(3);
        else
            script = s;
    }
    
    @Override
    public Object eval(Hashtable<String, Object> aliases) throws Exception {
    	if(script.startsWith("js_file:")){
    		script = new String(Loader.getInstanceRml().loadByName_chars(script.substring(8),true));
    	}
        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine engine = m.getEngineByName("js");
        Bindings bindings = engine.createBindings();
        bindings.putAll(aliases);
        
        try {
            return engine.eval(script, bindings);
        } catch (ScriptException e) {
            log.error("", e);
            return null; 
        }
    }
    
    
    

}

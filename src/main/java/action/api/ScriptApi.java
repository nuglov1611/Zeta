package action.api;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import core.document.worker.JavaScriptExecutor;

public abstract class ScriptApi {

	private static final Logger log = Logger.getLogger(ScriptApi.class);
	
	public static ScriptApi getAPI(String script){
		ScriptApi calc = null;
        if(script.length() >= 3 && script.trim().substring(0, 3).equalsIgnoreCase("js:")){
            calc = new JavaScriptExecutor(script); 
        }else{
            calc = new Calc(script);
        }
        return calc;
	}
    
    public abstract Object eval(Hashtable<String, Object> aliases) throws Exception;

    public static String macro(String str, Hashtable<String, Object> aliases) throws Exception {
        try {
            StringTokenizer st = new StringTokenizer("_" + str, "~");
            boolean flag = false;
            String result = "";
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (flag) {
                	ScriptApi c = ScriptApi.getAPI(s);
                    Object res = c.eval(aliases);
                    	result += res.toString();
                }
                else {
                    result += s;
                }
                flag = !flag;
            }
            result = result.substring(1);
            return result;
        }
        catch (Exception e) {
            log.error("Shit happens", e);
            return "";
        }
    }
}

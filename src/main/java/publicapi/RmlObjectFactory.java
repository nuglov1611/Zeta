package publicapi;
        
import java.util.Hashtable;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

import proguard.annotation.Keep;
import core.parser.Proper;
import core.rml.RmlObject;

/**
 * ������� ��� �������� Rml-��������
 * @author uglov
 *
 */
@Keep
public class RmlObjectFactory {
	private static final Logger log = Logger.getLogger(RmlObject.class);
	
	/**
	 * �������� RML-������� �� ��������
	 * @param objectName �������� �������
	 * @return ��������� RmlObject
	 */
	@Keep
	public static RmlObjectAPI createObject(String objectName){
		RmlObject res = null;
        try {
        	Class cl = Class.forName("core.reflection.rml." + objectName.toUpperCase());
        	res = (RmlObject) cl.newInstance();
        }
        catch (Exception e) {
        	log.error("!", e);
        	if (ZetaProperties.parser_debug > 2) {
                log.error("~rml.Parser::getContent ", e);
            }
            res = null;
        }
        return res;
	}

	
	/**
	 * �������� ���������� ��� ������� RML-�������
	 * @return ��������� RmlProperties
	 */
	@Keep
	public static RmlProperties getProperties(){
		return new Proper();
	}
	
	/**
	 * �������� ���������� ��� ������� RML-�������
	 * @param p ������� �������
	 * @return ��������� RmlProperties
	 */
	@Keep
	public static RmlProperties getProperties(Hashtable<String, Object> p){
		return new Proper(p);
	}
	
}

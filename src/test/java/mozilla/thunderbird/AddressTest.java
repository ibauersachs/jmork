package mozilla.thunderbird;

import java.util.HashMap;
import java.util.Map;

import mork.Alias;

import junit.framework.TestCase;

public abstract class AddressTest extends TestCase {
	Map<String,Alias> values;
	
	/**
	 * put the given name/value pair into the alias map
	 * @param id
	 * @param value
	 */
	public void put(String id,String value){
		put(values,id,value);
	}
	
	/**
	 * put the given name/value pair into the alias map
	 * @param values
	 * @param id
	 * @param value
	 */
	public void put(Map<String,Alias> values,String id,String value){
		values.put(id.trim(),new Alias(id,id,value,null));
	}
	
}

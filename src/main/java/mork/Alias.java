package mork;

/**
 * a single Alias
 * @author mhaller
 * @author wf
 *
 */
public class Alias {
	String refId;
	String id;
	String value;
	String valueRef;
	
	/**
	 * create an Alias
	 * @param refId
	 * @param id
	 * @param value
	 * @param valueref 
	 */
	public Alias(String refId, String id, String value, String valueref) {
		super();
		this.refId = refId;
		this.id = id;
		this.value = value;
		this.valueRef=valueref;
	}
	/**
	 * @return the valueRef
	 */
	public String getValueRef() {
		return valueRef;
	}
	/**
	 * @param valueRef the valueRef to set
	 */
	public void setValueRef(String valueRef) {
		this.valueRef = valueRef;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the refId
	 */
	public String getRefId() {
		return refId;
	}
	/**
	 * @param refId the refId to set
	 */
	public void setRefId(String refId) {
		this.refId = refId;
	}

	
}

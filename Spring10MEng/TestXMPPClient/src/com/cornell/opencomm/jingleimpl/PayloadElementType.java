package com.cornell.opencomm.jingleimpl;
import org.xmlpull.v1.XmlPullParser;

/**
 * This class models the PayloadElementType from the XEP-0166
 * 
 * @author Abhishek and Ming
 *
 */
public class PayloadElementType extends BaseElementType {

	private String attributeID = null;
	private String attributeName = null;
	private String attributeClockrate = null;
	private String attributeChannels = null;
	
	public static final String ELEMENT_PAYLOAD = "payload-type";
	public static final String ATTRIBUTE_NAME_ID = "id";
	public static final String ATTRIBUTE_NAME_NAME = "name";
	public static final String ATTRIBUTE_NAME_CLOCKRATE = "clockrate";
	public static final String ATTRIBUTE_NAME_CHANNELS = "channels";
	
	/**
	 * Constructor
	 */
	public PayloadElementType(){
		super();
	}
	
	// *******************************************GETTERS_START*****************************************
	
	public String getAttributeID() {
		return attributeID;
	}

	public String getAttributeName() {
		return attributeName;
	}
	
	public String getAttributeClockrate() {
		return attributeClockrate;
	}
	
	public String getAttributeChannels() {
		return attributeChannels;
	}
	
	//*******************************************GETTERS_END*****************************************
	
	
	// *******************************************SETTERS_START***************************************
	
	public void setAttributeID(String attributeID) {
		this.attributeID = attributeID;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public void setAttributeClockrate(String attributeClockrate) {
		this.attributeClockrate = attributeClockrate;
	}	

	public void setAttributeChannels(String attributeChannels) {
		this.attributeChannels = attributeChannels;
	}

	/**
	 * Returns the XML snippet corresponding to this element
	 * @return String containing the XML snippet corresponding to this element
	 */
	public String toXML(){
		StringBuilder buffer = new StringBuilder();
		buffer.append("<" + ELEMENT_PAYLOAD);
		
		if(isAttributeInitialized(attributeID))
			buffer.append(" " + ATTRIBUTE_NAME_ID + "=").append(getQuoted(attributeID));
		if(isAttributeInitialized(attributeName))
			buffer.append(" " + ATTRIBUTE_NAME_NAME + "=").append(getQuoted(attributeName));
		if(isAttributeInitialized(attributeClockrate))
			buffer.append(" " + ATTRIBUTE_NAME_CLOCKRATE + "=").append(getQuoted(attributeClockrate));
		if(isAttributeInitialized(attributeChannels))
			buffer.append(" " + ATTRIBUTE_NAME_CHANNELS + "=").append(getQuoted(attributeChannels));
		
		buffer.append("/>");
		
		return buffer.toString();
	}

	@Override
	public void setAttributesFromParser(XmlPullParser parser) {
		for(int i=0; i<parser.getAttributeCount(); i++){
			if(parser.getAttributeName(i).equals(ATTRIBUTE_NAME_CHANNELS)){
				setAttributeChannels(parser.getAttributeValue(i));
				continue;
			}
			if(parser.getAttributeName(i).equals(ATTRIBUTE_NAME_CLOCKRATE)){
				setAttributeClockrate(parser.getAttributeValue(i));
				continue;
			}
			if(parser.getAttributeName(i).equals(ATTRIBUTE_NAME_ID)){
				setAttributeID(parser.getAttributeValue(i));
				continue;
			}
			if(parser.getAttributeName(i).equals(ATTRIBUTE_NAME_NAME)){
				setAttributeName(parser.getAttributeValue(i));
				continue;
			}
		}		
	}
	/**
	 * This interface lists all current supported payload-type(codec) 
	 * For future development, add new supported payload-type here
	 * 
	 * @author Ming
	 * 
	 */
	public interface SupportedPayloadType{
		public static final String PAYLOAD_PCMU_NAME = "PCMU";
		public static final String PAYLOAD_PCMU_ID = "0";
		public static final String PAYLOAD_PCMA_NAME = "PCMA";
		public static final String PAYLAOD_PCMA_ID = "8";
	}
}

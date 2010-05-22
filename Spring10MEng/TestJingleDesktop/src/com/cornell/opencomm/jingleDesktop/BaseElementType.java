package com.cornell.opencomm.jingleDesktop;

import org.xmlpull.v1.XmlPullParser;

/**
 * This is a dummy base class. It has just a few methods needed by all the "real"
 * element types.
 * 
 * @author Abhishek and Ming
 *
 */
public abstract class BaseElementType {

	public static final String DOUBLE_QUOTE = "\"";
	
	/**
	 * Dummy Constructor, does nothing.
	 */
	public BaseElementType(){
		
	}
	
	/**
	 * Returns a quoted string. Primarily used by getChildElementXML
	 */
	public String getQuoted(String input){
		if(!input.startsWith(DOUBLE_QUOTE)){
			return DOUBLE_QUOTE + input + DOUBLE_QUOTE;
		} else
			return input;
	}

	/**
	 * Checks to see if the given attribute was initialized ever. If it was, then its value
	 * should go into the corresponding XML snippet.
	 * @param attribute the attribute which needs to be checked for initialization
	 * @return boolean indicating whether or not the attribute was ever initialized
	 */
	public boolean isAttributeInitialized(Object attribute){
		if(attribute instanceof Short){
			return (Short)attribute == -1 ? false : true;
		} else if(attribute instanceof String){
			String stringAttr = (String)attribute;
			return  (stringAttr == null || stringAttr.equals("")) ? false : true;
		} else if(attribute instanceof Integer){
			return (Integer)attribute == -1 ? false : true;
		} else 
			return false;
	}
	
	public abstract void setAttributesFromParser(XmlPullParser parser);
}

package com.cornell.opencomm.jingleimpl;

import org.xmlpull.v1.XmlPullParser;

/**
 * This is the base class to represent all elements as defined by XEP-0166, XEP-0167, XEP-0177
 * 
 * This contains utility functions needed by classes that represent each 
 * xml element.
 *  
 * @author Abhishek 
 * @author Ming
 */
public abstract class BaseElementType {

	public static final String DOUBLE_QUOTE = "\"";
	
	/**
	 * Constructor, does nothing.
	 */
	public BaseElementType(){
		
	}
	
	/**
	 * Returns a quoted form of the given string. Primarily used by <code>getChildElementXML</code> or
	 * <code>toXML</code> methods
	 *
	 * @param input the string to be quoted
	 * @return the quoted string
	 */
	public String getQuoted(String input){
		if(!input.startsWith(DOUBLE_QUOTE)){
			return DOUBLE_QUOTE + input + DOUBLE_QUOTE;
		} else
			return input;
	}

	/**
	 * Checks to see if the given attribute was initialized ever. If it was, then its value
	 * should go into the corresponding XML snippet that represents the element of which 
	 * this object is an attribute.
	 * 
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
	
	/**
	 * Sets the attributes of a given element if given an instance of XmlPullParser. 
	 * 
	 * @param parser the XmlPullParser instance, placed at that particular element
	 */
	public abstract void setAttributesFromParser(XmlPullParser parser);
}

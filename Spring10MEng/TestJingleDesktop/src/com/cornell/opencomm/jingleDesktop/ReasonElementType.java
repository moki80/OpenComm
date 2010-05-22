package com.cornell.opencomm.jingleDesktop;

public class ReasonElementType {

	private static final String DOUBLE_QUOTE = "\"";
	
	public static final String TYPE_ALTERNATIVE_SESSION = "alternative-session";
	public static final String TYPE_BUSY = "busy";
	public static final String TYPE_CANCEL = "cancel";
	public static final String TYPE_CONNECTIVITY_ERROR = "connectivity-error";
	public static final String TYPE_DECLINE = "declined";
	public static final String TYPE_EXPIRED = "expired";
	public static final String TYPE_FAILED_APPLICATION = "failed-application";
	public static final String TYPE_FAILED_TRANSPORT = "failed-transport";
	public static final String TYPE_GENERAL_ERROR = "general-error";
	public static final String TYPE_GONE = "gone";
	public static final String TYPE_INCOMPATIBLE_PARAMETERS = "incompatible-parameters";
	public static final String TYPE_MEDIA_ERROR = "media-error";
	public static final String TYPE_SECURITY_ERROR = "security-error";
	public static final String TYPE_SUCCESS = "success";
	public static final String TYPE_TIMEOUT = "timeout";
	public static final String TYPE_UNSUPPORTED_APPLICATIONS = "unsupported-applications";
	public static final String TYPE_UNSUPPORTED_TRANSPORTS = "unsupported-transports";
		
	private String reason = null; // will be one of the above TYPE_*
	private String text = null; // describes the reason.
	private String SID = null;
	
	public static final String ELEMENT_NAME_REASON = "reason";
	public static final String ELEMENT_NAME_SID = "sid";
	public static final String ELEMENT_NAME_TEXT = "text";
	
	public ReasonElementType(String reason, String text){
		this.reason = reason;
		this.text = text;
	}
	
	public String getReason(){
		return this.reason;
	}
	
	public void setAttributeSID(String sid){
		this.SID = sid;
	}
	
	/**
	 * Returns a quoted string. Primarily used by getChildElementXML
	 */
	private String getQuoted(String input){
		if(!input.startsWith(DOUBLE_QUOTE)){
			return DOUBLE_QUOTE + input + DOUBLE_QUOTE;
		} else
			return input;
	}
	
	public String toXML(){
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("<" + ELEMENT_NAME_REASON + ">");
		
		if(reason.equalsIgnoreCase(TYPE_ALTERNATIVE_SESSION)){
			buffer.append("<" + reason + ">");
			buffer.append("<" + ELEMENT_NAME_SID + ">");
			buffer.append(SID);
			buffer.append("</" + ELEMENT_NAME_SID + ">");
			buffer.append("</" + reason + ">");
		} else {
			if(text == null || text.equals("")){
				buffer.append("<" + reason + "/>");
			} else {
				buffer.append("<" + reason + ">");
				buffer.append("<" + ELEMENT_NAME_TEXT + ">");
				buffer.append(text);
				buffer.append("</" + ELEMENT_NAME_TEXT + ">");
				buffer.append("</" + reason + ">");
			}
		}
		
		buffer.append("</" + ELEMENT_NAME_REASON + ">");		
		
		return buffer.toString();
	}
	
	/**
	 * Checks if the reason cited in the XML is one of the legal reason types
	 * @param reason String reason whose legality is to be checked
	 * @return boolean whether reason is legal or not.
	 */
	public static boolean isValidReason(String reason){
		boolean valid = false;
		
		valid = reason.equals(TYPE_ALTERNATIVE_SESSION) ||
			reason.equals(TYPE_BUSY) ||
			reason.equals(TYPE_CANCEL) ||
			reason.equals(TYPE_CONNECTIVITY_ERROR) ||
			reason.equals(TYPE_DECLINE) ||
			reason.equals(TYPE_EXPIRED) ||
			reason.equals(TYPE_FAILED_APPLICATION) ||
			reason.equals(TYPE_FAILED_TRANSPORT) ||
			reason.equals(TYPE_GENERAL_ERROR) ||
			reason.equals(TYPE_GONE) ||
			reason.equals(TYPE_INCOMPATIBLE_PARAMETERS) ||
			reason.equals(TYPE_MEDIA_ERROR) ||
			reason.equals(TYPE_SECURITY_ERROR) ||
			reason.equals(TYPE_SUCCESS) ||
			reason.equals(TYPE_TIMEOUT) ||
			reason.equals(TYPE_UNSUPPORTED_APPLICATIONS) ||
			reason.equals(TYPE_UNSUPPORTED_TRANSPORTS);
			
		return valid;	 
	}
}

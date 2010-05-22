package com.cornell.opencomm.jingleimpl;

import java.util.UUID;
import java.util.Vector;

import org.jivesoftware.smack.packet.IQ;
import org.xmlpull.v1.XmlPullParser;

/**
 * This class models a Jingle Element as defined by XEP-0166 
 * 
 * This class is based on the earlier Jingle code being used by the network team
 * and is an attempt to make it more general.
 * 
 * @author Abhishek 
 * @author Ming
 *
 */
public class JingleIQPacket extends IQ {
	
	// All attributes of a Jingle element go below
	private String attributeAction = null;
	private String attributeInitiator = null;
	private String attributeResponder = null;
	private String attributeSID = null;
	
	// All child elements of a Jingle Element are declared below
	private Vector<ContentElementType> elementContent = null;
	private ReasonElementType elementReason = null;
	
	public static final String ATTRIBUTE_NAME_XMLNS = "xmlns";
	public static final String ATTRIBUTE_NAME_ACTION = "action";
	public static final String ATTRIBUTE_NAME_INITIATOR = "initiator";
	public static final String ATTRIBUTE_NAME_RESPONDER = "responder";
	public static final String ATTRIBUTE_NAME_SID = "sid";
	
	// Makes XML writer code more readable
	private static final String DOUBLE_QUOTE = "\"";
	public static final String NAMESPACE = "urn:xmpp:jingle:1";
	public static final String ELEMENT_NAME_JINGLE = "jingle";	
	
	// Constructors
	
	/**
	 * Constructs a general <code>JingleIQPacket</code>. The "from" and "to" fields
	 * get set later.
	 */
	public JingleIQPacket(){
		elementContent = new Vector<ContentElementType>();
		setType(IQ.Type.SET);
		setRandomAttributeSID(); // Callers can always replace this with a SID of choice
	}
	
	/**
	 * Constructor
	 * @param from the Sender
	 * @param to the Receiver
	 * @param action one of the actions defined in {@link JingleIQPacket.AttributeActionValues} interface
	 */
	public JingleIQPacket(String from, String to, String action){
		
		elementContent = new Vector<ContentElementType>();
		setRandomAttributeSID(); // Callers can always replace this with a SID of choice
		
		// JIDFrom = from;
		// JIDTo = to;
		attributeAction = action;
		
		// Set type in IQ
		setType(IQ.Type.SET);
		setFrom(from);
		setTo(to);
	}	
	
	// *******************************************GETTERS_START*****************************************
	public static String getELEMENT_NAME_JINGLE() {
		return ELEMENT_NAME_JINGLE;
	}

	public static String getNAMESPACE() {
		return NAMESPACE;
	}

	public String getAttributeAction() {
		return attributeAction;
	}

	public String getAttributeInitiator() {
		return attributeInitiator;
	}

	public String getAttributeResponder() {
		return attributeResponder;
	}

	public String getAttributeSID() {
		return attributeSID;
	}

	public Vector<ContentElementType> getElementContent() {
		return elementContent;
	}

	public ReasonElementType getElementReason() {
		return elementReason;
	}

	public String getJIDFrom() {
		return getFrom();
	}

	public String getJIDTo() {
		return getTo();
	}
	// *******************************************GETTERS_END*****************************************
	
	
	// *******************************************SETTERS_START***************************************
	public void setAttributeAction(String attribute_action) {
		attributeAction = attribute_action;
	}

	public void setAttributeInitiator(String attribute_initiator) {
		attributeInitiator = attribute_initiator;
	}

	public void setAttributeResponder(String attribute_responder) {
		attributeResponder = attribute_responder;
	}

	public void setAttributeSID(String attribute_sid) {
		attributeSID = attribute_sid;
	}
	
	public void setRandomAttributeSID(){
		attributeSID = UUID.randomUUID().toString();
	}

	public void setJIDFrom(String from) {
		// JIDFrom = from;
		setFrom(from);
	}

	public void setJIDTo(String to) {
		// JIDTo = to;
		setTo(to);
	}

	public void addContentType(ContentElementType content){
		elementContent.add(content);
	}
	
	public void setElementReason(ReasonElementType reason){
		elementReason = reason;
	}
	// *******************************************SETTERS_END***************************************
	
	/**
	 * Returns a quoted string. Primarily used by getChildElementXML
	 */
	private String getQuoted(String input){
		if(!input.startsWith(DOUBLE_QUOTE)){
			return DOUBLE_QUOTE + input + DOUBLE_QUOTE;
		} else
			return input;
	}

	@Override
	public String getChildElementXML() {
		StringBuilder buffer = new StringBuilder();		
		
		buffer.append(getJingleHeaderXML());
		
		if(elementReason != null){
			// only if elementReason is non null then the Jingle element contains a reason element
			// and no contents.
			buffer.append(elementReason.toXML());
		} else {
			for(ContentElementType content: elementContent){
				buffer.append(content.toXML());
			}
		}
		
		buffer.append("</" + ELEMENT_NAME_JINGLE + ">");
		
		return buffer.toString();
	}
	
	/**
	 * Constructs the starting tag of a Jingle element and populates the various attributes.
	 * @return a well-formed tag of type <code>XmlPullParser.START_TAG</code> containing all elements applicable to this Jingle Element
	 */
	public String getJingleHeaderXML() {
		StringBuilder header = new StringBuilder();
		
		header.append("<" + ELEMENT_NAME_JINGLE);
		
		header.append(" "+ATTRIBUTE_NAME_XMLNS+"=").append(getQuoted(NAMESPACE));
		header.append(" "+ATTRIBUTE_NAME_ACTION+"=").append(getQuoted(attributeAction));
				
		//the Initiator in this case is the JID of the SENDER
		if (this.getAttributeAction().equals(AttributeActionValues.SESSION_INITIATE)) {
			if (this.attributeInitiator == null) {			
				setAttributeInitiator(getFrom());
			}
			header.append(" "+ATTRIBUTE_NAME_INITIATOR+"=").append(getQuoted(attributeInitiator));
			if(this.attributeResponder == null){
				setAttributeResponder(getTo());
			}
			header.append(" "+ATTRIBUTE_NAME_RESPONDER+"=").append(getQuoted(attributeResponder));
		}
		// The responder in this case is the JID of the Sender
		else if (this.getAttributeAction().equals(AttributeActionValues.SESSION_ACCEPT)){
			if (this.attributeResponder == null){
				setAttributeResponder(getFrom());
			}
			if (this.attributeInitiator == null){
				setAttributeInitiator(getTo());
			}
			header.append(" "+ATTRIBUTE_NAME_INITIATOR+"=").append(getQuoted(attributeInitiator));
			header.append(" "+ATTRIBUTE_NAME_RESPONDER+"=").append(getQuoted(attributeResponder));			
		}
		
		header.append(" "+ATTRIBUTE_NAME_SID+"=").append(getQuoted(attributeSID));
		header.append(">");
		
		return header.toString();
	}
	
	/**
	 * Encapsulates all the attribute handling for a particular XML element in the class
	 * which handles the XML Element. Looks for each attribute provided by the parser and sets
	 * the classes instance variables accordingly
	 * 
	 * @param parser An instance of <code>XmlPullParser</code> which is processing a Jingle Element.
	 */
	public void setAttributesFromParser(XmlPullParser parser){
		for (int i =0; i<parser.getAttributeCount(); i++){
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_ACTION)){
				this.setAttributeAction(parser.getAttributeValue(i));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_INITIATOR)){
				this.setAttributeInitiator(parser.getAttributeValue(i));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_RESPONDER)){
				this.setAttributeResponder(parser.getAttributeValue(i));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_SID)){
				this.setAttributeSID(parser.getAttributeValue(i));
				continue;
			}
		}
	}

	/**
	 * This interface lists all possible values that the action attribute of a 
	 * Jingle Element can have.
	 * 
	 * @author Abhishek
	 *
	 */
	public interface AttributeActionValues {
		public static final String CONTENT_ACCEPT = "content-accept";
		public static final String CONTENT_ADD = "content-add";
		public static final String CONTENT_MODIFY = "content-modify";
		public static final String CONTENT_REJECT = "content-reject";
		public static final String CONTENT_REMOVE = "content-remove";
		public static final String DESCRIPTION_INFO = "description-info";
		public static final String SECURITY_INFO = "security-info";
		public static final String SESSION_ACCEPT = "session-accept";
		public static final String SESSION_INFO = "session-info";
		public static final String SESSION_INITIATE = "session-initiate";
		public static final String SESSION_TERMINATE = "session-terminate";
		public static final String TRANSPORT_ACCEPT = "transport-accept";
		public static final String TRANSPORT_INFO = "transport-info";
		public static final String TRANSPORT_REJECT = "transport-reject";
		public static final String TRANSPORT_REPLACE = "transport-replace";		
	}
	
}



package com.cornell.opencomm.jingleimpl;

import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;

/**
 * This class models a Description element as described in the XEP-0167
 * 
 * @author Abhishek
 * @author Ming
 */
public class DescriptionElementType extends BaseElementType{

	public static final String ELEMENT_DESCRIPTION = "description";
	public static final String ATTRIBUTE_NAME_XMLNS = "xmlns";
	public static final String ATTRIBUTE_NAME_MEDIA = "media";
	
	public static final String NAMESPACE_RTP_1="urn:xmpp:jingle:apps:rtp:1";
	
	
	private String attributeNamespace = NAMESPACE_RTP_1;
	private String attributeMedia = null;
	private Vector<PayloadElementType> payloads = null;
	
	
	
	/**
	 * Constructor
	 */
	public DescriptionElementType(){
		super();
		payloads = new Vector<PayloadElementType>();
	}
	
	public String getAttributeNamespace() {
		return attributeNamespace;
	}

	public void setAttributeNamespace(String attributeNamespace) {
		this.attributeNamespace = attributeNamespace;
	}

	public String getAttributeMedia() {
		return attributeMedia;
	}

	public void setAttributeMedia(String attributeMedia) {
		this.attributeMedia = attributeMedia;
	}

	public void addPayload(PayloadElementType payload){
		payloads.add(payload);
	}
	
	public Vector<PayloadElementType> getPayloads(){
		return payloads;
	}
	
	/**
	 * Returns the XML snippet corresponding to this element
	 * @return String containing the XML snippet corresponding to this element
	 */
	public String toXML(){
		StringBuilder buffer = new StringBuilder();
		buffer.append("<" + ELEMENT_DESCRIPTION);
		if(isAttributeInitialized(attributeNamespace))
			buffer.append(" "+ATTRIBUTE_NAME_XMLNS+"=").append(getQuoted(attributeNamespace));
		if(isAttributeInitialized(attributeMedia))
			buffer.append(" "+ATTRIBUTE_NAME_MEDIA+"=").append(getQuoted(attributeMedia));
		buffer.append(">");
		
		for(PayloadElementType payload: payloads){
			buffer.append(payload.toXML());
		}
		buffer.append("</" + ELEMENT_DESCRIPTION + ">");
		
		return buffer.toString();
	}

	@Override
	public void setAttributesFromParser(XmlPullParser parser) {
		this.setAttributeNamespace(parser.getNamespace());
		for (int i = 0; i<parser.getAttributeCount(); i++){
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_MEDIA)){
				this.setAttributeMedia(parser.getAttributeValue(i));
			}
		}
	}
	
	/**
	 * Contains various media type names
	 * 
	 * @author Abhishek
	 *
	 */
	public interface MediaTypes {
		public static final String TYPE_AUDIO = "audio";
		public static final String TYPE_VIDEO = "video";
	}
}

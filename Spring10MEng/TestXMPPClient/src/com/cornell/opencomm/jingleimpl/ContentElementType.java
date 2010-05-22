package com.cornell.opencomm.jingleimpl;
import org.xmlpull.v1.XmlPullParser;

/**
 * This class models the Content Element of a Jingle element based from XEP-0166
 * 
 * @author Abhishek 
 * @author Ming
 *
 */
public class ContentElementType extends BaseElementType{
	
	private String attributeCreator = null;
	private String attributeDisposition = null;
	private String attributeName = null;
	private String attributeSenders = null;
	
	private DescriptionElementType elementDescription = null;
	private TransportElementType elementTransport = null;
	
	public static final String ELEMENT_CONTENT = "content";
	public static final String ATTRIBUTE_NAME_CREATOR = "creator";
	public static final String ATTRIBUTE_NAME_DISPOSITION = "disposition";
	public static final String ATTRIBUTE_NAME_NAME = "name";
	public static final String ATTRIBUTE_NAME_SENDERS = "senders";
	
	/**
	 * Constructor
	 */
	public ContentElementType(){
		super();
	}
			
	public String getAttributeCreator() {
		return attributeCreator;
	}

	public void setAttributeCreator(String attributeCreator) {
		this.attributeCreator = attributeCreator;
	}

	public String getAttributeDisposition() {
		return attributeDisposition;
	}

	public void setAttributeDisposition(String attributeDisposition) {
		this.attributeDisposition = attributeDisposition;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeSenders() {
		return attributeSenders;
	}

	public void setAttributeSenders(String attributeSenders) {
		this.attributeSenders = attributeSenders;
	}

	public DescriptionElementType getElementDescription() {
		return elementDescription;
	}

	public void setElementDescription(DescriptionElementType elementDescription) {
		this.elementDescription = elementDescription;
	}

	public TransportElementType getElementTransport() {
		return elementTransport;
	}

	public void setElementTransport(TransportElementType elementTransport) {
		this.elementTransport = elementTransport;
	}

	/**
	 * Returns the XML snippet corresponding to this element
	 * @return String containing the XML snippet corresponding to this element
	 */
	public String toXML(){
		StringBuilder buffer = new StringBuilder();
		buffer.append("<" + ELEMENT_CONTENT);
		if(isAttributeInitialized(attributeCreator))
			buffer.append(" " + ATTRIBUTE_NAME_CREATOR + "=").append(getQuoted(attributeCreator));
		if(isAttributeInitialized(attributeDisposition))
			buffer.append(" " + ATTRIBUTE_NAME_DISPOSITION + "=").append(getQuoted(attributeDisposition));
		if(isAttributeInitialized(attributeName))
			buffer.append(" " + ATTRIBUTE_NAME_NAME + "=").append(getQuoted(attributeName));
		if(isAttributeInitialized(attributeSenders))
			buffer.append(" " + ATTRIBUTE_NAME_SENDERS + "=").append(getQuoted(attributeSenders));
		buffer.append(">");
		
		if(elementDescription != null)
			buffer.append(elementDescription.toXML());
		if(elementTransport != null)
			buffer.append(elementTransport.toXML());
		
		buffer.append("</" + ELEMENT_CONTENT + ">");
		
		return buffer.toString();
	}

	@Override
	public void setAttributesFromParser(XmlPullParser parser) {
		for(int i=0; i<parser.getAttributeCount(); i++){
			if(parser.getAttributeName(i).equals(ATTRIBUTE_NAME_CREATOR)){
				setAttributeCreator(parser.getAttributeValue(i));
				continue;
			}
			if(parser.getAttributeName(i).equals(ATTRIBUTE_NAME_DISPOSITION)){
				setAttributeDisposition(parser.getAttributeValue(i));
				continue;
			}
			if(parser.getAttributeName(i).equals(ATTRIBUTE_NAME_NAME)){
				setAttributeName(parser.getAttributeValue(i));
				continue;
			}
			if(parser.getAttributeName(i).equals(ATTRIBUTE_NAME_SENDERS)){
				setAttributeSenders(parser.getAttributeValue(i));
				continue;
			}
		}
	}
}

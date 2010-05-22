package com.cornell.opencomm.jingleDesktop;

import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;

public class TransportElementType extends BaseElementType{
	
	public static final int TYPE_RAW_UDP = 0;
	public static final int TYPE_ICE_UDP = 1;
	
	public static final String NAMESPACE_RAW_UDP = "urn:xmpp:jingle:transports:raw-udp:1";
	public static final String NAMESPACE_ICE_UDP = "urn:xmpp:jingle:transports:ice-udp:1";
	public static final String ELEMENT_TRANSPORT = "transport";
	
	public static final String ATTRIBUTE_NAME_PWD = "pwd";
	public static final String ATTRIBUTE_NAME_UFRAG = "ufrag";
	
	private int transportType;
	private String namespace = null;
	
	private String attributePwd = null;
	private String attributeUfrag = null;
	private Vector<CandidateElementType> candidates = null; // 1 or more candidate or remote-candidate elements
	
	/**
	 * Dummy Constructor
	 */
	public TransportElementType(){
		candidates = new Vector<CandidateElementType>();
	}
	
	/**
	 * Constructor
	 */
	public TransportElementType(int type){
		candidates = new Vector<CandidateElementType>();
		transportType = type;
		if(transportType == TYPE_ICE_UDP)
			namespace = NAMESPACE_ICE_UDP;
		else if(transportType == TYPE_RAW_UDP)
			namespace = NAMESPACE_RAW_UDP;		
	}
	
	public void setNamespace(String xmlns){
		namespace = xmlns;
	}
	
	public String getNamespace(){
		return namespace;
	}
	
	public String getAttributePwd() {
		return attributePwd;
	}


	public void setAttributePwd(String attributePwd) {
		this.attributePwd = attributePwd;
	}


	public String getAttributeUfrag() {
		return attributeUfrag;
	}


	public void setAttributeUfrag(String attributeUfrag) {
		this.attributeUfrag = attributeUfrag;
	}


	/**
	 * Adds a candidate to the list of candidates contained within this Transport Element
	 * @param candidate
	 */
	public void addCandidate(CandidateElementType candidate){
		candidates.add(candidate);
	}
	
	/**
	 * Returns the candidates contained in the Transport Element
	 * @return Vector<CandidateElementType> of candidates
	 */
	public Vector<CandidateElementType> getCandidates(){
		return candidates;
	}
	
	/**
	 * Generates XML snippet for the corresponding Element
	 * @return the XML snippet that represents this element
	 */
	public String toXML(){
		StringBuilder buffer = new StringBuilder();
		buffer.append("<" + ELEMENT_TRANSPORT);
		if(isAttributeInitialized(namespace))
			buffer.append(" xmlns=").append(getQuoted(namespace));
		if(transportType == TYPE_ICE_UDP){
			if(isAttributeInitialized(attributePwd))
				buffer.append(" "+ATTRIBUTE_NAME_PWD+"=").append(getQuoted(attributePwd));
			if(isAttributeInitialized(attributeUfrag))
				buffer.append(" "+ATTRIBUTE_NAME_UFRAG+"=").append(getQuoted(attributeUfrag));
		}
		buffer.append(">");
		
		for(CandidateElementType candidate: candidates){
			buffer.append(candidate.toXML());
		}
		
		buffer.append("</" + ELEMENT_TRANSPORT + ">");
		
		return buffer.toString();
	}

	@Override
	public void setAttributesFromParser(XmlPullParser parser) {
		this.setNamespace(parser.getNamespace());
		for (int i = 0; i < parser.getAttributeCount(); i++){
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_PWD)){
				this.setAttributePwd(parser.getAttributeValue(i));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_UFRAG)){
				this.setAttributeUfrag(parser.getAttributeValue(i));
				continue;
			}
		}
	}
}

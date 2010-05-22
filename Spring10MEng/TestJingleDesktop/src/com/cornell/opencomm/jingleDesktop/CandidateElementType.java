package com.cornell.opencomm.jingleDesktop;
import org.xmlpull.v1.XmlPullParser;

/**
 * This Class encapsulates all attributes of a Candidate type, be it RAW_UDP or
 * ICE_UDP
 * @author Abhishek and Ming
 *
 */
public class CandidateElementType extends BaseElementType{
	
	// These values are the possible values for candidateType
    public static final int TYPE_REMOTE_CANDIDATE = 0; // denotes remote-candidate as per XEP-0176
    public static final int TYPE_LOCAL_CANDIDATE = 1; // denotes candidate as per XEP-0176 and XEP-0177
    
    // All possible values for the attributeType. NOTE: These might be obsoleted by RFC 5245 for STUN
    private static final String NAT_TYPE_NONE = "host";
    private static final String NAT_TYPE_PEER_REFLEXIVE = "prflx";
    private static final String NAT_TYPE_RELAY = "relay";
    private static final String NAT_TYPE_SERVER_REFLEXIVE = "srflx";    

    public static final String LOCAL_CANDIDATE_ELEMENT_NAME = "candidate";
    public static final String REMOTE_CANDIDATE_ELEMENT_NAME = "remote-candidate";
    
    public static final String ATTRIBUTE_NAME_COMPONENT = "component";
    public static final String ATTRIBUTE_NAME_FOUNDATION = "foundation";
    public static final String ATTRIBUTE_NAME_GENERATION = "generation";
    public static final String ATTRIBUTE_NAME_ID = "id";
    public static final String ATTRIBUTE_NAME_IP = "ip";
    public static final String ATTRIBUTE_NAME_NETWORK = "network";
    public static final String ATTRIBUTE_NAME_PORT = "port";
    public static final String ATTRIBUTE_NAME_PRIORITY = "priority";
    public static final String ATTRIBUTE_NAME_PROTOCOL = "protocol";
    public static final String ATTRIBUTE_NAME_RELADDR = "rel-addr";
    public static final String ATTRIBUTE_NAME_RELPORT = "rel-port";
    public static final String ATTRIBUTE_NAME_TYPE = "type";
    
    private int candidateType;
    
    // Attributes of a CandidateElementType
	private Short attributeComponent = -1;
	private Short attributeFoundation = -1;
	private Short attributeGeneration = -1;
	private String attributeID = null;
	private String attributeIP = null;
	private Short attributeNetwork = -1;
	private Short attributePort = -1;
	private Integer attributePriority = -1;
	private String attributeProtocol = null;
	private String attributeRelAddr = null;
	private String attributeRelPort = null;
	private String attributeType = null;	
	
	/**
	 * Constructor
	 */
	public CandidateElementType(int candidateType){
		super();
		this.candidateType = candidateType;
	}	
	
	// *******************************************GETTERS_START*****************************************
	
	public short getAttributeComponent() {
		return attributeComponent;
	}

	public short getAttributeFoundation() {
		return attributeFoundation;
	}

	public short getAttributeGeneration() {
		return attributeGeneration;
	}
	
	public String getAttributeID() {
		return attributeID;
	}
	
	public String getAttributeIP() {
		return attributeIP;
	}
	
	public short getAttributeNetwork() {
		return attributeNetwork;
	}
	
	public short getAttributePort() {
		return attributePort;
	}
	
	public int getAttributePriority() {
		return attributePriority;
	}
	
	public String getAttributeProtocol() {
		return attributeProtocol;
	}
	
	public String getAttributeRelAddr() {
		return attributeRelAddr;
	}

	public String getAttributeRelPort() {
		return attributeRelPort;
	}
	
	public String getAttributeType() {
		return attributeType;
	}
	
	// *******************************************GETTERS_END*****************************************
	
	
	// *******************************************SETTERS_START***************************************
	
	public void setAttributeComponent(short attributeComponent) {
		this.attributeComponent = attributeComponent;
	}

	public void setAttributeFoundation(short attributeFoundation) {
		this.attributeFoundation = attributeFoundation;
	}

	public void setAttributeGeneration(short attributeGeneration) {
		this.attributeGeneration = attributeGeneration;
	}

	public void setAttributeID(String attributeID) {
		this.attributeID = attributeID;
	}

	public void setAttributeIP(String attributeIP) {
		this.attributeIP = attributeIP;
	}

	public void setAttributeNetwork(short attributeNetwork) {
		this.attributeNetwork = attributeNetwork;
	}

	public void setAttributePort(short attributePort) {
		this.attributePort = attributePort;
	}

	public void setAttributePriority(int attributePriority) {
		this.attributePriority = attributePriority;
	}

	public void setAttributeProtocol(String attributeProtocol) {
		this.attributeProtocol = attributeProtocol;
	}

	public void setAttributeRelAddr(String attributeRelAddr) {
		this.attributeRelAddr = attributeRelAddr;
	}

	public void setAttributeRelPort(String attributeRelPort) {
		this.attributeRelPort = attributeRelPort;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}
	
	// *******************************************SETTERS_END***************************************

	
	/**
	 * Returns the XML Snippet that corresponds to this CandidateELementType
	 * @return String containing the XML snippet corresponding to this CandidateElementType
	 */
	public String toXML(){
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("<");
		
		if(candidateType == TYPE_LOCAL_CANDIDATE)
			buffer.append(LOCAL_CANDIDATE_ELEMENT_NAME);
		else
			buffer.append(REMOTE_CANDIDATE_ELEMENT_NAME);
		
		// Handle attributes.
		if(isAttributeInitialized(attributeComponent))
			buffer.append(" "+ATTRIBUTE_NAME_COMPONENT+"=").append(getQuoted(attributeComponent.toString()));
		
		if(isAttributeInitialized(attributeFoundation))
			buffer.append(" "+ATTRIBUTE_NAME_FOUNDATION+"=").append(getQuoted(attributeFoundation.toString()));
		
		if(isAttributeInitialized(attributeGeneration))
			buffer.append(" "+ATTRIBUTE_NAME_GENERATION+"=").append(getQuoted(attributeGeneration.toString()));
		
		if(isAttributeInitialized(attributeID))
			buffer.append(" "+ATTRIBUTE_NAME_ID+"=").append(getQuoted(attributeID));
		
		if(isAttributeInitialized(attributeIP))
			buffer.append(" "+ATTRIBUTE_NAME_IP+"=").append(getQuoted(attributeIP));
		
		if(isAttributeInitialized(attributeNetwork))
			buffer.append(" "+ATTRIBUTE_NAME_NETWORK+"=").append(getQuoted(attributeNetwork.toString()));
		
		if(isAttributeInitialized(attributePort))
			buffer.append(" "+ATTRIBUTE_NAME_PORT+"=").append(getQuoted(attributePort.toString()));
		
		if(isAttributeInitialized(attributePriority))
			buffer.append(" "+ATTRIBUTE_NAME_PRIORITY+"=").append(getQuoted(attributePriority.toString()));
		
		if(isAttributeInitialized(attributeProtocol))
			buffer.append(" "+ATTRIBUTE_NAME_PROTOCOL+"=").append(getQuoted(attributeProtocol));
		
		if(isAttributeInitialized(attributeRelAddr))
			buffer.append(" "+ATTRIBUTE_NAME_RELADDR+"=").append(getQuoted(attributeRelAddr));
		
		if(isAttributeInitialized(attributeRelPort))
			buffer.append(" "+ATTRIBUTE_NAME_RELPORT+"=").append(getQuoted(attributeRelPort));
		
		if(isAttributeInitialized(attributeType))
			buffer.append(" "+ATTRIBUTE_NAME_TYPE+"=").append(getQuoted(attributeType));
		
		buffer.append("/>");
		
		return buffer.toString();
	}

	@Override
	public void setAttributesFromParser(XmlPullParser parser) {
		for (int i =0; i<parser.getAttributeCount(); i++){
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_COMPONENT)){
				this.setAttributeComponent(Short.parseShort(parser.getAttributeValue(i)));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_FOUNDATION)){
				this.setAttributeFoundation(Short.parseShort(parser.getAttributeValue(i)));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_GENERATION)){
				this.setAttributeGeneration(Short.parseShort(parser.getAttributeValue(i)));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_ID)){
				this.setAttributeID(parser.getAttributeValue(i));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_IP)){
				this.setAttributeIP(parser.getAttributeValue(i));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_NETWORK)){
				this.setAttributeNetwork(Short.parseShort(parser.getAttributeValue(i)));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_PORT)){
				this.setAttributePort(Short.parseShort(parser.getAttributeValue(i)));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_PRIORITY)){
				this.setAttributePriority(Integer.parseInt(parser.getAttributeValue(i)));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_PROTOCOL)){
				this.setAttributeProtocol(parser.getAttributeValue(i));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_RELADDR)){
				this.setAttributeRelAddr(parser.getAttributeValue(i));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_RELPORT)){
				this.setAttributeRelPort(parser.getAttributeValue(i));
				continue;
			}
			if (parser.getAttributeName(i).equalsIgnoreCase(ATTRIBUTE_NAME_TYPE)){
				this.setAttributeType(parser.getAttributeValue(i));
				continue;
			}	
		}	
		
	}
}

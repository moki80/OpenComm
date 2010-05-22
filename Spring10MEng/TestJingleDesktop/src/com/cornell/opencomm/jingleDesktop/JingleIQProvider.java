package com.cornell.opencomm.jingleDesktop;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * This class handles the parsing of incoming IQ packets
 * @author Abhishek
 *
 */
public class JingleIQProvider implements IQProvider{

	private int eventType;
	private JingleIQPacket jiqPacket;
	private boolean encounteredError = false;
	
	/**
	 * Constructor
	 */
	public JingleIQProvider(){
		
	}
	
	@Override
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		jiqPacket = new JingleIQPacket();
		eventType = parser.getEventType();
		
		while(!encounteredError && !(eventType == XmlPullParser.END_TAG && parser.getName().equalsIgnoreCase(JingleIQPacket.ELEMENT_NAME_JINGLE))){
			if(eventType == XmlPullParser.START_TAG && parser.getName().equals(JingleIQPacket.ELEMENT_NAME_JINGLE)){
				jiqPacket.setAttributesFromParser(parser);					
				parseJingleElement(parser);
			} else {
				// ERROR
				encounteredError = true;
				// TODO: create XMPPError
			}
			// eventType = parser.getEventType();
		}
		System.out.println(jiqPacket.toXML());
		return jiqPacket;
	}

	private void parseJingleElement(XmlPullParser parser) throws Exception{
		
		boolean encounteredReason = false;
		boolean encounteredContent = false;
		
		eventType = parser.next();
		
		while(!encounteredError && !(eventType == XmlPullParser.END_TAG && parser.getName().equalsIgnoreCase(JingleIQPacket.ELEMENT_NAME_JINGLE))){
			if(eventType == XmlPullParser.START_TAG){
				if(parser.getName().equals(ReasonElementType.ELEMENT_NAME_REASON)){
					
					encounteredReason = true;					
					if(encounteredContent){
						// Error case: Jingle ELement can't have both reason and content
						encounteredError = true;
					}
					
					ReasonElementType reasonElementType = null;
					String reason = null;
					String text = null;
					
					eventType = parser.next();
					
					if(eventType == XmlPullParser.START_TAG && parser.isEmptyElementTag()){
						// machine readable reason
						if(!ReasonElementType.isValidReason(parser.getName())){
							encounteredError = true;
							// TODO: create XMPError
						} else {
							reason = parser.getName();
						}
						eventType = parser.next(); // Eat up the end tag of the content-less tag						
					}
										
					eventType = parser.next();
										
					if(eventType == XmlPullParser.START_TAG && parser.getName().equals(ReasonElementType.ELEMENT_NAME_TEXT)){
						// text element
						eventType = parser.next();
						if(eventType == XmlPullParser.TEXT) {
							text = parser.getText();
							eventType = parser.next();
							if (!(eventType == XmlPullParser.END_TAG && parser.getName().equals(ReasonElementType.ELEMENT_NAME_TEXT))){
								encounteredError = true;
							}
						} else if(!(eventType == XmlPullParser.END_TAG && parser.getName().equals(ReasonElementType.ELEMENT_NAME_TEXT))){
							// ERROR:
							encounteredError = true;
						}						
					} else {
						// ERROR
						encounteredError = true;
					}
					
					reasonElementType = new ReasonElementType(reason, text);
					jiqPacket.setElementReason(reasonElementType);
					
					eventType = parser.next();
					
					if (!(eventType == XmlPullParser.END_TAG && parser.getName().equals(ReasonElementType.ELEMENT_NAME_REASON))){
						// ERROR
						encounteredError = true;
					}
				} else if(parser.getName().equals(ContentElementType.ELEMENT_CONTENT)){
					encounteredContent = true;
					if(encounteredReason){
						// Error case: Jingle ELement can't have both reason and content
						encounteredError = true;
					} else {
						ContentElementType content = parseContentElement(parser);
						jiqPacket.addContentType(content);
					}
				} else {
					// ERROR
					encounteredError = true;
					// TODO: create XMPPError
				}
			}
			eventType = parser.next();
		}
	}
	
	private ContentElementType parseContentElement(XmlPullParser parser) throws Exception{
		ContentElementType content = new ContentElementType();
		int descriptionCount = 0;
		int transportCount = 0;
		
		// First handle the Content elements attributes
		content.setAttributesFromParser(parser);
		
		eventType = parser.next();
		
		while(!encounteredError && (!(eventType == XmlPullParser.END_TAG && parser.getName().equals(ContentElementType.ELEMENT_CONTENT)))){
			if(eventType == XmlPullParser.START_TAG){
				if(parser.getName().equals(DescriptionElementType.ELEMENT_DESCRIPTION)){
					descriptionCount++;
					DescriptionElementType description = parseDescriptionElement(parser);
					content.setElementDescription(description);
				} else if(parser.getName().equals(TransportElementType.ELEMENT_TRANSPORT)){
					transportCount++;
					TransportElementType transport = parseTransportElement(parser);
					content.setElementTransport(transport);
				}
			} else {
				// ERROR
				encounteredError = true;
			}
			
			eventType = parser.next();
		}
		
		if(!encounteredError){
			if(!(descriptionCount == 1 && transportCount == 1)){
				encounteredError = true;
			}
		}
		
		return content;
	}

	private DescriptionElementType parseDescriptionElement(XmlPullParser parser) throws Exception{
		DescriptionElementType description = new DescriptionElementType();
		description.setAttributesFromParser(parser);
		
		eventType = parser.next();
		
		while(!encounteredError && !(eventType == XmlPullParser.END_TAG && parser.getName().equals(DescriptionElementType.ELEMENT_DESCRIPTION))){
			if(eventType == XmlPullParser.START_TAG){
				if(parser.getName().equals(PayloadElementType.ELEMENT_PAYLOAD)){
					PayloadElementType payload = parsePayloadElement(parser);
					description.addPayload(payload);
				} else {
					encounteredError = true;
				}
			} else {
				encounteredError = true;
			}
			eventType = parser.next();
		}
		
		return description;
	}
	
	private TransportElementType parseTransportElement(XmlPullParser parser) throws Exception {
		TransportElementType transport = new TransportElementType();
		transport.setAttributesFromParser(parser);
		eventType = parser.next();
		
		while(!encounteredError && !(eventType == XmlPullParser.END_TAG && parser.getName().equals(TransportElementType.ELEMENT_TRANSPORT))){
			if(eventType == XmlPullParser.START_TAG){
				if(parser.getName().equals(CandidateElementType.LOCAL_CANDIDATE_ELEMENT_NAME)){
					CandidateElementType candidate = parseLocalCandidateElement(parser);
					transport.addCandidate(candidate);
				} else if(parser.getName().equals(CandidateElementType.REMOTE_CANDIDATE_ELEMENT_NAME)) {
					CandidateElementType candidate = parseLocalCandidateElement(parser);
					transport.addCandidate(candidate);
				} else {
					encounteredError = true;
				}
			} else {
				encounteredError = true;
			}		
			eventType = parser.next();
		}
		
		return transport;
	}
	
	private PayloadElementType parsePayloadElement(XmlPullParser parser) throws Exception {
		PayloadElementType payload = new PayloadElementType();
		payload.setAttributesFromParser(parser);
		eventType = parser.next();
		
		while(!encounteredError && !(eventType == XmlPullParser.END_TAG && parser.getName().equals(PayloadElementType.ELEMENT_PAYLOAD))){
			encounteredError = true;
		}
		
		return payload;
	}

	private CandidateElementType parseLocalCandidateElement(XmlPullParser parser) throws Exception {
		CandidateElementType candidate = new CandidateElementType(CandidateElementType.TYPE_LOCAL_CANDIDATE);
		candidate.setAttributesFromParser(parser);
		eventType = parser.next();
		
		while(!encounteredError && !(eventType == XmlPullParser.END_TAG && parser.getName().equals(CandidateElementType.LOCAL_CANDIDATE_ELEMENT_NAME))){
			encounteredError = true;
		}
		
		return candidate;
	}
	
	private CandidateElementType parseRemoteCandidateElement(XmlPullParser parser) throws Exception {
		CandidateElementType candidate = new CandidateElementType(CandidateElementType.TYPE_REMOTE_CANDIDATE);
		candidate.setAttributesFromParser(parser);
		eventType = parser.next();
		
		while(!encounteredError && !(eventType == XmlPullParser.END_TAG && parser.getName().equals(CandidateElementType.REMOTE_CANDIDATE_ELEMENT_NAME))){
			encounteredError = true;
		}
		
		return candidate;
	}	
}

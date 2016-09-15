package org.processmining.plugins.predictive_monitor.bpm.server;

import org.processmining.operationalsupport.xml.XOSComparisonQueryConverter;
import org.processmining.operationalsupport.xml.XOSComparisonReplyConverter;
import org.processmining.operationalsupport.xml.XOSCreateSessionConverter;
import org.processmining.operationalsupport.xml.XOSDestroySessionConverter;
import org.processmining.operationalsupport.xml.XOSDocumentConverter;
import org.processmining.operationalsupport.xml.XOSPredictQueryConverter;
import org.processmining.operationalsupport.xml.XOSPredictReplyConverter;
import org.processmining.operationalsupport.xml.XOSRecommendQueryConverter;
import org.processmining.operationalsupport.xml.XOSRecommendReplyConverter;
import org.processmining.operationalsupport.xml.XOSRecommendationConverter;
import org.processmining.operationalsupport.xml.XOSResponseSetConverter;
import org.processmining.operationalsupport.xml.XOSSessionConverter;
import org.processmining.operationalsupport.xml.XOSSessionCreatedConverter;
import org.processmining.operationalsupport.xml.XOSSessionDestroyedConverter;
import org.processmining.operationalsupport.xml.XOSSimpleQueryConverter;
import org.processmining.operationalsupport.xml.XOSSimpleReplyConverter;
import org.processmining.operationalsupport.xml.XOSXESConverter;
import org.processmining.operationalsupport.xml.XOSXEventConverter;
import org.processmining.operationalsupport.xml.XOSXTraceConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class DataStructureSerializer extends XStream{
	public DataStructureSerializer()
	{
		super(new DomDriver());
		setMode(XStream.NO_REFERENCES);

		registerConverter(new XOSXESConverter());
		registerConverter(new XOSXTraceConverter());
		registerConverter(new XOSXEventConverter());

		registerConverter(new XOSDocumentConverter());
	}

}

package org.processmining.plugins.predictive_monitor.bpm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.extension.std.XExtendedEvent;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.operationalsupport.provider.AbstractProvider;
import org.processmining.operationalsupport.provider.Provider;
import org.processmining.operationalsupport.server.OSService;
import org.processmining.operationalsupport.session.Session;
import org.processmining.plugins.predictive_monitor.bpm.server.ServerConfiguration;
import org.processmining.plugins.predictive_monitor.bpm.server.TraceManager;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.ConfigurationOperation;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.EvaluateEventOperation;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.EvaluateTraceOperation;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.GetLocalTrainingFiles;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.Operation;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;





/**
 *
 * @author Fabrizio Maria Maggi
 *
 */

@Plugin(name = "Predictive Monitor", parameterLabels = { "Operational Support Service"}, returnLabels = { "Predictive Monitor" }, returnTypes = { PredictiveMonitor.class }, userAccessible = true)
public class PredictiveMonitor extends AbstractProvider {

	/**
	 *
	 */
	private static final long serialVersionUID = 3042748916288208677L;
	public static final String rootTrainingFilesFolder = "input";
	public static final String rootLogFolder = "log";
	public static final String rootDataStructureFolder = "dataStructures";
	private Map<String,ServerConfiguration> serverConfigurations;
	private List<String> serverTrainingFiles;
	private Map<Integer,XLog> openedFiles;
	private TraceManager traceManager;


	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "F.M. Maggi et al.", email = "F.M.Maggi@ut.ee", uiLabel = "Predictive Monitor B", pack = "PredictiveMonitor")
	@PluginVariant(variantLabel = "Predictive Monitoring B", requiredParameterLabels = { 0})
	public static Provider registerServiceProviderAUI(final UIPluginContext context, OSService service) {
		return registerServiceProviderA(context, service);
	}

	public static Provider registerServiceProviderA(final PluginContext context, OSService service) {

		try {
			PredictiveMonitor provider = new PredictiveMonitor(service);
			context.getFutureResult(0).setLabel(context.getFutureResult(0).getLabel() + " on port " + service);
			return provider;
		} catch (Exception e) {
			context.log(e);
		}
		return null;
	}

	public PredictiveMonitor(OSService owner) throws Exception {
		super(owner);
		serverConfigurations = new HashMap<String, ServerConfiguration>();
		openedFiles = new HashMap<Integer, XLog>();
		serverTrainingFiles = new ArrayList<String>();
		traceManager = new TraceManager();
		File rootFileDir = new File(rootTrainingFilesFolder);
		new File(rootLogFolder).mkdirs();
		new File(rootDataStructureFolder).mkdirs();
		
		Collection<File> files = FileUtils.listFiles(
				  rootFileDir, 
				  new RegexFileFilter("^(.*?)"), 
				  DirectoryFileFilter.DIRECTORY
				);

		for(File f: files)
		{
			String name = f.getAbsolutePath().substring(rootFileDir.getAbsolutePath().length()+1, f.getAbsolutePath().length());
			if(name.substring(name.length()-5, name.length()).contains(".xes"))
			{
				serverTrainingFiles.add(name);
			}
		}
	}

	protected String convert(XTrace trace, int pos) {
		XExtendedEvent ev = XExtendedEvent.wrap(trace.get(pos));
		return "" + ev.getTimestamp().getTime();
	}

	public String getName() {
		return "Predictive Monitor";
	}

	public boolean accept(final Session session, final List<String> modelLanguages, final List<String> queryLanguages, Object model) {

//		Map<String,Object>configuration =(HashMap<String, Object>) session.getObject("configuration");
//		if(configuration!=null)
//		{
//			Vector<Formula> formulas =(Vector<Formula>) configuration.get("formulas");
//			String runId = session.getObject("runId");
//			System.out.println(runId);
//			serverConfigurations.put(runId, new ServerConfiguration(configuration, formulas, log, runId)); 
//		} 
		return true;
	}
	
	
	

	public <R, L> R simple(final Session session, final XLog availableItems, final String langauge, final L query,
			final boolean done) throws Exception {

		if (query == null) {
			return null;
		}
		
		Operation operation = (Operation)query;
		
		if(operation instanceof ConfigurationOperation)
		{
			ConfigurationOperation configurationOperation = (ConfigurationOperation)operation;
			Map<String,Object>configuration = configurationOperation.getConfiguration();
			if(configuration!=null)
			{
				String runId = configurationOperation.getRunId();
				ServerConfiguration serverConfiguration =  new ServerConfiguration(configuration, runId);
				serverConfigurations.put(runId,serverConfiguration); 
				return (R) new Long(serverConfiguration.getInitTime());
			} 
			return (R) new Long(0);
		}

		else if(operation instanceof EvaluateTraceOperation)
		{			
			EvaluateTraceOperation evaluateTraceOperation = (EvaluateTraceOperation)operation;
			
	
			PredictionResult bestSuggestion = null;
		
			String runId = evaluateTraceOperation.getTraceRun().getRunId();
			XTrace trace = evaluateTraceOperation.getTraceRun().getTrace();
		
			bestSuggestion = serverConfigurations.get(runId).evaluateTrace(trace, true);
			
			return (R) bestSuggestion;
		}
		
		else if(operation instanceof EvaluateEventOperation)
		{			
			EvaluateEventOperation evaluateEventOperation = (EvaluateEventOperation)operation;
			
	
			PredictionResult bestSuggestion = null;
		
			String runId = evaluateEventOperation.getRunId();
			String traceId = evaluateEventOperation.getTraceId();
			XEvent event = evaluateEventOperation.getEvent();
			XTrace trace = traceManager.addEvent(event, traceId);
		
			bestSuggestion = serverConfigurations.get(runId).evaluateTrace(trace, false);
			return (R) bestSuggestion;
		}
		
		else if(operation instanceof GetLocalTrainingFiles)
		{
			return (R) serverTrainingFiles;
		}
		return null;
	}
}

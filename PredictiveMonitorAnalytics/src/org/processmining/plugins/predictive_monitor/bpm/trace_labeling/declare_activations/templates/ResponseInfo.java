package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.plugins.correlation.CorrelationMiner;
import org.processmining.plugins.correlation.ExtendedEvent;
import org.processmining.plugins.correlation.ExtendedTrace;
import org.processmining.plugins.declareminer.DeclareMinerInput;
import org.processmining.plugins.declareminer.DeclareModelGenerator;
import org.processmining.plugins.declareminer.MetricsValues;
import org.processmining.plugins.declareminer.apriori.FindItemSets;
import org.processmining.plugins.declareminer.enumtypes.AprioriKnowledgeBasedCriteria;
import org.processmining.plugins.declareminer.enumtypes.DeclareTemplate;
import org.processmining.plugins.declareminer.enumtypes.FrequentItemSetType;
import org.processmining.plugins.declareminer.templates.LTLFormula;
import org.processmining.plugins.declareminer.visualizing.ActivityDefinition;
import org.processmining.plugins.declareminer.visualizing.ConstraintDefinition;
import org.processmining.plugins.declareminer.visualizing.DeclareMinerOutput;
import org.processmining.plugins.declareminer.visualizing.Parameter;
import org.processmining.plugins.predictive_monitor.bpm.utility.Print;

public class ResponseInfo extends TemplateInfo {
	
	private static Print print = new Print();

	public MetricsValues getMetrics(DeclareMinerOutput model, ConstraintDefinition cd, String correlation, DeclareMinerInput input, float alpha, float support,
			XLog log, DeclareTemplate currentTemplate,
			UIPluginContext context){
		numberOfDiscoveredConstraints = 0;

		if(metricsValues4response==null){
			metricsValues4response = new HashMap<String, MetricsValues>();
		}
		//List<List<String>> declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(currentTemplate);
		//for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k++){
		if(context!=null){context.getProgress().inc();}
		MetricsValues values = null;
		String formula = LTLFormula.getFormulaByTemplate(currentTemplate);
		formula = formula.replace( "\""+"A"+"\"" , "\""+cd.getBranches(cd.getParameterWithId(1)).iterator().next()+"\"");
		formula = formula.replace( "\""+"B"+"\"" , "\""+cd.getBranches(cd.getParameterWithId(2)).iterator().next()+"\"");

		ArrayList<String> paramList = new ArrayList<String>();
		paramList.add(cd.getBranches(cd.getParameterWithId(1)).iterator().next().toString());
		paramList.add(cd.getBranches(cd.getParameterWithId(2)).iterator().next().toString());
		if(metricsValues4response.containsKey(currentTemplate+";"+cd.getBranches(cd.getParameterWithId(1)).iterator().next()+";"+cd.getBranches(cd.getParameterWithId(2)).iterator().next())){
			values = metricsValues4response.get(currentTemplate+";"+cd.getBranches(cd.getParameterWithId(1)).iterator().next()+";"+cd.getBranches(cd.getParameterWithId(2)).iterator().next());
			//	printMetrics(pw, formula, values.getSupportRule(), values.getConfidence(), values.getSuppAntec(), +values.getSupportConseq(), values.getSuppAntecSat(), values.getSupportConseqSat(), values.getI(), values.getCPIR());
		}else{
			//				if(false){
			//				//	values = computeMetrics(model, cd, input,currentTemplate, declareTemplateCandidateDispositionsList.get(k), log, pw, alpha,f, correlation);
			//				}else{
			values = computeMetrics(model.getModel(), cd, input,currentTemplate, paramList, log, alpha, correlation);
			//				}
			metricsValues4response.put(currentTemplate+";"+paramList.get(0)+";"+paramList.get(1), values);
		}
		if(values.getSupportRule()>=support){
			numberOfDiscoveredConstraints ++;
		}
		print.thatln("I am in ResponseInfo");
		print.thatln("interest factor"+ paramList+": "+values.getI());
		
		//metricsValues.add(values);
		//}
		return values;
	}


	public Vector<MetricsValues> getMetrics(DeclareMinerInput input, Map<FrequentItemSetType, Map<Set<String>, Float>> frequentItemSetTypeFrequentItemSetSupportMap, Map<DeclareTemplate, List<List<String>>> declareTemplateCandidateDispositionsMap, float alpha, float support,
			XLog log, PrintWriter pw, DeclareTemplate currentTemplate,
			UIPluginContext context, boolean verbose, FindItemSets f){
		numberOfDiscoveredConstraints = 0;
		Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();
		if(metricsValues4response==null){
			metricsValues4response = new HashMap<String, MetricsValues>();
		}
		List<List<String>> declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(currentTemplate);
		for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k++){
			if(context!=null){context.getProgress().inc();}
			MetricsValues values = null;
			String formula = LTLFormula.getFormulaByTemplate(currentTemplate);
			formula = formula.replace( "\""+"A"+"\"" , "\""+declareTemplateCandidateDispositionsList.get(k).get(0)+"\"");
			formula = formula.replace( "\""+"B"+"\"" , "\""+declareTemplateCandidateDispositionsList.get(k).get(1)+"\"");
			if(metricsValues4response.containsKey(currentTemplate+";"+declareTemplateCandidateDispositionsList.get(k).get(0)+";"+declareTemplateCandidateDispositionsList.get(k).get(1))){
				values = metricsValues4response.get(currentTemplate+";"+declareTemplateCandidateDispositionsList.get(k).get(0)+";"+declareTemplateCandidateDispositionsList.get(k).get(1));
				printMetrics(pw, formula, values.getSupportRule(), values.getConfidence(), values.getSuppAntec(), +values.getSupportConseq(), values.getSuppAntecSat(), values.getSupportConseqSat(), values.getI(), values.getCPIR());
			}else{
				if(verbose){
					values = computeMetrics(input,currentTemplate, declareTemplateCandidateDispositionsList.get(k), log, pw, alpha,f);
				}else{
					values = computeMetrics(input,currentTemplate, declareTemplateCandidateDispositionsList.get(k), log, null, alpha,f);
				}
				metricsValues4response.put(currentTemplate+";"+declareTemplateCandidateDispositionsList.get(k).get(0)+";"+declareTemplateCandidateDispositionsList.get(k).get(1), values);
			}
			if(values.getSupportRule()>=support){
				numberOfDiscoveredConstraints ++;
			}
//			if(ServerConfigurationClass.printDebug){
//				System.out.println("I am in ResponseInfo");
//				System.out.println("interest factor"+  declareTemplateCandidateDispositionsList.get(k)+": "+values.getI());
//			}
			metricsValues.add(values);
		}
		return metricsValues;
	}


	public Vector<Long> getTimeDistances(DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> fulfillments) {
		Vector<Long> timeDists = new Vector<Long>();
//		if(ServerConfigurationClass.printDebug)System.out.println("sono dentro, valutazione constraint numero fulfill "+ fulfillments.size());
		for(Integer pos : fulfillments){
			//System.out.println("sono dentro pos");
			boolean found = false;
			XEvent act = trace.get(pos);
			for(int c=pos; c<trace.size(); c++){
				//System.out.println("sono dentro c");
				XEvent event = trace.get(c);
				Iterator<Parameter> iter = constraintDefinition.getParameters().iterator();
				iter.next();
				Parameter p2 = iter.next();
				ActivityDefinition target = constraintDefinition.getBranches(p2).iterator().next();
				if(target.getName().equals((XConceptExtension.instance().extractName(act)))){
					target = constraintDefinition.getBranches(constraintDefinition.getParameterWithId(1)).iterator().next();
				}

				//	target = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
				String activityName = target.getName();
				String eventName = (XConceptExtension.instance().extractName(event));
				//}
				if(input.getAprioriKnowledgeBasedCriteriaSet().contains(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes)){
					if(!containsEventType(target.getName())){
						if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
							activityName = target.getName()+"-"+input.getReferenceEventType();
							eventName = (XConceptExtension.instance().extractName(event))+"-"+XLifecycleExtension.instance().extractTransition(event);
						}
					}else{
						if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
							eventName = (XConceptExtension.instance().extractName(event))+"-"+XLifecycleExtension.instance().extractTransition(event);
						}else{
							eventName = (XConceptExtension.instance().extractName(event))+"-"+input.getReferenceEventType();
						}
					}
				}
				if(eventName.equals(activityName)){
					found = true;
					long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
					long timeDistance2 = XTimeExtension.instance().extractTimestamp(act).getTime();
					long timeDiff = timeDistance1 - timeDistance2;
					timeDists.add(timeDiff);
					//					if(constraintDefinition.getName().equals("chain response")){
					//						System.out.println("quiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii: "+XConceptExtension.instance().extractName(act)+"       "+target.getName()+timeDiff);
					//					}
				}
				if(found){
					break;
				}
			}
		}
		print.thatln("sono fuori responseInfo");
		return timeDists;
	}

	public Vector<Long> getTimeDistances(HashMap<String,ExtendedEvent>  extEvents, DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> fulfillments, String correlation) {
		Vector<Long> timeDists = new Vector<Long>();
		CorrelationMiner miner = new CorrelationMiner();
		for(Integer pos : fulfillments){
			boolean found = false;
			XEvent act = trace.get(pos);
			//ExtendedEvent exActivation = extEvents.get(XConceptExtension.instance().extractName(trace.get(pos)));
			for(int c=pos; c<trace.size(); c++){
				XEvent event = trace.get(c);
				Iterator<Parameter> iter = constraintDefinition.getParameters().iterator();
				iter.next();
				Parameter p2 = iter.next();
				ActivityDefinition target = constraintDefinition.getBranches(p2).iterator().next();
				if(target.getName().equals((XConceptExtension.instance().extractName(act)))){
					target = constraintDefinition.getBranches(constraintDefinition.getParameterWithId(1)).iterator().next();
				}

				//	target = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
				String activityName = target.getName();
				String eventName = (XConceptExtension.instance().extractName(event));
				//}
				
				boolean evType = false;
				DeclareModelGenerator gen = new DeclareModelGenerator();
				
				if(gen.hasEventTypeInName(activityName)){
					evType= true;
				}
				if(input.getAprioriKnowledgeBasedCriteriaSet().contains(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes)){
					if(!containsEventType(target.getName())){
						if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
							activityName = target.getName()+"-"+input.getReferenceEventType();
							eventName = (XConceptExtension.instance().extractName(event))+"-"+XLifecycleExtension.instance().extractTransition(event);
						}
					}else{
						if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
							eventName = (XConceptExtension.instance().extractName(event))+"-"+XLifecycleExtension.instance().extractTransition(event);
						}else{
							eventName = (XConceptExtension.instance().extractName(event))+"-"+input.getReferenceEventType();
						}
					}
				}
				if(eventName.equals(activityName)){
					//	ExtendedEvent exTarget = extEvents.get(XConceptExtension.instance().extractName(trace.get(c)));
					if(miner.isValid(evType, correlation, act, event, extEvents)){
						found = true;
						long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
						long timeDistance2 = XTimeExtension.instance().extractTimestamp(act).getTime();
						long timeDiff = timeDistance1 - timeDistance2;
						timeDists.add(timeDiff);

						//					if(constraintDefinition.getName().equals("chain response")){
						//						System.out.println("quiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii: "+XConceptExtension.instance().extractName(act)+"       "+target.getName()+timeDiff);
						//					}
					}
				}
				if(found){
					break;
				}
			}
		}
		return timeDists;
	}


	public Vector<ExtendedTrace> getNonAmbiguousActivations(XLog log, DeclareTemplate template, String activation, String target){
		// for response 
		Vector<ExtendedTrace> output = new Vector<ExtendedTrace>();
		activation = activation.replaceAll("-assign","").replaceAll("-ate_abort","").replaceAll("-suspend","").replaceAll("-complete","").replaceAll("-autoskip","").replaceAll("-manualskip","").replaceAll("pi_abort","").replaceAll("-reassign","").replaceAll("-resume","").replaceAll("-schedule","").replaceAll("-start","").replaceAll("-unknown","").replaceAll("-withdraw","");
		target = target.replaceAll("-assign","").replaceAll("-ate_abort","").replaceAll("-suspend","").replaceAll("-complete","").replaceAll("-autoskip","").replaceAll("-manualskip","").replaceAll("pi_abort","").replaceAll("-reassign","").replaceAll("-resume","").replaceAll("-schedule","").replaceAll("-start","").replaceAll("-unknown","").replaceAll("-withdraw","");		
		if(template.equals(DeclareTemplate.Chain_Response)){
			for(XTrace trace: log){
				int pos = -1;
				ExtendedTrace extr = new ExtendedTrace();
				extr.setTrace(trace);
				extr.setNonambi(new Vector<Integer>());
				extr.setCorrespcorrel(new HashMap<Integer,Vector<Integer>>());
				XEvent toVerify = null;
				String oldEv = null;
				int i = -2;
				for(XEvent event : trace){
					String eventName = XConceptExtension.instance().extractName(event);
					if(i > -1){
						oldEv =  XConceptExtension.instance().extractName(trace.get(i));	
					}
					i++;
					if(toVerify!=null){
						toVerify = null;
						if(eventName.equals(target)&&(oldEv==null || (oldEv!=null&&!oldEv.equals(activation)))){
							Vector<Integer> nonambi = extr.getNonambi();
							nonambi.add(pos);
							extr.setNonambi(nonambi);

							HashMap<Integer,Vector<Integer>> corresp = extr.getCorrespcorrel();
							Vector<Integer> c = new Vector<Integer>();
							c.add(pos+1);
							corresp.put(pos,c);
							extr.setCorrespcorrel(corresp);

						}
					}
					pos++;
					if(eventName.equals(activation)){
						toVerify = event;
					}			
				}
				output.add(extr);
			}
		}



		if(template.equals(DeclareTemplate.Response)){

			for(XTrace trace: log){
				int pos = 0;
				HashMap<Integer,Vector<Integer>> numbAmb = new HashMap<Integer, Vector<Integer>>();

				ExtendedTrace extr = new ExtendedTrace();
				extr.setTrace(trace);
				extr.setNonambi(new Vector<Integer>());
				extr.setCorrespcorrel(new HashMap<Integer,Vector<Integer>>());
				for(XEvent event : trace){
					String eventName = XConceptExtension.instance().extractName(event);
					if(eventName.equals(activation)){
						numbAmb.put(pos, new Vector<Integer>());
					}
					if(eventName.equals(target)){
						for(int i:numbAmb.keySet()){
							Vector<Integer> trgts = numbAmb.get(i);
							trgts.add(pos);
							numbAmb.put(i, trgts);
						}
					}
					pos++;
				}

				for(int i:numbAmb.keySet()){
					if(numbAmb.get(i).size()>=1){
						Vector<Integer> nonambi = extr.getNonambi();
						nonambi.add(i);
						extr.setNonambi(nonambi);

						HashMap<Integer,Vector<Integer>> corresp = extr.getCorrespcorrel();
						Vector<Integer> c = numbAmb.get(i);
						corresp.put(i,c);
						extr.setCorrespcorrel(corresp);

					}
				}
				output.add(extr);
			}
		}





		if(template.equals(DeclareTemplate.Alternate_Response)){

			for(XTrace trace: log){
				int pos = 0;				
				ExtendedTrace extr = new ExtendedTrace();
				extr.setTrace(trace);
				extr.setNonambi(new Vector<Integer>());
				extr.setCorrespcorrel(new HashMap<Integer,Vector<Integer>>());
				int pend = -1;
				int targNum = 0;
				Vector<Integer> targetPos= new Vector<Integer>();
				int pendNum = 0;
				//Vector<Integer> candidate = new Vector<Integer>();
				//	int candidate = -1;
				for(XEvent event : trace){
					String eventName = XConceptExtension.instance().extractName(event);
					if(eventName.equals(activation)){
						if(targNum >= 1 && pendNum ==1){
							Vector<Integer> nonambi = extr.getNonambi();
							nonambi.add(pend);
							extr.setNonambi(nonambi);

							HashMap<Integer,Vector<Integer>> corresp = extr.getCorrespcorrel();
							Vector<Integer> c = (Vector<Integer>) targetPos.clone();
							corresp.put(pend,c);
							extr.setCorrespcorrel(corresp);

						}
						targetPos= new Vector<Integer>();
						if(targNum>=1){
							pendNum = 0;
						}
						targNum = 0;
						pendNum ++;
						pend = pos;
					}
					if(eventName.equals(target)){
						targNum ++;
						targetPos.add(pos);

					}

					pos++;
				}
				if(targNum >= 1 && pendNum ==1){
					Vector<Integer> nonambi = extr.getNonambi();
					nonambi.add(pend);
					extr.setNonambi(nonambi);

					HashMap<Integer,Vector<Integer>> corresp = extr.getCorrespcorrel();
					Vector<Integer> c = (Vector<Integer>) targetPos.clone();
					corresp.put(pend,c);
					extr.setCorrespcorrel(corresp);
				}
				output.add(extr);
			}
		}
		return output;
	}

}

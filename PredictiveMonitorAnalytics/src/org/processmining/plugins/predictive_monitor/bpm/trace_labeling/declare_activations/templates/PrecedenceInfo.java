package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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

public class PrecedenceInfo extends TemplateInfo {

	public MetricsValues getMetrics(DeclareMinerOutput model, ConstraintDefinition cd, String correlation, DeclareMinerInput input, float alpha, float support,
			XLog log, DeclareTemplate currentTemplate, UIPluginContext context){
		numberOfDiscoveredConstraints = 0;
		if(metricsValues4precedence==null){
			metricsValues4precedence = new HashMap<String, MetricsValues>();
		}
		//	Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();
		//	List<List<String>> declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(currentTemplate);

		ArrayList<String> paramList = new ArrayList<String>();
		paramList.add(cd.getBranches(cd.getParameterWithId(1)).iterator().next().toString());
		paramList.add(cd.getBranches(cd.getParameterWithId(2)).iterator().next().toString());
		//for(int k=0; k< actualParametersList.size(); k++){
		if(context!=null){context.getProgress().inc();}
		MetricsValues values = null;
		String formula = LTLFormula.getFormulaByTemplate(currentTemplate);
		formula = formula.replace( "\""+"A"+"\"" , "\""+paramList.get(0)+"\"");
		formula = formula.replace( "\""+"B"+"\"" , "\""+paramList.get(1)+"\"");

		if((metricsValues4precedence.containsKey(currentTemplate+";"+paramList.get(0)+";"+paramList.get(1)))){
			values = metricsValues4precedence.get(currentTemplate+";"+paramList.get(0)+";"+paramList.get(1));
			//printMetrics(pw, formula, values.getSupportRule(), values.getConfidence(), values.getSuppAntec(), +values.getSupportConseq(), values.getSuppAntecSat(), values.getSupportConseqSat(), values.getI(), values.getCPIR());
		}else{
			//		if(verbose){
			//			values = computeMetrics(model, cd, input, currentTemplate, actualParametersList.get(k), log, pw, alpha,f, correlation);
			//		}else{
			values = computeMetrics(model.getModel(), cd, input, currentTemplate, paramList, log, alpha, correlation);
			//		}
			metricsValues4precedence.put(currentTemplate+";"+paramList.get(0)+";"+paramList.get(1), values);
		}
		if(values.getSupportRule()>=support){
			numberOfDiscoveredConstraints ++;
		}
		//metricsValues.add(values);
		//}
		return values;
	}


	public Vector<MetricsValues> getMetrics(DeclareMinerInput input, Map<FrequentItemSetType, Map<Set<String>, Float>> frequentItemSetTypeFrequentItemSetSupportMap, Map<DeclareTemplate, List<List<String>>> declareTemplateCandidateDispositionsMap, float alpha, float support,
			XLog log, PrintWriter pw, DeclareTemplate currentTemplate,
			UIPluginContext context, boolean verbose, FindItemSets f){
		numberOfDiscoveredConstraints = 0;
		if(metricsValues4precedence==null){
			metricsValues4precedence = new HashMap<String, MetricsValues>();
		}
		Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();
		List<List<String>> declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(currentTemplate);

		List<List<String>> actualParametersList = new ArrayList<List<String>>();
		for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k++){
			List<String> actualParameters = new ArrayList<String>();
			actualParameters.add(declareTemplateCandidateDispositionsList.get(k).get(1));
			actualParameters.add(declareTemplateCandidateDispositionsList.get(k).get(0));
			actualParametersList.add(actualParameters);
		}
		for(int k=0; k< actualParametersList.size(); k++){
			if(context!=null){context.getProgress().inc();}
			MetricsValues values = null;
			String formula = LTLFormula.getFormulaByTemplate(currentTemplate);
			formula = formula.replace( "\""+"A"+"\"" , "\""+actualParametersList.get(k).get(0)+"\"");
			formula = formula.replace( "\""+"B"+"\"" , "\""+actualParametersList.get(k).get(1)+"\"");

			if((metricsValues4precedence.containsKey(currentTemplate+";"+actualParametersList.get(k).get(0)+";"+actualParametersList.get(k).get(1)))){
				values = metricsValues4precedence.get(currentTemplate+";"+actualParametersList.get(k).get(0)+";"+actualParametersList.get(k).get(1));
				printMetrics(pw, formula, values.getSupportRule(), values.getConfidence(), values.getSuppAntec(), +values.getSupportConseq(), values.getSuppAntecSat(), values.getSupportConseqSat(), values.getI(), values.getCPIR());
			}else{
				if(verbose){
					values = computeMetrics(input, currentTemplate, actualParametersList.get(k), log, pw, alpha,f);
				}else{
					values = computeMetrics(input, currentTemplate, actualParametersList.get(k), log, null, alpha,f);
				}
				metricsValues4precedence.put(currentTemplate+";"+actualParametersList.get(k).get(0)+";"+actualParametersList.get(k).get(1), values);
			}
			if(values.getSupportRule()>=support){
				numberOfDiscoveredConstraints ++;
			}
			metricsValues.add(values);
		}
		return metricsValues;
	}




	public Vector<Long> getTimeDistances(DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> fulfillments) {
		Vector<Long> timeDists = new Vector<Long>();
		for(Integer pos : fulfillments){
			boolean found = false;
			XEvent act = trace.get(pos);
			for(int c=pos; c>=0; c--){
				XEvent event = trace.get(c);
				ActivityDefinition target = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
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
					long timeDistance1 = XTimeExtension.instance().extractTimestamp(act).getTime();
					long timeDistance2 = XTimeExtension.instance().extractTimestamp(event).getTime();
					long timeDiff = timeDistance1 - timeDistance2;
					timeDists.add(timeDiff);

				}
				if(found){
					break;
				}
			}
		}
		return timeDists;
	}


	public Vector<Long> getTimeDistances(HashMap<String,ExtendedEvent>  extEvents, DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> fulfillments, String correlation) {
		Vector<Long> timeDists = new Vector<Long>();
		CorrelationMiner miner = new CorrelationMiner();
		for(Integer pos : fulfillments){
			boolean found = false;
			XEvent act = trace.get(pos);
			for(int c=pos; c>=0; c--){
				XEvent event = trace.get(c);
				ActivityDefinition target = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
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
					if(miner.isValid(evType, correlation, act, event, extEvents)){
						found = true;
						long timeDistance1 = XTimeExtension.instance().extractTimestamp(act).getTime();
						long timeDistance2 = XTimeExtension.instance().extractTimestamp(event).getTime();
						long timeDiff = timeDistance1 - timeDistance2;
						timeDists.add(timeDiff);

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
		if(template.equals(DeclareTemplate.Chain_Precedence)){
			for(XTrace trace: log){
				int pos = 0;
				ExtendedTrace extr = new ExtendedTrace();
				extr.setTrace(trace);
				extr.setNonambi(new Vector<Integer>());
				extr.setCorrespcorrel(new HashMap<Integer,Vector<Integer>>());
				XEvent toVerify = null;
				boolean currentAct = false;
				boolean found = false;
				for(XEvent event : trace){
					String eventName = XConceptExtension.instance().extractName(event);
					if(eventName.equals(activation)){
						currentAct = true;
					}else{
						currentAct = false;
					}
					if(found && !currentAct){
						Vector<Integer> nonambi = extr.getNonambi();
						nonambi.add(pos-1);
						extr.setNonambi(nonambi);


						HashMap<Integer,Vector<Integer>> corresp = extr.getCorrespcorrel();
						Vector<Integer> c = new Vector<Integer>();
						c.add(pos-2);
						corresp.put(pos-1,c);
						extr.setCorrespcorrel(corresp);
						found = false;
					}

					if(found && currentAct){
						found = false;
					}

					if(toVerify!=null){
						toVerify = null;
						if(eventName.equals(activation)){
							found = true;
						}
					}
					pos++;

					if(eventName.equals(target)){
						toVerify = event;
					}			
				}
				if(found){
					Vector<Integer> nonambi = extr.getNonambi();
					nonambi.add(pos-1);
					extr.setNonambi(nonambi);


					HashMap<Integer,Vector<Integer>> corresp = extr.getCorrespcorrel();
					Vector<Integer> c = new Vector<Integer>();
					c.add(pos-2);
					corresp.put(pos-1,c);
					extr.setCorrespcorrel(corresp);
					found = false;
				}
				output.add(extr);
			}
		}



		if(template.equals(DeclareTemplate.Precedence)){

			for(XTrace trace: log){
				int pos = 0;
				Vector<Integer> numbAmb = new Vector<Integer>();
				int numTarg = 0;
				Vector<Integer> targetPos= new Vector<Integer>();
				ExtendedTrace extr = new ExtendedTrace();
				extr.setTrace(trace);
				extr.setNonambi(new Vector<Integer>());
				extr.setCorrespcorrel(new HashMap<Integer,Vector<Integer>>());
				HashMap<Integer,Vector<Integer>> corresp  = new HashMap<Integer,Vector<Integer>>();
				for(XEvent event : trace){
					String eventName = XConceptExtension.instance().extractName(event);
					if(eventName.equals(activation) && numTarg >=1){
						numbAmb.add(pos);
						corresp = extr.getCorrespcorrel();
						Vector<Integer> c = (Vector<Integer>) targetPos.clone();
						corresp.put(pos,c);
						extr.setCorrespcorrel(corresp);
					}
					if(eventName.equals(target)){
						numTarg ++;
						targetPos.add(pos);
					}
					pos++;
				}

				extr.setNonambi(numbAmb);
				extr.setCorrespcorrel(corresp);
				output.add(extr);
			}
		}





		if(template.equals(DeclareTemplate.Alternate_Precedence)){

			for(XTrace trace: log){
				int pos = 0;
				ExtendedTrace extr = new ExtendedTrace();
				extr.setTrace(trace);
				extr.setNonambi(new Vector<Integer>());
				extr.setCorrespcorrel(new HashMap<Integer,Vector<Integer>>());
				int numbTarg = 0;
				int pendPos = 0;
				int actNum = 0;
				Vector<Integer> targetPos= new Vector<Integer>();
				//Vector<Integer> corresp = new Vector<Integer>();
				//Vector<Integer> nonambi = extr.getNonambi();
				for(XEvent event : trace){
					String eventName = XConceptExtension.instance().extractName(event);

					if(eventName.equals(target) ){

						if(numbTarg >= 1 && actNum ==1){
							Vector<Integer> nonambi = extr.getNonambi();
							nonambi.add(pendPos);
							extr.setNonambi(nonambi);

							HashMap<Integer, Vector<Integer>> corresp = extr.getCorrespcorrel();
							Vector<Integer> c = (Vector<Integer>) targetPos.clone();
							corresp.put(pendPos,c);
							extr.setCorrespcorrel(corresp);
						}
						if(actNum>=1){
							numbTarg = 0;
							targetPos = new Vector<Integer>();
						}
						numbTarg ++;
						actNum = 0;
						targetPos.add(pos);
					}

					if(eventName.equals(activation)){
						actNum ++;
						//	numbTarg = 0;
						pendPos = pos;
					}

					//					if(eventName.equals(activation) && (numbTarg == 1) && (actNum ==1)){
					//						nonambi.add(pos);
					//						corresp.add(targPos);
					//					//	numbTarg = 0;
					//					}	

					pos++;
				}
				if(numbTarg >= 1 && actNum ==1){
					Vector<Integer> nonambi = extr.getNonambi();
					nonambi.add(pendPos);
					extr.setNonambi(nonambi);

					HashMap<Integer, Vector<Integer>> corresp = extr.getCorrespcorrel();
					Vector<Integer> c = (Vector<Integer>) targetPos.clone();
					corresp.put(pendPos,c);
					extr.setCorrespcorrel(corresp);
				}
				//	extr.setNonambi(nonambi);
				//	extr.setCorrespcorrel(corresp);
				output.add(extr);
			}
		}



		return output;
	}



}

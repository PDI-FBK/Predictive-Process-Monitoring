package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.processmining.plugins.declareminer.DeclareMiner;
import org.processmining.plugins.declareminer.DeclareMinerInput;
import org.processmining.plugins.declareminer.enumtypes.DeclareTemplate;
import org.processmining.plugins.declareminer.visualizing.ActivityDefinition;
import org.processmining.plugins.declareminer.visualizing.AssignmentModel;
import org.processmining.plugins.declareminer.visualizing.ConstraintDefinition;
import org.processmining.plugins.declareminer.visualizing.ConstraintTemplate;
import org.processmining.plugins.declareminer.visualizing.IItem;
import org.processmining.plugins.declareminer.visualizing.Language;
import org.processmining.plugins.declareminer.visualizing.LanguageGroup;
import org.processmining.plugins.declareminer.visualizing.Parameter;
import org.processmining.plugins.declareminer.visualizing.TemplateBroker;
import org.processmining.plugins.declareminer.visualizing.XMLBrokerFactory;
import org.processmining.plugins.predictive_monitor.bpm.utility.Print;

public class DeclareTimeUtilManager {
	private static Print print = new Print();
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String[] params = new String[]{"a", "b"};
		DeclareTemplate currentTemplate = DeclareTemplate.Response;
		ConstraintDefinition cd = getConstraintDefinition(params, currentTemplate);
		print.thatln(cd.toString());

	}

	public static ConstraintDefinition getConstraintDefinition(String[] params, DeclareTemplate currentTemplate){
		Map<String, DeclareTemplate> templateNameStringDeclareTemplateMap = new HashMap<String, DeclareTemplate>();
		DeclareTemplate[] declareTemplates = DeclareTemplate.values();
		for(DeclareTemplate d : declareTemplates){
			String templateNameString = d.toString().replaceAll("_", " ").toLowerCase();
			templateNameStringDeclareTemplateMap.put(templateNameString, d);
		}

		Map<DeclareTemplate, ConstraintTemplate> declareTemplateConstraintTemplateMap = DeclareMiner.readConstraintTemplates(templateNameStringDeclareTemplateMap);

		InputStream ir = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/template.xml");
		File language = null;
		try {
			language = File.createTempFile("template", ".xml");
			BufferedReader br = new BufferedReader(new InputStreamReader(ir));
			String line = br.readLine();
			PrintStream out = new PrintStream(language);
			while (line != null) {
				out.println(line);
				line = br.readLine();
			}
			out.flush();
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		TemplateBroker template = XMLBrokerFactory.newTemplateBroker(language.getAbsolutePath());
		List<Language> languages = template.readLanguages();
		Language lang = languages.get(0);
		AssignmentModel model = new AssignmentModel(lang);
		model.setName("new model");
		ActivityDefinition activitydefinition = null;
		int constraintID = 0;
		int activityID = 1;
		for(String par : params){
			activitydefinition = model.addActivityDefinition(activityID); //new ActivityDefinition(parametersOfDiscoveredConstraintsInstantiationsOfCurrentTemplate[i][j], activityID, model);
			activitydefinition.setName(par);
			activityID++;
		}
		constraintID++;
		ConstraintDefinition constraintdefinition = new ConstraintDefinition(constraintID, model, declareTemplateConstraintTemplateMap.get(currentTemplate));
		Collection<Parameter> parameters = (declareTemplateConstraintTemplateMap.get(currentTemplate)).getParameters();
		int h = 0;
		for (Parameter parameter : parameters) {
			ActivityDefinition activityDefinition = model.activityDefinitionWithName(params[h]);
			constraintdefinition.addBranch(parameter, activityDefinition);
			h++;
		}
		return constraintdefinition;
	}


	public static Map<DeclareTemplate, ConstraintTemplate> readConstraintTemplates(Map<String, DeclareTemplate> templateNameStringDeclareTemplateMap){
		InputStream templateInputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/template.xml");
		File languageFile = null;
		try {
			languageFile = File.createTempFile("template", ".xml");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(templateInputStream));
			String line = bufferedReader.readLine();
			PrintStream out = new PrintStream(languageFile);
			while (line != null) {
				out.println(line);
				line = bufferedReader.readLine();
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		TemplateBroker templateBroker = XMLBrokerFactory.newTemplateBroker(languageFile.getAbsolutePath());
		List<Language> languagesList = templateBroker.readLanguages();

		//the first language in the list is the condec language, which is what we need
		Language condecLanguage = languagesList.get(0);
		List<IItem> templateList = new ArrayList<IItem>();
		List<IItem> condecLanguageChildrenList = condecLanguage.getChildren();
		for (IItem condecLanguageChild : condecLanguageChildrenList) {
			if (condecLanguageChild instanceof LanguageGroup) {
				templateList.addAll(visit(condecLanguageChild));
			} else {
				templateList.add(condecLanguageChild);
			}
		}

		Map<DeclareTemplate, ConstraintTemplate> declareTemplateConstraintTemplateMap = new HashMap<DeclareTemplate, ConstraintTemplate>();

		for(IItem item : templateList){
			if(item instanceof ConstraintTemplate){
				ConstraintTemplate constraintTemplate = (ConstraintTemplate)item;
				//				System.out.println(constraintTemplate.getName()+" @ "+constraintTemplate.getDescription()+" @ "+constraintTemplate.getText());
				if(templateNameStringDeclareTemplateMap.containsKey(constraintTemplate.getName().replaceAll("-", "").toLowerCase())){
					declareTemplateConstraintTemplateMap.put(templateNameStringDeclareTemplateMap.get(constraintTemplate.getName().replaceAll("-", "").toLowerCase()), constraintTemplate);
					//if(ServerConfigurationClass.printDebug)System.out.println(constraintTemplate.getName()+" @ "+templateNameStringDeclareTemplateMap.get(constraintTemplate.getName().replaceAll("-", "").toLowerCase()));
				}
			}
		}

		return declareTemplateConstraintTemplateMap;
	}
	
	private static List<IItem> visit(IItem item){
		List<IItem> templateList = new ArrayList<IItem>();
		if (item instanceof LanguageGroup) {
			LanguageGroup languageGroup = (LanguageGroup) item;
			List<IItem> childrenList = languageGroup.getChildren();
			for (IItem child : childrenList) {
				if (child instanceof LanguageGroup) {
					templateList.addAll(visit(child));
				}else {
					templateList.add(child);
				}
			}
		}
		return templateList;
	}

	private static boolean isBinary(DeclareTemplate template){
		return  template.equals(DeclareTemplate.Alternate_Precedence) || template.equals(DeclareTemplate.Alternate_Response) ||
				template.equals(DeclareTemplate.Alternate_Succession) || template.equals(DeclareTemplate.Chain_Precedence) ||
				template.equals(DeclareTemplate.Chain_Response) || template.equals(DeclareTemplate.Chain_Succession) ||
				template.equals(DeclareTemplate.CoExistence) || template.equals(DeclareTemplate.Precedence) ||
				template.equals(DeclareTemplate.Responded_Existence) || template.equals(DeclareTemplate.Response) ||
				template.equals(DeclareTemplate.Succession) || template.equals(DeclareTemplate.Exclusive_Choice) || template.equals(DeclareTemplate.Not_CoExistence)
				|| template.equals(DeclareTemplate.Not_Succession) || template.equals(DeclareTemplate.Not_Chain_Succession)
				|| template.equals(DeclareTemplate.Choice);
	}

	
	public static DeclareMinerInput getDeclareMinerInput(){
		DeclareMinerInput input = new DeclareMinerInput();
		return input;
	}

}

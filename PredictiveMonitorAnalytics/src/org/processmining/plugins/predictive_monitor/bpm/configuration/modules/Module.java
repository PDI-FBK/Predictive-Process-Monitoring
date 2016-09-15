package org.processmining.plugins.predictive_monitor.bpm.configuration.modules;

import java.util.List;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;

public interface Module {
	public String getModuleName();
	public List<Parameter> getParameterList();
}

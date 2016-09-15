package org.processmining.plugins.predictive_monitor.bpm.client_interface.resultPage;

public class ConfigurationPair {
    private String type;

    private Object value;

    ConfigurationPair(String type, Object object) {

        this.type = type;

        this.value = object;

    }

     

    public String getType() { return type; }

    public Object getValue() { return value; }

}

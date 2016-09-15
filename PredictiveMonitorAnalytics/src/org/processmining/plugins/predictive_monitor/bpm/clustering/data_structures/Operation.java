package org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures;

public enum Operation {
    NOOP   (0, "initial", 0, 0),
    OK  (0, "right",   1, 1),
    REPLACE(1, "replace", 1, 1),
    DELETE (1, "delete",  0, 1),
    INSERT (1, "insert",  1, 0);

    public final int cost;
    public final String text;
    public final int deltaI;
    public final int deltaJ;

    Operation(int cost, String text, int deltaI, int deltaJ) {
        this.cost = cost;
        this.text = text;
        this.deltaI = deltaI;
        this.deltaJ = deltaJ;
    }
    
    
}

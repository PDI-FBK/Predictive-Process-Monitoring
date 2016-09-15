package org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures;



public class Entry {

        private int cost;
        private Operation op;

        public Entry(int cost, Operation op) {
            this.cost = cost;
            this.op = op;
        }

		public int getCost() {
			return cost;
		}

		public Operation getOp() {
			return op;
		}

        
}

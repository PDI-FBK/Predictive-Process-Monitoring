package org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures;

	
public class Tuple<X, Y extends Comparable<Y>> implements
		Comparable<Tuple<X, Y>> {
	public final X x;
	public final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(Tuple<X, Y> other) {
		return this.y.compareTo(((Tuple<X, Y>) other).y);
	}

}




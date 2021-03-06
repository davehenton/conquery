package com.bakdata.conquery.models.query.queryplan.aggregators;

import com.bakdata.conquery.models.events.Block;
import com.bakdata.conquery.models.query.queryplan.EventIterating;

public interface Aggregator<T> extends Cloneable, EventIterating {

	T getAggregationResult();

	public void aggregateEvent(Block block, int event);
	
	public Aggregator<T> clone();
}

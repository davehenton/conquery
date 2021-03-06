package com.bakdata.conquery.models.concepts.select;

import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.query.queryplan.aggregators.Aggregator;
import com.bakdata.conquery.models.query.queryplan.aggregators.specific.PrefixTextAggregator;
import com.bakdata.conquery.models.query.select.Select;

import lombok.Getter;
import lombok.Setter;

@CPSType(id = "PREFIX", base = Select.class)
public class PrefixSelect extends ColumnSelect {

	@Getter
	@Setter
	private String selection;

	@Override
	protected Aggregator<?> createAggregator() {
		return new PrefixTextAggregator(getColumn(), selection);
	}
}

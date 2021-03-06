package com.bakdata.conquery.models.concepts.select;

import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.query.queryplan.aggregators.Aggregator;
import com.bakdata.conquery.models.query.queryplan.aggregators.specific.MultiSelectAggregator;
import com.bakdata.conquery.models.query.queryplan.aggregators.specific.SelectAggregator;
import com.bakdata.conquery.models.query.select.Select;

import lombok.Getter;
import lombok.Setter;

@CPSType(id = "SELECT", base = Select.class)
public class SelectSelect extends ColumnSelect {

	@Getter
	@Setter
	private String[] selection;

	@Override
	protected Aggregator<?> createAggregator() {
		if (selection.length == 1) {
			return new SelectAggregator(getColumn(), selection[0]);
		}

		return new MultiSelectAggregator(getColumn(), selection);
	}
}

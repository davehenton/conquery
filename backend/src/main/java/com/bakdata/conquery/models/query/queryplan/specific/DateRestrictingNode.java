package com.bakdata.conquery.models.query.queryplan.specific;

import java.util.Objects;

import com.bakdata.conquery.models.common.CDateSet;
import com.bakdata.conquery.models.datasets.Column;
import com.bakdata.conquery.models.datasets.Table;
import com.bakdata.conquery.models.events.Block;
import com.bakdata.conquery.models.query.QueryContext;
import com.bakdata.conquery.models.query.queryplan.QPChainNode;
import com.bakdata.conquery.models.query.queryplan.QPNode;
import com.bakdata.conquery.models.query.queryplan.QueryPlan;

public class DateRestrictingNode extends QPChainNode {

	private final CDateSet dateRange;
	private Column validityDateColumn;

	public DateRestrictingNode(CDateSet dateRange, QPNode child) {
		super(child);
		this.dateRange = dateRange;
	}

	@Override
	public void nextTable(QueryContext ctx, Table currentTable) {
		CDateSet dateRestriction = CDateSet.create(ctx.getDateRestriction());
		dateRestriction.retainAll(dateRange);
		
		super.nextTable(
			ctx.withDateRestriction(dateRestriction),
			currentTable
		);


		validityDateColumn = Objects.requireNonNull(context.getValidityDateColumn());

		if (!validityDateColumn.getType().isDateCompatible()) {
			throw new IllegalStateException("The validityDateColumn " + validityDateColumn + " is not a DATE TYPE");
		}
	}


	@Override
	public boolean nextEvent(Block block, int event) {
		if (block.eventIsContainedIn(event, validityDateColumn, dateRange)) {
			return getChild().aggregate(block, event);
		}
		return true;
	}

	@Override
	public boolean isContained() {
		return getChild().isContained();
	}

	@Override
	public QPNode clone(QueryPlan plan, QueryPlan clone) {
		return new DateRestrictingNode(dateRange, getChild().clone(plan, clone));
	}
}

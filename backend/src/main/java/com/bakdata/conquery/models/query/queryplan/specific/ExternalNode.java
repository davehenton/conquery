package com.bakdata.conquery.models.query.queryplan.specific;

import java.util.Set;

import org.hibernate.validator.constraints.NotEmpty;

import com.bakdata.conquery.ConqueryConstants;
import com.bakdata.conquery.models.common.CDateSet;
import com.bakdata.conquery.models.datasets.Table;
import com.bakdata.conquery.models.events.Block;
import com.bakdata.conquery.models.identifiable.ids.specific.DatasetId;
import com.bakdata.conquery.models.identifiable.ids.specific.TableId;
import com.bakdata.conquery.models.query.QueryContext;
import com.bakdata.conquery.models.query.entity.Entity;
import com.bakdata.conquery.models.query.queryplan.QPChainNode;
import com.bakdata.conquery.models.query.queryplan.QPNode;
import com.bakdata.conquery.models.query.queryplan.QueryPlan;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;

public class ExternalNode extends QPChainNode {

	@Getter
	private final DatasetId dataset;
	@Getter @NotEmpty
	private final Int2ObjectMap<CDateSet> includedEntities;
	private CDateSet contained;
	
	public ExternalNode(QPNode child, DatasetId dataset, Int2ObjectMap<CDateSet> includedEntities) {
		super(child);
		this.dataset = dataset;
		this.includedEntities = includedEntities;
	}

	@Override
	public void init(Entity entity) {
		super.init(entity);
		contained = includedEntities.get(entity.getId());
	}
	
	@Override
	public ExternalNode clone(QueryPlan plan, QueryPlan clone) {
		return new ExternalNode(getChild().clone(plan, clone), dataset, includedEntities);
	}
	
	@Override
	public void nextTable(QueryContext ctx, Table currentTable) {
		if(contained != null) {
			CDateSet newSet = CDateSet.create(ctx.getDateRestriction());
			newSet.retainAll(contained);
			super.nextTable(
				ctx.withDateRestriction(newSet),
				currentTable
			);
		}
		else
			super.nextTable(ctx, currentTable);
	}

	@Override
	public boolean nextEvent(Block block, int event) {
		if(contained != null) {
			return getChild().nextEvent(block, event);
		}
		else {
			return false;
		}
	}
	
	@Override
	public boolean isContained() {
		return getChild().isContained();
	}
	
	@Override
	public void collectRequiredTables(Set<TableId> requiredTables) {
		super.collectRequiredTables(requiredTables);
		//add the allIdsTable
		requiredTables.add(
			new TableId(
				dataset,
				ConqueryConstants.ALL_IDS_TABLE
			)
		);
	}
}

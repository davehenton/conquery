package com.bakdata.conquery.models.query.concept.specific;

import javax.validation.constraints.NotNull;

import com.bakdata.conquery.ConqueryConstants;
import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.common.CDateSet;
import com.bakdata.conquery.models.identifiable.ids.specific.DatasetId;
import com.bakdata.conquery.models.identifiable.ids.specific.TableId;
import com.bakdata.conquery.models.query.QueryPlanContext;
import com.bakdata.conquery.models.query.concept.CQElement;
import com.bakdata.conquery.models.query.queryplan.QPNode;
import com.bakdata.conquery.models.query.queryplan.QueryPlan;
import com.bakdata.conquery.models.query.queryplan.aggregators.specific.SpecialDateUnion;
import com.bakdata.conquery.models.query.queryplan.specific.ExternalNode;
import com.bakdata.conquery.models.query.queryplan.specific.SpecialDateUnionAggregatorNode;
import com.fasterxml.jackson.annotation.JsonCreator;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@CPSType(id="EXTERNAL_RESOLVED", base=CQElement.class)
@RequiredArgsConstructor(onConstructor_=@JsonCreator)
public class CQExternalResolved implements CQElement {

	@Getter @NotNull @NonNull
	private final Int2ObjectMap<CDateSet> values;

	@Override
	public QPNode createQueryPlan(QueryPlanContext context, QueryPlan plan) {
		DatasetId dataset = context.getWorker().getStorage().getDataset().getId();
		return new ExternalNode(
			new SpecialDateUnionAggregatorNode(
				new TableId(
					dataset,
					ConqueryConstants.ALL_IDS_TABLE
				),
				(SpecialDateUnion) plan.getAggregators().get(0)
			),
			dataset,
			values);
	}
}

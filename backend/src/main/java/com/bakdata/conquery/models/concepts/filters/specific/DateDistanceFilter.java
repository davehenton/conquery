package com.bakdata.conquery.models.concepts.filters.specific;

import java.util.EnumSet;

import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.api.description.FEFilter;
import com.bakdata.conquery.models.api.description.FEFilterType;
import com.bakdata.conquery.models.concepts.filters.Filter;
import com.bakdata.conquery.models.concepts.filters.SingleColumnFilter;
import com.bakdata.conquery.models.exceptions.ConceptConfigurationException;
import com.bakdata.conquery.models.query.concept.filter.FilterValue.CQIntegerRangeFilter;
import com.bakdata.conquery.models.query.filter.event.DateDistanceFilterNode;
import com.bakdata.conquery.models.query.queryplan.filter.FilterNode;
import com.bakdata.conquery.models.types.MajorTypeId;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * This filter represents a select in the front end. This means that the user can select one or more values from a list of values.
 */
@Getter @Setter @Slf4j
@CPSType(id="DATE_DISTANCE", base=Filter.class)
public class DateDistanceFilter extends SingleColumnFilter<CQIntegerRangeFilter> {

	
	
	@Override
	public EnumSet<MajorTypeId> getAcceptedColumnTypes() {
		return EnumSet.of(MajorTypeId.DATE);
	}
	
	@Override
	public void configureFrontend(FEFilter f) throws ConceptConfigurationException {
		switch (getColumn().getType()) {
			case DATE: {
				f.setType(FEFilterType.INTEGER_RANGE);
				return;
			}
			default:
				throw new ConceptConfigurationException(getConnector(), "DATE_DISTANCE filter is incompatible with columns of type " + getColumn().getType());
		}
	}
	
	@Override
	public FilterNode createAggregator(CQIntegerRangeFilter filterValue) {
		return new DateDistanceFilterNode(this, filterValue);
	}
}
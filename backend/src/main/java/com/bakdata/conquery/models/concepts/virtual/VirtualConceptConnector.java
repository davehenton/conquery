package com.bakdata.conquery.models.concepts.virtual;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.bakdata.conquery.io.jackson.serializer.IdReference;
import com.bakdata.conquery.models.concepts.Connector;
import com.bakdata.conquery.models.concepts.filters.Filter;
import com.bakdata.conquery.models.datasets.Table;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VirtualConceptConnector extends Connector {

	private static final long serialVersionUID = 1L;

	@NotNull @IdReference
	private Table table;
	@Valid @JsonManagedReference
	private Filter<?> filter;
	
	@Override
	public Collection<Filter<?>> collectAllFilters() {
		return Stream.of(getDateSelectionFilter(), filter).filter(Objects::nonNull).collect(Collectors.toList());
	}
/*
	@Override
	public EventProcessingResult processEvent(Event r) {
		CDateRange dateRange = extractValidityDates(r);
		getConcept().incMatchingEntries(dateRange);
		return EventProcessingResult.OK;
	}*/
}

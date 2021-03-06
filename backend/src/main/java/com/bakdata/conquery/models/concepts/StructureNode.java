package com.bakdata.conquery.models.concepts;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.bakdata.conquery.io.jackson.serializer.IdReferenceCollection;
import com.bakdata.conquery.models.common.KeyValue;
import com.bakdata.conquery.models.datasets.Dataset;
import com.bakdata.conquery.models.identifiable.Labeled;
import com.bakdata.conquery.models.identifiable.ids.specific.StructureNodeId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString(callSuper=true,of={"children"})
public class StructureNode extends Labeled<StructureNodeId> {

	public static final String MANAGED_STRUCTURE_STRUCTURE = "structure_structure";
	public static final String MANAGED_DATASET_STRUCTURE = "dataset_structure";
	
	@JsonBackReference(MANAGED_DATASET_STRUCTURE)
	private Dataset dataset;
	private String description;
	@Valid @JsonManagedReference(MANAGED_STRUCTURE_STRUCTURE)
	private List<StructureNode> children = Collections.emptyList();
	@JsonBackReference(MANAGED_STRUCTURE_STRUCTURE)
	private StructureNode parent;
	@IdReferenceCollection
	private List<Concept<?>> containedRoots = Collections.emptyList();
	private List<KeyValue> additionalInfos = Collections.emptyList();
	
	@Override
	public StructureNodeId createId() {
		return new StructureNodeId(parent.getId(), getName());
	}
	
	public Stream<StructureNode> streamAllChildren() {
		return Stream.concat(
			Stream.of(this),
			children.stream().flatMap(StructureNode::streamAllChildren)
		);
	}
}

package com.bakdata.conquery.models.messages.namespaces.specific;

import com.bakdata.conquery.io.cps.CPSType;
import com.bakdata.conquery.models.datasets.Import;
import com.bakdata.conquery.models.messages.namespaces.NamespacedMessage;
import com.bakdata.conquery.models.messages.namespaces.WorkerMessage;
import com.bakdata.conquery.models.worker.Worker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@CPSType(id="ADD_IMPORT", base=NamespacedMessage.class)
@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class AddImport extends WorkerMessage.Slow {

	private Import imp;

	@Override
	public void react(Worker context) throws Exception {
		context.getStorage().addImport(imp);
	}

}

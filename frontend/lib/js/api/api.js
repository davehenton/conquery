// @flow

import fetch                   from 'isomorphic-fetch';
import { type DatasetIdType }  from '../dataset/reducer';
import { type TreeNodeIdType } from '../category-trees/reducer';
import { getStoredAuthToken }  from '../authorization';

import { API_URL }             from '../environment';
import {
  transformQueryToApi,
} from './apiHelper';

import {
  transformFormQueryToApi,
} from './apiFormHelper';

type RequestType = {
  body?: Object | string,
  headers?: Object
};

function fetchJsonUnauthorized(url: string, request?: RequestType, rawBody?: boolean = false) {
  const finalRequest = request
    ? {
        ...request,
        body: rawBody ? request.body : JSON.stringify(request.body),
        headers: {
          "Accept": "application/json",
          "Content-Type": "application/json",
          ...request.headers,
        },
      }
    : {
        method: "GET",
        headers: {
          "Accept": "application/json",
        },
      };

  return fetch(url, finalRequest).then(
    response => {
      if (response.status >= 200 && response.status < 300)
        return response.json().catch(e => e); // Also handle empty responses
      else
        // Reject other status
        return response.json().then(Promise.reject.bind(Promise));
    },
    error => Promise.reject(error) // Network or connection failure
  );
}

function fetchJson(url: string, request?: RequestType, rawBody?: boolean = false) {
  const authToken = getStoredAuthToken() || '';
  const finalRequest = {
    ...(request || {}),
    headers: {
      "Authorization": `Bearer ${authToken}`,
      ...((request && request.headers) || {}),
    }
  };

  return fetchJsonUnauthorized(url, finalRequest, rawBody);
}

export function getDatasets() {
  return fetchJson(API_URL + `/datasets`);
}

export function postResults(datasetId: DatasetIdType, file: any) {
  return new Promise(resolve => {
    const reader = new FileReader();

    reader.onload = (evt) => {
      const fileContent = evt.target.result;

      resolve(fileContent);
    };

    reader.readAsText(file);
  }).then((results) => {
    const rawBody = true;

    return fetchJson(API_URL + `/datasets/${datasetId}/import`, {
      method: "POST",
      body: results,
      headers: {
        "Content-Type": "text/csv"
      }
    }, rawBody);
  });
};

export function getConcepts(datasetId: DatasetIdType) {
  return fetchJson(API_URL + `/datasets/${datasetId}/concepts`);
}

export function getConcept(datasetId: DatasetIdType, conceptId: TreeNodeIdType) {
  return fetchJson(API_URL + `/datasets/${datasetId}/concepts/${conceptId}`);
}

// Same signature as postFormQueries
export function postQueries(
  datasetId: DatasetIdType,
  query: Object,
  queryType: string,
  version: any
) {
  // Transform into backend-compatible format
  const body = transformQueryToApi(query, queryType, version);

  return fetchJson(API_URL + `/datasets/${datasetId}/queries`, {
    method: "POST",
    body,
  });
}

export function deleteQuery(datasetId: DatasetIdType, queryId: number) {
  return fetchJson(API_URL + `/datasets/${datasetId}/queries/${queryId}`, {
    method: 'DELETE',
  });
}

export function getQuery(datasetId: DatasetIdType, queryId: number) {
  return fetchJson(API_URL + `/datasets/${datasetId}/queries/${queryId}`);
}

// Same signature as postQueries
export function postFormQueries(
  datasetId: DatasetIdType,
  query: Object,
  queryType: string,
  version: any
) {
  // Transform into backend-compatible format
  const body = transformFormQueryToApi(query, version);

  return fetchJson(API_URL + `/datasets/${datasetId}/form-queries`, {
    method: "POST",
    body,
  });
}

export function deleteFormQuery(datasetId: DatasetIdType, queryId: number) {
  return fetchJson(API_URL + `/datasets/${datasetId}/form-queries/${queryId}`, {
    method: 'DELETE',
  });
}

export function getFormQuery(datasetId: DatasetIdType, queryId: number) {
  return fetchJson(API_URL + `/datasets/${datasetId}/form-queries/${queryId}`);
}

export function getStoredQueries(datasetId: DatasetIdType) {
  return fetchJson(API_URL + `/datasets/${datasetId}/stored-queries`);
}

export function getStoredQuery(datasetId: DatasetIdType, queryId: number) {
  return fetchJson(API_URL + `/datasets/${datasetId}/stored-queries/${queryId}`);
}

export function deleteStoredQuery(datasetId: DatasetIdType, queryId: number) {
  return fetchJson(API_URL + `/datasets/${datasetId}/stored-queries/${queryId}`, {
    method: 'DELETE',
  });
}

export function patchStoredQuery(datasetId: DatasetIdType, queryId: number, attributes: Object) {
  return fetchJson(API_URL + `/datasets/${datasetId}/stored-queries/${queryId}`, {
    method: 'PATCH',
    body: attributes,
  });
}

export function postPrefixForSuggestions(
  datasetId: DatasetIdType,
  conceptId: string,
  tableId: string,
  filterId: string,
  text: string,
) {
  return fetchJson(
    API_URL +
    `/datasets/${datasetId}/concepts/${conceptId}` +
    `/tables/${tableId}/filters/${filterId}/autocomplete`,
    {
      method: 'POST',
      body: { text },
    }
  );
};

export type ConceptListResolutionResultType = {
  resolvedConcepts?: String[],
  unknownCodes?: String[],
  resolvedFilter?: {
    filterId: String,
    tableId: String,
    value: {
      label: String,
      value: String
    }[]
  }
};

export function postConceptsListToResolve(
  datasetId: DatasetIdType,
  conceptId: string,
  concepts: string[],
): ConceptListResolutionResultType {
  return fetchJson(API_URL + `/datasets/${datasetId}/concepts/${conceptId}/resolve`, {
    method: 'POST',
    body: { concepts },
  });
};

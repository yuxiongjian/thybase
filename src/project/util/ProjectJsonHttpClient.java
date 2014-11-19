package project.util;

import java.util.Date;
import java.util.UUID;

import org.alexd.jsonrpc.JSONRPCException;
import org.alexd.jsonrpc.JSONRPCHttpClient;
import org.json.*;

public class ProjectJsonHttpClient extends JSONRPCHttpClient {

	public ProjectJsonHttpClient(String uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	protected static JSONArray getJSONArray( Object[] params) {
		JSONArray jsonParams = new JSONArray();
		for (Object item : params) {
			if (item != null && item.getClass().isArray()) {
				jsonParams.put(getJSONArray((Object[]) item));
			} else {
				if ( item.getClass().equals(java.util.Date.class))
				{
					jsonParams.put(Func.dateToString((Date)item));
				}
				else
					jsonParams.put(item);
			}
		}
		return jsonParams;
	}
	
	protected JSONObject doRequest(String method, Object[] params)
			throws JSONRPCException {
		// Copy method arguments in a json array
		JSONArray jsonParams = getJSONArray(params);
		

		// Create the json request object
		JSONObject jsonRequest = new JSONObject();
		
		//JSONObject jsonRequest = Func.createGson();
		try {
			jsonRequest.put("id", UUID.randomUUID().hashCode());
			jsonRequest.put("method", method);
			jsonRequest.put("params", jsonParams);
		} catch (JSONException e1) {
			throw new JSONRPCException("Invalid JSON request", e1);
		}
		return doJSONRequest(jsonRequest);
	}

}
package acr.browser.barebones.activities;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import android.os.AsyncTask;
import android.util.Log;

public class RetrieveRDFResourceTask extends
		AsyncTask<String, Void, Model> {

	@Override
	protected Model doInBackground(String... params) {
		
		String query = params[0];
		// default is turtle
		String fileType = "TURTLE"; 
		if (query == null)
			return null;
		if (query.endsWith(".ttl"))
			fileType = "TURTLE";
		else if (query.endsWith(".rdf"))
			fileType = "RDF/XML";
		
		
		HttpClient httpclient = new DefaultHttpClient();
	    
		HttpGet req = new HttpGet(query);
		req.addHeader("Accept", "text/turtle,text/n3,application/rdf+xml");
		
		HttpResponse response;
		try {
			response = httpclient.execute(req);
			StatusLine statusLine = response.getStatusLine();
		    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		        response.getEntity().writeTo(out);
		        
		        // parse retrieved RDF resource
		        Model model = ModelFactory.createDefaultModel();
		        model.read(new ByteArrayInputStream(out.toByteArray()), "http://example",  fileType);
//		        out.close();
		        return model;
		        
		    } else{
		        //Closes the connection.
		        response.getEntity().getContent().close();
		        Log.e("PRISSMA", "Error retrieving RDF resource: " + statusLine.getReasonPhrase());
				return null;
		    }
		} catch (Exception e) {
			Log.e("PRISSMA", "Exception loading RDF resource: " + e.getMessage());
			return null;
		} 
	}


}

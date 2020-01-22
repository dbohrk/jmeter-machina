package jmeter.createkey;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.iamcredentials.v1.IAMCredentials;
import com.google.api.services.iamcredentials.v1.model.GenerateIdTokenRequest;
import com.google.api.services.iamcredentials.v1.model.GenerateIdTokenResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class CreateToken extends AbstractJavaSamplerClient {

	@Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();

        // Start the timer!
        result.sampleStart();

        // Create token
        try {
        final HttpTransport httpTransport = new NetHttpTransport();

    	InputStream inStrm = new FileInputStream("goodcreds.json");

        ServiceAccountCredentials creds = ServiceAccountCredentials.fromStream(inStrm);
        System.out.printf("loaded creds\n");

        boolean scopedRequired = creds.createScopedRequired();
        System.out.printf("createScopedReq: %s\n", scopedRequired);

        GoogleCredentials gCreds = creds.createScoped("https://www.googleapis.com/auth/cloud-platform");
        System.out.printf("created scoped\n");

        HttpCredentialsAdapter adapter = new HttpCredentialsAdapter(gCreds);
        System.out.printf("created adapter\n");

        JsonFactory facJson = JacksonFactory.getDefaultInstance();
        IAMCredentials iamCreds = new IAMCredentials.Builder(httpTransport, facJson, adapter)
            .setApplicationName("iamcredentials")
            .build();
        System.out.printf("created iamCreds\n");

        GenerateIdTokenRequest req = new GenerateIdTokenRequest();
        String URL = context.getJMeterProperties().getProperty("URL");
        System.out.printf("URL: %s\n", URL);
        req.setAudience(URL);
        //req.setAudience("https://ekms.integrations.ionic.engineering");
        System.out.printf("set audience\n");

        List <String> lstDelgs = new ArrayList <String> ();
        req.setDelegates(lstDelgs);
        req.setIncludeEmail(true);
        System.out.printf("constructed delegates\n");

        IAMCredentials.Projects.ServiceAccounts.GenerateIdToken token;
        token = iamCreds.projects().serviceAccounts()
            .generateIdToken("projects/-/serviceAccounts/goodcreds-901@ionic-ekms.iam.gserviceaccount.com", req);
        System.out.printf("token created\n");

        GenerateIdTokenResponse resp = token.execute();
        String full_token = resp.getToken();
        System.out.printf("response: %s\n", full_token);
        context.getJMeterVariables().put("Jwt", full_token);
        
        result.sampleEnd();
        result.setSuccessful(true);
        result.setResponseCodeOK();
        result.setResponseMessage(full_token);

        return result;
        }
        
        catch (FileNotFoundException fnfe){
            result.sampleEnd();
        	fnfe.printStackTrace();
            result.setSuccessful(false);
        	return result;
        } 
        
        catch(IOException e) {
            result.sampleEnd();
        	e.printStackTrace();
            result.setSuccessful(false);
        	return result;        	
        }
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
        // TODO Auto-generated method stub
    }
}
package jmeter.createkey;
 
 
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.getkey.GetKeysRequest;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.error.IonicException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
 
public class FetchMultipleKeys extends AbstractJavaSamplerClient {
	
    String persistorPath;
    String keyCount;
    String keyFilePath;
	GetKeysRequest getKeysRequest = new GetKeysRequest();
	
    @Override
    public void setupTest(JavaSamplerContext context){
        super.setupTest(context);
        persistorPath = context.getJMeterVariables().get("sep_location");
        keyCount = context.getJMeterProperties().getProperty("keyCount");
        keyFilePath = context.getJMeterProperties().getProperty("keyFilePath");
        try {
			BufferedReader br = new BufferedReader(new FileReader(keyFilePath));
			while( true ) {
			    String line = br.readLine();
			    if ( line == null || getKeysRequest.getKeyIds().size() 
			    		>= Integer.parseInt(keyCount)) break;
			    getKeysRequest.add(line);
			}
			br.close();
			System.out.println("Key IDs: " + getKeysRequest.getKeyIds());
		} catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
    }
    
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        Agent agent = new Agent();
        SampleResult result = new SampleResult();
        //System.out.println("external ID:\n" + extId);
        //System.out.println("SEP Location:\n" + persistorPath);
        
        try {
        	DeviceProfilePersistorPlainText persistor = new DeviceProfilePersistorPlainText();
        	persistor.setFilePath(persistorPath);
            agent.initialize(persistor);
            //System.out.println("Java SDK Agent initialized");
        } catch(IonicException e) {
            System.out.println("Ionic init exception");
            System.out.println(e.getMessage());
            System.exit(1);
        }
        
        //get multiple keys
        //GetKeysRequest getKeysRequest = new GetKeysRequest();
        //getKeysRequest.addExternalId(extId);;
        //List<GetKeysResponse.Key> keys = null;
        GetKeysResponse keys = null;

        // Start the timer!
        result.sampleStart();
 
        // get a single key
        try {
            keys = agent.getKeys(getKeysRequest);
            if (keys.getKeys().size() == 0) {
                System.out.println("There were no keys or access was denied to the keys");
                System.exit(1);
            }
            
        } catch(IonicException e) {
            result.sampleEnd(); // Stop timer
            result.setSuccessful(false);
            result.setResponseMessage( "Get Key Exception: " + e );
            return result;
        }
 
        // Stop the timer!
        result.sampleEnd();
        result.setSuccessful(true);
        result.setResponseCodeOK();
        result.setResponseMessage(keys.getConversationId());
        //result.setResponseMessage(extId);
        return result;
}
 
    @Override
    public void teardownTest(JavaSamplerContext context){
        // TODO Auto-generated method stub
    }
}
package jmeter.createkey;
 
 
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.createkey.CreateKeysRequest;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.error.IonicException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
 
public class CreateBatchKeyTests extends AbstractJavaSamplerClient {

    @Override
    public void setupTest(JavaSamplerContext context){
        super.setupTest(context);
    } 
    
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        Agent agent = new Agent();
        SampleResult result = new SampleResult();
        String persistorPath = context.getJMeterVariables().get("sep_location");
        int keyCount = context.getIntParameter("keyCount");

 
        try {
        	DeviceProfilePersistorPlainText persistor = new DeviceProfilePersistorPlainText();
        	persistor.setFilePath(persistorPath);
            agent.initialize(persistor);
            //System.out.println("Agent for Create initialized");
        } catch(IonicException e) {
            System.out.println("Ionic init exception");
            System.out.println(e.getMessage());
            System.exit(1);
        }
        
    	CreateKeysRequest.Key keysToCreate = new CreateKeysRequest.Key("samKeyRef", keyCount);
    	CreateKeysRequest createRequest = new CreateKeysRequest();
    	createRequest.add(keysToCreate);
    	List<CreateKeysResponse.Key> keys = null;
                
        // Start the timer!
        result.sampleStart();
 
        // create multiple keys        	
        try {
        	keys = agent.createKeys(createRequest).getKeys();        
        }
        catch(IonicException e) {
            result.sampleEnd(); // Stop timer
            result.setSuccessful(false);
            result.setResponseMessage( "Create Key Exception: " + e );
            return result;
        }
 
        // Stop the timer!
        result.sampleEnd();
        result.setSuccessful(true);
        try {
			FileWriter writer = new FileWriter(persistorPath + "batchOutput.txt");
	        for (CreateKeysResponse.Key key : keys) {
	        	writer.write(key.getId() + System.lineSeparator());
	    	}
	        writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

        return result;
}

    @Override
    public void teardownTest(JavaSamplerContext context) {
        // TODO Auto-generated method stub
    }
    
}

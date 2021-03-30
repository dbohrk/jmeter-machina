package jmeter.createkey;
 
 
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.getkey.GetKeysRequest;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.error.IonicException;
import java.util.List;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
 
public class FetchMultipleKeysByExtId extends AbstractJavaSamplerClient {
	
	private static String persistorPath;
	private static String extId;
	
    @Override
    public void setupTest(JavaSamplerContext context){
        super.setupTest(context);
    }
    
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        Agent agent = new Agent();
        SampleResult result = new SampleResult();
        persistorPath = context.getJMeterVariables().get("sep_location");
        extId = context.getJMeterProperties().getProperty("externalId");
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
        GetKeysRequest getKeysRequest = new GetKeysRequest();
        getKeysRequest.addExternalId(extId);;
        List<GetKeysResponse.Key> keys = null;
        
        // Start the timer!
        result.sampleStart();
 
        // get a single key
        try {
            keys = agent.getKeys(getKeysRequest).getKeys();
            if (keys.size() == 0) {
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
        //result.setResponseMessage(extId);
        return result;
}
 
    @Override
    public void teardownTest(JavaSamplerContext context){
        // TODO Auto-generated method stub
    }
}
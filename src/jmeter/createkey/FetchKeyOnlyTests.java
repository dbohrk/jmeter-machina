package jmeter.createkey;
 
 
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.error.IonicException;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
 
public class FetchKeyOnlyTests extends AbstractJavaSamplerClient {
	
	private static String persistorPath;
	private static String keyId;

    @Override
    public void setupTest(JavaSamplerContext context){
        super.setupTest(context);
    }
    
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        Agent agent = new Agent();
        SampleResult result = new SampleResult();
        String cid = null;
        persistorPath = context.getJMeterProperties().getProperty("sep_location");
        keyId = context.getJMeterProperties().getProperty("keyId");
        
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
        
        // Start the timer!
        result.sampleStart();
 
        // get a single key
        try {
            cid = agent.getKey(keyId).getConversationId();
            //System.out.println(cid);
            
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
        result.setResponseMessage(cid);
        return result;
}
 
    @Override
    public void teardownTest(JavaSamplerContext context){
        // TODO Auto-generated method stub
    }
}
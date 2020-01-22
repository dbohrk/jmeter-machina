package jmeter.createkey;
 
 
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.error.IonicException;
import javax.xml.bind.DatatypeConverter;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
 
public class FetchKeyTests extends AbstractJavaSamplerClient {
    @Override
    public void setupTest(JavaSamplerContext context){
        super.setupTest(context);
    }
 
 
    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("sep_location", "/Users/sam/Downloads/perf-sep.txt");
        return defaultParameters;
    }
 
    
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        String persistorPath     = context.getParameter("sep_location");
        String keyId = context.getJMeterVariables().get("keyId");

        Agent agent = new Agent();
        try {
        	DeviceProfilePersistorPlainText persistor = new DeviceProfilePersistorPlainText();
        	persistor.setFilePath(persistorPath);
            agent.initialize(persistor);
            //System.out.println("Agent initialized");
        } catch(IonicException e) {
            System.out.println("Ionic init exception");
            System.out.println(e.getMessage());
            System.exit(1);
        }
        
        SampleResult result = new SampleResult();
        boolean success = true;
 
 
        // Start the timer!
        result.sampleStart();
 
 
        // get a  single key
        String cid = null;
        try {
            cid = agent.getKey(keyId).getConversationId();
            
        } catch(IonicException e) {
            result.sampleEnd(); // Stop timer
            result.setSuccessful(false);
            result.setResponseMessage( "Get Key Exception: " + e );
            return result;
        }
 
 
        // Stop the timer!
        result.sampleEnd();
        result.setSuccessful(success);
        return result;
}
 
 
    @Override
    public void teardownTest(JavaSamplerContext context){
        // TODO Auto-generated method stub
    }
}
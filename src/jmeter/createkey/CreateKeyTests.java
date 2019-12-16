package jmeter.createkey;
 
 
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPassword;
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.error.IonicException;
import javax.xml.bind.DatatypeConverter;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
 
public class CreateKeyTests extends AbstractJavaSamplerClient {
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
 
 
        // create single key
        CreateKeysResponse.Key key = null;
        try {
        	//System.out.println("Create Key");
            key = agent.createKey().getKeys().get(0);
            //System.out.println(key);
            context.getJMeterVariables().put("keyId", key.getId());
            context.getJMeterVariables().put("KeyBytes", DatatypeConverter.printHexBinary(key.getKey()));
            context.getJMeterVariables().put("FixedAttrs", key.getAttributesSigBase64FromServer());
            context.getJMeterVariables().put("MutableAttrs", key.getMutableAttributesSigBase64FromServer());

            
        } catch(IonicException e) {
            result.sampleEnd(); // Stop timer
            result.setSuccessful(false);
            result.setResponseMessage( "Create Key Exception: " + e );
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
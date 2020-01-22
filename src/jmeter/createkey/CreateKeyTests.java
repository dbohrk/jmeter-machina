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

	private static String persistorType;
	private static String persistorPath;
	private static String keyId;
    Agent agent = new Agent();

    @Override
    public void setupTest(JavaSamplerContext context){
        super.setupTest(context);
        persistorPath = context.getJMeterProperties().getProperty("sep_location");
        keyId = context.getJMeterProperties().getProperty("keyId");
        persistorType = context.getJMeterProperties().getProperty("persistorType");
        
        try {
        	if (persistorType == "plaintext") {
            	DeviceProfilePersistorPlainText persistor = new DeviceProfilePersistorPlainText();
            	persistor.setFilePath(persistorPath);
                agent.initialize(persistor);
                System.out.println("Java SDK Agent initialized with PlainText SEP");
        	}
        	else if (persistorType == "password") {
            	DeviceProfilePersistorPassword persistor = new DeviceProfilePersistorPassword();
            	persistor.setFilePath(persistorPath);
                agent.initialize(persistor);
                System.out.println("Java SDK Agent initialized with Password SEP");
        	}
        	else {
        		System.out.println("Unable to find Persistor Type, exiting...");
        		System.exit(1);
        	}

            CreateKeysResponse.Key key = null;
            key = agent.createKey().getKeys().get(0);

        } catch(IonicException e) {
            System.out.println("Ionic init exception");
            System.out.println(e.getMessage());
            System.exit(1);
        }
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
    public void teardownTest(JavaSamplerContext context) {
        // TODO Auto-generated method stub
    }

}

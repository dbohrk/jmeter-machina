package jmeter.createkey;
 
 
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.data.MetadataMap;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.agent.request.createkey.CreateKeysRequest;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.error.IonicException;

import javax.xml.bind.DatatypeConverter;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
 
public class KeyAttributesTests2 extends AbstractJavaSamplerClient {

    @Override
    public void setupTest(JavaSamplerContext context){
        super.setupTest(context);
    } 
    
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        Agent agent = new Agent();
        SampleResult result = new SampleResult();
        String persistorPath = context.getJMeterVariables().get("sep_location");
 
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
        
        //CreateKeysResponse.Key key = null;
        
        // Start the timer!
        result.sampleStart();
 
        // create single key
        try {
            //String cid = agent.createKey().getConversationId();
            CreateKeysResponse response = null;

        	CreateKeysRequest createKeysRequest = new CreateKeysRequest();
        	List<CreateKeysRequest.Key> keys = createKeysRequest.getKeys();

            //Non-Mutable attributes
            KeyAttributesMap attrs1 = new KeyAttributesMap();
            attrs1.set("aAttr1", Arrays.asList("aValue1", "aValue2"));
            KeyAttributesMap attrs2 = new KeyAttributesMap();
            attrs2.set("aAttr1b", Arrays.asList("aValue1b", "aValue2b"));

            //Mutable attributes
            KeyAttributesMap mAttrs1 = new KeyAttributesMap();
            mAttrs1.set("mAttr1", Arrays.asList("mValue1", "mValue2"));
            KeyAttributesMap mAttrs2 = new KeyAttributesMap();
            mAttrs2.set("mAttr2", Arrays.asList("mValue1", "mValue2"));

            CreateKeysRequest.Key request4Keys1 = new CreateKeysRequest.Key("first_req", 2, attrs1, mAttrs1); //Create keys with different attrs
            CreateKeysRequest.Key request4Keys2 = new CreateKeysRequest.Key("second_req", 1, attrs2, mAttrs2);
            keys.add(request4Keys1);
            keys.add(request4Keys2);
            response = agent.createKeys(createKeysRequest);
            
            //context.getJMeterVariables().put("KeyBytes", DatatypeConverter.printHexBinary(key.getKey()));
            //context.getJMeterVariables().put("FixedAttrs", key.getAttributesSigBase64FromServer());
            //context.getJMeterVariables().put("MutableAttrs", key.getMutableAttributesSigBase64FromServer());
        } catch(IonicException e) {
            result.sampleEnd(); // Stop timer
            result.setSuccessful(false);
            result.setResponseMessage( "Create Key Exception: " + e );
            return result;
        }
 
        // Stop the timer!
        result.sampleEnd();
        //context.getJMeterVariables().put("keyId", response.getId());
        result.setSuccessful(true);
        return result;
}

    @Override
    public void teardownTest(JavaSamplerContext context) {
        // TODO Auto-generated method stub
    }
    
}

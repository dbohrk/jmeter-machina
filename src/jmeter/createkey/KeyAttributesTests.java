package jmeter.createkey;
 
 
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.data.MetadataMap;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.error.IonicException;
import javax.xml.bind.DatatypeConverter;
import jmeter.createkey.KeyUtil;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
 
public class KeyAttributesTests extends AbstractJavaSamplerClient {

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
        	final KeyAttributesMap mutableAttributesQ1 = new KeyAttributesMap();
        	final KeyAttributesMap keyAttributesQ3 = new KeyAttributesMap();
        	final KeyAttributesMap keyAttributesQ4 = new KeyAttributesMap();
        	
        	String bunchOfAs = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        	String bunchOfDs = "dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd";

        	keyAttributesQ3.put("classification", Arrays.asList("01" + bunchOfAs, "02" + bunchOfAs, "03" + bunchOfAs,
            		"04" + bunchOfAs, "05" + bunchOfAs, "06" + bunchOfAs, "07" + bunchOfAs, "08" + bunchOfAs,
            		 "09" + bunchOfAs, "10" + bunchOfAs, "11" + bunchOfAs, "12" + bunchOfAs, "13" + bunchOfAs,
            		 "14" + bunchOfAs, "15" + bunchOfAs, "16" + bunchOfAs, "17" + bunchOfAs,
            		 "18" + bunchOfAs, "19" + bunchOfAs, "20" + bunchOfAs));
        	keyAttributesQ4.put("classification", Arrays.asList("01" + bunchOfDs, "02" + bunchOfDs, "03" + bunchOfDs,
            		"04" + bunchOfDs, "05" + bunchOfDs, "06" + bunchOfDs, "07" + bunchOfDs, "08" + bunchOfDs,
            		 "09" + bunchOfDs, "10" + bunchOfDs, "11" + bunchOfDs, "12" + bunchOfDs, "13" + bunchOfDs,
            		 "14" + bunchOfDs, "15" + bunchOfDs, "16" + bunchOfDs, "17" + bunchOfDs,
            		 "18" + bunchOfDs, "19" + bunchOfDs, "20" + bunchOfDs));
            //keyAttributesQ3.put("classification", Arrays.asList("allowvalue0", "allowvalue1", "allowvalue2"));
            //keyAttributesQ3.put("classification", Arrays.asList("allowvalue0", "allowvalue1", "allowvalue2"));

            List<KeyUtil.CreateAttributeMaps> createAttribsList = new ArrayList<KeyUtil.CreateAttributeMaps>();
            createAttribsList.add(new KeyUtil.CreateAttributeMaps(mutableAttributesQ1, keyAttributesQ3));
            //createAttribsList.add(new KeyUtil.CreateAttributeMaps(mutableAttributesQ1, keyAttributesQ3)); //same empty sets
            //createAttribsList.add(new KeyUtil.CreateAttributeMaps(mutableAttributesQ1, keyAttributesQ3)); //same empty sets
            final CreateKeysResponse createKeysResponse = KeyUtil.createKeysAndVerify(agent,createAttribsList);
            final Collection<CreateKeysResponse.Key> createKeysResponseKeys = createKeysResponse.getKeys();
            
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

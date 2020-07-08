package jmeter.createkey;
 
 
import com.ionic.sdk.device.profile.persistor.DeviceProfilePersistorPlainText;
import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.data.MetadataMap;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.error.IonicException;
import javax.xml.bind.DatatypeConverter;
import jmeter.createkey.KeyUtil;

import org.apache.commons.lang3.StringUtils;
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
        String keyId = null;
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
        
        // create single key
        try {
            //String cid = agent.createKey().getConversationId();
        	final KeyAttributesMap mutableAttributesQ1 = new KeyAttributesMap();
        	final KeyAttributesMap keyAttributesQ3 = new KeyAttributesMap();
        	final KeyAttributesMap keyAttributesQ4 = new KeyAttributesMap();
        	
        	String bunchOfAs = "a".repeat(254);
        	String bunchOfDs = "d".repeat(254);

        	keyAttributesQ3.put("allowattr1", Arrays.asList("01" + bunchOfAs, "02" + bunchOfAs, "03" + bunchOfAs,
            		"04" + bunchOfAs, "05" + bunchOfAs, "06" + bunchOfAs, "07" + bunchOfAs, "08" + bunchOfAs,
            		 "09" + bunchOfAs, "10" + bunchOfAs, "11" + bunchOfAs, "12" + bunchOfAs, "13" + bunchOfAs,
            		 "14" + bunchOfAs, "15" + bunchOfAs, "16" + bunchOfAs, "17" + bunchOfAs,
            		 "18" + bunchOfAs, "19" + bunchOfAs, "20" + bunchOfAs, "21" + bunchOfAs, "22" + bunchOfAs, "23" + bunchOfAs,
            	      "24" + bunchOfAs, "25" + bunchOfAs, "26" + bunchOfAs, "27" + bunchOfAs, "28" + bunchOfAs,
            	      "29" + bunchOfAs, "30" + bunchOfAs, "31" + bunchOfAs, "32" + bunchOfAs, "33" + bunchOfAs,
            	      "34" + bunchOfAs, "35" + bunchOfAs, "36" + bunchOfAs, "37" + bunchOfAs,
            	      "38" + bunchOfAs, "39" + bunchOfAs, "40" + bunchOfAs, "41" + bunchOfAs, "42" + bunchOfAs, "43" + bunchOfAs,
            	     "44" + bunchOfAs, "45" + bunchOfAs, "46" + bunchOfAs, "47" + bunchOfAs, "48" + bunchOfAs,
            	    "49" + bunchOfAs, "50" + bunchOfAs, "51" + bunchOfAs, "52" + bunchOfAs, "53" + bunchOfAs,
            	    "54" + bunchOfAs, "55" + bunchOfAs, "56" + bunchOfAs, "57" + bunchOfAs,
            	    "58" + bunchOfAs, "59" + bunchOfAs, "60" + bunchOfAs));
        	keyAttributesQ3.put("allowattr2", Arrays.asList("61" + bunchOfAs, "62" + bunchOfAs, "63" + bunchOfAs,
        		      "64" + bunchOfAs, "65" + bunchOfAs, "66" + bunchOfAs, "67" + bunchOfAs, "68" + bunchOfAs,
        		       "69" + bunchOfAs, "70" + bunchOfAs, "71" + bunchOfAs, "72" + bunchOfAs, "73" + bunchOfAs,
        		       "74" + bunchOfAs, "75" + bunchOfAs, "76" + bunchOfAs, "77" + bunchOfAs,
        		       "78" + bunchOfAs, "79" + bunchOfAs, "80" + bunchOfAs, "81" + bunchOfAs, "82" + bunchOfAs, "83" + bunchOfAs,
        		      "84" + bunchOfAs, "85" + bunchOfAs, "86" + bunchOfAs, "87" + bunchOfAs, "88" + bunchOfAs,
        		     "89" + bunchOfAs, "90" + bunchOfAs, "91" + bunchOfAs, "92" + bunchOfAs, "93" + bunchOfAs,
        		     "94" + bunchOfAs, "95" + bunchOfAs, "96" + bunchOfAs, "97" + bunchOfAs,
        		     "98" + bunchOfAs, "99" + bunchOfAs));


            //keyAttributesQ3.put("classification", Arrays.asList("allowvalue0", "allowvalue1", "allowvalue2"));
            //keyAttributesQ3.put("classification", Arrays.asList("allowvalue0", "allowvalue1", "allowvalue2"));

            List<KeyUtil.CreateAttributeMaps> createAttribsList = new ArrayList<KeyUtil.CreateAttributeMaps>();
            createAttribsList.add(new KeyUtil.CreateAttributeMaps(mutableAttributesQ1, keyAttributesQ3));
            //createAttribsList.add(new KeyUtil.CreateAttributeMaps(mutableAttributesQ1, keyAttributesQ3)); //same empty sets
            //createAttribsList.add(new KeyUtil.CreateAttributeMaps(mutableAttributesQ1, keyAttributesQ3)); //same empty sets
            
            // Start the timer!
            result.sampleStart();
     
            final CreateKeysResponse createKeysResponse = KeyUtil.createKeysAndVerify(agent,createAttribsList);
            //final Collection<CreateKeysResponse.Key> createKeysResponseKeys = createKeysResponse.getKeys();
            keyId = createKeysResponse.getKeys().get(0).getId();
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
        context.getJMeterVariables().put("keyId", keyId);
        result.setSuccessful(true);
        return result;
}

    @Override
    public void teardownTest(JavaSamplerContext context) {
        // TODO Auto-generated method stub
    }
    
}
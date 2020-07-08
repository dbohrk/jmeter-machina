package jmeter.createkey;

import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.key.AgentKey;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.agent.key.merge.KeyAttributesMapMerger;
import com.ionic.sdk.agent.key.merge.KeyAttributesMapMergerDefault;
import com.ionic.sdk.agent.request.createkey.CreateKeysRequest;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.agent.request.getkey.GetKeysRequest;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.agent.request.updatekey.UpdateKeysRequest;
import com.ionic.sdk.agent.request.updatekey.UpdateKeysResponse;
import java.lang.invoke.MethodHandles;

import com.ionic.sdk.error.IonicException;
//import com.ionic.test.util.TestUtils.Assert;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


public class KeyUtil {
  //private static Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass().getName());

  // BEGIN INTERNAL CLASSES

  public static class CreateAttributeMaps{
    private final Map<String, List<String>> mutableAttrsMap;
    private final KeyAttributesMap keyAttrsMap;

    public CreateAttributeMaps(Map<String, List<String>> mutableAttrsMap){ this(mutableAttrsMap, null); }

    public CreateAttributeMaps(KeyAttributesMap keyAttrsMap){ this(null, keyAttrsMap); }

    public CreateAttributeMaps(Map<String, List<String>> mutableAttrsMap, KeyAttributesMap keyAttrsMap){
      this.mutableAttrsMap = mutableAttrsMap;
      this.keyAttrsMap = keyAttrsMap;
    }

    public Map<String, List<String>> getMattrs(){ return this.mutableAttrsMap; }
    public KeyAttributesMap getCattrs(){ return this.keyAttrsMap; }

  } //CreateAttributeMaps

  public static class UpdateAttributeMaps{
    private final String keyId;
    private final Map<String, List<String>> mutableAttrsMap;
    //NOTE: Can't update CAttrs
    private final boolean force;

    public UpdateAttributeMaps(String keyId, Map<String, List<String>> mutableAttrsMap){ this(keyId, mutableAttrsMap, false); }
    public UpdateAttributeMaps(String keyId, Map<String, List<String>> mutableAttrsMap, boolean force){
      this.keyId = keyId;
      this.mutableAttrsMap = mutableAttrsMap;
      this.force = force;
    }

    public String getKeyId() { return this.keyId; }
    public Map<String, List<String>> getMattrs(){ return this.mutableAttrsMap; }
    public boolean getForce(){ return this.force; }
  } //UpdateAttributeMaps

  // END INTERNAL CLASSES

  // BEGIN CREATE KEY METHODS

  /**
   * Helper method that creates 1 new key for the given
   * Agent(configuration) with the given Mutable Attributes.
   *
   * @param agent
   * @param mutableAttrs
   * @return CreateKeysResponse
   * @throws IonicException
   */
  public static CreateKeysResponse createKeyWithMutableAttributes(Agent agent, Map<String, List<String>> mutableAttrs) throws IonicException {
    return createKeyAndVerify(agent, mutableAttrs, null);
  } //createKeyWithMutableAttributes

  /**
   * Helper method that creates 1 new key for the given
   * Agent(configuration) with the given Immutable Attributes.
   *
   * @param agent
   * @param keyAttrsMap
   * @return CreateKeysResponse
   * @throws IonicException
   */
  public static CreateKeysResponse createKeyWithStaticAttributes(Agent agent, KeyAttributesMap keyAttrsMap) throws IonicException {
    return createKeyAndVerify(agent, null, keyAttrsMap);
  } //createKeyWithStaticAttributes

  /**
   * Helper method that creates 1 new key for the given
   * Agent(configuration) with the given Mutable and Immutable Attributes.
   *
   * @param agent
   * @param mutableAttrsMap
   * @param keyAttrsMap
   * @return CreateKeysResponse
   * @throws IonicException
   */
  public static CreateKeysResponse createKeyAndVerify(Agent agent, Map<String, List<String>> mutableAttrsMap, KeyAttributesMap keyAttrsMap) throws IonicException {
    List<CreateAttributeMaps> createAttribsList = new ArrayList<CreateAttributeMaps>();
    final CreateAttributeMaps createAttributeMaps = new CreateAttributeMaps(mutableAttrsMap, keyAttrsMap);
    createAttribsList.add(createAttributeMaps);
    return createKeysAndVerify(agent, createAttribsList);
  } //createKeyWithMutableAndStaticAttributes


  /**
   * Helper method that creates 1 key for EACH Mutable and Immutable Attributes Entry
   *   in List<CreateAttributeMaps> createAttribsList
   *
   * @param agent
   * @param createAttribsList
   * @return CreateKeysResponse
   * @throws IonicException
   */
  public static CreateKeysResponse createKeysAndVerify(Agent agent, List<CreateAttributeMaps> createAttribsList)throws IonicException{
    //build the request
    CreateKeysRequest request = new CreateKeysRequest();
    List<CreateKeysRequest.Key> keys = (List<CreateKeysRequest.Key>) request.getKeys(); //empty
    int refIdNum = 0;
    for (CreateAttributeMaps mattrAndCattr : createAttribsList) {
      Map<String, List<String>> mutableAttrsMap = mattrAndCattr.getMattrs();
      KeyAttributesMap keyAttrsMap = mattrAndCattr.getCattrs();

      CreateKeysRequest.Key key = new CreateKeysRequest.Key("testRefid" + refIdNum, 1);
      keys.add(key); //gives us something to add the maps to
      if (keyAttrsMap != null) {
        keys.get(refIdNum).getAttributesMap().putAll(keyAttrsMap);
      }
      if (mutableAttrsMap != null) {
        keys.get(refIdNum).getMutableAttributesMap().putAll(mutableAttrsMap);
      }
      refIdNum++;
    }
    //send the create
    CreateKeysResponse createKeysResponse = agent.createKeys(request);

    //Assert.assertEquals(HttpURLConnection.HTTP_CREATED, createKeysResponse.getHttpResponseCode());
    ArrayList<CreateKeysResponse.Key> createKeysList = (ArrayList<CreateKeysResponse.Key> )createKeysResponse.getKeys();
    //Assert.assertEquals(createAttribsList.size(),createKeysList.size());

//    // check createresponse against original requested maps
//    for (CreateAttributeMaps mattrAndCattr : createAttribsList) {
//      Map<String, List<String>> mutableAttrsMap = mattrAndCattr.getMattrs();
//      KeyAttributesMap keyAttrsMap = mattrAndCattr.getCattrs();
//
//      Map<String, List<String>> tempMutableAttrsMap;
//      KeyAttributesMap tempKeyAttrsMap;
//
//      if (mutableAttrsMap != null) {
//        tempMutableAttrsMap = mutableAttrsMap;
//      } else {
//        tempMutableAttrsMap = Collections.<String, List<String>>emptyMap(); //empty, size 0
//      }
//      if (keyAttrsMap != null) {
//        tempKeyAttrsMap = keyAttrsMap;
//      } else {
//        tempKeyAttrsMap = new KeyAttributesMap(); //empty, size 0
//      }
//
//      List<String> matchedIds = new ArrayList<String>();
//      CreateKeysResponse.Key compareCreateKey = null;
//      for (CreateKeysResponse.Key thisKey : createKeysList) {
//        Map<String, List<String>> thisMattr = thisKey.getMutableAttributesMap();
//        KeyAttributesMap thisCattr = thisKey.getAttributesMap();
//
//        if ((thisMattr.equals(tempMutableAttrsMap)) &&
//            (thisCattr.equals(tempKeyAttrsMap)) &&
//            (!matchedIds.contains(thisKey.getId()))) //not already found
//        {
//          matchedIds.add(thisKey.getId());
//          compareCreateKey = thisKey;
//          break;
//        }
//      }
//      //Assert.assertNotNull(compareCreateKey);
//
//      KeyAttributesMap cattrs = compareCreateKey.getAttributesMap();
//      KeyAttributesMap mattrs = compareCreateKey.getMutableAttributesMap();
//      if (keyAttrsMap != null) {
//        //Assert.assertEquals(keyAttrsMap.size(), cattrs.size());
//        //Assert.assertTrue(cattrs.equals(keyAttrsMap));
//      } else {
//        //Assert.assertEquals(0, cattrs.size());
//      }
//      if (mutableAttrsMap != null) {
//
//        //Assert.assertEquals(mutableAttrsMap.size(), mattrs.size());
//        //Assert.assertTrue(mattrs.equals(mutableAttrsMap));
//      } else {
//        //Assert.assertEquals(0, mattrs.size());
//      }
//    } //check createresponse against original requested maps
//
//    // Check that Got Keys == Created Keys
//    // double-check that creation worked on server
//    List<String> keyIdsList = new ArrayList<String>();
//    final GetKeysRequest getKeysRequest = new GetKeysRequest();
//    for (CreateKeysResponse.Key thisKey : createKeysList) {
//      String keyId = thisKey.getId();
//      getKeysRequest.add(keyId);
//      keyIdsList.add(keyId);
//    }
//
//    final GetKeysResponse getKeysResponse = agent.getKeys(getKeysRequest);
//    List<GetKeysResponse.Key> getKeysList = getKeysResponse.getKeys();
//    //Assert.assertEquals("Requested #Of keys should match returned #of keys",createKeysList.size(), getKeysList.size());
//
//    for (GetKeysResponse.Key gotKey : getKeysList) {
//      //Assert.assertTrue(keyIdsList.contains(gotKey.getId()));
//
//      CreateKeysResponse.Key compareCreateKey = null;
//      for (CreateKeysResponse.Key thisKey : createKeysList) {
//        if (thisKey.getId().equals(gotKey.getId())){
//          compareCreateKey = thisKey;
//          break;
//        }
//      }
//      //Assert.assertNotNull(compareCreateKey); //did we find a match?
//
//      final KeyAttributesMap createKeyCattrs = compareCreateKey.getAttributesMap();
//      final KeyAttributesMap createKeyMattrs = compareCreateKey.getMutableAttributesMap();
//      //assert matching cattrs
//      final KeyAttributesMap gotKeyCattrs = gotKey.getAttributesMap();
//      if (createKeyCattrs != null) {
//        //Assert.assertNotNull(gotKeyCattrs);
//        if(gotKeyCattrs.containsKey("ionic-creation-timestamp")){ //Remove key-value added on C-Attr requests.
//          gotKeyCattrs.remove("ionic-creation-timestamp");
//        }
//        //Assert.assertEquals(createKeyCattrs.size(), gotKeyCattrs.size());
//        //Assert.assertTrue(gotKeyCattrs.equals(createKeyCattrs));
//        //TODO Assert Matching CSIG
//      } else {
//        //Assert.assertEquals(0, gotKeyCattrs.size());
//      }
//      //assert matching mattrs
//      final KeyAttributesMap gotKeyMattrs = gotKey.getMutableAttributesMap();
//      if (createKeyMattrs != null) {
//        //Assert.assertNotNull(gotKeyMattrs);
//        //Assert.assertEquals(createKeyMattrs.size(), gotKeyMattrs.size());
//        //Assert.assertTrue(gotKeyMattrs.equals(createKeyMattrs));
//        //TODO Assert Matching MSIG
//      } else {
//        //Assert.assertEquals(0, gotKeyMattrs.size());
//      }
//    } //check each key in getKeysList against createKeysList
    /*
     * TODO something like:
     * Assert.assertEquals(
     * createKey.getAttributesSigBase64FromServer(),
     * updateKey.getAttributesSigBase64FromServer());
     */
    return createKeysResponse;
  }

  /**
   * Utility that creates n number of keys, no key-attrs, no mutable-attrs.
   * @param agent
   * @param numberOfKeys
   * @return
   * @throws IonicException
   */
  public static CreateKeysResponse  createKeysAndVerify(Agent agent, int numberOfKeys) throws IonicException {
    CreateKeysResponse createKeysResponse = null;
    List<KeyUtil.CreateAttributeMaps> createAttribsList = new ArrayList<KeyUtil.CreateAttributeMaps>();
    for(int i=0; i<numberOfKeys; i++){
      final KeyAttributesMap keyAttributes = new KeyAttributesMap();
      final KeyAttributesMap mutableAttributes = new KeyAttributesMap();
      createAttribsList.add(new KeyUtil.CreateAttributeMaps(mutableAttributes, keyAttributes));
    }
    createKeysResponse = KeyUtil.createKeysAndVerify(agent, createAttribsList);
    return createKeysResponse;
  }

  // END CREATE KEY METHODS

  // BEGIN UPDATE KEY METHODS


  /**
   * Helper method that updates 1 key with originalKeyId for the given
   * Agent(configuration) with the given Mutable Attributes.
   *
   * @param agent
   * @param originalKeyId
   * @param mutableAttrsMap
   * @param force (optional)
   * @return UpdateKeysResponse
   * @throws IonicException
   */
  public static UpdateKeysResponse updateKeyAndVerify(Agent agent,
                                                      String originalKeyId,
                                                      Map<String, List<String>>mutableAttrsMap) throws IonicException {
    return updateKeyAndVerify(agent, originalKeyId, mutableAttrsMap, false);
  }
  public static UpdateKeysResponse updateKeyAndVerify(Agent agent,
                                                      String originalKeyId,
                                                      Map<String, List<String>>mutableAttrsMap,
                                                      boolean force) throws IonicException {
    List<UpdateAttributeMaps> updateAttribsList = new ArrayList<UpdateAttributeMaps>();
    final UpdateAttributeMaps updateAttributeMaps = new UpdateAttributeMaps(originalKeyId, mutableAttrsMap, force);
    updateAttribsList.add(updateAttributeMaps);
    return updateKeysAndVerify(agent, updateAttribsList);
  } //updateKeyWithMutableAttributes

  /**
   * Helper method that updates 1 key for EACH keyId + Mutable Attributes Entry
   *   in List<UpdateAttributeMaps> updateAttribsList
   *
   * @param agent
   * @param updateAttribsList
   * @return UpdateKeysResponse
   * @throws IonicException
   */
  public static UpdateKeysResponse updateKeysAndVerify(Agent agent,
                                                       List<UpdateAttributeMaps>updateAttribsList) throws IonicException {
    //build the update
    UpdateKeysRequest updateKeysRequest = new UpdateKeysRequest();
    UpdateKeysResponse updateKeysResponse = null;
    for (UpdateAttributeMaps updateAttributeMaps : updateAttribsList) {
      String keyId = updateAttributeMaps.getKeyId();

      Map<String, List<String>> mutableAttrsMap = updateAttributeMaps.getMattrs();
      boolean force = updateAttributeMaps.getForce();

      //logger.debug("DEBUG: mutableAttrsMap null? "+(mutableAttrsMap==null));

      //we need csig and msig (unless force is set)
      AgentKey updateKey = null;
      if (force) {
        updateKey = new AgentKey();
        updateKey.setId(keyId);
      } else {
        GetKeysResponse getSingleKeyResponse = agent.getKey(keyId);
        List<GetKeysResponse.Key> getKeysList = getSingleKeyResponse.getKeys();
        //Assert.assertEquals(1,getKeysList.size());
        updateKey = getKeysList.get(0);
      }
      //Assert.assertNotNull(updateKey);

      UpdateKeysRequest.Key updateKeyU = new UpdateKeysRequest.Key(updateKey, force);
      //updateKeyU.setId(keyId);
      //TODO: Don't wipe this out? Make sure it's 'old key vals' always?
      //updateKeyU.setAttributesMap(keyAttrsMap);
      if (mutableAttrsMap != null) {
        for (java.util.Map.Entry<String, List<String>> entry : mutableAttrsMap.entrySet()) {
          //logger.debug("DEBUG: Adding [" + entry.getKey() + "]:[" + entry.getValue() + "] to updateKeyU");
          updateKeyU.getMutableAttributesMap().put(entry.getKey(), entry.getValue());
        }
      }
      if (force) {
        updateKeyU.setAttributesSigBase64FromServer("");
        updateKeyU.setMutableAttributesSigBase64FromServer("");
      }
      updateKeysRequest.addKey(updateKeyU);
    }
    //send and print the update
    updateKeysResponse = agent.updateKeys(updateKeysRequest);
    //Assert.assertNotNull(updateKeysResponse);
    printUpdateKeysResponseData(updateKeysResponse, agent);

    //Assert.assertEquals(HttpURLConnection.HTTP_OK, updateKeysResponse.getHttpResponseCode());
    ArrayList<UpdateKeysResponse.Key> updateKeysList = (ArrayList<UpdateKeysResponse.Key> )updateKeysResponse.getKeys();

    //Assert.assertEquals(updateAttribsList.size(), updateKeysResponse.getKeys().size());

    // check update response against original requested maps
    for (UpdateAttributeMaps updateAttributeMaps : updateAttribsList) {
      String keyId = updateAttributeMaps.getKeyId();
      Map<String, List<String>> mutableAttrsMap = updateAttributeMaps.getMattrs();
      boolean force = updateAttributeMaps.getForce();

      // non-static method getKey(String) cannot be referenced from a static context
      UpdateKeysResponse.Key updateKey = null;
      for (UpdateKeysResponse.Key thisKey : updateKeysList) {
        if (thisKey.getId().equals(keyId)){
          updateKey = thisKey;
          break;
        }
      }
      //Assert.assertNotNull(updateKey); //did we find it?
      Map<String, List<String>> updateMutableAttrsMap = updateKey.getMutableAttributesMap();
      KeyAttributesMap updateKeyAttrsMap = updateKey.getAttributesMap();

      if (mutableAttrsMap != null) {
        //logger.debug("DEBUG: ["+keyId+"] " + "updateMutableAttrsMap=" + updateMutableAttrsMap.toString() + ", mutableAttrsMap=" + mutableAttrsMap.toString());
        //Assert.assertTrue(updateMutableAttrsMap.equals(mutableAttrsMap));
        //Assert.assertTrue(updateMutableAttrsMap.entrySet().containsAll(mutableAttrsMap.entrySet())); //additive
      } else {
        //Assert.assertEquals(0,updateMutableAttrsMap.size());
      }
//      if (keyAttrsMap != null) {
//        logger.debug("DEBUG: ["+keyId+"] " + "updateKeyAttrsMap=" + updateKeyAttrsMap.toString() + ", keyAttrsMap=" + keyAttrsMap.toString());
//        Assert.assertTrue(updateKeyAttrsMap.equals(keyAttrsMap));
//      } else {
//        Assert.assertEquals(0,updateKeyAttrsMap.size());
//      }
    } //each map set in updateAttribsList

    // double-check that update worked on server
    GetKeysRequest getKeysRequest = new GetKeysRequest();
    List<String> keyIdsList = new ArrayList<String>();
    for (UpdateAttributeMaps updateAttributeMaps : updateAttribsList) {
      String keyId = updateAttributeMaps.getKeyId();
      getKeysRequest.add(keyId);
      keyIdsList.add(keyId);
    }
    GetKeysResponse getKeysResponse = agent.getKeys(getKeysRequest);
    List<GetKeysResponse.Key> getKeysList = getKeysResponse.getKeys();
    //Assert.assertEquals(updateAttribsList.size(), getKeysList.size());

    for (GetKeysResponse.Key gotKey : getKeysList) {
      //Assert.assertTrue(keyIdsList.contains(gotKey.getId()));

      UpdateAttributeMaps foundMaps = null;
      for (UpdateAttributeMaps updateAttributeMaps : updateAttribsList) {
        if (updateAttributeMaps.getKeyId().equals(gotKey.getId())) {
          foundMaps = updateAttributeMaps;
          break;
        }
      }
      //Assert.assertNotNull(foundMaps); //did we find it?

      Map<String, List<String>> mutableAttrsMap = foundMaps.getMattrs();
      Map<String, List<String>> gotKeyMattrs = gotKey.getMutableAttributesMap();
      //KeyAttributesMap gotKeyCattrs = gotKey.getAttributesMap(); //original created vals; not useful yet

      if (mutableAttrsMap != null) {
        //logger.debug("DEBUG: ["+gotKey.getId()+"] " + "gotKeyMattrs=" + gotKeyMattrs.toString() + ", mutableAttrsMap=" + mutableAttrsMap.toString());
        //Assert.assertTrue(gotKeyMattrs.equals(mutableAttrsMap));
        //Assert.assertTrue(gotKeyMattrs.entrySet().containsAll(mutableAttrsMap.entrySet())); //additive
      } else {
        //TODO: will be original created key values:
        //Assert.assertEquals(0,gotKeyMattrs.size());
      }
    } //each key in getKeysList

    /*
     * TODO something like:
     * Assert.assertEquals(
     * createKey.getAttributesSigBase64FromServer(),
     * updateKey.getAttributesSigBase64FromServer());
     */
    return updateKeysResponse;
  }

  // END UPDATE KEY METHODS

  /**
   * Helper method to determine existence of a known key for a given Agent configuration
   *
   * @param agent
   * @param keyId
   * @return boolean
   * @throws IonicException
   */
  public static boolean keyExistsForAgent(Agent agent, String keyId) throws IonicException {
    boolean rc = false;
    if (agent != null) {
      if (!agent.getKey(keyId).getKeys().isEmpty()) {
        rc = true;
      }
    }
    return rc;
  } //keyExistsForAgent

  /**
   * Debug method to print out KeyIds and Errors in an UpdateKeysResponse
   * @param updateKeysResponse
   * @param agent
   */
  public static void printUpdateKeysResponseData(UpdateKeysResponse updateKeysResponse, Agent agent) {
    //logger.debug("DEBUG: CID=["+updateKeysResponse.getConversationId()+"]");
    //logger.debug("DEBUG: All keyIds in response:");
    List<UpdateKeysResponse.Key> keysList = updateKeysResponse.getKeys();
    for (UpdateKeysResponse.Key thisKey : keysList) {
      //logger.debug("DEBUG: ["+thisKey.getId()+"]");
    }
    //logger.debug("DEBUG: All errors in response:");
    for (UpdateKeysResponse.IonicError thisError : updateKeysResponse.getErrors()) {
      //logger.debug("DEBUG: [" + thisError.getKeyId() + "] srv=" + Integer.toString(thisError.getServerError()) +
          //",clnt=" + Integer.toString(thisError.getClientError()) + ",msg=" + thisError.getServerMessage());

      if (thisError.getServerError() == 500) {
        //fetch the key and see if it got updated
        final GetKeysRequest getKeysRequest = new GetKeysRequest();
        getKeysRequest.add(thisError.getKeyId());
        try {
          final GetKeysResponse getKeysResponse = agent.getKeys(getKeysRequest);

          List<GetKeysResponse.Key> failkeysList = getKeysResponse.getKeys();
          GetKeysResponse.Key thisKey = failkeysList.get(0);

          final KeyAttributesMap mutableAttributes = thisKey.getMutableAttributesMap();

          for (java.util.Map.Entry<String, List<String>> entry : mutableAttributes.entrySet()) {

            //logger.debug("DEBUG: Contents of key that got 500 on update: " + entry.getValue().toString());
          }
        } catch (IonicException e) {
          //logger.debug("DEBUG: AgentGetKey fail: " + e.getStackTrace());
        }
      }
    }
  } //printUpdateKeysResponseData

} //class
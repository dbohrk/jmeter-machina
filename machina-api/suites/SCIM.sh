baseDirectory="$(pwd)"
jmeterDirectory="jmeter"
reportsDirectory="reports"
dataDirectory="data"
testPlanDirectory="jmx"
testPlan="SCIM"
parallelTestPlan="KeylessDecision"
parallelThreadGroup="keylessDecision"

jmeterUsers=1
jmeterRampup=0
jmeterSeconds=5


#threadGroups=( "listScopes" "listDevices" "fetchDeviceDynamic" "fetchDeviceStatic" "addUsersInGroup" "addUsers" "listUsers" "deleteUsers" "fetchUserDynamic" "fetchUserStatic" \
#	"listRoles" "fetchRoleDynamic" "fetchRoleStatic" "createGroups" "listGroups" "deleteGroups" "fetchGroupDynamic" "fetchGroupStatic" "updateGroupPutDynamic" "updateGroupPutStatic" \
#	"updateGroupPatchDynamic" "updateGroupPatchStatic" )

#threadGroups=( "listScopes" "listDevices" "fetchDeviceDynamic" "addUsers" "listUsers" "deleteUsers" "fetchUserDynamic" \
#	"listRoles" "fetchRoleDynamic" "createGroups" "listGroups" "deleteGroups" "fetchGroupDynamic" "updateGroupPutDynamic" \
#	"updateGroupPatchDynamic" )

# threadGroups=( "listScopes" "listDevices" )
#threadGroups=( "addDeleteUsers" )
threadGroups=( "createDeleteGroups" )

for threadGroup in "${threadGroups[@]}"
do
	echo "***** Thread Group: $testPlan:$threadGroup without parallel load*****"

	source $baseDirectory/$jmeterDirectory/$testPlan.sh -t $threadGroup -u $jmeterUsers -s $jmeterSeconds -r $jmeterRampup
done


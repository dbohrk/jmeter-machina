baseDirectory="./"
reportsDirectory="reports"
dataDirectory="data"
testPlanDirectory="jmx"
testPlan="SCIM"

jmeterUsers=20
jmaterRampup=5
jmeterSeconds=90

# threadGroups=( "listScopes" "listDevices" "fetchDeviceDynamic" "fetchDeviceStatic" "addUsersInGroup" "addUsers" "listUsers" "deleteUsers" "fetchUserDynamic" "fetchUserStatic" \
#"listRoles" "fetchRoleDynamic" "fetchRoleStatic" "createGroups" "listGroups" "deleteGroups" "fetchGroupDynamic" "fetchGroupStatic" "updateGroupPutDynamic" "updateGroupPutStatic" \
#"updateGroupPatchDynamic" "updateGroupPatchStatic" )

threadGroups=( "listScopes" "listDevices" )

for threadGroup in "${threadGroups[@]}"
do
	echo $threadGroup"Users"

	rm $dataDirectory/$threadGroup.csv
	rm -rf $reportsDirectory/$threadGroup/*
	jmeter -n -t $testPlanDirectory/$testPlan.jmx -l $dataDirectory/$threadGroup.csv -J$threadGroup"Users"=$jmeterUsers -Jrampup=$jmeterRampup -Jseconds=$jmeterSeconds
	jmeter -g $dataDirectory/$threadGroup.csv -o $reportsDirectory/$threadGroup
done


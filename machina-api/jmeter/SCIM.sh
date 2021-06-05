baseDirectory="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
testPlan="SCIM"

# Thread Groups names must match Thread Groups in JMeter jmx file
threadGroups=( "listScopes" "listDevices" "fetchDeviceDynamic" "fetchDeviceStatic" "addUsersInGroup" "addUsers" \
	"listUsers" "listUsersExternalId" "listUsersExternalIdStartsWith" "listUsersExternalIdContains" "deleteUsers" "fetchUserDynamic" "fetchUserStatic" \
	"listRoles" "fetchRoleDynamic" "fetchRoleStatic" "createGroups" "listGroups" "deleteGroups" "createDeleteGroups" "fetchGroupDynamic" "fetchGroupStatic" \
	"updateGroupPutDynamic" "updateGroupPutStatic" "updateGroupPatchDynamic" "updateGroupPatchStatic" )

source $baseDirectory/api-jmeter.sh

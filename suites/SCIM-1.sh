basDirectory="$(pwd)"
testPlan="SCIM"
jDirectory="jmeter"
parallelTestExecution=0
parallelTestOnly=0

jmeterUsers=0
jmeterRampup=0
jmeterSeconds=0


tGroups=( "listScopes" "listDevices" "listUsers" "listRoles" "listGroups" "addDeleteUsers" "createDeleteGroups" \
	"fetchDeviceDynamic" "fetchUserDynamic" "fetchGroupDynamic" "updateGroupPutDynamic" "updateGroupPatchDynamic" )

#threadGroups=( "listScopes" "listDevices" "fetchDeviceDynamic" "fetchDeviceStatic" "addUsersInGroup" "addUsers" "listUsers" "deleteUsers" "fetchUserDynamic" "fetchUserStatic" \
#	"listRoles" "fetchRoleDynamic" "fetchRoleStatic" "createGroups" "listGroups" "deleteGroups" "fetchGroupDynamic" "fetchGroupStatic" "updateGroupPutDynamic" "updateGroupPutStatic" \
#	"updateGroupPatchDynamic" "updateGroupPatchStatic" )

#threadGroups=( "listScopes" "listDevices" "fetchDeviceDynamic" "addUsers" "listUsers" "deleteUsers" "fetchUserDynamic" \
#	"listRoles" "fetchRoleDynamic" "createGroups" "listGroups" "deleteGroups" "fetchGroupDynamic" "updateGroupPutDynamic" \
#	"updateGroupPatchDynamic" )

# threadGroups=( "listScopes" "listDevices" )
#threadGroups=( "addDeleteUsers" )
#threadGroups=( "createDeleteGroups" )

function usage
{
	echo ""
	echo "$(basename -s '.sh' "$0") usage:"
	echo ""
	echo "-u <number of users>. Default number of users is $jUsers."
	echo "-s <runtime in seconds>. Default runtime is $jSeconds seconds."
	echo "-r <ramp-up time in seconds>. Default ramp-up time is $jRampup seconds."
	echo "-p Execute second run of Thread Group with $parallelThreadGroup in $parallelTestPlan"
	echo "-P Only execute run of Thread Group with $parallelThreadGroup in $parallelTestPlan"
	printf '\nThread Groups to be executed:\n'
	printf '\t- %s\n' "${tGroups[@]}"
	exit 1;
}

while getopts :u:s:r:t:pPh option
do
	case "${option}"
		in
		u) jmeterUsers=${OPTARG};; 
		s) jmeterSeconds=${OPTARG};;	
		r) jmeterRampup=${OPTARG};;
		p) parallelTestExecution=1;;
		P) parallelTestOnly=1;;
		h) usage 1>&2;;									# display uasge (help)
		:) printf "Missing argument for -%s\n" "$OPTARG">&2; usage;;
		\?) printf "Invalid option -%s\n" "$OPTARG">&2; usage;;
		\*) usage 1>&2;;
	esac
done

for threadGroup in "${tGroups[@]}"
do
echo "*****"
echo "*****$threadGroup*****"
echo "*****"
echo "Users $jmeterUsers"
echo "Seconds $jmeterSeconds"
echo "Rampup $jmeterRampup"
echo "Parallel Execution $parallelExecution"
	source $basDirectory/$jDirectory/api-jmeter.sh
done

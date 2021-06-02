baseDirectory="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
# Get Machina SUT URI from configuration file
machinaURI=$(cat config/machinaConfig.csv | sed 's/,/\n/g' | grep 'api')
# Get Machina SUT verison using Machina URI
# xargs used to remove leading/trailing whitespace
machinaVersion=$(curl -s https://$machinaURI | grep "sprint" | cut -d ':' -f2 | tr -d \" | xargs)
reportsDirectory="reports"
dataDirectory="data"
testPlanDirectory="jmx"
# testPlan="SCIM"
# parallelTestExecution=false
# parallelTestOnly=false
parallelTestPlan="KeylessDecision"
parallelThreadGroup="keylessDecision"


#threadGroups=( "listScopes" "listDevices" "fetchDeviceDynamic" "fetchDeviceStatic" "addUsersInGroup" "addUsers" \
#	"listUsers" "listUsersExternalId" "listUsersExternalIdStartsWith" "listUsersExternalIdContains" "deleteUsers" "fetchUserDynamic" "fetchUserStatic" \
#	"listRoles" "fetchRoleDynamic" "fetchRoleStatic" "createGroups" "listGroups" "deleteGroups" "createDeleteGroups" "fetchGroupDynamic" "fetchGroupStatic" \
#	"updateGroupPutDynamic" "updateGroupPutStatic" "updateGroupPatchDynamic" "updateGroupPatchStatic" )

# jmeterUsers=0
# jmeterRampup=0
# jmeterSeconds=0


function usage
{
	echo ""
	echo "$(basename -s '.sh' "$0") usage:"
	echo ""
	echo "-u <number of users>. Default number of users is $jmeterUsers."
	echo "-s <runtime in seconds>. Default runtime is $jmeterSeconds seconds."
	echo "-r <ramp-up time in seconds>. Default ramp-up time is $jmeterRampup seconds."
	echo "-t <thread group name>. Required. Thread Groups available:"
	echo "-p Execute second run of Thread Group with $parallelThreadGroup in $parallelTestPlan"
	echo "-P Only exexute run of Thread Group with $parallelThreadGroup in $parallelTestPlan"
	printf '\t- %s\n' "${threadGroups[@]}"
	exit 1
}

while getopts :u:s:r:t:T:pPh option
do
	case "${option}"
		in
		u) jmeterUsers=${OPTARG};; 
		s) jmeterSeconds=${OPTARG};;	
		r) jmeterRampup=${OPTARG};;
		t) threadGroup=${OPTARG};[[ ${threadGroups[*]} =~ ${threadGroup} ]] || usage 1>&2;;	
		T) threadGroup=${OPTARG};;							# Thread Group without checking validity
		p) parallelTestExecution=true;;
		P) parallelTestOnly=true;;
		h) usage 1>&2;;									# display uasge (help)
		:) printf "Missing argument for -%s\n" "$OPTARG">&2; usage;;
		\?) printf "Invalid option -%s\n" "$OPTARG">&2; usage;;
		\*) usage 1>&2;;
	esac
done

# If no Thread Group is specified, print usage and exit
if [ -z "$threadGroup" ]
then
	echo "A Thread Group is required"
	usage 1>&2
fi

if [ $parallelTestOnly == false ]
then 
	echo "***** Thread Group: $testPlan:$threadGroup without parallel load"
	echo "***** Users/Threads = $jmeterUsers users"
	echo "***** Ramp-up = $jmeterRampup seconds"
	echo "***** Run time = $jmeterSeconds seconds"

	rm $dataDirectory/$threadGroup-$machinaVersion.csv 2> /dev/null
	rm -rf $reportsDirectory/$threadGroup-$machinaVersion 2> /dev/null
	jmeter -n -t $testPlanDirectory/$testPlan.jmx -l $dataDirectory/$threadGroup-$machinaVersion.csv -J$threadGroup"Users"=$jmeterUsers -Jrampup=$jmeterRampup -Jseconds=$jmeterSeconds
	jmeter -g $dataDirectory/$threadGroup-$machinaVersion.csv -o $reportsDirectory/$threadGroup-$machinaVersion

fi

if [ $parallelTestExecution == true ] || [ $parallelTestOnly == true ]
then
        echo "***** Thread Group: $testPlan:$threadGroup with parellel $parallelThreadGroup load"
	echo "***** Users/Threads = $jmeterUsers users"
	echo "***** Ramp-up = $jmeterRampup seconds"
	echo "***** Run time = $jmeterSeconds seconds"

        rm $dataDirectory/$threadGroup-with-$parallelThreadGroup-$machinaVersion.csv 2> /dev/null
        rm -rf $reportsDirectory/$threadGroup-with-$parallelThreadGroup-$machinaVersion 2> /dev/null

        rm $dataDirectory/$parallelThreadGroup-with-$threadGroup-$machinaVersion.csv 2> /dev/null
        rm -rf $reportsDirectory/$parallelThreadGroup-with-$threadGroup-$machinaVersion 2> /dev/null

        jmeter -n -t $testPlanDirectory/$testPlan.jmx -l $dataDirectory/$threadGroup-with-$parallelThreadGroup-$machinaVersion.csv -J$threadGroup"Users"=$jmeterUsers -Jrampup=$jmeterRampup -Jseconds=$jmeterSeconds&

#	Start Parallel Test Plan and Thread Group

        jmeter -n -t $testPlanDirectory/$parallelTestPlan.jmx -l $dataDirectory/$parallelThreadGroup-with-$threadGroup-$machinaVersion.csv -J$parallelThreadGroup"Users"=$jmeterUsers -Jrampup=$jmeterRampup -Jseconds=$jmeterSeconds&

#	Wait for first JMeter run to finish
	wait
#	Produce JMeter reports

        jmeter -g $dataDirectory/$threadGroup-with-$parallelThreadGroup-$machinaVersion.csv -o $reportsDirectory/$threadGroup-with-$parallelThreadGroup-$machinaVersion
        jmeter -g $dataDirectory/$parallelThreadGroup-with-$threadGroup-$machinaVersion.csv -o $reportsDirectory/$parallelThreadGroup-with-$threadGroup-$machinaVersion
fi


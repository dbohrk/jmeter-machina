baseDirectory="."
reportsDirectory="reports"
dataDirectory="data"
testPlanDirectory="jmx"
testPlan="Policy"
parallelTestExecution=false
parallelTestOnly=false
parallelTestPlan="KeylessDecision"
parallelThreadGroup="keylessDecision"

threadGroups=( "addPolicies" "updatePoliciesDynamic" "listPolicies" "fetchPolicyStatic" "fetchPolicyDynamic" "deletePolicies" "addDeletePolicies" \
	"updatePolicyCreatorStatic" "updatePolicyObligationsStatic")

jmeterUsers=0
jmeterRampup=0
jmeterSeconds=0


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

while getopts :u:s:r:t:pPh option
do
	case "${option}"
		in
		u) jmeterUsers=${OPTARG};; 
		s) jmeterSeconds=${OPTARG};;	
		r) jmeterRampup=${OPTARG};;
		t) threadGroup=${OPTARG};[[ ${threadGroups[*]} =~ ${threadGroup} ]] || usage 1>&2;;	
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

	rm $dataDirectory/$threadGroup.csv
	rm -rf $reportsDirectory/$threadGroup
	jmeter -n -t $testPlanDirectory/$testPlan.jmx -l $dataDirectory/$threadGroup.csv -J$threadGroup"Users"=$jmeterUsers -Jrampup=$jmeterRampup -Jseconds=$jmeterSeconds
	jmeter -g $dataDirectory/$threadGroup.csv -o $reportsDirectory/$threadGroup

fi

if [ $parallelTestExecution == true ] || [ $parallelTestOnly == true ]
then
        echo "***** Thread Group: $testPlan:$threadGroup with parellel $parallelThreadGroup load"
	echo "***** Users/Threads = $jmeterUsers users"
	echo "***** Ramp-up = $jmeterRampup seconds"
	echo "***** Run time = $jmeterSeconds seconds"

        rm $dataDirectory/$threadGroup-with-$parallelThreadGroup.csv
        rm -rf $reportsDirectory/$threadGroup-with-$parallelThreadGroup

        rm $dataDirectory/$parallelThreadGroup-with-$threadGroup.csv
        rm -rf $reportsDirectory/$parallelThreadGroup-with-$threadGroup

        jmeter -n -t $testPlanDirectory/$testPlan.jmx -l $dataDirectory/$threadGroup-with-$parallelThreadGroup.csv -J$threadGroup"Users"=$jmeterUsers -Jrampup=$jmeterRampup -Jseconds=$jmeterSeconds&

#	Start Parallel Test Plan and Thread Group

        jmeter -n -t $testPlanDirectory/$parallelTestPlan.jmx -l $dataDirectory/$parallelThreadGroup-with-$threadGroup.csv -J$parallelThreadGroup"Users"=$jmeterUsers -Jrampup=$jmeterRampup -Jseconds=$jmeterSeconds&

#	Wait for first JMeter run to finish
	wait
#	Produce JMeter reports

        jmeter -g $dataDirectory/$threadGroup-with-$parallelThreadGroup.csv -o $reportsDirectory/$threadGroup-with-$parallelThreadGroup
        jmeter -g $dataDirectory/$parallelThreadGroup-with-$threadGroup.csv -o $reportsDirectory/$parallelThreadGroup-with-$threadGroup
fi


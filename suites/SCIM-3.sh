basDirectory="$(pwd)"
testPlan="SCIM"
jDirectory="jmeter"
parallelTestExecution=0
parallelTestOnly=0

jmeterUsers=0
jmeterRampup=0
jmeterSeconds=0

# JMeter Thread Groups that Add or Delete items
tGroups=( "addUsers" "deleteUsers" )

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
	source $basDirectory/$jDirectory/api-jmeter.sh
done

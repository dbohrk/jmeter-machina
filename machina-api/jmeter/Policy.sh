baseDirectory="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
testPlan="Policy"	# JMeter jmx file name

# Thread Groups names must match Thread Groups in JMeter jmx file
threadGroups=( "addPolicies" "updatePoliciesDynamic" "listPolicies" "fetchPolicyStatic" "fetchPolicyDynamic" "deletePolicies" "addDeletePolicies" \
	"updatePolicyCreatorStatic" "updatePolicyObligationsStatic")

source $baseDirectory/api-jmeter.sh

baseDirectory="./"
reportsDirectory="reports"
dataDirectory="data"
testPlanDirectory="jmx"
testPlan=KeylessDecision

jmeterUsers=20
jmaterRampup=5
jmeterSeconds=90

threadGroups=( "keylessDecision" "addUsers" "addPolicies" "deletePolicies" )

for threadGroup in "${threadGroups[@]}"
do
	echo $threadGroup"Users"
	rm $dataDirectory/$threadGroup.csv
	rm -rf $reportsDirectory/$threadGroup/*
	jmeter -n -t $testPlanDirectory/$testPlan.jmx -l $dataDirectory/$threadGroup.csv -JkeylessDecisionUsers=$jmeterUsers -Jrampup=$jmeterRampup -Jseconds=$jmeterSeconds
	jmeter -g $dataDirectory/$threadGroup.csv -o $reportsDirectory/$threadGroup
done


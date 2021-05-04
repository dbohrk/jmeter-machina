baseDirectory="./"
reportsDirectory="reports"
dataDirectory="data"
testPlanDirectory="jmx"
testPlan="KeylessDecision"
parallelTestPlan="KeylessDecision"
parallelThreadGroup="keylessDecision"

jmeterUsers=20
jmaterRampup=5
jmeterSeconds=1800

threadGroups=( "keylessDecision" )

for threadGroup in "${threadGroups[@]}"
do
	echo '***** Thread Group: '$testPlan':'$threadGroup' without parallel load *****'

	rm $dataDirectory/$threadGroup.csv
	rm -rf $reportsDirectory/$threadGroup
	jmeter -n -t $testPlanDirectory/$testPlan.jmx -l $dataDirectory/$threadGroup.csv -J$threadGroup"Users"=$jmeterUsers -Jrampup=$jmeterRampup -Jseconds=$jmeterSeconds
	jmeter -g $dataDirectory/$threadGroup.csv -o $reportsDirectory/$threadGroup


        echo '***** Thread Group: '$testPlan':'$threadGroup' with '$parallelThreadGroup' load *****'

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
done


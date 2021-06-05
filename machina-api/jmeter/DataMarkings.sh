baseDirectory="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
testPlan="DataMarkings"

# Thread Groups names must match Thread Groups in JMeter jmx file
threadGroups=( "createMarkings" "updateMarking" "listMarkings" "fetchMarkingDynamic" "fetchMarkingStatic" "deleteMarkings" )

source $baseDirectory/api-jmeter.sh

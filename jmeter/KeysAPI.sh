baseDirectory="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
testPlan="KeysAPI"	# JMeter jmx file name

# Thread Groups names must match Thread Groups in JMeter jmx file
threadGroups=( "keyCreate" "keyFetch" "keyCreateFetch" "keyCreateExternalID" "keyFetchExternalID" "kns" )

source $baseDirectory/api-jmeter.sh

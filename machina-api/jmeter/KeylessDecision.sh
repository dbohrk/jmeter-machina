baseDirectory="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
TestPlan="KeylessDecision"	# JMeter jmx name

# Thread Groups names must match Thread Groups in JMeter jmx file
threadGroups=( "keylessDecision" )

source $baseDirectory/api-jmeter.sh

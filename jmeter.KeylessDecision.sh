# Keyless Decision
rm KeylessDecision.csv
rm -rf Report-KeylessDecision/*
jmeter -n -t KeylessDecision/KeylessDecision.jmx -l KeylessDecision.csv -JkeylessDecisionUsers=20 -Jrampup=5 -Jseconds=90
jmeter -g KeylessDecision.csv -o Report-KeylessDecision
#
# Add Users
# jmeter -n -t KeylessDecision/KeylessDecision.jmx -l headless.csv -JaddUsersUsers=20 -Jrampup=5 -Jseconds=90
#
# Add Policies
# jmeter -n -t KeylessDecision/KeylessDecision.jmx -l headless.csv -JaddPoliciesUsers=20 -Jrampup=5 -Jseconds=90
#
# Delete Policies
# jmeter -n -t KeylessDecision/KeylessDecision.jmx -l headless.csv -JdeletePoliciesUsers=20 -Jrampup=5 -Jseconds=90

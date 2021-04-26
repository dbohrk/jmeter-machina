# Keyless Decision
rm Data/KeylessDecision-without.csv
rm -rf Reports/KeylessDecision-without/*
jmeter -n -t KeylessDecision/KeylessDecision.jmx -l Data/KeylessDecision-without.csv -JkeylessDecisionUsers=20 -Jrampup=5 -Jseconds=1800
jmeter -g Data/KeylessDecision-without.csv -o Reports/KeylessDecision-without
# 
# Keyless Decision with Keyless Policy Decision
# rm Data/KeylessDecision.csv
# rm -rf Reports/KeylessDecision/*
# rm Data/KeylessDecision-with.csv
# rm -rf Reports/KeylessDecision-with/*
# jmeter -n -t KeylessDecision/KeylessDecision.jmx -l Data/KeylessDecision.csv -JkeylessDecisionUsers=20 -Jrampup=5 -Jseconds=1800&
# jmeter -n -t KeylessDecision/KeylessDecision.jmx -l Data/KeylessDecision-with.csv -JkeylessDecisionUsers=20 -Jrampup=5 -Jseconds=1800
# jmeter -g Data/KeylessDecision.csv -o Reports/KeylessDecision&
# jmeter -g Data/KeylessDecision-with.csv -o Reports/KeylessDecision-with&
#
# Add Users
# jmeter -n -t KeylessDecision/KeylessDecision.jmx -l headless.csv -JaddUsersUsers=20 -Jrampup=5 -Jseconds=90
#
# Add Policies
# jmeter -n -t KeylessDecision/KeylessDecision.jmx -l headless.csv -JaddPoliciesUsers=20 -Jrampup=5 -Jseconds=90
#
# Delete Policies
# jmeter -n -t KeylessDecision/KeylessDecision.jmx -l headless.csv -JdeletePoliciesUsers=20 -Jrampup=5 -Jseconds=90

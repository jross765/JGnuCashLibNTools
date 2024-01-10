#!/bin/bash
#######################################################################

# mvn package
mvn package -Dmaven.test.skip.exec

# ---------------------------------------------------------------------
# JavaDoc

cd gnucash-api
rm -rf target/site
mvn javadoc:javadoc
cd ..

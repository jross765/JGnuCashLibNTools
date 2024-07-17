#!/bin/bash
#######################################################################

# ---------------------------------------------------------------------
# Before we start...

# Check that we are in the correct directory
CURRDIR=$( pwd )
if [ ! -f "$CURRDIR/build.sh" ]
then
  echo "This build script has to be started in the directory where it is located."
  echo "Aborting."
  exit 1
fi

# ---------------------------------------------------------------------
# Core

echo "=============================================================="
echo "=============================================================="
echo "Building Packages"
echo "=============================================================="
echo "=============================================================="
  
# mvn package
mvn package -Dmaven.test.skip.exec

# ---------------------------------------------------------------------
# JavaDoc

echo ""
echo "=============================================================="
echo "=============================================================="
echo "Building JavaDoc"
echo "=============================================================="
echo "=============================================================="
  
# CAUTION: Currently, it is not possible to generate JavaDoc documentation
# because there are too many consistency errors in the JavaDoc comments
# all over the code.
# It will take some time and iterations to resolve this.
echo "OMITTING"
exit 0

JAVADOC_STATUS_ALL=0

for module in gnucash-base \
              gnucash-api \
              gnucash-api-ext
do
  echo ""
  echo "=============================================================="
  echo "Module '$module'"
  echo "=============================================================="
  
  cd $module || exit 1
  rm -rf target/site
  mvn javadoc:javadoc || JAVADOC_STATUS_ALL=1
  cd ..
done

exit $JAVADOC_STATUS_ALL

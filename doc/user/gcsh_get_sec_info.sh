#!/bin/bash

java -Dlog4j.configuration=$CONFIGPATH/log4j.cfg \
     -Dconfig=$CONFIGFILE \
     org.gnucash.tools.xml.get.info.GetSecInfo ${1+"$@"}

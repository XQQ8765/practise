.SUFFIXES = .java .class
SHELL=tcsh -f
JAVA_HOME = $(shell which java | sed 's/\/bin\/java$$//')
JAVA_RT = $(JAVA_HOME)/jre/lib/rt.jar:$(JAVA_HOME)/jre/lib/i18n.jar
SOURCES    = $(wildcard *.java)
CLASSFILES = $(SOURCES:.java=.class)

%.class: %.java
	java -classpath $${CLASSPATH}:$$GJDIR/classes -ms16m gjc.Main -bootclasspath $$GJDIR/classes:$(JAVA_RT) -unchecked $<

all:	PMGpp2.class BaratParser.java BaratParserTokenManager.class BaratParser.class\
	NameAnalysis.class TypeAnalysis.class $(CLASSFILES)

quick:	PMGpp2.class BaratParser.java
	java -classpath $${CLASSPATH}:$$GJDIR/classes -ms32m gjc.Main -bootclasspath $$GJDIR/classes:$(JAVA_RT) -unchecked *.java

boris:
	echo $(JAVA_HOME)

clean:
	rm -f core BaratParser*.java Token*.java ASCII_UCodeESC_CharStream*.java *.class *~

BaratParser.java:	BaratParser.jj
	java barat.parser.PMGpp2 BaratParser.jj | grep -v "magic cookie" > BaratParser-tmp.jj
	-(javacc BaratParser-tmp.jj && /bin/rm BaratParser-tmp.jj)
	sed 's/$$1/</g;s/$$2/>/g;s/$$3/,/g' < BaratParser.java > TMP
	mv -f TMP BaratParser.java
	sed 's/$$1/</g;s/$$2/>/g;s/$$3/,/g' < BaratParserTokenManager.java > TMP
	mv -f TMP BaratParserTokenManager.java

BaratParserTokenManager.class: BaratParserTokenManager.java

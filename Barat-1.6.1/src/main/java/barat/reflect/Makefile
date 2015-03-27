.SUFFIXES = .java .class

PMGJAVAC     = $(PMGDIR)/bin/pmgjavac #/home/bwana/bokowski/src/pmg/bin/pmgjavac
PMGCLASSPATH = $(CLASSPATH):$(PMGDIR):/usr/local/public/lib/classes:/opt/java/lib/classes.zip

SOURCES    = $(wildcard *.java)
CLASSFILES = $(SOURCES:.java=.class)
JC         = javac

all:	$(CLASSFILES)

clean:
	-/bin/rm -f *.class

%.class:	%.java
	$(JC) $<


.SUFFIXES = .java .class

SOURCES    = $(wildcard Test*.java) RunTests.java
CLASSFILES = $(SOURCES:.java=.class)
JC         = javac

all:	$(CLASSFILES)

clean:
	-/bin/rm -f *.class

%.class:	%.java
	$(JC) $<


# $Id: Makefile,v 1.2 2001/02/15 11:37:57 dahm Exp $

SOURCES := CodeGenerator.java InstructionFactory.java Conversion.java Suspension.java \
	   MethodObject.java PeepHole.java VariableEnvironment.java ClassMap.java \
	   ClassGenerator.java InnerClassVisitor.java CodeHook.java \
	   CollectionAttribute.java InnerClassGenerator.java CodeSuspension.java \
           Repository.java

#$(wildcard *.java)
CLASSES := $(patsubst %.java,%.class,$(SOURCES))
JC      := jikes
JFLAGS  := +E

#
# New rule for type dependent compilation
#
%.class: %.java
	@rm -f $@
	$(JC) $(JFLAGS) $<

all: $(CLASSES)

clean:
	rm -f core *~ *.class

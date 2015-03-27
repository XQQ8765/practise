# $Id: Makefile,v 1.12 2001/05/03 09:40:16 dahm Exp $

ARGUMENTS = AExpression AStatement ASwitchBranch AbstractMethod Block Catch\
            Class ConcreteMethod Constructor ConstructorCall Field\
	    Interface Parameter QualifiedName String VariableDeclaration\

INSTANCES = $(addsuffix ArrayList,$(ARGUMENTS))\
	$(addsuffix Collection,$(ARGUMENTS))\
	$(addsuffix Iterator,$(ARGUMENTS))\
	$(addsuffix IteratorWrapper,$(ARGUMENTS))\
	$(addsuffix List,$(ARGUMENTS))\
	$(addsuffix ListIterator,$(ARGUMENTS))

SOURCES = $(addsuffix .java,$(INSTANCES))
CLASSES = $(addsuffix .class,$(INSTANCES))

JC        = javac # jikes
JFLAGS    = # +E -g

all: instances classes

clean:
	-/bin/rm -f *.class $(SOURCES)

instances:  $(SOURCES)
classes:  $(CLASSES)

%.class: %.java
	$(JC) $(JFLAGS) $<

$(addsuffix ArrayList.java,$(ARGUMENTS)): %ArrayList.java: ArrayList.generic
	./instantiate ArrayList $*

$(addsuffix Collection.java,$(ARGUMENTS)): %Collection.java: Collection.generic
	./instantiate Collection $*

$(addsuffix Iterator.java,$(ARGUMENTS)): %Iterator.java: Iterator.generic
	./instantiate Iterator $*

$(addsuffix IteratorWrapper.java,$(ARGUMENTS)): %IteratorWrapper.java: IteratorWrapper.generic
	./instantiate IteratorWrapper $*

$(addsuffix List.java,$(ARGUMENTS)): %List.java: List.generic
	./instantiate List $*

$(addsuffix ListIterator.java,$(ARGUMENTS)): %ListIterator.java: ListIterator.generic
	./instantiate ListIterator $*


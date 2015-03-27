.SUFFIXES = .java .class

SRCDIR = $(shell pwd)
PUREDIR = $(SRCDIR)/../pure

SOURCES    = $(wildcard *.java)
CLASSFILES = $(SOURCES:.java=.class)
JC         = javac #jikes +E

VERSION = $(shell grep return Version.java | awk -F'[ \t\"]+' '{print "Barat-" $$3;}')

all:	subdirs thisdir

clean:
	cd collections && $(MAKE) clean
	cd reflect && $(MAKE) clean
	cd parser && $(MAKE) clean
	-/bin/rm -f *.class

subdirs:
	cd collections && $(MAKE) JC="$(JC)" instances
	cd reflect && $(MAKE) JC="$(JC)"
	cd collections && $(MAKE) JC="$(JC)" classes
	cd parser && $(MAKE)
	cd test && $(MAKE)

always:

doc:	always
	javadoc -d doc -version barat barat.reflect 

jar:	always
	(cd ..; jar cfm Barat.jar barat/Manifest barat/collections/*.class \
	 barat/reflect/*.class barat/codegen/*.class  barat/parser/*.class barat/*.class)

codegen:
	cd codegen && $(MAKE)

thisdir:	$(CLASSFILES)

distrib: all
	-/bin/rm -rf /tmp/$(VERSION)*
	mkdir /tmp/$(VERSION)
	cp -R ../* /tmp/$(VERSION)/
	find /tmp/$(VERSION) -type d -name 'CVS' -print | xargs /bin/rm -rf
	cd /tmp && tar zcfv $(VERSION).tar.gz $(VERSION)
	cd /tmp && zip -r $(VERSION).zip $(VERSION)
	-/bin/rm -rf /tmp/$(VERSION)

%.class: %.java
	$(JC) $<



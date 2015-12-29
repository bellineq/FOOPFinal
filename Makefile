JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
		Monopoly.java\


default: classes

classes: $(CLASSES:.java=.class)

run:
	java Monopoly

clean:
	$(RM) *.class

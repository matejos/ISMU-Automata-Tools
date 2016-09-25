
QS = $(shell ls *.qdef | fgrep -v '.new')

all : $(QS:.qdef=.new.qdef)

%.new.qdef : %.qdef
	echo -e '.new\n\n' | ./convert $<

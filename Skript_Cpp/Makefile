all : convert

CPPS = Skript/Skript.cpp
HS =

convert : $(CPPS:.cpp=.o)
	$(CXX) $^ -o $@

%.o : %.cpp $(HS)
	$(CXX) $< -c -o $@ -Wall -Wextra

YEAR ?= $(shell date +'%Y')

isenv : isenv_$(YEAR) foce

isenv_% : convert force
	mkdir -p $@
	cp convert $@
	cp isenv/Makefile $@
	sed isenv/prefix -e "s/@YEAR@/$(YEAR)/g" > $@/Skript_prefix.txt
	sed isenv/parsers -e "s/@YEAR@/$(YEAR)/g" > $@/Skript_parsers.txt
	cd $@ && $$SHELL

	mkdir isenv_$(YEAR)

force : ;

.PHONY : isenv isenv_% force

.PRECIOUS : isenv_%

all : convert

CPPS = Skript/Skript.cpp
HS =

convert : $(CPPS:.cpp=.o)
	$(CXX) $^ -o $@

%.o : %.cpp $(HS)
	$(CXX) $< -c -o $@ -Wall -Wextra

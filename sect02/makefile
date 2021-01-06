SO_ENV = /usr/bin/env
JAVAC  = javac
JAVA   = java
FIND   = find
ECHO   = echo
RM     = rm

.PHONY: clean ex*

# --------------------------
# Set the default source text file if not passed by the user
SRC := $(if $(SRC), $(SRC), "./$(firstword $(MAKECMDGOALS))/source.txt")

# --------------------------
# Clean all exercise classes file
clean:
	@$(SO_ENV) $(FIND) . -name '*.class' -exec $(SO_ENV) $(RM) {} \;
	@$(SO_ENV) $(ECHO) "Class files cleaned!"

# --------------------------
# Exercises class file
ex01: ex01/Lexer.class
ex02: ex02/Lexer.class
ex03: ex03/Lexer.class

# --------------------------
# Run given exercise
ex*: SPLITEX = $(subst /, , $(firstword $(subst ., , $<)))
ex*: NUMEX = $(word 2, $(subst x, , $(firstword $(SPLITEX))))
ex*:
	@$(SO_ENV) $(ECHO) "Running exercise $(NUMEX)..."
	@$(SO_ENV) $(JAVA) -cp ./$(firstword $(SPLITEX)) $(word 2, $(SPLITEX)) $(SRC)

# --------------------------
# Compile given exercise
%.class: %.java
	@$(SO_ENV) $(ECHO) "Compiling exercise $(NUMEX)..."
	@$(SO_ENV) $(JAVAC) -cp ./$(firstword $(SPLITEX)) ./$*.java
SO_ENV = /usr/bin/env
JAVAC  = javac
JAVA   = java
FIND   = find
ECHO   = echo
RM     = rm -f

.PHONY: clean ex*

# --------------------------
# Set the default source text file if not passed by the user
SRC := $(if $(SRC), $(SRC), "./$(firstword $(MAKECMDGOALS))/source.pas")

# --------------------------
# Clean all exercise classes file
clean:
	@$(SO_ENV) $(FIND) . -name '*.j' -exec $(SO_ENV) $(RM) {} \;
	@$(SO_ENV) $(FIND) . -name '*.class' -exec $(SO_ENV) $(RM) {} \;
	@$(SO_ENV) $(ECHO) "Class files cleaned!"

# --------------------------
# Exercises class file
ex01: ex01/Translator.class

# --------------------------
# Run given exercise
ex*: SPLITEX = $(subst /, , $(firstword $(subst ., , $<)))
ex*: NAMEX = $(firstword $(SPLITEX))
ex*: NUMEX = $(word 2, $(subst x, , $(NAMEX)))
ex*:
	@$(SO_ENV) $(ECHO) "Running exercise $(NUMEX)..."
	@$(SO_ENV) $(JAVA) -cp ./$(NAMEX) $(word 2, $(SPLITEX)) $(SRC)
	@$(SO_ENV) $(JAVA) -jar ./lib/jasmine.jar ./$(NAMEX)/Output.j
	@$(SO_ENV) $(JAVA) ./$(NAMEX)/Output

# --------------------------
# Compile given exercise
%.class: %.java
	@$(SO_ENV) $(ECHO) "Compiling exercise $(NUMEX)..."
	@$(SO_ENV) $(JAVAC) -cp ./$(NAMEX) ./$*.java
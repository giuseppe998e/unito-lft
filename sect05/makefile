SO_ENV = /usr/bin/env
JAVAC  = javac
JAVA   = java
PDFTEX = pdflatex
FIND   = find
ECHO   = echo
RM     = rm -f

.PHONY: clean ex*

# --------------------------
# Set the default source text file and .J file if not passed by the user
SRC := $(if $(SRC), $(SRC), "$(firstword $(MAKECMDGOALS))/source.pas")
OUT := $(if $(OUT), $(OUT), "$(firstword $(MAKECMDGOALS))/Output.j")

# --------------------------
# Clean all exercise classes file
clean:
	@$(SO_ENV) $(FIND) docs -not -name '*.tex' -exec $(SO_ENV) $(RM) {} \;
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
	@$(SO_ENV) $(JAVA) -cp $(NAMEX) $(word 2, $(SPLITEX)) $(SRC) $(OUT)
	@$(SO_ENV) $(JAVA) -jar libs/jasmin.jar -d $(NAMEX) $(OUT)
	@$(SO_ENV) $(JAVA) -cp $(NAMEX) Output

# --------------------------
# Compile given exercise
%.class: %.java
	@$(SO_ENV) $(ECHO) "Compiling exercise $(NUMEX)..."
	@$(SO_ENV) $(JAVAC) -cp $(NAMEX) $*.java

# --------------------------
# Compile latex file
pdf:
	@$(SO_ENV) $(LTOP)
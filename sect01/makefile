SO_ENV = /usr/bin/env
JAVAC  = javac
JAVA   = java
FIND   = find
ECHO   = echo
RM     = rm

.PHONY: clean ex*

# --------------------------
# Clean all exercise classes file
clean:
	@$(SO_ENV) $(FIND) . -name '*.class' -exec $(SO_ENV) $(RM) {} \;
	@$(SO_ENV) $(ECHO) "Class files cleaned!"

# --------------------------
# Exercises class file
ex01: ex01/NoTreZeri.class
ex02: ex02/Identificatore.class
ex03: ex03/Matricola.class
ex04: ex04/MatricolaSpazi.class
ex05: ex05/MatricolaContrario.class
ex06: ex06/MultipliBinari.class
ex07: ex07/AlfabetoPrime.class
ex08: ex08/AlfabetoUltime.class
ex09: ex09/MioNome.class
ex10: ex10/Commenti.class
ex11: ex11/CommentiReverse.class

# --------------------------
# Run given exercise
ex*: SPLITEX = $(subst /, , $(firstword $(subst ., , $<)))
ex*: NUMEX = $(word 2, $(subst x, , $(firstword $(SPLITEX))))
ex*:
	@$(SO_ENV) $(ECHO) "Running exercise $(NUMEX)..."
	@$(SO_ENV) $(JAVA) -cp ./$(firstword $(SPLITEX)) $(word 2, $(SPLITEX))

# --------------------------
# Compile given exercise
%.class: %.java
	@$(SO_ENV) $(ECHO) "Compiling exercise $(NUMEX)..."
	@$(SO_ENV) $(JAVAC) ./$*.java
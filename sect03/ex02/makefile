OS_ENV = /usr/bin/env
JAVAC  = javac
JAVA   = java
PDFTEX = pdflatex
MKDIR  = mkdir -p
ECHO   = echo
RM     = rm -rf

SRCDIR = src
OUTDIR = out
DOCDIR = doc

SRC := $(if $(SRC), $(SRC), "source.txt")

# --------------------------
# Run java application
.PHONY: run
run: $(OUTDIR)/Parser.class
	@$(SO_ENV) $(ECHO) "Running exercise..."
	@$(SO_ENV) $(JAVA) -cp $(OUTDIR) $(basename $(notdir $<)) $(SRC)

# --------------------------
# Build java class files
$(OUTDIR)/%.class: $(SRCDIR)/%.java
	@$(OS_ENV) $(ECHO) "Generating 'Java' class files..."
	@$(OS_ENV) $(MKDIR) $(OUTDIR)
	@$(OS_ENV) $(JAVAC) -cp $(SRCDIR) -d $(OUTDIR) $<

# --------------------------
# Clean all generated classes
.PHONY: clean
clean:
	@$(OS_ENV) $(ECHO) "Cleaning 'Java' class files..."
	@$(OS_ENV) $(RM) $(OUTDIR)/*
